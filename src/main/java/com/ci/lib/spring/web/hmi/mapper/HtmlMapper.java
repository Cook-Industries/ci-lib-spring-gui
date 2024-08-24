package com.ci.lib.spring.web.hmi.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.ci.lib.spring.web.hmi.container.*;
import com.ci.lib.spring.web.hmi.input.*;
import com.ci.lib.spring.web.hmi.input.Number;
import com.ci.lib.spring.web.util.StringAdapter;
import com.ci.lib.spring.web.util.StringConcat;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class HtmlMapper
{

    private static final String TAG_DIV                 = "div";
    private static final String TAG_LABEL               = "label";
    private static final String TAG_INPUT               = "input";
    private static final String TAG_SELECT              = "select";
    private static final String TAG_OPTION              = "option";
    private static final String TAG_FORM                = "form";

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
    private static final String CLASS_HIDDEN            = "d-none";
    private static final String CLASS_USER_SELECT_NONE  = "user-select-none";
    private static final String CLASS_FORM_CHECK        = "form-check";
    private static final String CLASS_FORM_CHECK_INPUT  = "form-check-input";
    private static final String CLASS_FORM_CHECK_LABEL  = "form-check-label";

    private static final String DATA_ATT_SUBMIT_ID      = "submit-id";
    private static final String DATA_ATT_SUBMIT_AS      = "submit-as";
    private static final String DATA_ATT_VALUE_TYPE     = "value-type";
    private static final String DATA_ATT_MAX_CHARACTERS = "max-characters";

    public static String map(Container container)
    {
        HtmlMapper mapper = new HtmlMapper();
        return mapper.render(container);
    }

    public static String map(List<Container> containers)
    {
        HtmlMapper   mapper = new HtmlMapper();
        StringConcat sc     = new StringConcat();

        containers.stream().forEach(c -> sc.append(mapper.render(c)));

        return sc.getString();
    }

    private String elementNotYetImplemented(String name)
    {
        String s = name + " element not supported yet.";
        return StringAdapter.withPrefixAndSuffix("<div>", s, "</div>");
    }

    private String render(Container container)
    {
        return switch (container.getType())
        {
            case AUDIO -> render((AudioContainer) container);
            case BUTTON -> render((ButtonContainer) container);
            case BUTTON_BAR -> render((ButtonBarContainer) container);
            case BUTTON_ICON -> render((ButtonIconContainer) container);
            case COLUMN -> render((ColumnContainer) container);
            case CONTENT -> render((ContentContainer) container);
            case HIDDEN -> render((HiddenContainer) container);
            case IMAGE -> render((ImageContainer) container);
            case FORM -> render((FormContainer) container);
            case LINK -> render((LinkContainer) container);
            case ROW -> render((RowContainer) container);
            case ROWED_CONTENT -> render((RowedContentContainer) container);
            case SPAN -> render((SpanContainer) container);
            case SPLITTED -> render((SplittedContainer) container);
            case TAB -> render((TabbedContainer) container);
            case TEXT -> render((TextContainer) container);
        };
    }

    private String render(AudioContainer audioContainer)
    {
        // TODO: implement
        return elementNotYetImplemented("audio");
    }

    private String render(ButtonContainer buttonContainer)
    {
        return render(buttonContainer.getButton());
    }

    private String render(ButtonBarContainer buttonBarContainer)
    {
        return buttonBarContainer.getButtons().stream().map(b -> render(b)).collect(Collectors.joining(""));
    }

    private String render(ButtonIconContainer buttonIconContainer)
    {
        return render(buttonIconContainer.getButton());
    }

    private String render(ContentContainer contentContainer)
    {
        String            content       = contentContainer.getContents().stream().map(this::render).collect(Collectors.joining());

        HtmlElementMapper elementMapper = HtmlElementMapper
                .builder()
                .tag(TAG_DIV)
                .attribute(new Attribute(ATT_ID, contentContainer.getUid()))
                .attribute(new Attribute(ATT_ON_CLICK, contentContainer.getOnClick()))
                .classes(contentContainer.getClasses())
                .dataAttribues(contentContainer.getDataAttributes())
                .content(content)
                .build();

        return elementMapper.html();
    }

    private String render(RowedContentContainer rowedContentContainer)
    {
        String            content       = rowedContentContainer.getRows().stream().map(this::render).collect(Collectors.joining());

        HtmlElementMapper elementMapper = HtmlElementMapper
                .builder()
                .tag(TAG_DIV)
                .attribute(new Attribute(ATT_ID, rowedContentContainer.getUid()))
                .attribute(new Attribute(ATT_ON_CLICK, rowedContentContainer.getOnClick()))
                .clazz("container")
                .classes(rowedContentContainer.getClasses())
                .dataAttribues(rowedContentContainer.getDataAttributes())
                .content(content)
                .build();

        return elementMapper.html();
    }

    private String render(RowContainer rowContainer)
    {
        String            content       = rowContainer.getColumns().stream().map(this::render).collect(Collectors.joining());

        HtmlElementMapper elementMapper = HtmlElementMapper
                .builder()
                .tag(TAG_DIV)
                .attribute(new Attribute(ATT_ID, rowContainer.getUid()))
                .attribute(new Attribute(ATT_ON_CLICK, rowContainer.getOnClick()))
                .clazz("row")
                .classes(rowContainer.getClasses())
                .dataAttribues(rowContainer.getDataAttributes())
                .content(content)
                .build();

        return elementMapper.html();
    }

    private String render(ColumnContainer columnContainer)
    {
        HtmlElementMapper elementMapper = HtmlElementMapper
                .builder()
                .tag(TAG_DIV)
                .attribute(new Attribute(ATT_ID, columnContainer.getUid()))
                .attribute(new Attribute(ATT_ON_CLICK, columnContainer.getOnClick()))
                .clazz("col")
                .classes(columnContainer.getClasses())
                .dataAttribues(columnContainer.getDataAttributes())
                .content(render(columnContainer.getContent()))
                .build();

        return elementMapper.html();
    }

    private String render(FormContainer formContainer)
    {
        String            content       =
                formContainer.getInputs().stream().map(i -> render(i, formContainer.getUid())).collect(Collectors.joining());

        HtmlElementMapper elementMapper = HtmlElementMapper
                .builder()
                .tag(TAG_DIV)
                .attribute(new Attribute(ATT_ID, formContainer.getUid()))
                .attribute(new Attribute(ATT_ON_CLICK, formContainer.getOnClick()))
                .classes(formContainer.getClasses())
                .dataAttribues(formContainer.getDataAttributes())
                .content(content)
                .build();

        return elementMapper.html();
    }

    private String render(HiddenContainer hiddenContainer)
    {
        HtmlElementMapper elementMapper = HtmlElementMapper
                .builder()
                .tag(TAG_DIV)
                .attribute(new Attribute(ATT_ID, hiddenContainer.getUid()))
                .attribute(new Attribute(ATT_ON_CLICK, hiddenContainer.getOnClick()))
                .clazz(CLASS_HIDDEN)
                .classes(hiddenContainer.getClasses())
                .dataAttribues(hiddenContainer.getDataAttributes())
                .content(render(hiddenContainer.getChild()))
                .build();

        return elementMapper.html();
    }

    private String render(ImageContainer imageContainer)
    {
        HtmlElementMapper elementMapper = HtmlElementMapper
                .builder()
                .tag("img")
                .attribute(new Attribute(ATT_ID, imageContainer.getUid()))
                .attribute(new Attribute(ATT_ON_CLICK, imageContainer.getOnClick()))
                .attribute(new Attribute(ATT_SRC, imageContainer.getSrc()))
                .classes(imageContainer.getClasses())
                .dataAttribues(imageContainer.getDataAttributes())
                .build();

        return elementMapper.html();
    }

    private String render(LinkContainer linkContainer)
    {
        HtmlElementMapper elementMapper = HtmlElementMapper
                .builder()
                .tag("a")
                .attribute(new Attribute(ATT_ID, linkContainer.getUid()))
                .attribute(new Attribute(ATT_ON_CLICK, linkContainer.getOnClick()))
                .attribute(new Attribute(ATT_HREF, linkContainer.getHref()))
                .attribute(new Attribute(ATT_TARGET, linkContainer.getTarget()))
                .classes(linkContainer.getClasses())
                .dataAttribues(linkContainer.getDataAttributes())
                .content(render(linkContainer.getContent()))
                .build();

        return elementMapper.html();
    }

    private String render(SpanContainer spanContainer)
    {
        HtmlElementMapper elementMapper = HtmlElementMapper
                .builder()
                .tag("span")
                .attribute(new Attribute(ATT_ID, spanContainer.getUid()))
                .attribute(new Attribute(ATT_ON_CLICK, spanContainer.getOnClick()))
                .classes(spanContainer.getClasses())
                .dataAttribues(spanContainer.getDataAttributes())
                .content(spanContainer.getText())
                .build();

        return elementMapper.html();
    }

    private String render(SplittedContainer splittedContainer)
    {
        String            head          = render(splittedContainer.getHead());
        String            tail          = render(splittedContainer.getTail());

        HtmlElementMapper elementMapper = HtmlElementMapper
                .builder()
                .tag(TAG_DIV)
                .attribute(new Attribute(ATT_ID, splittedContainer.getUid()))
                .attribute(new Attribute(ATT_ON_CLICK, splittedContainer.getOnClick()))
                .clazz("d-flex")
                .clazz("justify-content-between")
                .classes(splittedContainer.getClasses())
                .dataAttribues(splittedContainer.getDataAttributes())
                .content(StringAdapter.from(head, tail))
                .build();

        return elementMapper.html();
    }

    private String render(TabbedContainer tabbedContainer)
    {
        // TODO: implement
        return elementNotYetImplemented("tabbed container");
    }

    private String render(TextContainer textContainer)
    {
        HtmlElementMapper elementMapper = HtmlElementMapper
                .builder()
                .tag("p")
                .attribute(new Attribute(ATT_ID, textContainer.getUid()))
                .attribute(new Attribute(ATT_ON_CLICK, textContainer.getOnClick()))
                .classes(textContainer.getClasses())
                .dataAttribues(textContainer.getDataAttributes())
                .content(textContainer.getText())
                .build();

        return elementMapper.html();
    }

    private String render(Input input, String formId)
    {
        return switch (input.getType())
        {
            case BUTTON -> render((Button) input);
            case BUTTON_ICON -> render((ButtonIcon) input);
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
            case TABLE -> render((Table) input, formId);
            case TEXTAREA -> render((Textarea) input, formId);
            case TEXTBOX -> render((Textbox) input);
            case TEXTFIELD -> render((Textfield) input, formId);
        };
    }

    private String render(Button button)
    {
        HtmlElementMapper elementMapper = HtmlElementMapper
                .builder()
                .tag("button")
                .attribute(new Attribute(ATT_ID, button.getUid()))
                .attribute(new Attribute(ATT_ON_CLICK, button.getOnClick()))
                .clazz("btn")
                .clazz(button.getBtnClass().getClassName())
                .classes(button.getClasses())
                .dataAttribues(button.getDataAttributes())
                .content(button.getText())
                .build();

        return elementMapper.html();
    }

    private String render(ButtonIcon buttonIcon)
    {
        // TODO: implement
        return elementNotYetImplemented("btn icon");
    }

    private String render(Checkbox checkbox, String formId)
    {
        String            input         = HtmlElementMapper
                .builder()
                .isSingleTag(true)
                .tag(TAG_INPUT)
                .attribute(new Attribute(ATT_ID, checkbox.getUid()))
                .attribute(new Attribute(ATT_TYPE, checkbox.getType().name().toLowerCase()))
                // .attribute(ATT_ROLE, switch1.getType().name().toLowerCase())
                .attribute(new Attribute(ATT_ON_INPUT, checkbox.getOnInput()))
                .attribute(new Attribute(ATT_CHECKED, checkbox.getChecked()))
                .clazz(CLASS_FORM_CHECK_INPUT)
                .dataAttribue(DATA_ATT_SUBMIT_ID, formId)
                .dataAttribue(DATA_ATT_SUBMIT_AS, checkbox.getSubmitAs())
                .dataAttribue(DATA_ATT_VALUE_TYPE, checkbox.getType().name())
                .dataAttribues(checkbox.getDataAttributes())
                .build()
                .html();

        String            label         = HtmlElementMapper
                .builder()
                .tag(TAG_LABEL)
                .attribute(new Attribute(ATT_FOR, checkbox.getUid()))
                .clazz(CLASS_FORM_CHECK_LABEL)
                .clazz(CLASS_USER_SELECT_NONE)
                .content(checkbox.getName())
                .build()
                .html();

        HtmlElementMapper elementMapper =
                HtmlElementMapper.builder().tag(TAG_DIV).clazz(CLASS_FORM_CHECK).content(StringAdapter.from(input, label)).build();

        return elementMapper.html();
    }

    private String render(Currency currency, String formId)
    {
        // TODO: implement
        return elementNotYetImplemented("currency");
    }

    private String render(Date date, String formId)
    {
        String            label         = HtmlElementMapper
                .builder()
                .tag(TAG_LABEL)
                .attribute(new Attribute(ATT_FOR, date.getUid()))
                .clazz(CLASS_FORM_LABEL)
                .content(date.getName())
                .build()
                .html();

        String            input         = HtmlElementMapper
                .builder()
                .isSingleTag(true)
                .tag(TAG_INPUT)
                .attribute(new Attribute(ATT_ID, date.getUid()))
                .attribute(new Attribute(ATT_TYPE, date.getType().name().toLowerCase()))
                .attribute(new Attribute(ATT_VALUE, date.getValue()))
                .clazz(CLASS_FORM_CONTROL)
                .dataAttribue(DATA_ATT_SUBMIT_ID, formId)
                .dataAttribue(DATA_ATT_SUBMIT_AS, date.getSubmitAs())
                .dataAttribue(DATA_ATT_VALUE_TYPE, date.getType().name())
                .dataAttribues(date.getDataAttributes())
                .build()
                .html();

        HtmlElementMapper elementMapper = HtmlElementMapper.builder().tag(TAG_DIV).content(StringAdapter.from(label, input)).build();

        return elementMapper.html();
    }

    private String render(File file, String formId)
    {
        // TODO: implement
        return elementNotYetImplemented("file");
    }

    private String render(Hidden hidden, String formId)
    {
        String            input         = HtmlElementMapper
                .builder()
                .isSingleTag(true)
                .tag(TAG_INPUT)
                .attribute(new Attribute(ATT_ID, hidden.getUid()))
                .attribute(new Attribute(ATT_TYPE, "text"))
                .attribute(new Attribute(ATT_VALUE, hidden.getValue()))
                .clazz(CLASS_HIDDEN)
                .dataAttribue(DATA_ATT_SUBMIT_ID, formId)
                .dataAttribue(DATA_ATT_SUBMIT_AS, hidden.getSubmitAs())
                .dataAttribue(DATA_ATT_VALUE_TYPE, hidden.getType().name())
                .dataAttribues(hidden.getDataAttributes())
                .build()
                .html();

        HtmlElementMapper elementMapper = HtmlElementMapper.builder().tag(TAG_DIV).content(input).build();

        return elementMapper.html();
    }

    private String render(Link link, String formId)
    {
        // TODO: implement
        return elementNotYetImplemented("link input");
    }

    private String render(Number number, String formId)
    {
        String            label         = HtmlElementMapper
                .builder()
                .tag(TAG_LABEL)
                .attribute(new Attribute(ATT_FOR, number.getUid()))
                .clazz(CLASS_FORM_LABEL)
                .content(number.getName())
                .build()
                .html();

        String            input         = HtmlElementMapper
                .builder()
                .isSingleTag(true)
                .tag(TAG_INPUT)
                .attribute(new Attribute(ATT_ID, number.getUid()))
                .attribute(new Attribute(ATT_TYPE, number.getType().name().toLowerCase()))
                .attribute(new Attribute(ATT_VALUE, number.getValue().toString()))
                .attribute(new Attribute(ATT_PLACEHOLDER, number.getPlaceholder()))
                .clazz(CLASS_FORM_CONTROL)
                .dataAttribue(DATA_ATT_SUBMIT_ID, formId)
                .dataAttribue(DATA_ATT_SUBMIT_AS, number.getSubmitAs())
                .dataAttribue(DATA_ATT_VALUE_TYPE, number.getType().name())
                .dataAttribues(number.getDataAttributes())
                .build()
                .html();

        HtmlElementMapper elementMapper = HtmlElementMapper.builder().tag(TAG_DIV).content(StringAdapter.from(label, input)).build();

        return elementMapper.html();
    }

    private String render(Password password, String formId)
    {
        String            label         = HtmlElementMapper
                .builder()
                .tag(TAG_LABEL)
                .attribute(new Attribute(ATT_FOR, password.getUid()))
                .clazz(CLASS_FORM_LABEL)
                .content(password.getName())
                .build()
                .html();

        String            input         = HtmlElementMapper
                .builder()
                .isSingleTag(true)
                .tag(TAG_INPUT)
                .attribute(new Attribute(ATT_ID, password.getUid()))
                .attribute(new Attribute(ATT_TYPE, password.getType().name().toLowerCase()))
                .attribute(new Attribute(ATT_VALUE, ""))
                .attribute(new Attribute(ATT_PLACEHOLDER, password.getPlaceholder()))
                .clazz(CLASS_FORM_CONTROL)
                .dataAttribue(DATA_ATT_SUBMIT_ID, formId)
                .dataAttribue(DATA_ATT_SUBMIT_AS, password.getSubmitAs())
                .dataAttribue(DATA_ATT_VALUE_TYPE, password.getType().name())
                .dataAttribues(password.getDataAttributes())
                .build()
                .html();

        HtmlElementMapper elementMapper = HtmlElementMapper.builder().tag(TAG_DIV).content(StringAdapter.from(label, input)).build();

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

            String            input    = HtmlElementMapper
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

            String            btnLabel = HtmlElementMapper
                    .builder()
                    .tag(TAG_LABEL)
                    .attribute(new Attribute(ATT_FOR, radioID))
                    .clazz(CLASS_FORM_CHECK_LABEL)
                    .clazz(CLASS_USER_SELECT_NONE)
                    .content(btn.getText())
                    .build()
                    .html();

            HtmlElementMapper innerDiv = HtmlElementMapper
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

        String            outerLabel =
                HtmlElementMapper.builder().tag(TAG_LABEL).clazz(CLASS_USER_SELECT_NONE).content(radio.getName()).build().html();

        HtmlElementMapper outerDiv   = HtmlElementMapper
                .builder()
                .tag(TAG_DIV)
                .clazz(CLASS_FORM_CHECK)
                .dataAttribue(DATA_ATT_SUBMIT_ID, formId)
                .dataAttribue(DATA_ATT_SUBMIT_AS, radio.getSubmitAs())
                .dataAttribue(DATA_ATT_VALUE_TYPE, radio.getType().name())
                .content(StringAdapter.from(outerLabel, StringAdapter.from(buttons)))
                .build();

        return outerDiv.html();
    }

    private String render(Select select, String formId)
    {
        ArrayList<String> selections = new ArrayList<>();

        for (InputValue selection : select.getValues())
        {
            String option = HtmlElementMapper
                    .builder()
                    .tag(TAG_OPTION)
                    .attribute(new Attribute(ATT_VALUE, selection.getValue()))
                    .attribute(new Attribute(ATT_SELECTED, selection.getChecked()))
                    .content(selection.getText())
                    .build()
                    .html();

            selections.add(option);
        }

        String            label         = HtmlElementMapper
                .builder()
                .tag(TAG_LABEL)
                .attribute(new Attribute(ATT_FOR, select.getUid()))
                .clazz(CLASS_FORM_LABEL)
                .content(select.getName())
                .build()
                .html();

        String            input         = HtmlElementMapper
                .builder()
                .tag(TAG_SELECT)
                .attribute(new Attribute(ATT_ID, select.getUid()))
                .attribute(new Attribute(ATT_ON_INPUT, select.getOnInput()))
                .clazz("form-select")
                .dataAttribue(DATA_ATT_SUBMIT_ID, formId)
                .dataAttribue(DATA_ATT_SUBMIT_AS, select.getSubmitAs())
                .dataAttribue(DATA_ATT_VALUE_TYPE, select.getType().name())
                .dataAttribues(select.getDataAttributes())
                .content(StringAdapter.from(selections))
                .build()
                .html();

        HtmlElementMapper elementMapper = HtmlElementMapper.builder().tag(TAG_DIV).content(StringAdapter.from(label, input)).build();

        return elementMapper.html();
    }

    private String render(Slider slider, String formId)
    {
        String            label         = HtmlElementMapper
                .builder()
                .tag(TAG_LABEL)
                .attribute(new Attribute(ATT_FOR, slider.getUid()))
                .clazz(CLASS_FORM_LABEL)
                .content(slider.getName())
                .build()
                .html();

        String            input         = HtmlElementMapper
                .builder()
                .isSingleTag(true)
                .tag(TAG_INPUT)
                .attribute(new Attribute(ATT_ID, slider.getUid()))
                .attribute(new Attribute(ATT_TYPE, "range"))
                .attribute(new Attribute(ATT_VALUE, slider.getValue().toString()))
                .attribute(new Attribute(ATT_ON_INPUT, slider.getOnInput()))
                .attribute(new Attribute(ATT_MIN, slider.getMin().toString()))
                .attribute(new Attribute(ATT_MAX, slider.getMax().toString()))
                .clazz("form-range")
                .dataAttribue(DATA_ATT_SUBMIT_ID, formId)
                .dataAttribue(DATA_ATT_SUBMIT_AS, slider.getSubmitAs())
                .dataAttribue(DATA_ATT_VALUE_TYPE, slider.getType().name())
                .dataAttribues(slider.getDataAttributes())
                .build()
                .html();

        HtmlElementMapper elementMapper = HtmlElementMapper.builder().tag(TAG_DIV).content(StringAdapter.from(label, input)).build();

        return elementMapper.html();
    }

    private String render(Switch switch1, String formId)
    {
        String            input         = HtmlElementMapper
                .builder()
                .isSingleTag(true)
                .tag(TAG_INPUT)
                .attribute(new Attribute(ATT_ID, switch1.getUid()))
                .attribute(new Attribute(ATT_TYPE, "checkbox"))
                // .attribute(ATT_ROLE, switch1.getType().name().toLowerCase())
                .attribute(new Attribute(ATT_ON_INPUT, switch1.getOnInput()))
                .attribute(new Attribute(ATT_CHECKED, switch1.getChecked()))
                .clazz(CLASS_FORM_CHECK_INPUT)
                .dataAttribue(DATA_ATT_SUBMIT_ID, formId)
                .dataAttribue(DATA_ATT_SUBMIT_AS, switch1.getSubmitAs())
                .dataAttribue(DATA_ATT_VALUE_TYPE, switch1.getType().name())
                .dataAttribues(switch1.getDataAttributes())
                .build()
                .html();

        String            label         = HtmlElementMapper
                .builder()
                .tag(TAG_LABEL)
                .attribute(new Attribute(ATT_FOR, switch1.getUid()))
                .clazz(CLASS_FORM_CHECK_LABEL)
                .clazz(CLASS_USER_SELECT_NONE)
                .content(switch1.getName())
                .build()
                .html();

        HtmlElementMapper elementMapper = HtmlElementMapper
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
        HtmlElementMapper elementMapper = HtmlElementMapper
                .builder()
                .tag(TAG_DIV)
                .attribute(new Attribute(ATT_ID, textbox.getUid()))
                .clazz("text-preserve-newline")
                .classes(textbox.getClasses())
                .dataAttribues(textbox.getDataAttributes())
                .content(textbox.getValue())
                .build();

        return elementMapper.html();
    }

    private String render(Textfield textfield, String formId)
    {
        String            label         = HtmlElementMapper
                .builder()
                .tag(TAG_LABEL)
                .attribute(new Attribute(ATT_FOR, textfield.getUid()))
                .clazz(CLASS_FORM_LABEL)
                .content(textfield.getName())
                .build()
                .html();

        String            input         = HtmlElementMapper
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
                .dataAttribue(DATA_ATT_SUBMIT_ID, formId)
                .dataAttribue(DATA_ATT_SUBMIT_AS, textfield.getSubmitAs())
                .dataAttribue(DATA_ATT_VALUE_TYPE, textfield.getType().name())
                .dataAttribue(DATA_ATT_MAX_CHARACTERS, textfield.getMaxCharacters().toString())
                .dataAttribues(textfield.getDataAttributes())
                .build()
                .html();

        HtmlElementMapper elementMapper = HtmlElementMapper.builder().tag(TAG_DIV).content(StringAdapter.from(label, input)).build();

        return elementMapper.html();
    }
}
