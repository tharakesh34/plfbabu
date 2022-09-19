package com.pennant.backend.dao.cersai;

import java.util.List;
import java.util.Map;

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
import com.pennant.backend.model.finance.FinanceMain;

public interface CERSAIDAO {

	List<String> getotalRecords();

	void updateFileStatus(CersaiFileInfo fileInfo);

	void logFileInfo(CersaiFileInfo fileInfo);

	Long saveHeader(CersaiHeader ch);

	CersaiHeader getHeaderByBatchId(long batchId);

	List<CersaiBorrowers> getBorrowersByCollateralRef(String collateralRef);

	List<CersaiAssetOwners> getAssetOwnersByCollateralRef(String collateralRef);

	List<CersaiAddCollDetails> getCollateralDetailsByRef(String collateralRef);

	void saveCersaiCollateralDetails(List<CersaiAddCollDetails> collDetails);

	void saveBorrowerDetails(List<CersaiBorrowers> borrowers);

	void saveAssetOwnerDetails(List<CersaiAssetOwners> assetOwners);

	List<String> getSatisfyingRecords(String downloadType);

	CersaiSatisfyCollDetails getSatisfyCollDetailsByRef(String collateralRef);

	long saveSatisfyCollateral(CersaiSatisfyCollDetails cSCD);

	List<Map<String, Object>> getExtendedFieldMap(String reference, String tableName, String type);

	List<FinanceMain> getFinanceByCollateralRef(String collateralRef);

	CersaiModifyCollDetails getModifyCollDetailsByRef(String collateralRef);

	long saveModifyCollateral(CersaiModifyCollDetails colDtl);

	String getAssetCategory(Long id);

	List<String> getModifyRecords();

	String getRemarks(String collateralRef);

	long saveMovableAsset(CersaiMovableAsset cma);

	long saveImmovableAsset(CersaiImmovableAsset cima);

	long saveIntangibleAsset(CersaiIntangibleAsset cia);

	void logFileInfoException(long id, String finReference, String reason);

	String generateFileSeq();

	CersaiChargeHolder getChargeHolderDetails();

	String generateBatchRef();

}
