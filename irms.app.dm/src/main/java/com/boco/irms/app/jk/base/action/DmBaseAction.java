package com.boco.irms.app.jk.base.action;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import com.boco.core.bean.SpringContextUtil;
import com.boco.core.ibatis.dao.IbatisDAO;
import com.boco.core.utils.exception.UserException;

public class DmBaseAction {
	protected void write(HttpServletResponse response, String result) {
		response.setContentType("text/html");
		// 对输出字符进行设置
		response.setCharacterEncoding("utf-8");
		// 返回前台
		PrintWriter out = null;
		try {
			out = response.getWriter();
			out.write(result);
			out.flush();
		} catch (IOException e) {
			throw new UserException(e);
		} finally {
			if (out != null) {
				out.close();
			}
		}
	}

	public IbatisDAO getIbatisResDAO() {
		return (IbatisDAO) SpringContextUtil.getBean("IbatisResDAO");
	}
}