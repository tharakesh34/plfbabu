package com.pennant.webui.financemanagement.financeFlags.model;

import java.io.Serializable;
import java.math.BigDecimal;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.backend.model.financemanagement.FinanceFlag;
import com.pennanttech.pennapps.core.util.DateUtil;

public class FinFlagsListModelItemRenderer implements ListitemRenderer<FinanceFlag>, Serializable {

	private static final long serialVersionUID = -4562142056572229437L;

	public FinFlagsListModelItemRenderer() {

	}

	@Override
	public void render(Listitem item, FinanceFlag financeFlag, int count) {

		Listcell lc;
		lc = new Listcell(financeFlag.getFinType());
		lc.setParent(item);
		lc = new Listcell(financeFlag.getFinCategory());
		lc.setParent(item);
		lc = new Listcell(financeFlag.getCustCIF());
		lc.setParent(item);
		lc = new Listcell(financeFlag.getFinReference());
		lc.setParent(item);
		lc = new Listcell(financeFlag.getFinBranch());
		lc.setParent(item);
		lc = new Listcell(DateUtil.formatToLongDate(financeFlag.getFinStartDate()));
		lc.setParent(item);
		lc = new Listcell(String.valueOf(financeFlag.getGraceTerms() + financeFlag.getNumberOfTerms()));
		lc.setParent(item);
		lc = new Listcell(DateUtil.formatToLongDate(financeFlag.getMaturityDate()));
		lc.setParent(item);
		lc = new Listcell(financeFlag.getFinCcy());
		lc.setParent(item);
		BigDecimal finAmount = financeFlag.getFinAmount();
		if (financeFlag.getFeeChargeAmt() != null && financeFlag.getFeeChargeAmt().compareTo(BigDecimal.ZERO) > 0) {
			finAmount = finAmount.add(financeFlag.getFeeChargeAmt());
		}

		int formatter = CurrencyUtil.getFormat(financeFlag.getFinCcy());

		lc = new Listcell(CurrencyUtil.format(finAmount, formatter));
		lc.setStyle("text-align:right");
		lc.setParent(item);
		if (financeFlag.getFinRepaymentAmount() != null) {
			lc = new Listcell(CurrencyUtil.format(finAmount.subtract(financeFlag.getFinRepaymentAmount()), formatter));
			lc.setStyle("text-align:right");
		} else {
			lc = new Listcell("");

		}
		lc.setParent(item);
		lc = new Listcell(financeFlag.getRecordStatus());
		lc.setParent(item);
		item.setAttribute("id", financeFlag.getFinReference());
		item.setAttribute("recordType", financeFlag.getRecordType());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onFinanceFlagsItemDoubleClicked");
	}
}
