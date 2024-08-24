package com.ci.lib.spring.web.response;

import com.ci.lib.spring.web.hmi.modal.Modal;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
public class ModalResponse extends Response
{

    @NonNull
    private Modal modal;

    @Override
    protected Action inferType()
    {
        return Action.MODAL;
    }
}
