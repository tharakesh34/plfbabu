package com.pennant.backend.dao.lmtmasters;

import java.math.BigDecimal;

import com.pennant.backend.model.commodity.FinCommodityInventory;

public interface FinCommodityInventoryDAO {

	long save(FinCommodityInventory finCommodityInventory,String type);

	void update(FinCommodityInventory finCommodityInventory,String type);

	FinCommodityInventory getFinCommodityInventoryById(String loanRefNumber,String type);

	int getBulkCommodities(FinCommodityInventory finCommInventory);
	
	BigDecimal getTotalCommodityQuantity(FinCommodityInventory finCommInventory);
}
