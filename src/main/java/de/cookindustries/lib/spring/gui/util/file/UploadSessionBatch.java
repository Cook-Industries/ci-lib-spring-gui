/**
 * Copyright (c) 2016-2026 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.util.file;

import java.time.Instant;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

@Builder(toBuilder = true)
@Getter
@ToString
@Jacksonized
public class UploadSessionBatch<T>
{

    @NonNull
    private final UUID    uuid;

    private final long    receivedLength;

    @NonNull
    private final Instant createdAt;

    @NonNull
    private final Instant expiresAt;

    private final String  completionHandler;

}
