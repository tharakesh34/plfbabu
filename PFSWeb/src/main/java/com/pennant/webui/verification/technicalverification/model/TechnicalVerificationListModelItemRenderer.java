package com.pennant.webui.verification.technicalverification.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.pff.verification.model.TechnicalVerification;

/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class TechnicalVerificationListModelItemRenderer implements ListitemRenderer<TechnicalVerification>, Serializable {
	private static final long serialVersionUID = 1L;

	public TechnicalVerificationListModelItemRenderer() {
		super();
	}

	@Override
	public void render(Listitem item, TechnicalVerification fi, int count) throws Exception {
		Listcell lc;
		lc = new Listcell(String.valueOf(fi.getCustCif()));
		lc.setParent(item);
		lc = new Listcell(fi.getCollateralType());
		lc.setParent(item);
		lc = new Listcell(fi.getCollateralRef());
		lc.setParent(item);
		lc = new Listcell(fi.getKeyReference());
		lc.setParent(item);
		lc = new Listcell(fi.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(fi.getRecordType()));
		lc.setParent(item);
		item.setAttribute("id", fi.getId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onTechnicalVerificationItemDoubleClicked");
	}
}