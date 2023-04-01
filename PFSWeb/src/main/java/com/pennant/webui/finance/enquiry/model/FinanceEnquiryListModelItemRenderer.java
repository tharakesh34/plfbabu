package com.pennant.webui.finance.enquiry.model;

import java.io.Serializable;
import java.math.BigDecimal;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.backend.model.finance.FinanceEnquiry;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennanttech.pennapps.core.util.DateUtil;

public class FinanceEnquiryListModelItemRenderer implements ListitemRenderer<FinanceEnquiry>, Serializable {

	private static final long serialVersionUID = 5574543684897936853L;

	public FinanceEnquiryListModelItemRenderer() {

	}

	@Override
	public void render(Listitem item, FinanceEnquiry enquiry, int count) {

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
		lc = new Listcell(DateUtil.formatToLongDate(enquiry.getFinStartDate()));
		lc.setParent(item);
		lc = new Listcell(String.valueOf(enquiry.getNOInst()));// PSD# 145740
		lc.setParent(item);
		lc = new Listcell(DateUtil.formatToLongDate(enquiry.getMaturityDate()));
		lc.setParent(item);
		lc = new Listcell(enquiry.getFinCcy());
		lc.setParent(item);
		BigDecimal finAmount = BigDecimal.ZERO;
		if (PennantConstants.WORFLOW_MODULE_CD.equals(enquiry.getLovDescProductCodeName())) {
			finAmount = enquiry.getFinAmount();
		} else {
			finAmount = enquiry.getFinCurrAssetValue();
		}
		if (enquiry.getFeeChargeAmt() != null && enquiry.getFeeChargeAmt().compareTo(BigDecimal.ZERO) > 0) {
			finAmount = finAmount.add(enquiry.getFeeChargeAmt());
		}

		lc = new Listcell(CurrencyUtil.format(finAmount, CurrencyUtil.getFormat(enquiry.getFinCcy())));
		lc.setStyle("text-align:right");
		lc.setParent(item);
		if (enquiry.getFinRepaymentAmount() != null) {
			// KMILLMS-854: Loan basic details-loan O/S amount is not getting 0.
			BigDecimal curFinAmountValue = null;
			if (FinanceConstants.CLOSE_STATUS_CANCELLED.equals(enquiry.getClosingStatus())) {
				curFinAmountValue = BigDecimal.ZERO;
			} else {
				if (ImplementationConstants.ALW_DOWNPAY_IN_LOANENQ_AND_SOA) {
					curFinAmountValue = enquiry.getFinCurrAssetValue().add(enquiry.getFeeChargeAmt())
							.add(enquiry.getTotalCpz()).subtract(enquiry.getFinRepaymentAmount())
							.subtract(enquiry.getSvAmount()).subtract(enquiry.getAdvanceEMI());
				} else {
					curFinAmountValue = enquiry.getFinCurrAssetValue().add(enquiry.getFeeChargeAmt())
							.add(enquiry.getTotalCpz()).subtract(enquiry.getDownPayment())
							.subtract(enquiry.getFinRepaymentAmount()).subtract(enquiry.getSvAmount())
							.subtract(enquiry.getAdvanceEMI());
				}
			}
			lc = new Listcell(CurrencyUtil.format(curFinAmountValue, CurrencyUtil.getFormat(enquiry.getFinCcy())));
			lc.setStyle("text-align:right");
		} else {
			lc = new Listcell("");

		}
		lc.setParent(item);
		item.setAttribute("data", enquiry);
		ComponentsCtrl.applyForward(item, "onDoubleClick=onLoanItemDoubleClicked");
	}
}
