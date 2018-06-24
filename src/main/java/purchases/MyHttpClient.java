package purchases;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

public class MyHttpClient {

	private final static String USER_AGENT = "Mozilla/5.0";
	private final static String ACCESS_KEY = "a06235cb5b5a8ac7f61dd9c853951cc1";
	private final static String ENDPOINT = "latest";

	public static StringBuffer sendGet() throws UnsupportedOperationException, IOException{
		String url = "http://data.fixer.io/api/" + ENDPOINT + "?access_key=" + ACCESS_KEY;

		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(url);

		request.addHeader("User-Agent", USER_AGENT);

		HttpResponse response = client.execute(request);

		BufferedReader rd = new BufferedReader(
                       new InputStreamReader(response.getEntity().getContent()));

		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null) {	
			result.append(line);
		}

		return result;

	}	
}
