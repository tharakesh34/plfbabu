/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *																							*
 * FileName    		:  InstrumentwiseLimitListModelItemRenderer.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  18-01-2018    														*
 *                                                                  						*
 * Modified Date    :  18-01-2018    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 18-01-2018       PENNANT	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
*/
package com.pennant.webui.applicationmaster.instrumentwiselimit.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.applicationmaster.InstrumentwiseLimit;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class InstrumentwiseLimitListModelItemRenderer implements ListitemRenderer<InstrumentwiseLimit>, Serializable {

	private static final long serialVersionUID = 1L;

	public InstrumentwiseLimitListModelItemRenderer() {
		super();
	}

	@Override
	public void render(Listitem item, InstrumentwiseLimit instrumentwiseLimit, int count) {

		Listcell lc;
		lc = new Listcell(instrumentwiseLimit.getInstrumentMode());
		lc.setParent(item);

		lc = new Listcell(PennantApplicationUtil.amountFormate(instrumentwiseLimit.getPaymentMinAmtperTrans(),
				PennantConstants.defaultCCYDecPos));
		lc.setParent(item);

		lc = new Listcell(PennantApplicationUtil.amountFormate(instrumentwiseLimit.getPaymentMaxAmtperTran(),
				PennantConstants.defaultCCYDecPos));
		lc.setParent(item);

		lc = new Listcell(PennantApplicationUtil.amountFormate(instrumentwiseLimit.getPaymentMaxAmtperDay(),
				PennantConstants.defaultCCYDecPos));
		lc.setParent(item);

		lc = new Listcell(PennantApplicationUtil.amountFormate(instrumentwiseLimit.getReceiptMinAmtperTran(),
				PennantConstants.defaultCCYDecPos));
		lc.setParent(item);

		lc = new Listcell(PennantApplicationUtil.amountFormate(instrumentwiseLimit.getReceiptMaxAmtperTran(),
				PennantConstants.defaultCCYDecPos));
		lc.setParent(item);

		lc = new Listcell(PennantApplicationUtil.amountFormate(instrumentwiseLimit.getReceiptMaxAmtperDay(),
				PennantConstants.defaultCCYDecPos));
		lc.setParent(item);

		lc = new Listcell(instrumentwiseLimit.getRecordStatus());
		lc.setParent(item);

		lc = new Listcell(PennantJavaUtil.getLabel(instrumentwiseLimit.getRecordType()));
		lc.setParent(item);

		item.setAttribute("id", instrumentwiseLimit.getId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onInstrumentwiseLimitItemDoubleClicked");
	}
}