package com.adobe.aem.social.todomvc.impl;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;

import com.adobe.cq.social.scf.ClientUtilities;
import com.adobe.cq.social.scf.QueryRequestInfo;
import com.adobe.cq.social.scf.SocialComponent;
import com.adobe.cq.social.scf.SocialComponentFactory;
import com.adobe.cq.social.scf.core.AbstractSocialComponentFactory;
import com.adobe.cq.social.srp.SocialResourceProvider;
import com.adobe.cq.social.ugcbase.SocialUtils;

@Component(immediate = true)
@Service
public class TodoListFactory extends AbstractSocialComponentFactory implements SocialComponentFactory {

    @Override
    public SocialComponent getSocialComponent(final Resource todolist) {
        return new TodoListImpl(todolist, getClientUtilities(todolist.getResourceResolver()), getSocialUtils(),
            getSRP(todolist));
    }

    private SocialResourceProvider getSRP(final Resource resource) {
        SocialResourceProvider srp = getSocialUtils().getSocialResourceProvider(resource);
        srp.setConfig(getSocialUtils().getDefaultStorageConfig());
        return srp;
    }

    @Override
    public SocialComponent getSocialComponent(final Resource todolist, final SlingHttpServletRequest request) {
        return new TodoListImpl(todolist, getClientUtilities(request), getSocialUtils(), getSRP(todolist));
    }

    @Override
    public SocialComponent getSocialComponent(final Resource todolist, final ClientUtilities clientUtils,
        final QueryRequestInfo query) {
        return new TodoListImpl(todolist, clientUtils, getSocialUtils(), getSRP(todolist));
    }

    @Override
    public String getSupportedResourceType() {
        return "scf-todo/components/hbs/todos";
    }
}
