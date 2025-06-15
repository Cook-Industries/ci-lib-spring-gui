/**
 * Copyright(c) 2025 sebastian koch/Cook Industries. All rights reserved.
 *
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
package de.cookindustries.lib.spring.gui.html;

import de.cookindustries.lib.spring.gui.hmi.container.ButtonContainer;
import de.cookindustries.lib.spring.gui.hmi.container.ContentContainer;
import de.cookindustries.lib.spring.gui.hmi.container.TextContainer;
import de.cookindustries.lib.spring.gui.hmi.input.Button;

public class HtmlSiteLib
{

    private HtmlSiteLib()
    {}

    /**
     * Build a basic site with standard imports (bootstrap, jquery, sprintf, ci-lib-spring-web) with integrated loader, error and modal
     * overlay
     * 
     * @param title of the webpage
     * @return the created webpage builder for further usage
     */
    public static HtmlFileBuilder basic(String title)
    {
        return HtmlFileBuilder
            .site()
            .title(title)
            .importMap(new JsImport("cilib", "/js/ci-lib-spring-web.js"))
            .script(new JsPlainLink("/webjars/jquery/jquery.min.js"))
            .script(new JsPlainLink("/webjars/bootstrap/js/bootstrap.min.js"))
            .script(new JsPlainLink("/js/sprintf.min.js"))
            .css("/webjars/bootstrap/css/bootstrap.min.css")
            .css("/css/ci-core.css")
            .content(ContentContainer.builder().uid("modal-container").clazz("hidden").build())
            .content(ContentContainer
                .builder()
                .uid("global-loader-overlay")
                .clazz("d-flex")
                .clazz("justify-content-center")
                .clazz("align-items-center")
                .clazz("vh-100")
                .content(ContentContainer
                    .builder()
                    .uid("global-loader")
                    .clazz("d-flex")
                    .clazz("flex-column")
                    .clazz("justify-items-center")
                    .clazz("align-items-center")
                    .content(ContentContainer.builder().clazz("spinner-border").clazz("text-primary").build())
                    .content(TextContainer.builder().uid("global-loader-text").clazz("sr-only").text("loading...").build())
                    .build())
                .build())
            .content(ContentContainer
                .builder()
                .uid("error-overlay")
                .clazz(CSSClass.HIDDEN.getClassName())
                .content(ContentContainer
                    .builder()
                    .uid("error-border-container")
                    .content(ContentContainer.builder().uid("error-holder").build())
                    .content(ButtonContainer
                        .builder()
                        .button(Button.builder().text("OK").onClick("dismissErrors()").build())
                        .build())
                    .build())
                .build())
            .content(ContentContainer.builder().uid("popup-holder").build());
    }
}
