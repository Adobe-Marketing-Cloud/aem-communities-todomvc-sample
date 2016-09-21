package com.adobe.aem.social.todomvc.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.jcr.Session;

import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.PersistenceException;
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
import com.adobe.cq.social.srp.SocialResourceProvider;
import com.adobe.cq.social.ugcbase.CollabUser;
import com.adobe.cq.social.ugcbase.SocialUtils;

@Component(immediate = true)
@Service
public class TodoOperationsServiceImpl extends
    AbstractOperationService<TodoOperationExtension, TodoOperationTypes, TodoItem> implements TodoOperations {

    private static final Logger LOG = LoggerFactory.getLogger(TodoOperationsServiceImpl.class);

    @Reference
    private SocialUtils socialUtils;

    public Resource create(SlingHttpServletRequest request) throws OperationException {
        final Resource todolist = request.getResource();
        final Session userSession = request.getResourceResolver().adaptTo(Session.class);
        final String owner = userSession.getUserID();
        final String text = request.getParameter("itemText");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        final String date = sdf.format(new Date());
        return create(todolist, owner, text, date, request.getResourceResolver());
    }

    public Resource create(Resource todolist, String owner, String text, String date, ResourceResolver resolver)
        throws OperationException {
        if (StringUtils.isEmpty(text)) {
            throw new OperationException("todo item text must be present", 400);
        }
        if (!todolist.isResourceType("scf-todo/components/hbs/todos")) {
            throw new OperationException("todo items can only be created under a todo list component", 400);
        }
        final SocialResourceProvider srp = initializeSRPForTodos(todolist);
        if (!socialUtils.mayPost(resolver, todolist)) {
            throw new OperationException("cannot create todos", 400);
        }
        Map<String, Object> props = new HashMap<String, Object>(5);
        props.put("jcr:description", text);
        props.put("jcr:date", date);
        props.put(CollabUser.PROP_NAME, owner);
        props.put(SocialUtils.PN_PARENTID, todolist.getPath() + "/" + owner);
        props.put("sling:resourceType", "scf-todo/components/hbs/todoitem");
        props.put("jcr:primaryType", "nt:unstructured");
        // use the Resource API to create resource to make this data store agnostic
        Resource item;
        try {
            item =
                srp.create(resolver, socialUtils.resourceToUGCStoragePath(todolist) + "/" + owner + "/"
                        + createUniqueNameHint(text), props);
            resolver.commit();
        } catch (PersistenceException e) {
            LOG.error("Unable to create todo item", e);
            throw new OperationException("Unable to create todo item", 500);
        }
        return item;
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

    private SocialResourceProvider initializeSRPForTodos(final Resource todolist) {
        socialUtils.getUGCResource(todolist);
        SocialResourceProvider srp = socialUtils.getSocialResourceProvider(todolist);
        srp.setConfig(socialUtils.getDefaultStorageConfig());
        return srp;
    }

    private String createUniqueNameHint(String message) {
        StringBuilder nodeName;
        nodeName = new StringBuilder(socialUtils.generateRandomString(6)).append("-");
        message = message.replaceAll("\\<.*?>", "");
        message = message.replaceAll("\\&.*?\\;", "");
        message = message.replaceAll(" ", "-");
        if (message.length() > 20) {
            nodeName.append(message.substring(0, 20));
        } else {
            nodeName.append(message);
        }
        return nodeName.toString();
    }

}
