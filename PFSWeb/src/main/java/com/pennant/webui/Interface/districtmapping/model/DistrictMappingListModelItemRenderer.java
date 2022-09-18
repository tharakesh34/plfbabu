package com.pennant.webui.Interface.districtmapping.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.cersai.DistrictMapping;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class DistrictMappingListModelItemRenderer implements ListitemRenderer<DistrictMapping>, Serializable {

	private static final long serialVersionUID = 1L;

	public DistrictMappingListModelItemRenderer() {
		super();
	}

	@Override
	public void render(Listitem item, DistrictMapping districtMapping, int count) throws Exception {

		Listcell lc;

		lc = new Listcell(PennantApplicationUtil.formateInt(districtMapping.getMappingType()));
		lc.setParent(item);
		lc = new Listcell(districtMapping.getDistrict());
		lc.setParent(item);
		lc = new Listcell(districtMapping.getMappingValue());
		lc.setParent(item);
		lc = new Listcell(districtMapping.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(districtMapping.getRecordType()));
		lc.setParent(item);
		item.setAttribute("mappingType", districtMapping.getMappingType());
		item.setAttribute("district", districtMapping.getDistrict());
		item.setAttribute("mappingValue", districtMapping.getMappingValue());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onDistrictMappingItemDoubleClicked");
	}
}