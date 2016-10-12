//dm命名空间
Dm = {};
Dm.base={};
Dm.base.MessageDialog = function(title,message)
{
	var len = message.length;
	var size = 40;
	var count = 1;
	var outMessage = "";
	if(len / size != 0)
	{
		if(len % size == 0)
		{
			count = len / size;
		}else
		{
			count = len / size +1;
		}
		
	}
	for(var i = 0; i < count; i ++)
	{
		var str = message.substring(i*size,(i+1)*size);
		outMessage = "<p>"+ outMessage + str + "</p>";
	}
	var dialog = new ht.widget.Dialog();
    dialog.setConfig({
        title: title,
        titleAlign: "left",
        closable: true,
        draggable: true,
        content: outMessage,
        buttons: [
             {
                 label: "确定"
             }
         ],
         buttonsAlign: "center",
         action: function(item, e) {
             dialog.hide();
         }
    });
    dialog.show();
};