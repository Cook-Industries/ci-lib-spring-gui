/**
 * Copyright (c) 2016-2026 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.config.properties;

import java.time.Duration;

import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.boot.convert.DataSizeUnit;
import org.springframework.boot.convert.DurationFormat;
import org.springframework.boot.convert.DurationStyle;
import org.springframework.util.unit.DataSize;
import org.springframework.util.unit.DataUnit;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Path properties in relation to web interfaces.
 * 
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
@Data
public class CiLibWebUploadProperties
{

    @NotBlank
    private String                         url;

    /** upload part size in MB */
    @DataSizeUnit(DataUnit.MEGABYTES)
    private DataSize                       partSize           = DataSize.ofMegabytes(5);

    /** maximum number of parallel uploads of parts on a single file */
    @Min(value = 1, message = "number cannot be negative")
    private int                            maxPerFileParallel = 5;

    /** maximum number of parallel uploads of parts on the client */
    @Min(value = 1, message = "number cannot be negative")
    private int                            maxGlobalParallel  = 10;

    private int                            maxRetries         = 3;

    @DurationFormat(DurationStyle.SIMPLE)
    private Duration                       retryBaseDelay     = Duration.ofSeconds(3);

    @DurationFormat(DurationStyle.SIMPLE)
    private Duration                       retryMaxDelay      = Duration.ofSeconds(10);

    @Valid
    @NestedConfigurationProperty
    private CiLibWebUploadLimitsProperties limits             = new CiLibWebUploadLimitsProperties();

}
