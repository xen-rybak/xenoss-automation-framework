package io.xenoss.testdata;

import io.xenoss.backend.model.bidding.BaseExchange;
import io.xenoss.utils.RandomUtils;

import java.util.Map;
import java.util.Objects;

import static io.xenoss.utils.FileUtils.getResourceFileAsString;

public class BidRequestData {
    public String getBidRequest(BaseExchange exchange, String fileName, Map<String, Object> params) {
        return getBidRequest(String.format("bidrequests/%s/%s.json", exchange.getFilesPath()
                                                                             .toLowerCase(), fileName), params);
    }

    public String getBidRequest(String filePath, Map<String, Object> params) {
        var bidRequest = getResourceFileAsString(filePath);
        for (var param : params.entrySet()) {
            var replacementString = param.getValue() == null
                    ? "null"
                    : param.getValue()
                           .toString();
            var replacementPattern = Objects.equals(replacementString, "null")
                    || (!param.getKey()
                              .contains("_str") && (replacementString.trim()
                                                                     .startsWith("{") || replacementString.trim()
                                                                                                          .startsWith("[")))
                    || (replacementString.startsWith("number:"))
                    ? "\"${%s}\""
                    : "${%s}";
            bidRequest = bidRequest.replace(String.format(replacementPattern, param.getKey()),
                    replacementString.replace("number:", ""));
        }
        while (!bidRequest.equals(bidRequest.replace("${uuid}", RandomUtils.randomUuid()))) {
            bidRequest = bidRequest.replace("${uuid}", RandomUtils.randomUuid());
        }

        return bidRequest;
    }
}
