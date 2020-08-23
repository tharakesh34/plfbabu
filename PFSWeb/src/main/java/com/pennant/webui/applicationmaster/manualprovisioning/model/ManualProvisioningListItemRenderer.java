package com.pennant.webui.applicationmaster.manualprovisioning.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.backend.model.financemanagement.Provision;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.util.PennantAppUtil;

public class ManualProvisioningListItemRenderer implements ListitemRenderer<Provision>, Serializable {

	private static final long serialVersionUID = -4554647022945989420L;

	public ManualProvisioningListItemRenderer() {

	}

	@Override
	public void render(Listitem item, Provision provision, int count) throws Exception {

		int format = CurrencyUtil.getFormat(provision.getFinCcy());

		Listcell lc;
		lc = new Listcell(provision.getLovDescCustCIF());
		lc.setParent(item);
		lc = new Listcell(provision.getLovDescCustShrtName());
		lc.setParent(item);
		lc = new Listcell(provision.getFinReference());
		lc.setParent(item);
		lc = new Listcell(provision.getFinType());
		lc.setParent(item);
		lc = new Listcell();
		final Checkbox manualProvision = new Checkbox();
		manualProvision.setDisabled(true);
		manualProvision.setChecked(provision.isManualProvision());
		lc.appendChild(manualProvision);
		lc.setParent(item);
		lc = new Listcell(provision.getAssetCode());
		lc.setParent(item);
		lc = new Listcell(provision.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(provision.getRecordType()));
		lc.setParent(item);
		item.setAttribute("data", provision);
		ComponentsCtrl.applyForward(item, "onDoubleClick=onManualProvisionItemDoubleClicked");
	}
}