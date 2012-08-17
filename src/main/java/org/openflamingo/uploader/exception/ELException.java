package org.openflamingo.uploader.exception;

public class ELException extends SystemException {

	private static final long serialVersionUID = 1;

	private Throwable rootCause;

	public ELException() {
		super();
	}

	public ELException(String pMessage) {
		super(pMessage);
	}

	public ELException(Throwable pRootCause) {
		super(pRootCause.getLocalizedMessage());
		this.rootCause = pRootCause;
	}

	public ELException(String pMessage, Throwable pRootCause) {
		super(pMessage);
		this.rootCause = pRootCause;
	}

	public Throwable getRootCause() {
		return this.rootCause;
	}
}