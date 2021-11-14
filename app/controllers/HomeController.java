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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
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
	public HashMap<String, Integer> sorted;
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
    		return RepoIssues.getIssueList(r);
    	}).thenApply(issueList -> ok(views.html.issues.render(issueList)));
    	
//    	RepoIssues.getIssueList(r);
//    	return ok(views.html.issues.render(r));
//    	return CompletableFuture.supplyAsync(() -> {
//    		return RepoIssues.getIssueList(r);
//    	}).thenApply(repo -> ok(views.html.index.render(repo)));
//    	return ok(views.html.issues.render(r));
    	
    	
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
    	for(Repository rd : RepoDetails.repos) {
    		if(id.equals(rd.id))
			r= rd;
    	}
    	
    	System.out.println("Repos ID"+id);
    	return ok(views.html.RepoView.render(r));
    	
    }
    
    public Result commitStats() {
    	
        
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
    	return ok(views.html.commitSats.render(committers));
    	
    }
    
    public void findcommit() {
	  	
    	
		
  		JSONArray jsonObject = null;
  		
  		try {
  			System.out.println("In URL Builder");
  			URIBuilder builder = new URIBuilder("https://api.github.com/repos/"+r.login+"/"+r.repoName+"/commits");
  			builder.addParameter("accept", "application/vnd.github.v3+json");
  			builder.addParameter("X-RateLimit-Reset", "1350085394");
  			
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
//      	System.out.println("JSON value"+jsonObject);
      	System.out.println("In find commit function");
      	for(int i=0; i<jsonObject.length(); i++) {
      		Commits obj = new Commits();
      		System.out.println("Commit object created");
      	//	if(!JSONObject.NULL.equals(jsonObject.getJSONObject(i))) {
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
  	    		//JSONObject results = addDelStats(obj.commitUrl);
  	    		//System.out.println("Result object created.");
  				//JSONObject temptStats = (JSONObject)results.get("stats");
  				//System.out.println("In stats object");
  				//Integer addition = (Integer)temptStats.getInt("additions");
  				//obj.setAddition(addition);
  				//Integer deletion = (Integer)temptStats.getInt("deletions");
  				//obj.setDeletion(deletion);
  	    		System.out.println("message created");
  	    		obj.updateMap();
  	    		com.add(obj);
      	//	}
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
    }

