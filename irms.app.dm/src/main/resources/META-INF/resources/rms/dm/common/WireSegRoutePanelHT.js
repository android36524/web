$importjs(ctx+'/map/map-inc.js');
$importjs(ctx+'/map/dms-utils.js');
$importjs(ctx+'/map/dms-wiretoductline.js');
createWireSegPanelHT = function(param){
	var code = param.CUID;
	if(code){
    	tp.Default.OperateObject.contextObject.cuid = code;
    }
	var c = new dms.utils.createWireToDuctLinePanel(code);
	return c;
};
