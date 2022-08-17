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

import org.apache.commons.lang3.StringUtils;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.dao.tds.receivables.TdsReceivablesTxnStatus;
import com.pennant.backend.model.tds.receivables.TdsReceivable;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennanttech.pennapps.core.util.DateUtil;

/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class TdsReceivablesTxnCancelListModelItemRenderer implements ListitemRenderer<TdsReceivable>, Serializable {

	private static final long serialVersionUID = 1L;

	private boolean enqiryModule;

	public TdsReceivablesTxnCancelListModelItemRenderer(boolean enqiryModule) {
		super();
		this.setEnqiryModule(enqiryModule);
	}

	@Override
	public void render(Listitem item, TdsReceivable tdsReceivable, int index) throws Exception {

		Listcell lc;
		lc = new Listcell(tdsReceivable.getTanNumber());
		lc.setStyle("text-align:left;");
		lc.setParent(item);
		lc = new Listcell(tdsReceivable.getTanHolderName());
		lc.setStyle("text-align:left;");
		lc.setParent(item);
		lc = new Listcell(tdsReceivable.getAssessmentYear());
		lc.setStyle("text-align:center;");
		lc.setParent(item);
		lc = new Listcell(tdsReceivable.getCertificateNumber());
		lc.setStyle("text-align:left;");
		lc.setParent(item);
		lc = new Listcell(DateUtil.formatToLongDate(tdsReceivable.getCertificateDate()));
		lc.setStyle("text-align:center;");
		lc.setParent(item);

		lc = new Listcell(PennantApplicationUtil.amountFormate(tdsReceivable.getCertificateAmount(),
				PennantConstants.defaultCCYDecPos));
		lc.setStyle("text-align:right;");
		lc.setParent(item);

		if (tdsReceivable.getTxnID() != 0) {
			lc = new Listcell(String.valueOf(tdsReceivable.getTxnID()));
		} else {
			lc = new Listcell("");
		}
		lc.setStyle("text-align:left;");
		lc.setParent(item);

		lc = new Listcell(DateUtil.formatToLongDate(tdsReceivable.getTranDate()));
		lc.setStyle("text-align:center;");
		lc.setParent(item);

		lc = new Listcell(PennantApplicationUtil.amountFormate(tdsReceivable.getUtilizedAmount(),
				PennantConstants.defaultCCYDecPos));
		lc.setStyle("text-align:right;");
		lc.setParent(item);

		lc = new Listcell(PennantApplicationUtil.amountFormate(tdsReceivable.getBalanceAmount(),
				PennantConstants.defaultCCYDecPos));
		lc.setStyle("text-align:right;");
		lc.setParent(item);

		if (enqiryModule) {
			if (StringUtils.equals(tdsReceivable.getStatus(), PennantConstants.RECEIVABLE_CANCEL) || StringUtils
					.equals(tdsReceivable.getTxnStatus(), TdsReceivablesTxnStatus.ADJUSTMENTCANCEL.getCode())) {
				lc = new Listcell("Cancelled");
			} else {
				lc = new Listcell("");
			}
			lc.setParent(item);
		}

		lc = new Listcell(tdsReceivable.getRecordStatus());
		lc.setParent(item);

		item.setAttribute("data", tdsReceivable);
		ComponentsCtrl.applyForward(item, "onDoubleClick=onTdsReceivableTxnItemDoubleClicked");

	}

	public boolean isEnqiryModule() {
		return enqiryModule;
	}

	public void setEnqiryModule(boolean enqiryModule) {
		this.enqiryModule = enqiryModule;
	}
}