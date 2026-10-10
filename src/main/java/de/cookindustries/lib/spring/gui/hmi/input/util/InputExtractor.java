/**
 * Copyright (c) 2016-2026 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.hmi.input.util;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.cookindustries.lib.spring.gui.hmi.container.FormContainer;
import de.cookindustries.lib.spring.gui.hmi.input.File;
import de.cookindustries.lib.spring.gui.hmi.input.util.exception.FileUploadStartException;
import de.cookindustries.lib.spring.gui.hmi.input.util.exception.InputExtractionException;
import de.cookindustries.lib.spring.gui.hmi.input.util.exception.NullIgnoredException;
import de.cookindustries.lib.spring.gui.response.NotificationResponse;
import de.cookindustries.lib.spring.gui.response.Response;
import de.cookindustries.lib.spring.gui.util.file.FileUploadCoordinator;
import de.cookindustries.lib.spring.gui.util.file.FileUploadCoordinator.FileDeclaration;
import de.cookindustries.lib.spring.gui.util.file.FileUploadCoordinator.StartResult;

/**
 * Utility to extract information from {@code Form} data into data objects.
 * <p>
 * A {@code InputExtractor} is supposed to run as a helper to streamline the validation and retrival-of-data process. It is intended to work
 * with DTO objects either as POJOs with setters, or a builder.
 * <p>
 * Depending on the data types of the fields the data can be validated via the corresponding {@link AbsInputProcessor}. Either create a new
 * on with the restrictions that apply, or use one of the static defined ones in this class named {@code DEFAULT_[type]}.
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
 *     extractor.approve(); // throws if markers are raised
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
public final class InputExtractor<R>
{

    private static final Logger               LOG                                 = LoggerFactory.getLogger(InputExtractor.class);
    private static final ObjectMapper         MAPPER                              = new ObjectMapper();

    /** A default String input processor. Allows empty {@code Strings} and imposes no restrictions. */
    public static final StringInputProcessor  DEFAULT_STRING_PROCESSOR            =
        StringInputProcessor
            .builder()
            .build();

    /** A default String input processor. Rejects empty {@code Strings} and imposes no other restrictions. */
    public static final StringInputProcessor  DEFAULT_STRING_NOT_EMPTY_PROCESSOR  =
        StringInputProcessor
            .builder()
            .allowEmpty(false)
            .build();

    /** A default {@code Boolean} input processor. Defaults to {@code false}. */
    public static final BooleanInputProcessor DEFAULT_FALSE_BOOLEAN_PROCESSOR     =
        BooleanInputProcessor
            .builder()
            .fallback(false)
            .build();

    /** A default {@code Boolean} input processor. Defaults to {@code true}. */
    public static final BooleanInputProcessor DEFAULT_TRUE_BOOLEAN_PROCESSOR      =
        BooleanInputProcessor
            .builder()
            .fallback(true)
            .build();

    /** A default {@code Integer} input processor. Imposes no restrictions. */
    public static final IntegerInputProcessor DEFAULT_INTEGER_PROCESSOR           =
        IntegerInputProcessor
            .builder()
            .build();

    /** A default {@code Double} input processor. Imposes no restrictions. */
    public static final DoubleInputProcessor  DEFAULT_DOUBLE_PROCESSOR            =
        DoubleInputProcessor
            .builder()
            .build();

    /** A default {@code Date} input processor. Imposes no restrictions. */
    public static final DateInputProcessor    DEFAULT_DATE_PROCESSOR              =
        DateInputProcessor
            .builder()
            .build();

    /** A default {@code TagList} input processor. Imposes no restrictions. */
    public static final TagListInputProcessor DEFAULT_TAGLIST_PROCESSOR           =
        TagListInputProcessor
            .builder()
            .build();

    /** A default {@code TagList} input processor. Fails on no tags given. Imposes no other restrictions. */
    public static final TagListInputProcessor DEFAULT_NON_EMPTY_TAGLIST_PROCESSOR =
        TagListInputProcessor
            .builder()
            .allowEmpty(false)
            .build();

    /**
     * Starts an upload for the files a form announced. Normally this is the {@link FileUploadCoordinator}, passed as
     * {@code coordinator::startUpload}; a lambda does for tests.
     */
    @FunctionalInterface
    public interface UploadStarter
    {

        /**
         * @param completionHandlerKey key of the completion handler that takes over after the upload
         * @param formDataSupplier supplier with the validated form data
         * @param files the announced files
         * @return the started upload
         */
        StartResult startUpload(String completionHandlerKey, Supplier<?> formDataSupplier, List<FileDeclaration> files);
    }

    private final Locale                        locale;
    private final String                        formId;
    private final MultiValueMap<String, String> inputs;
    private final List<InputCheckMarker>        marker         = new ArrayList<>();
    private final Set<String>                   fileFields     = new HashSet<>();
    private final List<FileDeclaration>         declaredFiles  = new ArrayList<>();
    private final InputExtractorFactory         parentFactory;
    private final UploadStarter                 starter;
    private final long                          maxFileLength;
    private final long                          maxBatchLength;
    private final Supplier<R>                   formDataResultRefSupplier;
    private long                                announcedBytes = 0;

    /**
     * Create a extractor for a inputs from a {@link FormContainer} request, without upload support: file sizes are not limited and
     * {@link #approve(String, Supplier)} cannot start an upload.
     * <p>
     * This object expects that the {@code key} <b>__form_id</b> exists inside {@code inputs} and is non-null.
     * 
     * @param parentFactory the factory that created this instance
     * @param inputs map from the {@code Form}
     * @param locale to fetch error messages with
     * @param formDataResult supplier for the holder object that keeps the form field values
     * @throws IllegalArgumentException if {@code key} <b>__form_id</b> is {@code null} or empty
     */
    InputExtractor(InputExtractorFactory parentFactory, MultiValueMap<String, String> inputs, Locale locale,
        Supplier<R> formDataResult)
    {
        this(parentFactory, inputs, locale, null, Long.MAX_VALUE, Long.MAX_VALUE, formDataResult);
    }

    /**
     * Create a extractor for a inputs from a {@link FormContainer} request, with upload support.
     * <p>
     * This object expects that the {@code key} <b>__form_id</b> exists inside {@code inputs} and is non-null.
     * <p>
     * Usually created by the {@code InputExtractorFactory}, which provides the parameters from the application context.
     * 
     * @param parentFactory the factory that created this instance
     * @param inputs map from the {@code Form}
     * @param locale to fetch error messages with
     * @param starter to start uploads with, can be {@code null} if the form never uploads files
     * @param maxFileLength largest accepted size of a single file in bytes
     * @param maxBatchLength largest accepted total size of all files of one form in bytes
     * @param formDataResultRefSupplier reference to the form data result object
     * @throws IllegalArgumentException if {@code key} <b>__form_id</b> is {@code null} or empty, or a length is negative
     */
    InputExtractor(InputExtractorFactory parentFactory, MultiValueMap<String, String> inputs, Locale locale, UploadStarter starter,
        long maxFileLength, long maxBatchLength, Supplier<R> formDataResultRefSupplier)
    {
        if (maxFileLength < 0 || maxBatchLength < 0)
        {
            throw new IllegalArgumentException("max lengths cannot be negative");
        }

        this.parentFactory = parentFactory;
        this.inputs = inputs;
        this.locale = locale;
        this.starter = starter;
        this.maxFileLength = maxFileLength;
        this.maxBatchLength = maxBatchLength;
        this.formDataResultRefSupplier = formDataResultRefSupplier;

        formId = (String) inputs.getFirst("__form_id");

        if (formId == null || formId.isBlank())
        {
            throw new IllegalArgumentException("key [__form_id] cannot be null/empty");
        }

        if (LOG.isDebugEnabled())
        {
            LOG.debug("inputs given (values ommitted):");

            inputs
                .entrySet()
                .stream()
                .forEach(ent -> LOG.debug("{}", ent.getKey()));
        }
    }

    /**
     * Get the set {@link Locale} for this extractor.
     * 
     * @return the set {@code locale}
     */
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

                default -> {
                    activateMarker(key, MarkerCategory.ERROR, resultType, result.getTranslationPlaceholderValues());
                }
            };
        }
        catch (NullIgnoredException ex)
        {
            // value to parse is null, but this will be ignored and not raise a marker
        }
    }

    /**
     * Activate a marker for a failed input field.
     * 
     * @param key to identify the field
     * @param category of the marker to raise
     * @param type the result type of the check
     * @param translationPlaceholders potential placeholder values for the ui message
     */
    private void activateMarker(String key, MarkerCategory category, InputCheckResultType type, Object... translationPlaceholders)
    {
        marker.add(
            InputCheckMarker
                .builder()
                .formId(formId)
                .transferId(key)
                .category(category)
                .type(type.getMarkerType())
                .translationKey(type.getTranslationKey())
                .translationPlaceholders(translationPlaceholders)
                .build());
    }

    /**
     * Final check for this {@code InputExtractor}. If {@code markers} where raised it returns a {@link NotificationResponse} to trigger the
     * form field error markers. Else it activates the {@code formDataHandler} to do whatever is to do inside it and then use the
     * {@code responseCreator} to create a user defined response.
     * <p>
     * For forms with files use {@link #approve(String, Supplier)}.
     * 
     * @param formDataHandler consumer that gets the form data for further processing
     * @throws IllegalStateException if files were announced, since nothing would upload them
     */
    public Response approve(Consumer<R> formDataHandler, Supplier<Response> responseCreator)
    {
        Objects.requireNonNull(formDataHandler, "formDataHandler");
        Objects.requireNonNull(responseCreator, "responseCreator");

        try
        {
            approveInputs();
        }
        catch (InputExtractionException ex)
        {
            return parentFactory.buildFailedInputCheckResponse(this);
        }

        if (!declaredFiles.isEmpty())
        {
            throw new IllegalStateException("files were announced, use approve(completionHandlerKey, form) so they are uploaded");
        }

        formDataHandler.accept(formDataResultRefSupplier.get());

        return responseCreator.get();
    }

    /**
     * Final check for this {@code InputExtractor} for a form that may announce files.
     * <p>
     * If {@code markers} where raised it a {@link NotificationResponse}, just like {@link #approve(Consumer, Supplier)}.
     * <p>
     * If the inputs are fine and files were announced with {@link #checkFiles(String, boolean, boolean)}, it starts the upload and throws a
     * {@link FileUploadStartException}, which should be captured to create the {@code StartFileUploadResponse}. The form is then handled by
     * the completion handler registered under {@code completionHandlerKey} after all files have arrived.
     * <p>
     * If no files were announced it simply returns and the form can be handled right away.
     * <p>
     * Note: {@code formData} at this point may not be valid! The {@code approve} call builds the response for the UI to handle the failed
     * checks. If no checks are failed and this approve runs normally THEN the form data is valid in the object.
     * 
     * @param completionHandlerKey key of the completion handler that takes over after the upload
     * @param formData supplies the extracted form data
     * @throws IllegalStateException if files were announced, but this extractor cannot start uploads
     */
    public Response approve(String completionHandlerKey, Supplier<?> formData)
    {
        Objects.requireNonNull(completionHandlerKey, "completionHandlerKey");
        Objects.requireNonNull(formData, "form");

        if (starter == null)
        {
            throw new IllegalStateException("files announced but no upload starter given");
        }

        try
        {
            approveInputs();
        }
        catch (InputExtractionException ex)
        {
            return parentFactory.buildFailedInputCheckResponse(this);
        }

        return parentFactory.buildFileUploadStartResponse(starter.startUpload(completionHandlerKey, formData, List.copyOf(declaredFiles)),
            completionHandlerKey);
    }

    private void approveInputs() throws InputExtractionException
    {
        if (hasMarker())
        {
            throw new InputExtractionException();
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
    public InputExtractor<R> checkAndConsumeAsString(String key, Consumer<String> consumer)
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
    public InputExtractor<R> checkAndConsumeAsNotEmptyString(String key, Consumer<String> consumer)
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
    public InputExtractor<R> checkAndConsumeAsString(String key, StringInputProcessor processor, Consumer<String> consumer)
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
    public InputExtractor<R> checkAndConsumeAsBooleanDefaultFalse(String key, Consumer<Boolean> consumer)
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
    public InputExtractor<R> checkAndConsumeAsBooleanDefaultTrue(String key, Consumer<Boolean> consumer)
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
    private InputExtractor<R> checkAndConsumeAsBoolean(String key, BooleanInputProcessor processor, Consumer<Boolean> consumer)
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
    public InputExtractor<R> checkAndConsumeAsInteger(String key, Consumer<Integer> consumer)
    {
        return checkAndConsumeAsInteger(key, DEFAULT_INTEGER_PROCESSOR, consumer);
    }

    /**
     * Extract a submitted {@code value} and consume it as a {@link Integer}.
     * <p>
     * The {@code consumer} will only be triggered if the {@code value} associated with {@code key} is non-null, can be parsed as a
     * {@link Integer} and clears all checks from the {@link IntegerInputProcessor}.
     * 
     * @param key to extract
     * @param processor to apply
     * @param consumer to feed value to
     * @return {@code this} for chaining
     */
    public InputExtractor<R> checkAndConsumeAsInteger(String key, IntegerInputProcessor processor, Consumer<Integer> consumer)
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
    public InputExtractor<R> checkAndConsumeAsDouble(String key, Consumer<Double> consumer)
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
    public InputExtractor<R> checkAndConsumeAsDouble(String key, DoubleInputProcessor processor, Consumer<Double> consumer)
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
    public InputExtractor<R> checkAndConsumeAsDate(String key, Consumer<Date> consumer)
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
    public InputExtractor<R> checkAndConsumeAsDate(String key, DateInputProcessor processor, Consumer<Date> consumer)
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
    public <E extends Enum<E>> InputExtractor<R> checkAndConsumeAsEnum(String key, Class<E> enumClass, Consumer<E> consumer)
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
    public InputExtractor<R> checkAndConsumeAsTagList(String key, Consumer<TagList> consumer)
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
    public InputExtractor<R> checkAndConsumeAsNotEmptyTagList(String key, Consumer<TagList> consumer)
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
    private InputExtractor<R> extractAsTagList(String key, TagListInputProcessor processor, Consumer<TagList> consumer)
    {
        extractAndConsume(key, processor, consumer);

        return this;
    }

    /**
     * Check the files announced for a file field.
     * <p>
     * This function checks whether files are announced, how many and whether they are within the size limits, but <b>nothing</b> about
     * their content. Valid files are collected and uploaded on {@link #approve(String, Supplier)}.
     * <p>
     * A field with a file that is too large, or whose files together push the form over the batch limit, raises one
     * {@link InputCheckResultType#TOO_LARGE} marker for the field and none of its files are collected.
     * 
     * @param key to bind error messages to file field on {@code submitAs}
     * @param nullable true, if no file at all is allowed
     * @param multipleAllowed true, if more than one file can be uploaded
     * @return {@code this} for chaining
     * @throws IllegalStateException if {@code key} was already checked
     * @throws IllegalArgumentException if {@code key} is {@code null} or empty, or the announcement is not a list of files with name and
     *             size
     */
    public InputExtractor<R> checkFiles(String key, boolean nullable, boolean multipleAllowed)
    {
        if (key == null || key.isBlank())
        {
            throw new IllegalArgumentException("key cannot be null/empty");
        }

        if (!fileFields.add(key))
        {
            throw new IllegalStateException("files for key [" + key + "] were already checked");
        }

        List<FileDeclaration> announced = readAnnouncedFiles(key);

        if (announced.isEmpty())
        {
            if (!nullable)
            {
                activateMarker(key, MarkerCategory.ERROR, InputCheckResultType.EMPTY_BUT_EXPECTED, key);
            }
        }
        else if (!multipleAllowed && announced.size() > 1)
        {
            activateMarker(key, MarkerCategory.ERROR, InputCheckResultType.TOO_MANY, key);
        }
        else if (announced.stream().anyMatch(file -> file.length() > maxFileLength))
        {
            activateMarker(key, MarkerCategory.ERROR, InputCheckResultType.TOO_LARGE, key, maxFileLength);
        }
        else if (sum(announced) > maxBatchLength - announcedBytes)
        {
            activateMarker(key, MarkerCategory.ERROR, InputCheckResultType.TOO_LARGE, key, maxBatchLength);
        }
        else
        {
            declaredFiles.addAll(announced);
            announcedBytes += sum(announced);
        }

        return this;
    }

    /**
     * Sum up the sizes, saturating instead of overflowing.
     */
    private static long sum(List<FileDeclaration> files)
    {
        long total = 0;

        for (FileDeclaration file : files)
        {
            total += file.length();

            if (total < 0)
            {
                return Long.MAX_VALUE;
            }
        }

        return total;
    }

    /**
     * Read the files a client announced for a field, as sent by the browser: {@code [{"name":"a.png","size":1234,"type":"image/png"}]}.
     * <p>
     * A malformed announcement is not a user error, but a broken or manipulated client, and is rejected as such.
     */
    private List<FileDeclaration> readAnnouncedFiles(String key)
    {
        Objects.requireNonNull(key);

        String value = inputs.getFirst(key);

        if (value == null || value.isBlank())
        {
            return List.of();
        }

        try
        {
            JsonNode root = MAPPER.readTree(value);

            if (root == null || !root.isArray())
            {
                throw new IllegalArgumentException("files announced for [" + key + "] are not a list");
            }

            List<FileDeclaration> result = new ArrayList<>();

            for (JsonNode node : root)
            {
                JsonNode name = node.get("name");
                JsonNode size = node.get("size");

                if (name == null || !name.isTextual() || name.asText().isBlank()
                    || size == null || !size.canConvertToLong() || size.asLong() < 0)
                {
                    throw new IllegalArgumentException("file announced for [" + key + "] needs a name and a size >= 0");
                }

                result.add(new FileDeclaration(key, name.asText(), size.asLong()));
            }

            return result;
        }
        catch (JsonProcessingException ex)
        {
            throw new IllegalArgumentException("files announced for [" + key + "] are not valid json", ex);
        }
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
