/**
 * Copyright (c) 2016-2026 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.util.file;

import de.cookindustries.lib.spring.gui.config.properties.CiLibProperties;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public final class UploadSessionPolicy
{

    private final long partLength;

    private final int  maxParallelUploadsPerFile;

    private final int  maxParallelUploadsGlobal;

    private final int  maxRetries;

    private final long retryBaseDelayMillis;

    private final long retryMaxDelayMillis;

    public UploadSessionPolicy(CiLibProperties properties)
    {
        this.partLength = properties.getWeb().getUpload().getPartSize().toBytes();
        this.maxParallelUploadsPerFile = properties.getWeb().getUpload().getMaxPerFileParallel();
        this.maxParallelUploadsGlobal = properties.getWeb().getUpload().getMaxGlobalParallel();
        this.maxRetries = properties.getWeb().getUpload().getMaxRetries();
        this.retryBaseDelayMillis = properties.getWeb().getUpload().getRetryBaseDelay().toMillis();
        this.retryMaxDelayMillis = properties.getWeb().getUpload().getRetryMaxDelay().toMillis();

        if (partLength <= 0)
        {
            throw new IllegalArgumentException("partLength must be positive: " + partLength);
        }

        if (maxParallelUploadsPerFile < 1)
        {
            throw new IllegalArgumentException("maxParallelUploadsPerFile must be >= 1: " + maxParallelUploadsPerFile);
        }

        if (maxParallelUploadsGlobal < 1)
        {
            throw new IllegalArgumentException("maxParallelUploadsGlobal must be >= 1: " + maxParallelUploadsGlobal);
        }

    }
}
