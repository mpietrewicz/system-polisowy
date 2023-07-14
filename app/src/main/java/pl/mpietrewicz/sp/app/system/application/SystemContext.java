
package pl.mpietrewicz.sp.app.system.application;


import org.springframework.stereotype.Component;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;

@Component
public class SystemContext {
	
	public SystemUser getSystemUser(){
		return new SystemUser(new AggregateId("1"));//TODO introduce security integration
	}
}