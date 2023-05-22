package com.pennant.webui.verification.technicalverification.model;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.pff.verification.model.TechnicalVerification;

/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class TechnicalVerificationListModelItemRenderer
		implements ListitemRenderer<TechnicalVerification>, Serializable {
	private static final long serialVersionUID = 1L;

	public TechnicalVerificationListModelItemRenderer() {
		super();
	}

	@Override
	public void render(Listitem item, TechnicalVerification tv, int count) {
		Listcell lc;

		String customerName = tv.getCif();
		if (StringUtils.trimToNull(tv.getCustName()) != null) {
			customerName = customerName.concat(" - ").concat(tv.getCustName());
		}

		lc = new Listcell(customerName);
		lc.setParent(item);

		lc = new Listcell(tv.getCollateralType());
		lc.setParent(item);

		lc = new Listcell(tv.getCollateralRef());
		lc.setParent(item);

		lc = new Listcell(tv.getKeyReference());
		lc.setParent(item);

		lc = new Listcell(tv.getAgencyName());
		lc.setParent(item);

		lc = new Listcell(DateUtil.formatToLongDate(tv.getCreatedOn()));
		lc.setParent(item);

		lc = new Listcell(tv.getRecordStatus());
		lc.setParent(item);

		lc = new Listcell(PennantJavaUtil.getLabel(tv.getRecordType()));
		lc.setParent(item);

		item.setAttribute("id", tv.getId());
		item.setAttribute("AgentName", tv.getAgencyName());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onTechnicalVerificationItemDoubleClicked");
	}
}