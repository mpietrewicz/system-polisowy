package pl.mpietrewicz.sp.cqrs.command.handler;

import pl.mpietrewicz.sp.cqrs.annotations.Command;

/**
 * 
 * @author Slawek
 *
 * @param <C> command
 * @param <R> result type - for asynchronous {@link Command}commands (asynchronous=true) should be {@link Void}
 */
public interface CommandHandler<C, R> {

    public R handle(C command);
}