package com.boco.irms.app.dm.action;


import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;

import javax.xml.namespace.QName;
import javax.xml.rpc.ServiceException;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;


public class WebServiceTestClientAction {

	public static void main(String[] args){
		try {
	        //String wsdlurl = "http://10.31.2.252:8080/webdm/services/WebServiceTestServerAction?wsdl"; 
	        String wsdlurl = "http://10.31.2.252:8090/AssetMS/services/WebServiceTestServerAction?wsdl";
	        Service service = new Service();
	        Call call;
			call = (Call)service.createCall();
	        call.setOperationName(new QName(wsdlurl,"sayHello"));
			call.setTargetEndpointAddress(new URL(wsdlurl));
		    String result1 = (String) call.invoke(new Object[]{"sssssss"});
		    System.out.println(result1);
		    
/*	        call.setOperationName(new QName(wsdlurl,"mapTest"));
			call.setTargetEndpointAddress(new URL(wsdlurl));
			Map<String,String> testMap = new HashMap<String,String>();
			testMap.put("1", "AAAAAAAAAAA");
			testMap.put("2", "BBBBBBBBBBBB");
			testMap.put("3", "CCCCCCCCCCCC");
			Map<String,String> result1 = (Map<String,String>) call.invoke(new Object[]{testMap});
			for(String key : result1.keySet()){
				System.out.println("mapTest接口调用成功，参数="+key+",值="+result1.get(key));
			}*/
		} catch (ServiceException e) {
			e.printStackTrace();
		}catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		} 

	}
	
	private static String getXml(){
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><resource><rescuids>" +
				"MANHLE-4028818e2726ef1801272c4b5b961386,MANHLE-ff8080812db9c324012ec6c223d75b6f," +
				"</rescuids><projectstate>1</projectstate></resource>";
		return xml;
	}
	
	
}

