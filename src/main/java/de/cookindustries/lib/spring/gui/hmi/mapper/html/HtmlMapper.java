/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.hmi.mapper.html;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import de.cookindustries.lib.spring.gui.hmi.container.*;
import de.cookindustries.lib.spring.gui.hmi.input.*;
import de.cookindustries.lib.spring.gui.hmi.input.Number;
import de.cookindustries.lib.spring.gui.hmi.input.util.InputValue;
import de.cookindustries.lib.spring.gui.hmi.input.util.MarkerCategory;
import de.cookindustries.lib.spring.gui.util.StringAdapter;
import de.cookindustries.lib.spring.gui.util.StringConcat;
import lombok.Data;

/**
 * @since 1.0.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
@Data
public final class HtmlMapper
{

    private static final String TAG_DIV                 = "div";
    private static final String TAG_LABEL               = "label";
    private static final String TAG_INPUT               = "input";
    private static final String TAG_SELECT              = "select";
    private static final String TAG_OPTION              = "option";

    private static final String ATT_ID                  = "id";
    private static final String ATT_NAME                = "name";
    private static final String ATT_TYPE                = "type";
    private static final String ATT_FOR                 = "for";
    private static final String ATT_ON_INPUT            = "oninput";
    private static final String ATT_ON_CLICK            = "onclick";
    private static final String ATT_CHECKED             = "checked";
    private static final String ATT_SELECTED            = "selected";
    private static final String ATT_MIN                 = "min";
    private static final String ATT_MAX                 = "max";
    private static final String ATT_PLACEHOLDER         = "placeholder";
    private static final String ATT_HREF                = "href";
    private static final String ATT_TARGET              = "target";
    private static final String ATT_SRC                 = "src";
    private static final String ATT_VALUE               = "value";
    private static final String ATT_ON_KEY_DOWN         = "onkeydown";

    private static final String CLASS_INPUT_LEGEND      = "input-legend";
    private static final String CLASS_FORM_CONTROL      = "form-control";
    private static final String CLASS_HIDDEN            = "hidden";
    private static final String CLASS_USER_SELECT_NONE  = "user-select-none";
    private static final String CLASS_FORM_CHECK        = "form-checkbox";
    private static final String CLASS_FORM_CHECK_INPUT  = "form-checkbox-input";
    private static final String CLASS_FORM_CHECK_LABEL  = "form-checkbox-label";
    private static final String CLASS_FORM_SELECT       = "form-select";
    private static final String INPUT_CONTAINER         = "input-container";

    private static final String DATA_ATT_SUBMIT_ID      = "submit-id";
    private static final String DATA_ATT_SUBMIT_AS      = "submit-as";
    private static final String DATA_ATT_VALUE_TYPE     = "value-type";
    private static final String DATA_ATT_MAX_CHARACTERS = "max-characters";

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
    public static String map(List<Container> containers)
    {
        HtmlMapper   mapper = new HtmlMapper();
        StringConcat sc     = new StringConcat();

        containers
            .stream()
            .forEach(c -> sc.append(mapper.render(c)));

        return sc.toString();
    }

    /**
     * A temporary result for not yet implemented objects<br>
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
            case BUTTON_BAR -> render((ButtonBarContainer) container);
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
            case TAB -> render((TabContainer) container);
            case TABLE -> render((TableContainer) container);
            case TABLE_ROW -> render((TableRowContainer) container);
            case TEXT -> render((TextContainer) container);
        };
    }

    private String render(AudioContainer audioContainer)
    {
        // TODO: implement
        return elementNotYetImplemented("audio");
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
                    .dataAttribute("burger-url", item.getUrl())
                    .content(
                        HtmlElement
                            .builder()
                            .tag(TAG_DIV)
                            .clazz("burger-icon-slot")
                            .content(
                                HtmlElement
                                    .builder()
                                    .tag("img")
                                    .isSingleTag(true)
                                    .clazz("burger-icon-slot")
                                    .attribute(
                                        Attribute
                                            .builder()
                                            .name(ATT_SRC)
                                            .value(item.getImage())
                                            .build())
                                    .inactive(item.getImage() == null || item.getImage().isBlank())
                                    .build()
                                    .html())
                            .build()
                            .html())
                    .content(
                        HtmlElement
                            .builder()
                            .tag(TAG_DIV)
                            .clazz("burger-text")
                            .content(item.getText())
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
                        .tag("img")
                        .isSingleTag(true)
                        .clazz("burger-icon")
                        .attribute(
                            Attribute
                                .builder()
                                .name(ATT_SRC)
                                .value(burger.getImage())
                                .build())
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
        HtmlElement elementMapper =
            HtmlElement
                .builder()
                .tag("button")
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
                        .active(button.getOnClick() != null)
                        .value(button.getOnClick())
                        .build())
                .attribute(
                    Attribute
                        .builder()
                        .name("title")
                        .active(button.getTitle() != null && !button.getTitle().isBlank())
                        .value(button.getTitle())
                        .build())
                .clazz("btn")
                .clazz(button.getBtnClass().getClassName())
                .classes(button.getClasses())
                .dataAttributes(button.getDataAttributes())
                .content(button.getText())
                .build();

        return elementMapper.html();
    }

    private String render(ButtonBarContainer buttonBarContainer)
    {
        return HtmlElement
            .builder()
            .tag(TAG_DIV)
            .attribute(
                Attribute
                    .builder()
                    .name(ATT_ID)
                    .value(buttonBarContainer.getUid())
                    .build())
            .classes(buttonBarContainer.getClasses())
            .dataAttributes(buttonBarContainer.getDataAttributes())
            .contents(
                buttonBarContainer
                    .getButtons()
                    .stream()
                    .map(b -> render(b))
                    .toList())
            .build()
            .html();
    }

    private String render(ButtonIcon buttonIcon)
    {
        HtmlElement elementMapper =
            HtmlElement
                .builder()
                .tag(TAG_INPUT)
                .isSingleTag(true)
                .attribute(
                    Attribute
                        .builder()
                        .name(ATT_ID)
                        .value(buttonIcon.getUid())
                        .build())
                .attribute(
                    Attribute
                        .builder()
                        .name(ATT_TYPE)
                        .value("image")
                        .build())
                .attribute(
                    Attribute
                        .builder()
                        .name(ATT_ON_CLICK)
                        .active(buttonIcon.getOnClick() != null)
                        .value(buttonIcon.getOnClick())
                        .build())
                .attribute(
                    Attribute
                        .builder()
                        .name("title")
                        .active(buttonIcon.getTitle() != null && !buttonIcon.getTitle().isBlank())
                        .value(buttonIcon.getTitle())
                        .build())
                .attribute(
                    Attribute
                        .builder()
                        .name(ATT_SRC)
                        .value(buttonIcon.getImage())
                        .build())
                .clazz("btn")
                .clazz("btn-icon")
                .clazz(buttonIcon.getBtnClass().getClassName())
                .classes(buttonIcon.getClasses())
                .dataAttributes(buttonIcon.getDataAttributes())
                .dataAttribute("tooltip", buttonIcon.getTooltip().isBlank() ? null : buttonIcon.getTooltip())
                .build();

        return elementMapper.html();
    }

    private String render(ContentContainer contentContainer)
    {
        String      content       =
            contentContainer
                .getContents()
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
                        .value(contentContainer.getUid())
                        .build())
                .attribute(
                    Attribute
                        .builder()
                        .name(ATT_ON_CLICK)
                        .active(contentContainer.getOnClick() != null)
                        .value(contentContainer.getOnClick())
                        .build())
                .classes(contentContainer.getClasses())
                .dataAttributes(contentContainer.getDataAttributes())
                .content(content)
                .build();

        return elementMapper.html();
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
                .attribute(Attribute
                    .builder()
                    .name(ATT_ID)
                    .value(formContainer.getUid())
                    .build())
                .attribute(Attribute
                    .builder()
                    .name(ATT_ON_CLICK)
                    .value(formContainer.getOnClick())
                    .build())
                .clazz("form-container")
                .classes(formContainer.getClasses())
                .dataAttributes(formContainer.getDataAttributes())
                .content(content)
                .build();

        return elementMapper.html();
    }

    private String render(HiddenContainer hiddenContainer)
    {
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
                .content(render(hiddenContainer.getChild()))
                .build();

        return elementMapper.html();
    }

    private String render(ImageContainer imageContainer)
    {
        HtmlElement elementMapper =
            HtmlElement
                .builder()
                .tag("img")
                .attribute(
                    Attribute
                        .builder()
                        .name(ATT_ID)
                        .value(imageContainer.getUid())
                        .build())
                .attribute(
                    Attribute
                        .builder()
                        .name(ATT_ON_CLICK)
                        .active(imageContainer.getOnClick() != null)
                        .value(imageContainer.getOnClick())
                        .build())
                .attribute(
                    Attribute
                        .builder()
                        .name(ATT_SRC)
                        .value(imageContainer.getSrc())
                        .build())
                .classes(imageContainer.getClasses())
                .dataAttributes(imageContainer.getDataAttributes())
                .dataAttribute("tooltip", imageContainer.getTooltip().isBlank() ? null : imageContainer.getTooltip())
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
                        .name(ATT_ON_CLICK)
                        .active(linkContainer.getOnClick() != null)
                        .value(linkContainer.getOnClick())
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
                .dataAttribute("tooltip", linkContainer.getTooltip().isBlank() ? null : linkContainer.getTooltip())
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
                .btnClass(modalContainer.getBtnClassRight())
                .text(modalContainer.getBtnNameRight())
                .onClick(modalContainer.getBtnFunctionRight())
                .build());

        if (modalContainer.getBtnNameCenter() != null && !modalContainer.getBtnNameCenter().isEmpty())
        {
            buttons.add(
                Button
                    .builder()
                    .btnClass(modalContainer.getBtnClassCenter())
                    .text(modalContainer.getBtnNameCenter())
                    .onClick(modalContainer.getBtnFunctionCenter())
                    .build());
        }

        if (modalContainer.getBtnNameLeft() != null && !modalContainer.getBtnNameLeft().isEmpty())
        {
            buttons.add(
                Button
                    .builder()
                    .btnClass(modalContainer.getBtnClassLeft())
                    .text(modalContainer.getBtnNameLeft())
                    .onClick(modalContainer.getBtnFunctionLeft())
                    .build());
        }

        ButtonBarContainer buttonBar     =
            ButtonBarContainer.builder()
                .clazz("modal-buttons")
                .buttons(buttons)
                .build();

        ContentContainer   modalInlay    =
            ContentContainer
                .builder()
                .clazz("modal-inlay")
                .content(
                    TextContainer
                        .builder()
                        .clazz("modal-name")
                        .clazz("text-bold")
                        .text(modalContainer.getName())
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

        HtmlElement        elementMapper =
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
                .dataAttribute("close-on-overlay", modalContainer.getCloseOnOverlayClick().toString())
                .dataAttributes(modalContainer.getDataAttributes())
                .content(render(modalInlay))
                .build();

        return elementMapper.html();
    }

    private String render(SplittedContainer splittedContainer)
    {
        String      head          = render(splittedContainer.getHead());
        String      tail          = render(splittedContainer.getTail());

        HtmlElement elementMapper =
            HtmlElement
                .builder()
                .tag(TAG_DIV)
                .attribute(
                    Attribute
                        .builder()
                        .name(ATT_ID)
                        .value(splittedContainer.getUid())
                        .build())
                .attribute(
                    Attribute
                        .builder()
                        .name(ATT_ID)
                        .value(splittedContainer.getOnClick())
                        .build())
                .clazz("d-flex")
                .clazz("justify-content-between")
                .classes(splittedContainer.getClasses())
                .dataAttributes(splittedContainer.getDataAttributes())
                .content(StringAdapter.from(head, tail))
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
                .map(name -> {
                                               return HtmlElement
                                                   .builder()
                                                   .tag(TAG_DIV)
                                                   .clazz("table-head-cell")
                                                   .content(name)
                                                   .build()
                                                   .html();
                                           })
                .toList();

        List<String> rows          =
            table
                .getRows()
                .stream()
                .map(tr -> render(tr))
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
                .clazz("table")
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
                .map(tr -> render(tr))
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
        HtmlElement elementMapper =
            HtmlElement
                .builder()
                .tag(textContainer.getInline() ? "span" : "p")
                .attribute(
                    Attribute
                        .builder()
                        .name(ATT_ID)
                        .value(textContainer.getUid())
                        .build())
                .attribute(
                    Attribute
                        .builder()
                        .name(ATT_ON_CLICK)
                        .active(textContainer.getOnClick() != null)
                        .value(textContainer.getOnClick())
                        .build())
                .classes(textContainer.getClasses())
                .dataAttributes(textContainer.getDataAttributes())
                .dataAttribute("tooltip", textContainer.getTooltip().isBlank() ? null : textContainer.getTooltip())
                .content(textContainer.getText())
                .build();

        return elementMapper.html();
    }

    private String render(HeadingContainer textHeaderContainer)
    {
        HtmlElement elementMapper =
            HtmlElement
                .builder()
                .tag(String.format("h%s", textHeaderContainer.getSize()))
                .attribute(
                    Attribute
                        .builder()
                        .name(ATT_ID)
                        .value(textHeaderContainer.getUid())
                        .build())
                .attribute(
                    Attribute
                        .builder()
                        .name(ATT_ON_CLICK)
                        .active(textHeaderContainer.getOnClick() != null)
                        .value(textHeaderContainer.getOnClick())
                        .build())
                .classes(textHeaderContainer.getClasses())
                .dataAttributes(textHeaderContainer.getDataAttributes())
                .dataAttribute("tooltip", textHeaderContainer.getTooltip().isBlank() ? null : textHeaderContainer.getTooltip())
                .content(textHeaderContainer.getText())
                .build();

        return elementMapper.html();
    }

    private String render(Input input, String formId)
    {
        return switch (input.getType())
        {
            case CHECKBOX -> render((Checkbox) input, formId);
            case CURRENCY -> render((Currency) input, formId);
            case DATE -> render((Date) input, formId);
            case FILE -> render((File) input, formId);
            case HIDDEN -> render((Hidden) input, formId);
            case LINK -> render((Link) input, formId);
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
            .dataAttribute("tooltip", input.getTooltip().isBlank() ? null : input.getTooltip())
            .content(input.getName())
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
                    .build())
            .clazz(formClass)
            .clazz("input-field")
            .dataAttribute(DATA_ATT_SUBMIT_ID, formId)
            .dataAttribute(DATA_ATT_SUBMIT_AS, input.getSubmitAs())
            .dataAttribute(DATA_ATT_VALUE_TYPE, input.getType().name())
            .dataAttributes(input.getDataAttributes())
            .build();
    }

    private String createInputWrapper(String legend, String warningIcon, String errorIcon, String infoIcon, String input)
    {
        return HtmlElement
            .builder()
            .tag(TAG_DIV)
            .clazz(INPUT_CONTAINER)
            .content(legend)
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

        checkbox.getBoxes().stream().forEach(box -> {

            String      boxId   = box.getId().isBlank() ? "" + (cnt.getAndIncrement()) : box.getId();
            String      id      = checkbox.getSubmitAs().isBlank() ? boxId : checkbox.getSubmitAs() + "-" + boxId;

            String      input   =
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
                            .active(box.getChecked())
                            .build())
                    .clazz(CLASS_FORM_CHECK_INPUT)
                    .dataAttribute(DATA_ATT_SUBMIT_ID, formId)
                    .dataAttribute(DATA_ATT_SUBMIT_AS, id)
                    .dataAttribute(DATA_ATT_VALUE_TYPE, checkbox.getType().name())
                    .dataAttributes(checkbox.getDataAttributes())
                    .build()
                    .html();

            String      label   =
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
                    .content(box.getText())
                    .build()
                    .html();

            HtmlElement wrapper =
                HtmlElement
                    .builder()
                    .tag(TAG_DIV)
                    .clazz("input-checkbox-wrapper")
                    .content(input)
                    .content(label)
                    .content(resolveWarningIcon(formId, id))
                    .content(resolveErrorIcon(formId, id))
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
                .content(resolveInfoIcon(formId, checkbox.getUid(), checkbox.getInfoText(), checkbox.getInfoUrl()))
                .content(checkboxes)
                .build();

        return elementMapper.html();
    }

    private String render(Currency currency, String formId)
    {
        // TODO: implement
        return elementNotYetImplemented("currency");
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
                        .value(hidden.getValue())
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

    private String render(Link link, String formId)
    {
        // TODO: implement
        return elementNotYetImplemented("link input");
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
                        .value(number.getPlaceholder())
                        .build())
                .build()
                .html();

        return createInputWrapper(
            legend,
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
                        .value(password.getPlaceholder())
                        .build())
                .build()
                .html();

        return createInputWrapper(
            legend,
            resolveWarningIcon(formId, password.getSubmitAs()),
            resolveErrorIcon(formId, password.getSubmitAs()),
            resolveInfoIcon(formId, password.getSubmitAs(), password.getInfoText(), password.getInfoUrl()),
            input);
    }

    private String render(Radio radio, String formId)
    {
        ArrayList<String> buttons = new ArrayList<>();

        Integer           i       = 0;
        Boolean           checked = false;
        String            radioID;

        for (InputValue btn : radio.getValues())
        {
            radioID = radio.getUid() + "-" + i;

            String      input    =
                HtmlElement
                    .builder()
                    .isSingleTag(true)
                    .tag(TAG_INPUT)
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
                            .value(btn.getValue())
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
                            .active(btn.getChecked())
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
                    .content(btn.getText())
                    .build()
                    .html();

            HtmlElement innerDiv =
                HtmlElement
                    .builder()
                    .tag(TAG_DIV)
                    .attribute(
                        Attribute
                            .builder()
                            .name(ATT_ID)
                            .value(radio.getUid())
                            .build())
                    .clazz(CLASS_FORM_CHECK)
                    .content(StringAdapter.from(input, btnLabel))
                    .build();

            i++;
            checked = checked || btn.getChecked();

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
                .dataAttribute(DATA_ATT_SUBMIT_ID, formId)
                .dataAttribute(DATA_ATT_SUBMIT_AS, radio.getSubmitAs())
                .dataAttribute(DATA_ATT_VALUE_TYPE, radio.getType().name())
                .content(StringAdapter.from(legend, StringAdapter.from(buttons)))
                .build();

        return outerDiv.html();
    }

    private String render(Select select, String formId)
    {
        ArrayList<String> selections = new ArrayList<>();

        for (InputValue selection : select.getValues())
        {
            String option =
                HtmlElement
                    .builder()
                    .tag(TAG_OPTION)
                    .attribute(
                        Attribute
                            .builder()
                            .name(ATT_VALUE)
                            .value(selection.getValue())
                            .build())
                    .attribute(
                        Attribute
                            .builder()
                            .name(ATT_SELECTED)
                            .active(selection.getValue().equals(select.getSelected()))
                            .build())
                    .content(selection.getText())
                    .build()
                    .html();

            selections.add(option);
        }

        String legend =
            createLegend(select)
                .html();

        String input  =
            createInput(select, formId, CLASS_FORM_SELECT)
                .toBuilder()
                .tag(TAG_SELECT)
                .isSingleTag(false)
                .content(StringAdapter.from(selections))
                .build()
                .html();

        return createInputWrapper(
            legend,
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
                        .value(String.valueOf(slider.getValue()))
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
                        .value(String.valueOf(slider.getMin()))
                        .build())
                .attribute(
                    Attribute
                        .builder()
                        .name(ATT_MAX)
                        .value(String.valueOf(slider.getMax()))
                        .build())
                .clazz("form-range")
                .dataAttribute(DATA_ATT_SUBMIT_ID, formId)
                .dataAttribute(DATA_ATT_SUBMIT_AS, slider.getSubmitAs())
                .dataAttribute(DATA_ATT_VALUE_TYPE, slider.getType().name())
                .dataAttributes(slider.getDataAttributes())
                .build()
                .html();

        return createInputWrapper(
            legend,
            resolveWarningIcon(formId, slider.getSubmitAs()),
            resolveErrorIcon(formId, slider.getSubmitAs()),
            resolveInfoIcon(formId, slider.getSubmitAs(), slider.getInfoText(), slider.getInfoUrl()),
            input);
    }

    private String render(Switch switch1, String formId)
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
                        .name(ATT_ON_INPUT)
                        .value(switch1.getOnInput())
                        .build())
                .attribute(
                    Attribute
                        .builder()
                        .name(ATT_CHECKED)
                        .active(switch1.getChecked())
                        .build())
                .clazz(CLASS_FORM_CHECK_INPUT)
                .dataAttribute(DATA_ATT_SUBMIT_ID, formId)
                .dataAttribute(DATA_ATT_SUBMIT_AS, switch1.getSubmitAs())
                .dataAttribute(DATA_ATT_VALUE_TYPE, switch1.getType().name())
                .dataAttributes(switch1.getDataAttributes())
                .build()
                .html();

        String      legend        =
            createLegend(switch1)
                .html();

        HtmlElement elementMapper =
            HtmlElement
                .builder()
                .tag(TAG_DIV)
                .clazz(CLASS_FORM_CHECK)
                .clazz("form-switch")
                .content(StringAdapter.from(input, legend))
                .build();

        return elementMapper.html();
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
                        .value(tag.getValue())
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
                        .value(textarea.getPlaceholder())
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
                .content(textarea.getValue())
                .build()
                .html();

        return createInputWrapper(
            legend,
            resolveWarningIcon(formId, textarea.getSubmitAs()),
            resolveErrorIcon(formId, textarea.getSubmitAs()),
            resolveInfoIcon(formId, textarea.getSubmitAs(), textarea.getInfoText(), textarea.getInfoUrl()),
            input);
    }

    private String render(Textbox textbox)
    {
        HtmlElement elementMapper =
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
                .dataAttribute("tooltip", textbox.getTooltip().isBlank() ? null : textbox.getTooltip())
                .content(textbox.getValue())
                .build();

        return elementMapper.html();
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
                        .value(textfield.getValue())
                        .build())
                .attribute(
                    Attribute
                        .builder()
                        .name(ATT_PLACEHOLDER)
                        .value(textfield.getPlaceholder())
                        .build())
                .attribute(
                    Attribute
                        .builder()
                        .name(ATT_ON_KEY_DOWN)
                        .value(textfield.getOnKeydown())
                        .build())
                .clazz(CLASS_FORM_CONTROL)
                .dataAttribute(DATA_ATT_MAX_CHARACTERS, String.valueOf(textfield.getMaxCharacters()))
                .build()
                .html();

        return createInputWrapper(
            legend,
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
    private String resolveWarningIcon(String formId, String submitId)
    {
        return resolveIcon(formId, submitId, MarkerCategory.WARNING.name().toLowerCase(), "/images/alert-triangle-warning.svg", null, null);
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
        return resolveIcon(formId, submitId, MarkerCategory.ERROR.name().toLowerCase(), "/images/alert-triangle-error.svg", null, null);
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
        return resolveIcon(formId, submitId, "info", "/images/alert-circle.svg", text, url);
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

        HtmlElement.HtmlElementBuilder icon     = HtmlElement
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
                    .tag("image")
                    .isSingleTag(true)
                    .clazz("input-icon")
                    .clazz(type + "-icon")
                    .attribute(
                        Attribute
                            .builder()
                            .name(ATT_SRC)
                            .value(image)
                            .build())
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
                    .content(text == null ? "" : text)
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
}
