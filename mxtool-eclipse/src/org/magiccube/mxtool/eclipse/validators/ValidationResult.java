package org.magiccube.mxtool.eclipse.validators;

public class ValidationResult
{
	public ValidationResult(boolean p_validated, String p_message)
	{
		_validated = p_validated;
		_message = p_message;
	}

	public ValidationResult(boolean p_validated)
	{
		this(p_validated, null);
	}

	private boolean _validated = false;
	public boolean isValidated()
	{
		return _validated;
	}
	
	private String _message = null;
	public String getMessage()
	{
		return _message;
	}
}
