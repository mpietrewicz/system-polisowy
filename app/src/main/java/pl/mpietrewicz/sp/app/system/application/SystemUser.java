
package pl.mpietrewicz.sp.app.system.application;


import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;

/**
 * Application model of the logged user
 * 
 * TODO introduce roles model
 * 
 * @author Slawek
 *
 */
public class SystemUser {

	private AggregateId clientId;
		
	SystemUser(AggregateId clientId) {
		this.clientId = clientId;
	}

	/**
	 * 
	 * @return Domain model Client
	 */
	public AggregateId getClientId(){
		return clientId;
	}
	
	public boolean isLoogedIn(){
		return clientId != null;
	}
}