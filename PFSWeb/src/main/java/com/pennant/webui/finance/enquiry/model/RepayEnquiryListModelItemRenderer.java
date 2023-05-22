package com.pennant.webui.finance.enquiry.model;

import java.io.Serializable;

import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listgroup;
import org.zkoss.zul.Listgroupfoot;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennanttech.pennapps.core.util.DateUtil;

public class RepayEnquiryListModelItemRenderer implements ListitemRenderer<FinanceRepayments>, Serializable {

	private static final long serialVersionUID = 3541122568618470160L;
	private int formatter;

	public RepayEnquiryListModelItemRenderer(int formatter) {
		super();
		this.formatter = formatter;
	}

	@Override
	public void render(Listitem item, FinanceRepayments repayment, int count) {

		if (item instanceof Listgroup) {
			item.appendChild(
					new Listcell("Schedule Term : " + DateUtil.formatToLongDate(repayment.getFinSchdDate())));
		} else if (item instanceof Listgroupfoot) {
			Listcell cell = new Listcell("");
			cell.setSpan(6);
			item.appendChild(cell);
		} else {

			Listcell lc;
			lc = new Listcell(DateUtil.formatToLongDate(repayment.getFinPostDate()));
			lc.setParent(item);
			lc = new Listcell(String.valueOf(repayment.getLinkedTranId()));
			lc.setParent(item);
			lc = new Listcell(CurrencyUtil.format(repayment.getFinSchdPftPaid(), this.formatter));
			lc.setParent(item);
			lc = new Listcell(CurrencyUtil.format(repayment.getFinSchdPriPaid(), this.formatter));
			lc.setParent(item);
			lc = new Listcell(CurrencyUtil.format(repayment.getFinTotSchdPaid(), this.formatter));
			lc.setParent(item);
		}
	}
}
