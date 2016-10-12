package com.boco.transnms.dmma.devicemanage;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.boco.dmma.server.bo.ibo.pda.IPdaBO;
import com.boco.component.grid.pojo.GridCfg;
import com.boco.component.grid.ux.bo.XmlTemplateGridBO;
import com.boco.core.ibatis.vo.PageQuery;
import com.boco.core.ibatis.vo.PageResult;
import com.boco.dmma.server.bo.ibo.pdagroup.IPdaGroupBO;
import com.boco.dmma.server.bo.ibo.task.ITaskBO;
import com.boco.transnms.common.bussiness.consts.DuctEnum;
import com.boco.transnms.common.dto.base.DataObjectList;
import com.boco.transnms.dmma.utils.DmmaBoFactory;
import com.boco.transnms.dmma.utils.DmmaCommonIntf;
import com.boco.transnms.dmma.utils.DmmaDataTransUtils;
import com.boco.transnms.dmma.utils.DmmaEnumChangeIntf;
import com.boco.transnms.server.bo.base.BOHome;

public class DeviceBO extends XmlTemplateGridBO  implements DmmaCommonIntf<IPdaBO>{
	protected String getSQL(GridCfg arg1){
		String where = " where  1 = 2";
		if(arg1 != null && arg1.getCfgParams() != null && arg1.getCfgParams().get("DEVICE_GROUP_CUID") != null && StringUtils.isNotEmpty(arg1.getCfgParams().get("DEVICE_GROUP_CUID")))
		{
			where = "where GROUP_CUID='"+arg1.getCfgParams().get("DEVICE_GROUP_CUID")+"'";
		}
		String sql="select * from PDA_DEVICE ";
		sql = sql + where;
        return sql;
	}
	@Override
	public IPdaBO getBo() {
		return (IPdaBO) DmmaBoFactory.getInstance().getIPdaBO();
	}
	@Override
	public PageResult getGridData(PageQuery arg0, GridCfg arg1) {
		IPdaBO taskbo = getBo(); 
		DataObjectList data = taskbo.queryPda(getSQL(arg1));
		List<Map> elements = DmmaDataTransUtils.getElementsFromDataObjLst(data, arg1.getCustCode(),new DmmaEnumChangeIntf() {
			@Override
			public void changEnumValue(Object key, Object value, Map element) {
                     if(StringUtils.equalsIgnoreCase((String) key, "DEVICE_STATE")){
                    	 if(DuctEnum.DEVICE_STATE_ENUM.getAllEnum().containsKey(value)){
                    		 element.put(key, DuctEnum.DEVICE_STATE_ENUM.getAllEnum().get(value));
                    	 }
                     }				
			}
		});
		PageResult results = new PageResult<Map>(elements, data.getCountValue(), arg0.getCurPageNum(), arg0.getPageSize());
		return results;
	}
	
}
