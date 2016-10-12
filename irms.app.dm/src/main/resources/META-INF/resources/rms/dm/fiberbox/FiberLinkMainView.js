Ext.ns('Frame.op');
$importjs(ctx + "/rms/dm/fiberbox/FiberLinkMainViewHt.js"); 
$importjs(ctx+'/dwr/interface/FiberLinkAction.js');

/**
 * 光交接箱 纤芯关联 ext panel
 */
Frame.op.FiberLinkPanel = Ext.extend(Ext.Panel, {
    constructor: function (config) {
        config = config || {};
        this.id = "FL_" + config.cuid+new Date().getTime();
        Frame.op.FiberLinkPanel.superclass.constructor.call(this, config);
        this.bmClassId = config.bmClassId;
        this.cuid = config.cuid;
        this.name = config.name; // 光分纤箱名称
    },
    initComponent: function () {
        Frame.op.FiberLinkPanel.superclass.initComponent.call(this);
    },
    afterRender: function () {
        Frame.op.FiberLinkPanel.superclass.afterRender.call(this);

        var fiberLinkView = new FiberLinkView(this.cuid, this.name);
        var mv = fiberLinkView.getView();
        mv.className = 'graphView';
        var mainPanel = document.getElementById(this.id);
        mainPanel.firstChild.firstChild.appendChild(mv);
    }
});