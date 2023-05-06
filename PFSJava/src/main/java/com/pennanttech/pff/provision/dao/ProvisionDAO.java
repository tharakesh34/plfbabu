package com.pennanttech.pff.provision.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.provision.model.Provision;
import com.pennanttech.pff.provision.model.ProvisionRuleData;

public interface ProvisionDAO {

	void deleteQueue();

	long prepareQueueForSOM();

	long prepareQueueForEOM();

	long getQueueCount();

	int updateThreadID(long from, long to, int threadId);

	void updateProgress(String finReference, int progressInProcess);

	Long getLinkedTranId(String finReference);

	ProvisionRuleData getProvisionData(String finReference);

	Provision getProvision(String finReference);

	BigDecimal getCollateralValue(String finReference);

	BigDecimal getVasFee(String finReference);

	void save(Provision p, TableType tableType);

	void update(Provision p, TableType tableType);

	Provision getProvisionDetail(String finReference);

	void delete(String finReference, TableType type);

	Provision getProvisionById(long id, TableType tableType);

	List<Date> getProvisionDates();

	List<String> getAssetSubClassCodes(String code);
}