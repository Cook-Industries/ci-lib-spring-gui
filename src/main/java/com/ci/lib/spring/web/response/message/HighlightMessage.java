package com.ci.lib.spring.web.response.message;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

@SuperBuilder
@Getter
@Jacksonized
public final class HighlightMessage extends ResponseMessage
{

    @NonNull
    private final String formId;

    @NonNull
    private final String fieldId;

    @Override
    protected MessageTarget inferTarget()
    {
        return MessageTarget.MARKER;
    }

}
