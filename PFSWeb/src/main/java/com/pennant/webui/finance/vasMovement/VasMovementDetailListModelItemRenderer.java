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
 * * FileName : CheckListDetailListModelItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date :
 * 12-12-2011 * * Modified Date : 12-12-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 12-12-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.finance.vasMovement;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listgroup;
import org.zkoss.zul.Listgroupfoot;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.backend.model.finance.VasMovementDetail;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;

/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class VasMovementDetailListModelItemRenderer implements ListitemRenderer<VasMovementDetail>, Serializable {

	private static final long serialVersionUID = -7502706317125873983L;

	public VasMovementDetailListModelItemRenderer() {

	}

	@Override
	public void render(Listitem item, VasMovementDetail vasMovementDetail, int count) {

		if (item instanceof Listgroup) {
			Listcell cell = new Listcell(vasMovementDetail.getVasReference());
			cell.setStyle("font-weight:bold;color:##FF4500;");
			item.appendChild(cell);
		} else if (item instanceof Listgroupfoot) {
			Listcell cell = new Listcell("");
			cell.setSpan(9);
			item.appendChild(cell);
		} else {

			Listcell lc;
			lc = new Listcell(String.valueOf(vasMovementDetail.getVasMovementDetailId()));
			lc.setParent(item);

			lc = new Listcell(String.valueOf(vasMovementDetail.getVasProduct()));
			lc.setParent(item);

			lc = new Listcell(String.valueOf(vasMovementDetail.getVasProvider()));
			lc.setParent(item);

			lc = new Listcell(CurrencyUtil.format(vasMovementDetail.getVasAmount(), PennantConstants.defaultCCYDecPos));
			lc.setParent(item);

			lc = new Listcell(
					DateUtil.format(vasMovementDetail.getMovementDate(), DateFormat.LONG_DATE.getPattern()));
			lc.setParent(item);

			lc = new Listcell(
					CurrencyUtil.format(vasMovementDetail.getMovementAmt(), PennantConstants.defaultCCYDecPos));
			lc.setParent(item);

			lc = new Listcell(vasMovementDetail.getRecordStatus());
			lc.setParent(item);

			lc = new Listcell(PennantJavaUtil.getLabel(vasMovementDetail.getRecordType()));
			lc.setParent(item);

			item.setAttribute("data", vasMovementDetail);
			ComponentsCtrl.applyForward(item, "onDoubleClick=onVasMovementDetailItemDoubleClicked");
		}
	}
}