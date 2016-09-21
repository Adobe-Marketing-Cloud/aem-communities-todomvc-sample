package com.adobe.aem.social.todomvc.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.sling.api.resource.Resource;

import com.adobe.aem.social.todomvc.api.TodoList;
import com.adobe.cq.social.scf.ClientUtilities;
import com.adobe.cq.social.scf.CollectionPagination;
import com.adobe.cq.social.scf.SocialComponent;
import com.adobe.cq.social.scf.SocialComponentFactory;
import com.adobe.cq.social.scf.core.BaseSocialComponent;
import com.adobe.cq.social.scf.core.CollectionSortedOrder;
import com.adobe.cq.social.srp.SocialResourceProvider;
import com.adobe.cq.social.ugcbase.SocialUtils;

public class TodoListImpl extends BaseSocialComponent implements TodoList {

    final String currentUser;
    final String pathToUserTodos;
    final SocialResourceProvider srp;
    final Resource todoListComponent;
    final Resource usersList;
    final ClientUtilities clientUtils;

    public TodoListImpl(final Resource resource, final ClientUtilities clientUtils, final SocialUtils socialUtils,
        final SocialResourceProvider srp) {
        super(resource, clientUtils);
        currentUser = clientUtils.getAuthorizedUserId();
        pathToUserTodos = resource.getPath() + "/" + currentUser;
        this.srp = srp;
        this.todoListComponent = resource;
        this.usersList = resource.getResourceResolver().resolve(pathToUserTodos);
        this.clientUtils = clientUtils;
    }

    @Override
    public List<Object> getItems() {
        Iterator<Resource> items = srp.listChildren(usersList);
        List<Object> todoItems = new ArrayList<Object>();
        while (items.hasNext()) {
            final Resource item = items.next();
            todoItems.add(getTodoItemSocialComponent(item));
        }
        return todoItems;
    }

    private SocialComponent getTodoItemSocialComponent(final Resource item) {
        final SocialComponentFactory factory =
            this.clientUtils.getSocialComponentFactoryManager().getSocialComponentFactory(item);
        return factory.getSocialComponent(item);
    }

    @Override
    public int getTotalSize() {
        return Math.toIntExact(srp.countChildren(usersList));
    }

    @Override
    public void setPagination(CollectionPagination arg0) {
        // no-op

    }

    @Override
    public void setSortedOrder(CollectionSortedOrder arg0) {
        // no-op
    }

}
