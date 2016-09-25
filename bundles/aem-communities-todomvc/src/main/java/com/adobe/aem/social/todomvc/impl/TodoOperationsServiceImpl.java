package com.adobe.aem.social.todomvc.impl;

import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.jcr.Session;

import org.apache.commons.lang3.StringUtils;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.ReferencePolicy;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.aem.social.todomvc.api.TodoItem;
import com.adobe.aem.social.todomvc.api.TodoOperationExtension;
import com.adobe.aem.social.todomvc.api.TodoOperationTypes;
import com.adobe.aem.social.todomvc.api.TodoOperations;
import com.adobe.cq.social.scf.OperationException;
import com.adobe.cq.social.scf.core.operations.AbstractOperationService;
import com.adobe.cq.social.srp.SocialResourceProvider;
import com.adobe.cq.social.srp.utilities.api.SocialResourceUtilities;
import com.adobe.cq.social.ugcbase.CollabUser;
import com.adobe.cq.social.ugcbase.SocialUtils;

@Component(immediate = true)
@Service
public class TodoOperationsServiceImpl extends
    AbstractOperationService<TodoOperationExtension, TodoOperationTypes, TodoItem> implements TodoOperations {

    private static final Logger LOG = LoggerFactory.getLogger(TodoOperationsServiceImpl.class);

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY, policy = ReferencePolicy.STATIC)
    protected ResourceResolverFactory resourceResolverFactory;

    private static final String UGC_WRITER = "ugc-writer";

    private static SecureRandom randomGenerator = new SecureRandom();
    private static final char[] RANDOM_CHARS = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
        .toCharArray();

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
        final SocialResourceUtilities sru = resolver.adaptTo(SocialResourceUtilities.class);
        if (!sru.checkPermission(resolver, sru.resourceToACLPath(todolist), Session.ACTION_ADD_NODE)) {
            throw new OperationException("cannot create todos", 400);
        }
        Map<String, Object> props = new HashMap<String, Object>(5);
        props.put("jcr:description", text);
        props.put("jcr:date", date);
        props.put(CollabUser.PROP_NAME, owner);
        props.put(SocialUtils.PN_PARENTID, todolist.getPath() + "/" + owner);
        props.put(SocialUtils.PROP_COMPONENT, todolist.getPath());
        props.put("sling:resourceType", "scf-todo/components/hbs/todoitem");
        props.put("jcr:primaryType", "nt:unstructured");
        // use the Resource API to create resource to make this data store agnostic
        Resource item;
        try {
            item =
                srp.create(resolver, sru.resourceToUGCStoragePath(todolist) + "/" + owner + "/"
                        + createUniqueNameHint(text), props);
            resolver.commit();
        } catch (final PersistenceException e) {
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
        final Resource todoItem = request.getResource();
        final Session userSession = request.getResourceResolver().adaptTo(Session.class);
        final String user = userSession.getUserID();
        final boolean isDone = Boolean.parseBoolean(request.getParameter("isDone"));
        return updateStatus(todoItem, isDone, user);
    }

    public Resource updateStatus(final Resource todoItem, final boolean isDone, final String user)
        throws OperationException {
        if (!todoItem.isResourceType("scf-todo/components/hbs/todoitem")) {
            throw new OperationException("only todo items can be updated", 400);
        }
        final ValueMap props = todoItem.adaptTo(ValueMap.class);
        final String owner = props.get(CollabUser.PROP_NAME, "");
        final String currentUser = todoItem.getResourceResolver().getUserID();
        final SocialResourceUtilities sru = todoItem.getResourceResolver().adaptTo(SocialResourceUtilities.class);
        final Resource todoList =
            todoItem.getResourceResolver().getResource(props.get(SocialUtils.PROP_COMPONENT, ""));
        if (!!sru.checkPermission(todoItem.getResourceResolver(), sru.resourceToACLPath(todoList), Session.ACTION_ADD_NODE)
                && !StringUtils.equals(owner, currentUser)) {
            throw new OperationException("cannot create todos", 400);
        }
        ResourceResolver ugcResolver = null;
        try {
            ugcResolver = getUGCWriterResolver();
            final Resource modifiableTodoItem = ugcResolver.getResource(todoItem.getPath());
            final ModifiableValueMap modProps = modifiableTodoItem.adaptTo(ModifiableValueMap.class);
            modProps.put("isDone_b", isDone);
            ugcResolver.commit();
        } catch (final PersistenceException e) {
            LOG.error("Unable to update todo item", e);
            throw new OperationException("Unable to update todo item", 500);
        } finally {
            if (ugcResolver != null && ugcResolver.isLive()) {
                ugcResolver.close();
            }
        }
        return todoItem;
    }

    private SocialResourceProvider initializeSRPForTodos(final Resource todolist) {
        final SocialResourceUtilities sru = todolist.getResourceResolver().adaptTo(SocialResourceUtilities.class);
        sru.getUGCResource(todolist);
        SocialResourceProvider srp = sru.getSocialResourceProvider(todolist);
        srp.setConfig(sru.getStorageConfig(todolist));
        return srp;
    }

    private String createUniqueNameHint(String message) {
        StringBuilder nodeName;
        nodeName = new StringBuilder(generateRandomString(6)).append("-");
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

    private ResourceResolver getUGCWriterResolver() throws OperationException {
        try {
            return resourceResolverFactory.getServiceResourceResolver(Collections.singletonMap(
                ResourceResolverFactory.SUBSERVICE, (Object) UGC_WRITER));
        } catch (LoginException e) {
            throw new OperationException("Not allowed", e, 400);
        }
    }

    private String generateRandomString(final int length) {
        final StringBuilder str = new StringBuilder();
        for (int i = 0; i < length; i++) {
            str.append(RANDOM_CHARS[randomGenerator.nextInt(RANDOM_CHARS.length)]);
        }
        return str.toString();
    }

}
