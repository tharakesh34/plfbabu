package com.pennant.backend.dao.insurancedetails;

import java.util.Date;
import java.util.List;

import com.pennant.backend.model.finance.FinInsurances;
import com.pennant.backend.model.finance.FinSchFrqInsurance;

public interface FinInsurancesDAO {

	FinInsurances getFinInsuranceByID(FinInsurances finInsurance, String type,boolean isWIF);

	List<FinInsurances> getFinInsuranceListByRef(String finreference, String type,boolean isWIF);

	void update(FinInsurances finInsurance, String type,boolean isWIF);

	long save(FinInsurances finInsurance, String type,boolean isWIF);

	void delete(FinInsurances finInsurance, String type,boolean isWIF);

	void deleteFinInsurancesList(String finReference, boolean isWIF, String tableType);

	void deleteByFinReference(String finreference, String type);

	void saveFreqBatch(List<FinSchFrqInsurance> frqList, boolean isWIF, String tableType);

	void deleteFreqBatch(long insId, boolean isWIF, String tableType);
	
	List<FinSchFrqInsurance> getFinSchFrqInsuranceFinRef(String finReference, boolean isWIF, String tableType);
	
	List<FinInsurances> getInsurancesList(String insuranceType,String tableType);

	void updateInsSchdPaids(List<FinSchFrqInsurance> updateInsList);

	List<FinSchFrqInsurance> getInsScheduleBySchDate(String finReference, Date schDate);

	void updateInsPaids(List<FinSchFrqInsurance> updateInsList);

	List<FinSchFrqInsurance> getInsSchdToPost(String finReference, Date schDate);
}
