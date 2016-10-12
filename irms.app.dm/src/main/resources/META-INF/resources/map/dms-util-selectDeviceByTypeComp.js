
tp.utils.selectDeviceByTypeComp = function() {
	var input = ht.Default.createElement('input');
	var clearButton = document.createElement('button');
	clearButton.style.padding = 0;
	clearButton.style.position = 'absolute';
	clearButton.innerHTML = '<img src="' + ctx
			+ '/resources/tool/cancel.png" style="margin:0;padding0;">';
	clearButton.onclick = function(e) {// 此处有问题，清除当前值后，已经选择过的值还存在param中。
		input.value = '';
		input.id='';	
	};

	var subQueryButton = document.createElement('button');
	subQueryButton.style.padding = 0;
	subQueryButton.style.position = 'absolute';
	subQueryButton.id='subQueryButton';
	subQueryButton.innerHTML = '<img src="' + ctx + '/resources/tool/zoom.png">';
	var contextmenu = null;

	subQueryButton.onclick = function(e) {
		if (!contextmenu) {
			contextmenu = new ht.widget.ContextMenu(Dms.Default.contextMenu.SELECT_DEVICE_TYPE);
			contextmenu.input = input;
		}
		if (!contextmenu.isShowing()) {
			contextmenu.show(e.x, e.y);
		}
	};
	var inputForm = new ht.widget.FormPane();
	inputForm.addRow([ input, clearButton, subQueryButton ], [ 0.2, 20, 20 ]);
	inputForm.setVGap(0);
	inputForm.setHGap(0);
	inputForm.setPadding(0);
	inputForm.cleanAction=function(){
		input.value = '';
		input.id='';
	};
	inputForm.getResult=function(){
		return input;
	};
	

	inputForm.hide = function(){
		if(contextmenu){
			contextmenu.hide(); 
		}
	};
	return inputForm;
};
	

tp.utils.createMenuActionQueryDialog = function(code, parent,callback){
		var dialog = new ht.widget.Dialog(); 
		var c = tp.utils.createQueryPanel(code, dialog,parent.input);
		dialog.setConfig({
            title: "<html><font size=3>"+"查询</font></html>",
            width: 800,
            height: 400,
            titleAlign: "left", 
            draggable: true,
            closable: true,
            content: c
        });
	    dialog.onShown = function(operation) {
	        
	    };
	    dialog.onHidden = function(operation) {
	    };
	    dialog.show();
		return c;
};
