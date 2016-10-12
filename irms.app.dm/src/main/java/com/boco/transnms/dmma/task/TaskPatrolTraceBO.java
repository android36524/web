package com.boco.transnms.dmma.task;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.boco.component.grid.pojo.GridCfg;
import com.boco.component.grid.ux.bo.XmlTemplateGridBO;
import com.boco.core.ibatis.vo.PageQuery;
import com.boco.core.ibatis.vo.PageResult;
import com.boco.dmma.server.bo.ibo.task.ITaskBO;
import com.boco.transnms.common.dto.base.DataObjectList;
import com.boco.transnms.common.dto.base.GenericDO;
import com.boco.transnms.dmma.utils.DmmaBoFactory;
import com.boco.transnms.dmma.utils.DmmaCommonIntf;
import com.boco.transnms.dmma.utils.DmmaDataTransUtils;

public class TaskPatrolTraceBO  extends XmlTemplateGridBO  implements DmmaCommonIntf<ITaskBO>{

	@Override
	public ITaskBO getBo() {
		return (ITaskBO) DmmaBoFactory.getInstance().getBo();
	}
	
	@Override
	public PageResult getGridData(PageQuery arg0, GridCfg arg1) {
		ITaskBO taskbo = getBo();
		String taskCuid = "";
		 DataObjectList data = taskbo.getBussinesPoints(taskCuid);
	/*	Collection<String> hiddencuids = new ArrayList<String>();
		List<String> longilatis = new ArrayList<String>();
		for (GenericDO pointgdo : pointslist) {
			String hiddeninfo = pointgdo.getAttrString("HIDDENINFO");
			if (hiddeninfo != null && !"".equals(hiddeninfo)) {
				hiddencuids.add(pointgdo.getCuid());
			}
			double newlongititude = pointgdo.getAttrDouble("NEW_LONGITUDE");
			double newlatitude = pointgdo.getAttrDouble("NEW_LATITUDE");
			if (newlatitude != 0 && newlongititude != 0) {
				longilatis.add(pointgdo.getCuid());
			}

		}*/
		List<Map> elements = DmmaDataTransUtils.getElementsFromDataObjLst(data, arg1.getCustCode());
		PageResult results = new PageResult<Map>(elements, data.getCountValue(), arg0.getCurPageNum(), arg0.getPageSize());
		return results;
	}
}
