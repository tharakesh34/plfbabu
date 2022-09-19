package com.pennant.webui.cersai.assetsubtype.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.cersai.AssetSubType;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class AssetSubTypeListModelItemRenderer implements ListitemRenderer<AssetSubType>, Serializable {

	private static final long serialVersionUID = 1L;

	public AssetSubTypeListModelItemRenderer() {
		super();
	}

	@Override
	public void render(Listitem item, AssetSubType assetSubType, int count) throws Exception {

		Listcell lc;
		lc = new Listcell(assetSubType.getAssetTypeId());
		lc.setParent(item);
		lc = new Listcell(PennantApplicationUtil.formateInt(assetSubType.getId()));
		lc.setParent(item);
		lc = new Listcell(assetSubType.getDescription());
		lc.setParent(item);
		lc = new Listcell(assetSubType.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(assetSubType.getRecordType()));
		lc.setParent(item);
		item.setAttribute("assetTypeId", assetSubType.getAssetTypeId());
		item.setAttribute("id", assetSubType.getId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onAssetSubTypeItemDoubleClicked");
	}
}