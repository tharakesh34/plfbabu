/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : FacilityListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 25-11-2013 * *
 * Modified Date : 25-11-2013 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 25-11-2013 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.facility.facility.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.facility.Facility;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.util.DateUtil;

/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class FacilityListModelItemRenderer implements ListitemRenderer<Facility>, Serializable {

	private static final long serialVersionUID = 1L;

	public FacilityListModelItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, Facility facility, int count) {

		Listcell lc;
		lc = new Listcell(facility.getCAFReference());
		lc.setParent(item);
		lc = new Listcell(facility.getCustCIF());
		lc.setParent(item);
		lc = new Listcell(DateUtil.formatToLongDate(facility.getStartDate()));
		lc.setParent(item);
		lc = new Listcell(DateUtil.formatToLongDate(facility.getNextReviewDate()));
		lc.setParent(item);
		lc = new Listcell(facility.getPresentingUnit());
		lc.setParent(item);
		lc = new Listcell(facility.getCountryOfDomicile());
		lc.setParent(item);
		lc = new Listcell(facility.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(facility.getRecordType()));
		lc.setParent(item);
		item.setAttribute("id", facility.getId());
		ComponentsCtrl.applyForward(item, "onDoubleClick=onFacilityItemDoubleClicked");
	}
}