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
 * FileName    		:  AccountTypeListModelItemRenderer.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  26-05-2011    														*
 *                                                                  						*
 * Modified Date    :  26-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-05-2011       Pennant	                 0.1                                            * 
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

package com.pennant.webui.rmtmasters.accounttype.model;

import java.io.Serializable;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.rmtmasters.AccountType;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.PennantAppUtil;

/**
 * Item renderer for listItems in the listBox.
 * 
 */
public class AccountTypeListModelItemRenderer implements ListitemRenderer<AccountType>, Serializable {

	private static final long serialVersionUID = 4843791737029589915L;

	public AccountTypeListModelItemRenderer() {

	}

	@Override
	public void render(Listitem item, AccountType accountType, int count) throws Exception {

		Listcell lc;
		lc = new Listcell(accountType.getAcType());
		lc.setParent(item);
		lc = new Listcell(accountType.getAcTypeDesc());
		lc.setParent(item);
		/*
		 * lc = new Listcell(accountType.getAcHeadCode()); lc.setParent(item);
		 */
		lc = new Listcell(PennantAppUtil.getlabelDesc(accountType.getAcPurpose(),
				PennantStaticListUtil.getAccountPurpose()));
		lc.setParent(item);
		lc = new Listcell();
		final Checkbox cbIsInternalAc = new Checkbox();
		cbIsInternalAc.setDisabled(true);
		cbIsInternalAc.setChecked(accountType.isInternalAc());
		lc.appendChild(cbIsInternalAc);
		lc.setParent(item);
		lc = new Listcell();
		final Checkbox cbIsCustSysAc = new Checkbox();
		cbIsCustSysAc.setDisabled(true);
		cbIsCustSysAc.setChecked(accountType.isCustSysAc());
		lc.appendChild(cbIsCustSysAc);
		lc.setParent(item);
		lc = new Listcell();
		final Checkbox cbAcTypeIsActive = new Checkbox();
		cbAcTypeIsActive.setDisabled(true);
		cbAcTypeIsActive.setChecked(accountType.isAcTypeIsActive());
		lc.appendChild(cbAcTypeIsActive);
		lc.setParent(item);
		lc = new Listcell(accountType.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(accountType.getRecordType()));
		lc.setParent(item);

		item.setAttribute("id", accountType.getId());

		ComponentsCtrl.applyForward(item, "onDoubleClick=onAccountTypeItemDoubleClicked");
	}
}