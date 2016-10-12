/**
 * 地理信息图层显示面板 
 */
tp.widget.GeoLayerPanel = function(map) {
	var self = this;
	//资源图层配置列表
	self._geoLayerResource={};
	
	self._dataModel = new ht.DataModel();
	self._treeView = new ht.widget.TreeView(self._dataModel);

    self._getGeoLayer(map);
	//监听图层选中和反选事件
	self._treeView.onDataClicked = function(data) {
		var layerids = new Array;
		var selectDatas = self._treeView.getDataModel().sm();
		selectDatas.each(function(data) {
			var layerid = data.getAttr("layerid");
			if (layerid != null) {
				layerids = layerids.concat(layerid);
			}
		}, selectDatas);
		//获取选中地图及图层显示集合
		if (layerids.length > 0) {
			//Dms.Default.tpmap.getMap().fire('layerchange', layerids);
			var tpmap=Dms.Default.tpmap;
			tp.Default.design.Tools.loadGeoInfo(tpmap, layerids);
			tp.Default.DrawObject._drawState = 301;//为CAD工程加载背景地理信息
		} else {
			//Dms.Default.tpmap.getMap().fire('layerchange', [ -1 ]);
		}
	};
	
};

ht.Default.def('tp.widget.GeoLayerPanel',Object, {
	//加载背景地图服务的图层
	_getGeoLayer:function(map){
		var self=this;
		var layerMeta= map.getMapLayers();
		for(var i=0;i<layerMeta.length;i++){
			var layerName=layerMeta[i].name.split('-')[0],
			    layerId=layerMeta[i].id;
			if(self._geoLayerResource[layerName]==null){
				self._geoLayerResource[layerName]={
						'layerIds':[layerId],
						'layerName':layerMeta[i].name,
						};
			}
		}
		self._loadGeoLayerData();
	},
	_loadGeoLayerData : function() {
		var parent = new ht.Node();
		parent.setName("地理图层");
		for(var j in this._geoLayerResource){
		  var objLayer=this._geoLayerResource[j];
			  this.addLayerNode(parent, objLayer.layerName, objLayer.layerIds, null);
		}
		this._dataModel.add(parent);
		this._dataModel.getSelectionModel().clearSelection();//清除选中状态
		this._treeView.expandAll();
		this._treeView.setCheckMode('all');
		return  this._treeView;
	},
    getDataModel:function() {
	return this._dataModel;
	},
	getTreeView :function() {
		return this._treeView;
	},
// 增加图层节点
    addLayerNode: function(parentNode, layerName, layerid, maplayer) {
		var child = new ht.Node();
		child.setName(layerName);
		child.a('layerid', layerid);
		child.a('maplayer', maplayer);
		parentNode.addChild(child);
		this._dataModel.add(child);
		return child;
    }
});