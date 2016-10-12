package com.boco.irms.app.dm.bo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.boco.core.utils.exception.UserException;
import com.boco.transnms.client.model.base.BoCmdFactory;
import com.boco.transnms.common.dto.base.BoActionContext;
import com.boco.transnms.common.dto.base.DataObjectList;
import com.boco.transnms.common.dto.base.GenericDO;
import com.boco.transnms.server.bo.base.BoHomeFactory;
import com.boco.transnms.server.bo.ibo.dm.IDuctManagerBO;

public class DmBoHelper {
	private static DmBoHelper instance = new DmBoHelper();
	private DmBoHelper(){}
	
	public static DmBoHelper getInstance(){
		return instance;
	}
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public DataObjectList getWireSegList(String methodName,String wireSystemCuid){
		DataObjectList results = null;
		try {
			Map map = executeCmdReturnMap(methodName, wireSystemCuid);
			results = (DataObjectList) map.get("segsList");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		List<Map> list = new ArrayList<Map>();
		Map<String, GenericDO> cuidMap = new HashMap<String, GenericDO>();
		
		ArrayList<String> cuids = new ArrayList<String>();
		cuids.add(wireSystemCuid);//关联系统CUID
		
		if (results != null && results.size() > 0) {
			// 获取段的起止点，转换成对象
			for (int i = 0; i < results.size(); i++) {
				GenericDO gdo = results.get(i);
				String origId = gdo.getAttrString("ORIG_POINT_CUID");
				String destId = gdo.getAttrString("DEST_POINT_CUID");
				if (StringUtils.isNotEmpty(origId) && !cuids.contains(origId)) {
					cuids.add(origId);
				}
				if (StringUtils.isNotEmpty(destId) && !cuids.contains(destId)) {
					cuids.add(destId);
				}
			}
			try {
				IDuctManagerBO bo = (IDuctManagerBO) BoHomeFactory.getInstance().getBO("IDuctManagerBO");
				cuidMap = bo.getGenericDOMapByCuids(cuids);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
			for (int i = 0; i < results.size(); i++) {
				GenericDO gdo = results.get(i);
				String origId = gdo.getAttrString("ORIG_POINT_CUID");
				String destId = gdo.getAttrString("DEST_POINT_CUID");
				gdo.setAttrValue("OBJECTID", gdo.getObjectNum());
				gdo.setAttrValue("CREATE_TIME", gdo.getCreateTime());
				gdo.setAttrValue("LAST_MODIFY_TIME", gdo.getLastModifyTime());
				gdo.setAttrValue("RELATED_SYSTEM_CUID", cuidMap.get(wireSystemCuid).getAttrString("LABEL_CN"));
				gdo.setAttrValue("ORIG_POINT_CUID",cuidMap.get(origId));
				gdo.setAttrValue("DEST_POINT_CUID",cuidMap.get(destId));
			}
		}
		return results;
	}
	
	private  Map executeCmdReturnMap(String method,String sql){
		//从传输服务查询数据
		BoActionContext actionContext = new BoActionContext();
		actionContext.setUserId("SYS_USER-0");
		Map  results = new HashMap();
		try {
			results = (Map)BoCmdFactory.getInstance().execBoCmd(method, actionContext,sql);
		} catch (Exception e) {
			throw new UserException(e);
		}
		return results;
	}
}
