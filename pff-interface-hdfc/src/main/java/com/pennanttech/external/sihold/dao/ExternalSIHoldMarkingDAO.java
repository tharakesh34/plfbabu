package com.pennanttech.external.sihold.dao;

import java.util.Date;
import java.util.List;

import com.pennanttech.external.sihold.model.SIHoldDetails;

public interface ExternalSIHoldMarkingDAO {
	public void insertHoldData(SIHoldDetails details);

	public List<SIHoldDetails> getHoldRecords(int fileStatus);

	public void updateHoldRecordFileStatus(String accNumber, String loanRef, Date schDate, int fileStatus);

	public long getSeqNumber(String tableName);
}
