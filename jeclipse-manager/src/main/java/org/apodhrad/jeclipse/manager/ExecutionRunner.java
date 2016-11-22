package org.apodhrad.jeclipse.manager;

public interface ExecutionRunner {

	public void setExecutionOutput(Appendable appendableOutput);
	
	public void setTimeout(int timeout);
	
	public void execute(String... args);
}
