/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.hmi.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Component;

import de.cookindustries.lib.spring.gui.function.AbsFunctionCall;
import de.cookindustries.lib.spring.gui.hmi.container.Button;
import de.cookindustries.lib.spring.gui.hmi.container.Container;
import de.cookindustries.lib.spring.gui.hmi.container.ContentContainer;
import de.cookindustries.lib.spring.gui.hmi.container.ModalContainer;
import de.cookindustries.lib.spring.gui.hmi.container.TextContainer;
import de.cookindustries.lib.spring.gui.hmi.input.util.InputExtractor;
import de.cookindustries.lib.spring.gui.hmi.mapper.exception.JsonMapperException;
import de.cookindustries.lib.spring.gui.hmi.mapper.json.JsonMapper;
import de.cookindustries.lib.spring.gui.hmi.mapper.json.JsonTreeMapper;
import de.cookindustries.lib.spring.gui.hmi.mapper.json.JsonTreeRoot;
import de.cookindustries.lib.spring.gui.hmi.mapper.json.MapperResult;
import de.cookindustries.lib.spring.gui.hmi.mapper.util.ValueMap;
import de.cookindustries.lib.spring.gui.html.CSSClass;
import de.cookindustries.lib.spring.gui.html.CSSLink;
import de.cookindustries.lib.spring.gui.html.HtmlSite;
import de.cookindustries.lib.spring.gui.html.JsImport;
import de.cookindustries.lib.spring.gui.html.JsPlainLink;
import de.cookindustries.lib.spring.gui.html.SiteImports;
import de.cookindustries.lib.spring.gui.i18n.AbsTranslationProvider;
import de.cookindustries.lib.spring.gui.response.ContentResponse;
import de.cookindustries.lib.spring.gui.response.ModalResponse;
import de.cookindustries.lib.spring.gui.response.NotificationResponse;
import de.cookindustries.lib.spring.gui.response.message.MessageType;
import de.cookindustries.lib.spring.gui.response.message.PopupMessage;

/**
 * A set of convenience functions to create {@code HTML} sites or components to send as a response.
 * 
 * @since 1.0.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
@Component
public final class GUIFactory
{

    private final AbsTranslationProvider translationProvider;
    private final SiteImports            basicImports;

    /**
     * Construct a GUIFactory instance
     * 
     * @param properties to incorporate
     * @param translationProvider to fetch translated text from
     */
    public GUIFactory(GuiFactoryProperties properties, AbsTranslationProvider translationProvider)
    {
        this.translationProvider = translationProvider;

        List<CSSLink> cssLinks = properties.getCssPaths().stream().map(CSSLink::new).toList();

        basicImports =
            SiteImports
                .builder()
                .jsImport(new JsImport("cilib", "/js/ci-lib-spring-web.js"))
                .jsScript(new JsPlainLink("/webjars/jquery/jquery.min.js"))
                .jsScript(new JsPlainLink("/webjars/bootstrap/js/bootstrap.min.js"))
                .jsScript(new JsPlainLink("/webjars/yaireo__tagify/dist/tagify.js"))
                .cssLink(new CSSLink("/webjars/bootstrap/css/bootstrap.min.css"))
                .cssLink(new CSSLink("/webjars/yaireo__tagify/dist/tagify.css"))
                .cssLink(new CSSLink("/css/ci-core.css"))
                .cssLinks(cssLinks)
                .build();
    }

    /**
     * Read a static template and transform into a {@link Container}
     * 
     * @param resourcePath to load template from
     * @return the parsed {@code Container}
     */
    public MapperResult readStaticComponent(String resourcePath)
    {
        try
        {
            JsonTreeMapper treeMapper = new JsonTreeMapper();
            JsonTreeRoot   root       = treeMapper.map(resourcePath);
            JsonMapper     mapper     = new JsonMapper(root);

            return mapper.map();
        }
        catch (Exception e)
        {
            throw new JsonMapperException(String.format("error building gui component [%s] : [%s]", resourcePath, e.getMessage()));
        }
    }

    /**
     * Read a dynamic template and transform into a {@link Container}
     * 
     * @param resourcePath to load template from
     * @param locale to fetch translated text with
     * @param valueMaps dynamic {@code valueMap}s
     * @return the parsed {@code Container}
     */
    public MapperResult readDynamicComponent(String resourcePath, Locale locale, List<ValueMap> valueMaps)
    {
        try
        {
            JsonTreeMapper treeMapper = new JsonTreeMapper();
            JsonTreeRoot   root       = treeMapper.map(resourcePath);
            JsonMapper     mapper     = new JsonMapper(root, locale, translationProvider, valueMaps);

            return mapper.map();
        }
        catch (Exception e)
        {
            throw new JsonMapperException(String.format("error building gui component [%s] : [%s]", resourcePath, e.getMessage()));
        }
    }

    /**
     * Create a HTML site from a static content template.
     * 
     * @param title of the website
     * @param resourcePath to the static component template
     * @return a {@code HTML} {@code String} to render a website by a browser
     */
    public String createHtmlSiteWithStaticContent(String title, String resourcePath, AbsFunctionCall... initCalls)
    {
        return createHtmlSiteWithStaticContent(title, SiteImports.builder().build(), resourcePath, initCalls);
    }

    /**
     * Create a HTML site from a static content template.
     * 
     * @param title of the website
     * @param imports to include
     * @param resourcePath to load template from
     * @return a {@code HTML} {@code String} to render a website by a browser
     */
    public String createHtmlSiteWithStaticContent(String title, SiteImports imports, String resourcePath, AbsFunctionCall... initCalls)
    {
        MapperResult          result = readStaticComponent(resourcePath);
        List<AbsFunctionCall> calls  = new ArrayList<>();
        calls.addAll(result.getFunctions());
        calls.addAll(Arrays.asList(initCalls));

        return createHtmlSite(title, imports, result.getContainers(), calls);
    }

    /**
     * Create a HTML site from a dynamic content template.
     * 
     * @param title of the website
     * @param resourcePath to load template from
     * @param locale to fetch translations with
     * @param valueMaps to fetch dynamic values from
     * @return a {@code HTML} {@code String} to render a website by a browser
     */
    public String createHtmlSiteWithDynamicContent(String title, String resourcePath, Locale locale, List<ValueMap> valueMaps,
        AbsFunctionCall... initCalls)
    {
        return createHtmlSiteWithDynamicContent(title, SiteImports.builder().build(), resourcePath, locale, valueMaps, initCalls);
    }

    /**
     * Create a HTML site from a dynamic content template.
     * 
     * @param title of the website
     * @param imports to include
     * @param resourcePath to load template from
     * @param locale to fetch translations with
     * @param valueMaps to fetch dynamic values from
     * @return a {@code HTML} {@code String} to render a website by a browser
     */
    public String createHtmlSiteWithDynamicContent(String title, SiteImports imports, String resourcePath, Locale locale,
        List<ValueMap> valueMaps, AbsFunctionCall... initCalls)
    {
        MapperResult          result = readDynamicComponent(resourcePath, locale, valueMaps);
        List<AbsFunctionCall> calls  = new ArrayList<>();
        calls.addAll(result.getFunctions());
        calls.addAll(Arrays.asList(initCalls));

        return createHtmlSite(title, imports, result.getContainers(), calls);
    }

    /**
     * Create a HTML website based on the standard imports added with user imports
     * 
     * @param title of the website
     * @param imports defined by the user
     * @param content to set
     * @return a {@code HTML} {@code String} to render a website by a browser
     */
    private String createHtmlSite(String title, SiteImports imports, List<Container> content, List<AbsFunctionCall> initFunctions)
    {
        return HtmlSite.builder()
            .title(title)
            .jsImports(imports.getJsImports())
            .jsScripts(imports.getJsScripts())
            .cssLinks(imports.getCssLinks())
            .cssEntities(imports.getCssEntities())
            .jsImports(basicImports.getJsImports())
            .jsScripts(basicImports.getJsScripts())
            .cssLinks(basicImports.getCssLinks())
            .cssEntities(basicImports.getCssEntities())
            .container(
                ContentContainer
                    .builder()
                    .uid("modal-container")
                    .clazz("hidden")
                    .build())
            .container(
                ContentContainer
                    .builder()
                    .uid("global-loader-overlay")
                    .clazz("d-flex")
                    .clazz("justify-content-center")
                    .clazz("align-items-center")
                    .clazz("vh-100")
                    .content(
                        ContentContainer
                            .builder()
                            .uid("global-loader")
                            .clazz("d-flex")
                            .clazz("flex-column")
                            .clazz("justify-items-center")
                            .clazz("align-items-center")
                            .content(
                                ContentContainer
                                    .builder()
                                    .clazz("spinner-border")
                                    .clazz("text-primary")
                                    .build())
                            .content(
                                TextContainer
                                    .builder()
                                    .uid("global-loader-text")
                                    .clazz("sr-only")
                                    .text("loading...")
                                    .build())
                            .build())
                    .build())
            .container(ContentContainer
                .builder()
                .uid("error-overlay")
                .clazz(CSSClass.HIDDEN.getClassName())
                .content(
                    ContentContainer
                        .builder()
                        .uid("error-border-container")
                        .content(
                            ContentContainer
                                .builder()
                                .uid("error-holder")
                                .build())
                        .content(
                            Button
                                .builder()
                                .text("OK")
                                .onClick("CILIB.FunctionRegistry.call('dismissErrors');")
                                .build())
                        .build())
                .build())
            .container(
                ContentContainer
                    .builder()
                    .uid("popup-holder")
                    .build())
            .containers(content)
            .functions(initFunctions)
            .build()
            .getHtmlRep();
    }

    /**
     * Create a {@link ContentResponse} from a static template to append or replace existing content on a receiver and perform function
     * calls.
     * 
     * @param resourcePath to load template from
     * @param elementId to append/replace content in
     * @param replace whether the content should be appende (false), or replaced (true)
     * @param initCalls to perform on the receiver
     * @return a response with the processed content
     */
    public ContentResponse createStaticComponentResponse(String resourcePath, String elementId, Boolean replace,
        AbsFunctionCall... initCalls)
    {
        MapperResult          result = readStaticComponent(resourcePath);
        List<AbsFunctionCall> calls  = new ArrayList<>();
        calls.addAll(result.getFunctions());
        calls.addAll(Arrays.asList(initCalls));

        return ContentResponse
            .builder()
            .elementId(elementId)
            .content(
                ContentContainer
                    .builder()
                    .contents(result.getContainers())
                    .build())
            .calls(calls)
            .replace(replace)
            .build();
    }

    /**
     * Create a {@link ContentResponse} from a dynamic template to append or replace existing content on a receiver.
     * 
     * @param resourcePath to the dynamic component template
     * @param locale to fetch translations with
     * @param elementId to append/replace content in
     * @param replace whether the content should be appende (false), or replaced (true)
     * @param valueMaps to fetch dynamic values from
     * @param initCalls to perform on the receiver
     * @return a response with the processed content
     */
    public ContentResponse createDynamicComponentResponse(String resourcePath, Locale locale, String elementId, Boolean replace,
        List<ValueMap> valueMaps, AbsFunctionCall... initCalls)
    {
        MapperResult          result = readDynamicComponent(resourcePath, locale, valueMaps);
        List<AbsFunctionCall> calls  = new ArrayList<>();
        calls.addAll(result.getFunctions());
        calls.addAll(Arrays.asList(initCalls));

        return ContentResponse
            .builder()
            .elementId(elementId)
            .content(
                ContentContainer
                    .builder()
                    .contents(result.getContainers())
                    .build())
            .calls(calls)
            .replace(replace)
            .build();
    }

    /**
     * Create a {@link ModalResponse} from a static template.
     * 
     * @param resourcePath to load template from
     * @return a response with the processed content
     */
    public ModalResponse createStaticModalResponse(String resourcePath)
    {
        MapperResult   result  = readStaticComponent(resourcePath);
        ModalContainer content = (ModalContainer) result.getContainers().getFirst();

        return ModalResponse
            .builder()
            .modal(content)
            .calls(result.getFunctions())
            .build();
    }

    /**
     * Create a {@link ModalResponse} from a dynamic template.
     * 
     * @param resourcePath to the dynamic component template
     * @param locale to fetch translations with
     * @param valueMaps to fetch dynamic values from
     * @param initCalls to perform on the receiver
     * @return a response with the processed content
     */
    public ModalResponse createDynamicModalResponse(String resourcePath, Locale locale, List<ValueMap> valueMaps,
        AbsFunctionCall... initCalls)
    {
        MapperResult          result  = readDynamicComponent(resourcePath, locale, valueMaps);
        ModalContainer        content = (ModalContainer) result.getContainers().getFirst();
        List<AbsFunctionCall> calls   = new ArrayList<>();
        calls.addAll(result.getFunctions());
        calls.addAll(Arrays.asList(initCalls));

        return ModalResponse
            .builder()
            .modal(content)
            .calls(calls)
            .build();
    }

    /**
     * Create a {@link NotificationResponse} from a {@link InputExtractor}.
     * 
     * @param inputExtractor to extract messages from
     * @return a response containing the marker raised by {@code inputExtractor}
     */
    public NotificationResponse createActiveMarkerResponseFrom(InputExtractor inputExtractor)
    {
        return NotificationResponse
            .builder()
            .messages(inputExtractor.getMessages())
            .build();
    }

    /**
     * Create a {@link NotificationResponse} with a {@link PopupMessage}.
     * 
     * @param msg of to show in the pop-up
     * @param type to color the pop-up
     * @param calls to perform
     * @return a response containing a popup notification and function calls
     */
    public NotificationResponse createPopUpResponse(String msg, MessageType type, AbsFunctionCall... calls)
    {
        return NotificationResponse
            .builder()
            .message(
                PopupMessage
                    .builder()
                    .msg(msg)
                    .type(type)
                    .build())
            .calls(Arrays.asList(calls))
            .build();
    }
}
