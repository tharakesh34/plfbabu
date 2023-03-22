package com.pennanttech.pff.npa.web;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pff.npa.model.AssetClassSetupHeader;

public class AssetClassSetupListItemRenderer implements ListitemRenderer<AssetClassSetupHeader>, Serializable {
	private static final long serialVersionUID = 1L;

	public AssetClassSetupListItemRenderer() {
		super();
	}

	@Override
	public void render(Listitem item, AssetClassSetupHeader assetClassSetupHeader, int count) throws Exception {

		Listcell lc;
		lc = new Listcell(assetClassSetupHeader.getEntityCode());
		lc.setParent(item);
		lc = new Listcell(assetClassSetupHeader.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(assetClassSetupHeader.getRecordType()));
		lc.setParent(item);
		item.setAttribute("id", assetClassSetupHeader.getId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onAssetClassSetupItemDoubleClicked");
	}
}