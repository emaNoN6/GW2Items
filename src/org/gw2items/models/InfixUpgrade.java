/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gw2items.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.gw2items.Start;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author Michael
 */
public class InfixUpgrade {

	Buff buff = null;
	List<Attributes> attributes = null;

	/**
	 *
	 */
	public class Buff {

		String skill_id = null;
		List<String> description = null;

		/**
		 *
		 * @param o
		 */
		public Buff(JSONObject o) {
			this.skill_id = o.get("skill_id").toString();
			String[] d = new String[4];
			try {
				d = o.get("description").toString().split("\\r?\\n");
			} catch (NullPointerException e) {
				Start.setOutput("--Null Description in buff".concat(skill_id));
			}
			List<String> l = new ArrayList<>();
			l.addAll(Arrays.asList(d));
			this.description = l;
		}

		/**
		 *
		 * @return
		 */
		public String getSkill_id() {
			return skill_id;
		}

		/**
		 *
		 * @param skill_id
		 */
		public void setSkill_id(String skill_id) {
			this.skill_id = skill_id;
		}

		/**
		 *
		 * @return
		 */
		public List<String> getDescription() {
			return description;
		}

		/**
		 *
		 * @param description
		 */
		public void setDescription(List<String> description) {
			this.description = description;
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < this.description.size(); i++) {
				sb.append("\n\t\t").append(this.description.get(i));
			}
			return sb.toString();
		}
	}

	/**
	 *
	 * @return
	 */
	public List<Attributes> getAttributes() {
		return attributes;
	}

	/**
	 *
	 * @return
	 */
	public Buff getBuff() {
		return buff;
	}

	private void setAttributes(List<Attributes> attributes) {
		this.attributes = attributes;
	}

	/**
	 *
	 * @param obj
	 */
	public InfixUpgrade(JSONObject obj) {
		if (obj != null) {
			JSONArray a = (JSONArray) obj.get("attributes");
			JSONObject b = (JSONObject) obj.get("buff");
			List<Attributes> at = new ArrayList<>();
			for (int i = 0; i < a.size(); i++) {
				at.add(new Attributes((JSONObject) a.get(i)));
			}
			this.setAttributes(at);
			if (b != null) {
				Buff buf = new Buff(b);
				this.buff = buf;
			}
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		List<Attributes> a = this.getAttributes();
		if (a != null) {
			for (int j = 0; j < a.size(); j++) {
				sb.append("\n\t\t").append(a.get(j).toString());
			}
		} else {
			sb.append("none");
		}
		if (buff != null) {
			sb.append("\n\tbuff:").append(buff.toString());
		}
		return sb.toString();
	}
}
