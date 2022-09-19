package com.pennant.webui.cersai.securityinteresttype.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.cersai.SecurityInterestType;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class SecurityInterestTypeListModelItemRenderer implements ListitemRenderer<SecurityInterestType>, Serializable {

	private static final long serialVersionUID = 1L;

	public SecurityInterestTypeListModelItemRenderer() {
		super();
	}

	@Override
	public void render(Listitem item, SecurityInterestType securityInterestType, int count) throws Exception {

		Listcell lc;
		lc = new Listcell(String.valueOf(securityInterestType.getAssetCategoryId()));
		lc.setParent(item);
		lc = new Listcell(PennantApplicationUtil.formateInt(securityInterestType.getId()));
		lc.setParent(item);
		lc = new Listcell(securityInterestType.getDescription());
		lc.setParent(item);
		lc = new Listcell(securityInterestType.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(securityInterestType.getRecordType()));
		lc.setParent(item);
		item.setAttribute("assetCategoryId", securityInterestType.getAssetCategoryId());
		item.setAttribute("id", securityInterestType.getId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onSecurityInterestTypeItemDoubleClicked");
	}
}