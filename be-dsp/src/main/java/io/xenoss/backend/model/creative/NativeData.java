package io.xenoss.backend.model.creative;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigInteger;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class NativeData {
    private String sponsored;
    private String description;
    private Integer rating;
    private BigInteger likes;
    private BigInteger downloads;
    private String price;
    private String displayurl;
    private String ctatext;
}
