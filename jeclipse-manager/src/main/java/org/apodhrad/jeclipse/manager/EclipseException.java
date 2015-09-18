package org.apodhrad.jeclipse.manager;

/**
 * 
 * @author apodhrad
 *
 */
public class EclipseException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public EclipseException(String message) {
		super(message);
	}

	public EclipseException(String message, Throwable cause) {
		super(message, cause);
	}

}
