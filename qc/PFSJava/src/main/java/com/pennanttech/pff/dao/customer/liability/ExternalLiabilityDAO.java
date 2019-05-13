package com.pennanttech.pff.dao.customer.liability;

import java.math.BigDecimal;
import java.util.List;

import com.pennant.backend.model.customermasters.CustomerExtLiability;

public interface ExternalLiabilityDAO {

	long save(CustomerExtLiability custExtLiability, String type);

	void update(CustomerExtLiability custExtLiability, String type);

	void delete(long id, String type);

	void deleteByLinkId(Long linkId, String type);

	List<CustomerExtLiability> getLiabilities(long custId, String type);

	BigDecimal getTotalLiabilityByLinkId(Long liabilityLinkId);

	BigDecimal getTotalLiabilityByFinReference(String keyReference);

	List<CustomerExtLiability> getLiabilities(long linkId);

	BigDecimal getTotalLiabilityBySamplingId(long id);

}
