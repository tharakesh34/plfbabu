package com.pennanttech.backend.dao;

import java.sql.Timestamp;
import java.util.List;

import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.model.CustomerStaging;

public interface ExtractCustomerDataDAO {

	String getCALogicalView(TableType tableType);

	long saveDownloadheader(String processType, String downloadCode);

	void updateDownloadheader(long headerId, int count);

	List<Long> getFinApprovedCustomers(Timestamp prevTime, Timestamp curTime);

	List<Long> getFinClosedCustomers(Timestamp prevTime, Timestamp curTime, String processType);

	List<Long> getFinActiveCustomers();

	List<Long> extractCustomers(Timestamp curTime);

	void saveCustomerStaging(CustomerStaging customerStaging);

	CustomerStaging getCustomerDetailsById(long custId);

	void setCustAddressDetails(long custId, CustomerStaging custData);

	void setCustPhoneDetails(long custId, CustomerStaging custData);

	void setCustEmailDetails(long custId, CustomerStaging custData);

	void setCustDocDetails(long custId, CustomerStaging custData);
}
