package io.github.karMiguel.capzip.dtos.clickDto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClicksByPeriodDTO {

    private long countMorning = 0;
    private long countAfternoon = 0 ;
    private long countNight = 0;
    private long countDawn = 0;
}