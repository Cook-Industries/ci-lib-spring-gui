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

import de.cookindustries.lib.spring.gui.hmi.container.FormContainer;
import de.cookindustries.lib.spring.gui.hmi.input.File;
import de.cookindustries.lib.spring.gui.hmi.input.Input;
import de.cookindustries.lib.spring.gui.response.message.HighlightMessage;
import de.cookindustries.lib.spring.gui.response.message.MessageTarget;
import de.cookindustries.lib.spring.gui.response.message.MessageType;
import de.cookindustries.lib.spring.gui.response.message.ModalMessage;
import de.cookindustries.lib.spring.gui.response.message.ResponseMessage;

/**
 * Utility to extract information from {@code Form} data into data objects.
 * <p>
 * A {@code InputExtractor} is supposed to run as a helper to streamline the validation and retrival-of-data process. It is intended to work
 * with DTO objects either as POJOs with setters, or a builder.
 * <p>
 * A {@code InputExtractor} can olso validate a {@link MultipartFile} intended as a file upload from a {@link File}-input. This validation
 * only checks whether there are any files uploaded and how many, but nothing about the state or value of the files, since this is up to the
 * developer.
 * <p>
 * Output of validation is given in the form of {@link HighlightMessage}s bound to {@link Marker} definitions on the {@link Input}, or
 * alternativly as {@link ModalMessage}s with more descriptive error messages.
 * <p>
 * Example:
 * 
 * <pre>
 * 
 * public NotificationResponse handleFrom(MultiValueMap&lt;String, String&gt; formData, MultipartFile[] files)
 * {
 *     InputExtractor extractor = new InputExtractor(inputs, files);
 *     TransformDtoBuilder dtoBuilder = TransformDto.builder();
 * 
 *     extractor.consumeString("mail", dtoBuilder::mail);
 *     extractor.consumeString("name", dtoBuilder::name);
 *     extractor.consumeEnum("resultType", ImageType.class, dtoBuilder::resultType);
 *     extractor.checkFiles(false, true);
 * 
 *     if (extractor.hasMessages())
 *     {
 *         return NotificationResponse
 *             .builder()
 *             .messages(extractor.getMessages())
 *             .build();
 *     }
 * 
 *     TransformDto transformData = dtoBuilder.build();
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

    private final String                        formId;
    private final MultiValueMap<String, String> inputs;
    private final MultipartFile[]               files;
    private final List<ResponseMessage>         messages = new ArrayList<>();
    private final Boolean                       useModalMessages;

    /**
     * Create a extractor for a {@link FormContainer} result
     * <p>
     * It is assumed that the {@code key} {@code __form_id} exists inside {@code inputs} and is non-null.
     * 
     * @param inputs from the {@code Form}
     * @throws IllegalArgumentException if {@code key} {@code __form_id} id {@code null} or empty
     */
    public InputExtractor(MultiValueMap<String, String> inputs)
    {
        this(inputs, null, false);
    }

    /**
     * Create a extractor for a {@link FormContainer} result
     * <p>
     * It is assumed that the {@code key} {@code __form_id} exists inside {@code inputs} and is non-null.
     * 
     * @param inputs from the {@code Form}
     * @param files from the {@code Form}
     * @throws IllegalArgumentException if {@code key} {@code __form_id} id {@code null} or empty
     */
    public InputExtractor(MultiValueMap<String, String> inputs, MultipartFile[] files)
    {
        this(inputs, files, false);
    }

    /**
     * Create a extractor for this
     * <p>
     * It is assumed that the {@code key} {@code __form_id} exists inside {@code inputs} and is non-null.
     * 
     * @param inputs from the {@code Form}
     * @param files from the {@code Form}
     * @param useModalMessages true, if the reporting messages should be shown in a {@link MessageTarget#MODAL} instead of
     *            {@link MessageTarget#MARKER}
     * @throws IllegalArgumentException if {@code key} {@code __form_id} id {@code null} or empty
     */
    public InputExtractor(MultiValueMap<String, String> inputs, MultipartFile[] files, Boolean useModalMessages)
    {
        this.inputs = inputs;
        this.files = files;
        this.useModalMessages = useModalMessages;

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
     */
    private String getValue(String key)
    {
        if (key == null || key.isBlank())
        {
            throw new IllegalArgumentException("key cannot be null/empty");
        }

        String value = inputs.getFirst(key);

        return value;
    }

    /**
     * Check if the {@code key} is associated with a value, if not add a {@link ResponseMessage} to the internal {@code message} list
     * 
     * @param key to associate
     * @param value to check
     */
    private void checkValue(String key, String value)
    {
        if (value == null)
        {
            throw new IllegalArgumentException(String.format("value retrival to key [%s] is expected but returned null", key));
        }
    }

    /**
     * Consume a {@link String}
     * <p>
     * The {@code consumer} will only be triggered if the value associated with {@code key} is non-null.
     * 
     * @param key to look-up
     * @param consumer to feed
     * @return {@code this} for chaining
     */
    public InputExtractor consumeString(String key, Consumer<String> consumer)
    {
        consumeString(key, null, consumer);

        return this;
    }

    /**
     * Consume a {@link String} that conforms to a regex pattern
     * <p>
     * The {@code consumer} will only be triggered if the value associated with {@code key} is non-null and matches {@code pattern}.
     * 
     * @param key to look-up
     * @param pattern to apply
     * @param consumer to feed
     * @return {@code this} for chaining
     */
    public InputExtractor consumeString(String key, String pattern, Consumer<String> consumer)
    {
        String value = getValue(key);

        try
        {
            checkValue(key, value);

            if (pattern != null)
            {
                Pattern pat = Pattern.compile(pattern);
                Matcher mat = pat.matcher(value);

                if (!mat.matches())
                {
                    String msg = String.format("key [%s] value [%s] does not match pattern [%s]", key, value, pat);
                    addMessage(key, msg, MessageType.ERROR);

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
     * Consume a {@link Integer}
     * <p>
     * The {@code consumer} will only be triggered if the value associated with {@code key} is non-null.
     * 
     * @param key to look-up
     * @param consumer to feed
     * @return {@code this} for chaining
     */
    public InputExtractor consumeInteger(String key, Consumer<Integer> consumer)
    {
        consumeInteger(key, null, null, consumer);

        return this;
    }

    /**
     * Consume a {@link Integer} conforming to {@code lower}(inclusive) &gt;= value &lt;= {@code upper}(inclusive) bound range
     * <p>
     * The {@code consumer} will only be triggered if the value associated with {@code key} is non-null.
     * 
     * @param key to look-up
     * @param lowerBound of valid value (inclusive)
     * @param upperBound of valid value (inclusive)
     * @param consumer to feed
     * @return {@code this} for chaining
     */
    public InputExtractor consumeInteger(String key, Integer lowerBound, Integer upperBound, Consumer<Integer> consumer)
    {
        String value = getValue(key);;

        try
        {
            checkValue(key, value);

            Integer i = Integer.parseInt(value);

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
                String msg = String.format("key [%s] with value [%s] is out of range of [%s]/[%s]", key, value, lowerBound, upperBound);
                addMessage(key, msg, MessageType.ERROR);
            }
        }
        catch (NumberFormatException ex)
        {
            String msg = String.format("key [%s] value [%s] cannot be parsed as [Integer]", key, value);
            addMessage(key, msg, MessageType.ERROR);
        }
        catch (Exception ex)
        {
            addUnexpectedErrorMessage(key, ex.getMessage());
        }

        return this;
    }

    /**
     * Consume a {@link Double}
     * <p>
     * The {@code consumer} will only be triggered if the value associated with {@code key} is non-null.
     * 
     * @param key to look-up
     * @param consumer to feed
     * @return {@code this} for chaining
     */
    public InputExtractor consumeDouble(String key, Consumer<Double> consumer)
    {
        String value = getValue(key);

        try
        {
            checkValue(key, value);

            Double d = Double.parseDouble(value);

            consumer.accept(d);
        }
        catch (NumberFormatException ex)
        {
            String msg = String.format("key [%s] value [%s] cannot be parsed as [Double]", key, value);
            addMessage(key, msg, MessageType.ERROR);
        }
        catch (Exception ex)
        {
            addUnexpectedErrorMessage(key, ex.getMessage());
        }

        return this;
    }

    /**
     * Extract a {@link Date} in the format yyyy-mm-dd
     * 
     * @param key to look-up
     * @return {@code this} for chaining
     */
    public InputExtractor consumeDate(String key, Consumer<Date> consumer)
    {
        String value = getValue(key);

        try
        {
            checkValue(key, value);

            Date sqlDate = Date.valueOf(value);

            consumer.accept(sqlDate);
        }
        catch (Exception ex)
        {
            addUnexpectedErrorMessage(key, ex.getMessage());
        }

        return this;
    }

    /**
     * Extract a {@link Enum}
     * 
     * @param <E> enum class
     * @param key to look-up
     * @param source of enums
     * @param consumer to feed
     * @return {@code this} for chaining
     */
    public <E extends Enum<E>> InputExtractor consumeEnum(String key, Class<E> source, Consumer<E> consumer)
    {
        String value = getValue(key);

        try
        {
            checkValue(key, value);

            E e = Arrays.stream(source.getEnumConstants()).filter(t -> value.toUpperCase().equals(t.name())).findFirst().get();

            consumer.accept(e);
        }
        catch (NoSuchElementException ex)
        {
            String msg = String.format("key [%s] is not part of enum [%s]", key, source.getSimpleName());
            addMessage(key, msg, MessageType.ERROR);
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
     * Tests, based on the parameters, if a error message should be raised or not regarding an empty {@code files} array.
     * 
     * @param nullable true, if null/empty is an allowed value
     * @param multipleAllowed true, if more than one file can be uploaded
     * @return {@code this} for chaining
     */
    public InputExtractor checkFiles(Boolean nullable, Boolean multipleAllowed)
    {
        if (files == null || files.length == 0)
        {
            if (!nullable)
            {
                addMessage(formId, "files are null/empty but expected", MessageType.ERROR);
            }
        }
        else if (files.length > 1 && !multipleAllowed)
        {
            addMessage(formId, "more than one file uploaded yet only one expected", MessageType.ERROR);
        }

        return this;
    }

    /**
     * Add a message for an unexpected exception case
     * 
     * @param key which prompted the message
     * @param errorMsg to add
     */
    private void addUnexpectedErrorMessage(String key, String errorMsg)
    {
        addMessage(key, String.format("key [%s] resulted in unexpected error [%s]", key, errorMsg), MessageType.ERROR);
    }

    /**
     * Add a message to the internal list
     * 
     * @param key which prompted the message
     * @param msg to add
     * @param type that was tried to consume
     */
    private void addMessage(String key, String msg, MessageType type)
    {
        if (useModalMessages)
        {
            messages.add(ModalMessage.builder().msg(msg).type(type).build());
        }
        else
        {
            messages.add(HighlightMessage.builder().formId(formId).fieldId(key).type(type).build());
        }
    }

    /**
     * Check whether this extractor has raised any messages
     * 
     * @return true, if there are messages raise, false otherwise
     */
    public Boolean hasMessages()
    {
        return !messages.isEmpty();
    }

    /**
     * Get a unmodifiable list of {@link ResponseMessage}s raised by the consumations
     * 
     * @return list of messages
     */
    public List<ResponseMessage> getMessages()
    {
        return Collections.unmodifiableList(messages);
    }
}
