/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.hmi.input.util;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.cookindustries.lib.spring.gui.hmi.container.FormContainer;
import de.cookindustries.lib.spring.gui.hmi.input.File;
import de.cookindustries.lib.spring.gui.hmi.input.Input;
import de.cookindustries.lib.spring.gui.hmi.input.marker.Marker;
import de.cookindustries.lib.spring.gui.hmi.input.marker.MarkerCategory;
import de.cookindustries.lib.spring.gui.hmi.input.marker.MarkerType;
import de.cookindustries.lib.spring.gui.hmi.input.util.exception.ValueNotPresentException;
import de.cookindustries.lib.spring.gui.response.message.ActivateMarkerMessage;
import de.cookindustries.lib.spring.gui.response.message.MessageType;
import de.cookindustries.lib.spring.gui.response.message.ModalMessage;
import de.cookindustries.lib.spring.gui.response.message.ResponseMessage;

/**
 * Utility to extract information from {@code Form} data into data objects.
 * <p>
 * A {@code InputExtractor} is supposed to run as a helper to streamline the validation and retrival-of-data process. It is intended to work
 * with DTO objects either as POJOs with setters, or a builder.
 * <p>
 * A {@code InputExtractor} can olso validate a {@link MultipartFile} intended as a file upload from a {@link File} input. This validation
 * only checks whether there are any files uploaded and how many, but nothing about the state or value of the files, since this is up to the
 * developer.
 * <p>
 * Output of validation is given in the form of {@link ActivateMarkerMessage}s bound to {@link Marker} definitions on the {@link Input}, or
 * alternativly as {@link ModalMessage}s with more descriptive error messages for unexpected exceptions.
 * <p>
 * Example:
 * 
 * <pre>
 * 
 * public Response handleFrom(MultiValueMap&lt;String, String&gt; formData, MultipartFile[] files)
 * {
 *     InputExtractor extractor = new InputExtractor(inputs, files);
 *     DtoBuilder dtoBuilder = Dto.builder();
 * 
 *     extractor.consumeString("mail", dtoBuilder::mail);
 *     extractor.consumeString("name", dtoBuilder::name);
 *     extractor.consumeEnum("resultType", ImageType.class, dtoBuilder::resultType);
 *     extractor.checkFiles(false, true);
 * 
 *     if (extractor.hasMessages())
 *     {
 *         return guiFactory.getActiveMarkerResponse(extractor);
 *     }
 * 
 *     Dto data = dtoBuilder.build();
 * 
 *     // do here with the data whatever you need...
 *     // [...]
 * }
 * </pre>
 * 
 * @since 1.0.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
public final class InputExtractor
{

    private static final ObjectMapper           TAG_LIST_MAPPER = new ObjectMapper();

    private final String                        formId;
    private final MultiValueMap<String, String> inputs;
    private final MultipartFile[]               files;
    private final List<ResponseMessage>         messages        = new ArrayList<>();

    /**
     * Create a extractor for a {@link FormContainer} result
     * <p>
     * It is assumed that the {@code key} <b>__form_id</b> exists inside {@code inputs} and is non-null.
     * 
     * @param inputs from the {@code Form}
     * @throws IllegalArgumentException if {@code key} <b>__form_id</b> {@code null} or empty
     */
    public InputExtractor(MultiValueMap<String, String> inputs)
    {
        this(inputs, null);
    }

    /**
     * Create a extractor for a {@link FormContainer} result
     * <p>
     * This object expects that the {@code key} <b>__form_id</b> exists inside {@code inputs} and is non-null.
     * 
     * @param inputs from the {@code Form}
     * @param files from the {@code Form}
     * @throws IllegalArgumentException if {@code key} <b>__form_id</b> is {@code null} or empty
     */
    public InputExtractor(MultiValueMap<String, String> inputs, MultipartFile[] files)
    {
        this.inputs = inputs;
        this.files = files;

        formId = (String) inputs.getFirst("__form_id");

        if (formId == null || formId.isBlank())
        {
            throw new IllegalArgumentException("key [__form_id] cannot be null/empty");
        }
    }

    /**
     * Get a value for a {@code key}
     * 
     * @param key to look-up
     * @return the value as {@code String} associated with {@code key}
     * @throws IllegalArgumentException if {@code key} is {@code null} or empty
     * @throws ValueNotPresentException if the retrived {@code value} is {@code null}
     */
    private String getValue(String key)
    {
        if (key == null || key.isBlank())
        {
            throw new IllegalArgumentException("key cannot be null/empty");
        }

        String value = inputs.getFirst(key);

        if (value == null)
        {
            throw new ValueNotPresentException(key);
        }

        return value;
    }

    /**
     * Add a message for an unexpected exception case
     * 
     * @param key which prompted the message
     * @param errorMsg to add
     */
    private void addUnexpectedErrorMessage(String key, String errorMsg)
    {
        String msg = String.format("key [%s] resulted in unexpected error [%s]", key, errorMsg);

        messages.add(ModalMessage.builder().msg(msg).type(MessageType.ERROR).build());
    }

    /**
     * Add a message to the internal list
     * 
     * @param key which prompted the message
     * @param msg to add
     * @param type that was tried to consume
     */
    private void activateMarker(String key, MarkerCategory category, MarkerType type)
    {
        messages.add(
            ActivateMarkerMessage.builder()
                .formId(formId)
                .transferId(key)
                .markerCategory(category)
                .markerType(type)
                .type(MessageType.ERROR)
                .build());
    }

    /**
     * Extract a submitted {@code value} and consume it as a {@link String}. The {@code value} can be empty but not {@code null}.
     * <p>
     * The {@code consumer} will only be triggered if the {@code value} associated with {@code key} is non-null and can be parsed as the
     * designated type.
     * 
     * @param key to extract
     * @param consumer to feed value to
     * @return {@code this} for chaining
     * @throws IllegalArgumentException if key is {@code null} or empty
     * @throws ValueNotPresentException if the retrived {@code value} is {@code null}
     */
    public InputExtractor extractAndConsumeAsString(String key, Consumer<String> consumer)
    {
        extractAndConsumeAsString(key, null, consumer);

        return this;
    }

    /**
     * Extract a submitted {@code value} and consume it as a {@link String}. The {@code value} can <b>not</b> be empty or {@code null}.
     * 
     * @param key to extract
     * @param consumer to feed value to
     * @return {@code this} for chaining
     * @throws IllegalArgumentException if key is {@code null} or empty
     * @throws ValueNotPresentException if the retrived {@code value} is {@code null}
     */
    public InputExtractor extractAndConsumeAsNotEmptyString(String key, Consumer<String> consumer)
    {
        try
        {
            String value = getValue(key);

            if (value.isEmpty())
            {
                activateMarker(key, MarkerCategory.ERROR, MarkerType.EMPTY);

                return this;
            }

            consumer.accept(value);
        }
        catch (Exception ex)
        {
            addUnexpectedErrorMessage(key, ex.getMessage());
        }

        return this;
    }

    /**
     * Extract a submitted {@code value} and consume it as a {@link String}. The {@code value} <b>must</b> match the {@code pattern}.
     * <p>
     * The {@code consumer} will only be triggered if the {@code value} associated with {@code key} is non-null and can be parsed as the
     * designated type.
     * 
     * @param key to extract
     * @param pattern to apply
     * @param consumer to feed value to
     * @return {@code this} for chaining
     * @throws IllegalArgumentException if key is {@code null} or empty
     * @throws ValueNotPresentException if the retrived {@code value} is {@code null}
     */
    public InputExtractor extractAndConsumeAsString(String key, String pattern, Consumer<String> consumer)
    {
        try
        {
            String value = getValue(key);

            if (pattern != null)
            {
                Pattern pat = Pattern.compile(pattern);
                Matcher mat = pat.matcher(value);

                if (!mat.matches())
                {
                    activateMarker(key, MarkerCategory.ERROR, MarkerType.OUT_OF_RANGE);

                    return this;
                }
            }

            consumer.accept(value);
        }
        catch (Exception ex)
        {
            addUnexpectedErrorMessage(key, ex.getMessage());
        }

        return this;
    }

    /**
     * Extract a submitted {@code value} and consume it as a {@link Integer}.
     * <p>
     * The {@code consumer} will only be triggered if the {@code value} associated with {@code key} is non-null and can be parsed as the
     * designated type.
     * 
     * @param key to extract
     * @param consumer to feed value to
     * @return {@code this} for chaining
     * @throws IllegalArgumentException if key is {@code null} or empty
     * @throws ValueNotPresentException if the retrived {@code value} is {@code null}
     */
    public InputExtractor extractAndConsumeAsInteger(String key, Consumer<Integer> consumer)
    {
        extractAndConsumeAsInteger(key, null, null, consumer);

        return this;
    }

    /**
     * Extract a submitted {@code value} and consume it as a {@link Integer}. The {@code value} <b>must</b> conform inside
     * {@code lower}(inclusive) &lt;= {@code value} &lt;= {@code upper}(inclusive) range.
     * <p>
     * The {@code consumer} will only be triggered if the {@code value} associated with {@code key} is non-null and can be parsed as the
     * designated type.
     * 
     * @param key to extract
     * @param lowerBound of valid {@code value} (inclusive)
     * @param upperBound of valid {@code value} (inclusive)
     * @param consumer to feed value to
     * @return {@code this} for chaining
     * @throws IllegalArgumentException if key is {@code null} or empty
     * @throws ValueNotPresentException if the retrived {@code value} is {@code null}
     */
    public InputExtractor extractAndConsumeAsInteger(String key, Integer lowerBound, Integer upperBound, Consumer<Integer> consumer)
    {
        try
        {
            String  value = getValue(key);

            Integer i     = Integer.parseInt(value);

            if (lowerBound == null && upperBound == null)
            {
                consumer.accept(i);
            }
            else if (lowerBound != null && i >= lowerBound && upperBound != null && i <= upperBound)
            {
                consumer.accept(i);
            }
            else
            {
                activateMarker(key, MarkerCategory.ERROR, MarkerType.OUT_OF_RANGE);
            }
        }
        catch (Exception ex)
        {
            addUnexpectedErrorMessage(key, ex.getMessage());
        }

        return this;
    }

    /**
     * Extract a submitted {@code value} and consume it as a {@link Double}.
     * <p>
     * The {@code consumer} will only be triggered if the {@code value} associated with {@code key} is non-null and can be parsed as the
     * designated type.
     * 
     * @param key to extract
     * @param consumer to feed value to
     * @return {@code this} for chaining
     * @throws IllegalArgumentException if key is {@code null} or empty
     * @throws ValueNotPresentException if the retrived {@code value} is {@code null}
     */
    public InputExtractor extractAndConsumeAsDouble(String key, Consumer<Double> consumer)
    {
        return extractAndConsumeAsDouble(key, null, null, consumer);
    }

    /**
     * Extract a submitted {@code value} and consume it as a {@link Double}. The {@code value} <b>must</b> conform inside
     * {@code lower}(exclusive) &lt; {@code value} &lt; {@code upper}(exclusiv) range.
     * <p>
     * The {@code consumer} will only be triggered if the {@code value} associated with {@code key} is non-null and can be parsed as the
     * designated type.
     * 
     * @param key to extract
     * @param lowerBound of valid {@code value} (inclusive)
     * @param upperBound of valid {@code value} (inclusive)
     * @param consumer to feed value to
     * @return {@code this} for chaining
     * @throws IllegalArgumentException if key is {@code null} or empty
     * @throws ValueNotPresentException if the retrived {@code value} is {@code null}
     */
    public InputExtractor extractAndConsumeAsDouble(String key, Double lowerBound, Double upperBound, Consumer<Double> consumer)
    {
        try
        {
            String value = getValue(key);

            Double i     = Double.parseDouble(value);

            if (lowerBound == null && upperBound == null)
            {
                consumer.accept(i);
            }
            else if (lowerBound != null && i > lowerBound && upperBound != null && i < upperBound)
            {
                consumer.accept(i);
            }
            else
            {
                activateMarker(key, MarkerCategory.ERROR, MarkerType.OUT_OF_RANGE);
            }
        }
        catch (Exception ex)
        {
            addUnexpectedErrorMessage(key, ex.getMessage());
        }

        return this;
    }

    /**
     * Extract a submitted {@code value} and consume it as a {@link Date}. The {@code value} <b>must</b> be in the format
     * {@code yyyy-mm-dd}.
     * <p>
     * The {@code consumer} will only be triggered if the {@code value} associated with {@code key} is non-null and can be parsed as the
     * designated type.
     * 
     * @param key to extract
     * @param consumer to feed value to
     * @return {@code this} for chaining
     * @throws IllegalArgumentException if key is {@code null} or empty
     * @throws ValueNotPresentException if the retrived {@code value} is {@code null}
     */
    public InputExtractor extractAndConsumeAsDate(String key, Consumer<Date> consumer)
    {
        try
        {
            String value   = getValue(key);

            Date   sqlDate = Date.valueOf(value);

            consumer.accept(sqlDate);
        }
        catch (Exception ex)
        {
            addUnexpectedErrorMessage(key, ex.getMessage());
        }

        return this;
    }

    /**
     * Extract a submitted {@code value} and consume it as a {@link Enum}.
     * <p>
     * This function assumes that the {@code enumClass} constants conform to the default of all-caps form of enum names.
     * <p>
     * The {@code consumer} will only be triggered if the {@code value} associated with {@code key} is non-null and can be parsed as the
     * designated type.
     * 
     * @param <E> enum class type
     * @param key to extract
     * @param enumClass source of {@code Enum} values
     * @param consumer to feed value to
     * @return {@code this} for chaining
     * @throws IllegalArgumentException if key is {@code null} or empty
     * @throws ValueNotPresentException if the retrived {@code value} is {@code null}
     */
    public <E extends Enum<E>> InputExtractor extractAndConsumeAsEnum(String key, Class<E> enumClass, Consumer<E> consumer)
    {

        try
        {
            String value = getValue(key);

            E      e     = Arrays.stream(enumClass.getEnumConstants()).filter(t -> value.toUpperCase().equals(t.name())).findFirst().get();

            consumer.accept(e);
        }
        catch (NoSuchElementException ex)
        {
            activateMarker(key, MarkerCategory.ERROR, MarkerType.OUT_OF_RANGE);
        }
        catch (Exception ex)
        {
            addUnexpectedErrorMessage(key, ex.getMessage());
        }

        return this;
    }

    /**
     * Extract a submitted {@code value} and consume it as a {@link TagList}. The {@code value} <b>must</b> be in JSON format
     * {@code &#91;&#123;&quot;value&quot;:&quot;test 2&quot;&#125;, ...&#93;}.
     * <p>
     * A null or empty {@code value} will not be considered as a failure.
     * <p>
     * The {@code consumer} will only be triggered if the {@code value} associated with {@code key} is non-null and can be parsed as the
     * designated type.
     * 
     * @param key to extract
     * @param consumer to feed value to
     * @return {@code this} for chaining
     * @throws IllegalArgumentException if key is {@code null} or empty
     * @throws ValueNotPresentException if the retrived {@code value} is {@code null}
     */
    public InputExtractor extractAndConsumeAsTagList(String key, Consumer<TagList> consumer)
    {
        return extractAsTagList(key, consumer, false);
    }

    /**
     * Extract a submitted {@code value} and consume it as a {@link TagList}. The {@code value} <b>must</b> be in JSON format
     * {@code &#91;&#123;&quot;value&quot;:&quot;test 2&quot;&#125;, ...&#93;}. The {@code value} can <b>not</b> be empty or {@code null}.
     * <p>
     * The {@code consumer} will only be triggered if the {@code value} associated with {@code key} is non-null and can be parsed as the
     * designated type.
     * 
     * @param key to extract
     * @param consumer to feed value to
     * @return {@code this} for chaining
     * @throws IllegalArgumentException if key is {@code null} or empty
     * @throws ValueNotPresentException if the retrived {@code value} is {@code null}
     */
    public InputExtractor extractAndConsumeAsNotEmptyTagList(String key, Consumer<TagList> consumer)
    {
        return extractAsTagList(key, consumer, true);
    }

    /**
     * Extract a submitted {@code value} and consume it as a {@link TagList}. The {@code value} <b>must</b> be in JSON format
     * {@code &#91;&#123;&quot;value&quot;:&quot;test 2&quot;&#125;, ...&#93;}.
     * <p>
     * The {@code consumer} will only be triggered if the {@code value} associated with {@code key} is non-null and can be parsed as the
     * designated type.
     * 
     * @param key to extract
     * @param consumer to feed value to
     * @param raiseNullOrEmtpy wheter to activate a marker on {@code null} or empty {@code value}
     * @return {@code this} for chaining
     * @throws IllegalArgumentException if key is {@code null} or empty
     * @throws ValueNotPresentException if the retrived {@code value} is {@code null}
     */
    private InputExtractor extractAsTagList(String key, Consumer<TagList> consumer, boolean raiseNullOrEmtpy)
    {
        try
        {
            String value = getValue(key);

            if (value == null || value.isEmpty())
            {
                if (raiseNullOrEmtpy)
                {
                    activateMarker(key, MarkerCategory.ERROR, MarkerType.EMPTY);
                }

                return this;
            }

            TagList list = TAG_LIST_MAPPER.readValue(value, TagList.class);

            consumer.accept(list);
        }
        catch (Exception ex)
        {
            addUnexpectedErrorMessage(key, ex.getMessage());
        }

        return this;
    }

    /**
     * Check if {@code files} contain something.
     * <p>
     * This function assumes that the file transfer name - {@code submitAs} on the {@link File} input - is named {@code files} and will
     * activate the marker only on this
     * <p>
     * This function only checks <b>if</b> files are present, but <b>nothing</b> about their state. This is up to the developer.
     * 
     * @param key to bind error messages to file field
     * @param nullable true, if {@code files} is allowed to be {@code null} or {@code empty}
     * @param multipleAllowed true, if more than one file can be uploaded
     * @return {@code this} for chaining
     * @throws IllegalArgumentException if key is {@code null} or empty
     */
    public InputExtractor checkFiles(String key, boolean nullable, boolean multipleAllowed)
    {
        if (key == null || key.isBlank())
        {
            throw new IllegalArgumentException("key cannot be null/empty");
        }

        if (files == null || files.length == 0)
        {
            if (!nullable)
            {
                activateMarker(key, MarkerCategory.ERROR, MarkerType.EMPTY);
            }
        }
        else if (!multipleAllowed && files.length > 1)
        {
            activateMarker(key, MarkerCategory.ERROR, MarkerType.TOO_LONG);
        }

        return this;
    }

    /**
     * Check whether this extractor has raised any markers or unexpected exceptions
     * 
     * @return true, if there are messages raise, false otherwise
     */
    public Boolean hasMessages()
    {
        return !messages.isEmpty();
    }

    /**
     * Get a unmodifiable list of {@link ResponseMessage}s raised by the extractions
     * 
     * @return list of messages
     */
    public List<ResponseMessage> getMessages()
    {
        return Collections.unmodifiableList(messages);
    }
}
