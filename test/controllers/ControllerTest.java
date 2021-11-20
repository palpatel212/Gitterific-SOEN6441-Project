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

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import play.Application;
import play.mvc.Http.RequestBuilder;
import play.mvc.Result;
import play.test.Helpers;
import play.test.WithApplication;

public class ControllerTest extends WithApplication {
	@Test
    public void testCreateController() {
        RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/create");

        Result result = route(app, request);
        assertEquals(OK, result.status());
	}
}
