/**
 * Copyright (c) 2016-2026 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.hmi.mapper.html;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.apache.commons.text.StringEscapeUtils;

import de.cookindustries.lib.spring.gui.hmi.container.AudioContainer;
import de.cookindustries.lib.spring.gui.hmi.container.BurgerContainer;
import de.cookindustries.lib.spring.gui.hmi.container.Button;
import de.cookindustries.lib.spring.gui.hmi.container.ButtonIcon;
import de.cookindustries.lib.spring.gui.hmi.container.Container;
import de.cookindustries.lib.spring.gui.hmi.container.ContentContainer;
import de.cookindustries.lib.spring.gui.hmi.container.Direction;
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
import de.cookindustries.lib.spring.gui.hmi.input.Number;
import de.cookindustries.lib.spring.gui.hmi.input.Password;
import de.cookindustries.lib.spring.gui.hmi.input.Radio;
import de.cookindustries.lib.spring.gui.hmi.input.Select;
import de.cookindustries.lib.spring.gui.hmi.input.Slider;
import de.cookindustries.lib.spring.gui.hmi.input.SubmittableInput;
import de.cookindustries.lib.spring.gui.hmi.input.Switch;
import de.cookindustries.lib.spring.gui.hmi.input.Tag;
import de.cookindustries.lib.spring.gui.hmi.input.Textarea;
import de.cookindustries.lib.spring.gui.hmi.input.Textbox;
import de.cookindustries.lib.spring.gui.hmi.input.Textfield;
import de.cookindustries.lib.spring.gui.hmi.input.util.InputValue;
import de.cookindustries.lib.spring.gui.hmi.input.util.MarkerCategory;
import de.cookindustries.lib.spring.gui.hmi.svg.SVGElement;
import de.cookindustries.lib.spring.gui.hmi.svg.SVGGroup;
import de.cookindustries.lib.spring.gui.hmi.svg.SVGLine;
import de.cookindustries.lib.spring.gui.hmi.svg.SVGPath;
import de.cookindustries.lib.spring.gui.hmi.svg.SVGPathCommand;
import de.cookindustries.lib.spring.gui.hmi.svg.SVGText;
import de.cookindustries.lib.spring.gui.util.StringAdapter;
import lombok.Data;

/**
 * @since 1.0.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
@Data
public final class HtmlMapper
{

    private static final String        TAG_DIV                        = "div";
    private static final String        TAG_LABEL                      = "label";
    private static final String        TAG_INPUT                      = "input";
    private static final String        TAG_SELECT                     = "select";
    private static final String        TAG_OPTION                     = "option";
    private static final String        TAG_SVG                        = "svg";
    private static final String        TAG_LINE                       = "line";
    private static final String        TAG_TEXT                       = "text";
    private static final String        TAG_GROUP                      = "g";
    private static final String        TAG_PATH                       = "path";
    private static final String        TAG_SPAN                       = "span";
    private static final String        TAG_P                          = "p";
    private static final String        TAG_FIGURE                     = "figure";
    private static final String        TAG_AUDIO                      = "audio";
    private static final String        TAG_FIGCAPTION                 = "figcaption";

    private static final String        ATT_ID                         = "id";
    private static final String        ATT_NAME                       = "name";
    private static final String        ATT_TYPE                       = "type";
    private static final String        ATT_ARIA_VALUETEXT             = "aria-valuetext";
    private static final String        ATT_FOR                        = "for";
    private static final String        ATT_ON_INPUT                   = "oninput";
    private static final String        ATT_ON_CLICK                   = "onclick";
    private static final String        ATT_CHECKED                    = "checked";
    private static final String        ATT_SELECTED                   = "selected";
    private static final String        ATT_MIN                        = "min";
    private static final String        ATT_MAX                        = "max";
    private static final String        ATT_STEP                       = "step";
    private static final String        ATT_PLACEHOLDER                = "placeholder";
    private static final String        ATT_HREF                       = "href";
    private static final String        ATT_TARGET                     = "target";
    private static final String        ATT_SRC                        = "src";
    private static final String        ATT_VALUE                      = "value";
    private static final String        ATT_ON_KEY_DOWN                = "onkeydown";
    private static final String        ATT_STYLE                      = "style";
    private static final String        ATT_WIDTH                      = "width";
    private static final String        ATT_HEIGHT                     = "height";
    private static final String        ATT_D                          = "d";
    private static final String        ATT_DISABLED                   = "disabled";
    private static final String        ATT_CONTROLS                   = "controls";
    private static final String        ATT_AUTOPLAY                   = "autoplay";
    private static final String        ATT_ROLE                       = "role";

    private static final String        CLASS_INPUT_LEGEND             = "input-legend";
    private static final String        CLASS_FORM_CONTROL             = "form-control";
    private static final String        CLASS_HIDDEN                   = "hidden";
    private static final String        CLASS_USER_SELECT_NONE         = "user-select-none";
    private static final String        CLASS_FORM_CHECK               = "form-checkbox";
    private static final String        CLASS_FORM_CHECK_INPUT         = "form-checkbox-input";
    private static final String        CLASS_FORM_CHECK_LABEL         = "form-checkbox-label";
    private static final String        CLASS_FORM_SELECT              = "form-select";

    private static final String        INPUT_CONTAINER                = "input-container";

    private static final String        DATA_ATT_SUBMIT_ID             = "submit-id";
    private static final String        DATA_ATT_SUBMIT_AS             = "submit-as";
    private static final String        DATA_ATT_VALUE_TYPE            = "value-type";
    private static final String        DATA_ATT_MAX_CHARACTERS        = "max-characters";
    private static final String        DATA_ATT_ON_ENTER_PRESS        = "on-enter-press";
    private static final String        DATA_ATT_TOOLTIP               = "tooltip";

    private static final int           SLIDER_MAX_TICK_INTERVALS      = 20;
    private static final int           SLIDER_FALLBACK_TICK_INTERVALS = 10;

    private static final DecimalFormat DOUBLE_FORMATER                =
        new DecimalFormat("0.000", DecimalFormatSymbols.getInstance(Locale.ROOT));

    /**
     * Internal constructor
     */
    private HtmlMapper()
    {
        // private constructor
    }

    /**
     * Map a {@link Container} to its HTML represantation
     * 
     * @param container to map
     * @return resulting HTML String
     */
    public static String map(Container container)
    {
        HtmlMapper mapper = new HtmlMapper();

        return mapper.render(container);
    }

    /**
     * Map a list of {@link Container}s to a single HTML String
     * 
     * @param containers to map
     * @return resulting HTML String, containing all mappings one after the other
     */
    public static List<String> map(List<Container> containers)
    {
        HtmlMapper mapper = new HtmlMapper();

        return containers
            .stream()
            .map(mapper::render)
            .toList();
    }

    // === > helper functions ==============================================================================================================

    /**
     * A temporary result for not yet implemented objects.
     * <p>
     * TODO: remove
     * 
     * @param name of the element
     * @return a error div describing the missing element
     */
    private String elementNotYetImplemented(String name)
    {
        String s = name + " element not supported yet.";

        return StringAdapter.prefixAndSuffix("<div>", s, "</div>");
    }

    /**
     * Check whether a {@code String} is {@code null} or {@code blank}.
     * 
     * @param sup to test
     * @return {@code true} if {@code sup} is non {@code null} and non {@code blank}
     */
    private boolean isFilled(Supplier<String> sup)
    {
        String s = sup.get();

        return !(Objects.isNull(s) || s.isBlank());
    }

    /**
     * Escape HTML keys.
     * 
     * @param text to escape
     * @return {@code text} with escaped entities or {@code null} if {@code text} was {@code null}
     */
    private String htmlEscape(String text)
    {
        return StringEscapeUtils.escapeHtml4(text);
    }

    private static BigDecimal toBigDecimal(java.lang.Number number)
    {
        if (number instanceof BigDecimal decimal)
        {
            return decimal;
        }

        if (number instanceof BigInteger integer)
        {
            return new BigDecimal(integer);
        }

        return new BigDecimal(number.toString());
    }

    private static String plain(java.lang.Number number)
    {
        return toBigDecimal(number).toPlainString();
    }

    private static int sliderIndex(Slider slider)
    {
        return Math.max(slider.getCustomValues().indexOf(slider.getValue()), 0);
    }

    /**
     * Position of the slider's value between its bounds, from {@code 0} to {@code 1}. The stylesheet uses it to fill the track up to the
     * thumb.
     */
    private static String sliderRatio(Slider slider)
    {
        BigDecimal min;
        BigDecimal range;
        BigDecimal position;

        if (slider.isCustom())
        {
            min      = BigDecimal.ZERO;
            range    = BigDecimal.valueOf(slider.getCustomValues().size() - 1L);
            position = BigDecimal.valueOf(sliderIndex(slider));
        }
        else
        {
            min      = toBigDecimal(slider.getMin());
            range    = toBigDecimal(slider.getMax()).subtract(min);
            position = toBigDecimal((java.lang.Number) slider.getValue());
        }

        if (range.signum() <= 0)
        {
            return "0";
        }

        BigDecimal ratio = position.subtract(min).divide(range, 4, RoundingMode.HALF_UP);

        return ratio.max(BigDecimal.ZERO).min(BigDecimal.ONE).toPlainString();
    }

    /**
     * Number of gaps between the scale marks. Custom values get one mark each, a numeric slider the requested number of marks. Without a
     * request it gets one mark per step as long as that stays readable, otherwise a fixed number of evenly spread marks.
     */
    private static int sliderTickIntervals(Slider slider)
    {
        if (slider.isCustom())
        {
            return Math.max(slider.getCustomValues().size() - 1, 1);
        }

        if (slider.getMarks() != null)
        {
            return Math.max(slider.getMarks() - 1, 1);
        }

        BigDecimal range = toBigDecimal(slider.getMax()).subtract(toBigDecimal(slider.getMin()));
        BigDecimal step  = toBigDecimal(slider.getStep());

        if (range.signum() <= 0 || step.signum() <= 0)
        {
            return 1;
        }

        BigDecimal intervals = range.divide(step, 0, RoundingMode.DOWN);

        if (intervals.compareTo(BigDecimal.valueOf(SLIDER_MAX_TICK_INTERVALS)) > 0)
        {
            return SLIDER_FALLBACK_TICK_INTERVALS;
        }

        return Math.max(intervals.intValue(), 1);
    }

    // === < helper functions ==============================================================================================================

    /**
     * Render a {@link Container} as HTML component
     * 
     * @param container to render
     * @return the resulting HTML String or
     */
    private String render(Container container)
    {
        if (container == null)
        {
            return "";
        }

        return switch (container.getType())
        {
            case AUDIO -> render((AudioContainer) container);
            case BURGER -> render((BurgerContainer) container);
            case BUTTON -> render((Button) container);
            case BUTTON_ICON -> render((ButtonIcon) container);
            case CONTENT -> render((ContentContainer) container);
            case EMPTY -> "";
            case FORM -> render((FormContainer) container);
            case HEADING -> render((HeadingContainer) container);
            case HIDDEN -> render((HiddenContainer) container);
            case IMAGE -> render((ImageContainer) container);
            case LINK -> render((LinkContainer) container);
            case MODAL -> render((ModalContainer) container);
            case SPLITTED -> render((SplittedContainer) container);
            case SVG -> render((SVGContainer) container);
            case TAB -> render((TabContainer) container);
            case TABLE -> render((TableContainer) container);
            case TABLE_ROW -> render((TableRowContainer) container);
            case TEXT -> render((TextContainer) container);
        };
    }

    private String render(AudioContainer audio)
    {
        return HtmlElement
            .builder()
            .tag(TAG_FIGURE)
            .content(
                HtmlElement
                    .builder()
                    .tag(TAG_FIGCAPTION)
                    .dataAttribute(DATA_ATT_TOOLTIP, audio.getTooltip().isBlank() ? null : htmlEscape(audio.getTooltip()))
                    .content(audio.getCaption())
                    .build()
                    .html())
            .content(
                HtmlElement
                    .builder()
                    .tag(TAG_AUDIO)
                    .attribute(
                        Attribute
                            .builder()
                            .name(ATT_ID)
                            .value(audio.getUid())
                            .build())
                    .attribute(
                        Attribute
                            .builder()
                            .name(ATT_SRC)
                            .value(audio.getSrc())
                            .build())
                    .attribute(
                        Attribute
                            .builder()
                            .active(audio.isControls())
                            .name(ATT_CONTROLS)
                            .build())
                    .attribute(
                        Attribute
                            .builder()
                            .active(audio.isAutoplay())
                            .name(ATT_AUTOPLAY)
                            .build())
                    .build()
                    .html())
            .build()
            .html();
    }

    private String render(BurgerContainer burger)
    {
        List<String> items         =
            burger
                .getItems()
                .stream()
                .map(item -> HtmlElement
                    .builder()
                    .tag(TAG_DIV)
                    .clazz("burger-item")
                    .dataAttribute("burger-url", htmlEscape(item.getUrl()))
                    .dataAttribute("burger-method", htmlEscape(item.getMethod()))
                    .content(
                        HtmlElement
                            .builder()
                            .tag(TAG_DIV)
                            .clazz("burger-item-icon")
                            .content(
                                HtmlElement
                                    .builder()
                                    .tag("i")
                                    .clazz("bi")
                                    .clazz(item.getIcon())
                                    .inactive(item.getIcon().isBlank())
                                    .build()
                                    .html())
                            .build()
                            .html())
                    .content(
                        HtmlElement
                            .builder()
                            .tag(TAG_DIV)
                            .clazz("burger-text")
                            .content(htmlEscape(item.getText()))
                            .build()
                            .html())
                    .build()
                    .html())
                .toList();

        HtmlElement  elementMapper =
            HtmlElement
                .builder()
                .tag(TAG_DIV)
                .attribute(
                    Attribute
                        .builder()
                        .name(ATT_ID)
                        .value(burger.getUid())
                        .build())
                .clazz("burger-menu")
                .classes(burger.getClasses())
                .dataAttributes(burger.getDataAttributes())
                .content(
                    HtmlElement
                        .builder()
                        .tag("i")
                        .clazz("burger-icon")
                        .clazz("bi")
                        .clazz("bi-list")
                        .build()
                        .html())
                .content(
                    HtmlElement
                        .builder()
                        .tag(TAG_DIV)
                        .clazz("burger-dropdown")
                        .contents(items)
                        .build()
                        .html())
                .build();

        return elementMapper.html();
    }

    private String render(Button button)
    {
        return HtmlElement
            .builder()
            .tag("button")
            .dataAttribute(DATA_ATT_TOOLTIP, button.getTooltip().isBlank() ? null : htmlEscape(button.getTooltip()))
            .attribute(
                Attribute
                    .builder()
                    .name(ATT_ID)
                    .value(button.getUid())
                    .build())
            .attribute(
                Attribute
                    .builder()
                    .name(ATT_ON_CLICK)
                    .active(!button.getOnClick().isBlank())
                    .value(button.getOnClick())
                    .build())
            .clazz("btn")
            .clazz(button.getBtnClass().getClassName())
            .classes(button.getClasses())
            .dataAttributes(button.getDataAttributes())
            .content(htmlEscape(button.getText()))
            .build()
            .html();
    }

    private String render(ButtonIcon buttonIcon)
    {
        return HtmlElement
            .builder()
            .tag(TAG_DIV)
            .clazz("btn")
            .clazz("btn-icon")
            .clazz(buttonIcon.getBtnClass().getClassName())
            .classes(buttonIcon.getClasses())
            .dataAttributes(buttonIcon.getDataAttributes())
            .dataAttribute(DATA_ATT_TOOLTIP, buttonIcon.getTooltip().isBlank() ? null : htmlEscape(buttonIcon.getTooltip()))
            .attribute(
                Attribute
                    .builder()
                    .name(ATT_ID)
                    .value(buttonIcon.getUid())
                    .build())
            .attribute(
                Attribute
                    .builder()
                    .name(ATT_ON_CLICK)
                    .active(!buttonIcon.getOnClick().isBlank())
                    .value(buttonIcon.getOnClick())
                    .build())
            .content(
                HtmlElement
                    .builder()
                    .tag("i")
                    .clazz("bi")
                    .clazz(buttonIcon.getIcon())
                    .inactive(buttonIcon.getIcon().isBlank())
                    .build()
                    .html())
            .build()
            .html();
    }

    private String render(ContentContainer contentContainer)
    {
        String content =
            contentContainer
                .getContents()
                .stream()
                .map(this::render)
                .collect(Collectors.joining());

        return HtmlElement
            .builder()
            .tag(TAG_DIV)
            .dataAttribute(DATA_ATT_TOOLTIP, contentContainer.getTooltip().isBlank() ? null : htmlEscape(contentContainer.getTooltip()))
            .attribute(
                Attribute
                    .builder()
                    .name(ATT_ID)
                    .value(contentContainer.getUid())
                    .build())
            .classes(contentContainer.getClasses())
            .dataAttributes(contentContainer.getDataAttributes())
            .content(content)
            .build()
            .html();
    }

    private String render(FormContainer formContainer)
    {
        String      content       =
            formContainer
                .getInputs()
                .stream()
                .map(i -> render(i, formContainer.getUid()))
                .collect(Collectors.joining());

        HtmlElement elementMapper =
            HtmlElement
                .builder()
                .tag(TAG_DIV)
                .attribute(
                    Attribute
                        .builder()
                        .name(ATT_ID)
                        .value(formContainer.getUid())
                        .build())
                .clazz("form-container")
                .clazz(formContainer.getDirection() == Direction.HORIZONTAL ? "form-container-row" : "form-container-column")
                .classes(formContainer.getClasses())
                .dataAttributes(formContainer.getDataAttributes())
                .content(content)
                .build();

        return elementMapper.html();
    }

    private String render(HiddenContainer hiddenContainer)
    {
        String      children      =
            hiddenContainer
                .getChildren()
                .stream()
                .map(this::render)
                .collect(Collectors.joining());

        HtmlElement elementMapper =
            HtmlElement
                .builder()
                .tag(TAG_DIV)
                .attribute(
                    Attribute
                        .builder()
                        .name(ATT_ID)
                        .value(hiddenContainer.getUid())
                        .build())
                .clazz(CLASS_HIDDEN)
                .classes(hiddenContainer.getClasses())
                .dataAttributes(hiddenContainer.getDataAttributes())
                .content(children)
                .build();

        return elementMapper.html();
    }

    private String render(ImageContainer imageContainer)
    {
        HtmlElement elementMapper =
            HtmlElement
                .builder()
                .tag("img")
                .isSingleTag(true)
                .attribute(
                    Attribute
                        .builder()
                        .name(ATT_ID)
                        .value(imageContainer.getUid())
                        .build())
                .attribute(
                    Attribute
                        .builder()
                        .name(ATT_SRC)
                        .value(imageContainer.getSrc())
                        .build())
                .classes(imageContainer.getClasses())
                .dataAttributes(imageContainer.getDataAttributes())
                .dataAttribute(DATA_ATT_TOOLTIP, imageContainer.getTooltip().isBlank() ? null : htmlEscape(imageContainer.getTooltip()))
                .build();

        return elementMapper.html();
    }

    private String render(LinkContainer linkContainer)
    {
        HtmlElement elementMapper =
            HtmlElement
                .builder()
                .tag("a")
                .attribute(
                    Attribute
                        .builder()
                        .name(ATT_ID)
                        .value(linkContainer.getUid())
                        .build())
                .attribute(
                    Attribute
                        .builder()
                        .name(ATT_HREF)
                        .value(linkContainer.getHref())
                        .build())
                .attribute(
                    Attribute
                        .builder()
                        .name(ATT_TARGET)
                        .value(linkContainer.getTarget())
                        .build())
                .classes(linkContainer.getClasses())
                .dataAttributes(linkContainer.getDataAttributes())
                .dataAttribute(DATA_ATT_TOOLTIP, linkContainer.getTooltip().isBlank() ? null : htmlEscape(linkContainer.getTooltip()))
                .content(render(linkContainer.getContent()))
                .build();

        return elementMapper.html();
    }

    private String render(ModalContainer modalContainer)
    {
        List<Button> buttons = new ArrayList<>();

        // order of buttons is reveresed since they are right to left oriented in the modal

        buttons.add(
            Button
                .builder()
                .uid(modalContainer.getUid() + "-right-btn")
                .btnClass(modalContainer.getBtnClassRight())
                .text(htmlEscape(modalContainer.getBtnNameRight()))
                .onClick(modalContainer.getBtnFunctionRight())
                .build());

        if (modalContainer.getBtnNameCenter() != null && !modalContainer.getBtnNameCenter().isEmpty())
        {
            buttons.add(
                Button
                    .builder()
                    .uid(modalContainer.getUid() + "-center-btn")
                    .btnClass(modalContainer.getBtnClassCenter())
                    .text(htmlEscape(modalContainer.getBtnNameCenter()))
                    .onClick(modalContainer.getBtnFunctionCenter())
                    .build());
        }

        if (modalContainer.getBtnNameLeft() != null && !modalContainer.getBtnNameLeft().isEmpty())
        {
            buttons.add(
                Button
                    .builder()
                    .uid(modalContainer.getUid() + "-left-btn")
                    .btnClass(modalContainer.getBtnClassLeft())
                    .text(htmlEscape(modalContainer.getBtnNameLeft()))
                    .onClick(modalContainer.getBtnFunctionLeft())
                    .build());
        }

        ContentContainer buttonBar     =
            ContentContainer
                .builder()
                .clazz("modal-buttons")
                .contents(buttons)
                .build();

        ContentContainer modalInlay    =
            ContentContainer
                .builder()
                .clazz("modal-inlay")
                .content(
                    TextContainer
                        .builder()
                        .clazz("modal-name")
                        .clazz("text-bold")
                        .text(htmlEscape(modalContainer.getName()))
                        .build())
                .content(
                    ContentContainer
                        .builder()
                        .clazz("flex-vertical")
                        .clazz("overflow-auto")
                        .contents(modalContainer.getContents())
                        .build())
                .content(buttonBar)
                .build();

        HtmlElement      elementMapper =
            HtmlElement
                .builder()
                .tag(TAG_DIV)
                .attribute(
                    Attribute
                        .builder()
                        .name(ATT_ID)
                        .value(modalContainer.getUid())
                        .build())
                .clazz("modal-body")
                .classes(modalContainer.getClasses())
                .dataAttribute("close-on-overlay", String.valueOf(modalContainer.isCloseOnOverlayClick()))
                .dataAttributes(modalContainer.getDataAttributes())
                .content(render(modalInlay))
                .build();

        return elementMapper.html();
    }

    private String render(SplittedContainer splittedContainer)
    {
        String       head          = render(splittedContainer.getHead());
        String       tail          = render(splittedContainer.getTail());

        List<String> center        =
            splittedContainer
                .getCenter()
                .stream()
                .map(this::render)
                .toList();

        HtmlElement  elementMapper =
            HtmlElement
                .builder()
                .tag(TAG_DIV)
                .attribute(
                    Attribute
                        .builder()
                        .name(ATT_ID)
                        .value(splittedContainer.getUid())
                        .build())
                .clazz("d-flex")
                .clazz("justify-content-between")
                .classes(splittedContainer.getClasses())
                .dataAttributes(splittedContainer.getDataAttributes())
                .content(head)
                .contents(center)
                .content(tail)
                .build();

        return elementMapper.html();
    }

    private String render(SVGContainer svgContainer)
    {
        List<String> elements      =
            svgContainer
                .getElements()
                .stream()
                .map(elem -> render(elem, svgContainer.getUid()))
                .toList();

        HtmlElement  elementMapper =
            HtmlElement
                .builder()
                .tag(TAG_SVG)
                .attribute(
                    Attribute
                        .builder()
                        .name(ATT_ID)
                        .value(svgContainer.getUid())
                        .build())
                .attribute(
                    Attribute
                        .builder()
                        .name(ATT_WIDTH)
                        .value(String.valueOf(svgContainer.getWidth()))
                        .build())
                .attribute(
                    Attribute
                        .builder()
                        .name(ATT_HEIGHT)
                        .value(String.valueOf(svgContainer.getHeight()))
                        .build())
                .classes(svgContainer.getClasses())
                .dataAttributes(svgContainer.getDataAttributes())
                .contents(elements)
                .build();

        return elementMapper.html();
    }

    private String render(TabContainer tabbedContainer)
    {
        // TODO: implement
        return elementNotYetImplemented("tabbed container");
    }

    private String render(TableContainer table)
    {
        List<String> headColumns   =
            table
                .getColumnNames()
                .stream()
                .map(
                    name -> HtmlElement
                        .builder()
                        .tag(TAG_DIV)
                        .clazz("table-cell")
                        .content(htmlEscape(name))
                        .build()
                        .html())
                .toList();

        List<String> rows          =
            table
                .getRows()
                .stream()
                .map(this::render)
                .toList();

        HtmlElement  tableHead     =
            HtmlElement
                .builder()
                .tag(TAG_DIV)
                .clazz("table-head")
                .clazz("table-" + table.getName())
                .contents(headColumns)
                .build();

        HtmlElement  tableBody     =
            HtmlElement
                .builder()
                .tag(TAG_DIV)
                .clazz("table-body")
                .clazz("table-" + table.getName() + "-body")
                .contents(rows)
                .build();

        HtmlElement  elementMapper =
            HtmlElement
                .builder()
                .tag(TAG_DIV)
                .attribute(
                    Attribute
                        .builder()
                        .name(ATT_ID)
                        .value(table.getUid())
                        .build())
                .classes(table.getClasses())
                .dataAttributes(table.getDataAttributes())
                .content(tableHead.html())
                .content(tableBody.html())
                .build();

        return elementMapper.html();
    }

    private String render(TableRowContainer row)
    {
        List<String> cells         =
            row
                .getCells()
                .stream()
                .map(tr -> HtmlElement
                    .builder()
                    .tag(TAG_DIV)
                    .clazz("table-cell")
                    .content(render(tr))
                    .build()
                    .html())
                .toList();

        HtmlElement  elementMapper =
            HtmlElement
                .builder()
                .tag(TAG_DIV)
                .attribute(
                    Attribute
                        .builder()
                        .name(ATT_ID)
                        .value(row.getUid())
                        .build())
                .clazz("table-row")
                .clazz("table-" + row.getTableName())
                .classes(row.getClasses())
                .dataAttributes(row.getDataAttributes())
                .contents(cells)
                .build();

        return elementMapper.html();
    }

    private String render(TextContainer textContainer)
    {
        return HtmlElement
            .builder()
            .tag(textContainer.isInline() ? TAG_SPAN : TAG_P)
            .attribute(
                Attribute
                    .builder()
                    .name(ATT_ID)
                    .value(textContainer.getUid())
                    .build())
            .classes(textContainer.getClasses())
            .dataAttributes(textContainer.getDataAttributes())
            .dataAttribute(DATA_ATT_TOOLTIP, textContainer.getTooltip().isBlank() ? null : htmlEscape(textContainer.getTooltip()))
            .content(htmlEscape(textContainer.getText()))
            .build()
            .html();
    }

    private String render(HeadingContainer textHeaderContainer)
    {
        return HtmlElement
            .builder()
            .tag(String.format("h%s", textHeaderContainer.getSize()))
            .attribute(
                Attribute
                    .builder()
                    .name(ATT_ID)
                    .value(textHeaderContainer.getUid())
                    .build())
            .classes(textHeaderContainer.getClasses())
            .dataAttributes(textHeaderContainer.getDataAttributes())
            .dataAttribute(DATA_ATT_TOOLTIP,
                textHeaderContainer.getTooltip().isBlank() ? null : htmlEscape(textHeaderContainer.getTooltip()))
            .content(htmlEscape(textHeaderContainer.getText()))
            .build()
            .html();
    }

    private String render(Input input, String formId)
    {
        return switch (input.getType())
        {
            case CHECKBOX -> render((Checkbox) input, formId);
            case DATE -> render((Date) input, formId);
            case FILE -> render((File) input, formId);
            case HIDDEN -> render((Hidden) input, formId);
            case NUMBER -> render((Number) input, formId);
            case PASSWORD -> render((Password) input, formId);
            case RADIO -> render((Radio) input, formId);
            case SELECT -> render((Select) input, formId);
            case SLIDER -> render((Slider) input, formId);
            case SWITCH -> render((Switch) input, formId);
            case TAG -> render((Tag) input, formId);
            case TEXTAREA -> render((Textarea) input, formId);
            case TEXTBOX -> render((Textbox) input);
            case TEXTFIELD -> render((Textfield) input, formId);
        };
    }

    private HtmlElement createLegend(SubmittableInput input)
    {
        return createLegend(input, CLASS_INPUT_LEGEND);
    }

    private HtmlElement createLegend(SubmittableInput input, String formClass)
    {
        return HtmlElement
            .builder()
            .tag(TAG_LABEL)
            .attribute(
                Attribute
                    .builder()
                    .name(ATT_FOR)
                    .value(input.getUid())
                    .build())
            .clazz(formClass)
            .clazz(CLASS_USER_SELECT_NONE)
            .dataAttribute(DATA_ATT_TOOLTIP, input.getTooltip().isBlank() ? null : htmlEscape(input.getTooltip()))
            .content(htmlEscape(input.getName()))
            .build();
    }

    private HtmlElement createInput(SubmittableInput input, String formId, String formClass)
    {
        return HtmlElement
            .builder()
            .isSingleTag(true)
            .tag(TAG_INPUT)
            .attribute(
                Attribute
                    .builder()
                    .name(ATT_ID)
                    .value(input.getUid())
                    .build())
            .attribute(
                Attribute
                    .builder()
                    .name(ATT_TYPE)
                    .value(input.getType().name().toLowerCase())
                    .build())
            .attribute(
                Attribute
                    .builder()
                    .name(ATT_ON_INPUT)
                    .value(input.getOnInput())
                    .active(isFilled(input::getOnInput))
                    .build())
            .attribute(
                Attribute
                    .builder()
                    .name(ATT_DISABLED)
                    .value(ATT_DISABLED)
                    .active(input.isDisabled())
                    .build())
            .clazz(formClass)
            .clazz("input-field")
            .dataAttribute(DATA_ATT_SUBMIT_ID, formId)
            .dataAttribute(DATA_ATT_SUBMIT_AS, input.getSubmitAs())
            .dataAttribute(DATA_ATT_VALUE_TYPE, input.getType().name())
            .dataAttributes(input.getDataAttributes())
            .build();
    }

    private String createInputWrapper(String legend, String mandatoryIcon, String warningIcon, String errorIcon, String infoIcon,
        String input)
    {
        return createInputWrapper("", legend, mandatoryIcon, warningIcon, errorIcon, infoIcon, input);
    }

    private String createInputWrapper(String additionalClass, String legend, String mandatoryIcon, String warningIcon, String errorIcon,
        String infoIcon, String input)
    {
        HtmlElement.HtmlElementBuilder wrapper =
            HtmlElement
                .builder()
                .tag(TAG_DIV)
                .clazz(INPUT_CONTAINER);

        if (!additionalClass.isBlank())
        {
            wrapper.clazz(additionalClass);
        }

        return wrapper
            .content(legend)
            .content(mandatoryIcon)
            .content(warningIcon)
            .content(errorIcon)
            .content(infoIcon)
            .content(input)
            .build()
            .html();
    }

    private String render(Checkbox checkbox, String formId)
    {
        List<String>  boxes = new ArrayList<>();

        AtomicInteger cnt   = new AtomicInteger();

        checkbox
            .getBoxes()
            .stream()
            .forEach(
                box -> {
                    String id    = String.format("%s-%s-%s", checkbox.getUid(), checkbox.getSubmitAs(), cnt.getAndIncrement());
                    String submitId = String.format("%s-%s", checkbox.getSubmitAs(), box.getSubmitAs());

                    String input =
                        HtmlElement
                            .builder()
                            .isSingleTag(true)
                            .tag(TAG_INPUT)
                            .attribute(
                                Attribute
                                    .builder()
                                    .name(ATT_ID)
                                    .value(id)
                                    .build())
                            .attribute(
                                Attribute
                                    .builder()
                                    .name(ATT_TYPE)
                                    .value(checkbox.getType().name().toLowerCase())
                                    .build())
                            .attribute(
                                Attribute
                                    .builder()
                                    .name(ATT_ON_INPUT)
                                    .value(checkbox.getOnInput())
                                    .build())
                            .attribute(
                                Attribute
                                    .builder()
                                    .name(ATT_CHECKED)
                                    .active(box.isChecked())
                                    .build())
                            .clazz(CLASS_FORM_CHECK_INPUT)
                            .dataAttribute(DATA_ATT_SUBMIT_ID, formId)
                            .dataAttribute(DATA_ATT_SUBMIT_AS, submitId)
                            .dataAttribute(DATA_ATT_VALUE_TYPE, checkbox.getType().name())
                            .dataAttributes(checkbox.getDataAttributes())
                            .build()
                            .html();

                    String label =
                        HtmlElement
                            .builder()
                            .tag(TAG_LABEL)
                            .attribute(
                                Attribute
                                    .builder()
                                    .name(ATT_FOR)
                                    .value(id)
                                    .build())
                            .clazz("checkbox-label")
                            .clazz(CLASS_USER_SELECT_NONE)
                            .content(htmlEscape(box.getText()))
                            .build()
                            .html();

                    HtmlElement wrapper =
                        HtmlElement
                            .builder()
                            .tag(TAG_DIV)
                            .clazz("input-checkbox-wrapper")
                            .content(input)
                            .content(label)
                            .build();

                    boxes.add(wrapper.html());
                });

        String      legend        =
            createLegend(checkbox)
                .html();

        String      checkboxes    =
            HtmlElement
                .builder()
                .tag(TAG_DIV)
                .clazz("input-checkboxes-wrapper")
                .contents(boxes)
                .build()
                .html();

        HtmlElement elementMapper =
            HtmlElement
                .builder()
                .tag(TAG_DIV)
                .clazz(INPUT_CONTAINER)
                .content(StringAdapter.from(legend))
                .content(resolveMandatoryIcon(checkbox.isMandatory(), formId, checkbox.getSubmitAs(), checkbox.getMandatoryText()))
                .content(resolveErrorIcon(formId, checkbox.getSubmitAs()))
                .content(resolveWarningIcon(formId, checkbox.getSubmitAs()))
                .content(resolveInfoIcon(formId, checkbox.getUid(), checkbox.getInfoText(), checkbox.getInfoUrl()))
                .content(checkboxes)
                .build();

        return elementMapper.html();
    }

    private String render(Date date, String formId)
    {
        String legend =
            createLegend(date)
                .html();

        String input  =
            createInput(date, formId, CLASS_FORM_CONTROL)
                .toBuilder()
                .attribute(
                    Attribute
                        .builder()
                        .name(ATT_VALUE)
                        .value(date.getValue())
                        .build())
                .build()
                .html();

        return createInputWrapper(
            legend,
            resolveMandatoryIcon(date.isMandatory(), formId, date.getSubmitAs(), date.getMandatoryText()),
            resolveWarningIcon(formId, date.getSubmitAs()),
            resolveErrorIcon(formId, date.getSubmitAs()),
            resolveInfoIcon(formId, date.getSubmitAs(), date.getInfoText(), date.getInfoUrl()),
            input);
    }

    private String render(File file, String formId)
    {
        String legend =
            createLegend(file)
                .html();

        String input  =
            createInput(file, formId, CLASS_FORM_CONTROL)
                .toBuilder()
                .attribute(
                    Attribute
                        .builder()
                        .name("multiple")
                        .active(file.getMultiple())
                        .build())
                .attribute(
                    Attribute
                        .builder()
                        .name("accept")
                        .value(StringAdapter.separate(file.getAccepts(), ","))
                        .build())
                .build()
                .html();

        return createInputWrapper(
            legend,
            resolveMandatoryIcon(file.isMandatory(), formId, file.getSubmitAs(), file.getMandatoryText()),
            resolveWarningIcon(formId, file.getSubmitAs()),
            resolveErrorIcon(formId, file.getSubmitAs()),
            resolveInfoIcon(formId, file.getSubmitAs(), file.getInfoText(), file.getInfoUrl()),
            input);
    }

    private String render(Hidden hidden, String formId)
    {
        String      input         =
            HtmlElement
                .builder()
                .isSingleTag(true)
                .tag(TAG_INPUT)
                .attribute(
                    Attribute
                        .builder()
                        .name(ATT_ID)
                        .value(hidden.getUid())
                        .build())
                .attribute(
                    Attribute
                        .builder()
                        .name(ATT_TYPE)
                        .value("text")
                        .build())
                .attribute(
                    Attribute
                        .builder()
                        .name(ATT_VALUE)
                        .value(htmlEscape(hidden.getValue()))
                        .build())
                .clazz(CLASS_HIDDEN)
                .dataAttribute(DATA_ATT_SUBMIT_ID, formId)
                .dataAttribute(DATA_ATT_SUBMIT_AS, hidden.getSubmitAs())
                .dataAttribute(DATA_ATT_VALUE_TYPE, hidden.getType().name())
                .dataAttributes(hidden.getDataAttributes())
                .build()
                .html();

        HtmlElement elementMapper =
            HtmlElement
                .builder()
                .tag(TAG_DIV)
                .content(input)
                .build();

        return elementMapper.html();
    }

    private String render(Number number, String formId)
    {
        String legend =
            createLegend(number)
                .html();

        String input  =
            createInput(number, formId, CLASS_FORM_CONTROL)
                .toBuilder()
                .attribute(
                    Attribute
                        .builder()
                        .name(ATT_VALUE)
                        .value(String.valueOf(number.getValue()))
                        .build())
                .attribute(
                    Attribute
                        .builder()
                        .name(ATT_PLACEHOLDER)
                        .value(htmlEscape(number.getPlaceholder()))
                        .build())
                .attribute(
                    Attribute
                        .builder()
                        .name(ATT_MIN)
                        .value(String.valueOf(number.getMin()))
                        .build())
                .attribute(
                    Attribute
                        .builder()
                        .name(ATT_MAX)
                        .value(String.valueOf(number.getMax()))
                        .build())
                .attribute(
                    Attribute
                        .builder()
                        .name(ATT_STEP)
                        .value(String.valueOf(number.getStep()))
                        .build())
                .dataAttribute(DATA_ATT_ON_ENTER_PRESS, number.getOnEnterPress())
                .build()
                .html();

        return createInputWrapper(
            legend,
            resolveMandatoryIcon(number.isMandatory(), formId, number.getSubmitAs(), number.getMandatoryText()),
            resolveWarningIcon(formId, number.getSubmitAs()),
            resolveErrorIcon(formId, number.getSubmitAs()),
            resolveInfoIcon(formId, number.getSubmitAs(), number.getInfoText(), number.getInfoUrl()),
            input);
    }

    private String render(Password password, String formId)
    {
        String legend =
            createLegend(password)
                .html();

        String input  =
            createInput(password, formId, CLASS_FORM_CONTROL)
                .toBuilder()
                .attribute(
                    Attribute
                        .builder()
                        .name(ATT_VALUE)
                        .value("")
                        .build())
                .attribute(
                    Attribute
                        .builder()
                        .name(ATT_PLACEHOLDER)
                        .value(htmlEscape(password.getPlaceholder()))
                        .build())
                .dataAttribute(DATA_ATT_ON_ENTER_PRESS, password.getOnEnterPress())
                .build()
                .html();

        return createInputWrapper(
            legend,
            resolveMandatoryIcon(password.isMandatory(), formId, password.getSubmitAs(), password.getMandatoryText()),
            resolveWarningIcon(formId, password.getSubmitAs()),
            resolveErrorIcon(formId, password.getSubmitAs()),
            resolveInfoIcon(formId, password.getSubmitAs(), password.getInfoText(), password.getInfoUrl()),
            input);
    }

    private String render(Radio radio, String formId)
    {
        ArrayList<String> buttons    = new ArrayList<>();

        int               i          = 0;
        String            radioID;

        boolean           checkFirst =
            radio
                .getBoxes()
                .stream()
                .noneMatch(InputValue::isChecked);

        for (InputValue box : radio.getBoxes())
        {
            radioID = radio.getUid() + "-" + i;

            String      input    =
                HtmlElement
                    .builder()
                    .isSingleTag(true)
                    .tag(TAG_INPUT)
                    .dataAttribute(DATA_ATT_TOOLTIP, box.getTooltip().isBlank() ? null : htmlEscape(box.getTooltip()))
                    .attribute(
                        Attribute
                            .builder()
                            .name(ATT_ID)
                            .value(radioID)
                            .build())
                    .attribute(
                        Attribute
                            .builder()
                            .name(ATT_TYPE)
                            .value(radio.getType().name().toLowerCase())
                            .build())
                    .attribute(
                        Attribute
                            .builder()
                            .name(ATT_VALUE)
                            .value(htmlEscape(box.getValue()))
                            .build())
                    .attribute(
                        Attribute
                            .builder()
                            .name(ATT_NAME)
                            .value(radio.getUid())
                            .build())
                    .attribute(
                        Attribute
                            .builder()
                            .name(ATT_ON_INPUT)
                            .value(radio.getOnInput())
                            .build())
                    .attribute(
                        Attribute
                            .builder()
                            .name(ATT_CHECKED)
                            .active(checkFirst || box.isChecked())
                            .build())
                    .clazz(CLASS_FORM_CHECK_INPUT)
                    .build()
                    .html();

            String      btnLabel =
                HtmlElement
                    .builder()
                    .tag(TAG_LABEL)
                    .attribute(
                        Attribute
                            .builder()
                            .name(ATT_FOR)
                            .value(radioID)
                            .build())
                    .clazz(CLASS_FORM_CHECK_LABEL)
                    .clazz(CLASS_USER_SELECT_NONE)
                    .content(box.getText())
                    .build()
                    .html();

            HtmlElement innerDiv =
                HtmlElement
                    .builder()
                    .tag(TAG_DIV)
                    .clazz(CLASS_FORM_CHECK)
                    .content(input)
                    .content(btnLabel)
                    .build();

            i++;
            checkFirst = false;

            buttons.add(innerDiv.html());
        }

        String      legend   =
            createLegend(radio)
                .html();

        HtmlElement outerDiv =
            HtmlElement
                .builder()
                .tag(TAG_DIV)
                .clazz(CLASS_FORM_CHECK)
                .attribute(
                    Attribute
                        .builder()
                        .name(ATT_ID)
                        .value(radio.getUid())
                        .build())
                .dataAttribute(DATA_ATT_SUBMIT_ID, formId)
                .dataAttribute(DATA_ATT_SUBMIT_AS, radio.getSubmitAs())
                .dataAttribute(DATA_ATT_VALUE_TYPE, radio.getType().name())
                .content(legend)
                .contents(buttons)
                .build();

        return outerDiv.html();
    }

    private String render(Select select, String formId)
    {
        ArrayList<String> options = new ArrayList<>();

        for (InputValue option : select.getValues())
        {
            options.add(
                HtmlElement
                    .builder()
                    .tag(TAG_OPTION)
                    .dataAttribute(DATA_ATT_TOOLTIP, option.getTooltip().isBlank() ? null : htmlEscape(option.getTooltip()))
                    .attribute(
                        Attribute
                            .builder()
                            .name(ATT_VALUE)
                            .value(htmlEscape(option.getValue()))
                            .build())
                    .attribute(
                        Attribute
                            .builder()
                            .name(ATT_SELECTED)
                            .active(option.getValue().equals(select.getSelected()))
                            .build())
                    .content(option.getText())
                    .build()
                    .html());
        }

        String legend =
            createLegend(select)
                .html();

        String input  =
            createInput(select, formId, CLASS_FORM_SELECT)
                .toBuilder()
                .tag(TAG_SELECT)
                .isSingleTag(false)
                .contents(options)
                .build()
                .html();

        return createInputWrapper(
            legend,
            resolveMandatoryIcon(select.isMandatory(), formId, select.getSubmitAs(), select.getMandatoryText()),
            resolveWarningIcon(formId, select.getSubmitAs()),
            resolveErrorIcon(formId, select.getSubmitAs()),
            resolveInfoIcon(formId, select.getSubmitAs(), select.getInfoText(), select.getInfoUrl()),
            input);
    }

    private String render(Slider slider, String formId)
    {
        String legend =
            createLegend(slider)
                .html();

        String input  =
            HtmlElement
                .builder()
                .isSingleTag(true)
                .tag(TAG_INPUT)
                .attribute(
                    Attribute
                        .builder()
                        .name(ATT_ID)
                        .value(slider.getUid())
                        .build())
                .attribute(
                    Attribute
                        .builder()
                        .name(ATT_TYPE)
                        .value("range")
                        .build())
                .attribute(
                    Attribute
                        .builder()
                        .name(ATT_VALUE)
                        .value(
                            slider.isCustom()
                                ? String.valueOf(sliderIndex(slider))
                                : plain((java.lang.Number) slider.getValue()))
                        .build())
                .attribute(
                    Attribute
                        .builder()
                        .name(ATT_ON_INPUT)
                        .value(slider.getOnInput())
                        .build())
                .attribute(
                    Attribute
                        .builder()
                        .name(ATT_MIN)
                        .value(slider.isCustom() ? "0" : plain(slider.getMin()))
                        .build())
                .attribute(
                    Attribute
                        .builder()
                        .name(ATT_MAX)
                        .value(
                            slider.isCustom()
                                ? String.valueOf(slider.getCustomValues().size() - 1)
                                : plain(slider.getMax()))
                        .build())
                .attribute(
                    Attribute
                        .builder()
                        .name(ATT_STEP)
                        .value(slider.isCustom() ? "1" : plain(slider.getStep()))
                        .build())
                .attribute(
                    Attribute
                        .builder()
                        .name(ATT_DISABLED)
                        .value(ATT_DISABLED)
                        .active(slider.isDisabled())
                        .build())
                .attribute(
                    Attribute
                        .builder()
                        .name(ATT_ARIA_VALUETEXT)
                        .value(String.valueOf(slider.getValue()))
                        .active(slider.isCustom())
                        .build())
                .clazz("form-slider-input")
                .dataAttribute(DATA_ATT_SUBMIT_ID, formId)
                .dataAttribute(DATA_ATT_SUBMIT_AS, slider.getSubmitAs())
                .dataAttribute(DATA_ATT_VALUE_TYPE, slider.getType().name())
                .dataAttributes(slider.getDataAttributes())
                .build()
                .html();

        String ticks  =
            HtmlElement
                .builder()
                .tag(TAG_DIV)
                .clazz("input-slider-ticks")
                .build()
                .html();

        String style  = String.format("--ci-slider-ratio: %s; --ci-slider-ticks: %d", sliderRatio(slider), sliderTickIntervals(slider));

        String field  =
            slider.isCustom()
                ? renderCustomSliderField(slider, input, ticks, style)
                : renderNumericSliderField(slider, input, ticks, style);

        return createInputWrapper(
            legend,
            resolveMandatoryIcon(slider.isMandatory(), formId, slider.getSubmitAs(), slider.getMandatoryText()),
            resolveWarningIcon(formId, slider.getSubmitAs()),
            resolveErrorIcon(formId, slider.getSubmitAs()),
            resolveInfoIcon(formId, slider.getSubmitAs(), slider.getInfoText(), slider.getInfoUrl()),
            field);
    }

    private String renderNumericSliderField(Slider slider, String input, String ticks, String style)
    {
        String min    =
            HtmlElement
                .builder()
                .tag("span")
                .clazz("input-slider-min")
                .content(plain(slider.getMin()))
                .build()
                .html();

        String value  =
            HtmlElement
                .builder()
                .tag("output")
                .attribute(
                    Attribute
                        .builder()
                        .name(ATT_FOR)
                        .value(slider.getUid())
                        .build())
                .clazz("input-slider-value")
                .content(plain((java.lang.Number) slider.getValue()))
                .build()
                .html();

        String max    =
            HtmlElement
                .builder()
                .tag("span")
                .clazz("input-slider-max")
                .content(plain(slider.getMax()))
                .build()
                .html();

        return HtmlElement
            .builder()
            .tag(TAG_DIV)
            .attribute(
                Attribute
                    .builder()
                    .name(ATT_STYLE)
                    .value(style)
                    .build())
            .clazz("input-slider")
            .content(min)
            .content(input)
            .content(max)
            .content(ticks)
            .content(value)
            .build()
            .html();
    }

    private String renderCustomSliderField(Slider slider, String input, String ticks, String style)
    {
        StringBuilder labels = new StringBuilder();
        List<String>  values = slider.getCustomValues();
        int           active = sliderIndex(slider);

        for (int i = 0; i < values.size(); i++)
        {
            labels
                .append(
                    HtmlElement
                        .builder()
                        .tag("span")
                        .attribute(
                            Attribute
                                .builder()
                                .name(ATT_STYLE)
                                .value("--ci-slider-index: " + i)
                                .build())
                        .clazz(i == active ? "input-slider-label input-slider-label-active" : "input-slider-label")
                        .content(htmlEscape(values.get(i)))
                        .build()
                        .html());
        }

        String labelRow =
            HtmlElement
                .builder()
                .tag(TAG_DIV)
                .clazz("input-slider-labels")
                .content(labels.toString())
                .build()
                .html();

        return HtmlElement
            .builder()
            .tag(TAG_DIV)
            .attribute(
                Attribute
                    .builder()
                    .name(ATT_STYLE)
                    .value(style)
                    .build())
            .clazz("input-slider")
            .clazz("input-slider-custom")
            .content(input)
            .content(ticks)
            .content(labelRow)
            .build()
            .html();
    }

    private String render(Switch switch1, String formId)
    {
        String input  =
            HtmlElement
                .builder()
                .isSingleTag(true)
                .tag(TAG_INPUT)
                .attribute(
                    Attribute
                        .builder()
                        .name(ATT_ID)
                        .value(switch1.getUid())
                        .build())
                .attribute(
                    Attribute
                        .builder()
                        .name(ATT_TYPE)
                        .value("checkbox")
                        .build())
                .attribute(
                    Attribute
                        .builder()
                        .name(ATT_ROLE)
                        .value("switch")
                        .build())
                .attribute(
                    Attribute
                        .builder()
                        .name(ATT_ON_INPUT)
                        .value(switch1.getOnInput())
                        .build())
                .attribute(
                    Attribute
                        .builder()
                        .name(ATT_CHECKED)
                        .active(switch1.getChecked())
                        .build())
                .attribute(
                    Attribute
                        .builder()
                        .name(ATT_DISABLED)
                        .value(ATT_DISABLED)
                        .active(switch1.isDisabled())
                        .build())
                .clazz(CLASS_FORM_CHECK_INPUT)
                .clazz("form-switch-input")
                .clazz("input-field")
                .dataAttribute(DATA_ATT_SUBMIT_ID, formId)
                .dataAttribute(DATA_ATT_SUBMIT_AS, switch1.getSubmitAs())
                .dataAttribute(DATA_ATT_VALUE_TYPE, switch1.getType().name())
                .dataAttributes(switch1.getDataAttributes())
                .build()
                .html();

        String legend =
            createLegend(switch1)
                .html();

        return createInputWrapper(
            "input-container-switch",
            legend,
            resolveMandatoryIcon(switch1.isMandatory(), formId, switch1.getSubmitAs(), switch1.getMandatoryText()),
            resolveWarningIcon(formId, switch1.getSubmitAs()),
            resolveErrorIcon(formId, switch1.getSubmitAs()),
            resolveInfoIcon(formId, switch1.getSubmitAs(), switch1.getInfoText(), switch1.getInfoUrl()),
            input);
    }

    private String render(Tag tag, String formId)
    {
        String legend =
            createLegend(tag)
                .html();

        String input  =
            createInput(tag, formId, CLASS_FORM_CONTROL)
                .toBuilder()
                .attribute(
                    Attribute
                        .builder()
                        .name(ATT_VALUE)
                        .value(
                            htmlEscape(
                                tag
                                    .getTags()
                                    .stream()
                                    .collect(Collectors.joining(","))))
                        .build())
                .attribute(
                    Attribute
                        .builder()
                        .name("pattern")
                        .value(tag.getPattern())
                        .active(tag.getPattern() != null && !tag.getPattern().isBlank())
                        .build())
                .build()
                .html();

        return createInputWrapper(
            legend,
            resolveMandatoryIcon(tag.isMandatory(), formId, tag.getSubmitAs(), tag.getMandatoryText()),
            resolveWarningIcon(formId, tag.getSubmitAs()),
            resolveErrorIcon(formId, tag.getSubmitAs()),
            resolveInfoIcon(formId, tag.getSubmitAs(), tag.getInfoText(), tag.getInfoUrl()),
            input);
    }

    private String render(Textarea textarea, String formId)
    {
        String legend =
            createLegend(textarea)
                .html();

        String input  =
            createInput(textarea, formId, CLASS_FORM_CONTROL)
                .toBuilder()
                .tag("textarea")
                .isSingleTag(false)
                .attribute(
                    Attribute
                        .builder()
                        .name(ATT_PLACEHOLDER)
                        .value(htmlEscape(textarea.getPlaceholder()))
                        .build())
                .attribute(
                    Attribute
                        .builder()
                        .name(ATT_ON_KEY_DOWN)
                        .value(textarea.getOnKeydown())
                        .build())
                .attribute(
                    Attribute
                        .builder()
                        .name("rows")
                        .value(String.valueOf(textarea.getRows()))
                        .build())
                .attribute(
                    Attribute
                        .builder()
                        .name("cols")
                        .value(String.valueOf(textarea.getCols()))
                        .active(textarea.getCols() != null)
                        .build())
                .dataAttribute(DATA_ATT_MAX_CHARACTERS, String.valueOf(textarea.getMaxCharacters()))
                .content(htmlEscape(textarea.getValue()))
                .build()
                .html();

        return createInputWrapper(
            legend,
            resolveMandatoryIcon(textarea.isMandatory(), formId, textarea.getSubmitAs(), textarea.getMandatoryText()),
            resolveWarningIcon(formId, textarea.getSubmitAs()),
            resolveErrorIcon(formId, textarea.getSubmitAs()),
            resolveInfoIcon(formId, textarea.getSubmitAs(), textarea.getInfoText(), textarea.getInfoUrl()),
            input);
    }

    private String render(Textbox textbox)
    {
        String legend  =
            HtmlElement
                .builder()
                .tag(TAG_LABEL)
                .clazz(CLASS_USER_SELECT_NONE)
                .dataAttribute(DATA_ATT_TOOLTIP, textbox.getTooltip().isBlank() ? null : htmlEscape(textbox.getTooltip()))
                .content(htmlEscape(textbox.getName()))
                .build()
                .html();

        String content =
            HtmlElement
                .builder()
                .tag(TAG_DIV)
                .attribute(
                    Attribute
                        .builder()
                        .name(ATT_ID)
                        .value(textbox.getUid())
                        .build())
                .clazz("text-preserve-newline")
                .classes(textbox.getClasses())
                .dataAttributes(textbox.getDataAttributes())
                .content(htmlEscape(textbox.getText()))
                .build()
                .html();

        return createInputWrapper(legend, "", "", "", "", content);
    }

    private String render(Textfield textfield, String formId)
    {
        String legend =
            createLegend(textfield)
                .html();

        String input  =
            createInput(textfield, formId, CLASS_FORM_CONTROL)
                .toBuilder()
                .attribute(
                    Attribute
                        .builder()
                        .name(ATT_VALUE)
                        .value(htmlEscape(textfield.getValue()))
                        .build())
                .attribute(
                    Attribute
                        .builder()
                        .name(ATT_PLACEHOLDER)
                        .value(htmlEscape(textfield.getPlaceholder()))
                        .build())
                .attribute(
                    Attribute
                        .builder()
                        .name(ATT_ON_KEY_DOWN)
                        .value(textfield.getOnKeydown())
                        .build())
                .clazz(CLASS_FORM_CONTROL)
                .dataAttribute(DATA_ATT_MAX_CHARACTERS, String.valueOf(textfield.getMaxCharacters()))
                .dataAttribute(DATA_ATT_ON_ENTER_PRESS, textfield.getOnEnterPress())
                .build()
                .html();

        return createInputWrapper(
            legend,
            resolveMandatoryIcon(textfield.isMandatory(), formId, textfield.getSubmitAs(), textfield.getMandatoryText()),
            resolveWarningIcon(formId, textfield.getSubmitAs()),
            resolveErrorIcon(formId, textfield.getSubmitAs()),
            resolveInfoIcon(formId, textfield.getSubmitAs(), textfield.getInfoText(), textfield.getInfoUrl()),
            input);
    }

    /**
     * Create {@code warning icon} for an input field.
     * 
     * @param formId to which this field belongs for identifiaction
     * @param submitId of the field for identification
     * @return the resolved icon
     */
    private String resolveMandatoryIcon(boolean active, String formId, String submitId, String text)
    {
        if (active)
        {
            return resolveIcon(formId, submitId, "mandatory", "bi-asterisk", text, null);
        }

        return "";
    }

    /**
     * Create {@code warning icon} for an input field.
     * 
     * @param formId to which this field belongs for identifiaction
     * @param submitId of the field for identification
     * @return the resolved icon
     */
    private String resolveWarningIcon(String formId, String submitId)
    {
        return resolveIcon(formId, submitId, MarkerCategory.WARNING.name().toLowerCase(), "bi-exclamation-triangle", null, null);
    }

    /**
     * Create {@code error icon} for an input field.
     * 
     * @param formId to which this field belongs for identifiaction
     * @param submitId of the field for identification
     * @return the resolved icon
     */
    private String resolveErrorIcon(String formId, String submitId)
    {
        return resolveIcon(formId, submitId, MarkerCategory.ERROR.name().toLowerCase(), "bi-exclamation-triangle", null, null);
    }

    /**
     * Create {@code info icon} for an input field.
     * 
     * @param formId to which this field belongs for identifiaction
     * @param submitId of the field for identification
     * @return the resolved icon
     */
    private String resolveInfoIcon(String formId, String submitId, String text, String url)
    {
        return resolveIcon(formId, submitId, "info", "bi-exclamation-circle", text, url);
    }

    /**
     * Resolve a generic icon.
     * 
     * @param formId to which this field belongs for identifiaction
     * @param submitId of the field for identification
     * @param type of the icon
     * @param image of the icon
     * @param text of the tooltip
     * @param url for info-fetch
     * @return the resolved icon
     */
    private String resolveIcon(String formId, String submitId, String type, String image, String text, String url)
    {
        String                         id       = "input-icon-" + formId + "-" + type + "-" + submitId;
        boolean                        hasUrl   = url != null && !url.isBlank();

        boolean                        isHidden =
            List.of(MarkerCategory.WARNING.name().toLowerCase(), MarkerCategory.ERROR.name().toLowerCase()).contains(type)
                || (text == null || text.isBlank());

        HtmlElement.HtmlElementBuilder icon     =
            HtmlElement
                .builder()
                .tag(TAG_DIV)
                .attribute(
                    Attribute
                        .builder()
                        .name(ATT_ID)
                        .value(id)
                        .build())
                .clazz("input-icon-container")
                .clazz("input-icon-" + type)
                .clazz("tooltip-container")
                .clazz(MarkerCategory.ERROR.name().equalsIgnoreCase(type) ? "error-marker" : "")
                .clazz(isHidden ? CLASS_HIDDEN : "")
                .content(
                    HtmlElement
                        .builder()
                        .tag("i")
                        .clazz("bi")
                        .clazz(image)
                        .clazz(type + "-icon")
                        .build()
                        .html())
                .content(
                    HtmlElement
                        .builder()
                        .tag(TAG_DIV)
                        .clazz("input-icon-text")
                        .clazz("tooltip-text")
                        .attribute(
                            Attribute
                                .builder()
                                .name(ATT_ID)
                                .value(id + "-text")
                                .build())
                        .content(text == null ? "" : htmlEscape(text))
                        .build()
                        .html());

        if (hasUrl)
        {
            icon.dataAttribute("fetch-input-info-url", url);
        }

        return icon
            .build()
            .html();
    }

    private String render(SVGElement element, String svgId)
    {
        return switch (element.getType())
        {
            case GROUP -> render((SVGGroup) element, svgId);
            case LINE -> render((SVGLine) element, svgId);
            case TEXT -> render((SVGText) element, svgId);
            case PATH -> render((SVGPath) element, svgId);
        };
    }

    private String render(SVGGroup group, String svgId)
    {
        List<String> contents = new ArrayList<>();

        for (SVGElement element : group.getChildren())
        {
            contents.add(render(element, svgId));
        }

        HtmlElement elementMapper =
            HtmlElement
                .builder()
                .tag(TAG_GROUP)
                .isSingleTag(false)
                .attribute(
                    Attribute
                        .builder()
                        .name(ATT_ID)
                        .value(group.getUid())
                        .build())
                .attribute(
                    Attribute
                        .builder()
                        .name(ATT_STYLE)
                        .active(!group.getStyle().isBlank())
                        .value(group.getStyle())
                        .build())
                .classes(group.getClasses())
                .dataAttributes(group.getDataAttributes())
                .dataAttribute(DATA_ATT_TOOLTIP, group.getTooltip().isBlank() ? null : group.getTooltip())
                .contents(contents)
                .build();

        return elementMapper.html();
    }

    private String render(SVGLine line, String svgId)
    {
        HtmlElement elementMapper =
            HtmlElement
                .builder()
                .tag(TAG_LINE)
                .isSingleTag(true)
                .attribute(
                    Attribute
                        .builder()
                        .name(ATT_ID)
                        .value(line.getUid())
                        .build())
                .attribute(
                    Attribute
                        .builder()
                        .name(ATT_STYLE)
                        .active(!line.getStyle().isBlank())
                        .value(line.getStyle())
                        .build())
                .attribute(
                    Attribute
                        .builder()
                        .name("x1")
                        .value(DOUBLE_FORMATER.format(line.getX1()))
                        .build())
                .attribute(
                    Attribute
                        .builder()
                        .name("y1")
                        .value(DOUBLE_FORMATER.format(line.getY1()))
                        .build())
                .attribute(
                    Attribute
                        .builder()
                        .name("x2")
                        .value(DOUBLE_FORMATER.format(line.getX2()))
                        .build())
                .attribute(
                    Attribute
                        .builder()
                        .name("y2")
                        .value(DOUBLE_FORMATER.format(line.getY2()))
                        .build())
                .classes(line.getClasses())
                .dataAttributes(line.getDataAttributes())
                .dataAttribute(DATA_ATT_TOOLTIP, line.getTooltip().isBlank() ? null : line.getTooltip())
                .build();

        return elementMapper.html();
    }

    private String render(SVGPath path, String svgId)
    {
        List<String> commands = new ArrayList<>();

        for (SVGPathCommand command : path.getCommands())
        {
            commands.add(command.getCommandString());
        }

        HtmlElement elementMapper =
            HtmlElement
                .builder()
                .tag(TAG_PATH)
                .isSingleTag(true)
                .attribute(
                    Attribute
                        .builder()
                        .name(ATT_ID)
                        .value(path.getUid())
                        .build())
                .attribute(
                    Attribute
                        .builder()
                        .name(ATT_STYLE)
                        .active(!path.getStyle().isBlank())
                        .value(path.getStyle())
                        .build())
                .attribute(
                    Attribute
                        .builder()
                        .name(ATT_D)
                        .value(StringAdapter.separate(commands, " "))
                        .build())
                .classes(path.getClasses())
                .dataAttributes(path.getDataAttributes())
                .dataAttribute(DATA_ATT_TOOLTIP, path.getTooltip().isBlank() ? null : path.getTooltip())
                .build();

        return elementMapper.html();
    }

    private String render(SVGText line, String svgId)
    {
        HtmlElement elementMapper =
            HtmlElement
                .builder()
                .tag(TAG_TEXT)
                .isSingleTag(true)
                .attribute(
                    Attribute
                        .builder()
                        .name(ATT_ID)
                        .value(line.getUid())
                        .build())
                .attribute(
                    Attribute
                        .builder()
                        .name(ATT_STYLE)
                        .active(!line.getStyle().isBlank())
                        .value(line.getStyle())
                        .build())
                .attribute(
                    Attribute
                        .builder()
                        .name("x")
                        .value(DOUBLE_FORMATER.format(line.getX()))
                        .build())
                .attribute(
                    Attribute
                        .builder()
                        .name("y")
                        .value(DOUBLE_FORMATER.format(line.getY()))
                        .build())
                .attribute(
                    Attribute
                        .builder()
                        .name("dx")
                        .value(DOUBLE_FORMATER.format(line.getDx()))
                        .build())
                .attribute(
                    Attribute
                        .builder()
                        .name("y2")
                        .value(DOUBLE_FORMATER.format(line.getDy()))
                        .build())
                .attribute(
                    Attribute
                        .builder()
                        .name("rotate")
                        .value(DOUBLE_FORMATER.format(line.getRotate()))
                        .build())
                .attribute(
                    Attribute
                        .builder()
                        .name("y2")
                        .active(line.getTextLength() > 0d)
                        .value(DOUBLE_FORMATER.format(line.getTextLength()))
                        .build())
                .classes(line.getClasses())
                .dataAttributes(line.getDataAttributes())
                .dataAttribute(DATA_ATT_TOOLTIP, line.getTooltip().isBlank() ? null : line.getTooltip())
                .build();

        return elementMapper.html();
    }
}
