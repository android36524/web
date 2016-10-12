Ext.ns("Frame.tree");
Frame.tree.AsynTreeLoader = Ext.extend(Ext.tree.TreeLoader, {
	checkVisible : undefined,
	paramOrder : [ "cuid", "text", "leaf", "parentTreeNode",
	               "checked", "boName", "params", "treeParams",
	               "treeName", "system", "queryParams" ],
	paramsAsHash : false,
	directFn : function() {
		var a = arguments[arguments.length - 2];
		var c = arguments[arguments.length - 1];
		var d = {};
		for (var b = 0; b < a.paramOrder.length; b++) {
			d[a.paramOrder[b]] = arguments[b + 1];
		}
		TreePanelAction.loadData(d, function(e) {
			c.createDelegate(a, [ e, {
				status : true
			} ], 0)();
		});
	},
	createNode : function(a) {
		if (!Ext.isEmpty(a.icon)) {
			a.icon = ctx + "/" + a.icon;
		}
		if (this.checkVisible === true) {
			if (a.checked === true) {
				a.checked = true;
			} else {
				a.checked = false;
			}
		} else {
			if (this.checkVisible === false) {
				a.checked = undefined;
			} else {
				if (a.checked === true) {
					a.checked = true;
				} else {
					if (a.checked === false) {
						a.checked = false;
					} else {
						a.checked = undefined;
					}
				}
			}
		}
		return Frame.tree.AsynTreeLoader.superclass.createNode.call(this, a);
	},
	requestData : function(d, e, c) {
		if (this.fireEvent("beforeload", this, d, e) !== false) {
			if (Ext.isEmpty(d.attributes.boName)) {
				d.attributes.boName = this.boName;
			} else {
				this.boName = d.attributes.boName;
			}
			for (var b = 0; b < this.paramOrder.length; b++) {
				this.baseParams[this.paramOrder[b]] = d.attributes[this.paramOrder[b]];
			}
			var a = this.getParams(d);
			a.push(this);
			a.push(this.processDirectResponse.createDelegate(
					this, [ {
						callback : e,
						node : d,
						scope : c
					} ], true));
			this.directFn.apply(window, a);
		}
	},
	processResponse : function(d, c, j, k) {
		var l = d.responseText;
		try {
			var a = d.responseData || Ext.decode(l);
			c.beginUpdate();
			for (var f = 0, g = a.length; f < g; f++) {
				var b = this.createNode(a[f]);
				if (b) {
					b.index = Ext.isEmpty(c) ? f : c.childNodes.length;
					c.appendChild(b);
				}
			}
			c.endUpdate();
			this.runCallback(j, k || c, [ c ]);
		} catch (h) {
			this.handleFailure(d);
		}
	},
	doPreload : function(d) {
		if (d && d.attributes.children) {
			if (d.childNodes.length < 1) {
				var c = d.attributes.children;
				d.beginUpdate();
				for (var b = 0, a = c.length; b < a; b++) {
					var e = d
							.appendChild(this.createNode(c[b]));
					if (this.preloadChildren) {
						e.index = Ext.isEmpty(d) ? b
								: d.childNodes.length;
						this.doPreload(e);
					}
				}
				d.endUpdate();
			}
			return true;
		}
		return false;
	}
});
Frame.tree.AsynTreePanel = Ext.extend(Ext.tree.TreePanel, {
	checkVisible : undefined,
	initComponent : function() {
		if (!this.root) {
			this.root = new Ext.tree.AsyncTreeNode({
				text : "Root"
			});
		} else {
			if (Ext.isObject(this.root)) {
				this.root = new Ext.tree.AsyncTreeNode(this.root);
			}
		}

		this.loader = new Frame.tree.AsynTreeLoader({
			clearOnLoad : this.clearOnLoad,
			checkVisible : this.checkVisible
		});
		
		if (this.header !== false) {
			this.initTreeTools();
		}
		
		Frame.tree.AsynTreePanel.superclass.initComponent.call(this);
		this.on("dblclick", function(node, event) {
			if (node && Ext.isDefined(node.attributes.cuid) && node.attributes.cuid.indexOf("moreNodeBtn") != -1) {
				this.expandMoreNode(node);
			} else {
				this.fireEvent("treeNodeClick", node, event);
			}
		}, this);
	},
	expandMoreNode : function(node, callback) {
		if(!node.parentNode)
		{
			node.parentNode = {};
			node.parentNode.attributes = {};
		}
		if (node.parentNode.attributes) {
			node.parentNode.attributs = {
				params : {}
			};
		} else {
			if (node.parentNode.attributes.params) {
				node.parentNode.attributes.params = {};
			}
		}
		node.parentNode.attributes.params.pageNumber = node.attributes.params.pageNumber;
		node.parentNode.attributes.params.pageCount = node.attributes.params.pageCount;
		node.parentNode.attributes.params.redirectTemplateIds = node.attributes.params.redirectTemplateIds;
		node.getOwnerTree().loader.load(node.parentNode, function() {
			if(node.parentNode)
				node.parentNode.attributes.params.redirectTemplateIds = null;
			node.remove(true);
			
			if(callback) {
				callback();
			}
		});
	},
	initTreeTools : function() {
		this.tools = [{
			id : "refresh",
			scope : this,
			handler : function() {
				var node = this.getSelectionModel().getSelectedNode();
				if (node) {
					this.reloadNode(node);
				} else {
					this.reloadNode(this.getRootNode());
				}
			}
		}, {
			id : "plus",
			scope : this,
			handler : function() {
				this.expandAll();
			}
		}, {
			id : "minus",
			scope : this,
			handler : function() {
				this.collapseAll();
			}
		}];
	},
	reloadNode : function(node) {
		if(!node){
			node = this.getRootNode();
		}
		node.removeAll();
		node.attributes.params.pageNumber = null;
		if (node.attributes.params.pageCount != -1) {
			node.attributes.params.pageCount = null;
		}
		node.getOwnerTree().loader.load(node, function() {
			node.expand();
		});
	}
});
Ext.reg("asyntreepanel", Frame.tree.AsynTreePanel);
Frame.tree.AsynTreeGridLoader = Ext.extend(Frame.tree.AsynTreeLoader, {
	createNode : function(node) {
		if (!node.uiProvider) {
			node.uiProvider = Ext.ux.tree.TreeGridNodeUI;
		}
		Ext.apply(node, node.data);
		return Frame.tree.AsynTreeGridLoader.superclass.createNode.call(this, node);
	}
});
Frame.tree.AsynTreeGridPanel = Ext.extend(Ext.ux.tree.TreeGrid, {
	checkVisible : undefined,
	autoLoad : false,
	singleSelect : false,
	enableSort : false,
	onContextMenu:function(e){
		   e.preventDefault(); 
           e.stopEvent(); 
		
	},
    initEvents : function() {
		this.mon(this.innerHd, "contextmenu", this.onContextMenu, this);
        Frame.tree.AsynTreeGridPanel.superclass.initEvents.apply(this, arguments);
	},
	initComponent : function() {
		if (this.autoLoad == true) {
			this.autoLoad = false;
			this._autoLoad = true;
		} else {
			this._autoLoad = false;
		}
		var a = this;
		if(Frame.grid){
			if(Frame.grid.plugins.tbar) {
				var plugins = [];
				var pkg = Frame.grid.plugins.tbar;
				if(this.tbarPlugin) {
					this.tbarPluginKeys.push(this.tbarPlugin);
				}
				Ext.each(this.tbarPluginKeys, function(key, idx){
					var P = pkg[key];
					if(Ext.isFunction(P)) {
						var plugin = new P(this);
						if(Ext.isArray(plugin)) {
							Ext.each(plugin, function(p){
								if(p.divide == true) {
									plugins.push('-');
								}
								plugins.push(p);
							});
						}else {
							if(plugin.divide == true) {
								plugins.push('-');
							}
							plugins.push(plugin);
						}
					}
				}, this);
				if(plugins.length > 0) {
					if(this.tbar && this.tbar.length > 0) {
						this.tbar = plugins.concat(this.tbar);
					}else {
						this.tbar = plugins;
					}
				}
			}
			if(Frame.grid.plugins.event) {
				var pkg = Frame.grid.plugins.event;
				if(this.eventPlugin) {
					this.eventPluginKeys.push(this.eventPlugin);
				}
				Ext.each(this.eventPluginKeys, function(key, idx){
					var P = pkg[key];
					if(Ext.isFunction(P)) {
						var plugin = new P(this);
						if(Ext.isObject(plugin)) {
							if(!this.listeners) {
								this.listeners = {};
							}
							Ext.apply(this.listeners, plugin);
						}
					}
				}, this);
			}
		}
		if (this.singleSelect) {
			this.selModel = new Ext.tree.DefaultSelectionModel({
				listeners : {
					selectionchange : function(c, e) {
						var b = a.getChecked();
						for (var d = 0; d < b.length; d++) {
							b[d].getUI().toggleCheck(false);
						}
						if(e!=null){
							e.getUI().toggleCheck(true);
						}
					}
				}
			});
		} else {
			this.selModel = new Ext.tree.MultiSelectionModel({
				onNodeClick : function(d, f) {
					var g = false;
					for (var b = 0; b < d.parentNode.childNodes.length; b++) {
						var h = d.parentNode.childNodes[b];
						if (h.isSelected()) {
							g = true;
						}
					}
					var c = undefined;
					if (g) {
						c = d.parentNode.last;
					} else {
						d.parentNode.last = undefined;
					}
					if (f.shiftKey && !this.singleSelect && Ext.isDefined(c)) {
						a.selectRange(c, d, this.ctrlKey);
					} else {
						if (f.ctrlKey && this.isSelected(d)) {
							this.unselect(d);
						} else {
							this.select(d, f, f.ctrlKey);
							d.parentNode.last = d;
						}
					}
				},
				listeners : {
					selectionchange : function(e, d) {
						var c = a.getChecked();
						for (var f = 0; f < c.length; f++) {
							if (c[f].getUI().isChecked()) {
								var b = c[f].getUI().checkbox;
								if (b) {
									b.checked = false;
									var g = c[f].getUI().checkbox.checked;
									b.defaultChecked = g;
									c[f].getUI().node.attributes.checked = g;
								}
							}
						}
						for (var f = 0; f < d.length; f++) {
							if (!d[f].getUI().isChecked()) {
								var b = d[f].getUI().checkbox;
								if (b) {
									b.checked = true;
									var g = d[f].getUI().checkbox.checked;
									b.defaultChecked = g;
									d[f].getUI().node.attributes.checked = g;
								}
							}
						}
					}
				}
			});
		}
		if (!this.root) {
			this.root = new Ext.tree.AsyncTreeNode({
				text : "Root"
			});
		} else {
			if (Ext.isObject(this.root)) {
				this.root = new Ext.tree.AsyncTreeNode(this.root);
			}
		}
		this.loader = new Frame.tree.AsynTreeGridLoader({
			clearOnLoad : this.clearOnLoad,
			checkVisible : this.checkVisible
		});
		if (!this.columns) {
			this.columns = [ {
				dataIndex : "text",
				header : "名称",
				width : 150
			} ];
		}
		if (this.header !== false) {
			this.initTreeTools();
		}
		Frame.tree.AsynTreeGridPanel.superclass.initComponent.call(this);
		this.on("beforeload", function(node) {
			if (this.autoLoad === false) {
				this.autoLoad = true;
				return false;
			}
		});
		this.on("dblclick", function(node, event) {
			if (node && Ext.isDefined(node.attributes.cuid) && node.attributes.cuid.indexOf("moreNodeBtn") != -1) {
				this.expandMoreNode(node);
			} else {
				this.fireEvent("treeNodeClick", node, event);
			}
		}, this);
		this.on("checkchange", function(node, checked) {
			if (checked) {
				node.getUI().focus();
				node.getUI().addClass("x-tree-selected");
				node.parentNode.last = node;
				var selNodes = this.selModel.selNodes;
				var index = selNodes.indexOf(node);
				if (index == -1) {
					this.selModel.selNodes.push(node);
				}
				this.selModel.selMap[node.id] = node;
			} else {
				node.getUI().addClass("x-tree-selected");
				node.getUI().removeClass("x-tree-selected");
				node.parentNode.last = undefined;
				var selNodes = this.selModel.selNodes;
				var index = selNodes.indexOf(node);
				if (index != -1) {
					this.selModel.selNodes.splice(index, 1);
				}
				delete this.selModel.selMap[node.id];
			}
		}, this);
		if (this._autoLoad == true) {
			this.on("afterrender", function(b) {
				b.getRootNode().expand();
			}, this);
		}
	},
	selectRange : function(b, a, d) {
		if (!d) {
			this.selModel.clearSelections();
		}
		if (b.index <= a.index) {
			var c = b;
			while (c != null && c.previousSibling != a) {
				this.selModel.select(c, null, true);
				c = c.nextSibling;
			}
		} else {
			var c = a;
			while (c != null && c.previousSibling != b) {
				this.selModel.select(c, null, true);
				c = c.nextSibling;
			}
		}
	},
	expandMoreNode : function(node) {
		if(!node.parentNode)
		{
			node.parentNode = {};
			node.parentNode.attributes = {};
		}
		if (node.parentNode.attributes) {
			node.parentNode.attributes = {
				params : {}
			};
		} else {
			if (node.parentNode.attributes.params) {
				node.parentNode.attributes.params = {};
			}
		}
		node.parentNode.attributes.params.pageNumber = node.attributes.params.pageNumber;
		node.parentNode.attributes.params.pageCount = node.attributes.params.pageCount;
		node.parentNode.attributes.params.redirectTemplateIds = node.attributes.params.redirectTemplateIds;
		node.getOwnerTree().loader.load(
			node.parentNode,
			function() {
				node.parentNode.attributes.params.redirectTemplateIds = null;
				node.remove(true);
			}
		);
	},
	initTreeTools : function() {
		this.tools = [ {
			id : "refresh",
			scope : this,
			handler : function() {
				var a = this.getSelectionModel().getSelectedNode();
				if (a) {
					this.reloadNode(a);
				} else {
					this.reloadNode(this.getRootNode());
				}
			}
		}, {
			id : "plus",
			scope : this,
			handler : function() {
				this.expandAll();
			}
		}, {
			id : "minus",
			scope : this,
			handler : function() {
				this.collapseAll();
			}
		} ];
	},
	reloadNode : function(a) {
		if(!a){
			a = this.getRootNode();
		}
		a.removeAll(true);
		a.attributes.params.pageNumber = null;
		if (a.attributes.params.pageCount != -1) {
			a.attributes.params.pageCount = null;
		}
		if (a.isExpanded()) {
			a.getOwnerTree().loader.load(a);
		} else {
			a.expand();
		}
	}
});