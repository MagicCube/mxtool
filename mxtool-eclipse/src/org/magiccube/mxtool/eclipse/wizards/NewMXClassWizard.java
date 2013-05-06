package org.magiccube.mxtool.eclipse.wizards;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.magiccube.mxtool.code.gen.MXClassGenOptions;
import org.magiccube.mxtool.code.gen.MXClassGenerator;
import org.magiccube.mxtool.eclipse.properties.MXProjectProperties;
import org.magiccube.mxtool.eclipse.utils.ResourceHelper;

public class NewMXClassWizard extends Wizard implements INewWizard
{
	private MXClassGenOptions _options = null;
	private NewMXClassWizardPage _basicPage = null;

	public NewMXClassWizard()
	{
		this(new MXClassGenOptions());
	}

	public NewMXClassWizard(MXClassGenOptions p_options)
	{
		super();
		_options = p_options;
		_basicPage = new NewMXClassWizardPage();
		setNeedsProgressMonitor(true);
	}

	public void init(IWorkbench workbench, IStructuredSelection p_selection)
	{
		_selection = p_selection;
		
		if (_selection == null || _selection.size() == 0)
		{
			_basicPage.setErrorMessage("Please select a project first.");
			return;
		}
		
		IResource resource = (IResource) _selection.getFirstElement();
		_project = resource.getProject();
		try
		{
			_projectProperties = MXProjectProperties.getProperties(_project);
		}
		catch (CoreException e)
		{

		}

		if (_projectProperties == null || !_projectProperties.isEnabled())
		{
			_basicPage.setErrorMessage("MXFramework of the current project haven't been enabled yet. You can enable it in the 'MXFramework' page of Project Properties.");
			return;
		}
		
		
		_options.scriptPath = _projectProperties.getScriptPath();
		
		
		IFolder scriptFolder = _project.getFolder(_projectProperties.getScriptPath());
		if (scriptFolder == null)
		{
			_basicPage.setErrorMessage("The script path '" + _projectProperties.getScriptPath() + "' is not currently available. You can modify it in the 'MXFramework' page of Project Properties.");
			return;
		}
		
		if (resource.getFullPath().toString().startsWith(scriptFolder.getFullPath().toString()))
		{
			IPath relPath = resource.getFullPath().makeRelativeTo(scriptFolder.getFullPath());
			String ns = relPath.toString().replaceAll("\\/", ".");
			_options.namespace = ns;
		}
		
		_basicPage.init(_options);
	}
	
	
	
	private IStructuredSelection _selection = null;
	public IStructuredSelection getSelection()
	{
		return _selection;
	}
	
	private IProject _project = null;
	protected IProject getProject()
	{
		return _project;
	}
	
	private MXProjectProperties _projectProperties = null;
	protected MXProjectProperties getProjectProperties()
	{
		return _projectProperties;
	}
	
	
	private MXClassGenerator _classGenerator = null;
	public MXClassGenerator getClassGenerator()
	{
		if (_classGenerator == null)
		{
			_classGenerator = new MXClassGenerator();
		}
		return _classGenerator;
	}
	

	public void addPages()
	{
		addPage(_basicPage);
	}

	public boolean performFinish()
	{
		if (!_basicPage.validate())
		{
			return false;
		}
		
		IRunnableWithProgress op = new IRunnableWithProgress()
		{
			public void run(IProgressMonitor monitor) throws InvocationTargetException
			{
				try
				{
					_doFinish(_options, monitor);
				}
				catch (CoreException e)
				{
					throw new InvocationTargetException(e);
				}
				finally
				{
					monitor.done();
				}
			}
		};
		try
		{
			getContainer().run(true, false, op);
		}
		catch (InterruptedException e)
		{
			return false;
		}
		catch (InvocationTargetException e)
		{
			Throwable realException = e.getTargetException();
			MessageDialog.openError(getShell(), "Error", realException.getMessage());
			return false;
		}
		return true;
	}

	
	
	
	
	
	private void _doFinish(MXClassGenOptions p_options, IProgressMonitor p_monitor) throws CoreException
	{
		// create a sample file
		p_monitor.beginTask("Creating " + p_options.getFullClassName(), 2);
		final IFile file = getProject().getFile(p_options.getAbsolutePath());
		if (file.exists())
		{
			_throwCoreException(file.getFullPath() + " already exists.");
		}
		
		IFolder folder = (IFolder)file.getParent();
		ResourceHelper.prepareFolder(folder, p_monitor);
		
		try
		{
			InputStream stream = _openContentStream();
			file.create(stream, true, p_monitor);
			stream.close();
		}
		catch (IOException e)
		{
		}
		
		
		
		p_monitor.worked(1);
		p_monitor.setTaskName("Opening file for editing...");
		getShell().getDisplay().asyncExec(new Runnable()
		{
			public void run()
			{
				IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				try
				{
					IDE.openEditor(page, file, true);
				}
				catch (PartInitException e)
				{
				}
			}
		});
		p_monitor.worked(1);
	}

	private InputStream _openContentStream()
	{
		String contents = getClassGenerator().generateCode(_options).toString();
		return new ByteArrayInputStream(contents.getBytes());
	}

	private void _throwCoreException(String message) throws CoreException
	{
		IStatus status = new Status(IStatus.ERROR, "org.magiccube.mxtool.eclipse", IStatus.OK, message, null);
		throw new CoreException(status);
	}
}