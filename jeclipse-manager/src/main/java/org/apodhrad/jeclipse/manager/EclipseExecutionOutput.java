package org.apodhrad.jeclipse.manager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EclipseExecutionOutput implements Appendable {

	private Logger log = LoggerFactory.getLogger(getClass());
	private List<String> lines;

	public EclipseExecutionOutput() {
		lines = new ArrayList<String>();
	}

	public List<String> getLines() {
		return lines;
	}

	@Override
	public Appendable append(CharSequence line) throws IOException {
		log.info(line.toString());
		lines.add(line.toString());
		return null;
	}

	@Override
	public Appendable append(CharSequence line, int start, int end) throws IOException {
		return append(line);
	}

	@Override
	public Appendable append(char c) throws IOException {
		return append(String.valueOf(c));
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		for (String line: getLines()) {
			result.append(line).append("\n");
		}
		return result.toString();
	}

}
