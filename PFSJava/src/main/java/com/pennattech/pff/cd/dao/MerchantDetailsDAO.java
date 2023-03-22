package com.pennattech.pff.cd.dao;

import java.util.Map;

import com.pennant.backend.dao.impl.BasicCrudDao;
import com.pennanttech.pff.cd.model.MerchantDetails;
import com.pennanttech.pff.core.TableType;

public interface MerchantDetailsDAO extends BasicCrudDao<MerchantDetails> {

	MerchantDetails getMerchantDetails(long id, String type);

	boolean isDuplicateKey(MerchantDetails merchantDetails, TableType tableType);

	boolean isDuplicatePOSIdKey(MerchantDetails merchantDetails, TableType tableType);

	public Map<String, Object> getGSTDataMapForMerch(long mId);

	public MerchantDetails getDetails(String mId);

}
