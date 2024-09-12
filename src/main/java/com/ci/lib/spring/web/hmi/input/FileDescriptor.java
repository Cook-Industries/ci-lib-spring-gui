package com.ci.lib.spring.web.hmi.input;

import java.nio.charset.StandardCharsets;

import com.ci.lib.spring.web.hmi.input.exception.NoFileExtensionException;
import com.ci.lib.spring.web.hmi.input.exception.NoFileNameException;

import lombok.Data;

@Data
public class FileDescriptor
{

    private String fileName;
    private String fileType;
    private String fileContent;

    /**
     * Get the file name without extension
     * 
     * @return the filename
     * 
     * @throws NoFileNameException if name is empty/null
     */
    public String getFileName()
    {
        if (fileName == null || fileName.isBlank())
        {
            throw new NoFileNameException();
        }

        return fileName.substring(0, fileName.lastIndexOf("."));
    }

    /**
     * Tries to extract the file extension.<br>
     * First by parsing the complete file name and break it at '.' and using the last part behind it
     * 
     * @return the file extension without '.'
     * 
     * @throws NoFileExtensionException if the file extension cannot be extracted
     */
    public String getFileExtension()
    {
        String  ext     = "";
        Integer lastDot = fileName.lastIndexOf(".");

        if (lastDot > -1)
        {
            ext = fileName.substring(lastDot + 1);

            if (ext.isBlank())
            {
                throw new NoFileExtensionException();
            }
        }

        return ext;
    }

    /**
     * Get the full filename with extension
     * 
     * @return the full filename
     */
    public String getFullName()
    {
        return fileName;
    }

    /**
     * Get the raw data of this file
     * 
     * @return the content of this file
     */
    public byte[] getFileContent()
    {
        return fileContent.substring(fileContent.lastIndexOf(",") + 1).getBytes(StandardCharsets.UTF_8);
    }

}
