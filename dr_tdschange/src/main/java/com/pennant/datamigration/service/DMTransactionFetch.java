package com.pennant.datamigration.service;

import com.pennant.datamigration.model.MigrationData;
import com.pennant.datamigration.model.ReferenceID;

public interface DMTransactionFetch {

	MigrationData getEHFinanceDetails(String p0, ReferenceID p1);
	
	// TDS Change
    MigrationData getTDSFinanceDetails(final String finReference);

}