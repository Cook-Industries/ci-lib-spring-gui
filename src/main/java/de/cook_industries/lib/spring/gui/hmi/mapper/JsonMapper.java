/**
 * Copyright(c) 2024 sebastian koch/CI. All rights reserved.<br>
 * mailto: koch.sebastian@cook-industries.de
 *
 * @author : sebastian koch <koch.sebastian@cook-industries.de>
 */
package de.cook_industries.lib.spring.gui.hmi.mapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import de.cook_industries.lib.spring.gui.hmi.container.*;
import de.cook_industries.lib.spring.gui.hmi.input.*;
import de.cook_industries.lib.spring.gui.hmi.input.Number;
import de.cook_industries.lib.spring.gui.hmi.input.util.InputValue;
import de.cook_industries.lib.spring.gui.hmi.input.util.InputValueList;
import de.cook_industries.lib.spring.gui.hmi.input.util.Marker;
import de.cook_industries.lib.spring.gui.hmi.mapper.exception.JsonMapperException;
import de.cook_industries.lib.spring.gui.hmi.mapper.exception.JsonParsingException;
import de.cook_industries.lib.spring.gui.i18n.TranslationMap;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Data
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class JsonMapper
{

    private static final String          SRC                                 = "src";
    private static final String          SUFFIX                              = "suffix";
    private static final String          PREFIX                              = "prefix";
    private static final String          MAX_CHARS                           = "maxChars";
    private static final String          TARGET                              = "target";
    private static final String          HREF                                = "href";
    private static final String          MAX                                 = "max";
    private static final String          MIN                                 = "min";
    private static final String          CHECKED                             = "checked";
    private static final String          IMAGE                               = "image";
    private static final String          ON_CLICK                            = "onClick";
    private static final String          BTN_CLASS                           = "btnClass";
    private static final String          TEXT                                = "text";
    private static final String          PARAMETER_S_IS_EXPECTED_BUT_NOT_SET = "parameter [%s] is expected but not set";
    private static final String          DEFAULT_DATE                        = "0000-00-00";
    private static final String          PLACEHOLDER                         = "placeholder";
    private static final String          DEFAULT_VAL                         = "";
    private static final String          NAME                                = "name";
    private static final String          VALUE                               = "value";
    private static final String          SUBMIT_AS                           = "submitAs";
    private static final String          ON_INPUT                            = "onInput";
    private static final String          INDICATOR_VALUE_PLACEHOLDER         = "$$value$";
    private static final String          INDICATOR_TEXT_PLACEHOLDER          = "$$text$";
    private static final String          INDICATOR_CLASS_PLACEHOLDER         = "$$class$";

    //@formatter:off
    private static final ContainerType[]                     CONTAINER_CHILDREN                  =
            {
                ContainerType.AUDIO,
                ContainerType.BUTTON, 
                ContainerType.BUTTON_BAR, 
                ContainerType.BUTTON_ICON,
                ContainerType.CONTENT,
                ContainerType.FORM,
                ContainerType.HIDDEN,
                ContainerType.IMAGE,
                ContainerType.LINK,
                ContainerType.ROWED_CONTENT,
                ContainerType.SPLITTED,
                ContainerType.TAB,
                ContainerType.TEXT
            };
    //@formatter:on

    private static final ContainerType[] LINK_CHILDREN                       = {ContainerType.TEXT};

    private final TreeHandling           handling;
    private final Locale                 locale;
    private final TranslationMap         translationMap;
    private final ValueMap[]             valueMaps;

    @Getter(value = AccessLevel.NONE)
    @Setter(value = AccessLevel.NONE)
    private Integer                      count                               = 0;

    /**
     * Validate a {@link JsonTreeRoot} and map it to a {@link Container} in a {@link TreeHandling#STATIC} context.
     * <p>
     * Uses {@link Locale#ENGLISH} as a language, an empty {@link TranslationMap} and no {@link ValueMap}s.
     *
     * @param root to map
     * @return the mapped {@code Container}
     */
    public static Container map(JsonTreeRoot root)
    {
        root.validate();

        final JsonMapper mapper = new JsonMapper(TreeHandling.valueOf(root.getHandling().toUpperCase()), Locale.ENGLISH,
                new TranslationMap(), new ValueMap[0]);

        return mapper.transform(root);
    }

    /**
     * Map a {@link JsonTreeRoot} to a {@link Container} in a {@link TreeHandling#DYNAMIC} context.
     * <p>
     * Note, that any {@link ValueMap} provided for this will call {@link ValueMap#seal()} before it is used!
     *
     * @param root to map
     * @param locale the language to use from {@code translationMap}
     * @param translationMap the map to fetch tranlation values
     * @param valueMaps {@code ValueMap}s to link to dynamic fill-in content. These will be sealed before usage!
     * @return the mapped {@code Container}
     * @throws JsonMapperException if mapping is set to dynamic but no {@code valueMap} is provided
     */
    public static Container map(JsonTreeRoot root, Locale locale, TranslationMap translationMap, final ValueMap... valueMaps)
    {
        final TreeHandling handling = TreeHandling.valueOf(root.getHandling().toUpperCase());

        if (handling == TreeHandling.DYNAMIC && valueMaps.length == 0)
        {
            throw new JsonMapperException("Mapping is set to [dynamic] fill-in no [valueMap] are given");
        }

        Arrays.sort(valueMaps, Comparator.comparing(ValueMap::getPresedence).reversed());
        Arrays.stream(valueMaps).forEach(ValueMap::seal);

        final JsonMapper mapper = new JsonMapper(handling, locale, translationMap, valueMaps);

        return mapper.transform(root);
    }

    /*
     * --- > utility functions -------------------------------------------------------------------------------------------------------------
     */

    private Container throwNotSupported(PseudoElement element, Integer depth)
    {
        throw new JsonParsingException(element.getUid(), depth, this.count,
                String.format("container type [%s] is not supported", element.getType()));
    }

    /**
     * Extracts a value from the value map based on the provided pseudo element, key, and expected type.
     * <p>
     * If dynamic handling mode is enabled, it checks for indicator placeholders in the key to determine which mapping to use. If no value
     * is found and a default value is provided, it returns the default value instead of throwing an exception.
     *
     * @param <I> the expected result type
     * @param key the key used to look up the value in the value map.
     * @param depth the current depth level of parsing.
     * @param expectedType the expected type of the extracted value.
     * @param defaultValue the default value to return if no value is found.
     * @return the extracted value if found, or {@code defaultValue} if not found, or null if not found and no {@code defaultValue} is set
     * @throws JsonMapperException if there is an error while extracting the value.
     */
    private <I> I extractFromValueMaps(String key, Class<I> expectedType, I defaultValue) throws JsonMapperException
    {
        I value = null;

        if (this.handling == TreeHandling.DYNAMIC && key != null)
        {
            if (key.startsWith(INDICATOR_VALUE_PLACEHOLDER))
            {
                String keyName = key.substring(INDICATOR_VALUE_PLACEHOLDER.length());
                value = extractFromValueMaps(keyName, expectedType);
            }
            else if (key.startsWith(INDICATOR_TEXT_PLACEHOLDER) && expectedType.equals(String.class))
            {
                String keyName = key.substring(INDICATOR_TEXT_PLACEHOLDER.length());
                value = expectedType.cast(extractFromTranslationMap(keyName));
            }
        }

        if (value == null && defaultValue != null)
        {
            value = defaultValue;
        }

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
    private <I> I extractFromValueMaps(String objName, Class<I> expectedType) throws JsonMapperException
    {
        Optional<Object> opt;

        try
        {
            opt = Arrays.stream(valueMaps).map(m -> m.get(objName)).findFirst();
        }
        catch (NullPointerException ex)
        {
            return null;
        }

        if (opt.isEmpty())
        {
            return null;
        }

        Object o = opt.get();

        if (expectedType.isInstance(o))
        {
            return expectedType.cast(o);
        }
        else
        {
            throw new JsonMapperException(String
                    .format("object [%s] could not be extracted due to expected class is [%s] but got [%s]", objName, expectedType,
                            o.getClass().getSimpleName()));
        }
    }

    private String extractFromTranslationMap(String key)
    {
        return translationMap.getText(locale, key);
    }

    private <T> T getParameterValue(PseudoElement element, Integer depth, String key, Class<T> expectedType)
    {
        return getParameterValue(element, depth, key, expectedType, null);
    }

    /**
     * Extract a paramater from the {@code element}.
     * <p>
     * Replaces the content of the result with a value fetched from {@link #valueMaps} or {@link #translationMap} if the {@code key} starts
     * with '$$'
     * 
     * @param <T> the expected result type
     * @param element the element to extract from
     * @param depth the current depth in the tree
     * @param key the key to lookup
     * @param expectedType the expected result type
     * @param fallback to use if no result is found, can be {@code null}
     * @return the found parameter
     * @throws JsonParsingException if parameter is expected but not found and no {@code fallback} is provided
     */
    private <T> T getParameterValue(PseudoElement element, Integer depth, String key, Class<T> expectedType, T fallback)
    {
        String rawValue = element.getParameters().get(key);
        T      value    = null;

        if (rawValue == null || rawValue.isBlank())
        {
            if (fallback == null)
            {
                throw new JsonParsingException(element.getUid(), 0, this.count, String.format(PARAMETER_S_IS_EXPECTED_BUT_NOT_SET, key));
            }

            return fallback;
        }

        if (rawValue.startsWith("$$"))
        {
            value = extractFromValueMaps(rawValue, expectedType, fallback);
        }
        else
        {
            value = transformRawValue(element.getUid(), depth, rawValue, expectedType);
        }

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
    private <T> T transformRawValue(String uid, Integer depth, String value, Class<T> expectedType)
    {
        if (expectedType.equals(Boolean.class))
        {
            Boolean b = Boolean.valueOf(value);

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
    private Container transformInternalComponent(PseudoElement element, Integer depth)
    {
        String         path   = getParameterValue(element, depth, "path", String.class);
        JsonTreeMapper mapper = new JsonTreeMapper();

        JsonTreeRoot   root   = mapper.map(path);

        return JsonMapper.map(root, locale, translationMap, valueMaps);
    }

    /**
     * Traverse the css classes and replace {@code $$class$} parameters from {@link #valueMaps}.
     * <p>
     * Not founde {@code class}es will be ignored and will not be raised.
     * 
     * @param classes {@code List} of all classes {@code String}s to traverse
     * @return a {@code List} of {@code String}s containing the non-altered and the replaced classes
     */
    private List<String> replaceClasses(List<String> classes)
    {
        return classes.stream().map(c -> {
            if (c.startsWith(INDICATOR_CLASS_PLACEHOLDER))
            {
                String key  = c.replace(INDICATOR_CLASS_PLACEHOLDER, "");
                String text = extractFromValueMaps(key, String.class);

                if (text == null)
                {
                    return "";
                }

                return text;
            }

            return c;
        }).dropWhile(String::isBlank).collect(Collectors.toList());
    }

    private InputValueList extractInputValues(PseudoElement element, Integer depth, Boolean throwWhenEmpty)
    {
        InputValueList values = new InputValueList();

        for (PseudoElement pe : element.getChildren())
        {
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
    private InputValue transformInputValue(PseudoElement element, Integer depth)
    {
        List<String> classes = replaceClasses(element.getClasses());
        String       text    = getParameterValue(element, depth, TEXT, String.class);
        String       value   = getParameterValue(element, depth, VALUE, String.class);
        Boolean      checked = getParameterValue(element, depth, CHECKED, Boolean.class, Boolean.FALSE);

        return InputValue.builder().text(text).value(value).checked(checked).classes(classes).build();
    }

    /**
     * Internal JSON transform
     *
     * @param root
     * @return the transformed {@code Container}
     */
    private Container transform(JsonTreeRoot root) throws JsonMapperException
    {
        return transform(root.getRoot(), 0, ContainerType.values());
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
    private Container transform(PseudoElement element, Integer depth, ContainerType... allowedTypes) throws JsonParsingException
    {
        this.count++;

        MapperInternalElementType internalType = null;

        try
        {
            try
            {
                internalType = MapperInternalElementType.valueOf(element.getType().toUpperCase());
            }
            catch (Exception ex)
            {
                // do nothing
            }

            try
            {
                if (internalType != null)
                {
                    switch (internalType)
                    {
                        case COMPONENT:
                            return transformInternalComponent(element, depth);
                    }
                }
            }
            catch (Exception ex)
            {
                throw new JsonParsingException(element.getUid(), depth, this.count, "error parsing imported element", ex);
            }

            final ContainerType type = ContainerType.valueOf(element.getType().toUpperCase());

            if (!Arrays.asList(allowedTypes).contains(type))
            {
                throw new JsonParsingException(element.getUid(), depth, this.count,
                        String.format("container type [%s] is not allowed here", element.getType()));
            }

            return switch (type)
            {
                case AUDIO -> transfromAudioContainer(element, depth);
                case BUTTON -> transfromButtonContainer(element, depth);
                case BUTTON_BAR -> transformButtonBarContainer(element, depth);
                case BUTTON_ICON -> transformButtonIconContainer(element, depth);
                case COLUMN -> transformColumnContainer(element, depth);
                case CONTENT -> transformContentContainer(element, depth);
                case FORM -> transformFormContainer(element, depth);
                case HIDDEN -> transformHiddenContainer(element, depth);
                case IMAGE -> transformImageContainer(element, depth);
                case LINK -> transformLinkContainer(element, depth);
                case ROW -> transformRowContainer(element, depth);
                case ROWED_CONTENT -> transformRowedContentContainer(element, depth);
                case SPLITTED -> transformSplittedContainer(element, depth);
                case TAB -> transformTabbedContainer(element, depth);
                case TEXT -> transformTextContainer(element, depth);
            };
        }
        catch (final Exception ex)
        {
            throw new JsonParsingException(element.getUid(), depth, this.count, "error parsing element", ex);
        }
    }

    /**
     * Transform a {@link PseudoElement} to an {@link AudioContainer}
     *
     * @param element to transfrom
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private AudioContainer transfromAudioContainer(PseudoElement element, Integer depth)
    {
        String              uid        = extractFromValueMaps(element.getUid(), String.class, element.getUid());
        List<String>        classes    = replaceClasses(element.getClasses());
        Map<String, String> attributes = element.getAttributes();
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
     * Transform a {@link PseudoElement} to an {@link ButtonContainer}
     *
     * @param element to transfrom
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private ButtonContainer transfromButtonContainer(PseudoElement element, Integer depth)
    {
        Button button = transformButtonInput(element, depth);

        return ButtonContainer.builder().uid(DEFAULT_VAL).classes(List.of()).dataAttributes(Map.of()).button(button).build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link ButtonBarContainer}
     *
     * @param element to transfrom
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private ButtonBarContainer transformButtonBarContainer(PseudoElement element, Integer depth)
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
    private ButtonIconContainer transformButtonIconContainer(PseudoElement element, Integer depth)
    {
        return (ButtonIconContainer) throwNotSupported(element, depth);
    }

    /**
     * Transform a {@link PseudoElement} to an {@link ColumnContainer}
     *
     * @param element to transfrom
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private ColumnContainer transformColumnContainer(PseudoElement element, Integer depth)
    {
        String              uid        = extractFromValueMaps(element.getUid(), String.class, element.getUid());
        List<String>        classes    = replaceClasses(element.getClasses());
        Map<String, String> attributes = element.getAttributes();
        Container           content    =
                element.getChildren().isEmpty() ? null : transform(element.getChildren().get(0), depth + 1, CONTAINER_CHILDREN);

        return ColumnContainer.builder().uid(uid).classes(classes).dataAttributes(attributes).content(content).build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link ContentContainer}
     *
     * @param element to transfrom
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private ContentContainer transformContentContainer(PseudoElement element, Integer depth)
    {
        String              uid        = extractFromValueMaps(element.getUid(), String.class, element.getUid());
        List<String>        classes    = replaceClasses(element.getClasses());
        Map<String, String> attributes = element.getAttributes();
        List<Container>     contents   = new ArrayList<>();

        element.getChildren().stream().forEach(c -> contents.add(transform(c, depth + 1, CONTAINER_CHILDREN)));

        return ContentContainer.builder().uid(uid).classes(classes).dataAttributes(attributes).contents(contents).build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link FormContainer}
     *
     * @param element to transfrom
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private FormContainer transformFormContainer(PseudoElement element, Integer depth)
    {
        String              uid        = extractFromValueMaps(element.getUid(), String.class, element.getUid());
        List<String>        classes    = replaceClasses(element.getClasses());
        Map<String, String> attributes = element.getAttributes();

        List<Input>         inputs     = new ArrayList<>();

        element.getChildren().stream().forEach(c -> inputs.add(transformInput(c, depth + 1)));

        return FormContainer.builder().uid(uid).classes(classes).dataAttributes(attributes).inputs(inputs).build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link HiddenContainer}
     *
     * @param element to transfrom
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private HiddenContainer transformHiddenContainer(PseudoElement element, Integer depth)
    {
        String              uid        = extractFromValueMaps(element.getUid(), String.class, element.getUid());
        List<String>        classes    = replaceClasses(element.getClasses());
        Map<String, String> attributes = element.getAttributes();
        Container           child      =
                element.getChildren().isEmpty() ? null : transform(element.getChildren().get(0), depth + 1, CONTAINER_CHILDREN);

        return HiddenContainer.builder().uid(uid).classes(classes).dataAttributes(attributes).child(child).build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link ImageContainer}
     *
     * @param element to transfrom
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private ImageContainer transformImageContainer(PseudoElement element, Integer depth)
    {
        String              uid        = extractFromValueMaps(element.getUid(), String.class, element.getUid());
        List<String>        classes    = replaceClasses(element.getClasses());
        Map<String, String> attributes = element.getAttributes();
        String              src        = getParameterValue(element, depth, SRC, String.class);

        return ImageContainer.builder().uid(uid).classes(classes).dataAttributes(attributes).src(src).build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link LinkContainer}
     *
     * @param element to transfrom
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private LinkContainer transformLinkContainer(PseudoElement element, Integer depth)
    {
        String              uid        = extractFromValueMaps(element.getUid(), String.class, element.getUid());
        List<String>        classes    = replaceClasses(element.getClasses());
        Map<String, String> attributes = element.getAttributes();
        String              href       = getParameterValue(element, depth, HREF, String.class);
        String              target     = getParameterValue(element, depth, TARGET, String.class, "_self");
        Container           content    =
                element.getChildren().isEmpty() ? null : transform(element.getChildren().get(0), depth + 1, LINK_CHILDREN);

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
     * Transform a {@link PseudoElement} to an {@link RowContainer}
     *
     * @param element to transfrom
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private RowContainer transformRowContainer(PseudoElement element, Integer depth)
    {
        String                uid        = extractFromValueMaps(element.getUid(), String.class, element.getUid());
        List<String>          classes    = replaceClasses(element.getClasses());
        Map<String, String>   attributes = element.getAttributes();
        List<ColumnContainer> columns    = new ArrayList<>();

        for (PseudoElement pe : element.getChildren())
        {
            columns.add((ColumnContainer) transform(pe, depth, ContainerType.COLUMN));
        }

        return RowContainer.builder().uid(uid).classes(classes).dataAttributes(attributes).columns(columns).build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link RowedContentContainer}
     *
     * @param element to transfrom
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private RowedContentContainer transformRowedContentContainer(PseudoElement element, Integer depth)
    {
        String              uid        = extractFromValueMaps(element.getUid(), String.class, element.getUid());
        List<String>        classes    = replaceClasses(element.getClasses());
        Map<String, String> attributes = element.getAttributes();
        List<RowContainer>  rows       = new ArrayList<>();

        for (PseudoElement pe : element.getChildren())
        {
            rows.add((RowContainer) transform(pe, depth, ContainerType.ROW));
        }

        return RowedContentContainer.builder().uid(uid).classes(classes).dataAttributes(attributes).rows(rows).build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link SplittedContainer}
     *
     * @param element to transfrom
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private SplittedContainer transformSplittedContainer(PseudoElement element, Integer depth)
    {
        String              uid        = extractFromValueMaps(element.getUid(), String.class, element.getUid());
        List<String>        classes    = replaceClasses(element.getClasses());
        Map<String, String> attributes = element.getAttributes();
        Container           head       = null;
        Container           tail       = null;
        List<Container>     center     = new ArrayList<>();

        Integer             cnt        = 0;

        for (PseudoElement pe : element.getChildren())
        {
            if (cnt > 0 && cnt < element.getChildren().size() - 1)
            {
                center.add(transform(pe, depth, ContainerType.values()));
            }
            else if (cnt == 0)
            {
                head = transform(pe, depth, ContainerType.values());
            }
            else
            {
                tail = transform(pe, depth, ContainerType.values());
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
     * Transform a {@link PseudoElement} to an {@link TabbedContainer}
     *
     * @param element to transfrom
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private TabbedContainer transformTabbedContainer(PseudoElement element, Integer depth)
    {
        return (TabbedContainer) throwNotSupported(element, depth);
    }

    /**
     * Transform a {@link PseudoElement} to an {@link TextContainer}
     *
     * @param element to transfrom
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private TextContainer transformTextContainer(PseudoElement element, Integer depth)
    {
        String              uid        = extractFromValueMaps(element.getUid(), String.class, element.getUid());
        List<String>        classes    = replaceClasses(element.getClasses());
        Map<String, String> attributes = element.getAttributes();
        String              text       = getParameterValue(element, depth, TEXT, String.class);

        return TextContainer.builder().uid(uid).classes(classes).dataAttributes(attributes).text(text).build();
    }

    private Input transformInput(PseudoElement element, Integer depth)
    {
        try
        {
            return switch (InputType.valueOf(element.getType().toUpperCase()))
            {
                case BUTTON -> transformButtonInput(element, depth);
                case BUTTON_ICON -> transformButtonIconInput(element, depth);
                case CHECKBOX -> transformCheckboxInput(element, depth);
                case CURRENCY -> transformCurrencyInput(element, depth);
                case DATE -> transformDateInput(element, depth);
                case FILE -> transformFileInput(element, depth);
                case HIDDEN -> transformHiddenInput(element, depth);
                case LINK -> transformLinkInput(element, depth);
                case LIST -> transformListSelectionInput(element, depth);
                case NUMBER -> transformNumberInput(element, depth);
                case PASSWORD -> transformPasswordInput(element, depth);
                case RADIO -> transformRadioInput(element, depth);
                case SELECT -> transformSelectInput(element, depth);
                case SLIDER -> transformSliderInput(element, depth);
                case SWITCH -> transformSwitchInput(element, depth);
                case TABLE -> null; // TODO: fill
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
     * Transform a {@link PseudoElement} to an {@link Button} input
     *
     * @param element to transfrom
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private Button transformButtonInput(PseudoElement element, Integer depth)
    {
        String              uid        = extractFromValueMaps(element.getUid(), String.class, element.getUid());
        List<String>        classes    = replaceClasses(element.getClasses());
        Map<String, String> attributes = element.getAttributes();
        String              text       = getParameterValue(element, depth, TEXT, String.class);
        ButtonClass         btnClass   =
                ButtonClass.valueOf(getParameterValue(element, depth, BTN_CLASS, String.class, "DEFAULT").toUpperCase());
        String              onClick    = getParameterValue(element, depth, ON_CLICK, String.class);

        return Button.builder().uid(uid).classes(classes).dataAttributes(attributes).text(text).btnClass(btnClass).onClick(onClick).build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link ButtonIcon} input
     *
     * @param element to transfrom
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private ButtonIcon transformButtonIconInput(PseudoElement element, Integer depth)
    {
        String              uid        = extractFromValueMaps(element.getUid(), String.class, element.getUid());
        List<String>        classes    = replaceClasses(element.getClasses());
        Map<String, String> attributes = element.getAttributes();
        String              image      = getParameterValue(element, depth, IMAGE, String.class);
        ButtonClass         btnClass   = ButtonClass.valueOf(getParameterValue(element, depth, BTN_CLASS, String.class, "DEFAULT"));
        String              onClick    = getParameterValue(element, depth, ON_CLICK, String.class);

        return ButtonIcon
                .builder()
                .uid(uid)
                .classes(classes)
                .dataAttributes(attributes)
                .image(image)
                .btnClass(btnClass)
                .onClick(onClick)
                .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link Checkbox} input
     *
     * @param element to transfrom
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private Checkbox transformCheckboxInput(PseudoElement element, Integer depth)
    {
        String              uid        = extractFromValueMaps(element.getUid(), String.class, element.getUid());
        List<String>        classes    = replaceClasses(element.getClasses());
        Map<String, String> attributes = element.getAttributes();
        List<Marker>        marker     = element.getMarker();
        String              name       = getParameterValue(element, depth, NAME, String.class);
        String              submitAs   = getParameterValue(element, depth, SUBMIT_AS, String.class);
        String              onInput    = getParameterValue(element, depth, ON_INPUT, String.class, "");
        Boolean             checked    = getParameterValue(element, depth, CHECKED, Boolean.class, Boolean.FALSE);

        return Checkbox
                .builder()
                .uid(uid)
                .classes(classes)
                .dataAttributes(attributes)
                .marker(marker)
                .name(name)
                .submitAs(submitAs)
                .onInput(onInput)
                .checked(checked)
                .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link Currency} input
     *
     * @param element to transfrom
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private Currency transformCurrencyInput(PseudoElement element, Integer depth)
    {
        String              uid         = extractFromValueMaps(element.getUid(), String.class, element.getUid());
        List<String>        classes     = replaceClasses(element.getClasses());
        Map<String, String> attributes  = element.getAttributes();
        List<Marker>        marker      = element.getMarker();
        String              name        = getParameterValue(element, depth, NAME, String.class);
        String              submitAs    = getParameterValue(element, depth, SUBMIT_AS, String.class);
        String              onInput     = getParameterValue(element, depth, ON_INPUT, String.class, "");
        Integer             valueF      = getParameterValue(element, depth, "valueF", Integer.class, 0);
        Integer             valueB      = getParameterValue(element, depth, "valueB", Integer.class, 0);
        String              symbol      = getParameterValue(element, depth, "symbol", String.class, "");
        Integer             min         = getParameterValue(element, depth, MIN, Integer.class, Integer.MIN_VALUE);
        Integer             max         = getParameterValue(element, depth, MAX, Integer.class, Integer.MAX_VALUE);
        String              placeholder = getParameterValue(element, depth, PLACEHOLDER, String.class, "");

        return Currency
                .builder()
                .uid(uid)
                .classes(classes)
                .dataAttributes(attributes)
                .marker(marker)
                .name(name)
                .submitAs(submitAs)
                .onInput(onInput)
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
    private Date transformDateInput(PseudoElement element, Integer depth)
    {
        String              uid        = extractFromValueMaps(element.getUid(), String.class, element.getUid());
        List<String>        classes    = replaceClasses(element.getClasses());
        Map<String, String> attributes = element.getAttributes();
        List<Marker>        marker     = element.getMarker();
        String              name       = getParameterValue(element, depth, NAME, String.class);
        String              submitAs   = getParameterValue(element, depth, SUBMIT_AS, String.class);
        String              onInput    = getParameterValue(element, depth, ON_INPUT, String.class, DEFAULT_VAL);
        String              value      = getParameterValue(element, depth, VALUE, String.class, DEFAULT_DATE);

        return Date
                .builder()
                .uid(uid)
                .classes(classes)
                .dataAttributes(attributes)
                .marker(marker)
                .name(name)
                .submitAs(submitAs)
                .onInput(onInput)
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
    private File transformFileInput(PseudoElement element, Integer depth)
    {
        String              uid        = extractFromValueMaps(element.getUid(), String.class, element.getUid());
        List<String>        classes    = replaceClasses(element.getClasses());
        Map<String, String> attributes = element.getAttributes();
        List<Marker>        marker     = element.getMarker();
        String              name       = getParameterValue(element, depth, NAME, String.class);
        String              submitAs   = getParameterValue(element, depth, SUBMIT_AS, String.class);
        String              onInput    = getParameterValue(element, depth, ON_INPUT, String.class, DEFAULT_VAL);
        Boolean             multiple   = getParameterValue(element, depth, "multiple", Boolean.class, Boolean.FALSE);
        String              accept     = getParameterValue(element, depth, "accept", String.class, "*");

        return File
                .builder()
                .uid(uid)
                .classes(classes)
                .dataAttributes(attributes)
                .marker(marker)
                .name(name)
                .submitAs(submitAs)
                .onInput(onInput)
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
    private Hidden transformHiddenInput(PseudoElement element, Integer depth)
    {
        String              uid        = extractFromValueMaps(element.getUid(), String.class, element.getUid());
        List<String>        classes    = replaceClasses(element.getClasses());
        Map<String, String> attributes = element.getAttributes();
        List<Marker>        marker     = element.getMarker();
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
                .onInput(DEFAULT_VAL)
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
    private Link transformLinkInput(PseudoElement element, Integer depth)
    {
        String              uid        = extractFromValueMaps(element.getUid(), String.class, element.getUid());
        List<String>        classes    = replaceClasses(element.getClasses());
        Map<String, String> attributes = element.getAttributes();
        List<Marker>        marker     = element.getMarker();
        String              text       = getParameterValue(element, depth, TEXT, String.class);
        String              href       = getParameterValue(element, depth, HREF, String.class);
        String              target     = getParameterValue(element, depth, TARGET, String.class, "_self");
        String              onInput    = getParameterValue(element, depth, ON_INPUT, String.class, DEFAULT_VAL);

        return Link
                .builder()
                .uid(uid)
                .classes(classes)
                .dataAttributes(attributes)
                .marker(marker)
                .text(text)
                .href(href)
                .target(target)
                .onInput(onInput)
                .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link Link} input
     *
     * @param element to transfrom
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private ListSelection transformListSelectionInput(PseudoElement element, Integer depth)
    {
        String              uid            = extractFromValueMaps(element.getUid(), String.class, element.getUid());
        List<String>        classes        = replaceClasses(element.getClasses());
        Map<String, String> attributes     = element.getAttributes();
        List<Marker>        marker         = element.getMarker();
        String              name           = getParameterValue(element, depth, NAME, String.class);
        String              submitAs       = getParameterValue(element, depth, SUBMIT_AS, String.class);
        String              onInput        = getParameterValue(element, depth, ON_INPUT, String.class, DEFAULT_VAL);
        InputValueList      selectedValues =
                getParameterValue(element, depth, "selectedValues", InputValueList.class, new InputValueList());
        Boolean             multiple       = getParameterValue(element, depth, "multiple", Boolean.class, Boolean.FALSE);
        InputValueList      inputValues    = extractInputValues(element, depth, multiple);

        for (PseudoElement pe : element.getChildren())
        {
            inputValues.add(transformInputValue(pe, depth));
        }

        return ListSelection
                .builder()
                .uid(uid)
                .classes(classes)
                .dataAttributes(attributes)
                .marker(marker)
                .name(name)
                .submitAs(submitAs)
                .onInput(onInput)
                .values(inputValues)
                .multiple(multiple)
                .selected(selectedValues)
                .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link Number} input
     *
     * @param element to transfrom
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private Number transformNumberInput(PseudoElement element, Integer depth)
    {
        String              uid         = extractFromValueMaps(element.getUid(), String.class, element.getUid());
        List<String>        classes     = replaceClasses(element.getClasses());
        Map<String, String> attributes  = element.getAttributes();
        List<Marker>        marker      = element.getMarker();
        String              name        = getParameterValue(element, depth, NAME, String.class);
        String              submitAs    = getParameterValue(element, depth, SUBMIT_AS, String.class);
        String              onInput     = getParameterValue(element, depth, ON_INPUT, String.class, DEFAULT_VAL);
        Integer             value       = getParameterValue(element, depth, VALUE, Integer.class, 0);
        Integer             min         = getParameterValue(element, depth, MIN, Integer.class, Integer.MIN_VALUE);
        Integer             max         = getParameterValue(element, depth, MAX, Integer.class, Integer.MAX_VALUE);
        String              placeholder = getParameterValue(element, depth, PLACEHOLDER, String.class, DEFAULT_VAL);
        String              prefix      = getParameterValue(element, depth, PREFIX, String.class, DEFAULT_VAL);
        String              suffix      = getParameterValue(element, depth, SUFFIX, String.class, DEFAULT_VAL);

        return Number
                .builder()
                .uid(uid)
                .classes(classes)
                .dataAttributes(attributes)
                .marker(marker)
                .name(name)
                .submitAs(submitAs)
                .onInput(onInput)
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
    private Password transformPasswordInput(PseudoElement element, Integer depth)
    {
        String              uid         = extractFromValueMaps(element.getUid(), String.class, element.getUid());
        List<String>        classes     = replaceClasses(element.getClasses());
        Map<String, String> attributes  = element.getAttributes();
        List<Marker>        marker      = element.getMarker();
        String              name        = getParameterValue(element, depth, NAME, String.class);
        String              submitAs    = getParameterValue(element, depth, SUBMIT_AS, String.class);
        String              onInput     = getParameterValue(element, depth, ON_INPUT, String.class, DEFAULT_VAL);
        String              placeholder = getParameterValue(element, depth, PLACEHOLDER, String.class, "***");

        return Password
                .builder()
                .uid(uid)
                .classes(classes)
                .dataAttributes(attributes)
                .marker(marker)
                .name(name)
                .submitAs(submitAs)
                .onInput(onInput)
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
    private Radio transformRadioInput(PseudoElement element, Integer depth)
    {
        String              uid        = extractFromValueMaps(element.getUid(), String.class, element.getUid());
        List<String>        classes    = replaceClasses(element.getClasses());
        Map<String, String> attributes = element.getAttributes();
        List<Marker>        marker     = element.getMarker();
        String              name       = getParameterValue(element, depth, NAME, String.class);
        String              submitAs   = getParameterValue(element, depth, SUBMIT_AS, String.class);
        String              onInput    = getParameterValue(element, depth, ON_INPUT, String.class, DEFAULT_VAL);
        List<InputValue>    values     = extractInputValues(element, depth, true);

        return Radio
                .builder()
                .uid(uid)
                .classes(classes)
                .dataAttributes(attributes)
                .marker(marker)
                .name(name)
                .submitAs(submitAs)
                .onInput(onInput)
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
    private Select transformSelectInput(PseudoElement element, Integer depth)
    {
        String               uid         = extractFromValueMaps(element.getUid(), String.class, element.getUid());
        List<String>         classes     = replaceClasses(element.getClasses());
        Map<String, String>  attributes  = element.getAttributes();
        List<Marker>         marker      = element.getMarker();
        String               name        = getParameterValue(element, depth, NAME, String.class);
        String               submitAs    = getParameterValue(element, depth, SUBMIT_AS, String.class);
        String               onInput     = getParameterValue(element, depth, ON_INPUT, String.class, DEFAULT_VAL);
        String               valueSrc    = getParameterValue(element, depth, "values", String.class, "");
        InputValueList       values      = valueSrc.isBlank() ? extractInputValues(element, depth, false)
                : extractFromValueMaps(valueSrc, InputValueList.class, new InputValueList());

        final InputValueList inputValues = new InputValueList();

        for (final PseudoElement pe : element.getChildren())
        {
            inputValues.add(transformInputValue(pe, depth));
        }

        return Select
                .builder()
                .uid(uid)
                .classes(classes)
                .dataAttributes(attributes)
                .marker(marker)
                .name(name)
                .submitAs(submitAs)
                .onInput(onInput)
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
    private Slider transformSliderInput(PseudoElement element, Integer depth)
    {
        String              uid        = extractFromValueMaps(element.getUid(), String.class, element.getUid());
        List<String>        classes    = replaceClasses(element.getClasses());
        Map<String, String> attributes = element.getAttributes();
        List<Marker>        marker     = element.getMarker();
        String              name       = getParameterValue(element, depth, NAME, String.class);
        String              submitAs   = getParameterValue(element, depth, SUBMIT_AS, String.class);
        String              onInput    = getParameterValue(element, depth, ON_INPUT, String.class, DEFAULT_VAL);
        Integer             value      = getParameterValue(element, depth, VALUE, Integer.class, 0);
        Integer             min        = getParameterValue(element, depth, MIN, Integer.class, Integer.MIN_VALUE);
        Integer             max        = getParameterValue(element, depth, MAX, Integer.class, Integer.MAX_VALUE);

        return Slider
                .builder()
                .uid(uid)
                .classes(classes)
                .dataAttributes(attributes)
                .marker(marker)
                .name(name)
                .submitAs(submitAs)
                .onInput(onInput)
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
    private Switch transformSwitchInput(PseudoElement element, Integer depth)
    {
        String              uid        = extractFromValueMaps(element.getUid(), String.class, element.getUid());
        List<String>        classes    = replaceClasses(element.getClasses());
        Map<String, String> attributes = element.getAttributes();
        List<Marker>        marker     = element.getMarker();
        String              name       = getParameterValue(element, depth, NAME, String.class);
        String              submitAs   = getParameterValue(element, depth, SUBMIT_AS, String.class);
        String              onInput    = getParameterValue(element, depth, ON_INPUT, String.class, DEFAULT_VAL);
        Boolean             checked    = getParameterValue(element, depth, CHECKED, Boolean.class, Boolean.FALSE);

        return Switch
                .builder()
                .uid(uid)
                .classes(classes)
                .dataAttributes(attributes)
                .marker(marker)
                .name(name)
                .submitAs(submitAs)
                .onInput(onInput)
                .checked(checked)
                .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link Textarea} input
     *
     * @param element to transfrom
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private Textarea transformTextareaInput(PseudoElement element, Integer depth)
    {
        String              uid         = extractFromValueMaps(element.getUid(), String.class, element.getUid());
        List<String>        classes     = replaceClasses(element.getClasses());
        Map<String, String> attributes  = element.getAttributes();
        List<Marker>        marker      = element.getMarker();
        String              name        = getParameterValue(element, depth, NAME, String.class);
        String              submitAs    = getParameterValue(element, depth, SUBMIT_AS, String.class);
        String              onInput     = getParameterValue(element, depth, ON_INPUT, String.class, DEFAULT_VAL);
        String              value       = getParameterValue(element, depth, VALUE, String.class, "");
        Integer             maxChars    = getParameterValue(element, depth, MAX_CHARS, Integer.class, 32000);
        String              placeholder = getParameterValue(element, depth, PLACEHOLDER, String.class, "");

        return Textarea
                .builder()
                .uid(uid)
                .classes(classes)
                .dataAttributes(attributes)
                .marker(marker)
                .name(name)
                .submitAs(submitAs)
                .onInput(onInput)
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
    private Textbox transformTextboxInput(PseudoElement element, Integer depth)
    {
        final String value = getParameterValue(element, depth, VALUE, String.class, DEFAULT_VAL);

        return Textbox.builder().onInput(DEFAULT_VAL).value(value).build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link Textfield} input
     *
     * @param element to transfrom
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private Textfield transformTextfieldInput(PseudoElement element, Integer depth)
    {
        String              uid         = extractFromValueMaps(element.getUid(), String.class, element.getUid());
        List<String>        classes     = replaceClasses(element.getClasses());
        Map<String, String> attributes  = element.getAttributes();
        List<Marker>        marker      = element.getMarker();
        String              name        = getParameterValue(element, depth, NAME, String.class);
        String              submitAs    = getParameterValue(element, depth, SUBMIT_AS, String.class);
        String              onInput     = getParameterValue(element, depth, ON_INPUT, String.class, DEFAULT_VAL);
        final String        value       = getParameterValue(element, depth, VALUE, String.class, DEFAULT_VAL);
        final String        placeholder = getParameterValue(element, depth, PLACEHOLDER, String.class, DEFAULT_VAL);
        final String        prefix      = getParameterValue(element, depth, PREFIX, String.class, DEFAULT_VAL);
        final String        suffix      = getParameterValue(element, depth, SUFFIX, String.class, DEFAULT_VAL);
        final Integer       maxChars    = getParameterValue(element, depth, MAX_CHARS, Integer.class, 150);

        return Textfield
                .builder()
                .uid(uid)
                .classes(classes)
                .dataAttributes(attributes)
                .marker(marker)
                .name(name)
                .submitAs(submitAs)
                .onInput(onInput)
                .value(value)
                .placeholder(placeholder)
                .prefix(prefix)
                .suffix(suffix)
                .maxCharacters(maxChars)
                .build();
    }
}
