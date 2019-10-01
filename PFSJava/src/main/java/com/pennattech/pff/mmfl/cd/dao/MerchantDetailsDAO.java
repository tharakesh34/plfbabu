package com.pennattech.pff.mmfl.cd.dao;

import com.pennant.backend.dao.impl.BasicCrudDao;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.mmfl.cd.model.MerchantDetails;

public interface MerchantDetailsDAO extends BasicCrudDao<MerchantDetails> {

	MerchantDetails getMerchantDetails(long id, String type);

	boolean isDuplicateKey(MerchantDetails merchantDetails, TableType tableType);

	boolean isDuplicatePOSIdKey(MerchantDetails merchantDetails, TableType tableType);

}
