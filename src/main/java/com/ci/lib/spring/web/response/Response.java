package com.ci.lib.spring.web.response;

import java.util.List;

import com.ci.lib.spring.web.function.Call;

import lombok.Getter;
import lombok.NonNull;
import lombok.Singular;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
public abstract class Response
{

    @NonNull
    private final Action              action = inferType();

    @Singular
    private List<NotificationMessage> messages;

    @Singular
    private List<Call>                calls;

    protected abstract Action inferType();

}
