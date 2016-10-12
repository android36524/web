package com.boco.irms.app.utils;

import java.util.ArrayList;
import java.util.List;

import com.boco.core.utils.exception.UserException;

public class DMNameUtils {

    public static List<String> getDMDataName(String firstName,String lastName,String figure,int startNo,int no) {
    	List<String> list=new ArrayList<String>();
    	int number=figure.length()-String.valueOf(startNo).length();
    	if(number<0){
    		throw new UserException("开始编号尾数小于选择的位数");
    	}
		String numstr="";
		for(int i=0;i<number;i++){
			numstr=numstr+"0";
		}
		for(int i=0;i<no;i++){
			list.add(firstName+numstr+startNo+lastName);
			startNo++;
		}
    	return list;
    }
   
}
