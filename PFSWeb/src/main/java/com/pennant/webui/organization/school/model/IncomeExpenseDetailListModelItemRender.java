package com.pennant.webui.organization.school.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pff.organization.school.model.IncomeExpenseHeader;

public class IncomeExpenseDetailListModelItemRender implements ListitemRenderer<IncomeExpenseHeader>, Serializable  {
	private static final long serialVersionUID = 1L;
	
	public IncomeExpenseDetailListModelItemRender() {
		super();
	}

	@Override
	public void render(Listitem item, IncomeExpenseHeader incExpHeader, int index) throws Exception {
		Listcell lc;
		
		lc = new Listcell(incExpHeader.getCustCif());
		lc.setParent(item);
		lc = new Listcell(String.valueOf(incExpHeader.getFinancialYear()));
		lc.setParent(item);
		lc = new Listcell(incExpHeader.getName());
		lc.setParent(item);
		lc = new Listcell(incExpHeader.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(incExpHeader.getRecordType()));
		lc.setParent(item);
		item.setAttribute("id", incExpHeader.getId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onIncomeExpenseItemDoubleClicked");
		
	}
	
	

}
