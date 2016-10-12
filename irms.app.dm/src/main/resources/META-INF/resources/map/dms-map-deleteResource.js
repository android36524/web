/**
 * 删除光缆
 */
$importjs(ctx+'/dwr/interface/DeleteResInMapAction.js');

(function(window,object,undefined){
	"use strict";
	if(typeof dms === 'object')
	{
		dms.deleteResourcePanel = function(title,tip,cuids,dialog){
			var self = this;
			self.tip = tip;
			var c = self.createDeletePanel();
			var deleteCuids = [];
			deleteCuids = deleteCuids.concat(cuids);
			self.getOpticalWayBySystemCuid(deleteCuids);
			self.systemCuid = deleteCuids;
			self.dialog = dialog;
			return c;
		};
	}
	if(typeof ht === 'object')
	{
	ht.Default.def('dms.deleteResourcePanel',ht.widget.Panel,{
		tip : null,
		systemCuid : null,
		dialog : null,
		clearDeleteDefault : function(){
			var self = this;
			tp.utils.wholeUnLock();
			dms.Default.tpmap.reset();

			if(self.dialog){
				if(self.dialog._win()){//关闭ext调用的ht窗口
//					self.dialog._win.hide();
//					self.dialog.grid.store.reload();
				}else{
					document.body.removeChild(self.dialog.getView());
				}
			}
			
			if(dms.deletetableDm){
				dms.deletetableDm.clear();
			}
		},
		createDeletePanel : function(){
			var self = this;
			var borderPane = new ht.widget.BorderPane();
			
			var topPanel = self.createTopPanel();
			var centerPanel = self.createPanel();
			var bottomPanel = self.createBtnPanel();
			
			borderPane.setTopView(topPanel,40);
			borderPane.setBottomView(bottomPanel,40);
			borderPane.setCenterView(centerPanel,30);
			return borderPane;
		},
		
		createPanel : function(){
			var self = this;
			var rightPanel = new ht.widget.FormPane();
			var dm = dms.deletetableDm = new ht.DataModel();
			var tablePanel = new ht.widget.TablePane(dm);
			rightPanel.addRow(['删除光纤会造成如下光路核查失败：'],[0.1],20);
			rightPanel.addRow([tablePanel],[0.1],250);
			self.addRightColumn(tablePanel);
			return rightPanel;
		},
		
		addRightColumn : function(table){
			var cm = table.getColumnModel();
		    var column = new ht.Column();
		    column.setAccessType('attr');
		    column.setName('OID');
		    column.setWidth(80);
		    column.setDisplayName('序号');
		    cm.add(column);
		    
		    column = new ht.Column();
		    column.setAccessType('attr');
		    column.setName('LABEL_CN');
		    column.setWidth(400);
		    column.setDisplayName('光路名称');
		    cm.add(column);
		},
		createTopPanel : function(){
			var self = this;
			var topPanel = new ht.widget.FormPane();
			self.setPanelBorerder(topPanel);
			topPanel.getView().style.borderTopWidth ='0';
			topPanel.getView().style.borderLeftWidth ='0';
			topPanel.getView().style.borderRightWidth ='0';
//			icon: 'subGraph_image',
//            iconColor: '#FFFF00'
			topPanel.addRow([{
                image: {
                    toolTip: 'Critical',                            
                    icon: ctx+'/map/close.png',
                    iconColor: '#FFFF00'
                }
            },{
                element: self.tip, // 文字内容
                //color: 'red', //文字颜色
                font: 'bold 15px arial,sans-serif'
            }],[20,0.1],23);
			return topPanel;
		},
		createBtnPanel : function(){
			var self = this;
			var btnPanel = new ht.widget.FormPane();

			var confirmBtn = new ht.widget.Button();
			confirmBtn.setLabel('确定');
			self.setButton(confirmBtn);
			confirmBtn.onClicked = function(e) {
				self.deleteSystemForFlow(self.systemCuid);
			};
			var cancelBtn = new ht.widget.Button();
			cancelBtn.setLabel('取消');
			self.setButton(cancelBtn);
			
			cancelBtn.onClicked = function(e){
				self.clearDeleteDefault();
			};
			
			btnPanel.addRow([null,confirmBtn,cancelBtn,null],[0.1,0.1,0.1,0.1],[23]);
			self.setPanelBorerder(btnPanel);
			btnPanel.getView().style.borderLeftWidth ='0';
			btnPanel.getView().style.borderRightWidth ='0';
			btnPanel.getView().style.borderBottomWidth ='0';
			return btnPanel;
		},

		setPanelBorerder : function(panel){
			panel.getView().style.border = '1px solid rgb(7,61,86)';
//			panel.getView().style.borderTopWidth ='0';
//			panel.getView().style.borderBottomWidth ='0';
//			panel.getView().style.borderRightWidth ='0';
		},
		
		setButton : function(button){
			button.setBorderColor('#FFA000');
			button.setSelectBackground('rgb(7,97,134)');
		},
	    contains : function(bid)
	    {
	    	var bclassname = dms.branchClassName2ResName[bid];
    		if(bclassname)
    			return true;
	    	return false;
	    },
	    setDatas : function(dm,cuid,labelCn){
	    	var node = dm.getDataById(cuid);
	    	if(!node){
	    		node = new ht.Node();
	    		node.a('CUID',cuid);
	    		node.a('LABEL_CN',labelCn);
	    		node.setId(cuid);
	    		node.setName(labelCn);
	    		dm.add(node);
	    	}
	    	return node;
	    },
		getOpticalWayBySystemCuid : function(cuids) {
			var self = this;
			try {
				tp.utils.wholeLock();
				DeleteResInMapAction.getOpticalWayByWireSegCuids(cuids,{
					callback : function(datas){
						if(datas){
							//如果有光路，则在前台显示光路名称，提示其相关光路将会核查失败
							for(var i=0;i<datas.length;i++){
								var data = datas[i],
									cuid = data.CUID,
									labelCn = data.LABEL_CN;
								var opway = self.setDatas(dms.deletetableDm,cuid,labelCn);
								opway.a('OID',i+1);
							}
						}
					},
					errorHandler : function(error) {
						tp.utils.optionDialog("错误提示：",error);
						tp.utils.wholeUnLock();
					},
					async : false
				});
			} catch (e) {
				tp.utils.optionDialog("错误提示：","根据光缆得到光路出错！");
				tp.utils.wholeUnLock();
			}
		},
		deleteSystemForFlow : function(systemCuid) {
			var self = this;
			DeleteResInMapAction.deleteSystemForFlow(systemCuid,{
				callback : function(datas){
					tp.utils.optionDialog("温馨提示：","删除资源成功！");
					//刷新GRID
					if(self.dialog && self.dialog.grid && self.dialog.grid.reloadNode)
					{
						self.dialog.grid.reloadNode(null);
					}
				},
				errorHandler: function(error){
					tp.utils.optionDialog("错误提示：",error);
					tp.utils.wholeUnLock();
				},
				async : false
			});
			self.clearDeleteDefault();
		}
	});
	}
})(this,Object);