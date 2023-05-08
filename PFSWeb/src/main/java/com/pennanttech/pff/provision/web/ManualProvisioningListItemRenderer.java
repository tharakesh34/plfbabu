package com.pennanttech.pff.provision.web;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennanttech.pff.provision.model.Provision;

public class ManualProvisioningListItemRenderer implements ListitemRenderer<Provision>, Serializable {
	private static final long serialVersionUID = 1L;

	public ManualProvisioningListItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, Provision provision, int count) throws Exception {
		int format = CurrencyUtil.getFormat(provision.getFinCcy());

		Listcell lc;
		lc = new Listcell(provision.getCustCIF());
		lc.setParent(item);
		lc = new Listcell(provision.getCustShrtName());
		lc.setParent(item);
		lc = new Listcell(provision.getFinReference());
		lc.setParent(item);
		lc = new Listcell(provision.getFinType());
		lc.setParent(item);
		lc = new Listcell(PennantApplicationUtil.amountFormate(provision.getOsPrincipal(), format));
		lc.setParent(item);
		lc = new Listcell(
				PennantApplicationUtil.amountFormate(provision.getOsPrincipal().add(provision.getOsProfit()), format)); // FIXME
		lc.setParent(item);
		lc = new Listcell(provision.isManualProvision() ? "Y" : "F");
		lc.setParent(item);
		lc = new Listcell(provision.getLoanClassification());
		lc.setParent(item);
		lc = new Listcell(provision.getEffectiveClassification());
		lc.setParent(item);
		lc = new Listcell(provision.getRecordStatus());
		lc.setParent(item);

		item.setAttribute("data", provision);

		ComponentsCtrl.applyForward(item, "onDoubleClick=onManualProvisionItemDoubleClicked");
	}
}