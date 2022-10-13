package com.pennant.webui.Interface.provincemapping.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.cersai.ProvinceMapping;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class ProvinceMappingListModelItemRenderer implements ListitemRenderer<ProvinceMapping>, Serializable {

	private static final long serialVersionUID = 1L;

	public ProvinceMappingListModelItemRenderer() {
		super();
	}

	@Override
	public void render(Listitem item, ProvinceMapping provinceMapping, int count) throws Exception {

		Listcell lc;
		lc = new Listcell(provinceMapping.getMappingType());
		lc.setParent(item);
		lc = new Listcell(provinceMapping.getProvince());
		lc.setParent(item);
		lc = new Listcell(provinceMapping.getMappingValue());
		lc.setParent(item);
		lc = new Listcell(provinceMapping.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(provinceMapping.getRecordType()));
		lc.setParent(item);
		item.setAttribute("mappingType", provinceMapping.getMappingType());
		item.setAttribute("province", provinceMapping.getProvince());
		item.setAttribute("mappingValue", provinceMapping.getMappingValue());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onProvinceMappingItemDoubleClicked");
	}
}