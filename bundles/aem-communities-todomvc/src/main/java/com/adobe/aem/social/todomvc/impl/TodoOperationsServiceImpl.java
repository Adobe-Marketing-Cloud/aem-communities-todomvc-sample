package com.adobe.aem.social.todomvc.impl;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.jcr.Session;

import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.aem.social.todomvc.api.TodoItem;
import com.adobe.aem.social.todomvc.api.TodoOperationExtension;
import com.adobe.aem.social.todomvc.api.TodoOperationTypes;
import com.adobe.aem.social.todomvc.api.TodoOperations;
import com.adobe.cq.social.scf.OperationException;
import com.adobe.cq.social.scf.core.operations.AbstractOperationService;

@Component(immediate = true)
@Service
public class TodoOperationsServiceImpl extends
    AbstractOperationService<TodoOperationExtension, TodoOperationTypes, TodoItem> implements TodoOperations {

    private static final Logger LOG = LoggerFactory.getLogger(TodoOperationsServiceImpl.class);

    public Resource create(SlingHttpServletRequest request) throws OperationException {
        // TODO Auto-generated method stub
        return null;
    }

    public Resource create(Resource todolist, String owner, String text, String date, ResourceResolver resolver)
    // TODO Auto-generated method stub
    return null;
    }

    public void delete(SlingHttpServletRequest request) throws OperationException {
        // TODO Auto-generated method stub

    }

    public void delete(Resource todoItem) throws OperationException {
        // TODO Auto-generated method stub

    }

    public Resource updateStatus(SlingHttpServletRequest request) throws OperationException {
        // TODO Auto-generated method stub
        return null;
    }

    public Resource updateStatus(Resource todoItem, boolean isDone) throws OperationException {
        // TODO Auto-generated method stub
        return null;
    }

}
