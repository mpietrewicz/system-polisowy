package pl.mpietrewicz.sp.modules.balance.domain.balance;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class TestData {

    private final String operation;
    private final String registration;
    private final String change;
    private final String amount;

}