package com.ci.lib.spring.web.hmi.container;

import java.util.List;

import com.ci.lib.spring.web.hmi.input.Button;

import lombok.Getter;
import lombok.Singular;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
public class ButtonBarContainer extends Container
{

    @Singular
    private List<Button> buttons;

    @Override
    protected ContainerType inferType()
    {
        return ContainerType.BUTTON_BAR;
    }

}
