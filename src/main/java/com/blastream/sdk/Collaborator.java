package com.blastream.sdk;

import java.io.IOException;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

@SuppressWarnings("unchecked")
public class Collaborator {

	private Instance instance;
	private JSONObject data;

	public Collaborator(JSONObject data, Instance instance) {
		this.data = data;
		this.instance = instance;
	}

	public String getIframe(String width, String height, JSONObject params) {
        params.put("url", this.data.get("invite_link") + "?api=" + this.instance.getPublicKey());
        return this.instance.getIframe(width, height, params);
    }

	public String getDisplayname() {
		return this.data.get("displayname").toString();
	}

	public String getStatus() {
		return this.data.get("status").toString();
	}

	public JSONObject getData() {
		return this.data;
	}
	/**
	 * Mets à jours les paramètres du collaborateur
	 * @param displayname pseudonyme 
	 * @param status rôle
	 * @param params autres informations
	 * @return nouveaux paramètres
	 * @throws IOException
	 * @throws ParseException
	 * @throws InterruptedException
	 */
	public JSONObject update(String displayname, String status, JSONObject params) throws IOException, ParseException, InterruptedException {
		params.put("displayname", displayname);
		params.put("email", data.get("email"));
		params.put("Status", status);
		JSONObject res = this.instance.post("/channel/collabaorator/" + this.data.get("token"), params);
		this.data = res;
		return res;
	}
	/**
	 * Supprime le collaborateur
	 * @throws IOException
	 * @throws ParseException
	 * @throws InterruptedException
	 */
	public void remove() throws IOException, ParseException, InterruptedException {
		this.instance.delete("/channel/collaborator" + this.data.get("token"));
	}

}