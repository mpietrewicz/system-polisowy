package pl.mpietrewicz.sp.app.system.infrastructure.events.impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class PendingPublication {

    private final LocalDateTime created = LocalDateTime.now();
    private final Object event;
    private final String boundedContext;

}