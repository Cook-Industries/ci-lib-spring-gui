/**
 * Copyright (c) 2016-2026 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.hmi.mapper.json;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;

import de.cookindustries.lib.spring.gui.function.AbsFunctionCall;
import de.cookindustries.lib.spring.gui.function.CloseModal;
import de.cookindustries.lib.spring.gui.function.RegisterTagInput;
import de.cookindustries.lib.spring.gui.function.SubmitFromModal;
import de.cookindustries.lib.spring.gui.function.TagInputSettings;
import de.cookindustries.lib.spring.gui.hmi.container.AudioContainer;
import de.cookindustries.lib.spring.gui.hmi.container.BurgerContainer;
import de.cookindustries.lib.spring.gui.hmi.container.BurgerItem;
import de.cookindustries.lib.spring.gui.hmi.container.Button;
import de.cookindustries.lib.spring.gui.hmi.container.ButtonClass;
import de.cookindustries.lib.spring.gui.hmi.container.ButtonIcon;
import de.cookindustries.lib.spring.gui.hmi.container.Container;
import de.cookindustries.lib.spring.gui.hmi.container.ContainerType;
import de.cookindustries.lib.spring.gui.hmi.container.ContentContainer;
import de.cookindustries.lib.spring.gui.hmi.container.Direction;
import de.cookindustries.lib.spring.gui.hmi.container.EmptyContainer;
import de.cookindustries.lib.spring.gui.hmi.container.FormContainer;
import de.cookindustries.lib.spring.gui.hmi.container.HeadingContainer;
import de.cookindustries.lib.spring.gui.hmi.container.HiddenContainer;
import de.cookindustries.lib.spring.gui.hmi.container.ImageContainer;
import de.cookindustries.lib.spring.gui.hmi.container.LinkContainer;
import de.cookindustries.lib.spring.gui.hmi.container.ModalContainer;
import de.cookindustries.lib.spring.gui.hmi.container.SVGContainer;
import de.cookindustries.lib.spring.gui.hmi.container.SplittedContainer;
import de.cookindustries.lib.spring.gui.hmi.container.TabContainer;
import de.cookindustries.lib.spring.gui.hmi.container.TableContainer;
import de.cookindustries.lib.spring.gui.hmi.container.TableRowContainer;
import de.cookindustries.lib.spring.gui.hmi.container.TextContainer;
import de.cookindustries.lib.spring.gui.hmi.input.Checkbox;
import de.cookindustries.lib.spring.gui.hmi.input.Date;
import de.cookindustries.lib.spring.gui.hmi.input.File;
import de.cookindustries.lib.spring.gui.hmi.input.Hidden;
import de.cookindustries.lib.spring.gui.hmi.input.Input;
import de.cookindustries.lib.spring.gui.hmi.input.InputType;
import de.cookindustries.lib.spring.gui.hmi.input.Number;
import de.cookindustries.lib.spring.gui.hmi.input.Password;
import de.cookindustries.lib.spring.gui.hmi.input.Radio;
import de.cookindustries.lib.spring.gui.hmi.input.Select;
import de.cookindustries.lib.spring.gui.hmi.input.Slider;
import de.cookindustries.lib.spring.gui.hmi.input.Switch;
import de.cookindustries.lib.spring.gui.hmi.input.Tag;
import de.cookindustries.lib.spring.gui.hmi.input.Textarea;
import de.cookindustries.lib.spring.gui.hmi.input.Textbox;
import de.cookindustries.lib.spring.gui.hmi.input.Textfield;
import de.cookindustries.lib.spring.gui.hmi.input.util.InputValue;
import de.cookindustries.lib.spring.gui.hmi.input.util.InputValueList;
import de.cookindustries.lib.spring.gui.hmi.mapper.exception.JsonMapperException;
import de.cookindustries.lib.spring.gui.hmi.mapper.exception.JsonParsingException;
import de.cookindustries.lib.spring.gui.hmi.mapper.util.FlatMappable;
import de.cookindustries.lib.spring.gui.hmi.mapper.util.FlatMappableDissector;
import de.cookindustries.lib.spring.gui.hmi.mapper.util.FlatMappableList;
import de.cookindustries.lib.spring.gui.hmi.mapper.util.TokenMap;
import de.cookindustries.lib.spring.gui.hmi.svg.SVGElement;
import de.cookindustries.lib.spring.gui.hmi.svg.SVGGroup;
import de.cookindustries.lib.spring.gui.hmi.svg.SVGLine;
import de.cookindustries.lib.spring.gui.hmi.svg.SVGPath;
import de.cookindustries.lib.spring.gui.hmi.svg.SVGPathCmdA;
import de.cookindustries.lib.spring.gui.hmi.svg.SVGPathCmdC;
import de.cookindustries.lib.spring.gui.hmi.svg.SVGPathCmdH;
import de.cookindustries.lib.spring.gui.hmi.svg.SVGPathCmdL;
import de.cookindustries.lib.spring.gui.hmi.svg.SVGPathCmdM;
import de.cookindustries.lib.spring.gui.hmi.svg.SVGPathCmdQ;
import de.cookindustries.lib.spring.gui.hmi.svg.SVGPathCmdS;
import de.cookindustries.lib.spring.gui.hmi.svg.SVGPathCmdT;
import de.cookindustries.lib.spring.gui.hmi.svg.SVGPathCmdV;
import de.cookindustries.lib.spring.gui.hmi.svg.SVGPathCmdZ;
import de.cookindustries.lib.spring.gui.hmi.svg.SVGPathCommand;
import de.cookindustries.lib.spring.gui.hmi.svg.SVGPathCommandType;
import de.cookindustries.lib.spring.gui.hmi.svg.SVGText;
import de.cookindustries.lib.spring.gui.hmi.svg.SVGType;
import de.cookindustries.lib.spring.gui.hmi.util.TemplateFileCache;
import de.cookindustries.lib.spring.gui.hmi.util.exception.TemplateFileNotFoundException;
import de.cookindustries.lib.spring.gui.i18n.AbsTranslationProvider;
import de.cookindustries.lib.spring.gui.i18n.BasicText;
import de.cookindustries.lib.spring.gui.i18n.StaticTranslationProvider;
import de.cookindustries.lib.spring.gui.util.PathAdapter;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.NonNull;
import lombok.Singular;

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

    private static final Logger                                 LOG                            =
        LoggerFactory.getLogger(JsonMapper.class);

    private static final String                                 PARAM_ACTIVE                   = "active";
    private static final String                                 PARAM_ACCEPT                   = "accept";
    private static final String                                 PARAM_ANGLE_LARGE_ARC_FLAG     = "angleLargeArcFlag";
    private static final String                                 PARAM_AUTOPLAY                 = "autoplay";
    private static final String                                 PARAM_BOXES                    = "boxes";
    private static final String                                 PARAM_BTN_CENTER_CLASS         = "btnCenterClass";
    private static final String                                 PARAM_BTN_CENTER_FUNCTION      = "btnCenterFunction";
    private static final String                                 PARAM_BTN_CENTER_NAME          = "btnCenterName";
    private static final String                                 PARAM_BTN_CLASS                = "btnClass";
    private static final String                                 PARAM_BTN_LEFT_CLASS           = "btnLeftClass";
    private static final String                                 PARAM_BTN_LEFT_FUNCTION        = "btnLeftFunction";
    private static final String                                 PARAM_BTN_LEFT_NAME            = "btnLeftName";
    private static final String                                 PARAM_BTN_RIGHT_CLASS          = "btnRightClass";
    private static final String                                 PARAM_BTN_RIGHT_FUNCTION       = "btnRightFunction";
    private static final String                                 PARAM_BTN_RIGHT_NAME           = "btnRightName";
    private static final String                                 PARAM_CHECKED                  = "checked";
    private static final String                                 PARAM_CLOSE_ON_OVERLAY_CLICK   = "closeOnOverlayClick";
    private static final String                                 PARAM_CONNECTED_BTN            = "connectedBtn";
    private static final String                                 PARAM_CONTROLS                 = "controls";
    private static final String                                 PARAM_CAPTION                  = "caption";
    private static final String                                 PARAM_CONTROL_POINT_1_X        = "controlPoint1X";
    private static final String                                 PARAM_CONTROL_POINT_1_Y        = "controlPoint1Y";
    private static final String                                 PARAM_CONTROL_POINT_2_X        = "controlPoint2X";
    private static final String                                 PARAM_CONTROL_POINT_2_Y        = "controlPoint2Y";
    private static final String                                 PARAM_CONTROL_POINT_X          = "controlPointX";
    private static final String                                 PARAM_CONTROL_POINT_Y          = "controlPointY";
    private static final String                                 PARAM_DIRECTION                = "direction";
    private static final String                                 PARAM_DISABLED                 = "disabled";
    private static final String                                 PARAM_DX                       = "dx";
    private static final String                                 PARAM_DY                       = "dy";
    private static final String                                 PARAM_ELEMENT_SOURCE           = "elementSource";
    private static final String                                 PARAM_ELEMENT_SOURCE_IS_LIST   = "elementSourceIsList";
    private static final String                                 PARAM_END_POINT_X              = "endPointX";
    private static final String                                 PARAM_END_POINT_Y              = "endPointY";
    private static final String                                 PARAM_ENFORCE_WHITELIST        = "enforceWhitelist";
    private static final String                                 PARAM_FETCH_URL                = "fetchUrl";
    private static final String                                 PARAM_HEIGHT                   = "height";
    private static final String                                 PARAM_HREF                     = "href";
    private static final String                                 PARAM_ICON                     = "icon";
    private static final String                                 PARAM_ID                       = "id";
    private static final String                                 PARAM_INFO_TEXT                = "infoText";
    private static final String                                 PARAM_INFO_URL                 = "infoUrl";
    private static final String                                 PARAM_INLINE                   = "inline";
    private static final String                                 PARAM_IS_SOURCE_LIST           = "isSourceList";
    private static final String                                 PARAM_MANDATORY                = "mandatory";
    private static final String                                 PARAM_MAX                      = "max";
    private static final String                                 PARAM_STEP                     = "step";
    private static final String                                 PARAM_MAX_CHARS                = "maxChars";
    private static final String                                 PARAM_MAX_TAGS                 = "maxTags";
    private static final String                                 PARAM_METHOD                   = "method";
    private static final String                                 PARAM_MIN                      = "min";
    private static final String                                 PARAM_MULTIPLE                 = "multiple";
    private static final String                                 PARAM_NAME                     = "name";
    private static final String                                 PARAM_ON_CLICK                 = "onClick";
    private static final String                                 PARAM_ON_ENTER_PRESS           = "onEnterPress";
    private static final String                                 PARAM_ON_INPUT                 = "onInput";
    private static final String                                 PARAM_PATH                     = "path";
    private static final String                                 PARAM_PATTERN                  = "pattern";
    private static final String                                 PARAM_PLACEHOLDER              = "placeholder";
    private static final String                                 PARAM_RELATIVE                 = "relative";
    private static final String                                 PARAM_REQUEST_URL              = "requestUrl";
    private static final String                                 PARAM_ROTATE                   = "rotate";
    private static final String                                 PARAM_RX                       = "rx";
    private static final String                                 PARAM_RY                       = "ry";
    private static final String                                 PARAM_SEARCH_URL               = "searchUrl";
    private static final String                                 PARAM_SIZE                     = "size";
    private static final String                                 PARAM_SRC                      = "src";
    private static final String                                 PARAM_STYLE                    = "style";
    private static final String                                 PARAM_SUBMIT_AS                = "submitAs";
    private static final String                                 PARAM_SWEEP_FLAG               = "sweepFlag";
    private static final String                                 PARAM_TARGET                   = "target";
    private static final String                                 PARAM_TEXT                     = "text";
    private static final String                                 PARAM_TEXT_LENGTH              = "textLength";
    private static final String                                 PARAM_TOOLTIP                  = "tooltip";
    private static final String                                 PARAM_URL                      = "url";
    private static final String                                 PARAM_VALUE                    = "value";
    private static final String                                 PARAM_VALUES                   = "values";
    private static final String                                 PARAM_WIDTH                    = "width";
    private static final String                                 PARAM_X                        = "x";
    private static final String                                 PARAM_X1                       = "x1";
    private static final String                                 PARAM_X2                       = "x2";
    private static final String                                 PARAM_Y                        = "y";
    private static final String                                 PARAM_Y1                       = "y1";
    private static final String                                 PARAM_Y2                       = "y2";
    private static final String                                 PARAM_TABLE_NAME               = "tableName";
    private static final String                                 PARAM_COLUMN_NAMES             = "columnNames";
    private static final String                                 PARAM_SORTABLE                 = "sortable";

    private static final String                                 RANDOM_ID                      = "randomid";
    private static final String                                 CONNECTED_BTN                  = "connected-btn";
    private static final String                                 TYPE_ITEM                      = "burger-item";

    private static final String                                 DEFAULT_DATE                   = "0000-00-00";
    private static final String                                 DEFAULT_EMPTY_VAL              = "";
    private static final String                                 DEFAULT_TARGET_SELF            = "_self";
    private static final String                                 DEFAULT_BURGER_ICON            = "bi-caret-right-fill";
    private static final String                                 DEFAULT_VALUE_NOT_SET          = "value not set";
    private static final String                                 DEFAULT_NOT_SELECTED           = "NOT_SELECTED";

    private static final String                                 BASE_INDICATOR_START           = "$$";
    private static final String                                 INDICATOR_SEPARATOR            = "$";
    private static final String                                 INDICATOR_VALUE_PLACEHOLDER    =
        BASE_INDICATOR_START + "value" + INDICATOR_SEPARATOR;
    private static final String                                 INDICATOR_TEXT_PLACEHOLDER     =
        BASE_INDICATOR_START + "text" + INDICATOR_SEPARATOR;
    private static final String                                 INDICATOR_CLASS_PLACEHOLDER    =
        BASE_INDICATOR_START + "class" + INDICATOR_SEPARATOR;
    private static final String                                 INDICATOR_FUNCTION_PLACEHOLDER =
        BASE_INDICATOR_START + "function" + INDICATOR_SEPARATOR;

    private static final Pattern                                PATTERN_DATE                   =
        Pattern.compile("[0-9]{4}\\-[0-9]{2}\\-[0-9]{2}");

    private static final BigInteger                             BIG_INTEGER_MAX_INTEGER_VALUE  =
        new BigInteger(String.valueOf(Integer.MAX_VALUE));

    private static final StaticTranslationProvider              NOOP_TRANSLATION_PROVIDER      =
        new StaticTranslationProvider();

    // @formatter:off
    private static final TypeResolver<ContainerType, Container> CONTAINER_RESOLVER = 
    (mapper, type, element, depth) ->
        (switch (type)
        {
            case AUDIO -> mapper.transformAudioContainer(element, depth);
            case BURGER -> mapper.transformBurgerContainer(element, depth);
            case BUTTON -> mapper.transformButtonContainer(element, depth);
            case BUTTON_ICON -> mapper.transformButtonIconContainer(element, depth);
            case CONTENT -> mapper.transformContentContainer(element, depth);
            case EMPTY -> EmptyContainer.builder().build();
            case FORM -> mapper.transformFormContainer(element, depth);
            case HEADING -> mapper.transformHeadingContainer(element, depth);
            case HIDDEN -> mapper.transformHiddenContainer(element, depth);
            case IMAGE -> mapper.transformImageContainer(element, depth);
            case LINK -> mapper.transformLinkContainer(element, depth);
            case MODAL -> mapper.transformModal(element, depth);
            case SPLITTED -> mapper.transformSplittedContainer(element, depth);
            case SVG -> mapper.transformSVGContainer(element, depth);
            case TAB -> mapper.transformTabbedContainer(element, depth);
            case TABLE -> mapper.transformTableContainer(element, depth);
            case TABLE_ROW -> mapper.transformTableRowContainer(element, depth);
            case TEXT -> mapper.transformTextContainer(element, depth);
        });

     private static final TypeResolver<InputType, Input> INPUT_RESOLVER = 
    (mapper, type, element, depth) ->
        (switch (type)
        {
            case CHECKBOX -> mapper.transformCheckboxInput(element, depth);
            case DATE -> mapper.transformDateInput(element, depth);
            case FILE -> mapper.transformFileInput(element, depth);
            case HIDDEN -> mapper.transformHiddenInput(element, depth);
            case NUMBER -> mapper.transformNumberInput(element, depth);
            case PASSWORD -> mapper.transformPasswordInput(element, depth);
            case RADIO -> mapper.transformRadioInput(element, depth);
            case SELECT -> mapper.transformSelectInput(element, depth);
            case SLIDER -> mapper.transformSliderInput(element, depth);
            case SWITCH -> mapper.transformSwitchInput(element, depth);
            case TAG -> mapper.transformTagInput(element, depth);
            case TEXTAREA -> mapper.transformTextareaInput(element, depth);
            case TEXTBOX -> mapper.transformTextboxInput(element, depth);
            case TEXTFIELD -> mapper.transformTextfieldInput(element, depth);
        });

    private static final TypeResolver<SVGType, SVGElement> SVG_RESOLVER = 
    (mapper, type, element, depth) ->
        (switch (type)
        {
            case GROUP -> mapper.transformSVGGroup(element, depth);
            case LINE -> mapper. transformSVGLine(element, depth);
            case PATH -> mapper.transformSVGPath(element, depth);
            case TEXT -> mapper.transformSVGText(element, depth);
        });       
    // @formatter:on

    private static final ContainerType[]                        CONTENT_CONTAINER_CHILDREN     =
        {
            ContainerType.AUDIO,
            ContainerType.BUTTON,
            ContainerType.BURGER,
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

    private static final ContainerType[]                        LINK_CONTAINER_CHILDREN        =
        {ContainerType.IMAGE, ContainerType.TEXT};

    private static final ContainerType[]                        TABLE_ROW_CONTAINER_CHILDREN   =
        {
            ContainerType.BUTTON,
            ContainerType.BURGER,
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

    private static final ContainerType[]                        MODAL_CONTAINER_CHILDREN       =
        {
            ContainerType.FORM,
            ContainerType.IMAGE,
            ContainerType.LINK,
            ContainerType.TEXT,
            ContainerType.HEADING,
            ContainerType.TABLE
        };

    @NonNull
    private final TemplateFileCache                             templateFileCache;

    @NonNull
    private final String                                        srcPath;

    private String                                              srcPathSanitized;

    @NonNull
    @Default
    private final Locale                                        locale                         = Locale.ENGLISH;

    @NonNull
    @Default
    private final AbsTranslationProvider                        translationProvider            =
        NOOP_TRANSLATION_PROVIDER;

    @NonNull
    private final FlatMappableDissector                         flatMappableDissector;

    @Singular
    private final List<TokenMap>                                tokenMaps;

    private final Set<TokenMap>                                 finalizedTokenMaps             = new HashSet<>();

    private final Map<Integer, TokenMap>                        tempTokenMaps                  = new HashMap<>();

    private final List<AbsFunctionCall>                         functions                      = new ArrayList<>();

    private final String                                        uuid                           =
        UUID.randomUUID().toString();

    @Default
    private Integer                                             count                          = 0;

    @NonNull
    @Default
    private final ContainerType[]                               depth0AllowedTypes             = ContainerType.values();

    /**
     * Run the mapping process.
     * 
     * @return the mapped result
     */
    public MapperResult map()
    {
        long start = System.nanoTime();

        srcPathSanitized = PathAdapter.toUnixAndRemovePrefaceSlash(srcPath);

        PseudoElement root = templateFileCache.getTemplateTo(srcPathSanitized);

        LOG.debug("[{}]: ### start ###", uuid);

        tokenMaps
            .stream()
            .filter(Objects::nonNull)
            .sorted(
                Comparator
                    .comparing(TokenMap::getPrecedence)
                    .reversed())
            .forEach(
                tm -> {
                    tm
                        .getObjectsToDissect()
                        .forEach(etd -> finalizedTokenMaps.add(flatMappableDissector.dissect(etd, tm.getPrecedence(), locale)));

                    finalizedTokenMaps.add(tm);
                });

        LOG.trace("[{}]: map content in [{}] from [{}] with [{}] token maps", uuid, locale.toLanguageTag(),
            translationProvider.getClass().getSimpleName(), finalizedTokenMaps.size() + tempTokenMaps.size());

        if (LOG.isTraceEnabled())
        {
            finalizedTokenMaps
                .stream()
                .filter(Objects::nonNull)
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
    private Container throwNotSupported(PseudoElement element, int depth)
    {
        throw new JsonParsingException(element.getUid(), depth, this.count,
            String.format("container type [%s] is not supported", element.getType()));
    }

    /**
     * Resolve the {@code uid} on a {@link PseudoElement}.
     * <p>
     * {@code Uid}s can only contain letters, numbers, '-' and '_'.
     * 
     * @param element to resolve
     * @param depth the current depth in the tree
     * @return the resolved {@code uid} or 'unresolvable-uid-xxx' when no value replacement could be performed or a {@code uuid} in the form
     *         {@code randomid-xxxx-xxxx-xxxx-xxxxxxxxxxxx}, if {@code uid} is {@code null}
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

            if (Objects.isNull(resultUid))
            {
                LOG.warn("could not resolve uid key [{}] in [{}]", sourceUid, srcPathSanitized);

                resultUid = "unresolvable-uid-" + sourceUid.substring(INDICATOR_VALUE_PLACEHOLDER.length());
            }
        }
        else
        {
            resultUid = sourceUid;
        }

        LOG.trace("[{}]:[{}]: resolve uid to [{}]", uuid, depth, resultUid);

        return sanitizeUid(resultUid);
    }

    /**
     * Sanitize a uid to only have numbers, letters or '-' and '_'.
     * 
     * @param input to sanitize
     * @return the sanitized input
     */
    private String sanitizeUid(String input)
    {
        return input
            .replaceAll("[\s.]", "-")
            .replaceAll("[^a-zA-Z0-9_-]", DEFAULT_EMPTY_VAL);
    }

    /**
     * Traverse the css classes and replace {@code $$class$} parameters from {@link #finalizedTokenMaps}.
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
            .map(
                c -> {
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
            .filter(Predicate.not(String::isBlank))
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
        Map<String, String> sourceAttributes   = element.getAttributes();
        Map<String, String> resolvedAttributes = new HashMap<>();

        sourceAttributes
            .forEach(
                (key, value) -> {
                    String resolvedKey = key;
                    String resolvedValue = value.replace("\"", DEFAULT_EMPTY_VAL);

                    if (key.startsWith(INDICATOR_VALUE_PLACEHOLDER))
                    {
                        resolvedKey = handlePossiblePlaceholder(key, String.class, depth);
                    }

                    if (Objects.isNull(resolvedKey))
                    {
                        LOG.warn("could not resolve attribute key [{}] in [{}]", key, srcPathSanitized);

                        resolvedKey = "unresolveable-key-" + key.substring(INDICATOR_VALUE_PLACEHOLDER.length());
                    }

                    if (value.startsWith(INDICATOR_VALUE_PLACEHOLDER))
                    {
                        resolvedValue = handlePossiblePlaceholder(value, String.class, depth);
                    }

                    if (Objects.isNull(resolvedValue))
                    {
                        LOG.warn("could not resolve attribute value [{}] in [{}]", key, srcPathSanitized);

                        resolvedValue = "unresolveable-value-" + value.substring(INDICATOR_VALUE_PLACEHOLDER.length());
                    }

                    resolvedAttributes.put(sanitizeUid(resolvedKey), resolvedValue);
                });

        LOG.trace("[{}]:[{}]: resolve attributes {}", uuid, depth, resolvedAttributes);

        return Collections.unmodifiableMap(resolvedAttributes);
    }

    /**
     * Try to handle a placeholder by fetching from {@code #finalizedTokenMaps} based on {@code key} and {@code expectedType}.
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
     * Tries to handle a placeholder by fetching from {@code finalizedTokenMaps} based on {@code key} and {@code expectedType}.
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
     * Extract a value from {@link JsonMapper#finalizedTokenMaps}
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
            tempTokenMaps
                .values()
                .stream()
                .filter(Objects::nonNull)
                .map(m -> m.getValue(objName))
                .filter(Objects::nonNull)
                .findFirst();

        Optional<Object> result     =
            finalizedTokenMaps
                .stream()
                .filter(Objects::nonNull)
                .map(m -> m.getValue(objName))
                .filter(Objects::nonNull)
                .findFirst();

        if (tempResult.isEmpty()
            && result.isEmpty())
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
            return transformRawStringValue(objName, 0, s, expectedType);
        }
        else if (expectedType.equals(String.class))
        {
            return expectedType.cast(String.valueOf(obj));
        }

        throw new JsonMapperException(
            String.format("object [%s] could not be extracted due to expected class is [%s] but got [%s]", objName,
                expectedType.getSimpleName(), obj.getClass().getSimpleName()));
    }

    /**
     * Extract a class from {@link JsonMapper#finalizedTokenMaps}
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
            tempTokenMaps
                .values()
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
            finalizedTokenMaps
                .stream()
                .filter(Objects::nonNull)
                .map(m -> m.getClazz(objName))
                .filter(Objects::nonNull)
                .findFirst();

        return result.isEmpty()
            ? null
            : result.get();
    }

    /**
     * Extract a function from {@link JsonMapper#finalizedTokenMaps}
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
            tempTokenMaps
                .values()
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
            finalizedTokenMaps
                .stream()
                .filter(Objects::nonNull)
                .map(m -> m.getFunction(objName))
                .filter(Objects::nonNull)
                .findFirst();

        return result.isEmpty()
            ? null
            : result.get();
    }

    /**
     * Check if a {@code uid} is disabled in {@link JsonMapper#finalizedTokenMaps}
     *
     * @param uid of the element to check
     * @return {@code false}, if the element is marked as inactive, {@code true} otherwise
     * @throws JsonMapperException if value is not of type {@code expectedType}
     */
    private boolean checkActiveStateFromTokenMaps(String uid) throws JsonMapperException
    {
        if (Objects.isNull(uid))
        {
            return true;
        }

        boolean tempResult =
            tempTokenMaps
                .values()
                .stream()
                .filter(Objects::nonNull)
                .map(m -> m.isUidActive(uid))
                .filter(Objects::nonNull)
                .allMatch(b -> b);

        boolean result     =
            finalizedTokenMaps
                .stream()
                .filter(Objects::nonNull)
                .map(m -> m.isUidActive(uid))
                .filter(Objects::nonNull)
                .allMatch(b -> b);

        return (tempResult && result);
    }

    /**
     * Resolve the basic parameters for all container types.
     * 
     * @param element to resolve from
     * @param depth the current depth in the tree
     * @return extracted parameters in record
     */
    private UiElementParameters resolveUiElementBaseParameters(PseudoElement element, int depth)
    {
        String              uid        = resolveUid(element, depth);
        List<String>        classes    = resolveClasses(element.getClasses(), depth);
        Map<String, String> attributes = resolveAttributes(element, depth);
        String              tooltip    = getParameterValue(element, depth, PARAM_TOOLTIP, String.class, DEFAULT_EMPTY_VAL);

        return new UiElementParameters(uid, classes, attributes, tooltip);
    }

    /**
     * Resolve the basic parameters for all input types.
     * 
     * @param element to resolve from
     * @param depth the current depth in the tree
     * @return extracted parameters in record
     */
    private InputParameters resolveInputBaseParameters(PseudoElement element, int depth)
    {
        String  tooltip       = getParameterValue(element, depth, PARAM_TOOLTIP, String.class, DEFAULT_EMPTY_VAL);
        String  name          = getParameterValue(element, depth, PARAM_NAME, String.class);
        String  submitAs      = getParameterValue(element, depth, PARAM_SUBMIT_AS, String.class, DEFAULT_EMPTY_VAL);
        String  onInput       = getParameterValue(element, depth, PARAM_ON_INPUT, String.class, DEFAULT_EMPTY_VAL);
        String  infoText      = getParameterValue(element, depth, PARAM_INFO_TEXT, String.class, DEFAULT_EMPTY_VAL);
        String  infoUrl       = getParameterValue(element, depth, PARAM_INFO_URL, String.class, DEFAULT_EMPTY_VAL);
        boolean disabled      = getParameterValue(element, depth, PARAM_DISABLED, Boolean.class, false);
        boolean mandatory     = getParameterValue(element, depth, PARAM_MANDATORY, Boolean.class, false);
        String  mandatoryText = translationProvider.getText(locale, "input-mandatory", name);

        return new InputParameters(tooltip, name, submitAs, onInput, infoText, infoUrl, disabled, mandatory, mandatoryText);
    }

    /**
     * Extract a parameter from the {@code element}.
     * <p>
     * Replaces the content of the result with a value fetched from {@link #finalizedTokenMaps} or {@link #translationProvider} if the
     * {@code key} starts with '$$'
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
     * Extract a parameter from the {@code element}.
     * <p>
     * Replaces the content of the result with a value fetched from {@link #finalizedTokenMaps} or {@link #translationProvider} if the
     * {@code key} starts with '$$'
     * 
     * @param <T> the expected result type
     * @param element the element to extract from
     * @param depth the current depth in the tree
     * @param key the key to lookup
     * @param expectedType the expected result type
     * @param fallback to use if no result is found, can be {@code null}
     * @return the found parameter
     * @throws JsonParsingException if {@code key} parameter is expected but not found, and no {@code fallback} is provided, or could not be
     *             parsed
     */
    private <T> T getParameterValue(PseudoElement element, int depth, String key, Class<T> expectedType, T fallback)
    {
        return getParameterValue(element, depth, key, expectedType, fallback, true);
    }

    /**
     * Extract a paramater from the {@code element}.
     * <p>
     * Replaces the content of the result with a value fetched from {@link #finalizedTokenMaps} or {@link #translationProvider} if the
     * {@code key} starts with '$$'
     * 
     * @param <T> the expected result type
     * @param element the element to extract from
     * @param depth the current depth in the tree
     * @param key the key to lookup
     * @param expectedType the expected result type
     * @param fallback to use if no result is found, can be {@code null}
     * @param processPlaceholder wheter or not {@code $$...$} placeholder should be resolved
     * @return the found parameter
     * @throws JsonParsingException if {@code key} parameter is expected but not found, and no {@code fallback} is provided, or could not be
     *             parsed
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
                throw new JsonParsingException(uid, 0, this.count,
                    String.format("parameter [%s] is expected but not set and no fallback given", key));
            }

            LOG.trace("[{}]:[{}]: parameter is [{}]", uuid, depth, String.valueOf(fallback));

            return fallback;
        }

        LOG.trace("[{}]:[{}]: parameter is [{}]:[{}]", uuid, depth, rawValue.getClass().getSimpleName(), String.valueOf(rawValue));

        return resolveSingleValue(uid, depth, key, rawValue, expectedType, processPlaceholder);
    }

    /**
     * Resolve for a list of expected parameters.
     * 
     * @param <T> the expected result type
     * @param element the element to extract from
     * @param depth the current depth in the tree
     * @param key the key to lookup
     * @param expectedType the expected result type
     * @return the found parameter list
     * @throws JsonParsingException if {@code key} parameter is expected but not found or could not be parsed
     */
    private <T> List<T> getParameterList(PseudoElement element, int depth, String key, Class<T> expectedType)
    {
        return getParameterList(element, depth, key, expectedType, null, true);
    }

    /**
     * Resolve for a list of expected parameters.
     * 
     * @param <T> the expected result type
     * @param element the element to extract from
     * @param depth the current depth in the tree
     * @param key the key to lookup
     * @param expectedType the expected result type
     * @param fallback to use if no result is found, can be {@code null}
     * @return the found parameter list
     * @throws JsonParsingException if {@code key} parameter is expected but not found, and no {@code fallback} is provided, or could not be
     *             parsed
     */
    private <T> List<T> getParameterList(PseudoElement element, int depth, String key, Class<T> expectedType, List<T> fallback)
    {
        return getParameterList(element, depth, key, expectedType, fallback, true);
    }

    /**
     * Resolve for a list of expected parameters.
     * 
     * @param <T> the expected result type
     * @param element the element to extract from
     * @param depth the current depth in the tree
     * @param key the key to lookup
     * @param expectedType the expected result type
     * @param fallback to use if no result is found, can be {@code null}
     * @param processPlaceholder wheter or not {@code $$...$} placeholder should be resolved
     * @return the found parameter list
     * @throws JsonParsingException if {@code key} parameter is expected but not found, and no {@code fallback} is provided, or could not be
     *             parsed
     */
    private <T> List<T> getParameterList(PseudoElement element, int depth, String key, Class<T> elementType, List<T> fallback,
        boolean processPlaceholder)
    {
        Object rawValue = element.getParameters().get(key);

        String uid      = element.getUid() == null ? RANDOM_ID : element.getUid();

        LOG.trace("[{}]:[{}]: get list parameter [{}] from [{}] as List<{}>", uuid, depth, key, uid, elementType.getSimpleName());

        if (rawValue == null)
        {
            if (fallback == null)
            {
                throw new JsonParsingException(uid, 0, this.count,
                    String.format("parameter [%s] is expected but not set and no fallback given", key));
            }

            LOG.trace("[{}]:[{}]: parameter is [{}]", uuid, depth, String.valueOf(fallback));

            return fallback;
        }

        if (!(rawValue instanceof List<?> rawList))
        {
            throw new JsonParsingException(uid, 0, this.count,
                String.format("parameter [%s] expected a list but got [%s]", key, rawValue.getClass().getSimpleName()));
        }

        List<T> result = new ArrayList<>(rawList.size());

        for (Object rawElement : rawList)
        {
            result.add(resolveSingleValue(uid, depth, key, rawElement, elementType, processPlaceholder));
        }

        return result;
    }

    /**
     * Resolve a {@code Object} to a type {@code T}.
     * 
     * @param <T> to resolve to
     * @param uid of the element for error logging
     * @param depth of the tree
     * @param key of the parameter for error logging
     * @param rawValue the raw value to resolve
     * @param expectedType class of the target type
     * @param processPlaceholder wheter or not {@code $$...$} placeholder should be resolved
     * @return the resolved type
     * @throws JsonParsingException if the value could not be resolved
     */
    private <T> T resolveSingleValue(String uid, int depth, String key, Object rawValue, Class<T> expectedType, boolean processPlaceholder)
    {
        // ---> DO NOT CHANGE ORDER BETWEEN HERE...
        // resolve strings that can be replacement strings
        if (rawValue instanceof String strVal)
        {
            if (strVal.startsWith(BASE_INDICATOR_START) && processPlaceholder)
            {
                return handlePossiblePlaceholder(strVal, expectedType, depth);
            }
            else
            {
                // process strings that should resolve to enums
                if (expectedType.isEnum())
                {
                    @SuppressWarnings({"unchecked", "rawtypes"})
                    Enum<?> temp = Enum.valueOf((Class) expectedType, strVal.toUpperCase());

                    return expectedType.cast(temp);
                }

                return transformRawStringValue(uid, depth, strVal, expectedType);
            }
        }

        // resolve anything else
        if (expectedType.isInstance(rawValue))
        {
            return expectedType.cast(rawValue);
        }
        // ---> ... AND HERE.

        throw new JsonParsingException(uid, depth, this.count,
            String.format("parameter [%s] parsing inconclusive for value [%s] expected type [%s]", key, rawValue,
                expectedType.getSimpleName()));
    }

    /**
     * Transform the raw String value of a parameter.
     * 
     * @param <T> the expected result type
     * @param uid of the element to transform for error logging
     * @param depth of the tree
     * @param value the value to transform
     * @param expectedType the expected result type
     * @return the transformed {@code vale}
     * @throws JsonParsingException if the value could not be transformed
     */
    private <T> T transformRawStringValue(String uid, int depth, String value, Class<T> expectedType)
    {
        if (expectedType.equals(Boolean.class))
        {
            Boolean b = Boolean.valueOf(value.toLowerCase());

            return expectedType.cast(b);
        }

        if (expectedType.equals(BigInteger.class))
        {
            BigInteger i = new BigInteger(value);

            return expectedType.cast(i);
        }

        if (expectedType.equals(BigDecimal.class))
        {
            BigDecimal i = new BigDecimal(value);

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
     * @param element to transform
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
            sourceKey = getParameterValue(element, depth, PARAM_SRC, String.class);
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

                srcList
                    .getElements()
                    .forEach(
                        src -> {
                            JsonMapper internalMapper =
                                JsonMapper
                                    .builder()
                                    .templateFileCache(templateFileCache)
                                    .translationProvider(translationProvider)
                                    .flatMappableDissector(flatMappableDissector)
                                    .srcPath(path)
                                    .locale(locale)
                                    .tokenMaps(tokenMaps)
                                    .tokenMap(flatMappableDissector.dissect(src, depth, locale))
                                    .depth0AllowedTypes(allowedTypes)
                                    .build();

                            LOG.trace("[{}]:[{}]: map linked component list element [{}] @ mapper [{}]", uuid, depth, path,
                                internalMapper.uuid);

                            internalMapper.tempTokenMaps.putAll(tempTokenMaps);

                            results.add(internalMapper.map());
                        });

                List<Container>       containers = new ArrayList<>();
                List<AbsFunctionCall> functions  = new ArrayList<>();

                results.forEach(
                    res -> {
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
                        .srcPath(path)
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
                    .srcPath(path)
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
     * Transform children into a other type.
     * 
     * @param <T> the expected result type
     * @param element to fetch children from
     * @param parentUid the uid of the parent {@code element}
     * @param targetEnumType the enum to compare the {@code element} type with
     * @param returnType the expected return type of the operation
     * @param depth of the recursive operation
     * @param throwWhenEmpty whether to throw an exception on an empty children-list
     * @return a {@link InputValueList} of the transformed children
     */
    private <E, T> List<T> handleChildren(PseudoElement element, String parentUid, Class<E> targetEnumType, List<E> allowedTypes,
        Class<T> returnType, int depth,
        boolean throwWhenEmpty)
    {
        LOG.trace("[{}]:[{}]: map children of [{}] as [{}] to [{}]", uuid, depth, parentUid, targetEnumType.getSimpleName(),
            returnType.getSimpleName());

        if (throwWhenEmpty && element.getChildren().isEmpty())
        {
            LOG.trace("[{}]:[{}]: empty children are not allowed", uuid, depth);

            throw new JsonMapperException("children list is not allowed to be empty");
        }

        List<T> values = new ArrayList<>();

        for (PseudoElement pe : element.getChildren())
        {
            String  uid            = resolveUid(pe, depth);
            boolean processElement = shouldProcess(pe, uid, depth);

            if (!processElement)
            {
                LOG.trace("[{}]:[{}]: skip child [{}] due to parameter [active] is [false] or [uid] is deactivated", uuid, depth, uid);

                continue;
            }

            switch (targetEnumType.getSimpleName())
            {
                case "InputValue":
                    if ("INPUT_VALUE".equals(pe.getType().toUpperCase()))
                    {
                        values.add(returnType.cast(transformInputValue(pe, depth + 1)));
                    }
                    else
                    {
                        LOG.warn("[{}]:[{}]: the child transform of [{}] failed due to not be of type [INPUT_VALUE]", uuid, depth, uid);
                    }
                    break;

                case "SVGType":
                    transformSVGElement(pe, depth, SVGType.ALLOWED_BASE_TYPES)
                        .stream()
                        .map(returnType::cast)
                        .forEach(values::add);
                    break;

                case "SVGPathCommandType":
                    values.add(returnType.cast(transformSVGPathCommand(pe, depth)));
                    break;

                default:
                    LOG.error("[{}]:[{}]: could not resolve child [{}] due to parameter [{}] is [unknown]", uuid, depth, uid,
                        targetEnumType.getSimpleName());
            }
        }

        return values;
    }

    /**
     * Transform a {@link PseudoElement} to an {@link InputValue}.
     *
     * @param element to transform
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private InputValue transformInputValue(PseudoElement element, int depth)
    {
        List<String> classes = resolveClasses(element.getClasses(), depth);
        String       uid     = getParameterValue(element, depth, PARAM_ID, String.class, DEFAULT_EMPTY_VAL);
        String       text    = getParameterValue(element, depth, PARAM_TEXT, String.class, BasicText.NO_TEXT_SET.getText());
        String       value   = getParameterValue(element, depth, PARAM_VALUE, String.class, BasicText.NO_VALUE_SET.getText());
        boolean      checked = getParameterValue(element, depth, PARAM_CHECKED, Boolean.class, Boolean.FALSE);

        return InputValue
            .builder()
            .uid(uid)
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

        String          uid            = resolveUid(element, depth);

        boolean         processElement = shouldProcess(element, uid, depth);

        if (!processElement)
        {
            LOG.debug("[{}]:[{}]: skip CONTAINER [{}] due to parameter [active] is [false] or [uid] is deactivated", uuid, depth, uid);

            return List.of(EmptyContainer.builder().build());
        }

        LOG.trace("[{}]:[{}]: start transform >", uuid, depth);
        LOG.trace("[{}]:[{}]: map [{}] with allowed types [{}]", uuid, depth, uid, allowedTypes);

        try
        {
            List<Container> componentResult = resolveComponentLink(element, depth, allowedTypes);

            if (Objects.nonNull(componentResult))
            {
                return componentResult;
            }

            ContainerType type = ContainerType.valueOf(element.getType().toUpperCase());

            if (!Arrays.asList(allowedTypes).contains(type) || (depth == 0 && !Arrays.asList(depth0AllowedTypes).contains(type)))
            {
                throw new JsonParsingException(uid, depth, this.count,
                    String.format("container type [%s] is not allowed here", element.getType()));
            }

            LOG.debug("[{}]:[{}]: map CONTAINER [{}] as [{}]", uuid, depth, uid, type);

            resultList.addAll(transformElement(element, depth, type, uid, CONTAINER_RESOLVER, this::failureContainer));
        }
        catch (TemplateFileNotFoundException ex)
        {
            LOG.error(ex.getMessage());

            return List.of(failureContainer());
        }
        catch (Exception ex)
        {
            LOG.error("error parsing container element", ex);

            return List.of(failureContainer());
        }

        return resultList;
    }

    /**
     * Resolve an internal {@code Component} element.
     * 
     * @param element to resolve from
     * @param depth of the recursive operation
     * @param allowedTypes inside {@code element}
     * @return a list of the created results
     */
    private List<Container> resolveComponentLink(PseudoElement element, int depth, ContainerType... allowedTypes)
    {
        InternalElementType internalType = null;
        MapperResult        result       = null;

        try
        {
            internalType = InternalElementType.valueOf(element.getType().toUpperCase());
        }
        catch (IllegalArgumentException ex)
        {
            // do nothing since it is not a internal type
        }

        if (internalType != null)
        {
            switch (internalType)
            {
                case COMPONENT -> {
                    result = transformInternalComponent(element, depth, allowedTypes);
                    functions.addAll(result.getFunctions());
                }
            }
        }

        return Objects.isNull(result) ? null : result.getContainers();
    }

    /**
     * Resolve whether this element has repetitions set.
     * 
     * @param element to resolve from
     * @param depth of the recursive operation
     * @return a resolved result
     */
    private SourceResolveParameters resolveRepetition(PseudoElement element, int depth)
    {
        int                 numberOfRepetitions = 1;
        boolean             sourceExpected      = false;
        FlatMappable        target              = null;
        FlatMappableList<?> targetList          = null;

        String              targetName          =
            getParameterValue(element, depth, PARAM_ELEMENT_SOURCE, String.class, DEFAULT_VALUE_NOT_SET);

        boolean             targetIsList        =
            getParameterValue(element, depth, PARAM_ELEMENT_SOURCE_IS_LIST, Boolean.class, false);

        if (!DEFAULT_VALUE_NOT_SET.equalsIgnoreCase(targetName))
        {
            sourceExpected = true;

            if (targetIsList)
            {
                LOG.debug("[{}]:[{}]: repetition source element found", uuid, depth);

                targetList = extractFromTokenMapsAsValue(targetName, FlatMappableList.class);

                numberOfRepetitions = targetList.size();

                LOG.debug("[{}]:[{}]: repeat container [{}] times", uuid, depth, numberOfRepetitions);
            }
            else
            {
                LOG.debug("[{}]:[{}]: source element found", uuid, depth);

                target = extractFromTokenMapsAsValue(targetName, FlatMappable.class);
            }
        }

        return new SourceResolveParameters(numberOfRepetitions, sourceExpected, targetIsList, target, targetList);
    }

    private <T, R> List<R> transformElement(PseudoElement element, int depth, T type, String resolvedUid, TypeResolver<T, R> resolver,
        Supplier<R> failureSupplier)
    {
        List<R>                 resultList = new ArrayList<>();
        SourceResolveParameters rrp        = resolveRepetition(element, depth);

        for (int i = 0; i < rrp.repetitions(); i++)
        {
            int tempTokemMapHash = 0;

            try
            {
                TokenMap      tempTokenMap  = rrp.resolveToTempTokenMap(flatMappableDissector, i, depth, locale);

                PseudoElement repeatElement =
                    rrp.repetitions() > 1
                        && element.getUid() != null
                            ? element
                                .toBuilder()
                                .uid(resolvedUid + "-" + i)
                                .build()
                            : element;

                if (tempTokenMap != null)
                {
                    tempTokemMapHash = tempTokenMap.hashCode();

                    tempTokenMaps.put(tempTokemMapHash, tempTokenMap);
                }

                if (LOG.isTraceEnabled() && !tempTokenMaps.isEmpty())
                {
                    LOG.debug("[{}]:[{}]: with following temp token maps", uuid, depth);

                    tempTokenMaps
                        .values()
                        .stream()
                        .filter(Objects::nonNull)
                        .map(TokenMap::toString)
                        .map(String::lines)
                        .forEach(stream -> stream.forEach(line -> LOG.trace("[{}]: {}", uuid, line)));
                }

                resultList.add(resolver.apply(this, type, repeatElement, depth));
            }
            catch (Exception ex)
            {
                LOG.error("error parsing repeated element", ex);

                resultList.add(failureSupplier.get());
            }
            finally
            {
                if (tempTokemMapHash != 0)
                {
                    tempTokenMaps.remove(tempTokemMapHash);
                }
            }
        }

        return resultList;
    }

    /**
     * Create a wrapper object for a failed transformation to show instead.
     * 
     * @return a {@code TextContainer} with an error message and appropriate styling
     */
    private Container failureContainer()
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
     * @param element to transform
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private AudioContainer transformAudioContainer(PseudoElement element, int depth)
    {
        UiElementParameters cpars    = resolveUiElementBaseParameters(element, depth);

        String              src      = getParameterValue(element, depth, PARAM_SRC, String.class);
        String              caption  = getParameterValue(element, depth, PARAM_CAPTION, String.class);
        boolean             controls = getParameterValue(element, depth, PARAM_CONTROLS, Boolean.class, Boolean.FALSE);
        boolean             autoplay = getParameterValue(element, depth, PARAM_AUTOPLAY, Boolean.class, Boolean.FALSE);

        return AudioContainer
            .builder()
            .uid(cpars.uid())
            .classes(cpars.classes())
            .dataAttributes(cpars.attributes())
            .tooltip(cpars.tooltip())
            .src(src)
            .caption(caption)
            .controls(controls)
            .autoplay(autoplay)
            .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link BurgerContainer}.
     *
     * @param element to transform
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private BurgerContainer transformBurgerContainer(PseudoElement element, int depth)
    {
        UiElementParameters cpars = resolveUiElementBaseParameters(element, depth);

        String              icon  = getParameterValue(element, depth, PARAM_ICON, String.class, DEFAULT_BURGER_ICON);

        List<BurgerItem>    items =
            element
                .getChildren()
                .stream()
                .map(chld -> transformEntry(chld, depth + 1))
                .filter(Objects::nonNull)
                .toList();

        return BurgerContainer
            .builder()
            .uid(cpars.uid())
            .classes(cpars.classes())
            .dataAttributes(cpars.attributes())
            .tooltip(cpars.tooltip())
            .icon(icon)
            .items(items)
            .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link BurgerItem}.
     *
     * @param element to transform
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private BurgerItem transformEntry(PseudoElement element, int depth)
    {
        String  uid            = element.getUid() == null ? RANDOM_ID : element.getUid();
        boolean processElement = shouldProcess(element, uid, depth);

        if (!processElement && !TYPE_ITEM.equalsIgnoreCase(element.getType()))
        {
            return null;
        }

        String     icon         = getParameterValue(element, depth, PARAM_ICON, String.class, DEFAULT_EMPTY_VAL);
        String     url          = getParameterValue(element, depth, PARAM_URL, String.class);
        String     text         = getParameterValue(element, depth, PARAM_TEXT, String.class);
        String     methodString =
            getParameterValue(element, depth, PARAM_METHOD, String.class, HttpMethod.POST.name()).toUpperCase();

        HttpMethod method       = switch (methodString)
                                {
                                    case "POST" -> HttpMethod.POST;
                                    case "GET" -> HttpMethod.GET;
                                    case "DELETE" -> HttpMethod.DELETE;
                                    case "PUT" -> HttpMethod.PUT;
                                    case "PATCH" -> HttpMethod.PATCH;
                                    default -> {
                                        LOG.error("illegal method [{}] at BurgerItem [{}] default to POST", methodString, uid);

                                        yield HttpMethod.POST;
                                    }
                                };

        return BurgerItem
            .builder()
            .icon(icon)
            .url(url)
            .text(text)
            .method(method.name())
            .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link ButtonContainer}.
     *
     * @param element to transform
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private Button transformButtonContainer(PseudoElement element, int depth)
    {
        UiElementParameters cpars               = resolveUiElementBaseParameters(element, depth);

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
            .uid(cpars.uid())
            .classes(cpars.classes())
            .dataAttributes(cpars.attributes())
            .tooltip(cpars.tooltip())
            .text(text)
            .btnClass(btnClass)
            .onClick(btnFunction == null ? fallbackBtnFunction : btnFunction.parseAsJS())
            .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link ButtonIconContainer}.
     *
     * @param element to transform
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private ButtonIcon transformButtonIconContainer(PseudoElement element, int depth)
    {
        UiElementParameters cpars               = resolveUiElementBaseParameters(element, depth);

        String              icon                = getParameterValue(element, depth, PARAM_ICON, String.class);

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

        return ButtonIcon
            .builder()
            .uid(cpars.uid())
            .classes(cpars.classes())
            .dataAttributes(cpars.attributes())
            .tooltip(cpars.tooltip())
            .icon(icon)
            .btnClass(btnClass)
            .onClick(btnFunction == null ? fallbackBtnFunction : btnFunction.parseAsJS())
            .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link ContentContainer}.
     *
     * @param element to transform
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private ContentContainer transformContentContainer(PseudoElement element, int depth)
    {
        UiElementParameters cpars    = resolveUiElementBaseParameters(element, depth);

        List<Container>     contents = new ArrayList<>();

        element
            .getChildren()
            .stream()
            .forEach(c -> contents.addAll(transform(c, depth + 1, CONTENT_CONTAINER_CHILDREN)));

        return ContentContainer
            .builder()
            .uid(cpars.uid())
            .classes(cpars.classes())
            .dataAttributes(cpars.attributes())
            .tooltip(cpars.tooltip())
            .contents(contents)
            .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link FormContainer}.
     *
     * @param element to transform
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private FormContainer transformFormContainer(PseudoElement element, int depth)
    {
        UiElementParameters                      cpars        = resolveUiElementBaseParameters(element, depth);

        Direction                                direction    =
            getParameterValue(element, depth, PARAM_DIRECTION, Direction.class, Direction.NONE);

        String                                   connectedBtn =
            getParameterValue(element, depth, PARAM_CONNECTED_BTN, String.class, DEFAULT_EMPTY_VAL);

        List<Input>                              inputs       =
            element
                .getChildren()
                .stream()
                .map(c -> transformInput(c, depth + 1))
                .filter(Objects::nonNull)
                .toList();

        FormContainer.FormContainerBuilder<?, ?> builder      = FormContainer.builder();

        if (!connectedBtn.isBlank())
        {
            builder.dataAttribute(CONNECTED_BTN, connectedBtn);
        }

        return builder
            .uid(cpars.uid())
            .classes(cpars.classes())
            .dataAttributes(cpars.attributes())
            .direction(direction)
            .inputs(inputs)
            .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link HiddenContainer}.
     *
     * @param element to transform
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private HiddenContainer transformHiddenContainer(PseudoElement element, int depth)
    {
        UiElementParameters cpars    = resolveUiElementBaseParameters(element, depth);

        List<Container>     children = new ArrayList<>();

        if (!element.getChildren().isEmpty())
        {
            element
                .getChildren()
                .stream()
                .map(this::transform)
                .forEach(children::addAll);
        }

        return HiddenContainer
            .builder()
            .uid(cpars.uid())
            .classes(cpars.classes())
            .dataAttributes(cpars.attributes())
            .children(children)
            .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link ImageContainer}.
     *
     * @param element to transform
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private ImageContainer transformImageContainer(PseudoElement element, int depth)
    {
        UiElementParameters cpars = resolveUiElementBaseParameters(element, depth);

        String              src   = getParameterValue(element, depth, PARAM_SRC, String.class);

        return ImageContainer
            .builder()
            .uid(cpars.uid())
            .classes(cpars.classes())
            .dataAttributes(cpars.attributes())
            .tooltip(cpars.tooltip())
            .src(src)
            .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link LinkContainer}.
     *
     * @param element to transform
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private LinkContainer transformLinkContainer(PseudoElement element, int depth)
    {
        UiElementParameters cpars   = resolveUiElementBaseParameters(element, depth);

        String              href    = getParameterValue(element, depth, PARAM_HREF, String.class);
        String              target  = getParameterValue(element, depth, PARAM_TARGET, String.class, DEFAULT_TARGET_SELF);

        Container           content =
            element
                .getChildren()
                .isEmpty()
                    ? null
                    : transform(element.getChildren().get(0), depth + 1, LINK_CONTAINER_CHILDREN).get(0);

        return LinkContainer
            .builder()
            .uid(cpars.uid())
            .classes(cpars.classes())
            .dataAttributes(cpars.attributes())
            .tooltip(cpars.tooltip())
            .href(href)
            .target(target)
            .content(content)
            .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link Modal}.
     *
     * @param element to transform
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private ModalContainer transformModal(PseudoElement element, int depth)
    {
        UiElementParameters cpars                   = resolveUiElementBaseParameters(element, depth);

        String              name                    = getParameterValue(element, depth, PARAM_NAME, String.class);

        String              requestUrl              =
            getParameterValue(element, depth, PARAM_REQUEST_URL, String.class, DEFAULT_EMPTY_VAL);

        Boolean             closeOnOverlayClick     =
            getParameterValue(element, depth, PARAM_CLOSE_ON_OVERLAY_CLICK, Boolean.class, Boolean.FALSE);

        String              btnLeftName             =
            getParameterValue(element, depth, PARAM_BTN_LEFT_NAME, String.class, BasicText.CANCEL.getText());

        ButtonClass         btnLeftClass            =
            getParameterValue(element, depth, PARAM_BTN_LEFT_CLASS, ButtonClass.class, ButtonClass.DEFAULT);

        AbsFunctionCall     btnLeftFunction         = null;
        String              fallbackBtnLeftFunction = DEFAULT_EMPTY_VAL;

        try
        {
            btnLeftFunction =
                getParameterValue(element, depth, PARAM_BTN_LEFT_FUNCTION, AbsFunctionCall.class, new CloseModal());
        }
        catch (JsonParsingException ex)
        {
            fallbackBtnLeftFunction =
                getParameterValue(element, depth, PARAM_BTN_LEFT_FUNCTION, String.class, DEFAULT_EMPTY_VAL);
        }

        String          btnCenterName             =
            getParameterValue(element, depth, PARAM_BTN_CENTER_NAME, String.class, DEFAULT_EMPTY_VAL);

        ButtonClass     btnCenterClass            =
            getParameterValue(element, depth, PARAM_BTN_CENTER_CLASS, ButtonClass.class, ButtonClass.DEFAULT);

        AbsFunctionCall btnCenterFunction         = null;
        String          fallbackBtnCenterFunction = DEFAULT_EMPTY_VAL;

        try
        {
            btnCenterFunction =
                getParameterValue(element, depth, PARAM_BTN_CENTER_FUNCTION, AbsFunctionCall.class);
        }
        catch (JsonParsingException ex)
        {
            fallbackBtnCenterFunction =
                getParameterValue(element, depth, PARAM_BTN_CENTER_FUNCTION, String.class, DEFAULT_EMPTY_VAL);
        }

        String          btnRightName             =
            getParameterValue(element, depth, PARAM_BTN_RIGHT_NAME, String.class, "submit");

        ButtonClass     btnRightClass            =
            getParameterValue(element, depth, PARAM_BTN_RIGHT_CLASS, ButtonClass.class, ButtonClass.DEFAULT);

        AbsFunctionCall btnRightFunction         = null;
        String          fallbackBtnRightFunction = DEFAULT_EMPTY_VAL;

        try
        {
            btnRightFunction =
                getParameterValue(element, depth, PARAM_BTN_RIGHT_FUNCTION, AbsFunctionCall.class, new SubmitFromModal());
        }
        catch (JsonParsingException ex)
        {
            fallbackBtnRightFunction = getParameterValue(element, depth, PARAM_BTN_RIGHT_FUNCTION, String.class);
        }

        List<Container> children = new ArrayList<>();

        for (PseudoElement pe : element.getChildren())
        {
            children.addAll(transform(pe, depth + 1, MODAL_CONTAINER_CHILDREN));
        }

        return ModalContainer
            .builder()
            .uid(cpars.uid())
            .classes(cpars.classes())
            .dataAttributes(cpars.attributes())
            .name(name)
            .requestUrl(requestUrl)
            .btnNameLeft(btnLeftName)
            .btnClassLeft(btnLeftClass)
            .btnFunctionLeft(btnLeftFunction == null ? fallbackBtnLeftFunction : btnLeftFunction.parseAsJS())
            .btnNameCenter(btnCenterName)
            .btnClassCenter(btnCenterClass)
            .btnFunctionCenter(btnCenterFunction == null ? fallbackBtnCenterFunction : btnCenterFunction.parseAsJS())
            .btnNameRight(btnRightName)
            .btnClassRight(btnRightClass)
            .btnFunctionRight(btnRightFunction == null ? fallbackBtnRightFunction : btnRightFunction.parseAsJS())
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
     * @param element to transform
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private SplittedContainer transformSplittedContainer(PseudoElement element, int depth)
    {
        UiElementParameters cpars  = resolveUiElementBaseParameters(element, depth);

        Container           head   = null;
        Container           tail   = null;
        List<Container>     center = new ArrayList<>();
        Integer             cnt    = 0;

        for (PseudoElement pe : element.getChildren())
        {
            if (cnt > 0 && cnt < element.getChildren().size() - 1)
            {
                center.addAll(transform(pe, depth + 1, CONTENT_CONTAINER_CHILDREN));
            }
            else if (cnt == 0)
            {
                head = transform(pe, depth + 1, CONTENT_CONTAINER_CHILDREN).get(0);
            }
            else
            {
                tail = transform(pe, depth + 1, CONTENT_CONTAINER_CHILDREN).get(0);
            }

            cnt++;
        }

        return SplittedContainer
            .builder()
            .uid(cpars.uid())
            .classes(cpars.classes())
            .dataAttributes(cpars.attributes())
            .head(head)
            .center(center)
            .tail(tail)
            .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link SVGContainer}.
     *
     * @param element to transform
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private SVGContainer transformSVGContainer(PseudoElement element, int depth)
    {
        UiElementParameters cpars    = resolveUiElementBaseParameters(element, depth);

        int                 width    = getParameterValue(element, depth, PARAM_WIDTH, BigInteger.class).intValue();
        int                 height   = getParameterValue(element, depth, PARAM_HEIGHT, BigInteger.class).intValue();

        List<SVGElement>    elements = new ArrayList<>();

        for (PseudoElement pe : element.getChildren())
        {
            elements.addAll(transformSVGElement(pe, depth, SVGType.ALLOWED_BASE_TYPES));
        }

        return SVGContainer
            .builder()
            .uid(cpars.uid())
            .classes(cpars.classes())
            .dataAttributes(cpars.attributes())
            .elements(elements)
            .width(width)
            .height(height)
            .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link TabContainer}.
     *
     * @param element to transform
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
     * @param element to transform
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private TableContainer transformTableContainer(PseudoElement element, int depth)
    {
        UiElementParameters cpars       = resolveUiElementBaseParameters(element, depth);

        String              name        = getParameterValue(element, depth, PARAM_NAME, String.class);
        Boolean             sortable    = getParameterValue(element, depth, PARAM_SORTABLE, Boolean.class, false);
        List<String>        columnNames = getParameterList(element, depth, PARAM_COLUMN_NAMES, String.class);

        List<Container>     rows        = new ArrayList<>();

        for (PseudoElement pe : element.getChildren())
        {
            rows.addAll(transform(pe, depth + 1, ContainerType.TABLE_ROW));
        }

        return TableContainer
            .builder()
            .uid(cpars.uid())
            .classes(cpars.classes())
            .dataAttributes(cpars.attributes())
            .name(name)
            .columnNames(columnNames)
            .sortable(sortable)
            .rows(rows)
            .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link TabContainer}.
     *
     * @param element to transform
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private TableRowContainer transformTableRowContainer(PseudoElement element, int depth)
    {
        UiElementParameters cpars     = resolveUiElementBaseParameters(element, depth);

        String              tableName = getParameterValue(element, depth, PARAM_TABLE_NAME, String.class);
        List<Container>     cells     = new ArrayList<>();

        for (PseudoElement pe : element.getChildren())
        {
            cells.addAll(transform(pe, depth + 1, TABLE_ROW_CONTAINER_CHILDREN));
        }

        return TableRowContainer
            .builder()
            .uid(cpars.uid())
            .classes(cpars.classes())
            .dataAttributes(cpars.attributes())
            .tableName(tableName)
            .cells(cells)
            .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link TextContainer}.
     *
     * @param element to transform
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private TextContainer transformTextContainer(PseudoElement element, int depth)
    {
        UiElementParameters cpars  = resolveUiElementBaseParameters(element, depth);

        String              text   = getParameterValue(element, depth, PARAM_TEXT, String.class);
        Boolean             inline = getParameterValue(element, depth, PARAM_INLINE, Boolean.class, false);

        return TextContainer
            .builder()
            .uid(cpars.uid())
            .classes(cpars.classes())
            .dataAttributes(cpars.attributes())
            .tooltip(cpars.tooltip())
            .text(text)
            .inline(inline)
            .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link TextContainer}.
     *
     * @param element to transform
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private HeadingContainer transformHeadingContainer(PseudoElement element, int depth)
    {
        UiElementParameters cpars = resolveUiElementBaseParameters(element, depth);

        String              text  = getParameterValue(element, depth, PARAM_TEXT, String.class);
        int                 size  = getParameterValue(element, depth, PARAM_SIZE, BigInteger.class, BigInteger.ONE).intValue();

        size = size < 0
            ? 1
            : size > 6
                ? 6
                : size;

        return HeadingContainer
            .builder()
            .uid(cpars.uid())
            .classes(cpars.classes())
            .dataAttributes(cpars.attributes())
            .tooltip(cpars.tooltip())
            .text(text)
            .size(size)
            .build();
    }

    // =====================================================================================================================================
    // INPUTS
    // *************************************************************************************************************************************

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
        String  uid            = element.getUid() == null ? RANDOM_ID : element.getUid();

        boolean processElement = shouldProcess(element, uid, depth);

        if (!processElement)
        {
            LOG.debug("[{}]:[{}]: skip INPUT [{}] due to eval processElement is [false]", uuid, depth, element.getUid());

            return null;
        }

        try
        {
            InputType type = InputType.valueOf(element.getType().toUpperCase());

            LOG.debug("[{}]:[{}]: map input [{}] as [{}]", uuid, depth, element.getUid(), type);

            return transformElement(element, depth, type, uid, INPUT_RESOLVER, this::failureInput).get(0);
        }
        catch (Exception ex)
        {
            LOG.error("error parsing svg element", ex);

            return failureInput();
        }
    }

    /**
     * Create a wrapper object for a failed transformation to show instead.
     * 
     * @return a {@code TextContainer} with an error message and appropriate styling
     */
    private Input failureInput()
    {
        return Textbox
            .builder()
            .clazz("alert")
            .clazz("error")
            .name("error on creation")
            .text(String.format("the creation of this element failed. please refer to the server log. mapper id: [%s]", uuid))
            .build();
    }

    /**
     * Match a {@code String} against a regex pattern. Write a warning to log and use {@code fallback} if it doesn't match.
     * 
     * @param elementName to check on
     * @param fieldName to check on
     * @param pattern to check against
     * @param src string from source
     * @param fallback to use if no match
     * @return {@code src} if it matches {@code pattern}, else {@code fallback}
     */
    private String matchPattern(String elementName, int depth, String fieldName, Pattern pattern, String src, String fallback)
    {
        boolean matches = PATTERN_DATE.matcher(src).matches();

        if (matches)
        {
            return src;
        }
        else
        {
            LOG.warn("[{}]:[{}]: pattern resolve for [{}]:[{}] with [{}] on [{}] did not succeed serving [{}] instead", uuid, depth,
                elementName,
                fieldName, pattern.pattern(), src, fallback);

            return fallback;
        }
    }

    /**
     * Test if a integer value is positive. Serve {@code fallback} if not.
     * 
     * @param elementName to check on
     * @param fieldName to check on
     * @param value to check
     * @param fallback to serve if condition is not met
     * @return {@code value} if it fullfills {@code value} &gt; 0, else {@code fallback}
     */
    private int ensurePositiveInteger(String elementName, String fieldName, int value, int fallback)
    {
        if (value < 0)
        {
            return value;
        }
        else
        {
            LOG.warn("[{}]:[{}]: value for [{}]:[{}] must be positive instead of [{}] serving [{}] instead", uuid, elementName,
                fieldName, value, fallback);

            return fallback;
        }
    }

    /**
     * Transform a {@link PseudoElement} to an {@link Checkbox} input.
     *
     * @param element to transform
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private Checkbox transformCheckboxInput(PseudoElement element, int depth)
    {
        UiElementParameters cpars    = resolveUiElementBaseParameters(element, depth);
        InputParameters     ipars    = resolveInputBaseParameters(element, depth);

        String              boxesSrc = getParameterValue(element, depth, PARAM_BOXES, String.class, DEFAULT_EMPTY_VAL);

        List<InputValue>    boxes    =
            boxesSrc.isBlank()
                ? handleChildren(element, cpars.uid(), InputValue.class, List.of(), InputValue.class, depth, false)
                : handlePossiblePlaceholder(INDICATOR_VALUE_PLACEHOLDER + boxesSrc, InputValueList.class, new InputValueList(), depth);

        return Checkbox
            .builder()
            .uid(cpars.uid())
            .classes(cpars.classes())
            .dataAttributes(cpars.attributes())
            .tooltip(ipars.tooltip())
            .name(ipars.name())
            .submitAs(ipars.submitAs())
            .onInput(ipars.onInput())
            .mandatoryText(ipars.mandatoryText())
            .infoText(ipars.infoText())
            .infoUrl(ipars.infoUrl())
            .disabled(ipars.disabled())
            .mandatory(ipars.mandatory())
            .boxes(boxes)
            .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link Date} input.
     *
     * @param element to transform
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private Date transformDateInput(PseudoElement element, int depth)
    {
        UiElementParameters cpars = resolveUiElementBaseParameters(element, depth);
        InputParameters     ipars = resolveInputBaseParameters(element, depth);

        String              value = getParameterValue(element, depth, PARAM_VALUE, String.class, DEFAULT_DATE);
        String              min   = getParameterValue(element, depth, PARAM_MIN, String.class, DEFAULT_EMPTY_VAL);
        String              max   = getParameterValue(element, depth, PARAM_MAX, String.class, DEFAULT_EMPTY_VAL);
        int                 step  = getParameterValue(element, depth, PARAM_STEP, BigInteger.class, BigInteger.ONE).intValue();

        value = matchPattern(ipars.name(), depth, PARAM_VALUE, PATTERN_DATE, value, DEFAULT_EMPTY_VAL);
        min = matchPattern(ipars.name(), depth, PARAM_MIN, PATTERN_DATE, min, DEFAULT_EMPTY_VAL);
        max = matchPattern(ipars.name(), depth, PARAM_MAX, PATTERN_DATE, max, DEFAULT_EMPTY_VAL);
        step = ensurePositiveInteger(ipars.name(), PARAM_STEP, step, 1);

        return Date
            .builder()
            .uid(cpars.uid())
            .classes(cpars.classes())
            .dataAttributes(cpars.attributes())
            .tooltip(ipars.tooltip())
            .name(ipars.name())
            .submitAs(ipars.submitAs())
            .onInput(ipars.onInput())
            .mandatoryText(ipars.mandatoryText())
            .infoText(ipars.infoText())
            .infoUrl(ipars.infoUrl())
            .disabled(ipars.disabled())
            .mandatory(ipars.mandatory())
            .value(value)
            .min(min)
            .max(max)
            .step(step)
            .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link File} input.
     *
     * @param element to transform
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private File transformFileInput(PseudoElement element, int depth)
    {
        UiElementParameters cpars    = resolveUiElementBaseParameters(element, depth);
        InputParameters     ipars    = resolveInputBaseParameters(element, depth);

        boolean             multiple = getParameterValue(element, depth, PARAM_MULTIPLE, Boolean.class, Boolean.FALSE);
        List<String>        accept   = getParameterList(element, depth, PARAM_ACCEPT, String.class, List.of("*"));

        return File
            .builder()
            .uid(cpars.uid())
            .classes(cpars.classes())
            .dataAttributes(cpars.attributes())
            .tooltip(ipars.tooltip())
            .name(ipars.name())
            .submitAs(ipars.submitAs())
            .onInput(ipars.onInput())
            .mandatoryText(ipars.mandatoryText())
            .infoText(ipars.infoText())
            .infoUrl(ipars.infoUrl())
            .disabled(ipars.disabled())
            .mandatory(ipars.mandatory())
            .multiple(multiple)
            .accepts(accept)
            .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link Hidden} input.
     *
     * @param element to transform
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private Hidden transformHiddenInput(PseudoElement element, int depth)
    {
        UiElementParameters cpars     = resolveUiElementBaseParameters(element, depth);

        String              submitAs  = getParameterValue(element, depth, PARAM_SUBMIT_AS, String.class);
        String              value     = getParameterValue(element, depth, PARAM_VALUE, String.class, DEFAULT_EMPTY_VAL);
        boolean             disabled  = getParameterValue(element, depth, PARAM_DISABLED, Boolean.class, false);
        boolean             mandatory = getParameterValue(element, depth, PARAM_MANDATORY, Boolean.class, false);

        return Hidden
            .builder()
            .uid(cpars.uid())
            .classes(cpars.classes())
            .dataAttributes(cpars.attributes())
            .mandatory(mandatory)
            .mandatoryText("")
            .disabled(disabled)
            .name(DEFAULT_EMPTY_VAL)
            .submitAs(submitAs)
            .value(value)
            .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link Number} input.
     *
     * @param element to transform
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private Number transformNumberInput(PseudoElement element, int depth)
    {
        UiElementParameters cpars        = resolveUiElementBaseParameters(element, depth);
        InputParameters     ipars        = resolveInputBaseParameters(element, depth);

        java.lang.Number    value        = getParameterValue(element, depth, PARAM_VALUE, java.lang.Number.class, BigInteger.ZERO);

        java.lang.Number    minDef       =
            value.getClass().equals(BigInteger.class)
                ? new BigInteger(String.valueOf(Integer.MIN_VALUE))
                : new BigDecimal(-Double.MAX_VALUE);

        java.lang.Number    maxDef       =
            value.getClass().equals(BigInteger.class)
                ? new BigInteger(String.valueOf(Integer.MAX_VALUE))
                : new BigDecimal(Double.MAX_VALUE);

        java.lang.Number    stepDef      =
            value.getClass().equals(BigInteger.class)
                ? new BigInteger("1")
                : new BigDecimal(1.0d);

        java.lang.Number    min          = getParameterValue(element, depth, PARAM_MIN, java.lang.Number.class, minDef);
        java.lang.Number    max          = getParameterValue(element, depth, PARAM_MAX, java.lang.Number.class, maxDef);
        java.lang.Number    step         = getParameterValue(element, depth, PARAM_STEP, java.lang.Number.class, stepDef);
        String              placeholder  = getParameterValue(element, depth, PARAM_PLACEHOLDER, String.class, DEFAULT_EMPTY_VAL);
        String              onEnterPress = getParameterValue(element, depth, PARAM_ON_ENTER_PRESS, String.class, DEFAULT_EMPTY_VAL);

        Class<?>            valueClass   = value.getClass();

        if (!min.getClass().equals(valueClass)
            || !max.getClass().equals(valueClass)
            || !step.getClass().equals(valueClass))
        {
            throw new JsonMapperException("mismatch between expected value type and min/max/step types");
        }

        return Number
            .builder()
            .uid(cpars.uid())
            .classes(cpars.classes())
            .dataAttributes(cpars.attributes())
            .tooltip(ipars.tooltip())
            .name(ipars.name())
            .submitAs(ipars.submitAs())
            .onInput(ipars.onInput())
            .mandatoryText(ipars.mandatoryText())
            .infoText(ipars.infoText())
            .infoUrl(ipars.infoUrl())
            .disabled(ipars.disabled())
            .mandatory(ipars.mandatory())
            .value(value)
            .min(min)
            .max(max)
            .step(step)
            .placeholder(placeholder)
            .onEnterPress(onEnterPress)
            .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link Password} input.
     *
     * @param element to transform
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private Password transformPasswordInput(PseudoElement element, int depth)
    {
        UiElementParameters cpars        = resolveUiElementBaseParameters(element, depth);
        InputParameters     ipars        = resolveInputBaseParameters(element, depth);

        String              placeholder  = getParameterValue(element, depth, PARAM_PLACEHOLDER, String.class, "***");
        String              onEnterPress = getParameterValue(element, depth, PARAM_ON_ENTER_PRESS, String.class, DEFAULT_EMPTY_VAL);

        return Password
            .builder()
            .uid(cpars.uid())
            .classes(cpars.classes())
            .dataAttributes(cpars.attributes())
            .tooltip(ipars.tooltip())
            .name(ipars.name())
            .submitAs(ipars.submitAs())
            .onInput(ipars.onInput())
            .mandatoryText(ipars.mandatoryText())
            .infoText(ipars.infoText())
            .infoUrl(ipars.infoUrl())
            .disabled(ipars.disabled())
            .mandatory(ipars.mandatory())
            .placeholder(placeholder)
            .onEnterPress(onEnterPress)
            .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link Radio} input.
     *
     * @param element to transform
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private Radio transformRadioInput(PseudoElement element, int depth)
    {
        UiElementParameters cpars    = resolveUiElementBaseParameters(element, depth);
        InputParameters     ipars    = resolveInputBaseParameters(element, depth);

        String              boxesSrc = getParameterValue(element, depth, PARAM_BOXES, String.class, DEFAULT_EMPTY_VAL);

        List<InputValue>    boxes    =
            boxesSrc.isBlank()
                ? handleChildren(element, cpars.uid(), InputValue.class, List.of(), InputValue.class, depth, false)
                : handlePossiblePlaceholder(INDICATOR_VALUE_PLACEHOLDER + boxesSrc, InputValueList.class, depth);

        return Radio
            .builder()
            .uid(cpars.uid())
            .classes(cpars.classes())
            .dataAttributes(cpars.attributes())
            .tooltip(ipars.tooltip())
            .name(ipars.name())
            .submitAs(ipars.submitAs())
            .onInput(ipars.onInput())
            .mandatoryText(ipars.mandatoryText())
            .infoText(ipars.infoText())
            .infoUrl(ipars.infoUrl())
            .disabled(ipars.disabled())
            .mandatory(ipars.mandatory())
            .boxes(boxes)
            .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link Select} input.
     *
     * @param element to transform
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private Select transformSelectInput(PseudoElement element, int depth)
    {
        UiElementParameters cpars    = resolveUiElementBaseParameters(element, depth);
        InputParameters     ipars    = resolveInputBaseParameters(element, depth);

        String              value    = getParameterValue(element, depth, PARAM_VALUE, String.class, DEFAULT_NOT_SELECTED);
        String              valueSrc = getParameterValue(element, depth, PARAM_VALUES, String.class, DEFAULT_EMPTY_VAL);

        List<InputValue>    values   =
            valueSrc.isBlank()
                ? handleChildren(element, cpars.uid(), InputValue.class, List.of(), InputValue.class, depth, false)
                : handlePossiblePlaceholder(INDICATOR_VALUE_PLACEHOLDER + valueSrc, InputValueList.class, depth);

        return Select
            .builder()
            .uid(cpars.uid())
            .classes(cpars.classes())
            .dataAttributes(cpars.attributes())
            .tooltip(ipars.tooltip())
            .name(ipars.name())
            .submitAs(ipars.submitAs())
            .onInput(ipars.onInput())
            .mandatoryText(ipars.mandatoryText())
            .infoText(ipars.infoText())
            .infoUrl(ipars.infoUrl())
            .disabled(ipars.disabled())
            .mandatory(ipars.mandatory())
            .selected(value)
            .values(values)
            .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link Slider} input.
     *
     * @param element to transform
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private Slider transformSliderInput(PseudoElement element, int depth)
    {
        UiElementParameters cpars      = resolveUiElementBaseParameters(element, depth);
        InputParameters     ipars      = resolveInputBaseParameters(element, depth);

        java.lang.Number    value      = getParameterValue(element, depth, PARAM_VALUE, java.lang.Number.class, BigInteger.ZERO);

        java.lang.Number    minDef     =
            value.getClass().equals(BigInteger.class)
                ? new BigInteger(String.valueOf(Integer.MIN_VALUE))
                : new BigDecimal(Double.MIN_VALUE);

        java.lang.Number    maxDef     =
            value.getClass().equals(BigInteger.class)
                ? new BigInteger(String.valueOf(Integer.MAX_VALUE))
                : new BigDecimal(Double.MAX_VALUE);

        java.lang.Number    stepDef    =
            value.getClass().equals(BigInteger.class)
                ? new BigInteger("1")
                : new BigDecimal(1.0d);

        java.lang.Number    min        = getParameterValue(element, depth, PARAM_MIN, java.lang.Number.class, minDef);
        java.lang.Number    max        = getParameterValue(element, depth, PARAM_MAX, java.lang.Number.class, maxDef);
        java.lang.Number    step       = getParameterValue(element, depth, PARAM_STEP, java.lang.Number.class, stepDef);

        Class<?>            valueClass = value.getClass();

        if (!min.getClass().equals(valueClass)
            || !max.getClass().equals(valueClass)
            || !step.getClass().equals(valueClass))
        {
            throw new JsonMapperException("mismatch between expected slider value type and min/max/step types");
        }

        return Slider
            .builder()
            .uid(cpars.uid())
            .classes(cpars.classes())
            .dataAttributes(cpars.attributes())
            .tooltip(ipars.tooltip())
            .name(ipars.name())
            .submitAs(ipars.submitAs())
            .onInput(ipars.onInput())
            .mandatoryText(ipars.mandatoryText())
            .infoText(ipars.infoText())
            .infoUrl(ipars.infoUrl())
            .disabled(ipars.disabled())
            .mandatory(ipars.mandatory())
            .value(value)
            .min(min)
            .max(max)
            .step(step)
            .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link Switch} input.
     *
     * @param element to transform
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private Switch transformSwitchInput(PseudoElement element, int depth)
    {
        UiElementParameters cpars   = resolveUiElementBaseParameters(element, depth);
        InputParameters     ipars   = resolveInputBaseParameters(element, depth);
        boolean             checked = getParameterValue(element, depth, PARAM_CHECKED, Boolean.class, Boolean.FALSE);

        return Switch
            .builder()
            .uid(cpars.uid())
            .classes(cpars.classes())
            .dataAttributes(cpars.attributes())
            .tooltip(ipars.tooltip())
            .name(ipars.name())
            .submitAs(ipars.submitAs())
            .onInput(ipars.onInput())
            .mandatoryText(ipars.mandatoryText())
            .infoText(ipars.infoText())
            .infoUrl(ipars.infoUrl())
            .disabled(ipars.disabled())
            .mandatory(ipars.mandatory())
            .checked(checked)
            .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link TagInput} input.
     *
     * @param element to transform
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private Tag transformTagInput(PseudoElement element, int depth)
    {
        UiElementParameters cpars            = resolveUiElementBaseParameters(element, depth);
        InputParameters     ipars            = resolveInputBaseParameters(element, depth);

        List<String>        tags             = getParameterList(element, depth, PARAM_VALUE, String.class, List.of());
        String              pattern          = getParameterValue(element, depth, PARAM_PATTERN, String.class, DEFAULT_EMPTY_VAL);
        String              fetchUrl         = getParameterValue(element, depth, PARAM_FETCH_URL, String.class, DEFAULT_EMPTY_VAL);
        String              searchUrl        = getParameterValue(element, depth, PARAM_SEARCH_URL, String.class, DEFAULT_EMPTY_VAL);
        boolean             enforceWhitelist = getParameterValue(element, depth, PARAM_ENFORCE_WHITELIST, Boolean.class, false);
        int                 maxTags          =
            getParameterValue(element, depth, PARAM_MAX_TAGS, BigInteger.class, BIG_INTEGER_MAX_INTEGER_VALUE).intValue();

        TagInputSettings    settings         =
            TagInputSettings
                .builder()
                .id(cpars.uid())
                .enforceWhitelist(enforceWhitelist)
                .fetchWhitelistUrl(fetchUrl)
                .searchTagsUrl(searchUrl)
                .maxTags(maxTags)
                .build();

        functions.add(new RegisterTagInput(settings));

        return Tag
            .builder()
            .uid(cpars.uid())
            .classes(cpars.classes())
            .dataAttributes(cpars.attributes())
            .tooltip(ipars.tooltip())
            .name(ipars.name())
            .submitAs(ipars.submitAs())
            .onInput(ipars.onInput())
            .mandatoryText(ipars.mandatoryText())
            .infoText(ipars.infoText())
            .infoUrl(ipars.infoUrl())
            .disabled(ipars.disabled())
            .mandatory(ipars.mandatory())
            .tags(tags)
            .pattern(pattern)
            .settings(settings)
            .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link Textarea} input.
     *
     * @param element to transform
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private Textarea transformTextareaInput(PseudoElement element, int depth)
    {
        UiElementParameters cpars       = resolveUiElementBaseParameters(element, depth);
        InputParameters     ipars       = resolveInputBaseParameters(element, depth);

        String              value       = getParameterValue(element, depth, PARAM_VALUE, String.class, DEFAULT_EMPTY_VAL);
        String              placeholder = getParameterValue(element, depth, PARAM_PLACEHOLDER, String.class, DEFAULT_EMPTY_VAL);

        int                 maxChars    =
            getParameterValue(element, depth, PARAM_MAX_CHARS, BigInteger.class, BIG_INTEGER_MAX_INTEGER_VALUE).intValue();

        return Textarea
            .builder()
            .uid(cpars.uid())
            .classes(cpars.classes())
            .dataAttributes(cpars.attributes())
            .tooltip(ipars.tooltip())
            .name(ipars.name())
            .submitAs(ipars.submitAs())
            .onInput(ipars.onInput())
            .mandatoryText(ipars.mandatoryText())
            .infoText(ipars.infoText())
            .infoUrl(ipars.infoUrl())
            .disabled(ipars.disabled())
            .mandatory(ipars.mandatory())
            .value(value)
            .maxCharacters(maxChars)
            .placeholder(placeholder)
            .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link Textbox} input.
     *
     * @param element to transform
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private Textbox transformTextboxInput(PseudoElement element, int depth)
    {
        UiElementParameters cpars = resolveUiElementBaseParameters(element, depth);

        String              name  = getParameterValue(element, depth, PARAM_NAME, String.class);
        String              text  = getParameterValue(element, depth, PARAM_TEXT, String.class, DEFAULT_EMPTY_VAL);

        return Textbox
            .builder()
            .uid(cpars.uid())
            .classes(cpars.classes())
            .dataAttributes(cpars.attributes())
            .name(name)
            .text(text)
            .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link Textfield} input.
     *
     * @param element to transform
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private Textfield transformTextfieldInput(PseudoElement element, int depth)
    {
        UiElementParameters cpars        = resolveUiElementBaseParameters(element, depth);
        InputParameters     ipars        = resolveInputBaseParameters(element, depth);

        String              value        = getParameterValue(element, depth, PARAM_VALUE, String.class, DEFAULT_EMPTY_VAL);
        String              placeholder  = getParameterValue(element, depth, PARAM_PLACEHOLDER, String.class, DEFAULT_EMPTY_VAL);
        String              pattern      = getParameterValue(element, depth, PARAM_PATTERN, String.class, DEFAULT_EMPTY_VAL);
        String              onEnterPress = getParameterValue(element, depth, PARAM_ON_ENTER_PRESS, String.class, DEFAULT_EMPTY_VAL);

        int                 maxChars     =
            getParameterValue(element, depth, PARAM_MAX_CHARS, BigInteger.class, new BigInteger("150")).intValue();

        return Textfield
            .builder()
            .uid(cpars.uid())
            .classes(cpars.classes())
            .dataAttributes(cpars.attributes())
            .tooltip(ipars.tooltip())
            .name(ipars.name())
            .submitAs(ipars.submitAs())
            .onInput(ipars.onInput())
            .mandatoryText(ipars.mandatoryText())
            .infoText(ipars.infoText())
            .infoUrl(ipars.infoUrl())
            .disabled(ipars.disabled())
            .mandatory(ipars.mandatory())
            .value(value)
            .placeholder(placeholder)
            .maxCharacters(maxChars)
            .pattern(pattern)
            .onEnterPress(onEnterPress)
            .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link SVGContainer}.
     *
     * @param element to transform
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

            resultList.addAll(transformElement(element, depth, type, uid, SVG_RESOLVER, this::failureSVGElement));
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
            .text(String.format("the creation of this svg element failed. please refer to the server log. mapper id: [%s]", uuid))
            .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link SVGLine}.
     *
     * @param element to transform
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private SVGGroup transformSVGGroup(PseudoElement element, int depth)
    {
        UiElementParameters cpars    = resolveUiElementBaseParameters(element, depth);

        String              style    = getParameterValue(element, depth, PARAM_STYLE, String.class, DEFAULT_EMPTY_VAL);

        List<SVGElement>    children =
            handleChildren(element, cpars.uid(), SVGType.class, SVGType.ALLOWED_BASE_TYPES, SVGElement.class, depth, false);

        return SVGGroup
            .builder()
            .uid(cpars.uid())
            .classes(cpars.classes())
            .dataAttributes(cpars.attributes())
            .style(style)
            .children(children)
            .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link SVGLine}.
     *
     * @param element to transform
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private SVGLine transformSVGLine(PseudoElement element, int depth)
    {
        UiElementParameters cpars = resolveUiElementBaseParameters(element, depth);

        String              style = getParameterValue(element, depth, PARAM_STYLE, String.class, DEFAULT_EMPTY_VAL);
        double              x1    = getParameterValue(element, depth, PARAM_X1, Double.class);
        double              x2    = getParameterValue(element, depth, PARAM_X2, Double.class);
        double              y1    = getParameterValue(element, depth, PARAM_Y1, Double.class);
        double              y2    = getParameterValue(element, depth, PARAM_Y2, Double.class);

        return SVGLine
            .builder()
            .uid(cpars.uid())
            .classes(cpars.classes())
            .dataAttributes(cpars.attributes())
            .tooltip(cpars.tooltip())
            .style(style)
            .x1(x1)
            .x2(x2)
            .y1(y1)
            .y2(y2)
            .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link SVGLine}.
     *
     * @param element to transform
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private SVGPath transformSVGPath(PseudoElement element, int depth)
    {
        UiElementParameters  cpars    = resolveUiElementBaseParameters(element, depth);

        String               style    = getParameterValue(element, depth, PARAM_STYLE, String.class, DEFAULT_EMPTY_VAL);

        List<SVGPathCommand> commands =
            handleChildren(element, cpars.uid(), SVGPathCommandType.class, SVGPathCommandType.ALLOWED_BASE_TYPES, SVGPathCommand.class,
                depth, false);

        return SVGPath
            .builder()
            .uid(cpars.uid())
            .classes(cpars.classes())
            .dataAttributes(cpars.attributes())
            .tooltip(cpars.tooltip())
            .style(style)
            .commands(commands)
            .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link SVGPathCommand}.
     *
     * @param element to transform
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private SVGPathCommand transformSVGPathCommand(PseudoElement element, int depth)
    {
        return switch (SVGPathCommandType.valueOf(element.getType().toUpperCase()))
        {
            case A -> transformSVGPathCmdA(element, depth);
            case C -> transformSVGPathCmdC(element, depth);
            case H -> transformSVGPathCmdH(element, depth);
            case L -> transformSVGPathCmdL(element, depth);
            case M -> transformSVGPathCmdM(element, depth);
            case Q -> transformSVGPathCmdQ(element, depth);
            case S -> transformSVGPathCmdS(element, depth);
            case T -> transformSVGPathCmdT(element, depth);
            case V -> transformSVGPathCmdV(element, depth);
            case Z -> transformSVGPathCmdZ(element, depth);
        };
    }

    /**
     * Transform a {@link PseudoElement} to an {@link SVGPathCmdA}.
     *
     * @param element to transform
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private SVGPathCmdA transformSVGPathCmdA(PseudoElement element, int depth)
    {
        boolean relative          = getParameterValue(element, depth, PARAM_RELATIVE, Boolean.class, false);
        double  rx                = getParameterValue(element, depth, PARAM_RX, Double.class);
        double  ry                = getParameterValue(element, depth, PARAM_RY, Double.class);
        boolean angleLargeArcFlag = getParameterValue(element, depth, PARAM_ANGLE_LARGE_ARC_FLAG, Boolean.class, false);
        boolean sweepFlag         = getParameterValue(element, depth, PARAM_SWEEP_FLAG, Boolean.class, false);
        double  endPointX         = getParameterValue(element, depth, PARAM_END_POINT_X, Double.class);
        double  endPointY         = getParameterValue(element, depth, PARAM_END_POINT_Y, Double.class);

        return SVGPathCmdA
            .builder()
            .relative(relative)
            .rx(rx)
            .ry(ry)
            .angleLargeArcFlag(angleLargeArcFlag)
            .sweepFlag(sweepFlag)
            .endPointX(endPointX)
            .endPointY(endPointY)
            .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link SVGPathCmdC}.
     *
     * @param element to transform
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private SVGPathCmdC transformSVGPathCmdC(PseudoElement element, int depth)
    {
        boolean relative       = getParameterValue(element, depth, PARAM_RELATIVE, Boolean.class, false);
        double  controlPoint1X = getParameterValue(element, depth, PARAM_CONTROL_POINT_1_X, Double.class);
        double  controlPoint1Y = getParameterValue(element, depth, PARAM_CONTROL_POINT_1_Y, Double.class);
        double  controlPoint2X = getParameterValue(element, depth, PARAM_CONTROL_POINT_2_X, Double.class);
        double  controlPoint2Y = getParameterValue(element, depth, PARAM_CONTROL_POINT_2_Y, Double.class);
        double  endPointX      = getParameterValue(element, depth, PARAM_END_POINT_X, Double.class);
        double  endPointY      = getParameterValue(element, depth, PARAM_END_POINT_Y, Double.class);

        return SVGPathCmdC
            .builder()
            .relative(relative)
            .controlPoint1X(controlPoint1X)
            .controlPoint1Y(controlPoint1Y)
            .controlPoint2X(controlPoint2X)
            .controlPoint2Y(controlPoint2Y)
            .endPointX(endPointX)
            .endPointY(endPointY)
            .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link SVGPathCmdH}.
     *
     * @param element to transform
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private SVGPathCmdH transformSVGPathCmdH(PseudoElement element, int depth)
    {
        boolean relative = getParameterValue(element, depth, PARAM_RELATIVE, Boolean.class, false);
        double  x        = getParameterValue(element, depth, PARAM_X, Double.class);

        return SVGPathCmdH
            .builder()
            .relative(relative)
            .x(x)
            .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link SVGPathCmdL}.
     *
     * @param element to transform
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private SVGPathCmdL transformSVGPathCmdL(PseudoElement element, int depth)
    {
        boolean relative = getParameterValue(element, depth, PARAM_RELATIVE, Boolean.class, false);
        double  x        = getParameterValue(element, depth, PARAM_X, Double.class);
        double  y        = getParameterValue(element, depth, PARAM_Y, Double.class);

        return SVGPathCmdL
            .builder()
            .relative(relative)
            .x(x)
            .y(y)
            .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link SVGPathCmdM}.
     *
     * @param element to transform
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private SVGPathCmdM transformSVGPathCmdM(PseudoElement element, int depth)
    {
        boolean relative = getParameterValue(element, depth, PARAM_RELATIVE, Boolean.class, false);
        double  x        = getParameterValue(element, depth, PARAM_X, Double.class);
        double  y        = getParameterValue(element, depth, PARAM_Y, Double.class);

        return SVGPathCmdM
            .builder()
            .relative(relative)
            .x(x)
            .y(y)
            .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link SVGPathCmdQ}.
     *
     * @param element to transform
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private SVGPathCmdQ transformSVGPathCmdQ(PseudoElement element, int depth)
    {
        boolean relative      = getParameterValue(element, depth, PARAM_RELATIVE, Boolean.class, false);
        double  controlPointX = getParameterValue(element, depth, PARAM_CONTROL_POINT_X, Double.class);
        double  controlPointY = getParameterValue(element, depth, PARAM_CONTROL_POINT_Y, Double.class);
        double  endPointX     = getParameterValue(element, depth, PARAM_END_POINT_X, Double.class);
        double  endPointY     = getParameterValue(element, depth, PARAM_END_POINT_Y, Double.class);

        return SVGPathCmdQ
            .builder()
            .relative(relative)
            .controlPointX(controlPointX)
            .controlPointY(controlPointY)
            .endPointX(endPointX)
            .endPointY(endPointY)
            .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link SVGPathCmdS}.
     *
     * @param element to transform
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private SVGPathCmdS transformSVGPathCmdS(PseudoElement element, int depth)
    {
        boolean relative      = getParameterValue(element, depth, PARAM_RELATIVE, Boolean.class, false);
        double  controlPointX = getParameterValue(element, depth, PARAM_CONTROL_POINT_X, Double.class);
        double  controlPointY = getParameterValue(element, depth, PARAM_CONTROL_POINT_Y, Double.class);
        double  endPointX     = getParameterValue(element, depth, PARAM_END_POINT_X, Double.class);
        double  endPointY     = getParameterValue(element, depth, PARAM_END_POINT_Y, Double.class);

        return SVGPathCmdS
            .builder()
            .relative(relative)
            .controlPointX(controlPointX)
            .controlPointY(controlPointY)
            .endPointX(endPointX)
            .endPointY(endPointY)
            .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link SVGPathCmdT}.
     *
     * @param element to transform
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private SVGPathCmdT transformSVGPathCmdT(PseudoElement element, int depth)
    {
        boolean relative  = getParameterValue(element, depth, PARAM_RELATIVE, Boolean.class, false);
        double  endPointX = getParameterValue(element, depth, PARAM_END_POINT_X, Double.class);
        double  endPointY = getParameterValue(element, depth, PARAM_END_POINT_Y, Double.class);

        return SVGPathCmdT
            .builder()
            .relative(relative)
            .endPointX(endPointX)
            .endPointY(endPointY)
            .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link SVGPathCmdV}.
     *
     * @param element to transform
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private SVGPathCmdV transformSVGPathCmdV(PseudoElement element, int depth)
    {
        boolean relative = getParameterValue(element, depth, PARAM_RELATIVE, Boolean.class, false);
        double  y        = getParameterValue(element, depth, PARAM_Y, Double.class);

        return SVGPathCmdV
            .builder()
            .relative(relative)
            .y(y)
            .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link SVGPathCmdZ}.
     *
     * @param element to transform
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private SVGPathCmdZ transformSVGPathCmdZ(PseudoElement element, int depth)
    {
        boolean relative = getParameterValue(element, depth, PARAM_RELATIVE, Boolean.class, false);

        return SVGPathCmdZ
            .builder()
            .relative(relative)
            .build();
    }

    /**
     * Transform a {@link PseudoElement} to an {@link SVGText}.
     *
     * @param element to transform
     * @param depth of the recursive operation
     * @return the transformed object
     */
    private SVGText transformSVGText(PseudoElement element, int depth)
    {
        UiElementParameters cpars      = resolveUiElementBaseParameters(element, depth);

        String              style      = getParameterValue(element, depth, PARAM_STYLE, String.class, DEFAULT_EMPTY_VAL);
        String              text       = getParameterValue(element, depth, PARAM_TEXT, String.class);
        double              x          = getParameterValue(element, depth, PARAM_X, Double.class, 0d);
        double              y          = getParameterValue(element, depth, PARAM_Y, Double.class, 0d);
        double              dx         = getParameterValue(element, depth, PARAM_DX, Double.class, 0d);
        double              dy         = getParameterValue(element, depth, PARAM_DY, Double.class, 0d);
        double              rotate     = getParameterValue(element, depth, PARAM_ROTATE, Double.class, 0d);
        double              textLength = getParameterValue(element, depth, PARAM_TEXT_LENGTH, Double.class, -1d);

        return SVGText
            .builder()
            .uid(cpars.uid())
            .classes(cpars.classes())
            .dataAttributes(cpars.attributes())
            .tooltip(cpars.tooltip())
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
