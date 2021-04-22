
package com.pennant.backend.service.ckyc;

import java.util.List;

/**
 * Service declaration for methods that depends on <b>Country</b>.<br>
 * 
 */
public interface CKYCService {
	List<Long> getId();

	boolean prepareData(List<Long> id);

	void updateCkycNo(String ckycNo, String batchNo, String rowNo);

	int getCustId(String ckycNo);

	void updateCustomerWithCKycNo(int custId, String ckycNo);

}