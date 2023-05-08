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

import org.apache.commons.lang.StringUtils;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.customermasters.CustomerRating;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for listItems in the listBox.
 * 
 */
public class CustomerRatinglistItemRenderer implements ListitemRenderer<CustomerRating>, Serializable {

	private static final long serialVersionUID = 6321996138703133595L;

	public CustomerRatinglistItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, CustomerRating rating, int count) {

		Listcell lc;
		if (StringUtils.equals(rating.getRecordType(), PennantConstants.RCD_ADD)
				|| StringUtils.equals(rating.getRecordType(), PennantConstants.RCD_UPD)) {
			lc = new Listcell(rating.getLovDescCustRatingTypeName());
			lc.setParent(item);
		} else {
			lc = new Listcell(rating.getCustRatingType());
			lc.setParent(item);
		}
		lc = new Listcell(rating.getCustRatingCode() + (StringUtils.isBlank(rating.getLovDesccustRatingCodeDesc()) ? ""
				: "-" + rating.getLovDesccustRatingCodeDesc()));
		lc.setParent(item);
		lc = new Listcell(rating.getCustRating() + (StringUtils.isBlank(rating.getLovDescCustRatingName()) ? ""
				: "-" + rating.getLovDescCustRatingName()));
		lc.setParent(item);
		lc = new Listcell(rating.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(rating.getRecordType()));
		lc.setParent(item);
		item.setAttribute("data", rating);
		ComponentsCtrl.applyForward(item, "onDoubleClick=onCustomerRatingItemDoubleClicked");
	}
}