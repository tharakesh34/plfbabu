package com.pennant.backend.dao.finance;

import java.util.List;

import com.pennant.backend.model.miscPostingUpload.MiscPostingUpload;

public interface MiscPostingUploadDAO {
	List<MiscPostingUpload> getMiscPostingUploadsByUploadId(long uploadId, String type);

	MiscPostingUpload getMiscPostingUploadsByMiscId(long miscPostingId, String type);

	void update(MiscPostingUpload miscPostingUpload);

	void updateList(List<MiscPostingUpload> miscPostingUpload);

	String save(MiscPostingUpload miscPostingUpload);

	void saveList(List<MiscPostingUpload> miscPostingUpload);

	void deleteByUploadId(long uploadId);

	boolean getMiscPostingUploadsByReference(String reference, long uploadId, String type);

	long getMiscPostingBranchSeq();
}
