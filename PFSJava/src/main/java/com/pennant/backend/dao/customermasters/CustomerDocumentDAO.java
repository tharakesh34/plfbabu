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
 * * FileName : CustomerDocumentDAO.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 06-05-2011 * * Modified
 * Date : 06-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 06-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.customermasters;

import java.util.List;

import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.customermasters.ExternalDocument;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennanttech.pff.core.TableType;

/**
 * DAO methods declaration for the <b>CustomerDocument model</b> class.<br>
 * 
 */
public interface CustomerDocumentDAO {
	CustomerDocument getCustomerDocumentById(long id, String docType, String type);

	List<CustomerDocument> getCustomerDocumentByCustomer(final long id, String type);

	void update(CustomerDocument customerDocument, String type);

	void delete(CustomerDocument customerDocument, String type);

	void deleteByCustomer(long custId, String type);

	long save(CustomerDocument customerDocument, String type);

	DocumentDetails getCustDocByCustAndDocType(final long custId, String docType, String view);

	DocumentDetails getCustDocByCustAndDocType(final long docId, String view);

	List<DocumentDetails> getCustDocByCustId(long custId, String type);

	List<DocumentDetails> getCustDocListByDocTypes(long custId, List<String> docTypeList, String type);

	List<CustomerDocument> getCustomerDocumentByCustomerId(final long custId);

	boolean isDuplicateTitle(long custId, String custDocCategory, String custDocTitle);

	int getDocTypeCount(String docType);

	int getVersion(long custId, String docType);

	int getCustCountryCount(String countryCode);

	List<String> getDuplicateDocByTitle(String docCategory, String docNumber);

	int updateDocURI(String docURI, long id, TableType tableType);

	long save(ExternalDocument externalDocument, String type);

	List<ExternalDocument> getExternalDocuments(long bankId, String type);

	boolean getCustomerDocExists(long custId, String docType);

	List<Customer> getCustIdByDocTitle(String custDocTitle);
}
