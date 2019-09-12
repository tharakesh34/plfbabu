package com.pennant.backend.service.filedownload;

import java.util.List;

public interface LimitDownloadService {

	boolean processDownload(List<Long> limitHeaderIds) throws Exception;

	String getFileName();
}
