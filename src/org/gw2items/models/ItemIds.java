/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gw2items.models;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Michael
 */
public class ItemIds {

	private List<Integer> added;
	private List<Integer> removed;
	private List<Integer> remote;
	private List<Integer> local;

	/**
	 *
	 */
	public ItemIds() {
		this(null, null, null, null);
	}

	/**
	 *
	 * @param added List of item ids to be added.
	 * @param removed List of item ids to be removed.
	 * @param remote List of all remote item ids.
	 * @param local List of all local ids.
	 */
	public ItemIds(final List<Integer> added, final List<Integer> removed, final List<Integer> remote, final List<Integer> local) {
		this.added = new ArrayList<>(added); //.addAll(added);
		this.removed = new ArrayList<>(removed);
		this.remote = new ArrayList<>(remote);
		this.local = new ArrayList<>(local);
	}

	/**
	 *
	 * @return
	 */
	public Integer localCount() {
		return local.size();
	}

	/**
	 *
	 * @return
	 */
	public Integer remoteCount() {
		return remote.size();
	}

	/**
	 *
	 * @return
	 */
	public Integer addedCount() {
		return added.size();
	}

	/**
	 *
	 * @return
	 */
	public Integer removedCount() {
		return removed.size();
	}

	/**
	 *
	 * @return
	 */
	public List<Integer> getAdded() {
		return added;
	}

	/**
	 *
	 * @param added
	 */
	public void setAdded(ArrayList<Integer> added) {
		this.added = added;
	}

	/**
	 *
	 * @return
	 */
	public List<Integer> getRemoved() {
		return removed;
	}

	/**
	 *
	 * @param removed
	 */
	public void setRemoved(ArrayList<Integer> removed) {
		this.removed = removed;
	}

	/**
	 *
	 * @return
	 */
	public List<Integer> getRemote() {
		return remote;
	}

	/**
	 *
	 * @param remote
	 */
	public void setRemote(ArrayList<Integer> remote) {
		this.remote = remote;
	}

	/**
	 *
	 * @return
	 */
	public List<Integer> getLocal() {
		return local;
	}

	/**
	 *
	 * @param local
	 */
	public void setLocal(ArrayList<Integer> local) {
		this.local = local;
	}
}
