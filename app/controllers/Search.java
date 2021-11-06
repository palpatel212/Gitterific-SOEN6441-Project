package controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;

import java.util.ArrayList;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class Search {

public static JSONObject searchRepos(ArrayList<String> terms) {
		
		JSONObject jsonObject = null;
		try {
			URIBuilder builder = new URIBuilder("https://api.github.com/search/repositories");
			builder.addParameter("accept", "application/vnd.github.v3+json");
			builder.addParameter("per_page", "10");
			CloseableHttpClient httpclient = HttpClients.createDefault();

			HttpResponse resp = null;
			
			for(int i = 0;i<terms.size();i++) {
				builder.addParameter("q", terms.get(i));
			}
			
			HttpGet getAPI = new HttpGet(builder.build());
			resp = httpclient.execute(getAPI);
			
			StatusLine statusLine = resp.getStatusLine();
	        System.out.println(statusLine.getStatusCode() + " " + statusLine.getReasonPhrase());
	        String responseBody = EntityUtils.toString(resp.getEntity(), StandardCharsets.UTF_8);
	        System.out.println(responseBody.length());
	        
			try {
			     jsonObject = new JSONObject(responseBody);
			}catch (JSONException err){
			     err.printStackTrace();
			}
			
		} catch (URISyntaxException | IOException | RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jsonObject;
	}

	
	public static void main(String[] args) {
		ArrayList<String> terms = new ArrayList<>();
		terms.add("Apache");
		terms.add("kafka");
		
		JSONObject json = searchRepos(terms);
		System.out.println(json.toString(4));
	}

}
