/**
 * Copyright (c) 2016-2026 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.response;

import java.util.List;
import java.util.UUID;

import de.cookindustries.lib.spring.gui.util.file.UploadSessionLimits;
import de.cookindustries.lib.spring.gui.util.file.UploadSessionPolicy;
import de.cookindustries.lib.spring.gui.util.file.UploadSessionState;
import lombok.Getter;
import lombok.NonNull;
import lombok.Singular;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

@SuperBuilder
@Getter
@Jacksonized
@ToString
public final class StartFileUploadResponse extends Response
{

    @NonNull
    private final UUID                     batchUuid;

    @NonNull
    private final String                   uploadUrl;

    @NonNull
    private final UploadSessionPolicy      uploadPolicy;

    @NonNull
    private final UploadSessionLimits      uploadLimits;

    @Singular("fieldDescribtor")
    private final List<UploadSessionState> fieldDescribtors;

    @Override
    protected ResponseAction inferType()
    {
        return ResponseAction.FILE_UPLOAD;
    }

}
