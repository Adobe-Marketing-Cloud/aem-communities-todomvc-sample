package com.adobe.aem.social.todomvc.impl;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;

import com.adobe.cq.social.scf.ClientUtilities;
import com.adobe.cq.social.scf.QueryRequestInfo;
import com.adobe.cq.social.scf.SocialComponent;
import com.adobe.cq.social.scf.SocialComponentFactory;
import com.adobe.cq.social.scf.core.AbstractSocialComponentFactory;

@Service
@Component
public class TodoItemFactory extends AbstractSocialComponentFactory implements SocialComponentFactory {

    @Override
    public SocialComponent getSocialComponent(final Resource item) {
        return new TodoItemImpl(item, getClientUtilities(item.getResourceResolver()));
    }

    @Override
    public SocialComponent getSocialComponent(final Resource item, final SlingHttpServletRequest request) {
        return new TodoItemImpl(item, getClientUtilities(request));
    }

    @Override
    public SocialComponent getSocialComponent(final Resource item, final ClientUtilities clientUtils,
        final QueryRequestInfo query) {
        return new TodoItemImpl(item, clientUtils);
    }

    @Override
    public String getSupportedResourceType() {
        return "scf-todo/components/hbs/todoitem";
    }
}
