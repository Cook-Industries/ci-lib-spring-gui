package com.ci.lib.spring.web.function;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public final class CloseModalCall extends Call
{

    public CloseModalCall()
    {
        super("closeModal", 0);
    }
}
