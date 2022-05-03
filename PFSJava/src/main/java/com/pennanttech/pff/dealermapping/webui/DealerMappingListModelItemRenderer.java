package com.pennanttech.pff.dealermapping.webui;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.dealermapping.DealerMapping;
import com.pennant.backend.util.PennantJavaUtil;

public class DealerMappingListModelItemRenderer implements ListitemRenderer<DealerMapping>, Serializable {
	private static final long serialVersionUID = 1L;

	public DealerMappingListModelItemRenderer() {
		super();
	}

	@Override
	public void render(Listitem item, DealerMapping dealerMapping, int count) throws Exception {

		Listcell lc;

		lc = new Listcell(String.valueOf(dealerMapping.getMerchantName()));
		lc.setParent(item);

		lc = new Listcell(String.valueOf(dealerMapping.getStoreName()));
		lc.setParent(item);

		lc = new Listcell(String.valueOf(dealerMapping.getStoreAddress()));
		lc.setParent(item);

		lc = new Listcell(String.valueOf(dealerMapping.getStoreCity()));
		lc.setParent(item);

		lc = new Listcell(String.valueOf(dealerMapping.getStoreId()));
		lc.setParent(item);

		lc = new Listcell(String.valueOf(dealerMapping.getDealerCode()));
		lc.setParent(item);

		lc = new Listcell();
		final Checkbox dealerIsActive = new Checkbox();
		dealerIsActive.setDisabled(true);
		dealerIsActive.setChecked(dealerMapping.isActive());
		lc.appendChild(dealerIsActive);
		lc.setParent(item);

		lc = new Listcell(dealerMapping.getRecordStatus());
		lc.setParent(item);

		lc = new Listcell(PennantJavaUtil.getLabel(dealerMapping.getRecordType()));
		lc.setParent(item);

		item.setAttribute("dealerMapId", dealerMapping.getDealerMapId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onDealerMappingItemDoubleClicked");
	}

}
