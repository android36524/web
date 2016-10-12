package com.boco.irms.app.dm.gridbo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.boco.component.combox.pojo.ComboxCfg;
import com.boco.component.combox.pojo.ComboxItem;
import com.boco.component.combox.ux.bo.XmlTemplateComboxBO;
import com.boco.component.editor.bo.EditorMetaPluginBO;
import com.boco.component.editor.pojo.EditorColumnMeta;
import com.boco.component.editor.pojo.EditorPanelMeta;
import com.boco.component.query.pojo.WhereQueryItem;
import com.boco.core.bean.SpringContextUtil;
import com.boco.core.ibatis.dao.IbatisDAO;
import com.boco.core.ibatis.vo.PageQuery;
import com.boco.core.ibatis.vo.PageResult;
import com.boco.graphkit.ext.editor.EnumType;
import com.boco.graphkit.ext.editor.EnumTypeManager;
import com.boco.irms.app.dm.gridbo.DistrictCacheModel;
import com.boco.irms.app.utils.WebDMUtils;
import com.boco.transnms.client.model.base.BoCmdFactory;
import com.boco.transnms.common.dto.District;
import com.boco.transnms.common.dto.base.BoActionContext;
import com.boco.transnms.common.dto.base.GenericDO;
import com.boco.transnms.server.bo.base.BoHomeFactory;
import com.boco.transnms.server.bo.ibo.dm.IDuctManagerBO;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class DefaultEditorMetaPluginBO extends EditorMetaPluginBO {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private Map<String, EditorColumnMeta> editorColumnMetaMap = new HashMap<String, EditorColumnMeta>();
	
	private EditorPanelMeta editorMeta;

	public Map<String, EditorColumnMeta> getEditorColumnMetaMap() {
		return editorColumnMetaMap;
	}

	public void setEditorColumnMetaMap(Map<String, EditorColumnMeta> editorColumnMetaMap) {
		this.editorColumnMetaMap = editorColumnMetaMap;
	}

	public EditorPanelMeta getEditorMeta() {
		return editorMeta;
	}

	public void setEditorMeta(EditorPanelMeta editorMeta) {
		this.editorMeta = editorMeta;
	}
	protected IbatisDAO getIbatisCheckDAO() {
		return (IbatisDAO)SpringContextUtil.getBean("IbatisCheckDAO");
	}
	private IDuctManagerBO getDuctManagerBO() {
		return BoHomeFactory.getInstance().getBO(IDuctManagerBO.class);
	}

/*	public EditorPanelMeta editorPlugin(String paras, EditorPanelMeta editorMeta) {

		this.getEditorColumnMetaMap().clear();
		for (EditorColumnMeta colMeta : editorMeta.getEditorColumnMetas()) {
			this.getEditorColumnMetaMap().put(colMeta.getCuid(), colMeta);
		}
		this.setEditorMeta(editorMeta);

		// 默认值处理, 翻译枚举和关联属性
		paras = StringUtils.isNotEmpty(paras) ? paras.replaceAll("\\\\", "") : paras;
		setEditorMetaDefVal(paras);

		this.getEditorMeta().setAc(null);
		
		return this.getEditorMeta();
	}*/

	protected void setEditorMetaDefVal(String paras) {

		Map pms = new HashMap();
		if (StringUtils.isNotEmpty(paras)) {
			try {
				pms = JSON.parseObject(paras, Map.class);
			} catch (Exception e) {
				pms = new HashMap();
				logger.error("解析paras失败,paras=" + paras, e);
			}
		}
		GenericDO dto = WebDMUtils.createInstanceByClassName(this.getEditorMeta().getClassName(), pms);
		List<Map> list = new ArrayList<Map>(1);

		// 设置默认值
		setDtoDefVal(dto);

		// 翻译枚举和关联属性
		Map map = new HashMap();
		for (Object columnName : dto.getAllAttr().keySet()) {
			String colName = columnName.toString();
			Object value = dto.getAttrValue(colName);
			map.put(colName, translateDtoDefVal(colName, value));
		}
		list.add(map);

		PageResult result = new PageResult(list, 1, 1, 1);
		this.getEditorMeta().setResult(JSON.toJSONString(result.getElements()));
	}
	
	protected GenericDO setDtoDefVal(GenericDO dto) {
		dto.setAttrValue("CREATE_TIME", dto.getCreateTime());
		dto.setAttrValue("LAST_MODIFY_TIME", dto.getLastModifyTime());
		return dto;
	}

	protected Object translateDtoDefVal(String columnName, Object value) {
		Object obj = value;
		if ("RELATED_DISTRICT_CUID".equals(columnName) || "RELATED_SPACE_CUID".equals(columnName) || "DISTRICT_CUID".equals(columnName) || "SITE_CUID_A".equals(columnName) || "SITE_CUID_Z".equals(columnName)) {
			if (value instanceof String) {
				District dist = DistrictCacheModel.getInstance().getDistrictByCUID(String.valueOf(value));
				if (dist != null) {
					Map map = new HashMap();
					map.put("CUID", value);
					map.put("LABEL_CN", dist.getLabelCn());
					obj = map;
				}
			} else if (value instanceof GenericDO) {
				Map map = new HashMap();
				map.put("CUID", ((GenericDO) value).getCuid());
				map.put("LABEL_CN", ((GenericDO) value).getAttrValue("LABEL_CN"));
				obj = map;
			}
		} else {
			EditorColumnMeta columnMeta = this.getEditorColumnMetaMap().get(columnName);
			if (value != null && columnMeta != null && columnMeta.getXtype() != null && columnMeta.getCode() != null) {
				String editor = columnMeta.getXtype();
				String code = columnMeta.getCode();
				if ("enumbox".equals(editor) || "spacecombox".equals(editor)) {
					if (value instanceof Boolean) {
						value = (Boolean) value ? 1L : 0L;
					}
					Object[] gcEnum = EnumTypeManager.getInstance().getEnumTypes(code);
					if (gcEnum != null && !StringUtils.isEmpty(value.toString())) {
						Map enumMap = new HashMap();
						String valueStr = String.valueOf(value);
						String labelcn = "";
						for (Object oEnum : gcEnum) {
							EnumType etype = (EnumType) oEnum;
							String[] attrValue = valueStr.split(",");
							for (String val : attrValue) {
								if ("DeviceVendor".equals(code)) {
									if ((etype.value).toString().equals(String.valueOf(val))) {
										labelcn += "," + etype.dispalyName;
									}
								} else if (Long.parseLong((etype.value).toString()) == Long.parseLong(String.valueOf(val))) {
									labelcn += "," + etype.dispalyName;
								}
							}
						}
						enumMap.put("CUID", valueStr);
						enumMap.put("LABEL_CN", labelcn.length() == 0 ? labelcn : labelcn.substring(1));
						obj = enumMap;
					} 
				} else if ("asyncombox".equals(editor)) {
					if(columnName.equals("RELATED_TASK_CUID")){
						String cuid = "";
						if (value instanceof GenericDO) {
							cuid = String.valueOf(((GenericDO) value).getAttrValueT("CUID"));
						} else {
							cuid = String.valueOf(value);
						}
						Map map = new HashMap();
						map.put("CUID", cuid);
						map.put("LABEL_CN", this.getIbatisCheckDAO().getLabelCnByCuid(cuid));
						obj = map;
					}else if(columnName.equals("ROOM_TYPE")){
						String cuid = "";
						if (value instanceof GenericDO) {
							cuid = String.valueOf(((GenericDO) value).getAttrValueT("CUID"));
						} else {
							cuid = String.valueOf(value);
						}
						Map map = new HashMap();
						map.put("CUID", cuid);
						String label_CN = "";
						String sql = "SELECT  to_char(wm_concat(KEY_VALUE)) AS LABEL_CN  FROM ROOM_CFG_TYPE  WHERE KEY_NUM IN ('"+cuid.replace(",", "','")+"')";
						List<Map<String, Object>> resRet =this.getIbatisCheckDAO().querySql(sql);
						if(resRet!=null && resRet.size()>0){
							Map<String, Object> resM = (Map<String, Object>)resRet.get(0);
							label_CN = (String)resM.get("LABEL_CN");
						}
						map.put("LABEL_CN", label_CN);
						obj = map;
					}else{
						String cuid = "";
						if (value instanceof GenericDO) {
							cuid = String.valueOf(((GenericDO)value).getAttrValueT("CUID"));
						} else {
							cuid = String.valueOf(value);
						}
						if (StringUtils.isEmpty(cuid)) {
							obj = cuid;
						} else {
							XmlTemplateComboxBO xmlTemplateComboxBO = (XmlTemplateComboxBO)SpringContextUtil.getBean("XmlTemplateComboxBO");
							ComboxCfg comboxCfg = new ComboxCfg();
							Map cfgParams = new HashMap();
							cfgParams.put("code", columnMeta.getCode());
							Map<String, WhereQueryItem> queryItems = new HashMap<String, WhereQueryItem>();
							WhereQueryItem sqi = new WhereQueryItem();
							sqi.setKey("CUID");
							sqi.setValue(cuid);
							queryItems.put("CUID", sqi);
							comboxCfg.setCfgParams(cfgParams);
							comboxCfg.setQueryParams(queryItems);
							PageQuery PageQuery = new PageQuery(1, 5000);
							PageResult<ComboxItem> pr = xmlTemplateComboxBO.loadData(comboxCfg, PageQuery);
							if (pr.getElements().size() > 0) {
								ComboxItem comboxItem = pr.getElements().get(0);
								Map map = new HashMap();
								map.put("CUID", comboxItem.getValue());
								map.put("LABEL_CN", comboxItem.getText());
								obj = map;
							}
						}
					}
				} else if ("menucombox".equals(editor) || "dmcombox".equals(editor)) {
					String cuid = "";
					if (value instanceof GenericDO) {
						cuid = String.valueOf(((GenericDO)value).getAttrValueT("CUID"));
					} else {
						cuid = String.valueOf(value);
					}
					if (StringUtils.isEmpty(cuid)) {
						obj = cuid;
					} else {
						String labelCn = this.getIbatisCheckDAO().getLabelCnByCuid(cuid);
						if (StringUtils.isNotEmpty(labelCn)) {
							Map map = new HashMap();
							map.put("CUID", cuid);
							map.put("LABEL_CN", labelCn);
							obj = map;
						}
					}
				} else {
					if (value instanceof GenericDO) {
						Map map = new HashMap();
						map.put("CUID", ((GenericDO) value).getCuid());
						map.put("LABEL_CN", ((GenericDO) value).getAttrValue("LABEL_CN"));
						obj = map;
					}
					if (value instanceof String) {
						if (columnName.equals("RELATED_SYSTEM_CUID") || columnName.equals("RELATED_PROJECT_CUID") || columnName.equals("RELATED_MAINT_CUID")|| columnName.equals("ORIG_POINT_CUID")|| columnName.equals("DEST_POINT_CUID")) {
							if (!StringUtils.isEmpty(value.toString())) {
								String relatedValue = getLabelcnByCuid(value.toString());
								Map map = new HashMap();
								map.put("CUID", value);
								map.put("LABEL_CN", relatedValue);
								obj = map;
							}
						}
					}
				}
			} else {
				if (value instanceof GenericDO) {
					Map map = new HashMap();
					map.put("CUID", ((GenericDO) value).getCuid());
					map.put("LABEL_CN", ((GenericDO) value).getAttrValue("LABEL_CN"));
					obj = map;
				}
			}
		}
		return obj;
	}

	protected String getLabelcnByCuid(String cuid) {
		String name = cuid;
		try {
			GenericDO gdo = (GenericDO) BoCmdFactory.getInstance().execBoCmd("IDuctManagerBO.getObjByCuid", new BoActionContext(), cuid);
			if (gdo != null) {
				name = gdo.getAttrString("LABEL_CN");
				if (!StringUtils.isEmpty(name)) {
					name = gdo.getAttrString("LABEL_CN");
				}
			}
		} catch (Exception e) {
			logger.error("转换失败");
		}
		return name;
	}

	protected boolean containsEditorColumn(String columnName) {
		boolean isContain = false;
		if (StringUtils.isEmpty(columnName) || null == this.getEditorMeta() || null == this.getEditorMeta().getEditorColumnMetas()) {
			return isContain;
		}
		List<EditorColumnMeta> ecms = this.getEditorMeta().getEditorColumnMetas();
		for (int i = 0; i < ecms.size(); i++) {
			if (columnName.equals(ecms.get(i).getCuid())) {
				isContain = true;
				break;
			}
		}
		return isContain;
	}
	
}
