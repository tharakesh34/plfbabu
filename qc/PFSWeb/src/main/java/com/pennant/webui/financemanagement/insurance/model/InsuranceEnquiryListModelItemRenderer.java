package com.pennant.webui.financemanagement.insurance.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.insurance.InsuranceDetails;
import com.pennant.backend.util.PennantJavaUtil;

public class InsuranceEnquiryListModelItemRenderer implements ListitemRenderer<InsuranceDetails>, Serializable {
	public InsuranceEnquiryListModelItemRenderer() {
		super();
	}

	private static final long serialVersionUID = -4499074360503828269L;

	@Override
	public void render(Listitem item, InsuranceDetails details, int count) throws Exception {
		Listcell lc;
		lc = new Listcell(details.getFinReference());
		lc.setParent(item);
		lc = new Listcell(details.getReference());
		lc.setParent(item);
		lc = new Listcell(details.getPolicyNumber());
		lc.setParent(item);
		lc = new Listcell(details.getvASProviderId() + " - " + details.getVasProviderDesc());
		lc.setParent(item);
		lc = new Listcell(details.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(details.getRecordType()));
		lc.setParent(item);
		item.setAttribute("insuranceDetails", details);

		ComponentsCtrl.applyForward(item, "onDoubleClick=onInsuranceEnquiryItemDoubleClicked");
	}

}
