/**
 * Copyright(c) app.year sebastian koch/CI. All rights reserved.<br>
 *
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
package de.cookindustries.lib.spring.gui.hmi.modal;

import java.util.List;
import java.util.UUID;

import de.cookindustries.lib.spring.gui.hmi.UiElement;
import de.cookindustries.lib.spring.gui.hmi.container.Container;
import de.cookindustries.lib.spring.gui.hmi.mapper.ContainerHtmlMapper;
import lombok.Getter;
import lombok.NonNull;
import lombok.Singular;
import lombok.Builder.Default;
import lombok.experimental.SuperBuilder;

/**
 *

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
        return ContainerHtmlMapper.map(containers);
    }
}
