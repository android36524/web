//
//tp.grid = {};
//tp.grid.plugins = {};
//tp.grid.plugins.query = {};
//
//tp.grid.plugins.query.ManhleQueryPanel = function(){
//    	var basicFormPane = new ht.widget.FormPane();    
////      formPane.getItemBorderColor = function(){return 'red';};
////      formPane.getRowBorderColor = function(){return 'yellow';};
//        basicFormPane.addRow([{element: '名称:', align: 'left'}, tp.utils.creatInput('','input'), 
//        				 {element: '产权归属:', align: 'left'}, tp.utils.creatComboBox({values:[0,1,2,3], labels:['全部','自建','共建','合建']}), 
//        				 '人手井类型:', tp.utils.creatComboBox({values:[0,1,2,3], labels:['全部','自建','共建','合建']}),
//        				 '维护方式:', tp.utils.creatInput('','input'),null,tp.utils.creatButton('','查询')],
//        				[0.1, 0.2, 0.1, 0.2, 0.1, 0.2, 0.1, 0.2, 10, 80]);
//        basicFormPane.addRow(['用途:', tp.utils.creatComboBox({values:[0,1,2,3], labels:['全部','自建','共建','合建']}), 
//        				 '所属区域:', tp.utils.creatComboBox({values:[0,1,2,3], labels:['全部','自建','共建','合建']}),
//        				'维护单位:', tp.utils.creatInput('','input'), 
//        				 '所有权人:', tp.utils.creatInput('input3','input'),null ,tp.utils.creatButton('','清空')],
//        				[0.1, 0.2, 0.1, 0.2, 0.1, 0.2, 0.1, 0.2, 10, 80]);
//        return basicFormPane;
//};