package controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import models.Repository;
import play.data.FormFactory;
import play.mvc.Controller;
import scala.util.parsing.json.JSONArray;

public class Search extends Controller{

	@Inject
	FormFactory formFactory;
	static public List<Repository> repos = new ArrayList<Repository>();
	static public List<Repository> repoDetail = new ArrayList<Repository>();
	
	
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
	
	

	
	public static List<Repository> findrepo(List<String> keywords) {
		
		ArrayList<String> terms = new ArrayList<>();
		for(String k: keywords) {
			terms.add(k);
		}
		
		System.out.println("Searching repo");
		JSONObject json = searchRepos(terms);
		System.out.println(json.toString(4));
		
		org.json.JSONArray array = json.getJSONArray("items");
		for(int i=0; i<array.length();i++) {
			Repository obj = new Repository();
			JSONObject owner = (JSONObject)array.getJSONObject(i).get("owner");
			String authorProfile= (String)owner.getString("url");
			obj.setAuthorProfile(authorProfile); 
			String login= (String)owner.getString("login");
			obj.setLogin(login); 
			String repourl= (String)owner.getString("repos_url");
			obj.setRepourl(repourl); 
			String createdAt= array.getJSONObject(i).getString("created_at");
			obj.setCreatedAt(createdAt);
			String repoName= array.getJSONObject(i).getString("name");
			obj.setRepoName(repoName);
			
			String updatedAt= array.getJSONObject(i).getString("updated_at");
			obj.setUpdatedAt(updatedAt);
			String gitCommitsurl= array.getJSONObject(i).getString("git_commits_url");
			obj.setGitCommitsurl(gitCommitsurl);
			String Commitsurl= array.getJSONObject(i).getString("commits_url");
			obj.setCommitsUrl(Commitsurl);
			String issuesUrl= array.getJSONObject(i).getString("issues_url");
			obj.setIssuesUrl(issuesUrl);		
			repos.add(obj);
		}
		

		return repos;
	}

	
	
}