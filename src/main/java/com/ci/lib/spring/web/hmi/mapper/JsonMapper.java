package com.ci.lib.spring.web.hmi.mapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.ci.lib.spring.web.hmi.container.*;
import com.ci.lib.spring.web.hmi.input.*;
import com.ci.lib.spring.web.hmi.input.Number;
import com.ci.lib.spring.web.hmi.mapper.exception.JsonMapperException;
import com.ci.lib.spring.web.hmi.mapper.exception.JsonParsingException;

import lombok.Data;

@Data
public class JsonMapper
{

    private static final String          PARAMETER_S_IS_EXPECTED_BUT_NOT_SET = "parameter '%s' is expected but not set";
    private static final String          DEFAULT_DATE                        = "0000-00-00";
    private static final String          PLACEHOLDER                         = "placeholder";
    private static final String          DEFAULT_VAL                         = "";
    private static final String          NAME                                = "name";
    private static final String          VALUE                               = "value";
    private static final String          SUBMIT_AS                           = "submitAs";
    private static final String          ON_INPUT                            = "onInput";
    private static final String          IDENTIFIER_INDICATOR                = "..";

    private static final ContainerType[] CONTAINER_CHILDREN                  =
            {ContainerType.AUDIO, ContainerType.AUDIO, ContainerType.BUTTON, ContainerType.BUTTON_BAR, ContainerType.BUTTON_ICON,
                    ContainerType.CONTENT, ContainerType.FORM, ContainerType.HIDDEN, ContainerType.IMAGE, ContainerType.LINK,
                    ContainerType.ROWED_CONTENT, ContainerType.SPAN, ContainerType.SPLITTED, ContainerType.TAB, ContainerType.TEXT};

    private static final ContainerType[] LINK_CHILDREN                       = {ContainerType.TEXT};

    private final TreeHandling           handling;
    private final ValueMap               objectMap;

    private Integer                      count                               = 0;

    /**
     * Map a {@link JsonTreeRoot} to a {@link Container} in a {@link TreeHandling#STATIC} context
     * 
     * @param root to map
     * 
     * @return the mapped {@code Container}
     */
    public static Container map(JsonTreeRoot root)
    {
        JsonMapper mapper = new JsonMapper(TreeHandling.valueOf(root.getHandling().toUpperCase()), null);

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
        TreeHandling handling = TreeHandling.valueOf(root.getHandling().toUpperCase());

        if (handling == TreeHandling.DYNAMIC && valueMap == null)
        {
            throw new JsonMapperException("Mapping is set to [dynamic] fill-in but [valueMap] is null");
        }

        valueMap.seal();

        JsonMapper mapper = new JsonMapper(handling, valueMap);

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
        count++;

        try
        {
            ContainerType type = ContainerType.valueOf(element.getType().toUpperCase());

            if (!Arrays.asList(allowedTypes).contains(type))
            {
                throw new JsonParsingException(element.getUid(), depth, count,
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
        catch (Exception ex)
        {
            throw new JsonParsingException(element.getUid(), depth, count, "error parsing element", ex);
        }
    }

    private Container throwNotSupported(PseudoElement element, Integer depth)
    {
        throw new JsonParsingException(element.getUid(), depth, count,
                String.format("container type [%s] is not supported", element.getType()));
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
        Object o = objectMap.get(objName);

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

    /**
     * Extract a parameter from {@link JsonMapper#objectMap} to a distinct type
     * 
     * @param <I> expected type of returned value
     * @param element
     * @param paramName parameter name
     * @param depth of the recursive operation
     * @param expectedType expected class type of the returned value
     * @param defaultObject if no mapping is found
     * 
     * @return the extracted value of type {@code I}
     * 
     * @throws JsonMapperException when value is not of type {@code expectedType}
     */
    private <I> I extractFromOpjectMap(PseudoElement element, String paramName, Integer depth, Class<I> expectedType, I defaultObject)
            throws JsonMapperException
    {
        I      value      = null;
        String paramValue = element.getParameters().get(paramName);

        if (handling == TreeHandling.DYNAMIC && paramValue.startsWith(IDENTIFIER_INDICATOR))
        {
            value = extractFromObjectMap(paramValue, expectedType);
        }

        if (value == null)
        {
            return defaultObject;
        }

        return value;
    }

    /**
     * Try to extract a parameter from {@link PseudoElement#getParameters()} as a {@link String}
     * 
     * @param element to extract from
     * @param paramName to extract
     * @param depth of the recursive operation
     * 
     * @return the extracted value
     * 
     * @throws JsonParsingException if parameter could not be parsed or is not present
     */
    private String extractStringParam(PseudoElement element, String paramName, Integer depth) throws JsonParsingException
    {
        return extractStringParam(element, paramName, depth, null);
    }

    /**
     * Try to extract a parameter from {@link PseudoElement#getParameters()} as a {@link String}
     * 
     * @param element to extract from
     * @param paramName to extract
     * @param depth of the recursive operation
     * @param defaultValue to use if paramter is not present
     * 
     * @return the extracted value, or {@code defaultValue} if the parameter is not present
     * 
     * @throws JsonParsingException if parameter could not be parsed, or is not present and no default
     *         value is set
     */
    private String extractStringParam(PseudoElement element, String paramName, Integer depth, String defaultValue)
            throws JsonParsingException
    {
        String value      = null;
        String paramValue = element.getParameters().get(paramName);

        if (handling == TreeHandling.DYNAMIC && paramValue != null && paramValue.startsWith(IDENTIFIER_INDICATOR))
        {
            value = extractFromObjectMap(paramValue, String.class);
        }

        if (value == null)
        {
            try
            {
                value = element.getParameters().get(paramName);
            }
            catch (Exception ex)
            {
                if (defaultValue != null)
                {
                    return defaultValue;
                }

                throw new JsonParsingException(element.getUid(), depth, count,
                        String.format("could not parse parameter [%s] as string", paramName), ex);
            }
        }

        if (value == null)
        {
            if (defaultValue == null)
            {
                throw new JsonParsingException(element.getUid(), depth, count,
                        String.format(PARAMETER_S_IS_EXPECTED_BUT_NOT_SET, paramName));
            }

            return defaultValue;
        }

        return value;
    }

    /**
     * Try to extract a parameter from {@link PseudoElement#getParameters()} as a {@link Boolean}
     * 
     * @param element to extract from
     * @param paramName to extract
     * @param depth of the recursive operation
     * 
     * @return the extracted value
     * 
     * @throws JsonParsingException if parameter could not be parsed or is not present
     */
    private Boolean extractBooleanParam(PseudoElement element, String paramName, Integer depth)
    {
        return extractBooleanParam(element, paramName, depth, null);
    }

    /**
     * Try to extract a parameter from {@link PseudoElement#getParameters()} as a {@link Boolean}
     * 
     * @param element to extract from
     * @param paramName to extract
     * @param depth of the recursive operation
     * @param defaultValue to use if paramter is not present
     * 
     * @return the extracted value, or {@code defaultValue} if the parameter is not present
     * 
     * @throws JsonParsingException if parameter could not be parsed, or is not present and no default
     *         value is set
     */
    private Boolean extractBooleanParam(PseudoElement element, String paramName, Integer depth, Boolean defaultValue)
    {
        Boolean value      = null;
        String  paramValue = element.getParameters().get(paramName);

        if (handling == TreeHandling.DYNAMIC && paramValue != null && paramValue.startsWith(IDENTIFIER_INDICATOR))
        {
            value = extractFromObjectMap(paramValue, Boolean.class);
        }

        if (value == null)
        {
            try
            {
                value = Boolean.valueOf(element.getParameters().get(paramName));
            }
            catch (Exception ex)
            {
                if (defaultValue != null)
                {
                    return defaultValue;
                }

                throw new JsonParsingException(element.getUid(), depth, count,
                        String.format("could not parse parameter [%s] as boolean", paramName), ex);
            }
        }

        if (value == null)
        {
            if (defaultValue == null)
            {
                throw new JsonParsingException(element.getUid(), depth, count,
                        String.format(PARAMETER_S_IS_EXPECTED_BUT_NOT_SET, paramName));
            }

            return defaultValue;
        }

        return value;
    }

    /**
     * Try to extract a parameter from {@link PseudoElement#getParameters()} as a {@link Integer}
     * 
     * @param element to extract from
     * @param paramName to extract
     * @param depth of the recursive operation
     * 
     * @return the extracted value
     * 
     * @throws JsonParsingException if parameter could not be parsed or is not present
     */
    private Integer extractIntegerValue(PseudoElement element, String paramName, Integer depth)
    {
        return extractIntegerValue(element, paramName, depth, null);
    }

    /**
     * Try to extract a parameter from {@link PseudoElement#getParameters()} as a {@link Integer}
     * 
     * @param element to extract from
     * @param paramName to extract
     * @param depth of the recursive operation
     * @param defaultValue to use if paramter is not present
     * 
     * @return the extracted value, or {@code defaultValue} if the parameter is not present
     * 
     * @throws JsonParsingException if parameter could not be parsed, or is not present and no default
     *         value is set
     */
    private Integer extractIntegerValue(PseudoElement element, String paramName, Integer depth, Integer defaultValue)
    {
        Integer value      = null;
        String  paramValue = element.getParameters().get(paramName);

        if (handling == TreeHandling.DYNAMIC && paramValue != null && paramValue.startsWith(IDENTIFIER_INDICATOR))
        {
            value = extractFromObjectMap(paramValue, Integer.class);
        }

        if (value == null)
        {
            try
            {
                value = Integer.valueOf(element.getParameters().get(paramName));
            }
            catch (Exception ex)
            {
                if (defaultValue != null)
                {
                    return defaultValue;
                }

                throw new JsonParsingException(element.getUid(), depth, count,
                        String.format("could not parse parameter [%s] as integer", paramName), ex);
            }
        }

        if (value == null)
        {
            if (defaultValue == null)
            {
                throw new JsonParsingException(element.getUid(), depth, count,
                        String.format(PARAMETER_S_IS_EXPECTED_BUT_NOT_SET, paramName));
            }

            return defaultValue;
        }

        return value;
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
        return AudioContainer
                .builder()
                .uid(element.getUid())
                .classes(element.getClasses())
                .dataAttributes(element.getAttributes())
                .src(extractStringParam(element, "src", depth))
                .controls(extractBooleanParam(element, "controls", depth))
                .autoplay(extractBooleanParam(element, "autoplay", depth))
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
                .uid(element.getUid())
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
        List<Container> contents = new ArrayList<>();

        element.getChildren().stream().forEach(c -> contents.add(transform(c, depth + 1, CONTAINER_CHILDREN)));

        return ContentContainer
                .builder()
                .uid(element.getUid())
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
        List<Input> inputs = new ArrayList<>();

        element.getChildren().stream().forEach(c -> inputs.add(transformInput(c, depth + 1)));

        return FormContainer
                .builder()
                .uid(element.getUid())
                .classes(element.getClasses())
                .dataAttributes(element.getAttributes())
                .inputs(inputs)
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
                .uid(element.getUid())
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
        return ImageContainer
                .builder()
                .uid(element.getUid())
                .classes(element.getClasses())
                .dataAttributes(element.getAttributes())
                .src(extractStringParam(element, "src", depth))
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
        return LinkContainer
                .builder()
                .uid(element.getUid())
                .classes(element.getClasses())
                .dataAttributes(element.getAttributes())
                .href(extractStringParam(element, "href", depth))
                .target(extractStringParam(element, "target", depth))
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
        List<ColumnContainer> columns = new ArrayList<>();

        for (PseudoElement pe : element.getChildren())
        {
            columns.add(transformColumnContainer(pe, depth));
        }

        return RowContainer
                .builder()
                .uid(element.getUid())
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
        List<RowContainer> rows = new ArrayList<>();

        for (PseudoElement pe : element.getChildren())
        {
            rows.add(transformRowContainer(pe, depth));
        }

        return RowedContentContainer
                .builder()
                .uid(element.getUid())
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
        return SpanContainer
                .builder()
                .uid(element.getUid())
                .classes(element.getClasses())
                .dataAttributes(element.getAttributes())
                .text(extractStringParam(element, "text", depth))
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
        Container       head   = null;
        Container       tail   = null;
        List<Container> center = new ArrayList<>();

        Integer         cnt    = 0;

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
                .uid(element.getUid())
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
        return TextContainer
                .builder()
                .uid(element.getUid())
                .classes(element.getClasses())
                .dataAttributes(element.getAttributes())
                .text(extractStringParam(element, "text", depth, DEFAULT_VAL))
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
        catch (Exception ex)
        {
            throw new JsonParsingException(element.getUid(), depth, count, "error parsing element", ex);
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
        return Button
                .builder()
                .uid(element.getUid())
                .classes(element.getClasses())
                .dataAttributes(element.getAttributes())
                .text(extractStringParam(element, "text", depth))
                .btnClass(ButtonClass.valueOf(extractStringParam(element, "btnClass", depth, "default").toUpperCase()))
                .onClick(extractStringParam(element, "onClick", depth, DEFAULT_VAL))
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
        return ButtonIcon
                .builder()
                .uid(element.getUid())
                .classes(element.getClasses())
                .dataAttributes(element.getAttributes())
                .image(extractStringParam(element, "image", depth))
                .btnClass(ButtonClass.valueOf(extractStringParam(element, "btnClass", depth).toUpperCase()))
                .onClick(extractStringParam(element, "onClick", depth, DEFAULT_VAL))
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
        return Checkbox
                .builder()
                .uid(element.getUid())
                .classes(element.getClasses())
                .dataAttributes(element.getAttributes())
                .name(extractStringParam(element, NAME, depth))
                .submitAs(extractStringParam(element, SUBMIT_AS, depth))
                .onInput(extractStringParam(element, ON_INPUT, depth, DEFAULT_VAL))
                .checked(extractBooleanParam(element, "checked", depth, false))
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
        return Currency
                .builder()
                .uid(element.getUid())
                .classes(element.getClasses())
                .dataAttributes(element.getAttributes())
                .name(extractStringParam(element, NAME, depth))
                .submitAs(extractStringParam(element, SUBMIT_AS, depth))
                .onInput(extractStringParam(element, ON_INPUT, depth, DEFAULT_VAL))
                .valueF(extractIntegerValue(element, "valueF", depth, 0))
                .valueB(extractIntegerValue(element, "valueB", depth, 0))
                .symbol(extractStringParam(element, "symbol", depth, DEFAULT_VAL))
                .min(extractIntegerValue(element, "min", depth, Integer.MIN_VALUE))
                .max(extractIntegerValue(element, "max", depth, Integer.MAX_VALUE))
                .placeholder(extractStringParam(element, PLACEHOLDER, depth, DEFAULT_VAL))
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
        return Date
                .builder()
                .uid(element.getUid())
                .classes(element.getClasses())
                .dataAttributes(element.getAttributes())
                .name(extractStringParam(element, NAME, depth))
                .submitAs(extractStringParam(element, SUBMIT_AS, depth))
                .onInput(extractStringParam(element, ON_INPUT, depth, DEFAULT_VAL))
                .value(extractStringParam(element, VALUE, depth, DEFAULT_DATE))
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
        return File
                .builder()
                .uid(element.getUid())
                .classes(element.getClasses())
                .dataAttributes(element.getAttributes())
                .name(extractStringParam(element, NAME, depth))
                .submitAs(extractStringParam(element, SUBMIT_AS, depth))
                .onInput(extractStringParam(element, ON_INPUT, depth, DEFAULT_VAL))
                .value(extractStringParam(element, VALUE, depth))
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
        return Hidden
                .builder()
                .uid(element.getUid())
                .classes(element.getClasses())
                .dataAttributes(element.getAttributes())
                .name("")
                .submitAs(extractStringParam(element, SUBMIT_AS, depth))
                .onInput(DEFAULT_VAL)
                .value(extractStringParam(element, VALUE, depth, DEFAULT_VAL))
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
        return Link
                .builder()
                .uid(element.getUid())
                .classes(element.getClasses())
                .dataAttributes(element.getAttributes())
                .text(extractStringParam(element, "text", depth))
                .href(extractStringParam(element, "href", depth))
                .target(extractStringParam(element, "target", depth))
                .onInput(extractStringParam(element, ON_INPUT, depth, DEFAULT_VAL))
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
        return Number
                .builder()
                .uid(element.getUid())
                .classes(element.getClasses())
                .dataAttributes(element.getAttributes())
                .name(extractStringParam(element, NAME, depth))
                .submitAs(extractStringParam(element, SUBMIT_AS, depth))
                .onInput(extractStringParam(element, ON_INPUT, depth, DEFAULT_VAL))
                .value(extractIntegerValue(element, VALUE, depth, 0))
                .min(extractIntegerValue(element, "min", depth, Integer.MIN_VALUE))
                .max(extractIntegerValue(element, "max", depth, Integer.MAX_VALUE))
                .placeholder(extractStringParam(element, PLACEHOLDER, depth, DEFAULT_VAL))
                .prefix(extractStringParam(element, "prefix", depth, DEFAULT_VAL))
                .suffix(extractStringParam(element, "suffix", depth, DEFAULT_VAL))
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
        return Password
                .builder()
                .uid(element.getUid())
                .classes(element.getClasses())
                .dataAttributes(element.getAttributes())
                .name(extractStringParam(element, NAME, depth))
                .submitAs(extractStringParam(element, SUBMIT_AS, depth))
                .onInput(extractStringParam(element, ON_INPUT, depth, DEFAULT_VAL))
                .placeholder(extractStringParam(element, PLACEHOLDER, depth, "***"))
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
        return Radio
                .builder()
                .uid(element.getUid())
                .classes(element.getClasses())
                .dataAttributes(element.getAttributes())
                .name(extractStringParam(element, NAME, depth))
                .submitAs(extractStringParam(element, SUBMIT_AS, depth))
                .onInput(extractStringParam(element, ON_INPUT, depth, DEFAULT_VAL))
                // TODO: add data adapter
                // .values(extractIntegerValue(element, VALUE, depth, 0))
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
        return Select
                .builder()
                .uid(element.getUid())
                .classes(element.getClasses())
                .dataAttributes(element.getAttributes())
                .name(extractStringParam(element, NAME, depth))
                .submitAs(extractStringParam(element, SUBMIT_AS, depth))
                .onInput(extractStringParam(element, ON_INPUT, depth, DEFAULT_VAL))
                // TODO: add data adapter
                // .values(extractIntegerValue(element, VALUE, depth, 0))
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
        return Slider
                .builder()
                .uid(element.getUid())
                .classes(element.getClasses())
                .dataAttributes(element.getAttributes())
                .name(extractStringParam(element, NAME, depth))
                .submitAs(extractStringParam(element, SUBMIT_AS, depth))
                .onInput(extractStringParam(element, ON_INPUT, depth, DEFAULT_VAL))
                .value(extractIntegerValue(element, VALUE, depth, 0))
                .min(extractIntegerValue(element, "min", depth, Integer.MIN_VALUE))
                .max(extractIntegerValue(element, "max", depth, Integer.MAX_VALUE))
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
        return Switch
                .builder()
                .uid(element.getUid())
                .classes(element.getClasses())
                .dataAttributes(element.getAttributes())
                .name(extractStringParam(element, NAME, depth))
                .submitAs(extractStringParam(element, SUBMIT_AS, depth))
                .onInput(extractStringParam(element, ON_INPUT, depth, DEFAULT_VAL))
                .checked(extractBooleanParam(element, "checked", depth, false))
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
        return Textarea
                .builder()
                .uid(element.getUid())
                .classes(element.getClasses())
                .dataAttributes(element.getAttributes())
                .name(extractStringParam(element, NAME, depth))
                .submitAs(extractStringParam(element, SUBMIT_AS, depth))
                .onInput(extractStringParam(element, ON_INPUT, depth, DEFAULT_VAL))
                .value(extractStringParam(element, VALUE, depth, DEFAULT_DATE))
                .maxCharacters(extractIntegerValue(element, "maxCharacters", depth, 32000))
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
        return Textbox.builder().onInput(DEFAULT_VAL).value(extractStringParam(element, VALUE, depth, DEFAULT_DATE)).build();
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
        return Textfield
                .builder()
                .uid(element.getUid())
                .classes(element.getClasses())
                .dataAttributes(element.getAttributes())
                .name(extractStringParam(element, NAME, depth))
                .submitAs(extractStringParam(element, SUBMIT_AS, depth))
                .onInput(extractStringParam(element, ON_INPUT, depth, DEFAULT_VAL))
                .value(extractStringParam(element, VALUE, depth, DEFAULT_VAL))
                .placeholder(extractStringParam(element, PLACEHOLDER, depth, DEFAULT_VAL))
                .prefix(extractStringParam(element, "prefix", depth, DEFAULT_VAL))
                .suffix(extractStringParam(element, "suffix", depth, DEFAULT_VAL))
                .maxCharacters(extractIntegerValue(element, "maxCharacters", depth, 150))
                .build();
    }
}
