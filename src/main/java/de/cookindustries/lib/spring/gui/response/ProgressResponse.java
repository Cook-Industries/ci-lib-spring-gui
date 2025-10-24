package de.cookindustries.lib.spring.gui.response;

import io.micrometer.common.lang.NonNull;
import lombok.Getter;
import lombok.ToString;
import lombok.Builder.Default;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

/**
 * @since 3.4.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
@SuperBuilder
@Getter
@Jacksonized
@ToString
public final class ProgressResponse extends Response
{

    /** the {@code id} of the progress element */
    @NonNull
    private final String  elementId;

    @NonNull
    @Default
    private final String  text     = "loading";

    /** percent value between 0-100 */
    @NonNull
    @Default
    private final Integer progress = 0;

    @Override
    protected ResponseAction inferType()
    {
        return ResponseAction.PROGRESS;
    }

}
