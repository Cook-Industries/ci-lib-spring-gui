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

/**
 * Function to request a Modal from the backend.
 * 
 * @since 3.0.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
@Data
@EqualsAndHashCode(callSuper = false)
public final class RequestModal extends AbsFunctionCall
{

    public RequestModal(String url)
    {
        this(url, null);
    }

    public RequestModal(String url, AbsFunctionArgs args)
    {
        super();

        setParam(url);
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
