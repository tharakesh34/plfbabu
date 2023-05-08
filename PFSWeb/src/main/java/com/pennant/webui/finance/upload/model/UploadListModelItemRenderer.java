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
 * * FileName : UploadListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 04-10-2018 * *
 * Modified Date : 04-10-2018 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 04-10-2018 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.finance.upload.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.expenses.UploadHeader;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.UploadConstants;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;

/**
 * Item renderer for list items in the list box.
 * 
 */
public class UploadListModelItemRenderer implements ListitemRenderer<UploadHeader>, Serializable {

	private static final long serialVersionUID = 6352065299727172054L;

	String moduleName = "";

	public UploadListModelItemRenderer(String moduleName) {
		this.moduleName = moduleName;
	}

	public UploadListModelItemRenderer() {
	    super();
	}

	@Override
	public void render(Listitem item, UploadHeader uploadHeader, int count) {

		((Listbox) item.getParent()).setMultiple(true);
		Listcell lc;
		lc = new Listcell(String.valueOf(uploadHeader.getUploadId()));
		lc.setParent(item);
		// File Name
		lc = new Listcell(uploadHeader.getFileName());
		lc.setParent(item);
		lc = new Listcell(DateUtil.format(uploadHeader.getTransactionDate(), DateFormat.LONG_DATE.getPattern()));
		lc.setParent(item);
		lc = new Listcell(uploadHeader.getUserName());
		lc.setParent(item);
		// Transaction Date
		lc = new Listcell(uploadHeader.getEntityCode());
		lc.setParent(item);
		// Success count
		lc = new Listcell(String.valueOf(uploadHeader.getSuccessCount()));
		lc.setParent(item);
		// failed count
		lc = new Listcell(String.valueOf(uploadHeader.getFailedCount()));
		lc.setParent(item);
		// Total count
		lc = new Listcell(String.valueOf(uploadHeader.getTotalRecords()));
		lc.setParent(item);
		// record status
		lc = new Listcell(uploadHeader.getRecordStatus());
		lc.setParent(item);
		// record type
		lc = new Listcell(PennantJavaUtil.getLabel(uploadHeader.getRecordType()));
		lc.setParent(item);

		item.setAttribute("data", uploadHeader);
		item.setAttribute("id", uploadHeader.getId());

		if (!(UploadConstants.MANUAL_ADVISE_APPROVER.equals(this.moduleName))) {
			ComponentsCtrl.applyForward(item, "onDoubleClick=onUploadItemDoubleClicked");
		}
	}
}