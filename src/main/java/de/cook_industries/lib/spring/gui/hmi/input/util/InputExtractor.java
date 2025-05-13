package de.cook_industries.lib.spring.gui.hmi.input.util;

import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.util.MultiValueMap;

import de.cook_industries.lib.spring.gui.hmi.input.util.exception.KeyInvalidException;
import de.cook_industries.lib.spring.gui.response.message.HighlightMessage;
import de.cook_industries.lib.spring.gui.response.message.MessageType;
import de.cook_industries.lib.spring.gui.response.message.ModalMessage;
import de.cook_industries.lib.spring.gui.response.message.ResponseMessage;

/**
 * Utility to extract information from Forms to use them in the application
 */
public final class InputExtractor
{

    private static final DateFormat             format       = new SimpleDateFormat("yyyy-mm-dd");

    private final String                        formId;
    private final MultiValueMap<String, String> inputs;
    private final List<ResponseMessage>         messages     = new ArrayList<>();
    private Boolean                             modalMessage = false;

    /**
     * Create a extractor for this
     * 
     * @param inputs to use
     */
    public InputExtractor(MultiValueMap<String, String> inputs)
    {
        this.inputs = inputs;
        formId = inputs.getFirst("__form_id");

        if (formId == null || formId.isBlank())
        {
            throw new IllegalArgumentException("key [__form_id] cannot be null/empty");
        }
    }

    /**
     * Set this extractor messages to be {@link ModalMessage} instead of {@link HighlightMessage}
     */
    public void useModalMessages()
    {
        this.modalMessage = true;
    }

    /**
     * Check whether {@code key} is valid
     * 
     * @param key to check
     * 
     * @throws KeyInvalidException if the key is null or {@link String#isBlank()}
     */
    private void check(String key)
    {
        if (key == null || key.isBlank())
        {
            throw new KeyInvalidException("key cannot be null/empty");
        }
    }

    /**
     * Get a value for a {@code key}. If the {@code key} is not associated with a value, a
     * {@link ResponseMessage} will be added to the internal {@code message} list.
     * 
     * @param key
     * 
     * @return the value as {@code String} of {@code key} if it is associated with a value, or
     *         {@code null} if the key is not set
     * 
     * @throws KeyInvalidException if {@code key} is null/empty
     */
    private String getValue(String key)
    {
        check(key);

        @SuppressWarnings("null")
        String value = inputs.getFirst(key);

        if (value == null)
        {
            String msg = String.format("key value [%s] is null but expected", key);
            addMessage(key, msg, MessageType.ERROR);
        }

        return value;
    }

    /**
     * Extract a {@link String}
     * 
     * @param key to extract
     * @param consumer to feed
     * 
     * @throws KeyInvalidException if {@code key} is null/empty
     */
    public void asString(String key, Consumer<String> consumer)
    {
        asString(key, null, consumer);
    }

    /**
     * Extract a {@link Integer} that conforms to a regex pattern
     * 
     * @param key to extract
     * @param pattern to apply
     * @param consumer to feed
     * 
     * @throws KeyInvalidException if {@code key} is null/empty
     */
    public void asString(String key, String pattern, Consumer<String> consumer)
    {
        try
        {
            String value = getValue(key);

            if (value != null)
            {
                if (pattern != null)
                {
                    Pattern pat = Pattern.compile(pattern);
                    Matcher mat = pat.matcher(value);

                    if (!mat.matches())
                    {
                        String msg = String.format("key [%s] value [%s] does not match pattern [%s]", key, value, pat);
                        addMessage(key, msg, MessageType.ERROR);

                        return;
                    }
                }

                consumer.accept(value);
            }
        }
        catch (Exception ex)
        {
            addUnexpectedErrorMessage(key, ex.getMessage());
        }
    }

    /**
     * Extract a {@link Integer} in a lower and upper bound range
     * 
     * @param key to extract
     * @param consumer to feed
     * 
     * @throws KeyInvalidException if {@code key} is null/empty
     */
    public void asInteger(String key, Consumer<Integer> consumer)
    {
        asInteger(key, null, null, consumer);
    }

    /**
     * Extract a {@link Integer} in a lower and upper bound range
     * 
     * @param key to extract
     * @param lowerBound of valid value (inclusive)
     * @param upperBound of valid value (inclusive)
     * @param consumer to feed
     * 
     * @throws KeyInvalidException if {@code key} is null/empty
     */
    public void asInteger(String key, Integer lowerBound, Integer upperBound, Consumer<Integer> consumer)
    {
        String value = null;
        try
        {
            value = getValue(key);

            if (value != null && !value.isBlank())
            {
                Integer i = Integer.parseInt(value);

                if (lowerBound != null && upperBound != null && (i < lowerBound || i > upperBound))
                {
                    messages.add(HighlightMessage.builder().formId(formId).fieldId(key).type(MessageType.ERROR).build());
                }
                else
                {
                    consumer.accept(i);
                }
            }
        }
        catch (NumberFormatException ex)
        {
            String msg = String.format("key [%s] value [%s] cannot be parsed", key, value);
            addMessage(key, msg, MessageType.ERROR);
        }
        catch (Exception ex)
        {
            addUnexpectedErrorMessage(key, ex.getMessage());
        }
    }

    /**
     * Extract a {@link Date} in the format yyyy-mm-dd
     * 
     * @param key
     * 
     * @throws KeyInvalidException if {@code key} is null/empty
     */
    public void asSqlDate(String key, Consumer<Date> consumer)
    {
        String value = null;

        try
        {
            value = getValue(key);
            if (value != null)
            {
                java.util.Date date    = format.parse(value);

                Date           sqlDate = new java.sql.Date(date.getTime());

                consumer.accept(sqlDate);
            }
        }
        catch (ParseException ex)
        {
            String msg = String.format("key [%s] value [%s] is not in format yyyy-mm-dd", key, value);
            addMessage(key, msg, MessageType.ERROR);
        }
        catch (Exception ex)
        {
            addUnexpectedErrorMessage(key, ex.getMessage());
        }
    }

    /**
     * Extract a {@link Enum}
     * 
     * @param <E> enum class
     * @param key to extract
     * @param source of enums
     * @param consumer to feed
     * 
     * @throws KeyInvalidException if {@code key} is null/empty
     */
    public <E extends Enum<E>> void asEnum(String key, Class<E> source, Consumer<E> consumer)
    {
        try
        {
            String value = getValue(key);

            if (value != null)
            {
                E e = Arrays.stream(source.getEnumConstants()).filter(t -> value.equals(t.name())).findFirst().get();

                consumer.accept(e);
            }
        }
        catch (NoSuchElementException ex)
        {
            String msg = String.format("key [%s] is not part of enum [%s]", key, source.getSimpleName());
            addMessage(key, msg, MessageType.ERROR);
        }
        catch (Exception ex)
        {
            addUnexpectedErrorMessage(key, ex.getMessage());
        }
    }

    private void addUnexpectedErrorMessage(String key, String errorMsg)
    {
        addMessage(key, String.format("key [%s] unexpected error [%s]", key, errorMsg), MessageType.ERROR);
    }

    private void addMessage(String key, String msg, MessageType type)
    {
        if (modalMessage)
        {
            messages.add(ModalMessage.builder().msg(msg).type(type).build());
        }
        else
        {
            messages.add(HighlightMessage.builder().formId(formId).fieldId(key).type(type).build());
        }
    }

    /**
     * Check whether this extractor has raised any messages
     * 
     * @return true, if there are messages raise, false otherwise
     */
    public Boolean hasMessages()
    {
        return !messages.isEmpty();
    }

    /**
     * Get e list of any messages raised by the transformations
     * 
     * @return list of messages
     */
    public List<ResponseMessage> getMessages()
    {
        return Collections.unmodifiableList(messages);
    }
}
