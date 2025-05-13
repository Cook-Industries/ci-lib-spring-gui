package de.cook_industries.lib.spring.gui.response.message;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
public abstract class ResponseMessage
{

    @NonNull
    private final MessageTarget target = inferTarget();

    @NonNull
    private final MessageType   type;

    protected abstract MessageTarget inferTarget();

}
