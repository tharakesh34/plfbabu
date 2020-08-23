package com.pennant.backend.dao.systemmasters;

import java.util.List;

import com.pennant.backend.model.ocrmaster.OCRDetail;
import com.pennanttech.pff.core.TableType;

public interface OCRDetailDAO {

	OCRDetail getOCRDetail(long headerID, String type);

	void delete(OCRDetail ocrDetail, TableType tableType);

	String save(OCRDetail ocrDetail, TableType tableType);

	void update(OCRDetail ocrDetail, TableType tableType);

	List<OCRDetail> getOCRDetailList(long headerID, String type);

	void deleteList(OCRDetail ocrDetail, TableType type);

	boolean isDuplicateKey(int stepSequence, long headerID, long detailID, TableType tableType);

}
