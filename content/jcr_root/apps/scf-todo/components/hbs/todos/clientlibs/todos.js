(function($CQ, _, Backbone, SCF) {
    "use strict";
    var TodoList = SCF.Model.extend({
        modelName: "TodoListModel",
        addTodo: function(itemText) {
            var that = this;
            var success = function(response) {
                that.log.debug("added item");
                that.log.debug(response.response);
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

    var TodoListView = SCF.View.extend({
        viewName: "TodoList",
        init: function() {
            this.listenTo(this.model, "change", this.render);
            this.listenTo(this.model, "destroy", this.destroy);
        }
    });

    SCF.registerComponent('scf-todo/components/hbs/todos', TodoList, TodoListView);

})($CQ, _, Backbone, SCF);