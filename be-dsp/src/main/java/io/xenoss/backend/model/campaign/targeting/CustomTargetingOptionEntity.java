
package io.xenoss.backend.model.campaign.targeting;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
//@SuperBuilder
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class CustomTargetingOptionEntity<T> {
    // Instance fields
    private List<T> whitelist;
    private List<T> blacklist;
    private Boolean whitelistand;

    // Static methods
    public static <T> TargetingOptionEntityBuilder<T> builder() {
        return new TargetingOptionEntityBuilder<T>()
                .whitelistand(false)
                .blacklist(null)
                .whitelist(null);
    }

    public static class TargetingOptionEntityBuilder<T> {
        private List<T> wl;
        private List<T> bl;
        private Boolean wla;

        public TargetingOptionEntityBuilder<T> whitelist(List<T> list) {
            this.wl = list;
            return this;
        }
        public TargetingOptionEntityBuilder<T> blacklist(List<T> list) {
            this.bl = list;
            return this;
        }
        public TargetingOptionEntityBuilder<T> whitelistand(Boolean wla) {
            this.wla = wla;
            return this;
        }

        public CustomTargetingOptionEntity<T> build() {
            var toe = new CustomTargetingOptionEntity<T>();
            toe.setWhitelist(wl);
            toe.setBlacklist(bl);
            toe.setWhitelistand(wla);

            return toe;
        }

    }
}

