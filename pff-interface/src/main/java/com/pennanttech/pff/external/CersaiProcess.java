package com.pennanttech.pff.external;

import java.io.File;

import org.zkoss.util.media.Media;

import com.pennant.backend.util.PennantConstants;
import com.pennanttech.dataengine.model.DataEngineStatus;

public interface CersaiProcess {
	public static DataEngineStatus CERSAI_IMPORT = new DataEngineStatus(PennantConstants.CERSAI_IMPORT);

	public void processResponseFile(long userId, File file, Media media) throws Exception;

	void processResponseFile(long userId, File file, Media media, DataEngineStatus ds) throws Exception;

}
