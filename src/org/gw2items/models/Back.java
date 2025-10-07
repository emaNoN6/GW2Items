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
public class Back {

	InfusionSlot infusion_slots;
	InfixUpgrade infix_upgrade;
	Integer suffix_item_id;

	/**
	 *
	 * @param obj
	 */
	public Back(Object obj) {
		JSONObject o = (JSONObject) obj;
		final String suffix = o.get("suffix_item_id").toString();
		this.suffix_item_id = suffix.isEmpty()?0:Integer.parseInt(suffix);
		this.infusion_slots = new InfusionSlot((JSONArray) (o.get("infusion_slots")));
		this.infix_upgrade = new InfixUpgrade((JSONObject) o.get("infix_upgrade"));
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

		return MessageFormat.format("Back\n\tinfusion_slots: {0}\n\tinfix_upgrade={1}\n\tsuffix_item_id={2}{3}", infusion_slots.toString(), infix_upgrade.toString(), suffix_item_id, '}');
	}
}
