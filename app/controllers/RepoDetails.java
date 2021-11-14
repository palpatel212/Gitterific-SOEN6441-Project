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


public class RepoDetails {
	static public List<Repository> repos = new ArrayList<Repository>();
	public static void setRepoDetails(JSONObject repository) {
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
		obj.setRepoName(repository.getString("name"));
		
		Number id= repository.getNumber("id");
		String idtemp=id.toString();
		System.out.println("ID String"+idtemp);
		obj.setId(idtemp);
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
	
	public static List<Repository> getRepoDetails(String word) {
//		CompletableFuture<List<Repository>> future = new CompletableFuture<>();
		
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("accept", "application/vnd.github.v3+json");
		map.put("per_page", "10");
		map.put("q", word);
		
		String url = "https://api.github.com/search/repositories";
		
		ApiCall.getApiCall(url, map).thenAccept(responseBody -> {
			JSONObject json = new JSONObject(responseBody);
			System.out.println(json.toString(4));
//			CompletionStage<Done> result = cache.set("item.key", json.toString());
			repos.clear();
			org.json.JSONArray array = json.getJSONArray("items");
			
			ArrayList<JSONObject> listData = new ArrayList<JSONObject>();
			for(int i=0; i<array.length();i++) {
				listData.add(array.optJSONObject(i));
			}
			
			listData.parallelStream().forEach(RepoDetails::setRepoDetails);
		});
		
		//		future.complete(repos);
		
		return repos;
	}
}