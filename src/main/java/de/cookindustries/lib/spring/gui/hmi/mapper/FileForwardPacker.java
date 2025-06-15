/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.hmi.mapper;

import java.io.IOException;

import org.springframework.core.io.InputStreamResource;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

/**
 * @since 1.0.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
public class FileForwardPacker
{

    @SuppressWarnings("null")
    public static final MultiValueMap<String, Object> pack(MultiValueMap<String, String> valueMap, MultipartFile[] files) throws IOException
    {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

        for (String s : valueMap.keySet())
        {
            valueMap.get(s).stream().forEach(t -> body.add(s, t));
        }

        for (MultipartFile file : files)
        {
            InputStreamResource fileAsResource = new InputStreamResource(file.getInputStream()) {

                @Override
                public long contentLength()
                {
                    try
                    {
                        return file.getSize();
                    }
                    catch (Exception e)
                    {
                        return -1;
                    }
                }

                @Override
                public String getFilename()
                {
                    return file.getOriginalFilename();
                }
            };

            body.add("files", fileAsResource);
        }

        return body;
    }
}
