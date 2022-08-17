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
 * * FileName : TDSReceivableDetailListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date :
 * 03-09-2020 * * Modified Date : 03-09-2020 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 03-09-2020 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.tds.receivables.tdsreceivablestxn.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.tds.receivables.TdsReceivable;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennanttech.pennapps.core.util.DateUtil;

/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class TdsReceivablesTxnListModelItemRenderer implements ListitemRenderer<TdsReceivable>, Serializable {

	private static final long serialVersionUID = 1L;
	private int ccyFormatter = 2;

	public TdsReceivablesTxnListModelItemRenderer() {
		super();
	}

	@Override
	public void render(Listitem item, TdsReceivable tdsReceivable, int count) throws Exception {

		Listcell lc;

		lc = new Listcell(tdsReceivable.getTanNumber());
		lc.setParent(item);
		lc = new Listcell(tdsReceivable.getTanHolderName());
		lc.setParent(item);
		lc = new Listcell(tdsReceivable.getAssessmentYear());
		lc.setParent(item);
		lc.setStyle("text-align:center;");
		lc = new Listcell(tdsReceivable.getCertificateNumber());
		lc.setStyle("text-align:center;");
		lc.setParent(item);
		lc = new Listcell(DateUtil.formatToLongDate(tdsReceivable.getCertificateDate()));
		lc.setStyle("text-align:center;");

		lc.setParent(item);
		lc = new Listcell(

				PennantApplicationUtil.amountFormate(tdsReceivable.getCertificateAmount(), ccyFormatter));
		lc.setStyle("text-align:right;");
		lc.setParent(item);
		lc = new Listcell(PennantApplicationUtil.amountFormate(tdsReceivable.getUtilizedAmount(), ccyFormatter));
		lc.setStyle("text-align:right;");
		lc.setParent(item);
		lc = new Listcell(PennantApplicationUtil.amountFormate(tdsReceivable.getBalanceAmount(), ccyFormatter));
		lc.setStyle("text-align:right;");
		lc.setParent(item);
		lc = new Listcell(tdsReceivable.getRecordStatus());
		lc.setParent(item);
		lc.setParent(item);
		item.setAttribute("data", tdsReceivable);

		ComponentsCtrl.applyForward(item, "onDoubleClick=onTdsReceivableTxnItemDoubleClicked");
	}
}