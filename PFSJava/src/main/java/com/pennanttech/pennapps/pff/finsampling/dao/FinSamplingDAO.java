package com.pennanttech.pennapps.pff.finsampling.dao;

import com.pennanttech.pennapps.pff.sampling.model.Sampling;
import com.pennanttech.pff.core.TableType;

public interface FinSamplingDAO {
	void updateSampling(Sampling sampling, TableType tableType);

	void saveOrUpdateRemarks(Sampling sampling, TableType tableType);

}
