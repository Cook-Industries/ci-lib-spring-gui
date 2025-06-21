/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.response;

import de.cookindustries.lib.spring.gui.hmi.input.Input;
import de.cookindustries.lib.spring.gui.hmi.input.marker.Marker;
import de.cookindustries.lib.spring.gui.response.message.ActivateMarkerMessage;
import de.cookindustries.lib.spring.gui.response.message.ModalMessage;
import de.cookindustries.lib.spring.gui.response.message.PopupMessage;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

/**
 * A {@code notification} response to send information about API calls back to the GUI.
 * <p>
 * This object is an aggregator since the type of message and their behaviour is defined by the message to send.
 * <p>
 * Supported types:
 * <ul>
 * <li>{@link ActivateMarkerMessage} that links to a {@link Marker} on a {@link Input}</li>
 * <li>{@link ModalMessage} that will trigger a permanent modal frame that needs active user interaction to close it</li>
 * <li>{@link PopupMessage} that will show up in the designated pop-up region and will automatically disapear after a set time frame</li>
 * </ul>
 * 
 * @since 1.0.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
@SuperBuilder
@Getter
@Jacksonized
public class NotificationResponse extends Response
{

    @Override
    protected ResponseAction inferType()
    {
        return ResponseAction.NOTIFICATION;
    }

}
