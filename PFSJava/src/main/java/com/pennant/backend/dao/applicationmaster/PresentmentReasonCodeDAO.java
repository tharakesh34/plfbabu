package com.pennant.backend.dao.applicationmaster;

import com.pennant.backend.model.applicationmaster.PresentmentReasonCode;

public interface PresentmentReasonCodeDAO {
	PresentmentReasonCode getPresentmentReasonCodeById(String id, String type);

	void update(PresentmentReasonCode presentmentResponseCode, String type);

	void delete(PresentmentReasonCode presentmentResponseCode, String type);

	String save(PresentmentReasonCode presentmentResponseCode, String type);
}
