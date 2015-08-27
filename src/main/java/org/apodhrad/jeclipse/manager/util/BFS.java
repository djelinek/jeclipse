package org.apodhrad.jeclipse.manager.util;

import java.util.ArrayList;
import java.util.List;

import org.hamcrest.Matcher;

/**
 * Breadth First Search
 * 
 * @author apodhrad
 * 
 * @param T
 *            object to find
 */
public abstract class BFS<T> {

	public List<T> find(T parent, Matcher<?> matcher) {
		List<T> list = new ArrayList<T>();
		List<T> queue = new ArrayList<T>();
		// Initial push
		queue.add(parent);
		// Depth first search
		while (!queue.isEmpty()) {
			// Pop figure
			T child = queue.get(0);
			// If null then continue
			if (child == null) {
				continue;
			}
			// Does it matches?
			if (matcher.matches(child)) {
				list.add(child);
			}
			// Push another children
			for (T t : getChildren(child)) {
				queue.add(t);
			}
		}
		return list;
	}

	public abstract T[] getChildren(T child);
}
