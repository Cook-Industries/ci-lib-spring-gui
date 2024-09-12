/**
 * Copyright(c) 2024 sebastian koch/CI. All rights reserved.<br>
 * mailto: koch.sebastian@cook-industries.de
 *
 * @author : sebastian koch <koch.sebastian@cook-industries.de>
 */
package com.ci.lib.spring.web.hmi.mapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.ci.lib.spring.web.hmi.container.*;
import com.ci.lib.spring.web.hmi.input.*;
import com.ci.lib.spring.web.hmi.input.Number;
import com.ci.lib.spring.web.hmi.mapper.exception.JsonMapperException;
import com.ci.lib.spring.web.hmi.mapper.exception.JsonParsingException;

import lombok.Data;

@Data
public class JsonMapper
{

    private static final String                    CONTENT                             = "content";
    private static final String                    SRC                                 = "src";
    private static final String                    SUFFIX                              = "suffix";
    private static final String                    PREFIX                              = "prefix";
    private static final String                    MAX_CHARS                           = "maxChars";
    private static final String                    TARGET                              = "target";
    private static final String                    HREF                                = "href";
    private static final String                    MAX                                 = "max";
    private static final String                    MIN                                 = "min";
    private static final String                    CHECKED                             = "checked";
    private static final String                    IMAGE                               = "image";
    private static final String                    ON_CLICK                            = "onClick";
    private static final String                    BTN_CLASS                           = "btnClass";
    private static final String                    TEXT                                = "text";
    private static final String                    PARAMETER_S_IS_EXPECTED_BUT_NOT_SET = "parameter '%s' is expected but not set";
    private static final String                    DEFAULT_DATE                        = "0000-00-00";
    private static final String                    PLACEHOLDER                         = "placeholder";
    private static final String                    DEFAULT_VAL                         = "";
    private static final String                    NAME                                = "name";
    private static final String                    VALUE                               = "value";
    private static final String                    VALUES                              = "values";
    private static final String                    SUBMIT_AS                           = "submitAs";
    private static final String                    ON_INPUT                            = "onInput";
    private static final String                    IDENTIFIER_INDICATOR                = "..";

    private static final Function<String, Boolean> BOOLEAN                             = (s) -> Boolean.valueOf(s);
    private static final Function<String, Integer> INTEGER                             = (s) -> Integer.valueOf(s);

    //@formatter:off
    private static final ContainerType[]                     CONTAINER_CHILDREN                  =
            {
                ContainerType.AUDIO,
                ContainerType.AUDIO, 
                ContainerType.BUTTON, 
                ContainerType.BUTTON_BAR, 
                ContainerType.BUTTON_ICON,
                ContainerType.CONTENT,
                ContainerType.FORM,
                ContainerType.HEADING,
                ContainerType.HIDDEN,
                ContainerType.IMAGE,
                ContainerType.LINK,
                ContainerType.ROWED_CONTENT,
                ContainerType.SPAN,
                ContainerType.SPLITTED,
                ContainerType.TAB,
                ContainerType.TEXT
            };
    //@formatter:on

    private static final ContainerType[]           LINK_CHILDREN                       = {ContainerType.TEXT};
    private final TreeHandling                     handling;
    private final ValueMap                         objectMap;

    private Integer                                count                               = 0;

    /**
     * Map a {@link JsonTreeRoot} to a {@link Container} in a {@link TreeHandling#STATIC} context
     *
     * @param root to map
     *
     * @return the mapped {@code Container}
     */
    public static Container map(JsonTreeRoot root)
    {
        final JsonMapper mapper = new JsonMapper(TreeHandling.valueOf(root.getHandling().toUpperCase()), null);

        return mapper.transform(root);
    }

    /**
     * Map a {@link JsonTreeRoot} to a {@link Container} in a {@link TreeHandling#DYNAMIC} context
     *
     * @param root to map
     * @param valueMap object map to link to dynamic fill-in content
     *
     * @return the mapped {@code Container}
     *
     * @throws JsonMapperException if mapping is set to dynamic but no {@code valueMap} is provided
     */
    public static Container map(JsonTreeRoot root, ValueMap valueMap)
    {
        final TreeHandling handling = TreeHandling.valueOf(root.getHandling().toUpperCase());

        if (handling == TreeHandling.DYNAMIC && valueMap == null)
        {
            throw new JsonMapperException("Mapping is set to [dynamic] fill-in but [valueMap] is null");
        }

        valueMap.seal();

        final JsonMapper mapper = new JsonMapper(handling, valueMap);

        return mapper.transform(root);
    }

    /**
     * Internal JSON transform
     *
     * @param root
     *
     * @return the transformed {@code Container}
     */
    private Container transform(JsonTreeRoot root) throws JsonMapperException
    {
        return transform(root.getRoot(), 0, ContainerType.values());
    }

    /**
     * Entry function to transform elements from JSON to {@link Container} objects
     *
     * @param element to transform
     * @param depth inside the element tree
     * @param allowedTypes allowed types of transformations
     *
     * @return the transformed container
     *
     * @throws JsonParsingException on any failure, refer to exception text
     */
    private Container transform(PseudoElement element, Integer depth, ContainerType... allowedTypes) throws JsonParsingException
    {
        this.count++;

        try
        {
            final ContainerType type = ContainerType.valueOf(element.getType().toUpperCase());

            if (!Arrays.asList(allowedTypes).contains(type))
            {
                throw new JsonParsingException(element.getUid(), depth, this.count,
                        String.format("container type [%s] is not allowed", element.getType()));
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
                case HEADING -> transformHeadingContainer(element, depth);
                case HIDDEN -> transformHiddenContainer(element, depth);
                case IMAGE -> transformImageContainer(element, depth);
                case LINK -> transformLinkContainer(element, depth);
                case ROW -> transformRowContainer(element, depth);
                case ROWED_CONTENT -> transformRowedContentContainer(element, depth);
                case SPAN -> transformSpanContainer(element, depth);
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

    private Container throwNotSupported(PseudoElement element, Integer depth)
    {
        throw new JsonParsingException(element.getUid(), depth, this.count,
                String.format("container type [%s] is not supported", element.getType()));
    }

    private <I> I extractFromValueMap(PseudoElement element, String key, Integer depth, Class<I> expectedType) throws JsonMapperException
    {
        return extractFromValueMap(element, key, depth, expectedType, null);
    }

    private <I> I extractFromValueMap(PseudoElement element, String key, Integer depth, Class<I> expectedType, I defaultValue)
            throws JsonMapperException
    {
        I value = null;

        if (this.handling == TreeHandling.DYNAMIC && key != null && key.startsWith(IDENTIFIER_INDICATOR))
        {
            value = extractFromObjectMap(key, expectedType);
        }

        if (value == null && defaultValue != null)
        {
            value = defaultValue;
        }

        if (value == null)
        {
            throw new JsonParsingException(element.getUid(), depth, this.count, String.format(PARAMETER_S_IS_EXPECTED_BUT_NOT_SET, key));
        }

        return value;
    }

    /**
     * Extract a value from {@link JsonMapper#objectMap}
     *
     * @param <I> expected type of value
     * @param paramName name of parameter
     * @param expectedType class of value
     *
     * @return the value associated with {@code paramName}
     *
     * @throws JsonMapperException when value is not of type {@code expectedType}
     */
    private <I> I extractFromObjectMap(String objName, Class<I> expectedType) throws JsonMapperException
    {
        final Object o = this.objectMap.get(objName);

        if (o == null)
        {
            return null;
        }

        if (expectedType.isInstance(o))
        {
            return expectedType.cast(o);
        }
        else
        {
            throw new JsonMapperException(String
                    .format("object [%s] could not be set extracted due to expected class is [%s] but got [%s]", objName, expectedType,
                            o.getClass()));
        }
    }

    private String getParameterValue(PseudoElement element, String key)
    {
        String value = element.getParameters().get(key);

        if (value == null)
        {
            throw new JsonParsingException(element.getUid(), 0, this.count, String.format(PARAMETER_S_IS_EXPECTED_BUT_NOT_SET, key));
        }

        return value;
    }

    private String getParameterValue(PseudoElement element, String key, String fallback)
    {
        String value = element.getParameters().get(key);

        if (value == null)
        {
            return fallback;
        }

        return value;
    }

    /**
     * Transform a {@link PseudoElement} to an {@link ColumnContainer}
     *
     * @param element to transfrom
     * @param depth of the recursive operation
     *
     * @return the transformed object
     */
    private InputValue transformInputValue(PseudoElement element, Integer depth)
    {
        String  text    = getParameterValue(element, TEXT);
        Boolean checked = Boolean.valueOf(getParameterValue(element, CHECKED));

        return InputValue
                .builder()
                .text(extractFromValueMap(element, text, depth, String.class, text))
                .value(getParameterValue(element, VALUE))
                .checked(checked)
                .classes(element.getClasses())
                .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link AudioContainer}
     *
     * @param element to transfrom
     * @param depth of the recursive operation
     *
     * @return the transformed object
     */
    private AudioContainer transfromAudioContainer(PseudoElement element, Integer depth)
    {
        final String src      = getParameterValue(element, SRC);
        final String controls = getParameterValue(element, "controls");
        final String autoplay = getParameterValue(element, "autoplay");

        return AudioContainer
                .builder()
                .uid(extractFromValueMap(element, element.getUid(), depth, String.class, element.getUid()))
                .classes(element.getClasses())
                .dataAttributes(element.getAttributes())
                .src(extractFromValueMap(element, src, depth, String.class, src))
                .controls(extractFromValueMap(element, controls, depth, Boolean.class, BOOLEAN.apply(controls)))
                .autoplay(extractFromValueMap(element, autoplay, depth, Boolean.class, BOOLEAN.apply(autoplay)))
                .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link ButtonContainer}
     *
     * @param element to transfrom
     * @param depth of the recursive operation
     *
     * @return the transformed object
     */
    private ButtonContainer transfromButtonContainer(PseudoElement element, Integer depth)
    {
        return ButtonContainer
                .builder()
                .uid("")
                .classes(List.of())
                .dataAttributes(Map.of())
                .button(transformButtonInput(element, depth))
                .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link ButtonBarContainer}
     *
     * @param element to transfrom
     * @param depth of the recursive operation
     *
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
     *
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
     *
     * @return the transformed object
     */
    private ColumnContainer transformColumnContainer(PseudoElement element, Integer depth)
    {
        return ColumnContainer
                .builder()
                .uid(extractFromValueMap(element, element.getUid(), depth, String.class, element.getUid()))
                .classes(element.getClasses())
                .dataAttributes(element.getAttributes())
                .content(transform(element.getChildren().get(0), depth + 1, CONTAINER_CHILDREN))
                .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link ContentContainer}
     *
     * @param element to transfrom
     * @param depth of the recursive operation
     *
     * @return the transformed object
     */
    private ContentContainer transformContentContainer(PseudoElement element, Integer depth)
    {
        final List<Container> contents = new ArrayList<>();

        element.getChildren().stream().forEach(c -> contents.add(transform(c, depth + 1, CONTAINER_CHILDREN)));

        return ContentContainer
                .builder()
                .uid(extractFromValueMap(element, element.getUid(), depth, String.class, element.getUid()))
                .classes(element.getClasses())
                .dataAttributes(element.getAttributes())
                .contents(contents)
                .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link FormContainer}
     *
     * @param element to transfrom
     * @param depth of the recursive operation
     *
     * @return the transformed object
     */
    private FormContainer transformFormContainer(PseudoElement element, Integer depth)
    {
        final List<Input> inputs = new ArrayList<>();

        element.getChildren().stream().forEach(c -> inputs.add(transformInput(c, depth + 1)));

        return FormContainer
                .builder()
                .uid(extractFromValueMap(element, element.getUid(), depth, String.class, element.getUid()))
                .classes(element.getClasses())
                .dataAttributes(element.getAttributes())
                .inputs(inputs)
                .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link HeadingContainer}
     *
     * @param element to transfrom
     * @param depth of the recursive operation
     *
     * @return the transformed object
     */
    private HeadingContainer transformHeadingContainer(PseudoElement element, Integer depth)
    {
        final String text = getParameterValue(element, TEXT, DEFAULT_VAL);
        final String size = getParameterValue(element, "size", "1");

        return HeadingContainer
                .builder()
                .uid(extractFromValueMap(element, element.getUid(), depth, String.class, element.getUid()))
                .classes(element.getClasses())
                .dataAttributes(element.getAttributes())
                .text(extractFromValueMap(element, text, depth, String.class, text))
                .size(Integer.parseInt(size))
                .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link HiddenContainer}
     *
     * @param element to transfrom
     * @param depth of the recursive operation
     *
     * @return the transformed object
     */
    private HiddenContainer transformHiddenContainer(PseudoElement element, Integer depth)
    {
        return HiddenContainer
                .builder()
                .uid(extractFromValueMap(element, element.getUid(), depth, String.class, element.getUid()))
                .classes(element.getClasses())
                .dataAttributes(element.getAttributes())
                .child(transform(element.getChildren().get(0), depth + 1, CONTAINER_CHILDREN))
                .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link ImageContainer}
     *
     * @param element to transfrom
     * @param depth of the recursive operation
     *
     * @return the transformed object
     */
    private ImageContainer transformImageContainer(PseudoElement element, Integer depth)
    {
        final String src = getParameterValue(element, SRC);

        return ImageContainer
                .builder()
                .uid(extractFromValueMap(element, element.getUid(), depth, String.class, element.getUid()))
                .classes(element.getClasses())
                .dataAttributes(element.getAttributes())
                .src(extractFromValueMap(element, src, depth, String.class, src))
                .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link LinkContainer}
     *
     * @param element to transfrom
     * @param depth of the recursive operation
     *
     * @return the transformed object
     */
    private LinkContainer transformLinkContainer(PseudoElement element, Integer depth)
    {
        final String href    = getParameterValue(element, HREF);
        final String content = getParameterValue(element, CONTENT);

        return LinkContainer
                .builder()
                .uid(extractFromValueMap(element, element.getUid(), depth, String.class, element.getUid()))
                .classes(element.getClasses())
                .dataAttributes(element.getAttributes())
                .href(extractFromValueMap(element, href, depth, String.class, href))
                .target(extractFromValueMap(element, content, depth, String.class, content))
                .content(transform(element.getChildren().get(0), depth + 1, LINK_CHILDREN))
                .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link RowContainer}
     *
     * @param element to transfrom
     * @param depth of the recursive operation
     *
     * @return the transformed object
     */
    private RowContainer transformRowContainer(PseudoElement element, Integer depth)
    {
        final List<ColumnContainer> columns = new ArrayList<>();

        for (final PseudoElement pe : element.getChildren())
        {
            columns.add(transformColumnContainer(pe, depth));
        }

        return RowContainer
                .builder()
                .uid(extractFromValueMap(element, element.getUid(), depth, String.class, element.getUid()))
                .classes(element.getClasses())
                .dataAttributes(element.getAttributes())
                .columns(columns)
                .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link RowedContentContainer}
     *
     * @param element to transfrom
     * @param depth of the recursive operation
     *
     * @return the transformed object
     */
    private RowedContentContainer transformRowedContentContainer(PseudoElement element, Integer depth)
    {
        final List<RowContainer> rows = new ArrayList<>();

        for (final PseudoElement pe : element.getChildren())
        {
            rows.add(transformRowContainer(pe, depth));
        }

        return RowedContentContainer
                .builder()
                .uid(extractFromValueMap(element, element.getUid(), depth, String.class, element.getUid()))
                .classes(element.getClasses())
                .dataAttributes(element.getAttributes())
                .rows(rows)
                .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link SpanContainer}
     *
     * @param element to transfrom
     * @param depth of the recursive operation
     *
     * @return the transformed object
     */
    private SpanContainer transformSpanContainer(PseudoElement element, Integer depth)
    {
        final String text = getParameterValue(element, TEXT);

        return SpanContainer
                .builder()
                .uid(extractFromValueMap(element, element.getUid(), depth, String.class, element.getUid()))
                .classes(element.getClasses())
                .dataAttributes(element.getAttributes())
                .text(extractFromValueMap(element, text, depth, String.class, text))
                .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link SplittedContainer}
     *
     * @param element to transfrom
     * @param depth of the recursive operation
     *
     * @return the transformed object
     */
    private SplittedContainer transformSplittedContainer(PseudoElement element, Integer depth)
    {
        Container             head   = null;
        Container             tail   = null;
        final List<Container> center = new ArrayList<>();

        Integer               cnt    = 0;

        for (final PseudoElement pe : element.getChildren())
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
                .uid(extractFromValueMap(element, element.getUid(), depth, String.class, element.getUid()))
                .classes(element.getClasses())
                .dataAttributes(element.getAttributes())
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
     *
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
     *
     * @return the transformed object
     */
    private TextContainer transformTextContainer(PseudoElement element, Integer depth)
    {
        final String text = getParameterValue(element, TEXT, DEFAULT_VAL);

        return TextContainer
                .builder()
                .uid(extractFromValueMap(element, element.getUid(), depth, String.class, element.getUid()))
                .classes(element.getClasses())
                .dataAttributes(element.getAttributes())
                .text(extractFromValueMap(element, text, depth, String.class, text))
                .build();
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
            // TODO:
            throw new JsonParsingException("", depth, this.count, "error parsing element", ex);
        }
    }

    /**
     * Transform a {@link PseudoElement} to an {@link Button} input
     *
     * @param element to transfrom
     * @param depth of the recursive operation
     *
     * @return the transformed object
     */
    private Button transformButtonInput(PseudoElement element, Integer depth)
    {
        final String text     = getParameterValue(element, TEXT);
        final String btnClass = getParameterValue(element, BTN_CLASS, "default");
        final String onClick  = getParameterValue(element, ON_CLICK);

        return Button
                .builder()
                .uid(extractFromValueMap(element, element.getUid(), depth, String.class, element.getUid()))
                .classes(element.getClasses())
                .dataAttributes(element.getAttributes())
                .text(extractFromValueMap(element, text, depth, String.class, text))
                .btnClass(ButtonClass.valueOf(extractFromValueMap(element, btnClass, depth, String.class, btnClass).toUpperCase()))
                .onClick(extractFromValueMap(element, onClick, depth, String.class, onClick))
                .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link ButtonIcon} input
     *
     * @param element to transfrom
     * @param depth of the recursive operation
     *
     * @return the transformed object
     */
    private ButtonIcon transformButtonIconInput(PseudoElement element, Integer depth)
    {
        final String image    = getParameterValue(element, IMAGE);
        final String btnClass = getParameterValue(element, BTN_CLASS);
        final String onClick  = getParameterValue(element, ON_CLICK);

        return ButtonIcon
                .builder()
                .uid(extractFromValueMap(element, element.getUid(), depth, String.class, element.getUid()))
                .classes(element.getClasses())
                .dataAttributes(element.getAttributes())
                .image(extractFromValueMap(element, image, depth, String.class, image))
                .btnClass(ButtonClass.valueOf(extractFromValueMap(element, btnClass, depth, String.class, btnClass).toUpperCase()))
                .onClick(extractFromValueMap(element, onClick, depth, String.class, onClick))
                .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link Checkbox} input
     *
     * @param element to transfrom
     * @param depth of the recursive operation
     *
     * @return the transformed object
     */
    private Checkbox transformCheckboxInput(PseudoElement element, Integer depth)
    {
        final String name     = getParameterValue(element, NAME);
        final String submitAs = getParameterValue(element, SUBMIT_AS);
        final String onInput  = getParameterValue(element, ON_INPUT, "");
        final String checked  = getParameterValue(element, CHECKED, "false");

        return Checkbox
                .builder()
                .uid(extractFromValueMap(element, element.getUid(), depth, String.class, element.getUid()))
                .classes(element.getClasses())
                .dataAttributes(element.getAttributes())
                .name(extractFromValueMap(element, name, depth, String.class, name))
                .submitAs(extractFromValueMap(element, submitAs, depth, String.class, submitAs))
                .onInput(extractFromValueMap(element, onInput, depth, String.class, onInput))
                .checked(extractFromValueMap(element, checked, depth, Boolean.class, BOOLEAN.apply(checked)))
                .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link Currency} input
     *
     * @param element to transfrom
     * @param depth of the recursive operation
     *
     * @return the transformed object
     */
    private Currency transformCurrencyInput(PseudoElement element, Integer depth)
    {
        final String name        = getParameterValue(element, NAME);
        final String submitAs    = getParameterValue(element, SUBMIT_AS);
        final String onInput     = getParameterValue(element, ON_INPUT, "");
        final String valueF      = getParameterValue(element, "valueF", "0");
        final String valueB      = getParameterValue(element, "valueB", "0");
        final String symbol      = getParameterValue(element, "symbol", "");
        final String min         = getParameterValue(element, MIN, "" + Integer.MIN_VALUE);
        final String max         = getParameterValue(element, MAX, "" + Integer.MAX_VALUE);
        final String placeholder = getParameterValue(element, PLACEHOLDER, "");

        return Currency
                .builder()
                .uid(extractFromValueMap(element, element.getUid(), depth, String.class, element.getUid()))
                .classes(element.getClasses())
                .dataAttributes(element.getAttributes())
                .name(extractFromValueMap(element, name, depth, String.class, name))
                .submitAs(extractFromValueMap(element, submitAs, depth, String.class, submitAs))
                .onInput(extractFromValueMap(element, onInput, depth, String.class, onInput))
                .valueF(extractFromValueMap(element, valueF, depth, Integer.class, INTEGER.apply(valueF)))
                .valueB(extractFromValueMap(element, valueB, depth, Integer.class, INTEGER.apply(valueB)))
                .symbol(extractFromValueMap(element, symbol, depth, String.class, symbol))
                .min(extractFromValueMap(element, min, depth, Integer.class, INTEGER.apply(min)))
                .max(extractFromValueMap(element, max, depth, Integer.class, INTEGER.apply(max)))
                .placeholder(extractFromValueMap(element, placeholder, depth, String.class, placeholder))
                .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link Date} input
     *
     * @param element to transfrom
     * @param depth of the recursive operation
     *
     * @return the transformed object
     */
    private Date transformDateInput(PseudoElement element, Integer depth)
    {
        final String name     = getParameterValue(element, NAME);
        final String submitAs = getParameterValue(element, SUBMIT_AS);
        final String onInput  = getParameterValue(element, ON_INPUT, DEFAULT_VAL);
        final String value    = getParameterValue(element, VALUE, DEFAULT_DATE);

        return Date
                .builder()
                .uid(extractFromValueMap(element, element.getUid(), depth, String.class, element.getUid()))
                .classes(element.getClasses())
                .dataAttributes(element.getAttributes())
                .name(extractFromValueMap(element, name, depth, String.class, name))
                .submitAs(extractFromValueMap(element, submitAs, depth, String.class, submitAs))
                .onInput(extractFromValueMap(element, onInput, depth, String.class, onInput))
                .value(extractFromValueMap(element, value, depth, String.class, value))
                .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link File} input
     *
     * @param element to transfrom
     * @param depth of the recursive operation
     *
     * @return the transformed object
     */
    private File transformFileInput(PseudoElement element, Integer depth)
    {
        final String name     = getParameterValue(element, NAME);
        final String submitAs = getParameterValue(element, SUBMIT_AS);
        final String onInput  = getParameterValue(element, ON_INPUT, DEFAULT_VAL);
        final String multiple = getParameterValue(element, "multiple", "false");
        final String accept   = getParameterValue(element, "accept");

        return File
                .builder()
                .uid(extractFromValueMap(element, element.getUid(), depth, String.class, element.getUid()))
                .classes(element.getClasses())
                .dataAttributes(element.getAttributes())
                .name(extractFromValueMap(element, name, depth, String.class, name))
                .submitAs(extractFromValueMap(element, submitAs, depth, String.class, submitAs))
                .onInput(extractFromValueMap(element, onInput, depth, String.class, onInput))
                .multiple(extractFromValueMap(element, multiple, depth, Boolean.class, Boolean.valueOf(multiple)))
                .accept(extractFromValueMap(element, multiple, depth, String.class, accept))
                .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link Hidden} input
     *
     * @param element to transfrom
     * @param depth of the recursive operation
     *
     * @return the transformed object
     */
    private Hidden transformHiddenInput(PseudoElement element, Integer depth)
    {
        final String submitAs = getParameterValue(element, SUBMIT_AS);
        final String value    = getParameterValue(element, VALUE, DEFAULT_VAL);

        return Hidden
                .builder()
                .uid(extractFromValueMap(element, element.getUid(), depth, String.class, element.getUid()))
                .classes(element.getClasses())
                .dataAttributes(element.getAttributes())
                .name(DEFAULT_VAL)
                .submitAs(extractFromValueMap(element, submitAs, depth, String.class, submitAs))
                .onInput(DEFAULT_VAL)
                .value(extractFromValueMap(element, value, depth, String.class, value))
                .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link Link} input
     *
     * @param element to transfrom
     * @param depth of the recursive operation
     *
     * @return the transformed object
     */
    private Link transformLinkInput(PseudoElement element, Integer depth)
    {
        final String text    = getParameterValue(element, TEXT);
        final String href    = getParameterValue(element, HREF);
        final String target  = getParameterValue(element, TARGET);
        final String onInput = getParameterValue(element, ON_INPUT, DEFAULT_VAL);

        return Link
                .builder()
                .uid(extractFromValueMap(element, element.getUid(), depth, String.class, element.getUid()))
                .classes(element.getClasses())
                .dataAttributes(element.getAttributes())
                .text(extractFromValueMap(element, text, depth, String.class, text))
                .href(extractFromValueMap(element, href, depth, String.class, href))
                .target(extractFromValueMap(element, target, depth, String.class, target))
                .onInput(extractFromValueMap(element, onInput, depth, String.class, onInput))
                .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link Number} input
     *
     * @param element to transfrom
     * @param depth of the recursive operation
     *
     * @return the transformed object
     */
    private Number transformNumberInput(PseudoElement element, Integer depth)
    {
        final String name        = getParameterValue(element, NAME);
        final String submitAs    = getParameterValue(element, SUBMIT_AS);
        final String onInput     = getParameterValue(element, ON_INPUT, DEFAULT_VAL);
        final String value       = getParameterValue(element, VALUE, "0");
        final String min         = getParameterValue(element, MIN, "" + Integer.MIN_VALUE);
        final String max         = getParameterValue(element, MAX, "" + Integer.MAX_VALUE);
        final String placeholder = getParameterValue(element, PLACEHOLDER, DEFAULT_VAL);
        final String prefix      = getParameterValue(element, PREFIX, DEFAULT_VAL);
        final String suffix      = getParameterValue(element, SUFFIX, DEFAULT_VAL);

        return Number
                .builder()
                .uid(extractFromValueMap(element, element.getUid(), depth, String.class, element.getUid()))
                .classes(element.getClasses())
                .dataAttributes(element.getAttributes())
                .name(extractFromValueMap(element, name, depth, String.class, name))
                .submitAs(extractFromValueMap(element, submitAs, depth, String.class, submitAs))
                .onInput(extractFromValueMap(element, onInput, depth, String.class, onInput))
                .value(extractFromValueMap(element, value, depth, Integer.class, INTEGER.apply(value)))
                .min(extractFromValueMap(element, min, depth, Integer.class, INTEGER.apply(min)))
                .max(extractFromValueMap(element, max, depth, Integer.class, INTEGER.apply(max)))
                .placeholder(extractFromValueMap(element, placeholder, depth, String.class, placeholder))
                .prefix(extractFromValueMap(element, prefix, depth, String.class, prefix))
                .suffix(extractFromValueMap(element, suffix, depth, String.class, suffix))
                .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link Password} input
     *
     * @param element to transfrom
     * @param depth of the recursive operation
     *
     * @return the transformed object
     */
    private Password transformPasswordInput(PseudoElement element, Integer depth)
    {
        final String name        = getParameterValue(element, NAME);
        final String submitAs    = getParameterValue(element, SUBMIT_AS);
        final String onInput     = getParameterValue(element, ON_INPUT, DEFAULT_VAL);
        final String placeholder = getParameterValue(element, PLACEHOLDER, "***");

        return Password
                .builder()
                .uid(extractFromValueMap(element, element.getUid(), depth, String.class, element.getUid()))
                .classes(element.getClasses())
                .dataAttributes(element.getAttributes())
                .name(extractFromValueMap(element, name, depth, String.class, name))
                .submitAs(extractFromValueMap(element, submitAs, depth, String.class, submitAs))
                .onInput(extractFromValueMap(element, onInput, depth, String.class, onInput))
                .placeholder(extractFromValueMap(element, placeholder, depth, String.class, placeholder))
                .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link Radio} input
     *
     * @param element to transfrom
     * @param depth of the recursive operation
     *
     * @return the transformed object
     */
    private Radio transformRadioInput(PseudoElement element, Integer depth)
    {
        final String name     = getParameterValue(element, NAME);
        final String submitAs = getParameterValue(element, SUBMIT_AS);
        final String onInput  = getParameterValue(element, ON_INPUT, DEFAULT_VAL);
        final String values   = getParameterValue(element, VALUES);

        return Radio
                .builder()
                .uid(extractFromValueMap(element, element.getUid(), depth, String.class, element.getUid()))
                .classes(element.getClasses())
                .dataAttributes(element.getAttributes())
                .name(extractFromValueMap(element, name, depth, String.class, name))
                .submitAs(extractFromValueMap(element, submitAs, depth, String.class, submitAs))
                .onInput(extractFromValueMap(element, onInput, depth, String.class, onInput))
                .values(extractFromValueMap(element, values, depth, InputValueList.class))
                .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link Select} input
     *
     * @param element to transfrom
     * @param depth of the recursive operation
     *
     * @return the transformed object
     */
    private Select transformSelectInput(PseudoElement element, Integer depth)
    {
        final String         name        = getParameterValue(element, NAME);
        final String         submitAs    = getParameterValue(element, SUBMIT_AS);
        final String         onInput     = getParameterValue(element, ON_INPUT, DEFAULT_VAL);
        final String         values      = getParameterValue(element, VALUES, null);

        final InputValueList inputValues = new InputValueList();

        for (final PseudoElement pe : element.getChildren())
        {
            inputValues.add(transformInputValue(pe, depth));
        }

        return Select
                .builder()
                .uid(extractFromValueMap(element, element.getUid(), depth, String.class, element.getUid()))
                .classes(element.getClasses())
                .dataAttributes(element.getAttributes())
                .name(extractFromValueMap(element, name, depth, String.class, name))
                .submitAs(extractFromValueMap(element, submitAs, depth, String.class, submitAs))
                .onInput(extractFromValueMap(element, onInput, depth, String.class, onInput))
                .values(extractFromValueMap(element, values, depth, InputValueList.class, inputValues))
                .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link Slider} input
     *
     * @param element to transfrom
     * @param depth of the recursive operation
     *
     * @return the transformed object
     */
    private Slider transformSliderInput(PseudoElement element, Integer depth)
    {
        final String name     = getParameterValue(element, NAME);
        final String submitAs = getParameterValue(element, SUBMIT_AS);
        final String onInput  = getParameterValue(element, ON_INPUT, DEFAULT_VAL);
        final String value    = getParameterValue(element, VALUE, "0");
        final String min      = getParameterValue(element, MIN, "" + Integer.MIN_VALUE);
        final String max      = getParameterValue(element, MAX, "" + Integer.MAX_VALUE);

        return Slider
                .builder()
                .uid(extractFromValueMap(element, element.getUid(), depth, String.class, element.getUid()))
                .classes(element.getClasses())
                .dataAttributes(element.getAttributes())
                .name(extractFromValueMap(element, name, depth, String.class, name))
                .submitAs(extractFromValueMap(element, submitAs, depth, String.class, submitAs))
                .onInput(extractFromValueMap(element, onInput, depth, String.class, onInput))
                .value(extractFromValueMap(element, value, depth, Integer.class, INTEGER.apply(value)))
                .min(extractFromValueMap(element, min, depth, Integer.class, INTEGER.apply(min)))
                .max(extractFromValueMap(element, max, depth, Integer.class, INTEGER.apply(max)))
                .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link Switch} input
     *
     * @param element to transfrom
     * @param depth of the recursive operation
     *
     * @return the transformed object
     */
    private Switch transformSwitchInput(PseudoElement element, Integer depth)
    {
        final String name     = getParameterValue(element, NAME);
        final String submitAs = getParameterValue(element, SUBMIT_AS);
        final String onInput  = getParameterValue(element, ON_INPUT, DEFAULT_VAL);
        final String checked  = getParameterValue(element, CHECKED, "false");

        return Switch
                .builder()
                .uid(extractFromValueMap(element, element.getUid(), depth, String.class, element.getUid()))
                .classes(element.getClasses())
                .dataAttributes(element.getAttributes())
                .name(extractFromValueMap(element, name, depth, String.class, name))
                .submitAs(extractFromValueMap(element, submitAs, depth, String.class, submitAs))
                .onInput(extractFromValueMap(element, onInput, depth, String.class, onInput))
                .checked(extractFromValueMap(element, checked, depth, Boolean.class, BOOLEAN.apply(checked)))
                .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link Textarea} input
     *
     * @param element to transfrom
     * @param depth of the recursive operation
     *
     * @return the transformed object
     */
    private Textarea transformTextareaInput(PseudoElement element, Integer depth)
    {
        final String name     = getParameterValue(element, NAME);
        final String submitAs = getParameterValue(element, SUBMIT_AS);
        final String onInput  = getParameterValue(element, ON_INPUT, DEFAULT_VAL);
        final String value    = getParameterValue(element, VALUE, "0");
        final String maxChars = getParameterValue(element, MAX_CHARS, "32000");

        return Textarea
                .builder()
                .uid(extractFromValueMap(element, element.getUid(), depth, String.class, element.getUid()))
                .classes(element.getClasses())
                .dataAttributes(element.getAttributes())
                .name(extractFromValueMap(element, name, depth, String.class, name))
                .submitAs(extractFromValueMap(element, submitAs, depth, String.class, submitAs))
                .onInput(extractFromValueMap(element, onInput, depth, String.class, onInput))
                .value(extractFromValueMap(element, value, depth, String.class, value))
                .maxCharacters(extractFromValueMap(element, maxChars, depth, Integer.class, INTEGER.apply(maxChars)))
                .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link Textbox} input
     *
     * @param element to transfrom
     * @param depth of the recursive operation
     *
     * @return the transformed object
     */
    private Textbox transformTextboxInput(PseudoElement element, Integer depth)
    {
        final String value = getParameterValue(element, VALUE, DEFAULT_VAL);

        return Textbox.builder().onInput(DEFAULT_VAL).value(extractFromValueMap(element, value, depth, String.class, value)).build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link Textfield} input
     *
     * @param element to transfrom
     * @param depth of the recursive operation
     *
     * @return the transformed object
     */
    private Textfield transformTextfieldInput(PseudoElement element, Integer depth)
    {
        final String name        = getParameterValue(element, NAME);
        final String submitAs    = getParameterValue(element, SUBMIT_AS);
        final String onInput     = getParameterValue(element, ON_INPUT, DEFAULT_VAL);
        final String value       = getParameterValue(element, VALUE, DEFAULT_VAL);
        final String placeholder = getParameterValue(element, PLACEHOLDER, DEFAULT_VAL);
        final String prefix      = getParameterValue(element, PREFIX, DEFAULT_VAL);
        final String suffix      = getParameterValue(element, SUFFIX, DEFAULT_VAL);
        final String maxChars    = getParameterValue(element, MAX_CHARS, "150");

        return Textfield
                .builder()
                .uid(extractFromValueMap(element, element.getUid(), depth, String.class, element.getUid()))
                .classes(element.getClasses())
                .dataAttributes(element.getAttributes())
                .name(extractFromValueMap(element, name, depth, String.class, name))
                .submitAs(extractFromValueMap(element, submitAs, depth, String.class, submitAs))
                .onInput(extractFromValueMap(element, onInput, depth, String.class, onInput))
                .value(extractFromValueMap(element, value, depth, String.class, value))
                .placeholder(extractFromValueMap(element, placeholder, depth, String.class, placeholder))
                .prefix(extractFromValueMap(element, prefix, depth, String.class, prefix))
                .suffix(extractFromValueMap(element, suffix, depth, String.class, suffix))
                .maxCharacters(extractFromValueMap(element, maxChars, depth, Integer.class, INTEGER.apply(maxChars)))
                .build();
    }
}
