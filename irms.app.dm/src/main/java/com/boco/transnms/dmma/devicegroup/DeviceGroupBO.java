package com.boco.transnms.dmma.devicegroup;

import java.util.List;
import java.util.Map;

import com.boco.component.grid.pojo.GridCfg;
import com.boco.component.grid.ux.bo.XmlTemplateGridBO;
import com.boco.core.ibatis.vo.PageQuery;
import com.boco.core.ibatis.vo.PageResult;
import com.boco.dmma.server.bo.ibo.pdagroup.IPdaGroupBO;
import com.boco.dmma.server.bo.ibo.task.ITaskBO;
import com.boco.transnms.common.dto.base.DataObjectList;
import com.boco.transnms.dmma.utils.DmmaBoFactory;
import com.boco.transnms.dmma.utils.DmmaCommonIntf;
import com.boco.transnms.dmma.utils.DmmaDataTransUtils;
import com.boco.transnms.server.bo.base.BOHome;

public class DeviceGroupBO extends XmlTemplateGridBO  implements DmmaCommonIntf<IPdaGroupBO>{
	protected String getSQL(){
        String sql = "select CUID,ID,LABEL_CN,(SELECT LABEL_CN FROM DISTRICT WHERE CUID = pg.RELATED_DISTRICT_CUID ) AS RELATED_DISTRICT_CUID from pda_group pg where 1=1 ";      
        /*if(groupName!=null && !"".equals(groupName.getText().trim())){
        	sql+=" and pg.label_cn like '%"+groupName.getText()+"%' ";
        }
        if (related_district != null
				&& !"".equals(related_district.getText().trim())) {
			DataObjectList list = related_district.getViewResults();
			List<String> values = new ArrayList<String>();
			for (GenericDO district : list) {
				values.add(district.getCuid());
			}

			sql += "   and  "
					+ QuerySqlHelper.getQuerySqlByList(
							"pg.related_district_cuid", values);
        }
        
        sql += DmmaUtils.addUserSecurity("pg");
        */
		return sql;
	}
	@Override
	public IPdaGroupBO getBo() {
		return (IPdaGroupBO) DmmaBoFactory.getInstance().getIPdaGroupBO();
	}
	@Override
	public PageResult getGridData(PageQuery arg0, GridCfg arg1) {
		IPdaGroupBO taskbo = getBo(); 
		DataObjectList data = taskbo.queryPdaGroup(getSQL());
		List<Map> elements = DmmaDataTransUtils.getElementsFromDataObjLst(data, arg1.getCustCode());
		PageResult results = new PageResult<Map>(elements, data.getCountValue(), arg0.getCurPageNum(), arg0.getPageSize());
		return results;
	}
	
}
