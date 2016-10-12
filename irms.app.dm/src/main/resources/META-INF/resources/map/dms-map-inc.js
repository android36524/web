/**
 * 
 */
dms.Default.user = {};
dms.Default.user.distCuid = ac.relatedDistrictCuid || 'DISTRICT-00001';
dms.split = {};
dms.merge = {};
dms.Panel = {};
dms.move = {};
dms.split.wireSegRoutePoint = [];
dms.split.wireSegSplitPoint = [];

$importjs(ctx + "/map/render/map-renderer-actiontablecell.js");
$importjs(ctx + "/map/render/map-renderer-querypanel.js");
$importjs(ctx + "/map/render/map-renderer-number-double.js");

$importjs(ctx+'/dwr/interface/GridViewAction.js');
$importjs(ctx+'/dwr/interface/DistrictDataAction.js');
$importjs(ctx+'/dwr/interface/MapGridAction.js');
$importjs(ctx+'/dwr/interface/MapComboxAction.js');
$importjs(ctx+'/dwr/interface/GetServiceParamAction.js');
$importjs(ctx+'/dwr/interface/DeleteResInMapAction.js');
$importjs(ctx+'/dwr/interface/OpticalWayAction.js');
$importjs(ctx+'/dwr/interface/CalculateSystemLengthAction');

$importjs(ctx + "/rms/dm/fiberbox/FiberDpLinkViewHt.js"); 
$importjs(ctx + "/rms/dm/fiberbox/JumpLinkMainViewHt.js"); 
$importjs(ctx + "/rms/dm/fiberbox/FiberLinkMainViewHt.js"); 
$importjs(ctx+'/rms/dm/wire/WireRemainViewHt.js');
$importjs(ctx+'/rms/dm/common/ResourceTablePanelHT.js');
$importjs(ctx+'/rms/dm/common/RelatedSystemListPanelHT.js');
$importjs(ctx+'/rms/dm/common/ResImagsPanelHT.js');
$importjs(ctx+'/rms/dm/ductseg/LayingWireListPanelHT.js');
$importjs(ctx+'/rms/dm/ductseg/WirePassListPanelHT.js');
$importjs(ctx+'/rms/dm/interwire/InterWireListPanelHT.js');

$importjs(ctx + "/map/map-panel-propertypane.js");
$importjs(ctx + "/map/map-panel-fcabedit.js");
$importjs(ctx + "/map/dms-map-splitTools.js");
$importjs(ctx + "/map/dms-map-splitTools-add.js");
$importjs(ctx + "/map/dms-map-floatpane.js");
$importjs(ctx + "/map/dms-map-moveWireRoute.js");
$importjs(ctx + "/map/dms-map-moveWireRouteSetFBox.js");
$importjs(ctx + "/map/dms-map-mergeTools.js");
$importjs(ctx + "/map/dms-map-mergeTools-add.js");
$importjs(ctx + "/map/dms-map-mergeBranchTools.js");
$importjs(ctx + "/map/dms-map-mergeBranchTools-add.js");
$importjs(ctx + '/map/dms-map-deleteResource.js');
$importjs(ctx + "/map/dms-map-movePoints.js");
$importjs(ctx + "/map/dms-map-openPanelView.js");
$importjs(ctx + "/map/dms-query-fBoxPanel.js");
$importjs(ctx + "/map/dms-map-dataAttrpane.js");
