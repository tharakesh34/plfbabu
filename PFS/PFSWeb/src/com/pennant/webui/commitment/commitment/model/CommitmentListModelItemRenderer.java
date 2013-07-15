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
 * FileName    		:  CommitmentListModelItemRenderer.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  25-03-2013    														*
 *                                                                  						*
 * Modified Date    :  25-03-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 25-03-2013       Pennant	                 0.1                                            * 
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

package com.pennant.webui.commitment.commitment.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.commitment.Commitment;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class CommitmentListModelItemRenderer implements ListitemRenderer<Commitment>, Serializable {

	private static final long	serialVersionUID	= 1L;

	@Override
	public void render(Listitem item, Commitment commitment, int count) throws Exception {
			Listcell lc;
			lc = new Listcell(String.valueOf(commitment.getCustCIF()));
			lc.setParent(item);
			lc = new Listcell(commitment.getCmtReference());
			lc.setParent(item);
			lc = new Listcell(commitment.getCmtBranch());
			lc.setParent(item);
			lc = new Listcell(commitment.getCmtCcy());
			lc.setParent(item);
			lc = new Listcell(commitment.getCmtAccount());
			lc.setParent(item);
			lc = new Listcell(PennantApplicationUtil.formateDate(commitment.getCmtExpDate(), PennantConstants.dateFormat));
			lc.setParent(item);
			lc = new Listcell(PennantApplicationUtil.amountFormate(commitment.getCmtAmount(), commitment.getCcyEditField()));
			lc.setParent(item);
			lc = new Listcell(PennantApplicationUtil.amountFormate(commitment.getCmtUtilizedAmount(), commitment.getCcyEditField()));
			lc.setParent(item);
			lc = new Listcell(PennantApplicationUtil.amountFormate(commitment.getCmtAvailable(), commitment.getCcyEditField()));
			lc.setParent(item);
			lc = new Listcell(PennantApplicationUtil.formateDate(commitment.getCmtStartDate(), PennantConstants.dateFormat));
			lc.setParent(item);
			lc = new Listcell(commitment.getRecordStatus());
			lc.setParent(item);
			lc = new Listcell(PennantJavaUtil.getLabel(commitment.getRecordType()));
			lc.setParent(item);
			item.setAttribute("data", commitment);
			ComponentsCtrl.applyForward(item, "onDoubleClick=onCommitmentItemDoubleClicked");
		}
}