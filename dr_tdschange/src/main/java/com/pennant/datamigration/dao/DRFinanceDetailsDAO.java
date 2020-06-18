package com.pennant.datamigration.dao;

import java.math.BigDecimal;
import java.util.List;

import com.pennant.backend.model.applicationmaster.Assignment;
import com.pennant.datamigration.model.BlockedFinance;
import com.pennant.datamigration.model.DREMIHoliday;
import com.pennant.datamigration.model.DRFinanceDetails;
import com.pennant.datamigration.model.DRRateReviewCases;
import com.pennant.datamigration.model.DRRateReviewScheduleChange;
import com.pennant.datamigration.model.DRTDSChange;
import com.pennant.datamigration.model.ExcessCorrections;
import com.pennant.datamigration.model.ScheduleDiff;
import com.pennant.datamigration.model.scheduleIssue;

public interface DRFinanceDetailsDAO
{
    List<DRFinanceDetails> getDRFinanceReferenceList();
    
    void saveDRRateReviewStatus(final DRRateReviewScheduleChange p0);
    
    void saveDRFinanceReference(final DRFinanceDetails p0);
    
    void addBlockedFinance(final BlockedFinance p0);
    
    void removeBlockedFinance(final BlockedFinance p0);
    
    void updateScheduleIssue(final scheduleIssue p0);
    
    void updateScheduleDiff(final ScheduleDiff p0);
    
    List<DRRateReviewCases> getDRRateReviewCases();
    
    void saveExcessCorrection(final ExcessCorrections p0);
    
    void updateExcessCorrection(final ExcessCorrections p0);
    
    void prepareHC41();
    
    BigDecimal getOldGLBalance(final String p0);
    
    List<DRFinanceDetails> getDRCorrectionDM(final String p0);
    
    void updateDMCorrectionSts(final DRFinanceDetails p0);
    
    List<DREMIHoliday> getDREMIHoliday();

	void updateDREMIHoliday(DREMIHoliday erEH);
	
	// TDS Change
		List<DRTDSChange> getDRTDSChangeList();
		void updateDRTDSChange(DRTDSChange drTDS);

}