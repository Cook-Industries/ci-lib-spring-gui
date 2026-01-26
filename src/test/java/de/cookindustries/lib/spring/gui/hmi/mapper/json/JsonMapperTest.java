/**
 * Copyright (c) 2016-2026 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.hmi.mapper.json;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Locale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.cookindustries.lib.spring.gui.config.properties.CiLibProperties;
import de.cookindustries.lib.spring.gui.config.properties.CiLibResourcesPathProperties;
import de.cookindustries.lib.spring.gui.config.properties.CiLibResourcesProperties;
import de.cookindustries.lib.spring.gui.hmi.container.*;
import de.cookindustries.lib.spring.gui.hmi.input.*;
import de.cookindustries.lib.spring.gui.hmi.input.Number;
import de.cookindustries.lib.spring.gui.hmi.input.util.InputValue;
import de.cookindustries.lib.spring.gui.hmi.input.util.InputValueList;
import de.cookindustries.lib.spring.gui.hmi.mapper.html.HtmlMapper;
import de.cookindustries.lib.spring.gui.hmi.mapper.util.FlatMappableDissector;
import de.cookindustries.lib.spring.gui.hmi.mapper.util.TokenMap;
import de.cookindustries.lib.spring.gui.hmi.util.TemplateFileCache;
import de.cookindustries.lib.spring.gui.i18n.AbsTranslationProvider;
import de.cookindustries.lib.spring.gui.i18n.StaticTranslationProvider;

public class JsonMapperTest
{

    private TemplateFileCache      templateFileCache;
    private FlatMappableDissector  flatMappableDissector;
    private AbsTranslationProvider translationProvider;

    @BeforeEach
    public void init()
    {
        CiLibResourcesPathProperties resPath = new CiLibResourcesPathProperties();
        resPath.setTemplates("json-test-files");

        CiLibResourcesProperties res = new CiLibResourcesProperties();
        res.setPath(resPath);

        CiLibProperties properties = new CiLibProperties();
        properties.setResources(res);

        templateFileCache = new TemplateFileCache(properties);
        templateFileCache.init();

        flatMappableDissector = new FlatMappableDissector(new StaticTranslationProvider());

        translationProvider = new StaticTranslationProvider();
    }

    private void checkAgainstErrorContainer(Container container)
    {
        assertEquals(TextContainer.class, container.getClass());

        TextContainer text = (TextContainer) container;
        assertTrue(text.getText().startsWith("the creation of this element failed. please refer to the server log. mapper id: "));
    }

    private boolean checkDivTags(String html)
    {
        return checkTags("div", html);
    }

    private boolean checkTags(String tag, String html)
    {
        int count = 0;
        int index = 0;

        while ((index = html.indexOf(tag, index)) != -1)
        {
            count++;
            index += tag.length();
        }

        return count % 2 == 0;
    }

    @Test
    void test_map_withComponent()
    {
        // setup
        JsonMapper mapper =
            JsonMapper
                .builder()
                .rscPath("json-mapper/root-component.json")
                .locale(Locale.ENGLISH)
                .templateFileCache(templateFileCache)
                .translationProvider(translationProvider)
                .flatMappableDissector(flatMappableDissector)
                .build();

        // run
        MapperResult result = mapper.map();

        // verify
        assertNotNull(result);
    }

    @Test
    void test_map_resolveUid()
    {
        // setup
        TokenMap     tokenMap =
            TokenMap
                .builder()
                .value("changeIt", "changed-uid without-forbidden%$§$§\"-chars")
                .build();

        JsonMapper   mapper   =
            JsonMapper
                .builder()
                .rscPath("json-mapper/replace-uid.json")
                .locale(Locale.ENGLISH)
                .templateFileCache(templateFileCache)
                .translationProvider(translationProvider)
                .flatMappableDissector(flatMappableDissector)
                .tokenMap(tokenMap)
                .build();

        // run
        MapperResult result   = mapper.map();

        // verify
        assertNotNull(result);

        // verify
        assertEquals("changed-uid-without-forbidden-chars", result.getContainers().get(0).getUid());
    }

    @Test
    void test_map_replaceClass_withExisting()
    {
        // setup
        TokenMap     tokenMap =
            TokenMap
                .builder()
                .clazz("class1", "testClass")
                .build();

        JsonMapper   mapper   =
            JsonMapper
                .builder()
                .rscPath("json-mapper/replace-class.json")
                .locale(Locale.ENGLISH)
                .templateFileCache(templateFileCache)
                .translationProvider(translationProvider)
                .flatMappableDissector(flatMappableDissector)
                .tokenMap(tokenMap)
                .build();

        // run
        MapperResult result   = mapper.map();

        // verify
        assertNotNull(result);

        // verify
        assertTrue(result.getContainers().get(0).getClasses().contains("testClass"));
        assertTrue(result.getContainers().get(0).getClasses().contains("class2"));
    }

    @Test
    void test_map_replaceClass_notExisting()
    {
        // setup
        TokenMap     tokenMap =
            TokenMap
                .builder()
                .build();

        JsonMapper   mapper   =
            JsonMapper
                .builder()
                .rscPath("json-mapper/replace-class.json")
                .locale(Locale.ENGLISH)
                .templateFileCache(templateFileCache)
                .translationProvider(translationProvider)
                .flatMappableDissector(flatMappableDissector)
                .tokenMap(tokenMap)
                .build();

        // run
        MapperResult result   = mapper.map();

        // verify
        assertFalse(
            result
                .getContainers()
                .get(0)
                .getClasses()
                .contains("testClass"));
        assertTrue(
            result
                .getContainers()
                .get(0)
                .getClasses()
                .contains("class2"));
    }

    @Test
    void test_map_attributes()
    {
        TokenMap     tokenMap  =
            TokenMap
                .builder()
                .value("test", "replaced-key")
                .build();

        // setup
        JsonMapper   mapper    =
            JsonMapper
                .builder()
                .rscPath("json-mapper/map-attributes.json")
                .locale(Locale.ENGLISH)
                .templateFileCache(templateFileCache)
                .translationProvider(translationProvider)
                .flatMappableDissector(flatMappableDissector)
                .tokenMap(tokenMap)
                .build();

        // run
        MapperResult result    = mapper.map();

        // verify
        Container    container = result.getContainers().get(0);
        assertNotNull(container);
        assertEquals(4, container.getDataAttributes().size());
        assertNotNull(container.getDataAttributes().get("key1"));
        assertNotNull(container.getDataAttributes().get("key2"));
        assertNotNull(container.getDataAttributes().get("replaced-key"));
        assertNotNull(container.getDataAttributes().get("_forbidden-chars-2"));
    }

    @Test
    void test_map_parameter()
    {
        // setup
        TokenMap     tokenMap =
            TokenMap
                .builder()
                .value("param", "testText")
                .build();

        JsonMapper   mapper   =
            JsonMapper
                .builder()
                .rscPath("json-mapper/replace-parameter.json")
                .locale(Locale.ENGLISH)
                .templateFileCache(templateFileCache)
                .translationProvider(translationProvider)
                .flatMappableDissector(flatMappableDissector)
                .tokenMap(tokenMap)
                .build();

        // run
        MapperResult result   = mapper.map();

        // verify
        assertEquals("testText", ((TextContainer) result.getContainers().get(0)).getText());
    }

    @Test
    void test_map_AudioContainer()
    {
        // setup
        JsonMapper   mapper    =
            JsonMapper
                .builder()
                .rscPath("json-mapper/container/audio-container.json")
                .locale(Locale.ENGLISH)
                .templateFileCache(templateFileCache)
                .translationProvider(translationProvider)
                .flatMappableDissector(flatMappableDissector)
                .build();

        // run
        MapperResult result    = mapper.map();

        // verify
        Container    container = result.getContainers().get(0);
        assertEquals(AudioContainer.class, container.getClass());
        assertEquals("uid", container.getUid());
        assertEquals(1, container.getClasses().size());
        assertEquals(1, container.getDataAttributes().size());

        AudioContainer audioContainer = (AudioContainer) container;
        assertEquals("path/to/resource", audioContainer.getSrc());
        assertEquals(false, audioContainer.getControls());
        assertEquals(true, audioContainer.getAutoplay());

        String html = HtmlMapper.map(container);
        assertTrue(checkDivTags(html));
    }

    @Test
    void test_map_Button()
    {
        // setup
        JsonMapper   mapper    =
            JsonMapper
                .builder()
                .rscPath("json-mapper/container/button-container.json")
                .locale(Locale.ENGLISH)
                .templateFileCache(templateFileCache)
                .translationProvider(translationProvider)
                .flatMappableDissector(flatMappableDissector)
                .build();

        // run
        MapperResult result    = mapper.map();

        // verify
        Container    container = result.getContainers().get(0);
        assertEquals(Button.class, container.getClass());

        Button button = (Button) container;
        assertEquals("uid", button.getUid());
        assertEquals(1, button.getClasses().size());
        assertEquals(1, button.getDataAttributes().size());
        assertEquals("this is a button", button.getText());
        assertEquals("doShit()", button.getOnClick());
        assertEquals(ButtonClass.SUCCESS, button.getBtnClass());

        String html = HtmlMapper.map(container);
        assertTrue(checkDivTags(html));
    }

    // @Test
    void test_map_ButtonBarContainer()
    {
        // setup
        JsonMapper mapper =
            JsonMapper
                .builder()
                .rscPath("json-mapper/container/button-bar-container.json")
                .locale(Locale.ENGLISH)
                .templateFileCache(templateFileCache)
                .translationProvider(translationProvider)
                .flatMappableDissector(flatMappableDissector)
                .build();

        // run
        MapperResult result = mapper.map();

        // verify
        checkAgainstErrorContainer(result.getContainers().get(0));
    }

    // @Test
    void test_map_ButtonIconContainer()
    {
        // setup
        JsonMapper mapper =
            JsonMapper
                .builder()
                .rscPath("json-mapper/container/button-icon-container.json")
                .locale(Locale.ENGLISH)
                .templateFileCache(templateFileCache)
                .translationProvider(translationProvider)
                .flatMappableDissector(flatMappableDissector)
                .build();

        // run
        MapperResult result = mapper.map();

        // verify
        checkAgainstErrorContainer(result.getContainers().get(0));
    }

    @Test
    void test_map_ContentContainer()
    {
        // setup
        JsonMapper   mapper    =
            JsonMapper
                .builder()
                .rscPath("json-mapper/container/content-container.json")
                .locale(Locale.ENGLISH)
                .templateFileCache(templateFileCache)
                .translationProvider(translationProvider)
                .flatMappableDissector(flatMappableDissector)
                .build();

        // run
        MapperResult result    = mapper.map();

        // verify
        Container    container = result.getContainers().get(0);
        assertEquals(ContentContainer.class, container.getClass());
        assertEquals("uid", container.getUid());
        assertEquals(1, container.getClasses().size());
        assertEquals(1, container.getDataAttributes().size());

        String html = HtmlMapper.map(container);
        assertTrue(checkDivTags(html));
    }

    @Test
    void test_map_FormContainer()
    {
        // setup
        JsonMapper   mapper    =
            JsonMapper
                .builder()
                .rscPath("json-mapper/container/form-container.json")
                .locale(Locale.ENGLISH)
                .templateFileCache(templateFileCache)
                .translationProvider(translationProvider)
                .flatMappableDissector(flatMappableDissector)
                .build();

        // run
        MapperResult result    = mapper.map();

        // verify
        Container    container = result.getContainers().get(0);
        assertEquals(FormContainer.class, container.getClass());
        assertEquals("uid", container.getUid());
        assertEquals(1, container.getClasses().size());
        assertEquals(1, container.getDataAttributes().size());

        String html = HtmlMapper.map(container);
        assertTrue(checkDivTags(html));
    }

    @Test
    void test_map_HiddenContainer()
    {
        // setup
        JsonMapper   mapper    =
            JsonMapper
                .builder()
                .rscPath("json-mapper/container/hidden-container.json")
                .locale(Locale.ENGLISH)
                .templateFileCache(templateFileCache)
                .translationProvider(translationProvider)
                .flatMappableDissector(flatMappableDissector)
                .build();

        // run
        MapperResult result    = mapper.map();

        // verify
        Container    container = result.getContainers().get(0);
        assertEquals(HiddenContainer.class, container.getClass());
        assertEquals("uid", container.getUid());
        assertEquals(1, container.getClasses().size());
        assertEquals(1, container.getDataAttributes().size());

        String html = HtmlMapper.map(container);
        assertTrue(checkDivTags(html));
    }

    @Test
    void test_map_ImageContainer()
    {
        // setup
        JsonMapper   mapper    =
            JsonMapper
                .builder()
                .rscPath("json-mapper/container/image-container.json")
                .locale(Locale.ENGLISH)
                .templateFileCache(templateFileCache)
                .translationProvider(translationProvider)
                .flatMappableDissector(flatMappableDissector)
                .build();

        // run
        MapperResult result    = mapper.map();

        // verify
        Container    container = result.getContainers().get(0);
        assertEquals(ImageContainer.class, container.getClass());
        assertEquals("uid", container.getUid());
        assertEquals(1, container.getClasses().size());
        assertEquals(1, container.getDataAttributes().size());

        ImageContainer imageContainer = (ImageContainer) container;
        assertEquals("path/to/resource", imageContainer.getSrc());

        String html = HtmlMapper.map(container);
        assertFalse(checkTags("img", html));
    }

    @Test
    void test_map_LinkContainer()
    {
        // setup
        JsonMapper   mapper    =
            JsonMapper
                .builder()
                .rscPath("json-mapper/container/link-container.json")
                .locale(Locale.ENGLISH)
                .templateFileCache(templateFileCache)
                .translationProvider(translationProvider)
                .flatMappableDissector(flatMappableDissector)
                .build();

        // run
        MapperResult result    = mapper.map();

        // verify
        Container    container = result.getContainers().get(0);
        assertEquals(LinkContainer.class, container.getClass());
        assertEquals("uid", container.getUid());
        assertEquals(1, container.getClasses().size());
        assertEquals(1, container.getDataAttributes().size());

        LinkContainer linkContainer = (LinkContainer) container;
        assertEquals("link/to/somewhere", linkContainer.getHref());
        assertEquals("_self", linkContainer.getTarget());

        String html = HtmlMapper.map(container);
        assertTrue(checkDivTags(html));
    }

    @Test
    void test_map_SplittedContainer()
    {
        // setup
        JsonMapper   mapper    =
            JsonMapper
                .builder()
                .rscPath("json-mapper/container/splitted-container.json")
                .locale(Locale.ENGLISH)
                .templateFileCache(templateFileCache)
                .translationProvider(translationProvider)
                .flatMappableDissector(flatMappableDissector)
                .build();

        // run
        MapperResult result    = mapper.map();

        // verify
        Container    container = result.getContainers().get(0);
        assertEquals(SplittedContainer.class, container.getClass());
        assertEquals("uid", container.getUid());
        assertEquals(1, container.getClasses().size());
        assertEquals(1, container.getDataAttributes().size());

        SplittedContainer splittedContainer = (SplittedContainer) container;
        assertNotNull(splittedContainer.getHead());
        assertNotNull(splittedContainer.getTail());
        assertEquals(2, splittedContainer.getCenter().size());

        String html = HtmlMapper.map(container);
        assertTrue(checkDivTags(html));
    }

    // @Test
    void test_map_TabbedContainer()
    {
        // setup
        JsonMapper mapper =
            JsonMapper
                .builder()
                .rscPath("json-mapper/container/tabbed-container.json")
                .locale(Locale.ENGLISH)
                .templateFileCache(templateFileCache)
                .translationProvider(translationProvider)
                .flatMappableDissector(flatMappableDissector)
                .build();

        // run
        MapperResult result = mapper.map();

        // verify
        checkAgainstErrorContainer(result.getContainers().get(0));
    }

    @Test
    void test_map_TextContainer()
    {
        // setup
        JsonMapper   mapper    =
            JsonMapper
                .builder()
                .rscPath("json-mapper/container/text-container.json")
                .locale(Locale.ENGLISH)
                .templateFileCache(templateFileCache)
                .translationProvider(translationProvider)
                .flatMappableDissector(flatMappableDissector)
                .build();

        // run
        MapperResult result    = mapper.map();

        // verify
        Container    container = result.getContainers().get(0);
        assertEquals(TextContainer.class, container.getClass());
        assertEquals("uid", container.getUid());
        assertEquals(1, container.getClasses().size());
        assertEquals(1, container.getDataAttributes().size());

        TextContainer textContainer = (TextContainer) container;
        assertEquals("some text", textContainer.getText());

        String html = HtmlMapper.map(container);
        assertTrue(checkDivTags(html));
    }

    @Test
    void test_map_CheckboxInput()
    {
        // setup
        JsonMapper    mapper    =
            JsonMapper
                .builder()
                .rscPath("json-mapper/input/checkbox-input.json")
                .locale(Locale.ENGLISH)
                .templateFileCache(templateFileCache)
                .translationProvider(translationProvider)
                .flatMappableDissector(flatMappableDissector)
                .build();

        // run
        MapperResult  result    = mapper.map();

        // verify
        Container     container = result.getContainers().get(0);
        FormContainer form      = (FormContainer) container;
        Input         input     = form.getInputs().get(0);
        assertEquals(Checkbox.class, input.getClass());

        Checkbox checkbox = (Checkbox) input;
        assertEquals("name", checkbox.getName());
        assertEquals("checkbox", checkbox.getSubmitAs());

        String html = HtmlMapper.map(container);
        assertTrue(checkDivTags(html));
    }

    @Test
    void test_map_CurrencyInput()
    {
        // setup
        JsonMapper    mapper    =
            JsonMapper
                .builder()
                .rscPath("json-mapper/input/currency-input.json")
                .locale(Locale.ENGLISH)
                .templateFileCache(templateFileCache)
                .translationProvider(translationProvider)
                .flatMappableDissector(flatMappableDissector)
                .build();

        // run
        MapperResult  result    = mapper.map();

        // verify
        Container     container = result.getContainers().get(0);
        FormContainer form      = (FormContainer) container;
        Input         input     = form.getInputs().get(0);
        assertEquals(Currency.class, input.getClass());

        Currency currency = (Currency) input;
        assertEquals("name", currency.getName());
        assertEquals("currency", currency.getSubmitAs());

        String html = HtmlMapper.map(container);
        assertTrue(checkDivTags(html));
    }

    @Test
    void test_map_DateInput()
    {
        // setup
        JsonMapper    mapper    =
            JsonMapper
                .builder()
                .rscPath("json-mapper/input/date-input.json")
                .locale(Locale.ENGLISH)
                .templateFileCache(templateFileCache)
                .translationProvider(translationProvider)
                .flatMappableDissector(flatMappableDissector)
                .build();

        // run
        MapperResult  result    = mapper.map();

        // verify
        Container     container = result.getContainers().get(0);
        FormContainer form      = (FormContainer) container;
        Input         input     = form.getInputs().get(0);
        assertEquals(Date.class, input.getClass());

        Date date = (Date) input;
        assertEquals("name", date.getName());
        assertEquals("date", date.getSubmitAs());

        String html = HtmlMapper.map(container);
        assertTrue(checkDivTags(html));
    }

    @Test
    void test_map_FileInput()
    {
        // setup
        JsonMapper    mapper    =
            JsonMapper
                .builder()
                .rscPath("json-mapper/input/file-input.json")
                .locale(Locale.ENGLISH)
                .templateFileCache(templateFileCache)
                .translationProvider(translationProvider)
                .flatMappableDissector(flatMappableDissector)
                .build();

        // run
        MapperResult  result    = mapper.map();

        // verify
        Container     container = result.getContainers().get(0);
        FormContainer form      = (FormContainer) container;
        Input         input     = form.getInputs().get(0);
        assertEquals(File.class, input.getClass());

        File file = (File) input;
        assertEquals("name", file.getName());
        assertEquals("file", file.getSubmitAs());

        String html = HtmlMapper.map(container);
        assertTrue(checkDivTags(html));
    }

    @Test
    void test_map_HiddenInput()
    {
        // setup
        JsonMapper    mapper    =
            JsonMapper
                .builder()
                .rscPath("json-mapper/input/hidden-input.json")
                .locale(Locale.ENGLISH)
                .templateFileCache(templateFileCache)
                .translationProvider(translationProvider)
                .flatMappableDissector(flatMappableDissector)
                .build();

        // run
        MapperResult  result    = mapper.map();

        // verify
        Container     container = result.getContainers().get(0);
        FormContainer form      = (FormContainer) container;
        Input         input     = form.getInputs().get(0);
        assertEquals(Hidden.class, input.getClass());

        Hidden hidden = (Hidden) input;
        assertEquals("hidden", hidden.getSubmitAs());

        String html = HtmlMapper.map(container);
        assertTrue(checkDivTags(html));
    }

    @Test
    void test_map_LinkInput()
    {
        // setup
        JsonMapper    mapper    =
            JsonMapper
                .builder()
                .rscPath("json-mapper/input/link-input.json")
                .locale(Locale.ENGLISH)
                .templateFileCache(templateFileCache)
                .translationProvider(translationProvider)
                .flatMappableDissector(flatMappableDissector)
                .build();

        // run
        MapperResult  result    = mapper.map();

        // verify
        Container     container = result.getContainers().get(0);
        FormContainer form      = (FormContainer) container;
        Input         input     = form.getInputs().get(0);
        assertEquals(Link.class, input.getClass());

        Link link = (Link) input;
        assertEquals("link", link.getText());
        assertEquals("link/to/somewhere", link.getHref());

        String html = HtmlMapper.map(container);
        assertTrue(checkDivTags(html));
    }

    @Test
    void test_map_NumberInput()
    {
        // setup
        JsonMapper    mapper    =
            JsonMapper
                .builder()
                .rscPath("json-mapper/input/number-input.json")
                .locale(Locale.ENGLISH)
                .templateFileCache(templateFileCache)
                .translationProvider(translationProvider)
                .flatMappableDissector(flatMappableDissector)
                .build();

        // run
        MapperResult  result    = mapper.map();

        // verify
        Container     container = result.getContainers().get(0);
        FormContainer form      = (FormContainer) container;
        Input         input     = form.getInputs().get(0);
        assertEquals(Number.class, input.getClass());

        Number number = (Number) input;
        assertEquals("name", number.getName());
        assertEquals("number", number.getSubmitAs());

        String html = HtmlMapper.map(container);
        assertTrue(checkDivTags(html));
    }

    @Test
    void test_map_PasswordInput()
    {
        // setup
        JsonMapper    mapper    =
            JsonMapper
                .builder()
                .rscPath("json-mapper/input/password-input.json")
                .locale(Locale.ENGLISH)
                .templateFileCache(templateFileCache)
                .translationProvider(translationProvider)
                .flatMappableDissector(flatMappableDissector)
                .build();

        // run
        MapperResult  result    = mapper.map();

        // verify
        Container     container = result.getContainers().get(0);
        FormContainer form      = (FormContainer) container;
        Input         input     = form.getInputs().get(0);
        assertEquals(Password.class, input.getClass());

        Password password = (Password) input;
        assertEquals("name", password.getName());
        assertEquals("password", password.getSubmitAs());

        String html = HtmlMapper.map(container);
        assertTrue(checkDivTags(html));
    }

    @Test
    void test_map_RadioInput()
    {
        // setup
        JsonMapper    mapper    =
            JsonMapper
                .builder()
                .rscPath("json-mapper/input/radio-input.json")
                .locale(Locale.ENGLISH)
                .templateFileCache(templateFileCache)
                .translationProvider(translationProvider)
                .flatMappableDissector(flatMappableDissector)
                .build();

        // run
        MapperResult  result    = mapper.map();

        // verify
        Container     container = result.getContainers().get(0);
        FormContainer form      = (FormContainer) container;
        Input         input     = form.getInputs().get(0);
        assertEquals(Radio.class, input.getClass());

        Radio radio = (Radio) input;
        assertEquals("name", radio.getName());
        assertEquals("radio", radio.getSubmitAs());
        assertEquals(2, radio.getValues().size());

        String html = HtmlMapper.map(container);
        assertTrue(checkDivTags(html));
    }

    @Test
    void test_map_SelectInput()
    {
        // setup
        InputValueList list = new InputValueList();
        list.add(
            InputValue
                .builder()
                .id("id")
                .text("text")
                .value("value")
                .build());
        TokenMap      map       =
            TokenMap
                .builder()
                .value("list", list)
                .build();
        JsonMapper    mapper    =
            JsonMapper
                .builder()
                .rscPath("json-mapper/input/select-input.json")
                .locale(Locale.ENGLISH)
                .templateFileCache(templateFileCache)
                .translationProvider(translationProvider)
                .flatMappableDissector(flatMappableDissector)
                .tokenMap(map)
                .build();

        // run
        MapperResult  result    = mapper.map();

        // verify
        Container     container = result.getContainers().get(0);
        FormContainer form      = (FormContainer) container;
        Input         input1    = form.getInputs().get(0);
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
        assertEquals(1, select2.getValues().size());

        String html = HtmlMapper.map(container);
        assertTrue(checkDivTags(html));
    }

    @Test
    void test_map_SliderInput()
    {
        // setup
        JsonMapper    mapper    =
            JsonMapper
                .builder()
                .rscPath("json-mapper/input/slider-input.json")
                .locale(Locale.ENGLISH)
                .templateFileCache(templateFileCache)
                .translationProvider(translationProvider)
                .flatMappableDissector(flatMappableDissector)
                .build();

        // run
        MapperResult  result    = mapper.map();

        // verify
        Container     container = result.getContainers().get(0);
        FormContainer form      = (FormContainer) container;
        Input         input     = form.getInputs().get(0);
        assertEquals(Slider.class, input.getClass());

        Slider slider = (Slider) input;
        assertEquals("name", slider.getName());
        assertEquals("slider", slider.getSubmitAs());

        String html = HtmlMapper.map(container);
        assertTrue(checkDivTags(html));
    }

    @Test
    void test_map_SwitchInput()
    {
        // setup
        JsonMapper    mapper    =
            JsonMapper
                .builder()
                .rscPath("json-mapper/input/switch-input.json")
                .locale(Locale.ENGLISH)
                .templateFileCache(templateFileCache)
                .translationProvider(translationProvider)
                .flatMappableDissector(flatMappableDissector)
                .build();

        // run
        MapperResult  result    = mapper.map();

        // verify
        Container     container = result.getContainers().get(0);
        FormContainer form      = (FormContainer) container;
        Input         input     = form.getInputs().get(0);
        assertEquals(Switch.class, input.getClass());

        Switch switchi = (Switch) input;
        assertEquals("name", switchi.getName());
        assertEquals("switch", switchi.getSubmitAs());

        String html = HtmlMapper.map(container);
        assertTrue(checkDivTags(html));
    }

    @Test
    void test_map_TextareaInput()
    {
        // setup
        JsonMapper    mapper    =
            JsonMapper
                .builder()
                .rscPath("json-mapper/input/textarea-input.json")
                .locale(Locale.ENGLISH)
                .templateFileCache(templateFileCache)
                .translationProvider(translationProvider)
                .flatMappableDissector(flatMappableDissector)
                .build();

        // run
        MapperResult  result    = mapper.map();

        // verify
        Container     container = result.getContainers().get(0);
        FormContainer form      = (FormContainer) container;
        Input         input     = form.getInputs().get(0);
        assertEquals(Textarea.class, input.getClass());

        Textarea textarea = (Textarea) input;
        assertEquals("name", textarea.getName());
        assertEquals("textarea", textarea.getSubmitAs());

        String html = HtmlMapper.map(container);
        assertTrue(checkDivTags(html));
    }

    @Test
    void test_map_TextboxInput()
    {
        // setup
        JsonMapper    mapper    =
            JsonMapper
                .builder()
                .rscPath("json-mapper/input/textbox-input.json")
                .locale(Locale.ENGLISH)
                .templateFileCache(templateFileCache)
                .translationProvider(translationProvider)
                .flatMappableDissector(flatMappableDissector)
                .build();

        // run
        MapperResult  result    = mapper.map();

        // verify
        Container     container = result.getContainers().get(0);
        FormContainer form      = (FormContainer) container;
        Input         input     = form.getInputs().get(0);
        assertEquals(Textbox.class, input.getClass());

        String html = HtmlMapper.map(container);
        assertTrue(checkDivTags(html));
    }

    @Test
    void test_map_TextfieldInput()
    {
        // setup
        JsonMapper    mapper    =
            JsonMapper
                .builder()
                .rscPath("json-mapper/input/textfield-input.json")
                .locale(Locale.ENGLISH)
                .templateFileCache(templateFileCache)
                .translationProvider(translationProvider)
                .flatMappableDissector(flatMappableDissector)
                .build();

        // run
        MapperResult  result    = mapper.map();

        // verify
        Container     container = result.getContainers().get(0);
        FormContainer form      = (FormContainer) container;
        Input         input     = form.getInputs().get(0);
        assertEquals(Textfield.class, input.getClass());

        Textfield textfield = (Textfield) input;
        assertEquals("name", textfield.getName());
        assertEquals("textfield", textfield.getSubmitAs());

        String html = HtmlMapper.map(container);
        assertTrue(checkDivTags(html));
    }
}
