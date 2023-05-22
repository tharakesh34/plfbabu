/**
 * Copyright 2010 the original author or authors.
 * 
 * This file is part of Zksample2. http://zksample2.sourceforge.net/
 *
 * Zksample2 is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * Zksample2 is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Zksample2. If not, see
 * <http://www.gnu.org/licenses/gpl.html>.
 */
package com.pennant.webui.customermasters.customer.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.customermasters.DirectorDetail;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for listItems in the listBox.
 * 
 */
public class CustomerDirectorListItemRenderer implements ListitemRenderer<DirectorDetail>, Serializable {

	private static final long serialVersionUID = 6321996138703133595L;

	public CustomerDirectorListItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, DirectorDetail detail, int count) {

		Listcell lc;
		lc = new Listcell(detail.getFirstName());
		lc.setParent(item);
		lc = new Listcell(detail.getShortName());
		lc.setParent(item);
		if (detail.getRecordType().equals(PennantConstants.RCD_ADD)
				|| detail.getRecordType().equals(PennantConstants.RCD_UPD)) {
			lc = new Listcell(detail.getLovDescCustGenderCodeName());
			lc.setParent(item);
			lc = new Listcell(detail.getLovDescCustSalutationCodeName());
			lc.setParent(item);
		} else {
			lc = new Listcell(detail.getCustGenderCode() + "-" + detail.getLovDescCustGenderCodeName());
			lc.setParent(item);
			lc = new Listcell(detail.getCustSalutationCode() + "-" + detail.getLovDescCustSalutationCodeName());
			lc.setParent(item);
		}

		lc = new Listcell(detail.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(detail.getRecordType()));
		lc.setParent(item);
		item.setAttribute("data", detail);
		ComponentsCtrl.applyForward(item, "onDoubleClick=onCustomerDirectorItemDoubleClicked");
	}
}