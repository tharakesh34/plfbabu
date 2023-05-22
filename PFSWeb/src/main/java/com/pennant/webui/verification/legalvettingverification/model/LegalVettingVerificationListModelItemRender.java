package com.pennant.webui.verification.legalvettingverification.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.pff.verification.model.LegalVetting;

public class LegalVettingVerificationListModelItemRender implements ListitemRenderer<LegalVetting>, Serializable {
	private static final long serialVersionUID = 1L;

	List<Long> list = new ArrayList<>();

	public LegalVettingVerificationListModelItemRender() {
		super();
	}

	@Override
	public void render(Listitem item, LegalVetting lv, int count) throws Exception {

		Listcell lc;
		lc = new Listcell(String.valueOf(lv.getCif()));
		lc.setParent(item);
		lc = new Listcell(String.valueOf(lv.getCollateralType()));
		lc.setParent(item);
		lc = new Listcell(String.valueOf(lv.getReferenceFor()));
		lc.setParent(item);
		lc = new Listcell(lv.getKeyReference());
		lc.setParent(item);
		lc = new Listcell(lv.getAgencyName());
		lc.setParent(item);
		lc = new Listcell(Labels.getLabel("label_Vetting"));
		lc.setParent(item);
		lc = new Listcell(DateUtil.formatToLongDate(lv.getCreatedOn()));
		lc.setParent(item);
		lc = new Listcell(lv.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(lv.getRecordType()));
		lc.setParent(item);

		item.setAttribute("verificationId", lv.getVerificationId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onLegalVerificationItemDoubleClicked");
	}

}
