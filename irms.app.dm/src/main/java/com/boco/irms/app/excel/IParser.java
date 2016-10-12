package com.boco.irms.app.excel;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Workbook;

public interface IParser {
	/**
	 * 读取多个sheet页数据
	 * 
	 * @param sheetNames
	 * @return
	 */
	public Map<String, List<Map<String, Object>>> parse(String[] sheetNames);

	/**
	 * 读取制定sheet页数据
	 * 
	 * @param sheetName
	 * @return
	 */
	public List<Map<String, Object>> parse(String sheetName) throws Exception;

	public void init(String filePath);

	public void init(File file);

	public void init(String extensionName, FileInputStream fis);

	public void init(Workbook workbook);
}