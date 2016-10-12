package com.boco.irms.app.dm.fileimport.action;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.alibaba.fastjson.JSON;
import com.boco.core.bean.SpringContextUtil;
import com.boco.core.ibatis.dao.IbatisDAO;
import com.boco.core.ibatis.vo.Record;
import com.boco.core.ibatis.vo.ServiceActionContext;
import com.boco.core.spring.SysProperty;
import com.boco.core.utils.exception.UserException;
import com.boco.core.utils.id.CUIDHexGenerator;
import com.boco.core.utils.lang.Assert;
import com.boco.core.utils.lang.TimeFormatHelper;
import com.boco.transnms.server.common.cfg.SystemEnv;

@Controller
public class FileImportAction {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@RequestMapping(value = "dm/fileImport.do")
	public void execute(HttpServletRequest request, HttpServletResponse response, @RequestParam
	String type, @RequestParam
	String relatedServiceCuid, @RequestParam
	String validateSize) throws IOException {
		ServiceActionContext ac = new ServiceActionContext(request);
		String msg = "";
		// 转型为MultipartHttpRequest：
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		// 获得文件：
		MultipartFile file = multipartRequest.getFile("file");
		if (validateSize.equals("true")) {
			long size = file.getSize();
			if (size > 20 * 1024 * 1024) {
				msg = "上传文件大小超过20M";
				throw new UserException(msg);
			}
		}
		
		// 获得文件名：
		String fileName = file.getOriginalFilename();
		String oldFileName = fileName.replace(".", ",");
		String[] oldFileNameArray = oldFileName.split(",");
		int length = oldFileNameArray.length;
		String endName = oldFileNameArray[length - 1];
		String newFileName = System.currentTimeMillis() + "." + endName;

		// 上传模板文件
		String tempFilePath = SysProperty.getInstance().getValue("sysfile");
		String date = TimeFormatHelper.getFormatDate(new Date(), "yyMMdd");
		if (!tempFilePath.endsWith(File.separator)) {
			tempFilePath = tempFilePath + File.separator + date;
		} else {
			tempFilePath = tempFilePath + date;
		}
		tempFilePath = StringUtils.replace(tempFilePath, "//", File.separator);
		tempFilePath = StringUtils.replace(tempFilePath, "/", File.separator);
		tempFilePath = StringUtils.replace(tempFilePath, "\\", File.separator);
		String filePath = this.getRealFilePath(tempFilePath, request);
		String modelPath = tempFilePath + File.separator + newFileName;

		File d = new File(filePath);
		if (!d.exists()) {
			d.mkdirs();
		}
		filePath += (File.separator + newFileName);
		this.logger.debug("==filePath==" + filePath);

		Record record = new Record("T_SYS_FILE");
		record.addColValue("CUID", CUIDHexGenerator.getInstance().generate("T_SYS_FILE"));
		record.addColValue("ATTACH_TYPE", type);
		record.addColValue("RELATED_SERVICE_CUID", relatedServiceCuid);
		record.addColValue("ATTACH_FILENAME", fileName);
		record.addColValue("ATTACH_ADDRESS", modelPath);
		record.addColValue("ADD_USER_CUID", ac.getUserId());
		record.addColValue("ADD_USER_NAME", ac.getUserName());
		record.addColValue("CREATE_TIME", new Date());

		FileOutputStream fout = null;
		JSONObject jo = new JSONObject();
		try {
			fout = new FileOutputStream(filePath);
			fout.write(file.getBytes());
			fout.flush();
			fout.close();
			this.getIbatisDAO().insertDynamicTable(record);
			jo.put("success", true);
			jo.put("filepath", modelPath);
		} catch (Exception e) {
			msg = msg + "上传出错，请与管理员联系！";
			modelPath = null;
			fout = null;
			e.printStackTrace();
		} finally {
			jo.put("msg", msg);
			this.write(response, jo.toString());
		}
	}

	/**
	 * 下载文件
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "dm/download.do")
	public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		try {
			String fileName = request.getParameter("fileName");
			if (!StringUtils.isEmpty(fileName)) {
				fileName = URLDecoder.decode(fileName, "UTF-8");
			}

			String tempFilePath = request.getParameter("file");
			tempFilePath = URLDecoder.decode(tempFilePath, "UTF-8");
			if (System.getProperty("os.name").startsWith("Windows")) {
				tempFilePath = "file:" + StringUtils.replace(tempFilePath, "//", File.separator);
			} else {
				tempFilePath = StringUtils.replace(tempFilePath, "//", File.separator);
			}
			tempFilePath = StringUtils.replace(tempFilePath, "//", File.separator);
			tempFilePath = StringUtils.replace(tempFilePath, "/", File.separator);
			tempFilePath = StringUtils.replace(tempFilePath, "\\", File.separator);
			String filePath = this.getRealFilePath(tempFilePath, request);
			filePath = StringUtils.replace(filePath, "//", File.separator);
			this.logger.debug("==filePath==" + filePath);

			response.setContentType("application/octet-stream;charset=utf-8");
			response.setHeader("Content-disposition", "attachment;filename=" + fileName);

			bis = new BufferedInputStream(new FileInputStream(filePath));
			bos = new BufferedOutputStream(response.getOutputStream());
			byte[] buff = new byte[2048];
			int bytesRead = 0;
			while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
				bos.write(buff, 0, bytesRead);
			}

			response.flushBuffer();
			bis.close();
			bos.close();
		} catch (Exception e) {
			this.logger.debug("下载文件出错，请与管理员联系！");
			e.printStackTrace();
		} finally {
			if (bis != null) {
				bis.close();
			}
			if (bos != null) {
				bos.close();
			}
		}
	}

	/**
	 * 图片预览
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "dm/readImg.do")
	public void readImg(HttpServletRequest request, HttpServletResponse response) throws Exception {
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		try {
			String fileName = request.getParameter("fileName");
			if (!StringUtils.isEmpty(fileName)) {
				fileName = URLDecoder.decode(fileName, "UTF-8");
			}

			String tempFilePath = request.getParameter("file");
			tempFilePath = URLDecoder.decode(tempFilePath, "UTF-8");
			tempFilePath = StringUtils.replace(tempFilePath, "//", File.separator);
			tempFilePath = StringUtils.replace(tempFilePath, "/", File.separator);
			tempFilePath = StringUtils.replace(tempFilePath, "\\", File.separator);
			String filePath = this.getRealFilePath(tempFilePath, request);
			filePath = StringUtils.replace(filePath, "//", File.separator);
			this.logger.debug("==filePath==" + filePath);

			response.setContentType("image/jpeg;image/jpg;image/gif;image/png;charset=utf-8");
			response.setHeader("Content-disposition", "attachment;filename=" + fileName);

			bis = new BufferedInputStream(new FileInputStream(filePath));
			bos = new BufferedOutputStream(response.getOutputStream());
			byte[] buff = new byte[2048];
			int bytesRead = 0;
			while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
				bos.write(buff, 0, bytesRead);
			}

			response.flushBuffer();
			bis.close();
			bos.close();
		} catch (Exception e) {
			this.logger.debug("下载文件出错，请与管理员联系！");
			e.printStackTrace();
		} finally {
			if (bis != null) {
				bis.close();
			}
			if (bos != null) {
				bos.close();
			}
		}
	}

	/**
	 * 删除附件
	 * 
	 * @param list
	 */
	@SuppressWarnings({ "deprecation", "rawtypes" })
	@RequestMapping(value = "dm/deleteWf.do")
	public void deleteWf(HttpServletRequest request, HttpServletResponse response, String formListStr) {
		List<Map> list = com.alibaba.fastjson.JSONObject.parseArray(formListStr, Map.class);
		String cuid = "";
		List<String> cuidList = new ArrayList<String>();
		for (Map<String, String> map : list) {
			cuid = map.get("id");
			String address = map.get("address");
			File file = new File(address);
			file.delete();
			cuidList.add(cuid);
		}
		Map<String, List<String>> map = new HashMap<String, List<String>>();
		map.put("list", cuidList);
		this.getIbatisDAO().getSqlMapClientTemplate().delete("DmTSysFile.deleteWf", map);
	}

	private String getRealFilePath(String file, HttpServletRequest request) {
		String filePath = SystemEnv.getPathEnv("IRMS_SERVER_HOME");
		filePath = StringUtils.replace(filePath, "/", File.separator);
		filePath = StringUtils.replace(filePath, "\\", File.separator);
		String realFilePath = filePath.substring(0, filePath.lastIndexOf(File.separator) + 1);
		if (file.startsWith(".." + File.separator)) {
			realFilePath += file.substring(3);
		} else {
			realFilePath += file;
		}

		realFilePath = StringUtils.replace(realFilePath, "//", File.separator);
		return realFilePath;
	}
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "dm/getSysFileByServiceId.do",method=RequestMethod.POST)
	public void getSysFileByServiceId(HttpServletRequest request, HttpServletResponse response,  @RequestParam String relatedServiceCuid) throws IOException {
		Assert.notNull(relatedServiceCuid,"获取附件关系失败,所属资源ID为空！");
		String querySql = "select CUID, RELATED_SHEET_CUID, ATTACH_TYPE, ATTACH_FILENAME, ATTACH_ADDRESS,"
            +" ADD_USER_CUID, ADD_USER_NAME, CREATE_TIME, EOMS_ID, RELATED_ORDER_CUID,"
            +" REALTED_TASK_CUID, RELATED_SERVICE_CUID, RELATED_PRODUCT_CUID FROM T_SYS_FILE WHERE "
			+" RELATED_SERVICE_CUID = '"+relatedServiceCuid+ "'";
		List result = this.getIbatisDAO().querySql(querySql);
		this.write(response, JSON.toJSONString(result));
	}
	
	private void write(HttpServletResponse response, String result) {
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

	private IbatisDAO getIbatisDAO() {
		return (IbatisDAO) SpringContextUtil.getBean("IbatisResDAO");
	}
}