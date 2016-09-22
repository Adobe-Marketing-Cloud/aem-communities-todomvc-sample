package com.adobe.aem.social.todomvc.api;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;

import com.adobe.cq.social.scf.OperationException;

/**
 * provides all operations needed to manage a users todo list
 */
public interface TodoOperations {
    Resource create(final SlingHttpServletRequest request) throws OperationException;

    Resource create(final Resource todolist, final String owner, final String text, String date,
        final ResourceResolver resolver) throws OperationException;

    void delete(final SlingHttpServletRequest request) throws OperationException;

    void delete(final Resource todoItem) throws OperationException;

    Resource updateStatus(final SlingHttpServletRequest request) throws OperationException;

    Resource updateStatus(final Resource todoItem, boolean isDone, String user) throws OperationException;
}
