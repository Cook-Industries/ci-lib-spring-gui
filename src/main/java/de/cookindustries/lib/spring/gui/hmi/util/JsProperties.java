package de.cookindustries.lib.spring.gui.hmi.util;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Singular;
import lombok.ToString;

@Builder
@Getter
@ToString
public final class JsProperties
{

    @Singular
    private final List<WebSocketDef> websockets;

}
