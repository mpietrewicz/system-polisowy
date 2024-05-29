package pl.mpietrewicz.sp.modules.contract.readmodel.model;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Builder
@Getter
public class TerminateComponent {

    private LocalDate terminatedDate;

}