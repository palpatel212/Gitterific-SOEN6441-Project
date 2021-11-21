package controllers;

import play.test.Helpers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static play.mvc.Http.Status.SEE_OTHER;
import static play.test.Helpers.GET;
import static play.mvc.Http.Status.BAD_REQUEST;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.POST;
import static play.test.Helpers.route;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.Before;

import models.Issues;
import models.Repository;
import play.Application;
import play.mvc.Http.RequestBuilder;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;
import play.test.WithApplication;

public class ControllerTest extends WithApplication {
	
	@Before
	public void setup() {
	   List<Repository> repos = new ArrayList<Repository>();
	   Repository r = new Repository();
	   r.setId("51803340");
	   r.setGitCommitsurl("https://api.github.com/repos/DeborahK/Angular-GettingStarted/commits{/sha}");
	   r.setCommitsUrl("https://api.github.com/repos/DeborahK/Angular-GettingStarted/commits{/sha}");
	   r.setIssuesUrl("https://api.github.com/repos/DeborahK/Angular-GettingStarted/issues{/number}");
	   
	   repos.add(r);
	   RepoDetails.repos = repos;
	   
	   List<Issues> issueList = new ArrayList<Issues>();
	   Issues i = new Issues();
	   i.setTitle("This is the issue the");
	   issueList.add(i);
	   HomeController.issueList = issueList; 
	}
	
	@Test
    public void testCreateController() {
        RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/create");

        Result result = route(app, request);
        assertEquals(OK, result.status());
	}
	
   @Test
    public void testFormSubmit() {
	   Map<String, String> data = new HashMap<>();
	   data.put("keyword", "angular");
        RequestBuilder request = Helpers.fakeRequest()
                .method(POST).bodyForm(data).uri("/create");

        Result result = route(app, request);
        assertEquals(OK, result.status());
    }
   
   @Test
   public void testIssues() {
       RequestBuilder request = Helpers.fakeRequest()
               .method(GET).uri("/issues/51803340");

       Result result = route(app, request);
       assertEquals(OK, result.status());
   }
   
   @Test
   public void testIssueStats() {
	   RequestBuilder request = Helpers.fakeRequest()
               .method(GET).uri("/issueStats");

       Result result = route(app, request);
       assertEquals(OK, result.status());
       assertEquals("text/html", result.contentType().get());
       assertEquals("utf-8", result.charset().get());
   }

}
