package com.pennant.backend.dao.systemmasters;

import com.pennant.backend.model.ocrmaster.OCRHeader;
import com.pennanttech.pff.core.TableType;

public interface OCRHeaderDAO {

	OCRHeader getOCRHeaderById(long headerId, String type);

	String save(OCRHeader ocrHeader, TableType tableType);

	void update(OCRHeader ocrHeader, TableType tableType);

	void delete(OCRHeader ocrHeader, TableType tableType);

	boolean isDuplicateKey(String ocrID, TableType tableType);

	OCRHeader getOCRHeaderByOCRId(String ocrID, String type);

}
