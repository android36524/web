package com.boco.irms.app.excel;

import java.util.HashMap;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;

import com.boco.core.utils.exception.UserException;

public class RowParser {
	public static Map<String, Object> parse(Row row, Map<String, Integer> columnCnNameMap) throws UserException {
		Map<String, Object> record = new HashMap<String, Object>();
		try {
			for (String columnName : columnCnNameMap.keySet()) {
				int columnNo = columnCnNameMap.get(columnName);
				Cell cell = row.getCell(columnNo);
				Object value = null;
				if (cell != null) {
					switch (cell.getCellType()) {
					case XSSFCell.CELL_TYPE_STRING:
						value = cell.getRichStringCellValue().getString().trim();
						break;
					case XSSFCell.CELL_TYPE_NUMERIC:
						value = cell.getNumericCellValue();
						break;
					case XSSFCell.CELL_TYPE_FORMULA:
						value = cell.getNumericCellValue();
						break;
					case XSSFCell.CELL_TYPE_BOOLEAN:
						value = cell.getBooleanCellValue();
						break;
					case XSSFCell.CELL_TYPE_BLANK:
						value = null;
						break;
					case XSSFCell.CELL_TYPE_ERROR:
						value = null;
						break;
					default:
						value = cell.toString();
						break;
					}

					if (value != null) {
						record.put(columnName, value);
					}
				}
			}
		} catch (Exception e) {
			throw new UserException(e.getMessage(), e);
		}
		return record;
	}
}