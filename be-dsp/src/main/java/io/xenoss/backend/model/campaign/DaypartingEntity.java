package io.xenoss.backend.model.campaign;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class DaypartingEntity {
    private Integer dayOfWeek;
    private List<Integer> halfHourOfDay;
}
