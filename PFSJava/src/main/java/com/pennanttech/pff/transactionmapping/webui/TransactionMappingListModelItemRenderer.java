package com.pennanttech.pff.transactionmapping.webui;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pff.cd.model.TransactionMapping;

public class TransactionMappingListModelItemRenderer implements ListitemRenderer<TransactionMapping>, Serializable {
	private static final long serialVersionUID = 1L;

	public TransactionMappingListModelItemRenderer() {
		super();
	}

	@Override
	public void render(Listitem item, TransactionMapping mapping, int count) {

		Listcell lc;

		lc = new Listcell(String.valueOf(mapping.getPosId()));
		lc.setParent(item);

		lc = new Listcell(String.valueOf(mapping.getDealerCode()));
		lc.setParent(item);

		lc = new Listcell(mapping.getDealerName());
		lc.setParent(item);

		lc = new Listcell(String.valueOf(mapping.getMid()));
		lc.setParent(item);

		lc = new Listcell(String.valueOf(mapping.getTid()));
		lc.setParent(item);

		lc = new Listcell();
		final Checkbox dealerIsActive = new Checkbox();
		dealerIsActive.setDisabled(true);
		dealerIsActive.setChecked(mapping.isActive());
		lc.appendChild(dealerIsActive);
		lc.setParent(item);

		lc = new Listcell(mapping.getRecordStatus());
		lc.setParent(item);

		lc = new Listcell(PennantJavaUtil.getLabel(mapping.getRecordType()));
		lc.setParent(item);

		item.setAttribute("id", mapping.getId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onTransactionMappingItemDoubleClicked");
	}

}
