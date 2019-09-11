package com.pennant.backend.service.finance;

import java.util.List;

import com.pennant.backend.model.expenses.UploadHeader;
import com.pennant.backend.model.miscPostingUpload.MiscPostingUpload;

public interface MiscPostingUploadService {
	void save(List<MiscPostingUpload> MiscPostingUpload, long uploadId);

	List<MiscPostingUpload> getMiscPostingUploadsByUploadId(long uploadId);

	MiscPostingUpload getMiscPostingUploadByMiscId(long miscPostingId);

	void delete(List<MiscPostingUpload> miscPostingUploadList);

	void updateList(List<MiscPostingUpload> miscPostingUpload);

	void update(MiscPostingUpload miscPostingUpload);

	List<MiscPostingUpload> validateMiscPostingUploads(UploadHeader uploadHeader);

	void insertInJVPosting(UploadHeader uploadHeader);

	List<MiscPostingUpload> validateBasedonTransactionId(List<MiscPostingUpload> list, UploadHeader uploadHeader);

	void deleteByUploadId(long uploadId);
}
