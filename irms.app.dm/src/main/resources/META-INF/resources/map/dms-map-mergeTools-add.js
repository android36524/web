$importjs(ctx+'/dwr/interface/MergeDuctLineSegsAction.js');
$importjs(ctx+'/dwr/interface/MergeWireSegsAction.js');

(function(window,Object,undefined){
	dms.merge.mergeTools.AddMergeSegPanel = function (mergeCuid, firstPointCuid, lastPointCuid){
		var segGroupCuid = "";//勘误的单位工程
		var scene = "";//勘误场景
		if(dms.designer.segGroupCuid){
			segGroupCuid = dms.designer.segGroupCuid;
		}
		if(dms.designer.scene){
			scene = dms.designer.scene;
		}
		if(mergeCuid){
			var className = mergeCuid.split("-")[0];
			
			if(className === 'WIRE_SYSTEM'){
				MergeWireSegsAction.saveAllData(mergeCuid, firstPointCuid, lastPointCuid,{
					callback : function(data) {
						if (data) {
							tp.utils.optionDialog("温馨提示",
									'<div style="text-align:center;font-size:12px">' + data + '</div>');
						}
						dms.merge.mergeTools.clearMergeSegUnkown();
						dms.Default.tpmap.refreshMap();
						tp.Default.OperateObject.curInterator.setSeriesTip('');
					},
					errorHandler : function(error) {
						tp.utils.optionDialog("错误提示：",error);
						dms.merge.mergeTools.clearMergeSegUnkown();
						dms.Default.tpmap.refreshMap();
					}
				});
			}else{
				MergeDuctLineSegsAction.saveAllDataRes(mergeCuid, firstPointCuid, lastPointCuid,scene,segGroupCuid,{
					callback : function(data) {
						if (data) {
							tp.utils.optionDialog("温馨提示",
									'<div style="text-align:center;font-size:12px">' + data + '</div>');
						}
						dms.merge.mergeTools.clearMergeSegUnkown();
						dms.Default.tpmap.refreshMap();
						tp.Default.OperateObject.curInterator.setSeriesTip('');
					},
					errorHandler : function(error) {
						tp.utils.optionDialog("错误提示：",error);
						dms.merge.mergeTools.clearMergeSegUnkown();
						dms.Default.tpmap.refreshMap();
					}
				});
			}
		}
	};
	
	dms.merge.mergeTools.clearMergeSegUnkown = function(){
		dms.Default.tpmap.refreshMap();
		dms.Default.tpmap.reset();
		dms.merge.mergeTools.dataModel = null;
		dms.merge.mergeTools.mergeSegCuid = "";
		dms.merge.mergeTools.bClassName = "";
		if(dms.merge.mergeTools.mergeSegView){
			document.body.removeChild(dms.merge.mergeTools.mergeSegView);
			dms.merge.mergeTools.mergeSegView = null;
		}
	};
})(this,Object);