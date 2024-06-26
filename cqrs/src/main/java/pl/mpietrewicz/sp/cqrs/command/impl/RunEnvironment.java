package pl.mpietrewicz.sp.cqrs.command.impl;

import org.springframework.stereotype.Component;
import pl.mpietrewicz.sp.cqrs.command.handler.CommandHandler;

import javax.inject.Inject;

/**
 * @author Slawek
 */
@Component
public class RunEnvironment {

	public interface HandlersProvider{
		CommandHandler<Object, Object> getHandler(Object command);
	}
	
	@Inject
	private HandlersProvider handlersProfiver;
	
	public Object run(Object command) {		
		CommandHandler<Object, Object> handler = handlersProfiver.getHandler(command);
		
		//You can add Your own capabilities here: dependency injection, security, transaction management, logging, profiling, spying, storing commands, etc
		
		Object result = handler.handle(command);

		//You can add Your own capabilities here
		
		return result;
	}

}