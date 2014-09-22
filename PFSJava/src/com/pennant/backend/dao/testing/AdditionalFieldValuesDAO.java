package com.pennant.backend.dao.testing;

import java.util.List;

import com.pennant.backend.model.testing.AdditionalFieldValues;

public interface AdditionalFieldValuesDAO {
	
	AdditionalFieldValues getAdditionalFieldValues();
	AdditionalFieldValues getNewAdditionalFieldValues();
	AdditionalFieldValues getAdditionalFieldValuesById(String id,String type);
	void update(AdditionalFieldValues additionalFieldValues,String type);
	void delete(AdditionalFieldValues additionalFieldValues,String type);
	String save(AdditionalFieldValues additionalFieldValues,String type);
	void initialize(AdditionalFieldValues additionalFieldValues);
	void refresh(AdditionalFieldValues entity);
	List<AdditionalFieldValues> getAddfeldList(String module, String type);
}
