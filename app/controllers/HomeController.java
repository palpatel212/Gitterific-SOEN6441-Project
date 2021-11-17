package controllers;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import models.Commits;
import models.Committer;
import models.Issues;
import models.RepoData;
import models.Repository;
import models.User;
import play.data.Form;
import play.data.FormFactory;
import play.i18n.MessagesApi;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;




/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */

public class HomeController extends Controller {
    /**
     * An action that renders an HTML page with a welcome message.
     * The configuration in the <code>routes</code> file means that
     * this method will be called when the application receives a
     * <code>GET</code> request with a path of <code>/</code>.
     */
	public List<Commits> com = new ArrayList<Commits>();
	public List<Committer> committers = new ArrayList<Committer>();
	List<Issues> issueList = new ArrayList<Issues>();
	public ArrayList<String> RepoCollabs;
	public HashMap<String, Integer> sorted;
	public List<Integer> additionResult = new ArrayList<Integer>();
	public List<Integer> deletionResult = new ArrayList<Integer>();
	public Integer maxAdd;
	public Integer maxDel;
	public Integer minAdd;
	public Integer minDel;
	public OptionalDouble avgAdd;
	public OptionalDouble avgDel;
	static RepoData repos;
	Repository r= new Repository();
	FormFactory formFactory;
	MessagesApi messagesApi;
	Form<RepoData> repoForm;
	@Inject
	public HomeController(FormFactory formFactory, MessagesApi messagesApi) {
		this.messagesApi = messagesApi;
		this.formFactory = formFactory;
	}
	
	public List<String> keywords;
	    public Result index() {
        return ok("Hello world");
    }
    
    public Result create(Http.Request request) {
    	repoForm = formFactory.form(RepoData.class);
    	System.out.println("In create");
    	return ok(views.html.create.render(repoForm,request,messagesApi.preferred(request)));
    }
    
    public CompletionStage<Result> save(Http.Request request) {
    	
    	Form<RepoData> repoForm = formFactory.form(RepoData.class);
    	repos = repoForm.bindFromRequest(request).get();
    	String keyword= repos.getKeyword();
    	System.out.println(keyword);
    	
    	return CompletableFuture.supplyAsync(() -> {
    		return RepoDetails.getRepoDetails(keyword);
    	}).thenApply(repo -> ok(views.html.index.render(repo)));
    }
    
    public Result collaborators(String id) {
    	for(Repository rd : RepoDetails.repos) {
    		if(id.equals(rd.id))
			r= rd;
    	}
    	return ok(views.html.collaborators.render(r));
    }
    
    public CompletionStage<Result> issues(String id) {
    	
    	for(Repository rd : RepoDetails.repos) {
    		if(id.equals(rd.id))
			r= rd;
    	}
    	
    	return CompletableFuture.supplyAsync(() -> {
    		this.issueList = RepoIssues.getIssueList(r.getIssuesUrl());
    		return this.issueList;
    	}).thenApply(issueList -> ok(views.html.issues.render(issueList)));
    	
    }
    
    public Result userinfo(String login)
    {
    	User userDetail=UserController.storeUserInfo(UserController.UserApiCall(login));
    	return ok(views.html.user.render(userDetail));
    }
    
    public Result commits(String id) {
    	for(Repository rd : RepoDetails.repos) {
    		if(id.equals(rd.id)) {
			r= rd;
    		System.out.println("found repo");
    		}
    	}
    	
    	findcommit();
    	
    	return ok(views.html.commits.render(com));
    }
    
    public Result repo(String id)
    {
    	this.issueList.clear();
    	for(Repository rd : RepoDetails.repos) {
    		if(id.equals(rd.id))
			r= rd;
    	}
    	System.out.println("Repos ID"+id);
    	System.out.println(r.getContributorURL());
    	System.out.println(r.getIssuesUrl());
    	this.issueList = RepoIssues.getIssueList(r.getIssuesUrl());
    	this.RepoCollabs = RepoDetails.listCollabRepos(r.getContributorURL());
    	
    	return ok(views.html.RepoView.render(r, issueList, RepoCollabs));
    }
    
    public Result commitStats() {
    	
        
    	additionResult.clear();
    	deletionResult.clear();
    	
    	int count =0;
    	committers.clear();
    	Iterator hmIterator = sorted.entrySet().iterator();
    	while (hmIterator.hasNext()&& count<10) {
            Map.Entry mapElement = (Map.Entry)hmIterator.next();
            Committer c = new Committer();
            String name = (String) mapElement.getKey(); 
            c.setName(name);
            Integer commitNum = (Integer) mapElement.getValue();
            c.setCommitNum(commitNum);
            committers.add(c);
            count++;
        }
    	
    	for(Commits c:com) {
    			JSONObject results = addDelStats(c.commitUrl);
	    		System.out.println("Result object created.");
				JSONObject temptStats = (JSONObject)results.get("stats");
				if(!JSONObject.NULL.equals(temptStats)) {
					System.out.println("In stats object");
					Integer addition = (Integer)temptStats.getInt("additions");
					additionResult.add(addition);
					Integer deletion = (Integer)temptStats.getInt("deletions");
					deletionResult.add(deletion);
				}
				avgAdd = additionResult
			            .stream()
			            .mapToDouble(a -> a)
			            .average();
				
				
				avgDel = deletionResult
			            .stream()
			            .mapToDouble(a -> a)
			            .average();
				
				maxAdd = additionResult.stream().collect(Collectors.summarizingInt(Integer::intValue)).getMax();
				maxDel = deletionResult.stream().collect(Collectors.summarizingInt(Integer::intValue)).getMax();
				
				minAdd = additionResult.stream().collect(Collectors.summarizingInt(Integer::intValue)).getMin();
				minDel = deletionResult.stream().collect(Collectors.summarizingInt(Integer::intValue)).getMin();	
				
				}
        
    	
    	return ok(views.html.commitSats.render(committers,avgAdd,avgDel,maxAdd,maxDel,minAdd,minDel));
    }
    
    public Result issueStats() {
    	
    	List<String> issueTitles = this.issueList.stream().map(i -> i.getTitle()).collect(Collectors.toList());
    	System.out.println("This are issue titles");
    	List<String> allWords = issueTitles.stream().flatMap(i -> Arrays.stream(i.split(" "))).collect(Collectors.toList());
    	
    	Map<String, Long> finalMapDescendingOrder = new LinkedHashMap<>();
    	
    	allWords.stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting())).entrySet().stream()
    	.sorted(Map.Entry.<String, Long>comparingByValue().reversed()).forEachOrdered(e -> finalMapDescendingOrder.put(e.getKey(), e.getValue()));
    	
    	System.out.println("Statistics :");
//    	System.out.println(finalMapDescendingOrder);
    	return ok(views.html.issueStats.render(finalMapDescendingOrder));
    }
    
public void findcommit() {
	  	
    	System.out.println("In find commit");
		
  		JSONArray jsonObject = null;
  		
  		try {
  			System.out.println("In URL Builder");
  			URIBuilder builder = new URIBuilder("https://api.github.com/repos/"+r.login+"/"+r.repoName+"/commits");
  			builder.addParameter("accept", "application/vnd.github.v3+json");
  			builder.addParameter("X-RateLimit-Reset", "1350085394");
  			builder.addParameter("per_page", "10");
  			
  			CloseableHttpClient httpclient = HttpClients.createDefault();

  			HttpResponse resp = null;
  			
  			
  			
  			HttpGet getAPI = new HttpGet(builder.build());
  			resp = httpclient.execute(getAPI);
  			
  			StatusLine statusLine = resp.getStatusLine();
  	        System.out.println(statusLine.getStatusCode() + " " + statusLine.getReasonPhrase());
  	        String responseBody = EntityUtils.toString(resp.getEntity(), StandardCharsets.UTF_8);
  	        System.out.println(responseBody.length());
  	        
  			try {
  			     jsonObject = new JSONArray(responseBody);
  			}catch (JSONException err){
  			     err.printStackTrace();
  			}
  			
  		} catch (URISyntaxException | IOException | RuntimeException e) {
  			// TODO Auto-generated catch block
  			e.printStackTrace();
  		}
  		
  		
      	
      	com.clear();
      	committers.clear();
     	System.out.println("JSON value"+jsonObject);
      	System.out.println("In find commit function");
      	for(int i=0; i<jsonObject.length(); i++) {
      		Commits obj = new Commits();
      		System.out.println("Commit object created");
      		if(!JSONObject.NULL.equals(jsonObject.getJSONObject(i))) {
  	    		String commitUrl= jsonObject.getJSONObject(i).getString("url");
  	    		obj.setCommitUrl(commitUrl);
  	    		System.out.println("url object created");
  	    		JSONObject temptCommitter = (JSONObject)jsonObject.getJSONObject(i).get("author");
  	    		String committerName= (String)temptCommitter.getString("login");
  	    		obj.setCommitterName(committerName);
  	    		Integer committerId= (Integer)temptCommitter.getNumber("id");
  	    		obj.setCommitterId(committerId);
  	    		JSONObject tempt = (JSONObject)jsonObject.getJSONObject(i).get("commit");
  	    		String commitName= (String)tempt.getString("message");
  	    		obj.setCommitName(commitName);	
  	    		obj.updateMap();
  	    		com.add(obj);
  	    		
  	    		
      		}
      	}
      	
      	HashMap<String, Integer> temp =Commits.countMap.entrySet()
                .stream()
                .sorted((i1, i2)
                            -> i1.getValue().compareTo(
                                i2.getValue()))
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    Map.Entry::getValue,
                    (e1, e2) -> e1, LinkedHashMap::new));
      	

      	sorted = temp.entrySet() .stream() .sorted(Collections.reverseOrder(Map.Entry.comparingByValue())) .collect( Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));

      }
    
public static JSONObject addDelStats(String url) {
		
		JSONObject jsonObject = null;
		try {
			URIBuilder builder = new URIBuilder(url);
			builder.addParameter("accept", "application/vnd.github.v3+json");
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
			}catch (JSONException err){
			     err.printStackTrace();
			}
			
		} catch (URISyntaxException | IOException | RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jsonObject;
	}
  }

