/*
 * To change this template, choose Tools | Templates
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
public class UpgradeComponent {

	String type;
	List<String> flags;
	List<String> infusion_upgrade_flags;
	List<String> bonuses;
	InfixUpgrade infix_upgrade;
	String suffix;

	/**
	 *
	 * @param obj
	 */
	public UpgradeComponent(Object obj) {
		JSONObject o = (JSONObject) obj;
		int i;
		this.type = o.get("type").toString();
		List<String> f = new ArrayList<>();
		JSONArray ja;
		ja = (JSONArray) o.get("flags");
		for (i = 0; i < ja.size(); i++) {
			f.add(ja.get(i).toString());
		}
		this.flags = f;
		List<String> inf = new ArrayList<>();
		ja = (JSONArray) o.get("infusion_upgrade_flags");
		for (i = 0; i < ja.size(); i++) {
			inf.add(ja.get(i).toString());
		}
		this.infusion_upgrade_flags = inf;
		List<String> b = new ArrayList<>();
		ja = (JSONArray) o.get("bonuses");
		if (ja != null) {
			for (i = 0; i < ja.size(); i++) {
				b.add(ja.get(i).toString());
			}
		}
		this.bonuses = b;
		this.infix_upgrade = new InfixUpgrade((JSONObject) o.get("infix_upgrade"));
		this.suffix = o.get("suffix").toString();

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
	public List<String> getFlags() {
		return flags;
	}

	private String listToString(List<String> l) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < flags.size(); i++) {
			sb.append(flags.get(i));
			if (i < (flags.size() - 1)) {
				sb.append(", ");
			}
		}
		return sb.toString();
	}

	/**
	 *
	 * @return
	 */
	public String getFlagsString() {
		return listToString(flags);
	}

	/**
	 *
	 * @return
	 */
	public List<String> getInfusion_upgrade_flags() {
		return infusion_upgrade_flags;
	}

	/**
	 *
	 * @return
	 */
	public String getInfusionFlagsString() {
		return listToString(infusion_upgrade_flags);
	}

	/**
	 *
	 * @return
	 */
	public List<String> getBonuses() {
		return bonuses;
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
	public String getSuffix() {
		return suffix;
	}

	@Override
	public String toString() {
		StringBuilder inf = new StringBuilder();
		StringBuilder fl = new StringBuilder();
		StringBuilder iuf = new StringBuilder();
		StringBuilder b = new StringBuilder();
		List<Attributes> a = this.infix_upgrade.getAttributes();
		if (a != null) {
			for (int j = 0; j < a.size(); j++) {
				inf.append("\n\t\t").append(a.get(j).toString());
			}
		} else {
			inf.append("none");
		}

		if (this.bonuses != null) {
			for (int j = 0; j < this.bonuses.size(); j++) {
				b.append(MessageFormat.format("\n\t\t({0}) {1}", j + 1, getBonuses().get(j).toString()).replaceAll("<[^>]*>", ""));
			}
		} else {
			b.append("none");
		}

		if (this.infusion_upgrade_flags != null) {
			for (int j = 0; j < this.infusion_upgrade_flags.size(); j++) {
				iuf.append("\n\t\t").append(this.infusion_upgrade_flags.get(j).toString());
			}
		} else {
			iuf.append("none");
		}

		if (this.flags != null) {
			for (int j = 0; j < this.flags.size(); j++) {
				fl.append("\n\t\t").append(this.flags.get(j).toString());
			}
		} else {
			fl.append("none");
		}
		return MessageFormat.format("UpgradeComponent\n\ttype: {0}\n\tflags: {1}\n\tinfusion_upgrade_flags: {2}\n\tbonuses: {3}\n\tinfix_upgrade: {4}\n\tsuffix: {5}", this.type, fl, iuf, b, infix_upgrade.toString(), this.suffix);
	}
}
