package org.magiccube.mxbuild;

public class MXBuildError
{
	public String path = null;
	public int lineNumber = 0;
	public String description = null;
	
	public MXBuildError(String p_path, int p_lineNumber, String p_description)
	{
		path = p_path;
		lineNumber = p_lineNumber;
		description = p_description;
	}
}
