package com.boco.irms.app.dm.bo;

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
import com.boco.irms.app.utils.ColumnsFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Font;

public class ExportBO implements IGridExportBO {
	public List<ExportFile> export(GridExportParam param) {
		GridCfg gridCfg = param.getGridCfg();
		List colList = param.getColList();
		List<String> cuidList = param.getCuidList();
		if ((cuidList != null) && (cuidList.size() > 0)) {
			String v = "";
			for (String cuid : cuidList) {
				v = v + "'" + cuid + "',";
			}
			v = v.substring(0, v.length() - 1);
			WhereQueryItem item = new WhereQueryItem();
			item.setKey("CUID");
			item.setRelation("IN");
			item.setType("string");
			item.setValue(v);
			gridCfg.getQueryParams().put("CUID", item);
		}

		String boName = gridCfg.getBoName();
		IGridBO dataBo = (IGridBO) SpringContextUtil.getBean(boName);
		GridMeta meta = dataBo.getGridMeta(gridCfg);
		PageQuery queryParam = new PageQuery(Integer.valueOf(0),
				Integer.valueOf(5000));
		queryParam.setTotalNum(Integer.valueOf(5000));
		PageResult page = dataBo.getGridData(queryParam, gridCfg);
		
		List<PageResult> list = this.getResourcePageResult(page);
		List fileList = new ArrayList();

		ExportFile ef = this.createExportFile(param, meta, list);
		fileList.add(ef);
		
		return fileList;
	}

	private HSSFSheet createGridSheet(HSSFWorkbook wb,String sheetName, GridMeta meta,
			List<String> colList) {
		HSSFSheet sheet = wb.createSheet(sheetName);
		HSSFRow headerRow = sheet.createRow(0);
		List<GridColumn> columns = meta.getColumns();
		HSSFCell noCell = headerRow.createCell(0);
		noCell.setCellValue("序号");
		int colIndex = 1;
		String col;
		for (Iterator i$ = colList.iterator(); i$.hasNext();) {
			col = (String) i$.next();
			for (GridColumn column : columns)
				if (column.getDataIndex().equals(col)) {
					String header = column.getHeader();
					if (StringUtils.isNotBlank(header)) {
						HSSFCell cell = headerRow.createCell(colIndex++);
						header = header.replaceAll("<[^>]+>", "");
						cell.setCellValue(header);
						if(header!=null&&
								((sheetName.equals("机房")&&("机房名称".equals(header)||
										"修改后机房名称".equals(header)||"产权".equals(header)||
										"业务级别".equals(header)||"机房类型".equals(header)||
										"所属站点".equals(header)||"钥匙类型".equals(header)||
										"维护方式".equals(header)||"设备状态".equals(header)))
								||(sheetName.equals("光分纤箱")&&("所属区域".equals(header)||
										"名称".equals(header)||"模板名称".equals(header)||
										"产权归属".equals(header)||"用途".equals(header)||
										"编号".equals(header)||"集客接入场景".equals(header)||
										"设备标识".equals(header)||"是否正使用".equals(header)||
										"设备序列号".equals(header)||"设备供应商".equals(header)||
										"厂商特征值".equals(header)||"所有权人".equals(header)||
										"使用单位".equals(header)||"地址".equals(header)||
										"维护单位".equals(header)||"维护人".equals(header)||
										"维护人联系电话".equals(header)||"维护方式".equals(header)||
										"入网时间".equals(header)||"建设单位".equals(header)||
										"核查日期".equals(header)||"维护人通信地址".equals(header)||
										"经度".equals(header)||"纬度".equals(header)||
										"数据库主键".equals(header)||
										"备注".equals(header)||"巡检人".equals(header)))
								||(sheetName.equals("光接头盒")&&("所属区域".equals(header)||
										"名称".equals(header)||"集客接入场景".equals(header)||
										"维护方式".equals(header)||"产权归属".equals(header)||
										"经度".equals(header)||"纬度".equals(header)||
										"数据库主键".equals(header)||
										"是否预覆盖接入点".equals(header)))
								||(sheetName.equals("光交接箱")&&("所属区域".equals(header)||
										"名称".equals(header)||"模板名称".equals(header)||
										"集客接入场景".equals(header)||"产权归属".equals(header)||
										"经度".equals(header)||"纬度".equals(header)||
										"数据库主键".equals(header)||
										"用途".equals(header)||"维护方式".equals(header)))
								
								)
						){
							HSSFCellStyle cellStyle = wb.createCellStyle();
							HSSFFont font = wb.createFont();
							font.setColor(Font.COLOR_RED);
							cellStyle.setFont(font);
							cell.setCellStyle(cellStyle);
						}
					}
				}
		}
		return sheet;
	}

	private void createGridSheetData(HSSFSheet sheet, GridMeta meta,
			PageResult page, List<String> colList) {
		List<GridColumn> columns = meta.getColumns();
		int rowIndex = 1;
		String sheetName = sheet==null?"":sheet.getSheetName();
		for (Iterator i$ = page.getElements().iterator(); i$.hasNext();) {
			Object obj = i$.next();
			HSSFRow row = sheet.createRow(rowIndex);
			Map map;
			if ((obj instanceof Map))
				map = (Map) obj;
			else {
				try {
					map = BeanUtils.describe(obj);
				} catch (Exception e) {
					throw new RuntimeException(obj.getClass() + "对象转换错误!");
				}
			}
			HSSFCell noCell = row.createCell(0);
			noCell.setCellValue(rowIndex);
			rowIndex++;
			int cellIndex = 1;
			String col;
			for (Iterator j$ = colList.iterator(); j$.hasNext();) {
				col = (String) j$.next();
				for (GridColumn column : columns) {
					String header = column.getHeader();
					if(!isValid(sheetName,header)){
						if ((StringUtils.isNotBlank(header))
								&& (col.equalsIgnoreCase(column.getDataIndex()))) {
						     cellIndex++;
						}
						continue;
					}
					if ((StringUtils.isNotBlank(header))
							&& (col.equalsIgnoreCase(column.getDataIndex()))) {
						HSSFCell cell = row.createCell(cellIndex++);
						Object value = map.get(column.getDataIndex());
						if (value != null) {
							String v = "";
							if (value instanceof Date){
								v = TimeFormatHelper.getFormatDate(
										(Date) value, "yyyy-MM-dd HH:mm:ss");
							}else if(value instanceof Map){
								Object labelcn = ((Map) value).get("LABEL_CN");
								v = (labelcn == null? "": labelcn.toString() );
							}else {
								v = value.toString();
							}
							if ((("rendererRel".equals(column.getRenderer())) || ("rendererCount"
									.equals(column.getRenderer())))
									&& (v.length() > 1)) {
								v = v.substring(v.indexOf("[") + 1,
										v.length() - 1);
							}

							cell.setCellValue(v);
						}
					}
				}
			}
		}
	}
	
	private List<PageResult> getResourcePageResult(PageResult page){
		if(page ==null)
			return null;
		List<PageResult> result = new ArrayList<PageResult>();
		PageResult page1 = new PageResult(new ArrayList(), 0, 0, 0);
		PageResult page2 = new PageResult(new ArrayList(), 0, 0, 0);
		PageResult page3 = new PageResult(new ArrayList(), 0, 0, 0);
		PageResult page4 = new PageResult(new ArrayList(), 0, 0, 0);
		for (Iterator i$ = page.getElements().iterator(); i$.hasNext();) {
			Object obj = i$.next();
			Map map;
			if ((obj instanceof Map))
				map = (Map) obj;
			else {
				try {
					map = BeanUtils.describe(obj);
				} catch (Exception e) {
					throw new RuntimeException(obj.getClass() + "对象转换错误!");
				}
			}
			if(map.get("RES_TYPE")!=null){
				String cuid = String.valueOf(((Map)map.get("RES_TYPE")).get("CUID"));
				if("1".equals(cuid)){
					page1.getElements().add(map);
				}else if("2".equals(cuid)){
					page2.getElements().add(map);
				}else if("3".equals(cuid)){
					page3.getElements().add(map);
				}else if("4".equals(cuid)){
					page4.getElements().add(map);
				}
			}

		}
		
		if(page1!=null&&page1.getElements().size()>0){
			result.add(page1);
		}
		
		if(page2!=null&&page2.getElements().size()>0){
			result.add(page2);
		}
		
		if(page3!=null&&page3.getElements().size()>0){
			result.add(page3);
		}
		
		if(page4!=null&&page4.getElements().size()>0){
			result.add(page4);
		}
		
		return result;
	}
	
	private ExportFile createExportFile(GridExportParam param,GridMeta meta,
			List<PageResult> list){
		String tempfilePath = SysProperty.getInstance().getValue("tempfile");
		File folder = new File(tempfilePath);
		if (!folder.exists()) {
			folder.mkdirs();
		}
		
		List colList = ColumnsFactory.getInstance().getRoomColList();
		ColumnsFactory.getInstance().dealRoomGridMeta(meta);
		
		String randomfm = UUID.randomUUID().toString();
		String filePath = tempfilePath + "/" + randomfm + ".xls";
		HSSFWorkbook wb = new HSSFWorkbook();
		File file = new File(filePath);

		for(PageResult page:list){
			String sheetName = getSheetName(page);
			dealMeta(sheetName,meta);
			colList = getColList(sheetName);
			HSSFSheet sheet = createGridSheet(wb, sheetName, meta, colList);
			createGridSheetData(sheet, meta, page, colList);
		}
		
		
		String fileName = meta.getLabelCn();

		String resName = (String) param.getGridCfg().getCfgParams().get("labelCn");
		if ((resName != null) && (!resName.equals(""))) {
			fileName = resName + fileName;
		}
		try {
			FileOutputStream out = new FileOutputStream(file);
			wb.write(out);
			out.close();
		} catch (IOException e) {
			throw new RuntimeException("文件写入异常！" + e.getMessage());
		} finally {
			FileOutputStream out = null;
		}
		ExportFile ef = new ExportFile();
		ef.setFilePath(filePath);

		if (StringUtils.isBlank(fileName)) {
			fileName = "数据表格";
		}
		ef.setFileName("导出：" + fileName + ".xls");
		ef.setNum(0);
		
		return ef;
	}
	
	private String getSheetName(PageResult page){
		if(page==null||
				page.getElements()==null||page.getElements().size()<1)
			return "";
		Object obj = page.getElements().iterator().next();
		Map map;
		if ((obj instanceof Map))
			map = (Map) obj;
		else {
			try {
				map = BeanUtils.describe(obj);
			} catch (Exception e) {
				throw new RuntimeException(obj.getClass() + "对象转换错误!");
			}
		}
		
		String sheetName = "";		
		if(map.get("RES_TYPE")!=null){
			String cuid = String.valueOf(((Map)map.get("RES_TYPE")).get("CUID"));
			if("1".equals(cuid)){
				sheetName = "机房";
			}else if("2".equals(cuid)){
				sheetName = "光接头盒";
			}else if("3".equals(cuid)){
				sheetName = "光交接箱";
			}else if("4".equals(cuid)){
				sheetName = "光分纤箱";
			}
		}
		
		return  sheetName;
	}
	
	private boolean isValid(String sheetName,String header){
		if(sheetName==null||header==null
				||"".equals(sheetName)||"".equals(header)){
			return false;
		}
		
		if(sheetName.equals("机房")&&(
				header.equals("数据库主键")||header.equals("机房名称"))){
			return true;
		}else if(sheetName.equals("光交接箱")&&(
				header.equals("数据库主键")||header.equals("名称")||
				header.equals("所属区域")||header.equals("经度")||
				header.equals("纬度"))){
			return true;
		}else if(sheetName.equals("光分纤箱")&&(
				header.equals("数据库主键")||header.equals("名称")||
				header.equals("所属区域")||header.equals("经度")||
				header.equals("纬度"))){
			return true;
		}else if(sheetName.equals("光接头盒")&&(
				header.equals("数据库主键")||header.equals("名称")||
				header.equals("所属区域")||header.equals("经度")||
				header.equals("纬度"))){
			return true;
		}
		
		return false;
	}
	
	private void dealMeta(String sheetName,GridMeta meta){
		if(sheetName==null||"".equals(sheetName)||meta==null){
			return;
		}
		if("机房".equals(sheetName)){
			ColumnsFactory.getInstance().dealRoomGridMeta(meta);
		}else if("光分纤箱".equals(sheetName)){
			ColumnsFactory.getInstance().dealDPGridMeta(meta);
		}else if("光交接箱".equals(sheetName)){
			ColumnsFactory.getInstance().dealCabGridMeta(meta);
		}else if("光接头盒".equals(sheetName)){
			ColumnsFactory.getInstance().dealJointBoxGridMeta(meta);
		}
	}
	
	
	private List<String> getColList(String sheetName){
		List<String> result = new ArrayList<String>();
		if(sheetName==null||"".equals(sheetName)){
			return result;
		}

		if("机房".equals(sheetName)){
			result =  ColumnsFactory.getInstance().getRoomColList();
		}else if("光分纤箱".equals(sheetName)){
			result =  ColumnsFactory.getInstance().getDPColList();
		}else if("光交接箱".equals(sheetName)){
			result =  ColumnsFactory.getInstance().getCabColList();
		}else if("光接头盒".equals(sheetName)){
			result =  ColumnsFactory.getInstance().getJointBoxColList();
		}
		
		return result;
	}
}