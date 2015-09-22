package org.apodhrad.jeclipse.manager;

/**
 * 
 * @author apodhrad
 *
 */
public class EclipseLogMessage {

	private int severity;
	private String source;
	private String message;

	public EclipseLogMessage(int severity, String source, String message) {
		this.severity = severity;
		this.source = source;
		this.message = message;
	}

	public int getSeverity() {
		return severity;
	}

	public String getSource() {
		return source;
	}

	public String getMessage() {
		return message;
	}

	@Override
	public String toString() {
		return severity + " - [" + source + "] " + message;  
	}

}
