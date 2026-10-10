package de.cookindustries.lib.spring.gui.util.file;

import java.util.UUID;

/**
 * A file of a finished upload. This is what a completion handler works with.
 *
 * @param fileUuid the id under which the receiver holds the data
 * @param fieldId id of the form field the file was selected in
 * @param filename the name the client announced
 * @param length size in bytes
 */
public record UploadedFile(UUID fileUuid, String fieldId, String filename, long length)
{
}
