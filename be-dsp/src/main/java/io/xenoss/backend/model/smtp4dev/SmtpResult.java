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
public class SmtpResult {
    private Boolean isRelayed;
    private String deliveredTo;
    private String id;
    private String from;
    private List<String> to;
    private String receivedDate;
    private String subject;
    private Integer attachmentCount;
    private Boolean isUnread;
}
