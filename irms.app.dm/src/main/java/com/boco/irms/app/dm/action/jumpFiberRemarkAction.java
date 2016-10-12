package com.boco.irms.app.dm.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.boco.core.utils.exception.UserException;
import com.boco.transnms.common.dto.JumpFiber;
import com.boco.transnms.common.dto.base.BoActionContext;
import com.boco.transnms.common.dto.base.DataObjectList;
import com.boco.transnms.server.bo.base.BoHomeFactory;
import com.boco.transnms.server.bo.ibo.dm.IDuctManagerBO;

@Controller
@RequestMapping(value = "/jumpFiberRemarkAction")
public class jumpFiberRemarkAction {
	private IDuctManagerBO getDuctManagerBO() {
		return BoHomeFactory.getInstance().getBO(IDuctManagerBO.class);
	}

	public List<Map<String, String>> getJumpFIberNames(String cuid,
			String labelCn, String remark) {
		List<Map<String, String>> JumpFIberNames = new ArrayList<Map<String, String>>();
		String sql = JumpFiber.AttrName.relatedSiteCuid + "='" + cuid + "'";
		if (labelCn != null && labelCn.length() > 0) {
			sql = sql + " AND LABEL_CN LIKE '%" + labelCn + "%'";
		}
		if (remark != null && remark.length() > 0) {
			sql = sql + " AND REMARK LIKE '%" + remark + "%'";
		}
		try {
			DataObjectList jumpFibers = getDuctManagerBO().getObjectsBySql(sql,
					new JumpFiber());
			if (jumpFibers != null && jumpFibers.size() > 0) {
				for (int i = 0; i < jumpFibers.size(); i++) {
					Map<String, String> names = new HashMap<String, String>();
					JumpFiber jf = (JumpFiber) jumpFibers.get(i);
					names.put("JUMP_FIBER_CUID", jf.getCuid());
					names.put("LABEL_CN", jf.getLabelCn());
					names.put("REMARK", jf.getRemark());
					JumpFIberNames.add(names);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new UserException("查询跳纤数据失败，请联系后台人员查看日志！");
		}
		return JumpFIberNames;
	}

	public String saveJumpFIberNames(List<Map<String, String>> array) {
		String msg = "success";
		DataObjectList dol = new DataObjectList();
		if (array != null && array.size() > 0) {
			for (int i = 0; i < array.size(); i++) {
				Map<String, String> object = array.get(i);
				String cuid = object.get("JUMP_FIBER_CUID");
				JumpFiber jf = (JumpFiber) getDuctManagerBO().getObjByCuid(
						new BoActionContext(), cuid);
				String remark = object.get("REMARK");
				if (!jf.getRemark().equals(remark)) {
					jf.setRemark(remark);
					dol.add(jf);
				}
			}
			try {
				getDuctManagerBO().updateDbos(new BoActionContext(),dol);
			} catch (Exception e) {
				msg="error";
				e.printStackTrace();
				throw new UserException("保存失败，请联系后台人员查看日志！");
			}
		}
		return msg;
	}
}
