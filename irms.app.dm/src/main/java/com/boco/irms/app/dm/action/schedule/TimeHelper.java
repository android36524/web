package com.boco.irms.app.dm.action.schedule;
import java.util.Calendar;
import java.util.Date;

public class TimeHelper {
	
	public static Date getFirstDayOfMonth(int monthOffset,Date date) {
		Calendar c = Calendar.getInstance();  
		c.setTime(date);
        c.add(Calendar.MONTH, monthOffset);
        c.set(Calendar.DAY_OF_MONTH,1);
        c.set(Calendar.HOUR_OF_DAY ,0);
        c.set(Calendar.MINUTE,0);
        c.set(Calendar.SECOND,0);
        return c.getTime();
	}
	
	public static Date getFirstDayOfMonth(int monthOffset,int month) {
		Calendar c = Calendar.getInstance();  
		c.set(Calendar.MONTH,month-1);
        c.add(Calendar.MONTH, monthOffset);
        c.set(Calendar.DAY_OF_MONTH,1);
        c.set(Calendar.HOUR_OF_DAY ,0);
        c.set(Calendar.MINUTE,0);
        c.set(Calendar.SECOND,0);
        return c.getTime();
	}
	
	//根据指定时间得到相对该时间某一天的开始时间 
	public static Date getBeginTimeOfDay(Date date,int offset){
		Calendar c = Calendar.getInstance();  
		c.setTime(date);
        c.add(Calendar.DAY_OF_MONTH, offset);
        c.set(Calendar.HOUR_OF_DAY ,0);
        c.set(Calendar.MINUTE,0);
        c.set(Calendar.SECOND,0);
        c.set(Calendar.MILLISECOND,0);
        return c.getTime();
		
	}
	//得到上个月的最后一天
	public static Date getLastDayOfMonth(Date date) {
		Calendar c = Calendar.getInstance();  
		c.setTime(date);
        c.set(Calendar.DAY_OF_MONTH,1);
        c.add(Calendar.DAY_OF_MONTH, -1);
        return c.getTime();
	}
	
}
