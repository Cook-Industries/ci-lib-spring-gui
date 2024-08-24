package com.ci.lib.spring.web.hmi.input;

import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
public abstract class SubmittableInput extends Input
{

    @NonNull
    private String name;
    @NonNull
    private String submitAs;
}
