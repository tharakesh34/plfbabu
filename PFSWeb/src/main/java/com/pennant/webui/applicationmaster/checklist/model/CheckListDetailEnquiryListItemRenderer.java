package com.pennant.webui.applicationmaster.checklist.model;

import java.io.Serializable;

import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.lmtmasters.FinanceCheckListReference;

/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class CheckListDetailEnquiryListItemRenderer
		implements ListitemRenderer<FinanceCheckListReference>, Serializable {

	private static final long serialVersionUID = 2744829555068348957L;

	public CheckListDetailEnquiryListItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, FinanceCheckListReference financeCheckListReference, int count) {

		Listcell lc;
		lc = new Listcell(String.valueOf(financeCheckListReference.getQuestionId()));
		lc.setParent(item);
		lc = new Listcell(financeCheckListReference.getLovDescQuesDesc());
		lc.setParent(item);
		lc = new Listcell(financeCheckListReference.getLovDescAnswerDesc());
		lc.setParent(item);
		lc = new Listcell(financeCheckListReference.getRemarks());
		lc.setParent(item);
		item.setAttribute("data", financeCheckListReference);

	}
}