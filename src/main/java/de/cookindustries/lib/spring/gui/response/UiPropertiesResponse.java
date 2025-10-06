package de.cookindustries.lib.spring.gui.response;

import de.cookindustries.lib.spring.gui.hmi.util.JsProperties;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@ToString
public final class UiPropertiesResponse extends Response
{

    @NonNull
    private final JsProperties properties;

    @Override
    protected ResponseAction inferType()
    {
        return ResponseAction.PROPERTIES;
    }

}
