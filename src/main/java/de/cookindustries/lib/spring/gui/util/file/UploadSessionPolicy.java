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

    private final int  retryBaseDelayMillis;

    private final int  retryMaxDelayMillis;

    public UploadSessionPolicy(CiLibProperties properties)
    {
        this.partLength = properties.getWeb().getUpload().getPartSize().toBytes();
        this.maxParallelUploadsPerFile = properties.getWeb().getUpload().getMaxPerFileParallel();
        this.maxParallelUploadsGlobal = properties.getWeb().getUpload().getMaxGlobalParallel();
        this.maxRetries = properties.getWeb().getUpload().getMaxRetries();
        this.retryBaseDelayMillis = properties.getWeb().getUpload().getRetryBaseDelay().toMillisPart();
        this.retryMaxDelayMillis = properties.getWeb().getUpload().getRetryMaxDelay().toMillisPart();

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
