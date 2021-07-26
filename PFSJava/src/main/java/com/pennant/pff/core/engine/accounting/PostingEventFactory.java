package com.pennant.pff.core.engine.accounting;

import com.pennant.pff.core.engine.accounting.event.PostingEvent;
import com.pennanttech.pennapps.core.FactoryException;
import com.pennanttech.pff.constants.AccountingEvent;

public class PostingEventFactory {
	private PostingEvent disbInsPostingEvent;
	private PostingEvent vasFeePostingEvent;

	public PostingEvent getAccountingEventEvent(String accEvent) {
		switch (accEvent) {
		case AccountingEvent.DISBINS:
			return disbInsPostingEvent;
		case AccountingEvent.VAS_FEE:
			return vasFeePostingEvent;
		default:
			break;
		}

		throw new FactoryException(
				String.format("[PostingEvent not implemented for the specified event %s].", accEvent));
	}

	public void setDisbInsPostingEvent(PostingEvent disbInsPostingEvent) {
		this.disbInsPostingEvent = disbInsPostingEvent;
	}

	public void setVasFeePostingEvent(PostingEvent vasFeePostingEvent) {
		this.vasFeePostingEvent = vasFeePostingEvent;
	}

}
