package com.pennant.webui.cersai.assetcategory.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.cersai.AssetCategory;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class AssetCategoryListModelItemRenderer implements ListitemRenderer<AssetCategory>, Serializable {

	private static final long serialVersionUID = 1L;

	public AssetCategoryListModelItemRenderer() {
		super();
	}

	@Override
	public void render(Listitem item, AssetCategory assetCategory, int count) throws Exception {

		Listcell lc;
		lc = new Listcell(PennantApplicationUtil.formateInt(assetCategory.getId()));
		lc.setParent(item);
		lc = new Listcell(assetCategory.getDescription());
		lc.setParent(item);
		lc = new Listcell(assetCategory.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(assetCategory.getRecordType()));
		lc.setParent(item);
		item.setAttribute("id", assetCategory.getId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onAssetCategoryItemDoubleClicked");
	}
}