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
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import de.cookindustries.lib.spring.gui.config.CiLibInputExtractionExceptionHandler;
import de.cookindustries.lib.spring.gui.hmi.container.FormContainer;
import de.cookindustries.lib.spring.gui.hmi.input.File;
import de.cookindustries.lib.spring.gui.hmi.input.util.exception.InputExtractionException;
import de.cookindustries.lib.spring.gui.hmi.input.util.exception.NullIgnoreException;
import de.cookindustries.lib.spring.gui.response.NotificationResponse;

/**
 * Utility to extract information from {@code Form} data into data objects.
 * <p>
 * A {@code InputExtractor} is supposed to run as a helper to streamline the validation and retrival-of-data process. It is intended to work
 * with DTO objects either as POJOs with setters, or a builder.
 * <p>
 * Depending on the data types of the fields the data can be validated via the corresponding {@link AbsInputProcessor}. Either create a new
 * on with the restrictions that apply, or use on of the static defined ones in this class named with {@code DEFAULT_[type]}.
 * <p>
 * A {@code InputExtractor} can also validate a {@link MultipartFile} intended as a file upload from a {@link File} input. This validation
 * only checks whether there are any files uploaded and how many, but nothing about the state or value of the files, since this is up to the
 * developer.
 * <p>
 * Example:
 * 
 * <pre>
 * 
 * public Response handleForm(MultiValueMap&lt;String, String&gt; formData, MultipartFile[] files)
 * {
 *     Locale locale = Locale.ENGLISH;
 *     InputExtractor extractor = new InputExtractor(inputs, files);
 *     DtoBuilder dtoBuilder = Dto.builder();
 * 
 *     extractor.consumeString("mail", dtoBuilder::mail);
 *     extractor.consumeString("name", dtoBuilder::name);
 *     extractor.consumeEnum("resultType", ImageType.class, dtoBuilder::resultType);
 *     extractor.checkFiles(false, true);
 * 
 *     if (extractor.hasMarker())
 *     {
 *         return guiFactory.getActiveMarkerResponse(extractor, locale);
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

    private static final Logger                 LOG                                 = LoggerFactory.getLogger(InputExtractor.class);

    /** A default String input processor. Allows empty {@code Strings} and imposes no restrictions. */
    public static final StringInputProcessor    DEFAULT_STRING_PROCESSOR            =
        StringInputProcessor
            .builder()
            .build();

    /** A default String input processor. Rejects empty {@code Strings} and imposes no other restrictions. */
    public static final StringInputProcessor    DEFAULT_STRING_NOT_EMPTY_PROCESSOR  =
        StringInputProcessor
            .builder()
            .allowEmpty(false)
            .build();

    /** A default {@code Boolean} input processor. Defaults to {@code false}. */
    public static final BooleanInputProcessor   DEFAULT_FALSE_BOOLEAN_PROCESSOR     =
        BooleanInputProcessor
            .builder()
            .fallback(false)
            .build();

    /** A default {@code Boolean} input processor. Defaults to {@code true}. */
    public static final BooleanInputProcessor   DEFAULT_TRUE_BOOLEAN_PROCESSOR      =
        BooleanInputProcessor
            .builder()
            .fallback(true)
            .build();

    /** A default {@code Integer} input processor. Imposes no restrictions. */
    public static final IntegerInputProcessor   DEFAULT_INTEGER_PROCESSOR           =
        IntegerInputProcessor
            .builder()
            .build();

    /** A default {@code Double} input processor. Imposes no restrictions. */
    public static final DoubleInputProcessor    DEFAULT_DOUBLE_PROCESSOR            =
        DoubleInputProcessor
            .builder()
            .build();

    /** A default {@code Date} input processor. Imposes no restrictions. */
    public static final DateInputProcessor      DEFAULT_DATE_PROCESSOR              =
        DateInputProcessor
            .builder()
            .build();

    /** A default {@code TagList} input processor. Imposes no restrictions. */
    public static final TagListInputProcessor   DEFAULT_TAGLIST_PROCESSOR           =
        TagListInputProcessor
            .builder()
            .build();

    /** A default {@code TagList} input processor. Fails on no tags given. Imposes no other restrictions. */
    public static final TagListInputProcessor   DEFAULT_NON_EMPTY_TAGLIST_PROCESSOR =
        TagListInputProcessor
            .builder()
            .allowEmpty(false)
            .build();

    private final Locale                        locale;
    private final String                        formId;
    private final MultiValueMap<String, String> inputs;
    private final MultipartFile[]               files;
    private final List<InputCheckMarker>        marker                              = new ArrayList<>();

    /**
     * Create a extractor for a inputs from a {@link FormContainer} request
     * <p>
     * It is assumed that the {@code key} <b>__form_id</b> exists inside {@code inputs} and is non-null.
     * 
     * @param inputs from the {@code Form}
     * @throws IllegalArgumentException if {@code key} <b>__form_id</b> {@code null} or empty
     */
    public InputExtractor(MultiValueMap<String, String> inputs)
    {
        this(inputs, null, Locale.ENGLISH);
    }

    /**
     * Create a extractor for a inputs from a {@link FormContainer} request
     * <p>
     * It is assumed that the {@code key} <b>__form_id</b> exists inside {@code inputs} and is non-null.
     * 
     * @param inputs from the {@code Form}
     * @param locale to fetch error messages with
     * @throws IllegalArgumentException if {@code key} <b>__form_id</b> is {@code null} or empty
     */
    public InputExtractor(MultiValueMap<String, String> inputs, Locale locale)
    {
        this(inputs, null, locale);
    }

    /**
     * Create a extractor for a inputs from a {@link FormContainer} request
     * <p>
     * It is assumed that the {@code key} <b>__form_id</b> exists inside {@code inputs} and is non-null.
     * 
     * @param inputs from the {@code Form}
     * @param files from the {@code Form}
     * @throws IllegalArgumentException if {@code key} <b>__form_id</b> is {@code null} or empty
     */
    public InputExtractor(MultiValueMap<String, String> inputs, MultipartFile[] files)
    {
        this(inputs, files, Locale.ENGLISH);
    }

    /**
     * Create a extractor for a inputs from a {@link FormContainer} request
     * <p>
     * This object expects that the {@code key} <b>__form_id</b> exists inside {@code inputs} and is non-null.
     * 
     * @param inputs from the {@code Form}
     * @param files from the {@code Form}
     * @param locale to fetch error messages with
     * @throws IllegalArgumentException if {@code key} <b>__form_id</b> is {@code null} or empty
     */
    public InputExtractor(MultiValueMap<String, String> inputs, MultipartFile[] files, Locale locale)
    {
        this.inputs = inputs;
        this.files = files;
        this.locale = locale;

        formId = (String) inputs.getFirst("__form_id");

        if (formId == null || formId.isBlank())
        {
            throw new IllegalArgumentException("key [__form_id] cannot be null/empty");
        }

        if (LOG.isDebugEnabled())
        {
            LOG.debug("inputs given:");

            inputs
                .entrySet()
                .stream()
                .forEach(ent -> LOG.debug("{}:{}", String.format("%-20s", ent.getKey()), ent.getValue()));

            LOG.debug("files given: [{}]", files != null ? files.length : "none");
        }
    }

    public Locale getLocale()
    {
        return locale;
    }

    /**
     * Extract the value, parse and check it and give appropriate feedback.
     * 
     * @param <T> the type of expected result
     * @param key to lookup
     * @param processor to apply
     * @param consumer to feed
     */
    private <T> void extractAndConsume(String key, AbsInputProcessor<T> processor, Consumer<T> consumer)
    {
        try
        {
            if (key == null || key.isBlank())
            {
                throw new IllegalArgumentException("key cannot be null/empty");
            }

            String               value      = inputs.getFirst(key);

            InputCheckResult<T>  result     = processor.process(value);

            InputCheckResultType resultType = result.getType();

            switch (resultType)
            {
                case PASS -> {
                    T obj = result.getResult();

                    consumer.accept(obj);
                }

                default -> activateMarker(key, MarkerCategory.ERROR, resultType.getMarkerType());
            };
        }
        catch (NullIgnoreException ex)
        {
            // value to parse is null, but this will be ignored and not raise a marker
        }
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
        marker.add(
            InputCheckMarker
                .builder()
                .formId(formId)
                .transferId(key)
                .category(category)
                .type(type)
                .build());
    }

    /**
     * Final check for this {@code InputExtractor}. If {@code markers} where raised it throws an {@link InputExtractionException}.
     * <p>
     * Under normal conditions this should be captured by {@link CiLibInputExtractionExceptionHandler} and implicitly create a
     * {@link NotificationResponse} which will trigger the highlight markers in the UI.
     * 
     * @throws InputExtractionException if {@code this} extractor has raised any markers
     */
    public void approve()
    {
        if (hasMarker())
        {
            throw new InputExtractionException(this);
        }
    }

    /**
     * Extract a submitted {@code value} and consume it as a {@link String}.
     * <p>
     * The {@code consumer} will only be triggered if the {@code value} associated with {@code key} is non-null.
     * 
     * @param key to extract
     * @param consumer to feed value to
     * @return {@code this} for chaining
     */
    public InputExtractor checkAndConsumeAsString(String key, Consumer<String> consumer)
    {
        return checkAndConsumeAsString(key, DEFAULT_STRING_PROCESSOR, consumer);
    }

    /**
     * Extract a submitted {@code value} and consume it as a {@link String}.
     * <p>
     * The {@code consumer} will only be triggered if the {@code value} associated with {@code key} is is non-null and <b>not</b> empty.
     * 
     * @param key to extract
     * @param consumer to feed value to
     * @return {@code this} for chaining
     */
    public InputExtractor checkAndConsumeAsNotEmptyString(String key, Consumer<String> consumer)
    {
        return checkAndConsumeAsString(key, DEFAULT_STRING_NOT_EMPTY_PROCESSOR, consumer);
    }

    /**
     * Extract a submitted {@code value} and consume it as a {@link String}.
     * <p>
     * The {@code consumer} will only be triggered if the {@code value} associated with {@code key} clears all checks from the
     * {@link StringInputProcessor}.
     * 
     * @param key to extract
     * @param processor to apply
     * @param consumer to feed value to
     * @return {@code this} for chaining
     */
    public InputExtractor checkAndConsumeAsString(String key, StringInputProcessor processor, Consumer<String> consumer)
    {
        extractAndConsume(key, processor, consumer);

        return this;
    }

    /**
     * Extract a submitted {@code value} and consume it as a {@link Boolean}.
     * <p>
     * If the {@code value} is non-null, it gets parsed as {@link Boolean}, if not, it will default to {@code false}.
     * 
     * @param key to extract
     * @param consumer to feed value to
     * @return {@code this} for chaining
     * @since 2.4.0
     */
    public InputExtractor checkAndConsumeAsBooleanDefaultFalse(String key, Consumer<Boolean> consumer)
    {
        return checkAndConsumeAsBoolean(key, DEFAULT_FALSE_BOOLEAN_PROCESSOR, consumer);
    }

    /**
     * Extract a submitted {@code value} and consume it as a {@link Boolean}.
     * <p>
     * If the {@code value} is non-null, it gets parsed as {@link Boolean}. if not, it will default to {@code true}.
     * 
     * @param key to extract
     * @param consumer to feed value to
     * @return {@code this} for chaining
     * @since 2.4.0
     */
    public InputExtractor checkAndConsumeAsBooleanDefaultTrue(String key, Consumer<Boolean> consumer)
    {
        return checkAndConsumeAsBoolean(key, DEFAULT_TRUE_BOOLEAN_PROCESSOR, consumer);
    }

    /**
     * Extract a submitted {@code value} and consume it as a {@link Boolean}.
     * <p>
     * The {@code consumer} will only be triggered if the {@code value} associated with {@code key} is non-null, can be parsed as a
     * {@link Boolean} and clears all checks from the {@link BooleanInputProcessor}.
     * 
     * @param key to extract
     * @param processor to apply
     * @param consumer to feed value to
     * @return {@code this} for chaining
     * @since 2.4.0
     */
    private InputExtractor checkAndConsumeAsBoolean(String key, BooleanInputProcessor processor, Consumer<Boolean> consumer)
    {
        extractAndConsume(key, processor, consumer);

        return this;
    }

    /**
     * Extract a submitted {@code value} and consume it as a {@link Integer}.
     * <p>
     * The {@code consumer} will only be triggered if the {@code value} associated with {@code key} is non-null, can be parsed as a
     * {@link Integer}.
     * 
     * @param key to extract
     * @param consumer to feed value to
     * @return {@code this} for chaining
     */
    public InputExtractor checkAndConsumeAsInteger(String key, Consumer<Integer> consumer)
    {
        return checkAndConsumeAsInteger(key, DEFAULT_INTEGER_PROCESSOR, consumer);
    }

    /**
     * Extract a submitted {@code value} and consume it as a {@link Integer}. The {@code value} <b>must</b> conform inside
     * {@code lower}(inclusive) &lt;= {@code value} &lt;= {@code upper}(inclusive) range.
     * <p>
     * The {@code consumer} will only be triggered if the {@code value} associated with {@code key} is non-null, can be parsed as a
     * {@link Integer} and clears all checks from the {@link IntegerInputProcessor}.
     * 
     * @param key to extract
     * @param processor to apply
     * @param consumer to feed value to
     * @return {@code this} for chaining
     */
    public InputExtractor checkAndConsumeAsInteger(String key, IntegerInputProcessor processor, Consumer<Integer> consumer)
    {
        extractAndConsume(key, processor, consumer);

        return this;
    }

    /**
     * Extract a submitted {@code value} and consume it as a {@link Double}.
     * <p>
     * The {@code consumer} will only be triggered if the {@code value} associated with {@code key} is non-null, can be parsed as a
     * {@link Integer}.
     * 
     * @param key to extract
     * @param consumer to feed value to
     * @return {@code this} for chaining
     */
    public InputExtractor checkAndConsumeAsDouble(String key, Consumer<Double> consumer)
    {
        return checkAndConsumeAsDouble(key, DEFAULT_DOUBLE_PROCESSOR, consumer);
    }

    /**
     * Extract a submitted {@code value} and consume it as a {@link Double}.
     * <p>
     * The {@code consumer} will only be triggered if the {@code value} associated with {@code key} is non-null, can be parsed as a
     * {@link Double} and clears all checks from the {@link DoubleInputProcessor}.
     * 
     * @param key to extract
     * @param processor to apply
     * @param consumer to feed value to
     * @return {@code this} for chaining
     */
    public InputExtractor checkAndConsumeAsDouble(String key, DoubleInputProcessor processor, Consumer<Double> consumer)
    {
        extractAndConsume(key, processor, consumer);

        return this;
    }

    /**
     * Extract a submitted {@code value} and consume it as a {@link Date}. The {@code value} <b>must</b> be in the format
     * {@code yyyy-mm-dd}.
     * <p>
     * The {@code consumer} will only be triggered if the {@code value} associated with {@code key} is non-null and can be parsed as a
     * {@link Date}.
     * 
     * @param key to extract
     * @param consumer to feed value to
     * @return {@code this} for chaining
     */
    public InputExtractor checkAndConsumeAsDate(String key, Consumer<Date> consumer)
    {
        return checkAndConsumeAsDate(key, DEFAULT_DATE_PROCESSOR, consumer);
    }

    /**
     * Extract a submitted {@code value} and consume it as a {@link Date}. The {@code value} <b>must</b> be in the format
     * {@code yyyy-mm-dd}.
     * <p>
     * The {@code consumer} will only be triggered if the {@code value} associated with {@code key} is non-null, can be parsed as a
     * {@link Double} and clears all checks from the {@link DoubleInputProcessor}.
     * 
     * @param key to extract
     * @param processor to apply
     * @param consumer to feed value to
     * @return {@code this} for chaining
     */
    public InputExtractor checkAndConsumeAsDate(String key, DateInputProcessor processor, Consumer<Date> consumer)
    {
        extractAndConsume(key, processor, consumer);

        return this;
    }

    /**
     * Extract a submitted {@code value} and consume it as a {@link Enum}.
     * <p>
     * This function assumes that the {@code enumClass} constants conform to the default of all-caps no spaces form of enum names.
     * <p>
     * The {@code consumer} will only be triggered if the {@code value} associated with {@code key} is non-null and can be parsed as the
     * given {@code Enum} type.
     * 
     * @param <E> enum class type
     * @param key to extract
     * @param enumClass source of {@code Enum} values
     * @param consumer to feed value to
     * @return {@code this} for chaining
     */
    public <E extends Enum<E>> InputExtractor checkAndConsumeAsEnum(String key, Class<E> enumClass, Consumer<E> consumer)
    {
        extractAndConsume(
            key,
            EnumInputProcessor
                .<E>builder()
                .enumClass(enumClass)
                .build(),
            consumer);

        return this;
    }

    /**
     * Extract a submitted {@code value} and consume it as a {@link TagList}. The {@code value} <b>must</b> be in JSON format
     * {@code &#91;&#123;&quot;value&quot;:&quot;test 2&quot;&#125;, ...&#93;}.
     * <p>
     * A empty {@code value} will <b>not</b> be considered as a failure.
     * 
     * @param key to extract
     * @param consumer to feed value to
     * @return {@code this} for chaining
     */
    public InputExtractor checkAndConsumeAsTagList(String key, Consumer<TagList> consumer)
    {
        return extractAsTagList(key, DEFAULT_TAGLIST_PROCESSOR, consumer);
    }

    /**
     * Extract a submitted {@code value} and consume it as a {@link TagList}. The {@code value} <b>must</b> be in JSON format
     * {@code &#91;&#123;&quot;value&quot;:&quot;test 2&quot;&#125;, ...&#93;}. The {@code value} can <b>not</b> be empty or {@code null}.
     * <p>
     * The {@code consumer} will only be triggered if the {@code value} associated with {@code key} is non-null and can be parsed as a
     * {@code TagList}.
     * 
     * @param key to extract
     * @param consumer to feed value to
     * @return {@code this} for chaining
     */
    public InputExtractor checkAndConsumeAsNotEmptyTagList(String key, Consumer<TagList> consumer)
    {
        return extractAsTagList(key, DEFAULT_NON_EMPTY_TAGLIST_PROCESSOR, consumer);
    }

    /**
     * Extract a submitted {@code value} and consume it as a {@link TagList}. The {@code value} <b>must</b> be in JSON format
     * {@code &#91;&#123;&quot;value&quot;:&quot;test 2&quot;&#125;, ...&#93;}.
     * <p>
     * The {@code consumer} will only be triggered if the {@code value} associated with {@code key} is non-null and can be parsed as a
     * {@code TagList} and clears all checks from the {@link TagListInputProcessor}.
     * 
     * @param key to extract
     * @param consumer to feed value to
     * @param raiseNullOrEmtpy wheter to activate a marker on {@code null} or empty {@code value}
     * @return {@code this} for chaining
     */
    private InputExtractor extractAsTagList(String key, TagListInputProcessor processor, Consumer<TagList> consumer)
    {
        extractAndConsume(key, processor, consumer);

        return this;
    }

    /**
     * Check if {@code files} contain something.
     * <p>
     * This function <b>only</b> checks if files are present, but <b>nothing</b> about their state. This is up to the developer.
     * 
     * @param key to bind error messages to file field on {@code submitAs}
     * @param nullable true, if {@code files} is allowed to be {@code null} or {@code empty}
     * @param multipleAllowed true, if more than one file can be uploaded
     * @return {@code this} for chaining
     */
    public InputExtractor checkFiles(String key, boolean nullable, boolean multipleAllowed)
    {
        if (key == null || key.isBlank())
        {
            throw new IllegalArgumentException("key cannot be null/empty");
        }

        if (files == null || files.length == 0)
        {
            if (!nullable) // this needs to be in its own if since the null check against files will trigger
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
     * @return true, if there are markers raise, false otherwise
     */
    public boolean hasMarker()
    {
        return !marker.isEmpty();
    }

    /**
     * Get a unmodifiable list of {@link InputCheckMarker}s raised by the extractions
     * 
     * @return list of messages
     */
    public List<InputCheckMarker> getMarker()
    {
        return Collections.unmodifiableList(marker);
    }
}
