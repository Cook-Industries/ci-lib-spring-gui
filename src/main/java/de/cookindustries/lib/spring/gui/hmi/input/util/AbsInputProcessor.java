/**
 * Copyright (c) 2016-2026 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.hmi.input.util;

import java.util.Optional;

import de.cookindustries.lib.spring.gui.hmi.input.util.exception.NullIgnoreException;
import lombok.Getter;
import lombok.NonNull;
import lombok.Builder.Default;
import lombok.experimental.SuperBuilder;

/**
 * A abstract processor to parse and check a {@link String} {@code input} to a generic type {@code T}.
 * <p>
 * Objects of this class are considered final and stateless, so that it can parse multiple instances of inputs, without the need for a
 * creation of the allways equal object.
 * 
 * @since 2.4.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
@SuperBuilder
@Getter
public abstract class AbsInputProcessor<T>
{

    @NonNull
    @Default
    private final Boolean ignoreNull = false;

    /**
     * Create an {@link InputCheckResult} with a {@code type} and a empty result object.
     * 
     * @param type of the result
     * @return a empty {@code InputCheckResult}
     */
    protected final InputCheckResult<T> createEmptyResult(InputCheckResultType type)
    {
        return createResult(type, null);
    }

    /**
     * Create an {@link InputCheckResult} with a {@code type} and a {@code result} object.
     * 
     * @param type of the result
     * @param result object to include
     * @return a {@code InputCheckResult}
     */
    protected final InputCheckResult<T> createResult(InputCheckResultType type, T result)
    {
        T endResult = prepare(result);

        return InputCheckResult
            .<T>builder()
            .type(type)
            .result(Optional.ofNullable(endResult))
            .build();
    }

    /**
     * Parse the raw {@link String} {@code input} value from the {@code UI form} to {@code T}.
     * <p>
     * Variable {@code input} is never {@code null};
     * 
     * @param input to transfrom to the designated type
     * @return the transformed {@code input}
     */
    protected abstract T parseRaw(String input);

    /**
     * Check function to test the {@code input} against type specific constraints.
     * <p>
     * Use
     * <ul>
     * <li>{@link #createEmptyResult(InputCheckResultType)} for failures</li>
     * <li>{@link #createResult(InputCheckResultType, Object)} for a passes</li>
     * </ul>
     * 
     * @param input to check
     * @return a {@code InputCheckResult} containing the result type and a optional result object
     */
    protected abstract InputCheckResult<T> check(T input);

    /**
     * Make final preparations to the {@code #check(Object)} result {@code output} before calling
     * {@code #createResult(InputCheckResultType, Object)} the endresult.
     * <p>
     * The default implementation does nothing and simply returns the input as-is.
     * 
     * @param output to transform
     * @return the transformed {@code output}
     */
    protected T prepare(T output)
    {
        return output;
    }

    /**
     * Check the given {@code String} from the {@code UI form}.
     * <p>
     * This function has 3 steps:
     * <ol>
     * <li>check if the {@code input} is {@code null}. If so, return with an empty {@link InputCheckResult} set to
     * {@link InputCheckResultType#NOT_PRESENT}.</li>
     * <li>call {@link #parseRaw(String)} with {@code input}. If this throws an {@code Exception}, return with an empty
     * {@link InputCheckResult} set to {@link InputCheckResultType#NOT_PARSABLE}.</li>
     * <li>call {@link #check(Object)} and return the appropriate {@link InputCheckResult}.</li>
     * </ol>
     * 
     * @param input to process
     * @return a {@code InputCheckResult} representing the result of the processing
     */
    final InputCheckResult<T> process(String input)
    {
        if (input == null)
        {
            if (ignoreNull)
            {
                throw new NullIgnoreException();
            }

            return createEmptyResult(InputCheckResultType.NOT_PRESENT);
        }

        try
        {
            T value = parseRaw(input);

            return check(value);
        }
        catch (Exception ex)
        {
            return createEmptyResult(InputCheckResultType.NOT_PARSABLE);
        }
    }

}
