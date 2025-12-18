/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.hmi.svg;

import java.util.Arrays;
import java.util.List;

public enum SVGType {

    LINE, TEXT, GROUP;

    public static final List<SVGType> ALLOWED_BASE_TYPES = Arrays.asList(SVGType.values());

}
