package com.adobe.aem.social.todomvc.api;

import java.util.List;

import com.adobe.cq.social.scf.OperationExtension;

public interface TodoOperationExtension extends OperationExtension<TodoItem> {
    @Override
    public List<TodoOperationTypes> getOperationsToHookInto();
}
