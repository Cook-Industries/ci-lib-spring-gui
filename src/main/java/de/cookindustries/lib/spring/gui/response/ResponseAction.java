/**
 * Copyright (c) 2016-2025 sebastian koch/Cook Industries.
 * <p>
 * Licensed under the MIT License.
 * <p>
 * See LICENSE file in the project root for full license information.
 */
package de.cookindustries.lib.spring.gui.response;

import de.cookindustries.lib.spring.gui.hmi.container.Container;
import de.cookindustries.lib.spring.gui.hmi.modal.Modal;

/**
 * A fixed set of {@code Action}s that can be performed by the GUI
 * 
 * @since 1.0.0
 * @author <a href="mailto:development@cook-industries.de">sebastian koch</a>
 */
public enum ResponseAction {

    /** show a notification */
    NOTIFICATION,

    /** parse and show a {@link Modal} as the top-most */
    MODAL,

    /** hide the top-most open {@link Modal} */
    MODAL_HIDE,

    /** parse and either replace/append a {@link Container} */
    CONTENT,

    /** remove a {@link Container} */
    REMOVE,

    /** TODO: what does this? */
    TAB_OPEN,

    /** TODO: what does this? */
    TAB_CLOSE,

    /** a compund set of several {@link ResponseAction}s */
    COMPOUND,

    /** a implementer defined call to a function */
    CUSTOM;
}
