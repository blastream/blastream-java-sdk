import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;


public class Channel extends Instance{
	private String request_url;
	private String app_url;
	private String slug;
	private String public_key;
	private String private_key;
	private String token;
	private String APIToken;
	private String channel_url;
	private Integer embed;
	private String whitelabel_url;
	private Boolean is_channel;
	private String id;
	
	private Channel channel;
	/**
	 * Constructor of Chanel class
	 * @param public_key
	 * @param private_key
	 * @param custom_domain custom url can be ""
	 */
	public Channel(String public_key, String private_key, String custom_domain) {
		super(public_key, private_key, custom_domain);
		this.request_url = "https://api.v2.blastream.com";
		this.app_url = "app.v2.blastream.com";
		this.embed = 1;
		this.token = "";
		this.APIToken = "";
		this.public_key = public_key;
		this.private_key = private_key;
		if (custom_domain != "") {
			this.whitelabel_url = custom_domain;
		} else {
			this.whitelabel_url = "";
		}
		this.is_channel = true;
		
	}
	
	public void setToken(String token) {
		this.token = token;
	}
	
	public void setAPIToken(String token) {
		this.APIToken = token;
	}
	
	public String getAPIToken() {
		return this.APIToken;
	}
	
	public void setChannelUrl(String url) {
		this.channel_url = url;
	}

	public String getChannelUrl() {
		return this.channel_url;
	}
	
	public void setSlug(String slug) {
		this.slug = slug;
	}
	
	public Channel getChannel() {
		return this.channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getId() {
		return this.id;
	}
	
	/**
	 * Modify access rules 
	 * @param privacy access rules can be "PRIVATE" or "PUBLIC"
	 * @param params other rules like password (is automatly generated if not specified)
	 * 
	 */
	public JSONObject setAccessRule(String privacy, JSONObject params) throws IOException, ParseException, InterruptedException {
		Integer priv = 2;
		if (privacy == "PRIVATE") {
			priv = 2;
		}else if(privacy == "PUBLIC") {
			priv = 0;
		}
		JSONObject data = new JSONObject();
		data.put("privacy",priv);
		data.put("data", params);
		return this.put("/channel/rule", data);
	}
	
	/**
	 * Create a token for all orators
	 * @param params empty
	 * 
	 */
	public JSONObject createOrRefreshSpeakersToken(JSONObject params) throws IOException, ParseException, InterruptedException {
		return this.post("/channel/speakers-token",params);
	}
	/**
	 *Remove speakers token 
	 */
	public JSONObject removeSpeakersToken() throws IOException, ParseException, InterruptedException {
		return this.delete("/channel/speakers-token");
	}
	/**
	 * Get speakers token
	 * @return
	 * @throws IOException
	 * @throws ParseException
	 * @throws InterruptedException
	 */
	public JSONObject getSpeakersToken() throws IOException, ParseException, InterruptedException {
		return this.get("/channel/speakers-token");
	}
	
	/**
	 * Get  list of replays
	 * @return 
	 * 
	 */
	public JSONObject getReplays() throws IOException, ParseException, InterruptedException {
		return this.get("/channel/videos");
	}
	
	/**
	 * Get Settings
	 * @return 
	 * 
	 */
	public JSONObject getSettings() throws IOException, ParseException, InterruptedException {
		return this.get("/channel/settings");
	}
	
	/**
	 * Update advanced settings
	 * @param params Settings to modify
	 * @return New settings
	 * 
	 */
	public JSONObject updateAdvancedSettings(JSONObject params) throws IOException, ParseException, InterruptedException {
		JSONObject settings = this.getSettings();
		
		settings.put("advanced", params);
		return this.updateSettings(settings);
	}
	
	/**
	 * Get list of collaborators
	 * @param type Type of researched collaborators ("speaker","moderator", "animator" or "" for all collaborators)
	 * @return List of collaborators
	 * 
	 */
	public JSONArray getCollaborators(String type) throws IOException, ParseException, InterruptedException {
		final HttpClient httpClient = HttpClient.newBuilder()
	            .version(HttpClient.Version.HTTP_2)
	            .build();
		JSONArray list;
		if (type == "") {
			HttpRequest request = HttpRequest.newBuilder()
	                .GET()
	                .uri(URI.create(this.request_url + "/channel/collaborators"))
	                .setHeader("X-Api-Public", this.public_key).setHeader("X-Api-Private", this.private_key).setHeader("X-Auth-Token",this.getAPIToken())
	                .build();
			HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
			
			JSONParser parse = new JSONParser();
		    list = (JSONArray) parse.parse(response.body());

		}else {
			HttpRequest request = HttpRequest.newBuilder()
	                .GET()
	                .uri(URI.create(this.request_url + "/channel/collaborators" + type))
	                .setHeader("X-Api-Public", this.public_key).setHeader("X-Api-Private", this.private_key).setHeader("X-Auth-Token",this.getAPIToken())
	                .build();
			HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
			
			JSONParser parse = new JSONParser();
		    list = (JSONArray) parse.parse(response.body());
		}
		return list;
	}
	
	/**
	 * Get a collaborator or create one if it doesn't exist
	 * @param displayname username of collaborator
	 * @param status The status of the collaborator
	 * @param params other informations on the collaborator can be empty
	 * @return Object Collaborator
	 * 
	 */
	public Collaborator createOrGetCollaborator(String displayname, String status, JSONObject params) throws IOException, ParseException, InterruptedException {
		JSONArray collabs = this.getCollaborators("");
		Integer nbCollabs = collabs.size();
		for(Integer i = 0; i< nbCollabs; i++) {
			JSONParser parse = new JSONParser();
		    JSONObject collab = (JSONObject) parse.parse(collabs.get(i).toString());
		    if ((collab.get("displayname").toString().equals(displayname) && (collab.get("status").toString().equals(status)))) {
		    	this.setToken(collab.get("token").toString());
		    	super.setToken(collab.get("token").toString());
		    	this.setChannelUrl(collab.get("invite_link").toString());
		    	super.setChannelUrl(collab.get("invite_link").toString());
				return new Collaborator(collab, this);
			}
		}
		
		return this.createCollaborator(displayname, status, params);
	}
	
	/**
	 * Create a collaborator
	 * @param displayname username of collaborator
	 * @param status The status of the collaborator
	 * @param params params other informations on the collaborator take default values if empty
	 * @return object Collaborator
	 * 
	 */
	public Collaborator createCollaborator(String displayname, String status, JSONObject params) throws IOException, ParseException, InterruptedException {
		params.put("displayname", displayname);
		params.put("status", status);
		JSONObject collab = this.put("/channel/collaborator", params);
		this.setToken(collab.get("token").toString());
		return new Collaborator(collab, this);
	}
	
	/**
	 * Modify settings
	 * @param params modified settings
	 * @return json with new settings
	 * 
	 */
	public JSONObject updateSettings(JSONObject params) throws IOException, ParseException, InterruptedException {
		JSONObject data = new JSONObject();
		data.put("data", params);
		return this.post("/channel/settings", data);
	}
	
	/**
	 * Modify chat settings
	 * @param params modified settings
	 * @return New chat settings
	 * 
	 */
	public JSONObject updateChatSettings(JSONObject params) throws IOException, ParseException, InterruptedException {
		JSONObject data = new JSONObject();
		data.put("data", params);
		return this.post("/chat/settings", data);
	}
	
	/**
	 * Modify subscription of channel
	 * @param plan type of the subscription exemple Videoconference
	 * @param billing Payment (monthly or hourly)
	 * @return
	 * 
	 */
	public JSONObject updateSubscription(String plan, String billing) throws IOException, ParseException, InterruptedException {
		JSONObject data = new JSONObject();
		data.put("plan", plan);
		data.put("billing", billing);
		return this.post("/channel/subscription", data);
	}
	
	/**
	 * Customize the room
	 * @param params modified settings 
	 * @return New settings
	 * 
	 */
	public JSONObject setCustom(JSONObject params) throws IOException, ParseException, InterruptedException {
		JSONObject data = new JSONObject();
		data.put("data", params);
		return this.post("/channel/custom", data);
	}
	
	/**
	 * Remove customization
	 * @return Default settings
	 *
	 */
	public JSONObject removeCustom() throws IOException, ParseException, InterruptedException {
		return this.delete("/channel/custom");
	}
	 /**
	  * disconnect all users
	  * @param params empty
	  *  
	  * 
	  */
	public JSONObject disconnectAll(JSONObject params) throws IOException, ParseException, InterruptedException {
		return this.post("/channel/disconnectall", params);
	}
	
	/**
	 * Send a message in chat
	 * @param params Contains username, message and channel name
	 * @throws IOException
	 * @throws ParseException
	 * @throws InterruptedException
	 */
	public JSONObject sendMessage(JSONObject params) throws IOException, ParseException, InterruptedException {
		this.is_channel = false;
		JSONObject data = new JSONObject();
		data.put("msg", params.get("msg"));
		data.put("username", params.get("username"));
		data.put("slug", this.slug);
		JSONObject result = this.post("/api/msg", data);
		this.is_channel = true;
		return result;
	}
	/**
	 * 
	 * Start the livestream
	 * @param params empty
	 * @throws IOException
	 * @throws ParseException
	 * @throws InterruptedException
	 */
	public JSONObject startLivestreaming(JSONObject params) throws IOException, ParseException, InterruptedException {
		return this.post("/channel/livestreaming/start",params);
	}
	/**
	 * End the livestream
	 * @param params empty
	 * @throws IOException
	 * @throws ParseException
	 * @throws InterruptedException
	 */
	public JSONObject stopLivestreaming(JSONObject params) throws IOException, ParseException, InterruptedException {
		return this.post("/channel/livestreaming/stop",params);
	}
	/**
	 * Start recording
	 * @param params empty
	 * @throws IOException
	 * @throws ParseException
	 * @throws InterruptedException
	 */
	public JSONObject startRecord(JSONObject params) throws IOException, ParseException, InterruptedException {
		return this.post("/channel/startrecord",params);
	}
	/**
	 * End the recording
	 * @param params empty
	 * @throws IOException
	 * @throws ParseException
	 * @throws InterruptedException
	 */
	public JSONObject stopRecord(JSONObject params) throws IOException, ParseException, InterruptedException {
		return this.post("/channel/stoprecord",params);
	}
	/**
	 * Delete the channel
	 * @throws IOException
	 * @throws ParseException
	 * @throws InterruptedException
	 */
	public JSONObject remove() throws IOException, ParseException, InterruptedException {
		this.is_channel = false;
		JSONObject result = this.delete("/space/" + this.slug);
		this.is_channel = true;
		return result;
	}
	
	/**
	 * Create a scene
	 * @param name scene name
	 * @param data scene settings (All parameters in the API template must be present)
	 * @return a scene object
	 * @throws IOException
	 * @throws ParseException
	 * @throws InterruptedException
	 */
	public Scene createsScene(String name, JSONObject data) throws IOException, ParseException, InterruptedException {
		data.put("name", name);
		JSONObject params = new JSONObject();
		params.put("data", data);
		return new Scene(this.put("/channel/scene", params), this);
	}
	/**
	 * Get a list of all scenes
	 * @return array contenant toutes les scènes
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws ParseException
	 */
	public JSONArray getScenes() throws IOException, InterruptedException, ParseException{
		final HttpClient httpClient = HttpClient.newBuilder()
	            .version(HttpClient.Version.HTTP_2)
	            .build();
		Object list;
		
		HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(this.request_url + "/channel/scenes"))
                .setHeader("X-Api-Public", this.public_key).setHeader("X-Api-Private", this.private_key).setHeader("X-Auth-Token", this.getAPIToken())
                .build();
		HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
		
		
		JSONParser parse = new JSONParser();
	    list =  parse.parse(response.body());
	    JSONArray scenesList = new JSONArray();
	    scenesList.add(list);
	   return scenesList;
	}
	/**
	 * Upload a picture
	 * @param name picture name
	 * @param filePath path of the file
	 * @return a file path
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws ParseException
	 * @throws URISyntaxException
	 */
	public JSONObject uploadPic(String name, String filePath) throws IOException, InterruptedException, ParseException, URISyntaxException {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpPost uploadFile = new HttpPost(this.request_url+ "/broadcaster/upload/pic");
		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		builder.addTextBody("X-Auth-Token",this.getAPIToken());
		
		// attache the file to the POST :
		File file = new File(filePath);
		builder.addBinaryBody("file",
				new FileInputStream(file),
				ContentType.APPLICATION_OCTET_STREAM,
				file.getName());
		
		HttpEntity multipart = builder.build();
		uploadFile.setEntity(multipart);
		uploadFile.addHeader("X-Auth-Token",this.getAPIToken());
		CloseableHttpResponse response = httpClient.execute(uploadFile);
		HttpEntity responseEntity = response.getEntity();		
		
		String jsonString = EntityUtils.toString(response.getEntity());		
		JSONObject params = new JSONObject();
		JSONParser parse = new JSONParser();
	    params = (JSONObject) parse.parse(jsonString);
		return params ;
	}
	/**
	 * Upload a scene picture
	 * @param name image name
	 * @param filePath file path
	 * @return a file path
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws ParseException
	 * @throws URISyntaxException
	 */
	public JSONObject uploadScenePic(String name, String filePath) throws IOException, InterruptedException, ParseException, URISyntaxException {
		JSONObject response = this.uploadPic(name, filePath);
		response.put("file", "./docs" + response.get("file"));
		return response;
	}
}
