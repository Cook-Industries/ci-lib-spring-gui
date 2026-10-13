/**
 * Copyright (c) 2016-2026 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.util.file;

import java.time.Duration;

/**
 * Server limits that are not part of the policy sent to the client.
 */
public record UploadSessionLimits(long maxFileLength, long maxBatchLength, Duration sessionTtl)
{

    public UploadSessionLimits
    {
        if (maxFileLength <= 0 || maxBatchLength <= 0)
        {
            throw new IllegalArgumentException("max lengths must be positive");
        }

        if (sessionTtl == null || sessionTtl.isZero() || sessionTtl.isNegative())
        {
            throw new IllegalArgumentException("sessionTtl must be positive: " + sessionTtl);
        }
    }
}
