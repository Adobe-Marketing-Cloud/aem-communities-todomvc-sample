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
import com.adobe.cq.social.scf.SocialComponent;
import com.adobe.cq.social.scf.SocialComponentFactory;
import com.adobe.cq.social.scf.SocialComponentFactoryManager;
import com.adobe.cq.social.scf.SocialOperationResult;
import com.adobe.cq.social.scf.core.operations.AbstractSocialOperation;

@Component(immediate = true)
@Service
@Property(name = PostOperation.PROP_OPERATION_NAME, value = "social:todos:updateItem")
public class UpdateOperation extends AbstractSocialOperation {

    @Reference
    private TodoOperations todos;

    @Reference
    private SocialComponentFactoryManager scfm;

    @Override
    protected SocialOperationResult performOperation(SlingHttpServletRequest request) throws OperationException {
        final Resource item = this.todos.updateStatus(request);
        return new SocialOperationResult(getSocialComponent(item, request), "updated item",
            HttpServletResponse.SC_OK, request.getResource().getPath());
    }

    private SocialComponent getSocialComponent(final Resource item, final SlingHttpServletRequest req) {
        final SocialComponentFactory scf = scfm.getSocialComponentFactory(item);
        return scf.getSocialComponent(item, req);
    }
}
