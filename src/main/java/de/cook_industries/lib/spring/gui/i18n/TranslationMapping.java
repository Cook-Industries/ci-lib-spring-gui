package de.cook_industries.lib.spring.gui.i18n;

import java.util.List;

import lombok.Data;

@Data
public class TranslationMapping
{

    private String                       language;
    private String                       country;
    private List<TranslationMappingText> elements;
}
