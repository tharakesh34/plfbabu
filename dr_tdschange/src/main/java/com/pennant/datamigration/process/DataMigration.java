package com.pennant.datamigration.process;

import com.pennant.datamigration.service.*;
import org.springframework.context.*;
import com.pennant.backend.model.finance.*;

public class DataMigration
{
    private static boolean isSuccess;
    private static DMTransactionService detailService;
    
    static {
        DataMigration.isSuccess = false;
    }
    
    public static DMTransactionService getDetailService() {
        return DataMigration.detailService;
    }
    
    public static void setDetailService(final DMTransactionService detailService) {
        DataMigration.detailService = detailService;
    }
    
    public static boolean processFinance(final ApplicationContext mainContext) {
        try {
            setDetailService((DMTransactionService)mainContext.getBean("dmFinanceDetailService"));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return DataMigration.isSuccess;
    }
    
    private static void doProcess(final FinScheduleData financeDetails) {
    }
}