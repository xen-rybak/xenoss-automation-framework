package io.xenoss.testdata;

import io.xenoss.backend.model.content.nativead.request.iab.NativeAssetRequestEntity;
import io.xenoss.backend.model.content.nativead.request.iab.NativeTitleRequestEntity;
import io.xenoss.backend.model.content.nativead.request.iab.NativeDataRequestEntity;
import io.xenoss.backend.model.content.nativead.request.iab.NativeAssetType;
import io.xenoss.backend.model.content.nativead.request.iab.NativeImgRequestEntity;
import io.xenoss.backend.model.content.nativead.request.iab.NativeVideoRequestEntity;
import io.xenoss.backend.model.content.nativead.request.iab.NativeAdRequestEntity;
import io.xenoss.backend.model.content.nativead.request.iab.NativeSubSectionEntity;

import java.util.List;

public class BaseNativeAdData {
    public NativeAssetRequestEntity getTitleAsset(int id, boolean required, int maxLength) {
        return NativeAssetRequestEntity.builder()
                                       .id(id)
                                       .required(required ? 1 : 0)
                                       .title(NativeTitleRequestEntity.builder()
                                                                      .len(maxLength)
                                                                      .build())
                                       .build();
    }

    public NativeAssetRequestEntity getDataAsset(int id, boolean required, NativeAssetType type, int maxLength) {
        return NativeAssetRequestEntity.builder()
                                       .id(id)
                                       .required(required ? 1 : 0)
                                       .data(NativeDataRequestEntity.builder()
                                                                    .type(type.getValue())
                                                                    .len(maxLength)
                                                                    .build())
                                       .build();
    }

    public NativeAssetRequestEntity getImageAsset(
            int id, boolean required, NativeAssetType type, int hMin, int wMin) {
        return getImageAsset(id, required, type, hMin, wMin, null);
    }

    public NativeAssetRequestEntity getImageAsset(
            int id, boolean required, NativeAssetType type, int hMin, int wMin, List<String> mimes) {
        return NativeAssetRequestEntity.builder()
                                       .id(id)
                                       .required(required ? 1 : 0)
                                       .img(NativeImgRequestEntity.builder()
                                                                  .type(type.getValue())
                                                                  .hmin(hMin)
                                                                  .wmin(wMin)
                                                                  .mimes(mimes)
                                                                  .build())
                                       .build();
    }

    public NativeAssetRequestEntity getVideoAsset(
            int id, boolean required, List<String> mimes, int minDuration, int maxDuration, List<Integer> protocols) {
        return NativeAssetRequestEntity.builder()
                                       .id(id)
                                       .required(required ? 1 : 0)
                                       .video(NativeVideoRequestEntity.builder()
                                                                      .mimes(mimes)
                                                                      .minduration(minDuration)
                                                                      .maxduration(maxDuration)
                                                                      .protocols(protocols)
                                                                      .build())
                                       .build();
    }

    // Overload with explicit parameters
    protected List<NativeAssetRequestEntity> getFullImageAssetsList(
            int mediumTitleLength,
            int logoHeight,
            int logoWidth,
            int imageHeight,
            int imageWidth,
            String imageMimeType,
            int downloadsLength,
            int descriptionLength,
            int priceLength,
            int ratingLength,
            int sponsoredLength,
            int displayUrlLength,
            int ctaTextLength
    ) {
        return List.of(
                getTitleAsset(0, true, mediumTitleLength),
                getImageAsset(1, true, NativeAssetType.ICON, logoHeight, logoWidth),
                getImageAsset(2, true, NativeAssetType.MAIN, imageHeight, imageWidth, List.of(imageMimeType)),
                getDataAsset(3, true, NativeAssetType.DOWNLOADS, downloadsLength),
                getDataAsset(4, true, NativeAssetType.DESC, descriptionLength),
                getDataAsset(5, true, NativeAssetType.PRICE, priceLength),
                getDataAsset(6, true, NativeAssetType.RATING, ratingLength),
                getDataAsset(7, true, NativeAssetType.SPONSORED, sponsoredLength),
                getDataAsset(8, true, NativeAssetType.DISPLAYURL, displayUrlLength),
                getDataAsset(9, true, NativeAssetType.CTATEXT, ctaTextLength)
        );
    }

    // Overload with explicit parameters
    protected List<NativeAssetRequestEntity> getMinimalImageAssetsList(
            int shortTitleLength,
            int logoHeight,
            int logoWidth,
            int imageHeight,
            int imageWidth,
            String imageMimeType,
            int downloadsLength,
            int descriptionLength,
            int priceLength,
            int ratingLength,
            int sponsoredLength
    ) {
        return List.of(
                getTitleAsset(0, true, shortTitleLength + 1),
                getImageAsset(1, false, NativeAssetType.ICON, logoHeight + 1, logoWidth),
                getImageAsset(2, true, NativeAssetType.MAIN, imageHeight - 1, imageWidth - 1, List.of(imageMimeType)),
                getDataAsset(3, false, NativeAssetType.DOWNLOADS, downloadsLength + 1),
                getDataAsset(4, false, NativeAssetType.DESC, descriptionLength - 1),
                getDataAsset(5, false, NativeAssetType.PRICE, priceLength),
                getDataAsset(6, false, NativeAssetType.RATING, ratingLength),
                getDataAsset(7, false, NativeAssetType.SPONSORED, sponsoredLength)
        );
    }


    public NativeAdRequestEntity getMinNativeImageAsset(
            int titleMaxLength, int imageMinH, int imageMinW, List<String> mimes, int descriptionMaxLength) {
        var assets = List.of(
                getTitleAsset(0, true, titleMaxLength),
                getImageAsset(1, true, NativeAssetType.MAIN, imageMinH, imageMinW, mimes),
                getDataAsset(2, true, NativeAssetType.DESC, descriptionMaxLength)
        );
        return NativeAdRequestEntity.builder()
                                    .nativeAd(NativeSubSectionEntity.builder()
                                                                    .ver("1")
                                                                    .assets(assets)
                                                                    .build())
                                    .build();
    }
}
