package com.pennant.app.util;

import org.apache.commons.lang3.StringUtils;

public class GenerateChangeSetMain {

	public static void main(String[] args) {
		GenerateChangeSet generateChangeSet = new GenerateChangeSet();

		String tables = "rejectfinagreementdetail ,rejectfinanceeligibilitydetail ,rejectfinancemain ,rejectfinancescoreheader ,rejectfinbillingheader ,rejectfinblacklistdetail, ,rejectfindedupdetail ,rejectfindefermentdetail ,rejectfindefermentheader ,rejectfindisbursementdetails ,rejectfinfeecharges ,rejectfinfeedetail ,rejectfinfeescheduledetail ,rejectfinodpenaltyrates ,rejectfinpolicecasedetail ,rejectfinrepayinstruction ,rejectfinscheduledetails ,restructure ,restructure_details ,sanction_conditions ,scheduleduetaxdetails ,subventiondetails ,subvention_knockoff_details ,subventionscheduledetail ,uploadfinexpenses ,uploadratechangefinref ,uploadreceipt ,uploadtaxpercent ,vasmovement ,vasmovementdetails ,wiffinancemain ,wiffindefermentdetail ,wiffindefermentheader ,wiffindisbursementdetails ,wiffinfeecharges ,wiffinfeedetail ,wiffinrepayinstruction ,wiffinscheduledetails ,wiffinsteppolicydetail ,wifindicativetermdetail";

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
			e.printStackTrace();
		}

	}

}
