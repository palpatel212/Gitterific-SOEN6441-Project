package controllers;

import java.io.IOException;

import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import models.Repository;
import models.User;
import play.mvc.Controller;
import javax.inject.Inject;

import play.cache.*;
import play.mvc.*;
import javax.inject.Inject;


public class Search extends Controller{

	static public List<Repository> repos = new ArrayList<Repository>();
	static public List<Repository> repoDetail = new ArrayList<Repository>();
	private AsyncCacheApi cache;
	
	public static CompletionStage<JSONObject> searchRepos(String word) {
		CompletableFuture<JSONObject> future = new CompletableFuture<>();
		JSONObject jsonObject = null;
		try {
			
			URIBuilder builder = new URIBuilder("https://api.github.com/search/repositories");
			builder.addParameter("accept", "application/vnd.github.v3+json");
			builder.addParameter("per_page", "10");
			builder.addParameter("q", word);
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
			
		} catch (URISyntaxException | IOException | RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		future.complete(jsonObject);
		return future;
	}
	
	public static void getInfoFromJson(JSONObject repository) {
		Repository obj = new Repository();
		
		obj.setVisibility(repository.getString("visibility"));
		obj.setForks(repository.getInt("forks"));
		obj.setWatchers_count(repository.getInt("watchers_count"));
		obj.setScore(repository.getInt("score"));
		obj.setStars(repository.getInt("stargazers_count"));
		obj.setCreatedAt(repository.getString("created_at").substring(0,10));
		
		
		
		JSONObject owner = (JSONObject) repository.get("owner");
		
		obj.setLogin(owner.getString("login"));
		obj.setRepourl(owner.getString("repos_url"));
//		obj.setCreatedAt(repository.getString("created_at"));
		obj.setRepoName(repository.getString("name"));
		
		Number id= repository.getNumber("id");
		String idtemp=id.toString();
		System.out.println("ID String"+idtemp);
		obj.setId(idtemp);
		
//		obj.setUpdatedAt(repository.getString("updated_at"));
		obj.setGitCommitsurl(repository.getString("git_commits_url"));
		obj.setCommitsUrl(repository.getString("commits_url"));
		obj.setIssuesUrl(repository.getString("issues_url"));
		
		JSONArray arr = repository.getJSONArray("topics");
		ArrayList<String> topics = new ArrayList<String>();
		for(int i = 0;i< arr.length();i++) {
			topics.add(arr.getString(i));
		}
		
		obj.setTopics(topics);
		repos.add(obj);
	}
	
	public static CompletionStage<List<Repository>> getRepoAndUserDetails(String word) {
		CompletableFuture<List<Repository>> future = new CompletableFuture<>();
		
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("accept", "application/vnd.github.v3+json");
		map.put("per_page", "10");
		map.put("q", word);
		
		String url = "https://api.github.com/search/repositories";
		
		ApiCall.getApiCall(url, map).thenAccept(json -> {
			System.out.println(json.toString(4));
			
//			CompletionStage<Done> result = cache.set("item.key", json.toString());
			repos.clear();
			org.json.JSONArray array = json.getJSONArray("items");
			
			ArrayList<JSONObject> listData = new ArrayList<JSONObject>();
			for(int i=0; i<array.length();i++) {
				listData.add(array.optJSONObject(i));
			}
			
			listData.parallelStream().forEach(Search::getInfoFromJson);
		});
		
		future.complete(repos);
		
		return future;
	}
	
}