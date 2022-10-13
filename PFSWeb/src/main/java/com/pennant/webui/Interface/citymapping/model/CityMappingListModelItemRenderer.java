package com.pennant.webui.Interface.citymapping.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.cersai.CityMapping;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class CityMappingListModelItemRenderer implements ListitemRenderer<CityMapping>, Serializable {

	private static final long serialVersionUID = 1L;

	public CityMappingListModelItemRenderer() {
		super();
	}

	@Override
	public void render(Listitem item, CityMapping cityMapping, int count) throws Exception {

		Listcell lc;
		lc = new Listcell(cityMapping.getMappingType());
		lc.setParent(item);
		lc = new Listcell(cityMapping.getCityCode());
		lc.setParent(item);
		lc = new Listcell(cityMapping.getMappingValue());
		lc.setParent(item);
		lc = new Listcell(cityMapping.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(cityMapping.getRecordType()));
		lc.setParent(item);
		item.setAttribute("mappingType", cityMapping.getMappingType());
		item.setAttribute("cityCode", cityMapping.getCityCode());
		item.setAttribute("mappingValue", cityMapping.getMappingValue());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onCityMappingItemDoubleClicked");
	}
}