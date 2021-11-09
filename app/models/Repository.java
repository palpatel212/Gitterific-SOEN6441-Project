package models;

import java.util.Set;



public class Repository {

	public String authorProfile;
	public String repourl;
	public String createdAt;
	public String updatedAt;
	public String gitCommitsurl;
	public String commitsUrl;
	public String issuesUrl;
	
	
	
	public String getAuthorProfile() {
		return authorProfile;
	}
	public void setAuthorProfile(String authorProfile) {
		this.authorProfile = authorProfile;
	}
	public String getRepourl() {
		return repourl;
	}
	public void setRepourl(String repourl) {
		this.repourl = repourl;
	}
	public String getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(String createdAt) {
		
		this.createdAt = createdAt;
	}
	public String getUpdatedAt() {
		return updatedAt;
	}
	public void setUpdatedAt(String updatedAt) {
		this.updatedAt = updatedAt;
	}
	public String getGitCommitsurl() {
		return gitCommitsurl;
	}
	public void setGitCommitsurl(String gitCommitsurl) {
		this.gitCommitsurl = gitCommitsurl;
	}
	public String getCommitsUrl() {
		return commitsUrl;
	}
	public void setCommitsUrl(String commitsUrl) {
		this.commitsUrl = commitsUrl;
	}
	public String getIssuesUrl() {
		return issuesUrl;
	}
	public void setIssuesUrl(String issuesUrl) {
		this.issuesUrl = issuesUrl;
	}
	
	
	
}
