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
 * FileName    		:  CustomerDocumentListModelItemRenderer.java                                                   * 	  
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

package com.pennant.webui.customermasters.customerdocument.model;

import java.io.Serializable;
import java.util.List;

import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.util.PennantJavaUtil;

/**
 * Item renderer for listItems in the listBox.
 * 
 */
public class CustomerDocumentListModelItemRenderer implements ListitemRenderer<CustomerDocument>, Serializable {
	private static final long serialVersionUID = -2861700164113321338L;
	
	private List<DocumentDetails> verificationDocuments;

	public CustomerDocumentListModelItemRenderer(List<DocumentDetails> verificationDocuments) {
		this.verificationDocuments = verificationDocuments;
	}
	
	@Override
	public void render(Listitem item, CustomerDocument customerDocument, int count) throws Exception {

		Listcell lc;
		lc = new Listcell(customerDocument.getLovDescCustCIF());
		lc.setParent(item);
		lc = new Listcell(customerDocument.getCustDocType() + "-" + customerDocument.getLovDescCustDocCategory());
		lc.setParent(item);
		lc = new Listcell(customerDocument.getCustDocTitle());
		lc.setParent(item);
		lc = new Listcell(customerDocument.getCustDocSysName());
		lc.setParent(item);
		lc = new Listcell(customerDocument.getRecordStatus());
		lc.setParent(item);
		lc = new Listcell(PennantJavaUtil.getLabel(customerDocument.getRecordType()));
		lc.setParent(item);

		item.setAttribute("id", customerDocument.getCustID());
		item.setAttribute("docCategory", customerDocument.getCustDocCategory());

		// set the verification documents
		if (verificationDocuments != null) {
			boolean exists = false;
			for (DocumentDetails doument : verificationDocuments) {
				if (customerDocument.getCustDocCategory().equals(doument.getDocModule())) {
					exists = true;
				}
			}

			if (!exists) {
				DocumentDetails doument = new DocumentDetails();
				doument.setDocId(customerDocument.getCustID());
				doument.setDocModule(customerDocument.getCustDocCategory());
				doument.setDoumentType(com.pennanttech.pennapps.pff.verification.DocumentType.CUSTOMER.getKey());
				verificationDocuments.add(doument);
			}
		}

		ComponentsCtrl.applyForward(item, "onDoubleClick=onCustomerDocumentItemDoubleClicked");

	}
}