package com.pennant.backend.dao.finance;

import java.util.List;

import com.pennant.backend.model.finance.ExposureLinking;

public interface ExposureLinkingDAO {

	String save(List<ExposureLinking> exposureLinkings);

	List<ExposureLinking> getExposureLinkgs(String finReference);

}
