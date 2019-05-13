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
 * FileName    		:  UploadListModelItemRenderer.java                                     * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  04-10-2018    														*
 *                                                                  						*
 * Modified Date    :  04-10-2018    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 04-10-2018       Pennant	                 0.1                                            * 
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

package com.pennant.webui.finance.upload.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.expenses.UploadHeader;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for list items in the list box.
 * 
 */
public class UploadListModelItemRenderer implements ListitemRenderer<UploadHeader>, Serializable {

	private static final long serialVersionUID = 6352065299727172054L;

	public UploadListModelItemRenderer() {

	}

	@Override
	public void render(Listitem item, UploadHeader uploadHeader, int count) throws Exception {

		Listcell lc;
		//File Name
		lc = new Listcell(uploadHeader.getFileName());
		lc.setParent(item);
		// Transaction Date
		lc = new Listcell(DateUtility.formatToLongDate(uploadHeader.getTransactionDate()));
		lc.setParent(item);
		//Success count
		//lc = new Listcell(String.valueOf(uploadHeader.getSuccessCount()));
		//lc.setParent(item);
		//failed count
		//lc = new Listcell(String.valueOf(uploadHeader.getFailedCount()));
		//lc.setParent(item);
		// Total count
		lc = new Listcell(String.valueOf(uploadHeader.getTotalRecords()));
		lc.setParent(item);
		//record status
		lc = new Listcell(uploadHeader.getRecordStatus());
		lc.setParent(item);
		//record type
		lc = new Listcell(PennantJavaUtil.getLabel(uploadHeader.getRecordType()));
		lc.setParent(item);

		item.setAttribute("id", uploadHeader.getUploadId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onUploadItemDoubleClicked");
	}
}