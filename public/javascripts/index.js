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
		var div = document.getElementById('result_contents');
		
		
		var child_div = document.createElement('div');
		child_div.innerHTML += response;
		div.prepend(child_div)
		
		alert(response);
	}
}