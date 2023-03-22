package com.pennanttech.pff.npa.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.pennant.app.core.CustEODEvent;
import com.pennanttech.pff.npa.model.AssetClassSetupHeader;
import com.pennanttech.pff.npa.model.AssetClassification;
import com.pennanttech.pff.provision.model.NpaProvisionStage;

public interface AssetClassificationService {

	void clearStage();

	void process(CustEODEvent custEODEvent);

	void createSnapshots(Date appDate);

	long prepareQueue();

	void handleFailures();

	long getQueueCount();

	int updateThreadID(long from, long to, int threadID);

	void updateProgress(long finID, int progressInProcess);

	AssetClassification getClassification(long finID);

	void setNpaClassification(AssetClassification ca);

	List<NpaProvisionStage> getLinkedLoans(AssetClassification ca);

	void saveOrUpdate(AssetClassification item);

	void saveStage(List<NpaProvisionStage> list);

	Map<String, AssetClassSetupHeader> getAssetClassSetups();

	AssetClassification getNpaDetails(long finID);

	AssetClassification setEffClassification(AssetClassification npa);

	void setLoanInfo(AssetClassification ac);

	void doPostNpaChange(AssetClassification npaAc);

	void doReversalNpaPostings(AssetClassification npaAc);

	void updateClassification(AssetClassification ac);

	AssetClassification getAssetClassification(long finID);

	boolean isEffNpaStage(long finID);

	String getNpaRepayHierarchy(long finID);

	void doCloseLoan(long finID);

	Long getNpaMovemntId(long finID);

	void saveNpaMovement(AssetClassification npa);

	void updateNpaMovement(long id, AssetClassification npa);

	AssetClassification getNpaMovemnt(long finID);

	void saveNpaTaggingMovement(AssetClassification npa);

}