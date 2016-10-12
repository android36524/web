package com.boco.irms.app.dm.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.util.StringUtils;
import com.boco.common.util.debug.LogHome;
import com.boco.transnms.client.model.base.BoCmdFactory;
import com.boco.transnms.common.dto.base.BoActionContext;
import com.boco.transnms.common.dto.base.DataObjectList;
import com.boco.transnms.common.dto.base.GenericDO;

/**
 * 查看关联线设施
 * @author wangqin
 *
 */
public class RelatedSystemAction {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	/**
	 * 查看查看关联线设施
	 * @param resourcesCuid
	 * @return
	 */
	public List<Map> getRelatedSystemList(String resourcesCuid){
		String method="IDuctManagerBO.getSystemlistBycuid";
		List<Map> list = new ArrayList<Map>();
		DataObjectList results =new DataObjectList();
        try {
			results = (DataObjectList) BoCmdFactory.getInstance().execBoCmd(method, new BoActionContext(), resourcesCuid);
            if(results !=null && results.size()>0){
            	for (int i = 0; i < results.size(); i++) {
            		GenericDO gdo = results.get(i);
            		Map<String, String> map = new HashMap<String, String>();
                    map.put("CUID", gdo.getCuid());
                    String relatedSysValue = getLabelcnByCuid(gdo.getAttrString("RELATED_SYSTEM_CUID")); 
                    map.put("RELATED_SYSTEM_CUID", relatedSysValue);
                    String relatedBranValue = getLabelcnByCuid(gdo.getAttrString("RELATED_BRANCH_CUID")); 
                    map.put("RELATED_BRANCH_CUID", relatedBranValue);
                    map.put("LABEL_CN", gdo.getAttrString("LABEL_CN"));
                    list.add(map);
            	}
            }
            } catch (Exception ex) {
            	LogHome.getLog().error("查询信息报错", ex);
            }
		return list;
	}
	
	
	/**
     * 通过CUID获取名称
     * @param cuid
     * @return
     */
    public String getLabelcnByCuid(String cuid){
		String name = cuid;
		try {
			GenericDO gdo = (GenericDO) BoCmdFactory.getInstance().execBoCmd("IDuctManagerBO.getObjByCuid",new BoActionContext(), cuid);
			if(gdo != null){
				name = gdo.getAttrString("LABEL_CN");
				if(!StringUtils.isEmpty(name)){
					name = gdo.getAttrString("LABEL_CN");
				}
			}	
		} catch (Exception e) {
			logger.error("转换失败");
		}
		return name;
	}
}
