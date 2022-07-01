import java.io.IOException;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

public class Scene {
	private JSONObject data;
	private Instance instance;

	public Scene(JSONObject data, Instance instance) {
		this.data = data;
		this.instance = instance;
	}
	/**
	 * Mets à jour les paramètres de la scène
	 * @param data nouveaux paramètres
	 * @return Paramètres de la scène
	 * @throws IOException
	 * @throws ParseException
	 * @throws InterruptedException
	 */
	public JSONObject update(JSONObject data) throws IOException, ParseException, InterruptedException {
		JSONObject res = this.instance.post("/channel/scene/" + this.data.get("id"), data);
		this.data = res;
		return res;
	}

	public JSONObject getData() {
		return this.data;
	}

	public String isDefault() {
		return this.data.get("default_scene").toString();
	}
	/**
	 * Supprime la scène
	 * @param data paramètres de la scène
	 * @throws IOException
	 * @throws ParseException
	 * @throws InterruptedException
	 */
	public void remove(JSONObject data) throws IOException, ParseException, InterruptedException {
		this.instance.delete("/channel/scene/" + this.data.get("id").toString());
	}
	/**
	 * Permets d'obtenir une scène
	 * @param id id de la scène
	 * @return une scène
	 * @throws IOException
	 * @throws ParseException
	 * @throws InterruptedException
	 */
	public Scene getScene(String id) throws IOException, ParseException, InterruptedException {
		JSONObject scene = this.instance.get("/channel/scene/" + id);
		return new Scene(scene, this.instance);
	}
}