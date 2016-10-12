package com.boco.irms.app.dm.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.boco.core.utils.exception.UserException;
import com.boco.transnms.common.dto.TempCutoverWireSeg;
import com.boco.transnms.common.dto.base.BoActionContext;
import com.boco.transnms.common.dto.base.BoQueryContext;
import com.boco.transnms.common.dto.base.DataObjectList;
import com.boco.transnms.common.dto.base.DboCollection;
import com.boco.transnms.common.dto.base.GenericDO;
import com.boco.transnms.server.bo.base.BoHomeFactory;
import com.boco.transnms.server.bo.dm.system.cutover.TempCutoverWireSegBO;
import com.boco.transnms.server.bo.ibo.dm.IDuctManagerBO;

public class CutOverRouteManageAction {
      //TEMP_CUTOVER_WIRE_SEG
	 public List<Map<String,String>> getWireSegs(String cutOverCuid){
		 DataObjectList tempCutoverWireSegs = null;
		 List<Map<String,String>> result = new ArrayList<Map<String,String>>();
		 //String sql = TempCutoverWireSeg.AttrName.relatedCutoverTaskCuid + "= '" + cutOverCuid + "'";
		 String sql = "select cuid,label_cn from TEMP_CUTOVER_WIRE_SEG where " + TempCutoverWireSeg.AttrName.relatedCutoverTaskCuid + "= '" + cutOverCuid + "'";;
		 TempCutoverWireSegBO bo = new TempCutoverWireSegBO();
		// tempCutoverWireSegs = bo.getTempCutoverWireSegByTaskCuid(new BoQueryContext(), cutOverCuid);
		 try {
			//tempCutoverWireSegs = getDuctManagerBO().getObjectsBySql(sql, new TempCutoverWireSeg());
			Class[] cla = new Class[]{String.class,String.class};
			tempCutoverWireSegs = getDuctManagerBO().getDatasBySql(sql, cla);
			for (GenericDO gdo : tempCutoverWireSegs) {
				Map<String, String> map = new HashMap<String, String>();
				map.put("CUID", gdo.getAttrString("1"));
				map.put("WIRE_SEG_NAME", gdo.getAttrString("2"));
				result.add(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new UserException(e.getMessage());
		}
		 return result;
	 }
	 
	 private IDuctManagerBO getDuctManagerBO(){
		 return BoHomeFactory.getInstance().getBO(IDuctManagerBO.class);
	 }
}
