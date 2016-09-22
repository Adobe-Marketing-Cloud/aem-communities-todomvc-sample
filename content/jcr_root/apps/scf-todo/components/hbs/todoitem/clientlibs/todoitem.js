(function($CQ, _, Backbone, SCF) {
    "use strict";
    var TodoItem = SCF.Model.extend({
        modelName: "TodoItemModel",
        setState: function(state) {
            var that = this;
            var success = function(response) {
                that.log.debug("updated item");
                that.log.debug(response.response);
                that.set({
                    "active": !state
                });
            };
            var error = function(e) {
                that.log.error("Unable to update item");
            };
            var postData = {};
            postData[":operation"] = "social:todos:updateItem";
            postData.isDone = state;
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

    var TodoItemView = SCF.View.extend({
        viewName: "TodoItem",
        init: function() {
            this.listenTo(this.model, "change", this.render);
            this.listenTo(this.model, "destroy", this.destroy);
        },
        updateState: function(e) {
            var status = e.currentTarget.checked;
            this.model.setState(status);
        }
    });

    SCF.registerComponent('scf-todo/components/hbs/todoitem', TodoItem, TodoItemView);

})($CQ, _, Backbone, SCF);