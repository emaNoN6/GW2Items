/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gw2items.models;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author Michael
 */
public class Recipe {

	private String recipe_id;
	private String type;
	private String output_item_id;
	private String output_item_count;
	private String min_rating;
	private String time_to_craft_ms;
	private String disciplines;
	private String flags;
	private List<Ingredient> ingredients;

	/**
	 *
	 */
	public static class Ingredient {

		private String item_id;
		private String count;

		/**
		 *
		 * @param item_id
		 * @param count
		 */
		public Ingredient(String item_id, String count) {
			this.item_id = item_id;
			this.count = count;
		}

		/**
		 *
		 * @return
		 */
		public String getItem_id() {
			return item_id;
		}

		/**
		 *
		 * @param item_id
		 */
		public void setItem_id(String item_id) {
			this.item_id = item_id;
		}

		/**
		 *
		 * @return
		 */
		public String getCount() {
			return count;
		}

		/**
		 *
		 * @param count
		 */
		public void setCount(String count) {
			this.count = count;
		}

		@Override
		public String toString() {
			return MessageFormat.format("Ingredient{item_id={0}, count={1}", item_id, count);
		}
	}

	/**
	 *
	 */
	public Recipe() {
		this(null, null, null, null, null, null, null, null);
	}

	/**
	 *
	 * @param recipe_id
	 * @param type
	 * @param output_item_count
	 * @param min_rating
	 * @param time_to_craft_ms
	 * @param disciplines
	 * @param flags
	 * @param ingredients
	 */
	public Recipe(String recipe_id, String type, String output_item_count, String min_rating, String time_to_craft_ms, String disciplines, String flags, List<Ingredient> ingredients) {
		this.recipe_id = recipe_id;
		this.type = type;
		this.output_item_count = output_item_count;
		this.min_rating = min_rating;
		this.time_to_craft_ms = time_to_craft_ms;
		this.disciplines = disciplines;
		this.flags = flags;
		this.ingredients = ingredients;
	}

	/**
	 *
	 * @return
	 */
	public String getRecipe_id() {
		return recipe_id;
	}

	/**
	 *
	 * @param recipe_id
	 */
	public void setRecipe_id(String recipe_id) {
		this.recipe_id = recipe_id;
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
	 * @param type
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 *
	 * @return
	 */
	public String getOutput_item_id() {
		return output_item_id;
	}

	/**
	 *
	 * @param output_item_id
	 */
	public void setOutput_item_id(String output_item_id) {
		this.output_item_id = output_item_id;
	}

	/**
	 *
	 * @return
	 */
	public String getOutput_item_count() {
		return output_item_count;
	}

	/**
	 *
	 * @param output_item_count
	 */
	public void setOutput_item_count(String output_item_count) {
		this.output_item_count = output_item_count;
	}

	/**
	 *
	 * @return
	 */
	public String getMin_rating() {
		return min_rating;
	}

	/**
	 *
	 * @param min_rating
	 */
	public void setMin_rating(String min_rating) {
		this.min_rating = min_rating;
	}

	/**
	 *
	 * @return
	 */
	public String getTime_to_craft_ms() {
		return time_to_craft_ms;
	}

	/**
	 *
	 * @param time_to_craft_ms
	 */
	public void setTime_to_craft_ms(String time_to_craft_ms) {
		this.time_to_craft_ms = time_to_craft_ms;
	}

	/**
	 *
	 * @return
	 */
	public String getDisciplines() {
		return disciplines;
	}

	/**
	 *
	 * @param disciplines
	 */
	public void setDisciplines(String disciplines) {
		this.disciplines = disciplines;
	}

	/**
	 *
	 * @return
	 */
	public String getFlags() {
		return flags;
	}

	/**
	 *
	 * @param flags
	 */
	public void setFlags(String flags) {
		this.flags = flags;
	}

	/**
	 *
	 * @return
	 */
	public List<Ingredient> getIngredients() {
		return ingredients;
	}

	/**
	 *
	 * @param ingredients
	 */
	public void setIngredients(List<Ingredient> ingredients) {
		this.ingredients = ingredients;
	}

	@Override
	public String toString() {
		return MessageFormat.format("Recipe[recipe_id={0}, type={1}, output_item_id={2}, output_item_count={3}, min_rating={4}, time_to_craft_ms={5}, disciplines={6}, flags={7}, ingredients={8}]", recipe_id, type, output_item_id, output_item_count, min_rating, time_to_craft_ms, disciplines, flags, ingredients);
	}

	/**
	 *
	 * @param o
	 */
	public Recipe(JSONObject o) {
		this.recipe_id = o.get("recipe_id").toString();
		this.type = o.get("type").toString();
		this.output_item_id = o.get("output_item_id").toString();
		this.output_item_count = o.get("output_item_count").toString();
		this.min_rating = o.get("min_rating").toString();
		this.time_to_craft_ms = o.get("time_to_craft_ms").toString();
		this.disciplines = jsonArrayToString((JSONArray) o.get("disciplines"));
		this.flags = jsonArrayToString((JSONArray) o.get("flags"));
		this.ingredients = getIngredients((JSONArray) o.get("ingredients"));

	}

	private List<Ingredient> getIngredients(JSONArray obj) {
		List<Ingredient> l = new ArrayList<>();
		for (int j = 0; j < obj.size(); j++) {
			JSONObject o = (JSONObject) obj.get(j);
			String i = o.get("item_id").toString();
			String c = o.get("count").toString();
			l.add(new Ingredient(i, c));
		}
		return l;
	}

	private String jsonArrayToString(JSONArray a) {
		StringBuilder ret = new StringBuilder();
		String retVal;
		for (int i = 0; i < a.size(); i++) {
			ret.append(a.get(i).toString()).append(',');
		}
		if (ret.length() > 1) {
			retVal = ret.toString().substring(0, ret.length() - 1);
		} else {
			retVal = "none";
		}
		return retVal;
	}

}
