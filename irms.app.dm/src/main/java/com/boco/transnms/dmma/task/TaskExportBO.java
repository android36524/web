package com.boco.transnms.dmma.task;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import net.sf.json.JSONArray;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.boco.common.util.debug.LogHome;
import com.boco.component.export.bo.IExportBO;
import com.boco.component.export.pojo.ExportCfg;
import com.boco.component.export.pojo.ExportFile;
import com.boco.component.grid.bo.IGridBO;
import com.boco.component.grid.bo.IGridExportBO;
import com.boco.component.grid.pojo.GridCfg;
import com.boco.component.grid.pojo.GridColumn;
import com.boco.component.grid.pojo.GridMeta;
import com.boco.component.grid.ux.pojo.GridExportParam;
import com.boco.component.query.pojo.WhereQueryItem;
import com.boco.core.bean.SpringContextUtil;
import com.boco.core.ibatis.vo.PageQuery;
import com.boco.core.ibatis.vo.PageResult;
import com.boco.core.ibatis.vo.ServiceActionContext;
import com.boco.core.spring.SysProperty;
import com.boco.core.utils.lang.TimeFormatHelper;

public class TaskExportBO implements IGridExportBO{
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	

	private HSSFSheet createGridSheet(HSSFWorkbook wb, int i, GridMeta meta, List<String> colList, String sheetName) {
		HSSFSheet sheet = wb.createSheet(sheetName);
		HSSFRow headerRow = sheet.createRow(0); 
		List<GridColumn> columns = meta.getColumns();
		HSSFCell noCell = headerRow.createCell(0);
		noCell.setCellValue("序号");
		int colIndex = 1;
		for(GridColumn column : columns) {
			
				String header = column.getHeader();
				if(StringUtils.isNotBlank(header)) {
					HSSFCell cell = headerRow.createCell(colIndex++);
					header = header.replaceAll("<[^>]+>", "");
					cell.setCellValue(header);
				}
		
		}
		return sheet;
	}
	
	private void createGridSheetData(HSSFSheet sheet, GridMeta meta, JSONArray page, List<String> colList) {
		List<GridColumn> columns = meta.getColumns();
		int rowIndex = 1;
		for(Object obj : page) {
			HSSFRow row = sheet.createRow(rowIndex);
			Map<String, Object> map;
			if(obj instanceof Map){
				map = (Map<String, Object>) obj;
			}else{
				try {
					map = BeanUtils.describe(obj);
				} catch (Exception e) {
					throw new RuntimeException(obj.getClass()+"对象转换错误!");
				} 
			}
			HSSFCell noCell = row.createCell(0);
			noCell.setCellValue(rowIndex);
			rowIndex++;
			int cellIndex = 1;
				for(GridColumn column : columns) {
					String header = column.getHeader();
						HSSFCell cell = row.createCell(cellIndex++);
						Object value = map.get(column.getDataIndex());
						if(value != null) {
							String v = "";
							if(value instanceof Date) {
								v = TimeFormatHelper.getFormatDate((Date)value, TimeFormatHelper.TIME_FORMAT_A);
							}else if(value instanceof Map){
								Object o = ((Map)value).get("LABEL_CN");
								v = (o == null? "":o.toString());
							}else {
								v = value.toString();
							}
							if("rendererRel".equals(column.getRenderer()) || "rendererCount".equals(column.getRenderer())) {
								if(v.length() > 1) {
									v = v.substring(v.indexOf("[")+1, v.length()-1);
								}
							}
							cell.setCellValue(v);
						}
			}
		}
	}

	private File zip(List<ExportFile> fileList) {
		
		File zipFile = new File(fileList.get(0).getFilePath()+".zip");
		
		FileInputStream  in = null;
		ZipOutputStream zipOut = null;
		try {
			zipOut = new ZipOutputStream(new FileOutputStream(zipFile));
			for (ExportFile file : fileList) {
				in = new FileInputStream(file.getFilePath());
				ZipEntry zipEntry = new ZipEntry(file.getFileName());
				zipOut.putNextEntry(zipEntry);
				int nNumber;  
				byte[] buffer = new byte[2048000];  
				while ((nNumber = in.read(buffer)) != -1){
					zipOut.write(buffer, 0, nNumber); 
				}  
			}
			zipOut.flush();  
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage());
		} catch (IOException e) {		
			logger.error(e.getMessage());
		} finally {
			try {
				zipOut.close();
				in.close();
			} catch (IOException e) {
				logger.error(e.getMessage());
			}
		}
		return zipFile;
	}

	

	@Override
	public List<ExportFile> export(GridExportParam param) {
		GridCfg gridCfg = param.getGridCfg();
		List<String> colList = param.getColList();
		List<String> cuidList = param.getCuidList();
		LogHome.getLog(this.getClass()).info("导出EXCEL gridCfg:"+gridCfg);
		LogHome.getLog(this.getClass()).info("导出EXCEL colList:"+colList);
		LogHome.getLog(this.getClass()).info("导出EXCEL cuidList:"+cuidList);
		if(cuidList != null && cuidList.size() > 0) {
			String v = "";
			for(String cuid : cuidList) {
				v += "'"+cuid + "',";
			}
			v = v.substring(0, v.length() -1);
			WhereQueryItem item = new WhereQueryItem();
			item.setKey("CUID");
			item.setRelation("IN");
			item.setType("string");
			item.setValue(v);
			gridCfg.getQueryParams().put("CUID", item);
		}
		String tempfilePath = param.getGridCfg().getAc().getServerPath().replaceAll("\\\\", "/")+"temp";
		File folder = new File(tempfilePath);
		if(!folder.exists()) {
			folder.mkdirs();
		}
		
		List<ExportFile> fileList = new ArrayList<ExportFile>();
		String boName = gridCfg.getBoName();
		IGridBO dataBo = (IGridBO)SpringContextUtil.getBean(boName);
		GridMeta meta = new GridMeta();
		Set<String> paramsKeySets = gridCfg.getCfgParams().keySet();
		String[] keys = gridCfg.getCfgParams().get("keys").split(",");
		String[] values = gridCfg.getCfgParams().get("values").split(",");
		if(keys.length == values.length){
			for(int i =0;i<keys.length;i++){
				if(org.springframework.util.StringUtils.isEmpty(keys[i])){
					continue;
				};
				meta.addColumn(keys[i], values[i], 500);
			}
		}
	
		int pageSize = 30000, curPageNum = 1, resultSize = 0;
		LogHome.getLog(this.getClass()).info("导出EXCEL boName:"+boName);
		LogHome.getLog(this.getClass()).info("导出EXCEL getCfgParams:"+gridCfg.getCfgParams());
		LogHome.getLog(this.getClass()).info("导出EXCEL meta:"+meta);
		do {
			PageQuery queryParam = new PageQuery(0, 5000);
			queryParam.setTotalNum(5000);
			JSONArray jsonArray = JSONArray.fromObject(gridCfg.getCfgParams().get("dataExport"));
			
			if (resultSize > 0 || curPageNum == 1) {
				HSSFWorkbook wb = new HSSFWorkbook();
				HSSFSheet sheet = this.createGridSheet(wb, 0, meta, colList,gridCfg.getCfgParams().get("sheetName"));
				this.createGridSheetData(sheet, meta, jsonArray, colList);
				
				String randomfm = UUID.randomUUID().toString();
				randomfm = gridCfg.getCfgParams().get("fileName");
				String filePath = tempfilePath + "/"+randomfm+".xls";
				File file = new File(filePath);

				FileOutputStream out;
				try {
					out = new FileOutputStream(file);
					wb.write(out);
					out.close();
				} catch (IOException e) {
					throw new RuntimeException("文件写入异常！"+e.getMessage());
				} finally {
					out = null; 
				}
				
				ExportFile ef = new ExportFile();
				ef.setFilePath("/temp/"+randomfm+".xls");
				String fileName = meta.getLabelCn();
				if(StringUtils.isBlank(fileName)) {
					fileName = "数据表格";
				}
				ef.setFileName("导出：" + fileName +  "_" + curPageNum +".xls");
				ef.setNum(0);
				fileList.add(ef);
			}
		} while (resultSize == pageSize);
		
		if (fileList.size() > 1) {
			File file = zip(fileList);
			fileList.clear();
			ExportFile ef = new ExportFile();
			ef.setFilePath(file.getPath());
			ef.setFileName(file.getName());
			ef.setNum(0);
			fileList.add(ef);
		}
		
		return fileList;
	}
	

}
