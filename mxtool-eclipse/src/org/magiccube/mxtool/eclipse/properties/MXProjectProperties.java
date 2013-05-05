package org.magiccube.mxtool.eclipse.properties;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;
import org.magiccube.mxtool.eclipse.Activator;

public class MXProjectProperties
{
	private IProject _project = null;
	
	public MXProjectProperties()
	{
		
	}
	
	private MXProjectProperties(IProject p_project) throws CoreException
	{
		_project = p_project;
	}
	
	public static MXProjectProperties getProperties(IProject p_project) throws CoreException
	{
		MXProjectProperties properties = new MXProjectProperties(p_project);
		return properties;
	}
	
	
	
	
	public boolean isEnabled()
	{
		Object enabled;
		try
		{
			enabled = _project.getPersistentProperty(new QualifiedName("", Activator.PLUGIN_ID + ".project.enabled"));
		}
		catch (CoreException e)
		{
			return false;
		}
		
		if (enabled != null)
		{
			return enabled.equals("true");
		}
		else
		{
			return false;
		}
	}
	public void setEnabled(boolean p_enabled)
	{
		if (!p_enabled)
		{
			setScriptPath(null);
		}
		try
		{
			_project.setPersistentProperty(new QualifiedName("", Activator.PLUGIN_ID + ".project.enabled"), p_enabled ? "true" : "false");
		}
		catch (CoreException e)
		{
			e.printStackTrace();
		}
	}
	
	public String getScriptPath()
	{
		String scriptPath;
		try
		{
			scriptPath = _project.getPersistentProperty(new QualifiedName("", Activator.PLUGIN_ID + ".project.scriptPath"));
			return scriptPath;
		}
		catch (CoreException e)
		{
			return null;
		}		
	}
	public void setScriptPath(String p_path)
	{
		try
		{
			_project.setPersistentProperty(new QualifiedName("", Activator.PLUGIN_ID + ".project.scriptPath"), p_path);
		}
		catch (CoreException e)
		{
			e.printStackTrace();
		}
	}
}
