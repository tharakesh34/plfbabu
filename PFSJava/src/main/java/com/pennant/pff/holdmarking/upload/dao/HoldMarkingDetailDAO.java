package com.pennant.pff.holdmarking.upload.dao;

import java.util.List;

import com.pennant.pff.holdmarking.model.HoldMarkingDetail;

public interface HoldMarkingDetailDAO {

	void saveDetail(HoldMarkingDetail hmd);

	HoldMarkingDetail getHoldByReference(long finId, String accNum);

	int getCountId(long id);

	List<HoldMarkingDetail> getHoldDtlsByRefAndAcc(String reference, String accNumber);
}