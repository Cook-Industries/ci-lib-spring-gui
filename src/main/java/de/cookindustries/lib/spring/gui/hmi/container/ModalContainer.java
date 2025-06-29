/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.hmi.container;

import java.util.List;

import de.cookindustries.lib.spring.gui.function.CloseModal;
import de.cookindustries.lib.spring.gui.hmi.input.ButtonClass;
import lombok.Getter;
import lombok.NonNull;
import lombok.Singular;
import lombok.Builder.Default;
import lombok.experimental.SuperBuilder;

/**
 * @since 1.0.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
@SuperBuilder
@Getter
public final class ModalContainer extends Container
{

    @NonNull
    private String          name;

    @NonNull
    private String          requestUrl;

    @Default
    private String          btnNameLeft         = null;

    @Default
    @NonNull
    private ButtonClass     btnClassLeft        = ButtonClass.DEFAULT;

    @Default
    private String          btnFunctionLeft     = null;

    @Default
    private String          btnNameCenter       = null;

    @Default
    @NonNull
    private ButtonClass     btnClassCenter      = ButtonClass.DEFAULT;

    @Default
    private String          btnFunctionCenter   = null;

    @NonNull
    private String          btnNameRight;

    @Default
    @NonNull
    private ButtonClass     btnClassRight       = ButtonClass.DEFAULT;

    @Default
    private String          btnFunctionRight    = new CloseModal().parseAsJS();

    @NonNull
    @Singular
    private List<Container> contents;

    @Default
    private boolean         closeOnOverlayClick = false;

    @Override
    protected ContainerType inferType()
    {
        return ContainerType.MODAL;
    }
}
