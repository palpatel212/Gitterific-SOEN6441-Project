package controllers;

import java.util.Arrays;
import java.util.List;

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
    
    
    public Result save(Http.Request request) {
    	System.out.println("ENtered...");
    	Form<RepoData> repoForm = formFactory.form(RepoData.class);
    	repos = repoForm.bindFromRequest(request).get();
    	System.out.println("In save");
 
		return displayRepo();
    }
    
    
    
    public Result displayRepo() {
    	String keyword= repos.getKeyword();	
    	String[] words = keyword.split(" ");
    	keywords = Arrays.asList(words);
    	System.out.println(keywords);
    	System.out.println("Entered display Repo");
    	List<Repository> repo = Search.findrepo(keywords);
    	return ok(views.html.index.render(repo));
    }
 
    

}

//Example form injecting a messagesAction