package pl.mpietrewicz.sp.modules.balance.webui;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import pl.mpietrewicz.sp.cqrs.command.Gate;
import pl.mpietrewicz.sp.ddd.canonicalmodel.publishedlanguage.AggregateId;
import pl.mpietrewicz.sp.modules.balance.readmodel.BalanceFinder;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Map;

@Getter
@RequiredArgsConstructor
@CrossOrigin(origins = {"${cors-origin}"})
@RestController
public class BalanceController {

    private final BalanceFinder balanceFinder;

    private final Gate gate;

    @GetMapping("balance/{contractId}/paidTo")
    public Map<YearMonth, BigDecimal> getBalance(@PathVariable String contractId) {
        return balanceFinder.findPaidTo(new AggregateId(contractId));
    }

}