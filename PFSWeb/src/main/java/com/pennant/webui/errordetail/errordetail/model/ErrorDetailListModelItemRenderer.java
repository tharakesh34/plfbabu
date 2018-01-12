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
 * FileName    		:  ErrorDetailListModelItemRenderer.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  05-05-2016    														*
 *                                                                  						*
 * Modified Date    :  05-05-2016    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 05-05-2016       Pennant	                 0.1                                            * 
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

package com.pennant.webui.errordetail.errordetail.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;

/**
 * Item renderer for listitems in the listbox.
 * 
 */
public class ErrorDetailListModelItemRenderer implements ListitemRenderer<ErrorDetails>, Serializable {

	private static final long serialVersionUID = 1L;

	@Override
	public void render(Listitem item, ErrorDetails errorDetail, int count) throws Exception {

		Listcell lc;
		lc = new Listcell(errorDetail.getErrorCode());
		lc.setParent(item);
		lc = new Listcell(errorDetail.getErrorLanguage());
		lc.setParent(item);
		lc = new Listcell(PennantStaticListUtil.getlabelDesc(errorDetail.getErrorSeverity(),
				PennantStaticListUtil.getSysParamType()));
		lc.setParent(item);
		lc = new Listcell(errorDetail.getErrorMessage());
		lc.setParent(item);
		lc = new Listcell(errorDetail.getErrorExtendedMessage());
		lc.setParent(item);
		lc = new Listcell(errorDetail.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(errorDetail.getRecordType()));
		lc.setParent(item);

		item.setAttribute("id", errorDetail.getId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onErrorDetailItemDoubleClicked");
	}
}