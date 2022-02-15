package com.pennant.app.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennanttech.pennapps.core.resource.Literal;

public class GenerateChangeSetMain {
	private static final Logger logger = LogManager.getLogger(GenerateChangeSetMain.class);

	public static void main(String[] args) {

		GenerateChangeSet generateChangeSet = new GenerateChangeSet();

		String tables = "rejectfinagreementdetail ,rejectfinanceeligibilitydetail ,rejectfinancemain ,rejectfinancescoreheader ,rejectfinbillingheader ,rejectfinblacklistdetail, ,rejectfindedupdetail ,rejectfindefermentdetail ,rejectfindefermentheader ,rejectfindisbursementdetails ,rejectfinfeecharges ,rejectfinfeedetail ,rejectfinfeescheduledetail ,rejectfinodpenaltyrates ,rejectfinpolicecasedetail ,rejectfinrepayinstruction ,rejectfinscheduledetails ,restructure ,restructure_details ,sanction_conditions ,scheduleduetaxdetails ,subventiondetails ,subvention_knockoff_details ,subventionscheduledetail ,synopsisdetails ,uploadfinexpenses ,uploadratechangefinref ,uploadreceipt ,uploadtaxpercent ,vasmovement ,vasmovementdetails ,wiffinancemain ,wiffindefermentdetail ,wiffindefermentheader ,wiffindisbursementdetails ,wiffinfeecharges ,wiffinfeedetail ,wiffinrepayinstruction ,wiffinscheduledetails ,wiffinsteppolicydetail ,wifindicativetermdetail";

		int id = 154;
		try {
			for (String table : tables.split(",")) {
				String changeSet = null;

				changeSet = generateChangeSet.getChangeSet(StringUtils.trimToEmpty(table), id);

				if (changeSet != null) {
					System.out.println(changeSet);
					System.out.println();
					id = id + 1;
				}

			}
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

	}

}
