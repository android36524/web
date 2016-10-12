/**
 * 
 */
dms.openDuctSystemQueryPanel = function(bmclassName,bonames,formName,ductDm){
	var open = false;
	seajs.use("./jslib/tp/component/dialoggridpanel",function(){
		
		var dialogGridPanel = new tp.component.DiaLogGridPanel({
			bmclassid : bmclassName,
			boname : bonames,
	//		width : 600,
	//		height : 300,
			plugins : {
				queryform : 'jslib/tp/map/'+formName
	//			queryform : 'map/dms-querySystemForm'
			},
			listeners : {
				select : function(data){
					var cuid = data.CUID,
					queryDm=ductDm,
					wireSegCuid = tp.Default.OperateObject.contextObject.cuid;
					if(!open)
					{
						tp.utils.createWireToDuctlineDialog(cuid,wireSegCuid,'',function(selectData){
							var json = selectData;
							queryDm.deserialize(json);//如果原来有值，会直接追加在后面
						});
						open = true;
					}
				}
			}
		});
	
		dialogGridPanel.show();
	});
};
