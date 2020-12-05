package com.pennant.backend.dao.finance;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.LMSServiceLog;

public interface FinServiceInstrutionDAO {

	void saveList(List<FinServiceInstruction> finServiceInstructionList, String type);

	void deleteList(String finReference, String tableType, String finEvent);

	List<FinServiceInstruction> getFinServiceInstructions(String finReference, String type, String finEvent);

	void save(FinServiceInstruction finServiceInstruction, String type);

	List<FinServiceInstruction> getFinServInstByServiceReqNo(final String finReference, Date fromDate,
			String serviceReqNo, String finEvent);

	List<FinServiceInstruction> getFinServiceInstAddDisbDetail(String finReference, Date fromDate, String finEvent);

	boolean getFinServInstDetails(String finEvent, String serviceReqNo);

	List<FinServiceInstruction> getFinServiceInstDetailsByServiceReqNo(String finReference, String serviceReqNo);

	public void saveLMSServiceLOGList(List<LMSServiceLog> lmsServiceLog);

	BigDecimal getOldRate(String finReference, Date schdate);

	List<LMSServiceLog> getLMSServiceLogList(String notificationFlag);

	void updateNotificationFlag(String notificationFlag, long id);

	List<FinServiceInstruction> getDMFinServiceInstructions(String finReference, String type);

	BigDecimal getNewRate(String finReference, Date schdate);

	List<String> getFinEventByFinRef(String finReference, String type);

	List<FinServiceInstruction> getOrgFinServiceInstructions(String finReference, String type);

}
