package com.boco.irms.app.dm.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.boco.core.utils.exception.UserException;
import com.boco.topo.Icon;
import com.boco.transnms.common.bussiness.consts.DuctEnum.WireCutStateEnum;
import com.boco.transnms.common.bussiness.consts.DuctEnum.WireCutTypeEnum;
import com.boco.transnms.common.dto.Accesspoint;
import com.boco.transnms.common.dto.CutoverScheme;
import com.boco.transnms.common.dto.CutoverTask;
import com.boco.transnms.common.dto.Fiber;
import com.boco.transnms.common.dto.Odf;
import com.boco.transnms.common.dto.Site;
import com.boco.transnms.common.dto.WireSeg;
import com.boco.transnms.common.dto.base.BoActionContext;
import com.boco.transnms.common.dto.base.BoQueryContext;
import com.boco.transnms.common.dto.base.DataObjectList;
import com.boco.transnms.common.dto.base.DboCollection;
import com.boco.transnms.common.dto.base.GenericDO;
import com.boco.transnms.server.bo.base.BoHomeFactory;
import com.boco.transnms.server.bo.ibo.dm.ICutoverSchemeBO;
import com.boco.transnms.server.bo.ibo.dm.ICutoverTaskBO;
import com.boco.transnms.server.bo.ibo.dm.IDuctManagerBO;
import com.boco.transnms.server.bo.ibo.dm.IFiberBOX;

public class CutOverTaskAction {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private DataObjectList selectFiberList = null;

	private ICutoverSchemeBO getCutoverSchemeBO() {
		return BoHomeFactory.getInstance().getBO(ICutoverSchemeBO.class);
	}
	
	private IFiberBOX getFiberBO() {
			return BoHomeFactory.getInstance().getBO(IFiberBOX.class);
	}

	private ICutoverTaskBO getCutoverTaskBO() {
		return BoHomeFactory.getInstance().getBO(ICutoverTaskBO.class);
	}

	private IDuctManagerBO getDuctManagerBO() {
		return BoHomeFactory.getInstance().getBO(IDuctManagerBO.class);
	}

	public List<Map<String,String>> doGetWireFinishCut(String cuid) {
		List<Map<String,String>> results = null;
		GenericDO dbo = getDuctManagerBO().getObjByCuid(new BoActionContext(),cuid);
		try {
			// 是光缆成端割接
			if (dbo.getAttrValue(CutoverTask.AttrName.cutoverType).equals(WireCutTypeEnum._finishcut)) {
				long state = (Long) dbo.getAttrValue(CutoverTask.AttrName.cutoverState);
				// 只有割接任务是新建和设计的时候才能在进行设计
				if (state == WireCutStateEnum._cutsuccess) {
					throw new UserException("执行割接成功的割接任务不能进行方案设计!");
				}
				if (state == WireCutStateEnum._cutfail) {
					String wireSegCuid = getRelatedCuid(dbo.getAttrValue(CutoverTask.AttrName.relatedWireSegCuid));
					DboCollection dboCollection = getCutoverTaskBO().getCutoverTaskBySegCuid(new BoQueryContext(),wireSegCuid);
					if (dboCollection != null && dboCollection.size() > 0) {
						for (int i = 0; i < dboCollection.size(); i++) {
							GenericDO gDO = (GenericDO) dboCollection.getAttrField(CutoverTask.CLASS_NAME, i);
							long cutOverState = (Long) gDO.getAttrValue(CutoverTask.AttrName.cutoverState);
							if (cutOverState == WireCutStateEnum._new || cutOverState == WireCutStateEnum._schemedesign) {
								throw new UserException("该光缆段下存在其他未执行割接的任务，不能对该任务再进行方案设计!");
							}
						}
					}
					
//					 * int ri = JOptionPane.showConfirmDialog(null,
//					 * "该任务是割接失败的任务，进行方案设计将删除原来的全部设计!"
//					 * ,"确定",JOptionPane.YES_NO_OPTION,
//					 * JOptionPane.WARNING_MESSAGE, null); if (ri != 0) {
//					 * return; }
					 
					getCutoverSchemeBO().deleteCutoverSchemeBytaskCuid(new BoActionContext(), dbo.getCuid());
				}
                results = getCutOverTaskDatas(dbo); 
			}// 取对象的割接场景来判断是成端割接或者是中间光缆段割接
			else if (dbo.getAttrValue(CutoverTask.AttrName.cutoverType).equals(WireCutTypeEnum._middlesegcut)) {
				// 只有割接任务是新建和设计的时候才能在进行设计
				long state = (Long) dbo.getAttrValue(CutoverTask.AttrName.cutoverState);
				if (state == WireCutStateEnum._cutsuccess) {
					throw new UserException("执行割接成功的割接任务不能进行方案设计!");
				}
				
//				 * int flag=MessagePane.showYesNoMessage(
//				 * "中间光缆段割接在设计时会重新生成所有的割接信息，\n如果要修改现有的割接方案的路由信息，请选择"
//				 * +"\"修改具体路由\""+"按钮！\n确定要继续吗？");
//				 * if(flag==JOptionPane.YES_OPTION){ IView view =
//				 * ViewFactory.getInstance
//				 * ().createView("CutOverPointSelectView",
//				 * ShellFactoryName.MainModalDialogShellFactory);
//				 * view.openView(dbo); }
				 
			}
		} catch (Exception ex) {
			if(ex instanceof UserException)
			{
				throw  new UserException(ex.getMessage());
			}
			logger.error("方案设计打开界面失败!", ex);
			ex.printStackTrace();
		}

		return results;
	}
	
	//获取数据
	@SuppressWarnings("unused")
	private List<Map<String,String>> getCutOverTaskDatas(GenericDO dbo){
		String wireSegCuid = getRelatedCuid(dbo.getAttrValue(CutoverTask.AttrName.relatedWireSegCuid));
		String deviceCuid = getRelatedCuid(dbo.getAttrValue(CutoverTask.AttrName.relatedDeviceCuid));
		GenericDO wireSegDto=null;
		GenericDO deviceDto=null;
		if(wireSegCuid!=null && !("").equals(wireSegCuid)){
			wireSegDto = getDuctManagerBO().getObjByCuid(new BoActionContext(), wireSegCuid);
		}
		if(deviceCuid!=null && !("").equals(deviceCuid)){
			deviceDto = getDuctManagerBO().getObjByCuid(new BoActionContext(), deviceCuid);
		}
		if(wireSegDto!=null && deviceDto!=null ){
			DataObjectList cutoverSchemes = getCutoverSchemeBO().getCutOverSchemeByTaskCuid(new BoQueryContext(),dbo.getCuid());
			List<String> fiberCuid = new ArrayList<String>();
			if(cutoverSchemes!=null && cutoverSchemes.size()>0){
				for (GenericDO cutoverScheme : cutoverSchemes) {
					String relatedFiberCuid = (String) cutoverScheme.getAttrValue(CutoverScheme.AttrName.relatedFiberCuid);
					if(!fiberCuid.contains(relatedFiberCuid)){
						fiberCuid.add(relatedFiberCuid);
					}
				}
				selectFiberList = (DataObjectList) getDuctManagerBO().getObjsByCuid(new BoActionContext(), fiberCuid.toArray(new String[fiberCuid.size()]));
			}
			return getTreeByDevAndWireSeg(wireSegDto,deviceDto);
		}else if(wireSegDto!=null && deviceDto==null ){
			return getFiberTreeByWireSeg(wireSegDto);
		}else if(wireSegDto==null && deviceDto!=null ){
			return getFiberTreeByDevice(deviceDto);
		}
		return  null;
	}
	
	
	
	private DataObjectList getFibersBySql(String sql,String pointAttr){
		DataObjectList upFibers = new DataObjectList();
		DataObjectList fibersList= getFiberBO().getFiberBySql(new BoActionContext(), sql);
		if(fibersList!=null && fibersList.size()>0){
			for (GenericDO fiberDto : fibersList) {
				if(!fiberDto.getAttrValue(pointAttr).equals("")){
					upFibers.add(fiberDto);
				}
			}
		}
		return upFibers;
	}
	/**
	 * 得到纤芯的关联光缆段cuid
	 * @param pointAttr
	 * @param fibersListBySql
	 * @param wireSegCuidList
	 * @return
	 */
	private void getWireSegsCuidByFibers(DataObjectList upFibers,List<String> wireSegCuidList){
		for (GenericDO fiber : upFibers) {
			String wireSegCuid = (String) fiber.getAttrValue(Fiber.AttrName.relatedSegCuid);
			if(wireSegCuid !=null && !wireSegCuid.equals("")){
				if(!wireSegCuidList.contains(wireSegCuid)){
					wireSegCuidList.add(wireSegCuid);
				}
			}
		}
	}
	/**
	 * 判断纤芯是否上架
	 * @param origAttr
	 * @param destAttr
	 * @param fibers
	 * @return
	 */
	private DataObjectList getUpFibers(String origAttr,String destAttr,DataObjectList fibers){
		DataObjectList upFibers = new DataObjectList();
		for (GenericDO fiber : fibers) {
			if(!("").equals(origAttr) && ("").equals(destAttr)){
				String pointCuid = (String) fiber.getAttrValue(origAttr);
				if(pointCuid!=null && !("").equals(pointCuid)){
					upFibers.add(fiber);
				}
			}else if(("").equals(origAttr) && !("").equals(destAttr)){
				String pointCuid = (String) fiber.getAttrValue(destAttr);
				if(pointCuid!=null && !("").equals(pointCuid)){
					upFibers.add(fiber);
				}
			}else if(!("").equals(origAttr) && !("").equals(destAttr)){
				String origPointCuid = (String) fiber.getAttrValue(origAttr);
				String destPointCuid = (String) fiber.getAttrValue(destAttr);
				if((origPointCuid!=null && !("").equals(origPointCuid))&&(destPointCuid!=null && !("").equals(destPointCuid))){
					upFibers.add(fiber);
				}
			}
		}
		return upFibers;
	}
	
	private List<Map<String,String>> getFibersByDevice(DataObjectList deviceDtoList,DataObjectList upFibers,DataObjectList wireSegList,GenericDO wireSegDto,String pointAttr){
		List<Map<String,String>> results = new ArrayList<Map<String,String>>();
		if(deviceDtoList!=null && deviceDtoList.size()>0){
    		DataObjectList fibers = new DataObjectList();
        	for (GenericDO deviceDto : deviceDtoList) {
    			for (GenericDO fiberDto : upFibers) {
    				if(deviceDto.getCuid().equals(fiberDto.getAttrValue(pointAttr))){
    					fibers.add(fiberDto);
    				}
    			}
    			if(fibers!=null && fibers.size()>0){
    				buildResultTree(fibers, results,wireSegDto.getCuid());
    				buildResultTree(wireSegList, results,deviceDto.getCuid());
    				buildResultTree(deviceDtoList, results,null);
    			}
    		}
		}
		return results;
    }
	
	 private void getDeviceCuidByFibers(String pointAttr,DataObjectList upFibers,List<String> deviceCuids){
    	if(upFibers!=null && upFibers.size()>0){
    		for (GenericDO fiberDto : upFibers) {
        		String origEqptCuid = (String) fiberDto.getAttrValue(pointAttr);
        		if(!deviceCuids.contains(origEqptCuid)){
        			deviceCuids.add(origEqptCuid);
        		}
    		}
    	}
	 }
	
	/**
     * 根据光缆段构建纤芯树结构
     * 选择光缆段的A或者Z端确定设备
     */
	private List<Map<String,String>> getFiberTreeByWireSeg(GenericDO wireSegDto){
		List<Map<String,String>> fiberTree = new ArrayList<Map<String,String>>();
		try {
			DataObjectList wireSegList = new DataObjectList();
			wireSegList.add(wireSegDto);
			String segCuid = wireSegDto.getCuid();
			//得到光缆段下的纤芯
			DataObjectList fiberList = getFiberBO().getFibersByWireSegCuid(new BoActionContext(), segCuid);
			if(fiberList!=null && fiberList.size()>0){
				String origCuid= getRelatedCuid(wireSegDto.getAttrValue(WireSeg.AttrName.origPointCuid));
				String destCuid = getRelatedCuid(wireSegDto.getAttrValue(WireSeg.AttrName.destPointCuid));
				GenericDO origDto =  getDuctManagerBO().getObjByCuid(new BoActionContext(), origCuid);
				GenericDO destDto = getDuctManagerBO().getObjByCuid(new BoActionContext(), destCuid);
				boolean flag = (origDto instanceof Site || origDto instanceof Accesspoint) &&(destDto instanceof Site || destDto instanceof Accesspoint);
				if((origDto instanceof Site || origDto instanceof Accesspoint) && !flag){//起点为站点或者接入点
					DataObjectList deviceDtoList = new DataObjectList();//纤芯关联的设备
					List<String> deviceCuids = new ArrayList<String>();//纤芯关联的设备cuid
					DataObjectList upFibers = getUpFibers(Fiber.AttrName.origPointCuid,"",fiberList);//返回上架纤芯
					getDeviceCuidByFibers(Fiber.AttrName.origEqpCuid,upFibers,deviceCuids);
					if(deviceCuids!=null && deviceCuids.size()>0){
						//得到设备对象
						deviceDtoList = (DataObjectList) getDuctManagerBO().getObjsByCuid(new BoActionContext(), deviceCuids.toArray(new String[deviceCuids.size()]));
						fiberTree = getFibersByDevice(deviceDtoList, upFibers,wireSegList, wireSegDto,Fiber.AttrName.origEqpCuid);
					}
				}else if((destDto instanceof Site || destDto instanceof Accesspoint) && !flag){//止点为站点或者接入点
					DataObjectList deviceDtoList = new DataObjectList();
					List<String> deviceCuids = new ArrayList<String>();//纤芯关联的设备
					DataObjectList upFibers = getUpFibers("", Fiber.AttrName.destPointCuid, fiberList);
					getDeviceCuidByFibers(Fiber.AttrName.destEqpCuid,upFibers,deviceCuids);
					if(deviceCuids!=null && deviceCuids.size()>0){
						//得到设备对象
						deviceDtoList = (DataObjectList) getDuctManagerBO().getObjsByCuid(new BoActionContext(), deviceCuids.toArray(new String[deviceCuids.size()]));
						fiberTree = getFibersByDevice(deviceDtoList, upFibers,wireSegList, wireSegDto,Fiber.AttrName.destEqpCuid);
					}
				}else {//起点和止点都为站点或者接入点
					List<String> origDeviceCuids = new ArrayList<String>();//纤芯关联的起设备
					List<String> destDeviceCuids = new ArrayList<String>();//纤芯关联的止设备
					DataObjectList origUpFibers=getUpFibers(Fiber.AttrName.origPointCuid, "", fiberList);
					getDeviceCuidByFibers(Fiber.AttrName.origEqpCuid, origUpFibers, origDeviceCuids);
					DataObjectList destUpFibers=getUpFibers("", Fiber.AttrName.destPointCuid, fiberList);
					getDeviceCuidByFibers(Fiber.AttrName.destEqpCuid, destUpFibers, destDeviceCuids);
					DataObjectList origDeviceDtoList = new DataObjectList();
					if(origDeviceCuids!=null && origDeviceCuids.size()>0){
						//得到设备对象
						origDeviceDtoList = (DataObjectList) getDuctManagerBO().getObjsByCuid(new BoActionContext(), origDeviceCuids.toArray(new String[origDeviceCuids.size()]));
					}
					fiberTree.addAll(getFibersByDevice(origDeviceDtoList, origUpFibers,wireSegList, wireSegDto,Fiber.AttrName.origEqpCuid));
					
					DataObjectList destDeviceDtoList = new DataObjectList();
					if(destDeviceCuids!=null && destDeviceCuids.size()>0){
						//得到设备对象
						destDeviceDtoList = (DataObjectList) getDuctManagerBO().getObjsByCuid(new BoActionContext(), new String[destDeviceCuids.size()]);
					}
					fiberTree.addAll(getFibersByDevice(destDeviceDtoList, destUpFibers,wireSegList, wireSegDto,Fiber.AttrName.destEqpCuid));
				}
			}
			if(fiberTree==null || fiberTree.size()==0){
				throw new UserException("纤芯两端没有上架信息，不能进行方案设计!");
			}
		} catch (Exception e) {
			logger.info("构建树结构失败",e);
			e.printStackTrace();
		}
		return fiberTree;
	}
	/**
	 * 
	 * @param wireSegDto
	 * @param deviceDto
	 * @return
	 */
	private List<Map<String,String>> getTreeByDevAndWireSeg(GenericDO wireSegDto,GenericDO deviceDto){
		List<Map<String,String>> fiberTree = new ArrayList<Map<String,String>>();
		DataObjectList wiresegList = new DataObjectList();
		wiresegList.add(wireSegDto);
		DataObjectList deviceDtoList = new DataObjectList();
		deviceDtoList.add(deviceDto);
		String accesspointCuid = (String) deviceDto.getAttrValue(Odf.AttrName.relatedAccessPoint);
		String siteCuid="";
		//是接入点下的设备
		if(accesspointCuid!=null && !("").equals(accesspointCuid)){
			siteCuid=accesspointCuid;
		}else{//站点下的设备
			siteCuid= (String) deviceDto.getAttrValue(Odf.AttrName.relatedSiteCuid);
		}
		String sql="";
		String pointAttr="";
		String EqptCuid = deviceDto.getCuid();
		//设备是光缆段的起设备
		if(siteCuid!=null && !("").equals(siteCuid)){
			if(siteCuid.equals((String)wireSegDto.getAttrValue(WireSeg.AttrName.origPointCuid))){
				sql = Fiber.AttrName.origEqpCuid + "=" +"'"+EqptCuid+"' and "+Fiber.AttrName.relatedSegCuid + "='"+wireSegDto.getCuid()+"'";
				pointAttr=Fiber.AttrName.origPointCuid;
			}else if(siteCuid.equals((String)wireSegDto.getAttrValue(WireSeg.AttrName.destPointCuid))){
				sql = Fiber.AttrName.destEqpCuid + "=" +"'"+EqptCuid+"' and "+Fiber.AttrName.relatedSegCuid + "='"+wireSegDto.getCuid()+"'";
				pointAttr = Fiber.AttrName.destPointCuid;
			}
		}
		if(!sql.equals("")){
			DataObjectList fibers = getFibersBySql(sql,pointAttr);
			if(fibers == null||fibers.size() == 0)
				throw new UserException("没有相关纤芯");
			buildResultTree(fibers, fiberTree,wireSegDto.getCuid());
		}
		buildResultTree(wiresegList, fiberTree,deviceDto.getCuid());
		buildResultTree(deviceDtoList, fiberTree,null);
		return fiberTree;
	}
	/**
     * 根据设备构建纤芯树结构
     * 从端子和终端盒，或者新建任务为入口进来的。
     * 设备类型有：ODF架、 综合机架 、终端盒
     * 根据所选择的纤芯确定光缆段
     */
	private List<Map<String,String>> getFiberTreeByDevice(GenericDO EqptDto){
		List<Map<String,String>> fiberTree = new ArrayList<Map<String,String>>();
		try {
			DataObjectList eqpDtoList = new DataObjectList();
			eqpDtoList.add(EqptDto);
    		String EqptCuid = EqptDto.getCuid();
        	String origSql = Fiber.AttrName.origEqpCuid + "=" +"'"+EqptCuid+"'";
        	String destSql = Fiber.AttrName.destEqpCuid + "=" +"'"+EqptCuid+"'";
        	DataObjectList wireSegList = new DataObjectList();//纤芯关联的光缆段
        	DataObjectList fibers = new DataObjectList();//上架的纤芯
        	List<String> wireSegCuidList = new ArrayList<String>();//纤芯关联的光缆段Cuid
        	DataObjectList fibersListByorigSql = getFiberBO().getFiberBySql(new BoActionContext(), origSql);
        	DataObjectList fibersListBydestSql = getFiberBO().getFiberBySql(new BoActionContext(), destSql);
        	String pointAttr ="";
        	if(fibersListByorigSql !=null && fibersListByorigSql.size()>0){
        		pointAttr = Fiber.AttrName.origPointCuid;
        		DataObjectList upFibers = getUpFibers(pointAttr,"",fibersListByorigSql);
        		getWireSegsCuidByFibers(upFibers,wireSegCuidList);
        		if(upFibers!=null && upFibers.size()>0){
        			fibers.addAll(upFibers);
        		}
        	}
        	if(fibersListBydestSql !=null && fibersListBydestSql.size()>0){
        		pointAttr = Fiber.AttrName.destPointCuid;
        		DataObjectList upFibers = getUpFibers("",pointAttr,fibersListBydestSql);
        		getWireSegsCuidByFibers(fibersListBydestSql,wireSegCuidList);
        		if(upFibers!=null && upFibers.size()>0){
        			fibers.addAll(upFibers);
        		}
        	}
        	if(wireSegCuidList!=null && wireSegCuidList.size()>0){
        		wireSegList = (DataObjectList) getDuctManagerBO().getObjsByCuid(new BoActionContext(),wireSegCuidList.toArray(new String[wireSegCuidList.size()]));
        	}
        	if(wireSegList!=null && wireSegList.size()>0){
        		buildFibersTreesByWireSeg(wireSegList, fibers, fiberTree);
        		buildResultTree(wireSegList, fiberTree,EqptDto.getCuid());
        	}
        	buildResultTree(eqpDtoList, fiberTree,null);
        	/*GenericNode node= new GenericNode(EqptDto.getCuid(), EqptDto);
        	node.setIcon(Icon.getIconByDto(EqptDto));
        	if(fiberBox.getElementByID(node.getID()) == null){
        		fiberBox.addElement(node);
        	}*/
		} catch (Exception e) {
			logger.info("构建树结构失败!",e);
			e.printStackTrace();
		}
		return fiberTree;
	}
	
	 /**
	  * 封装结果树集合
	  * @param dataObjectList
	  * @param resultTree
	  * @param parentCuid
	  */
	 private void buildResultTree(DataObjectList dataObjectList,List<Map<String,String>> resultTree,String parentCuid){
		 Iterator<? extends GenericDO> iterator = dataObjectList.iterator();
		 while (iterator.hasNext()) {
			 Map<String,String> map = new HashMap<String, String>();
			 GenericDO next = iterator.next();
			 map.put("CUID", getRelatedCuid(next.getCuid()));
			 map.put("LABEL_CN", next.getAttrString("LABEL_CN"));
			 map.put("PARENTCIUD", parentCuid);
			 map.put("ICON", Icon.getIconByType(next));
			 resultTree.add(map);
		 }
	 }
	 private void buildFibersTreesByWireSeg(DataObjectList wireSegList,DataObjectList fibers,List<Map<String,String>> fiberTree){
			
		 for( int i =0;i<wireSegList.size(); i++){
				GenericDO wiresegDto = (GenericDO) wireSegList.get(i);
				String wireSegCuid = wiresegDto.getCuid();
				for (GenericDO fiber : fibers) {
					String cuid = (String) fiber.getAttrValue(Fiber.AttrName.relatedSegCuid);
					if(wireSegCuid.equals(cuid)){
						Map<String,String> map = new HashMap<String, String>();
						map.put("CUID", fiber.getCuid());
						map.put("LABEL_CN", fiber.getAttrString("LABEL_CN"));
						map.put("ICON",Icon.getIconByType(fiber));
						map.put("PARENTCIUD", wireSegCuid);
						fiberTree.add(map);
					}
				}
			}
	 }

	public static String getRelatedCuid(Object obj) {
		if (obj instanceof GenericDO) {
			return ((GenericDO) obj).getCuid();
		} else if (obj instanceof String) {
			return (String) obj;
		} else if (obj != null) {
			return (String) obj.toString();
		} else {
			return null;
		}
	}
}
