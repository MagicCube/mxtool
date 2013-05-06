package org.magiccube.mxtool.code.gen;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Path;

public class MXClassGenOptions
{
	public String scriptPath = null;
	public String namespace = null;
	public String className = null;
	public String classNamePostfix = null;
	public boolean isSingleton = false;
	public String superClass = null;
	public String superClassType = "MXObject";
	public List<String> importedClasses = new ArrayList<String>();
	
	public MXClassGenOptions()
	{
		this("MXObject");
	}
	
	public MXClassGenOptions(String p_superClassType)
	{
		superClassType = p_superClassType;
		if (superClassType.equals("MXObject"))
		{
			className = "NewClass";
			superClass = "MXObject";
		}
		else if (superClassType.equals("MXComponent"))
		{
			className = "NewComponent";
			superClass = "MXComponent";
		}
		else if (superClassType.equals("MXView"))
		{
			className = "NewView";
			superClass = "mx.view.View";
			classNamePostfix = "View";
		}
		else if (superClassType.equals("MXScene"))
		{
			className = "NewScene";
			superClass = "mx.scn.Scene";
			classNamePostfix = "Scene";
		}
		else if (superClassType.equals("MXApp"))
		{
			className = "NewApp";
			superClass = "mx.app.Application";
			classNamePostfix = "App";
		}
		
		importedClasses.add("MXObject");
		importedClasses.add("MXComponent");
		importedClasses.add("mx.view.View");
		importedClasses.add("mx.scn.Scene");
		importedClasses.add("mx.app.Application");
	}
	
	
	
	public String getFullClassName()
	{
		return namespace + "." + className;
	}
	
	
	public Path getAbsolutePath()
	{
		String path = scriptPath + "/" + namespace.replaceAll("\\.", "\\/") + "/";
		path += className + ".js";
		return new Path(path);
	}
	
}
