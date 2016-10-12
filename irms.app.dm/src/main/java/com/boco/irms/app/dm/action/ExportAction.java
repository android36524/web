package com.boco.irms.app.dm.action;

import com.boco.component.export.pojo.ExportFile;
import com.boco.component.export.pojo.ExportInfo;
import com.boco.component.grid.bo.IGridBO;
import com.boco.component.grid.bo.IGridDetailBO;
import com.boco.component.grid.bo.IGridExportBO;
import com.boco.component.grid.bo.IGridViewBO;
import com.boco.component.grid.pojo.GridCfg;
import com.boco.component.grid.pojo.GridColumn;
import com.boco.component.grid.pojo.GridColumnCust;
import com.boco.component.grid.pojo.GridMeta;
import com.boco.component.grid.pojo.GridQueryCust;
import com.boco.component.grid.pojo.PropertyGridRecord;
import com.boco.component.grid.ux.pojo.GridExportParam;
import com.boco.core.bean.SpringContextUtil;
import com.boco.core.ibatis.dao.IbatisDAOHelper;
import com.boco.core.ibatis.vo.JsonReaderResponse;
import com.boco.core.ibatis.vo.PageQuery;
import com.boco.core.ibatis.vo.PageResult;
import com.boco.core.ibatis.vo.ServiceActionContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExportAction {
	private Logger logger = LoggerFactory.getLogger(getClass());

	private IGridBO getBO(String boName) {
		if (StringUtils.isBlank(boName)) {
			boName = "XmlTemplateGridBO";
		}
		this.logger.info("GridBO:" + boName);
		return (IGridBO) SpringContextUtil.getBean(boName);
	}

	private IGridDetailBO getDetailBO(String boName) {
		if (StringUtils.isBlank(boName)) {
			boName = "XmlTemplateGridBO";
		}
		return (IGridDetailBO) SpringContextUtil.getBean(boName);
	}

	public ExportInfo exportGridData(HttpServletRequest request,
			GridCfg gridCfg, List<String> colList, List<String> cuidList)
			throws Exception {
		String boName = gridCfg.getExportBoName();
		if (StringUtils.isEmpty(boName)) {
			boName = "ExportBO";
		}
		if (gridCfg.getBoName() == null) {
			gridCfg.setBoName("XmlTemplateGridBO");
		}
		long startTime = System.currentTimeMillis();
		IGridExportBO bo = null;
		try {
			bo = (IGridExportBO) SpringContextUtil.getBean(boName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (bo == null) {
			throw new RuntimeException("未找到" + boName + "对应的实现类！");
		}
		ServiceActionContext ac = new ServiceActionContext(request);
		gridCfg.setAc(ac);

		boolean isExportAll = false;
		boolean isExportNote = false;
		int startNum = 1;
		if (gridCfg.getCfgParams() != null) {
			isExportNote = Boolean.parseBoolean((String) gridCfg.getCfgParams()
					.get("isExportNote"));
			isExportAll = Boolean.parseBoolean((String) gridCfg.getCfgParams()
					.get("isExportAll"));

			String exportStartNum = (String) gridCfg.getCfgParams().get(
					"exportStartNum");
			if ((StringUtils.isNotBlank(exportStartNum))
					&& (exportStartNum.matches("\\d*"))) {
				startNum = Integer.parseInt((String) gridCfg.getCfgParams()
						.get("exportStartNum"));
			}
		}
		GridExportParam param = new GridExportParam();
		param.setGridCfg(gridCfg);
		param.setColList(colList);
		param.setCuidList(cuidList);
		param.setNote(isExportNote);
		param.setExportAll(isExportAll);
		param.setStartNum(startNum);

		List<ExportFile> files = bo.export(param);

		ExportInfo info = new ExportInfo();
		info.setFiles(files);
		info.setSeconds(System.currentTimeMillis() - startTime);
		int total = 0;
		for (ExportFile f : files) {
			total += f.getNum();
		}
		info.setTotal(total);
		return info;
	}
}