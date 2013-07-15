package com.pennant.webui.financemanagement.bankorcorpcreditreview.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.financemanagement.bankorcorpcreditreview.FinCreditReviewDetails;
import com.pennant.backend.util.PennantJavaUtil;

public class CreditApplicationReviewListModelItemRenderer implements ListitemRenderer<FinCreditReviewDetails>, Serializable {

	private static final long serialVersionUID = 2572007482335898401L;
	//Upgraded to ZK-6.5.1.1 Added an additional parameter of type count 	
	@Override
	public void render(Listitem item, FinCreditReviewDetails creditReviewDetails, int count) throws Exception {

		//final AccountingSet creditReviewDetails = (AccountingSet) data;
		Listcell lc;
	  	lc = new Listcell(String.valueOf(creditReviewDetails.getDetailId()));
		lc.setParent(item);
		lc = new Listcell(String.valueOf(creditReviewDetails.getLovDescCustCIF()));
		lc.setParent(item);
		if("C".equals(creditReviewDetails.getCreditRevCode())){
			lc = new Listcell("Corporate");
		}else{
			lc = new Listcell("");
		}	  	
		lc.setParent(item);
	  	lc = new Listcell(creditReviewDetails.getAuditYear());
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
