package com.pennant.webui.financemanagement.ocr;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.finance.FinOCRHeader;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantJavaUtil;

public class OCRMaintenanceListModelItemRenderer implements ListitemRenderer<FinOCRHeader>, Serializable {

	private static final long serialVersionUID = -8480551675388111310L;

	@Override
	public void render(Listitem item, FinOCRHeader finOCRHeader, int index) throws Exception {

		Listcell lc;
		lc = new Listcell(finOCRHeader.getFinReference());
		lc.setParent(item);
		lc = new Listcell(PennantApplicationUtil.amountFormate(finOCRHeader.getTotalDemand(), 0));
		lc.setParent(item);
		lc = new Listcell(finOCRHeader.getOcrType());
		lc.setParent(item);
		lc = new Listcell(finOCRHeader.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(finOCRHeader.getRecordType()));
		lc.setParent(item);

		item.setAttribute("finID", finOCRHeader.getFinID());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onOCRMaintenanceItemDoubleClicked");

	}

}
