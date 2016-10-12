
$importjs(ctx+'/dwr/interface/MergeDuctLineBranchsAction.js');

(function(window,Object,undefined){
	dms.merge.mergeBranchTools.AddMergeBranchPanel = function (mergeCuid, firstBranchCuid, lastBranchCuid){
		MergeDuctLineBranchsAction.saveAllData(mergeCuid, firstBranchCuid, lastBranchCuid,{
			callback : function(data){
				if (data) {
					tp.utils.optionDialog("温馨提示",
							'<div style="text-align:center;font-size:12px">' + data + '</div>');
				}
				dms.merge.mergeBranchTools.clearMergeSegUnkown();
				dms.Default.tpmap.refreshMap();
			},
			errorHandler : function(error){
				tp.utils.optionDialog("错误提示：", error);
				tp.utils.wholeUnLock();
			}
		});
	};
	
	dms.merge.mergeBranchTools.clearMergeSegUnkown = function(){
		dms.Default.tpmap.refreshMap();
		dms.Default.tpmap.reset();
		dms.merge.mergeBranchTools.dataModel = null;
		dms.merge.mergeBranchTools.mergeSegCuid = "";
		dms.merge.mergeBranchTools.bClassName = "";
		if(dms.merge.mergeBranchTools.mergeSegView){
			document.body.removeChild(dms.merge.mergeBranchTools.mergeSegView);
			dms.merge.mergeBranchTools.mergeSegView = null;
		}
	};
})(this,Object);