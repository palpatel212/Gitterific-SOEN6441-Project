package models;
/**
 * Model class for storing Repository created by user details
 * 
 * @author Krupali Bhatt
 * @version 1.0
 */


public class UserRepos {
	public String repoName;
	public String repoId;

	public String getRepoName() {
		return repoName;
	}
	public void setRepoName(String repoName) {
		this.repoName = repoName;
	}
	public String getRepoId() {
		return repoId;
	}
	public void setRepoId(String repoId) {
		this.repoId = repoId;
	}

}