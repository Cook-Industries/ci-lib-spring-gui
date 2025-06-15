package de.cookindustries.lib.spring.gui.response.message;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

@SuperBuilder
@Getter
@Jacksonized
public final class PopupMessage extends ResponseMessage
{

    @NonNull
    private final String msg;

    @Override
    protected MessageTarget inferTarget()
    {
        return MessageTarget.POP_UP;
    }

}
