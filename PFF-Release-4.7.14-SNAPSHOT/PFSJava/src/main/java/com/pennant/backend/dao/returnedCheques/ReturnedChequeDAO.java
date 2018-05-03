package com.pennant.backend.dao.returnedCheques;

import java.util.List;

import com.pennant.backend.dao.impl.BasicCrudDao;
import com.pennant.backend.model.returnedcheques.ReturnedChequeDetails;
import com.pennant.backend.model.returnedcheques.ReturnedCheques;
import com.pennanttech.pff.core.TableType;

/**
 * Data access layer for <code>Academic</code> with set of CRUD operations.
 * 
 * @param <T>
 */
public interface ReturnedChequeDAO extends BasicCrudDao<ReturnedChequeDetails>{
	ReturnedChequeDetails getReturnedChequeById(String custCIF,String chequeNo,String type);
	
	List<ReturnedCheques> fetchReturnedCheques(ReturnedCheques  returnedCheques );

	/**
	 * Checks whether another record exists with the key attributes in the specified table type.
	 * 
	 * @param chequeNo
	 *            ChequeNo of the returnedChequeDetails.
	 * @param custCIF
	 *            CustCIF of the returnedChequeDetails.
	 * @param tableType
	 *            The type of the table.
	 * @return true if the record exists.
	 */
	boolean isDuplicateKey(String chequeNo, String custCIF, TableType tableType);
}
