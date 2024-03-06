# blastream-java-sdk

All URIs are relative to *https://api.v2.blastream.com/api-docs*  
This is also where you can find all parameters related to params variable

### Examples
```java
import com.blastream.sdk.Instance;
import com.blastream.sdk.Channel;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

String publicKey = "XXXXXXXXXXXXXX";
String privateKey = "YYYYYYYYYYYYY";

//Create a simulcast target
Instance instance = new Instance(publicKey, privateKey, "");
try {
	Channel channel = instance.createOrGetChannel("ma-room-java", new JSONObject());
	JSONObject simulcastParams = new JSONObject();
    imulcastParams.put("rtmp_username", "username");
	simulcastParams.put("rtmp_password", "pass");
    channel.createSimulcast("live-to-my-cdn", "rtmp://live.monsite.com/live", "stream-name", simulcastParams);	
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

//Create or get a participant
instance = new Instance(publicKey, privateKey, "");
try {
	Channel channel = instance.createOrGetParticipant("ma-room-java", "participant-username", new JSONObject());            
    String iframeParticipant = channel.getIframe("600", "400", new JSONObject());
	System.out.println(iframeParticipant);
}
catch(Exception e) {
	System.out.println(e.toString());
}

//Update design of channel
instance = new Instance(publicKey, privateKey, "");
try {
	Channel channel = instance.createOrGetChannel("ma-room-java", new JSONObject());
    JSONObject picInfo = channel.uploadPic("/home/ubuntu/test.jpg");
    JSONArray colors = new JSONArray();
    colors.add("#ff0000");
    colors.add("#00ff00");
    colors.add("#ff00ff");
    colors.add("#ffff00");
    JSONObject customData = new JSONObject();
    customData.put("logo", picInfo.get("file"));
    customData.put("colors", colors);
    customData.put("css", "body { background: green; }");
    channel.setCustom(customData);
}
catch(Exception e) {
	System.out.println(e.toString());
}
```
