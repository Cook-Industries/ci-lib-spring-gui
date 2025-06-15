/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * 
 * Licensed under the MIT License.
 * See LICENSE file in the project root for full license information.
 * 
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
package de.cookindustries.lib.spring.gui.hmi.input.util;

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

import de.cookindustries.lib.spring.gui.hmi.input.util.exception.InvalidInputKeyException;
import de.cookindustries.lib.spring.gui.response.message.HighlightMessage;
import de.cookindustries.lib.spring.gui.response.message.MessageType;
import de.cookindustries.lib.spring.gui.response.message.ModalMessage;
import de.cookindustries.lib.spring.gui.response.message.ResponseMessage;

/**
 * Utility to extract information from form data
 */
public final class InputExtractor
{

    private static final DateFormat             DATE_FORMAT = new SimpleDateFormat("yyyy-mm-dd");

    private final String                        formId;
    private final MultiValueMap<String, String> inputs;
    private final List<ResponseMessage>         messages    = new ArrayList<>();
    private final Boolean                       useModalMessages;

    /**
     * Create a extractor for this
     * <p>
     * It is assumed that the {@code key} '__form_id' exists inside {@code inputs} and is non-null.
     * 
     * @param inputs to use
     * @throws IllegalArgumentException if key '__form_id' id {@code null} or empty
     */
    public InputExtractor(MultiValueMap<String, String> inputs)
    {
        this(inputs, false);
    }

    /**
     * Create a extractor for this
     * <p>
     * It is assumed that the {@code key} '__form_id' exists inside {@code inputs} and is non-null.
     * 
     * @param inputs to use
     * @throws IllegalArgumentException if key '__form_id' id {@code null} or empty
     */
    public InputExtractor(MultiValueMap<String, String> inputs, Boolean useModalMessages)
    {
        this.inputs = inputs;
        this.useModalMessages = useModalMessages;

        formId = inputs.getFirst("__form_id");

        if (formId == null || formId.isBlank())
        {
            throw new IllegalArgumentException("key [__form_id] cannot be null/empty");
        }
    }

    /**
     * Check whether {@code key} is valid
     * 
     * @param key to check
     * @return {@code this} for chaining
     * @throws InvalidInputKeyException if the key is {@code null} or empty
     */
    private void check(String key)
    {
        if (key == null || key.isBlank())
        {
            throw new InvalidInputKeyException("key cannot be null/empty");
        }
    }

    /**
     * Get a value for a {@code key}
     * 
     * @param key to look-up
     * @return the value as {@code String} associated with {@code key}
     * @throws InvalidInputKeyException if {@code key} is {@code null} or emtpy
     */
    private String getValue(String key)
    {
        check(key);

        String value = inputs.getFirst(key);

        return value;
    }

    /**
     * Check if the {@code key} is associated with a value, if not add a {@link ResponseMessage} to the internal {@code message} list
     * 
     * @param key to associate
     * @param value to check
     */
    private void checkValue(String key, String value)
    {
        if (value == null)
        {
            String msg = String.format("value retrival to key [%s] is expected but returned null", key);
            addMessage(key, msg, MessageType.ERROR);
        }
    }

    /**
     * Consume a {@link String}
     * <p>
     * The {@code consumer} will only be triggered if the value associated with {@code key} is non-null.
     * 
     * @param key to look-up
     * @param consumer to feed
     * @return {@code this} for chaining
     * @throws InvalidInputKeyException if {@code key} is null/empty
     */
    public InputExtractor consumeString(String key, Consumer<String> consumer)
    {
        consumeString(key, null, consumer);

        return this;
    }

    /**
     * Consume a {@link String} that conforms to a regex pattern
     * <p>
     * The {@code consumer} will only be triggered if the value associated with {@code key} is non-null and matches {@code pattern}.
     * 
     * @param key to look-up
     * @param pattern to apply
     * @param consumer to feed
     * @return {@code this} for chaining
     * @throws InvalidInputKeyException if {@code key} is null/empty
     */
    public InputExtractor consumeString(String key, String pattern, Consumer<String> consumer)
    {
        try
        {
            String value = getValue(key);

            checkValue(key, value);

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

                        return this;
                    }
                }

                consumer.accept(value);
            }
        }
        catch (Exception ex)
        {
            addUnexpectedErrorMessage(key, ex.getMessage());
        }

        return this;
    }

    /**
     * Consume a {@link Integer}
     * <p>
     * The {@code consumer} will only be triggered if the value associated with {@code key} is non-null.
     * 
     * @param key to look-up
     * @param consumer to feed
     * @return {@code this} for chaining
     * @throws InvalidInputKeyException if {@code key} is null/empty
     */
    public InputExtractor consumeInteger(String key, Consumer<Integer> consumer)
    {
        consumeInteger(key, null, null, consumer);

        return this;
    }

    /**
     * Extract a {@link Integer} conforming to {@code lower}(inclusive) &gt;= value &lt;= {@code upper}(inclusive) bound range
     * <p>
     * The {@code consumer} will only be triggered if the value associated with {@code key} is non-null.
     * 
     * @param key to look-up
     * @param lowerBound of valid value (inclusive)
     * @param upperBound of valid value (inclusive)
     * @param consumer to feed
     * @throws InvalidInputKeyException if {@code key} is null/empty
     */
    public InputExtractor consumeInteger(String key, Integer lowerBound, Integer upperBound, Consumer<Integer> consumer)
    {
        String value = null;

        try
        {
            value = getValue(key);

            checkValue(key, value);

            if (value != null && !value.isBlank())
            {
                Integer i = Integer.parseInt(value);

                if (lowerBound != null && upperBound != null && i >= lowerBound && i <= upperBound)
                {
                    consumer.accept(i);
                }
                else
                {
                    String msg = String.format("key [%s] with value [%s] is out of range of [%s]/[%s]", key, value, lowerBound, upperBound);
                    addMessage(key, msg, MessageType.ERROR);
                }
            }
        }
        catch (NumberFormatException ex)
        {
            String msg = String.format("key [%s] value [%s] cannot be parsed as [Integer]", key, value);
            addMessage(key, msg, MessageType.ERROR);
        }
        catch (Exception ex)
        {
            addUnexpectedErrorMessage(key, ex.getMessage());
        }

        return this;
    }

    /**
     * Extract a {@link Date} in the format yyyy-mm-dd
     * 
     * @param key to look-up
     * @throws InvalidInputKeyException if {@code key} is null/empty
     */
    public InputExtractor consumeDate(String key, Consumer<Date> consumer)
    {
        String value = null;

        try
        {
            value = getValue(key);

            checkValue(key, value);

            if (value != null)
            {
                java.util.Date date    = DATE_FORMAT.parse(value);

                Date           sqlDate = new java.sql.Date(date.getTime());

                consumer.accept(sqlDate);
            }
        }
        catch (ParseException ex)
        {
            String msg = String.format("key [%s] value [%s] cannot be parsed as [SqlDate] in format yyyy-mm-dd", key, value);
            addMessage(key, msg, MessageType.ERROR);
        }
        catch (Exception ex)
        {
            addUnexpectedErrorMessage(key, ex.getMessage());
        }

        return this;
    }

    /**
     * Extract a {@link Enum}
     * 
     * @param <E> enum class
     * @param key to look-up
     * @param source of enums
     * @param consumer to feed
     * @throws InvalidInputKeyException if {@code key} is null/empty
     */
    public <E extends Enum<E>> InputExtractor consumeEnum(String key, Class<E> source, Consumer<E> consumer)
    {
        try
        {
            String value = getValue(key);

            checkValue(key, value);

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

        return this;
    }

    /**
     * Add a message for an unexpected exception case
     * 
     * @param key which prompted the message
     * @param errorMsg to add
     */
    private void addUnexpectedErrorMessage(String key, String errorMsg)
    {
        addMessage(key, String.format("key [%s] resulted in unexpected error [%s]", key, errorMsg), MessageType.ERROR);
    }

    /**
     * Add a message to the internal list
     * 
     * @param key which prompted the message
     * @param msg to add
     * @param type that was tried to consume
     */
    private void addMessage(String key, String msg, MessageType type)
    {
        if (useModalMessages)
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
     * Get a unmodifiable list of {@link ResponseMessage}s raised by the consumations
     * 
     * @return list of messages
     */
    public List<ResponseMessage> getMessages()
    {
        return Collections.unmodifiableList(messages);
    }
}
