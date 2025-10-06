/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.response;

import de.cookindustries.lib.spring.gui.hmi.container.Container;
import de.cookindustries.lib.spring.gui.hmi.container.ModalContainer;

/**
 * A fixed set of {@code Action}s that can be performed by the GUI
 * 
 * @since 1.0.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
public enum ResponseAction {

    /** show a notification */
    NOTIFICATION,

    /** parse and show a {@link ModalContainer} as the top-most */
    MODAL,

    /** parse and either replace/append a {@link Container} */
    CONTENT,

    /** remove a {@link Container} */
    REMOVE,

    /** a compund set of several {@link ResponseAction}s */
    COMPOUND,

    /** update progress on the global loader */
    PROGRESS,

    /** send generated data back to the gui */
    DATA,

    FETCH_TAGS,

    FETCH_TAGS_RESULT,

    /** initiate a redirect on the client */
    REDIRECT,

    /** send dynamic properties to the ui */
    PROPERTIES;
}
