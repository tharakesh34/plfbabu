package com.pennant.backend.dao.finance;

import java.util.List;

import com.pennant.backend.model.finance.RestructureCharge;
import com.pennant.backend.model.finance.RestructureDetail;
import com.pennant.backend.model.finance.RestructureType;

public interface RestructureDAO {

	long save(RestructureDetail restructureDetail, String tableType);

	RestructureDetail getRestructureDetailById(long restructureId, String type);

	RestructureDetail getRestructureDetailByFinReference(long finID, String type);

	void update(RestructureDetail restructureDetail, String tableType);

	void delete(long restructureId, String tableType);

	List<RestructureCharge> getRestructureCharges(long restructureId, String type);

	int saveChargeList(List<RestructureCharge> chargeList, String type);

	void deleteChargeList(long restructureId, String type);

	void deleteChargeList(long restructureId, int chargeSeq, String type);

	void deleteRestructureCharges(long id, String type);

	boolean isExistRestructureType(long rstTypeId, boolean isStepFinance);

	boolean isExistRestructureReason(String code);

	boolean checkLoanProduct(long finID);

	RestructureType getRestructureTypeById(String rstTypeId);

}
