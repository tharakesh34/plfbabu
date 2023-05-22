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
 * * FileName : LegalDetailListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 16-06-2018
 * * * Modified Date : 16-06-2018 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 16-06-2018 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.legal.legaldetail.model;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.legal.LegalDetail;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.util.DateUtil;

/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class LegalDetailListModelItemRenderer implements ListitemRenderer<LegalDetail>, Serializable {

	private static final long serialVersionUID = 1L;

	public LegalDetailListModelItemRenderer() {
		super();
	}

	@Override
	public void render(Listitem item, LegalDetail legalDetail, int count) {

		Listcell lc;
		lc = new Listcell(legalDetail.getLoanReference());
		lc.setParent(item);

		lc = new Listcell(StringUtils.trimToEmpty(legalDetail.getApplicantName()));
		lc.setParent(item);

		lc = new Listcell(StringUtils.trimToEmpty(legalDetail.getRequestStage()));
		lc.setParent(item);

		lc = new Listcell(legalDetail.getCollateralReference());
		lc.setParent(item);

		lc = new Listcell(legalDetail.getLegalReference());
		lc.setParent(item);

		lc = new Listcell(legalDetail.getBranchDesc());
		lc.setParent(item);

		lc = new Listcell(DateUtil.format(legalDetail.getLegalDate(), PennantConstants.dateFormat));
		lc.setParent(item);

		if (legalDetail.isActive()) {
			lc = new Listcell("Active");
			lc.setStyle("font-weight:bold;color:#00F566;");
			lc.setParent(item);
		} else {
			lc = new Listcell("Inactive");
			lc.setStyle("font-weight:bold;color:#E37114;");
			lc.setParent(item);
		}

		lc = new Listcell(legalDetail.getRecordStatus());
		lc.setParent(item);

		lc = new Listcell(PennantJavaUtil.getLabel(legalDetail.getRecordType()));
		lc.setParent(item);

		item.setAttribute("legalId", legalDetail.getLegalId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onLegalDetailItemDoubleClicked");
	}
}