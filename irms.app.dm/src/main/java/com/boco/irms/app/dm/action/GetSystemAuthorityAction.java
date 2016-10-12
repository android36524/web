package com.boco.irms.app.dm.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.boco.common.util.except.UserException;
import com.boco.core.ibatis.vo.ServiceActionContext;
import com.boco.transnms.common.dto.SysUser;
import com.boco.transnms.common.dto.base.BoActionContext;
import com.boco.transnms.server.bo.base.BoHomeFactory;
import com.boco.transnms.server.bo.ibo.misc.ISecurityBO;

public class GetSystemAuthorityAction {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	/**
	 * 判断用户是否具有审批权限
	 * @return
	 */
	public Boolean hasApprovalAuthority(HttpServletRequest request){
		ServiceActionContext ac = new ServiceActionContext(request);
		SysUser user = new SysUser();
		user.setCuid(ac.getUserCuid());
		if (ac.getUserName().trim() != null)
			user.setUserName(ac.getUserName());
		else
			user.setUserName(ac.getUserId());
		ISecurityBO securityBO = (ISecurityBO)BoHomeFactory.getInstance().getBO(ISecurityBO.class);
	    try {
			List<String> list = securityBO.getUserActions(new BoActionContext(),user);
			return list.contains("IProjectManagementBO.approval");
		} catch (UserException e) {
			logger.error("查询用户权限失败", e);
		}
	    return null;
	}
}
