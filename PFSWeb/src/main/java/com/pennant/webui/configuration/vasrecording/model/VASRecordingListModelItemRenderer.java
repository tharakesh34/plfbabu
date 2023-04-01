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
 * * FileName : VASRecordingListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 02-12-2016
 * * * Modified Date : 02-12-2016 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 02-12-2016 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.configuration.vasrecording.model;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.configuration.VASRecording;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennanttech.pennapps.core.util.DateUtil;

/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class VASRecordingListModelItemRenderer implements ListitemRenderer<VASRecording>, Serializable {

	private static final long serialVersionUID = 1L;

	@Override
	public void render(Listitem item, VASRecording vASRecording, int count) {

		Listcell lc;
		lc = new Listcell(vASRecording.getProductCode());
		lc.setParent(item);
		lc = new Listcell(PennantApplicationUtil.getLabelDesc(vASRecording.getPostingAgainst(),
				PennantStaticListUtil.getRecAgainstTypes()));
		lc.setParent(item);
		lc = new Listcell(String.valueOf(vASRecording.getPrimaryLinkRef()));
		lc.setParent(item);
		lc = new Listcell(vASRecording.getVasReference());
		lc.setParent(item);
		lc = new Listcell(String.valueOf(vASRecording.getFee()));
		lc.setParent(item);
		lc = new Listcell(PennantApplicationUtil.getLabelDesc(vASRecording.getFeePaymentMode(),
				PennantStaticListUtil.getFeeTypes()));
		lc.setParent(item);
		lc = new Listcell(DateUtil.format(vASRecording.getValueDate(), PennantConstants.dateFormat));
		lc.setParent(item);
		lc = new Listcell(DateUtil.format(vASRecording.getAccrualTillDate(), PennantConstants.dateFormat));
		lc.setParent(item);
		lc = new Listcell(DateUtil.format(vASRecording.getRecurringDate(), PennantConstants.dateFormat));
		lc.setParent(item);
		lc = new Listcell(vASRecording.getDsaId());
		lc.setParent(item);
		lc = new Listcell(vASRecording.getDmaId());
		lc.setParent(item);
		lc = new Listcell(vASRecording.getFulfilOfficerId());
		lc.setParent(item);
		lc = new Listcell(vASRecording.getReferralId());
		lc.setParent(item);

		String vasStatus = "";
		if (StringUtils.equals(vASRecording.getVasStatus(), "C")) {
			vasStatus = Labels.getLabel("label_VasStatus_Cancel");
		} else {
			if (StringUtils.isEmpty(vASRecording.getRecordType())) {
				vasStatus = Labels.getLabel("label_VasStatus_Approved");
			}
		}

		lc = new Listcell(vasStatus);
		if (StringUtils.equals(vASRecording.getVasStatus(), "C")) {
			lc.setStyle("color:red");
		}
		lc.setParent(item);
		lc = new Listcell(vASRecording.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(vASRecording.getRecordType()));
		lc.setParent(item);
		item.setAttribute("vasRecording", vASRecording);
		ComponentsCtrl.applyForward(item, "onDoubleClick=onVASRecordingItemDoubleClicked");
	}
}