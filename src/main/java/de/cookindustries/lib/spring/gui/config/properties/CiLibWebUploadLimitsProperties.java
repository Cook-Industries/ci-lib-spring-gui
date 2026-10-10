/**
 * Copyright (c) 2016-2026 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.config.properties;

import java.time.Duration;

import org.springframework.boot.convert.DataSizeUnit;
import org.springframework.boot.convert.DurationFormat;
import org.springframework.boot.convert.DurationStyle;
import org.springframework.util.unit.DataSize;
import org.springframework.util.unit.DataUnit;

import jakarta.validation.constraints.AssertTrue;
import lombok.Data;

/**
 * Path properties in relation to web interfaces.
 * 
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
@Data
public class CiLibWebUploadLimitsProperties
{

    /** upload part size in MB */
    @DataSizeUnit(DataUnit.MEGABYTES)
    private DataSize maxFileSize  = DataSize.ofMegabytes(10);

    /** upload part size in MB */
    @DataSizeUnit(DataUnit.MEGABYTES)
    private DataSize maxBatchSize = DataSize.ofBytes(maxFileSize.toBytes() * 3);

    @DurationFormat(DurationStyle.SIMPLE)
    private Duration maxTtl       = Duration.ofMinutes(10);

    @AssertTrue(message = "maxBatchSize must be greater than or equal to maxFileSize")
    public boolean isBatchSizeValid()
    {
        return maxBatchSize != null
            && maxFileSize != null
            && maxBatchSize.compareTo(maxFileSize) >= 0;
    }

}
