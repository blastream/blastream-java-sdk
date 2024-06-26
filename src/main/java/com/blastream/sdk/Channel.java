package com.blastream.sdk;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@SuppressWarnings("unchecked")
public class Channel extends Instance {

	static final Logger logger = LoggerFactory.getLogger(Channel.class);

	private String id;
	private String apiPrefix;

	/**
	 * Constructor of Chanel class
	 * @param public_key
	 * @param private_key
	 * @param custom_domain custom url can be ""
	 */
	public Channel(String public_key, String private_key, String custom_domain) {
		super(public_key, private_key, custom_domain);
        this.apiPrefix = "";
		this.isChannel = true;
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
	                .uri(URI.create(this.requestUrl + "/channel/collaborators"))
	                .setHeader("X-Api-Public", this.publicKey).setHeader("X-Api-Private", this.privateKey).setHeader("X-Auth-Token",this.getAPIToken())
	                .build();
			HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
			
			JSONParser parse = new JSONParser();
		    list = (JSONArray) parse.parse(response.body());

		}else {
			HttpRequest request = HttpRequest.newBuilder()
	                .GET()
	                .uri(URI.create(this.requestUrl + "/channel/collaborators" + type))
	                .setHeader("X-Api-Public", this.publicKey).setHeader("X-Api-Private", this.privateKey).setHeader("X-Auth-Token",this.getAPIToken())
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
		    	//super.setToken(collab.get("token").toString());
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
		this.isChannel = false;
		JSONObject data = new JSONObject();
		data.put("msg", params.get("msg"));
		data.put("username", params.get("username"));
		data.put("slug", this.slug);
		JSONObject result = this.post("/api/msg", data);
		this.isChannel = true;
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
		this.isChannel = false;
		JSONObject result = this.delete("/space/" + this.slug);
		this.isChannel = true;
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
                .uri(URI.create(this.requestUrl + "/channel/scenes"))
                .setHeader("X-Api-Public", this.publicKey).setHeader("X-Api-Private", this.privateKey).setHeader("X-Auth-Token", this.getAPIToken())
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
	 * @param filePath path of the file
	 * @return a file path
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws ParseException
	 * @throws URISyntaxException
	 */
	public JSONObject uploadPic(String filePath) throws IOException, InterruptedException, ParseException, URISyntaxException {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpPost uploadFile = new HttpPost(this.requestUrl+ "/broadcaster/upload/pic");
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
	public JSONObject uploadScenePic(String filePath) throws IOException, InterruptedException, ParseException, URISyntaxException {
		JSONObject response = this.uploadPic(filePath);
		response.put("file", "./docs" + response.get("file"));
		return response;
	}

    public void setApiPrefix(String prefix) {
        this.apiPrefix = prefix;
    }
    
    public String getApiPrefix() {
        return this.apiPrefix;
    }

    public JSONObject setMode(String mode) throws IOException, InterruptedException, ParseException, URISyntaxException {
        if(mode.equals("vodToLive")) {
            JSONObject settings = this.getSettings();
            JSONObject advanced = (JSONObject) settings.get("advanced");
            advanced.put("live_blastream_source", "vod");
            advanced.put("live_proto", "hls");

            JSONObject settingsToSend = new JSONObject();
        
            settingsToSend.put("advanced", advanced);
            settingsToSend.put("autojoin", 0);
            settingsToSend.put("autolivestream", 1);
            settingsToSend.put("allowed_request_cam", 0);

            return this.updateSettings(settingsToSend);
        }
        if(mode.equals("rtmp")) {
            JSONObject settings = this.getSettings();
            JSONObject advanced = (JSONObject) settings.get("advanced");
            advanced.put("streaming_kind", "rtmp");
            advanced.put("live_proto", "hls");

            JSONObject settingsToSend = new JSONObject();
        
            settingsToSend.put("advanced", advanced);
            settingsToSend.put("autojoin", 0);
            settingsToSend.put("autolivestream", 0);
            settingsToSend.put("allowed_request_cam", 0);

            return this.updateSettings(settingsToSend);
        }
        return new JSONObject();
    }
    
    public JSONObject getSession() throws IOException, InterruptedException, ParseException, URISyntaxException {
        return this.get("/live/session?channel_slug=" + this.slug);
    }
    
    public JSONObject startSession(String queryString) throws IOException, InterruptedException, ParseException, URISyntaxException {
        JSONObject session = this.getSession();
        return this.get("/videoconf/" + this.id + "/session/" + session.get("token") + '?' + queryString);
    }
    
    public JSONObject stopSession() throws IOException, InterruptedException, ParseException, URISyntaxException {
        return this.post("/channel/" + this.apiPrefix + '_' + this.apiPrefix + "_" + this.slug + "/stopvisio", new JSONObject());
    }

    public JSONObject createSimulcast(String name, String rtmpUrl, String rtmpKey, JSONObject params) throws IOException, InterruptedException, ParseException, URISyntaxException {
        JSONObject data = new JSONObject();
		data.put("name", name);
        data.put("rtmp_url", rtmpUrl);
        data.put("rtmp_key", rtmpKey);
        data.put("active", 1);
        data.put("chat", 0);
        data.put("service", "rtmp");

        if(params.containsKey("rtmp_username"))
            data.put("rtmp_username", params.get("rtmp_username"));

        if(params.containsKey("rtmp_password"))
            data.put("rtmp_password", params.get("rtmp_password"));

        return this.put("/simulcast", data);
    }
    
    public JSONObject deleteSimulcast(Integer simulcastId) throws IOException, InterruptedException, ParseException, URISyntaxException {
        return this.delete("/simulcast/" + simulcastId.toString());
    }

    public JSONObject getStatSessions(Integer start, Integer limit) throws IOException, InterruptedException, ParseException, URISyntaxException {
        return this.get("/channel/session_stats/?start=" + start + "&limit=" + limit);
    }
    
    public JSONObject getStatSession(Integer id) throws IOException, InterruptedException, ParseException, URISyntaxException {
        return this.get("/channel/session_stats/" + id.toString());
    }

    public JSONObject publishReplay(Integer replayId) throws IOException, InterruptedException, ParseException, URISyntaxException {
        return this.post("/video/" + replayId + "/activate", new JSONObject());
    }
    
}