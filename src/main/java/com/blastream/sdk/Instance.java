package com.blastream.sdk;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.Locale;
import java.util.regex.Pattern;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("unchecked")
public class Instance {

	static final Logger logger = LoggerFactory.getLogger(Instance.class);
	
	protected String requestUrl;
	protected String appUrl;
	protected String slug;
	protected String publicKey;
	protected String privateKey;
	protected String apiToken;
	protected String token;
	protected String channelUrl;
	protected Integer embed;
	protected String whitelabelUrl;
	protected Boolean isChannel;
	protected Channel channel;
	
	/**
	 * Constructeur de la class Instance
	 * @param public_key clé publique
	 * @param private_key clé privée
	 * @param custom_domain url custom peut être ""
	 */
	public Instance(String public_key, String private_key, String custom_domain) {
		this.publicKey = public_key;
		this.privateKey = private_key;
		this.requestUrl = "https://api.v2.blastream.com";
		this.appUrl = "app.v2.blastream.com";
		this.embed = 1;
		this.token = "";
		this.apiToken = "";
		this.isChannel = false;
		this.whitelabelUrl = custom_domain != "" ? custom_domain : "";
	}
	
	public String getPublicKey() {
		return this.publicKey;
	}
	
	public String getToken() {
		return this.token;
	}
	
	public void setToken(String token) {
		this.token = token;
	}
	
	public String getAPIToken() {
		return this.apiToken;
	}
	
	public void setAPIToken(String token) {
		this.apiToken = token;
	}
	
	public Channel getChannel() {
		return this.channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}
	
	public String getChannelUrl() {
		return this.channelUrl;
	}
	
	public void setChannelUrl(String url) {
		this.channelUrl = url;
	}

	public String getUrl() {
		String url = this.channelUrl ;
		if(this.whitelabelUrl != "") {
			url = url.replace(this.appUrl, this.whitelabelUrl);
		}
		return url + "?token=" + this.token + "&api=" + this.publicKey + "&embed=1";
	}
	
	public void setRequestUrl(String url) {
		this.requestUrl = url;
	}
	
	public void setSlug(String slug) {
		if (slug.matches("/[^A-Za-z0-9-]/")) {
			logger.error("This is not a valid slug! Only alphanumeric and '-' character are accepted");
		} else {
			if (slug.length() > 64 || slug.length() < 2) {
				logger.error("Slug is either too long or too short");
			} else {
				this.slug = slug.toLowerCase();;
			}
		}
	}
	
	public String slugify(String text) {
		  final Pattern NONLATIN = Pattern.compile("[^\\w-]");
		  final Pattern WHITESPACE = Pattern.compile("[\\s]");
		  
		  String nowhitespace = WHITESPACE.matcher(text).replaceAll("-");
		  String normalized = Normalizer.normalize(nowhitespace, Form.NFD);
		  String slug = NONLATIN.matcher(normalized).replaceAll("");
		  return slug.toLowerCase(Locale.ENGLISH);
	}
	
	private final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .build();

	public JSONObject get(String url) throws IOException, ParseException, InterruptedException {
		// We create a request here a GET with a uri and a header
		HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(this.requestUrl + url))
                .setHeader("X-Api-Public", this.publicKey).setHeader("X-Api-Private", this.privateKey).setHeader("X-Auth-Token",this.getAPIToken())
                .build();
		
		// we send the request and save the response the body and header are strings
		HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
		
	    JSONParser parse = new JSONParser();
	    JSONObject response_obj = (JSONObject) parse.parse(response.body());
	    
	    return response_obj;

	}

	public JSONObject post(String url, JSONObject params) throws IOException, ParseException, InterruptedException {
		// We create a request POST 
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(this.requestUrl + url))
				.POST(HttpRequest.BodyPublishers.ofString(params.toString()))
                .setHeader("X-Api-Public", this.publicKey).setHeader("X-Api-Private", this.privateKey).setHeader("X-Auth-Token",this.getAPIToken()).setHeader("Content-type", "application/json")
				.build();
		
		HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
		
        JSONParser parse = new JSONParser();
	    JSONObject response_obj = (JSONObject) parse.parse(response.body());

		return response_obj;
	}
	
	public JSONObject put(String url, JSONObject params) throws IOException, ParseException, InterruptedException {
		// We create a request PUT
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(this.requestUrl + url))
				.PUT(HttpRequest.BodyPublishers.ofString(params.toString()))
                .setHeader("X-Api-Public", this.publicKey).setHeader("X-Api-Private", this.privateKey).setHeader("X-Auth-Token", this.getAPIToken()).setHeader("Content-type", "application/json")
				.build();
		
		HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
		
		JSONParser parse = new JSONParser();
		JSONObject response_obj = (JSONObject) parse.parse(response.body());
		
		return response_obj;
	}
	
	public JSONObject delete(String url) throws IOException, ParseException, InterruptedException {
		// We create a request PUT
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(this.requestUrl + url))
				.DELETE()
		        .setHeader("X-Api-Public", this.publicKey).setHeader("X-Api-Private", this.privateKey).setHeader("X-Auth-Token", this.getAPIToken()).setHeader("Content-type", "application/json")
				.build();
				
		HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
		
		JSONParser parse = new JSONParser();
		JSONObject response_obj = (JSONObject) parse.parse(response.body());
		
		return response_obj;
	}
	
	/**
	 * Permets d'obtenir une room ou d'en créer une si elle n'existe pas
	 * @param slug nom de la room
	 * @param params paramètres de subscriptions pour créer.
	 * @return un channel
	 * @throws IOException
	 * @throws ParseException
	 * @throws InterruptedException
	 */
	public Channel createOrGetChannel(String slug, JSONObject params) throws IOException, ParseException, InterruptedException {
		this.setSlug(slug);
		JSONObject result = new JSONObject();
		result = this.post("/space/channel/" + this.slug, params);
		return this.initChannel(result);
	}
	/**
	 * Permets d'obtenir un participant 
	 * @param slug nom de la room
	 * @param id id du participant
	 * @param params informations sur le participant
	 * @return un channel
	 * @throws IOException
	 * @throws ParseException
	 * @throws InterruptedException
	 */
	public Channel createOrGetParticipant(String slug, String id, JSONObject params) throws IOException, ParseException, InterruptedException {
		this.setSlug(slug);
		if (id == null) {
			logger.error("identifier id is null");
		}
		params.put("id", id);
		if (params.get("nickname") == null) {
			params.put("nickname", id);
		}
		JSONObject result = this.post("/space/channel/" +this.slug + "/participant", params);		
		return this.initChannel(result);
	}
	
	public Channel initChannel(JSONObject result) {
		channel = new Channel(this.publicKey,this.privateKey, this.whitelabelUrl);
		channel.setRequestUrl(this.requestUrl);
		channel.setSlug(this.slug);
		channel.setResponseToken(result);
		channel.setId(result.get("id").toString());
		channel.setChannelUrl(result.get("url").toString());
		channel.setToken(result.get("token").toString());		
		channel.setAPIToken(result.get("token").toString());
		setChannelUrl(result.get("url").toString());
		setToken(result.get("token").toString());
		return channel;
	}
	
	public void setResponseToken(JSONObject result) {
		this.token = result.get("token").toString();
		this.channelUrl = result.get("url").toString();
	}
	/**
	 * Supprime un token d'un utilisateur ou d'un admin
	 * @param token token à supprimer
	 * @param params vide
	 * @throws IOException
	 * @throws ParseException
	 * @throws InterruptedException
	 */
	public JSONObject revokeToken(String token, JSONObject params) throws IOException, ParseException, InterruptedException {
		return this.post("/space/revoke-token/" + token, params);
	}
	/**
	 * Supprime tous les tokens liés à un channel
	 * @param slug nom du channel
	 * @param params vide
	 * @throws IOException
	 * @throws ParseException
	 * @throws InterruptedException
	 */
	public JSONObject revokeTokens(String slug, JSONObject params) throws IOException, ParseException, InterruptedException {
		return this.post("/space/revoke-token/" + slug, params);
	}
	/**
	 * Record an url to be notified when a new record is available
	 * @param url 
	 * @throws IOException
	 * @throws ParseException
	 * @throws InterruptedException
	 */
	public JSONObject registerHook(String url) throws IOException, ParseException, InterruptedException {
		JSONObject params = new JSONObject();
		params.put("url", url);
		return this.post("/space/hook", params);
	}
	
	public JSONObject getPlans() throws IOException, ParseException, InterruptedException {
		return this.get("/plans");
	}
	/**
	 * Get an Iframe
	 * @param width width of the frame
	 * @param height height of the frame
	 * @param params parameters of the Iframe
	 * @return
	 */
	public String getIframe(String width, String height, JSONObject params) {
		String url;
		if(!params.containsKey("url")) {
			url = this.getUrl();
		}else {
			url = params.get("url").toString();
			params.remove("url");
		}
		
		String style = "";
		if (params.containsKey("style")) {
			style = params.get("style").toString();
			params.remove("style");
		}
						
		String htmlFrame = "<iframe allow=\"microphone; camera; display-capture\" width=\"" + width + "\" height=\"" + height + "\" src=\"" + url + "\" frameborder=\"0\" scrolling=\"no\" allowFullScreen=\"true\" style=\"" + style + "\" webkitallowfullscreen=\"true\" mozallowfullscreen=\"true\"></iframe>" ;
		return htmlFrame;
	}
}