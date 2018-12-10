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
 * FileName    		:  InsuranceRebookingListModelItemRenderer.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :    																	*
 *                                                                  						*
 * Modified Date    :      																	*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 *                                            * 
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

package com.pennant.webui.financemanagement.insurance.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.configuration.VASRecording;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.PennantAppUtil;

/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class InsuranceRebookingListModelItemRenderer implements ListitemRenderer<VASRecording>, Serializable {

	private static final long serialVersionUID = 1L;

	@Override
	public void render(Listitem item, VASRecording vASRecording, int count) throws Exception {

		Listcell lc;
		lc = new Listcell(vASRecording.getProductCode());
		lc.setParent(item);
		lc = new Listcell(PennantAppUtil.getlabelDesc(vASRecording.getPostingAgainst(),
				PennantStaticListUtil.getRecAgainstTypes()));
		lc.setParent(item);
		lc = new Listcell(vASRecording.getVasReference());
		lc.setParent(item);
		lc = new Listcell(String.valueOf(vASRecording.getPrimaryLinkRef()));
		lc.setParent(item);
		lc = new Listcell(vASRecording.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(vASRecording.getRecordType()));
		lc.setParent(item);
		item.setAttribute("vASRecording", vASRecording);
		ComponentsCtrl.applyForward(item, "onDoubleClick=onInsuranceRebookingItemDoubleClicked");
	}
}