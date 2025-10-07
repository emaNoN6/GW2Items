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
public class Armor {

	private String type;
	private String weight_class;
	private int defense;
	private InfusionSlot infusion_slots;
	private InfixUpgrade infix_upgrade;
	private Integer suffix_item_id;

	/**
	 *
	 * @param type
	 * @param weight_class
	 * @param defense
	 * @param infusion_slots
	 * @param infix_upgrade
	 * @param suffix_item_id
	 */
	public Armor(String type, String weight_class, int defense, InfusionSlot infusion_slots, InfixUpgrade infix_upgrade, String suffix_item_id) {
		this.type = type;
		this.weight_class = weight_class;
		this.defense = defense;
		this.infusion_slots = infusion_slots;
		this.infix_upgrade = infix_upgrade;
		this.suffix_item_id = suffix_item_id.isEmpty()?0:Integer.parseInt(suffix_item_id);
	}

	/**
	 *
	 * @param obj
	 */
	public Armor(Object obj) {
		JSONObject o = (JSONObject) obj;
		final String suffix = o.get("suffix_item_id").toString();
		this.type = o.get("type").toString();
		this.weight_class = o.get("weight_class").toString();
		this.defense = Integer.parseInt(o.get("defense").toString());
		this.suffix_item_id = suffix.isEmpty()?0:Integer.parseInt(suffix);
		this.infusion_slots = new InfusionSlot((JSONArray)(o.get("infusion_slots")));
		this.infix_upgrade = new InfixUpgrade((JSONObject) o.get("infix_upgrade"));
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
	public String getWeight_class() {
		return weight_class;
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

		return MessageFormat.format("Armor\n\t" + "type: {0}\n\t" + "weight_class: {1}\n\t" + "defense: {2}\n\t" + "infusion_slot: {3}\n\t" + "infix_upgrade: {4}\n\t" + "suffix_item_id: {5}", type, weight_class, defense, infusion_slots.toString(), infix_upgrade.toString(), suffix_item_id);
	}
}
