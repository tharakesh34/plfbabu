
package com.pennant.backend.dao.ckyc;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import com.pennant.backend.model.cky.CKYCDtl20;
import com.pennant.backend.model.cky.CKYCDtl30;
import com.pennant.backend.model.cky.CKYCDtl60;
import com.pennant.backend.model.cky.CKYCDtl70;
import com.pennant.backend.model.cky.CKYCLog;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.customermasters.CustomerEMail;
import com.pennant.backend.model.customermasters.CustomerPhoneNumber;

/**
 * DAO methods implementation for the <b>Country model</b> class.<br>
 * 
 */
public interface CKYCDAO {

	Customer getCustomerDetail(long id);

	int saveFile(CKYCLog file);

	int updatNameFlag(CKYCLog file);

	int updatPersonalFlag(CKYCLog file);

	int addressUpdateFlag(CKYCLog file);

	int phoneUpdateFlag(CKYCLog file);

	int emailUpdateFlag(CKYCLog file);

	int docUpdateFlag(CKYCLog file);

	String getCkycNo(long custId);

	List<CustomerDocument> getPofAddr(long custId);

	List<Customer> getId();

	CKYCDtl20 getDtl20(long custId);

	List<CKYCDtl30> getDtls30(long custId);

	List<CKYCDtl60> getDtl60(long custId);

	List<CKYCDtl70> getDtl70(long custId);

	int saveDtl20(CKYCDtl20 dtl20);

	int saveDtl30(CKYCDtl30 dtl30);

	int saveDtl60(CKYCDtl60 dtl60);

	int saveDtl70(CKYCDtl70 dtl70);

	List<CustomerAddres> getCustomerAddresById(long custId, String ckycNo);

	List<CustomerPhoneNumber> getCustomerPhoneNumberById(long custId, String ckycNo);

	List<CustomerEMail> getCustomerEmailById(long custId, String ckycNo);

	List<CustomerDocument> getcustDocsByCustId(long custId, String ckycNo);

	CKYCLog applicantNameFlag(long custId, String ckycNo);

	CKYCLog personalDetailFlag(long custId, String ckycNo);

	int addressDetailFlag(long custId, String ckycNo);

	int contactFlag(long custId, String ckycNo);

	int emailFlag(long custId, String ckycNo);

	int imgDtlFlag(long custId, String ckycNo);

	long getBatchNO();

	String getCode(String masterType, String kyeType);

	boolean cleanData();

	int updateCkycNo(String ckycNo, String batchNo, String rowNo);

	int getCustId(String ckycNo);

	void updateCustomerWithCKycNo(int custId, String ckycNo);

	String getLeadIdByCustId(long custId);

	Map<String, Object> getcKYCdocMaster();

	Timestamp getLastMntOn(String custId, String ckycNo);
}
