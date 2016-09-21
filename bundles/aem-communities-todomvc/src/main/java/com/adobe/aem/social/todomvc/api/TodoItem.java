package com.adobe.aem.social.todomvc.api;

import com.adobe.cq.social.scf.SocialComponent;

/**
 * A Social Component that represents a single todo item
 */
public interface TodoItem extends SocialComponent {

    /**
     * @return the text of the todo item
     */
    String getItemText();

    /**
     * @return true if the item is still pending, false if the item has been completed
     */
    boolean isActive();
}
