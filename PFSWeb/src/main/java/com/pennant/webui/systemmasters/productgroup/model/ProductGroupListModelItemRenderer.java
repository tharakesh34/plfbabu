package com.pennant.webui.systemmasters.productgroup.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.systemmasters.ProductGroup;
import com.pennant.backend.util.PennantJavaUtil;

public class ProductGroupListModelItemRenderer implements ListitemRenderer<ProductGroup>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void render(Listitem item, ProductGroup productGroup, int index) {
		Listcell lc;
		lc = new Listcell(productGroup.getModelId());
		lc.setParent(item);
		lc = new Listcell(String.valueOf(productGroup.getProductCategoryId()));
		lc.setParent(item);
		lc = new Listcell(String.valueOf(productGroup.getChannel()));
		lc.setParent(item);
		lc = new Listcell();
		final Checkbox cbProductGroupIsActive = new Checkbox();
		cbProductGroupIsActive.setDisabled(true);
		cbProductGroupIsActive.setChecked(productGroup.isActive());
		lc.appendChild(cbProductGroupIsActive);

		lc.setParent(item);
		lc = new Listcell(productGroup.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(productGroup.getRecordType()));
		lc.setParent(item);
		item.setAttribute("id", productGroup.getProductGroupId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onProductGroupItemDoubleClicked");
	}

}
