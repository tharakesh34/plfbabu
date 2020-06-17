package com.pennant.datamigration.dao;

import java.util.List;

import com.pennant.backend.model.applicationmaster.Assignment;
import com.pennant.backend.model.applicationmaster.BusinessVertical;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.datamigration.model.BasicLoanRecon;
import com.pennant.datamigration.model.ClientRescheduleData;
import com.pennant.datamigration.model.CutOffDateSchedule;
import com.pennant.datamigration.model.DRCorrections;
import com.pennant.datamigration.model.FeeTypeVsGLMapping;
import com.pennant.datamigration.model.ScheduleRate;
import com.pennant.datamigration.model.SourceReport;
import com.pennant.datamigration.model.TabAgreementDate;

public interface BasicLoanReconDAO
{
    void saveRecon(final BasicLoanRecon p0);
    
    void cleanDestination();
    
    List<FinanceType> getDMFinTypes(final String p0);
    
    SourceReport getSourceReportDetails(final long p0);
    
    List<FeeTypeVsGLMapping> getFeeTypeVsGLMappings();
    
    List<ScheduleRate> getScheduleRates(final long p0);
    
    TabAgreementDate getTabAgreementDate(final long p0);
    
    List<ClientRescheduleData> getClientRescheduleData(final String p0);
    
    List<BusinessVertical> getBusinessVerticals();
    
    Assignment getAssignment(final long p0);
    
    DRCorrections getDRCorrections(final String p0);
    
    CutOffDateSchedule getCutOffDateSchedule(final String p0);
    
    CutOffDateSchedule getEMICorrSchedule(final String p0);
}