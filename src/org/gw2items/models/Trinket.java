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
public class Trinket {

	String type;
	InfusionSlot infusion_slots;
	InfixUpgrade infix_upgrade;
	Integer suffix_item_id;

	/**
	 *
	 * @param obj
	 */
	public Trinket(Object obj) {
		JSONObject o = (JSONObject) obj;
		final String suffix = o.get("suffix_item_id").toString();
		this.type = o.get("type").toString();
		this.suffix_item_id = suffix.isEmpty()?0:Integer.valueOf(suffix);
		this.infusion_slots = new InfusionSlot((JSONArray) (o.get("infusion_slots")));
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
		} else {
			sb.append("none");
		}
		return(MessageFormat.format("Trinket\n\ttype: {0}\n\tinfusion_slots: {1}\n\t{2}\n\tsuffix_item_id: {3}", this.type, this.infusion_slots.toString(), infix_upgrade.toString(), this.suffix_item_id));
	}
}
