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
 * * FileName : EmployerDetailListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date :
 * 31-07-2013 * * Modified Date : 31-07-2013 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 31-07-2013 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.systemmasters.employerdetail.model;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.systemmasters.EmployerDetail;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.util.DateUtil;

/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class EmployerDetailListModelItemRenderer implements ListitemRenderer<EmployerDetail>, Serializable {

	private static final long serialVersionUID = 1L;

	public EmployerDetailListModelItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, EmployerDetail employerDetail, int count) {

		Listcell lc;
		lc = new Listcell(String.valueOf(employerDetail.getEmployerId()));
		lc.setParent(item);
		lc = new Listcell(employerDetail.getEmpName());
		lc.setParent(item);
		lc = new Listcell(employerDetail.getLovDescIndustryDesc());
		lc.setParent(item);
		lc = new Listcell(DateUtil.formatToLongDate(employerDetail.getEstablishDate()));
		lc.setParent(item);
		lc = new Listcell(employerDetail.getEmpPOBox());
		lc.setParent(item);
		lc = new Listcell(StringUtils.trimToEmpty(employerDetail.getLovDescCityName()));
		lc.setParent(item);
		lc = new Listcell(employerDetail.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(employerDetail.getRecordType()));
		lc.setParent(item);

		item.setAttribute("id", employerDetail.getId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onEmployerDetailItemDoubleClicked");
	}
}