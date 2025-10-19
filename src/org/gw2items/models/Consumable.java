/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gw2items.models;

import java.text.MessageFormat;
import org.json.simple.JSONObject;

/**
 *
 * @author Michael
 */
public class Consumable {

	String type;
	int duration_ms;
	String description;
	String unlock_type;
	String color_id;
	String recipe_id;

	/**
	 *
	 * @param obj
	 */
	public Consumable(Object obj) {
		JSONObject o = (JSONObject) obj;
		this.type = o.get("type").toString();
		this.description = (o.containsKey("description")) ? o.get("description").toString() : null;
		this.duration_ms = (o.containsKey("duration_ms")) ? Integer.parseInt(o.get("duration_ms").toString()) : 0;
		this.unlock_type = (o.containsKey("unlock_type")) ? o.get("unlock_type").toString() : null;
		this.color_id = (o.containsKey("color_id")) ? o.get("color_id").toString() : null;
		this.recipe_id = (o.containsKey("recipe_id")) ? o.get("recipe_id").toString() : null;
	}

	private String toHMS(int milliseconds) {
		int seconds = milliseconds / 1000 % 60;
		int minutes = (milliseconds / (1000 * 60)) % 60;
		int hours = (milliseconds / (1000 * 60 * 60)) % 24;
		return String.format("%dh %dm %ds", hours, minutes, seconds);
	}

	/**
	 *
	 * @return
	 */
	public String getType() {
		return type;
	}

	/**
	 *
	 * @return
	 */
	public int getDuration_ms() {
		return duration_ms;
	}

	/**
	 *
	 * @return
	 */
	public String getDescription() {
		return description;
	}

	/**
	 *
	 * @return
	 */
	public String getUnlock_type() {
		return unlock_type;
	}

	/**
	 *
	 * @return
	 */
	public String getColor_id() {
		return color_id;
	}

	/**
	 *
	 * @return
	 */
	public String getRecipe_id() {
		return recipe_id;
	}

	@Override
	public String toString() {
		return MessageFormat.format("Consumable\n\ttype: {0}\n\tdescription: {1}\n\tduration: {2}\n\tunlock_type: {3}\n\tcolor_id: {4}\n\trecipe_id: {5}", type, description, toHMS(duration_ms), unlock_type, color_id, recipe_id);
	}
}
