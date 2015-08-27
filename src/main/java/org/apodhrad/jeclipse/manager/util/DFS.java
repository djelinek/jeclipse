package org.apodhrad.jeclipse.manager.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.hamcrest.Matcher;

/**
 * Depth First Search
 * 
 * @author apodhrad
 * 
 * @param T
 *            object to find
 */
public abstract class DFS<T> {

	public List<T> find(T parent, Matcher<?> matcher) {
		List<T> list = new ArrayList<T>();
		Stack<T> stack = new Stack<T>();
		// Initial push
		stack.push(parent);
		// Depth first search
		while (!stack.isEmpty()) {
			// Pop figure
			T child = stack.pop();
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
				stack.push(t);
			}
		}
		return list;
	}

	public abstract T[] getChildren(T child);
}
