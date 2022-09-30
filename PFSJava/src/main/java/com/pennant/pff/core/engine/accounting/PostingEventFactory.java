package com.pennant.pff.core.engine.accounting;

import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.pff.core.engine.accounting.event.PostingEvent;
import com.pennanttech.pennapps.core.FactoryException;
import com.pennanttech.pff.constants.AccountingEvent;

public class PostingEventFactory {
	private PostingEvent disbInsPostingEvent;
	private PostingEvent vasFeePostingEvent;
	private PostingEvent bouncePostingEvent;

	public PostingEvent getAccountingEventEvent(String accEvent) {
		switch (accEvent) {
		case AccountingEvent.DISBINS:
			return disbInsPostingEvent;
		case AccountingEvent.VAS_FEE:
			return vasFeePostingEvent;
		case AccountingEvent.MANFEE:
			return bouncePostingEvent;
		default:
			break;
		}

		throw new FactoryException(
				String.format("[PostingEvent not implemented for the specified event %s].", accEvent));
	}

	@Autowired
	public void setDisbInsPostingEvent(PostingEvent disbInsPostingEvent) {
		this.disbInsPostingEvent = disbInsPostingEvent;
	}

	@Autowired
	public void setVasFeePostingEvent(PostingEvent vasFeePostingEvent) {
		this.vasFeePostingEvent = vasFeePostingEvent;
	}

	@Autowired
	public void setBouncePostingEvent(PostingEvent bouncePostingEvent) {
		this.bouncePostingEvent = bouncePostingEvent;
	}

}
