package io.xenoss.backend.model.bidding.response;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.Map;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class BidEntity {
    private String id;
    private String impid;
    private String adid;
    private String nurl;
    private String burl;
    private String lurl;
    private String adm;
    private String iurl;
    private String cid;
    private String crid;
    private String dealid;
    private List<Integer> attr;
    private Integer h;
    private Integer w;
    private Integer protocol;
    private Integer qagmediarating;
    private Float price;
    private List<String> adomain;
    private Map<String, Object> ext;
}
