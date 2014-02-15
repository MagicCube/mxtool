package org.magiccube.mxtool.eclipse.builders;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.magiccube.common.io.TextStreamReader;
import org.magiccube.mxbuild.MXBuildError;
import org.magiccube.mxbuild.MXBuildResult;
import org.magiccube.mxbuild.MXModuleBuild;
import org.magiccube.mxtool.eclipse.resource.MXProjectResource;
import org.magiccube.mxtool.eclipse.validators.MXScriptValidator;
import org.magiccube.mxtool.eclipse.validators.ValidationResult;

@SuppressWarnings("rawtypes")
public class MXBuilder extends IncrementalProjectBuilder
{
	public static final String BUILDER_ID = "org.magiccube.mxtool.eclipse.builders.mxBuilder";
	private static final String MARKER_TYPE = "org.magiccube.mxtool.eclipse.builders.mxBuilderProblem";

	private MXProjectResource _projectResource = null;
	private boolean _fullBuild = false;

	public MXBuilder()
	{
		
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.internal.events.InternalBuilder#build(int,
	 * java.util.Map, org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected IProject[] build(int kind, Map args, IProgressMonitor monitor) throws CoreException
	{
		if (_projectResource == null)
		{
			_projectResource = new MXProjectResource(getProject());
		}
		if (_projectResource.getProjectProperties() == null || !_projectResource.getProjectProperties().isEnabled())
		{
			return null;
		}
		
		
		if (kind == FULL_BUILD)
		{
			fullBuild(monitor);
		}
		else
		{
			IResourceDelta delta = getDelta(getProject());
			if (delta == null)
			{
				fullBuild(monitor);
			}
			else
			{
				incrementalBuild(delta, monitor);
			}
		}
		return null;
	}

	protected void fullBuild(final IProgressMonitor monitor) throws CoreException
	{
		if (_projectResource == null)
		{
			_projectResource = new MXProjectResource(getProject());
		}
		if (_projectResource.getProjectProperties() == null || !_projectResource.getProjectProperties().isEnabled())
		{
			return;
		}
		
		try
		{
			_fullBuild = true;
			getProject().accept(new MXRecursiveVisitor(monitor));
		}
		catch (Exception e)
		{
		}
		_fullBuild = false;
		
		String[] modules = _projectResource.getModuleNames();
		for (String module : modules)
		{
			_buildMXModuleScript(module, monitor);
			_buildMXModuleCss(module, monitor);
		}
	}

	protected void incrementalBuild(IResourceDelta delta, IProgressMonitor monitor) throws CoreException
	{
		if (_projectResource == null)
		{
			_projectResource = new MXProjectResource(getProject());
		}
		if (_projectResource.getProjectProperties() == null || !_projectResource.getProjectProperties().isEnabled())
		{
			return;
		}
		
		delta.accept(new MXDeltaVisitor(monitor));
	}
	
	
	
	
	


	private void _validateAndBuildMXScriptFile(IFile file, IProgressMonitor monitor) throws CoreException
	{
		if (_validateMXScriptFile(file) && !_fullBuild)
		{
			String moduleName = _getModuleNameFromResource(file);
			
			_buildMXModuleScript(moduleName, monitor);
		}
	}

	private String _getModuleNameFromResource(IResource p_resource)
	{
		String moduleName;
		String path = p_resource.getProjectRelativePath().makeRelativeTo(_projectResource.getScriptPath()).toString();
		moduleName = path.substring(0, path.indexOf('/'));
		return moduleName;
	}
	
	
	
	
	
	private void _validateAndBuildMXCssFile(IFile file, IProgressMonitor monitor) throws CoreException
	{
		if (!_fullBuild)
		{
			String moduleName = _getModuleNameFromResource(file);
			
			_buildMXModuleCss(moduleName, monitor);
		}
	}
	
	private void _buildMXModuleCss(String p_moduleName, IProgressMonitor monitor) throws CoreException
	{
		if (_projectResource.getProjectProperties().isValidationOnly()) return;
		
		IFolder moduleFolder = _projectResource.getFolderOfNamespace(p_moduleName);
		String modulePath = moduleFolder.getLocation().toString();
		MXModuleBuild build = null;
		try
		{
			build = new MXModuleBuild(modulePath);
			MXBuildResult result = build.compileModuleCss();
			if (result.success)
			{
				moduleFolder.getFile("res/min.css").refreshLocal(IResource.DEPTH_ONE, monitor);
			}
			else
			{
				for (MXBuildError error : result.errors)
				{
					IFile errorFile = moduleFolder.getFile(error.path); 
					_addMarker(errorFile, error.description, error.lineNumber, IMarker.SEVERITY_ERROR);
				}
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private void _buildMXModuleScript(String p_moduleName, IProgressMonitor monitor) throws CoreException
	{
		if (_projectResource.getProjectProperties().isValidationOnly()) return;
		
		IFolder moduleFolder = _projectResource.getFolderOfNamespace(p_moduleName);
		String modulePath = moduleFolder.getLocation().toString();
		MXModuleBuild build = null;
		try
		{
			build = new MXModuleBuild(modulePath);
			MXBuildResult result = build.compileModuleJavaScript();
			if (result.success)
			{
				moduleFolder.getFile("min.js").refreshLocal(IResource.DEPTH_ONE, monitor);
			}
			else
			{
				for (MXBuildError error : result.errors)
				{
					IFile errorFile = moduleFolder.getFile(error.path); 
					_addMarker(errorFile, error.description, error.lineNumber, IMarker.SEVERITY_ERROR);
				}
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private boolean _validateMXScriptFile(IFile file)
	{
		String className = _projectResource.getClassNameOfFile(file);
		if (!className.matches("^[a-z][a-zA-Z0-9\\.-]+$"))
		{
			return true;
		}
		
		boolean result = true;
		MXScriptValidator validator = new MXScriptValidator(_projectResource);
		InputStream stream;
		try
		{
			stream = file.getContents();
			TextStreamReader reader = new TextStreamReader(stream);
			String line = null;
			String[] lines = reader.readLines();
			for (int i = 0; i < lines.length; i++)
			{
				line = lines[i];
				if (line.trim().equals(""))
				{
					continue;
				}
				ValidationResult validationResult = validator.validate(file, line);
				if (!validationResult.isValidated())
				{
					result = false;
					_addMarker(file, validationResult.getMessage(), i + 1, IMarker.SEVERITY_ERROR);
				}
			}
			reader.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			result = false;
		}
		return result;
	}
	

	private void _addMarker(IFile p_file, String p_message, int p_lineNumber, int p_severity)
	{
		try
		{
			IMarker marker = p_file.createMarker(MARKER_TYPE);
			marker.setAttribute(IMarker.MESSAGE, p_message);
			marker.setAttribute(IMarker.SEVERITY, p_severity);
			if (p_lineNumber == -1)
			{
				p_lineNumber = 1;
			}
			marker.setAttribute(IMarker.LINE_NUMBER, p_lineNumber);
		}
		catch (CoreException e)
		{
		}
	}

	private void _deleteMarkers(IResource file)
	{
		try
		{
			file.deleteMarkers(MARKER_TYPE, false, IResource.DEPTH_ZERO);
		}
		catch (CoreException ce)
		{
		}
	}
	
	private boolean _needMXBuild(IResource resource)
	{
		if (_projectResource == null)
		{
			_projectResource = new MXProjectResource(getProject());
		}
		if (_projectResource.getProjectProperties() == null || !_projectResource.getProjectProperties().isEnabled())
		{
			return false;
		}
		if (!resource.getProjectRelativePath().toString().startsWith(_projectResource.getScriptPath().toString()))
		{
			return false;
		}
		
		if (resource.getProjectRelativePath().toString().startsWith(_projectResource.getScriptPath().toString() + "/lib"))
		{
			return false;
		}
		
		return true;
	}
	
	
	
	
	class MXDeltaVisitor implements IResourceDeltaVisitor
	{
		private IProgressMonitor _monitor;
		
		public MXDeltaVisitor(IProgressMonitor monitor)
		{
			_monitor = monitor;
		}
		
		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.core.resources.IResourceDeltaVisitor#visit(org.eclipse
		 * .core.resources.IResourceDelta)
		 */
		public boolean visit(IResourceDelta delta) throws CoreException
		{
			IResource resource = delta.getResource();
			if (!_needMXBuild(resource)) return true;
			
			
			// Start MXBuild
			_deleteMarkers(resource);
			switch (delta.getKind())
			{
				case IResourceDelta.ADDED:
				case IResourceDelta.CHANGED:
					if (resource instanceof IFile)
					{
						if (resource.getName().endsWith(".js") && !resource.getName().equals("min.js"))
						{
							_validateAndBuildMXScriptFile((IFile)resource, _monitor);
						}
						else if (resource.getName().endsWith(".css") && !resource.getName().equals("min.css"))
						{
							_validateAndBuildMXCssFile((IFile)resource, _monitor);
						}
					}
					break;
				case IResourceDelta.REMOVED:
					if (resource instanceof IFile)
					{
						if (resource.getName().equals("min.js") || resource.getName().equals("min.js"))
						{
							return true;
						}
						if (resource.getName().equals("min.css") || resource.getName().equals("min.css"))
						{
							return true;
						}
					}
					
					try
					{
						String moduleName = _getModuleNameFromResource(resource);
						_buildMXModuleScript(moduleName, _monitor);
						_buildMXModuleCss(moduleName, _monitor);
					}
					catch (Exception e)
					{
						
					}
					break;
			}
			// return true to continue visiting children.
			return true;
		}
	}

	class MXRecursiveVisitor implements IResourceVisitor
	{
		private IProgressMonitor _monitor;
		
		public MXRecursiveVisitor(IProgressMonitor monitor)
		{
			_monitor = monitor;
		}
		
		public boolean visit(IResource resource)
		{
			if (!_needMXBuild(resource)) return true;
			
			if (resource instanceof IFile)
			{
				if (resource.getName().endsWith(".js") && !resource.getName().equals("min.js"))
				{
					try
					{
						_validateAndBuildMXScriptFile((IFile)resource, _monitor);
					}
					catch (CoreException e)
					{
						e.printStackTrace();
					}
				}
				else if (resource.getName().endsWith(".css") && !resource.getName().equals("min.css"))
				{
					try
					{
						_validateAndBuildMXCssFile((IFile)resource, _monitor);
					}
					catch (CoreException e)
					{
						e.printStackTrace();
					}
				}
			}
			return true;
		}
	}
}
