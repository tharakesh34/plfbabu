package com.pennant.backend.dao.returnedCheques;

import java.util.List;

import com.pennant.backend.model.returnedcheques.ReturnedChequeDetails;
import com.pennant.backend.model.returnedcheques.ReturnedCheques;

public interface ReturnedChequeDAO {

	ReturnedChequeDetails getReturnedChequeById(String custCIF,String chequeNo,String type);
	void update(ReturnedChequeDetails returnedChequeDetails,String type);
	void delete(ReturnedChequeDetails returnedChequeDetails,String type);
	void save(ReturnedChequeDetails returnedChequeDetails,String type);
	List<ReturnedCheques> fetchReturnedCheques(ReturnedCheques  returnedCheques );

}
