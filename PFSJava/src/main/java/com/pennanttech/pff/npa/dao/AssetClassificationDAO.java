package com.pennanttech.pff.npa.dao;

import java.util.Date;
import java.util.List;
import java.util.Set;

import com.pennant.backend.model.finance.FinanceMain;
import com.pennanttech.pff.npa.model.AssetClassification;
import com.pennanttech.pff.provision.model.NpaProvisionStage;

public interface AssetClassificationDAO {

	void clearStage();

	void saveStage(List<NpaProvisionStage> list);

	void deleteSnapshots(Date appDate);

	void createSnapShots(Date appDate, Long finID, Long effFinID);

	void deleteQueue();

	long prepareQueue();

	void handleFailures();

	long getQueueCount();

	int updateThreadID(long from, long to, int threadID);

	void updateProgress(long finID, int progressInProcess);

	AssetClassification getClassification(long finID);

	long getCustId(long finID);

	List<FinanceMain> getPrimaryLoans(long custID);

	List<FinanceMain> getCoApplicantLoans(long finID);

	List<FinanceMain> getGuarantorLoans(long finID);

	List<NpaProvisionStage> getPastDueInfoFromStage(Set<String> finReferences);

	void save(AssetClassification ac);

	void update(AssetClassification ac);

	List<AssetClassification> getClassifications(long finID);

	AssetClassification getNpaDetails(long finID);

	AssetClassification getLoanInfo(long finID);

	void updateClassification(AssetClassification item);

	AssetClassification getAssetClassification(long finID);

	AssetClassification getNpaClassification(long finID);

	boolean isEffNpaStage(long finID);

	String getNpaRepayHierarchy(long finID);

	void deleteLinkedLoansForES(long finID);

	String getEntityCodeFromStage(long finID);

	void updatePastDuesForES(long finID);

	boolean checkDependency(long npaClassID);

	Long getNpaMovemntId(long finID);

	void saveNpaMovement(AssetClassification as);

	void updateNpaMovement(long id, AssetClassification as);

	void saveNpaTaggingMovement(AssetClassification as);

	AssetClassification getNpaMovemnt(long finID);

	void updatePrvPastDuedays(AssetClassification ac);

	boolean isNpaLoan(long finID);
}