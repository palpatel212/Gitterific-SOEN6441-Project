package controllers;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletionStage;

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
import models.RepoData;
import models.Repository;
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
    	return Search.getRepoAndUserDetails(keyword).thenApplyAsync((repo -> ok(views.html.index.render(repo)))); 
    }
    
    public Result collaborators(String id) {
    	for(Repository rd : Search.repos) {
    		if(id.equals(rd.id))
			r= rd;
    	}
    	return ok(views.html.user.render(r));
    }
    
    public Result issues(String id) {
    	
    	for(Repository rd : Search.repos) {
    		if(id.equals(rd.id))
			r= rd;
    	}
    	return ok(views.html.issues.render(r));
    	
    	
    }
    public Result commits(String id) {
    	for(Repository rd : Search.repos) {
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
    	for(Repository rd : Search.repos) {
    		if(id.equals(rd.id))
			r= rd;
    	}

    	System.out.println("Repos ID"+id);
    	return ok(views.html.RepoView.render(r));
    	
    }
    public void findcommit() {
    	
    	JSONArray jsonObject = null;
    	try {
    		System.out.println("Calling commit API by passing two arguements");
    		URIBuilder builder = new URIBuilder("https://api.github.com/repos/"+r.login+"/"+r.repoName+"/commits");
    		builder.addParameter("accept", "application/vnd.github.v3+json");
    		builder.addParameter("per_page", "100");
    	
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
    	System.out.println("JSON value"+jsonObject);
    	System.out.println("In find commit function");
    	for(int i=0; i<jsonObject.length(); i++) {
    		Commits obj = new Commits();
    		System.out.println("Commit object created");
    		String commitUrl= jsonObject.getJSONObject(i).getString("url");
    		obj.setCommitUrl(commitUrl);
    		System.out.println("url object created");
    		JSONObject tempt = (JSONObject)jsonObject.getJSONObject(i).get("commit");
    		String commitName= (String)tempt.getString("message");
    		obj.setCommitName(commitName);	
    		System.out.println("message created");
    		com.add(obj);
    	}
    }
}
