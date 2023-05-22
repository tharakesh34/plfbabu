package com.pennant.webui.financemanagement.bankorcorpcreditreview.model;

import java.io.Serializable;

import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.financemanagement.bankorcorpcreditreview.FinCreditReviewDetails;
import com.pennant.backend.util.FacilityConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

public class CreditApplicationReviewListModelItemRenderer
		implements ListitemRenderer<FinCreditReviewDetails>, Serializable {

	private static final long serialVersionUID = 2572007482335898401L;

	public CreditApplicationReviewListModelItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, FinCreditReviewDetails creditReviewDetails, int count) {

		Listcell lc;
		lc = new Listcell(String.valueOf(creditReviewDetails.getDetailId()));
		lc.setParent(item);
		lc = new Listcell(String.valueOf(creditReviewDetails.getLovDescCustCIF()));
		lc.setParent(item);
		lc = new Listcell(String.valueOf(creditReviewDetails.getCustomerId()));
		lc.setParent(item);
		lc = new Listcell(String.valueOf(creditReviewDetails.getLovDescCustShrtName()));
		lc.setParent(item);
		lc = new Listcell(String.valueOf(creditReviewDetails.getLovDescMaxAuditYear()));
		lc.setParent(item);
		lc = new Listcell(String.valueOf(creditReviewDetails.getLovDescMinAuditYear()));
		lc.setParent(item);
		if (PennantConstants.PFF_CUSTCTG_CORP.equals(creditReviewDetails.getCreditRevCode())) {
			lc = new Listcell(Labels.getLabel("label_Corporate"));
		} else if (PennantConstants.PFF_CUSTCTG_SME.equals(creditReviewDetails.getCreditRevCode())) {
			lc = new Listcell(Labels.getLabel("label_Financial"));
		} else if (PennantConstants.PFF_CUSTCTG_INDIV.equals(creditReviewDetails.getCreditRevCode())) {
			lc = new Listcell(Labels.getLabel("label_Individual"));
		} else {
			lc = new Listcell("");
		}
		lc.setParent(item);
		lc = new Listcell(creditReviewDetails.getAuditYear());
		lc.setParent(item);
		lc = new Listcell(creditReviewDetails.getAuditPeriod() == 12 ? "1" + FacilityConstants.YEAR
				: String.valueOf(creditReviewDetails.getAuditPeriod()) + FacilityConstants.MONTH);
		lc.setStyle("text-align:right;");
		lc.setParent(item);
		lc = new Listcell(creditReviewDetails.getBankName());
		lc.setParent(item);
		lc = new Listcell(creditReviewDetails.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(creditReviewDetails.getRecordType()));
		lc.setParent(item);
		item.setAttribute("data", creditReviewDetails);
		ComponentsCtrl.applyForward(item, "onDoubleClick=onCreditApplicationReviewItemDoubleClicked");
	}
}
