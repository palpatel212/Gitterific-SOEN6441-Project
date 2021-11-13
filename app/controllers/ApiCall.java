package controllers;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ApiCall {
	
	public static CompletionStage<JSONObject> getApiCall(String url, HashMap<String, String> queryParamters) {
		CompletableFuture<JSONObject> future = new CompletableFuture<>();
		JSONObject jsonObject = null;
		
		try {
			URIBuilder builder = new URIBuilder(url);
			for(Map.Entry<String, String> entry: queryParamters.entrySet()) {
				builder.addParameter(entry.getKey(),entry.getValue());
			}
			
			CloseableHttpClient httpclient = HttpClients.createDefault();
			HttpResponse resp = null;
			
			HttpGet getAPI = new HttpGet(builder.build());
    		resp = httpclient.execute(getAPI);
    		
    		StatusLine statusLine = resp.getStatusLine();
            System.out.println(statusLine.getStatusCode() + " " + statusLine.getReasonPhrase());
            String responseBody = EntityUtils.toString(resp.getEntity(), StandardCharsets.UTF_8);
            System.out.println(responseBody.length());
            
            try {
	        	jsonObject = new JSONObject(responseBody);
	        } catch (JSONException err){
			     err.printStackTrace();
			}
			
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		future.complete(jsonObject);
		return future;
	}
	
}
