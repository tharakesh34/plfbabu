package com.pennanttech.pff.mmfl.cd.webui;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pff.mmfl.cd.model.ConsumerProduct;

public class ConsumerProductListModelRenderer implements ListitemRenderer<ConsumerProduct>, Serializable {
	private static final long serialVersionUID = 1L;

	public ConsumerProductListModelRenderer() {
		super();
	}

	@Override
	public void render(Listitem item, ConsumerProduct product, int count) {

		Listcell lc;
		lc = new Listcell(product.getModelId());
		lc.setParent(item);
		lc = new Listcell(product.getAssetDescription());
		lc.setParent(item);
		lc = new Listcell(product.getModelStatus());
		lc.setParent(item);
		if (product.isActive()) {
			lc = new Listcell("1");
			lc.setParent(item);
		} else {
			lc = new Listcell("0");
			lc.setParent(item);
		}
		lc = new Listcell(product.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(product.getRecordType()));
		lc.setParent(item);
		item.setAttribute("ProductId", product.getProductId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onConsumerProductListItemDoubleClicked");
	}
}
