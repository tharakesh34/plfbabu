package com.pennanttech.pff.mmfl.cd.webui;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pff.cd.model.Manufacturer;

public class ManufacturerListModelItemRenderer implements ListitemRenderer<Manufacturer>, Serializable {
	private static final long serialVersionUID = 1L;

	public ManufacturerListModelItemRenderer() {
		super();
	}

	@Override
	public void render(Listitem item, Manufacturer manufacturer, int count) {

		Listcell lc;
		lc = new Listcell(manufacturer.getName());
		lc.setParent(item);
		lc = new Listcell(manufacturer.getDescription());
		lc.setParent(item);
		lc = new Listcell(manufacturer.getChannel());
		lc.setParent(item);
		if (manufacturer.isActive()) {
			lc = new Listcell("1");
			lc.setParent(item);
		} else {
			lc = new Listcell("0");
			lc.setParent(item);
		}
		lc = new Listcell(manufacturer.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(manufacturer.getRecordType()));
		lc.setParent(item);
		item.setAttribute("ManufacturerId", manufacturer.getManufacturerId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onManufacturerListItemDoubleClicked");
	}
}
