package com.pennant.webui.applicationmaster.loantypeKnockoff.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import com.pennant.backend.model.finance.FinTypeKnockOff;
import com.pennant.backend.util.PennantJavaUtil;

public class LoanTypeKnockOffModelItemRender implements ListitemRenderer<FinTypeKnockOff>,Serializable {

	private static final long serialVersionUID = 1L;

	public LoanTypeKnockOffModelItemRender() {
	}

	@Override
	public void render(Listitem item, FinTypeKnockOff data, int index) throws Exception {
		Listcell lc;
		lc=new Listcell(data.getLoanType());
		lc.setParent(item);
		lc = new Listcell(data.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(data.getRecordType()));
		lc.setParent(item);
		
		item.setAttribute("data", data);
		ComponentsCtrl.applyForward(item, "onDoubleClick=onLoanTypeKnockOffItemDoubleClicked");
	}

}