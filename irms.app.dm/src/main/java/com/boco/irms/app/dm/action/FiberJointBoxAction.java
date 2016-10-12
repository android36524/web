package com.boco.irms.app.dm.action;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.util.StringUtils;
import com.boco.core.utils.exception.UserException;
import com.boco.irms.app.utils.WebDMUtils;
import com.boco.transnms.client.model.base.BoCmdFactory;
import com.boco.transnms.common.dto.FiberJointBox;
import com.boco.transnms.common.dto.base.BoActionContext;
import com.boco.transnms.common.dto.base.DataObjectList;
import com.boco.transnms.common.dto.base.GenericDO;
/**
 * 光接头盒设备管理
 * @author wangqin
 *
 */
public class FiberJointBoxAction {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	/**
	 * 根据接头盒所在位置设置经纬度
	 * @param fiberJBLists
	 */
	public void modifyFiberJointBoxByLocation(List<Map>  fiberJBLists){
		String method = "IFiberJointBoxBO.modifyFiberJointBoxsWithLocation";
		DataObjectList list = new DataObjectList();
		if(fiberJBLists !=null && fiberJBLists.size()>0){
			for(int i=0;i<fiberJBLists.size();i++){
				Map fiberJBObjArr =  fiberJBLists.get(i);
				String fiberJointBoxCuid=fiberJBObjArr.get("CUID").toString();
				String fjbClassName=GenericDO.parseClassNameFromCuid(fiberJointBoxCuid);
				GenericDO gdo=WebDMUtils.createInstanceByClassName(fjbClassName,fiberJBObjArr);
				FiberJointBox jointBox = (FiberJointBox) gdo;
		        GenericDO locationGDO = new GenericDO();
		        String relatedLocationCuid = jointBox.getRelatedLocationCuid();
		        if(!StringUtils.isEmpty(relatedLocationCuid)){
		        	String className = relatedLocationCuid.split("-")[0];
			        locationGDO = locationGDO.createInstanceByClassName();
			        locationGDO.setClassName(className);
			        locationGDO.setCuid(relatedLocationCuid);
			        jointBox.setAttrValue(FiberJointBox.AttrName.relatedLocationCuid, locationGDO);
		        }
		        jointBox.removeAttr("LAST_MODIFY_TIME");
		        list.add(jointBox);
			}
		}
		try {
    		BoCmdFactory.getInstance().execBoCmd(method, new BoActionContext(),list);
		} catch (Exception e) {
			logger.error("修改光接头盒设备管理失败",e);
			throw new UserException(e);
		}
	}
}
