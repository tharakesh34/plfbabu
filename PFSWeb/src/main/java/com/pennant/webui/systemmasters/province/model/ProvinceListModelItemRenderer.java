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
 * * FileName : ProvinceListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 26-05-2011 * *
 * Modified Date : 26-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 26-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.systemmasters.province.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.systemmasters.Province;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for listItems in the listBox.
 * 
 */
public class ProvinceListModelItemRenderer implements ListitemRenderer<Province>, Serializable {

	private static final long serialVersionUID = -3187829903184756130L;

	public ProvinceListModelItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, Province province, int count) {

		Listcell lc;
		lc = new Listcell(province.getCPCountry());
		lc.setParent(item);
		lc = new Listcell(province.getLovDescCPCountryName());
		lc.setParent(item);
		lc = new Listcell(province.getCPProvince());
		lc.setParent(item);
		lc = new Listcell(province.getCPProvinceName());
		lc.setParent(item);
		lc = new Listcell(province.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(province.getRecordType()));
		lc.setParent(item);

		item.setAttribute("cpCountry", province.getCPCountry());
		item.setAttribute("cpProvince", province.getCPProvince());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onProvinceItemDoubleClicked");
	}
}