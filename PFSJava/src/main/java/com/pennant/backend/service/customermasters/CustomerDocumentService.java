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
 * * FileName : CustomerDocumentService.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 06-05-2011 * *
 * Modified Date : 06-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 06-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.service.customermasters;

import java.util.List;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.documentdetails.DocumentDetails;

/**
 * Service declaration for methods that depends on <b>CustomerDocument</b>.<br>
 * 
 */
public interface CustomerDocumentService {
	AuditHeader saveOrUpdate(AuditHeader auditHeader);

	CustomerDocument getCustomerDocumentById(long id, String docType);

	CustomerDocument getApprovedCustomerDocumentById(long id, String docType);

	AuditHeader delete(AuditHeader auditHeader);

	AuditHeader doApprove(AuditHeader auditHeader);

	AuditHeader doReject(AuditHeader auditHeader);

	DocumentDetails getCustDocByCustAndDocType(final long custId, String docType);

	String getCustCRCPRById(long custId, String type);

	List<CustomerDocument> getApprovedCustomerDocumentById(long id);

	int getVersion(long custId, String docType);

	AuditDetail validateCustomerDocuments(CustomerDocument customerDocument, Customer customer);

	String getDocTypeByMasterDefByCode(String masterType, String keyCode);

	boolean getCustomerDocExists(long custId, String docType);

	List<Customer> getCustIdByDocTitle(String custDocTitle);
}