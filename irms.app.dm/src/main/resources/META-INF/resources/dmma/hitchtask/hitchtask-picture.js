var HitchTaskPicPanel = Ext.extend(Ext.Window ,{
	title:"故障图片查看",
	width:800,
	modal:true,
	constructor : function(configer) {
		 configer.layout="fit";
		 configer.items=[
					{
						xtype:"panel",
						header:false,
						height:300,
						width:350,
						border :false,
						listeners :{
						afterrender:function(cmp){
							var proxy = new Ext.dd.DDProxy( configer.data.id);
						}
						},
						html :'<img id=\'' + configer.data.id + '\' src=\'' +  configer.data.src + '\' ></img>',
						title:"故障图片查看"
					}
			];
		HitchTaskPicPanel.superclass.constructor.call(this, configer);
		 
	},
	initComponent:function() {
		HitchTaskPicPanel.superclass.initComponent.call(this);
	},
	  zoom : function(el, offset, type) {

              var width = el.getWidth();

              var height = el.getHeight();

              var nwidth = type ? (width * offset) : (width / offset);

              var nheight = type ? (height * offset) : (height / offset);

              var left = type ? -((nwidth - width) / 2) : ((width - nwidth) / 2);

              var top = type ? -((nheight - height) / 2) : ((height - nheight) / 2);

              el.animate({

                                   height : {

                                          to : nheight,

                                          from : height

                                   },

                                   width : {

                                          to : nwidth,

                                          from : width

                                   },

                                   left : {

                                          by : left

                                   },

                                   top : {

                                          by : top

                                   }

                            }, null, null, 'backBoth', 'motion');

       },
	buttons:[new Ext.Slider({
        width: 214,
        value:50,
        increment: 10,
        minValue: 0,
        maxValue: 100,
		listeners :{
			"changecomplete":function(slider,newValue ){
				if(newValue >50){
					slider.ownerCt.ownerCt.zoom(Ext.get("img_1"), 1.5, true);
				}else{
					slider.ownerCt.ownerCt.zoom(Ext.get("img_1"), 1.5, false);
				}
				
			}
		},
		plugins:new Ext.slider.Tip({
        getText: function(thumb){
            return String.format('<b>{0}%</b>', thumb.value);
        }
    })
    }),{
		text:"关闭",
		handler:function(btn){
			btn.ownerCt.ownerCt.close();
		}
	}],
	buttonAlign:"right",
	height:360
	
	
});