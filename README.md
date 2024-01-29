# blastream-java-sdk

All URIs are relative to *https://api.v2.blastream.com/api-docs*  
This is also where you can find all parameters related to params variable

### Examples
```java
String publicKey = "XXXXXXXXXXXXXX";
String privateKey = "YYYYYYYYYYYYY";

//Create a simulcast target
Instance instance = new Instance(publicKey, privateKey, "");
try {
	Channel channel = instance.createOrGetChannel("ma-room-java", new JSONObject());
	channel.createSimulcast("live-to-my-cdn", "rtmp://live.monsite.com/live", "stream-name", new JSONObject());
    //you can delete it with channel.deleteSimulcast(simulcastId);
}
catch(Exception e) {
	System.out.println(e.toString());
}

//Create or get a room with admin link
instance = new Instance(publicKey, privateKey, ""); 
try {
	Channel channel = instance.createOrGetChannel("ma-room-java", new JSONObject());
	String iframe = instance.getIframe("600", "400", new JSONObject());
	System.out.println(iframe);
}
catch(Exception e) {
	System.out.println(e.toString());
}

//Create or get a room with speaker link
instance = new Instance(publicKey, privateKey, "");
try {
	Channel channel = instance.createOrGetChannel("ma-room-java", new JSONObject());
    channel.createOrGetCollaborator("Speaker Name", "speaker", new JSONObject()); 
    String iframeColab = channel.getIframe("600", "400", new JSONObject());
	System.out.println(iframeColab);
}
catch(Exception e) {
	System.out.println(e.toString());
}
```