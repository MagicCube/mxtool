package org.magiccube.mxbuild;

import java.util.ArrayList;
import java.util.List;

public class MXBuildResult
{
	public boolean success = false;
	public List<MXBuildError> errors = new ArrayList<MXBuildError>();
	
	public MXBuildResult(boolean p_success)
	{
		success = p_success;
	}
	
	public MXBuildResult merge(MXBuildResult p_result)
	{
		if (p_result.success == false)
		{
			this.success = false;
			errors.addAll(p_result.errors);
		}
		return this;
	}
}
