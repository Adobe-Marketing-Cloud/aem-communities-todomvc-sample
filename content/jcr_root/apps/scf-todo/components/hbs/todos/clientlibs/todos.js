(function($CQ, _, Backbone, SCF) {"use strict";
	var TodoList = SCF.Model.extend({
		modelName : "TodoListModel"
	});

	var TodoListView = SCF.View.extend({
		viewName : "TodoList",
		init : function() {
			this.listenTo(this.model, "change", this.render);
			this.listenTo(this.model, "destroy", this.destroy);
		}
	});

	SCF.registerComponent('scf-todo/components/hbs/todos', TodoList, TodoListView);

})($CQ, _, Backbone, SCF);
