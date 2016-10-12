package com.boco.irms.app.dm.action;

import java.util.List;
import java.util.Map;

import org.springframework.util.CollectionUtils;

import com.boco.common.util.debug.LogHome;
import com.boco.core.bean.SpringContextUtil;
import com.boco.core.ibatis.dao.IbatisDAO;
import com.boco.core.utils.exception.UserException;
import com.boco.transnms.client.model.base.BoCmdFactory;
import com.boco.transnms.common.dto.AnPos;
import com.boco.transnms.common.dto.base.BoActionContext;
import com.boco.transnms.common.dto.base.BoQueryContext;
import com.boco.transnms.common.dto.base.DataObjectList;

/**
 * @author miao
 */
public class PosAction {
	@SuppressWarnings({ "rawtypes" })
	public void doAdd(List<Map> list, String relatedCabCuid) throws UserException {
		if (CollectionUtils.isEmpty(list)) {
			return;
		}
		try {
			IbatisDAO updatePOS = (IbatisDAO)SpringContextUtil.getBean("IbatisResDAO");
			for (Map simpleMap : list) {
				String cuid = String.valueOf(simpleMap.get("CUID"));
				/*
				 * 因为updateDbos方法是更新pos的所有属性，
				 * 所以需要使用通过pos的cuid值通过getTransElementByCuids方法查询出整各数据
				 * ，然后再把光交接箱的CUID值放进去然后更新POS数据
				 */
			
			String updateSql = "UPDATE AN_POS SET RELATED_CAB_CUID =  '" + relatedCabCuid + "' WHERE CUID='" + cuid + "'";

			updatePOS.updateSql(updateSql);
    		
			}
		} catch (Exception e) {
			LogHome.getLog().error("调用方法出错", e);
			throw new UserException("入库时错误");
		}
	}

	@SuppressWarnings("rawtypes")
	public void doDelete(List<Map> resList) throws UserException {
		if (resList == null || resList.size() == 0) {
			return;
		}

		String posCuid = resList.get(0).get("CUID").toString();
		String relatedCabCuid = resList.get(0).get("relatedCabCuid").toString();
		String sql =
			"(ORIG_EQP_CUID='" + posCuid + "'" + " AND DEST_EQP_CUID='" + relatedCabCuid + "') OR (ORIG_EQP_CUID='" + relatedCabCuid + "' AND DEST_EQP_CUID='"
				+ posCuid + "')";
		DataObjectList list = new DataObjectList();
		try {
			list = (DataObjectList) BoCmdFactory.getInstance().execBoCmd("IJumpFiberBO.getJumpFiberBySql", new BoActionContext(), sql);
		} catch (Exception e) {
			LogHome.getLog().error("doDelete接口调用JumpFiberBySql查询出错", e);
		}

		if (list != null) {
			if (list.size() > 0) {
				LogHome.getLog().error("POS有跳纤，不能够删除关联关系");
				return;
			} else {
				DataObjectList deleteList = new DataObjectList();
				try {
					deleteList = (DataObjectList) BoCmdFactory.getInstance().execBoCmd("IOnuAndPosNeBO.getTransElementByCuids", new BoQueryContext(), posCuid);
					if (deleteList.size() > 0) {
						deleteList.get(0).setAttrValue(AnPos.AttrName.relatedCabCuid, "");
					}
				} catch (Exception e) {
					LogHome.getLog().error("doDelete接口调用IOnuAndPosNeBO.getTransElementByCuids查询出错", e);
				}
				try {
					if (deleteList.size() > 0) {
						BoCmdFactory.getInstance().execBoCmd("IDuctManagerBO.updateDbos", new BoActionContext(), deleteList);
					}
				} catch (Exception e) {
					LogHome.getLog().error("doDelete接口调用getIDuctManagerBO().updateDbos出错", e);
				}
			}
		}
	}


}