package com.pennanttech.pff.external.merchant;

import java.io.File;

import org.zkoss.util.media.Media;

import com.pennanttech.dataengine.model.DataEngineStatus;

public interface MerchantUTRProcess {
	public void processMerchantUTRUpload(long userId, File file, Media media, DataEngineStatus status) throws Exception;
}
