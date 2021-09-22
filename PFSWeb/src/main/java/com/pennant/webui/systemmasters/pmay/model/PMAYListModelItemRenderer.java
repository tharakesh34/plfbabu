package com.pennant.webui.systemmasters.pmay.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.finance.PMAY;
import com.pennant.backend.util.PennantJavaUtil;

public class PMAYListModelItemRenderer implements ListitemRenderer<PMAY>, Serializable {
	private static final long serialVersionUID = 1L;

	public PMAYListModelItemRenderer() {
		super();
	}

	@Override
	public void render(Listitem item, PMAY pmay, int count) throws Exception {

		Listcell lc;
		lc = new Listcell(pmay.getFinReference());
		lc.setParent(item);
		lc = new Listcell(pmay.getCustCif());
		lc.setParent(item);
		lc = new Listcell(pmay.getCustShrtName());
		lc.setParent(item);

		lc = new Listcell(pmay.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(pmay.getRecordType()));
		lc.setParent(item);
		item.setAttribute("finID", pmay.getFinID());
		item.setAttribute("finReference", pmay.getFinReference());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onPMAYItemDoubleClicked");
	}
}
