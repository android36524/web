package com.boco.irms.app.dm.action.calllater;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import com.boco.common.util.debug.LogHome;
import com.boco.core.bean.SpringContextUtil;
import com.boco.core.ibatis.dao.IbatisDAO;
import com.boco.irms.app.dm.action.multithread.AsyncTaskExecutor;

/**
 * @description 计算业务区与站点关系
 * @author liuchao
 * @verison 
 * @date 2015年1月13日 下午3:05:31
 */
public class GridSiteRelCalcuCallLater implements Callable<String> {
	
	private String gridCuid;
	
	public GridSiteRelCalcuCallLater(String cuid){
		this.gridCuid = cuid;
	}
	
	@Override
	public String call() throws Exception {
		long st = System.currentTimeMillis();  
		try{
			IbatisDAO sde = (IbatisDAO)SpringContextUtil.getBean("IbatisSdeDAO");
			//IbatisDAO irms = (IbatisDAO)SpringContextUtil.getBean("IbatisResDAO");
			String sql = "SELECT SITE.CUID as RELATED_RIGHT_CUID,GRID.CUID AS RELATED_LEFT_CUID,'GRID' as RELATED_L_TYPE_CUID,'SITE' as RELATED_R_TYPE_CUID,'GRID@SITE0' as RELATED_RELATION_CUID,GRID.CUID||'_'||SITE.CUID as CUID"+
						 " FROM GEO_SITE SITE, GEO_GRID GRID "+
						 " WHERE GRID.CUID = '" + gridCuid + "' AND SDE.ST_WITHIN(SITE.SHAPE,GRID.SHAPE) = 1";
			List<Map> list = sde.querySql(sql);
			String delete = "DELETE FROM T_MD_RES_RES WHERE RELATED_LEFT_CUID = '" + gridCuid + "' AND RELATED_RELATION_CUID = 'GRID@SITE0'";
			sde.deleteSql(delete);
			sde.insertDynamicTableBatch("T_MD_RES_RES", list);
		}catch(Exception e)
		{
			LogHome.getLog().error(e.getMessage(),e);
		}
		long end = System.currentTimeMillis();
		System.out.println("grid and site relation success build on one thread in "+(end-st)+"ms!!!");
		return null;
	}

	
	public void execute()
	{
        try {  
             AsyncTaskExecutor<String> executor = new AsyncTaskExecutor<String>(this,1); 
             executor.start();
             executor.getService().shutdown();  
        }  catch (Exception e) {  
             e.printStackTrace();  
         }
	}
}
