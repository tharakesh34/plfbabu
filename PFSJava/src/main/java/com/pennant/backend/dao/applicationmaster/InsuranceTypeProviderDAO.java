package com.pennant.backend.dao.applicationmaster;

import java.util.List;

import com.pennant.backend.model.applicationmaster.InsuranceTypeProvider;

public interface InsuranceTypeProviderDAO {
	List<InsuranceTypeProvider> getInsuranceTypeProviderListByID(String id, String type) ;
	InsuranceTypeProvider getInsuranceTypeProviderByID(InsuranceTypeProvider insuranceTypeProvider, String type);
	void update(InsuranceTypeProvider insuranceTypeProvider, String type);
	String save(InsuranceTypeProvider insuranceTypeProvider, String type);
	void delete(InsuranceTypeProvider insuranceTypeProvider, String type);

}
