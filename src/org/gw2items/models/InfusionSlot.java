/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gw2items.models;

import java.text.MessageFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author Michael
 */
public class InfusionSlot {

	String flags;
	String item;

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
	public String getItem() {
		return item;
	}

	/**
	 *
	 * @param item
	 */
	public void setItem(String item) {
		this.item = item;
	}

	/**
	 *
	 * @param flags
	 * @param item
	 */
	public InfusionSlot(String flags, String item) {
		this.flags = flags;
		this.item = item;
	}

	/**
	 *
	 * @param a
	 */
	public InfusionSlot(JSONArray a) {
		StringBuilder sb = new StringBuilder();
		String re = ".*?((?:[a-z][a-z]+))";

		Pattern p = Pattern.compile(re, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

		for (int i = 0; i < a.size(); i++) {
			JSONObject o = (JSONObject) a.get(i);
			String txt = o.get("flags").toString();
			Matcher m = p.matcher(txt);
			if (m.find()) {
				sb.append(m.group(1)).append(',');
			}
		}
		int l = sb.length();
		if (l == 0) {
			this.flags = "none";
		} else {
			this.flags = sb.substring(0, l - 1).toString();
		}
	}

	@Override
	public String toString() {
		return MessageFormat.format("{0}", flags);
	}
}
