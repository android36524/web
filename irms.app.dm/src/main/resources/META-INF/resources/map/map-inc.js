Dms = {};
dms = Dms;
Dms.Default = {
		tpmap:{
			reset:function(){},
			getGraphView:function(){}
		},
		mainViewGridCfg: {},
		gridPanel:null
};
dms.utils = {};
dms.wireFailure = {};
dms.glaywire = {};
dms.widget = {};

htconfig = {
		Color: {
            toolTipBackground: '#DAECF4',
            titleBackground: '#076186',
            titleIconBackground: 'white',
            headerBackground: '#DAECF4',
            highlight: '#4799BC',
        },
        Style: {
            'edge.offset': 3
        },
        Default: {
            toolTipDelay: 100,
            disabledBackground : 'rgba(0,0,0,0.3)'
        }
    };
//引入ht JS
$importjs(ctx + "/jslib/tp/tp-map-inc.js");
$importjs(ctx + "/jslib/ht/ht-dashflow.js");
