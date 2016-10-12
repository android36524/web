package com.boco.gis.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import twaver.Dummy;
import twaver.Element;
import twaver.Node;
import twaver.PersistenceManager;
import twaver.TDataBox;

import com.boco.common.util.debug.LogHome;
import com.boco.graphkit.ext.IViewElement;
import com.boco.graphkit.ext.component.TWDataBox;
import com.boco.graphkit.ext.component.TWNetwork;
import com.boco.graphkit.ext.draw.element.DrawingElement;
import com.boco.transnms.client.model.base.BoCmdFactory;
import com.boco.transnms.common.bussiness.helper.XmlHelper;
import com.boco.transnms.common.dto.base.BoActionContext;
import com.boco.transnms.common.dto.base.DboBlob;
import com.boco.transnms.common.dto.base.GenericDO;
import com.boco.transnms.common.dto.misc.AllPics;
import com.boco.transnms.common.dto.misc.ElementPic;
import com.boco.transnms.common.dto.misc.VirtualElement;
import com.boco.transnms.server.bo.helper.topo.AllPicsBOHelper;

public class PicBlobUtils {

    public static boolean createXmlAllPics(String cuid, String assisCuid, long objectTypeCode, TDataBox box) {
        try {
            DboBlob blob = new DboBlob();
            String xml = PersistenceManager.writeByXML(box, true);
            blob.setBlobBytes(xml.getBytes());
            boolean isSaveSuccess = createAllPics(cuid, assisCuid, objectTypeCode, blob);
            if (box instanceof TWDataBox) {
                ((TWDataBox) box).setPropertyChangedStateCleard();
            }
            return isSaveSuccess;
        } catch (Exception ex) {
            LogHome.getLog().error("", ex);
            return false;
        }
    }

    public static boolean createXmlAllPics(String cuid, String assisCuid, long objectTypeCode, TDataBox box, boolean withElementId) {
        try {
            DboBlob blob = new DboBlob();
            String xml = PersistenceManager.writeByXML(box, withElementId);
            blob.setBlobBytes(xml.getBytes());
            boolean isSaveSuccess = createAllPics(cuid, assisCuid, objectTypeCode, blob);
            if (box instanceof TWDataBox) {
                ((TWDataBox) box).setPropertyChangedStateCleard();
            }
            return isSaveSuccess;
        } catch (Exception ex) {
            LogHome.getLog().error("", ex);
            return false;
        }
    }

    public static AllPics generateXmlAllPics(String cuid, String assisCuid, long objectTypeCode, TDataBox box, boolean withElementId) {
        AllPics obj = new AllPics();
        obj.setCuid(cuid);
        obj.setAssisCuid(assisCuid);
        obj.setObjectTypeCode(objectTypeCode);
        try {
            DboBlob blob = new DboBlob();
            String xml = PersistenceManager.writeByXML(box, withElementId);
            blob.setBlobBytes(xml.getBytes());
            obj.setPic(blob);
        } catch (Exception ex) {
            LogHome.getLog().error("", ex);
        }
        return obj;
    }

    public static AllPics generateXmlAllPics(String cuid, long objectTypeCode, TDataBox box, boolean withElementId) {
        AllPics obj = new AllPics();
        obj.setCuid(cuid);
        obj.setObjectTypeCode(objectTypeCode);
        try {
            DboBlob blob = new DboBlob();
            String xml = PersistenceManager.writeByXML(box, withElementId);
            blob.setBlobBytes(xml.getBytes());
            obj.setPic(blob);
        } catch (Exception ex) {
            LogHome.getLog().error("", ex);
        }
        return obj;
    }

    public static boolean createAllPics(String cuid, String assisCuid, long objectTypeCode, Iterator iterator, boolean changeDtoID) {
        ArrayList list = new ArrayList();
        while (iterator.hasNext()) {
            Element element = (Element) iterator.next();
            if (element instanceof Node && element instanceof IViewElement) {
                GenericDO dbo = ((IViewElement) element).getNodeValue();
                int x = ((Node) element).getLocation().x;
                int y = ((Node) element).getLocation().y;
                if (dbo instanceof VirtualElement) {
                    VirtualElement virtualElement = (VirtualElement) dbo.deepClone();
                    virtualElement.setLocationX(x);
                    virtualElement.setLocationY(y);
                    list.add(virtualElement);
                } else {
                    ElementPic elementPic = new ElementPic();
                    if (changeDtoID) {
                        elementPic.setCuid(dbo.getCuid());
                    } else {
                        elementPic.setCuid(element.getID().toString());
                    }
                    elementPic.setX(x);
                    elementPic.setY(y);
                    list.add(elementPic);
                }
            } else if (element instanceof DrawingElement) {
                list.add(element);
            }
        }
        boolean isSaveSuccess = createAllPics(cuid, assisCuid, objectTypeCode, list);
        return isSaveSuccess;
    }

    public static boolean createAllPics(String cuid, String assisCuid, long objectTypeCode, Iterator iterator) {
        return createAllPics(cuid, assisCuid, objectTypeCode, iterator, true);
    }

    public static boolean createAllVisiblePics(String cuid, String assisCuid, long objectTypeCode, TWNetwork network) {
        TDataBox box = network.getDataBox();
        ArrayList list = new ArrayList();
        List elemetns = box.getLayers();
        Iterator iterator = elemetns.iterator();
        while (iterator.hasNext()) {
            Element element = (Element) iterator.next();
            if (!network.isVisible(element)) {
                continue;
            }
            if (element instanceof Node && element instanceof IViewElement) {
                GenericDO dbo = ((IViewElement) element).getNodeValue();
                ElementPic elementPic = new ElementPic();
                elementPic.setCuid(dbo.getCuid());
                elementPic.setX(((Node) element).getLocation().x);
                elementPic.setY(((Node) element).getLocation().y);
                list.add(elementPic);
            } else if (element instanceof DrawingElement) {
                list.add(element);
            }
        }
        boolean isSaveSuccess = createAllPics(cuid, assisCuid, objectTypeCode, list);
        if (box instanceof TWDataBox) {
            ((TWDataBox) box).setPropertyChangedStateCleard();
        }
        return isSaveSuccess;
    }

    public static List getAllPicsElement(TDataBox box) {
        ArrayList list = new ArrayList();
        List elemetns = box.getLayers();
        Iterator iterator = elemetns.iterator();
        while (iterator.hasNext()) {
            Element element = (Element) iterator.next();
            if (element instanceof Node && !(element instanceof Dummy) && element instanceof IViewElement) {
                GenericDO dbo = ((IViewElement) element).getNodeValue();
                ElementPic elementPic = new ElementPic();
                elementPic.setCuid(dbo.getCuid());
                elementPic.setX(((Node) element).getLocation().x);
                elementPic.setY(((Node) element).getLocation().y);
                list.add(elementPic);
            } else if (element instanceof DrawingElement) {
                list.add(element);
            }
        }
        return list;
    }

    public static boolean createAllPics(String cuid, String assisCuid, long objectTypeCode, TDataBox box) {
        List list = getAllPicsElement(box);
        boolean isSaveSuccess = createAllPics(cuid, assisCuid, objectTypeCode, list);
        if (box instanceof TWDataBox) {
            ((TWDataBox) box).setPropertyChangedStateCleard();
        }
        return isSaveSuccess;
    }

    public static boolean createAllPics(String cuid, String assisCuid, long objectTypeCode, List list) {
        byte[] bsObjList = null;
        try {
            bsObjList = XmlHelper.writeByXML(list);
        } catch (IOException ex) {
            LogHome.getLog().error(ex.getMessage());
        }
        return createAllPics(cuid, assisCuid, objectTypeCode, bsObjList);
    }

    public static boolean createAllPics(String cuid, String assisCuid, long objectTypeCode, byte[] blobBytes) {
        DboBlob blob = new DboBlob();
        blob.setBlobBytes(blobBytes);
        return createAllPics(cuid, assisCuid, objectTypeCode, blob);
    }

    public static boolean createAllPics(String cuid, String assisCuid, long objectTypeCode, DboBlob varPic) {
        AllPics obj = new AllPics();
        obj.setCuid(cuid);
        obj.setAssisCuid(assisCuid);
        obj.setObjectTypeCode(objectTypeCode);
        obj.setPic(varPic);
        return saveAllPics(obj);
    }

    protected static boolean saveAllPics(AllPics obj) {
        try {
            BoCmdFactory.getInstance().execBoCmd(AllPicsBOHelper.ActionName.addDistinctAllPics,
                                                 new BoActionContext(), obj);
            return true;
        } catch (Exception ex) {
            LogHome.getLog().error(ex.getMessage());
        }
        return false;
    }

    public static AllPics getAllPics(AllPics obj) {
        AllPics allPics = null;
        try {
            allPics = (AllPics) BoCmdFactory.getInstance().execBoCmd(AllPicsBOHelper.ActionName.getAllPics, new BoActionContext(), obj);
        } catch (Exception ex) {
            LogHome.getLog().error(ex.getMessage());
        }
        return allPics;
    }


    public static String getAllPicBlobBytes(AllPics obj) {
        String xml = null;
        try {
            AllPics allPics = getAllPics(obj);
            if (allPics != null && allPics.getPic() != null) {
                xml = new String(allPics.getPic().getBlobBytes());
                xml = replaceXml(xml);
            }
        } catch (Exception ex) {
            LogHome.getLog().error(ex.getMessage());
        }
        return xml;
    }

    public static String getAllPicBlobBytes(String cuid, String assisCuid, long objectTypeCode) {
        AllPics obj = new AllPics();
        obj.setCuid(cuid);
        obj.setAssisCuid(assisCuid);
        obj.setObjectTypeCode(objectTypeCode);
        return getAllPicBlobBytes(obj);
    }

    public static String getAllPicBlobBytes(String cuid, long objectTypeCode) {
        AllPics obj = new AllPics();
        obj.setCuid(cuid);
        obj.setObjectTypeCode(objectTypeCode);
        return getAllPicBlobBytes(obj);
    }

    public static boolean getAllPicBlobBytes(String cuid, long objectTypeCode, TDataBox box, Element rootElement) {
        return getAllPicBlobBytes(cuid, null, objectTypeCode, box, rootElement);
    }

    public static boolean getAllPicBlobBytes(String cuid, String assisCuid, long objectTypeCode, TDataBox box) {
        return getAllPicBlobBytes(cuid, assisCuid, objectTypeCode, box, null);
    }

    public static boolean getAllPicBlobBytes(String cuid, String assisCuid, long objectTypeCode, TDataBox box, Element rootElement) {
        AllPics obj = new AllPics();
        obj.setCuid(cuid);
        if (assisCuid != null) {
            obj.setAssisCuid(assisCuid);
        }
        obj.setObjectTypeCode(objectTypeCode);
        String xml = getAllPicBlobBytes(obj);
        try {
            if (xml != null) {
                PersistenceManager.readByXML(box, xml, rootElement);
                return true;
            }
            return false;
        } catch (Exception ex) {
            LogHome.getLog().error(ex);
            LogHome.getLog().error("xml:" + xml);
            return false;
        }
    }

    public static String getAllPicBlobBytes(String cuid, long objectTypeCode, TDataBox box) {
        AllPics obj = new AllPics();
        obj.setCuid(cuid);
        obj.setObjectTypeCode(objectTypeCode);
        return getAllPicBlobBytes(obj);
    }

    public static String replaceXml(String xml) {
        if (xml.indexOf("com.boco.transnms.client.view.area.AreaConst$ElementTypeEnum") >= 0) {
            xml = xml.replaceAll("com.boco.transnms.client.view.area.AreaConst\\$ElementTypeEnum",
                                 "com.boco.transnms.client.NmsClientConsts\\$ElementTypeEnum");
        }
        return xml;
    }

}
