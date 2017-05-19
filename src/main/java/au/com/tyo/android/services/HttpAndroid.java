package au.com.tyo.android.services;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.MethodOverride;
import com.google.api.client.http.EmptyContent;
import com.google.api.client.http.GZipEncoding;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpContent;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpMethods;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpResponseInterceptor;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.UrlEncodedContent;
import com.google.api.client.http.json.JsonHttpContent;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Preconditions;

import java.io.IOException;
import java.io.InputStream;

import au.com.tyo.services.HttpConnection;

;

/**
 * Created by Eric Tang (eric.tang@tyo.com.au) on 16/5/17.
 *
 * most code is from Google JAVA API Client implementation
 *
 * TODO
 *
 * please be noted this class is partly implemented
 *
 */

public class HttpAndroid extends HttpConnection {

    /** HTTP method. */
    private String requestMethod;

    /** URI template for the path relative to the base URL. */
    private String uriTemplate;

    /** HTTP content or {@code null} for none. */
    private HttpContent httpContent;

    /** HTTP headers used for the Google client request. */
    private HttpHeaders requestHeaders = new HttpHeaders();

    /** HTTP headers of the last response or {@code null} before request has been executed. */
    private HttpHeaders lastResponseHeaders;

    /** Status code of the last response or {@code -1} before request has been executed. */
    private int lastStatusCode = -1;

    /** Status message of the last response or {@code null} before request has been executed. */
    private String lastStatusMessage;

    /** Whether to disable GZip compression of HTTP content. */
    private boolean disableGZipContent;

    private HttpRequestFactory httpRequestFactory;

    public static class DisableTimeout implements HttpRequestInitializer {
        public void initialize(HttpRequest request) {
            request.setConnectTimeout(0);
            request.setReadTimeout(0);
        }
    }

    public static HttpRequestFactory createRequestFactory(HttpTransport transport) {
        final DisableTimeout disableTimeout = new DisableTimeout();
        return transport.createRequestFactory(new HttpRequestInitializer() {
            public void initialize(HttpRequest request) {
                disableTimeout.initialize(request);
            }
        });
    }

    final DisableTimeout disableTimeout = new DisableTimeout();

    public HttpAndroid() {
        httpRequestFactory = createRequestFactory(AndroidHttp.newCompatibleTransport());
    }

    @Override
    public String upload(String url, Settings settings) throws Exception {
        return null;
    }

    @Override
    public String post(Settings settings, int postMethod) throws Exception {
        return null;
    }

    @Override
    public String get(String url, long storedModifiedDate, boolean keepAlive) throws Exception {
        return connect(url);
    }

    @Override
    public void setHeaders(Object[] objects) {

    }

    @Override
    public String getUrl() {
        return null;
    }

    @Override
    public String createCookieFile() {
        return null;
    }

    protected IOException newExceptionOnError(HttpResponse response) {
        return new HttpResponseException(response);
    }

    /**
     *
     * @param url
     * @return
     */
    private HttpRequest buildHttpRequest(String url) throws IOException {
        return buildHttpRequest(HttpMethods.GET, url, null);
    }

    /**
     *
     * @param requestMethod
     * @param url
     * @param content
     * @return
     * @throws IOException
     */
    private HttpRequest buildHttpRequest(String requestMethod, String url, HttpContent content) throws IOException {
        Preconditions.checkArgument(true);
        if (null == requestMethod)
            requestMethod = HttpMethods.GET;
        Preconditions.checkArgument(requestMethod.equals(HttpMethods.GET));
        final HttpRequest httpRequest = httpRequestFactory.buildRequest(requestMethod, new GenericUrl(url), content);
        new MethodOverride().intercept(httpRequest);

        // custom methods may use POST with no content but require a Content-Length header
        if (content == null && (requestMethod.equals(HttpMethods.POST)
                || requestMethod.equals(HttpMethods.PUT) || requestMethod.equals(HttpMethods.PATCH))) {
            httpRequest.setContent(new EmptyContent());
        }
        httpRequest.getHeaders().putAll(requestHeaders);
        if (!disableGZipContent) {
            httpRequest.setEncoding(new GZipEncoding());
        }
        final HttpResponseInterceptor responseInterceptor = httpRequest.getResponseInterceptor();
        httpRequest.setResponseInterceptor(new HttpResponseInterceptor() {

            public void interceptResponse(HttpResponse response) throws IOException {
                if (responseInterceptor != null) {
                    responseInterceptor.interceptResponse(response);
                }
                if (!response.isSuccessStatusCode() && httpRequest.getThrowExceptionOnExecuteError()) {
                    throw newExceptionOnError(response);
                }
            }
        });
        return httpRequest;
    }

    private String connect(String url) throws IOException {
        HttpRequest request = buildHttpRequest(url);
        HttpResponse response = request.execute();
        return httpInputStreamToText(response.getContent());
    }

    /**
     * post urlencoded content
     *
     * @param url
     * @param settings
     * @return
     * @throws Exception
     */
    @Override
    public InputStream post(String url, Settings settings) throws Exception {
        UrlEncodedContent content = new UrlEncodedContent(settings.paramsToMap());
        return post(url, content);
    }

    /**
     *
     * @param url
     * @param json
     * @return
     * @throws IOException
     */
    @Override
    public InputStream postJSON(String url, Object json) throws IOException {
        JsonHttpContent content = new JsonHttpContent(new JacksonFactory(), json);


        // not needed
//        final HttpHeaders httpHeaders = new HttpHeaders();
//        httpHeaders.setContentType("application/json");
//        request.setHeaders(httpHeaders);

        return post(url, content);
    }

    private InputStream post(String url, HttpContent content) throws IOException {
        HttpRequest request = buildHttpRequest(HttpMethods.POST, url, content);
        HttpResponse response = request.execute();
        return response.getContent();
    }
}