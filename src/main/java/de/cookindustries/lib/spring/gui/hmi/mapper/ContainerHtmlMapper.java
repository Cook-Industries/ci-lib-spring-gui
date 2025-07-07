/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.hmi.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import de.cookindustries.lib.spring.gui.hmi.container.*;
import de.cookindustries.lib.spring.gui.hmi.input.*;
import de.cookindustries.lib.spring.gui.hmi.input.Number;
import de.cookindustries.lib.spring.gui.hmi.input.marker.Marker;
import de.cookindustries.lib.spring.gui.hmi.input.util.InputValue;
import de.cookindustries.lib.spring.gui.util.StringAdapter;
import de.cookindustries.lib.spring.gui.util.StringConcat;
import lombok.Data;

/**
 * @since 1.0.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
@Data
public final class ContainerHtmlMapper
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

    private static final String CLASS_FORM_LABEL        = "form-label";
    private static final String CLASS_FORM_CONTROL      = "form-control";
    private static final String CLASS_HIDDEN            = "hidden";
    private static final String CLASS_USER_SELECT_NONE  = "user-select-none";
    private static final String CLASS_FORM_CHECK        = "form-check";
    private static final String CLASS_FORM_CHECK_INPUT  = "form-check-input";
    private static final String CLASS_FORM_CHECK_LABEL  = "form-check-label";
    private static final String CLASS_FORM_SELECT       = "form-select";
    private static final String CLASS_ERROR_HIGHLIGHT   = "error-highlight";
    private static final String CLASS_TEXT_COLOR_RED    = "text-color-red";

    private static final String DATA_ATT_SUBMIT_ID      = "submit-id";
    private static final String DATA_ATT_SUBMIT_AS      = "submit-as";
    private static final String DATA_ATT_VALUE_TYPE     = "value-type";
    private static final String DATA_ATT_MAX_CHARACTERS = "max-characters";

    /**
     * Internal constructor
     */
    private ContainerHtmlMapper()
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
        ContainerHtmlMapper mapper = new ContainerHtmlMapper();

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
        ContainerHtmlMapper mapper = new ContainerHtmlMapper();
        StringConcat        sc     = new StringConcat();

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
            case BUTTON -> render((Button) container);
            case BUTTON_BAR -> render((ButtonBarContainer) container);
            case BUTTON_ICON -> render((ButtonIcon) container);
            case CONTENT -> render((ContentContainer) container);
            case HIDDEN -> render((HiddenContainer) container);
            case IMAGE -> render((ImageContainer) container);
            case FORM -> render((FormContainer) container);
            case LINK -> render((LinkContainer) container);
            case MODAL -> render((ModalContainer) container);
            case SPLITTED -> render((SplittedContainer) container);
            case TAB -> render((TabContainer) container);
            case TEXT -> render((TextContainer) container);
        };
    }

    private String render(AudioContainer audioContainer)
    {
        // TODO: implement
        return elementNotYetImplemented("audio");
    }

    private String render(Button button)
    {
        return renderInternal(button);
    }

    private String render(ButtonBarContainer buttonBarContainer)
    {
        return HtmlElement
            .builder()
            .tag(TAG_DIV)
            .attribute(new Attribute(ATT_ID, buttonBarContainer.getUid()))
            .classes(buttonBarContainer.getClasses())
            .dataAttributes(buttonBarContainer.getDataAttributes())
            .contents(
                buttonBarContainer
                    .getButtons()
                    .stream()
                    .map(b -> renderInternal(b))
                    .toList())
            .build()
            .html();
    }

    private String render(ButtonIcon buttonIcon)
    {
        return elementNotYetImplemented("btn icon");
    }

    private String renderInternal(Button button)
    {
        HtmlElement elementMapper =
            HtmlElement
                .builder()
                .tag("button")
                .attribute(new Attribute(ATT_ID, button.getUid()))
                .attribute(new Attribute(ATT_ON_CLICK, button.getOnClick()))
                .clazz("btn")
                .clazz(button.getBtnClass().getClassName())
                .classes(button.getClasses())
                .dataAttributes(button.getDataAttributes())
                .content(button.getText())
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
                .attribute(new Attribute(ATT_ID, contentContainer.getUid()))
                .attribute(new Attribute(ATT_ON_CLICK, contentContainer.getOnClick()))
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
                .attribute(new Attribute(ATT_ID, formContainer.getUid()))
                .attribute(new Attribute(ATT_ON_CLICK, formContainer.getOnClick()))
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
                .attribute(new Attribute(ATT_ID, hiddenContainer.getUid()))
                .attribute(new Attribute(ATT_ON_CLICK, hiddenContainer.getOnClick()))
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
                .attribute(new Attribute(ATT_ID, imageContainer.getUid()))
                .attribute(new Attribute(ATT_ON_CLICK, imageContainer.getOnClick()))
                .attribute(new Attribute(ATT_SRC, imageContainer.getSrc()))
                .classes(imageContainer.getClasses())
                .dataAttributes(imageContainer.getDataAttributes())
                .build();

        return elementMapper.html();
    }

    private String render(LinkContainer linkContainer)
    {
        HtmlElement elementMapper =
            HtmlElement
                .builder()
                .tag("a")
                .attribute(new Attribute(ATT_ID, linkContainer.getUid()))
                .attribute(new Attribute(ATT_ON_CLICK, linkContainer.getOnClick()))
                .attribute(new Attribute(ATT_HREF, linkContainer.getHref()))
                .attribute(new Attribute(ATT_TARGET, linkContainer.getTarget()))
                .classes(linkContainer.getClasses())
                .dataAttributes(linkContainer.getDataAttributes())
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
                .attribute(new Attribute(ATT_ID, modalContainer.getUid()))
                .classes(modalContainer.getClasses())
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
                .attribute(new Attribute(ATT_ID, splittedContainer.getUid()))
                .attribute(new Attribute(ATT_ON_CLICK, splittedContainer.getOnClick()))
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

    private String render(TextContainer textContainer)
    {
        HtmlElement elementMapper =
            HtmlElement
                .builder()
                .tag("p")
                .attribute(new Attribute(ATT_ID, textContainer.getUid()))
                .attribute(new Attribute(ATT_ON_CLICK, textContainer.getOnClick()))
                .classes(textContainer.getClasses())
                .dataAttributes(textContainer.getDataAttributes())
                .content(textContainer.getText())
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
            case LIST -> render((ListSelection) input, formId);
            case NUMBER -> render((Number) input, formId);
            case PASSWORD -> render((Password) input, formId);
            case RADIO -> render((Radio) input, formId);
            case SELECT -> render((Select) input, formId);
            case SLIDER -> render((Slider) input, formId);
            case SWITCH -> render((Switch) input, formId);
            case TABLE -> render((Table) input, formId);
            case TEXTAREA -> render((Textarea) input, formId);
            case TEXTBOX -> render((Textbox) input);
            case TEXTFIELD -> render((Textfield) input, formId);
        };
    }

    private String render(Checkbox checkbox, String formId)
    {
        String      input         =
            HtmlElement
                .builder()
                .isSingleTag(true)
                .tag(TAG_INPUT)
                .attribute(new Attribute(ATT_ID, checkbox.getUid()))
                .attribute(new Attribute(ATT_TYPE, checkbox.getType().name().toLowerCase()))
                .attribute(new Attribute(ATT_ON_INPUT, checkbox.getOnInput()))
                .attribute(new Attribute(ATT_CHECKED, checkbox.isChecked()))
                .clazz(CLASS_FORM_CHECK_INPUT)
                .dataAttribute(DATA_ATT_SUBMIT_ID, formId)
                .dataAttribute(DATA_ATT_SUBMIT_AS, checkbox.getSubmitAs())
                .dataAttribute(DATA_ATT_VALUE_TYPE, checkbox.getType().name())
                .dataAttributes(checkbox.getDataAttributes())
                .build()
                .html();

        String      label         =
            HtmlElement
                .builder()
                .tag(TAG_LABEL)
                .attribute(new Attribute(ATT_FOR, checkbox.getUid()))
                .clazz(CLASS_FORM_CHECK_LABEL)
                .clazz(CLASS_USER_SELECT_NONE)
                .content(checkbox.getName())
                .build()
                .html();

        HtmlElement elementMapper =
            HtmlElement
                .builder()
                .tag(TAG_DIV)
                .clazz(CLASS_FORM_CHECK)
                .content(StringAdapter.from(input, label))
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
        String      label         =
            HtmlElement
                .builder()
                .tag(TAG_LABEL)
                .attribute(new Attribute(ATT_FOR, date.getUid()))
                .clazz(CLASS_FORM_LABEL)
                .content(date.getName())
                .build()
                .html();

        String      input         =
            HtmlElement
                .builder()
                .isSingleTag(true)
                .tag(TAG_INPUT)
                .attribute(new Attribute(ATT_ID, date.getUid()))
                .attribute(new Attribute(ATT_TYPE, date.getType().name().toLowerCase()))
                .attribute(new Attribute(ATT_VALUE, date.getValue()))
                .clazz(CLASS_FORM_CONTROL)
                .dataAttribute(DATA_ATT_SUBMIT_ID, formId)
                .dataAttribute(DATA_ATT_SUBMIT_AS, date.getSubmitAs())
                .dataAttribute(DATA_ATT_VALUE_TYPE, date.getType().name())
                .dataAttributes(date.getDataAttributes())
                .build()
                .html();

        HtmlElement elementMapper =
            HtmlElement
                .builder()
                .tag(TAG_DIV)
                .content(StringAdapter.from(label, input))
                .build();

        return elementMapper.html();
    }

    private String render(File file, String formId)
    {
        String      label         =
            HtmlElement
                .builder()
                .tag(TAG_LABEL)
                .attribute(new Attribute(ATT_FOR, file.getUid()))
                .clazz(CLASS_FORM_LABEL)
                .content(file.getName())
                .build()
                .html();

        String      input         =
            HtmlElement
                .builder()
                .isSingleTag(true)
                .tag(TAG_INPUT)
                .attribute(new Attribute(ATT_ID, file.getUid()))
                .attribute(new Attribute(ATT_TYPE, file.getType().name().toLowerCase()))
                .attribute(new Attribute("multiple", file.isMultiple()))
                .attribute(new Attribute("accept", StringAdapter.separate(file.getAccepts(), ",")))
                .clazz(CLASS_FORM_CONTROL)
                .dataAttribute(DATA_ATT_SUBMIT_ID, formId)
                .dataAttribute(DATA_ATT_SUBMIT_AS, file.getSubmitAs())
                .dataAttribute(DATA_ATT_VALUE_TYPE, file.getType().name())
                .dataAttributes(file.getDataAttributes())
                .build()
                .html();

        HtmlElement elementMapper =
            HtmlElement
                .builder()
                .tag(TAG_DIV)
                .content(StringAdapter.from(label, resolveMarker(formId, file.getSubmitAs(), file.getMarker()), input))
                .build();

        return elementMapper.html();
    }

    private String render(Hidden hidden, String formId)
    {
        String      input         =
            HtmlElement
                .builder()
                .isSingleTag(true)
                .tag(TAG_INPUT)
                .attribute(new Attribute(ATT_ID, hidden.getUid()))
                .attribute(new Attribute(ATT_TYPE, "text"))
                .attribute(new Attribute(ATT_VALUE, hidden.getValue()))
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

    private String render(ListSelection list, String formId)
    {
        ArrayList<String> options = new ArrayList<>();

        for (InputValue selection : list.getValues())
        {
            String option =
                HtmlElement
                    .builder()
                    .tag(TAG_OPTION)
                    .attribute(new Attribute(ATT_VALUE, selection.getValue()))
                    .attribute(new Attribute(ATT_SELECTED, selection.getChecked()))
                    .content(selection.getText())
                    .build()
                    .html();

            options.add(option);
        }

        ArrayList<String> selections = new ArrayList<>();

        for (InputValue selection : list.getSelected())
        {
            UUID   uuid   = UUID.randomUUID();

            String option =
                HtmlElement
                    .builder()
                    .tag(TAG_DIV)
                    .attribute(new Attribute(ATT_ID, uuid.toString()))
                    .content(
                        render(
                            ContentContainer
                                .builder()
                                .clazz("form-list-selection-item")
                                .dataAttribute(ATT_VALUE, selection.getValue())
                                .content(
                                    TextContainer
                                        .builder()
                                        .text(selection.getText())
                                        .build())
                                .content(
                                    Button
                                        .builder()
                                        .text("X")
                                        .onClick(String.format("removeListSelectionItem('%s')", uuid))
                                        .build())
                                .build()))
                    .build()
                    .html();

            selections.add(option);
        }

        String      label           =
            HtmlElement
                .builder()
                .tag(TAG_LABEL)
                .attribute(new Attribute(ATT_FOR, list.getUid()))
                .clazz(CLASS_FORM_LABEL)
                .content(list.getName())
                .build()
                .html();

        UUID        selectId        = UUID.randomUUID();

        String      input           =
            HtmlElement
                .builder()
                .tag(TAG_SELECT)
                .attribute(new Attribute(ATT_ID, selectId.toString()))
                .clazz("form-list-selection")
                .dataAttribute("multiple", String.valueOf(list.isMultiple()))
                .content(StringAdapter.from(options))
                .build()
                .html();

        String      selectHolder    =
            HtmlElement
                .builder()
                .tag(TAG_DIV)
                .clazz("form-list-selection-select-holder")
                .content(input)
                .content(
                    render(
                        Button
                            .builder()
                            .text("+")
                            .onClick(String.format("addListSelectionItem('%s', '%s')", selectId, list.getUid()))
                            .build()))
                .build()
                .html();

        String      selectionHolder =
            HtmlElement
                .builder()
                .tag(TAG_DIV)
                .attribute(new Attribute(ATT_ID, list.getUid()))
                .attribute(new Attribute(ATT_ON_INPUT, list.getOnInput()))
                .dataAttributes(list.getDataAttributes())
                .dataAttribute(DATA_ATT_SUBMIT_ID, formId)
                .dataAttribute(DATA_ATT_SUBMIT_AS, list.getSubmitAs())
                .dataAttribute(DATA_ATT_VALUE_TYPE, list.getType().name())
                .dataAttributes(list.getDataAttributes())
                .content(StringAdapter.from(selections))
                .build()
                .html();

        String      outerBody       =
            HtmlElement
                .builder()
                .tag(TAG_DIV)
                .content(selectHolder)
                .content(selectionHolder)
                .build()
                .html();

        HtmlElement elementMapper   =
            HtmlElement
                .builder()
                .tag(TAG_DIV)
                .content(label)
                .content(outerBody)
                .build();

        return elementMapper.html();
    }

    private String render(Number number, String formId)
    {
        String      label         =
            HtmlElement
                .builder()
                .tag(TAG_LABEL)
                .attribute(new Attribute(ATT_FOR, number.getUid()))
                .clazz(CLASS_FORM_LABEL)
                .content(number.getName())
                .build()
                .html();

        String      input         =
            HtmlElement
                .builder()
                .isSingleTag(true)
                .tag(TAG_INPUT)
                .attribute(new Attribute(ATT_ID, number.getUid()))
                .attribute(new Attribute(ATT_TYPE, number.getType().name().toLowerCase()))
                .attribute(new Attribute(ATT_VALUE, String.valueOf(number.getValue())))
                .attribute(new Attribute(ATT_PLACEHOLDER, number.getPlaceholder()))
                .clazz(CLASS_FORM_CONTROL)
                .dataAttribute(DATA_ATT_SUBMIT_ID, formId)
                .dataAttribute(DATA_ATT_SUBMIT_AS, number.getSubmitAs())
                .dataAttribute(DATA_ATT_VALUE_TYPE, number.getType().name())
                .dataAttributes(number.getDataAttributes())
                .build()
                .html();

        HtmlElement elementMapper =
            HtmlElement
                .builder()
                .tag(TAG_DIV)
                .content(StringAdapter.from(label, resolveMarker(formId, number.getSubmitAs(), number.getMarker()), input))
                .build();

        return elementMapper.html();
    }

    private String render(Password password, String formId)
    {
        String      label         =
            HtmlElement
                .builder()
                .tag(TAG_LABEL)
                .attribute(new Attribute(ATT_FOR, password.getUid()))
                .clazz(CLASS_FORM_LABEL)
                .content(password.getName())
                .build()
                .html();

        String      input         =
            HtmlElement
                .builder()
                .isSingleTag(true)
                .tag(TAG_INPUT)
                .attribute(new Attribute(ATT_ID, password.getUid()))
                .attribute(new Attribute(ATT_TYPE, password.getType().name().toLowerCase()))
                .attribute(new Attribute(ATT_VALUE, ""))
                .attribute(new Attribute(ATT_PLACEHOLDER, password.getPlaceholder()))
                .clazz(CLASS_FORM_CONTROL)
                .dataAttribute(DATA_ATT_SUBMIT_ID, formId)
                .dataAttribute(DATA_ATT_SUBMIT_AS, password.getSubmitAs())
                .dataAttribute(DATA_ATT_VALUE_TYPE, password.getType().name())
                .dataAttributes(password.getDataAttributes())
                .build()
                .html();

        HtmlElement elementMapper =
            HtmlElement
                .builder()
                .tag(TAG_DIV)
                .content(StringAdapter.from(label, resolveMarker(formId, password.getSubmitAs(), password.getMarker()), input))
                .build();

        return elementMapper.html();
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
                    .attribute(new Attribute(ATT_ID, radioID))
                    .attribute(new Attribute(ATT_TYPE, radio.getType().name().toLowerCase()))
                    .attribute(new Attribute(ATT_VALUE, btn.getValue()))
                    .attribute(new Attribute(ATT_NAME, radio.getUid()))
                    .attribute(new Attribute(ATT_ON_INPUT, radio.getOnInput()))
                    .attribute(new Attribute(ATT_CHECKED, btn.getChecked()))
                    .clazz(CLASS_FORM_CHECK_INPUT)
                    .build()
                    .html();

            String      btnLabel =
                HtmlElement
                    .builder()
                    .tag(TAG_LABEL)
                    .attribute(new Attribute(ATT_FOR, radioID))
                    .clazz(CLASS_FORM_CHECK_LABEL)
                    .clazz(CLASS_USER_SELECT_NONE)
                    .content(btn.getText())
                    .build()
                    .html();

            HtmlElement innerDiv =
                HtmlElement
                    .builder()
                    .tag(TAG_DIV)
                    .attribute(new Attribute(ATT_ID, radio.getUid()))
                    .clazz(CLASS_FORM_CHECK)
                    .content(StringAdapter.from(input, btnLabel))
                    .build();

            i++;
            checked = checked || btn.getChecked();

            buttons.add(innerDiv.html());
        }

        String      outerLabel =
            HtmlElement
                .builder()
                .tag(TAG_LABEL)
                .clazz(CLASS_USER_SELECT_NONE)
                .content(radio.getName())
                .build()
                .html();

        HtmlElement outerDiv   =
            HtmlElement
                .builder()
                .tag(TAG_DIV)
                .clazz(CLASS_FORM_CHECK)
                .dataAttribute(DATA_ATT_SUBMIT_ID, formId)
                .dataAttribute(DATA_ATT_SUBMIT_AS, radio.getSubmitAs())
                .dataAttribute(DATA_ATT_VALUE_TYPE, radio.getType().name())
                .content(StringAdapter.from(outerLabel, StringAdapter.from(buttons)))
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
                    .attribute(new Attribute(ATT_VALUE, selection.getValue()))
                    .attribute(new Attribute(ATT_SELECTED, selection.getChecked()))
                    .content(selection.getText())
                    .build()
                    .html();

            selections.add(option);
        }

        String      label         =
            HtmlElement
                .builder()
                .tag(TAG_LABEL)
                .attribute(new Attribute(ATT_FOR, select.getUid()))
                .clazz(CLASS_FORM_LABEL)
                .content(select.getName())
                .build()
                .html();

        String      input         =
            HtmlElement
                .builder()
                .tag(TAG_SELECT)
                .attribute(new Attribute(ATT_ID, select.getUid()))
                .attribute(new Attribute(ATT_ON_INPUT, select.getOnInput()))
                .clazz(CLASS_FORM_SELECT)
                .dataAttribute(DATA_ATT_SUBMIT_ID, formId)
                .dataAttribute(DATA_ATT_SUBMIT_AS, select.getSubmitAs())
                .dataAttribute(DATA_ATT_VALUE_TYPE, select.getType().name())
                .dataAttributes(select.getDataAttributes())
                .content(StringAdapter.from(selections))
                .build()
                .html();

        HtmlElement elementMapper =
            HtmlElement
                .builder()
                .tag(TAG_DIV)
                .content(StringAdapter.from(label, input))
                .build();

        return elementMapper.html();
    }

    private String render(Slider slider, String formId)
    {
        String      label         =
            HtmlElement
                .builder()
                .tag(TAG_LABEL)
                .attribute(new Attribute(ATT_FOR, slider.getUid()))
                .clazz(CLASS_FORM_LABEL)
                .content(slider.getName())
                .build()
                .html();

        String      input         =
            HtmlElement
                .builder()
                .isSingleTag(true)
                .tag(TAG_INPUT)
                .attribute(new Attribute(ATT_ID, slider.getUid()))
                .attribute(new Attribute(ATT_TYPE, "range"))
                .attribute(new Attribute(ATT_VALUE, String.valueOf(slider.getValue())))
                .attribute(new Attribute(ATT_ON_INPUT, slider.getOnInput()))
                .attribute(new Attribute(ATT_MIN, String.valueOf(slider.getMin())))
                .attribute(new Attribute(ATT_MAX, String.valueOf(slider.getMax())))
                .clazz("form-range")
                .dataAttribute(DATA_ATT_SUBMIT_ID, formId)
                .dataAttribute(DATA_ATT_SUBMIT_AS, slider.getSubmitAs())
                .dataAttribute(DATA_ATT_VALUE_TYPE, slider.getType().name())
                .dataAttributes(slider.getDataAttributes())
                .build()
                .html();

        HtmlElement elementMapper =
            HtmlElement
                .builder()
                .tag(TAG_DIV)
                .content(StringAdapter.from(label, input))
                .build();

        return elementMapper.html();
    }

    private String render(Switch switch1, String formId)
    {
        String      input         =
            HtmlElement
                .builder()
                .isSingleTag(true)
                .tag(TAG_INPUT)
                .attribute(new Attribute(ATT_ID, switch1.getUid()))
                .attribute(new Attribute(ATT_TYPE, "checkbox"))
                // .attribute(ATT_ROLE, switch1.getType().name().toLowerCase())
                .attribute(new Attribute(ATT_ON_INPUT, switch1.getOnInput()))
                .attribute(new Attribute(ATT_CHECKED, switch1.isChecked()))
                .clazz(CLASS_FORM_CHECK_INPUT)
                .dataAttribute(DATA_ATT_SUBMIT_ID, formId)
                .dataAttribute(DATA_ATT_SUBMIT_AS, switch1.getSubmitAs())
                .dataAttribute(DATA_ATT_VALUE_TYPE, switch1.getType().name())
                .dataAttributes(switch1.getDataAttributes())
                .build()
                .html();

        String      label         =
            HtmlElement
                .builder()
                .tag(TAG_LABEL)
                .attribute(new Attribute(ATT_FOR, switch1.getUid()))
                .clazz(CLASS_FORM_CHECK_LABEL)
                .clazz(CLASS_USER_SELECT_NONE)
                .content(switch1.getName())
                .build()
                .html();

        HtmlElement elementMapper =
            HtmlElement
                .builder()
                .tag(TAG_DIV)
                .clazz(CLASS_FORM_CHECK)
                .clazz("form-switch")
                .content(StringAdapter.from(input, label))
                .build();

        return elementMapper.html();
    }

    private String render(Table table, String formId)
    {
        // TODO: implement
        return elementNotYetImplemented("table");
    }

    private String render(Textarea textarea, String formId)
    {
        // TODO: implement
        return elementNotYetImplemented("textarea");
    }

    private String render(Textbox textbox)
    {
        HtmlElement elementMapper =
            HtmlElement
                .builder()
                .tag(TAG_DIV)
                .attribute(new Attribute(ATT_ID, textbox.getUid()))
                .clazz("text-preserve-newline")
                .classes(textbox.getClasses())
                .dataAttributes(textbox.getDataAttributes())
                .content(textbox.getValue())
                .build();

        return elementMapper.html();
    }

    private String render(Textfield textfield, String formId)
    {
        String      label         =
            HtmlElement
                .builder()
                .tag(TAG_LABEL)
                .attribute(new Attribute(ATT_FOR, textfield.getUid()))
                .clazz(CLASS_FORM_LABEL)
                .content(textfield.getName())
                .build()
                .html();

        String      input         =
            HtmlElement
                .builder()
                .isSingleTag(true)
                .tag(TAG_INPUT)
                .attribute(new Attribute(ATT_ID, textfield.getUid()))
                .attribute(new Attribute(ATT_TYPE, textfield.getType().name().toLowerCase()))
                .attribute(new Attribute(ATT_VALUE, textfield.getValue()))
                .attribute(new Attribute(ATT_ON_INPUT, textfield.getOnInput()))
                .attribute(new Attribute(ATT_PLACEHOLDER, textfield.getPlaceholder()))
                .attribute(new Attribute(ATT_ON_KEY_DOWN, textfield.getOnKeydown()))
                .clazz(CLASS_FORM_CONTROL)
                .dataAttribute(DATA_ATT_SUBMIT_ID, formId)
                .dataAttribute(DATA_ATT_SUBMIT_AS, textfield.getSubmitAs())
                .dataAttribute(DATA_ATT_VALUE_TYPE, textfield.getType().name())
                .dataAttribute(DATA_ATT_MAX_CHARACTERS, String.valueOf(textfield.getMaxCharacters()))
                .dataAttributes(textfield.getDataAttributes())
                .build()
                .html();

        String      content       = StringAdapter.from(label, resolveMarker(formId, textfield.getSubmitAs(), textfield.getMarker()), input);
        HtmlElement elementMapper =
            HtmlElement
                .builder()
                .tag(TAG_DIV)
                .content(content)
                .build();

        return elementMapper.html();
    }

    private String resolveMarker(String formId, String submitAsId, List<Marker> marker)
    {
        List<String> results = new ArrayList<>();

        for (Marker m : marker)
        {
            results
                .add(
                    HtmlElement
                        .builder()
                        .tag(TAG_DIV)
                        .attribute(
                            new Attribute(ATT_ID,
                                String
                                    .format("error-marker-%s-%s-%s-%s", formId, submitAsId, m.getCategory(), m.getType())))
                        .clazz(CLASS_HIDDEN)
                        .clazz(CLASS_ERROR_HIGHLIGHT)
                        .clazz("error-marker")
                        .clazz(CLASS_TEXT_COLOR_RED)
                        .content(m.getText())
                        .build()
                        .html());
        }

        return StringAdapter.from(results);
    }
}
