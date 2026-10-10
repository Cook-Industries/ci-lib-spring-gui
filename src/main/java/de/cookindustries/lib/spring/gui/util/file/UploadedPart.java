package de.cookindustries.lib.spring.gui.util.file;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

@Builder
@Getter
@ToString
@Jacksonized
public final class UploadedPart
{

    private final int  partNumber;

    private final long size;

}
