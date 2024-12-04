package com.ci.lib.spring.web.hmi.input;

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

import com.ci.lib.spring.web.response.message.HighlightMessage;
import com.ci.lib.spring.web.response.message.MessageType;
import com.ci.lib.spring.web.response.message.ModalMessage;
import com.ci.lib.spring.web.response.message.ResponseMessage;

public final class InputExtractor
{

    private static final DateFormat             format   = new SimpleDateFormat("yyyy-mm-dd");

    private final String                        formId;
    private final MultiValueMap<String, String> inputs;
    private final List<ResponseMessage>         messages = new ArrayList<>();

    public InputExtractor(MultiValueMap<String, String> inputs)
    {
        this.inputs = inputs;
        formId = inputs.getFirst("__form_id");
    }

    private void checkKey(String key)
    {
        if (key == null)
        {
            // TODO: better exception
            throw new IllegalArgumentException("key cannot be null");
        }
    }

    private String getValue(String key)
    {
        checkKey(key);

        @SuppressWarnings("null")
        String value = inputs.getFirst(key);

        if (value == null)
        {
            messages
                    .add(ModalMessage
                            .builder()
                            .msg(String.format("key value [%s] is null but expected", key))
                            .type(MessageType.ERROR)
                            .build());
        }

        return value;
    }

    public void asString(String key, Consumer<String> consumer)
    {
        asString(key, null, consumer);
    }

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
                        messages.add(HighlightMessage.builder().formId(formId).fieldId(key).type(MessageType.ERROR).build());

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

    public void asInteger(String key, Consumer<Integer> consumer)
    {
        asInteger(key, null, null, consumer);
    }

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
            messages
                    .add(ModalMessage
                            .builder()
                            .msg(String.format("key [%s] value [%s] cannot be parsed", key, value))
                            .type(MessageType.ERROR)
                            .build());
        }
        catch (Exception ex)
        {
            addUnexpectedErrorMessage(key, ex.getMessage());
        }
    }

    /**
     * Extract a String in the format yyyy-mm-dd to a {@link Date} object.
     * 
     * @param key
     * 
     * @return
     * 
     * @throws ParseException
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
            messages
                    .add(ModalMessage
                            .builder()
                            .msg(String.format("key [%s] value [%s] is not in format yyyy-mm-dd", key, value))
                            .type(MessageType.ERROR)
                            .build());
        }
        catch (Exception ex)
        {
            addUnexpectedErrorMessage(key, ex.getMessage());
        }
    }

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
            messages
                    .add(ModalMessage
                            .builder()
                            .msg(String.format("key [%s] is not part of enum [%s]", key, source.getSimpleName()))
                            .type(MessageType.ERROR)
                            .build());
        }
        catch (Exception ex)
        {
            addUnexpectedErrorMessage(key, ex.getMessage());
        }
    }

    private void addUnexpectedErrorMessage(String key, String errorMsg)
    {
        messages
                .add(ModalMessage
                        .builder()
                        .msg(String.format("key [%s] unexpected error [%s]", key, errorMsg))
                        .type(MessageType.ERROR)
                        .build());
    }

    public Boolean hasMessages()
    {
        return !messages.isEmpty();
    }

    public List<ResponseMessage> getMessages()
    {
        return Collections.unmodifiableList(messages);
    }
}
