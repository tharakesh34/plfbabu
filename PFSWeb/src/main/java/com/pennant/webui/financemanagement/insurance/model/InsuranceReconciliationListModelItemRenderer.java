package com.pennant.webui.financemanagement.insurance.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.insurance.InsuranceDetails;
import com.pennant.backend.util.PennantJavaUtil;

public class InsuranceReconciliationListModelItemRenderer implements ListitemRenderer<InsuranceDetails>, Serializable {
	public InsuranceReconciliationListModelItemRenderer() {
		super();
	}
	private static final long serialVersionUID = -4499074360503828269L;

	@Override
	public void render(Listitem item, InsuranceDetails insuranceDetails, int count) throws Exception {
		Listcell lc;
		lc = new Listcell(insuranceDetails.getReference());
		lc.setParent(item);
		lc = new Listcell(insuranceDetails.getPolicyNumber());
		lc.setParent(item);
		lc = new Listcell(insuranceDetails.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(insuranceDetails.getRecordType()));
		lc.setParent(item);
		item.setAttribute("id", insuranceDetails.getId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onInsuranceDetailsItemDoubleClicked");
	}

}
