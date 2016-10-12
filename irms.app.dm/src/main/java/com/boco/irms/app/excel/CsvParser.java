package com.boco.irms.app.excel;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.boco.core.utils.exception.UserException;

public class CsvParser implements IParser {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private DataInputStream dis;

	private BufferedReader br;

	public void init(String filePath) {
		this.init(new File(filePath));
	}

	public void init(File file) {
		try {
			this.init(file.getName().substring(file.getName().lastIndexOf(".") + 1), new FileInputStream(file));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void init(String extensionName, FileInputStream fis) {
		try {
			this.dis = new DataInputStream(fis);
			this.br = new BufferedReader(new InputStreamReader(this.dis, "GBK"));
		} catch (Exception e) {
			this.logger.error(e.getMessage());
			throw new UserException(e);
		}
	}

	public void init(Workbook workbook) {
	}

	/**
	 * 读取多个sheet页数据
	 * 
	 * @param sheetNames
	 * @return
	 */
	public Map<String, List<Map<String, Object>>> parse(String[] sheetNames) {
		return null;
	}

	/**
	 * 读取制定sheet页数据
	 * 
	 * @param sheetName
	 * @return
	 * @throws Exception 
	 */
	public List<Map<String, Object>> parse(String sheetName) throws Exception {
		List<Map<String, Object>> recordList = new ArrayList<Map<String, Object>>();
		try {
			List<String[]> records = new ArrayList<String[]>();
			int length = 0;
			String line = "";
			while ((line = br.readLine()) != null) {
				String[] contents = line.split(",");
				if (length != contents.length && length != 0) {
					for (int i = 0; i < length - contents.length; i++) {
						line += " ";
					}
					contents = line.split(",");
				} else {
					length = contents.length;
				}
				records.add(contents);
			}

			for (int i = 1; i < records.size(); i++) {
				Map<String, Object> recordMap = new HashMap<String, Object>();
				String[] array = records.get(i);
				for (int j = 0; j < array.length; j++) {
					recordMap.put(records.get(0)[j], array[j].trim());
				}
				recordList.add(recordMap);
			}
		} catch (Exception e) {
			this.logger.error(e.getMessage());
			throw new UserException("CSV文件读取错误!");
		} finally {
			if (this.dis != null) {
				this.dis.close();
			}
			if (this.br != null) {
				this.br.close();
			}
		}
		return recordList;
	}

	public static void main(String[] args) {
		String filePath = "E:\\ZL_ADD-GX-201511191836.csv";
		try {
			IParser parser = new CsvParser();
			parser.init(filePath);
			List<Map<String, Object>> recordList = parser.parse("ZL_ADD-GX-201511191836");
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