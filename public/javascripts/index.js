function search() {
	let searchSocket = new WebSocket("ws://localhost:9000/socket");
	var searchKey = document.getElementById("searchTerm").value;
	let message = {
		"keyword": searchKey
	};
	let msg = JSON.stringify(message);	
	searchSocket.onopen = function(){
		alert("started");
		searchSocket.send(msg);
	}
	searchSocket.onmessage = function(event) {
		var response = event.data;
		const Res = JSON.parse(response);
		
		var div = document.getElementById('result_contents');

		var child_div = document.createElement('div');
		console.log(Res.data.length);
		
	
		
		for(let i=0; i<Res.data.length; i++)
		{
			var inner = "<div>"
			inner += "<div>"
			const ResObj = Res.data[i];
			inner += '<p> User: ' + '<a href="http://localhost:9000/user/' + ResObj.login + '">' + ResObj.login+ '</a> </p>';
			inner += '<p> Repository: ' + '<a href="http://localhost:9000/check/' + ResObj.id + '">' + ResObj.repoName+ '</a> </p>';
			inner += "<p> Topics: </p>";
			const ResTopics = Res.data[i].topics;
			for( let j = 0; j< ResTopics.length; j++)
			{
			 inner += '<li>' + '<a href="http://localhost:9000/topicsearch/' + ResTopics[j] + '">' + ResTopics[j] + '</a> </li>';
			}			
			inner += " </div> </div>"
			child_div.innerHTML += inner;
		}
		
		div.prepend(child_div)
		alert(response);
	}
}