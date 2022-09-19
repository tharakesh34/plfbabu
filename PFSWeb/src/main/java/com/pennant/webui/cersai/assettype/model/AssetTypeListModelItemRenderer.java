package com.pennant.webui.cersai.assettype.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.cersai.AssetTyp;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class AssetTypeListModelItemRenderer implements ListitemRenderer<AssetTyp>, Serializable {

	private static final long serialVersionUID = 1L;

	public AssetTypeListModelItemRenderer() {
		super();
	}

	@Override
	public void render(Listitem item, AssetTyp assetType, int count) throws Exception {

		Listcell lc;

		lc = new Listcell(assetType.getAssetCategoryId());
		lc.setParent(item);
		lc = new Listcell(PennantApplicationUtil.formateInt(assetType.getId()));
		lc.setParent(item);
		lc = new Listcell(assetType.getDescription());
		lc.setParent(item);
		lc = new Listcell(assetType.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(assetType.getRecordType()));
		lc.setParent(item);
		item.setAttribute("assetCategoryId", assetType.getAssetCategoryId());
		item.setAttribute("id", assetType.getId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onAssetTypeItemDoubleClicked");
	}
}