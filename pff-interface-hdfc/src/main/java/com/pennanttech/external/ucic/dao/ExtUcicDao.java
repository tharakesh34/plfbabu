package com.pennanttech.external.ucic.dao;

import java.util.Date;
import java.util.List;

import com.pennanttech.external.ucic.model.ExtCustAddress;
import com.pennanttech.external.ucic.model.ExtCustDoc;
import com.pennanttech.external.ucic.model.ExtCustEmail;
import com.pennanttech.external.ucic.model.ExtCustPhones;
import com.pennanttech.external.ucic.model.ExtUcicCust;
import com.pennanttech.external.ucic.model.ExtUcicData;
import com.pennanttech.external.ucic.model.ExtUcicFinDetails;

public interface ExtUcicDao {

	long getSeqNumber(String tableName);

	public List<ExtUcicCust> fetchCustomersForCoreBankId(int fileStatus, int process_flag);

	public List<ExtUcicCust> fetchCustomersForWeeklyFile(int fileStatus);

	public ExtUcicCust fetchRecord(long finId);

	public void updateRecordProcessingFlagAndFileStatus(ExtUcicCust customer, int process_flag, int file_status);

	public void saveHistory(ExtUcicCust customer, Date latestMntDate, Date systemDate);

	public void insertRecord(ExtUcicCust customer);

	public void deleteRecord(long finId);

	public boolean isRecordExist(long finId);

	public boolean isFileProcessed(String fileName);

	public void saveResponseFile(String fileName, String fileLocation, int fileStatus, int extractionStatus,
			String errorCode, String errorMessage);

	public void updateResponseFileProcessingFlag(long id, int status);

	public void updateResponseFileExtractionFlag(long id, int status, int extraction);

	public void updateFileRecordProcessingFlag(long header_id, String custid, int status);

	public void updateUcicIdInMain(String custId, String ucicId);

	public void updateUcicIdInCustomers(String custId, String ucicId);

	public String getExistingUcicIc(String custId);

	public List<ExtUcicData> getCustBasedOnUcicId(String ucicId);

	public boolean isCustomerInMakerStage(String custId);

	public void updateFileRecordProcessingFlagAndStatus(long header_id, String custid, int status, int processFlag,
			String processDesc, int ackStatus);

	public int saveResponseFileRecordsData(List<ExtUcicData> extUcicDatas);

	public List<ExtUcicData> fetchListOfAckRecords(int status, int ack_status);

	public int updateAckFileRecordsStatus(List<ExtUcicData> extUcicDatas);

	public boolean setLoanAccNumber(ExtUcicCust c, String mandateType);

	public boolean setCustEmployerName(ExtUcicCust c, int currentEmployer);

	public List<ExtCustEmail> getCustEmails(ExtUcicCust c);

	public List<ExtCustPhones> getCustPhones(ExtUcicCust c);

	public List<ExtCustAddress> getCustAddress(ExtUcicCust c);

	public List<ExtCustDoc> getCustDocs(ExtUcicCust c);

	public List<ExtUcicFinDetails> getCustFinDetailsByCustId(long custId);

	public List<ExtUcicFinDetails> getCustomerFinDetailsByCustCif(String custCif);

	public List<ExtUcicFinDetails> getCustomerFinDetailsWithGuarantorCif(String custCif);

	public String executeDataExtractionFromSP();

	public String executeUcicRequestFileSP(String fileName);

	public String executeUcicWeeklyRequestFileSP(String fileName);

	public String executeUcicResponseFileSP(String fileName);

	public String executeUcicAckFileSP(String fileName);

	public int updateAckForFile(String fileName, int ackStatus);
}
