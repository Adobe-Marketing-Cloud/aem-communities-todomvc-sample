package com.adobe.aem.social.todomvc.endpoints;

import javax.servlet.http.HttpServletResponse;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.servlets.post.PostOperation;

import com.adobe.aem.social.todomvc.api.TodoOperations;
import com.adobe.cq.social.scf.OperationException;
import com.adobe.cq.social.scf.SocialOperationResult;
import com.adobe.cq.social.scf.core.operations.AbstractSocialOperation;

@Component(immediate = true)
@Service
@Property(name = PostOperation.PROP_OPERATION_NAME, value = "social:todos:createItem")
public class CreateOperation extends AbstractSocialOperation {

    @Reference
    private TodoOperations todos;

    @Override
    protected SocialOperationResult performOperation(SlingHttpServletRequest request) throws OperationException {
        final Resource item = this.todos.create(request);
        return new SocialOperationResult(null, "created item", HttpServletResponse.SC_OK, request.getResource()
            .getPath());
    }
}
