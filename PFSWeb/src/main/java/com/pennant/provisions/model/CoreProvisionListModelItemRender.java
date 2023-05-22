package com.pennant.provisions.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.backend.model.financemanagement.Provision;

public class CoreProvisionListModelItemRender implements ListitemRenderer<Provision>, Serializable {

	private static final long serialVersionUID = -6954091801433341494L;

	public CoreProvisionListModelItemRender() {
	    super();
	}

	@Override
	public void render(Listitem item, Provision provisions, int count) {

		Listcell lc;
		lc = new Listcell(provisions.getFinReference());
		lc.setParent(item);
		// lc = new Listcell(DateUtility.formatToLongDate(provisions.getProvisionCalDate()));
		lc.setParent(item);
		lc = new Listcell(CurrencyUtil.format(provisions.getProvisionedAmt(), 2));
		lc.setStyle("text-align:right;");
		lc.setParent(item);
		// lc = new Listcell(PennantAppUtil.amountFormate(provisions.getProvisionAmtCal(), 2));
		lc.setStyle("text-align:right;");
		lc.setParent(item);
		// lc = new Listcell(PennantAppUtil.amountFormate(provisions.getNonFormulaProv(), 2));
		lc.setStyle("text-align:right;");
		lc.setParent(item);

		lc = new Listcell();
		final Checkbox cbIsInternalAc = new Checkbox();
		cbIsInternalAc.setDisabled(true);
		// cbIsInternalAc.setChecked(provisions.isUseNFProv());
		lc.appendChild(cbIsInternalAc);
		lc.setStyle("text-align:center");
		lc.setParent(item);

		// lc = new Listcell(DateUtility.formatToLongDate(provisions.getPrevProvisionCalDate()));
		lc.setParent(item);

		// lc = new Listcell(PennantAppUtil.amountFormate(provisions.getPrevProvisionedAmt(), 2));
		lc.setStyle("text-align:right;");
		lc.setParent(item);

		// lc = new Listcell(provisions.getTransRef());
		lc.setParent(item);

		item.setAttribute("data", provisions);

		ComponentsCtrl.applyForward(item, "onClick=onProvisionItemChecked");

	}
}
