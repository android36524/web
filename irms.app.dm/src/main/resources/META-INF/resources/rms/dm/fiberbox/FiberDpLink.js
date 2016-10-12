Ext.ns('Frame.op');
$importjs(ctx + "/rms/dm/fiberbox/FiberDpLinkViewHt.js"); 
$importjs(ctx+'/dwr/interface/FiberManagerAction.js');

/**
 * 光分线箱纤芯关联 ext panel
 */
Frame.op.FiberDpLinkPanel = Ext.extend(Ext.Panel, {
    constructor: function (config) {
        config = config || {};
        this.id = "FL_" + config.cuid+ new Date().getTime();
        Frame.op.FiberDpLinkPanel.superclass.constructor.call(this, config);
        this.bmClassId = config.bmClassId;
        this.cuid = config.cuid;
        this.name = config.name; // 光分纤箱名称
    },
    initComponent: function () {
        Frame.op.FiberDpLinkPanel.superclass.initComponent.call(this);
    },
    afterRender: function () {
        Frame.op.FiberDpLinkPanel.superclass.afterRender.call(this);

        var fiberDpLinkView = new FiberDpLinkView(this.cuid, this.name);
        var mv = fiberDpLinkView.getView();
        mv.className = 'graphView';
        var mainPanel = document.getElementById(this.id);
        mainPanel.firstChild.firstChild.appendChild(mv);
    }
});