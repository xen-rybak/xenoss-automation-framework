package io.xenoss.backend.model.smtp4dev;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class SmtpResponse {
    private List<SmtpResult> results;
    private Integer currentPage;
    private Integer pageCount;
    private Integer pageSize;
    private Integer rowCount;
    private Integer firstRowOnPage;
    private Integer lastRowOnPage;
}
