/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.hmi.mapper.json;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.cookindustries.lib.spring.gui.function.AbsFunctionCall;
import de.cookindustries.lib.spring.gui.function.RegisterTagInput;
import de.cookindustries.lib.spring.gui.hmi.Position;
import de.cookindustries.lib.spring.gui.hmi.container.*;
import de.cookindustries.lib.spring.gui.hmi.input.*;
import de.cookindustries.lib.spring.gui.hmi.input.Number;
import de.cookindustries.lib.spring.gui.hmi.input.marker.Marker;
import de.cookindustries.lib.spring.gui.hmi.input.marker.MarkerCategory;
import de.cookindustries.lib.spring.gui.hmi.input.marker.MarkerType;
import de.cookindustries.lib.spring.gui.hmi.input.util.InputValue;
import de.cookindustries.lib.spring.gui.hmi.input.util.InputValueList;
import de.cookindustries.lib.spring.gui.hmi.mapper.exception.JsonMapperException;
import de.cookindustries.lib.spring.gui.hmi.mapper.exception.JsonParsingException;
import de.cookindustries.lib.spring.gui.hmi.mapper.util.FlatMappable;
import de.cookindustries.lib.spring.gui.hmi.mapper.util.FlatMappableDissector;
import de.cookindustries.lib.spring.gui.hmi.mapper.util.FlatMappableList;
import de.cookindustries.lib.spring.gui.hmi.mapper.util.TokenMap;
import de.cookindustries.lib.spring.gui.hmi.util.TemplateFileCache;
import de.cookindustries.lib.spring.gui.i18n.AbsTranslationProvider;
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

    private static final String                    SRC                            = "src";
    private static final String                    SUFFIX                         = "suffix";
    private static final String                    PREFIX                         = "prefix";
    private static final String                    MAX_CHARS                      = "maxChars";
    private static final String                    TARGET                         = "target";
    private static final String                    HREF                           = "href";
    private static final String                    MAX                            = "max";
    private static final String                    MIN                            = "min";
    private static final String                    CHECKED                        = "checked";
    private static final String                    IMAGE                          = "image";
    private static final String                    ON_CLICK                       = "onClick";
    private static final String                    BTN_CLASS                      = "btnClass";
    private static final String                    TEXT                           = "text";
    private static final String                    DEFAULT_DATE                   = "0000-00-00";
    private static final String                    PLACEHOLDER                    = "placeholder";
    private static final String                    DEFAULT_VAL                    = "";
    private static final String                    NAME                           = "name";
    private static final String                    VALUE                          = "value";
    private static final String                    SUBMIT_AS                      = "submitAs";
    private static final String                    ON_INPUT                       = "onInput";
    private static final String                    TOOLTIP                        = "tooltip";
    private static final String                    TOOLTIP_POSITION               = "tooltipPosition";
    private static final String                    ACTIVE                         = "active";
    private static final String                    INFO_TEXT                      = "infoText";
    private static final String                    INFO_URL                       = "infoUrl";

    private static final String                    BASE_INDICATOR_START           = "$$";
    private static final String                    INDICATOR_VALUE_PLACEHOLDER    = "$$value$";
    private static final String                    INDICATOR_TEXT_PLACEHOLDER     = "$$text$";
    private static final String                    INDICATOR_CLASS_PLACEHOLDER    = "$$class$";
    private static final String                    INDICATOR_FUNCTION_PLACEHOLDER = "$$function$";

    private static final String                    LOG_INDENT_STRING              = "- ";

    private static final StaticTranslationProvider NOOP_TRANSLATION_PROVIDER      = new StaticTranslationProvider();

    private static final ContainerType[]           CONTAINER_CHILDREN             =
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
            ContainerType.TAB,
            ContainerType.TABLE,
            ContainerType.TEXT,
            ContainerType.HEADING
        };

    private static final ContainerType[]           LINK_CHILDREN                  = {ContainerType.TEXT};

    private static final ContainerType[]           TABLE_ROW_CHILDREN             =
        Arrays
            .stream(ContainerType.values())
            .filter(ct -> !List.of(
                ContainerType.MODAL,
                ContainerType.TABLE,
                ContainerType.TABLE_ROW,
                ContainerType.TAB,
                ContainerType.FORM)
                .contains(ct))
            .toList()
            .toArray(new ContainerType[0]);

    private static final ContainerType[]           MODAL_CHILDREN                 =
        {
            ContainerType.FORM,
            ContainerType.IMAGE,
            ContainerType.LINK,
            ContainerType.TEXT,
            ContainerType.HEADING,
            ContainerType.BURGER
        };

    @NonNull
    private final TemplateFileCache                templateFileCache;

    @NonNull
    private final String                           path;

    private PseudoElement                          root;

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

    private final List<AbsFunctionCall>            functions                      = new ArrayList<>();

    @Default
    private final String                           uuid                           = Long.toString(System.currentTimeMillis(), 36);;

    @Default
    private Integer                                count                          = 0;

    /**
     * Run the mapping process.
     * 
     * @return the mapped result
     */
    public MapperResult map()
    {
        long start = System.nanoTime();

        this.root = templateFileCache.getTemplateTo(path);

        LOG.debug("[{}]: ### start ###", uuid);
        LOG.trace("[{}]: map content for [{}] from [{}] with [{}] value maps", uuid, locale.toLanguageTag(),
            translationProvider.getClass().getSimpleName(), tokenMaps.size());

        tokenMaps = tokenMaps
            .stream()
            .filter(Objects::nonNull)
            .sorted(
                Comparator
                    .comparing(TokenMap::getPresedence)
                    .reversed())
            .toList();

        MapperResult result =
            MapperResult
                .builder()
                .containers(transform(root))
                .functions(functions)
                .build();

        long         end    = System.nanoTime();
        long         time   = end - start;

        LOG.debug("[{}]: ### done ### ... mapping took [{}]ms", uuid, time / 1000000L);

        return result;
    }

    /*
     * --- > utility functions -------------------------------------------------------------------------------------------------------------
     */

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
     * @return the resolved {@code uid}
     */
    private String resolveUid(PseudoElement element, int depth)
    {
        String uid = element.getUid();

        if (uid == null)
        {
            uid = UUID.randomUUID().toString();
        }
        else if (uid.startsWith(INDICATOR_VALUE_PLACEHOLDER))
        {
            uid = resolveUid(element, depth);
        }

        String indent = LOG_INDENT_STRING.repeat(depth + 1);

        LOG.trace("[{}]:[{}]:{}resolve uid to [{}]", uuid, depth, indent, uid);

        return uid;
    }

    /**
     * Traverse the css classes and replace {@code $$class$} parameters from {@link #valueMaps}.
     * <p>
     * Not found {@code class}es will be ignored.
     * 
     * @param classes {@code List} of all classes {@code String}s to traverse
     * @return a {@code List} of {@code String}s containing the non-altered and the replaced classes
     */
    private List<String> resolveClasses(List<String> classes, int depth)
    {
        String indent = LOG_INDENT_STRING.repeat(depth + 1);

        LOG.trace("[{}]:[{}]:{}resolve classes {}", uuid, depth, indent, classes);

        return classes
            .stream()
            .map(c -> {
                if (c.startsWith(INDICATOR_CLASS_PLACEHOLDER))
                {
                    String key = c.replace(INDICATOR_CLASS_PLACEHOLDER, "");
                    String text = extractFromTokenMapsAsClass(key);

                    if (text == null)
                    {
                        return "";
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

        String              indent     = LOG_INDENT_STRING.repeat(depth + 1);

        LOG.trace("[{}]:[{}]:{}resolve attributes {}", uuid, depth, indent, attributes);

        return attributes;
    }

    /**
     * Tries to handle a placeholder by fetching from {@code #valueMaps} based on {@code key} and {@code expectedType}.
     * <p>
     * If dynamic handling mode is enabled, it checks for indicator placeholders in the key to determine which mapping to use. If no value
     * is found, the {@code null} is returned.
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
     * Tries to handle a placeholder by fetching from {@code valueMaps} based on {@code key} and {@code expectedType}.
     * <p>
     * If dynamic handling mode is enabled, it checks for indicator placeholders in the {@code key} to determine which mapping to use. If no
     * value is found, the {@code defaultValue} is returned.
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
        I      value  = null;

        String indent = LOG_INDENT_STRING.repeat(depth + 1);

        LOG.trace("[{}]:[{}]:{}try resolving placeholder [{}] as [{}]", uuid, depth, indent, key, expectedType.getSimpleName());

        if (key == null)
        {
            throw new JsonMapperException("trying to parse a [null] parameter key");
        }
        else
        {
            if (key.startsWith(INDICATOR_TEXT_PLACEHOLDER) && expectedType.equals(String.class))
            {
                LOG.trace("[{}]:[{}]:{}resolve as [TEXT]", uuid, depth, indent);

                String keyName = key.substring(INDICATOR_TEXT_PLACEHOLDER.length());
                value = expectedType.cast(translationProvider.getText(locale, keyName));
            }
            else if (key.startsWith(INDICATOR_VALUE_PLACEHOLDER))
            {
                LOG.trace("[{}]:[{}]:{}resolve as [VALUE]", uuid, depth, indent);

                String keyName = key.substring(INDICATOR_VALUE_PLACEHOLDER.length()).replaceFirst("[?]", "[]");
                value = extractFromTokenMapsAsValue(keyName, expectedType);
            }
            else if (key.startsWith(INDICATOR_CLASS_PLACEHOLDER))
            {
                LOG.trace("[{}]:[{}]:{}resolve as [CLASS]", uuid, depth, indent);

                String keyName = key.substring(INDICATOR_CLASS_PLACEHOLDER.length());
                value = expectedType.cast(extractFromTokenMapsAsClass(keyName));
            }
            else if (key.startsWith(INDICATOR_FUNCTION_PLACEHOLDER))
            {
                LOG.trace("[{}]:[{}]:{}resolve as [FUNCTION]", uuid, depth, indent);

                String keyName = key.substring(INDICATOR_FUNCTION_PLACEHOLDER.length());
                value = expectedType.cast(extractFromTokenMapsAsFunction(keyName));
            }
        }

        if (value == null && defaultValue != null)
        {
            value = defaultValue;
        }

        LOG.trace("[{}]:[{}]:{}resolved to [{}]", uuid, depth, indent, String.valueOf(value));

        return value;
    }

    /**
     * Extract a value from {@link JsonMapper#valueMaps}
     *
     * @param <I> expected type of value
     * @param paramName name of parameter
     * @param expectedType class of value
     * @return the value associated with {@code paramName}
     * @throws JsonMapperException if value is not of type {@code expectedType}
     */
    private <I> I extractFromTokenMapsAsValue(String objName, Class<I> expectedType) throws JsonMapperException
    {
        Optional<Object> result =
            tokenMaps.stream()
                .filter(Objects::nonNull)
                .map(m -> m.getValue(objName))
                .filter(Objects::nonNull)
                .findFirst();

        if (result.isEmpty())
        {
            return null;
        }

        Object o = result.get();

        if (expectedType.isInstance(o))
        {
            return expectedType.cast(o);
        }
        else if (o instanceof String s)
        {
            return transformRawValue(objName, 0, s, expectedType);
        }

        throw new JsonMapperException(String
            .format("object [%s] could not be extracted due to expected class is [%s] but got [%s]", objName,
                expectedType.getSimpleName(),
                o.getClass().getSimpleName()));
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
        return tokenMaps.stream()
            .filter(Objects::nonNull)
            .map(m -> m.getClazz(objName))
            .filter(Objects::nonNull)
            .findFirst()
            .orElse(null);
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
        return tokenMaps.stream()
            .filter(Objects::nonNull)
            .map(m -> m.getFunction(objName))
            .filter(Objects::nonNull)
            .findFirst()
            .orElse(null);
    }

    /**
     * Check if a {@code uid} is disabled {@link JsonMapper#tokenMaps}
     *
     * @param uid of the element to check
     * @return {@code false}, if the element is marked as inactive, {@code true} otherwise
     * @throws JsonMapperException if value is not of type {@code expectedType}
     */
    private boolean checkActiveStateFromTokenMaps(String uid) throws JsonMapperException
    {

        return uid == null
            ? true
            : tokenMaps.stream()
                .filter(Objects::nonNull)
                .map(m -> m.isUidActive(uid))
                .filter(Objects::nonNull)
                .allMatch(b -> b);
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
        String rawValue = element.getParameters().get(key);
        T      value    = null;

        String indent   = LOG_INDENT_STRING.repeat(depth + 1);

        String uid      = element.getUid() == null ? "random id" : element.getUid();

        LOG.trace("[{}]:[{}]:{}get parameter [{}] from [{}] as [{}]", uuid, depth, indent, key, uid,
            expectedType.getSimpleName());

        if (rawValue == null || rawValue.isBlank())
        {
            if (fallback == null)
            {
                throw new JsonParsingException(uid, 0, this.count,
                    String.format("parameter [%s] is expected but not set", key));
            }

            LOG.trace("[{}]:[{}]:{}parameter is [{}]", uuid, depth, indent, String.valueOf(fallback));

            return fallback;
        }

        if (rawValue.startsWith(BASE_INDICATOR_START) && processPlaceholder)
        {
            value = handlePossiblePlaceholder(rawValue, expectedType, fallback, depth);
        }
        else
        {
            value = transformRawValue(uid, depth, rawValue, expectedType);
        }

        LOG.trace("[{}]:[{}]:{}parameter is [{}]", uuid, depth, indent, String.valueOf(value));

        return value;
    }

    /**
     * Transform the raw value of a parameter
     * <p>
     * 'true' gets cast to a boolean value
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

        if (expectedType.equals(String.class))
        {
            return expectedType.cast(value);
        }

        throw new JsonParsingException(uid, depth, 0,
            String.format("could not transform [%s] to the expected type [%s]", value, expectedType));
    }

    /**
     * Transform a {@link PseudoElement} as a internal component
     *
     * @param element to transfrom
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private MapperResult transformInternalComponent(PseudoElement element, int depth)
    {
        String       indent       = LOG_INDENT_STRING.repeat(depth + 1);
        String       path         = getParameterValue(element, depth, "path", String.class);
        Boolean      isSourceList = getParameterValue(element, depth, "isSourceList", Boolean.class, false);
        String       sourceKey    = null;
        MapperResult result       = null;

        try
        {
            sourceKey = getParameterValue(element, depth, "sourceKey", String.class);
        }
        catch (Exception ex)
        {
            // ignore it
        }

        if (sourceKey != null)
        {
            if (isSourceList)
            {
                FlatMappableList   srcList = extractFromTokenMapsAsValue(sourceKey, FlatMappableList.class);

                List<MapperResult> results = new ArrayList<>();

                srcList.getElements().forEach(src -> {
                    JsonMapper internalMapper =
                        JsonMapper
                            .builder()
                            .templateFileCache(templateFileCache)
                            .translationProvider(translationProvider)
                            .flatMappableDissector(flatMappableDissector)
                            .path(path)
                            .locale(locale)
                            .tokenMaps(tokenMaps)
                            .tokenMap(flatMappableDissector.dissect(src, depth, locale))
                            .build();

                    LOG.trace("[{}]:[{}]:{}map linked component list element [{}] @ [{}]", uuid, depth, indent, internalMapper.uuid, path);

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
                        .path(path)
                        .locale(locale)
                        .tokenMaps(tokenMaps)
                        .tokenMap(flatMappableDissector.dissect(srcElement, depth, locale))
                        .build();

                LOG.trace("[{}]:[{}]:{}map linked sourced component [{}] @ [{}]", uuid, depth, indent, internalMapper.uuid, path);

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
                    .path(path)
                    .locale(locale)
                    .tokenMaps(tokenMaps)
                    .build();

            LOG.trace("[{}]:[{}]:{}map linked component with [{}] @ [{}]", uuid, depth, indent, internalMapper.uuid, path);

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
        String indent = LOG_INDENT_STRING.repeat(depth + 1);

        LOG.trace("[{}]:[{}]:{}map input children", uuid, depth, indent);

        InputValueList values = new InputValueList();

        for (PseudoElement pe : element.getChildren())
        {
            String  uid            = element.getUid() == null ? "random uid" : element.getUid();

            boolean processElement = shouldProcess(element, uid, depth);

            if (!processElement)
            {
                LOG.debug("[{}]:[{}]:{} skip InputValue [{}] due to parameter [active] is [false] or [uid] is deactivated", uuid, depth,
                    indent, uid);

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
     * Transform a {@link PseudoElement} to an {@link InputValue}
     *
     * @param element to transfrom
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private InputValue transformInputValue(PseudoElement element, int depth)
    {
        List<String> classes = resolveClasses(element.getClasses(), depth);
        String       id      = getParameterValue(element, depth, "id", String.class, "");
        String       text    = getParameterValue(element, depth, TEXT, String.class, "no text set");
        String       value   = getParameterValue(element, depth, VALUE, String.class, "no value set");
        Boolean      checked = getParameterValue(element, depth, CHECKED, Boolean.class, Boolean.FALSE);

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
     * Internal JSON transform
     *
     * @param root
     * @return the transformed {@code Container}
     */
    private List<Container> transform(PseudoElement root) throws JsonMapperException
    {
        return transform(root, 0, ContainerType.values());
    }

    /**
     * Transform {@link PseudoMarker} from a {@link PseudoElement} to internal {@link Marker}s.
     * 
     * @param ownerId uid of the parent
     * @param marker list of markers to transform
     * @return list containing all markers
     */
    private List<Marker> transformMarker(String ownerId, List<PseudoMarker> marker, int depth)
    {
        List<Marker> list = new ArrayList<>();

        for (PseudoMarker psm : marker)
        {
            MarkerCategory category = MarkerCategory.valueOf(psm.getCategory().toUpperCase());
            MarkerType     type     = MarkerType.valueOf(psm.getType().toUpperCase());
            String         rawText  = psm.getText();
            String         text     =
                rawText.startsWith(BASE_INDICATOR_START)
                    ? handlePossiblePlaceholder(rawText, String.class, depth)
                    : rawText;

            list.add(
                Marker
                    .builder()
                    .ownerId(ownerId)
                    .category(category)
                    .type(type)
                    .text(text)
                    .build());
        }

        return list;
    }

    private boolean shouldProcess(PseudoElement element, String uid, int depth)
    {
        return getParameterValue(element, depth, ACTIVE, Boolean.class, true)
            && checkActiveStateFromTokenMaps(uid);
    }

    /*
     * --- < utility functions -------------------------------------------------------------------------------------------------------------
     */

    /**
     * Entry function to transform elements from JSON to {@link Container} objects
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

        String indent = LOG_INDENT_STRING.repeat(depth + 1);

        LOG.trace("");

        String  uid            = element.getUid() == null ? "random uid" : element.getUid();

        boolean processElement = shouldProcess(element, uid, depth);

        if (!processElement)
        {
            LOG.debug("[{}]:[{}]:{} skip CONTAINER [{}] due to parameter [active] is [false] or [uid] is deactivated", uuid, depth, indent,
                uid);

            return List.of(EmptyContainer.builder().build());
        }

        LOG.trace("[{}]:[{}]:{}map [{}] with allowed types [{}]", uuid, depth, indent, uid, allowedTypes);

        InternalElementType internalType = null;

        try
        {
            try
            {
                internalType = InternalElementType.valueOf(element.getType().toUpperCase());
            }
            catch (Exception ex)
            {
                // do nothing since it is not a internal type
            }

            if (internalType != null)
            {
                switch (internalType)
                {
                    case COMPONENT:
                        MapperResult result = transformInternalComponent(element, depth);

                        functions.addAll(result.getFunctions());

                        return result.getContainers();
                }
            }

            ContainerType type = ContainerType.valueOf(element.getType().toUpperCase());

            if (!Arrays.asList(allowedTypes).contains(type))
            {
                throw new JsonParsingException(uid, depth, this.count,
                    String.format("container type [%s] is not allowed here", element.getType()));
            }

            LOG.debug("[{}]:[{}]:{}map CONTAINER [{}] as [{}]", uuid, depth, indent, uid, type);

            Container result = switch (type)
            {
                case AUDIO -> transfromAudioContainer(element, depth);
                case BURGER -> transfromBurgerContainer(element, depth);
                case BUTTON -> transfromButtonContainer(element, depth);
                case BUTTON_BAR -> transformButtonBarContainer(element, depth);
                case BUTTON_ICON -> transformButtonIconContainer(element, depth);
                case CONTENT -> transformContentContainer(element, depth);
                case EMPTY -> EmptyContainer.builder().build();
                case FORM -> transformFormContainer(element, depth);
                case HEADING -> transformHeadingContainer(element, depth);
                case HIDDEN -> transformHiddenContainer(element, depth);
                case IMAGE -> transformImageContainer(element, depth);
                case LINK -> transformLinkContainer(element, depth);
                case MODAL -> transformModal(element, depth);
                case SPLITTED -> transformSplittedContainer(element, depth);
                case TAB -> transformTabbedContainer(element, depth);
                case TABLE -> transformTableContainer(element, depth);
                case TABLE_ROW -> transformTableRowContainer(element, depth);
                case TEXT -> transformTextContainer(element, depth);
            };

            return List.of(result);
        }
        catch (Exception ex)
        {
            LOG.error("error parsing element", ex);

            return List.of(failureContainer());
        }
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
            .text(String.format("the creation of this element failed. please rever to the server log. mapper id: [%s]", uuid))
            .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link AudioContainer}
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
        String              src        = getParameterValue(element, depth, SRC, String.class);
        Boolean             controls   = getParameterValue(element, depth, "controls", Boolean.class, Boolean.FALSE);
        Boolean             autoplay   = getParameterValue(element, depth, "autoplay", Boolean.class, Boolean.FALSE);

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
     * Transform a {@link PseudoElement} to an {@link BurgerContainer}
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
        String              image      = getParameterValue(element, depth, IMAGE, String.class, "/images/burger-menu-icon.svg");

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
            .image(image)
            .items(items)
            .build();
    }

    private BurgerItem transformEntry(PseudoElement element, int depth)
    {
        String  uid            = element.getUid() == null ? "random uid" : element.getUid();
        boolean processElement = shouldProcess(element, uid, depth);

        if (!processElement && !element.getType().equalsIgnoreCase("item"))
        {
            return null;
        }

        String image = getParameterValue(element, depth, IMAGE, String.class, DEFAULT_VAL);
        String url   = getParameterValue(element, depth, "url", String.class);
        String text  = getParameterValue(element, depth, "text", String.class);

        return BurgerItem
            .builder()
            .image(image)
            .url(url)
            .text(text)
            .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link ButtonContainer}
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
        String              text                = getParameterValue(element, depth, TEXT, String.class);
        ButtonClass         btnClass            =
            ButtonClass.valueOf(getParameterValue(element, depth, BTN_CLASS, String.class, "DEFAULT").toUpperCase());

        AbsFunctionCall     btnFunction         = null;
        String              fallbackBtnFunction = "";

        try
        {
            btnFunction =
                getParameterValue(element, depth, ON_CLICK, AbsFunctionCall.class);
        }
        catch (JsonParsingException ex)
        {
            fallbackBtnFunction =
                getParameterValue(element, depth, ON_CLICK, String.class, "");
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
     * Transform a {@link PseudoElement} to an {@link ButtonBarContainer}
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
     * Transform a {@link PseudoElement} to an {@link ButtonIconContainer}
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
        String              image      = getParameterValue(element, depth, IMAGE, String.class);
        ButtonClass         btnClass   = ButtonClass.valueOf(getParameterValue(element, depth, BTN_CLASS, String.class, "DEFAULT"));
        String              onClick    = getParameterValue(element, depth, ON_CLICK, String.class);
        String              title      = getParameterValue(element, depth, "title", String.class, "");

        return ButtonIcon
            .builder()
            .uid(uid)
            .classes(classes)
            .dataAttributes(attributes)
            .image(image)
            .btnClass(btnClass)
            .onClick(onClick)
            .title(title)
            .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link ContentContainer}
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
            .forEach(c -> contents.addAll(transform(c, depth + 1, CONTAINER_CHILDREN)));

        return ContentContainer
            .builder()
            .uid(uid)
            .classes(classes)
            .dataAttributes(attributes)
            .contents(contents)
            .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link FormContainer}
     *
     * @param element to transfrom
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private FormContainer transformFormContainer(PseudoElement element, int depth)
    {
        String              uid          = resolveUid(element, depth);
        List<String>        classes      = resolveClasses(element.getClasses(), depth);
        Map<String, String> attributes   = resolveAttributes(element, depth);
        String              connectedBtn = getParameterValue(element, depth, "connectedBtn", String.class, "");
        List<Input>         inputs       = new ArrayList<>();

        element
            .getChildren()
            .stream()
            .forEach(c -> inputs.add(transformInput(c, depth + 1)));

        FormContainer.FormContainerBuilder<?, ?> builder = FormContainer.builder();

        if (!connectedBtn.isBlank())
        {
            builder.dataAttribute("connected-btn", connectedBtn);
        }

        return builder
            .uid(uid)
            .classes(classes)
            .dataAttributes(attributes)
            .inputs(inputs)
            .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link HiddenContainer}
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
                .isEmpty() ? null : transform(element.getChildren().get(0), depth + 1, CONTAINER_CHILDREN).get(0);

        return HiddenContainer
            .builder()
            .uid(uid)
            .classes(classes)
            .dataAttributes(attributes)
            .child(child)
            .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link ImageContainer}
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
        String              src        = getParameterValue(element, depth, SRC, String.class);

        return ImageContainer
            .builder()
            .uid(uid)
            .classes(classes)
            .dataAttributes(attributes)
            .src(src)
            .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link LinkContainer}
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
        String              href       = getParameterValue(element, depth, HREF, String.class);
        String              target     = getParameterValue(element, depth, TARGET, String.class, "_self");
        Container           content    =
            element
                .getChildren()
                .isEmpty() ? null : transform(element.getChildren().get(0), depth + 1, LINK_CHILDREN).get(0);

        return LinkContainer
            .builder()
            .uid(uid)
            .classes(classes)
            .dataAttributes(attributes)
            .href(href)
            .target(target)
            .content(content)
            .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link Modal}
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
        String              name                    = getParameterValue(element, depth, NAME, String.class);
        String              requestUrl              = getParameterValue(element, depth, "requestUrl", String.class);
        Boolean             closeOnOverlayClick     =
            getParameterValue(element, depth, "closeOnOverlayClick", Boolean.class, Boolean.FALSE);

        String              btnNameLeft             = getParameterValue(element, depth, "btnNameLeft", String.class, "");
        ButtonClass         btnClassLeft            = ButtonClass.valueOf(
            getParameterValue(element, depth, "btnClassLeft", String.class, "DEFAULT").toUpperCase());

        AbsFunctionCall     btnFunctionLeft         = null;
        String              fallbackBtnFunctionLeft = "";

        try
        {
            btnFunctionLeft =
                getParameterValue(element, depth, "btnFunctionLeft", AbsFunctionCall.class);
        }
        catch (JsonParsingException ex)
        {
            fallbackBtnFunctionLeft =
                getParameterValue(element, depth, "btnFunctionLeft", String.class, "");
        }

        String          btnNameCenter             = getParameterValue(element, depth, "btnNameCenter", String.class, "");
        ButtonClass     btnClassCenter            = ButtonClass.valueOf(
            getParameterValue(element, depth, "btnClassCenter", String.class, "DEFAULT").toUpperCase());

        AbsFunctionCall btnFunctionCenter         = null;
        String          fallbackBtnFunctionCenter = "";

        try
        {
            btnFunctionCenter =
                getParameterValue(element, depth, "btnFunctionCenter", AbsFunctionCall.class);
        }
        catch (JsonParsingException ex)
        {
            fallbackBtnFunctionCenter =
                getParameterValue(element, depth, "btnFunctionCenter", String.class, "");
        }

        String          btnNameRight             = getParameterValue(element, depth, "btnNameRight", String.class);
        ButtonClass     btnClassRight            = ButtonClass.valueOf(
            getParameterValue(element, depth, "btnClassRight", String.class, "SUCCESS").toUpperCase());

        AbsFunctionCall btnFunctionRight         = null;
        String          fallbackBtnFunctionRight = "";

        try
        {
            btnFunctionRight =
                getParameterValue(element, depth, "btnFunctionRight", AbsFunctionCall.class);
        }
        catch (JsonParsingException ex)
        {
            fallbackBtnFunctionRight =
                getParameterValue(element, depth, "btnFunctionRight", String.class);
        }

        List<Container> children = new ArrayList<>();

        for (PseudoElement pe : element.getChildren())
        {
            children.addAll(transform(pe, depth + 1, MODAL_CHILDREN));
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
     * Transform a {@link PseudoElement} to an {@link SplittedContainer}
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
     * Transform a {@link PseudoElement} to an {@link TabContainer}
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
        String              name           = getParameterValue(element, depth, NAME, String.class);
        Integer             numOfColumns   = getParameterValue(element, depth, "numOfColumns", Integer.class);
        String              columnNames    = getParameterValue(element, depth, "columnNames", String.class, null, false);
        Boolean             sortable       = getParameterValue(element, depth, "sortable", Boolean.class, false);

        List<String>        columnNameList =
            Arrays
                .stream(columnNames.split(" "))
                .filter(Objects::nonNull)
                .map(cn -> handlePossiblePlaceholder(cn, String.class, cn, depth))
                .toList();

        LOG.warn("number of columns for table [{}] differ [{}/{}]", name, columnNameList.size(), numOfColumns);

        List<Container> rows = new ArrayList<>();

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
            .numOfColumns(numOfColumns)
            .columnNames(columnNameList)
            .sortable(sortable)
            .rows(rows)
            .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link TabContainer}
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
            cells.addAll(transform(pe, depth, TABLE_ROW_CHILDREN));
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
     * Transform a {@link PseudoElement} to an {@link TextContainer}
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
        String              text       = getParameterValue(element, depth, TEXT, String.class);
        Boolean             inline     = getParameterValue(element, depth, "inline", Boolean.class, false);

        return TextContainer
            .builder()
            .uid(uid)
            .classes(classes)
            .dataAttributes(attributes)
            .text(text)
            .inline(inline)
            .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link TextContainer}
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
        String              text       = getParameterValue(element, depth, TEXT, String.class);
        Integer             size       = getParameterValue(element, depth, "size", Integer.class, Integer.valueOf(1));

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
            .text(text)
            .size(size)
            .build();
    }

    private Input transformInput(PseudoElement element, int depth)
    {
        String    indent = LOG_INDENT_STRING.repeat(depth + 1);

        InputType type   = InputType.valueOf(element.getType().toUpperCase());

        LOG.trace("");

        String  uid            = element.getUid() == null ? "random uid" : element.getUid();

        boolean processElement = shouldProcess(element, uid, depth);

        if (!processElement)
        {
            LOG.debug("[{}]:[{}]:{} skip INPUT [{}] due to eval processElement is [false]", uuid, depth, indent, element.getUid());

            return Hidden.builder().uid(element.getUid()).name("").submitAs("").value("").build();
        }

        LOG.debug("[{}]:[{}]:{}map INPUT [{}] as [{}]", uuid, depth, indent, element.getUid(), type);

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
     * Transform a {@link PseudoElement} to an {@link Checkbox} input
     *
     * @param element to transfrom
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private Checkbox transformCheckboxInput(PseudoElement element, int depth)
    {
        String              uid             = resolveUid(element, depth);
        List<String>        classes         = resolveClasses(element.getClasses(), depth);
        Map<String, String> attributes      = resolveAttributes(element, depth);
        List<Marker>        marker          = transformMarker(uid, element.getMarker(), depth);
        String              tooltip         = getParameterValue(element, depth, TOOLTIP, String.class, "");
        Position            tooltipPosition =
            Position.valueOf(getParameterValue(element, depth, TOOLTIP_POSITION, String.class, "RIGHT").toUpperCase());
        String              name            = getParameterValue(element, depth, NAME, String.class);
        String              submitAs        = getParameterValue(element, depth, SUBMIT_AS, String.class, "");
        String              onInput         = getParameterValue(element, depth, ON_INPUT, String.class, DEFAULT_VAL);
        String              infoText        = getParameterValue(element, depth, INFO_TEXT, String.class, DEFAULT_VAL);
        String              infoUrl         = getParameterValue(element, depth, INFO_URL, String.class, DEFAULT_VAL);
        String              boxesSrc        = getParameterValue(element, depth, "boxes", String.class, "");
        InputValueList      boxes           =
            boxesSrc.isBlank()
                ? handleInputValueChildren(element, depth, false)
                : handlePossiblePlaceholder(boxesSrc, InputValueList.class, new InputValueList(), depth);

        return Checkbox
            .builder()
            .uid(uid)
            .classes(classes)
            .dataAttributes(attributes)
            .marker(marker)
            .tooltip(tooltip)
            .tooltipPosition(tooltipPosition)
            .name(name)
            .submitAs(submitAs)
            .onInput(onInput)
            .infoText(infoText)
            .infoUrl(infoUrl)
            .boxes(boxes)
            .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link Currency} input
     *
     * @param element to transfrom
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private Currency transformCurrencyInput(PseudoElement element, int depth)
    {
        String              uid             = resolveUid(element, depth);
        List<String>        classes         = resolveClasses(element.getClasses(), depth);
        Map<String, String> attributes      = resolveAttributes(element, depth);
        List<Marker>        marker          = transformMarker(uid, element.getMarker(), depth);
        String              tooltip         = getParameterValue(element, depth, TOOLTIP, String.class, "");
        Position            tooltipPosition =
            Position.valueOf(getParameterValue(element, depth, TOOLTIP_POSITION, String.class, "RIGHT").toUpperCase());
        String              name            = getParameterValue(element, depth, NAME, String.class);
        String              submitAs        = getParameterValue(element, depth, SUBMIT_AS, String.class);
        String              onInput         = getParameterValue(element, depth, ON_INPUT, String.class, DEFAULT_VAL);
        String              infoText        = getParameterValue(element, depth, INFO_TEXT, String.class, DEFAULT_VAL);
        String              infoUrl         = getParameterValue(element, depth, INFO_URL, String.class, DEFAULT_VAL);
        Integer             valueF          = getParameterValue(element, depth, "valueF", Integer.class, 0);
        Integer             valueB          = getParameterValue(element, depth, "valueB", Integer.class, 0);
        String              symbol          = getParameterValue(element, depth, "symbol", String.class, "");
        Integer             min             = getParameterValue(element, depth, MIN, Integer.class, Integer.MIN_VALUE);
        Integer             max             = getParameterValue(element, depth, MAX, Integer.class, Integer.MAX_VALUE);
        String              placeholder     = getParameterValue(element, depth, PLACEHOLDER, String.class, "");

        return Currency
            .builder()
            .uid(uid)
            .classes(classes)
            .dataAttributes(attributes)
            .marker(marker)
            .tooltip(tooltip)
            .tooltipPosition(tooltipPosition)
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
     * Transform a {@link PseudoElement} to an {@link Date} input
     *
     * @param element to transfrom
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private Date transformDateInput(PseudoElement element, int depth)
    {
        String              uid             = resolveUid(element, depth);
        List<String>        classes         = resolveClasses(element.getClasses(), depth);
        Map<String, String> attributes      = resolveAttributes(element, depth);
        List<Marker>        marker          = transformMarker(uid, element.getMarker(), depth);
        String              tooltip         = getParameterValue(element, depth, TOOLTIP, String.class, "");
        Position            tooltipPosition =
            Position.valueOf(getParameterValue(element, depth, TOOLTIP_POSITION, String.class, "RIGHT").toUpperCase());
        String              name            = getParameterValue(element, depth, NAME, String.class);
        String              submitAs        = getParameterValue(element, depth, SUBMIT_AS, String.class);
        String              onInput         = getParameterValue(element, depth, ON_INPUT, String.class, DEFAULT_VAL);
        String              infoText        = getParameterValue(element, depth, INFO_TEXT, String.class, DEFAULT_VAL);
        String              infoUrl         = getParameterValue(element, depth, INFO_URL, String.class, DEFAULT_VAL);
        String              value           = getParameterValue(element, depth, VALUE, String.class, DEFAULT_DATE);

        return Date
            .builder()
            .uid(uid)
            .classes(classes)
            .dataAttributes(attributes)
            .marker(marker)
            .tooltip(tooltip)
            .tooltipPosition(tooltipPosition)
            .name(name)
            .submitAs(submitAs)
            .onInput(onInput)
            .infoText(infoText)
            .infoUrl(infoUrl)
            .value(value)
            .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link File} input
     *
     * @param element to transfrom
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private File transformFileInput(PseudoElement element, int depth)
    {
        String              uid             = resolveUid(element, depth);
        List<String>        classes         = resolveClasses(element.getClasses(), depth);
        Map<String, String> attributes      = resolveAttributes(element, depth);
        List<Marker>        marker          = transformMarker(uid, element.getMarker(), depth);
        String              tooltip         = getParameterValue(element, depth, TOOLTIP, String.class, "");
        Position            tooltipPosition =
            Position.valueOf(getParameterValue(element, depth, TOOLTIP_POSITION, String.class, "RIGHT").toUpperCase());
        String              name            = getParameterValue(element, depth, NAME, String.class);
        String              submitAs        = getParameterValue(element, depth, SUBMIT_AS, String.class);
        String              onInput         = getParameterValue(element, depth, ON_INPUT, String.class, DEFAULT_VAL);
        String              infoText        = getParameterValue(element, depth, INFO_TEXT, String.class, DEFAULT_VAL);
        String              infoUrl         = getParameterValue(element, depth, INFO_URL, String.class, DEFAULT_VAL);
        Boolean             multiple        = getParameterValue(element, depth, "multiple", Boolean.class, Boolean.FALSE);
        String              accept          = getParameterValue(element, depth, "accept", String.class, "*");

        return File
            .builder()
            .uid(uid)
            .classes(classes)
            .dataAttributes(attributes)
            .marker(marker)
            .tooltip(tooltip)
            .tooltipPosition(tooltipPosition)
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
     * Transform a {@link PseudoElement} to an {@link Hidden} input
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
        List<Marker>        marker     = transformMarker(uid, element.getMarker(), depth);
        String              submitAs   = getParameterValue(element, depth, SUBMIT_AS, String.class);
        String              value      = getParameterValue(element, depth, VALUE, String.class, DEFAULT_VAL);

        return Hidden
            .builder()
            .uid(uid)
            .classes(classes)
            .dataAttributes(attributes)
            .marker(marker)
            .name(DEFAULT_VAL)
            .submitAs(submitAs)
            .value(value)
            .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link Link} input
     *
     * @param element to transfrom
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private Link transformLinkInput(PseudoElement element, int depth)
    {
        String              uid             = resolveUid(element, depth);
        List<String>        classes         = resolveClasses(element.getClasses(), depth);
        Map<String, String> attributes      = resolveAttributes(element, depth);
        List<Marker>        marker          = transformMarker(uid, element.getMarker(), depth);
        String              tooltip         = getParameterValue(element, depth, TOOLTIP, String.class, "");
        Position            tooltipPosition =
            Position.valueOf(getParameterValue(element, depth, TOOLTIP_POSITION, String.class, "RIGHT").toUpperCase());
        String              text            = getParameterValue(element, depth, TEXT, String.class);
        String              href            = getParameterValue(element, depth, HREF, String.class);
        String              target          = getParameterValue(element, depth, TARGET, String.class, "_self");

        return Link
            .builder()
            .uid(uid)
            .classes(classes)
            .dataAttributes(attributes)
            .marker(marker)
            .tooltip(tooltip)
            .tooltipPosition(tooltipPosition)
            .text(text)
            .href(href)
            .target(target)
            .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link Number} input
     *
     * @param element to transfrom
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private Number transformNumberInput(PseudoElement element, int depth)
    {
        String              uid             = resolveUid(element, depth);
        List<String>        classes         = resolveClasses(element.getClasses(), depth);
        Map<String, String> attributes      = resolveAttributes(element, depth);
        List<Marker>        marker          = transformMarker(uid, element.getMarker(), depth);
        String              tooltip         = getParameterValue(element, depth, TOOLTIP, String.class, "");
        Position            tooltipPosition =
            Position.valueOf(getParameterValue(element, depth, TOOLTIP_POSITION, String.class, "RIGHT").toUpperCase());
        String              name            = getParameterValue(element, depth, NAME, String.class);
        String              submitAs        = getParameterValue(element, depth, SUBMIT_AS, String.class);
        String              onInput         = getParameterValue(element, depth, ON_INPUT, String.class, DEFAULT_VAL);
        String              infoText        = getParameterValue(element, depth, INFO_TEXT, String.class, DEFAULT_VAL);
        String              infoUrl         = getParameterValue(element, depth, INFO_URL, String.class, DEFAULT_VAL);
        Integer             value           = getParameterValue(element, depth, VALUE, Integer.class, 0);
        Integer             min             = getParameterValue(element, depth, MIN, Integer.class, Integer.MIN_VALUE);
        Integer             max             = getParameterValue(element, depth, MAX, Integer.class, Integer.MAX_VALUE);
        String              placeholder     = getParameterValue(element, depth, PLACEHOLDER, String.class, DEFAULT_VAL);
        String              prefix          = getParameterValue(element, depth, PREFIX, String.class, DEFAULT_VAL);
        String              suffix          = getParameterValue(element, depth, SUFFIX, String.class, DEFAULT_VAL);

        return Number
            .builder()
            .uid(uid)
            .classes(classes)
            .dataAttributes(attributes)
            .marker(marker)
            .tooltip(tooltip)
            .tooltipPosition(tooltipPosition)
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
            .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link Password} input
     *
     * @param element to transfrom
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private Password transformPasswordInput(PseudoElement element, int depth)
    {
        String              uid             = resolveUid(element, depth);
        List<String>        classes         = resolveClasses(element.getClasses(), depth);
        Map<String, String> attributes      = resolveAttributes(element, depth);
        List<Marker>        marker          = transformMarker(uid, element.getMarker(), depth);
        String              tooltip         = getParameterValue(element, depth, TOOLTIP, String.class, "");
        Position            tooltipPosition =
            Position.valueOf(getParameterValue(element, depth, TOOLTIP_POSITION, String.class, "RIGHT").toUpperCase());
        String              name            = getParameterValue(element, depth, NAME, String.class);
        String              submitAs        = getParameterValue(element, depth, SUBMIT_AS, String.class);
        String              onInput         = getParameterValue(element, depth, ON_INPUT, String.class, DEFAULT_VAL);
        String              infoText        = getParameterValue(element, depth, INFO_TEXT, String.class, DEFAULT_VAL);
        String              infoUrl         = getParameterValue(element, depth, INFO_URL, String.class, DEFAULT_VAL);
        String              placeholder     = getParameterValue(element, depth, PLACEHOLDER, String.class, "***");

        return Password
            .builder()
            .uid(uid)
            .classes(classes)
            .dataAttributes(attributes)
            .marker(marker)
            .tooltip(tooltip)
            .tooltipPosition(tooltipPosition)
            .name(name)
            .submitAs(submitAs)
            .onInput(onInput)
            .infoText(infoText)
            .infoUrl(infoUrl)
            .placeholder(placeholder)
            .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link Radio} input
     *
     * @param element to transfrom
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private Radio transformRadioInput(PseudoElement element, int depth)
    {
        String              uid             = resolveUid(element, depth);
        List<String>        classes         = resolveClasses(element.getClasses(), depth);
        Map<String, String> attributes      = resolveAttributes(element, depth);
        List<Marker>        marker          = transformMarker(uid, element.getMarker(), depth);
        String              tooltip         = getParameterValue(element, depth, TOOLTIP, String.class, "");
        Position            tooltipPosition =
            Position.valueOf(getParameterValue(element, depth, TOOLTIP_POSITION, String.class, "RIGHT").toUpperCase());
        String              name            = getParameterValue(element, depth, NAME, String.class);
        String              submitAs        = getParameterValue(element, depth, SUBMIT_AS, String.class);
        String              onInput         = getParameterValue(element, depth, ON_INPUT, String.class, DEFAULT_VAL);
        String              infoText        = getParameterValue(element, depth, INFO_TEXT, String.class, DEFAULT_VAL);
        String              infoUrl         = getParameterValue(element, depth, INFO_URL, String.class, DEFAULT_VAL);
        List<InputValue>    values          = handleInputValueChildren(element, depth, true);

        return Radio
            .builder()
            .uid(uid)
            .classes(classes)
            .dataAttributes(attributes)
            .marker(marker)
            .tooltip(tooltip)
            .tooltipPosition(tooltipPosition)
            .name(name)
            .submitAs(submitAs)
            .onInput(onInput)
            .infoText(infoText)
            .infoUrl(infoUrl)
            .values(values)
            .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link Select} input
     *
     * @param element to transfrom
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private Select transformSelectInput(PseudoElement element, int depth)
    {
        String              uid             = resolveUid(element, depth);
        List<String>        classes         = resolveClasses(element.getClasses(), depth);
        Map<String, String> attributes      = resolveAttributes(element, depth);
        List<Marker>        marker          = transformMarker(uid, element.getMarker(), depth);
        String              tooltip         = getParameterValue(element, depth, TOOLTIP, String.class, "");
        Position            tooltipPosition =
            Position.valueOf(getParameterValue(element, depth, TOOLTIP_POSITION, String.class, "RIGHT").toUpperCase());
        String              name            = getParameterValue(element, depth, NAME, String.class);
        String              submitAs        = getParameterValue(element, depth, SUBMIT_AS, String.class);
        String              onInput         = getParameterValue(element, depth, ON_INPUT, String.class, DEFAULT_VAL);
        String              infoText        = getParameterValue(element, depth, INFO_TEXT, String.class, DEFAULT_VAL);
        String              infoUrl         = getParameterValue(element, depth, INFO_URL, String.class, DEFAULT_VAL);
        String              value           = getParameterValue(element, depth, VALUE, String.class, "NOT_SELECTED");
        String              valueSrc        = getParameterValue(element, depth, "values", String.class, "");
        InputValueList      values          =
            valueSrc.isBlank()
                ? handleInputValueChildren(element, depth, false)
                : handlePossiblePlaceholder("$$value$" + valueSrc, InputValueList.class, depth);

        return Select
            .builder()
            .uid(uid)
            .classes(classes)
            .dataAttributes(attributes)
            .marker(marker)
            .tooltip(tooltip)
            .tooltipPosition(tooltipPosition)
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
     * Transform a {@link PseudoElement} to an {@link Slider} input
     *
     * @param element to transfrom
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private Slider transformSliderInput(PseudoElement element, int depth)
    {
        String              uid             = resolveUid(element, depth);
        List<String>        classes         = resolveClasses(element.getClasses(), depth);
        Map<String, String> attributes      = resolveAttributes(element, depth);
        List<Marker>        marker          = transformMarker(uid, element.getMarker(), depth);
        String              tooltip         = getParameterValue(element, depth, TOOLTIP, String.class, "");
        Position            tooltipPosition =
            Position.valueOf(getParameterValue(element, depth, TOOLTIP_POSITION, String.class, "RIGHT").toUpperCase());
        String              name            = getParameterValue(element, depth, NAME, String.class);
        String              submitAs        = getParameterValue(element, depth, SUBMIT_AS, String.class);
        String              onInput         = getParameterValue(element, depth, ON_INPUT, String.class, DEFAULT_VAL);
        String              infoText        = getParameterValue(element, depth, INFO_TEXT, String.class, DEFAULT_VAL);
        String              infoUrl         = getParameterValue(element, depth, INFO_URL, String.class, DEFAULT_VAL);
        Integer             value           = getParameterValue(element, depth, VALUE, Integer.class, 0);
        Integer             min             = getParameterValue(element, depth, MIN, Integer.class, Integer.MIN_VALUE);
        Integer             max             = getParameterValue(element, depth, MAX, Integer.class, Integer.MAX_VALUE);

        return Slider
            .builder()
            .uid(uid)
            .classes(classes)
            .dataAttributes(attributes)
            .marker(marker)
            .tooltip(tooltip)
            .tooltipPosition(tooltipPosition)
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
     * Transform a {@link PseudoElement} to an {@link Switch} input
     *
     * @param element to transfrom
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private Switch transformSwitchInput(PseudoElement element, int depth)
    {
        String              uid             = resolveUid(element, depth);
        List<String>        classes         = resolveClasses(element.getClasses(), depth);
        Map<String, String> attributes      = resolveAttributes(element, depth);
        List<Marker>        marker          = transformMarker(uid, element.getMarker(), depth);
        String              tooltip         = getParameterValue(element, depth, TOOLTIP, String.class, "");
        Position            tooltipPosition =
            Position.valueOf(getParameterValue(element, depth, TOOLTIP_POSITION, String.class, "RIGHT").toUpperCase());
        String              name            = getParameterValue(element, depth, NAME, String.class);
        String              submitAs        = getParameterValue(element, depth, SUBMIT_AS, String.class);
        String              onInput         = getParameterValue(element, depth, ON_INPUT, String.class, DEFAULT_VAL);
        String              infoText        = getParameterValue(element, depth, INFO_TEXT, String.class, DEFAULT_VAL);
        String              infoUrl         = getParameterValue(element, depth, INFO_URL, String.class, DEFAULT_VAL);
        Boolean             checked         = getParameterValue(element, depth, CHECKED, Boolean.class, Boolean.FALSE);

        return Switch
            .builder()
            .uid(uid)
            .classes(classes)
            .dataAttributes(attributes)
            .marker(marker)
            .tooltip(tooltip)
            .tooltipPosition(tooltipPosition)
            .name(name)
            .submitAs(submitAs)
            .onInput(onInput)
            .infoText(infoText)
            .infoUrl(infoUrl)
            .checked(checked)
            .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link Textfield} input
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
        List<Marker>        marker           = transformMarker(uid, element.getMarker(), depth);
        String              tooltip          = getParameterValue(element, depth, TOOLTIP, String.class, DEFAULT_VAL);
        Position            tooltipPosition  =
            Position.valueOf(getParameterValue(element, depth, TOOLTIP_POSITION, String.class, "RIGHT").toUpperCase());
        String              name             = getParameterValue(element, depth, NAME, String.class);
        String              submitAs         = getParameterValue(element, depth, SUBMIT_AS, String.class);
        String              onInput          = getParameterValue(element, depth, ON_INPUT, String.class, DEFAULT_VAL);
        String              infoText         = getParameterValue(element, depth, INFO_TEXT, String.class, DEFAULT_VAL);
        String              infoUrl          = getParameterValue(element, depth, INFO_URL, String.class, DEFAULT_VAL);
        String              value            = getParameterValue(element, depth, VALUE, String.class, DEFAULT_VAL);
        String              pattern          = getParameterValue(element, depth, "pattern", String.class, DEFAULT_VAL);
        String              fetchUrl         = getParameterValue(element, depth, "fetchUrl", String.class, DEFAULT_VAL);
        String              searchUrl        = getParameterValue(element, depth, "searchUrl", String.class, DEFAULT_VAL);
        Boolean             enforceWhitelist = getParameterValue(element, depth, "enforceWhitlist", Boolean.class, Boolean.FALSE);

        functions.add(new RegisterTagInput(uid, fetchUrl, searchUrl, enforceWhitelist));

        return Tag
            .builder()
            .uid(uid)
            .classes(classes)
            .dataAttributes(attributes)
            .marker(marker)
            .tooltip(tooltip)
            .tooltipPosition(tooltipPosition)
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
     * Transform a {@link PseudoElement} to an {@link Textarea} input
     *
     * @param element to transfrom
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private Textarea transformTextareaInput(PseudoElement element, int depth)
    {
        String              uid             = resolveUid(element, depth);
        List<String>        classes         = resolveClasses(element.getClasses(), depth);
        Map<String, String> attributes      = resolveAttributes(element, depth);
        List<Marker>        marker          = transformMarker(uid, element.getMarker(), depth);
        String              tooltip         = getParameterValue(element, depth, TOOLTIP, String.class, "");
        Position            tooltipPosition =
            Position.valueOf(getParameterValue(element, depth, TOOLTIP_POSITION, String.class, "RIGHT").toUpperCase());
        String              name            = getParameterValue(element, depth, NAME, String.class);
        String              submitAs        = getParameterValue(element, depth, SUBMIT_AS, String.class);
        String              onInput         = getParameterValue(element, depth, ON_INPUT, String.class, DEFAULT_VAL);
        String              infoText        = getParameterValue(element, depth, INFO_TEXT, String.class, DEFAULT_VAL);
        String              infoUrl         = getParameterValue(element, depth, INFO_URL, String.class, DEFAULT_VAL);
        String              value           = getParameterValue(element, depth, VALUE, String.class, "");
        Integer             maxChars        = getParameterValue(element, depth, MAX_CHARS, Integer.class, 32000);
        String              placeholder     = getParameterValue(element, depth, PLACEHOLDER, String.class, "");

        return Textarea
            .builder()
            .uid(uid)
            .classes(classes)
            .dataAttributes(attributes)
            .marker(marker)
            .tooltip(tooltip)
            .tooltipPosition(tooltipPosition)
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
     * Transform a {@link PseudoElement} to an {@link Textbox} input
     *
     * @param element to transfrom
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private Textbox transformTextboxInput(PseudoElement element, int depth)
    {
        String value = getParameterValue(element, depth, VALUE, String.class, DEFAULT_VAL);

        return Textbox
            .builder()
            .onInput(DEFAULT_VAL)
            .value(value)
            .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link Textfield} input
     *
     * @param element to transfrom
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private Textfield transformTextfieldInput(PseudoElement element, int depth)
    {
        String              uid             = resolveUid(element, depth);
        List<String>        classes         = resolveClasses(element.getClasses(), depth);
        Map<String, String> attributes      = resolveAttributes(element, depth);
        List<Marker>        marker          = transformMarker(uid, element.getMarker(), depth);
        String              tooltip         = getParameterValue(element, depth, TOOLTIP, String.class, "");
        Position            tooltipPosition =
            Position.valueOf(getParameterValue(element, depth, TOOLTIP_POSITION, String.class, "RIGHT").toUpperCase());
        String              name            = getParameterValue(element, depth, NAME, String.class);
        String              submitAs        = getParameterValue(element, depth, SUBMIT_AS, String.class);
        String              onInput         = getParameterValue(element, depth, ON_INPUT, String.class, DEFAULT_VAL);
        String              infoText        = getParameterValue(element, depth, INFO_TEXT, String.class, DEFAULT_VAL);
        String              infoUrl         = getParameterValue(element, depth, INFO_URL, String.class, DEFAULT_VAL);
        String              value           = getParameterValue(element, depth, VALUE, String.class, DEFAULT_VAL);
        String              placeholder     = getParameterValue(element, depth, PLACEHOLDER, String.class, DEFAULT_VAL);
        String              prefix          = getParameterValue(element, depth, PREFIX, String.class, DEFAULT_VAL);
        String              suffix          = getParameterValue(element, depth, SUFFIX, String.class, DEFAULT_VAL);
        Integer             maxChars        = getParameterValue(element, depth, MAX_CHARS, Integer.class, 150);

        return Textfield
            .builder()
            .uid(uid)
            .classes(classes)
            .dataAttributes(attributes)
            .marker(marker)
            .tooltip(tooltip)
            .tooltipPosition(tooltipPosition)
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
            .build();
    }
}
