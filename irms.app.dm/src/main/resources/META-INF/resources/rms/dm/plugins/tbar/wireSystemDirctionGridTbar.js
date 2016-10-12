Ext.ns('Frame.grid.plugins.tbar');

Frame.grid.plugins.tbar.wireSystemDirctionGridTbar = Ext.extend(Object, {
	constructor: function(grid){
		this.grid = grid;
		Frame.grid.plugins.tbar.wireSystemDirctionGridTbar.superclass.constructor.call(this);
		return this.createButtons();
		
	},
	
	createButtons : function(){
		var detailedButton = new Ext.Button({
			text:'明细',
			handler:this._detailedView,
			iconCls : 'c_link_edit',
			scope:this
			
		});
		return [
			'-',
			detailedButton,
			'-'];
	},
	
	//明细
	_detailedView : function(){
		var scope=this;
		var segRecords = this.grid.getSelectionModel().getSelections(); //选择记录
		
		if(Ext.isEmpty(segRecords) || segRecords.length == 0 || segRecords.length > 1){
			Ext.Msg.alert('温馨提示', '请选择一条数据进行操作.');
			return;
		}else{
			var dirction = segRecords[0].data.DIRCTION;
			var no = segRecords[0].data.NO;
			var wireCuid = this.grid.cuid;
			var labelCn = this.grid.labelCn;
			var cuid = wireCuid + "," + no;
			var url = '/rms/dm/optical/opticalwaypanel.jsp?code=service_dict_dm.DM_FIBERJOINTBOXEXPORT&hasQuery=false';
	    	FrameHelper.openUrl(ctx + url + '&cuid=' + cuid + '&labelCn=' + labelCn,labelCn + '光缆系统路由明细');
		}  	
	}
});