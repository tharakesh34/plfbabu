package com.pennant.datamigration.service;

import java.util.List;

import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.datamigration.model.BlockedFinance;
import com.pennant.datamigration.model.DREMIHoliday;
import com.pennant.datamigration.model.DRFinanceDetails;
import com.pennant.datamigration.model.DRRateReviewScheduleChange;
import com.pennant.datamigration.model.DRTDSChange;
import com.pennant.datamigration.model.MigrationData;
import com.pennant.datamigration.model.ReferenceID;
import com.pennanttech.pff.core.TableType;

public interface DMTransactionService
{
    FinScheduleData getFinanceDetails(final String p0, final String p1, final DRRateReviewScheduleChange p2);
    
    FinScheduleData getRRFinanceDetails(final String p0, final String p1);
    
    List<DRFinanceDetails> getDRFinanceReferenceList();
    
    void removeBlockedFinance(final BlockedFinance p0);
    
    int validateSchedules(final FinScheduleData p0, final TableType p1, final DRRateReviewScheduleChange p2);
    
    List<DRFinanceDetails> getDRCorrectionDM(final String p0);
    
    void updateDMCorrectionSts(final DRFinanceDetails p0);
    
    FinScheduleData getFinanceDetails(final String p0);
    
    FinScheduleData processDR(final FinScheduleData p0);
    
    List<DREMIHoliday> getDREHListList();
    
    MigrationData procEHSchedule(MigrationData p0, ReferenceID p1) throws Exception;
    
    void updateDREMIHoliday(DREMIHoliday dreh);
    
 // TDS Change
 	List<DRTDSChange> getDRTDSChangeList();
 	
 	MigrationData processTDSChange(MigrationData sMD) throws Exception;
    
    MigrationData saveTDSChange(MigrationData dMD) throws Exception;
}