package com.pennant.webui.finance.enquiry.model;

import java.io.Serializable;
import java.math.BigDecimal;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.finance.FinanceEnquiry;
import com.pennant.util.PennantAppUtil;

public class FinanceEnquiryListModelItemRenderer implements ListitemRenderer<FinanceEnquiry>, Serializable{

	private static final long serialVersionUID = 5574543684897936853L;

	public FinanceEnquiryListModelItemRenderer() {
		
	}
	
	@Override
	public void render(Listitem item, FinanceEnquiry enquiry, int count) throws Exception {

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
		lc = new Listcell(DateUtility.formatToLongDate(enquiry.getFinStartDate()));
		lc.setParent(item);
		if (enquiry.getNOInst() > 0) {
			lc = new Listcell(String.valueOf(enquiry.getNOInst()));
		} else {
			lc = new Listcell(String.valueOf(enquiry.getNumberOfTerms()));
		}
		lc.setParent(item);
		lc = new Listcell(DateUtility.formatToLongDate(enquiry.getMaturityDate()));
		lc.setParent(item);
		lc = new Listcell(enquiry.getFinCcy());
		lc.setParent(item);
		BigDecimal finAmount = enquiry.getFinCurrAssetValue();
		if(enquiry.getFeeChargeAmt() != null && enquiry.getFeeChargeAmt().compareTo(BigDecimal.ZERO) > 0){
			finAmount = finAmount.add(enquiry.getFeeChargeAmt());
		}
		if(enquiry.getInsuranceAmt() != null && enquiry.getInsuranceAmt().compareTo(BigDecimal.ZERO) > 0){
			finAmount = finAmount.add(enquiry.getInsuranceAmt());
		}
		if(enquiry.getDownPayment() != null && enquiry.getDownPayment().compareTo(BigDecimal.ZERO) > 0){
			finAmount = finAmount.subtract(enquiry.getDownPayment());
		}
		
		lc = new Listcell(PennantAppUtil.amountFormate(finAmount,CurrencyUtil.getFormat(enquiry.getFinCcy())));
		lc.setStyle("text-align:right");
		lc.setParent(item);
		if(enquiry.getFinRepaymentAmount()!=null){
			lc = new Listcell(PennantAppUtil.amountFormate(finAmount
					.subtract(enquiry.getFinRepaymentAmount()),CurrencyUtil.getFormat(enquiry.getFinCcy())));
			lc.setStyle("text-align:right");
		}else{
			lc = new Listcell("");
			
		}
		lc.setParent(item);
		item.setAttribute("data", enquiry);
		ComponentsCtrl.applyForward(item, "onDoubleClick=onLoanItemDoubleClicked");
	}
}
