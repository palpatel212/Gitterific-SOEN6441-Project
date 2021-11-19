package controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.*;

import java.util.ArrayList;

import controllers.HomeController;
import org.junit.Test;
import play.mvc.Result;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.mockito.Mock;
import models.Repository;

import org.mockito.InjectMocks;


public class HomeControllerTests {
	
	@Mock
	RepoDetails repoDetails;
	
	@Before
	public void setup() {
		initMocks(this);
	}
	
}
