package com.pennant.webui.reports.customer.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listgroup;
import org.zkoss.zul.Listgroupfoot;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.finance.FinanceEnquiry;
import com.pennant.util.PennantAppUtil;

public class CustomerEnquiryListModelItemRender implements ListitemRenderer<FinanceEnquiry>,
		Serializable {

	private static final long serialVersionUID = -6954091801433341494L;
	
	public CustomerEnquiryListModelItemRender() {
		
	}
	
	@Override
	public void render(Listitem item,final FinanceEnquiry aFinanceEnq, int count) throws Exception {

		if (item instanceof Listgroup) {
			Listcell cell = new Listcell(aFinanceEnq.getLovDescFinTypeName());
			cell.setSpan(3);
			item.appendChild(cell);

			cell = new Listcell(CurrencyUtil.getCcyDesc(aFinanceEnq.getFinCcy()));
			cell.setSpan(3);
			item.appendChild(cell);
		} else if (item instanceof Listgroupfoot) {
			Listcell cell = new Listcell(PennantAppUtil.amountFormate(
					aFinanceEnq.getFinAmount().add(aFinanceEnq.getFeeChargeAmt()).subtract(aFinanceEnq.getFinRepaymentAmount()),
					CurrencyUtil.getFormat(aFinanceEnq.getFinCcy())));
			cell.setSpan(5);
			item.appendChild(cell);
		} else {
			Listcell lc;
			lc = new Listcell(aFinanceEnq.getFinReference());
			lc.setParent(item);
			lc = new Listcell(aFinanceEnq.getFinBranch());
			lc.setParent(item);
			lc = new Listcell(DateUtility.formatToLongDate(aFinanceEnq.getFinStartDate()));
			lc.setParent(item);
			lc = new Listcell(DateUtility.formatToLongDate(aFinanceEnq.getGrcPeriodEndDate()));
			lc.setParent(item);
			lc = new Listcell(String.valueOf(aFinanceEnq.getNumberOfTerms()));
			lc.setParent(item);
			lc = new Listcell(DateUtility.formatToLongDate(aFinanceEnq.getMaturityDate()));
			lc.setParent(item);
			lc = new Listcell(PennantAppUtil.amountFormate(
					aFinanceEnq.getFinAmount(),
					CurrencyUtil.getFormat(aFinanceEnq.getFinCcy())));
			lc.setStyle("text-align:right;");
			lc.setParent(item);
			lc = new Listcell(PennantAppUtil.amountFormate(
					aFinanceEnq.getFinAmount().add(aFinanceEnq.getFeeChargeAmt()).subtract(aFinanceEnq.getFinRepaymentAmount()),
					CurrencyUtil.getFormat(aFinanceEnq.getFinCcy())));
			lc.setStyle("text-align:right");
			lc.setParent(item);
			lc = new Listcell(DateUtility.formatToLongDate(aFinanceEnq.getNextDueDate()));
			lc.setParent(item);
			lc = new Listcell(PennantAppUtil.amountFormate(
					aFinanceEnq.getNextDueAmount(),
					CurrencyUtil.getFormat(aFinanceEnq.getFinCcy())));
			lc.setStyle("text-align:right");
			lc.setParent(item);
			item.setAttribute("data", aFinanceEnq);
			ComponentsCtrl.applyForward(item, "onDoubleClick=onLoanItemDoubleClicked");
		}
	}
}
