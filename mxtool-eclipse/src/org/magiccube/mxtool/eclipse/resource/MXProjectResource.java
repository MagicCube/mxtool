package org.magiccube.mxtool.eclipse.resource;

import java.util.ArrayList;
import java.util.List;

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
		if (_scriptFolder == null && getScriptPath() != null)
		{
			_scriptFolder = _project.getFolder(getScriptPath());
		}
		return _scriptFolder;
	}

	public String[] getNamespaces() throws CoreException
	{
		List<String> result = new ArrayList<String>();
		IFolder rootFolder = getScriptFolder();
		List<IFolder> subfolders = _getSubfolders(rootFolder, true);
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
	
	public String[] getModuleNames() throws CoreException 
	{
		List<String> result = new ArrayList<String>();
		IFolder rootFolder = getScriptFolder();
		List<IFolder> subfolders = _getSubfolders(rootFolder, false);
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
			if (className.equals("mx.MXObject") || className.equals("mx.MXComponent") || className.equals("mx.MXEvent"))
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
			try
			{
				if (member instanceof IFile && member.getFileExtension().equals("js"))
				{
					IFile file = (IFile) member;
					if (file.getName().matches("^[A-Z][a-zA-Z0-9]+\\.js$"))
					{
						result.add(file);
					}
				}
				else if (member instanceof IFolder)
				{
					if (!member.getName().matches("[a-z0-9]+"))
					{
						continue;
					}
					List<IFile> files = _getJavaScripts((IFolder) member);
					result.addAll(files);
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return result;
	}

	private List<IFolder> _getSubfolders(IFolder p_parentFolder, boolean p_recursive) throws CoreException
	{
		List<IFolder> result = new ArrayList<IFolder>();
		IResource[] members = p_parentFolder.members();
		for (IResource member : members)
		{
			if (member instanceof IFolder)
			{
				try
				{
					if (member.getName().equals("res"))
					{
						continue;
					}
					if (!member.getName().matches("[a-z0-9]+"))
					{
						continue;
					}
					IFolder folder = (IFolder) member;
					result.add(folder);
					if (p_recursive)
					{
						result.addAll(_getSubfolders(folder, true));
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		return result;
	}

	public IFile getFileOfClass(String p_className, String p_extension)
	{
		if (p_className.equals("MXObject"))
		{
			p_className = "mx.MXObject";
		}
		else if (p_className.equals("MXComponent"))
		{
			p_className = "mx.MXComponent";
		}
		return getScriptFolder().getFile(p_className.replaceAll("\\.", "\\/") + "." + p_extension);
	}

	public IFile getFileOfClass(String p_className)
	{
		return getFileOfClass(p_className, "js");
	}

	public IFolder getFolderOfNamespace(String p_namespace)
	{
		return getScriptFolder().getFolder(p_namespace.replaceAll("\\.", "\\/"));
	}

	public String getClassNameOfFile(IFile p_file)
	{
		return getNamespaceOfFile(p_file) + "." + p_file.getName().substring(0, p_file.getName().length() - 3);
	}

	public String getNamespaceOfFile(IFile p_file)
	{
		return p_file.getParent().getProjectRelativePath().makeRelativeTo(getScriptPath()).toString().replaceAll("\\/", ".");
	}

	public boolean hasClass(String p_className)
	{
		if (p_className.equals("MXObject") || p_className.equals("MXComponent"))
		{
			return true;
		}
		IFile file = getFileOfClass(p_className);
		return file.exists();
	}

	public boolean hasNamespace(String p_namespace)
	{
		IFolder folder = getFolderOfNamespace(p_namespace);
		return folder.exists();
	}

	public boolean hasResource(String res)
	{
		int pos = res.lastIndexOf(".");
		if (pos > 0)
		{
			String resName = res.substring(0, pos);
			String extension = res.substring(pos + 1);
			IFile file = getFileOfClass(resName, extension);
			return file.exists();
		}
		return true;
	}
}
