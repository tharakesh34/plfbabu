package com.pennant.webui.finance.enquiry.model;

import java.io.Serializable;

import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.expenses.FinExpenseMovements;
import com.pennant.util.PennantAppUtil;

public class ExpenseMovementListModelItemRenderer implements ListitemRenderer<FinExpenseMovements>, Serializable{

	private static final long serialVersionUID = 3541122568618470160L;

	private int ccyFormatter = 0;
	
	public ExpenseMovementListModelItemRenderer(int ccyFormatter) {
		this.ccyFormatter = ccyFormatter;
	}
	@Override
	public void render(Listitem item, FinExpenseMovements detail, int count) throws Exception {
		
		Listcell lc;
		lc = new Listcell(detail.getModeType());
		lc.setParent(item);

		lc = new Listcell(detail.getFileName());
		lc.setParent(item);

		lc = new Listcell(String.valueOf(detail.getTransactionDate()));
		lc.setParent(item);

		lc = new Listcell(String.valueOf(detail.getLastMntBy()));
		lc.setParent(item);

		lc = new Listcell(detail.getTransactionType());
		lc.setParent(item);

		lc = new Listcell(PennantAppUtil.amountFormate(detail.getTransactionAmount(),ccyFormatter));
		lc.setStyle("text-align:right;");
		lc.setParent(item);


		}
}
