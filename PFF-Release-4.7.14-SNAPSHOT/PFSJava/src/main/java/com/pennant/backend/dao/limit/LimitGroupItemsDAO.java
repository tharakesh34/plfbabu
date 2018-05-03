package com.pennant.backend.dao.limit;

import java.util.List;

import com.pennant.backend.model.limit.LimitGroupItems;

public interface LimitGroupItemsDAO {
	LimitGroupItems getLimitGroupItems();
	LimitGroupItems getNewLimitGroupItems();
	List<LimitGroupItems> getLimitGroupItemsById(String id,String type);
	void update(LimitGroupItems limitGroupItems,String type);
	void delete(String limitGroupCode,String type);
	String save(LimitGroupItems limitGroupItems,String type);
	
	void deleteLimitGroupItems(LimitGroupItems limitGroupItems,String type);
	
	String getItemCodes(LimitGroupItems limitGroupItems, String id);
	List<LimitGroupItems> getLimitGroupItemById(String id, String type);

	int validationCheck(String limitGroup, String type);
	int limitItemCheck(String lmtItem, String limitCategory,String type);
}
