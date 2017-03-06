package org.buldakov.wikirace;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.buldakov.wikirace.links.LinkExtractor;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) throws IOException {
        String baseUri = "https://en.wikipedia.org";

        OkHttpClient client = new OkHttpClient();
        HttpUrl endpoint = HttpUrl.parse(baseUri);
        Request request = new Request.Builder()
                .url(endpoint.newBuilder().addPathSegment("wiki").addPathSegment("Matrix").build())
                .build();

        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            throw new IOException("Error: " + response);
        }
        LinkExtractor extractor = new LinkExtractor();
        List<HttpUrl> links = extractor.getLinks(response.body().string(), baseUri)
                .stream().map(HttpUrl::parse)
                .filter(url -> url.pathSize() > 1)
                .filter(url -> url.host().equals(endpoint.host()))
                .filter(url -> url.scheme().equals(endpoint.scheme()))
                .filter(url -> url.port() == endpoint.port())
                .collect(Collectors.toList());

        System.out.println(links);
    }
}
