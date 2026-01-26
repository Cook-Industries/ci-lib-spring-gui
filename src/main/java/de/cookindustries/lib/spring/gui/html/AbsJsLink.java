/**
 * Copyright (c) 2016-2026 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.html;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @since 1.0.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Getter(value = AccessLevel.PROTECTED)
public abstract class AbsJsLink implements HtmlExportable
{

    private final String href;

}
