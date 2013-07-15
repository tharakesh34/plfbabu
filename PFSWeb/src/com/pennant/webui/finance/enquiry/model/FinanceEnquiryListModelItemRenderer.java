package com.pennant.webui.finance.enquiry.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.finance.FinanceEnquiry;
import com.pennant.backend.util.PennantConstants;
import com.pennant.util.PennantAppUtil;

public class FinanceEnquiryListModelItemRenderer implements ListitemRenderer<FinanceEnquiry>, Serializable{

	private static final long serialVersionUID = 5574543684897936853L;
	//Upgraded to ZK-6.5.1.1 Added an additional parameter of type count 	
	@Override
	public void render(Listitem item, FinanceEnquiry enquiry, int count) throws Exception {
		//final FinanceEnquiry enquiry = (FinanceEnquiry) data;
		Listcell lc;
		lc = new Listcell(enquiry.getFinType());
		lc.setParent(item);
		lc = new Listcell(enquiry.getLovDescProductCodeName());
		lc.setParent(item);
		lc = new Listcell(enquiry.getLovDescCustCIF());
		lc.setParent(item);
		lc = new Listcell(enquiry.getFinReference());
		lc.setParent(item);
		lc = new Listcell(enquiry.getFinBranch());
		lc.setParent(item);
		lc = new Listcell(DateUtility.formatUtilDate(enquiry.getFinStartDate(),
				PennantConstants.dateFormate));
		lc.setParent(item);
		lc = new Listcell(String.valueOf(enquiry.getNumberOfTerms()));
		lc.setParent(item);
		lc = new Listcell(DateUtility.formatUtilDate(enquiry.getMaturityDate(),
				PennantConstants.dateFormate));
		lc.setParent(item);
		lc = new Listcell(enquiry.getFinCcy());
		lc.setParent(item);
		lc = new Listcell(PennantAppUtil.amountFormate(enquiry.getFinAmount(),enquiry.getLovDescFinFormatter()));
		lc.setStyle("text-align:right");
		lc.setParent(item);
		if(enquiry.getFinRepaymentAmount()!=null){
			lc = new Listcell(PennantAppUtil.amountFormate(enquiry.getFinAmount()
					.subtract(enquiry.getFinRepaymentAmount()),enquiry.getLovDescFinFormatter()));
			lc.setStyle("text-align:right");
		}else{
			lc = new Listcell("");
			
		}
		lc.setParent(item);
		item.setAttribute("data", enquiry);
		ComponentsCtrl.applyForward(item, "onDoubleClick=onLoanItemDoubleClicked");
	}
}
