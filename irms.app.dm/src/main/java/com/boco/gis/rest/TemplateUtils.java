package com.boco.gis.rest;

import java.awt.Rectangle;
import java.beans.DefaultPersistenceDelegate;
import java.sql.Timestamp;
import java.util.List;

import twaver.ClientPropertyPersistentFilter;
import twaver.Element;
import twaver.PersistenceManager;
import twaver.TDataBox;
import twaver.TWaverConst;

import com.boco.graphkit.ext.IViewElement;
import com.boco.transnms.common.bussiness.consts.PropertyConst;
import com.boco.transnms.common.dto.base.BoActionContext;
import com.boco.transnms.common.dto.base.DboBlob;
import com.boco.transnms.common.dto.base.GenericDO;
import com.boco.transnms.common.dto.common.Template;
import com.boco.transnms.server.bo.base.BoHomeFactory;
import com.boco.transnms.server.bo.ibo.common.ITemplateBOX;

public class TemplateUtils {

    public static ClientPropertyPersistentFilter persistentFilter = new ClientPropertyPersistentFilter() {
        public boolean isTransient(Element element, Object clientPropertyKey) {
            if (element instanceof IViewElement) {
                GenericDO dbo = ((IViewElement) element).getNodeValue();
                if (dbo != null && dbo.getAttrValue(String.valueOf(clientPropertyKey)) instanceof Timestamp) {
                    return true;
                }
                if(dbo != null && dbo.getAllAttr().containsKey("ABRACKCUID")){
                	return false;
                }
                if(dbo != null && dbo.getAllAttr().containsKey("AB_CHOOSE")){
                	return false;
                }
                if(dbo != null && dbo.getAllAttr().containsKey("A_LIST")){
                	return false;
                }
                if(dbo != null && dbo.getAllAttr().containsKey("B_LIST")){
                	return false;
                }
                if (dbo != null && (
                    (dbo.containsAttr(String.valueOf(clientPropertyKey))))) {
                    return dbo.getAllAttr().containsKey(clientPropertyKey);
                }
            }
            return false;
        }
    };
    
    public static String getXmlByBox(TDataBox box) {
    
    String xml="";
    try{
        PersistenceManager.registerClassDelegate(Timestamp.class, new DefaultPersistenceDelegate(new String[] {"time"}));
        PersistenceManager.setClientPropertyFilter(persistentFilter);
        xml = PersistenceManager.writeByXML(box, false);
        PersistenceManager.setClientPropertyFilter(null);
        PersistenceManager.unregisterClassDelegate(Timestamp.class);
    }catch(Exception ex){
        ex.printStackTrace();
    }
    	return xml;
    }
    
    public static String getTemplateXml(Template template) throws Exception {
        if( template == null ) {
            return null;
        }
        String templateXml = null;
        ITemplateBOX templatebo = BoHomeFactory.getInstance().getBO(ITemplateBOX.class);
        DboBlob dboBlob = (DboBlob) templatebo.getTemplatePic(new BoActionContext(), template);
        if (dboBlob != null) {
            String xml = new String(dboBlob.getBlobBytes(), TWaverConst.DEFAULT_ENCODING).trim();
            templateXml = PicBlobUtils.replaceXml(xml);
        }

        return templateXml;
      }
    
    public static void clearBoxDataProperty(TDataBox dataBox) {
        dataBox.putClientProperty(PropertyConst.TEMPLATE_DATA_PIC_FLAG, null);
        dataBox.putClientProperty(PropertyConst.TEMPLATE_SLOT_PAIR_FLAG, null);
    }
    
    public static Rectangle getMaxRect(List<Element> allList, int border) {
        if (allList != null && allList.size() > 0) {
            Element firstElement = allList.get(0);
            double minX = firstElement.getX();
            double minY = firstElement.getY();
            double maxX = firstElement.getX() + firstElement.getWidth();
            double maxY = firstElement.getY() + firstElement.getHeight();
            for (Element element : allList) {
                if (minX > element.getX()) {
                    minX = element.getX();
                }
                if (minY > element.getY()) {
                    minY = element.getY();
                }
                if (maxX < element.getX() + element.getWidth()) {
                    maxX = element.getX() + element.getWidth();
                }
                if (maxY < element.getY() + element.getHeight()) {
                    maxY = element.getY() + element.getHeight();
                }
            }
            int locX = (minX > border) ? (int) (minX - border) : 0;
            int locY = (minY > border) ? (int) (minY - border) : 0;
            int width = (int) (maxX - minX + 3 * border);
            int height = (int) (maxY - minY + 3 * border);
            Rectangle maxRect = new Rectangle(locX, locY, width, height);
            return maxRect;
        }
        return null;
    }
}
