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
import models.RepoTopics;
import models.Repository;
import models.User;
import play.data.Form;
import play.data.FormFactory;
import play.i18n.MessagesApi;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import controllers.ApiCall;
import controllers.RepoDetails;


/**
 * Defines methods that renders different views
 */
public class HomeController extends Controller {
	
	static List<Issues> issueList = new ArrayList<Issues>();
	public ArrayList<String> RepoCollabs;
	
	static RepoData repos;
	public Repository r= new Repository();
	FormFactory formFactory;
	MessagesApi messagesApi;
	Form<RepoData> repoForm;
	@Inject
	public HomeController(FormFactory formFactory, MessagesApi messagesApi) {
		this.messagesApi = messagesApi;
		this.formFactory = formFactory;
	}

	public List<String> keywords;
	
	 /**
	   * This method renders index view
	   * @return Result
	   */
	public Result index() {
        return ok("Hello world");
    }
    
    /**
	   * This method renders search Repo view
	   * @param request http-Request
	   * @return Result
	   */    
    public CompletionStage<Result> create(Http.Request request) {
    	
    	return CompletableFuture.supplyAsync(() -> {
    		repoForm = formFactory.form(RepoData.class);
        	return repoForm;
    	}).thenApply(repoForm -> ok(views.html.create.render(repoForm,request,messagesApi.preferred(request))));
    }
    /**
	   * This method lists repositories  
	   * @param request http-Request
	   * @return Result
	   */
    public CompletionStage<Result> save(Http.Request request) {
    	
    	Form<RepoData> repoForm = formFactory.form(RepoData.class);
    	repos = repoForm.bindFromRequest(request).get();
    	String keyword= repos.getKeyword();
    	System.out.println(keyword);
    	
    	return CompletableFuture.supplyAsync(() -> {
    		return RepoDetails.getRepoDetails(keyword);
    	}).thenApply(repo -> ok(views.html.index.render(repo)));
    }
    
    /**
	   * This method lists issues
	   * @param id Repository Id
	   * @return Result
	   */
    public CompletionStage<Result> issues(String id) {
    	
    	for(Repository rd : RepoDetails.repos) {
    		if(id.equals(rd.id))
			r= rd;
    	}
    	
    	return CompletableFuture.supplyAsync(() -> {
    		issueList = RepoIssues.getIssueList(r.getIssuesUrl());
    		return issueList;
    	}).thenApply(issueList -> ok(views.html.issues.render(issueList)));
    }
    
    /**
	   * This method displays userInfo
	   * @param login Userlogin
	   * @return Result
	   */
    public Result userinfo(String login)
    {
//    	return CompletableFuture.supplyAsync(() -> {
//    		User userDetail=UserDetails.storeUserInfo(UserDetails.UserApiCall(login));
//    		return userDetail;
//    	}).thenApply(userDetail -> ok(views.html.user.render(userDetail)));
//    	
    	User userDetail=UserDetails.storeUserInfo(UserDetails.UserApiCall(login));
    	return ok(views.html.user.render(userDetail));
    }
    
    public CompletionStage<Result> userrepos(String id)
    {
    	issueList.clear();
    	for(Repository rd : RepoDetails.repos) {
    		if(id.equals(rd.id))
			r= rd;
    	}
    	
    	return CompletableFuture.supplyAsync(() -> {
    		issueList = RepoIssues.getIssueList(r.getIssuesUrl());
        	return issueList;
    	}).thenApply(issues -> {
    		this.RepoCollabs = RepoDetails.listCollabRepos(r.getContributorURL());
    		return ok(views.html.RepoView.render(r, issues, RepoCollabs));
    	});
    }
    
    
	public Result topicsearch(String t) {
    	List<Repository> r = RepoTopics.getRepoDetails(t);
    	return ok(views.html.index.render(r));
	}
    /**
	   * This method renders commits view
	   * @param id RepositoryId
	   * @return Result
	   */
    public CompletionStage<Result> commits(String id) {
    	for(Repository rd : RepoDetails.repos) {
    		if(id.equals(rd.id)) {
			r= rd;
    		System.out.println("found repo");
    		}
    	}
    	
    	return CompletableFuture.runAsync(() -> {
    		CommitDetails.findcommit(r);
    	}).thenApply(c -> ok(views.html.commits.render(CommitDetails.com)));
    }
    
    /**
	   * This method renders repo view
	   * @param id RepoID
	   * @return Result
	   */
    
    public CompletionStage<Result> repo(String id)
    {
    	issueList.clear();
    	for(Repository rd : RepoDetails.repos) {
    		if(id.equals(rd.id))
			r= rd;
    	}
    	
    	return CompletableFuture.supplyAsync(() -> {
    		issueList = RepoIssues.getIssueList(r.getIssuesUrl());
        	return issueList;
    	}).thenApply(issues -> {
    		this.RepoCollabs = RepoDetails.listCollabRepos(r.getContributorURL());
    		return ok(views.html.RepoView.render(r, issues, RepoCollabs));
    	});
    }
    
    /**
	   * This method renders commitStats view
	   * @return Result
	   */
    public Result commitStats() {
    	CommitDetails.commitStatistics();
    	return ok(views.html.commitSats.render(CommitDetails.committers,CommitDetails.avgAdd,CommitDetails.avgDel,CommitDetails.maxAdd,CommitDetails.maxDel,CommitDetails.minAdd,CommitDetails.minDel));
    }
    
    /**
	   * This method renders issueStatistics view
	   * @return Result
	   */
    public CompletionStage<Result> issueStats() {
    	return CompletableFuture.supplyAsync(() -> {
    		List<String> issueTitles = issueList.stream().map(i -> i.getTitle()).collect(Collectors.toList());
        	System.out.println("This are issue titles");
        	List<String> allWords = issueTitles.stream().flatMap(i -> Arrays.stream(i.split(" "))).collect(Collectors.toList());
        	
        	Map<String, Long> finalMapDescendingOrder = new LinkedHashMap<>();
        	
        	allWords.stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting())).entrySet().stream()
        	.sorted(Map.Entry.<String, Long>comparingByValue().reversed()).forEachOrdered(e -> finalMapDescendingOrder.put(e.getKey(), e.getValue()));
        	
        	return finalMapDescendingOrder;
    	}).thenApply(finalMapDescendingOrder -> ok(views.html.issueStats.render(finalMapDescendingOrder)));
    }
} 