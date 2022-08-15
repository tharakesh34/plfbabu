package com.pennanttech.pff.overdraft.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.presentment.model.PresentmentCharge;
import com.pennanttech.pff.presentment.model.PresentmentDetail;

public interface OverdraftPresentmentDAO {
	void cancelManualAdvise(long presentmentID);

	void cancelODDetails(long presentmentID);

	void cancelPresentmentCharges(long presentmentID);

	void update(List<FinODDetails> odDetails, String type);

	void update(List<ManualAdvise> maList);

	void updateCharges(long presentmentId, BigDecimal charges);

	void savePresentmentCharge(List<PresentmentCharge> pcList, TableType tableType);

	List<PresentmentCharge> getPresentmentCharges(long detailId, String type);

	void updateBatchApprovedDate(long presentmentId, Date approvedDate);

	List<PresentmentDetail> getPreviousPresentmentBatches(PresentmentDetail presentmentDetail);

}
