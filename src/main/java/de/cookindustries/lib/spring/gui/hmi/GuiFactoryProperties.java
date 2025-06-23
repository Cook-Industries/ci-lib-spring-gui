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
