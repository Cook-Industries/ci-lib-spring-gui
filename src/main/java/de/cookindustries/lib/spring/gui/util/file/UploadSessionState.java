/**
 * Copyright (c) 2016-2026 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.util.file;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Singular;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

@Builder(toBuilder = true)
@Getter
@ToString
@Jacksonized
public final class UploadSessionState
{

    @NonNull
    private final UUID               uuid;

    @NonNull
    private final UUID               batchUuid;

    @NonNull
    private final String             fieldId;

    @NonNull
    private final String             filename;

    @NonNull
    private final Instant            created;

    private final long               expectedLength;

    private final long               submittedLength;

    private final boolean            finished;

    @Singular
    private final List<UploadedPart> parts;

}
