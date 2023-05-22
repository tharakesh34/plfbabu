package com.pennant.webui.applicationmaster.returnedcheque.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.backend.model.returnedcheques.ReturnedChequeDetails;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantJavaUtil;

public class ReturnedChequeListModelItemRender implements ListitemRenderer<ReturnedChequeDetails>, Serializable {
	private static final long serialVersionUID = -5018118741984246012L;

	public ReturnedChequeListModelItemRender() {
	    super();
	}

	@Override
	public void render(Listitem item, ReturnedChequeDetails returnedCheque, int count) {
		Listcell lc;
		lc = new Listcell(returnedCheque.getCustCIF());
		lc.setParent(item);
		lc = new Listcell(returnedCheque.getChequeNo());
		lc.setParent(item);
		returnedCheque.setCcyEditField(CurrencyUtil.getFormat(returnedCheque.getCurrency()));
		lc = new Listcell(
				PennantApplicationUtil.amountFormate(returnedCheque.getAmount(), returnedCheque.getCcyEditField()));
		lc.setParent(item);
		lc = new Listcell(returnedCheque.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(returnedCheque.getRecordType()));
		lc.setParent(item);

		item.setAttribute("custCIF", returnedCheque.getCustCIF());
		item.setAttribute("chequeNo", returnedCheque.getChequeNo());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onReturnedChequeItemDoubleClicked");
	}

}
