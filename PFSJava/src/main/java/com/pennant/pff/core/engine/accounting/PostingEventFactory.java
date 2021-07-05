package com.pennant.pff.core.engine.accounting;

import com.pennant.app.constants.AccountingEvent;
import com.pennant.pff.core.engine.accounting.event.PostingEvent;
import com.pennanttech.pennapps.core.FactoryException;

public class PostingEventFactory {
	private PostingEvent disbInsPostingEvent;
	private PostingEvent vasFeePostingEvent;

	public PostingEvent getAccountingEventEvent(AccountingEvent event) {
		switch (event) {
		case DISBINS:
			return disbInsPostingEvent;
		case VASFEE:
			return vasFeePostingEvent;
		default:
			break;
		}

		throw new FactoryException(
				String.format("[PostingEvent not implemented for the specified event %s].", event.name()));
	}

	public void setDisbInsPostingEvent(PostingEvent disbInsPostingEvent) {
		this.disbInsPostingEvent = disbInsPostingEvent;
	}

	public void setVasFeePostingEvent(PostingEvent vasFeePostingEvent) {
		this.vasFeePostingEvent = vasFeePostingEvent;
	}

}
