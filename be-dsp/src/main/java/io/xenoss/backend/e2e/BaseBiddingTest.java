package io.xenoss.backend.e2e;

import io.xenoss.backend.client.BidderClient;
import io.xenoss.backend.exceptions.NoBidException;
import io.xenoss.backend.exceptions.UnexpectedBidException;
import io.xenoss.backend.model.bidding.BaseExchange;
import io.xenoss.backend.model.bidding.response.trace.BidResponseTrace;
import io.xenoss.backend.model.bidding.response.trace.DecisionStagesEntity;
import io.xenoss.http.Response;
import io.xenoss.utils.ActionTimer;
import io.xenoss.utils.SerializationUtils;
import io.xenoss.utils.WaitUtils;
import lombok.SneakyThrows;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.testng.TestException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

import static java.net.HttpURLConnection.HTTP_NO_CONTENT;
import static java.net.HttpURLConnection.HTTP_OK;
import static org.assertj.core.api.Assertions.assertThat;

public interface BaseBiddingTest {
    Map<ImmutablePair<BaseExchange, Boolean>, BidderClient> bidderClients = new ConcurrentHashMap<>();

    default BidderClient getBidderClient(BaseExchange exchange) {
        return getBidderClient(exchange, false);
    }

    default BidderClient getBidderClient(BaseExchange exchange, boolean gzip) {
        return bidderClients.computeIfAbsent(new ImmutablePair<>(exchange, gzip),
                (pair) -> new BidderClient(pair.getKey(), pair.getValue()));
    }

    default BidderClient getBidderClient(BaseExchange exchange, boolean gzip, BiFunction<BaseExchange, Boolean, ? extends BidderClient> clientConstructor) {
        return bidderClients.computeIfAbsent(new ImmutablePair<>(exchange, gzip),
                (pair) -> clientConstructor.apply(pair.getKey(), pair.getValue()));
    }

    @SneakyThrows
    default Response waitForStartBidding(BaseExchange exchange, String bidRequest) {
        return waitForStartBidding(exchange, bidRequest, 30);
    }

    @SneakyThrows
    default Response waitForStartBidding(BaseExchange exchange, String bidRequest, int timeoutSeconds) {
        var attempts = 0;
        var timer = ActionTimer.start(timeoutSeconds);
        while (!timer.isExpired()) {
            var response = getBidderClient(exchange).sendBidRequest(bidRequest);
            attempts++;
            if (response.getStatusCode() == HTTP_OK) {
                return response;
            }
            WaitUtils.forSeconds(1);
        }

        throw new NoBidException(String.format(
                "No bid response in %s seconds (%s attempts). Response:\n%s",
                attempts,
                timeoutSeconds,
                getBidderClient(exchange).sendBidRequest(bidRequest, true)
                                         .asString()
        ));
    }

    @SneakyThrows
    default Response waitForNoBid(BaseExchange exchange, String bidRequest) {
        final int timeout = 30; // seconds
        var timer = ActionTimer.start(timeout);

        while (!timer.isExpired()) {
            var response = getBidderClient(exchange).sendBidRequest(bidRequest);
            if (response.getStatusCode() == HTTP_NO_CONTENT && response.asString()
                                                                       .isEmpty()) {
                return response;
            }
            WaitUtils.forSeconds(1);
        }

        throw new UnexpectedBidException(String.format(
                "Still bid response in %s seconds. Trace:\n%s",
                timeout,
                getBidderClient(exchange).sendBidRequest(bidRequest, true)
                                         .asString()
        ));
    }

    default Response waitForNoBid(BaseExchange exchange, String bidRequest, BidResponseTrace expectedTrace) {
        var response = waitForNoBid(exchange, bidRequest);
        var actualTrace = getBidderClient(exchange).sendBidRequest(bidRequest, true)
                                                   .asString();
        var actualParsedTrace = parseBidResponseTrace(actualTrace);
        if (expectedTrace.getTargetingTree() != null) {
            checkTargetingTree(actualParsedTrace.getTargetingTree(), expectedTrace.getTargetingTree());
        }
        if (expectedTrace.getDecisionStages() != null) {
            checkDecisionStages(actualParsedTrace.getDecisionStages(), expectedTrace.getDecisionStages());
        }

        return response;
    }

    private static BidResponseTrace parseBidResponseTrace(String trace) {
        var tree = new HashMap<String, List<String>>();
        var found = new ArrayList<String>();
        var traceRows = trace.split("\n");

        var decisionStagesString = new StringBuilder();
        var foundCollectionMode = false;
        var decisionStageCollectionMode = false;

        String treeRecord = null;
        for (var row : traceRows) {
            var trimmedRow = row.trim();
            if (trimmedRow.startsWith("Found:")) {
                foundCollectionMode = true;
            } else if (trimmedRow.startsWith("[") && foundCollectionMode) {
                decisionStageCollectionMode = true;
                decisionStagesString.append(trimmedRow)
                                    .append(System.lineSeparator());
            } else if (trimmedRow.startsWith("(org=")) {
                if (foundCollectionMode) {
                    found.add(trimmedRow);
                } else {
                    treeRecord = trimmedRow;
                    tree.put(treeRecord, new ArrayList<>());
                }
            } else {
                if (decisionStageCollectionMode) {
                    decisionStagesString.append(trimmedRow)
                                        .append(System.lineSeparator());
                } else if (treeRecord != null && tree.get(treeRecord) != null) {
                    tree.get(treeRecord)
                        .add(trimmedRow);
                }
            }
        }

        return BidResponseTrace.builder()
                               .targetingTree(tree)
                               .found(found)
                               .decisionStages(SerializationUtils.fromJson(decisionStagesString.toString(), DecisionStagesEntity.class))
                               .build();

    }

    private static void checkTargetingTree(Map<String, List<String>> actualTree, Map<String, List<String>> expectedTree) {
        for (var expectedTargetingTreeEntry : expectedTree.entrySet()) {
            var expectedNode = actualTree.entrySet()
                                         .stream()
                                         .filter(entry -> entry.getKey()
                                                               .contains(expectedTargetingTreeEntry.getKey()))
                                         .findFirst()
                                         .orElseThrow(() -> new TestException(String.format(
                                                 "No targeting tree record with %s substring found",
                                                 expectedTargetingTreeEntry.getKey())));
            assertThat(expectedNode.getValue())
                    .as("Check nobid reason")
                    .usingComparator(
                            (actual, expected) -> expected.stream()
                                                          .allMatch(expectedRecord -> actual.stream()
                                                                                            .anyMatch(actualRecord -> actualRecord.contains(expectedRecord)))
                                    ? 0
                                    : 1)
                    .isEqualTo(expectedTargetingTreeEntry.getValue());
        }
    }

    private static void checkDecisionStages(DecisionStagesEntity actualDecisionStages, DecisionStagesEntity expectedDecisionStages) {
        BiFunction<String, String, Boolean> comparator =
                (expected, actual) -> expected == null || actual.contains(expected);

        for (var expectedDecisionStage : expectedDecisionStages) {
            var isFound = actualDecisionStages.stream()
                                              .anyMatch(actualDecisionStage -> {
                                                  var actualSelection = actualDecisionStage.getSelection();
                                                  var expectedSelection = expectedDecisionStage.getSelection();

                                                  return expectedSelection == null
                                                          || (actualSelection != null
                                                            && comparator.apply(expectedSelection.getAccount(), actualSelection.getAccount())
                                                            && comparator.apply(expectedSelection.getOrganizationId(), actualSelection.getOrganizationId())
                                                            && comparator.apply(expectedSelection.getLineitem(), actualSelection.getLineitem())
                                                            && comparator.apply(expectedSelection.getCampaign(), actualSelection.getCampaign())
                                                            && comparator.apply(expectedSelection.getCreative(), actualSelection.getCreative())
                                                            && comparator.apply(expectedSelection.getAssets(), actualSelection.getAssets())) // TODO: replace with array search
                                                          && comparator.apply(expectedDecisionStage.getDescription(), actualDecisionStage.getDescription())
                                                          && comparator.apply(expectedDecisionStage.getType(), actualDecisionStage.getType())
                                                          && comparator.apply(expectedDecisionStage.getStatus()
                                                                                                   .name(), actualDecisionStage.getStatus()
                                                                                                                               .name());
                                              });
            assertThat(isFound)
                    .as("The following Decision Stage:\n%s\nis not found in\n%s",
                            expectedDecisionStage.toString(),
                            SerializationUtils.toJson(actualDecisionStages))
                    .isTrue();
        }
    }
}
