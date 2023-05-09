package com.pennanttech.pff.provision.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.npa.model.AssetClassCode;
import com.pennanttech.pff.npa.model.AssetSubClassCode;
import com.pennanttech.pff.provision.model.NpaProvisionStage;
import com.pennanttech.pff.provision.model.Provision;
import com.pennanttech.pff.provision.model.ProvisionRuleData;

public interface ProvisionDAO {

	void deleteQueue();

	long prepareQueueForSOM();

	long prepareQueueForEOM();

	long getQueueCount();

	int updateThreadID(long from, long to, int threadId);

	void updateProgress(long finID, int progressInProcess);

	Long getLinkedTranId(long finID);

	ProvisionRuleData getProvisionData(long finID);

	Provision getProvision(long finID);

	BigDecimal getCollateralValue(String reference);

	BigDecimal getVasFee(String finReference);

	void save(Provision p, TableType tableType);

	void update(Provision p, TableType tableType);

	Provision getProvisionDetail(long finID);

	void delete(long finID, TableType type);

	Provision getProvisionById(long id, TableType tableType);

	List<Date> getProvisionDates();

	List<AssetClassCode> getAssetClassCodes();

	List<AssetSubClassCode> getAssetSubClassCodes(Long classId);

	boolean isRecordExists(long finID);

	List<NpaProvisionStage> getNPAProvisionDetails(long finID);
}