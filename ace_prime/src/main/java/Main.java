import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class Main {
    public static void main(String... a) {
        System.out.println("ACE");

        CloseableHttpClient httpclient = HttpClients.createDefault();
        String query = String.format("http://5.9.247.121/d34dc0d3");
        HttpGet method = new HttpGet(query);
        method.addHeader("X-0x0ACE-Key", getKey());
        try (CloseableHttpResponse execute = httpclient.execute(method)) {
            try (BufferedReader buffer = new BufferedReader(new InputStreamReader(execute.getEntity().getContent()))) {
                String result = buffer.lines().collect(Collectors.joining("\n"));
                System.out.println(result);
                getStream(result);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void getStream(String pageCode) throws UnsupportedEncodingException {
        String input = getHtmlForm(pageCode);
        String firstArg = getLowerPrime(input);
        String secondArg = getHigherPrime(input);

        String collect = IntStream.range(Integer.valueOf(firstArg), Integer.valueOf(secondArg)).skip(1).parallel().filter(value ->
                BigInteger.valueOf(value).isProbablePrime(1)
        ).mapToObj(i -> String.valueOf(i)).collect(Collectors.joining(", "));

        postResponse(collect, getVerificationCode(pageCode));
    }

    private static String getHigherPrime(String input) {
        int last = input.lastIndexOf(", ") + 2;
        int closing = input.lastIndexOf("]");
        String secondArg = input.substring(last, closing);
        System.out.println("secondArg: " + secondArg);
        System.out.println();
        return secondArg;
    }

    private static String getLowerPrime(String input) {
        int firstComma = input.indexOf(",");
        int opening = input.indexOf("[") + 1;
        String firstArg = input.substring(opening, firstComma);
        System.out.println("firstArg: " + firstArg);
        return firstArg;
    }

    private static String getHtmlForm(String code) {
        int start = code.indexOf("class=\"challenge\"");
        int end = code.indexOf("]") + 1;

        return code.substring(start, end);
    }

    private static String getVerificationCode(String code) {
        int hiddenStart = code.indexOf("value=") + 7;
        int hiddenEnd = code.indexOf("==\"") + 2;
        return code.substring(hiddenStart, hiddenEnd);
    }

    private static void postResponse(String stream, String verification) throws UnsupportedEncodingException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        String query = String.format("http://5.9.247.121/d34dc0d3");
        HttpPost method = new HttpPost(query);
        List<NameValuePair> parameters = new ArrayList<>();
        parameters.add(new BasicNameValuePair("verification", verification));
        parameters.add(new BasicNameValuePair("solution", stream));
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(parameters);
        method.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        method.setEntity(entity);
        method.addHeader("X-0x0ACE-Key", getKey());
        try (CloseableHttpResponse execute = httpclient.execute(method)) {
            System.out.println(execute.getStatusLine().getStatusCode());
            try (BufferedReader buffer = new BufferedReader(new InputStreamReader(execute.getEntity().getContent()))) {
                String result = buffer.lines().collect(Collectors.joining("\n"));
                System.out.println(result);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getKey() {
        return "YOUR_KEY";
    }

}
