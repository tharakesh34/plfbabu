package com.pennant.backend.dao.finance;

import java.util.List;

import com.pennant.backend.model.financemanagement.FinFlagsDetail;

public interface FinFlagDetailsDAO {
	FinFlagsDetail getfinFlagDetails();
	FinFlagsDetail getNewFinFlagsDetail();
	
	void save(FinFlagsDetail finFlagsDetail, String type);
	void delete(String finRef, String flagCode, String moduleName, String type);
	void update(FinFlagsDetail finFlagsDetail, String type);
	
	void savefinFlagList(List<FinFlagsDetail> finFlagsDetail, String type);
	void deleteList(String finRef,String moduleName,  String type);
	
	List<FinFlagsDetail> getFinFlagsByFinRef(String finReference, String moduleName, String type);
	FinFlagsDetail getFinFlagsByRef(String finReference,String flagcode,String moduleName, String type);
	List<String> getScheduleEffectModuleList(boolean schdChangeReq);
	void updateList(List<FinFlagsDetail> finFlagsDetail,String type);
	int getFinFlagDetailCountByRef(String finReference, String type);
	
}
