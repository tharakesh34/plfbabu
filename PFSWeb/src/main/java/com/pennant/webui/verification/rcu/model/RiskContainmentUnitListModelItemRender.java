package com.pennant.webui.verification.rcu.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.pff.verification.model.RiskContainmentUnit;

public class RiskContainmentUnitListModelItemRender implements ListitemRenderer<RiskContainmentUnit>, Serializable {
	private static final long serialVersionUID = 1L;

	@Override
	public void render(Listitem item, RiskContainmentUnit rcu, int count) throws Exception {
		Listcell lc;
		lc = new Listcell(String.valueOf(rcu.getCif()));
		lc.setParent(item);
		lc = new Listcell(rcu.getKeyReference());
		lc.setParent(item);
		lc = new Listcell(rcu.getAgencyName());
		lc.setParent(item);
		lc = new Listcell(DateUtil.formatToLongDate(rcu.getCreatedOn()));
		lc.setParent(item);
		lc = new Listcell(rcu.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(rcu.getRecordType()));
		lc.setParent(item);

		item.setAttribute("verificationId", rcu.getVerificationId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onRiskContainmentUnitItemDoubleClicked");
	}
}
