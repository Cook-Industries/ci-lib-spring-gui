/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.function;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public final class RequestModal extends AbsFunctionCall
{

    public RequestModal(String requestUrl, AbsFunctionArgs args)
    {
        super();

        setParam(requestUrl);
        setParam(args);
    }

    @Override
    protected String functionName()
    {
        return "requestModal";
    }

    @Override
    protected Integer numberOfParameters()
    {
        return 2;
    }

}
