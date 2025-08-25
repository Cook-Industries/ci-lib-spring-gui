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
import de.cookindustries.lib.spring.gui.function.SubmitFromModal;
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
    private final String          name;

    @NonNull
    private final String          requestUrl;

    @Default
    private final String          btnNameLeft         = null;

    @NonNull
    @Default
    private final ButtonClass     btnClassLeft        = ButtonClass.DEFAULT;

    @Default
    private final String          btnFunctionLeft     = new CloseModal().parseAsJS();

    @Default
    private final String          btnNameCenter       = null;

    @NonNull
    @Default
    private final ButtonClass     btnClassCenter      = ButtonClass.DEFAULT;

    @Default
    private final String          btnFunctionCenter   = null;

    @NonNull
    private final String          btnNameRight;

    @NonNull
    @Default
    private final ButtonClass     btnClassRight       = ButtonClass.DEFAULT;

    @Default
    private final String          btnFunctionRight    = new SubmitFromModal().parseAsJS();

    @NonNull
    @Singular
    private final List<Container> contents;

    @NonNull
    @Default
    private final Boolean         closeOnOverlayClick = Boolean.FALSE;

    @Override
    protected ContainerType inferType()
    {
        return ContainerType.MODAL;
    }
}
