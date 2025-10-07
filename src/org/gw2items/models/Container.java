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
public class Container {
	String type;
	
	/**
	 *
	 * @param obj
	 */
	public Container(Object obj) {
		JSONObject o = (JSONObject) obj;
		this.type = o.get("type").toString();
	}

	/**
	 *
	 * @return
	 */
	public String getType() {
		return type;
	}
	
	@Override
	public String toString() {
		return MessageFormat.format("Container\n\ttype: {0}", this.type);
	}
}
