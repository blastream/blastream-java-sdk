import java.io.*;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.Locale;
import java.util.regex.Pattern;

public class Instance {

	private String request_url;
	private String app_url;
	private String slug;
	private String public_key;
	private String private_key;
	private String APIToken;
	private String token;
	private String channel_url;
	private Integer embed;
	private String whitelabel_url;
	private Boolean is_channel;
	/**
	 * Constructeur de la class Instance
	 * @param public_key clé publique
	 * @param private_key clé privée
	 * @param custom_domain url custom peut être ""
	 */
	public Instance(String public_key, String private_key, String custom_domain) {
		this.request_url = "https://api.v2.blastream.com";
		this.app_url = "app.v2.blastream.com";
		this.embed = 1;
		this.is_channel = false;
		this.token = "";
		this.APIToken = "";
		this.public_key = public_key;
		this.private_key = private_key;

		if (custom_domain != "") {
			this.whitelabel_url = custom_domain;
		} else {
			this.whitelabel_url = "";
		}
	}
	
	public String getPublicKey() {
		return this.public_key;
	}
	
	public String getToken() {
		return this.token;
	}
	
	public String getAPIToken() {
		return this.APIToken;
	}
	
	public void setToken(String token) {
		this.token = token;
	}
	
	public void setChannelUrl(String url) {
		this.channel_url = url;
	}

	public String getUrl() {
		String url = this.channel_url ;
		if(this.whitelabel_url != "") {
			url = url.replace(this.app_url, this.whitelabel_url);
		}
		return url + "?token=" + this.token + "&api=" + this.public_key + "&embed=1";
	}
	
	public void setRequestUrl(String url) {
		this.request_url = url;
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
                .uri(URI.create(this.request_url + url))
                .setHeader("X-Api-Public", this.public_key).setHeader("X-Api-Private", this.private_key).setHeader("X-Auth-Token",this.getAPIToken())
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
				.uri(URI.create(this.request_url + url))
				.POST(HttpRequest.BodyPublishers.ofString(params.toString()))
                .setHeader("X-Api-Public", this.public_key).setHeader("X-Api-Private", this.private_key).setHeader("X-Auth-Token",this.getAPIToken()).setHeader("Content-type", "application/json")
				.build();
		
		HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
		
        JSONParser parse = new JSONParser();
	    JSONObject response_obj = (JSONObject) parse.parse(response.body());

		
		return response_obj;
	}
	
	public JSONObject put(String url, JSONObject params) throws IOException, ParseException, InterruptedException {
		// We create a request PUT
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(this.request_url + url))
				.PUT(HttpRequest.BodyPublishers.ofString(params.toString()))
                .setHeader("X-Api-Public", this.public_key).setHeader("X-Api-Private", this.private_key).setHeader("X-Auth-Token", this.getAPIToken()).setHeader("Content-type", "application/json")
				.build();
		
		HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
		
		JSONParser parse = new JSONParser();
		JSONObject response_obj = (JSONObject) parse.parse(response.body());
		
		return response_obj;
	}
	
	public JSONObject delete(String url) throws IOException, ParseException, InterruptedException {
		// We create a request PUT
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(this.request_url + url))
				.DELETE()
		        .setHeader("X-Api-Public", this.public_key).setHeader("X-Api-Private", this.private_key).setHeader("X-Auth-Token", this.getAPIToken()).setHeader("Content-type", "application/json")
				.build();
				
		HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
		
		JSONParser parse = new JSONParser();
		JSONObject response_obj = (JSONObject) parse.parse(response.body());
		
		return response_obj;
		
	}
	
	public void setSlug(String slug) {
		if(slug.matches("/[^A-Za-z0-9-]/")) {
			System.out.println("This is not a valid slug! Only alphanumeric and '-' character are accepted");
		}else {
		
			if (slug.length() > 64 || slug.length()<2) {
				System.out.println("Slug is either too long or too short");
			}else {
				this.slug = slug;
			}
		}
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
		if(id == null) {
			System.out.println("identifier_undefined");
		}
		params.put("id", id);
		
		if(params.get("nickname") == null) {
			params.put("nickname", id);
		}
		
		JSONObject result = this.post("/space/channel/" +this.slug + "/participant", params);		
		return this.initChannel(result);
	}
	
	public Channel initChannel(JSONObject result) {
		Channel channel = new Channel(this.public_key,this.private_key, this.whitelabel_url);
		channel.setRequestUrl(this.request_url);
		channel.setSlug(this.slug);
		channel.setResponseToken(result);
		channel.setId(result.get("id").toString());
		channel.setChannelUrl(result.get("url").toString());
		channel.setToken(result.get("token").toString());
		this.channel_url = result.get("url").toString();
		this.token = result.get("token").toString();
		if(APIToken == "") {
			this.APIToken = result.get("token").toString();
			channel.setAPIToken(result.get("token").toString());
		}
		return channel;
	}
	
	public void setResponseToken(JSONObject result) {
		this.token = result.get("token").toString();
		this.channel_url = result.get("url").toString();
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
	public String getIframe(String width, String height, JSONObject params ) {
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
