/**
 * Copyright (c) 2016-2026 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.util.file;

import java.util.List;

import de.cookindustries.lib.spring.gui.response.Response;
import jakarta.validation.constraints.NotNull;

/**
 * Runs once per upload, after all files are in. Registered by {@link #completeKey()}.
 *
 * @param <D> the form data POJO
 * @param <R> the result of the completion
 */
public interface CompletionHandler<D, R extends Response>
{

    /**
     * Defines the key under which name this handler will be registered in the {@link FileUploadCoordinator}.
     * 
     * @return a unique key for this handler
     */
    String completeKey();

    Class<D> formType();

    /**
     * @return the result handed back to the caller of {@link FileUploadCoordinator#complete}
     */
    R onComplete(@NotNull D formData, List<UploadedFile> files) throws Exception;

}
