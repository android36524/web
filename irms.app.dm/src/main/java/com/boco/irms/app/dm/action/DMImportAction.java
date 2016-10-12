package com.boco.irms.app.dm.action;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jxl.Workbook;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.alibaba.fastjson.JSON;
import com.boco.common.util.debug.LogHome;
import com.boco.core.ibatis.vo.ServiceActionContext;
import com.boco.irms.app.utils.ImportConverter;
import com.boco.transnms.common.dto.Accesspoint;
import com.boco.transnms.common.dto.AnOnu;
import com.boco.transnms.common.dto.AnPos;
import com.boco.transnms.common.dto.DuctSeg;
import com.boco.transnms.common.dto.DuctSystem;
import com.boco.transnms.common.dto.Fiber;
import com.boco.transnms.common.dto.FiberCab;
import com.boco.transnms.common.dto.FiberDp;
import com.boco.transnms.common.dto.FiberJointBox;
import com.boco.transnms.common.dto.HangWall;
import com.boco.transnms.common.dto.HangWallSeg;
import com.boco.transnms.common.dto.Inflexion;
import com.boco.transnms.common.dto.InterWire;
import com.boco.transnms.common.dto.Manhle;
import com.boco.transnms.common.dto.OverlayResource;
import com.boco.transnms.common.dto.Pole;
import com.boco.transnms.common.dto.PolewaySeg;
import com.boco.transnms.common.dto.PolewaySystem;
import com.boco.transnms.common.dto.Room;
import com.boco.transnms.common.dto.Stone;
import com.boco.transnms.common.dto.StonewaySeg;
import com.boco.transnms.common.dto.StonewaySystem;
import com.boco.transnms.common.dto.SysUser;
import com.boco.transnms.common.dto.UpLine;
import com.boco.transnms.common.dto.UpLineSeg;
import com.boco.transnms.common.dto.WireRemain;
import com.boco.transnms.common.dto.WireSeg;
import com.boco.transnms.common.dto.WireSystem;
import com.boco.transnms.common.dto.WireToDuctline;
import com.boco.transnms.common.dto.dm.ImpExpInfo;
import com.boco.transnms.server.common.cfg.SystemEnv;

@Controller
public class DMImportAction {
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "dm/import.do")
	public void importData(HttpServletRequest request, HttpServletResponse response) throws Exception {

		LogHome.getLog().info("开始解析");
		ServiceActionContext ac = new ServiceActionContext(request);
		String key = request.getParameter("key");
		// 转型为MultipartHttpRequest：
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		List logList = new ArrayList();
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			MultipartFile file = multipartRequest.getFile("file");
			Workbook workbook = Workbook.getWorkbook(file.getInputStream());
			SysUser user = new SysUser();
			user.setCuid(ac.getUserCuid());
			ImportConverter imp = new ImportConverter(user);
			List list = new ArrayList();
			if (key.equals(Manhle.CLASS_NAME)) {
				list = imp.importManhle(workbook, "");
			} else if (key.equals(Pole.CLASS_NAME)) {
				list = imp.importPole(workbook, "");
			} else if (key.equals(Stone.CLASS_NAME)) {
				list = imp.importStone(workbook, "");
			} else if (key.equals(Inflexion.CLASS_NAME)) {
				list = imp.importInflexion(workbook, "");
			} else if (key.equals(FiberCab.CLASS_NAME)) {
				list = imp.importFiberCab(workbook, "");
			} else if (key.equals(FiberDp.CLASS_NAME)) {
				list = imp.importFiberDp(workbook, "");
			} else if (key.equals(FiberJointBox.CLASS_NAME)) {
				list = imp.importFiberJointBox(workbook, "");
			} else if (key.equals(Accesspoint.CLASS_NAME)) {
				list = imp.importAccesspoint(workbook, "");
			} else if (key.equals(WireSystem.CLASS_NAME)) {
				list = imp.importWireSystem(workbook);
			} else if (key.equals(WireSeg.CLASS_NAME)) {
				list = imp.importWireSeg(workbook);
			} else if (key.equals(DuctSystem.CLASS_NAME)) {
				list = imp.importDuctSystem(workbook);
			} else if (key.equals(DuctSeg.CLASS_NAME)) {
				list = imp.importDuctSeg(workbook);
			} else if (key.equals(PolewaySystem.CLASS_NAME)) {
				list = imp.importPolewaySystem(workbook);
			} else if (key.equals(StonewaySystem.CLASS_NAME)) {
				list = imp.importStonewaySystem(workbook);
			} else if (key.equals(UpLine.CLASS_NAME)) {
				list = imp.importUpLine(workbook);
			} else if (key.equals(HangWall.CLASS_NAME)) {
				list = imp.importHangWall(workbook);
			} else if (key.equals(PolewaySeg.CLASS_NAME)) {
				list = imp.importPolewaySeg(workbook);
			} else if (key.equals(UpLineSeg.CLASS_NAME)) {
				list = imp.importUplineSeg(workbook);
			} else if (key.equals(HangWallSeg.CLASS_NAME)) {
				list = imp.importHangwallSeg(workbook);
			} else if (key.equals(StonewaySeg.CLASS_NAME)) {
				list = imp.importStonewaySeg(workbook);
			} else if (key.equals(WireRemain.CLASS_NAME)) {
				list = imp.importWireRemain(workbook);
			} else if (key.equals(WireToDuctline.CLASS_NAME)) {
				list = imp.importWireToDuctline(workbook);
			} else if (key.equals(Fiber.CLASS_NAME)) {
				list = imp.importWireFiber(workbook);
			} else if (key.equals(Fiber.CLASS_NAME)) {
				list = imp.importFiber(workbook);
			} else if (key.equals(Room.CLASS_NAME)) {
				list = imp.importRoom(workbook);
			} else if (key.equals(OverlayResource.CLASS_NAME)) {
				list = imp.importOverlayResource(workbook);
			} else if (key.equals("LongtitudeLatitude")) {
				list = imp.importLongitudeLattitude(workbook);
			} else if (key.equals("Onubox")) {
				list = imp.importOnubox(workbook);
			} else if (key.equals(AnPos.CLASS_NAME)) {
				list = imp.importPos(workbook);
			} else if (key.equals(AnOnu.CLASS_NAME)) {
				list = imp.importOnu(workbook);
			} else if (key.equals(InterWire.CLASS_NAME)) {
				list = imp.importIntertWire(workbook);
			}

			if (list != null && list.size() > 0) {
				logList.addAll(list);
			} else {
				logList.add("数据导入成功");
			}
			result.put("success", true);
		} catch (Exception e) {
			e.printStackTrace();
			logList.add("请选择正确的excel文件");
			logList.add(e.getMessage());
			logList.add(e.getStackTrace().toString());
			LogHome.getLog().info(e.getMessage());
			LogHome.getLog().info(e.getStackTrace().toString());
			result.put("success", false);
		}

		String msg = "";
		for (int i = 0; i < logList.size(); i++) {
			if (logList.get(i) != null && logList.get(i) instanceof ImpExpInfo) {
				String impExpInfo = dealImpExpInfo((ImpExpInfo) logList.get(i));
				if ("".equals(msg)) {
					msg = impExpInfo;
				} else {
					msg = msg + "\r\n" + impExpInfo;
				}
				continue;
			} else if (logList.get(i) != null && logList.get(i).toString().indexOf("<font") > -1) {
				String str = logList.get(i).toString();
				str = str.replaceAll("</font>", "");
				String subStr = str.substring(str.indexOf("<"), str.indexOf(">") + 1);
				str = str.replaceAll(subStr, "");
				if ("".equals(msg)) {
					msg = str;
				} else {
					msg = msg + "\r\n" + str;
				}
				continue;
			}else if ("".equals(msg)) {
				msg = logList.get(i).toString();
			} else {
				msg = msg + "\r\n";
			}
		}
		LogHome.getLog().info(msg);
		result.put("msg", msg);
		response.setContentType("text/html;charset=UTF-8");
		response.getWriter().write(JSON.toJSONString(result));
	}

	@RequestMapping(value = "dm/export.do")
	public void export(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.setCharacterEncoding("GBK");
		String template = java.net.URLDecoder.decode(request.getParameter("name"), "utf-8");
		String te = new String(template.getBytes("GBK"), "ISO-8859-1");
		LogHome.getLog().info("文件名template：" + template + "======" + "转码后te：" + te);

		String filePath = getFilePath(request) + template + ".xls";
		filePath = StringUtils.replace(filePath, "//", File.separator);
		filePath = StringUtils.replace(filePath, "\\", File.separator);
		LogHome.getLog().info("导出文件路径filePath:" + filePath);

		response.setContentType("application/msexcel");
		response.setHeader("Content-disposition", "attachment;filename=" + te + ".xls");

		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		try {
			bis = new BufferedInputStream(new FileInputStream(filePath));
			bos = new BufferedOutputStream(response.getOutputStream());
			byte[] buff = new byte[2048];
			int bytesRead;
			while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
				bos.write(buff, 0, bytesRead);
			}
		} catch (Exception e) {
			LogHome.getLog().info("出错template：" + template + "======" + "出错te：" + te, e);
		} finally {
			if (bis != null) {
				bis.close();
			}
			if (bos != null) {
				bos.close();
			}
		}
	}

	private String getFilePath(HttpServletRequest request) {
		String type = request.getParameter("type");
		type = StringUtils.isEmpty(type) ? "" : type;
		String filePath = SystemEnv.getPathEnv("IRMS_SERVER_HOME");
		if (filePath.endsWith(File.separator)) {
			if (!type.equalsIgnoreCase("pon")) {
			filePath += ("conf/import/线路数据/");
		} else {
				filePath += ("conf/import/PON数据/");
			}
		} else {
			if (!type.equalsIgnoreCase("pon")) {
			filePath += ("/conf/import/线路数据/");
			} else {
				filePath += ("/conf/import/PON数据/");
			}
		}
		return filePath;
	}

	private String dealImpExpInfo(ImpExpInfo _impExpInfo) {
		if (_impExpInfo == null) {
			return "";
		}
		String result = "";
		result = "非法数据：第" + _impExpInfo.getRowNum() + "行，" + _impExpInfo.getName() + _impExpInfo.getPostInfo() + "!";
		return result;
	}
}