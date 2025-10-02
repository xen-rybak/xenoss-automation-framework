package io.xenoss.backend.client;

import static java.net.HttpURLConnection.HTTP_OK;

public class FileDownloadClient extends BaseClient {
    public FileDownloadClient() {
        super("");
    }

    public byte[] downloadFile(String path) {
        return get(path).then()
                        .assertThat()
                        .statusCode(HTTP_OK)
                        .extract()
                        .asByteArray();
    }
}
