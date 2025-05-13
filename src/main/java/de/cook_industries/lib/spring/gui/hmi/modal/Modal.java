/**
 * Copyright(c) app.year sebastian koch/CI. All rights reserved.<br>
 * mailto: koch.sebastian@cook-industries.de
 *
 * @author : sebastian koch <koch.sebastian@cook-industries.de>
 */
package de.cook_industries.lib.spring.gui.hmi.modal;

import java.util.List;
import java.util.UUID;

import de.cook_industries.lib.spring.gui.hmi.UiElement;
import de.cook_industries.lib.spring.gui.hmi.container.Container;
import de.cook_industries.lib.spring.gui.hmi.mapper.HtmlMapper;

import lombok.Getter;
import lombok.NonNull;
import lombok.Singular;
import lombok.Builder.Default;
import lombok.experimental.SuperBuilder;

/**
 *
 * @author <a href="mailto:koch.sebastian@cook-industries.de">sebastian koch</a>
 */
@SuperBuilder
@Getter
public final class Modal extends UiElement
{

    @NonNull
    private String          name;
    @NonNull
    private String          requestUrl;
    @NonNull
    @Default
    private String          identifier          = UUID.randomUUID().toString();
    @NonNull
    @Singular
    private List<Container> containers;
    @NonNull
    @Default
    private Boolean         closeOnOverlayClick = false;

    public String getContentHtml()
    {
        return HtmlMapper.map(containers);
    }
}
