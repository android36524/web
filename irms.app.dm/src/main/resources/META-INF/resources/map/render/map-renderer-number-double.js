
//浮点型校验组件,对于经纬度需要配置经纬度范围
(function(window,Object,undefined){
	"use strict";
	
	Renderer.DMDoubleRenderer = function() {
		Renderer.DMDoubleRenderer.superClass.constructor.call(this);
	};
	ht.Default.def("Renderer.DMDoubleRenderer", ht.Property, {
	  _minValue: null,
	  _maxValue: null,
	  setUserParam: function(userParam) //自定义的子类方法
	  {
	    if (userParam) {
	      var param = $.trim(userParam);
	      if (param.length > 0) {
	        var objArr = param.split("|");
	        var cAlign = objArr[0]; //位置
	        this._minValue = objArr[1]; //最小值界限
	        this._maxValue = objArr[2]; //最大值界限
	
	        if (cAlign && (cAlign === 'left' || cAlign === 'center' || cAlign === 'right')) {
	          this.setAlign(cAlign);
	        } else {
	          this.setAlign('left');
	        }
	      }
	    }
	  },
	  getValue: function(data, property) //重写了父类的getValue方法
	  {
	    var value = data.getAttr(property.getName());
	    if (value) {
	      var reg = /^(([0-9]+\.[0-9]*[1-9][0-9]*)|([0-9]*[1-9][0-9]*\.[0-9]+)|([0-9]*[1-9][0-9]*))$/;
	      if (!reg.test(value)) {
	        data.setAttr(property.getName(), "");
	        return "";
	      } else {
	        if (this._minValue && this._maxValue) {
	          if (this._minValue <= value < this._maxValue) {
	            return value;
	          } else {
	            data.setAttr(property.getName(), "");
	            return "";
	          }
	        } else {
	          return value;
	        }
	      }
	    }
	  },
	  beginEditing: function(info) {
	    var self = this,
	      data = info.data,
	      view = info.view,
	      input = document.createElement("input"),
	      //            rect = info.editorRect,
	      rect = info.rect,
	      view = info.view;
	    input.style.position = "absolute";
	    input.style.left = rect.x + view.tx() + "px";
	    input.style.top = rect.y + view.ty() + "px";
	    input.style.width = rect.width + "px";
	    input.style.height = rect.height + "px";
	    input.style.margin = 0;
	    input.style.padding = 0;
	    input.style.border = 0;
	    input.outline = 0;
	
	    //        document.body.appendChild(input);
	    view.getView().appendChild(input);
	    view.setCurrentEditor(input);
	
	    var endEditing = function() {
	      if (self.validate(input.value)) {
	        view.endEditing();
	      }
	    };
	    input.onblur = function() {
	      view.endEditing();
	    };
	    input.onkeydown = function(e) {
	      if (e.keyCode == 13) {
	        endEditing();
	      }
	    };
	    input.commitValue = function() {
	      if (self.validate(input.value)) {
	        data.a(self.getName(), input.value);
	      } else {
	        data.a(self.getName(), "");
	      }
	      //            document.body.removeChild(input);
	      view.getView().removeChild(input);
	    };
	    input.oninput = function(e) {
	      if (!self.validate(input.value)) {
	        input.style.color = "red";
	      } else {
	        input.style.color = "black";
	      }
	    };
	    input.focus();
	    input.value = data.a(self.getName()) || "";
	  },
	  validate: function(value) {
	    var reg = /^(([0-9]+\.[0-9]*[1-9][0-9]*)|([0-9]*[1-9][0-9]*\.[0-9]+)|([0-9]*[1-9][0-9]*))$/;
	    if (!reg.test(value)) {
	      return false;
	    } else {
	      if (this._minValue && this._maxValue) {
	        if (this._minValue <= value < this._maxValue) {
	          return true;
	        } else {
	          return false;
	        }
	      } else {
	        return true;
	      }
	    }
	  }
	});
})(this,Object);