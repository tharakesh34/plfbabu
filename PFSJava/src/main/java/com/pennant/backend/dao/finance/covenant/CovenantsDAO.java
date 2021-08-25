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
 * * FileName : FinCovenantTypeDAO.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 14-08-2013 * * Modified
 * Date : 14-08-2013 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 14-08-2013 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.finance.covenant;

import java.util.List;

import com.pennant.backend.model.finance.covenant.Covenant;
import com.pennant.backend.model.finance.covenant.CovenantDocument;
import com.pennanttech.pff.core.TableType;

public interface CovenantsDAO {

	Covenant getCovenant(long id, String module, TableType tableType);

	List<Covenant> getCovenants(String finReference, String module, TableType tableType);

	List<CovenantDocument> getCovenantDocuments(long covenantId, TableType tableType);

	void delete(Covenant finCovenantType, TableType tableType);

	String save(Covenant aFinCovenantType, TableType tableType);

	void saveDocuments(List<CovenantDocument> covenantDocuments, TableType tableType);

	void update(Covenant aFinCovenantType, TableType tableType);

	void updateDocuments(List<CovenantDocument> covenantDocuments, TableType tableType);

	boolean isExists(Covenant finCovenantType, TableType tableType);

	void deleteDocuments(List<CovenantDocument> documents, TableType tableType);

	List<Covenant> getCovenantsAlertList();

	List<Covenant> getCovenants(String finReference);

	void deleteDocumentByDocumentId(Long documentId, String tableType);

}