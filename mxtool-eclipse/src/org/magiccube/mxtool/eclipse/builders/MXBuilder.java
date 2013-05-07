package org.magiccube.mxtool.eclipse.builders;

import java.io.InputStream;
import java.util.Map;

import org.eclipse.core.resources.IFile;
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
import org.magiccube.mxtool.eclipse.resource.MXProjectResource;
import org.magiccube.mxtool.validators.MXScriptLineValidator;
import org.magiccube.mxtool.validators.ValidationResult;

@SuppressWarnings("rawtypes")
public class MXBuilder extends IncrementalProjectBuilder
{
	public static final String BUILDER_ID = "org.magiccube.mxtool.eclipse.builders.mxBuilder";
	private static final String MARKER_TYPE = "org.magiccube.mxtool.eclipse.builders.mxBuilderProblem";

	private MXProjectResource _projectResource = null;

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
		try
		{
			getProject().accept(new MXResourceVisitor());
		}
		catch (CoreException e)
		{
		}
	}

	protected void incrementalBuild(IResourceDelta delta, IProgressMonitor monitor) throws CoreException
	{
		delta.accept(new MXDeltaVisitor());
	}
	
	
	
	
	


	private void _checkJavaScript(IResource resource)
	{
		if (resource instanceof IFile && resource.getName().endsWith(".js"))
		{
			IFile file = (IFile) resource;
			_deleteMarkers(file);
			
			if (_projectResource == null)
			{
				_projectResource = new MXProjectResource(getProject());
				if (_projectResource.getProjectProperties() == null || !_projectResource.getProjectProperties().isEnabled())
				{
					return;
				}
			}
			
			
			if (!file.getProjectRelativePath().toString().startsWith(_projectResource.getScriptPath().toString()))
			{
				return;
			}

			MXScriptLineValidator validator = new MXScriptLineValidator(_projectResource);
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
					ValidationResult result = validator.validate(file, line);
					if (!result.isValidated())
					{
						_addMarker(file, result.getMessage(), i + 1, IMarker.SEVERITY_ERROR);
					}
				}
				reader.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
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

	private void _deleteMarkers(IFile file)
	{
		try
		{
			file.deleteMarkers(MARKER_TYPE, false, IResource.DEPTH_ZERO);
		}
		catch (CoreException ce)
		{
		}
	}
	
	
	
	
	
	
	class MXDeltaVisitor implements IResourceDeltaVisitor
	{
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
			switch (delta.getKind())
			{
				case IResourceDelta.ADDED:
					// handle added resource
					_checkJavaScript(resource);
					break;
				case IResourceDelta.REMOVED:
					// handle removed resource
					break;
				case IResourceDelta.CHANGED:
					// handle changed resource
					_checkJavaScript(resource);
					break;
			}
			// return true to continue visiting children.
			return true;
		}
	}

	class MXResourceVisitor implements IResourceVisitor
	{
		public boolean visit(IResource resource)
		{
			_checkJavaScript(resource);
			// return true to continue visiting children.
			return true;
		}
	}
}
