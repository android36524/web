package com.boco.irms.app.dm.gridbo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

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
import com.boco.core.spring.SysProperty;
import com.boco.core.utils.lang.TimeFormatHelper;

@SuppressWarnings("unchecked")
public class DmGridExportBO implements IGridExportBO{

	public List<ExportFile> export(GridExportParam param) {
		GridCfg gridCfg = param.getGridCfg();
		List<String> colList = param.getColList();
		List<String> cuidList = param.getCuidList();
		String tempfilePath = SysProperty.getInstance().getValue("tempfile");
		File folder = new File(tempfilePath);
		if(!folder.exists()) {
			folder.mkdirs();
		}
		String randomfm = UUID.randomUUID().toString();
		String filePath = tempfilePath + "/"+randomfm+".xls";
		HSSFWorkbook wb = new HSSFWorkbook();
		File file = new File(filePath);
		
		String boName = gridCfg.getBoName();
		IGridBO dataBo = (IGridBO)SpringContextUtil.getBean(boName);
		GridMeta meta = dataBo.getGridMeta(gridCfg);
		HSSFSheet sheet = this.createGridSheet(wb, 0, meta, colList);
		String fileName = meta.getLabelCn();
	    
		String resName = param.getGridCfg().getCfgParams().get("labelCn");
		if(resName != null && !resName.equals("")){
			fileName = resName + fileName;
		}

		PageQuery queryParam = new PageQuery(0, 5000);
		queryParam.setTotalNum(5000);
		PageResult page = dataBo.getGridData(queryParam, gridCfg);
		this.createGridSheetData(sheet, meta, page, colList);
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
		List<ExportFile> fileList = new ArrayList<ExportFile>();
		ExportFile ef = new ExportFile();
		ef.setFilePath(filePath);
		
		if(StringUtils.isBlank(fileName)) {
			fileName = "数据表格";
		}
		ef.setFileName("导出："+fileName+".xls");
		ef.setNum(0);
		fileList.add(ef);
		return fileList;
	}

	private HSSFSheet createGridSheet(HSSFWorkbook wb, int i, GridMeta meta, List<String> colList) {
		HSSFSheet sheet = wb.createSheet(meta.getLabelCn()+"【"+(i+1)+"】");
		HSSFRow headerRow = sheet.createRow(0); 
		List<GridColumn> columns = meta.getColumns();
		HSSFCell noCell = headerRow.createCell(0);
		noCell.setCellValue("序号");
		int colIndex = 1;
		for(String col : colList) {
			for(GridColumn column : columns) {
				if(column.getDataIndex().equals(col)) {
					String header = column.getHeader();
					if(StringUtils.isNotBlank(header)) {
						HSSFCell cell = headerRow.createCell(colIndex++);
						header = header.replaceAll("<[^>]+>", "");
						cell.setCellValue(header);
					}
				}
			}
		}
		return sheet;
	}
	
	private void createGridSheetData(HSSFSheet sheet, GridMeta meta, PageResult page, List<String> colList) {
		List<GridColumn> columns = meta.getColumns();
		int rowIndex = 1;
		for(Object obj : page.getElements()) {
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
			for(String col : colList) {
				for(GridColumn column : columns) {
					String header = column.getHeader();
					if(StringUtils.isNotBlank(header) && col.equalsIgnoreCase(column.getDataIndex())) {
						HSSFCell cell = row.createCell(cellIndex++);
						Object value = map.get(column.getDataIndex());
						if(value != null) {
							String v = "";
							if(value instanceof Date) {
								v = TimeFormatHelper.getFormatDate((Date)value, TimeFormatHelper.TIME_FORMAT_A);
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
		}
	}

}
