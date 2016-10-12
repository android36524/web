

var HitchTaskQueryPropertyPanel = Ext.extend(Ext.grid.PropertyGrid ,{
	title:"基本属性",
	hideHeaders :true,
	autoHeight: true,
	ayout:'fit',
	viewConfig: {
			fit:true,
			selectedRowClass:"x-grid3-row-data-selected",
			templates:{
					cell : new Ext.Template(
					'<td class="x-grid3-col x-grid3-cell x-grid3-td-{id} {css}" style="{style}" tabIndex="0" {cellAttr}>',
					'<div class="x-grid3-cell-inner x-grid3-col-{id}" unselectable="on" {attr}> <div class="tip-target" ext:qtip="{value}">{value}</div></div>',
					'</td>'
					)
												
			},
			forceFit: true								  
	},
	isAllEidte:false,
	listeners :{
		beforeedit:function( source,  recordId,  value,  oldValue ){
			return this.isAllEidte?true:false;
		}
	},
	constructor : function(configer) {
		this.isAllEidte = configer.isAllEidte;
		HitchTaskQueryPropertyPanel.superclass.constructor.call(this, configer);
	},
	 initComponent : function(){
		HitchTaskQueryPropertyPanel.superclass.initComponent.call(this);
    }
});