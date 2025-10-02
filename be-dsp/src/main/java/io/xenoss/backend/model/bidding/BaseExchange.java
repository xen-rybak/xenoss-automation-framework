package io.xenoss.backend.model.bidding;

import io.xenoss.backend.model.creative.OpenRtbPlacementType;
import io.xenoss.config.ConfigurationManager;
import lombok.Getter;

import java.util.Map;

import static io.xenoss.backend.model.bidding.ImpressionType.IB;

@Getter
public abstract class BaseExchange {
    public static final Map<OpenRtbPlacementType, ImpressionType> DEFAULT_MAPPING = Map.of(
            OpenRtbPlacementType.BANNER, IB,
            OpenRtbPlacementType.NATIVE, IB,
            OpenRtbPlacementType.VIDEO, IB,
            OpenRtbPlacementType.AUDIO, IB);

    private static final String DEFAULT_BID_REQUEST_FILES_PATH = "verve";
    private static final String DEFAULT_BID_REQUEST_PATH = "rtb/%s/bid";

    private final String name;
    private final String filesPath;
    private final String url;
    private final Float commission;
    private final Map<OpenRtbPlacementType, ImpressionType> impressionMappings;

    BaseExchange(String name, Map<OpenRtbPlacementType, ImpressionType> impressionMappings) {
        this(
                name,
                name,
                String.format("%s/%s", ConfigurationManager.getConfig()
                                                                    .getEnvironmentConfig()
                                                                    .getBidderUrl(), String.format(DEFAULT_BID_REQUEST_PATH, name)),
                impressionMappings,
                0F);
    }

    public BaseExchange(String name, String url) {
        this(name, url, 0F);
    }

    public BaseExchange(String name, String url, Float commission) {
        this(name, DEFAULT_BID_REQUEST_FILES_PATH, url, DEFAULT_MAPPING, commission);
    }

    public BaseExchange(
            String name,
            String filesPath,
            String url,
            Map<OpenRtbPlacementType, ImpressionType> impressionMappings,
            Float commission) {
        this.url = url;
        this.name = name;
        this.filesPath = filesPath;
        this.commission = commission;
        this.impressionMappings = impressionMappings;
    }
}
