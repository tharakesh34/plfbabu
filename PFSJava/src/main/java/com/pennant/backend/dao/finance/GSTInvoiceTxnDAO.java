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
 * FileName    		:  GSTInvoiceTxnDAO.java                                                * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  18-04-2018    														*
 *                                                                  						*
 * Modified Date    :  18-04-2018    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 18-04-2018       Pennant	                 0.1                                            * 
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
package com.pennant.backend.dao.finance;

import java.util.Date;
import java.util.List;

import com.pennant.backend.model.finance.GSTInvoiceTxn;
import com.pennant.backend.model.finance.SeqGSTInvoice;

public interface GSTInvoiceTxnDAO {

	long save(GSTInvoiceTxn gstInvoiceTxn);

	long saveSeqGSTInvoice(SeqGSTInvoice seqGSTInvoice);

	void updateSeqGSTInvoice(SeqGSTInvoice seqGSTInvoice);

	void updateGSTInvoiceNo(GSTInvoiceTxn gstInvoiceTxn);

	List<GSTInvoiceTxn> getGSTInvoiceTxnList();

	SeqGSTInvoice getSeqNoFromSeqGSTInvoice(SeqGSTInvoice seqGSTInvoice);

	SeqGSTInvoice getSeqGSTInvoice(SeqGSTInvoice seqGSTInvoice);

	boolean isGstInvoiceExist(String custCif, String finReference, String invoiceType, Date fromDate, Date toDate);

	void deleteSeqGSTInvoice(SeqGSTInvoice seqGSTInvoice);

	void updateSeqNo();
}