(function($CQ, _, Backbone, SCF) {
    "use strict";
    var TodoList = SCF.Model.extend({
        modelName: "TodoListModel",
        relationships: {
            "items": {
                collection: "TodoItemsCollection",
                model: "TodoItemModel"
            }
        },
        filter: function(state) {
            this.fetch({
                data: $.param({
                    filter: state
                })
            });
        },
        addTodo: function(itemText) {
            var that = this;
            var success = function(response) {
                that.log.debug("added item");
                that.log.debug(response.response);
                var TodoItemKlass = SCF.Models[that.constructor.prototype.relationships.items.model];
                var newToDo = TodoItemKlass.createLocally(response.response);
                var items = that.get("items");
                items.push(newToDo);
                that.set({
                    "items": items,
                    "totalSize": that.get("totalSize") + 1
                });
            };
            var error = function(e) {
                that.log.error("Unable to add item");
            };
            var postData = {};
            postData[":operation"] = "social:todos:createItem";
            postData.itemText = itemText;
            $CQ.ajax(SCF.config.urlRoot + this.get("id") + SCF.constants.URL_EXT, {
                dataType: "json",
                type: "POST",
                contentType: "application/x-www-form-urlencoded; charset=UTF-8",
                xhrFields: {
                    withCredentials: true
                },
                data: this.addEncoding(postData),
                "success": success,
                "error": error
            });
        }
    });

    var TodoItemsCollection = Backbone.Collection.extend({
        collectionName: "TodoItemsCollection"
    });

    var TodoListView = SCF.View.extend({
        viewName: "TodoList",
        init: function() {
            this.listenTo(this.model, "change", this.render);
            this.listenTo(this.model, "destroy", this.destroy);
        },
        addTodo: function(e) {
            var text = this.getField("itemText");
            this.model.addTodo(text);
        },
        checkForEnter: function(e) {
            if (e.keyCode == 13) {
                e.preventDefault();
                e.stopImmediatePropagation();
                this.addTodo(e);
            }
        },
        filter: function(e) {
            var state = e.currentTarget.getAttribute("data-filter");
            SCF.Router.navigate(window.location.pathname + "?filter=" + state);
            this.model.filter(state);
        }
    });

    SCF.registerComponent('scf-todo/components/hbs/todos', TodoList, TodoListView);

})($CQ, _, Backbone, SCF);