package com.boco.irms.app.dm.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.boco.transnms.common.dto.FiberJointBox;
import com.boco.transnms.common.dto.base.BoActionContext;
import com.boco.transnms.common.dto.base.DataObjectList;
import com.boco.transnms.common.dto.base.GenericDO;
import com.boco.transnms.server.bo.base.BoHomeFactory;
import com.boco.transnms.server.bo.ibo.dm.IDuctManagerBO;
import com.boco.transnms.server.bo.ibo.dm.IFiberJointBoxBO;

public class MovePointResourceAction {
	private IDuctManagerBO getDuctManagerBO(){
		return BoHomeFactory.getInstance().getBO(IDuctManagerBO.class);
	}
	private IFiberJointBoxBO getfiBoxBO(){
		return BoHomeFactory.getInstance().getBO(IFiberJointBoxBO.class);
	}
	
	public void modifyPointLocation(List<Map> oldData){
		HashMap<String,String> pointMap = new HashMap<String,String>();
		for(Map data: oldData){
			String cuid = data.get("CUID").toString();
			String latiLongi = data.get("LONGILATI").toString();
			pointMap.put(cuid, latiLongi);
			DataObjectList fiberjointbox = getfiBoxBO().getFiberJointBoxsByRelateLocationCuid(new BoActionContext(), cuid);	
			for(GenericDO fjb: fiberjointbox){
				String jointBoxCuid = fjb.getCuid();				
				pointMap.put(jointBoxCuid, latiLongi);
			}					
			
		}		
		getDuctManagerBO().modifyMovePointLocation(new BoActionContext(),pointMap);
	}

}
