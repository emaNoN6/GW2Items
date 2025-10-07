/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gw2items.models;

import java.text.MessageFormat;
import org.gw2items.ChatCode;
import org.gw2items.Enums.Flags;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author Michael
 */
public class ItemDetail {

	private Integer itemId;
	private String name;
	private String description;
	private String type;
	private int level;
	private String rarity;
	private int vendorValue;
	private String iconFileId;
	private String iconFileSignature;
	private String gameTypes;
	private String flags;
	private String restrictions;
	private String preview;
	private Object object;

	/**
	 *
	 */
	public ItemDetail() {
		this(null, "EMPTY", null, "ERROR", 0, "ERROR", 0, null, null, null, null, null, null);
	}

	/**
	 *
	 * @param obj
	 */
	public ItemDetail(JSONObject obj) {
		this.itemId = Integer.parseInt(obj.get("item_id").toString());
		this.name = (String) obj.get("name");
		this.description = ((String)obj.get("description")).replaceAll("<[^>]*>", "");
		this.type = (String) obj.get("type");
		this.level = Integer.parseInt((String) obj.get("level"));
		this.rarity = (String) obj.get("rarity");
		this.vendorValue = Integer.parseInt((String) obj.get("vendor_value"));
		this.iconFileId = obj.get("icon_file_id").toString();
		this.iconFileSignature = (String) obj.get("icon_file_signature");
		this.gameTypes = jsonArrayToString((JSONArray) obj.get("game_types"));
		this.flags = flagsToCSV((JSONArray) obj.get("flags"));
		this.restrictions = jsonArrayToString((JSONArray) obj.get("restrictions"));
		this.preview = new ChatCode(Integer.parseInt(obj.get("item_id").toString())).toString();

	}

	public int getVendorValue() {
		return vendorValue;
	}

	public String getPreview() {
		return preview;
	}

	/**
	 *
	 * @param item_id
	 * @param name
	 * @param description
	 * @param type
	 * @param level
	 * @param rarity
	 * @param vendor_value
	 * @param icon_file_id
	 * @param icon_file_signature
	 * @param game_types
	 * @param flags
	 * @param restrictions
	 * @param o
	 */
	public ItemDetail(Integer item_id, String name, String description, String type, int level, String rarity, int vendor_value, String icon_file_id, String icon_file_signature, String game_types, String flags, String restrictions, Object o) {
		this.itemId = item_id;
		this.name = name;
		this.description = description;
		this.type = type;
		this.level = level;
		this.rarity = rarity;
		this.vendorValue = vendor_value;
		this.iconFileId = icon_file_id;
		this.iconFileSignature = icon_file_signature;
		this.gameTypes = game_types;
		this.flags = flags;
		this.restrictions = restrictions;
		this.object = o;
	}

	/**
	 *
	 * @return
	 */
	public Integer getItemId() {
		return itemId;
	}

	/**
	 *
	 * @param itemId
	 */
	public void setItemId(Integer itemId) {
		this.itemId = itemId;
	}

	/**
	 *
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 *
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
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
	 * @param description
	 */
	public void setDescription(String description) {
		this.description = description;
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
	public int getLevel() {
		return level;
	}

	/**
	 *
	 * @param level
	 */
	public void setLevel(int level) {
		this.level = level;
	}

	/**
	 *
	 * @return
	 */
	public String getRarity() {
		return rarity;
	}

	/**
	 *
	 * @param rarity
	 */
	public void setRarity(String rarity) {
		this.rarity = rarity;
	}

	/**
	 *
	 * @return
	 */
	public int getVendor_Value() {
		return vendorValue;
	}

	/**
	 *
	 * @return
	 */
	public String getVendor_value() {
		int v = vendorValue;
		int g = (int) Math.floor(v / 100 / 100);
		v -= g * 100 * 100;
		int s = (int) Math.floor(v / 100);
		v -= s * 100;
		int c = v;

		return "" + ((g > 0) ? g + "g " : "") + ((s > 0) ? s + "s " : "") + ((c > 0) ? c + "c" : "");
	}

	/**
	 *
	 * @param vendorValue
	 */
	public void setVendorValue(int vendorValue) {
		this.vendorValue = vendorValue;
	}

	/**
	 *
	 * @return
	 */
	public String getIconFileId() {
		return iconFileId;
	}

	/**
	 *
	 * @param iconFileId
	 */
	public void setIconFileId(String iconFileId) {
		this.iconFileId = iconFileId;
	}

	/**
	 *
	 * @return
	 */
	public String getIconFileSignature() {
		return iconFileSignature;
	}

	/**
	 *
	 * @param iconFileSignature
	 */
	public void setIconFileSignature(String iconFileSignature) {
		this.iconFileSignature = iconFileSignature;
	}

	/**
	 *
	 * @return
	 */
	public String getGameTypes() {
		return gameTypes;
	}

	/**
	 *
	 * @param gameTypes
	 */
	public void setGameTypes(String gameTypes) {
		this.gameTypes = gameTypes;
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
	public String getRestrictions() {
		return restrictions;
	}

	/**
	 *
	 * @param o
	 */
	public void setItem(Object o) {
		this.object = o;
	}

	/**
	 *
	 * @param restrictions
	 */
	public void setRestrictions(JSONArray restrictions) {
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
			retVal = null;
		}
		return retVal;
	}

	/**
	 * 
	 * @param stringArray Array of strings to join
	 * @return Comma separated string
	 */
	private String flagsToCSV(final JSONArray stringArray) {
		final StringBuilder ret = new StringBuilder();
		String retVal = null;
		for (int i = 0; i < stringArray.size(); i++) {
			ret.append(Flags.valueOf(stringArray.get(i).toString())).append(',');
		}
		if (ret.length() > 1) {
			retVal = ret.toString().substring(0, ret.length() - 1);
		}
		return retVal;
	}

	@Override
	public String toString() {
		String icon = "[img]https://render.guildwars2.com/file/".concat(iconFileSignature).concat("/").concat(iconFileId).concat(".png[/img]"); //{signature}/{file_id}.{format}
		return MessageFormat.format("\n" + "\n" + "Item Detail:\n" + "\titem_id: {0}\n" + "\ticon: {12}\n" + "\tname: {1}\n" + "\tdescription: {2}\n" + "\ttype: {3}\n" + "\tlevel: {4}\n" + "\trarity: {5}\n" + "\tvendor_value: {6}\n" + "\tgame_types: {9}\n" + "\tflags: {10}\n" + "\trestrictions: {11}", itemId, name, description, type, level, rarity, getVendor_value(), iconFileId, iconFileSignature, gameTypes, flags, restrictions, icon);
	}
}
