/**
 * Copyright (c) 2016-2026 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.i18n;

import java.util.Locale;
import java.util.Map;

/**
 * Translations for one {@link Locale}.
 *
 * @param checksum over all keys and texts, changes with any of them
 * @param texts the translation templates by key, placeholders like {@code {0}} are not filled
 */
public record UiTranslations(String checksum, Map<String, String> texts)
{
}
