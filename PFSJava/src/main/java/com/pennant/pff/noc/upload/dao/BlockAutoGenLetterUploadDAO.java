package com.pennant.pff.noc.upload.dao;

import java.util.List;

import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.pff.noc.upload.model.BlockAutoGenLetterUpload;
import com.pennanttech.pff.core.TableType;

public interface BlockAutoGenLetterUploadDAO {

	List<BlockAutoGenLetterUpload> getDetails(long id);

	void update(List<BlockAutoGenLetterUpload> details);

	void update(List<Long> headerIds, String errorCode, String errorDesc);

	String getSqlQuery();

	boolean isValidateAction(long finid);

	void delete(long finid);

	void save(BlockAutoGenLetterUpload bagu);

	void savebyLog(BlockAutoGenLetterUpload bu);

	String getRemarks(Long finID);

	FinanceMain getFinanceMain(long finID, TableType tabeType);

	boolean isLetterInitiated(Long finID, String letterType);

}