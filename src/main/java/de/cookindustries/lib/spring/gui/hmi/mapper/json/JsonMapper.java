/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.hmi.mapper.json;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.cookindustries.lib.spring.gui.function.AbsFunctionCall;
import de.cookindustries.lib.spring.gui.function.CloseModal;
import de.cookindustries.lib.spring.gui.function.RegisterTagInput;
import de.cookindustries.lib.spring.gui.function.SubmitFromModal;
import de.cookindustries.lib.spring.gui.function.TagInputSettings;
import de.cookindustries.lib.spring.gui.hmi.container.*;
import de.cookindustries.lib.spring.gui.hmi.input.*;
import de.cookindustries.lib.spring.gui.hmi.input.Number;
import de.cookindustries.lib.spring.gui.hmi.input.util.InputValue;
import de.cookindustries.lib.spring.gui.hmi.input.util.InputValueList;
import de.cookindustries.lib.spring.gui.hmi.mapper.exception.JsonMapperException;
import de.cookindustries.lib.spring.gui.hmi.mapper.exception.JsonParsingException;
import de.cookindustries.lib.spring.gui.hmi.mapper.util.FlatMappable;
import de.cookindustries.lib.spring.gui.hmi.mapper.util.FlatMappableDissector;
import de.cookindustries.lib.spring.gui.hmi.mapper.util.FlatMappableList;
import de.cookindustries.lib.spring.gui.hmi.mapper.util.TokenMap;
import de.cookindustries.lib.spring.gui.hmi.svg.*;
import de.cookindustries.lib.spring.gui.hmi.util.TemplateFileCache;
import de.cookindustries.lib.spring.gui.i18n.AbsTranslationProvider;
import de.cookindustries.lib.spring.gui.i18n.BasicText;
import de.cookindustries.lib.spring.gui.i18n.StaticTranslationProvider;

import lombok.Builder;
import lombok.NonNull;
import lombok.Singular;
import lombok.Builder.Default;

/**
 * This object is used read a path and map the result {@link PseudoElement} into a in-memory tree of {@link Container}s.
 * <p>
 * The {@code mapper} will try to replace certain {@code tokens} with their corresponding values from a given set of {@link TokenMap}s.
 * These {@code tokens} are:
 * <ul>
 * <li>$$value${@code name} - to replace {@code values}, {@code parameters} on {@code elements}/li>
 * <li>$$text${@code name} - to replace text with a translation defined by a {@link Locale} from a {@link AbsTranslationProvider}</li>
 * <li>$$function${@code name} - to replace trigger to {@link AbsFunctionCall} entries with functions</li>
 * <li>$$class${@code name} - to replace styling classes with {@link TokenMap} classes</li>
 * </ul>
 * The {@code name} attribute is the key to lookup inside the respective source of {@code tokens}.
 * 
 * @since 1.0.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
@Builder
public class JsonMapper
{

    private static final Logger                    LOG                            = LoggerFactory.getLogger(JsonMapper.class);

    private static final String                    PARAM_ACTIVE                   = "active";
    private static final String                    PARAM_AUTOPLAY                 = "autoplay";
    private static final String                    PARAM_BTN_CLASS                = "btnClass";
    private static final String                    PARAM_BTN_CLASS_CENTER         = "btnClassCenter";
    private static final String                    PARAM_BTN_CLASS_LEFT           = "btnClassLeft";
    private static final String                    PARAM_BTN_CLASS_RIGHT          = "btnClassRight";
    private static final String                    PARAM_BTN_FUNCTION_CENTER      = "btnFunctionCenter";
    private static final String                    PARAM_BTN_FUNCTION_LEFT        = "btnFunctionLeft";
    private static final String                    PARAM_BTN_FUNCTION_RIGHT       = "btnFunctionRight";
    private static final String                    PARAM_BTN_NAME_CENTER          = "btnNameCenter";
    private static final String                    PARAM_BTN_NAME_LEFT            = "btnNameLeft";
    private static final String                    PARAM_BTN_NAME_RIGHT           = "btnNameRight";
    private static final String                    PARAM_CHECKED                  = "checked";
    private static final String                    PARAM_CLOSE_ON_OVERLAY_CLICK   = "closeOnOverlayClick";
    private static final String                    PARAM_CONNECTED_BTN            = "connectedBtn";
    private static final String                    PARAM_CONTROLS                 = "controls";
    private static final String                    PARAM_DIRECTION                = "direction";
    private static final String                    PARAM_ELEMENT_SOURCE           = "elementSource";
    private static final String                    PARAM_HREF                     = "href";
    private static final String                    PARAM_ICON                     = "icon";
    private static final String                    PARAM_ID                       = "id";
    private static final String                    PARAM_IMAGE                    = "image";
    private static final String                    PARAM_INFO_TEXT                = "infoText";
    private static final String                    PARAM_INFO_URL                 = "infoUrl";
    private static final String                    PARAM_IS_SOURCE_LIST           = "isSourceList";
    private static final String                    PARAM_MAX                      = "max";
    private static final String                    PARAM_MAX_CHARS                = "maxChars";
    private static final String                    PARAM_MIN                      = "min";
    private static final String                    PARAM_NAME                     = "name";
    private static final String                    PARAM_ON_CLICK                 = "onClick";
    private static final String                    PARAM_ON_ENTER_PRESS           = "onEnterPress";
    private static final String                    PARAM_ON_INPUT                 = "onInput";
    private static final String                    PARAM_PATH                     = "path";
    private static final String                    PARAM_PLACEHOLDER              = "placeholder";
    private static final String                    PARAM_PREFIX                   = "prefix";
    private static final String                    PARAM_REPETITION_SOURCE        = "repetitionSource";
    private static final String                    PARAM_REQUEST_URL              = "requestUrl";
    private static final String                    PARAM_SIZE                     = "size";
    private static final String                    PARAM_SOURCE_KEY               = "sourceKey";
    private static final String                    PARAM_SRC                      = "src";
    private static final String                    PARAM_SUBMIT_AS                = "submitAs";
    private static final String                    PARAM_SUFFIX                   = "suffix";
    private static final String                    PARAM_TARGET                   = "target";
    private static final String                    PARAM_TEXT                     = "text";
    private static final String                    PARAM_TITLE                    = "title";
    private static final String                    PARAM_TOOLTIP                  = "tooltip";
    private static final String                    PARAM_URL                      = "url";
    private static final String                    PARAM_VALUE                    = "value";
    private static final String                    PARAM_X                        = "x";
    private static final String                    PARAM_Y                        = "y";
    private static final String                    PARAM_DX                       = "dx";
    private static final String                    PARAM_DY                       = "dy";
    private static final String                    PARAM_X1                       = "x1";
    private static final String                    PARAM_X2                       = "x2";
    private static final String                    PARAM_Y1                       = "y1";
    private static final String                    PARAM_Y2                       = "y2";
    private static final String                    PARAM_ROTATE                   = "rotate";
    private static final String                    PARAM_TEXT_LENGTH              = "textLength";
    private static final String                    PARAM_WIDTH                    = "width";
    private static final String                    PARAM_HEIGHT                   = "height";
    private static final String                    PARAM_STYLE                    = "style";

    private static final String                    RANDOM_ID                      = "randomid";
    private static final String                    CONNECTED_BTN                  = "connected-btn";
    private static final String                    TYPE_ITEM                      = "item";

    private static final String                    DEFAULT_DATE                   = "0000-00-00";
    private static final String                    DEFAULT_EMPTY_VAL              = "";
    private static final String                    DEFAULT_TARGET_SELF            = "_self";
    private static final String                    DEFAULT_BURGER_ICON            = "bi-caret-right-fill";

    private static final String                    BASE_INDICATOR_START           = "$$";
    private static final String                    INDICATOR_VALUE_PLACEHOLDER    = "$$value$";
    private static final String                    INDICATOR_TEXT_PLACEHOLDER     = "$$text$";
    private static final String                    INDICATOR_CLASS_PLACEHOLDER    = "$$class$";
    private static final String                    INDICATOR_FUNCTION_PLACEHOLDER = "$$function$";

    private static final StaticTranslationProvider NOOP_TRANSLATION_PROVIDER      = new StaticTranslationProvider();

    private static final ContainerType[]           CONTENT_CONTAINER_CHILDREN     =
        {
            ContainerType.AUDIO,
            ContainerType.BUTTON,
            ContainerType.BURGER,
            ContainerType.BUTTON_BAR,
            ContainerType.BUTTON_ICON,
            ContainerType.CONTENT,
            ContainerType.FORM,
            ContainerType.HIDDEN,
            ContainerType.IMAGE,
            ContainerType.LINK,
            ContainerType.SPLITTED,
            ContainerType.SVG,
            ContainerType.TAB,
            ContainerType.TABLE,
            ContainerType.TEXT,
            ContainerType.HEADING,
            ContainerType.EMPTY
        };

    private static final ContainerType[]           LINK_CONTAINER_CHILDREN        = {ContainerType.TEXT};

    private static final ContainerType[]           TABLE_ROW_CONTAINER_CHILDREN   =
        {
            ContainerType.AUDIO,
            ContainerType.BUTTON,
            ContainerType.BURGER,
            ContainerType.BUTTON_BAR,
            ContainerType.BUTTON_ICON,
            ContainerType.CONTENT,
            ContainerType.HIDDEN,
            ContainerType.IMAGE,
            ContainerType.LINK,
            ContainerType.SPLITTED,
            ContainerType.SVG,
            ContainerType.TEXT,
            ContainerType.EMPTY
        };

    private static final ContainerType[]           MODAL_CONTAINER_CHILDREN       =
        {
            ContainerType.FORM,
            ContainerType.IMAGE,
            ContainerType.LINK,
            ContainerType.TEXT,
            ContainerType.HEADING,
            ContainerType.TABLE
        };

    @NonNull
    private final TemplateFileCache                templateFileCache;

    @NonNull
    private final String                           rscPath;

    @NonNull
    @Default
    private final Locale                           locale                         = Locale.ENGLISH;

    @NonNull
    @Default
    private final AbsTranslationProvider           translationProvider            = NOOP_TRANSLATION_PROVIDER;

    @NonNull
    private final FlatMappableDissector            flatMappableDissector;

    /** A list of {@link TokenMap}s sorted by {@link TokenMap#getPresedence()} desc */
    @Singular
    private List<TokenMap>                         tokenMaps;

    private final Map<Integer, TokenMap>           tempTokenMaps                  = new HashMap<>();

    private final List<AbsFunctionCall>            functions                      = new ArrayList<>();

    private final String                           uuid                           = nextId();

    @Default
    private Integer                                count                          = 0;

    @NonNull
    @Default
    private final ContainerType[]                  depth0AllowedTypes             = ContainerType.values();

    /**
     * Run the mapping process.
     * 
     * @return the mapped result
     */
    public MapperResult map()
    {
        long          start = System.nanoTime();

        PseudoElement root  = templateFileCache.getTemplateTo(rscPath);

        LOG.debug("[{}]: ### start ###", uuid);
        LOG.trace("[{}]: map content in [{}] from [{}] with [{}] token maps", uuid, locale.toLanguageTag(),
            translationProvider.getClass().getSimpleName(), tokenMaps.size());

        tokenMaps = tokenMaps
            .stream()
            .filter(Objects::nonNull)
            .sorted(
                Comparator
                    .comparing(TokenMap::getPresedence)
                    .reversed())
            .toList();

        if (LOG.isTraceEnabled())
        {
            tokenMaps
                .stream()
                .map(TokenMap::toString)
                .map(String::lines)
                .forEach(stream -> stream.forEach(line -> LOG.trace("[{}]: {}", uuid, line)));
        }

        long end    = System.nanoTime();
        long time   = end - start;
        long timeMs = time / 1000000L;

        LOG.debug("[{}]: ### done ### ... mapping took [{}]ms", uuid, timeMs);

        MapperResult result =
            MapperResult
                .builder()
                .containers(transform(root))
                .functions(functions)
                .time(timeMs)
                .build();

        return result;
    }

    /*
     * --- > utility functions -------------------------------------------------------------------------------------------------------------
     */

    /**
     * Create a new id for this mapper.
     * 
     * @return a id build from {@code day-of-month} {@code month-value / 2} {@code 5-digit random number}
     */
    private String nextId()
    {
        LocalDate now    = LocalDate.now();

        int       d      = now.getDayOfMonth();
        int       m      = now.getMonthValue();
        int       mDigit = (m + 1) / 2;
        int       rand   =
            ThreadLocalRandom
                .current()
                .nextInt(0, 100_000);

        return String.format("%02d%d%05d", d, mDigit, rand);
    }

    private Container throwNotSupported(PseudoElement element, int depth)
    {
        throw new JsonParsingException(element.getUid(), depth, this.count,
            String.format("container type [%s] is not supported", element.getType()));
    }

    /**
     * Resolve the {@code uid} on a {@link PseudoElement}.
     * 
     * @param element to resolve
     * @param depth the current depth in the tree
     * @return the resolved {@code uid} or a {@code uuid} in the form {@code randomid-xxxx-xxxx-xxxx-xxxxxxxxxxxx}, if {@code uid} is
     *         {@code null}
     */
    private String resolveUid(PseudoElement element, int depth)
    {
        String sourceUid = element.getUid();
        String resultUid;

        if (sourceUid == null)
        {
            String random = UUID.randomUUID().toString();
            resultUid = RANDOM_ID + random.substring(8);
        }
        else if (sourceUid.startsWith(INDICATOR_VALUE_PLACEHOLDER))
        {
            resultUid = handlePossiblePlaceholder(sourceUid, String.class, depth);
        }
        else
        {
            resultUid = sourceUid;
        }

        LOG.trace("[{}]:[{}]: resolve uid to [{}]", uuid, depth, resultUid);

        return resultUid;
    }

    /**
     * Traverse the css classes and replace {@code $$class$} parameters from {@link #tokenMaps}.
     * <p>
     * Not found {@code class}es will be ignored.
     * 
     * @param classes {@code List} of all classes {@code String}s to traverse
     * @param depth the recursive depth count
     * @return a {@code List} of {@code String}s containing the non-altered and the replaced classes
     */
    private List<String> resolveClasses(List<String> classes, int depth)
    {
        LOG.trace("[{}]:[{}]: resolve classes {}", uuid, depth, classes);

        return classes
            .stream()
            .map(c -> {
                if (c.startsWith(INDICATOR_CLASS_PLACEHOLDER))
                {
                    String key = c.replace(INDICATOR_CLASS_PLACEHOLDER, DEFAULT_EMPTY_VAL);
                    String text = extractFromTokenMapsAsClass(key);

                    if (text == null)
                    {
                        return DEFAULT_EMPTY_VAL;
                    }

                    return text;
                }

                return c;
            })
            .filter(s -> !s.isBlank())
            .collect(Collectors.toList());
    }

    /**
     * Resolve the {@code attributes} on a {@link PseudoElement}.
     * 
     * @param element to resolve
     * @param depth the current depth in the tree
     * @return the resolved {@code attributes}
     */
    private Map<String, String> resolveAttributes(PseudoElement element, int depth)
    {
        Map<String, String> attributes = element.getAttributes();

        LOG.trace("[{}]:[{}]: resolve attributes {}", uuid, depth, attributes);

        return attributes;
    }

    /**
     * Try to handle a placeholder by fetching from {@code #tokenMaps} based on {@code key} and {@code expectedType}.
     * <p>
     * If no value is found, the {@code null} is returned.
     * 
     * @param <I> the expected result type
     * @param key the key used to look up the value in the value map.
     * @param expectedType the expected type of the extracted value.
     * @param depth the current depth in the tree
     * @return the extracted value if found, or {@code null}
     */
    private <I> I handlePossiblePlaceholder(String key, Class<I> expectedType, int depth)
    {
        return handlePossiblePlaceholder(key, expectedType, null, depth);
    }

    /**
     * Tries to handle a placeholder by fetching from {@code tokenMaps} based on {@code key} and {@code expectedType}.
     * <p>
     * If no value is found, the {@code defaultValue} is returned.
     *
     * @param <I> the expected result type
     * @param key the key used to look up the value in the value map.
     * @param expectedType the expected type of the extracted value.
     * @param defaultValue the default value to return if no value is found.
     * @param depth the current depth in the tree
     * @return the extracted value if found, or {@code defaultValue} if not found
     */
    private <I> I handlePossiblePlaceholder(String key, Class<I> expectedType, I defaultValue, int depth)
    {
        I value = null;

        LOG.trace("[{}]:[{}]: try resolving placeholder [{}] as [{}]", uuid, depth, key, expectedType.getSimpleName());

        if (key == null)
        {
            throw new JsonMapperException("trying to parse a [null] parameter key");
        }
        else
        {
            if (key.startsWith(INDICATOR_TEXT_PLACEHOLDER) && expectedType.equals(String.class))
            {
                LOG.trace("[{}]:[{}]: resolve as [TEXT]", uuid, depth);

                String keyName = key.substring(INDICATOR_TEXT_PLACEHOLDER.length());

                value = expectedType.cast(translationProvider.getText(locale, keyName));
            }
            else if (key.startsWith(INDICATOR_VALUE_PLACEHOLDER))
            {
                LOG.trace("[{}]:[{}]: resolve as [VALUE]", uuid, depth);

                String keyName = key.substring(INDICATOR_VALUE_PLACEHOLDER.length());

                value = extractFromTokenMapsAsValue(keyName, expectedType);
            }
            else if (key.startsWith(INDICATOR_CLASS_PLACEHOLDER))
            {
                LOG.trace("[{}]:[{}]: resolve as [CLASS]", uuid, depth);

                String keyName = key.substring(INDICATOR_CLASS_PLACEHOLDER.length());

                value = expectedType.cast(extractFromTokenMapsAsClass(keyName));
            }
            else if (key.startsWith(INDICATOR_FUNCTION_PLACEHOLDER))
            {
                LOG.trace("[{}]:[{}]: resolve as [FUNCTION]", uuid, depth);

                String keyName = key.substring(INDICATOR_FUNCTION_PLACEHOLDER.length());

                value = expectedType.cast(extractFromTokenMapsAsFunction(keyName));
            }
        }

        if (value == null && defaultValue != null)
        {
            value = defaultValue;
        }

        LOG.trace("[{}]:[{}]: resolved to [{}]", uuid, depth, String.valueOf(value));

        return value;
    }

    /**
     * Extract a value from {@link JsonMapper#tokenMaps}
     *
     * @param <I> expected type of value
     * @param objName name of parameter
     * @param expectedType class of value
     * @return the value associated with {@code paramName}
     * @throws JsonMapperException if value is not of type {@code expectedType}
     */
    private <I> I extractFromTokenMapsAsValue(String objName, Class<I> expectedType) throws JsonMapperException
    {
        Optional<Object> tempResult =
            tempTokenMaps.values()
                .stream()
                .filter(Objects::nonNull)
                .map(m -> m.getValue(objName))
                .filter(Objects::nonNull)
                .findFirst();

        Optional<Object> result     =
            tokenMaps.stream()
                .filter(Objects::nonNull)
                .map(m -> m.getValue(objName))
                .filter(Objects::nonNull)
                .findFirst();

        if (tempResult.isEmpty() && result.isEmpty())
        {
            return null;
        }

        Object obj =
            tempResult.isEmpty()
                ? result.get()
                : tempResult.get();

        if (expectedType.isInstance(obj))
        {
            return expectedType.cast(obj);
        }
        else if (obj instanceof Enum e && expectedType.equals(String.class))
        {
            return expectedType.cast(e.name());
        }
        else if (obj instanceof String s)
        {
            return transformRawValue(objName, 0, s, expectedType);
        }

        throw new JsonMapperException(
            String.format("object [%s] could not be extracted due to expected class is [%s] but got [%s]", objName,
                expectedType.getSimpleName(), obj.getClass().getSimpleName()));
    }

    /**
     * Extract a class from {@link JsonMapper#tokenMaps}
     *
     * @param <I> expected type of value
     * @param paramName name of parameter
     * @param expectedType class of value
     * @return the value associated with {@code paramName}
     * @throws JsonMapperException if value is not of type {@code expectedType}
     */
    private String extractFromTokenMapsAsClass(String objName) throws JsonMapperException
    {
        Optional<String> tempResult =
            tempTokenMaps.values()
                .stream()
                .filter(Objects::nonNull)
                .map(m -> m.getClazz(objName))
                .filter(Objects::nonNull)
                .findFirst();

        if (tempResult.isPresent())
        {
            return tempResult.get();
        }

        Optional<String> result =
            tokenMaps.stream()
                .filter(Objects::nonNull)
                .map(m -> m.getClazz(objName))
                .filter(Objects::nonNull)
                .findFirst();

        return result.isEmpty()
            ? null
            : result.get();
    }

    /**
     * Extract a function from {@link JsonMapper#tokenMaps}
     *
     * @param <I> expected type of value
     * @param paramName name of parameter
     * @param expectedType class of value
     * @return the value associated with {@code paramName}
     * @throws JsonMapperException if value is not of type {@code expectedType}
     */
    private AbsFunctionCall extractFromTokenMapsAsFunction(String objName) throws JsonMapperException
    {
        Optional<AbsFunctionCall> tempResult =
            tempTokenMaps.values()
                .stream()
                .filter(Objects::nonNull)
                .map(m -> m.getFunction(objName))
                .filter(Objects::nonNull)
                .findFirst();

        if (tempResult.isPresent())
        {
            return tempResult.get();
        }

        Optional<AbsFunctionCall> result =
            tokenMaps.stream()
                .filter(Objects::nonNull)
                .map(m -> m.getFunction(objName))
                .filter(Objects::nonNull)
                .findFirst();

        return result.isEmpty()
            ? null
            : result.get();
    }

    /**
     * Check if a {@code uid} is disabled in {@link JsonMapper#tokenMaps}
     *
     * @param uid of the element to check
     * @return {@code false}, if the element is marked as inactive, {@code true} otherwise
     * @throws JsonMapperException if value is not of type {@code expectedType}
     */
    private boolean checkActiveStateFromTokenMaps(String uid) throws JsonMapperException
    {
        boolean tempResult =
            tempTokenMaps.values()
                .stream()
                .filter(Objects::nonNull)
                .map(m -> m.isUidActive(uid))
                .filter(Objects::nonNull)
                .allMatch(b -> b);;

        boolean result =
            tokenMaps.stream()
                .filter(Objects::nonNull)
                .map(m -> m.isUidActive(uid))
                .filter(Objects::nonNull)
                .allMatch(b -> b);

        return uid == null || (tempResult && result);
    }

    /**
     * Extract a paramater from the {@code element}.
     * <p>
     * Replaces the content of the result with a value fetched from {@link #tokenMaps} or {@link #translationProvider} if the {@code key}
     * starts with '$$'
     * 
     * @param <T> the expected result type
     * @param element the element to extract from
     * @param depth the current depth in the tree
     * @param key the key to lookup
     * @param expectedType the expected result type
     * @return the found parameter
     * @throws JsonParsingException if parameter is expected but not found or could not be parsed
     */
    private <T> T getParameterValue(PseudoElement element, int depth, String key, Class<T> expectedType)
    {
        return getParameterValue(element, depth, key, expectedType, null, true);
    }

    /**
     * Extract a paramater from the {@code element}.
     * <p>
     * Replaces the content of the result with a value fetched from {@link #tokenMaps} or {@link #translationProvider} if the {@code key}
     * starts with '$$'
     * 
     * @param <T> the expected result type
     * @param element the element to extract from
     * @param depth the current depth in the tree
     * @param key the key to lookup
     * @param expectedType the expected result type
     * @param fallback to use if no result is found, can be {@code null}
     * @return the found parameter
     * @throws JsonParsingException if parameter is expected but not found and no {@code fallback} is provided, or could not be parsed
     */
    private <T> T getParameterValue(PseudoElement element, int depth, String key, Class<T> expectedType, T fallback)
    {
        return getParameterValue(element, depth, key, expectedType, fallback, true);
    }

    /**
     * Extract a paramater from the {@code element}.
     * <p>
     * Replaces the content of the result with a value fetched from {@link #tokenMaps} or {@link #translationProvider} if the {@code key}
     * starts with '$$'
     * 
     * @param <T> the expected result type
     * @param element the element to extract from
     * @param depth the current depth in the tree
     * @param key the key to lookup
     * @param expectedType the expected result type
     * @param fallback to use if no result is found, can be {@code null}
     * @param processPlaceholder wheter or not {@code placeholder should be replaced}
     * @return the found parameter
     * @throws JsonParsingException if parameter is expected but not found and no {@code fallback} is provided, or could not be parsed
     */
    private <T> T getParameterValue(PseudoElement element, int depth, String key, Class<T> expectedType, T fallback,
        boolean processPlaceholder)
    {
        Object rawValue = element.getParameters().get(key);

        String uid      = element.getUid() == null ? RANDOM_ID : element.getUid();

        LOG.trace("[{}]:[{}]: get parameter [{}] from [{}] as [{}]", uuid, depth, key, uid, expectedType.getSimpleName());

        if (rawValue == null)
        {
            if (fallback == null)
            {
                throw new JsonParsingException(uid, 0, this.count, String.format("parameter [%s] is expected but not set", key));
            }

            LOG.trace("[{}]:[{}]: parameter is [{}]", uuid, depth, String.valueOf(fallback));

            return fallback;
        }

        LOG.trace("[{}]:[{}]: parameter is [{}]:[{}]", uuid, depth, rawValue.getClass().getSimpleName(), String.valueOf(rawValue));

        // ---> DO NOT CHANGE ORDER BETWEEN HERE...
        if (rawValue instanceof String enumVal && expectedType.isEnum())
        {
            @SuppressWarnings({"unchecked", "rawtypes"})
            Enum<?> temp = Enum.valueOf((Class) expectedType, enumVal.toUpperCase());

            return expectedType.cast(temp);
        }

        if (rawValue instanceof String strVal)
        {
            if (strVal.startsWith(BASE_INDICATOR_START) && processPlaceholder)
            {
                return handlePossiblePlaceholder(strVal, expectedType, fallback, depth);
            }
            else
            {
                return transformRawValue(uid, depth, strVal, expectedType);
            }
        }

        if (expectedType.isInstance(rawValue))
        {
            return expectedType.cast(rawValue);
        }
        // ---> ... AND HERE.

        throw new JsonParsingException(uid, 0, this.count,
            String.format("parameter [%s] parsing inconclusive for value [%s] expected type [%s]", key, rawValue,
                expectedType.getSimpleName()));
    }

    /**
     * Transform the raw value of a parameter.
     * 
     * @param <T> the expected result type
     * @param uid of the element to transform for error logging
     * @param depth of the tree
     * @param value the value to transform
     * @param expectedType the expected result type
     * @return the transformed {@code vale}
     * @throws JsonParsingException if the value could not be transformed
     */
    private <T> T transformRawValue(String uid, int depth, String value, Class<T> expectedType)
    {
        if (expectedType.equals(Boolean.class))
        {
            Boolean b = Boolean.valueOf(value.toLowerCase());

            return expectedType.cast(b);
        }

        if (expectedType.equals(Integer.class))
        {
            Integer i = Integer.valueOf(value);

            return expectedType.cast(i);
        }

        if (expectedType.equals(Double.class))
        {
            Double i = Double.valueOf(value);

            return expectedType.cast(i);
        }

        if (expectedType.equals(String.class))
        {
            return expectedType.cast(value);
        }

        throw new JsonParsingException(uid, depth, 0,
            String.format("could not transform [%s] to the expected type [%s]", value, expectedType));
    }

    /**
     * Transform a {@link PseudoElement} as a internal component.
     *
     * @param element to transfrom
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private MapperResult transformInternalComponent(PseudoElement element, int depth, ContainerType... allowedTypes)
    {
        String       path         = getParameterValue(element, depth, PARAM_PATH, String.class);
        Boolean      isSourceList = getParameterValue(element, depth, PARAM_IS_SOURCE_LIST, Boolean.class, false);
        String       sourceKey    = null;
        MapperResult result       = null;

        try
        {
            sourceKey = getParameterValue(element, depth, PARAM_SOURCE_KEY, String.class);
        }
        catch (Exception ex)
        {
            // ignore it
        }

        if (sourceKey != null)
        {
            if (isSourceList)
            {
                FlatMappableList<?> srcList = extractFromTokenMapsAsValue(sourceKey, FlatMappableList.class);

                List<MapperResult>  results = new ArrayList<>();

                srcList.getElements().forEach(src -> {
                    JsonMapper internalMapper =
                        JsonMapper
                            .builder()
                            .templateFileCache(templateFileCache)
                            .translationProvider(translationProvider)
                            .flatMappableDissector(flatMappableDissector)
                            .rscPath(path)
                            .locale(locale)
                            .tokenMaps(tokenMaps)
                            .tokenMap(flatMappableDissector.dissect(src, depth, locale))
                            .depth0AllowedTypes(allowedTypes)
                            .build();

                    LOG.trace("[{}]:[{}]: map linked component list element [{}] @ mapper [{}]", uuid, depth, path, internalMapper.uuid);

                    internalMapper.tempTokenMaps.putAll(tempTokenMaps);

                    results.add(internalMapper.map());
                });

                List<Container>       containers = new ArrayList<>();
                List<AbsFunctionCall> functions  = new ArrayList<>();

                results.forEach(res -> {
                    containers.addAll(res.getContainers());
                    functions.addAll(res.getFunctions());
                });

                result =
                    MapperResult
                        .builder()
                        .containers(containers)
                        .functions(functions)
                        .build();
            }
            else
            {
                FlatMappable srcElement     = extractFromTokenMapsAsValue(sourceKey, FlatMappable.class);

                JsonMapper   internalMapper =
                    JsonMapper
                        .builder()
                        .templateFileCache(templateFileCache)
                        .translationProvider(translationProvider)
                        .flatMappableDissector(flatMappableDissector)
                        .rscPath(path)
                        .locale(locale)
                        .tokenMaps(tokenMaps)
                        .tokenMap(flatMappableDissector.dissect(srcElement, depth, locale))
                        .depth0AllowedTypes(allowedTypes)
                        .build();

                LOG.trace("[{}]:[{}]: map linked sourced component [{}] @ mapper [{}]", uuid, depth, path, internalMapper.uuid);

                internalMapper.tempTokenMaps.putAll(tempTokenMaps);

                result = internalMapper.map();
            }
        }
        else
        {
            JsonMapper internalMapper =
                JsonMapper
                    .builder()
                    .templateFileCache(templateFileCache)
                    .translationProvider(translationProvider)
                    .flatMappableDissector(flatMappableDissector)
                    .rscPath(path)
                    .locale(locale)
                    .tokenMaps(tokenMaps)
                    .depth0AllowedTypes(allowedTypes)
                    .build();

            LOG.trace("[{}]:[{}]: map linked component [{}] @ mapper [{}]", uuid, depth, path, internalMapper.uuid);

            internalMapper.tempTokenMaps.putAll(tempTokenMaps);

            result = internalMapper.map();
        }

        return result;
    }

    /**
     * Transform a list of children that are expected to be parsable as {@link InputValue} into a {@link InputValueList}.
     * 
     * @param element to fetch children from
     * @param depth of the recursive operation
     * @param throwWhenEmpty whether to throw an exception on an empty children-list
     * @return a {@link InputValueList} of the transformed children
     */
    private InputValueList handleInputValueChildren(PseudoElement element, int depth, boolean throwWhenEmpty)
    {
        LOG.trace("[{}]:[{}]: map input children", uuid, depth);

        InputValueList values = new InputValueList();

        for (PseudoElement pe : element.getChildren())
        {
            String  uid            = element.getUid() == null ? RANDOM_ID : element.getUid();

            boolean processElement = shouldProcess(element, uid, depth);

            if (!processElement)
            {
                LOG.trace("[{}]:[{}]: skip InputValue [{}] due to parameter [active] is [false] or [uid] is deactivated", uuid, depth, uid);

                continue;
            }

            if (pe.getType().toUpperCase().equals("INPUT_VALUE"))
            {
                values.add(transformInputValue(pe, depth + 1));
            }
        }

        if (throwWhenEmpty && values.isEmpty())
        {
            throw new JsonParsingException(element.getUid(), depth, 0, "InputValue list can not be emtpy");
        }

        return values;
    }

    /**
     * Transform a {@link PseudoElement} to an {@link InputValue}.
     *
     * @param element to transfrom
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private InputValue transformInputValue(PseudoElement element, int depth)
    {
        List<String> classes = resolveClasses(element.getClasses(), depth);
        String       id      = getParameterValue(element, depth, PARAM_ID, String.class, DEFAULT_EMPTY_VAL);
        String       text    = getParameterValue(element, depth, PARAM_TEXT, String.class, BasicText.NO_TEXT_SET.getText());
        String       value   = getParameterValue(element, depth, PARAM_VALUE, String.class, BasicText.NO_VALUE_SET.getText());
        Boolean      checked = getParameterValue(element, depth, PARAM_CHECKED, Boolean.class, Boolean.FALSE);

        return InputValue
            .builder()
            .id(id)
            .text(text)
            .value(value)
            .checked(checked)
            .classes(classes)
            .build();
    }

    /**
     * Internal JSON transform.
     *
     * @param root element
     * @return the transformed {@code Container}
     */
    private List<Container> transform(PseudoElement root) throws JsonMapperException
    {
        return transform(root, 0, ContainerType.values());
    }

    private boolean shouldProcess(PseudoElement element, String uid, int depth)
    {
        return getParameterValue(element, depth, PARAM_ACTIVE, Boolean.class, true)
            && checkActiveStateFromTokenMaps(uid);
    }

    /*
     * --- < utility functions -------------------------------------------------------------------------------------------------------------
     */

    /**
     * Entry function to transform elements from JSON to {@link Container} objects.
     *
     * @param element to transform
     * @param depth inside the element tree
     * @param allowedTypes allowed types of transformations
     * @return the transformed container
     * @throws JsonParsingException on any failure, refer to exception text
     */
    private List<Container> transform(PseudoElement element, int depth, ContainerType... allowedTypes) throws JsonParsingException
    {
        this.count++;

        List<Container> resultList     = new ArrayList<>();

        String          uid            = element.getUid() == null ? RANDOM_ID : element.getUid();

        boolean         processElement = shouldProcess(element, uid, depth);

        if (!processElement)
        {
            LOG.debug("[{}]:[{}]: skip CONTAINER [{}] due to parameter [active] is [false] or [uid] is deactivated", uuid, depth, uid);

            return List.of(EmptyContainer.builder().build());
        }

        LOG.trace("[{}]:[{}]: start transform >", uuid, depth);
        LOG.trace("[{}]:[{}]: map [{}] with allowed types [{}]", uuid, depth, uid, allowedTypes);

        InternalElementType internalType = null;

        try
        {
            try
            {
                internalType = InternalElementType.valueOf(element.getType().toUpperCase());

                switch (internalType)
                {
                    case COMPONENT:
                        MapperResult result = transformInternalComponent(element, depth, allowedTypes);

                        functions.addAll(result.getFunctions());

                        return result.getContainers();
                }
            }
            catch (IllegalArgumentException | NullPointerException ex)
            {
                // do nothing since it is not a internal type
            }

            ContainerType type = ContainerType.valueOf(element.getType().toUpperCase());

            if (!Arrays.asList(allowedTypes).contains(type) || (depth == 0 && !Arrays.asList(depth0AllowedTypes).contains(type)))
            {
                throw new JsonParsingException(uid, depth, this.count,
                    String.format("container type [%s] is not allowed here", element.getType()));
            }

            LOG.debug("[{}]:[{}]: map CONTAINER [{}] as [{}]", uuid, depth, uid, type);

            int                 numberOfRepetitions = 1;
            FlatMappableList<?> targetList          = null;
            FlatMappable        target              = null;

            try
            {
                String targetListName = getParameterValue(element, depth, PARAM_REPETITION_SOURCE, String.class);
                targetList = extractFromTokenMapsAsValue(targetListName, FlatMappableList.class);

                numberOfRepetitions = targetList.size();

                LOG.debug("[{}]:[{}]: repeat container [{}] times", uuid, depth, numberOfRepetitions);
            }
            catch (JsonParsingException ex)
            {
                LOG.trace("[{}]:[{}]: repetitionSource not set", uuid, depth);

                try
                {
                    String targetName = getParameterValue(element, depth, PARAM_ELEMENT_SOURCE, String.class);
                    target = extractFromTokenMapsAsValue(targetName, FlatMappable.class);

                    LOG.debug("[{}]:[{}]: source element found", uuid, depth);
                }
                catch (JsonParsingException exInner)
                {
                    LOG.trace("[{}]:[{}]: elementSource not set", uuid, depth);
                }
            }
            catch (JsonMapperException ex)
            {
                LOG.error("elementSource/repetitionSource expected but not found", ex);

                resultList.add(failureContainer());

                return resultList;
            }

            for (int i = 0; i < numberOfRepetitions; i++)
            {
                TokenMap      tempTokenMap  = null;
                PseudoElement repeatElement =
                    numberOfRepetitions > 1 && element.getUid() != null
                        ? element
                            .toBuilder()
                            .uid(uid + "-" + i)
                            .build()
                        : element;

                if (targetList != null)
                {
                    tempTokenMap = flatMappableDissector.dissect(targetList.get(i), depth, locale);

                    tempTokenMaps.put(tempTokenMap.hashCode(), tempTokenMap);
                }

                if (target != null)
                {
                    tempTokenMap = flatMappableDissector.dissect(target, depth, locale);

                    tempTokenMaps.put(tempTokenMap.hashCode(), tempTokenMap);
                }

                if (LOG.isTraceEnabled() && !tempTokenMaps.isEmpty())
                {
                    LOG.debug("[{}]:[{}]: with following temp token maps", uuid, depth);

                    tempTokenMaps.values()
                        .stream()
                        .map(TokenMap::toString)
                        .map(String::lines)
                        .forEach(stream -> stream.forEach(line -> LOG.trace("[{}]: {}", uuid, line)));
                }

                Container result = switch (type)
                {
                    case AUDIO -> transfromAudioContainer(repeatElement, depth);
                    case BURGER -> transfromBurgerContainer(repeatElement, depth);
                    case BUTTON -> transfromButtonContainer(repeatElement, depth);
                    case BUTTON_BAR -> transformButtonBarContainer(repeatElement, depth);
                    case BUTTON_ICON -> transformButtonIconContainer(repeatElement, depth);
                    case CONTENT -> transformContentContainer(repeatElement, depth);
                    case EMPTY -> EmptyContainer.builder().build();
                    case FORM -> transformFormContainer(repeatElement, depth);
                    case HEADING -> transformHeadingContainer(repeatElement, depth);
                    case HIDDEN -> transformHiddenContainer(repeatElement, depth);
                    case IMAGE -> transformImageContainer(repeatElement, depth);
                    case LINK -> transformLinkContainer(repeatElement, depth);
                    case MODAL -> transformModal(repeatElement, depth);
                    case SPLITTED -> transformSplittedContainer(repeatElement, depth);
                    case SVG -> transformSVGContainer(repeatElement, depth);
                    case TAB -> transformTabbedContainer(repeatElement, depth);
                    case TABLE -> transformTableContainer(repeatElement, depth);
                    case TABLE_ROW -> transformTableRowContainer(repeatElement, depth);
                    case TEXT -> transformTextContainer(repeatElement, depth);
                };

                resultList.add(result);

                if (tempTokenMap != null)
                {
                    tempTokenMaps.remove(tempTokenMap.hashCode());
                }
            }
        }
        catch (Exception ex)
        {
            LOG.error("error parsing element", ex);

            return List.of(failureContainer());
        }

        return resultList;
    }

    /**
     * Create a wrapper object for a failed transformation to show instead.
     * 
     * @return a {@code TextContainer} with an error message and appropriate styling
     */
    private TextContainer failureContainer()
    {
        return TextContainer
            .builder()
            .clazz("alert")
            .clazz("error")
            .text(String.format("the creation of this element failed. please refer to the server log. mapper id: [%s]", uuid))
            .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link AudioContainer}.
     *
     * @param element to transfrom
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private AudioContainer transfromAudioContainer(PseudoElement element, int depth)
    {
        String              uid        = resolveUid(element, depth);
        List<String>        classes    = resolveClasses(element.getClasses(), depth);
        Map<String, String> attributes = resolveAttributes(element, depth);
        String              src        = getParameterValue(element, depth, PARAM_SRC, String.class);
        Boolean             controls   = getParameterValue(element, depth, PARAM_CONTROLS, Boolean.class, Boolean.FALSE);
        Boolean             autoplay   = getParameterValue(element, depth, PARAM_AUTOPLAY, Boolean.class, Boolean.FALSE);

        return AudioContainer
            .builder()
            .uid(uid)
            .classes(classes)
            .dataAttributes(attributes)
            .src(src)
            .controls(controls)
            .autoplay(autoplay)
            .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link BurgerContainer}.
     *
     * @param element to transfrom
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private BurgerContainer transfromBurgerContainer(PseudoElement element, int depth)
    {
        String              uid        = resolveUid(element, depth);
        List<String>        classes    = resolveClasses(element.getClasses(), depth);
        Map<String, String> attributes = resolveAttributes(element, depth);
        String              icon       = getParameterValue(element, depth, PARAM_ICON, String.class, DEFAULT_BURGER_ICON);

        List<BurgerItem>    items      =
            element
                .getChildren()
                .stream()
                .map(chld -> transformEntry(chld, depth + 1))
                .filter(Objects::nonNull)
                .toList();

        return BurgerContainer
            .builder()
            .uid(uid)
            .classes(classes)
            .dataAttributes(attributes)
            .icon(icon)
            .items(items)
            .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link BurgerItem}.
     *
     * @param element to transfrom
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private BurgerItem transformEntry(PseudoElement element, int depth)
    {
        String  uid            = element.getUid() == null ? RANDOM_ID : element.getUid();
        boolean processElement = shouldProcess(element, uid, depth);

        if (!processElement && !element.getType().equalsIgnoreCase(TYPE_ITEM))
        {
            return null;
        }

        String icon = getParameterValue(element, depth, PARAM_ICON, String.class, DEFAULT_EMPTY_VAL);
        String url  = getParameterValue(element, depth, PARAM_URL, String.class);
        String text = getParameterValue(element, depth, PARAM_TEXT, String.class);

        return BurgerItem
            .builder()
            .icon(icon)
            .url(url)
            .text(text)
            .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link ButtonContainer}.
     *
     * @param element to transfrom
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private Button transfromButtonContainer(PseudoElement element, int depth)
    {
        String              uid                 = resolveUid(element, depth);
        List<String>        classes             = resolveClasses(element.getClasses(), depth);
        Map<String, String> attributes          = resolveAttributes(element, depth);
        String              text                = getParameterValue(element, depth, PARAM_TEXT, String.class);
        ButtonClass         btnClass            =
            getParameterValue(element, depth, PARAM_BTN_CLASS, ButtonClass.class, ButtonClass.DEFAULT);
        AbsFunctionCall     btnFunction         = null;
        String              fallbackBtnFunction = DEFAULT_EMPTY_VAL;

        try
        {
            btnFunction = getParameterValue(element, depth, PARAM_ON_CLICK, AbsFunctionCall.class);
        }
        catch (JsonParsingException ex)
        {
            fallbackBtnFunction = getParameterValue(element, depth, PARAM_ON_CLICK, String.class, DEFAULT_EMPTY_VAL);
        }

        return Button
            .builder()
            .uid(uid)
            .classes(classes)
            .dataAttributes(attributes)
            .text(text)
            .btnClass(btnClass)
            .onClick(btnFunction == null ? fallbackBtnFunction : btnFunction.parseAsJS())
            .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link ButtonBarContainer}.
     *
     * @param element to transfrom
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private ButtonBarContainer transformButtonBarContainer(PseudoElement element, int depth)
    {
        return (ButtonBarContainer) throwNotSupported(element, depth);
    }

    /**
     * Transform a {@link PseudoElement} to an {@link ButtonIconContainer}.
     *
     * @param element to transfrom
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private ButtonIcon transformButtonIconContainer(PseudoElement element, int depth)
    {
        String              uid        = resolveUid(element, depth);
        List<String>        classes    = resolveClasses(element.getClasses(), depth);
        Map<String, String> attributes = resolveAttributes(element, depth);
        String              tooltip    = getParameterValue(element, depth, PARAM_TOOLTIP, String.class, DEFAULT_EMPTY_VAL);
        String              image      = getParameterValue(element, depth, PARAM_IMAGE, String.class);
        ButtonClass         btnClass   = getParameterValue(element, depth, PARAM_BTN_CLASS, ButtonClass.class, ButtonClass.DEFAULT);
        String              onClick    = getParameterValue(element, depth, PARAM_ON_CLICK, String.class);
        String              title      = getParameterValue(element, depth, PARAM_TITLE, String.class, DEFAULT_EMPTY_VAL);

        return ButtonIcon
            .builder()
            .uid(uid)
            .classes(classes)
            .dataAttributes(attributes)
            .tooltip(tooltip)
            .image(image)
            .btnClass(btnClass)
            .onClick(onClick)
            .title(title)
            .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link ContentContainer}.
     *
     * @param element to transfrom
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private ContentContainer transformContentContainer(PseudoElement element, int depth)
    {
        String              uid        = resolveUid(element, depth);
        List<String>        classes    = resolveClasses(element.getClasses(), depth);
        Map<String, String> attributes = resolveAttributes(element, depth);
        List<Container>     contents   = new ArrayList<>();

        element
            .getChildren()
            .stream()
            .forEach(c -> contents.addAll(transform(c, depth + 1, CONTENT_CONTAINER_CHILDREN)));

        return ContentContainer
            .builder()
            .uid(uid)
            .classes(classes)
            .dataAttributes(attributes)
            .contents(contents)
            .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link FormContainer}.
     *
     * @param element to transfrom
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private FormContainer transformFormContainer(PseudoElement element, int depth)
    {
        String              uid          = resolveUid(element, depth);
        Direction           direction    = getParameterValue(element, depth, PARAM_DIRECTION, Direction.class, Direction.NONE);
        List<String>        classes      = resolveClasses(element.getClasses(), depth);
        Map<String, String> attributes   = resolveAttributes(element, depth);
        String              connectedBtn = getParameterValue(element, depth, PARAM_CONNECTED_BTN, String.class, DEFAULT_EMPTY_VAL);
        List<Input>         inputs       = new ArrayList<>();

        element
            .getChildren()
            .stream()
            .forEach(c -> inputs.add(transformInput(c, depth + 1)));

        FormContainer.FormContainerBuilder<?, ?> builder = FormContainer.builder();

        if (!connectedBtn.isBlank())
        {
            builder.dataAttribute(CONNECTED_BTN, connectedBtn);
        }

        return builder
            .uid(uid)
            .direction(direction)
            .classes(classes)
            .dataAttributes(attributes)
            .inputs(inputs)
            .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link HiddenContainer}.
     *
     * @param element to transfrom
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private HiddenContainer transformHiddenContainer(PseudoElement element, int depth)
    {
        String              uid        = resolveUid(element, depth);
        List<String>        classes    = resolveClasses(element.getClasses(), depth);
        Map<String, String> attributes = resolveAttributes(element, depth);
        Container           child      =
            element
                .getChildren()
                .isEmpty() ? null : transform(element.getChildren().get(0), depth + 1, CONTENT_CONTAINER_CHILDREN).get(0);

        return HiddenContainer
            .builder()
            .uid(uid)
            .classes(classes)
            .dataAttributes(attributes)
            .child(child)
            .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link ImageContainer}.
     *
     * @param element to transfrom
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private ImageContainer transformImageContainer(PseudoElement element, int depth)
    {
        String              uid        = resolveUid(element, depth);
        List<String>        classes    = resolveClasses(element.getClasses(), depth);
        Map<String, String> attributes = resolveAttributes(element, depth);
        String              tooltip    = getParameterValue(element, depth, PARAM_TOOLTIP, String.class, DEFAULT_EMPTY_VAL);
        String              src        = getParameterValue(element, depth, PARAM_SRC, String.class);

        return ImageContainer
            .builder()
            .uid(uid)
            .classes(classes)
            .dataAttributes(attributes)
            .tooltip(tooltip)
            .src(src)
            .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link LinkContainer}.
     *
     * @param element to transfrom
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private LinkContainer transformLinkContainer(PseudoElement element, int depth)
    {
        String              uid        = resolveUid(element, depth);
        List<String>        classes    = resolveClasses(element.getClasses(), depth);
        Map<String, String> attributes = resolveAttributes(element, depth);
        String              tooltip    = getParameterValue(element, depth, PARAM_TOOLTIP, String.class, DEFAULT_EMPTY_VAL);
        String              href       = getParameterValue(element, depth, PARAM_HREF, String.class);
        String              target     = getParameterValue(element, depth, PARAM_TARGET, String.class, DEFAULT_TARGET_SELF);
        Container           content    =
            element
                .getChildren()
                .isEmpty()
                    ? null
                    : transform(element.getChildren().get(0), depth + 1, LINK_CONTAINER_CHILDREN).get(0);

        return LinkContainer
            .builder()
            .uid(uid)
            .classes(classes)
            .dataAttributes(attributes)
            .tooltip(tooltip)
            .href(href)
            .target(target)
            .content(content)
            .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link Modal}.
     *
     * @param element to transfrom
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private ModalContainer transformModal(PseudoElement element, int depth)
    {
        String              uid                     = resolveUid(element, depth);
        List<String>        classes                 = resolveClasses(element.getClasses(), depth);
        Map<String, String> attributes              = resolveAttributes(element, depth);
        String              name                    = getParameterValue(element, depth, PARAM_NAME, String.class);
        String              requestUrl              = getParameterValue(element, depth, PARAM_REQUEST_URL, String.class, DEFAULT_EMPTY_VAL);
        Boolean             closeOnOverlayClick     =
            getParameterValue(element, depth, PARAM_CLOSE_ON_OVERLAY_CLICK, Boolean.class, Boolean.FALSE);

        String              btnNameLeft             =
            getParameterValue(element, depth, PARAM_BTN_NAME_LEFT, String.class, BasicText.CANCEL.getText());
        ButtonClass         btnClassLeft            =
            getParameterValue(element, depth, PARAM_BTN_CLASS_LEFT, ButtonClass.class, ButtonClass.DEFAULT);

        AbsFunctionCall     btnFunctionLeft         = null;
        String              fallbackBtnFunctionLeft = DEFAULT_EMPTY_VAL;

        try
        {
            btnFunctionLeft =
                getParameterValue(element, depth, PARAM_BTN_FUNCTION_LEFT, AbsFunctionCall.class, new CloseModal());
        }
        catch (JsonParsingException ex)
        {
            fallbackBtnFunctionLeft =
                getParameterValue(element, depth, PARAM_BTN_FUNCTION_LEFT, String.class, DEFAULT_EMPTY_VAL);
        }

        String          btnNameCenter             =
            getParameterValue(element, depth, PARAM_BTN_NAME_CENTER, String.class, DEFAULT_EMPTY_VAL);
        ButtonClass     btnClassCenter            =
            getParameterValue(element, depth, PARAM_BTN_CLASS_CENTER, ButtonClass.class, ButtonClass.DEFAULT);

        AbsFunctionCall btnFunctionCenter         = null;
        String          fallbackBtnFunctionCenter = DEFAULT_EMPTY_VAL;

        try
        {
            btnFunctionCenter =
                getParameterValue(element, depth, PARAM_BTN_FUNCTION_CENTER, AbsFunctionCall.class);
        }
        catch (JsonParsingException ex)
        {
            fallbackBtnFunctionCenter =
                getParameterValue(element, depth, PARAM_BTN_FUNCTION_CENTER, String.class, DEFAULT_EMPTY_VAL);
        }

        String          btnNameRight             = getParameterValue(element, depth, PARAM_BTN_NAME_RIGHT, String.class, "submit");
        ButtonClass     btnClassRight            =
            getParameterValue(element, depth, PARAM_BTN_CLASS_RIGHT, ButtonClass.class, ButtonClass.DEFAULT);

        AbsFunctionCall btnFunctionRight         = null;
        String          fallbackBtnFunctionRight = DEFAULT_EMPTY_VAL;

        try
        {
            btnFunctionRight =
                getParameterValue(element, depth, PARAM_BTN_FUNCTION_RIGHT, AbsFunctionCall.class, new SubmitFromModal());
        }
        catch (JsonParsingException ex)
        {
            fallbackBtnFunctionRight = getParameterValue(element, depth, PARAM_BTN_FUNCTION_RIGHT, String.class);
        }

        List<Container> children = new ArrayList<>();

        for (PseudoElement pe : element.getChildren())
        {
            children.addAll(transform(pe, depth + 1, MODAL_CONTAINER_CHILDREN));
        }

        return ModalContainer
            .builder()
            .uid(uid)
            .classes(classes)
            .dataAttributes(attributes)
            .name(name)
            .requestUrl(requestUrl)
            .btnNameLeft(btnNameLeft)
            .btnClassLeft(btnClassLeft)
            .btnFunctionLeft(btnFunctionLeft == null ? fallbackBtnFunctionLeft : btnFunctionLeft.parseAsJS())
            .btnNameCenter(btnNameCenter)
            .btnClassCenter(btnClassCenter)
            .btnFunctionCenter(btnFunctionCenter == null ? fallbackBtnFunctionCenter : btnFunctionCenter.parseAsJS())
            .btnNameRight(btnNameRight)
            .btnClassRight(btnClassRight)
            .btnFunctionRight(btnFunctionRight == null ? fallbackBtnFunctionRight : btnFunctionRight.parseAsJS())
            .closeOnOverlayClick(closeOnOverlayClick)
            .content(
                ContentContainer
                    .builder()
                    .contents(children)
                    .build())
            .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link SplittedContainer}.
     *
     * @param element to transfrom
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private SplittedContainer transformSplittedContainer(PseudoElement element, int depth)
    {
        String              uid        = resolveUid(element, depth);
        List<String>        classes    = resolveClasses(element.getClasses(), depth);
        Map<String, String> attributes = resolveAttributes(element, depth);
        Container           head       = null;
        Container           tail       = null;
        List<Container>     center     = new ArrayList<>();
        Integer             cnt        = 0;

        for (PseudoElement pe : element.getChildren())
        {
            if (cnt > 0 && cnt < element.getChildren().size() - 1)
            {
                center.addAll(transform(pe, depth, ContainerType.values()));
            }
            else if (cnt == 0)
            {
                head = transform(pe, depth, ContainerType.values()).get(0);
            }
            else
            {
                tail = transform(pe, depth, ContainerType.values()).get(0);
            }

            cnt++;
        }

        return SplittedContainer
            .builder()
            .uid(uid)
            .classes(classes)
            .dataAttributes(attributes)
            .head(head)
            .center(center)
            .tail(tail)
            .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link SVGContainer}.
     *
     * @param element to transfrom
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private SVGContainer transformSVGContainer(PseudoElement element, int depth)
    {
        String              uid        = resolveUid(element, depth);
        List<String>        classes    = resolveClasses(element.getClasses(), depth);
        Map<String, String> attributes = resolveAttributes(element, depth);
        Integer             width      = getParameterValue(element, depth, PARAM_WIDTH, Integer.class);
        Integer             height     = getParameterValue(element, depth, PARAM_HEIGHT, Integer.class);

        List<SVGElement>    elements   = new ArrayList<>();

        for (PseudoElement pe : element.getChildren())
        {
            elements.addAll(transformSVGElement(pe, depth, SVGType.ALLOWED_BASE_TYPES));
        }

        return SVGContainer
            .builder()
            .uid(uid)
            .classes(classes)
            .dataAttributes(attributes)
            .elements(elements)
            .width(width)
            .height(height)
            .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link TabContainer}.
     *
     * @param element to transfrom
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private TabContainer transformTabbedContainer(PseudoElement element, int depth)
    {
        return (TabContainer) throwNotSupported(element, depth);
    }

    /**
     * Transform a {@link PseudoElement} to an {@link TabContainer}
     *
     * @param element to transfrom
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private TableContainer transformTableContainer(PseudoElement element, int depth)
    {
        String              uid            = resolveUid(element, depth);
        List<String>        classes        = resolveClasses(element.getClasses(), depth);
        Map<String, String> attributes     = resolveAttributes(element, depth);
        String              name           = getParameterValue(element, depth, PARAM_NAME, String.class);
        String              columnNames    = getParameterValue(element, depth, "columnNames", String.class, null, false);
        Boolean             sortable       = getParameterValue(element, depth, "sortable", Boolean.class, false);

        List<String>        columnNameList =
            Arrays
                .stream(columnNames.split("\\|\\|"))
                .filter(Objects::nonNull)
                .map(cn -> handlePossiblePlaceholder(cn, String.class, cn, depth))
                .toList();

        List<Container>     rows           = new ArrayList<>();

        for (PseudoElement pe : element.getChildren())
        {
            rows.addAll(transform(pe, depth, ContainerType.TABLE_ROW));
        }

        return TableContainer
            .builder()
            .uid(uid)
            .classes(classes)
            .dataAttributes(attributes)
            .name(name)
            .columnNames(columnNameList)
            .sortable(sortable)
            .rows(rows)
            .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link TabContainer}.
     *
     * @param element to transfrom
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private TableRowContainer transformTableRowContainer(PseudoElement element, int depth)
    {
        String              uid        = resolveUid(element, depth);
        List<String>        classes    = resolveClasses(element.getClasses(), depth);
        Map<String, String> attributes = resolveAttributes(element, depth);
        String              tableName  = getParameterValue(element, depth, "tableName", String.class);
        List<Container>     cells      = new ArrayList<>();

        for (PseudoElement pe : element.getChildren())
        {
            cells.addAll(transform(pe, depth + 1, TABLE_ROW_CONTAINER_CHILDREN));
        }

        return TableRowContainer
            .builder()
            .uid(uid)
            .classes(classes)
            .dataAttributes(attributes)
            .tableName(tableName)
            .cells(cells)
            .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link TextContainer}.
     *
     * @param element to transfrom
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private TextContainer transformTextContainer(PseudoElement element, int depth)
    {
        String              uid        = resolveUid(element, depth);
        List<String>        classes    = resolveClasses(element.getClasses(), depth);
        Map<String, String> attributes = resolveAttributes(element, depth);
        String              tooltip    = getParameterValue(element, depth, PARAM_TOOLTIP, String.class, DEFAULT_EMPTY_VAL);
        String              text       = getParameterValue(element, depth, PARAM_TEXT, String.class);
        Boolean             inline     = getParameterValue(element, depth, "inline", Boolean.class, false);

        return TextContainer
            .builder()
            .uid(uid)
            .classes(classes)
            .dataAttributes(attributes)
            .tooltip(tooltip)
            .text(text)
            .inline(inline)
            .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link TextContainer}.
     *
     * @param element to transfrom
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private HeadingContainer transformHeadingContainer(PseudoElement element, int depth)
    {
        String              uid        = resolveUid(element, depth);
        List<String>        classes    = resolveClasses(element.getClasses(), depth);
        Map<String, String> attributes = resolveAttributes(element, depth);
        String              tooltip    = getParameterValue(element, depth, PARAM_TOOLTIP, String.class, DEFAULT_EMPTY_VAL);
        String              text       = getParameterValue(element, depth, PARAM_TEXT, String.class);
        Integer             size       = getParameterValue(element, depth, PARAM_SIZE, Integer.class, 1);

        size = size < 0
            ? 1
            : size > 6
                ? 6
                : size;

        return HeadingContainer
            .builder()
            .uid(uid)
            .classes(classes)
            .dataAttributes(attributes)
            .tooltip(tooltip)
            .text(text)
            .size(size)
            .build();
    }

    /**
     * Entry function to transform elements from JSON to {@link Input} objects
     *
     * @param element to transform
     * @param depth inside the element tree
     * @return the transformed input
     * @throws JsonParsingException on any failure, refer to exception text
     */
    private Input transformInput(PseudoElement element, int depth)
    {
        InputType type = InputType.valueOf(element.getType().toUpperCase());

        LOG.trace(DEFAULT_EMPTY_VAL);

        String  uid            = element.getUid() == null ? "random uid" : element.getUid();

        boolean processElement = shouldProcess(element, uid, depth);

        if (!processElement)
        {
            LOG.debug("[{}]:[{}]: skip INPUT [{}] due to eval processElement is [false]", uuid, depth, element.getUid());

            return Hidden
                .builder()
                .uid(element.getUid())
                .name(DEFAULT_EMPTY_VAL)
                .submitAs(DEFAULT_EMPTY_VAL)
                .value(DEFAULT_EMPTY_VAL)
                .build();
        }

        LOG.debug("[{}]:[{}]: map INPUT [{}] as [{}]", uuid, depth, element.getUid(), type);

        try
        {
            return switch (type)
            {
                case CHECKBOX -> transformCheckboxInput(element, depth);
                case CURRENCY -> transformCurrencyInput(element, depth);
                case DATE -> transformDateInput(element, depth);
                case FILE -> transformFileInput(element, depth);
                case HIDDEN -> transformHiddenInput(element, depth);
                case LINK -> transformLinkInput(element, depth);
                case NUMBER -> transformNumberInput(element, depth);
                case PASSWORD -> transformPasswordInput(element, depth);
                case RADIO -> transformRadioInput(element, depth);
                case SELECT -> transformSelectInput(element, depth);
                case SLIDER -> transformSliderInput(element, depth);
                case SWITCH -> transformSwitchInput(element, depth);
                case TAG -> transformTagInput(element, depth);
                case TEXTAREA -> transformTextareaInput(element, depth);
                case TEXTBOX -> transformTextboxInput(element, depth);
                case TEXTFIELD -> transformTextfieldInput(element, depth);
            };
        }
        catch (final Exception ex)
        {
            // TODO: write better exception
            throw new JsonParsingException(element.getUid(), depth, this.count, "error parsing element", ex);
        }
    }

    /**
     * Transform a {@link PseudoElement} to an {@link Checkbox} input.
     *
     * @param element to transfrom
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private Checkbox transformCheckboxInput(PseudoElement element, int depth)
    {
        String              uid        = resolveUid(element, depth);
        List<String>        classes    = resolveClasses(element.getClasses(), depth);
        Map<String, String> attributes = resolveAttributes(element, depth);
        String              tooltip    = getParameterValue(element, depth, PARAM_TOOLTIP, String.class, DEFAULT_EMPTY_VAL);
        String              name       = getParameterValue(element, depth, PARAM_NAME, String.class);
        String              submitAs   = getParameterValue(element, depth, PARAM_SUBMIT_AS, String.class, DEFAULT_EMPTY_VAL);
        String              onInput    = getParameterValue(element, depth, PARAM_ON_INPUT, String.class, DEFAULT_EMPTY_VAL);
        String              infoText   = getParameterValue(element, depth, PARAM_INFO_TEXT, String.class, DEFAULT_EMPTY_VAL);
        String              infoUrl    = getParameterValue(element, depth, PARAM_INFO_URL, String.class, DEFAULT_EMPTY_VAL);
        String              boxesSrc   = getParameterValue(element, depth, "boxes", String.class, DEFAULT_EMPTY_VAL);
        InputValueList      boxes      =
            boxesSrc.isBlank()
                ? handleInputValueChildren(element, depth, false)
                : handlePossiblePlaceholder(boxesSrc, InputValueList.class, new InputValueList(), depth);

        return Checkbox
            .builder()
            .uid(uid)
            .classes(classes)
            .dataAttributes(attributes)
            .tooltip(tooltip)
            .name(name)
            .submitAs(submitAs)
            .onInput(onInput)
            .infoText(infoText)
            .infoUrl(infoUrl)
            .boxes(boxes)
            .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link Currency} input.
     *
     * @param element to transfrom
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private Currency transformCurrencyInput(PseudoElement element, int depth)
    {
        String              uid         = resolveUid(element, depth);
        List<String>        classes     = resolveClasses(element.getClasses(), depth);
        Map<String, String> attributes  = resolveAttributes(element, depth);
        String              tooltip     = getParameterValue(element, depth, PARAM_TOOLTIP, String.class, DEFAULT_EMPTY_VAL);
        String              name        = getParameterValue(element, depth, PARAM_NAME, String.class);
        String              submitAs    = getParameterValue(element, depth, PARAM_SUBMIT_AS, String.class);
        String              onInput     = getParameterValue(element, depth, PARAM_ON_INPUT, String.class, DEFAULT_EMPTY_VAL);
        String              infoText    = getParameterValue(element, depth, PARAM_INFO_TEXT, String.class, DEFAULT_EMPTY_VAL);
        String              infoUrl     = getParameterValue(element, depth, PARAM_INFO_URL, String.class, DEFAULT_EMPTY_VAL);
        Integer             valueF      = getParameterValue(element, depth, "valueF", Integer.class, 0);
        Integer             valueB      = getParameterValue(element, depth, "valueB", Integer.class, 0);
        String              symbol      = getParameterValue(element, depth, "symbol", String.class, DEFAULT_EMPTY_VAL);
        Integer             min         = getParameterValue(element, depth, PARAM_MIN, Integer.class, Integer.MIN_VALUE);
        Integer             max         = getParameterValue(element, depth, PARAM_MAX, Integer.class, Integer.MAX_VALUE);
        String              placeholder = getParameterValue(element, depth, PARAM_PLACEHOLDER, String.class, DEFAULT_EMPTY_VAL);

        return Currency
            .builder()
            .uid(uid)
            .classes(classes)
            .dataAttributes(attributes)
            .tooltip(tooltip)
            .name(name)
            .submitAs(submitAs)
            .onInput(onInput)
            .infoText(infoText)
            .infoUrl(infoUrl)
            .valueF(valueF)
            .valueB(valueB)
            .symbol(symbol)
            .min(min)
            .max(max)
            .placeholder(placeholder)
            .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link Date} input.
     *
     * @param element to transfrom
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private Date transformDateInput(PseudoElement element, int depth)
    {
        String              uid        = resolveUid(element, depth);
        List<String>        classes    = resolveClasses(element.getClasses(), depth);
        Map<String, String> attributes = resolveAttributes(element, depth);
        String              tooltip    = getParameterValue(element, depth, PARAM_TOOLTIP, String.class, DEFAULT_EMPTY_VAL);
        String              name       = getParameterValue(element, depth, PARAM_NAME, String.class);
        String              submitAs   = getParameterValue(element, depth, PARAM_SUBMIT_AS, String.class);
        String              onInput    = getParameterValue(element, depth, PARAM_ON_INPUT, String.class, DEFAULT_EMPTY_VAL);
        String              infoText   = getParameterValue(element, depth, PARAM_INFO_TEXT, String.class, DEFAULT_EMPTY_VAL);
        String              infoUrl    = getParameterValue(element, depth, PARAM_INFO_URL, String.class, DEFAULT_EMPTY_VAL);
        String              value      = getParameterValue(element, depth, PARAM_VALUE, String.class, DEFAULT_DATE);

        return Date
            .builder()
            .uid(uid)
            .classes(classes)
            .dataAttributes(attributes)
            .tooltip(tooltip)
            .name(name)
            .submitAs(submitAs)
            .onInput(onInput)
            .infoText(infoText)
            .infoUrl(infoUrl)
            .value(value)
            .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link File} input.
     *
     * @param element to transfrom
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private File transformFileInput(PseudoElement element, int depth)
    {
        String              uid        = resolveUid(element, depth);
        List<String>        classes    = resolveClasses(element.getClasses(), depth);
        Map<String, String> attributes = resolveAttributes(element, depth);
        String              tooltip    = getParameterValue(element, depth, PARAM_TOOLTIP, String.class, DEFAULT_EMPTY_VAL);
        String              name       = getParameterValue(element, depth, PARAM_NAME, String.class);
        String              submitAs   = getParameterValue(element, depth, PARAM_SUBMIT_AS, String.class);
        String              onInput    = getParameterValue(element, depth, PARAM_ON_INPUT, String.class, DEFAULT_EMPTY_VAL);
        String              infoText   = getParameterValue(element, depth, PARAM_INFO_TEXT, String.class, DEFAULT_EMPTY_VAL);
        String              infoUrl    = getParameterValue(element, depth, PARAM_INFO_URL, String.class, DEFAULT_EMPTY_VAL);
        Boolean             multiple   = getParameterValue(element, depth, "multiple", Boolean.class, Boolean.FALSE);
        String              accept     = getParameterValue(element, depth, "accept", String.class, "*");

        return File
            .builder()
            .uid(uid)
            .classes(classes)
            .dataAttributes(attributes)
            .tooltip(tooltip)
            .name(name)
            .submitAs(submitAs)
            .onInput(onInput)
            .infoText(infoText)
            .infoUrl(infoUrl)
            .multiple(multiple)
            .accept(accept)
            .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link Hidden} input.
     *
     * @param element to transfrom
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private Hidden transformHiddenInput(PseudoElement element, int depth)
    {
        String              uid        = resolveUid(element, depth);
        List<String>        classes    = resolveClasses(element.getClasses(), depth);
        Map<String, String> attributes = resolveAttributes(element, depth);
        String              submitAs   = getParameterValue(element, depth, PARAM_SUBMIT_AS, String.class);
        String              value      = getParameterValue(element, depth, PARAM_VALUE, String.class, DEFAULT_EMPTY_VAL);

        return Hidden
            .builder()
            .uid(uid)
            .classes(classes)
            .dataAttributes(attributes)
            .name(DEFAULT_EMPTY_VAL)
            .submitAs(submitAs)
            .value(value)
            .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link Link} input.
     *
     * @param element to transfrom
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private Link transformLinkInput(PseudoElement element, int depth)
    {
        String              uid        = resolveUid(element, depth);
        List<String>        classes    = resolveClasses(element.getClasses(), depth);
        Map<String, String> attributes = resolveAttributes(element, depth);
        String              tooltip    = getParameterValue(element, depth, PARAM_TOOLTIP, String.class, DEFAULT_EMPTY_VAL);
        String              text       = getParameterValue(element, depth, PARAM_TEXT, String.class);
        String              href       = getParameterValue(element, depth, PARAM_HREF, String.class);
        String              target     = getParameterValue(element, depth, PARAM_TARGET, String.class, DEFAULT_TARGET_SELF);

        return Link
            .builder()
            .uid(uid)
            .classes(classes)
            .dataAttributes(attributes)
            .tooltip(tooltip)
            .text(text)
            .href(href)
            .target(target)
            .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link Number} input.
     *
     * @param element to transfrom
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private Number transformNumberInput(PseudoElement element, int depth)
    {
        String              uid          = resolveUid(element, depth);
        List<String>        classes      = resolveClasses(element.getClasses(), depth);
        Map<String, String> attributes   = resolveAttributes(element, depth);
        String              tooltip      = getParameterValue(element, depth, PARAM_TOOLTIP, String.class, DEFAULT_EMPTY_VAL);
        String              name         = getParameterValue(element, depth, PARAM_NAME, String.class);
        String              submitAs     = getParameterValue(element, depth, PARAM_SUBMIT_AS, String.class);
        String              onInput      = getParameterValue(element, depth, PARAM_ON_INPUT, String.class, DEFAULT_EMPTY_VAL);
        String              infoText     = getParameterValue(element, depth, PARAM_INFO_TEXT, String.class, DEFAULT_EMPTY_VAL);
        String              infoUrl      = getParameterValue(element, depth, PARAM_INFO_URL, String.class, DEFAULT_EMPTY_VAL);
        String              value        = getParameterValue(element, depth, PARAM_VALUE, String.class, DEFAULT_EMPTY_VAL);
        Integer             min          = getParameterValue(element, depth, PARAM_MIN, Integer.class, Integer.MIN_VALUE);
        Integer             max          = getParameterValue(element, depth, PARAM_MAX, Integer.class, Integer.MAX_VALUE);
        String              placeholder  = getParameterValue(element, depth, PARAM_PLACEHOLDER, String.class, DEFAULT_EMPTY_VAL);
        String              prefix       = getParameterValue(element, depth, PARAM_PREFIX, String.class, DEFAULT_EMPTY_VAL);
        String              suffix       = getParameterValue(element, depth, PARAM_SUFFIX, String.class, DEFAULT_EMPTY_VAL);
        String              onEnterPress = getParameterValue(element, depth, PARAM_ON_ENTER_PRESS, String.class, DEFAULT_EMPTY_VAL);

        return Number
            .builder()
            .uid(uid)
            .classes(classes)
            .dataAttributes(attributes)
            .tooltip(tooltip)
            .name(name)
            .submitAs(submitAs)
            .onInput(onInput)
            .infoText(infoText)
            .infoUrl(infoUrl)
            .value(value)
            .min(min)
            .max(max)
            .placeholder(placeholder)
            .prefix(prefix)
            .suffix(suffix)
            .onEnterPress(onEnterPress)
            .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link Password} input.
     *
     * @param element to transfrom
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private Password transformPasswordInput(PseudoElement element, int depth)
    {
        String              uid          = resolveUid(element, depth);
        List<String>        classes      = resolveClasses(element.getClasses(), depth);
        Map<String, String> attributes   = resolveAttributes(element, depth);
        String              tooltip      = getParameterValue(element, depth, PARAM_TOOLTIP, String.class, DEFAULT_EMPTY_VAL);
        String              name         = getParameterValue(element, depth, PARAM_NAME, String.class);
        String              submitAs     = getParameterValue(element, depth, PARAM_SUBMIT_AS, String.class);
        String              onInput      = getParameterValue(element, depth, PARAM_ON_INPUT, String.class, DEFAULT_EMPTY_VAL);
        String              infoText     = getParameterValue(element, depth, PARAM_INFO_TEXT, String.class, DEFAULT_EMPTY_VAL);
        String              infoUrl      = getParameterValue(element, depth, PARAM_INFO_URL, String.class, DEFAULT_EMPTY_VAL);
        String              placeholder  = getParameterValue(element, depth, PARAM_PLACEHOLDER, String.class, "***");
        String              onEnterPress = getParameterValue(element, depth, PARAM_ON_ENTER_PRESS, String.class, DEFAULT_EMPTY_VAL);

        return Password
            .builder()
            .uid(uid)
            .classes(classes)
            .dataAttributes(attributes)
            .tooltip(tooltip)
            .name(name)
            .submitAs(submitAs)
            .onInput(onInput)
            .infoText(infoText)
            .infoUrl(infoUrl)
            .placeholder(placeholder)
            .onEnterPress(onEnterPress)
            .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link Radio} input.
     *
     * @param element to transfrom
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private Radio transformRadioInput(PseudoElement element, int depth)
    {
        String              uid        = resolveUid(element, depth);
        List<String>        classes    = resolveClasses(element.getClasses(), depth);
        Map<String, String> attributes = resolveAttributes(element, depth);
        String              tooltip    = getParameterValue(element, depth, PARAM_TOOLTIP, String.class, DEFAULT_EMPTY_VAL);
        String              name       = getParameterValue(element, depth, PARAM_NAME, String.class);
        String              submitAs   = getParameterValue(element, depth, PARAM_SUBMIT_AS, String.class);
        String              onInput    = getParameterValue(element, depth, PARAM_ON_INPUT, String.class, DEFAULT_EMPTY_VAL);
        String              infoText   = getParameterValue(element, depth, PARAM_INFO_TEXT, String.class, DEFAULT_EMPTY_VAL);
        String              infoUrl    = getParameterValue(element, depth, PARAM_INFO_URL, String.class, DEFAULT_EMPTY_VAL);
        List<InputValue>    values     = handleInputValueChildren(element, depth, true);

        return Radio
            .builder()
            .uid(uid)
            .classes(classes)
            .dataAttributes(attributes)
            .tooltip(tooltip)
            .name(name)
            .submitAs(submitAs)
            .onInput(onInput)
            .infoText(infoText)
            .infoUrl(infoUrl)
            .values(values)
            .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link Select} input.
     *
     * @param element to transfrom
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private Select transformSelectInput(PseudoElement element, int depth)
    {
        String              uid        = resolveUid(element, depth);
        List<String>        classes    = resolveClasses(element.getClasses(), depth);
        Map<String, String> attributes = resolveAttributes(element, depth);
        String              tooltip    = getParameterValue(element, depth, PARAM_TOOLTIP, String.class, DEFAULT_EMPTY_VAL);
        String              name       = getParameterValue(element, depth, PARAM_NAME, String.class);
        String              submitAs   = getParameterValue(element, depth, PARAM_SUBMIT_AS, String.class);
        String              onInput    = getParameterValue(element, depth, PARAM_ON_INPUT, String.class, DEFAULT_EMPTY_VAL);
        String              infoText   = getParameterValue(element, depth, PARAM_INFO_TEXT, String.class, DEFAULT_EMPTY_VAL);
        String              infoUrl    = getParameterValue(element, depth, PARAM_INFO_URL, String.class, DEFAULT_EMPTY_VAL);
        String              value      = getParameterValue(element, depth, PARAM_VALUE, String.class, "NOT_SELECTED");
        String              valueSrc   = getParameterValue(element, depth, "values", String.class, DEFAULT_EMPTY_VAL);
        InputValueList      values     =
            valueSrc.isBlank()
                ? handleInputValueChildren(element, depth, false)
                : handlePossiblePlaceholder("$$value$" + valueSrc, InputValueList.class, depth);

        return Select
            .builder()
            .uid(uid)
            .classes(classes)
            .dataAttributes(attributes)
            .tooltip(tooltip)
            .name(name)
            .submitAs(submitAs)
            .onInput(onInput)
            .infoText(infoText)
            .infoUrl(infoUrl)
            .selected(value)
            .values(values)
            .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link Slider} input.
     *
     * @param element to transfrom
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private Slider transformSliderInput(PseudoElement element, int depth)
    {
        String              uid        = resolveUid(element, depth);
        List<String>        classes    = resolveClasses(element.getClasses(), depth);
        Map<String, String> attributes = resolveAttributes(element, depth);
        String              tooltip    = getParameterValue(element, depth, PARAM_TOOLTIP, String.class, DEFAULT_EMPTY_VAL);
        String              name       = getParameterValue(element, depth, PARAM_NAME, String.class);
        String              submitAs   = getParameterValue(element, depth, PARAM_SUBMIT_AS, String.class);
        String              onInput    = getParameterValue(element, depth, PARAM_ON_INPUT, String.class, DEFAULT_EMPTY_VAL);
        String              infoText   = getParameterValue(element, depth, PARAM_INFO_TEXT, String.class, DEFAULT_EMPTY_VAL);
        String              infoUrl    = getParameterValue(element, depth, PARAM_INFO_URL, String.class, DEFAULT_EMPTY_VAL);
        Integer             value      = getParameterValue(element, depth, PARAM_VALUE, Integer.class, 0);
        Integer             min        = getParameterValue(element, depth, PARAM_MIN, Integer.class, Integer.MIN_VALUE);
        Integer             max        = getParameterValue(element, depth, PARAM_MAX, Integer.class, Integer.MAX_VALUE);

        return Slider
            .builder()
            .uid(uid)
            .classes(classes)
            .dataAttributes(attributes)
            .tooltip(tooltip)
            .name(name)
            .submitAs(submitAs)
            .onInput(onInput)
            .infoText(infoText)
            .infoUrl(infoUrl)
            .value(value)
            .min(min)
            .max(max)
            .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link Switch} input.
     *
     * @param element to transfrom
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private Switch transformSwitchInput(PseudoElement element, int depth)
    {
        String              uid        = resolveUid(element, depth);
        List<String>        classes    = resolveClasses(element.getClasses(), depth);
        Map<String, String> attributes = resolveAttributes(element, depth);
        String              tooltip    = getParameterValue(element, depth, PARAM_TOOLTIP, String.class, DEFAULT_EMPTY_VAL);
        String              name       = getParameterValue(element, depth, PARAM_NAME, String.class);
        String              submitAs   = getParameterValue(element, depth, PARAM_SUBMIT_AS, String.class);
        String              onInput    = getParameterValue(element, depth, PARAM_ON_INPUT, String.class, DEFAULT_EMPTY_VAL);
        String              infoText   = getParameterValue(element, depth, PARAM_INFO_TEXT, String.class, DEFAULT_EMPTY_VAL);
        String              infoUrl    = getParameterValue(element, depth, PARAM_INFO_URL, String.class, DEFAULT_EMPTY_VAL);
        Boolean             checked    = getParameterValue(element, depth, PARAM_CHECKED, Boolean.class, Boolean.FALSE);

        return Switch
            .builder()
            .uid(uid)
            .classes(classes)
            .dataAttributes(attributes)
            .tooltip(tooltip)
            .name(name)
            .submitAs(submitAs)
            .onInput(onInput)
            .infoText(infoText)
            .infoUrl(infoUrl)
            .checked(checked)
            .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link TagInput} input.
     *
     * @param element to transfrom
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private Tag transformTagInput(PseudoElement element, int depth)
    {
        String              uid              = resolveUid(element, depth);
        List<String>        classes          = resolveClasses(element.getClasses(), depth);
        Map<String, String> attributes       = resolveAttributes(element, depth);
        String              tooltip          = getParameterValue(element, depth, PARAM_TOOLTIP, String.class, DEFAULT_EMPTY_VAL);
        String              name             = getParameterValue(element, depth, PARAM_NAME, String.class);
        String              submitAs         = getParameterValue(element, depth, PARAM_SUBMIT_AS, String.class);
        String              onInput          = getParameterValue(element, depth, PARAM_ON_INPUT, String.class, DEFAULT_EMPTY_VAL);
        String              infoText         = getParameterValue(element, depth, PARAM_INFO_TEXT, String.class, DEFAULT_EMPTY_VAL);
        String              infoUrl          = getParameterValue(element, depth, PARAM_INFO_URL, String.class, DEFAULT_EMPTY_VAL);
        String              value            = getParameterValue(element, depth, PARAM_VALUE, String.class, DEFAULT_EMPTY_VAL);
        String              pattern          = getParameterValue(element, depth, "pattern", String.class, DEFAULT_EMPTY_VAL);
        String              fetchUrl         = getParameterValue(element, depth, "fetchUrl", String.class, DEFAULT_EMPTY_VAL);
        String              searchUrl        = getParameterValue(element, depth, "searchUrl", String.class, DEFAULT_EMPTY_VAL);
        Boolean             enforceWhitelist = getParameterValue(element, depth, "enforceWhitelist", Boolean.class, Boolean.FALSE);
        Integer             maxTags          = getParameterValue(element, depth, "maxTags", Integer.class, Integer.MAX_VALUE);

        TagInputSettings    settings         = TagInputSettings.builder()
            .id(uid)
            .enforceWhitelist(enforceWhitelist)
            .fetchWhitelistUrl(fetchUrl)
            .searchTagsUrl(searchUrl)
            .maxTags(maxTags)
            .build();

        functions.add(new RegisterTagInput(settings));

        return Tag
            .builder()
            .uid(uid)
            .classes(classes)
            .dataAttributes(attributes)
            .tooltip(tooltip)
            .name(name)
            .submitAs(submitAs)
            .onInput(onInput)
            .infoText(infoText)
            .infoUrl(infoUrl)
            .value(value)
            .pattern(pattern)
            .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link Textarea} input.
     *
     * @param element to transfrom
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private Textarea transformTextareaInput(PseudoElement element, int depth)
    {
        String              uid         = resolveUid(element, depth);
        List<String>        classes     = resolveClasses(element.getClasses(), depth);
        Map<String, String> attributes  = resolveAttributes(element, depth);
        String              tooltip     = getParameterValue(element, depth, PARAM_TOOLTIP, String.class, DEFAULT_EMPTY_VAL);
        String              name        = getParameterValue(element, depth, PARAM_NAME, String.class);
        String              submitAs    = getParameterValue(element, depth, PARAM_SUBMIT_AS, String.class);
        String              onInput     = getParameterValue(element, depth, PARAM_ON_INPUT, String.class, DEFAULT_EMPTY_VAL);
        String              infoText    = getParameterValue(element, depth, PARAM_INFO_TEXT, String.class, DEFAULT_EMPTY_VAL);
        String              infoUrl     = getParameterValue(element, depth, PARAM_INFO_URL, String.class, DEFAULT_EMPTY_VAL);
        String              value       = getParameterValue(element, depth, PARAM_VALUE, String.class, DEFAULT_EMPTY_VAL);
        Integer             maxChars    = getParameterValue(element, depth, PARAM_MAX_CHARS, Integer.class, 32000);
        String              placeholder = getParameterValue(element, depth, PARAM_PLACEHOLDER, String.class, DEFAULT_EMPTY_VAL);

        return Textarea
            .builder()
            .uid(uid)
            .classes(classes)
            .dataAttributes(attributes)
            .tooltip(tooltip)
            .name(name)
            .submitAs(submitAs)
            .onInput(onInput)
            .infoText(infoText)
            .infoUrl(infoUrl)
            .value(value)
            .maxCharacters(maxChars)
            .placeholder(placeholder)
            .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link Textbox} input.
     *
     * @param element to transfrom
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private Textbox transformTextboxInput(PseudoElement element, int depth)
    {
        String value = getParameterValue(element, depth, PARAM_VALUE, String.class, DEFAULT_EMPTY_VAL);

        return Textbox
            .builder()
            .onInput(DEFAULT_EMPTY_VAL)
            .value(value)
            .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link Textfield} input.
     *
     * @param element to transfrom
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private Textfield transformTextfieldInput(PseudoElement element, int depth)
    {
        String              uid          = resolveUid(element, depth);
        List<String>        classes      = resolveClasses(element.getClasses(), depth);
        Map<String, String> attributes   = resolveAttributes(element, depth);
        String              tooltip      = getParameterValue(element, depth, PARAM_TOOLTIP, String.class, DEFAULT_EMPTY_VAL);
        String              name         = getParameterValue(element, depth, PARAM_NAME, String.class);
        String              submitAs     = getParameterValue(element, depth, PARAM_SUBMIT_AS, String.class);
        String              onInput      = getParameterValue(element, depth, PARAM_ON_INPUT, String.class, DEFAULT_EMPTY_VAL);
        String              infoText     = getParameterValue(element, depth, PARAM_INFO_TEXT, String.class, DEFAULT_EMPTY_VAL);
        String              infoUrl      = getParameterValue(element, depth, PARAM_INFO_URL, String.class, DEFAULT_EMPTY_VAL);
        String              value        = getParameterValue(element, depth, PARAM_VALUE, String.class, DEFAULT_EMPTY_VAL);
        String              placeholder  = getParameterValue(element, depth, PARAM_PLACEHOLDER, String.class, DEFAULT_EMPTY_VAL);
        String              prefix       = getParameterValue(element, depth, PARAM_PREFIX, String.class, DEFAULT_EMPTY_VAL);
        String              suffix       = getParameterValue(element, depth, PARAM_SUFFIX, String.class, DEFAULT_EMPTY_VAL);
        Integer             maxChars     = getParameterValue(element, depth, PARAM_MAX_CHARS, Integer.class, 150);
        String              onEnterPress = getParameterValue(element, depth, PARAM_ON_ENTER_PRESS, String.class, DEFAULT_EMPTY_VAL);

        return Textfield
            .builder()
            .uid(uid)
            .classes(classes)
            .dataAttributes(attributes)
            .tooltip(tooltip)
            .name(name)
            .submitAs(submitAs)
            .onInput(onInput)
            .infoText(infoText)
            .infoUrl(infoUrl)
            .value(value)
            .placeholder(placeholder)
            .prefix(prefix)
            .suffix(suffix)
            .maxCharacters(maxChars)
            .onEnterPress(onEnterPress)
            .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link SVGContainer}.
     *
     * @param element to transfrom
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private List<SVGElement> transformSVGElement(PseudoElement element, int depth, List<SVGType> allowedTypes)
    {
        this.count++;

        List<SVGElement> resultList     = new ArrayList<>();

        String           uid            = element.getUid() == null ? RANDOM_ID : element.getUid();

        boolean          processElement = shouldProcess(element, uid, depth);

        if (!processElement)
        {
            LOG.debug("[{}]:[{}]: skip svg element [{}] due to parameter [active] is [false] or [uid] is deactivated", uuid, depth, uid);

            return resultList;
        }

        try
        {
            SVGType type = SVGType.valueOf(element.getType().toUpperCase());

            if (!allowedTypes.contains(type))
            {
                return List.of(notAllowedSVGElement());
            }

            LOG.debug("[{}]:[{}]: map svg element [{}] as [{}]", uuid, depth, uid, type);

            int                 numberOfRepetitions = 1;
            FlatMappableList<?> targetList          = null;
            FlatMappable        target              = null;

            try
            {
                String targetListName = getParameterValue(element, depth, PARAM_REPETITION_SOURCE, String.class);
                targetList = extractFromTokenMapsAsValue(targetListName, FlatMappableList.class);

                numberOfRepetitions = targetList.size();

                LOG.debug("[{}]:[{}]: repeat svg element [{}] times", uuid, depth, numberOfRepetitions);
            }
            catch (JsonParsingException ex)
            {
                LOG.trace("[{}]:[{}]: repetitionSource not set", uuid, depth);

                try
                {
                    String targetName = getParameterValue(element, depth, PARAM_ELEMENT_SOURCE, String.class);
                    target = extractFromTokenMapsAsValue(targetName, FlatMappable.class);

                    LOG.debug("[{}]:[{}]: source element found", uuid, depth);
                }
                catch (JsonParsingException exInner)
                {
                    LOG.trace("[{}]:[{}]: elementSource not set", uuid, depth);
                }
            }
            catch (JsonMapperException ex)
            {
                LOG.error("elementSource/repetitionSource expected but not found", ex);

                resultList.add(failureSVGElement());

                return resultList;
            }

            for (int i = 0; i < numberOfRepetitions; i++)
            {
                TokenMap      tempTokenMap  = null;
                PseudoElement repeatElement =
                    numberOfRepetitions > 1 && element.getUid() != null
                        ? element
                            .toBuilder()
                            .uid(uid + "-" + i)
                            .build()
                        : element;

                if (targetList != null)
                {
                    tempTokenMap = flatMappableDissector.dissect(targetList.get(i), depth, locale);

                    tempTokenMaps.put(tempTokenMap.hashCode(), tempTokenMap);
                }

                if (target != null)
                {
                    tempTokenMap = flatMappableDissector.dissect(target, depth, locale);

                    tempTokenMaps.put(tempTokenMap.hashCode(), tempTokenMap);
                }

                if (LOG.isTraceEnabled() && !tempTokenMaps.isEmpty())
                {
                    LOG.debug("[{}]:[{}]: with following temp token maps", uuid, depth);

                    tempTokenMaps.values()
                        .stream()
                        .map(TokenMap::toString)
                        .map(String::lines)
                        .forEach(stream -> stream.forEach(line -> LOG.trace("[{}]: {}", uuid, line)));
                }

                SVGElement result = switch (type)
                {
                    case GROUP -> transformSVGGroup(repeatElement, depth);
                    case LINE -> transformSVGLine(repeatElement, depth);
                    case TEXT -> transformSVGText(repeatElement, depth);
                };

                resultList.add(result);

                if (tempTokenMap != null)
                {
                    tempTokenMaps.remove(tempTokenMap.hashCode());
                }
            }
        }
        catch (Exception ex)
        {
            LOG.error("error parsing svg element", ex);

            return List.of(failureSVGElement());
        }

        return resultList;
    }

    private SVGElement notAllowedSVGElement()
    {
        return SVGText
            .builder()
            .clazz("svg-text-warn")
            .text(String.format("the creation of this svg element is not allowed here."))
            .build();
    }

    private SVGElement failureSVGElement()
    {
        return SVGText
            .builder()
            .clazz("svg-text-warn")
            .text(String.format("the creation of this svg element failed. please rever to the server log. mapper id: [%s]", uuid))
            .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link SVGLine}.
     *
     * @param element to transfrom
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private SVGGroup transformSVGGroup(PseudoElement element, int depth)
    {
        String              uid        = resolveUid(element, depth);
        List<String>        classes    = resolveClasses(element.getClasses(), depth);
        Map<String, String> attributes = resolveAttributes(element, depth);
        String              tooltip    = getParameterValue(element, depth, PARAM_TOOLTIP, String.class, DEFAULT_EMPTY_VAL);
        String              style      = getParameterValue(element, depth, PARAM_STYLE, String.class, DEFAULT_EMPTY_VAL);

        List<SVGElement>    children   = new ArrayList<>();

        for (PseudoElement pe : element.getChildren())
        {
            children.addAll(transformSVGElement(pe, depth, SVGType.ALLOWED_BASE_TYPES));
        }

        return SVGGroup
            .builder()
            .uid(uid)
            .classes(classes)
            .dataAttributes(attributes)
            .tooltip(tooltip)
            .style(style)
            .children(children)
            .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link SVGLine}.
     *
     * @param element to transfrom
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private SVGLine transformSVGLine(PseudoElement element, int depth)
    {
        String              uid        = resolveUid(element, depth);
        List<String>        classes    = resolveClasses(element.getClasses(), depth);
        Map<String, String> attributes = resolveAttributes(element, depth);
        String              tooltip    = getParameterValue(element, depth, PARAM_TOOLTIP, String.class, DEFAULT_EMPTY_VAL);
        String              style      = getParameterValue(element, depth, PARAM_STYLE, String.class, DEFAULT_EMPTY_VAL);
        Double              x1         = getParameterValue(element, depth, PARAM_X1, Double.class);
        Double              x2         = getParameterValue(element, depth, PARAM_X2, Double.class);
        Double              y1         = getParameterValue(element, depth, PARAM_Y1, Double.class);
        Double              y2         = getParameterValue(element, depth, PARAM_Y2, Double.class);

        return SVGLine
            .builder()
            .uid(uid)
            .classes(classes)
            .dataAttributes(attributes)
            .tooltip(tooltip)
            .style(style)
            .x1(x1)
            .x2(x2)
            .y1(y1)
            .y2(y2)
            .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link SVGText}.
     *
     * @param element to transfrom
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private SVGText transformSVGText(PseudoElement element, int depth)
    {
        String              uid        = resolveUid(element, depth);
        List<String>        classes    = resolveClasses(element.getClasses(), depth);
        Map<String, String> attributes = resolveAttributes(element, depth);
        String              tooltip    = getParameterValue(element, depth, PARAM_TOOLTIP, String.class, DEFAULT_EMPTY_VAL);
        String              style      = getParameterValue(element, depth, PARAM_STYLE, String.class, DEFAULT_EMPTY_VAL);
        String              text       = getParameterValue(element, depth, PARAM_TEXT, String.class);
        Double              x          = getParameterValue(element, depth, PARAM_X, Double.class, 0d);
        Double              y          = getParameterValue(element, depth, PARAM_Y, Double.class, 0d);
        Double              dx         = getParameterValue(element, depth, PARAM_DX, Double.class, 0d);
        Double              dy         = getParameterValue(element, depth, PARAM_DY, Double.class, 0d);
        Double              rotate     = getParameterValue(element, depth, PARAM_ROTATE, Double.class, 0d);
        Double              textLength = getParameterValue(element, depth, PARAM_TEXT_LENGTH, Double.class, -1d);

        return SVGText
            .builder()
            .uid(uid)
            .classes(classes)
            .dataAttributes(attributes)
            .tooltip(tooltip)
            .style(style)
            .text(text)
            .x(x)
            .y(y)
            .dx(dx)
            .dy(dy)
            .rotate(rotate)
            .textLength(textLength)
            .build();
    }

}
