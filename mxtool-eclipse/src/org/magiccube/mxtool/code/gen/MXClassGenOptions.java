package org.magiccube.mxtool.code.gen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	public boolean genCss = false;
	public boolean genHtml = false;
	public List<String> importedClasses = new ArrayList<String>();
	public Map<String, Object> properties = new HashMap<String, Object>(); 
	
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
			className = "App";
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
	
	
	public Path getJavaScriptPath()
	{
		String path = scriptPath + "/" + namespace.replaceAll("\\.", "\\/") + "/";
		path += className + ".js";
		return new Path(path);
	}
	
	public Path getCssPath()
	{
		String ns = null;
		int pos = namespace.indexOf(".");
		if (pos == -1)
		{
			ns = namespace + ".res.";
		}
		else
		{
			ns = namespace.substring(0, pos) + ".res.";
		}
		String path = scriptPath + "/" + ns.replaceAll("\\.", "\\/") + className + ".css";
		return new Path(path);
	}

	public Path getDebugHtmlPath()
	{
		int pos = scriptPath.lastIndexOf("/");
		String path = scriptPath.substring(0, pos);
		path += "/" + className;
		path += "-debug.html";
		return new Path(path);
	}
}
