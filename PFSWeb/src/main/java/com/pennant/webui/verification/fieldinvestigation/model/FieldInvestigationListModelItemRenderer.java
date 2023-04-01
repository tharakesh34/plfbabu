package com.pennant.webui.verification.fieldinvestigation.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.pff.verification.model.FieldInvestigation;

/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class FieldInvestigationListModelItemRenderer implements ListitemRenderer<FieldInvestigation>, Serializable {
	private static final long serialVersionUID = 1L;

	public FieldInvestigationListModelItemRenderer() {
		super();
	}

	@Override
	public void render(Listitem item, FieldInvestigation fi, int count) {
		Listcell lc;
		lc = new Listcell(String.valueOf(fi.getCif()));
		lc.setParent(item);
		lc = new Listcell(fi.getAddressType());
		lc.setParent(item);
		lc = new Listcell(fi.getZipCode());
		lc.setParent(item);
		lc = new Listcell(fi.getKeyReference());
		lc.setParent(item);
		lc = new Listcell(fi.getAgencyName());
		lc.setParent(item);
		lc = new Listcell(DateUtil.formatToLongDate(fi.getCreatedOn()));
		lc.setParent(item);
		lc = new Listcell(fi.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(fi.getRecordType()));
		lc.setParent(item);
		item.setAttribute("id", fi.getId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onFieldInvestigationItemDoubleClicked");
	}
}