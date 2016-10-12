$importjs(ctx+'/rms/dm/common/dm-base.js');


createResourceTablePanelHT = function(param){
	var datamodel = new ht.DataModel();
	var pageTable =  new tp.widget.PageTable(datamodel);
	pageTable.initMeta(param.code);
	pageTable._queryParams = getWhereQueryItems(param.condition);
	var baseParams = {
			count : true,
			start : 0,
			limit : 20,
			totalNum : 20
		};
	pageTable.loadGridData(pageTable._queryParams, baseParams);
    var basicFormPane = new ht.widget.FormPane(); 
    basicFormPane.addRow([pageTable],[0.1],[0.1]);

    return basicFormPane;
};

getWhereQueryItems = function(condition) {
	var whereQueryItems = {};
	for(var i= 0;i<condition.length;i++) {
		var con = condition[i];
		var whereQueryItem = {
			key : con.key,
			relation : con.relation,
			value : con.value,
			type : Ext.isEmpty(con.type)?'string':con.type
		};
		whereQueryItems[con.key] = whereQueryItem;
	}
	return whereQueryItems;
};
