package com.pennant.backend.service.cersai;

import java.util.List;

import com.pennant.backend.model.cersai.CersaiAddCollDetails;
import com.pennant.backend.model.cersai.CersaiAssetOwners;
import com.pennant.backend.model.cersai.CersaiBorrowers;
import com.pennant.backend.model.cersai.CersaiChargeHolder;
import com.pennant.backend.model.cersai.CersaiFileInfo;
import com.pennant.backend.model.cersai.CersaiHeader;
import com.pennant.backend.model.cersai.CersaiImmovableAsset;
import com.pennant.backend.model.cersai.CersaiIntangibleAsset;
import com.pennant.backend.model.cersai.CersaiModifyCollDetails;
import com.pennant.backend.model.cersai.CersaiMovableAsset;
import com.pennant.backend.model.cersai.CersaiSatisfyCollDetails;

public interface CERSAIService {

	void logFileInfo(CersaiFileInfo fileInfo);

	List<String> getotalRecords(String downloadType);

	void updateFileStatus(CersaiFileInfo fileInfo);

	CersaiHeader processFileHeader(long totalRecords, String type);

	List<CersaiAddCollDetails> processCollateralDetails(String collaterRef, long batchId, long borrowerCount,
			long assetOenerCount, int serialNo, String batchRef);

	List<CersaiBorrowers> getCustomerDetails(String collateralRef);

	List<CersaiAssetOwners> getAssetOwnerDetails(String collateralRef);

	List<CersaiBorrowers> processBorrowers(List<CersaiBorrowers> cb, long batchId);

	List<CersaiAssetOwners> processAssetOwners(List<CersaiAssetOwners> ca, long batchId);

	CersaiMovableAsset processMovable(String collateral, long batchId, String collateralType);

	CersaiImmovableAsset processImmovable(String collateral, long batchId, String collateralType);

	CersaiIntangibleAsset processInTangible(String collateral, long batchId, String collateralType);

	List<String> getSatisfyingRecords(String downloadType);

	CersaiSatisfyCollDetails processSatisfyCollaterals(String collateralRef, long batchId, int serialNo,
			String batchRef);

	CersaiModifyCollDetails processModifyCollaterals(String collateralRef, long batchId, int serialNo, String batchRef);

	String getAssetCategory(Long id);

	List<String> getModifyRecords();

	void logFileInfoException(long headerId, String valueOf, String message);

	String generateFileSeq();

	CersaiChargeHolder getChargeHolderDetails();

	String generateBatchRef();
}
