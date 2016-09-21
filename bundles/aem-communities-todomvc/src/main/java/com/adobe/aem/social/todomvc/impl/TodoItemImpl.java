package com.adobe.aem.social.todomvc.impl;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;

import com.adobe.aem.social.todomvc.api.TodoItem;
import com.adobe.cq.social.scf.ClientUtilities;
import com.adobe.cq.social.scf.core.BaseSocialComponent;

public class TodoItemImpl extends BaseSocialComponent implements TodoItem {

    private final ValueMap itemProps;

    public TodoItemImpl(Resource resource, ClientUtilities clientUtils) {
        super(resource, clientUtils);
        itemProps = resource.adaptTo(ValueMap.class);
    }

    @Override
    public String getItemText() {
        return itemProps.get("jcr:description", "");
    }

    @Override
    public boolean isActive() {
        return !itemProps.get("isDone_b", false);
    }
}
