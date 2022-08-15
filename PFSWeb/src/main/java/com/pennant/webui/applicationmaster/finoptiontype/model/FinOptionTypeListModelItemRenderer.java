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
 * * FileName : OptionTypeListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 22-02-2019 *
 * * Modified Date : 22-02-2019 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 22-02-2019 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.applicationmaster.finoptiontype.model;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.Property;
import com.pennant.backend.model.applicationmaster.FinOptionType;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pff.staticlist.AppStaticList;

/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class FinOptionTypeListModelItemRenderer implements ListitemRenderer<FinOptionType>, Serializable {

	private static final long serialVersionUID = 1L;

	private transient List<Property> listFrequencies = AppStaticList.getFrequencies();

	public FinOptionTypeListModelItemRenderer() {
		super();
	}

	@Override
	public void render(Listitem item, FinOptionType finOptionType, int count) {

		Listcell lc;
		lc = new Listcell(finOptionType.getCode());
		lc.setParent(item);
		lc = new Listcell(finOptionType.getDescription());
		lc.setParent(item);
		lc = new Listcell(finOptionType.getOptionType());
		lc.setParent(item);
		lc = new Listcell(getFrequencies(finOptionType.getFrequency()));
		lc.setParent(item);
		lc = new Listcell(finOptionType.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(finOptionType.getRecordType()));
		lc.setParent(item);
		item.setAttribute("id", finOptionType.getId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onOptionTypeItemDoubleClicked");
	}

	private String getFrequencies(String frequency) {
		for (Property property : listFrequencies) {
			if (StringUtils.equals(property.getKey().toString(), frequency)) {
				return frequency + " - " + property.getValue();
			}
		}

		return "";
	}

}