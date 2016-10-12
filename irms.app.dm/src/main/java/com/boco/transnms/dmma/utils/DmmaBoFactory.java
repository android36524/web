package com.boco.transnms.dmma.utils;
import com.boco.dmma.server.bo.ibo.project.ICreateResourceBO;
import com.boco.dmma.server.bo.ibo.pda.IPdaBO;
import com.boco.dmma.server.bo.ibo.hitch.IHitchBO;
import com.boco.dmma.server.bo.ibo.pdagroup.IPdaGroupBO;
import com.boco.dmma.server.bo.ibo.quartz.IQuartzBO;
import com.boco.dmma.server.bo.ibo.task.ITaskBO;
import com.boco.transnms.server.bo.base.BOHome;
import com.boco.transnms.server.bo.base.BoHomeFactory;
import com.boco.transnms.server.bo.base.SeviceException;
import com.boco.transnms.server.bo.ibo.dm.IWireSegBO;

public class DmmaBoFactory {
	private static DmmaBoFactory bo = new DmmaBoFactory();
	public static DmmaBoFactory getInstance(){
		return bo;
	}
	private DmmaBoFactory(){
		
	};
	protected ITaskBO taskbo;
    
	public Object getBo(){
		try {
			return taskbo = BOHome.getBO(ITaskBO.class);
		} catch (SeviceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	public Object getHitBo() {
		try {
			return BOHome.getBO(IHitchBO.class);
		} catch (SeviceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	public Object getIPdaGroupBO() {
		try {
			return BOHome.getBO(IPdaGroupBO.class);
		} catch (SeviceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	public Object getIPdaBO() {
		try {
			return BOHome.getBO(IPdaBO.class);
		} catch (SeviceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	public ICreateResourceBO getICreateResourceBO() {
		try {
			return BOHome.getBO(ICreateResourceBO.class);
		} catch (SeviceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	public IQuartzBO getQuartzBO() {
		try {
			return BOHome.getBO(IQuartzBO.class);
		} catch (SeviceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public IWireSegBO getWireSegBO() {
	        return (IWireSegBO)BoHomeFactory.getInstance().getBO(IWireSegBO.class);
	    }
}
