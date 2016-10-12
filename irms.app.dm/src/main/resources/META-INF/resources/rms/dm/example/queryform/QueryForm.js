define(function(require,exports,module){
	
	require('rms/dm/example/combo/PropertyRightCombo');
	
	tp.plugin.QueryForm = function(config){
	
		tp.plugin.QueryForm.superClass.constructor.call(this,config);
	};
	
	ht.Default.def(tp.plugin.QueryForm,tp.widget.Component,{
		
		initComponent : function(){
			
			this._initView();
			tp.plugin.QueryForm.superClass.initComponent.call(this);
		},
		
		_initView : function(){
			
			this.formPanel = new tp.widget.form.FormPanel({
				items : [
					new tp.widget.form.TextField({name : 'LABEL_CN',label:'机房名称'}),
					new tp.widget.form.TextField({name : 'SITE_NAME',label:'所属站点'}),
					new tp.widget.form.Combo({
						name : 'PROPERTY_RIGHT',
						label : '产权',
	                    values: [1, 2, 3],
	                    labels: ['移动','电信','联通']
					
					})
				]
			});
		},
		
		getItem : function(){
			
			return this.formPanel;
		},
		
		getView : function(){
			
			return this.formPanel.getView();
		}
	});
	
	return tp.plugin.QueryForm;
});
