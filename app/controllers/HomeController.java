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
import java.util.concurrent.TimeUnit;
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
//import models.UserRepos;
import play.data.Form;
import play.data.FormFactory;
import play.i18n.MessagesApi;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.WebSocket;
import controllers.ApiCall;
import controllers.RepoDetails;
import play.cache.*;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import actors.TimeActor;
import actors.UserActor;
import play.libs.streams.ActorFlow;
import akka.actor.*;
import akka.stream.*;

/**
 * Defines methods that renders different views
 */
public class HomeController extends Controller {
	
	public static List<Issues> issueList = new ArrayList<Issues>();
	public ArrayList<String> RepoCollabs;
	public User UserDetail;
//	public List<UserRepos> ur;
	
	static RepoData repos;
	public Repository r= new Repository();
	FormFactory formFactory;
	MessagesApi messagesApi;
	Form<RepoData> repoForm;
	
	private final ActorSystem actorSystem;
	private final Materializer materializer;
	
	@Inject
	public HomeController(FormFactory formFactory, MessagesApi messagesApi, ActorSystem actorSystem, Materializer materializer) {
		this.messagesApi = messagesApi;
		this.formFactory = formFactory;
		this.actorSystem = actorSystem;
		this.materializer = materializer;
		
		actorSystem.actorOf(TimeActor.props(), "timeActor");
	}
	
	public Cache<String, List<Repository>> cache = Caffeine.newBuilder().build();

	public List<String> keywords;
	
	 /**
	   * This method renders index view
	   * @return Result
	   */
	public Result index() {
        return ok("Hello world");
    }
	
    public WebSocket socket() {
	   return WebSocket.Json.accept(
	       request -> ActorFlow.actorRef(UserActor::props, actorSystem, materializer));
	}

    /**
	   * This method renders search Repo view
	   * @param request http-Request
	   * @return Result
	   */
    public Result create(Http.Request request) {
    	repoForm = formFactory.form(RepoData.class);
    	System.out.println("In create");
    	return ok(views.html.create.render(repoForm,request,messagesApi.preferred(request), null, null));
    }

    /**
	   * This method lists repositories  
	   * @param request http-Request
	   * @return Result
	   */
    
    public Result onSearch(Http.Request request) {
    	String url = routes.HomeController.socket()
    		      .webSocketURL(request);
    	return ok(views.html.webSocket.render(url));
    }
    
    public CompletionStage<Result> save(Http.Request request) {
    	
    	Form<RepoData> repoForm = formFactory.form(RepoData.class);
    	repos = repoForm.bindFromRequest(request).get();
    	String keyword= repos.getKeyword();
    	String url = routes.HomeController.socket()
  		      .webSocketURL(request);
    	
    	return CompletableFuture.supplyAsync(() -> {
    		List<Repository> listOfRepos;
    		listOfRepos = cache.getIfPresent(keyword);
    		if(listOfRepos == null) {
    			System.out.println("Keyword not present in the cache");
    			listOfRepos = RepoDetails.getRepoDetails(keyword);
    			cache.put(keyword, listOfRepos);
    		}
    		return listOfRepos;
    	}).thenApply(repo -> ok(views.html.create.render(repoForm,request,messagesApi.preferred(request), repo, url)));
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
    	UserDetail=UserDetails.storeUserInfo(UserDetails.UserApiCall(login));
    	return ok(views.html.user.render(UserDetail));
    }
    
    public CompletionStage<Result> userrepos(String id)
    {
    	
    	issueList.clear();
    	for(Repository rd : UserDetail.userReposlist) {
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