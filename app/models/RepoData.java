package models;
import play.data.validation.Constraints;

public class RepoData {

@Constraints.Required
public String keyword;

/**
* 
* Default Constructor
*/

public RepoData() {
}
/**
* 
* 
* @return keyword the word of type String to be searched
*/
public String getKeyword() {
return keyword;
}
/**
* 
* @param keyword   keyword to be set
*/
public void setKeyword(String keyword) {
this.keyword = keyword;
}

/* (non-Javadoc)
* @see java.lang.Object#toString()
*/
@Override
public String toString() {
return "TweetData [keyword=" + keyword + "]";
}
}