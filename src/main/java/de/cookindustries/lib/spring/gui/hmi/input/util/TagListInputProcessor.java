package de.cookindustries.lib.spring.gui.hmi.input.util;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Builder.Default;

/**
 * A processor to parse and check a {@code input} as {@link Boolean}.
 * 
 * @since 2.4.0 *
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
@Builder
@Getter
public final class TagListInputProcessor extends AbsInputProcessor<TagList>
{

    private static final ObjectMapper TAG_LIST_MAPPER = new ObjectMapper();

    /** Whether or not an empty {@code input} is allowed */
    @Default
    @NonNull
    private final Boolean             allowEmpty      = true;

    /** Default value if the {@code input} is empty */
    @Default
    private final TagList             fallback        = new TagList();

    /** Whitelist value if the {@code input} is empty */
    @Default
    private final TagList             whitelist       = null;

    @Override
    protected TagList parseRaw(String input)
    {
        if (input.isEmpty() && allowEmpty)
        {
            return fallback;
        }

        try
        {
            return TAG_LIST_MAPPER.readValue(input, TagList.class);
        }
        catch (Exception ex)
        {
            throw new IllegalArgumentException();
        }
    }

    @Override
    protected InputCheckResult<TagList> check(TagList input)
    {
        TagList tagList = new TagList();

        if (whitelist != null)
        {
            List<Tag> tags = input.stream().filter(whitelist::contains).toList();

            tagList.addAll(tags);
        }
        else
        {
            tagList.addAll(input);
        }

        return createResult(InputCheckResultType.PASS, tagList);
    }

}
