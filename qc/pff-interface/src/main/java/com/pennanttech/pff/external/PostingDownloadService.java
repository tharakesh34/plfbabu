package com.pennanttech.pff.external;

import java.util.Date;

public interface PostingDownloadService {

	void sendPostings(Date postingDate, long userId) throws Exception;

}
