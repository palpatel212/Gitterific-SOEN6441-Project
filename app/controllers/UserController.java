package controllers;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

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

import models.Repository;
import models.User;
import play.mvc.Controller;
import play.mvc.Result;

public class UserController extends Controller{
	
	static public User userInfo=new User();
	
	//Calling User api for getting JSON object of user information
	public static JSONObject UserApiCall(String login) {
		JSONObject jsonObject = null;
		try {
			URIBuilder builder = new URIBuilder("https://api.github.com/users/"+login);
			builder.addParameter("accept", "application/vnd.github.v3+json");
			CloseableHttpClient httpclient = HttpClients.createDefault();

			HttpResponse resp = null;
			
			
			HttpGet getAPI = new HttpGet(builder.build());
			resp = httpclient.execute(getAPI);
			
			StatusLine statusLine = resp.getStatusLine();
	        System.out.println(statusLine.getStatusCode() + " " + statusLine.getReasonPhrase());
	        String responseBody = EntityUtils.toString(resp.getEntity(), StandardCharsets.UTF_8);
	        System.out.println(responseBody.length());
	        
			try {
			     jsonObject = new JSONObject(responseBody);
			}catch (JSONException err){
			     err.printStackTrace();
			}
			
		} catch (URISyntaxException | IOException | RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		System.out.println(jsonObject);
//		System.out.println(jsonObject.getString("repos_url"));
		return jsonObject;
	}
	
public static User storeUserInfo(JSONObject user) {
		
 		userInfo.setName((String)(user.getString("name")));
		userInfo.setFollowers((Integer)(user.getNumber("followers")));
     	userInfo.setFollowing((Integer)user.getNumber("following"));
		userInfo.setPublicRepos((Integer)user.getNumber("public_repos"));
		userInfo.setRepoURL(user.getString("repos_url"));
		userInfo.setFollowersURL(user.getString("followers_url"));
		userInfo.setFollowingURL(user.getString("following_url"));
		userInfo.setHtmlURL(user.getString("html_url"));
		userInfo.setLogin(user.getString("login"));
		userInfo.setAvatarURL(user.getString("avatar_url"));
		userInfo.setUserRepos(user.getString("repos_url"));
		return userInfo;
		
	}

//Calling repo_url
public static ArrayList<String> listUserRepos(String repourl)
{
	JSONArray JsonobjectArray = null;
	try {
//  		System.out.println("***"+repourl);
		URIBuilder builder = new URIBuilder(repourl);
		builder.addParameter("accept", "application/vnd.github.v3+json");
//		builder.addParameter("per_page", "10");
		CloseableHttpClient httpclient = HttpClients.createDefault();

		HttpResponse resp = null;
		
		
		HttpGet getAPI = new HttpGet(builder.build());
		resp = httpclient.execute(getAPI);
		
		StatusLine statusLine = resp.getStatusLine();
        System.out.println(statusLine.getStatusCode() + " " + statusLine.getReasonPhrase());
        String responseBody = EntityUtils.toString(resp.getEntity(), StandardCharsets.UTF_8);
        System.out.println(responseBody.length());
        
		try {
			JsonobjectArray = new JSONArray(responseBody);
		}catch (JSONException err){
		     err.printStackTrace();
		}
		
	} catch (URISyntaxException | IOException | RuntimeException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
//	System.out.println(JsonobjectArray);
	ArrayList<String> userReposList= new ArrayList<String>();
	
	for(int i=0; i<JsonobjectArray.length();i++) {
//		Repository obj = new Repository();
		JSONObject repoOfUser = (JSONObject)JsonobjectArray.getJSONObject(i);
		String reponame= (String)repoOfUser.getString("name");
		userReposList.add(reponame);
		
	}
//   	System.out.println("------------------"+userReposList);
	return userReposList;
	
}

}
	
	
//	public Result collaborators(String id) {
//    	for(Repository rd : Search.repos) {
//    		if(id.equals(rd.id))
//			r= rd;
//    	}
//    	return ok(views.html.user.render(r));	
//    }
	

