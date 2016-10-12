Dms.className2ResName = {
	'SITE' : '站点',
	'ACCESSPOINT' : '接入点',
	'FIBER_DP' : '光分纤箱',
	'FIBER_CAB' : '光交接箱',
	'FIBER_JOINT_BOX' : '光接头盒',
	'MANHLE' : '人手井',
	'POLE' : '电杆',
	'STONE' : '标石',
	'INFLEXION' : '拐点',
	'DUCT_SEG' : '管道段',
	'POLEWAY_SEG' : '杆路段',
	'STONEWAY_SEG' : '直埋段',
	'UP_LINE_SEG' : '引上段',
	'HANG_WALL_SEG' : '挂墙段',
	'WIRE_SEG' : '光缆段',
	'FIBER_TERMINAL_BOX' : '光终端盒',
	'AN_POS': '分光器',
	'GPON_COVER': '覆盖范围',
	'BUSINESS_COMMUNITY': '家客业务区网格',
	'T_ROFH_FULL_ADDRESS': '标准地址'
};

Dms.branchClassName2ResName = {
	'DUCT_SEG' : '管道分支',
	'POLEWAY_SEG' : '杆路分支',
	'STONEWAY_SEG' : '标石路由分支',
	'UP_LINE_SEG' : '引上',
	'HANG_WALL_SEG' : '挂墙',
	'WIRE_SEG' : '光缆分支'
};

Dms.systemClassName2ResName = {
	'DUCT_SEG' : '管道系统',
	'POLEWAY_SEG' : '杆路系统',
	'STONEWAY_SEG' : '标石路由系统',
	'UP_LINE_SEG' : '引上',
	'HANG_WALL_SEG' : '挂墙',
	'WIRE_SEG' : '光缆'
};

Dms.selectSystemNameResName = {
	'DUCT_SEG' : 'service_dict_dm.DM_DUCT_SYSTEM',
	'POLEWAY_SEG' : 'service_dict_dm.DM_POLEWAY_SYSTEM',
	'STONEWAY_SEG' : 'service_dict_dm.DM_STONEWAY_SYSTEM',
	'UP_LINE_SEG' : 'service_dict_dm.DM_UPLINE',
	'HANG_WALL_SEG' : 'service_dict_dm.DM_HANG_WALL',
	'WIRE_SEG' : 'service_dict_dm.DM_WIRE_SYSTEM'
};

Dms.selectSystemTypeResName = {
		//第一个值代表弹出某个类型的系统，第二个值代表查询哪个类型资源的bo，第三个代表要用的哪个form界面（所属系统、模板名称、所属工程）
		'service_dict_dm.DM_DUCT_SYSTEM' : 'IRMS.RMS.DUCT_SYSTEM,SystemGridTemplateProxyBO,tp-querySystemForm',
		'service_dict_dm.DM_POLEWAY_SYSTEM' : 'IRMS.RMS.POLEWAY_SYSTEM,SystemGridTemplateProxyBO,tp-querySystemForm',
		'service_dict_dm.DM_STONEWAY_SYSTEM' : 'IRMS.RMS.STONEWAY_SYSTEM,SystemGridTemplateProxyBO,tp-querySystemForm',
		'service_dict_dm.DM_UPLINE' : 'IRMS.RMS.UP_LINE,SystemGridTemplateProxyBO,tp-querySystemForm',
		'service_dict_dm.DM_HANG_WALL' : 'IRMS.RMS.HANG_WALL,SystemGridTemplateProxyBO,tp-querySystemForm',
		'service_dict_dm.DM_WIRE_SYSTEM' : 'IRMS.RMS.WIRE_SYSTEM,SystemGridTemplateProxyBO,tp-querySystemForm',
		'service_dict_dm.DM_TEMPLATE' : 'IRMS.RMS.TEMPLATE,GridTemplateProxyBO,tp-queryTemplateForm',
		'service_dict_dm.DM_PROJECT_MANAGEMENT' : 'IRMS.RMS.PROJECT_MANAGEMENT,GridTemplateProxyBO,tp-queryProjectForm'
};

// 新增加某种类型的线使用已有点时的过滤
Dms.getPointTypeByResName = {
	'DUCT_SEG' : ['MANHLE', 'FIBER_CAB', 'FIBER_DP', 'ACCESSPOINT', 'SITE', 'PRESET_POINT'],
	'POLEWAY_SEG' : ['POLE', 'FIBER_CAB', 'FIBER_DP', 'INFLEXION', 'ACCESSPOINT', 'SITE', 'PRESET_POINT'],
	'STONEWAY_SEG' : ['STONE', 'MANHLE', 'FIBER_CAB', 'FIBER_DP', 'ACCESSPOINT', 'SITE', 'PRESET_POINT'],
	'UP_LINE_SEG' : ['INFLEXION', 'POLE', 'MANHLE', 'STONE', 'FIBER_CAB', 'FIBER_DP', 'FIBER_JOINT_BOX', 'ACCESSPOINT', 'SITE', 'PRESET_POINT'],
	'HANG_WALL_SEG' : ['INFLEXION', 'POLE', 'FIBER_CAB', 'FIBER_DP', 'ACCESSPOINT', 'SITE', 'PRESET_POINT'],
	'WIRE_SEG' : ['FIBER_JOINT_BOX', 'FIBER_DP', 'FIBER_CAB', 'ACCESSPOINT', 'SITE', 'PRESET_POINT']
};

//新增加某种类型的线使用已有点时的过滤
dms.getSplitPointTypeByResName = {
	'DUCT_SEG' : ['MANHLE','ACCESSPOINT', 'SITE', 'PRESET_POINT','POLE','STONE','INFLEXION'],
	'POLEWAY_SEG' : ['POLE','ACCESSPOINT', 'SITE', 'PRESET_POINT','MANHLE','STONE','INFLEXION'],
	'STONEWAY_SEG' : ['STONE','ACCESSPOINT', 'SITE', 'PRESET_POINT','MANHLE','POLE','INFLEXION'],
	'UP_LINE_SEG' : ['POLE','ACCESSPOINT', 'SITE', 'PRESET_POINT','MANHLE','STONE','INFLEXION'],
	'HANG_WALL_SEG' : ['INFLEXION','ACCESSPOINT', 'SITE', 'PRESET_POINT','MANHLE','POLE','STONE'],
	'WIRE_SEG' : ['FIBER_JOINT_BOX', 'FIBER_DP', 'FIBER_CAB', 'ACCESSPOINT', 'SITE', 'PRESET_POINT','MANHLE','POLE','STONE','INFLEXION']
};
// 图形化自动敷设时选择只能选择线需要过滤点查询
Dms.getDuctLineTypeByResName = {
	'WIRE_SEG' : ['DUCT_SEG', 'POLEWAY_SEG', 'STONEWAY_SEG', 'UP_LINE_SEG', 'HANG_WALL_SEG']
};

//移动点需要过滤点查询
Dms.getMovePointTypeByResName = {
	'POINT' : ['MANHLE', 'POLE', 'STONE', 'ACCESSPOINT', 'FIBER_DP','FIBER_CAB','FIBER_JOINT_BOX','INFLEXION','SITE','AN_POS','TRANS_ELEMENT']
};

dms.isFiberPoint={
		'FIBER_CAB' : '光交接箱',
		'FIBER_DP' : '光分纤箱',
		'FIBER_JOINT_BOX' : '光接头盒'
};

dms.isPoint={
		'SITE' : '站点',
		'ACCESSPOINT' : '接入点',
		'FIBER_DP' : '光分纤箱',
		'FIBER_CAB' : '光交接箱',
		'FIBER_JOINT_BOX' : '光接头盒',
		'MANHLE' : '人手井',
		'POLE' : '电杆',
		'STONE' : '标石',
		'INFLEXION' : '拐点',
		'AN_POS':'分光器'
};

//新增线设施弹出右键时根据类型需要隐藏的右键菜单
//现已改为需要显示的类型--by张宇航
dms.isHideContextMenu={
		'DUCT_SEG':'MANHLE,STONE,FIBER_CAB',
		'POLEWAY_SEG':'POLE,INFLEXION,FIBER_DP,FIBER_CAB',
		'STONEWAY_SEG':'STONE,MANHLE,FIBER_CAB',
		'UP_LINE_SEG':'MANHLE,INFLEXION,POLE,STONE,FIBER_DP,FIBER_CAB',
		'HANG_WALL_SEG':'INFLEXION,POLE,FIBER_DP,FIBER_CAB',
		'WIRE_SEG':'FIBER_DP,FIBER_CAB,FIBER_JOINT_BOX'
};

//承载段合并分支点选已有点 ---wq
dms.getPointTypeBySysResName = {
		'DUCT_SYSTEM' : ['MANHLE','ACCESSPOINT', 'SITE', 'PRESET_POINT','POLE','STONE','INFLEXION'],
		'POLEWAY_SYSTEM' : ['POLE','ACCESSPOINT', 'SITE', 'PRESET_POINT','MANHLE','STONE','INFLEXION'],
		'STONEWAY_SYSTEM' : ['STONE','ACCESSPOINT', 'SITE', 'PRESET_POINT','MANHLE','POLE','INFLEXION'],
		'WIRE_SYSTEM' : ['FIBER_JOINT_BOX', 'FIBER_DP', 'FIBER_CAB', 'ACCESSPOINT', 'SITE', 'PRESET_POINT','MANHLE','POLE','STONE','INFLEXION']
};

//承载段合并段点选已有点 ----wq
dms.getPointTypeByBraResName = {
		'DUCT_BRANCH' : ['MANHLE','ACCESSPOINT', 'SITE', 'PRESET_POINT','POLE','STONE','INFLEXION'],
		'POLEWAY_BRANCH' : ['POLE','ACCESSPOINT', 'SITE', 'PRESET_POINT','MANHLE','STONE','INFLEXION'],
		'STONEWAY_BRANCH' : ['STONE','ACCESSPOINT', 'SITE', 'PRESET_POINT','MANHLE','POLE','INFLEXION'],
		'UP_LINE' : ['POLE','ACCESSPOINT', 'SITE', 'PRESET_POINT','MANHLE','STONE','INFLEXION'],
		'HANG_WALL' : ['INFLEXION','ACCESSPOINT', 'SITE', 'PRESET_POINT','MANHLE','POLE','STONE'],
		'WIRE_SYSTEM' : ['FIBER_JOINT_BOX', 'FIBER_DP', 'FIBER_CAB', 'ACCESSPOINT', 'SITE', 'PRESET_POINT','MANHLE','POLE','STONE','INFLEXION']
};

dms.systemClassNameResName = {
		'DUCT_SYSTEM' : '管道',
		'POLEWAY_SYSTEM' : '杆路',
		'STONEWAY_SYSTEM' : '直埋',
		'UP_LINE' : '引上',
		'HANG_WALL' : '挂墙',
		'WIRE_SYSTEM' : '光缆'
};
//图形化承载已有光缆
dms.getWireTypByName = {
		'DUCT_LINE' : ['WIRE_SEG']
};