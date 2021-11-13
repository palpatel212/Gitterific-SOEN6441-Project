package controllers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import models.Issues;
import models.Repository;

public class RepoIssues {
	static public List<Issues> IssueList = new ArrayList<Issues>();
	
	public static void setRepoIssueObject (JSONObject Issue) {
		Issues issueObj = new Issues();
		issueObj.setTitle(Issue.getString("title"));
		IssueList.add(issueObj);
	}
	
	public static void getIssueList(Repository repo){
		String issueURL = repo.getIssuesUrl();
		String trimmedIssueURL = "";
		int index = issueURL.indexOf("{");
		if(index != -1) {
			trimmedIssueURL = issueURL.substring(0, index);
		}
		System.out.println(trimmedIssueURL);
		HashMap<String, String> map = new HashMap<String, String>();
		
		// Calling the API
		ApiCall.getApiCall(trimmedIssueURL, map).thenAccept(reponseBody -> {
			JSONArray IssueArray = new JSONArray(reponseBody);
			
			ArrayList<JSONObject> listData = new ArrayList<JSONObject>();
			for(int i=0; i<IssueArray.length();i++) {
				listData.add(IssueArray.optJSONObject(i));
			}
			
			listData.parallelStream().forEach(RepoIssues::setRepoIssueObject);
		});

		return;
	}
}
