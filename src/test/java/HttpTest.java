import org.openflamingo.uploader.util.ResourceUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import sun.misc.IOUtils;

import java.net.URI;

/**
 * Description.
 *
 * @author Edward KIM
 * @since 1.0
 */
public class HttpTest {

    public static void main(String[] args) throws Exception {
        HttpTest.invokeHttpClient4();
        // HttpTest.invokeSimpleClient();
    }
    public static void invokeHttpClient4() throws Exception {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        ClientHttpRequest request = factory.createRequest(
            new URI("http://openapi.seoul.go.kr:8088/json/4150495f3231323066686172656e68656974/서울시%20강우량%20정보/1/1000000000"),
            HttpMethod.GET
        );
        ClientHttpResponse response = request.execute();
        System.out.println(response.getStatusText());
        System.out.println(response.getRawStatusCode());
        IOUtils.readFully(response.getBody(), 4096, false);
        response.close();
    }

    public static void invokeSimpleClient() throws Exception {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        ClientHttpRequest request = factory.createRequest(
            new URI("http://openapi.seoul.go.kr:8088/json/4150495f3231323066686172656e68656974/서울시%20강우량%20정보/1/1000000000"),
            HttpMethod.GET);
        ClientHttpResponse response = request.execute();
        System.out.println(response.getStatusText());
        System.out.println(response.getRawStatusCode());
        System.out.println(ResourceUtils.getResourceTextContents(response.getBody()));
        response.close();
    }
}
