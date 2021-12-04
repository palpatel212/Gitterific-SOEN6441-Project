package actors;

import actors.TimeActor.getNewData;
import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import controllers.RepoDetails;
import models.Repository;
import utils.Util;
import play.libs.Json;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class KeywordSearchActor extends AbstractActor{

	private final ActorRef webSocket;
	private Map<String, List<Repository>> searchHistory = new HashMap<>();
	
	public KeywordSearchActor(ActorRef ws) {
		this.webSocket = ws;
	}
	
    @Override
    public void preStart() {
    	
       	context().actorSelection("/user/timeActor/")
                 .tell(new TimeActor.RegisterMsg(), self());
    }
    
    @Override
    public void postStop() {
    	
       	context().actorSelection("/user/timeActor/")
                 .tell(new TimeActor.DeRegisterMsg(), self());
    }
    
    public static Props props(ActorRef webSocket) {
        return Props.create(KeywordSearchActor.class, webSocket);
    }
    
	@Override
	public Receive createReceive() {
		// TODO Auto-generated method stub
		return receiveBuilder()
   			 .match(ObjectNode.class, searchObject -> sendNewData(searchObject.get("keyword").textValue()))
			 .build();
	}
	
	
	private void sendNewData(String keyword) {
		if(searchHistory.containsKey(keyword.toLowerCase())) {
			System.out.println("in the search history");
			JsonNode jsonObject = Json.toJson(searchHistory.get(keyword.toLowerCase()));
			webSocket.tell(Util.createResponse(jsonObject, true), self());
		}
		else {
			System.out.println("not in the search history");
			List<Repository> r = RepoDetails.getRepoDetails(keyword);
			searchHistory.putIfAbsent(keyword.toLowerCase(), r);
			System.out.println(searchHistory);
			JsonNode obj = Json.toJson(r);
			System.out.println(obj);
			webSocket.tell(Util.createResponse(obj, true), self());
		}
	}
	
}
