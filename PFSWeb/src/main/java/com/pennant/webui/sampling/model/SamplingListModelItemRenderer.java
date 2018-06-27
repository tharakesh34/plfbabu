package com.pennant.webui.sampling.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennanttech.pennapps.pff.sampling.model.Sampling;

public class SamplingListModelItemRenderer implements ListitemRenderer<Sampling>, Serializable{
	private static final long serialVersionUID = 1L;

	public SamplingListModelItemRenderer() {
		super();
	}
	
	@Override
	public void render(Listitem item, Sampling sampling, int index) throws Exception {
		Listcell lc;
		lc=new Listcell(sampling.getCustCif());
		lc.setParent(item);
		lc=new Listcell(sampling.getCustShrtName());
		lc.setParent(item);
		lc=new Listcell(sampling.getKeyReference());
		lc.setParent(item);
		lc=new Listcell(sampling.getFinType().concat("-"+sampling.getFinTypeDesc()));
		lc.setParent(item);
		lc=new Listcell(sampling.getRecordStatus());
		lc.setParent(item);
		lc=new Listcell(sampling.getRecordType());
		lc.setParent(item);
		
		item.setAttribute("data", sampling);
		ComponentsCtrl.applyForward(item, "onDoubleClick=onSamplingItemDoubleClicked");
	}

}
