/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.i18n;

import java.util.Locale;

/**
 * Concrete implementation of {@link AbsTranslationProvider} using the fallback values from {@link BasicText} with the locale
 * {@link Locale#ENGLISH}.
 * 
 * @since 1.0.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
public final class StaticTranslationProvider extends AbsTranslationProvider
{

    @Override
    protected void initMaps()
    {
        Locale eng = Locale.ENGLISH;

        for (BasicText text : BasicText.values())
        {
            addTranslation(eng, text.getText(), text.getText());
        }
    }
}
