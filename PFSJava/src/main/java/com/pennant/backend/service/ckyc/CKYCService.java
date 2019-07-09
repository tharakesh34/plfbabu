
package com.pennant.backend.service.ckyc;

import java.util.List;

import com.pennant.backend.model.cky.CKYCDtl20;
import com.pennant.backend.model.cky.CKYCDtl30;
import com.pennant.backend.model.cky.CKYCDtl60;
import com.pennant.backend.model.cky.CKYCDtl70;

/**
 * Service declaration for methods that depends on <b>Country</b>.<br>
 * 
 */
public interface CKYCService {
	List<Long> getId();

	boolean prepareData(List<Long> id);

	void updateCkycNo(String ckycNo, String batchNo, String rowNo);

	CKYCDtl20 getDetails20(long custId);

	List<CKYCDtl30> getDetails30(long custId, String ckycNo);

	List<CKYCDtl60> getDetails60(long custId, String ckycNo);

	List<CKYCDtl70> getDetails70(long custId, String ckycNo);

}