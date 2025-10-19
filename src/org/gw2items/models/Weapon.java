/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gw2items.models;

import java.text.MessageFormat;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author Michael
 */
public class Weapon {

	private String type = null;
	private String damage_type = null;
	private final int min_power;
	private final int max_power;
	private final int defense;
	private final InfusionSlot infusion_slots;
	private final InfixUpgrade infix_upgrade;
	private Integer suffix_item_id = null;

	/**
	 *
	 * @param o
	 */
	public Weapon(Object o) {
		JSONObject obj = (JSONObject) o;
		final String suffix = obj.get("suffix_item_id").toString();
		this.type = obj.get("type").toString();
		this.damage_type = obj.get("damage_type").toString();
		this.min_power = Integer.parseInt(obj.get("min_power").toString());
		this.max_power = Integer.parseInt(obj.get("max_power").toString());
		this.defense = Integer.parseInt(obj.get("defense").toString());
		this.suffix_item_id = suffix.isEmpty()?0:Integer.valueOf(suffix);
		this.infusion_slots = new InfusionSlot((JSONArray) obj.get("infusion_slots"));
		this.infix_upgrade = new InfixUpgrade((JSONObject) obj.get("infix_upgrade"));
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
	public String getDamage_type() {
		return damage_type;
	}

	/**
	 *
	 * @return
	 */
	public int getMin_power() {
		return min_power;
	}

	/**
	 *
	 * @return
	 */
	public int getMax_power() {
		return max_power;
	}

	/**
	 *
	 * @return
	 */
	public int getDefense() {
		return defense;
	}

	/**
	 *
	 * @return
	 */
	public InfusionSlot getInfusion_slots() {
		return infusion_slots;
	}

	/**
	 *
	 * @return
	 */
	public InfixUpgrade getInfix_upgrade() {
		return infix_upgrade;
	}

	/**
	 *
	 * @return
	 */
	public Integer getSuffix_item_id() {
			return suffix_item_id;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		InfixUpgrade i = this.infix_upgrade;
		List<Attributes> a = i.getAttributes();
		if (a != null) {
			for (int j = 0; j < a.size(); j++) {
				sb.append("\n\t\t").append(a.get(j).toString());
			}
		} else sb.append("none");
		return MessageFormat.format("Weapon\n\ttype: {0}\n\tdamage_type: {1}\n\tmin_power: {2}\n\tmax_power: {3}\n\tdefense: {4}\n\tinfusion_slots: {5}\n\tinfix_upgrade: {6}\n\tsuffix_item_id: {7}", type, damage_type, min_power, max_power, defense, infusion_slots.toString(), sb.toString(), suffix_item_id);
	}
}