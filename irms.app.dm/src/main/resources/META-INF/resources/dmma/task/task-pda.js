var PdaSelectWindow = Ext.extend(Ext.form.TriggerField,  {
    selfWindow:"",
    initComponent : function(){
		 PdaSelectWindow.superclass.initComponent.call(this);
  },
	onDestroy : function(){
		Ext.destroy(this.selfWindow);
        PdaSelectWindow.onDestroy.call(this);
    },

    onTriggerClick : function(){
		var me =this;
		
		me.selfWindow = new Ext.Window({
            title: '端口新增界面',
            closable:true,
			height:600,
			width:800,
			modal:true,
			html:'<iframe id="iframe_pda" name="iframe_pda" src="'+ctx+'/cmp_res/grid/EditorGridPanel.jsp?code=service_dict_dm.PDA_DEVICE" style="width:100%; height:100%;" frameborder="0"></iframe>',
			buttons:[{
				text:"选中",
				handler:function(btn,event){
			    	
					me.setValue("确认确认确认");
					me.selfWindow.close();
					
					
				}
			},{
				text:"关闭",
				handler:function(btn,event){
			    	me.selfWindow.close();
				}
			}]
			});
			me.selfWindow.show();
    }
});
Ext.reg('pdaselectwindow', PdaSelectWindow);