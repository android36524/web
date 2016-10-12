package com.boco.irms.app.dm.gridbo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.boco.component.bo.AbstractTemplateBO;
import com.boco.component.combox.bo.IComboxBO;
import com.boco.component.combox.pojo.ComboxCfg;
import com.boco.component.combox.pojo.ComboxItem;
import com.boco.core.ibatis.vo.PageQuery;
import com.boco.core.ibatis.vo.PageResult;
import com.boco.graphkit.ext.editor.EnumType;
import com.boco.graphkit.ext.editor.EnumTypeManager;

public class EnumTemplateComboxBO extends AbstractTemplateBO implements IComboxBO {
	
	Logger logger = LoggerFactory.getLogger(this.getClass());
	public PageResult<ComboxItem> loadData(ComboxCfg comboxCfg,
			PageQuery pageQuery) {
		Map<String, String> cfgParams = comboxCfg.getCfgParams();
		String code = cfgParams.get("code");
		if(StringUtils.isBlank(code)) {
			throw new RuntimeException("缺少参数code!");
		}
		PageResult<ComboxItem> list = null;		
		try{
			Object[]  gcEnum = EnumTypeManager.getInstance().getEnumTypes(code);

			if(gcEnum != null && gcEnum.length>0){
				List<ComboxItem> result = new ArrayList<ComboxItem>();
				for(Object obj : gcEnum){
					EnumType nm = (EnumType)obj;
					ComboxItem item = new ComboxItem();
					item.setValue(String.valueOf(nm.getValue()));
					item.setText(nm.getDisplayName());
					Map itmeMap = new HashMap();
					itmeMap.put(String.valueOf(nm.getValue()),nm.getDisplayName());
					item.setData(itmeMap);
					result.add(item);
				}
				list = new PageResult<ComboxItem>(result, result.size(), 0,  result.size());
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return list;
	}

}
