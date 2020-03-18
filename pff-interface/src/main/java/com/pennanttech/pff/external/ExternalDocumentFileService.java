package com.pennanttech.pff.external;

import com.pennanttech.pennapps.dms.model.DMSQueue;

public interface ExternalDocumentFileService {

	String store(DMSQueue dmsQueue);

	DMSQueue retrieve(DMSQueue dmsQueue);
}
