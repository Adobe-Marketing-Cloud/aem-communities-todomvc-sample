package com.adobe.aem.social.todomvc.impl;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import javax.jcr.RepositoryException;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;

import com.adobe.aem.social.todomvc.api.TodoList;
import com.adobe.cq.social.scf.ClientUtilities;
import com.adobe.cq.social.scf.CollectionPagination;
import com.adobe.cq.social.scf.QueryRequestInfo;
import com.adobe.cq.social.scf.SocialComponent;
import com.adobe.cq.social.scf.SocialComponentFactory;
import com.adobe.cq.social.scf.core.BaseSocialComponent;
import com.adobe.cq.social.scf.core.CollectionSortedOrder;
import com.adobe.cq.social.srp.SocialResourceProvider;
import com.adobe.cq.social.srp.internal.SocialResourceUtils;
import com.adobe.cq.social.srp.utilities.api.SocialResourceUtilities;
import com.adobe.cq.social.ugc.api.ComparisonType;
import com.adobe.cq.social.ugc.api.ConstraintGroup;
import com.adobe.cq.social.ugc.api.Operator;
import com.adobe.cq.social.ugc.api.PathConstraint;
import com.adobe.cq.social.ugc.api.PathConstraintType;
import com.adobe.cq.social.ugc.api.SearchResults;
import com.adobe.cq.social.ugc.api.UgcFilter;
import com.adobe.cq.social.ugc.api.UgcFilter.Comparison;
import com.adobe.cq.social.ugc.api.UgcSearch;
import com.adobe.cq.social.ugc.api.UgcSort;
import com.adobe.cq.social.ugc.api.UgcSort.Direction;
import com.adobe.cq.social.ugc.api.ValueConstraint;
import com.adobe.cq.social.ugcbase.SocialUtils;

public class TodoListImpl extends BaseSocialComponent implements TodoList {

    final private String currentUser;
    final private String pathToUserTodos;
    final private SocialResourceProvider srp;
    final private Resource todoListComponent;
    final private Resource usersList;
    final private ClientUtilities clientUtils;
    final private QueryRequestInfo query;
    private int filteredSize = 0;
    private boolean listInitialized = false;
    private List<Object> todoItems;

    public TodoListImpl(final Resource resource, final ClientUtilities clientUtils, final QueryRequestInfo query,
        final SocialUtils socialUtils, final SocialResourceProvider srp) {
        super(resource, clientUtils);
        currentUser = clientUtils.getAuthorizedUserId();
        pathToUserTodos = resource.getPath() + "/" + currentUser;
        this.srp = srp;
        this.todoListComponent = resource;
        this.usersList = resource.getResourceResolver().resolve(pathToUserTodos);
        this.clientUtils = clientUtils;
        this.query = query;
    }

    @Override
    public List<Object> getItems() {
        if (!listInitialized) {
            Iterator<Resource> items = query.isQuery() ? getFilteredItems() : getAllItems();
            this.todoItems = new ArrayList<Object>();
            while (items.hasNext()) {
                final Resource item = items.next();
                todoItems.add(getTodoItemSocialComponent(item));
            }
            listInitialized = true;
        }
        return this.todoItems;
    }

    private Iterator<Resource> getFilteredItems() {
        final UgcFilter filter = new UgcFilter();
        ConstraintGroup stateGroup = new ConstraintGroup(Operator.Or);
        final String[] filters = query.getPredicates().get("filter");

        if (filters == null || filters.length == 0) {
            return getAllItems();
        }
        final String requestedFilter = filters[0];
        if (StringUtils.equals("done", requestedFilter)) {
            stateGroup.addConstraint(new ValueConstraint<Boolean>("isDone_b", Boolean.TRUE));
        } else if (StringUtils.equals("active", requestedFilter)) {
            stateGroup
                .addConstraint(new ValueConstraint<Boolean>("isDone_b", Boolean.TRUE, ComparisonType.NotEquals));
        } else {
            return getAllItems();
        }
        filter.and(stateGroup);
        final String ugcParentPath =
            todoListComponent.getResourceResolver().adaptTo(SocialResourceUtilities.class)
                .resourceToUGCStoragePath(usersList);
        final ConstraintGroup pathFilters = new ConstraintGroup(Operator.And);
        pathFilters
            .addConstraint(new PathConstraint(ugcParentPath, PathConstraintType.IsDescendantNode, Operator.Or));
        filter.and(pathFilters);
        filter.addSort(new UgcSort("added", Direction.Asc));
        final UgcSearch search = todoListComponent.getResourceResolver().adaptTo(UgcSearch.class);
        try {
            SearchResults<Resource> results =
                search.find(null, todoListComponent.getResourceResolver(), filter, 0, 100000, true);
            this.filteredSize = Math.toIntExact(results.getTotalNumberOfResults());
            return results.getResults().iterator();
        } catch (final RepositoryException e) {
            // TODO log error message
            return getAllItems();
        }
    }

    private Iterator<Resource> getAllItems() {
        List<Entry<String, Boolean>> sortOrder = new ArrayList<Entry<String, Boolean>>();
        sortOrder.add(new AbstractMap.SimpleEntry<String, Boolean>("added", false));
        return srp.listChildren(usersList.getPath(), resource.getResourceResolver(), 0, -1, sortOrder);
    }

    private SocialComponent getTodoItemSocialComponent(final Resource item) {
        final SocialComponentFactory factory =
            this.clientUtils.getSocialComponentFactoryManager().getSocialComponentFactory(item);
        return factory.getSocialComponent(item);
    }

    @Override
    public int getTotalSize() {
        if (!listInitialized) {
            getItems();
        }
        return this.query.isQuery() ? this.filteredSize : Math.toIntExact(srp.countChildren(usersList));
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
