package com.boco.irms.app.excel;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import com.boco.core.utils.exception.UserException;

public class ExcelParser implements IParser {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private Workbook book = null;

	public void init(String filePath) {
		this.init(new File(filePath));
	}

	public void init(File file) {
		try {
			this.init(file.getName().substring(file.getName().lastIndexOf(".") + 1), new FileInputStream(file));
		} catch (Exception e) {
			this.logger.error(e.getMessage());
			throw new UserException(e);
		}
	}

	public void init(String extensionName, FileInputStream fis) {
		try {
			if ("xls".equalsIgnoreCase(extensionName)) {
				this.book = new HSSFWorkbook(fis);
			} else if ("xlsx".equalsIgnoreCase(extensionName)) {
				this.book = new XSSFWorkbook(fis);
			} else {
				throw new Exception("不支持的文件类型");
			}
		} catch (Exception e) {
			this.logger.error(e.getMessage());
			throw new UserException(e);
		}
	}

	public void init(Workbook workbook) {
		this.book = workbook;
	}

	/**
	 * 读取多个sheet页数据
	 * 
	 * @param sheetNames
	 * @return
	 */
	public Map<String, List<Map<String, Object>>> parse(String[] sheetNames) {
		Map<String, List<Map<String, Object>>> sheetMap = new HashMap<String, List<Map<String, Object>>>();
		try {
			for (String sheetName : sheetNames) {
				sheetMap.put(sheetName, this.parse(sheetName));
			}
		} catch (Exception e) {
			this.logger.error(e.getMessage());
			throw new UserException(e);
		}
		return sheetMap;
	}

	/**
	 * 读取制定sheet页数据
	 * 
	 * @param sheetName
	 * @return
	 */
	public List<Map<String, Object>> parse(String sheetName) throws Exception {
		Map<String, Integer> columnCnNameMap = new HashMap<String, Integer>();
		if (sheetName == null || "".equals(sheetName.trim())) {
			throw new UserException("没有找到对应的sheet名称");
		}
		Sheet sheet = this.book.getSheet(sheetName);
		Row firstRow = sheet.getRow(0);
		for (int j = firstRow.getFirstCellNum(); j <= firstRow.getLastCellNum(); j++) {
			Cell cell = firstRow.getCell(j);
			if (cell == null) {
				continue;
			}

			String columnName = cell.toString();
			if (!columnCnNameMap.containsKey(columnName)) {
				columnCnNameMap.put(columnName, j);
			} else {
				throw new UserException("Excel中包含有重复的列，重复列名为【" + columnName + "】");
			}
		}

		List<Map<String, Object>> recordList = new ArrayList<Map<String, Object>>();
		for (int i = 1; i <= sheet.getPhysicalNumberOfRows(); i++) {
			Row row = sheet.getRow(i);
			if (row == null) {
				continue;
			}

			Map<String, Object> recordMap = RowParser.parse(row, columnCnNameMap);
			if (!CollectionUtils.isEmpty(recordMap)) {
				recordList.add(recordMap);
			}
		}
		return recordList;
	}

	public static void main(String[] args) {
		String filePath = "C:\\Users\\Administrator\\Downloads\\导出：人井.xlss";
		try {
			IParser parser = new ExcelParser();
			parser.init(filePath);
			List<Map<String, Object>> recordList = parser.parse("人井");
			for (Map<String, Object> map : recordList) {
				for (String key : map.keySet()) {
					System.out.print(key + ":" + map.get(key));
					System.out.print(",");
				}
				System.out.println();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}