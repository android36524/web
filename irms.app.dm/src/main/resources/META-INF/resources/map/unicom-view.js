Dms.Default.Unicom = {};
// 光缆组织图视图
Dms.Default.Unicom.initView = function(data) {
    var map = Dms.initMap(data);
    document.body.innerHTML = null;

    var dm_wireseg = new ht.DataModel();
    var dm_property = new ht.DataModel();
    var dm_site = new ht.DataModel();

    var _segView = new Dms.Default.Unicom.WireSegTreeView(dm_wireseg);
    var _siteView = new Dms.Default.Unicom.SiteTreeView(dm_site);
    var _propertyView = new Dms.Default.Unicom.PropertyView(dm_property);; // 属性信息

    var _tabView = new Dms.Default.Unicom.TabView();
    _tabView.add("光缆", _segView, true); // 光缆导航树
    _tabView.add("站点", _siteView); // 站点导航树

    // 工具条
    var toolBarItems = Dms.Default.Unicom.getToolItems();
    var _toolbar = new ht.widget.Toolbar(toolBarItems);
    _toolbar.getView().style.zIndex = 1;

    var mainPanel = document.createElement("div");
    mainPanel.appendChild(map.getView());

    var _leftPanel = new ht.widget.BorderPane();
    var _rightPanel = new ht.widget.BorderPane();

    _leftPanel.setCenterView(_tabView);
    //_leftPanel.setBottomView(_propertyView);

    _rightPanel.setTopView(_toolbar);
    _rightPanel.setCenterView(mainPanel)

    var _splitView = new ht.widget.SplitView(_leftPanel, _rightPanel, "horizontal", 0.2);
    var _view = _splitView.getView();
    _view.className = 'main';


    document.body.appendChild(_splitView.getView());

    window.addEventListener('resize', function(e) {
        _splitView.invalidate();
    }, false);
};


// 页签
Dms.Default.Unicom.TabView = function() {
    var _self = this;
    Dms.Default.Unicom.TabView.superClass.constructor.apply(this);

    window.addEventListener('resize', function(e) {
        _self.invalidate();
    }, false);
};
ht.Default.def("Dms.Default.Unicom.TabView", ht.widget.TabView, {
    // 加入视图做为页签
    add: function(name, view, selected) {
        var tabModel = this.getTabModel();

        var tab = new ht.Tab();
        tab.setName(name);
        tab.setView(view);

        tabModel.add(tab);

        if (selected) tabModel.sm().setSelection(tab);
    }

});

// 光缆导航树
Dms.Default.Unicom.WireSegTreeView = function(dataModel) {
    //var treeview = new ht.widget.TreeView();
    Dms.Default.Unicom.WireSegTreeView.superClass.constructor.apply(this);
    var _self = this;
    _self.setDataModel(dataModel);
    _self.init(); // 初始化 

    window.addEventListener('resize', function(e) {
        _self.invalidate();
    }, false);
};
ht.Default.def("Dms.Default.Unicom.WireSegTreeView", ht.widget.TreeView, {
    // 组件初始化
    init: function() {
        var _self = this;
        // 初始化根节点, level = 0
        var root = _self.add(null, 'DISTRICT_0001', '全国', 0);

        // 以下为示例数据

        // 省节点, level = 1
        var p1 = _self.add(root, 'DISTRICT-00001-00001', '湖南', 1);
        var p2 = _self.add(root, 'DISTRICT-00001-00002', '河南', 1);
        var p3 = _self.add(root, 'DISTRICT-00001-00003', '江苏', 1);
        var p4 = _self.add(root, 'DISTRICT-00001-00004', '西藏', 1);
        var p5 = _self.add(root, 'DISTRICT-00001-00005', '吉林', 1);
        var p6 = _self.add(root, 'DISTRICT-00001-00006', '江西', 1);
        var p7 = _self.add(root, 'DISTRICT-00001-00007', '辽宁', 1);
        var p8 = _self.add(root, 'DISTRICT-00001-00008', '山东', 1);

        // 市节点, level = 2
        var c1 = _self.add(p2, 'DISTRICT-00001-00002-00003', '郑州', 2);
        var c2 = _self.add(p2, 'DISTRICT-00001-00002-00002', '洛阳', 2);
        var c3 = _self.add(p2, 'DISTRICT-00001-00002-00005', '南阳', 2);
        var c4 = _self.add(p2, 'DISTRICT-00001-00002-00006', '新乡', 2);
        var c5 = _self.add(p2, 'DISTRICT-00001-00002-00007', '开封', 2);
        var c6 = _self.add(p2, 'DISTRICT-00001-00002-00008', '信阳', 2);
        var c7 = _self.add(p2, 'DISTRICT-00001-00002-00009', '平顶山', 2);
        var c8 = _self.add(p2, 'DISTRICT-00001-00002-00010', '商丘', 2);
        var c9 = _self.add(p2, 'DISTRICT-00001-00002-00011', '驻马店', 2);
        var c10 = _self.add(p2, 'DISTRICT-00001-00002-00012', '焦作', 2);
        var c11 = _self.add(p2, 'DISTRICT-00001-00002-00013', '安阳', 2);
        var c12 = _self.add(p2, 'DISTRICT-00001-00002-00014', '濮阳', 2);
        var c13 = _self.add(p2, 'DISTRICT-00001-00002-00015', '许昌', 2);
        var c14 = _self.add(p2, 'DISTRICT-00001-00002-00016', '周口', 2);
        var c15 = _self.add(p2, 'DISTRICT-00001-00002-00017', '漯河', 2);
        var c16 = _self.add(p2, 'DISTRICT-00001-00002-00018', '三门峡', 2);
        var c17 = _self.add(p2, 'DISTRICT-00001-00002-00019', '鹤壁', 2);
        var c18 = _self.add(p2, 'DISTRICT-00001-00002-00020', '济源', 2);

        // 光缆段节点, level = 3
        var w0 = _self.add(c1, 'WIRE_SEG-4028908344ce90130144ce9a116e001b', '光接头盒1--光接头盒2', 3);
        var w0 = _self.add(c2, 'WIRE_SEG-4028908344ce90130144cefd78410032', '光交接箱1--光交接箱2', 3);

    },

    // 添加节点
    add: function(parent, id, name, level) {
        var node = new ht.Node();
        node.setName(name);
        node.setId(id);
        if (parent) {
            node.setParent(parent);
        }
        if (level) { // 表示当前节点的级别，可能是全国/省/市/光缆段
            node.setAttr('_level', level);
        }

        this.dm().add(node);
        return node;
    },
    onDataClicked: function(data) {
        var level = data.getAttr('_level');
        if (level < 3) {
            return;
        }
        console.info('光缆定位');
        var sql = "CUID='" + data.getId() + "'";
        Dms.Default.tpmap.locateByCondition("WIRE_SEG", sql);
    }
});

// 站点导航树
Dms.Default.Unicom.SiteTreeView = function(dataModel) {
    //var _treeview = new ht.widget.TreeView();
    Dms.Default.Unicom.SiteTreeView.superClass.constructor.apply(this);
    var _self = this;
    _self.setDataModel(dataModel);


    window.addEventListener('resize', function(e) {
        _self.invalidate();
    }, false);
};
ht.Default.def("Dms.Default.Unicom.SiteTreeView", ht.widget.TreeView, {

});

// 属性信息窗口
Dms.Default.Unicom.PropertyView = function(dataModel) {
    Dms.Default.Unicom.PropertyView.superClass.constructor.apply(this);
    var _self = this;
    _self.setDataModel(dataModel);

    window.addEventListener('resize', function(e) {
        _self.invalidate();
    }, false);
};
ht.Default.def("Dms.Default.Unicom.PropertyView", ht.widget.PropertyView, {


});

// 工具条
Dms.Default.Unicom.getToolItems = function(owner) {
    var getMap = function() {
        return Dms.Default.tpmap.getMap();
    };
    return [{
            type: 'toggle',
            //icon: 'category',
            label: '地图平移',
            selected: false,
            action: function(item) {
                tp.Default.DrawObject._movePointState = 0;
                tp.Default.DrawObject._drawState = 0;
                //graphView.getView().style.cursor = 'hand';
            }
        }, {
            type: 'button',
            //icon: 'category',
            label: '选择',
            selected: false,
            action: function(item) {
                tp.Default.DrawObject._movePointState = 0;
                tp.Default.DrawObject._drawState = 1;
                //graphView.getView().style.cursor = 'default';
            }
        }, {
            type: 'button',
            //icon: 'category',
            label: '放大',
            selected: false,
            action: function(item) {
                var map = getMap();
                if (map.getZoom() < map.getMaxZoom()) {
                    map.zoomIn();
                }
            }
        }, {
            type: 'button',
            //icon: 'category',
            label: '缩小',
            selected: false,
            action: function(item) {
                var map = getMap();
                if (map.getZoom() > map.getMinZoom()) {
                    map.zoomOut();
                }
            }
        }, {
            type: 'button',
            //icon: 'category',
            label: '全图',
            selected: false,
            action: function(item) {
                getMap().setZoom(0);
            }
        }, {
            type: 'button',
            //icon: 'category',
            label: '测距',
            selected: false,
            action: function(item) {
                Dms.Default.tpmap.measureLine();
            }
        }, {
            type: 'button',
            //icon: 'category',
            label: '测面积',
            selected: false,
            action: function(item) {
                Dms.Default.tpmap.measurePloygon();
            }
        }, {
            type: 'button',
            //icon: 'category',
            label: '清除选择',
            selected: false,
            action: function(item) {
                Dms.Default.tpmap.reset();
            }
        }, {
            type: 'button',
            //icon: 'category',
            label: '|',
            selected: false
        },
        // 下面是图元的编辑功能
        , {
            type: 'button',
            //icon: 'category',
            label: '站点',
            selected: false,
            action: function(item) {
                Dms.Tools.addMapPoint('SITE');
            }
        }, {
            type: 'button',
            //icon: 'category',
            label: '光分纤箱',
            selected: false,
            action: function(item) {
                Dms.Tools.addMapPoint('FIBER_DP');
            }
        }, {
            type: 'button',
            //icon: 'category',
            label: '光交接箱',
            selected: false,
            action: function(item) {
                Dms.Tools.addMapPoint('FIBER_CAB');
            }
        }, {
            type: 'button',
            //icon: 'category',
            label: '光接头盒',
            selected: false,
            action: function(item) {
                Dms.Tools.addMapPoint('FIBER_JOINT_BOX');
            }
        }, {
            type: 'button',
            //icon: 'category',
            label: '光缆段',
            selected: false,
            action: function(item) {
                Dms.Tools.addMapLine('WIRE_SEG', 'FIBER_JOINT_BOX');
            }
        }


    ]
};