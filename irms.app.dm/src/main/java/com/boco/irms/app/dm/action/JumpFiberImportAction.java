package com.boco.irms.app.dm.action;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jxl.Workbook;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.alibaba.fastjson.JSON;
import com.boco.common.util.debug.LogHome;
import com.boco.core.ibatis.vo.ServiceActionContext;
import com.boco.irms.app.utils.ImportConverter;
import com.boco.transnms.common.dto.SysUser;
import com.boco.transnms.common.dto.dm.ImpExpInfo;
import com.boco.transnms.server.common.cfg.SystemEnv;
@Controller
public class JumpFiberImportAction {
	@RequestMapping(value="dm/jumpfiberimport.do")
	public void importData(HttpServletRequest request,
			HttpServletResponse response) throws Exception{ 

		LogHome.getLog().info("开始解析");
		ServiceActionContext ac = new ServiceActionContext(request);
		String key=request.getParameter("key");
		// 转型为MultipartHttpRequest：   
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;  
		ArrayList logList=new ArrayList();
		Map<String,Object> result = new HashMap<String,Object>();
		try{
			MultipartFile file = multipartRequest.getFile("file");
			Workbook workbook=Workbook.getWorkbook(file.getInputStream());
			SysUser user=new SysUser();
			user.setCuid(ac.getUserCuid());
			ImportConverter imp=new ImportConverter(user);
			ArrayList list = new ArrayList();
			if(key.equals("ODFPortImport")){
				list=imp.importXdfJump(workbook);
			}else if(key.equals("ODFWireImport")){
				list=imp.importODFPortFiberConnect(workbook);	
		    }else if(key.equals("ODFFiberImport")){
		    	list=imp.importDdfPortRemark(workbook);
		    }else if(key.equals("JointBoxFiberImport")){
		    	list=imp.importFBoxFiberConn(workbook);
		    }else if(key.equals("FiberCabFiberDpImport")){
		    	list=imp.importJumpFiberConn(workbook);
			}else if(key.equals("FiberCabFiberDpPosImport")){
				list=imp.importJumpFiberPosConn(workbook);
			}
			
			if(list!=null&&list.size()>0){
				logList.addAll(list);
			}else{
				logList.add("数据导入成功");
			}
			result.put("success",true);
		}catch(Exception e){
			e.printStackTrace();
			logList.add("请选择正确的excel文件");
			logList.add(e.getMessage());
			logList.add(e.getStackTrace().toString());
			LogHome.getLog().info(e.getMessage());
			LogHome.getLog().info(e.getStackTrace().toString());
			result.put("success",false);
		}
		String msg="";
		for (int i = 0; i < logList.size(); i++) {
			if (logList.get(i) != null && logList.get(i) instanceof ImpExpInfo) {
				String impExpInfo = dealImpExpInfo((ImpExpInfo) logList.get(i));
				if ("".equals(msg)) {
					msg = impExpInfo;
				} else {
					msg = msg + "\r\n" + impExpInfo;
				}
				continue;
			} else if (logList.get(i) != null
					&& logList.get(i).toString().indexOf("<font") > -1) {
				String str = logList.get(i).toString();
				str = str.replaceAll("</font>", "");
				String subStr = str.substring(str.indexOf("<"), str.indexOf(">")+1);
				str = str.replaceAll(subStr, "");
				if ("".equals(msg)) {
					msg = str;
				} else {
					msg = msg + "\r\n" + str;
				}
				continue;
			}
			if ("".equals(msg)) {
				msg = logList.get(i).toString();
			} else {
				msg = msg + "\r\n" + logList.get(i).toString();
			}
		}
		LogHome.getLog().info(msg);
		result.put("msg",msg);
		response.setContentType("text/html;charset=UTF-8");
		response.getWriter().write(JSON.toJSONString(result));
	}
	@RequestMapping(value="dm/modelexport.do")
	public void export(HttpServletRequest request,
			HttpServletResponse response) throws Exception{
		request.setCharacterEncoding("GBK");
        String template = java.net.URLDecoder.decode(request.getParameter("name"), "utf-8");
        String te = new String(template.getBytes("GBK"), "ISO-8859-1");
        template = template + ".xls";
        String filePath = getFilePath(request) + template;
        response.setContentType("application/msexcel");
        LogHome.getLog().info("文件名template："+template+"======"+"转码后te："+te);
        response.setHeader("Content-disposition", "attachment;filename=" + te +".xls");
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        LogHome.getLog().info("导出文件路径filePath:"+filePath);
        try {
            bis = new BufferedInputStream(new FileInputStream(filePath));
            bos = new BufferedOutputStream(response.getOutputStream());
            byte[] buff = new byte[2048];
            int bytesRead;
            while ( -1 != (bytesRead = bis.read(buff, 0, buff.length))) {
                bos.write(buff, 0, bytesRead);
            }
        } catch (Exception e) {
            LogHome.getLog().info("出错template："+template+"======"+"出错te："+te,e);
        } finally {
            if (bis != null)
                bis.close();
            if (bos != null)
                bos.close();
        }
	}
	private String getFilePath(HttpServletRequest request){
    	String filePath = "";
     	String serverHome = SystemEnv.getPathEnv("IRMS_SERVER_HOME");
        filePath = serverHome + "/import/设备数据/";
        return filePath;
    }
	
	private String dealImpExpInfo(ImpExpInfo _impExpInfo){ 
		if(_impExpInfo==null){
			return "";
		}
		String result ="";
		result = "非法数据：第" + _impExpInfo.getRowNum() + "行，" + _impExpInfo.getName() + _impExpInfo.getPostInfo()+"!";
		return result;
	}
	
}
