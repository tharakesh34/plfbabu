package com.pennant.pff.dao.subvention;

import java.util.List;

import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.pff.model.subvention.Subvention;
import com.pennant.pff.model.subvention.SubventionHeader;
import com.pennanttech.dataengine.model.DataEngineStatus;
import com.pennanttech.pennapps.core.model.ErrorDetail;

public interface SubventionUploadDAO {

	List<Subvention> getSubventionDetails(long batchId);

	List<FinanceMain> getFinanceMain(long batchId);

	List<FinFeeDetail> getFinFeeDetails(long batchId, String feeTypeCode);

	void updateFinFeeDetails(String finReference, FinFeeDetail finFee);

	void updateSubventionDetails(Subvention subVention);

	int getSucessCount(String finRef, String status);

	boolean isFileExists(String name);

	long saveSubventionHeader(String ref, String entityCode);

	int logSubvention(List<ErrorDetail> errDetail, Long id);

	void updateRemarks(SubventionHeader subventionHeader);

	int saveSubvention(List<Subvention> subventions, long id);

	void updateDeRemarks(SubventionHeader header, DataEngineStatus deStatus);

	Subvention getGstDetails(String finReference);

}
