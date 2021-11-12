package controllers;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletionStage;

import javax.inject.Inject;

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
    		if(id.equals(rd.id))
			r= rd;
    	}
    	return ok(views.html.commits.render(r));
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
}

//Example form injecting a messagesAction