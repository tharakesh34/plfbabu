package com.pennant.pff.holdmarking.upload.dao;

import java.util.List;

import com.pennant.pff.holdmarking.model.HoldMarkingHeader;

public interface HoldMarkingHeaderDAO {

	long saveHeader(HoldMarkingHeader hmu);

	int getCountFinId(long finId, String accNum);

	List<HoldMarkingHeader> getHoldListByFinId(long finId);

	List<HoldMarkingHeader> getHoldByFinId(long finId, String accNum);

	void updateHeader(HoldMarkingHeader hmh);

	List<HoldMarkingHeader> getHoldByAccNum(String accNum);
}
