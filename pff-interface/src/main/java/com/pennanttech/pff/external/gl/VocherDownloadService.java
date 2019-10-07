package com.pennanttech.pff.external.gl;

import java.util.Date;

public interface VocherDownloadService {
	void downloadVocher(long userId, String userName, Date fromDate, Date toDate);
}
