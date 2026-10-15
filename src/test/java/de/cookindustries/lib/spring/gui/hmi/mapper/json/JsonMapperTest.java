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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.cookindustries.lib.spring.gui.config.properties.CiLibProperties;
import de.cookindustries.lib.spring.gui.config.properties.CiLibResourcesPathProperties;
import de.cookindustries.lib.spring.gui.config.properties.CiLibResourcesProperties;
import de.cookindustries.lib.spring.gui.function.CloseModal;
import de.cookindustries.lib.spring.gui.function.SubmitFromModal;
import de.cookindustries.lib.spring.gui.hmi.container.AudioContainer;
import de.cookindustries.lib.spring.gui.hmi.container.BurgerContainer;
import de.cookindustries.lib.spring.gui.hmi.container.Button;
import de.cookindustries.lib.spring.gui.hmi.container.ButtonClass;
import de.cookindustries.lib.spring.gui.hmi.container.ButtonIcon;
import de.cookindustries.lib.spring.gui.hmi.container.Container;
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
import de.cookindustries.lib.spring.gui.hmi.container.TableContainer;
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
import de.cookindustries.lib.spring.gui.hmi.input.util.InputValueList;
import de.cookindustries.lib.spring.gui.hmi.mapper.html.HtmlMapper;
import de.cookindustries.lib.spring.gui.hmi.mapper.util.FlatMappable;
import de.cookindustries.lib.spring.gui.hmi.mapper.util.FlatMappableDissector;
import de.cookindustries.lib.spring.gui.hmi.mapper.util.FlatMappableList;
import de.cookindustries.lib.spring.gui.hmi.mapper.util.TokenMap;
import de.cookindustries.lib.spring.gui.hmi.svg.SVGLine;
import de.cookindustries.lib.spring.gui.hmi.util.TemplateFileCache;
import de.cookindustries.lib.spring.gui.i18n.AbsTranslationProvider;
import de.cookindustries.lib.spring.gui.i18n.StaticTranslationProvider;

class JsonMapperTest
{

    private TemplateFileCache      templateFileCache;
    private FlatMappableDissector  flatMappableDissector;
    private AbsTranslationProvider translationProvider;

    @BeforeEach
    void init()
    {
        CiLibResourcesPathProperties resPath = new CiLibResourcesPathProperties();
        resPath.setTemplates("json-test-files");

        CiLibResourcesProperties res = new CiLibResourcesProperties();
        res.setPath(resPath);

        CiLibProperties properties = new CiLibProperties();
        properties.setResources(res);

        templateFileCache = new TemplateFileCache(properties);
        templateFileCache.init();

        flatMappableDissector = new FlatMappableDissector(new StaticTranslationProvider(), properties);

        translationProvider = new StaticTranslationProvider();
    }

    private void checkAgainstErrorContainer(Container container)
    {
        assertEquals(TextContainer.class, container.getClass());

        TextContainer text = (TextContainer) container;
        assertTrue(text.getText().startsWith("the creation of this element failed."));
    }

    private boolean checkDivTags(String html)
    {
        return checkTags("div", html);
    }

    private void checkToolTip(String content)
    {
        assertTrue(content.contains("data-tooltip=\"test tooltip\""), "expected tooltip parameter");
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
        JsonMapper   mapper =
            JsonMapper
                .builder()
                .srcPath("json-mapper/root-component.ged")
                .locale(Locale.ENGLISH)
                .templateFileCache(templateFileCache)
                .translationProvider(translationProvider)
                .flatMappableDissector(flatMappableDissector)
                .build();

        MapperResult result;

        // run
        result = mapper.map();

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
                .srcPath("json-mapper/replace-uid.ged")
                .locale(Locale.ENGLISH)
                .templateFileCache(templateFileCache)
                .translationProvider(translationProvider)
                .flatMappableDissector(flatMappableDissector)
                .tokenMap(tokenMap)
                .build();

        MapperResult result;

        // run
        result = mapper.map();

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
                .srcPath("json-mapper/replace-class.ged")
                .locale(Locale.ENGLISH)
                .templateFileCache(templateFileCache)
                .translationProvider(translationProvider)
                .flatMappableDissector(flatMappableDissector)
                .tokenMap(tokenMap)
                .build();

        MapperResult result;

        // run
        result = mapper.map();

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
                .srcPath("json-mapper/replace-class.ged")
                .locale(Locale.ENGLISH)
                .templateFileCache(templateFileCache)
                .translationProvider(translationProvider)
                .flatMappableDissector(flatMappableDissector)
                .tokenMap(tokenMap)
                .build();

        MapperResult result;

        // run
        result = mapper.map();

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
        TokenMap     tokenMap =
            TokenMap
                .builder()
                .value("test", "replaced-key")
                .build();

        // setup
        JsonMapper   mapper   =
            JsonMapper
                .builder()
                .srcPath("json-mapper/map-attributes.ged")
                .locale(Locale.ENGLISH)
                .templateFileCache(templateFileCache)
                .translationProvider(translationProvider)
                .flatMappableDissector(flatMappableDissector)
                .tokenMap(tokenMap)
                .build();

        MapperResult result;

        // run
        result = mapper.map();

        // verify
        Container container = result.getContainers().get(0);
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
                .srcPath("json-mapper/replace-parameter.ged")
                .locale(Locale.ENGLISH)
                .templateFileCache(templateFileCache)
                .translationProvider(translationProvider)
                .flatMappableDissector(flatMappableDissector)
                .tokenMap(tokenMap)
                .build();

        MapperResult result;

        // run
        result = mapper.map();

        // verify
        assertEquals("testText", ((TextContainer) result.getContainers().get(0)).getText());
    }

    @Test
    void test_map_AudioContainer()
    {
        // setup
        JsonMapper   mapper =
            JsonMapper
                .builder()
                .srcPath("json-mapper/container/audio-container.ged")
                .locale(Locale.ENGLISH)
                .templateFileCache(templateFileCache)
                .translationProvider(translationProvider)
                .flatMappableDissector(flatMappableDissector)
                .build();

        MapperResult result;

        // run
        result = mapper.map();

        // verify
        Container container = result.getContainers().get(0);
        assertEquals(AudioContainer.class, container.getClass());
        assertEquals("uid", container.getUid());
        assertEquals(1, container.getClasses().size());
        assertEquals(1, container.getDataAttributes().size());

        AudioContainer audioContainer = (AudioContainer) container;
        assertEquals("path/to/resource", audioContainer.getSrc());
        assertEquals(false, audioContainer.isControls());
        assertEquals(true, audioContainer.isAutoplay());

        String html = HtmlMapper.map(container);
        assertTrue(checkDivTags(html));
        checkToolTip(html);
    }

    @Test
    void test_map_Button()
    {
        // setup
        JsonMapper   mapper =
            JsonMapper
                .builder()
                .srcPath("json-mapper/container/button-container.ged")
                .locale(Locale.ENGLISH)
                .templateFileCache(templateFileCache)
                .translationProvider(translationProvider)
                .flatMappableDissector(flatMappableDissector)
                .build();

        MapperResult result;

        // run
        result = mapper.map();

        // verify
        Container container = result.getContainers().get(0);
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
        checkToolTip(html);
    }

    @Test
    void test_map_ButtonIconContainer()
    {
        // setup
        JsonMapper   mapper =
            JsonMapper
                .builder()
                .srcPath("json-mapper/container/button-icon-container.ged")
                .locale(Locale.ENGLISH)
                .templateFileCache(templateFileCache)
                .translationProvider(translationProvider)
                .flatMappableDissector(flatMappableDissector)
                .build();

        MapperResult result;

        // run
        result = mapper.map();

        // verify
        Container container = result.getContainers().get(0);
        assertEquals(ButtonIcon.class, container.getClass());

        ButtonIcon button = (ButtonIcon) container;
        assertEquals("uid", button.getUid());
        assertEquals(1, button.getClasses().size());
        assertEquals(1, button.getDataAttributes().size());
        assertEquals("/images/btn-icon.png", button.getIcon());
        assertEquals("doShit()", button.getOnClick());
        assertEquals(ButtonClass.SUCCESS, button.getBtnClass());

        String html = HtmlMapper.map(container);
        assertTrue(checkDivTags(html));
        checkToolTip(html);
    }

    @Test
    void test_map_ContentContainer()
    {
        // setup
        JsonMapper   mapper =
            JsonMapper
                .builder()
                .srcPath("json-mapper/container/content-container.ged")
                .locale(Locale.ENGLISH)
                .templateFileCache(templateFileCache)
                .translationProvider(translationProvider)
                .flatMappableDissector(flatMappableDissector)
                .build();

        MapperResult result;

        // run
        result = mapper.map();

        // verify
        Container container = result.getContainers().get(0);
        assertEquals(ContentContainer.class, container.getClass());
        assertEquals("uid", container.getUid());
        assertEquals(1, container.getClasses().size());
        assertEquals(1, container.getDataAttributes().size());

        String html = HtmlMapper.map(container);
        assertTrue(checkDivTags(html));
        checkToolTip(html);
    }

    @Test
    void test_map_FormContainer()
    {
        // setup
        JsonMapper   mapper =
            JsonMapper
                .builder()
                .srcPath("json-mapper/container/form-container.ged")
                .locale(Locale.ENGLISH)
                .templateFileCache(templateFileCache)
                .translationProvider(translationProvider)
                .flatMappableDissector(flatMappableDissector)
                .build();

        MapperResult result;

        // run
        result = mapper.map();

        // verify
        Container container = result.getContainers().get(0);
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
        JsonMapper   mapper =
            JsonMapper
                .builder()
                .srcPath("json-mapper/container/hidden-container.ged")
                .locale(Locale.ENGLISH)
                .templateFileCache(templateFileCache)
                .translationProvider(translationProvider)
                .flatMappableDissector(flatMappableDissector)
                .build();

        MapperResult result;

        // run
        result = mapper.map();

        // verify
        Container container = result.getContainers().get(0);
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
        JsonMapper   mapper =
            JsonMapper
                .builder()
                .srcPath("json-mapper/container/image-container.ged")
                .locale(Locale.ENGLISH)
                .templateFileCache(templateFileCache)
                .translationProvider(translationProvider)
                .flatMappableDissector(flatMappableDissector)
                .build();

        MapperResult result;

        // run
        result = mapper.map();

        // verify
        Container container = result.getContainers().get(0);
        assertEquals(ImageContainer.class, container.getClass());
        assertEquals("uid", container.getUid());
        assertEquals(1, container.getClasses().size());
        assertEquals(1, container.getDataAttributes().size());

        ImageContainer imageContainer = (ImageContainer) container;
        assertEquals("path/to/resource", imageContainer.getSrc());

        String html = HtmlMapper.map(container);
        assertFalse(checkTags("img", html));
        checkToolTip(html);
    }

    @Test
    void test_map_LinkContainer()
    {
        // setup
        String    srcPath = "json-mapper/container/link-container.ged";
        Container container;

        // run
        container = mapContainer(srcPath);

        // verify
        LinkContainer link = (LinkContainer) container;
        assertEquals("_blank", link.getTarget());
        assertEquals(TextContainer.class, link.getContent().getClass());
        assertEquals("link label", ((TextContainer) link.getContent()).getText());
    }

    @Test
    void test_map_SplittedContainer()
    {
        // setup
        JsonMapper   mapper =
            JsonMapper
                .builder()
                .srcPath("json-mapper/container/splitted-container.ged")
                .locale(Locale.ENGLISH)
                .templateFileCache(templateFileCache)
                .translationProvider(translationProvider)
                .flatMappableDissector(flatMappableDissector)
                .build();

        MapperResult result;

        // run
        result = mapper.map();

        // verify
        Container container = result.getContainers().get(0);
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
                .srcPath("json-mapper/container/tabbed-container.ged")
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
    void test_map_TableContainer()
    {
        // setup
        JsonMapper   mapper =
            JsonMapper
                .builder()
                .srcPath("json-mapper/container/table-container.ged")
                .locale(Locale.ENGLISH)
                .templateFileCache(templateFileCache)
                .translationProvider(translationProvider)
                .flatMappableDissector(flatMappableDissector)
                .build();

        MapperResult result;

        // run
        result = mapper.map();

        // verify
        Container container = result.getContainers().get(0);
        assertEquals(TableContainer.class, container.getClass());
        assertEquals("uid", container.getUid());
        assertEquals(1, container.getClasses().size());
        assertEquals(1, container.getDataAttributes().size());

        TableContainer tableContainer = (TableContainer) container;
        assertEquals("col1", tableContainer.getColumnNames().get(0));
        assertEquals("col2", tableContainer.getColumnNames().get(1));
        assertEquals("col3", tableContainer.getColumnNames().get(2));

        String html = HtmlMapper.map(container);
        assertTrue(checkDivTags(html));
    }

    @Test
    void test_map_TextContainer()
    {
        // setup
        JsonMapper   mapper =
            JsonMapper
                .builder()
                .srcPath("json-mapper/container/text-container.ged")
                .locale(Locale.ENGLISH)
                .templateFileCache(templateFileCache)
                .translationProvider(translationProvider)
                .flatMappableDissector(flatMappableDissector)
                .build();

        MapperResult result;

        // run
        result = mapper.map();

        // verify
        Container container = result.getContainers().get(0);
        assertEquals(TextContainer.class, container.getClass());
        assertEquals("uid", container.getUid());
        assertEquals(1, container.getClasses().size());
        assertEquals(1, container.getDataAttributes().size());

        TextContainer textContainer = (TextContainer) container;
        assertEquals("some text", textContainer.getText());

        String html = HtmlMapper.map(container);
        assertTrue(checkDivTags(html));
        checkToolTip(html);
    }

    @Test
    void test_map_CheckboxInput()
    {
        // setup
        JsonMapper   mapper =
            JsonMapper
                .builder()
                .srcPath("json-mapper/input/checkbox-input.ged")
                .locale(Locale.ENGLISH)
                .templateFileCache(templateFileCache)
                .translationProvider(translationProvider)
                .flatMappableDissector(flatMappableDissector)
                .build();

        MapperResult result;

        // run
        result = mapper.map();

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
    void test_map_DateInput()
    {
        // setup
        JsonMapper   mapper =
            JsonMapper
                .builder()
                .srcPath("json-mapper/input/date-input.ged")
                .locale(Locale.ENGLISH)
                .templateFileCache(templateFileCache)
                .translationProvider(translationProvider)
                .flatMappableDissector(flatMappableDissector)
                .build();

        MapperResult result;

        // run
        result = mapper.map();

        // verify
        Container     container = result.getContainers().get(0);
        FormContainer form      = (FormContainer) container;
        Input         input     = form.getInputs().get(0);
        assertEquals(Date.class, input.getClass());

        Date date = (Date) input;
        assertEquals("name", date.getName());
        assertEquals("date", date.getSubmitAs());
        assertEquals("2026-05-01", date.getValue());

        String html = HtmlMapper.map(container);
        assertTrue(checkDivTags(html));
    }

    @Test
    void test_map_FileInput()
    {
        // setup
        JsonMapper   mapper =
            JsonMapper
                .builder()
                .srcPath("json-mapper/input/file-input.ged")
                .locale(Locale.ENGLISH)
                .templateFileCache(templateFileCache)
                .translationProvider(translationProvider)
                .flatMappableDissector(flatMappableDissector)
                .build();

        MapperResult result;

        // run
        result = mapper.map();

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
        JsonMapper   mapper =
            JsonMapper
                .builder()
                .srcPath("json-mapper/input/hidden-input.ged")
                .locale(Locale.ENGLISH)
                .templateFileCache(templateFileCache)
                .translationProvider(translationProvider)
                .flatMappableDissector(flatMappableDissector)
                .build();

        MapperResult result;

        // run
        result = mapper.map();

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
    void test_map_NumberInput()
    {
        // setup
        JsonMapper   mapper =
            JsonMapper
                .builder()
                .srcPath("json-mapper/input/number-input.ged")
                .locale(Locale.ENGLISH)
                .templateFileCache(templateFileCache)
                .translationProvider(translationProvider)
                .flatMappableDissector(flatMappableDissector)
                .build();

        MapperResult result;

        // run
        result = mapper.map();

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
    void test_map_NumberInputInteger()
    {
        // setup
        JsonMapper   mapper =
            JsonMapper
                .builder()
                .srcPath("json-mapper/input/number-input-integer.ged")
                .locale(Locale.ENGLISH)
                .templateFileCache(templateFileCache)
                .translationProvider(translationProvider)
                .flatMappableDissector(flatMappableDissector)
                .build();

        MapperResult result;

        // run
        result = mapper.map();

        // verify
        Container     container = result.getContainers().get(0);
        FormContainer form      = (FormContainer) container;
        Input         input     = form.getInputs().get(0);
        assertEquals(Number.class, input.getClass());

        Number number = (Number) input;
        assertEquals("name", number.getName());
        assertEquals("number", number.getSubmitAs());
        assertEquals("12", String.valueOf(number.getValue()));
        assertEquals("0", String.valueOf(number.getMin()));
        assertEquals("15", String.valueOf(number.getMax()));
        assertEquals("2", String.valueOf(number.getStep()));

        String html = HtmlMapper.map(container);
        assertTrue(checkDivTags(html));
    }

    @Test
    void test_map_NumberInputDouble()
    {
        // setup
        JsonMapper   mapper =
            JsonMapper
                .builder()
                .srcPath("json-mapper/input/number-input-double.ged")
                .locale(Locale.ENGLISH)
                .templateFileCache(templateFileCache)
                .translationProvider(translationProvider)
                .flatMappableDissector(flatMappableDissector)
                .build();

        MapperResult result;

        // run
        result = mapper.map();

        // verify
        Container     container = result.getContainers().get(0);
        FormContainer form      = (FormContainer) container;
        Input         input     = form.getInputs().get(0);
        assertEquals(Number.class, input.getClass());

        Number number = (Number) input;
        assertEquals("name", number.getName());
        assertEquals("number", number.getSubmitAs());
        assertEquals("12.0", String.valueOf(number.getValue()));
        assertEquals("0.0", String.valueOf(number.getMin()));
        assertEquals("15.0", String.valueOf(number.getMax()));
        assertEquals("2.0", String.valueOf(number.getStep()));

        String html = HtmlMapper.map(container);
        assertTrue(checkDivTags(html));
    }

    @Test
    void test_map_PasswordInput()
    {
        // setup
        JsonMapper   mapper =
            JsonMapper
                .builder()
                .srcPath("json-mapper/input/password-input.ged")
                .locale(Locale.ENGLISH)
                .templateFileCache(templateFileCache)
                .translationProvider(translationProvider)
                .flatMappableDissector(flatMappableDissector)
                .build();

        MapperResult result;

        // run
        result = mapper.map();

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
        JsonMapper   mapper =
            JsonMapper
                .builder()
                .srcPath("json-mapper/input/radio-input.ged")
                .locale(Locale.ENGLISH)
                .templateFileCache(templateFileCache)
                .translationProvider(translationProvider)
                .flatMappableDissector(flatMappableDissector)
                .build();

        MapperResult result;

        // run
        result = mapper.map();

        // verify
        Container     container = result.getContainers().get(0);
        FormContainer form      = (FormContainer) container;
        Input         input     = form.getInputs().get(0);
        assertEquals(Radio.class, input.getClass());

        Radio radio = (Radio) input;
        assertEquals("name", radio.getName());
        assertEquals("radio", radio.getSubmitAs());
        assertEquals(2, radio.getBoxes().size());

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
                .uid("uid")
                .text("text")
                .value("value")
                .build());

        TokenMap     map    =
            TokenMap
                .builder()
                .value("list", list)
                .build();

        JsonMapper   mapper =
            JsonMapper
                .builder()
                .srcPath("json-mapper/input/select-input.ged")
                .locale(Locale.ENGLISH)
                .templateFileCache(templateFileCache)
                .translationProvider(translationProvider)
                .flatMappableDissector(flatMappableDissector)
                .tokenMap(map)
                .build();

        MapperResult result;

        // run
        result = mapper.map();

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
        JsonMapper   mapper =
            JsonMapper
                .builder()
                .srcPath("json-mapper/input/slider-input.ged")
                .locale(Locale.ENGLISH)
                .templateFileCache(templateFileCache)
                .translationProvider(translationProvider)
                .flatMappableDissector(flatMappableDissector)
                .build();

        MapperResult result;

        // run
        result = mapper.map();

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
    void test_map_SliderInput_customValues()
    {
        // run
        Slider slider = (Slider) mapInput("json-mapper/input/slider-custom-input.ged");

        // verify
        assertTrue(slider.isCustom());
        assertEquals(List.of("off", "low", "medium", "high"), slider.getCustomValues());
        assertEquals("medium", slider.getValue());
        assertNull(slider.getMin());
        assertNull(slider.getMax());
        assertNull(slider.getStep());
        assertNull(slider.getMarks());
    }

    @Test
    void test_map_SliderInput_customValuesWithoutValue_selectsFirst()
    {
        // run
        Slider slider = (Slider) mapInput("json-mapper/input/slider-custom-default-input.ged");

        // verify
        assertEquals("off", slider.getValue());
    }

    @Test
    void test_map_SliderInput_customValuesWithNumericParameter_fails()
    {
        // run
        Input input = mapInput("json-mapper/input/slider-custom-conflict-input.ged");

        // verify
        assertEquals(Textbox.class, input.getClass());
    }

    @Test
    void test_map_SliderInput_customValuesWithUnknownValue_fails()
    {
        // run
        Input input = mapInput("json-mapper/input/slider-custom-unknown-value-input.ged");

        // verify
        assertEquals(Textbox.class, input.getClass());
    }

    @Test
    void test_map_SliderInput_marks()
    {
        // run
        Slider slider = (Slider) mapInput("json-mapper/input/slider-marks-input.ged");

        // verify
        assertFalse(slider.isCustom());
        assertEquals(5, slider.getMarks());
    }

    @Test
    void test_map_SliderInput_tooFewMarks_fails()
    {
        // run
        Input input = mapInput("json-mapper/input/slider-marks-too-few-input.ged");

        // verify
        assertEquals(Textbox.class, input.getClass());
    }

    @Test
    void test_map_SwitchInput()
    {
        // setup
        JsonMapper   mapper =
            JsonMapper
                .builder()
                .srcPath("json-mapper/input/switch-input.ged")
                .locale(Locale.ENGLISH)
                .templateFileCache(templateFileCache)
                .translationProvider(translationProvider)
                .flatMappableDissector(flatMappableDissector)
                .build();

        MapperResult result;

        // run
        result = mapper.map();

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
    void test_map_TagInput()
    {
        // setup
        JsonMapper   mapper =
            JsonMapper
                .builder()
                .srcPath("json-mapper/input/tag-input.ged")
                .locale(Locale.ENGLISH)
                .templateFileCache(templateFileCache)
                .translationProvider(translationProvider)
                .flatMappableDissector(flatMappableDissector)
                .build();

        MapperResult result;

        // run
        result = mapper.map();

        // verify
        Container     container = result.getContainers().get(0);
        FormContainer form      = (FormContainer) container;
        Input         input     = form.getInputs().get(0);
        assertEquals(Tag.class, input.getClass());

        Tag tag = (Tag) input;
        assertEquals("name", tag.getName());
        assertEquals("tag", tag.getSubmitAs());
        assertEquals("#?[a-zA-Z]{1}[a-zA-Z0-9\\-]{2,*}", tag.getPattern());
        assertEquals(10, tag.getSettings().getMaxTags());

        String html = HtmlMapper.map(container);
        assertTrue(checkDivTags(html));
    }

    @Test
    void test_map_TextareaInput()
    {
        // setup
        JsonMapper   mapper =
            JsonMapper
                .builder()
                .srcPath("json-mapper/input/textarea-input.ged")
                .locale(Locale.ENGLISH)
                .templateFileCache(templateFileCache)
                .translationProvider(translationProvider)
                .flatMappableDissector(flatMappableDissector)
                .build();

        MapperResult result;

        // run
        result = mapper.map();

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
        JsonMapper   mapper =
            JsonMapper
                .builder()
                .srcPath("json-mapper/input/textbox-input.ged")
                .locale(Locale.ENGLISH)
                .templateFileCache(templateFileCache)
                .translationProvider(translationProvider)
                .flatMappableDissector(flatMappableDissector)
                .build();

        MapperResult result;

        // run
        result = mapper.map();

        // verify
        Container     container = result.getContainers().get(0);
        FormContainer form      = (FormContainer) container;
        Input         input     = form.getInputs().get(0);
        assertEquals(Textbox.class, input.getClass());

        Textbox textbox = (Textbox) input;
        assertEquals("name", textbox.getName());
        assertEquals("text", textbox.getText());

        String html = HtmlMapper.map(container);
        assertTrue(checkDivTags(html));
    }

    @Test
    void test_map_TextfieldInput()
    {
        // setup
        JsonMapper   mapper =
            JsonMapper
                .builder()
                .srcPath("json-mapper/input/textfield-input.ged")
                .locale(Locale.ENGLISH)
                .templateFileCache(templateFileCache)
                .translationProvider(translationProvider)
                .flatMappableDissector(flatMappableDissector)
                .build();

        MapperResult result;

        // run
        result = mapper.map();

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

    @Test
    void test_map_CheckboxInput_boxesKeepTheirSubmitAs()
    {
        // setup
        JsonMapper   mapper =
            JsonMapper
                .builder()
                .srcPath("json-mapper/input/checkbox-input.ged")
                .locale(Locale.ENGLISH)
                .templateFileCache(templateFileCache)
                .translationProvider(translationProvider)
                .flatMappableDissector(flatMappableDissector)
                .build();

        MapperResult result;

        // run
        result = mapper.map();

        // verify
        FormContainer form     = (FormContainer) result.getContainers().get(0);
        Checkbox      checkbox = (Checkbox) form.getInputs().get(0);

        assertEquals("checkbox-2", checkbox.getBoxes().get(0).getSubmitAs());
        assertEquals("checkbox-2", checkbox.getBoxes().get(1).getSubmitAs());
    }

    @Test
    void test_map_ContentContainer_direction()
    {
        // setup
        String    srcPath = "json-mapper/container/content-container-direction.ged";
        Container container;

        // run
        container = mapContainer(srcPath);

        // verify
        assertEquals(Direction.VERTICAL_REVERSE, ((ContentContainer) container).getDirection());
    }

    @Test
    void test_map_SplittedContainer_direction()
    {
        // setup
        String    srcPath = "json-mapper/container/splitted-container-direction.ged";
        Container container;

        // run
        container = mapContainer(srcPath);

        // verify
        assertEquals(Direction.HORIZONTAL_REVERSE, ((SplittedContainer) container).getDirection());
    }

    @Test
    void test_map_BurgerContainer_iconButtonClassAndItemIconSize()
    {
        // setup
        String    srcPath = "json-mapper/container/burger-container-options.ged";
        Container container;

        // run
        container = mapContainer(srcPath);

        // verify
        BurgerContainer burger = (BurgerContainer) container;
        assertEquals("bi-list", burger.getIcon());
        assertEquals(ButtonClass.DANGER, burger.getBtnClass());
        assertEquals(2, burger.getItems().size());
        assertEquals(3, burger.getItems().get(0).getIconSize());
        assertEquals(0, burger.getItems().get(1).getIconSize());
    }

    @Test
    void test_map_BurgerContainer_defaultIcon()
    {
        // setup
        String    srcPath = "json-mapper/container/burger-container-default.ged";
        Container container;

        // run
        container = mapContainer(srcPath);

        // verify
        assertEquals("bi-list", ((BurgerContainer) container).getIcon());
    }

    @Test
    void test_map_BurgerContainer_nonPositiveIconSize_isIgnored()
    {
        // setup
        String    srcPath = "json-mapper/container/burger-container-icon-size-invalid.ged";
        Container container;

        // run
        container = mapContainer(srcPath);

        // verify
        assertEquals(0, ((BurgerContainer) container).getItems().get(0).getIconSize());
    }

    @Test
    void test_map_ButtonIconContainer_alt()
    {
        // setup
        String    srcPath = "json-mapper/container/button-icon-alt-container.ged";
        Container container;

        // run
        container = mapContainer(srcPath);

        // verify
        assertEquals("close dialog", ((ButtonIcon) container).getAlt());
    }

    @Test
    void test_map_TextfieldInput_onKeydown()
    {
        // setup
        String srcPath = "json-mapper/input/textfield-keydown-input.ged";
        Input  input;

        // run
        input = mapInput(srcPath);

        // verify
        assertEquals("onKey()", ((Textfield) input).getOnKeydown());
    }

    @Test
    void test_map_TextareaInput_onKeydownRowsAndCols()
    {
        // setup
        String srcPath = "json-mapper/input/textarea-sized-input.ged";
        Input  input;

        // run
        input = mapInput(srcPath);

        // verify
        Textarea textarea = (Textarea) input;
        assertEquals("onKey()", textarea.getOnKeydown());
        assertEquals(8, textarea.getRows());
        assertEquals(40, textarea.getCols());
    }

    @Test
    void test_map_TextareaInput_nonPositiveRowsAndCols_fallBack()
    {
        // setup
        String srcPath = "json-mapper/input/textarea-rows-invalid-input.ged";
        Input  input;

        // run
        input = mapInput(srcPath);

        // verify
        Textarea textarea = (Textarea) input;
        assertEquals(5, textarea.getRows());
        assertNull(textarea.getCols());
    }

    @Test
    void test_map_DateInput_minMaxAndStep()
    {
        // setup
        String srcPath = "json-mapper/input/date-step-input.ged";
        Input  input;

        // run
        input = mapInput(srcPath);

        // verify
        Date date = (Date) input;
        assertEquals("2026-01-01", date.getMin());
        assertEquals("2026-12-31", date.getMax());
        assertEquals(7, date.getStep());
    }

    @Test
    void test_map_DateInput_nonPositiveStep_fallsBackToOne()
    {
        // setup
        String srcPath = "json-mapper/input/date-step-invalid-input.ged";
        Input  input;

        // run
        input = mapInput(srcPath);

        // verify
        assertEquals(1, ((Date) input).getStep());
    }

    private Container mapContainer(String srcPath)
    {
        JsonMapper mapper =
            JsonMapper
                .builder()
                .srcPath(srcPath)
                .locale(Locale.ENGLISH)
                .templateFileCache(templateFileCache)
                .translationProvider(translationProvider)
                .flatMappableDissector(flatMappableDissector)
                .build();

        return mapper.map().getContainers().get(0);
    }

    private Input mapInput(String srcPath)
    {
        JsonMapper mapper =
            JsonMapper
                .builder()
                .srcPath(srcPath)
                .locale(Locale.ENGLISH)
                .templateFileCache(templateFileCache)
                .translationProvider(translationProvider)
                .flatMappableDissector(flatMappableDissector)
                .build();

        return ((FormContainer) mapper.map().getContainers().get(0)).getInputs().get(0);
    }

    @Test
    void test_map_AudioContainer_allParameters()
    {
        // setup
        String    srcPath = "json-mapper/container/audio-container-all.ged";
        Container container;

        // run
        container = mapContainer(srcPath);

        // verify
        AudioContainer audio = (AudioContainer) container;
        assertEquals("uid", audio.getUid());
        assertEquals(Set.of("class1"), audio.getClasses());
        assertEquals(Map.of("key1", "value1"), audio.getDataAttributes());
        assertEquals("tip", audio.getTooltip());
        assertEquals("path/to/resource", audio.getSrc());
        assertEquals("audio caption", audio.getCaption());
        assertTrue(audio.isControls());
        assertTrue(audio.isAutoplay());
        assertTrue(audio.isLooped());
        assertTrue(audio.isMuted());
        assertEquals(AudioContainer.Preload.METADATA, audio.getPreload());
    }

    @Test
    void test_map_AudioContainer_defaults()
    {
        // setup
        String    srcPath = "json-mapper/container/audio-container-minimal.ged";
        Container container;

        // run
        container = mapContainer(srcPath);

        // verify
        AudioContainer audio = (AudioContainer) container;
        assertFalse(audio.isAutoplay());
        assertFalse(audio.isLooped());
        assertFalse(audio.isMuted());
        assertEquals(AudioContainer.Preload.AUTO, audio.getPreload());
    }

    @Test
    void test_map_BurgerContainer_itemTextUrlAndMethod()
    {
        // setup
        String    srcPath = "json-mapper/container/burger-container-item-method.ged";
        Container container;

        // run
        container = mapContainer(srcPath);

        // verify
        BurgerContainer burger = (BurgerContainer) container;
        assertEquals(3, burger.getItems().size());
        assertEquals("first", burger.getItems().get(0).getText());
        assertEquals("/first", burger.getItems().get(0).getUrl());
        assertEquals("bi-x", burger.getItems().get(0).getIcon());
        assertEquals("GET", burger.getItems().get(0).getMethod());
        assertEquals("POST", burger.getItems().get(1).getMethod());
        assertEquals("POST", burger.getItems().get(2).getMethod());
    }

    @Test
    void test_map_ContentContainer_children()
    {
        // setup
        String    srcPath = "json-mapper/container/content-container-children.ged";
        Container container;

        // run
        container = mapContainer(srcPath);

        // verify
        ContentContainer content = (ContentContainer) container;
        assertEquals(2, content.getContents().size());
        assertEquals("first text", ((TextContainer) content.getContents().get(0)).getText());
        assertEquals("second text", ((TextContainer) content.getContents().get(1)).getText());
    }

    @Test
    void test_map_HiddenContainer_children()
    {
        // setup
        String    srcPath = "json-mapper/container/hidden-container-children.ged";
        Container container;

        // run
        container = mapContainer(srcPath);

        // verify
        HiddenContainer hidden = (HiddenContainer) container;
        assertEquals(2, hidden.getChildren().size());
        assertEquals("first text", ((TextContainer) hidden.getChildren().get(0)).getText());
        assertEquals("second text", ((TextContainer) hidden.getChildren().get(1)).getText());
    }

    @Test
    void test_map_FormContainer_direction()
    {
        // setup
        String    srcPath = "json-mapper/container/form-container-direction.ged";
        Container container;

        // run
        container = mapContainer(srcPath);

        // verify
        assertEquals(Direction.HORIZONTAL, ((FormContainer) container).getDirection());
    }

    @Test
    void test_map_FormContainer_connectedBtn()
    {
        // setup
        String    srcPath = "json-mapper/container/form-container-direction.ged";
        Container container;

        // run
        container = mapContainer(srcPath);

        // verify
        assertEquals("btn-1", container.getDataAttributes().get("connected-btn"));
    }

    @Test
    void test_map_TextContainer_inline()
    {
        // setup
        String    srcPath = "json-mapper/container/text-container-inline.ged";
        Container container;

        // run
        container = mapContainer(srcPath);

        // verify
        assertTrue(((TextContainer) container).isInline());
    }

    @Test
    void test_map_HeadingContainer_allParameters()
    {
        // setup
        String    srcPath = "json-mapper/container/heading-container.ged";
        Container container;

        // run
        container = mapContainer(srcPath);

        // verify
        HeadingContainer heading = (HeadingContainer) container;
        assertEquals("uid", heading.getUid());
        assertEquals(Set.of("class1"), heading.getClasses());
        assertEquals(Map.of("key1", "value1"), heading.getDataAttributes());
        assertEquals("tip", heading.getTooltip());
        assertEquals("some heading", heading.getText());
        assertEquals(3, heading.getSize());
    }

    @Test
    void test_map_HeadingContainer_sizeAboveSix_isClamped()
    {
        // setup
        String    srcPath = "json-mapper/container/heading-container-size-clamped.ged";
        Container container;

        // run
        container = mapContainer(srcPath);

        // verify
        assertEquals(6, ((HeadingContainer) container).getSize());
    }

    @Test
    void test_map_EmptyContainer()
    {
        // setup
        String    srcPath = "json-mapper/container/empty-container.ged";
        Container container;

        // run
        container = mapContainer(srcPath);

        // verify
        assertEquals(EmptyContainer.class, container.getClass());
    }

    @Test
    void test_map_ModalContainer_allParameters()
    {
        // setup
        String    srcPath = "json-mapper/container/modal-container-all.ged";
        Container container;

        // run
        container = mapContainer(srcPath);

        // verify
        ModalContainer modal = (ModalContainer) container;
        assertEquals("uid", modal.getUid());
        assertEquals(Set.of("class1"), modal.getClasses());
        assertEquals(Map.of("key1", "value1"), modal.getDataAttributes());
        assertEquals("modal name", modal.getName());
        assertEquals("/modal/request", modal.getRequestUrl());
        assertTrue(modal.isCloseOnOverlayClick());
        assertEquals("left", modal.getBtnNameLeft());
        assertEquals(ButtonClass.DANGER, modal.getBtnClassLeft());
        assertEquals("leftFn()", modal.getBtnFunctionLeft());
        assertEquals("center", modal.getBtnNameCenter());
        assertEquals(ButtonClass.SUCCESS, modal.getBtnClassCenter());
        assertEquals("centerFn()", modal.getBtnFunctionCenter());
        assertEquals("right", modal.getBtnNameRight());
        assertEquals(ButtonClass.WARNING, modal.getBtnClassRight());
        assertEquals("rightFn()", modal.getBtnFunctionRight());
        assertEquals(1, modal.getContents().size());
        assertEquals(1, ((ContentContainer) modal.getContents().get(0)).getContents().size());
    }

    @Test
    void test_map_ModalContainer_defaults()
    {
        // setup
        String    srcPath = "json-mapper/container/modal-container-minimal.ged";
        Container container;

        // run
        container = mapContainer(srcPath);

        // verify
        ModalContainer modal = (ModalContainer) container;
        assertEquals("", modal.getRequestUrl());
        assertFalse(modal.isCloseOnOverlayClick());
        assertEquals("cancel", modal.getBtnNameLeft());
        assertEquals(ButtonClass.DEFAULT, modal.getBtnClassLeft());
        assertEquals(new CloseModal().parseAsJS(), modal.getBtnFunctionLeft());
        assertEquals("", modal.getBtnNameCenter());
        assertEquals(ButtonClass.DEFAULT, modal.getBtnClassCenter());
        assertEquals("", modal.getBtnFunctionCenter());
        assertEquals("submit", modal.getBtnNameRight());
        assertEquals(ButtonClass.DEFAULT, modal.getBtnClassRight());
        assertEquals(new SubmitFromModal().parseAsJS(), modal.getBtnFunctionRight());
    }

    @Test
    void test_map_CheckboxInput_allParameters()
    {
        // setup
        String srcPath = "json-mapper/input/checkbox-all-input.ged";
        Input  input;

        // run
        input = mapInput(srcPath);

        // verify
        Checkbox checkbox = (Checkbox) input;
        assertSubmittableBase(checkbox);
        assertEquals(2, checkbox.getBoxes().size());
        assertEquals("box-1", checkbox.getBoxes().get(0).getUid());
        assertEquals("sub-1", checkbox.getBoxes().get(0).getSubmitAs());
        assertEquals("first", checkbox.getBoxes().get(0).getText());
        assertEquals("1", checkbox.getBoxes().get(0).getValue());
        assertTrue(checkbox.getBoxes().get(0).isChecked());
        assertEquals("second", checkbox.getBoxes().get(1).getText());
        assertFalse(checkbox.getBoxes().get(1).isChecked());
    }

    @Test
    void test_map_DateInput_allParameters()
    {
        // setup
        String srcPath = "json-mapper/input/date-all-input.ged";
        Input  input;

        // run
        input = mapInput(srcPath);

        // verify
        Date date = (Date) input;
        assertSubmittableBase(date);
        assertEquals("2026-05-01", date.getValue());
        assertEquals("2026-01-01", date.getMin());
        assertEquals("2026-12-31", date.getMax());
        assertEquals(7, date.getStep());
    }

    @Test
    void test_map_FileInput_allParameters()
    {
        // setup
        String srcPath = "json-mapper/input/file-all-input.ged";
        Input  input;

        // run
        input = mapInput(srcPath);

        // verify
        File file = (File) input;
        assertSubmittableBase(file);
        assertTrue(file.getMultiple());
        assertEquals(List.of(".png", ".pdf"), file.getAccepts());
    }

    @Test
    void test_map_HiddenInput_allParameters()
    {
        // setup
        String srcPath = "json-mapper/input/hidden-all-input.ged";
        Input  input;

        // run
        input = mapInput(srcPath);

        // verify
        Hidden hidden = (Hidden) input;
        assertEquals("uid", hidden.getUid());
        assertEquals(Set.of("class1", "class2"), hidden.getClasses());
        assertEquals(Map.of("key1", "value1"), hidden.getDataAttributes());
        assertEquals("submit", hidden.getSubmitAs());
        assertEquals("secret", hidden.getValue());
        assertTrue(hidden.isDisabled());
        assertTrue(hidden.isMandatory());
    }

    @Test
    void test_map_NumberInput_allParameters()
    {
        // setup
        String srcPath = "json-mapper/input/number-all-input.ged";
        Input  input;

        // run
        input = mapInput(srcPath);

        // verify
        Number number = (Number) input;
        assertSubmittableBase(number);
        assertEquals("5", String.valueOf(number.getValue()));
        assertEquals("1", String.valueOf(number.getMin()));
        assertEquals("10", String.valueOf(number.getMax()));
        assertEquals("2", String.valueOf(number.getStep()));
        assertEquals("ph", number.getPlaceholder());
        assertEquals("enter()", number.getOnEnterPress());
    }

    @Test
    void test_map_PasswordInput_allParameters()
    {
        // setup
        String srcPath = "json-mapper/input/password-all-input.ged";
        Input  input;

        // run
        input = mapInput(srcPath);

        // verify
        Password password = (Password) input;
        assertSubmittableBase(password);
        assertEquals("pw", password.getPlaceholder());
        assertEquals("enter()", password.getOnEnterPress());
    }

    @Test
    void test_map_RadioInput_allParameters()
    {
        // setup
        String srcPath = "json-mapper/input/radio-all-input.ged";
        Input  input;

        // run
        input = mapInput(srcPath);

        // verify
        Radio radio = (Radio) input;
        assertSubmittableBase(radio);
        assertEquals(2, radio.getBoxes().size());
        assertEquals("box-1", radio.getBoxes().get(0).getUid());
        assertEquals("first", radio.getBoxes().get(0).getText());
        assertEquals("1", radio.getBoxes().get(0).getValue());
        assertTrue(radio.getBoxes().get(0).isChecked());
        assertEquals("second", radio.getBoxes().get(1).getText());
        assertFalse(radio.getBoxes().get(1).isChecked());
    }

    @Test
    void test_map_SelectInput_allParameters()
    {
        // setup
        String srcPath = "json-mapper/input/select-all-input.ged";
        Input  input;

        // run
        input = mapInput(srcPath);

        // verify
        Select select = (Select) input;
        assertSubmittableBase(select);
        assertEquals("2", select.getSelected());
        assertEquals(2, select.getValues().size());
        assertEquals("first", select.getValues().get(0).getText());
        assertEquals("2", select.getValues().get(1).getValue());
    }

    @Test
    void test_map_SliderInput_allParameters()
    {
        // setup
        String srcPath = "json-mapper/input/slider-all-input.ged";
        Input  input;

        // run
        input = mapInput(srcPath);

        // verify
        Slider slider = (Slider) input;
        assertSubmittableBase(slider);
        assertEquals("5", slider.getValue());
        assertEquals("0", String.valueOf(slider.getMin()));
        assertEquals("10", String.valueOf(slider.getMax()));
        assertEquals("1", String.valueOf(slider.getStep()));
    }

    @Test
    void test_map_SwitchInput_allParameters()
    {
        // setup
        String srcPath = "json-mapper/input/switch-all-input.ged";
        Input  input;

        // run
        input = mapInput(srcPath);

        // verify
        Switch switchInput = (Switch) input;
        assertSubmittableBase(switchInput);
        assertTrue(switchInput.getChecked());
    }

    @Test
    void test_map_TagInput_allParameters()
    {
        // setup
        String srcPath = "json-mapper/input/tag-all-input.ged";
        Input  input;

        // run
        input = mapInput(srcPath);

        // verify
        Tag tag = (Tag) input;
        assertSubmittableBase(tag);
        assertEquals(List.of("one", "two"), tag.getTags());
        assertEquals("[a-z]+", tag.getPattern());
        assertEquals("uid", tag.getSettings().getId());
        assertEquals(3, tag.getSettings().getMaxTags());
        assertEquals("/tags/fetch", tag.getSettings().getFetchWhitelistUrl());
        assertEquals("/tags/search", tag.getSettings().getSearchTagsUrl());
        assertTrue(tag.getSettings().isEnforceWhitelist());
    }

    @Test
    void test_map_TagInput_tags()
    {
        // setup
        String srcPath = "json-mapper/input/tag-input.ged";
        Input  input;

        // run
        input = mapInput(srcPath);

        // verify
        assertEquals(List.of("test", "germany"), ((Tag) input).getTags());
    }

    @Test
    void test_map_TextareaInput_allParameters()
    {
        // setup
        String srcPath = "json-mapper/input/textarea-all-input.ged";
        Input  input;

        // run
        input = mapInput(srcPath);

        // verify
        Textarea textarea = (Textarea) input;
        assertSubmittableBase(textarea);
        assertEquals("body", textarea.getValue());
        assertEquals("ph", textarea.getPlaceholder());
        assertEquals(100, textarea.getMaxCharacters());
        assertEquals("key()", textarea.getOnKeydown());
        assertEquals(3, textarea.getRows());
        assertEquals(20, textarea.getCols());
    }

    @Test
    void test_map_TextboxInput_allParameters()
    {
        // setup
        String srcPath = "json-mapper/input/textbox-all-input.ged";
        Input  input;

        // run
        input = mapInput(srcPath);

        // verify
        Textbox textbox = (Textbox) input;
        assertEquals("uid", textbox.getUid());
        assertEquals(Set.of("class1", "class2"), textbox.getClasses());
        assertEquals(Map.of("key1", "value1"), textbox.getDataAttributes());
        assertEquals("name", textbox.getName());
        assertEquals("some text", textbox.getText());
    }

    @Test
    void test_map_TextfieldInput_allParameters()
    {
        // setup
        String srcPath = "json-mapper/input/textfield-all-input.ged";
        Input  input;

        // run
        input = mapInput(srcPath);

        // verify
        Textfield textfield = (Textfield) input;
        assertSubmittableBase(textfield);
        assertEquals("init", textfield.getValue());
        assertEquals("ph", textfield.getPlaceholder());
        assertEquals(20, textfield.getMaxCharacters());
        assertEquals("[a-z]+", textfield.getPattern());
        assertEquals("key()", textfield.getOnKeydown());
        assertEquals("enter()", textfield.getOnEnterPress());
    }

    private void assertSubmittableBase(SubmittableInput input)
    {
        assertEquals("uid", input.getUid());
        assertEquals(Set.of("class1", "class2"), input.getClasses());
        assertEquals(Map.of("key1", "value1"), input.getDataAttributes());
        assertEquals("name", input.getName());
        assertEquals("submit", input.getSubmitAs());
        assertEquals("tip", input.getTooltip());
        assertEquals("inp()", input.getOnInput());
        assertEquals("info text", input.getInfoText());
        assertEquals("/info/url", input.getInfoUrl());
        assertEquals("input is mandatory", input.getMandatoryText());
        assertTrue(input.isDisabled());
        assertTrue(input.isMandatory());
    }

    @Test
    void test_map_nestedRepetition_innerItemShadowsOuter()
    {
        // setup
        JsonMapper      mapper     =
            JsonMapper
                .builder()
                .srcPath("json-mapper/nested-repetition.ged")
                .locale(Locale.ENGLISH)
                .templateFileCache(templateFileCache)
                .translationProvider(translationProvider)
                .flatMappableDissector(flatMappableDissector)
                .tokenMap(groupsTokenMap())
                .build();

        List<Container> containers;

        // run
        containers = mapper.map().getContainers();

        // verify
        assertEquals(2, containers.size());
        assertEquals(List.of("group a", "member a1", "member a2"), texts(containers.get(0)));
        assertEquals(List.of("group b", "member b1", "member b2"), texts(containers.get(1)));
    }

    @Test
    void test_map_componentSource_shadowsRepetitionItem()
    {
        // setup
        JsonMapper      mapper     =
            JsonMapper
                .builder()
                .srcPath("json-mapper/component-source-in-repetition.ged")
                .locale(Locale.ENGLISH)
                .templateFileCache(templateFileCache)
                .translationProvider(translationProvider)
                .flatMappableDissector(flatMappableDissector)
                .tokenMap(groupsTokenMap())
                .build();

        List<Container> containers;

        // run
        containers = mapper.map().getContainers();

        // verify
        assertEquals(2, containers.size());
        assertEquals(List.of("leader a"), texts(containers.get(0)));
        assertEquals(List.of("leader b"), texts(containers.get(1)));
    }

    @Test
    void test_map_unresolvedPlaceholder_usesFallback()
    {
        // setup
        String    srcPath = "json-mapper/unresolved-optional-parameters.ged";
        Container container;

        // run
        container = mapContainer(srcPath);

        // verify
        ContentContainer content = (ContentContainer) container;
        assertEquals(1, content.getContents().size());

        TextContainer text = (TextContainer) content.getContents().get(0);
        assertEquals("text", text.getText());
        assertEquals("", text.getTooltip());
        assertFalse(text.isInline());
    }

    @Test
    void test_map_unresolvedPlaceholder_requiredParameter_failsElement()
    {
        // setup
        String    srcPath = "json-mapper/unresolved-required-parameter.ged";
        Container container;

        // run
        container = mapContainer(srcPath);

        // verify
        checkAgainstErrorContainer(container);
    }

    @Test
    void test_map_NumberInput_integerTokens()
    {
        // setup
        TokenMap tokenMap =
            TokenMap
                .builder()
                .value("value", 5)
                .value("min", 0)
                .value("max", 10L)
                .value("step", 1)
                .build();
        Number   number;

        // run
        number = (Number) mapInput("json-mapper/input/number-token-input.ged", tokenMap);

        // verify
        assertEquals(BigInteger.valueOf(5), number.getValue());
        assertEquals(BigInteger.valueOf(0), number.getMin());
        assertEquals(BigInteger.valueOf(10), number.getMax());
        assertEquals(BigInteger.valueOf(1), number.getStep());
    }

    @Test
    void test_map_NumberInput_decimalTokenWithIntegerBounds()
    {
        // setup
        TokenMap tokenMap =
            TokenMap
                .builder()
                .value("price", 1.5)
                .build();
        Number   number;

        // run
        number = (Number) mapInput("json-mapper/input/number-mixed-input.ged", tokenMap);

        // verify
        assertEquals(new BigDecimal("1.5"), number.getValue());
        assertEquals(new BigDecimal("0"), number.getMin());
        assertEquals(new BigDecimal("10"), number.getMax());
        assertEquals(new BigDecimal("1"), number.getStep());
    }

    @Test
    void test_map_SliderInput_decimalTokenValue()
    {
        // setup
        TokenMap tokenMap =
            TokenMap
                .builder()
                .value("ratio", 0.5)
                .build();
        Slider   slider;

        // run
        slider = (Slider) mapInput("json-mapper/input/slider-token-input.ged", tokenMap);

        // verify
        assertEquals("0.5", slider.getValue());
        assertEquals(new BigDecimal("0"), slider.getMin());
        assertEquals(new BigDecimal("1"), slider.getMax());
        assertEquals(new BigDecimal("0.1"), slider.getStep());
    }

    @Test
    void test_map_HeadingContainer_sizeFromToken()
    {
        // setup
        TokenMap         tokenMap =
            TokenMap
                .builder()
                .value("level", 3)
                .build();
        HeadingContainer heading;

        // run
        heading = (HeadingContainer) mapContainer("json-mapper/container/heading-token-container.ged", tokenMap);

        // verify
        assertEquals(3, heading.getSize());
    }

    @Test
    void test_map_SVGContainer_literalNumbers()
    {
        // setup
        String    srcPath = "json-mapper/container/svg-line-container.ged";
        Container container;

        // run
        container = mapContainer(srcPath);

        // verify
        SVGContainer svg = (SVGContainer) container;
        assertEquals(1, svg.getElements().size());

        SVGLine line = (SVGLine) svg.getElements().get(0);
        assertEquals(0.0, line.getX1());
        assertEquals(0.5, line.getY1());
        assertEquals(100.0, line.getX2());
        assertEquals(49.5, line.getY2());
    }

    private Container mapContainer(String srcPath, TokenMap tokenMap)
    {
        JsonMapper mapper =
            JsonMapper
                .builder()
                .srcPath(srcPath)
                .locale(Locale.ENGLISH)
                .templateFileCache(templateFileCache)
                .translationProvider(translationProvider)
                .flatMappableDissector(flatMappableDissector)
                .tokenMap(tokenMap)
                .build();

        return mapper.map().getContainers().get(0);
    }

    private Input mapInput(String srcPath, TokenMap tokenMap)
    {
        JsonMapper mapper =
            JsonMapper
                .builder()
                .srcPath(srcPath)
                .locale(Locale.ENGLISH)
                .templateFileCache(templateFileCache)
                .translationProvider(translationProvider)
                .flatMappableDissector(flatMappableDissector)
                .tokenMap(tokenMap)
                .build();

        return ((FormContainer) mapper.map().getContainers().get(0)).getInputs().get(0);
    }

    private TokenMap groupsTokenMap()
    {
        return TokenMap
            .builder()
            .value(
                "groups",
                new FlatMappableList<>(
                    List.of(
                        new Group("group a", new Member("leader a"), List.of(new Member("member a1"), new Member("member a2"))),
                        new Group("group b", new Member("leader b"), List.of(new Member("member b1"), new Member("member b2"))))))
            .build();
    }

    private List<String> texts(Container container)
    {
        return ((ContentContainer) container)
            .getContents()
            .stream()
            .map(content -> ((TextContainer) content).getText())
            .toList();
    }

    public static class Member implements FlatMappable
    {

        private final String name;

        Member(String name)
        {
            this.name = name;
        }

        public String getName()
        {
            return name;
        }
    }

    public static class Group implements FlatMappable
    {

        private final String       name;
        private final Member       leader;
        private final List<Member> members;

        Group(String name, Member leader, List<Member> members)
        {
            this.name = name;
            this.leader = leader;
            this.members = members;
        }

        public String getName()
        {
            return name;
        }

        public Member getLeader()
        {
            return leader;
        }

        public List<Member> getMembers()
        {
            return members;
        }
    }
}
