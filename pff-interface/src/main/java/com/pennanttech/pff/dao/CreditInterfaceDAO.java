package com.pennanttech.pff.dao;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.backend.model.servicetask.ServiceTaskDetail;
import com.pennant.backend.model.solutionfactory.ExtendedFieldDetail;
import com.pennant.backend.model.systemmasters.City;

public interface CreditInterfaceDAO {

	
	List<ExtendedFieldDetail> getExtendedFieldDetailsByFieldName(Set<String> fieldNames);

	List<CustomerDetails> getCoApplicants(List<Long> coApplicantIDs, String string);

    ExtendedFieldHeader getExtendedFieldHeaderByModuleName(String string, String string2);

	List<Customer> getCustomerByID(List<Long> customerIds, String type);

	Map<String, Object> getExtendedField(String custCIF, String string);
	
	public void save(ServiceTaskDetail serviceTaskDetail, String type);

	public List<ServiceTaskDetail> getServiceTaskDetails(String module, String reference, String serviceTaskName);
	
	City getCityDetails(final String pCCountry, String pCProvince, String pCCity, String type);

	void saveExtendedDetails(Map<String, Object> appplicationdata, String type, String tableName);

	void updateExtendedDetails(String custCIF, int seqNo, Map<String, Object> appplicationdata, String type, String tableName);

	
}
