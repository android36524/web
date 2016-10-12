package com.boco.irms.app.dm.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import com.boco.common.util.debug.LogHome;
import com.boco.common.util.except.UserException;
import com.boco.core.ibatis.vo.ServiceActionContext;
import com.boco.gis.rest.DmDesignerTools;
import com.boco.transnms.client.model.base.BoCmdFactory;
import com.boco.transnms.common.bussiness.consts.DuctEnum;
import com.boco.transnms.common.dto.Accesspoint;
import com.boco.transnms.common.dto.AnOnu;
import com.boco.transnms.common.dto.DdmPortToFiber;
import com.boco.transnms.common.dto.Fcabport;
import com.boco.transnms.common.dto.Fiber;
import com.boco.transnms.common.dto.FiberCab;
import com.boco.transnms.common.dto.FiberDp;
import com.boco.transnms.common.dto.FiberDpPort;
import com.boco.transnms.common.dto.FiberJointBox;
import com.boco.transnms.common.dto.FiberJointPoint;
import com.boco.transnms.common.dto.Fibercabmodule;
import com.boco.transnms.common.dto.InterWire;
import com.boco.transnms.common.dto.JumpFiber;
import com.boco.transnms.common.dto.LinkPort;
import com.boco.transnms.common.dto.Odf;
import com.boco.transnms.common.dto.Odfmodule;
import com.boco.transnms.common.dto.Odfport;
import com.boco.transnms.common.dto.Ptp;
import com.boco.transnms.common.dto.TransElement;
import com.boco.transnms.common.dto.WireSeg;
import com.boco.transnms.common.dto.WireSystem;
import com.boco.transnms.common.dto.base.BoActionContext;
import com.boco.transnms.common.dto.base.BoQueryContext;
import com.boco.transnms.common.dto.base.DataObjectList;
import com.boco.transnms.common.dto.base.DboCollection;
import com.boco.transnms.common.dto.base.GenericDO;
import com.boco.transnms.server.bo.base.BoHomeFactory;
import com.boco.transnms.server.bo.helper.cm.JumpFiberBOHelper;
import com.boco.transnms.server.bo.ibo.cm.IDistrictBO;
import com.boco.transnms.server.bo.ibo.cm.IInterWireBO;
import com.boco.transnms.server.bo.ibo.cm.IPTPBO;
import com.boco.transnms.server.bo.ibo.cm.IPhyOdfBO;
import com.boco.transnms.server.bo.ibo.cm.IPhyOdfPortBO;
import com.boco.transnms.server.bo.ibo.cm.IPhyOdfmoduleBOX;
import com.boco.transnms.server.bo.ibo.dm.IDuctManagerBO;
import com.boco.transnms.server.bo.ibo.dm.IFcabportBO;
import com.boco.transnms.server.bo.ibo.dm.IFiberBO;
import com.boco.transnms.server.bo.ibo.dm.IFiberCabBO;
import com.boco.transnms.server.bo.ibo.dm.IFiberDpBO;
import com.boco.transnms.server.bo.ibo.dm.IFiberDpPortBO;
import com.boco.transnms.server.bo.ibo.dm.IFiberJointBoxBO;
import com.boco.transnms.server.bo.ibo.dm.IFiberJointPointBO;
import com.boco.transnms.server.bo.ibo.dm.IFibercabmoduleBO;
import com.boco.transnms.server.bo.ibo.dm.IWireSystemBO;
@SuppressWarnings({"rawtypes","unchecked"})
public class FiberLinkAction {
        
	    private static Map<String,String> DEVICE_TYPE = new HashMap<String, String>();
		private static Map<String,String> ICON_MAP = new HashMap<String, String>();
		
		static {
			DEVICE_TYPE.put("ODF", "机架");
			DEVICE_TYPE.put("FIBER_CAB", "交接箱");
			DEVICE_TYPE.put("FIBER_DP", "分纤箱");
			DEVICE_TYPE.put("FIBER_JOINT_BOX", "接头盒");
			
			ICON_MAP.put("光缆", "/resources/map/WIRE_SEG.png");
			ICON_MAP.put("纤芯", "/resources/topo/dm/isfixed.gif");
		}
		/**
		 * 根据光分纤箱cuid查询关联的光缆信息（光缆+光缆段）
		 * 
		 * @param fibercab 光交接箱cuid
		 */
		public List getWireInfoByFCab(String fibercabcuid) throws Exception{
			if (fibercabcuid == null || fibercabcuid.trim().length() == 0){
				return null;	
			}
			//查询以站点为起始点的光缆段，并通过光缆段查询光缆系统  by liuyumiao
			HashMap wireSystemMap = null;
			if(fibercabcuid.startsWith("INTER_WIRE"))
			{
				DataObjectList wireSegdev = null; 
		        String sql = "("+WireSeg.AttrName.cuid + "='" + fibercabcuid + "' )"; 
		        IDuctManagerBO getDuctManBO = BoHomeFactory.getInstance().getBO(IDuctManagerBO.class);
		        wireSegdev = getDuctManBO.getObjectsBySql(sql, new InterWire());
				wireSystemMap = getWireSystemMap(new BoActionContext(),wireSegdev);
			}
			else
			{
				IDuctManagerBO IDuctManBO = BoHomeFactory.getInstance().getBO(IDuctManagerBO.class);
				GenericDO fiberCabObj = IDuctManBO.getObjByCuid(new BoActionContext(), fibercabcuid);
				if(fiberCabObj!=null){
					String relatedSiteCuid = (String) fiberCabObj.getAttrValue(FiberCab.AttrName.relatedSiteCuid);
					DataObjectList wireSegsBySql = getWireSegsBySql(new BoActionContext(),relatedSiteCuid);
					wireSystemMap = getWireSystemMap(new BoActionContext(),wireSegsBySql);
				}
			}
			
			IWireSystemBO ibo = BoHomeFactory.getInstance().getBO(IWireSystemBO.class);
			HashMap<WireSystem, DataObjectList> dataList = ibo.getWireSyetemByDevCuid(new BoActionContext(), fibercabcuid);
			if(wireSystemMap!=null){
				dataList.putAll(wireSystemMap);
			}
			if (dataList == null || dataList.size() == 0) {
				return null;
			}
			String icon = ICON_MAP.get("光缆");
			// 光缆集合
			List<Map<String, Object>> wireSystemList = new ArrayList<Map<String, Object>>();
			// 光缆段集合
			List<Map<String, Object>> wireSegList = new ArrayList<Map<String, Object>>();

			for(Map.Entry<WireSystem, DataObjectList> entry : dataList.entrySet()) {
				WireSystem ws = entry.getKey();
				boolean isAdd = false;
				for (int i = 0; i < wireSystemList.size(); i++ ){
					Map<String,Object> sys = wireSystemList.get(i);
					if (sys.get("CUID").equals(ws.getCuid())){
						isAdd = true;
						break;
					}
				}
				
				if (isAdd == false) {
					Map attr = ws.getAllAttr();
					attr.put("ICON", icon);
					wireSystemList.add(attr);
				}

				List segList = entry.getValue();
				if (segList == null || segList.size() ==0 ) {
					continue;
				}
				for(Object item : segList) {
					if(item instanceof WireSeg)
					{
						WireSeg seg = (WireSeg) item;
						Map attr = seg.getAllAttr();
						attr.put("ICON", icon);
						wireSegList.add(attr);
					}
					else if(item instanceof InterWire)
					{
						InterWire seg = (InterWire) item;
						Map attr = seg.getAllAttr();
						attr.put("ICON", icon);
						wireSegList.add(attr);
					}
				}
			}

			List result = new ArrayList();
			result.add(wireSystemList);
			result.add(wireSegList);

			return result;
		}
		/**
		 * 传入cuid为inter_wire，因此没有光缆系统
		 * @param fibercabcuid
		 * @return
		 * @throws Exception
		 */
		public List getWireInfoByInterwire(String interwireCuid) throws Exception{
			if (interwireCuid == null || interwireCuid.trim().length() == 0){
				return null;	
			}
			//根据传入层间光缆查询数据  by chenhao
			DataObjectList wireSegdev = null; 
	        String sql = "("+WireSeg.AttrName.cuid + "='" + interwireCuid + "' )"; 
	        IDuctManagerBO getDuctManBO = BoHomeFactory.getInstance().getBO(IDuctManagerBO.class);
	        wireSegdev = getDuctManBO.getObjectsBySql(sql, new InterWire());
			String icon = ICON_MAP.get("光缆");
			List<Map<String, Object>> wireSegList = new ArrayList<Map<String, Object>>();
	        for(Object item : wireSegdev) {
				if(item instanceof InterWire)
				{
					InterWire seg = (InterWire) item;
					Map attr = seg.getAllAttr();
					attr.put("ICON", icon);
					wireSegList.add(attr);
				}
			}
			List result = new ArrayList();
			result.add(wireSegList);
			return result;
		}
		/**
		 * 根据光缆段查找相关的纤芯
		 * 
		 * @param segCuid 光缆段cuid
		 */
		public List getFiberByWireSeg(String segCuid) throws Exception{
			if (StringUtils.isBlank(segCuid)) {
				return null;
			}
			IFiberBO ibo = BoHomeFactory.getInstance().getBO(IFiberBO.class);
			DataObjectList fiberList = ibo.getFibersByWireSegCuid(new BoActionContext(), segCuid);
			if (fiberList == null || fiberList.size() == 0) {
				return null;
			}
			
			List result = new ArrayList();
			
			String icon = ICON_MAP.get("纤芯");
			for(int i=0;i<fiberList.size();i++)
			{
				GenericDO child = fiberList.get(i);
				Map attrMap = child.getAllAttr();
				attrMap.put("ICON", icon);
				result.add(attrMap);
			}
			//纤芯根据序号排序
			Collections.sort(result, new Comparator(){
				@Override
				public int compare(Object o1, Object o2) {
					if(o1 instanceof Map && o2 instanceof Map)
					{
						Object no1 = ((Map) o1).get("WIRE_NO");
						Object no2 = ((Map) o2).get("WIRE_NO");
						if(no1 instanceof Long && no2 instanceof Long)
						{
							long l1= (Long) no1;
							long l2= (Long) no2;
							if(l1 > l2)
							{
								return 1;
							}
							else
							{
								return -1;
							}
						}
					}
					return 0;
				}
			});
			return result;
		}
		
		/**
		 * 根据模块查找相关的端子
		 * 
		 * @param segCuid 光缆段cuid
		 */
		public List getPortByMoudle(String segCuid) throws Exception{
			if (StringUtils.isBlank(segCuid)) {
				return null;
			}
			//ODF端子
			if(segCuid.startsWith("ODF"))
			{
				IPhyOdfPortBO ibo = BoHomeFactory.getInstance().getBO(IPhyOdfPortBO.class);
				DataObjectList odfportList = ibo.getODFPortByODMCuid(new BoActionContext(), segCuid);
				if (odfportList == null || odfportList.size() == 0) {
					return null;
				}
				List result = new ArrayList();
				for(int i=0;i<odfportList.size();i++)
				{
					GenericDO child = odfportList.get(i);
					Odfport ports = (Odfport) child;
					String icon = "/resources/topo/alarm/c.png";
					if (ports.getIsConnectedToFiber()) {
						icon = "/resources/topo/alarm/w.png";
					}
					Map attrMap = child.getAllAttr();
					attrMap.put("ICON", icon);
					result.add(attrMap);
				}
				return result;
			}
			else
			{
				IFcabportBO ibo = BoHomeFactory.getInstance().getBO(IFcabportBO.class);
				DataObjectList fiberList = ibo.getFcabportsByModuleCuid(new BoActionContext(), segCuid);
				if (fiberList == null || fiberList.size() == 0) {
					return null;
				}
				List result = new ArrayList();
				for(int i=0;i<fiberList.size();i++)
				{
					GenericDO child = fiberList.get(i);
					Fcabport ports = (Fcabport) child;
					String icon = "/resources/topo/alarm/c.png";
					if (ports.getIsConnectedToFiber()) {
						icon = "/resources/topo/alarm/w.png";
					}
					Map attrMap = child.getAllAttr();
					attrMap.put("ICON", icon);
					result.add(attrMap);
				}
				return result;
			}
		}
		/**
		 * 根据纤芯查找端子
		 * 
		 * @param fiberCuid 纤芯cuid
		 */
		public List getSidePointsByFiberCuid(String fiberCuid) throws Exception {
			if (StringUtils.isBlank(fiberCuid)) {
				return null;
			}
			DataObjectList portList = _getSidePointsByFiberCuid(fiberCuid);

			if (portList == null || portList.size() == 0) {
				return null;
			}
			
			IFiberBO ibo = BoHomeFactory.getInstance().getBO(IFiberBO.class);
			Fiber fiber = ibo.getfiberByCuid(new BoActionContext(), fiberCuid);

			List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
			for (GenericDO dbo : portList) {
				if(dbo instanceof Ptp)
				{
					Ptp port = (Ptp) dbo;
					String icon = "/resources/topo/alarm/c.png";
					port.setAttrValue("ICON", icon);
					String deviceCuid = (String)port.getAttrValue("RELATED_NE_CUID");
					String typeName = "";
					String deviceName = "";
					DataObjectList posports =  null;
					Class[] classType = new Class[] {String.class};
					try{
						IDuctManagerBO ductmanagerBo = (IDuctManagerBO) BoHomeFactory.getInstance().getBO(IDuctManagerBO.class);
						posports = ductmanagerBo.getDatasBySql("SELECT LABEL_CN FROM AN_POS WHERE CUID='"+deviceCuid+"'", classType);
					}catch(Exception e){
						LogHome.getLog().error(e);
					}
					for(GenericDO gdo : posports)
					{
						typeName = "POS";
						deviceName = gdo.getAttrString("1");
					}
					if(StringUtils.isBlank(typeName))
					{
						try{
							IDuctManagerBO ductmanagerBo = (IDuctManagerBO) BoHomeFactory.getInstance().getBO(IDuctManagerBO.class);
							posports = ductmanagerBo.getDatasBySql("SELECT LABEL_CN FROM AN_ONU WHERE CUID='"+deviceCuid+"'", classType);
						}catch(Exception e){
							LogHome.getLog().error(e);
						}
						for(GenericDO gdo : posports)
						{
							typeName = "ONU";
							deviceName = gdo.getAttrString("1");
						}
					}
					if (port.getCuid().equals(fiber.getOrigPointCuid())) {
						port.setLabelCn("A端: " + port.getLabelCn() + "【" + typeName
								+ "：" + deviceName + "】");
					}
					if (port.getCuid().equals(fiber.getDestPointCuid())) {
						port.setLabelCn("Z端: " + port.getLabelCn() + "【" + typeName
								+ "：" + deviceName + "】");
					}
					
					result.add(port.getAllAttr());
				}
				else
				{
					LinkPort port = (LinkPort) dbo;
					String icon = "/resources/topo/alarm/c.png";
					if (port.getIsConnectedToFiber()) {
						icon = "/resources/topo/alarm/w.png";
					}
					if (port.getServiceState() == DuctEnum.DM_SERVICE_STATE._impropriate) {
						icon = "/resources/topo/alarm/m.png";
					} else if (port.getServiceState() == DuctEnum.DM_SERVICE_STATE._bad) {
						icon = "/resources/topo/alarm/StopSound.png";
					} else if (port.getServiceState() == DuctEnum.DM_SERVICE_STATE._destine) {
						icon = "/resources/topo/alarm/u.png";
					}
					
					port.setAttrValue("ICON", icon);
					GenericDO device = (GenericDO)port.getAttrValue("RELATED_DEVICE_CUID");
					String className = device.getClassName().split("-")[0];
					String typeName = getDeviceTypeLabel(className);
					if (port.getCuid().equals(fiber.getOrigPointCuid())) {
						port.setLabelCn("A端: " + port.getLabelCn() + "【" + typeName
								+ "：" + device.getAttrString("LABEL_CN") + "】");
					}
					if (port.getCuid().equals(fiber.getDestPointCuid())) {
						port.setLabelCn("Z端: " + port.getLabelCn() + "【" + typeName
								+ "：" + device.getAttrString("LABEL_CN") + "】");
					}
					
					result.add(port.getAllAttr());
				}
			}

			return result;
		}
		
		private static String getDeviceTypeLabel(String className) {
			String typeName = DEVICE_TYPE.get(className);
			if (typeName == null) {
				typeName = "机架";
			}
			return typeName;
		}
		
		private DataObjectList _getSidePointsByFiberCuid(String fiberCuid) {
			IFiberBO ibo = BoHomeFactory.getInstance().getBO(IFiberBO.class);
			return ibo.getSidePointsByFiberCuid(new BoActionContext(), fiberCuid);
		}
		
		/**
		 * 纤芯关联---关联---操作处理
		 * 
		 * @param fiberCuids 以逗号分隔的纤芯cuid字符串
		 * @param portCuids 以逗号分隔的端子cuid字符串
		 * @return
		 */
		public List<String> addFibersConnect(HttpServletRequest request,String fiberCuids, String portCuids) throws Exception{
			if (StringUtils.isBlank(fiberCuids) || StringUtils.isBlank(portCuids)) {
				return new ArrayList();
			}
			IFiberBO ibo = BoHomeFactory.getInstance().getBO(IFiberBO.class);
			DmDesignerTools.setActionContext(new ServiceActionContext(request));
			BoActionContext context = DmDesignerTools.getActionContext();
			List<String> result = new ArrayList<String>();
			try {
                //将存量和设计的端子分开
//				String [] portCuidArr=portCuids.split(",");
//				String [] fiberCuidArr=fiberCuids.split(",");
//				String portCuidsSJ="";
//				String fiberCuidsSJ="";
//				DataObjectList portCL=new DataObjectList();
//				DataObjectList fiberCL=new DataObjectList();
//				for(int i=0;i<portCuidArr.length;i++){
//				    Fcabport port=BoHomeFactory.getInstance().getBO(IFcabportBO.class).getFcabportByCuid(new BoActionContext(), portCuidArr[i]);
//					if(port!=null){
//						portCuidsSJ=portCuidArr[i]+",";
//						fiberCuidsSJ=fiberCuidArr[i]+",";
//					}else{
//						//这里直接将纤芯的orig_point_cuid或者dest_point_cuid更新为端口cuid
//						Fiber fiber=BoHomeFactory.getInstance().getBO(IFiberBO.class).getfiberByCuid(new BoActionContext(), fiberCuidArr[i]);
//						if ((fiber.getAttrValue("ORIG_POINT_CUID") == null) || (((String)fiber.getAttrValue("ORIG_POINT_CUID")).trim().length() == 0)){
//							fiber.setOrigPointCuid(portCuidArr[i]);
//							getDMGenericDAOX().updateDbo(new BoActionContext(), fiber);
//						}else if ((fiber.getAttrValue("DEST_POINT_CUID") == null) || (((String)fiber.getAttrValue("DEST_POINT_CUID")).trim().length() == 0)){
//							fiber.setDestPointCuid(portCuidArr[i]);
//							getDMGenericDAOX().updateDbo(new BoActionContext(), fiber);
//						}else{
//							 throw new UserException(fiber.getLabelCn() + "的两端已经做了关联");
//						}
//						result.add(fiberCuidArr[i]);
//						portCL.add(port);
//						fiberCL.add(fiber);
//					}
//				}
//				//存量的端子关联设计的纤芯时，入DDM_PORT_TO_FIBER库
//				addDdmPortToFiber(portCL,fiberCL);
				//这里取到的仅仅是设计中的数据
				DataObjectList fibers = _getFiberByCuids(context, fiberCuids);
				DataObjectList ports = _getFCabPortByCuid(context, portCuids);
				//将port按portCuids重新排序
				DataObjectList dataList = ibo.addFibersConnect(context, fibers,ports);
				if (dataList == null || dataList.size() == 0) {
					return new ArrayList();
				}
				result.add(0, "SUCCESS");
				for (GenericDO dbo : dataList) {
					Fiber fiber = (Fiber) dbo;
					result.add(fiber.getCuid());
				}
			} catch (Exception ex) {
				result.add(0, ex.getMessage());
			}
			
			return result;
		}
		
		public List<String> addFibersConnectByAccesspoint(HttpServletRequest request,String fiberCuids, String portCuids) throws Exception{
			if (StringUtils.isBlank(fiberCuids) || StringUtils.isBlank(portCuids)) {
				return new ArrayList();
			}
			IFiberBO ibo = BoHomeFactory.getInstance().getBO(IFiberBO.class);
			DmDesignerTools.setActionContext(new ServiceActionContext(request));
			BoActionContext context = DmDesignerTools.getActionContext();
			List<String> result = new ArrayList<String>();
			try {
				//这里取到的仅仅是设计中的数据
				DataObjectList fibers = _getFiberByCuids(context, fiberCuids);
				DataObjectList ports = _getPortByCuid(context, portCuids);
				//将port按portCuids重新排序
				DataObjectList dataList = ibo.addFibersConnect(context, fibers,ports);
				if (dataList == null || dataList.size() == 0) {
					return new ArrayList();
				}
				result.add(0, "SUCCESS");
				for (GenericDO dbo : dataList) {
					Fiber fiber = (Fiber) dbo;
					result.add(fiber.getCuid());
				}
			} catch (Exception ex) {
				result.add(0, ex.getMessage());
			}
			
			return result;
		}
		
		private DataObjectList _getPortByCuid(BoActionContext context, String portCuids) {
			DataObjectList resultPorts=new DataObjectList();
			IFcabportBO ibo = BoHomeFactory.getInstance().getBO(IFcabportBO.class);
			String sql = "cuid in ('" + portCuids.replace(",", "','") + "')";
//			DataObjectList ports= ibo.getFcabportBySql(context, sql);
			for (String cuid : portCuids.split(",")) {
				if(cuid.startsWith("ODFPORT"))
				{
					Odfport port = BoHomeFactory.getInstance().getBO(IPhyOdfPortBO.class).getOdfportByCuid(new BoActionContext(), cuid);
					resultPorts.add(port);
				}
				else if(cuid.startsWith("FIBER_DP_PORT"))
				{
					FiberDpPort port = BoHomeFactory.getInstance().getBO(IFiberDpPortBO.class).getFiberDpPortByCuid(new BoActionContext(), cuid);
					resultPorts.add(port);
				}
				else if(cuid.startsWith("FIBER_JOINT_POINT"))
				{
					FiberJointPoint port = BoHomeFactory.getInstance().getBO(IFiberJointPointBO.class).getFiberJointPointByCuid(new BoActionContext(), cuid);
					resultPorts.add(port);
				}
				else if(cuid.startsWith("FCABPORT"))
				{
					Fcabport port = BoHomeFactory.getInstance().getBO(IFcabportBO.class).getFcabportByCuid(new BoActionContext(), cuid);
					resultPorts.add(port);
				}
				else if(cuid.startsWith("PTP"))
				{
					Ptp port = BoHomeFactory.getInstance().getBO(IPTPBO.class).getPTPByCuid(new BoActionContext(), cuid);
					resultPorts.add(port);
				}
			}
			return resultPorts;
		}
		
		private DataObjectList _getFCabPortByCuid(BoActionContext context, String portCuids) {
			DataObjectList resultPorts = new DataObjectList();
			DataObjectList ports = new DataObjectList();
			//modify by chenhao 查询光交端子和多媒体箱内置ONU端子
			if(portCuids.indexOf("FCABPORT") != -1)
			{
				IFcabportBO ibo = BoHomeFactory.getInstance().getBO(IFcabportBO.class);
				String sql = "cuid in ('" + portCuids.replace(",", "','") + "')";
				ports = ibo.getFcabportBySql(context, sql);
				for (String cuid : portCuids.split(",")) {
					for(int i=0;i<ports.size();i++){
						Fcabport port=(Fcabport)ports.get(i);
						if(cuid.equalsIgnoreCase(port.getCuid())){
							resultPorts.add(port);
						}
					}
				}
			}
			else
			{
				String[] arr = portCuids.split(",");
				for(String cuid : arr)
				{
					GenericDO gdo = BoHomeFactory.getInstance().getBO(IPTPBO.class)
							.getPTPByCuid(new BoActionContext(), cuid);
					if(gdo != null)
					{
						ports.add(gdo);
					}
				}
				for (String cuid : portCuids.split(",")) {
					for(int i = 0;i < ports.size();i++){
						Ptp port = (Ptp) ports.get(i);
						if(cuid.equalsIgnoreCase(port.getCuid())){
							resultPorts.add(port);
						}
					}
				}
			}
			//modify end
			return resultPorts;
		}
		
		
		/**
		 * 查找纤芯
		 * 
		 * @param context
		 * @param fiberCuids 逗号分隔的纤芯cuid字符串，两头无逗号
		 * @return
		 */ 
		private DataObjectList _getFiberByCuids(BoActionContext context, String fiberCuids) {
			List<String> fiberCuidList = new ArrayList<String>();
			for (String cuid : fiberCuids.split(",")) {
				fiberCuidList.add(cuid);
			}
			IFiberBO ibo = BoHomeFactory.getInstance().getBO(IFiberBO.class);
			DataObjectList fibers = ibo.getfiberByCuids(context, fiberCuidList);
			
			return fibers;
		}
		
		/**
		 * 删除端子和纤芯的关系
		 * 
		 * @param fiberMap
		 * @return
		 * @throws Exception
		 */
		public Map deleteFibersConnect(HttpServletRequest request,Map<String, String> fiberMap) throws Exception{
			if (fiberMap == null || fiberMap.size() == 0) {
				return null;
			}

			HashMap params = new HashMap();
			IFiberBO ibo = BoHomeFactory.getInstance().getBO(IFiberBO.class);
			DmDesignerTools.setActionContext(new ServiceActionContext(request));
			BoActionContext context = DmDesignerTools.getActionContext();
			Map result = new HashMap();
			DataObjectList fiberPortsCL=new DataObjectList();
			try{
				for (Map.Entry<String, String> entry : fiberMap.entrySet()) {
					Fiber fiber = (Fiber) ibo.getfiberByCuid(context,
							entry.getKey());
					DataObjectList fiberPorts = _getFCabPortByCuid(context,
							entry.getValue());
					fiberPortsCL.addAll(fiberPorts);
					params.put(fiber, fiberPorts);
				}

				Map dataMap = ibo.deleteFibersConnect(context, params);
				//添加判断，如果是存量占用的端子，断开连接后状态任然是占用
				String[] portCuids = new String[fiberPortsCL.size()];
				for (int i = 0; i < portCuids.length; i++) {
					Fcabport fcabport =(Fcabport)fiberPortsCL.get(i);
					portCuids[i] = fcabport.getCuid();
				}
				DataObjectList list = new DataObjectList();
				IDuctManagerBO ductManagerBO=	BoHomeFactory.getInstance().getBO(IDuctManagerBO.class);
				DataObjectList resGdo = (DataObjectList)BoCmdFactory.getInstance().execBoCmd("IOrientedDesignBO.getObjsByCuids",context,portCuids);
				for (int j=0;j<resGdo.size();j++){
					GenericDO child = resGdo.get(j);
					if(child!=null && child.getAttrBool("IS_CONNECTED_TO_FIBER")){
						//update 设计中的数据的字段为占用
						for(int i = 0; i < fiberPortsCL.size(); i++){
							Fcabport fcabportSJ =(Fcabport)fiberPortsCL.get(i);
							if(fcabportSJ.getCuid().equals(child.getCuid())){
								fcabportSJ.setAttrValue("IS_CONNECTED_TO_FIBER", true);
								list.add(fcabportSJ);
								break;
							}
						}
					}
				}
				if(list.size()>0){
					ductManagerBO.updateDbos(new BoActionContext(), list);
				}
				// 只对断开失败的纤芯加以返回，以便页面撤消断开操作
				Map fail_map = (Map)dataMap.get("FAIL");
				Map<String, Object> fail_result = new HashMap<String, Object>();
				Iterator iter = fail_map.entrySet().iterator();
				while (iter.hasNext()) {
					Fiber fiber = (Fiber)iter.next();
					fail_result.put(fiber.getCuid(), fail_map.get(fiber));
				}
				result.put("_MESSAGE", "SUCCESS"); // 这里的SUCCESS仅仅表示ibo.deleteFibersConnect无异常
				result.put("_FAIL_DATA", fail_result); // 返回操作失败相关的纤芯和失败原因
			}catch(Exception ex) {
				LogHome.getLog().error("纤芯关联[断开]操作异常：", ex);
				result.put("_MESSAGE", ex.getMessage());
			}
			return result;
		}
		/**
		 * 获得子节点信息
		 */
		public List getChildrenByParentCuid(String parentCuid) {
			if (parentCuid.startsWith(FiberCab.CLASS_NAME)) {
	            return getFiberCabChildren(parentCuid);
	        }
			else if (parentCuid.startsWith(Fibercabmodule.CLASS_NAME)) {
	            return getFibercabmoduleChildren(parentCuid);
	        }
			else if (parentCuid.startsWith(FiberDp.CLASS_NAME)) {
	            return getFiberDpChildren(parentCuid);
	        }
			else if(parentCuid.startsWith(Fcabport.CLASS_NAME))
			{
				return getFportChildren(parentCuid);
			}
			else if(parentCuid.startsWith(FiberDpPort.CLASS_NAME))
			{
				return getFportChildren(parentCuid);
			}
			else if(parentCuid.startsWith(Odf.CLASS_NAME))
			{
				return getOdfmoduleChildren(parentCuid);
			}
			else if(parentCuid.startsWith(Odfmodule.CLASS_NAME))
			{
				return getOdfportChildren(parentCuid);
			}
			else if(parentCuid.startsWith(TransElement.CLASS_NAME))
			{
				return getPosportChildren(parentCuid);
			}
			else if(parentCuid.startsWith(FiberJointBox.CLASS_NAME))
			{
				return getFiberJointboxChildren(parentCuid);
			}
			return null;
		}
		private List getOdfmoduleChildren(String parentCuid)
		{
			DataObjectList odfmodules =  null;
			try{
				odfmodules = BoHomeFactory.getInstance().getBO(IPhyOdfmoduleBOX.class).getOdmsBySql(new BoActionContext(), "RELATED_DEVICE_CUID='"+parentCuid+"'");
			}catch(Exception e){
				LogHome.getLog().error(e);
			}
			return converToList(odfmodules);
		}
		private List getOdfportChildren(String parentCuid)
		{
			DataObjectList odfports =  null;
			try{
				odfports = BoHomeFactory.getInstance().getBO(IPhyOdfPortBO.class).getOdfportBySql(new BoActionContext(), "RELATED_MODULE_CUID='"+parentCuid+"'");
			}catch(Exception e){
				LogHome.getLog().error(e);
			}
			return converToList(odfports);
		}
		private List getPosportChildren(String parentCuid)
		{
			DataObjectList posports =  null;
			try{
				Class[] classType = new Class[] {String.class, String.class};
				IDuctManagerBO ductmanagerBo = (IDuctManagerBO) BoHomeFactory.getInstance().getBO(IDuctManagerBO.class);
				posports = ductmanagerBo.getDatasBySql("SELECT CUID, LABEL_CN FROM PTP WHERE RELATED_NE_CUID='"+parentCuid+"'", classType);
			}catch(Exception e){
				LogHome.getLog().error(e);
			}
			for(GenericDO gdo : posports)
			{
				gdo.setAttrValue("CUID", gdo.getAttrString("1"));
				gdo.setAttrValue("LABEL_CN", gdo.getAttrString("2"));
			}
			return converToList(posports);
		}
		/**
		 * 根据传入类型（接入点或层间光缆），查询交接箱、分纤箱、ONU、POS、ODF
		 */
		public List getAccesspointChildren(String accesspointCuid)
		{
			List list = new ArrayList();
			if(accesspointCuid.startsWith("ACCESSPOINT"))
			{
				//交接箱
				list.addAll(getFibercabBySql(" RELATED_SITE_CUID='"+accesspointCuid+"' "));
				//分纤箱
				list.addAll(getFiberdpBySql(" RELATED_SITE_CUID='"+accesspointCuid+"' "));
				//终端盒
				list.addAll(getJointboxBySql(" RELATED_LOCATION_CUID='"+accesspointCuid+"' "));
				//ONU
				list.addAll(getOnuBySql(" RELATED_SITE_CUID='"+accesspointCuid+"' "));
				//POS
				list.addAll(getPosBySql(" RELATED_SITE_CUID='"+accesspointCuid+"' "));
				//ODF
				list.addAll(getOdfBySql(" RELATED_SITE_CUID='"+accesspointCuid+"' "));
				return list;
			}
			else if(accesspointCuid.startsWith("INTER_WIRE"))
			{
				InterWire interWire =  null;
				try{
					interWire = BoHomeFactory.getInstance().getBO(IInterWireBO.class).getInterWireByCuid(new BoActionContext(), accesspointCuid);
				}catch(Exception e){
					LogHome.getLog().error(e);
				}
				list.addAll(getInterwireChildren(interWire.getDestPointCuid()));
				list.addAll(getInterwireChildren(interWire.getOrigPointCuid()));
			}
			return list;
		}
		private List<Map> getOdfBySql(String sql) {
			DataObjectList odfdevices =  null;
			try{
				odfdevices = BoHomeFactory.getInstance().getBO(IPhyOdfBO.class).getOdfsBySql(new BoActionContext(), sql);
			}catch(Exception e){
				LogHome.getLog().error(e);
			}
			List<Map> odfList = converToList(odfdevices);
			for(Map map : odfList)
			{
				map.put("LABEL_CN", map.get("LABEL_CN"));
				map.put("DEVICE_TABLE_TYPE", "ODF");
			}
			return odfList;
		}
		private List<Map> getPosBySql(String sql) {
			DataObjectList posdevices =  null;
			try{
				Class[] classType = new Class[] {String.class, String.class};
				IDuctManagerBO ductmanagerBo = (IDuctManagerBO) BoHomeFactory.getInstance().getBO(IDuctManagerBO.class);
				posdevices = ductmanagerBo.getDatasBySql("SELECT CUID, LABEL_CN FROM AN_POS WHERE " + sql, classType);
			}catch(Exception e){
				LogHome.getLog().error(e);
			}
			for(GenericDO gdo : posdevices)
			{
				gdo.setAttrValue("CUID", gdo.getAttrString("1"));
				gdo.setAttrValue("LABEL_CN", gdo.getAttrString("2"));
			}
			List<Map> posList = converToList(posdevices);
			for(Map map : posList)
			{
				map.put("DEVICE_TABLE_TYPE", "POS");
			}
			return posList;
		}
		private List<Map> getOnuBySql(String sql) {
			DataObjectList onudevices =  null;
			try{
				Class[] classType = new Class[] {String.class, String.class};
				IDuctManagerBO ductmanagerBo = (IDuctManagerBO) BoHomeFactory.getInstance().getBO(IDuctManagerBO.class);
				onudevices = ductmanagerBo.getDatasBySql("SELECT CUID, LABEL_CN FROM AN_ONU WHERE " + sql, classType);
			}catch(Exception e){
				LogHome.getLog().error(e);
			}
			for(GenericDO gdo : onudevices)
			{
				gdo.setAttrValue("CUID", gdo.getAttrString("1"));
				gdo.setAttrValue("LABEL_CN", gdo.getAttrString("2"));
			}
			List<Map> onuList = converToList(onudevices);
			for(Map map : onuList)
			{
				map.put("DEVICE_TABLE_TYPE", "ONU");
			}
			return onuList;
		}
		private List<Map> getJointboxBySql(String sql) {
			DataObjectList fiberjointdevices =  null;
			try{
				fiberjointdevices = BoHomeFactory.getInstance().getBO(IFiberJointBoxBO.class).getFiberJointBoxBySql(new BoActionContext(), sql);
			}catch(Exception e){
				LogHome.getLog().error(e);
			}
			List<Map> fdpList = converToList(fiberjointdevices);
			for(Map map : fdpList)
			{
				map.put("DEVICE_TABLE_TYPE", "FIBER_JOINT_BOX");
			}
			return fdpList;
		}
		private List<Map> getFiberdpBySql(String sql) {
			DataObjectList fiberdpdevices =  null;
			try{
				fiberdpdevices = BoHomeFactory.getInstance().getBO(IFiberDpBO.class).getFiberDpBySql(new BoActionContext(), sql);
			}catch(Exception e){
				LogHome.getLog().error(e);
			}
			List<Map> fdpList = converToList(fiberdpdevices);
			for(Map map : fdpList)
			{
				map.put("DEVICE_TABLE_TYPE", "FIBER_DP");
			}
			return fdpList;
		}
		private List<Map> getFibercabBySql(String sql) {
			DataObjectList fibercabdevices =  null;
			try{
				fibercabdevices = BoHomeFactory.getInstance().getBO(IFiberCabBO.class).getFiberCabBySql(new BoActionContext(), sql);
			}catch(Exception e){
				LogHome.getLog().error(e);
			}
			List<Map> fcabList = converToList(fibercabdevices);
			for(Map map : fcabList)
			{
				map.put("DEVICE_TABLE_TYPE", "FIBER_CAB");
			}
			return fcabList;
		}
		private List getInterwireChildren(String childCuid)
		{
			List list = new ArrayList();
			if(childCuid.startsWith("FIBER_CAB"))
			{
				list.addAll(getFibercabBySql(" CUID='"+childCuid+"' "));
			}
			else if(childCuid.startsWith("FIBER_DP"))
			{
				list.addAll(getFiberdpBySql(" CUID='"+childCuid+"' "));
			}
			else if(childCuid.startsWith("FIBER_JOINT_BOX"))
			{
				list.addAll(getJointboxBySql(" CUID='"+childCuid+"' "));
			}
			else if(childCuid.startsWith("TRANS_ELEMENT"))
			{
				list.addAll(getPosBySql(" CUID='"+childCuid+"' "));
				list.addAll(getOnuBySql(" CUID='"+childCuid+"' "));
			}
			else if(childCuid.startsWith("ODF"))
			{
				list.addAll(getOdfBySql(" CUID='"+childCuid+"' "));
			}
			return list;
		}
		/**
		 * 接入点下的各设备获得子节点信息
		 */
		public List getChildrenByParentCuids(List<String> parentCuids) {
			List list = new ArrayList();
			for(String parentCuid : parentCuids)
			{
				List<Map> tempList = getChildrenByParentCuid(parentCuid);
				for(Map map : tempList)
				{
					map.put("PARENT_CUID", parentCuid);
				}
				list.addAll(tempList);
			}
			return list;
		}
		private List getFiberJointboxChildren(String fiberJointboxCuid)
		{
			DataObjectList fiberjointmodules =  null;
			try{
				fiberjointmodules = BoHomeFactory.getInstance().getBO(IFiberJointPointBO.class).getFiberJointPointBySql(new BoActionContext(), "RELATED_DEVICE_CUID='"+fiberJointboxCuid+"'");
			}catch(Exception e){
				LogHome.getLog().error(e);
			}
			return converToList(fiberjointmodules);
		}
		private List getFiberCabChildren(String fiberCabCuid)
		{
			DataObjectList fibercabmodules =  null;
			List list = new ArrayList();
			try{
				fibercabmodules = BoHomeFactory.getInstance().getBO(IFibercabmoduleBO.class).getFibercabmoduleBySql(new BoActionContext(), "RELATED_DEVICE_CUID='"+fiberCabCuid+"'");
				//modify by chenhao
				list.addAll(converToList(fibercabmodules));
				FiberCab fiberCab = BoHomeFactory.getInstance().getBO(IFiberCabBO.class).getFiberCabByCuid(new BoActionContext(), fiberCabCuid);
				if(fiberCab.getAttrLong(FiberCab.AttrName.deviceType) == 2)
				{
					list.addAll(getOnuByOnubox(fiberCabCuid));
				}
				//modify end
			}catch(Exception e){
				LogHome.getLog().error(e);
			}
			return list;
		}
		/**
		 * 追加内置ONU端子
		 * @param boxCuid
		 * @return
		 */
		private List getOnuByOnubox(String boxCuid)
		{
			List list = new ArrayList();
			try{
				//追加内置POS端子
				Class[] cla = new Class[]{String.class,String.class};
				String posSql = "select cuid,label_cn from an_onu where " + AnOnu.AttrName.relatedCabCuid + "='" + boxCuid + "'";
				DataObjectList posInfos = BoHomeFactory.getInstance().getBO(IDuctManagerBO.class).getDatasBySql(posSql, cla);
				if(null != posInfos && posInfos.size() > 0){
					String onuCuids = "'";
					for(GenericDO gdo : posInfos){
						String cuid = gdo.getAttrString("1");
						String labelCn = gdo.getAttrString("2");
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("CUID", cuid);
						map.put("LABEL_CN", labelCn);
						map.put("BM_CLASS_ID", AnOnu.CLASS_NAME);
						onuCuids += cuid + "',";
						list.add(map);
					}
					DataObjectList onuPorts = BoHomeFactory.getInstance().getBO(IPTPBO.class)
							.getAllPtpByneCuids(new BoActionContext(), onuCuids.substring(0, onuCuids.length() - 1));
					for(GenericDO onuPort : onuPorts){
						Ptp ptp = (Ptp)onuPort;
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("CUID", ptp.getCuid());
						map.put("LABEL_CN", ptp.getAttrValue("LABEL_CN"));
						map.put(Ptp.AttrName.relatedNeCuid, ptp.getRelatedNeCuid());
						String icon = "/resources/topo/alarm/c.png";
						map.put("ICON", icon);
						map.put("BM_CLASS_ID", ptp.getBmClassId());
						list.add(map);
					}
				}
			}catch (Exception e)
			{
				LogHome.getLog().error(e);
			}
			return list;
		}
		private List getFibercabmoduleChildren(String fiberCabMudlueCuid)
		{
			DataObjectList fibercabports = null;
			try{
				fibercabports = BoHomeFactory.getInstance().getBO(IFcabportBO.class).getFcabportBySql(new BoActionContext(), "RELATED_MODULE_CUID='"+fiberCabMudlueCuid+"'");
			}catch(Exception e){
				LogHome.getLog().error(e);
			}
			return converToList(fibercabports);
		}
		private List getFiberDpChildren(String fiberDpCuid)
		{
			
			DataObjectList fiberdpports = null;
			try{
				fiberdpports = BoHomeFactory.getInstance().getBO(IFiberDpPortBO.class).getFiberDpPortBySql(new BoActionContext(), "RELATED_DEVICE_CUID='"+fiberDpCuid+"'");
			}catch(Exception e){
				LogHome.getLog().error(e);
			}
			return converToList(fiberdpports);
		}
		private List getFportChildren(String portCuid)
		{
			IDistrictBO districtBO = BoHomeFactory.getInstance().getBO(IDistrictBO.class);
			GenericDO parent = districtBO.getObjByCuid(new BoActionContext(), portCuid);
			if(parent == null)
			{
				parent = new GenericDO();
				parent.setCuid(portCuid);
			}
			DataObjectList jumperFibers = null;
			if (parent.getAttrValue(FiberJointPoint.AttrName.isConnected)!=null &&
			          (Boolean) parent.getAttrValue(FiberJointPoint.AttrName.isConnected)) {
			      try {
			    	  jumperFibers =  (DataObjectList) BoCmdFactory.getInstance().execBoCmd(JumpFiberBOHelper.ActionName.getJumpFiberByPointCuid,
			                  new BoActionContext(),parent.getCuid());
			      } catch (Exception e) {
			          LogHome.getLog().info(e);
			      }
			}
			return converToList(jumperFibers);
		}
		private List converToList(DataObjectList list)
		{
			List result = new ArrayList();
			if(list != null && list.size()>0)
			{
				for(int i=0;i<list.size();i++)
				{
					GenericDO child = list.get(i);
					result.add(converToMap(child));
				}
			}
			return result;
		}
		/**
		 *将GenericDO转换为Map
		 */
		private Map converToMap(GenericDO child){
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("CUID", child.getCuid());
			map.put("LABEL_CN", child.getAttrValue("LABEL_CN"));
			String icon = "";
			if(child instanceof Fibercabmodule || child instanceof Odfmodule)
			{
				icon = "/resources/topo/dm/Ddfmodule.gif";
			}
			if(child instanceof Fcabport){
				Fcabport port = (Fcabport)child;
				icon = "/resources/topo/alarm/c.png";
		        if (port.getIsConnectedToFiber()) {
		        	icon = "/resources/topo/alarm/w.png";
		        }
		        if (port.getServiceState() == DuctEnum.DM_SERVICE_STATE._impropriate) {
		        	icon = "/resources/topo/alarm/m.png";
		        } else if (port.getServiceState() == DuctEnum.DM_SERVICE_STATE._bad) {
		        	icon = "/resources/topo/alarm/StopSound.png";
		        } else if (port.getServiceState() == DuctEnum.DM_SERVICE_STATE._destine) {
		        	icon = "/resources/topo/alarm/u.png";
		        }
		        long portRow=port.getNumInMrow();
		        long portCol=port.getNumInMcol();
		        map.put("LABEL_CN",child.getAttrString("LABEL_CN")+"("+portRow+"-"+portCol+")");
			}
			if(child instanceof FiberDpPort){
				FiberDpPort port = (FiberDpPort)child;
				icon = "/resources/topo/alarm/c.png";
		        if (port.getIsConnectedToFiber()) {
		        	icon = "/resources/topo/alarm/w.png";
		        }
		        if (port.getServiceState() == DuctEnum.DM_SERVICE_STATE._impropriate) {
		        	icon = "/resources/topo/alarm/m.png";
		        } else if (port.getServiceState() == DuctEnum.DM_SERVICE_STATE._bad) {
		        	icon = "/resources/topo/alarm/StopSound.png";
		        } else if (port.getServiceState() == DuctEnum.DM_SERVICE_STATE._destine) {
		        	icon = "/resources/topo/alarm/u.png";
		        }
		        long portRow=port.getNumInMrow();
		        long portCol=port.getNumInMcol();
		        map.put("LABEL_CN",child.getAttrString("LABEL_CN")+"("+portRow+"-"+portCol+")");
			}
			if(child instanceof FiberJointPoint)
			{
				FiberJointPoint port = (FiberJointPoint)child;
				icon = "/resources/topo/alarm/c.png";
		        if (port.getIsConnectedToFiber()) {
		        	icon = "/resources/topo/alarm/w.png";
		        }
		        if (port.getServiceState() == DuctEnum.DM_SERVICE_STATE._impropriate) {
		        	icon = "/resources/topo/alarm/m.png";
		        } else if (port.getServiceState() == DuctEnum.DM_SERVICE_STATE._bad) {
		        	icon = "/resources/topo/alarm/StopSound.png";
		        } else if (port.getServiceState() == DuctEnum.DM_SERVICE_STATE._destine) {
		        	icon = "/resources/topo/alarm/u.png";
		        }
		        long portRow=port.getNumInMrow();
		        long portCol=port.getNumInMcol();
		        map.put("LABEL_CN",child.getAttrString("LABEL_CN")+"("+portRow+"-"+portCol+")");
			}
			if(child instanceof Odfport)
			{
				Odfport port = (Odfport)child;
				icon = "/resources/topo/alarm/c.png";
		        if (port.getIsConnectedToFiber()) {
		        	icon = "/resources/topo/alarm/w.png";
		        }
		        if (port.getServiceState() == DuctEnum.DM_SERVICE_STATE._impropriate) {
		        	icon = "/resources/topo/alarm/m.png";
		        } else if (port.getServiceState() == DuctEnum.DM_SERVICE_STATE._bad) {
		        	icon = "/resources/topo/alarm/StopSound.png";
		        } else if (port.getServiceState() == DuctEnum.DM_SERVICE_STATE._destine) {
		        	icon = "/resources/topo/alarm/u.png";
		        }
		        long portRow=port.getNumInMrow();
		        long portCol=port.getNumInMcol();
		        map.put("LABEL_CN",child.getAttrString("LABEL_CN")+"("+portRow+"-"+portCol+")");
			}
			if(child instanceof Ptp || (child instanceof GenericDO && ((GenericDO) child).getAttrString("CUID").startsWith("PTP")))
			{
				icon = "/resources/topo/alarm/c.png";
			}
			if(child instanceof JumpFiber)
			{
				JumpFiber jf = (JumpFiber)child;
	            if ((Boolean) child.getAttrValue(JumpFiber.AttrName.isFixed)) {
	                icon =  "/resources/topo/dm/fixed.png";
	            } else {
	                icon =  "/resources/topo/dm/isfixed.gif";
	            }
	            map.put("ORIG_POINT_CUID", jf.getOrigPointCuid());
	            map.put("DEST_POINT_CUID", jf.getDestPointCuid());
			}
			map.put("ICON", icon);
			map.put("BM_CLASS_ID",child.getBmClassId());
			
			return map;
		}
		
		/**
		 * 判断纤芯是否被光路使用
		 * @param jumpfiberCuid
		 * @return
		 */
		public boolean isFiberInOpticalwayRoute(String fiberCuid){ 
			String [] fiberCuids = fiberCuid.split(",");
			String cuids = "";
			for(String cuid : fiberCuids)
			{
				cuids +=",'"+cuid+"'";
			}
	    	String actionName = "IOpticalWayBO.getOWBySql";
        	String sql = " CUID IN (SELECT OPR.RELATED_SERVICE_CUID FROM OPTICAL_ROUTE OPR ,OPTICAL_ROUTE_TO_PATH OPRTP "
        			+ "WHERE OPR.CUID = OPRTP.OPTICAL_ROUTE_CUID AND PATH_CUID IN "
        			+ "(SELECT OPTICAL_CUID FROM OPTICAL_TO_FIBER WHERE FIBER_CUID IN ("+cuids.substring(1)+")))";

	    	try {
				DboCollection dbos = (DboCollection) BoCmdFactory.getInstance().execBoCmd(actionName, new BoQueryContext(), sql);
				if(dbos != null && dbos.size() > 0){
					return true;
				}
			} catch (Exception e) {
				LogHome.getLog().error("根据纤芯查询光路出错",e);
			}
	    	return false;
		}
		//通过光缆段查询光缆系统 by liuyumiao
		 private HashMap getWireSystemMap(BoActionContext actionContext, DataObjectList wireSegs) throws Exception {
			    IWireSystemBO bo = BoHomeFactory.getInstance().getBO(IWireSystemBO.class);
		        HashMap<WireSystem, DataObjectList> wireSystemMap = new HashMap<WireSystem, DataObjectList>();
		        if (wireSegs != null && wireSegs.size() > 0) {
		            for (int i = 0; i < wireSegs.size(); i++) {
		            	GenericDO wireSeg = null;
		            	String relatedSystemCuid = "";
		            	if(wireSegs.get(i) instanceof WireSeg)
		            	{
		            		wireSeg = (WireSeg) wireSegs.get(i);
		            		relatedSystemCuid = ((WireSeg) wireSeg).getRelatedSystemCuid();
		            	}
		            	else if(wireSegs.get(i) instanceof InterWire)
		            	{
		            		wireSeg = (InterWire) wireSegs.get(i);
		            		relatedSystemCuid = ((InterWire) wireSeg).getRelatedSystemCuid();
		            	}
		                WireSystem wireSystem = bo.getWireSystemByCuid(actionContext, relatedSystemCuid);
		                if (relatedSystemCuid != null && wireSystemMap.get(wireSystem) != null) {
		                    DataObjectList relatedWireSegs = (DataObjectList) wireSystemMap.get(wireSystem);
		                    if (relatedWireSegs.size() == 0) {
		                        relatedWireSegs.add(wireSeg);
		                    } else {
		                        boolean canAdd = true;
		                        for (int j = 0; j < relatedWireSegs.size(); j++) {
		                            WireSeg seg = (WireSeg) relatedWireSegs.get(j);
		                            if (seg.getCuid().equals(wireSeg.getCuid())) {
		                                canAdd = false;
		                                break;
		                            }
		                        }
		                        if (canAdd) {
		                            relatedWireSegs.add(wireSeg);
		                        }
		                    }
		                } else {
		                    DataObjectList relatedWireSegs = new DataObjectList();
		                    relatedWireSegs.add(wireSeg);
		                    wireSystemMap.put(wireSystem, relatedWireSegs);
		                }
		            }
		        }
				return wireSystemMap;
		    }
		 //通过起始点查询光缆段 by liuyumiao
		 private DataObjectList getWireSegsBySql(BoActionContext actionContext, String pointCuid) throws Exception {
		    	DataObjectList wireSegdev = null; 
		        String sql = "("+WireSeg.AttrName.origPointCuid + "='" + pointCuid + "' or " + WireSeg.AttrName.destPointCuid + " ='" + pointCuid + "')"; 
		        IDuctManagerBO getDuctManBO = BoHomeFactory.getInstance().getBO(IDuctManagerBO.class);
		        wireSegdev = getDuctManBO.getObjectsBySql(sql, new WireSeg());
		        return wireSegdev;    
		    }
		/*
		 * 查询设计和存量中的光交接箱数据
		 */
		public  List getFCabChildrenByParentCuid(String parentCuid) {
			if (parentCuid.startsWith(FiberCab.CLASS_NAME)) {
	            return getFCabChildren(parentCuid);
	        }
			else if (parentCuid.startsWith(Fibercabmodule.CLASS_NAME)) {
	            return getFcabmoduleChildren(parentCuid);
	        }
			else if(parentCuid.startsWith(Fcabport.CLASS_NAME))
			{
				return getFCabportChildren(parentCuid);
			}
			return null;
		}
		private List getFCabChildren(String fiberCabCuid)
		{
			DataObjectList fibercabmodules =  null;
//			DboCollection fibercabmodulesCL = new DboCollection();
			try{
				fibercabmodules = BoHomeFactory.getInstance().getBO(IFibercabmoduleBO.class).getFibercabmoduleBySql(new BoActionContext(), "RELATED_DEVICE_CUID='"+fiberCabCuid+"'");
			    //取存量表的模块数据
//				fibercabmodulesCL=(DboCollection) BoCmdFactory.getInstance().execBoCmd("IOrientedDesignBO.getObjectsBySql",new BoQueryContext(),"RELATED_DEVICE_CUID='"+fiberCabCuid+"'",Fibercabmodule.CLASS_NAME);
//				if(fibercabmodulesCL!=null&&fibercabmodulesCL.size()>0){
//					for(int i=0;i<fibercabmodulesCL.size();i++){
//						fibercabmodules.add(fibercabmodulesCL.getAttrField(Fibercabmodule.CLASS_NAME,i));
//					}
//				}
			}catch(Exception e){
				LogHome.getLog().error(e);
			}
			return converToList(fibercabmodules);
		}
		private List getFcabmoduleChildren(String fiberCabMudlueCuid)
		{
			DataObjectList fibercabports = null;
			try{
				fibercabports = BoHomeFactory.getInstance().getBO(IFcabportBO.class).getFcabportBySql(new BoActionContext(), "RELATED_MODULE_CUID='"+fiberCabMudlueCuid+"'");
			}catch(Exception e){
				LogHome.getLog().error(e);
			}
			return converToList(fibercabports);
		}
		private List getFCabportChildren(String portCuid)
		{
			IDistrictBO districtBO = BoHomeFactory.getInstance().getBO(IDistrictBO.class);
			GenericDO parent = districtBO.getObjByCuid(new BoActionContext(), portCuid);
			if(parent == null)
			{
				parent = new GenericDO();
				parent.setCuid(portCuid);
			}
			DataObjectList jumperFibers = null;
			if (parent.getAttrValue(FiberJointPoint.AttrName.isConnected)!=null &&
			          (Boolean) parent.getAttrValue(FiberJointPoint.AttrName.isConnected)) {
			      try {
			    	  jumperFibers =  (DataObjectList) BoCmdFactory.getInstance().execBoCmd(JumpFiberBOHelper.ActionName.getJumpFiberByPointCuid,
			                  new BoActionContext(),parent.getCuid());
			      } catch (Exception e) {
			          LogHome.getLog().info(e);
			      }
			}
			return converToList(jumperFibers);
		}
		public List getFCabPortByMoudle(String segCuid) throws Exception{
			if (StringUtils.isBlank(segCuid)) {
				return null;
			}
			
			IFcabportBO ibo = BoHomeFactory.getInstance().getBO(IFcabportBO.class);
			DataObjectList fiberList = ibo.getFcabportsByModuleCuid(new BoActionContext(), segCuid);
//			DboCollection fcabPortsCL=(DboCollection) BoCmdFactory.getInstance().execBoCmd("IOrientedDesignBO.getObjectsBySql",new BoQueryContext(),"related_module_cuid='"+segCuid+"'",Fcabport.CLASS_NAME);
//			if (fcabPortsCL!=null&&fcabPortsCL.size()>0) {
//				for(int i=0;i<fcabPortsCL.size();i++){
//					GenericDO fcabPort=fcabPortsCL.getAttrField(Fcabport.CLASS_NAME,i);
//					fiberList.add(fcabPort);
//				}
//			}else if(fiberList == null || fiberList.size() == 0){
//				return null;
//			}
			if(fiberList == null || fiberList.size() == 0){
				return null;
			}
			List result = new ArrayList();
			for(int i=0;i<fiberList.size();i++)
			{
				GenericDO child = fiberList.get(i);
				Fcabport ports = (Fcabport) child;
				String icon = "/resources/topo/alarm/c.png";
				if(ports.getIsConnectedToFiber()){
			        icon = "/resources/topo/alarm/w.png";
				}
				Map attrMap = child.getAllAttr();
				attrMap.put("ICON", icon);
				result.add(attrMap);
			}
			return result;
		}
		private void addDdmPortToFiber(DataObjectList ports,DataObjectList fibers) throws UserException, Exception{
			DataObjectList dataList = new DataObjectList();
			for(int i=0;i<ports.size();i++){
				Fcabport port=(Fcabport)ports.get(i);
				Fiber fiber=(Fiber)fibers.get(i);
				fiber.getRelatedSegCuid();
				fiber.getRelatedSystemCuid();
				fiber.getDestPointCuid()	;
				fiber.getWireNo();
				DdmPortToFiber ddmPortToFiber=new DdmPortToFiber();
				ddmPortToFiber.setCuid();
				ddmPortToFiber.setAttrValue("DEVICE_TYPE", 6);
				ddmPortToFiber.setAttrValue("RELATED_DISTRICT_LABEL_CN", port.getRelatedDistrictCuid());
				ddmPortToFiber.setAttrValue("DEVICE_CUID", port.getRelatedDeviceCuid());
				ddmPortToFiber.setAttrValue("LABEL_CN", port.getLabelCn());
				ddmPortToFiber.setAttrValue("RELATED_POINT_CUID",port.getRelatedDeviceCuid() );
				ddmPortToFiber.setAttrValue("MODULE_CUID",port.getRelatedModuleCuid() );
				ddmPortToFiber.setAttrValue("WIRE_SYSTEM_CUID",fiber.getRelatedSystemCuid());
				ddmPortToFiber.setAttrValue("WIRE_SEG_CUID",fiber.getRelatedSegCuid());
				ddmPortToFiber.setAttrValue("WIRE_SEG_ORIG_POINT_CUID",fiber.getOrigPointCuid());
				ddmPortToFiber.setAttrValue("WIRE_SEG_DEST_POINT_CUID",	fiber.getDestPointCuid());
				ddmPortToFiber.setAttrValue("FIBER_NO",fiber.getWireNo());
				ddmPortToFiber.setAttrValue("CREATE_TIME",new Date());
				dataList.add(ddmPortToFiber);
			}
			BoHomeFactory.getInstance().getBO(IDuctManagerBO.class).addDDMDuctLine(new BoActionContext(), dataList);
        
		}

		public  List getOdfAndChildrenByAccessPointCuid(String parentCuid) {
			
			String className = parentCuid.split("-")[0];
			if (className.equals(Accesspoint.CLASS_NAME)) {
				String sql = " RELATED_ACCESS_POINT='"+parentCuid+"' ";
				LogHome.getLog().info("查询ODF设备的SQL"+sql);
	            return getOdfdevBySql(sql);
	        }else if(className.equals(Odf.CLASS_NAME)){
	        	return getOdfmoduleChild(parentCuid);
	        }else if(className.equals(Odfmodule.CLASS_NAME)){
	        	return getOdfportChild(parentCuid);
	        }

			return null;
		}
		private List<Map> getOdfdevBySql(String sql) {
			 IDuctManagerBO getDuctManBO = BoHomeFactory.getInstance().getBO(IDuctManagerBO.class);
			DataObjectList odfdevices =  null;
			try{
				odfdevices = getDuctManBO.getObjectsBySql(sql, new Odf());
				if(odfdevices!=null && odfdevices.size()>0){
					LogHome.getLog().info("设计库查到的ODF设备"+odfdevices);
				}else{
					 odfdevices = (DataObjectList) BoCmdFactory.getInstance().execBoCmd("IOrientedDesignBO.getObjectsBySql",new BoActionContext(),sql,Odf.CLASS_NAME);
					LogHome.getLog().info("现网库查到的ODF设备"+odfdevices);
				}
			odfdevices.sort(Odfport.AttrName.labelCn, true);
				
			}catch(Exception e){
				LogHome.getLog().error(e);
			}
			
			return converToList(odfdevices);

		}
		private List getOdfmoduleChild(String parentCuid)
		{
			String sql = " RELATED_DEVICE_CUID='"+parentCuid+"' ";
			 IDuctManagerBO getDuctManBO = BoHomeFactory.getInstance().getBO(IDuctManagerBO.class);
			DataObjectList odfmodules =  null;
			try{
				odfmodules = getDuctManBO.getObjectsBySql(sql, new Odfmodule());
				if(odfmodules!=null && odfmodules.size()>0){
					 LogHome.getLog().info("设计库查到的ODF模块"+odfmodules);
				}else{
					odfmodules = (DataObjectList) BoCmdFactory.getInstance().execBoCmd("IOrientedDesignBO.getObjectsBySql",new BoActionContext(),sql,Odfmodule.CLASS_NAME);
					LogHome.getLog().info("现网库查到的ODF模块"+odfmodules);
				}
				odfmodules.sort(Odfport.AttrName.labelCn, true);
			}catch(Exception e){
				LogHome.getLog().error(e);
			}
			 
			 return converToList(odfmodules);
		}
		private List getOdfportChild(String parentCuid)
		{
			String sql = " RELATED_MODULE_CUID='"+parentCuid+"' ";
			IDuctManagerBO getDuctManBO = BoHomeFactory.getInstance().getBO(IDuctManagerBO.class);
			DataObjectList odfports =  null;
			try{
				odfports = getDuctManBO.getObjectsBySql(sql, new Odfport());
				//zhaotingshuai modify start  
				//Colspan sequenced first,rows later
				odfports.sort(Odfport.AttrName.numInMcol, true);
				odfports.sort(Odfport.AttrName.numInMrow, true);
				//zhaotingshuai modify stop
				if(odfports!=null && odfports.size()>0){
					 LogHome.getLog().info("设计库查到的ODF端子"+odfports);
				}else{
					odfports = (DataObjectList) BoCmdFactory.getInstance().execBoCmd("IOrientedDesignBO.getObjectsBySql",new BoActionContext(),sql,Odfport.CLASS_NAME);
					 LogHome.getLog().info("现网库查到的ODF端子"+odfports);
				}
//				odfports.sort(Odfport.AttrName.numInMrow, true);
//				odfports.sort(Odfport.AttrName.numInMcol, true);
				
			}catch(Exception e){
				LogHome.getLog().error(e);
			}
			
			return converToList(odfports);
		}
		public List getWireInfoByAccesspoint(String accesspointcuid) throws Exception{
			if (accesspointcuid == null || accesspointcuid.trim().length() == 0){
				return null;	
			}

			IWireSystemBO ibo = BoHomeFactory.getInstance().getBO(IWireSystemBO.class);
			HashMap<WireSystem, DataObjectList> dataList = ibo.getWireSyetemByDevCuid(new BoActionContext(), accesspointcuid);

			if (dataList == null || dataList.size() == 0) {
				return null;
			}
			String icon = ICON_MAP.get("光缆");
			// 光缆集合
			List<Map<String, Object>> wireSystemList = new ArrayList<Map<String, Object>>();
			// 光缆段集合
			List<Map<String, Object>> wireSegList = new ArrayList<Map<String, Object>>();

			for(Map.Entry<WireSystem, DataObjectList> entry : dataList.entrySet()) {
				WireSystem ws = entry.getKey();
				boolean isAdd = false;
				for (int i = 0; i < wireSystemList.size(); i++ ){
					Map<String,Object> sys = wireSystemList.get(i);
					if (sys.get("CUID").equals(ws.getCuid())){
						isAdd = true;
						break;
					}
				}
				
				if (isAdd == false) {
					Map attr = ws.getAllAttr();
					attr.put("ICON", icon);
					wireSystemList.add(attr);
				}

				List segList = entry.getValue();
				if (segList == null || segList.size() ==0 ) {
					continue;
				}
				for(Object item : segList) {
					if(item instanceof WireSeg)
					{
						WireSeg seg = (WireSeg) item;
						Map attr = seg.getAllAttr();
						attr.put("ICON", icon);
						wireSegList.add(attr);
					}
				}
			}

			List result = new ArrayList();
			result.add(wireSystemList);
			result.add(wireSegList);

			return result;
		}
		public List<String> addodfFibersConnect(HttpServletRequest request,String fiberCuids, String portCuids) throws Exception{
			if (StringUtils.isBlank(fiberCuids) || StringUtils.isBlank(portCuids)) {
				return new ArrayList();
			}
			IFiberBO ibo = BoHomeFactory.getInstance().getBO(IFiberBO.class);
			ServiceActionContext ac = new ServiceActionContext(request);
			BoActionContext context = new BoActionContext();
			String userCuid = ac.getUserCuid();
			String userid = ac.getUserId();
			context.setUserId(userCuid);
			context.setUserName(userid);
			List<String> result = new ArrayList<String>();
			try {
				List portlist=new ArrayList();
				String[] portcuids=portCuids.split(",");
				for(int i=0;i<portcuids.length;i++){
					portlist.add(portcuids[i]);
				}
				DataObjectList fibers = _getFiberByCuids(context, fiberCuids);
				
				DataObjectList ports = _getPortByCuid(context, portCuids);
				DataObjectList portsbyf = new DataObjectList();
				for(int i=0;i<fibers.size();i++){
					portsbyf.add((GenericDO) ports.getObjectByCuid((String) portlist.get(i)).get(0));
				}
				DataObjectList dataList = ibo.addFibersConnect(context, fibers,
						portsbyf);

				if (dataList == null || dataList.size() == 0) {
					return new ArrayList();
				}
				result.add(0, "SUCCESS");
				for (GenericDO dbo : dataList) {
					Fiber fiber = (Fiber) dbo;
					result.add(fiber.getCuid());
				}
			} catch (Exception ex) {
				result.add(0, ex.getMessage());
			}
			
			return result;
		}
		//删除ODF设备
		private static final String RESULT="OK";
		public String isOdfConnFiber(String odfcuid) throws Exception{ 
			GenericDO odfdevice = (GenericDO) BoCmdFactory.getInstance().execBoCmd("IOrientedDesignBO.getObjByCuid",new BoActionContext(),odfcuid);
			if(odfdevice!=null){
				throw new UserException("现网数据不可以删除！");
			}
	    	try {
	    		IPhyOdfBO bo = BoHomeFactory.getInstance().getBO(IPhyOdfBO.class);
	    		bo.deleteRelateObjByOdf(new BoActionContext(), odfcuid,true);
			} catch (Exception e) {
				throw new UserException(e.getMessage());
			}
	    	return RESULT;
		}
	}
