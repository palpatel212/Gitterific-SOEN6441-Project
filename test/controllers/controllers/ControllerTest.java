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

/**This class tests controller
 *
 */
public class ControllerTest extends WithApplication {
	
	/**
	 * This method creates a set up for testing
	 * @author Parth Parekh
	 */
	@Before
	public void setup() {
	   List<Repository> repos = new ArrayList<Repository>();
	   Repository r = new Repository();
	   r.setId("189491745");
	   r.setLogin("lyft");
	   r.setRepoName("flinkk8soperator");
	   r.setGitCommitsurl("https://api.github.com/repos/lyft/flinkk8soperator/commits{/sha}");
	   r.setCommitsUrl("https://api.github.com/repos/lyft/flinkk8soperator/commits{/sha}");
	   r.setIssuesUrl("https://api.github.com/repos/lyft/flinkk8soperator/issues{/number}");
	   r.setContributorURL("https://api.github.com/repos/lyft/flinkk8soperator/contributors");
	   
	   repos.add(r);
	   RepoDetails.repos = repos;
	   
	   List<Issues> issueList = new ArrayList<Issues>();
	   Issues i = new Issues();
	   i.setTitle("This is the issue the");
	   issueList.add(i);
	   HomeController.issueList = issueList; 
	}
	
	/**
	 * This method tests the create controller
	 * @author Parth Parekh
	 */	
	@Test
    public void testCreateController() {
        RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/create");

        Result result = route(app, request);
        assertEquals(OK, result.status());
	}
	
	/**
	 * This method tests the form submit
	 * @author Parth Parekh
	 */
   @Test
    public void testFormSubmit() {
	   Map<String, String> data = new HashMap<>();
	   data.put("keyword", "flink");
        RequestBuilder request = Helpers.fakeRequest()
                .method(POST).bodyForm(data).uri("/create");

        Result result = route(app, request);
        assertEquals(OK, result.status());
    }
   
   /**
	 * This method tests the issues
	 * @author Parth Parekh
	 */
   @Test
   public void testIssues() {
       RequestBuilder request = Helpers.fakeRequest()
               .method(GET).uri("/issues/189491745");

       Result result = route(app, request);
       assertEquals(OK, result.status());
   }
   
   /**
	 * This method tests the IssueStats
	 * @author Parth Parekh
	 */
   @Test
   public void testIssueStats() {
	   RequestBuilder request = Helpers.fakeRequest()
               .method(GET).uri("/issueStats");

       Result result = route(app, request);
       assertEquals(OK, result.status());
       assertEquals("text/html", result.contentType().get());
       assertEquals("utf-8", result.charset().get());
   }
   
   /**
	 * This method tests the commits
	 * @author Juhi Patel
	 */
   @Test
 public void testCommits() {
     RequestBuilder request = Helpers.fakeRequest()
             .method(GET).uri("/commits/189491745");

     Result result = route(app, request);
     assertEquals(OK, result.status());
 }
  
   /**
	 * This method tests the commitstats
	 * @author Juhi Patel
	 */
   @Test
   public void testCommitStats() {
	   RequestBuilder request = Helpers.fakeRequest()
               .method(GET).uri("/commitstats");

       Result result = route(app, request);
       assertEquals(OK, result.status());
       assertEquals("text/html", result.contentType().get());
       assertEquals("utf-8", result.charset().get());
   }
   
   /**
	 * This method tests the user
	 * @author Krupali Bhatt
	 */
   @Test
   public void testUser() {
       RequestBuilder request = Helpers.fakeRequest()
               .method(GET).uri("/user/lyft");

       Result result = route(app, request);
       assertEquals(OK, result.status());
   }

  
}
