package io.xenoss.backend.client;

import io.xenoss.backend.model.creative.CreativeType;
import io.xenoss.http.Response;
import io.xenoss.backend.model.bidding.response.BidEntity;
import io.xenoss.backend.model.bidding.BaseExchange;
import io.xenoss.backend.model.content.nativead.response.NativeAdResponseEntity;
import io.xenoss.backend.model.content.vast.VastEvent;
import io.xenoss.backend.model.creative.OpenRtbPlacementType;
import io.xenoss.backend.model.content.vast.VastVideo;
import io.xenoss.config.ConfigurationManager;
import io.xenoss.config.EnvironmentConfig;
import io.xenoss.utils.ActionTimer;
import io.xenoss.utils.RandomUtils;


import io.xenoss.utils.RegexUtils;
import io.xenoss.utils.SerializationUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.testng.TestException;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.net.HttpURLConnection.HTTP_MOVED_TEMP;
import static java.net.HttpURLConnection.HTTP_NO_CONTENT;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class BidderClient extends BaseClient {
    // Static final constants
    private static final String BID_REQUEST_WITH_TRACE_PATH = "?trace=true";
    private static final String EVENT_PATH = "t/%s";
    private static final EnvironmentConfig CONFIG = ConfigurationManager.getConfig()
                                                                        .getEnvironmentConfig();

    // Instance fields
    private final BaseExchange exchange;
    private final BaseClient noticesClient;
    private final BaseClient internalTrackerClient;
    private final BaseClient externalTrackerClient;

    public BidderClient(BaseExchange exchange) {
        this(exchange, false);
    }

    public BidderClient(BaseExchange exchange, boolean gzip) {
        super("", gzip);
        this.exchange = exchange;
        this.noticesClient = new BaseClient("") {
        };
        this.internalTrackerClient = new BaseClient(CONFIG.getInternalEventTrackerUrl()) {
        };
        this.externalTrackerClient = new BaseClient(CONFIG.getExternalEventTrackerUrl()) {
        };
    }

    public Response sendBidRequest(String bidRequest) {
        return sendBidRequest(bidRequest, false);
    }

    public Response sendBidRequest(String bidRequest, Boolean trace) {
        return post(trace
                ? String.format("%s%s", exchange.getUrl(), BID_REQUEST_WITH_TRACE_PATH)
                : exchange.getUrl(), bidRequest);
    }

    public Response sendWinNotice(BidEntity bid, CreativeType type) {
        return sendWinNotice(bid, type.getOpenRtbCreativeType());
    }

    public Response sendWinNotice(BidEntity bid, OpenRtbPlacementType type) {
        var auctionPricePlaceholder = "${AUCTION_PRICE}";
        var impressionType = exchange.getImpressionMappings()
                                     .get(type);
        String impressionUrl = switch (impressionType) {
            case IB -> bid.getBurl();
            case IW -> bid.getNurl();
            case IV -> SerializationUtils.fromXml(bid.getAdm(), VastVideo.class)
                                         .getAd()
                                         .getInLine()
                                         .getImpression()
                                         .stream()
                                         .filter(imp -> imp.contains(auctionPricePlaceholder))
                                         .findFirst()
                                         .orElseThrow()
                                         .trim();
            default -> null;
        };

        assert impressionUrl != null;

        if (type == OpenRtbPlacementType.VIDEO) {
            var impTrackers = SerializationUtils.fromXml(bid.getAdm(), VastVideo.class)
                                                .getAd()
                                                .getInLine()
                                                .getImpression()
                                                .stream()
                                                .filter(imp -> !imp.contains(auctionPricePlaceholder))
                                                .toList();
            for (var impTracker : impTrackers) {
                log.info("Triggering Impression Tracker for Video creative...");
                noticesClient.get(impTracker);
            }
        } else if (bid.getAdm() != null) {
            var htmlContent = Jsoup.parse(bid.getAdm());
            var impTrackers = htmlContent.getElementsByTag("img")
                                         .stream()
                                         .filter(img -> img.attribute("height") != null
                                                 && img.attribute("height")
                                                       .getValue()
                                                       .equalsIgnoreCase("1"))
                                         .map(img -> img.attribute("src")
                                                        .getValue())
                                         .toList();
            for (var impTracker : impTrackers) {
                log.info("Triggering Impression Tracker for HTML creative...");
                doClick(impTracker);
            }
        }

        return sendWinNotice(impressionUrl.replace(auctionPricePlaceholder, bid.getPrice()
                                                                               .toString()));
    }

    public Response sendWinNotice(String url) {
        return ActionTimer.waitFor(
                () -> noticesClient.get(url),
                (response) -> response.getStatusCode() == HTTP_NO_CONTENT);
    }

    public Response doClick(BidEntity bid, CreativeType type) {
        return doClick(bid, type.getOpenRtbCreativeType());
    }

    @SneakyThrows
    public Response doClick(BidEntity bid, OpenRtbPlacementType type) {
        if (type.equals(OpenRtbPlacementType.VIDEO)) {
            var vastContent = SerializationUtils.fromXml(bid.getAdm(), VastVideo.class);
            var vastClicks = vastContent.getAd()
                                        .getInLine()
                                        .getCreatives()
                                        .getFirst()
                                        .getLinear()
                                        .getVideoClicks();

            for (var clickTracker : vastClicks.getClickTracking()) {
                log.info("Triggering Click Tracker for Video creative...");
                doClick(clickTracker.trim());
            }
            return doClick(vastClicks.getClickThrough());
        } else if (type.equals(OpenRtbPlacementType.NATIVE)) {
            var nativeContent = bid.getAdm() == null
                    ? SerializationUtils.fromJson(SerializationUtils.toJson(bid.getExt()), NativeAdResponseEntity.class)
                    : SerializationUtils.fromJson(bid.getAdm(), NativeAdResponseEntity.class);

            var linkSection = nativeContent.getNativeAd()
                                           .getLink();
            for (var clickTracker : linkSection.getClicktrackers()) {
                doClick(clickTracker);
            }
            return doClick(linkSection.getUrl());
        } else {
            var htmlContent = Jsoup.parse(bid.getAdm());
            var clickUrl = htmlContent.getElementsByTag("a")
                                      .getFirst()
                                      .attribute("href")
                                      .getValue();
            return doClick(clickUrl);
        }
    }

    public Response doClick(String url) {
        var responseWithoutRedirects = noticesClient.get(url, null, true, false);

        if (responseWithoutRedirects.statusCode() == HTTP_MOVED_TEMP) {
            return noticesClient.get(URLDecoder.decode(responseWithoutRedirects.getHeader("Location"), StandardCharsets.UTF_8));
        } else {
            return responseWithoutRedirects;
        }
    }

    public Response sendInstallEvent(BidEntity bid) {
        return sendPostClickEvent("install", bid);
    }

    public Response sendPostClickEvent(String eventName, BidEntity bid) {
        var impressionId = RegexUtils.extractByRegex(bid.getBurl(), ".*/rtb/(.*)/ib?");
        return externalTrackerClient.get(String.format(EVENT_PATH,
                        Objects.equals(eventName, "install") ? eventName : "event"),
                Map.of(
                        "creativeId", new Object[]{bid.getCrid()},
                        "campaignId", new Object[]{bid.getCid()},
                        "appId", new Object[]{RandomUtils.randomUuid()},
                        "impression", new Object[]{impressionId},
                        "eventName", new Object[]{eventName}
                ));
    }

    public void sendVideoEvents(List<VastEvent> eventNames, BidEntity bid) {
        var vastContent = SerializationUtils.fromXml(bid.getAdm(), VastVideo.class);
        var trackingEvents = vastContent.getAd()
                                        .getInLine()
                                        .getCreatives()
                                        .getFirst()
                                        .getLinear()
                                        .getTrackingEvents();
        for (var eventName : eventNames) {
            assertThat(noticesClient.get(trackingEvents.stream()
                                                       .filter(trackingEntity -> trackingEntity.getEvent()
                                                                                               .equals(eventName))
                                                       .findFirst()
                                                       .orElseThrow(() -> new TestException(String.format("No %s event", eventName)))
                                                       .getValue())
                                    .statusCode())
                    .isEqualTo(HTTP_NO_CONTENT);
        }
    }
}
