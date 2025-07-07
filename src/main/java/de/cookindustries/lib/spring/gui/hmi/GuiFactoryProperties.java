/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.hmi;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Builder.Default;

@Builder
@Getter(value = AccessLevel.PACKAGE)
public class GuiFactoryProperties
{

    @Default
    private final String cssBaseFilePath = null;
}
