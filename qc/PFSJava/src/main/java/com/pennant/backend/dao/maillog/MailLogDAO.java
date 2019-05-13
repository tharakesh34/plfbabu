package com.pennant.backend.dao.maillog;

import com.pennant.app.util.MailLog;

public interface MailLogDAO {
	void saveMailLog(MailLog mailLog);

	long getMailReference();
}
