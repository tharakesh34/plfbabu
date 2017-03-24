package com.pennant.backend.dao.limits;

import java.util.List;

import com.pennant.backend.model.limits.ClosedFacilityDetail;

public interface ClosedFacilityDAO {

	List<ClosedFacilityDetail> fetchClosedFacilityDetails();

	void updateClosedFacilityStatus(List<ClosedFacilityDetail> proClFacilityList);

}
