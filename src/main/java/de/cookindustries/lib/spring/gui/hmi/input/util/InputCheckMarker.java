package de.cookindustries.lib.spring.gui.hmi.input.util;

import de.cookindustries.lib.spring.gui.hmi.input.marker.MarkerCategory;
import de.cookindustries.lib.spring.gui.hmi.input.marker.MarkerType;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Builder
@Getter
public class InputCheckMarker
{

    @NonNull
    private final String         formId;

    @NonNull
    private final String         transferId;

    @NonNull
    private final MarkerCategory category;

    @NonNull
    private final MarkerType     type;

}
