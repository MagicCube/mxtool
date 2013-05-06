package org.magiccube.mxtool.eclipse.resource;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.magiccube.mxtool.eclipse.properties.MXProjectProperties;

public class MXProjectResource
{
	private IProject _project = null;
	
	public MXProjectResource(IProject p_project)
	{
		_project = p_project;
		try
		{
			_properties = MXProjectProperties.getProperties(p_project);
		}
		catch (CoreException e)
		{
			
		}
	}
	
	private MXProjectProperties _properties = null;
	public MXProjectProperties getProjectProperties()
	{
		return _properties;
	}
	
	private Path _scriptPath = null;
	public Path getScriptPath()
	{
		if (_scriptPath == null && _properties.getScriptPath() != null)
		{
			_scriptPath = new Path(_properties.getScriptPath());
		}
		return _scriptPath;
	}
	
	private IFolder _scriptFolder = null;
	public IFolder getScriptFolder()
	{
		if (_scriptFolder == null)
		{
			_scriptFolder = _project.getFolder(getScriptPath());
		}
		return _scriptFolder;
	}
	
	public String[] getNamespaces() throws CoreException
	{
		List<String> result = new ArrayList<String>();
		IFolder rootFolder = getScriptFolder();
		List<IFolder> subfolders = _getSubfolders(rootFolder);
		for (IFolder subfolder : subfolders)
		{
			IPath path = subfolder.getProjectRelativePath().makeRelativeTo(getScriptPath());
			String ns = path.toString().replaceAll("\\/", ".");
			if (ns.equals("lib") || ns.startsWith("lib"))
			{
				continue;
			}
			result.add(ns);
		}
		
		String[] array = new String[result.size()];
		array = result.toArray(array);
		return array;
	}
	
	public String[] getClassNames() throws CoreException
	{
		List<String> result = new ArrayList<String>();
		result.add("MXObject");
		result.add("MXComponent");
		
		IFolder rootFolder = getScriptFolder();
		List<IFile> jsFiles = _getJavaScripts(rootFolder);
		for (IFile jsFile : jsFiles)
		{
			IPath path = jsFile.getProjectRelativePath().makeRelativeTo(getScriptPath());
			String className = path.toString().replaceAll("\\/", ".");
			className = className.substring(0, className.length() - 3);
			if (className.startsWith("lib"))
			{
				continue;
			}
			if (className.equals("mx.MXObject") || className.equals("mx.MXComponent"))
			{
				continue;
			}
			result.add(className);
		}
		
		String[] array = new String[result.size()];
		array = result.toArray(array);
		return array;
	}

	private List<IFile> _getJavaScripts(IFolder p_parentFolder) throws CoreException
	{
		List<IFile> result = new ArrayList<IFile>();
		IResource[] members = p_parentFolder.members();
		for (IResource member : members)
		{
			if (member instanceof IFile && member.getFileExtension().equals("js"))
			{
				IFile file = (IFile)member;
				String name = file.getName();
				System.out.println(name);
				if (file.getName().matches("^[A-Z][a-zA-Z0-9]+\\.js$"))
				{
					result.add(file);
				}
			}
			else if (member instanceof IFolder)
			{
				List<IFile> files = _getJavaScripts((IFolder)member);
				result.addAll(files);
			}
		}
		return result;
	}

	private List<IFolder> _getSubfolders(IFolder p_parentFolder) throws CoreException
	{
		List<IFolder> result = new ArrayList<IFolder>();
		IResource[] members = p_parentFolder.members();
		for (IResource member : members)
		{
			if (member instanceof IFolder)
			{
				IFolder folder = (IFolder)member;
				result.add(folder);
				result.addAll(_getSubfolders(folder));
			}
		}
		return result;
	}
}
