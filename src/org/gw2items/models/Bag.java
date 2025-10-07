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
public class Bag {
	String no_sell_or_sort;
	int size;
	
	/**
	 *
	 * @param obj
	 */
	public Bag(Object obj) {
		JSONObject o = (JSONObject)obj;
		this.no_sell_or_sort = o.get("no_sell_or_sort").toString();
		this.size = Integer.parseInt(o.get("size").toString());
	}

	/**
	 *
	 * @return
	 */
	public String getNo_sell_or_sort() {
		return no_sell_or_sort;
	}

	/**
	 *
	 * @return
	 */
	public int getSize() {
		return size;
	}

	@Override
	public String toString() {
		return MessageFormat.format("Bag\n\tno_sell_or_sort: {0}\n\tsize: {1}", no_sell_or_sort, size);
	}
}
