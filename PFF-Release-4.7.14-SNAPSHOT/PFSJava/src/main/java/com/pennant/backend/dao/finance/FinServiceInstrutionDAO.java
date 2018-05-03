package com.pennant.backend.dao.finance;

import java.util.List;

import com.pennant.backend.model.finance.FinServiceInstruction;

public interface FinServiceInstrutionDAO {

	void saveList(List<FinServiceInstruction> finServiceInstructionList, String type);
	void deleteList(String finReference,String tableType,String finEvent);
	List<FinServiceInstruction> getFinServiceInstructions(String finReference, String type,String finEvent);
}
