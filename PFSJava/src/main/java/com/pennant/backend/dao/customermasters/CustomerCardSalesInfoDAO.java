package com.pennant.backend.dao.customermasters;

import java.util.List;

import com.pennant.backend.model.customermasters.CustCardSales;
import com.pennant.backend.model.customermasters.CustCardSalesDetails;

public interface CustomerCardSalesInfoDAO {

	CustCardSales getCustomerCardSalesInfoById(long id, String type);

	List<CustCardSales> getCardSalesInfoByCustomer(final long id, String type);

	void update(CustCardSales customerCardSalesInfo, String type);

	void delete(CustCardSales customerCardSalesInfo, String type);

	void deleteByCustomer(long custID, String type);

	long save(CustCardSales customerCardSalesInfo, String type);

	int getVersion(long id);

	CustCardSales getCustomerCardSalesInfoByCustId(CustCardSales customerBankInfo, String type);

	List<CustCardSalesDetails> getCardSalesInfoSubDetailById(long id, String type);

	long save(CustCardSalesDetails cardSalesInfoSubDetails, String type);

	void update(CustCardSalesDetails cardSalesInfoSubDetail, String type);

	void delete(CustCardSalesDetails cardSalesInfoSubDetails, String type);

	void delete(long cardSalesId, String type);

	int getCustomerCardSalesInfoByCustMerchantId(long custId, String merchantId, long Id, String type);
}
