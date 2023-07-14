
package pl.mpietrewicz.sp.ddd.sharedkernel.exceptions;

import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;

@SuppressWarnings("serial")
public class DomainOperationException extends RuntimeException{

	private AggregateId aggregateId;

	public DomainOperationException(AggregateId aggregateId, String message){
		super(message);
		this.aggregateId = aggregateId;
	}
	
	public AggregateId getAggregateId() {
		return aggregateId;
	}
}