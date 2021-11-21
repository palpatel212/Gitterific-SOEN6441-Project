package models;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**This class contains methods for testing UserRepos class
 * @author Pal Patel
 *
 */
public class UserReposTest{
	
	@Test
	public void tRepoName(){
		UserRepos testRepoName= new UserRepos();
		testRepoName.setRepoName("Repo Name");
		assertEquals(testRepoName.getRepoName(),"Repo Name");
	}
	
	@Test
	public void tRepoId(){
		UserRepos testRepoId= new UserRepos();
		testRepoId.setRepoId("Repo Id");
		assertEquals(testRepoId.getRepoId(),"Repo Id");
	}
}