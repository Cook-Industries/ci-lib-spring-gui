/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.hmi;

import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Component;

import de.cookindustries.lib.spring.gui.function.FunctionCall;
import de.cookindustries.lib.spring.gui.hmi.container.Button;
import de.cookindustries.lib.spring.gui.hmi.container.Container;
import de.cookindustries.lib.spring.gui.hmi.container.ContentContainer;
import de.cookindustries.lib.spring.gui.hmi.container.ModalContainer;
import de.cookindustries.lib.spring.gui.hmi.container.TextContainer;
import de.cookindustries.lib.spring.gui.hmi.input.util.InputExtractor;
import de.cookindustries.lib.spring.gui.hmi.mapper.JsonMapper;
import de.cookindustries.lib.spring.gui.hmi.mapper.JsonTreeMapper;
import de.cookindustries.lib.spring.gui.hmi.mapper.JsonTreeRoot;
import de.cookindustries.lib.spring.gui.hmi.mapper.ValueMap;
import de.cookindustries.lib.spring.gui.hmi.mapper.exception.JsonMapperException;
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

        basicImports =
            SiteImports
                .builder()
                .jsImport(new JsImport("cilib", "/js/ci-lib-spring-web.js"))
                .jsScript(new JsPlainLink("/webjars/jquery/jquery.min.js"))
                .jsScript(new JsPlainLink("/webjars/bootstrap/js/bootstrap.min.js"))
                .jsScript(new JsPlainLink("/js/sprintf.min.js"))
                .cssLink(new CSSLink("/webjars/bootstrap/css/bootstrap.min.css"))
                .cssLink(
                    properties.getCssBaseFilePath() == null
                        ? new CSSLink("/css/ci-core.css")
                        : new CSSLink(properties.getCssBaseFilePath()))
                .build();
    }

    /**
     * Read a static template and transform into a {@link Container}
     * 
     * @param resourcePath to load template from
     * @return the parsed {@code Container}
     */
    public Container readStaticComponent(String resourcePath)
    {
        try
        {
            JsonTreeMapper mapper = new JsonTreeMapper();
            JsonTreeRoot   root   = mapper.map(resourcePath);

            return JsonMapper.map(root);
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
    public Container readDynamicComponent(String resourcePath, Locale locale, ValueMap... valueMaps)
    {
        try
        {
            JsonTreeMapper mapper = new JsonTreeMapper();
            JsonTreeRoot   root   = mapper.map(resourcePath);

            return JsonMapper.map(root, locale, translationProvider, valueMaps);
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
    public String createHtmlSiteWithStaticContent(String title, String resourcePath)
    {
        Container content = readStaticComponent(resourcePath);

        return createHtmlSite(title, SiteImports.builder().build(), content);
    }

    /**
     * Create a HTML site from a static content template.
     * 
     * @param title of the website
     * @param imports to include
     * @param resourcePath to load template from
     * @return a {@code HTML} {@code String} to render a website by a browser
     */
    public String createHtmlSiteWithStaticContent(String title, SiteImports imports, String resourcePath)
    {
        Container content = readStaticComponent(resourcePath);

        return createHtmlSite(title, imports, content);
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
    public String createHtmlSiteWithDynamicContent(String title, String resourcePath, Locale locale,
        ValueMap... valueMaps)
    {
        Container content = readDynamicComponent(resourcePath, locale, valueMaps);

        return createHtmlSite(title, SiteImports.builder().build(), content);
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
        ValueMap... valueMaps)
    {
        Container content = readDynamicComponent(resourcePath, locale, valueMaps);

        return createHtmlSite(title, imports, content);
    }

    /**
     * Create a HTML website based on the standard imports added with user imports
     * 
     * @param title of the website
     * @param imports defined by the user
     * @param content to set
     * @return a {@code HTML} {@code String} to render a website by a browser
     */
    private String createHtmlSite(String title, SiteImports imports, Container content)
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
                                .onClick("dismissErrors()")
                                .build())
                        .build())
                .build())
            .container(
                ContentContainer
                    .builder()
                    .uid("popup-holder")
                    .build())
            .container(content)
            .build()
            .getHtmlRep();
    }

    /**
     * Create a {@link ContentResponse} from a static template to append or replace existing content on a receiver.
     * 
     * @param resourcePath to load template from
     * @param elementId to append/replace content in
     * @param replace whether the content should be appende (false), or replaced (true)
     * @return a response with the processed content
     */
    public ContentResponse createStaticComponentResponse(String resourcePath, String elementId, Boolean replace)
    {
        return createStaticComponentResponse(resourcePath, elementId, replace, List.of());
    }

    /**
     * Create a {@link ContentResponse} from a static template to append or replace existing content on a receiver and perform function
     * calls.
     * 
     * @param resourcePath to load template from
     * @param elementId to append/replace content in
     * @param replace whether the content should be appende (false), or replaced (true)
     * @param functionCalls to perform on the receiver
     * @return a response with the processed content
     */
    public ContentResponse createStaticComponentResponse(String resourcePath, String elementId, Boolean replace,
        List<FunctionCall> functionCalls)
    {
        Container content = readStaticComponent(resourcePath);

        return ContentResponse
            .builder()
            .content(content)
            .calls(functionCalls)
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
        ModalContainer content = (ModalContainer) readStaticComponent(resourcePath);

        return ModalResponse
            .builder()
            .modal(content)
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
     * @return a response with the processed content
     */
    public ContentResponse createDynamicComponentResponse(String resourcePath, Locale locale, String elementId, Boolean replace,
        ValueMap... valueMaps)
    {
        return createDynamicComponentResponse(resourcePath, locale, elementId, replace, List.of(), valueMaps);
    }

    /**
     * Create a {@link ContentResponse} from a dynamic template to append or replace existing content on a receiver.
     * 
     * @param resourcePath to the dynamic component template
     * @param locale to fetch translations with
     * @param elementId to append/replace content in
     * @param replace whether the content should be appende (false), or replaced (true)
     * @param functionCalls to perform on the receiver
     * @param valueMaps to fetch dynamic values from
     * @return a response with the processed content
     */
    public ContentResponse createDynamicComponentResponse(String resourcePath, Locale locale, String elementId, Boolean replace,
        List<FunctionCall> functionCalls, ValueMap... valueMaps)
    {
        Container content = readDynamicComponent(resourcePath, locale, valueMaps);

        return ContentResponse
            .builder()
            .content(content)
            .calls(functionCalls)
            .replace(replace)
            .build();
    }

    /**
     * Create a {@link ModalResponse} from a dynamic template.
     * 
     * @param resourcePath to the dynamic component template
     * @param locale to fetch translations with
     * @param valueMaps to fetch dynamic values from
     * @return a response with the processed content
     */
    public ModalResponse createDynamicModalResponse(String resourcePath, Locale locale, ValueMap... valueMaps)
    {
        ModalContainer content = (ModalContainer) readDynamicComponent(resourcePath, locale, valueMaps);

        return ModalResponse
            .builder()
            .modal(content)
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
     * @param functionCalls to perform
     * @return a response containing a popup notification and function calls
     */
    public NotificationResponse createPopUpResponse(String msg, MessageType type, List<FunctionCall> functionCalls)
    {
        return NotificationResponse
            .builder()
            .message(
                PopupMessage
                    .builder()
                    .msg(msg)
                    .type(type)
                    .build())
            .calls(functionCalls)
            .build();
    }
}
