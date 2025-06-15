/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * 
 * Licensed under the MIT License.
 * See LICENSE file in the project root for full license information.
 * 
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
package de.cookindustries.lib.spring.gui.hmi.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Locale;

import org.junit.jupiter.api.Test;

import de.cookindustries.lib.spring.gui.hmi.container.*;
import de.cookindustries.lib.spring.gui.hmi.input.*;
import de.cookindustries.lib.spring.gui.hmi.input.Number;
import de.cookindustries.lib.spring.gui.hmi.mapper.exception.JsonMapperException;
import de.cookindustries.lib.spring.gui.hmi.mapper.exception.JsonParsingException;
import de.cookindustries.lib.spring.gui.i18n.TranslationMap;

public class JsonMapperTest
{

    private JsonTreeRoot getRoot(String path)
    {
        JsonTreeMapper jsonTreeMapper = new JsonTreeMapper();

        return jsonTreeMapper.map(path);
    }

    @Test
    void test_map_basic()
    {
        // setup
        JsonTreeRoot root = getRoot("json-test-files/json-mapper/basic-root-static.json");

        // run
        Container container = JsonMapper.map(root);

        // verify
        assertNotNull(container);
    }

    @Test
    void test_map_customInputs()
    {
        // setup
        JsonTreeRoot root = getRoot("json-test-files/json-mapper/basic-root-static.json");

        // run
        Container container = JsonMapper.map(root, Locale.ENGLISH, new TranslationMap());

        // verify
        assertNotNull(container);
    }

    @Test
    void test_map_customInputs_throws_atNoValueMaps()
    {
        // setup
        JsonTreeRoot root = getRoot("json-test-files/json-mapper/basic-root-dynamic.json");

        // run & verify
        assertThrows(JsonMapperException.class, () -> JsonMapper.map(root, Locale.ENGLISH, new TranslationMap()));
    }

    @Test
    void test_map_withComponent()
    {
        // setup
        JsonTreeRoot root = getRoot("json-test-files/json-mapper/root-with-component.json");

        // run
        Container container = JsonMapper.map(root);

        // verify
        assertNotNull(container);
    }

    @Test
    void test_map_withComponent_throws()
    {
        // setup
        JsonTreeRoot root = getRoot("json-test-files/json-mapper/root-with-component-faulty.json");

        // run & verify
        assertThrows(JsonParsingException.class, () -> JsonMapper.map(root));
    }

    @Test
    void test_map_replaceClass_withExisting()
    {
        // setup
        JsonTreeRoot root     = getRoot("json-test-files/json-mapper/replace-class.json");
        ValueMap     valueMap = new ValueMap();
        valueMap.add("class1", "testClass");

        // run
        Container container = JsonMapper.map(root, Locale.ENGLISH, new TranslationMap(), valueMap);

        // verify
        assertTrue(container.getClasses().contains("testClass"));
        assertTrue(container.getClasses().contains("class2"));
    }

    @Test
    void test_map_replaceClass_notExisting()
    {
        // setup
        JsonTreeRoot root      = getRoot("json-test-files/json-mapper/replace-class.json");
        ValueMap     valueMap  = new ValueMap();

        // run
        Container    container = JsonMapper.map(root, Locale.ENGLISH, new TranslationMap(), valueMap);

        // verify
        assertFalse(container.getClasses().contains("testClass"));
        assertTrue(container.getClasses().contains("class2"));
    }

    @Test
    void test_map_attributes()
    {
        // setup
        JsonTreeRoot root = getRoot("json-test-files/json-mapper/map-attributes.json");

        // run
        Container container = JsonMapper.map(root);

        // verify
        assertNotNull(container.getDataAttributes().get("key1"));
    }

    @Test
    void test_map_parameter()
    {
        // setup
        JsonTreeRoot root     = getRoot("json-test-files/json-mapper/replace-parameter.json");
        ValueMap     valueMap = new ValueMap();
        valueMap.add("param", "testText");

        // run
        Container container = JsonMapper.map(root, Locale.ENGLISH, new TranslationMap(), valueMap);

        // verify
        assertEquals("testText", ((TextContainer) container).getText());
    }

    @Test
    void test_map_parameter_throws_onMiss()
    {
        // setup
        JsonTreeRoot root     = getRoot("json-test-files/json-mapper/parameter-missing.json");
        ValueMap     valueMap = new ValueMap();

        // run & verify
        assertThrows(JsonParsingException.class, () -> JsonMapper.map(root, Locale.ENGLISH, new TranslationMap(), valueMap));
    }

    @Test
    void test_map_audioContainer()
    {
        // setup
        JsonTreeRoot root      = getRoot("json-test-files/json-mapper/container/audio-container.json");

        // run
        Container    container = JsonMapper.map(root);

        // verify
        assertEquals(AudioContainer.class, container.getClass());
        assertEquals("uid", container.getUid());
        assertEquals(1, container.getClasses().size());
        assertEquals(1, container.getDataAttributes().size());

        AudioContainer audioContainer = (AudioContainer) container;
        assertEquals("path/to/resource", audioContainer.getSrc());
        assertEquals(Boolean.FALSE, audioContainer.getControls());
        assertEquals(Boolean.TRUE, audioContainer.getAutoplay());
    }

    @Test
    void test_map_ButtonContainer()
    {
        // setup
        JsonTreeRoot root      = getRoot("json-test-files/json-mapper/container/button-container.json");

        // run
        Container    container = JsonMapper.map(root);

        // verify
        assertEquals(ButtonContainer.class, container.getClass());

        Button button = ((ButtonContainer) container).getButton();
        assertEquals("uid", button.getUid());
        assertEquals(1, button.getClasses().size());
        assertEquals(1, button.getDataAttributes().size());
        assertEquals("this is a button", button.getText());
        assertEquals("doShit()", button.getOnClick());
        assertEquals(ButtonClass.SUCCESS, button.getBtnClass());
    }

    @Test
    void test_map_ButtonBarContainer()
    {
        // setup
        JsonTreeRoot root = getRoot("json-test-files/json-mapper/container/button-bar-container.json");

        // run & verify
        assertThrows(JsonParsingException.class, () -> JsonMapper.map(root));
    }

    @Test
    void test_map_ButtonIconContainer()
    {
        // setup
        JsonTreeRoot root = getRoot("json-test-files/json-mapper/container/button-icon-container.json");

        // run & verify
        assertThrows(JsonParsingException.class, () -> JsonMapper.map(root));
    }

    @Test
    void test_map_ContentContainer()
    {
        // setup
        JsonTreeRoot root      = getRoot("json-test-files/json-mapper/container/content-container.json");

        // run
        Container    container = JsonMapper.map(root);

        // verify
        assertEquals(ContentContainer.class, container.getClass());
        assertEquals("uid", container.getUid());
        assertEquals(1, container.getClasses().size());
        assertEquals(1, container.getDataAttributes().size());
    }

    @Test
    void test_map_FormContainer()
    {
        // setup
        JsonTreeRoot root      = getRoot("json-test-files/json-mapper/container/form-container.json");

        // run
        Container    container = JsonMapper.map(root);

        // verify
        assertEquals(FormContainer.class, container.getClass());
        assertEquals("uid", container.getUid());
        assertEquals(1, container.getClasses().size());
        assertEquals(1, container.getDataAttributes().size());
    }

    @Test
    void test_map_HiddenContainer()
    {
        // setup
        JsonTreeRoot root      = getRoot("json-test-files/json-mapper/container/hidden-container.json");

        // run
        Container    container = JsonMapper.map(root);

        // verify
        assertEquals(HiddenContainer.class, container.getClass());
        assertEquals("uid", container.getUid());
        assertEquals(1, container.getClasses().size());
        assertEquals(1, container.getDataAttributes().size());
    }

    @Test
    void test_map_ImageContainer()
    {
        // setup
        JsonTreeRoot root      = getRoot("json-test-files/json-mapper/container/image-container.json");

        // run
        Container    container = JsonMapper.map(root);

        // verify
        assertEquals(ImageContainer.class, container.getClass());
        assertEquals("uid", container.getUid());
        assertEquals(1, container.getClasses().size());
        assertEquals(1, container.getDataAttributes().size());

        ImageContainer imageContainer = (ImageContainer) container;
        assertEquals("path/to/resource", imageContainer.getSrc());
    }

    @Test
    void test_map_LinkContainer()
    {
        // setup
        JsonTreeRoot root      = getRoot("json-test-files/json-mapper/container/link-container.json");

        // run
        Container    container = JsonMapper.map(root);

        // verify
        assertEquals(LinkContainer.class, container.getClass());
        assertEquals("uid", container.getUid());
        assertEquals(1, container.getClasses().size());
        assertEquals(1, container.getDataAttributes().size());

        LinkContainer linkContainer = (LinkContainer) container;
        assertEquals("link/to/somewhere", linkContainer.getHref());
        assertEquals("_self", linkContainer.getTarget());
    }

    @Test
    void test_map_SplittedContainer()
    {
        // setup
        JsonTreeRoot root      = getRoot("json-test-files/json-mapper/container/splitted-container.json");

        // run
        Container    container = JsonMapper.map(root);

        // verify
        assertEquals(SplittedContainer.class, container.getClass());
        assertEquals("uid", container.getUid());
        assertEquals(1, container.getClasses().size());
        assertEquals(1, container.getDataAttributes().size());

        SplittedContainer splittedContainer = (SplittedContainer) container;
        assertNotNull(splittedContainer.getHead());
        assertNotNull(splittedContainer.getTail());
        assertEquals(2, splittedContainer.getCenter().size());
    }

    @Test
    void test_map_TabbedContainer()
    {
        // setup
        JsonTreeRoot root = getRoot("json-test-files/json-mapper/container/tabbed-container.json");

        // run & verify
        assertThrows(JsonParsingException.class, () -> JsonMapper.map(root));
    }

    @Test
    void test_map_TextContainer()
    {
        // setup
        JsonTreeRoot root      = getRoot("json-test-files/json-mapper/container/text-container.json");

        // run
        Container    container = JsonMapper.map(root);

        // verify
        assertEquals(TextContainer.class, container.getClass());
        assertEquals("uid", container.getUid());
        assertEquals(1, container.getClasses().size());
        assertEquals(1, container.getDataAttributes().size());

        TextContainer textContainer = (TextContainer) container;
        assertEquals("some text", textContainer.getText());
    }

    @Test
    void test_map_ButtonInput()
    {
        // setup
        JsonTreeRoot  root  = getRoot("json-test-files/json-mapper/input/button-input.json");

        // run
        FormContainer form  = (FormContainer) JsonMapper.map(root);

        // verify
        Input         input = form.getInputs().get(0);
        assertEquals(Button.class, input.getClass());

        Button button = (Button) input;
        assertEquals("text", button.getText());
        assertEquals(ButtonClass.SUCCESS, button.getBtnClass());
        assertEquals("doShit()", button.getOnClick());
    }

    @Test
    void test_map_CheckboxInput()
    {
        // setup
        JsonTreeRoot  root  = getRoot("json-test-files/json-mapper/input/checkbox-input.json");

        // run
        FormContainer form  = (FormContainer) JsonMapper.map(root);

        // verify
        Input         input = form.getInputs().get(0);
        assertEquals(Checkbox.class, input.getClass());

        Checkbox checkbox = (Checkbox) input;
        assertEquals("name", checkbox.getName());
        assertEquals("checkbox", checkbox.getSubmitAs());
    }

    @Test
    void test_map_CurrencyInput()
    {
        // setup
        JsonTreeRoot  root  = getRoot("json-test-files/json-mapper/input/currency-input.json");

        // run
        FormContainer form  = (FormContainer) JsonMapper.map(root);

        // verify
        Input         input = form.getInputs().get(0);
        assertEquals(Currency.class, input.getClass());

        Currency currency = (Currency) input;
        assertEquals("name", currency.getName());
        assertEquals("currency", currency.getSubmitAs());
    }

    @Test
    void test_map_DateInput()
    {
        // setup
        JsonTreeRoot  root  = getRoot("json-test-files/json-mapper/input/date-input.json");

        // run
        FormContainer form  = (FormContainer) JsonMapper.map(root);

        // verify
        Input         input = form.getInputs().get(0);
        assertEquals(Date.class, input.getClass());

        Date date = (Date) input;
        assertEquals("name", date.getName());
        assertEquals("date", date.getSubmitAs());
    }

    @Test
    void test_map_FileInput()
    {
        // setup
        JsonTreeRoot  root  = getRoot("json-test-files/json-mapper/input/file-input.json");

        // run
        FormContainer form  = (FormContainer) JsonMapper.map(root);

        // verify
        Input         input = form.getInputs().get(0);
        assertEquals(File.class, input.getClass());

        File file = (File) input;
        assertEquals("name", file.getName());
        assertEquals("file", file.getSubmitAs());
    }

    @Test
    void test_map_HiddenInput()
    {
        // setup
        JsonTreeRoot  root  = getRoot("json-test-files/json-mapper/input/hidden-input.json");

        // run
        FormContainer form  = (FormContainer) JsonMapper.map(root);

        // verify
        Input         input = form.getInputs().get(0);
        assertEquals(Hidden.class, input.getClass());

        Hidden hidden = (Hidden) input;
        assertEquals("hidden", hidden.getSubmitAs());
    }

    @Test
    void test_map_LinkInput()
    {
        // setup
        JsonTreeRoot  root  = getRoot("json-test-files/json-mapper/input/link-input.json");

        // run
        FormContainer form  = (FormContainer) JsonMapper.map(root);

        // verify
        Input         input = form.getInputs().get(0);
        assertEquals(Link.class, input.getClass());

        Link link = (Link) input;
        assertEquals("link", link.getText());
        assertEquals("link/to/somewhere", link.getHref());
    }

    @Test
    void test_map_NumberInput()
    {
        // setup
        JsonTreeRoot  root  = getRoot("json-test-files/json-mapper/input/number-input.json");

        // run
        FormContainer form  = (FormContainer) JsonMapper.map(root);

        // verify
        Input         input = form.getInputs().get(0);
        assertEquals(Number.class, input.getClass());

        Number number = (Number) input;
        assertEquals("name", number.getName());
        assertEquals("number", number.getSubmitAs());
    }

    @Test
    void test_map_PasswordInput()
    {
        // setup
        JsonTreeRoot  root  = getRoot("json-test-files/json-mapper/input/password-input.json");

        // run
        FormContainer form  = (FormContainer) JsonMapper.map(root);

        // verify
        Input         input = form.getInputs().get(0);
        assertEquals(Password.class, input.getClass());

        Password password = (Password) input;
        assertEquals("name", password.getName());
        assertEquals("password", password.getSubmitAs());
    }

    @Test
    void test_map_RadioInput()
    {
        // setup
        JsonTreeRoot  root  = getRoot("json-test-files/json-mapper/input/radio-input.json");

        // run
        FormContainer form  = (FormContainer) JsonMapper.map(root);

        // verify
        Input         input = form.getInputs().get(0);
        assertEquals(Radio.class, input.getClass());

        Radio radio = (Radio) input;
        assertEquals("name", radio.getName());
        assertEquals("radio", radio.getSubmitAs());
        assertEquals(2, radio.getValues().size());
    }

    @Test
    void test_map_SelectInput()
    {
        // setup
        JsonTreeRoot  root   = getRoot("json-test-files/json-mapper/input/select-input.json");

        // run
        FormContainer form   = (FormContainer) JsonMapper.map(root);

        // verify
        Input         input1 = form.getInputs().get(0);
        assertEquals(Select.class, input1.getClass());

        Input input2 = form.getInputs().get(1);
        assertEquals(Select.class, input2.getClass());

        Select select1 = (Select) input1;
        assertEquals("name", select1.getName());
        assertEquals("select1", select1.getSubmitAs());
        assertEquals(1, select1.getValues().size());

        Select select2 = (Select) input2;
        assertEquals("name", select2.getName());
        assertEquals("select2", select2.getSubmitAs());
        assertEquals(0, select2.getValues().size());
    }

    @Test
    void test_map_SliderInput()
    {
        // setup
        JsonTreeRoot  root  = getRoot("json-test-files/json-mapper/input/slider-input.json");

        // run
        FormContainer form  = (FormContainer) JsonMapper.map(root);

        // verify
        Input         input = form.getInputs().get(0);
        assertEquals(Slider.class, input.getClass());

        Slider slider = (Slider) input;
        assertEquals("name", slider.getName());
        assertEquals("slider", slider.getSubmitAs());
    }

    @Test
    void test_map_SwitchInput()
    {
        // setup
        JsonTreeRoot  root  = getRoot("json-test-files/json-mapper/input/switch-input.json");

        // run
        FormContainer form  = (FormContainer) JsonMapper.map(root);

        // verify
        Input         input = form.getInputs().get(0);
        assertEquals(Switch.class, input.getClass());

        Switch switchi = (Switch) input;
        assertEquals("name", switchi.getName());
        assertEquals("switch", switchi.getSubmitAs());
    }

    @Test
    void test_map_TextareaInput()
    {
        // setup
        JsonTreeRoot  root  = getRoot("json-test-files/json-mapper/input/textarea-input.json");

        // run
        FormContainer form  = (FormContainer) JsonMapper.map(root);

        // verify
        Input         input = form.getInputs().get(0);
        assertEquals(Textarea.class, input.getClass());

        Textarea textarea = (Textarea) input;
        assertEquals("name", textarea.getName());
        assertEquals("textarea", textarea.getSubmitAs());
    }

    @Test
    void test_map_TextboxInput()
    {
        // setup
        JsonTreeRoot  root  = getRoot("json-test-files/json-mapper/input/textbox-input.json");

        // run
        FormContainer form  = (FormContainer) JsonMapper.map(root);

        // verify
        Input         input = form.getInputs().get(0);
        assertEquals(Textbox.class, input.getClass());
    }

    @Test
    void test_map_TextfieldInput()
    {
        // setup
        JsonTreeRoot  root  = getRoot("json-test-files/json-mapper/input/textfield-input.json");

        // run
        FormContainer form  = (FormContainer) JsonMapper.map(root);

        // verify
        Input         input = form.getInputs().get(0);
        assertEquals(Textfield.class, input.getClass());

        Textfield textfield = (Textfield) input;
        assertEquals("name", textfield.getName());
        assertEquals("textfield", textfield.getSubmitAs());
    }
}
