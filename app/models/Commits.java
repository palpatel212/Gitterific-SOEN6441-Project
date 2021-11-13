package models;

public class Commits {
	
		public static Integer totalCommits;
		public String commitName;
		public String commitUrl;
		public String total;
		public String addition;
		public String deletion;
		
		
		
		public static Integer getTotalCommits() {
			return totalCommits;
		}
		public static void setTotalCommits(Integer totalCommits) {
			Commits.totalCommits = totalCommits;
		}
		public String getTotal() {
			return total;
		}
		public void setTotal(String total) {
			this.total = total;
		}
		public String getAddition() {
			return addition;
		}
		public void setAddition(String addition) {
			this.addition = addition;
		}
		public String getDeletion() {
			return deletion;
		}
		public void setDeletion(String deletion) {
			this.deletion = deletion;
		}
		public String getCommitName() {
			return commitName;
		}
		public void setCommitName(String commitName) {
			this.commitName = commitName;
		}
		public String getCommitUrl() {
			return commitUrl;
		}
		public void setCommitUrl(String commitUrl) {
			this.commitUrl = commitUrl;
		}
		
		
		
}
