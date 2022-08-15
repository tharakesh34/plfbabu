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
 * * FileName : TdsReceivableListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date :
 * 03-09-2020 * * Modified Date : 03-09-2020 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 03-09-2020 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.tds.receivables.tdsreceivable.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.tds.receivables.TdsReceivable;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.util.DateUtil;

/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class TdsReceivableListModelItemRenderer implements ListitemRenderer<TdsReceivable>, Serializable {

	private static final long serialVersionUID = 1L;

	public TdsReceivableListModelItemRenderer() {
		super();
	}

	@Override
	public void render(Listitem item, TdsReceivable tdsReceivable, int count) throws Exception {

		Listcell lc;
		lc = new Listcell(String.valueOf(tdsReceivable.getTanNumber()));
		lc.setStyle("text-align:left;");
		lc.setParent(item);
		lc = new Listcell(tdsReceivable.getTanHolderName());
		lc.setStyle("text-align:center;");
		lc.setParent(item);
		lc = new Listcell(String.valueOf(tdsReceivable.getCertificateNumber()));
		lc.setStyle("text-align:center;");
		lc.setParent(item);
		lc = new Listcell(DateUtil.formatToLongDate(tdsReceivable.getCertificateDate()));
		lc.setStyle("text-align:center;");
		lc.setParent(item);
		lc = new Listcell(tdsReceivable.getAssessmentYear());
		lc.setStyle("text-align:center;");
		lc.setParent(item);
		lc = new Listcell(DateUtil.formatToLongDate(tdsReceivable.getDateOfReceipt()));
		lc.setStyle("text-align:center;");
		lc.setParent(item);
		lc = new Listcell(String.valueOf(tdsReceivable.getCertificateQuarter()));
		lc.setStyle("text-align:center;");
		lc.setParent(item);
		lc = new Listcell(tdsReceivable.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(tdsReceivable.getRecordType()));
		lc.setParent(item);
		item.setAttribute("iD", tdsReceivable.getId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onTdsReceivableItemDoubleClicked");
	}
}