Ext.ns('Frame.op');
$importjs(ctx + "/jsp_space/maintain/districts/query/tbar/FiberLinksMainViewHt.js"); 
$importjs(ctx+'/dwr/interface/FiberLinkAction.js');

/**
 * 接入点或层间光缆 纤芯关联
 */
Frame.op.FiberLinksPanel = Ext.extend(Ext.Panel, {
    constructor: function (config) {
        config = config || {};
        this.id = "FL_" + config.cuid+new Date().getTime();
        Frame.op.FiberLinksPanel.superclass.constructor.call(this, config);
        this.bmClassId = config.bmClassId;
        this.cuid = config.cuid;
        this.name = config.name; // 光分纤箱名称
    },
    initComponent: function () {
        Frame.op.FiberLinksPanel.superclass.initComponent.call(this);
    },
    afterRender: function () {
        Frame.op.FiberLinksPanel.superclass.afterRender.call(this);

        var fiberLinkView = new FiberLinksView(this.cuid, this.name);
        var mv = fiberLinkView.getView();
        mv.className = 'graphView';
        var mainPanel = document.getElementById(this.id);
        mainPanel.firstChild.firstChild.appendChild(mv);
    }
});