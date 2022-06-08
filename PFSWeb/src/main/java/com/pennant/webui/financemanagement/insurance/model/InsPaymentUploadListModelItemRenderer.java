package com.pennant.webui.financemanagement.insurance.model;

import java.io.Serializable;

import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.insurance.InsurancePaymentInstructions;
import com.pennant.backend.util.PennantJavaUtil;

public class InsPaymentUploadListModelItemRenderer
		implements ListitemRenderer<InsurancePaymentInstructions>, Serializable {
	private static final long serialVersionUID = 1L;

	public InsPaymentUploadListModelItemRenderer() {
		super();
	}

	@Override
	public void render(Listitem item, InsurancePaymentInstructions instructions, int index) {
		Listcell lc;
		lc = new Listcell(instructions.getEntityCode());
		lc.setParent(item);

		lc = new Listcell(instructions.getPaymentType());
		lc.setParent(item);

		lc = new Listcell(instructions.getRecordStatus());
		lc.setParent(item);

		lc = new Listcell(PennantJavaUtil.getLabel(instructions.getRecordType()));
		lc.setParent(item);
	}

}
