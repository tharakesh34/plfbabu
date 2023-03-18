package com.pennant.backend.dao.finance;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.LMSServiceLog;

public interface FinServiceInstrutionDAO {

	int saveList(List<FinServiceInstruction> finServiceInstructionList, String type);

	void deleteList(long finID, String finEvent, String tableType);

	List<FinServiceInstruction> getFinServiceInstructions(long finID, String type, String finEvent);

	List<FinServiceInstruction> getFinServiceInstructions(long finID, String finEvent);

	void save(FinServiceInstruction finServiceInstruction, String type);

	List<FinServiceInstruction> getFinServInstByServiceReqNo(long finID, Date fromDate, String serviceReqNo,
			String finEvent);

	List<FinServiceInstruction> getFinServiceInstAddDisbDetail(long finID, Date fromDate, String finEvent);

	boolean getFinServInstDetails(String finEvent, String serviceReqNo);

	public void saveLMSServiceLOGList(List<LMSServiceLog> lmsServiceLog);

	BigDecimal getOldRate(long finID, Date schdate);

	List<LMSServiceLog> getLMSServiceLogList(String notificationFlag);

	void updateNotificationFlag(String notificationFlag, long id);

	BigDecimal getNewRate(long finID, Date schdate);

	List<String> getFinEventByFinRef(String finReference, String type);

	List<FinServiceInstruction> getOrgFinServiceInstructions(long finID, String type);

	boolean isFinServiceInstExists(long finID, String table);

	FinServiceInstruction getFinServiceInstDetailsBySerReqNo(long finID, String serviceReqNo);

}
