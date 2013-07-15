package com.pennant.backend.dao.testing;

import java.util.List;

import com.pennant.backend.model.testing.AdditionalFieldValues;

public interface AdditionalFieldValuesDAO {
	
	public AdditionalFieldValues getAdditionalFieldValues();
	public AdditionalFieldValues getNewAdditionalFieldValues();
	public AdditionalFieldValues getAdditionalFieldValuesById(String id,String type);
	public void update(AdditionalFieldValues additionalFieldValues,String type);
	public void delete(AdditionalFieldValues additionalFieldValues,String type);
	public String save(AdditionalFieldValues additionalFieldValues,String type);
	public void initialize(AdditionalFieldValues additionalFieldValues);
	public void refresh(AdditionalFieldValues entity);
	public List<AdditionalFieldValues> getAddfeldList(String module, String type);

}
