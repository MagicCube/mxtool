package org.magiccube.mxtool.eclipse.wizards;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
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
import org.magiccube.mxtool.code.gen.CssGenerator;
import org.magiccube.mxtool.code.gen.MXClassGenOptions;
import org.magiccube.mxtool.code.gen.MXClassGenerator;
import org.magiccube.mxtool.code.gen.MXHtmlGenerator;
import org.magiccube.mxtool.eclipse.properties.MXProjectProperties;
import org.magiccube.mxtool.eclipse.resource.MXProjectResource;
import org.magiccube.mxtool.eclipse.resource.ResourceHelper;
import org.magiccube.mxtool.eclipse.wizards.pages.NewMXClassWizardPage;

public abstract class NewMXClassWizard extends Wizard implements INewWizard
{
	public NewMXClassWizard()
	{
		this(new MXClassGenOptions());
	}

	public NewMXClassWizard(MXClassGenOptions p_options)
	{
		super();
		_genOptions = p_options;
		setNeedsProgressMonitor(true);
	}

	public void init(IWorkbench workbench, IStructuredSelection p_selection)
	{
		_selection = p_selection;
		
		if (_selection == null || _selection.size() == 0)
		{
			getBasicPage().setErrorMessage("Please select a project first.");
			return;
		}
		
		IResource resource = (IResource) _selection.getFirstElement();
		_project = resource.getProject();
		_mxProjectResource = new MXProjectResource(_project);

		if (getProjectProperties() == null || !getProjectProperties().isEnabled())
		{
			getBasicPage().setErrorMessage("MXFramework of the current project haven't been enabled yet. You can enable it in the 'MXFramework' page of Project Properties.");
			return;
		}
		
		
		_genOptions.scriptPath = getProjectProperties().getScriptPath();
		IContainer scriptFolder = null;
		if (_genOptions.scriptPath.equals("/"))
		{
			scriptFolder = _project;
		}
		else
		{
			scriptFolder = _project.getFolder(getProjectProperties().getScriptPath());
		}
		
		if (scriptFolder == null || !scriptFolder.exists())
		{
			getBasicPage().setErrorMessage("The script path '" + getProjectProperties().getScriptPath() + "' is not currently available. You can modify it in the 'MXFramework' page of Project Properties.");
			return;
		}
		
		if (resource.getFullPath().toString().startsWith(scriptFolder.getFullPath().toString()))
		{
			IPath relPath = resource.getFullPath().makeRelativeTo(scriptFolder.getFullPath());
			String ns = relPath.toString().replaceAll("\\/", ".");
			_genOptions.namespace = ns;
		}
		
		getBasicPage().init(_genOptions);
	}
	
	
	
	private IStructuredSelection _selection = null;
	public IStructuredSelection getSelection()
	{
		return _selection;
	}
	
	private IProject _project = null;
	public IProject getProject()
	{
		return _project;
	}
	
	private MXProjectResource _mxProjectResource = null;
	public MXProjectResource getMXProjectResource()
	{
		return _mxProjectResource;
	}
	
	public MXProjectProperties getProjectProperties()
	{
		return _mxProjectResource.getProjectProperties();
	}
	
	
	
	private MXClassGenOptions _genOptions = null;
	public MXClassGenOptions getGenOptions()
	{
		return _genOptions;
	}
	
	protected abstract MXClassGenerator getClassGenerator();
	protected abstract MXHtmlGenerator getHtmlGenerator();
	protected abstract CssGenerator getCssGenerator();
	
	private NewMXClassWizardPage _basicPage = null;
	protected NewMXClassWizardPage getBasicPage()
	{
		if (_basicPage == null)
		{
			_basicPage = new NewMXClassWizardPage();
		}
		return _basicPage;
	}
	

	public void addPages()
	{
		addPage(getBasicPage());
	}

	public boolean performFinish()
	{
		if (!getBasicPage().validate())
		{
			return false;
		}
		
		IRunnableWithProgress op = new IRunnableWithProgress()
		{
			public void run(IProgressMonitor monitor) throws InvocationTargetException
			{
				try
				{
					doFinish(monitor);
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

	
	
	
	
	
	protected void doFinish(IProgressMonitor p_monitor) throws CoreException
	{
		IFile javaScriptFile = genJavaScriptFile(p_monitor);
		if (getGenOptions().genHtml && getHtmlGenerator() != null)
		{
			IFile htmlFile = genHtmlFile(p_monitor);
			openFile(htmlFile, p_monitor);
		}
		if (getGenOptions().genCss)
		{
			IFile cssFile = genCssFile(p_monitor);
			openFile(cssFile, p_monitor);
		}
		openFile(javaScriptFile, p_monitor);
	}

	protected IFile genJavaScriptFile(IProgressMonitor p_monitor) throws CoreException
	{
		String js = getClassGenerator().generateCode(getGenOptions()).toString();
		
		IFile file = getProject().getFile(getGenOptions().getJavaScriptPath());
		if (file.exists())
		{
			_throwCoreException(file.getFullPath() + " already exists.");
		}
		IContainer folder = file.getParent();
		if (folder instanceof IFolder)
		{
			ResourceHelper.prepareFolder((IFolder)folder, p_monitor);
		}
		try
		{
			InputStream stream = openContentStream(js);
			file.create(stream, true, p_monitor);
			stream.close();
		}
		catch (IOException e)
		{
			
		}
		return file;
	}

	protected IFile genHtmlFile(IProgressMonitor p_monitor) throws CoreException
	{
		String html = getHtmlGenerator().generateCode(getGenOptions()).toString();
		IFile file = getProject().getFile(getGenOptions().getDebugHtmlPath());
		if (file.exists())
		{
			_throwCoreException(file.getFullPath() + " already exists.");
		}
		IContainer folder = file.getParent();
		if (folder instanceof IFolder)
		{
			ResourceHelper.prepareFolder((IFolder)folder, p_monitor);
		}
		try
		{
			InputStream stream = openContentStream(html);
			file.create(stream, true, p_monitor);
			stream.close();
		}
		catch (IOException e)
		{
			
		}
		return file;
	}
	
	protected IFile genCssFile(IProgressMonitor p_monitor) throws CoreException
	{
		String css = getCssGenerator().generateCode(getGenOptions()).toString();
		
		IFile file = getProject().getFile(getGenOptions().getCssPath());
		if (file.exists())
		{
			_throwCoreException(file.getFullPath() + " already exists.");
		}
		IContainer folder = file.getParent();
		if (folder instanceof IFolder)
		{
			ResourceHelper.prepareFolder((IFolder)folder, p_monitor);
		}
		try
		{
			InputStream stream = openContentStream(css);
			file.create(stream, true, p_monitor);
			stream.close();
		}
		catch (IOException e)
		{
			
		}
		return file;
	}

	protected void openFile(final IFile p_file, IProgressMonitor p_monitor)
	{
		getShell().getDisplay().asyncExec(new Runnable()
		{
			public void run()
			{
				IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				try
				{
					IDE.openEditor(page, p_file, true);
				}
				catch (PartInitException e)
				{
				}
			}
		});
	}

	protected InputStream openContentStream(String contents)
	{
		try
		{
			return new ByteArrayInputStream(contents.getBytes("UTF-8"));
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
			return null;
		}
	}

	private void _throwCoreException(String message) throws CoreException
	{
		IStatus status = new Status(IStatus.ERROR, "org.magiccube.mxtool.eclipse", IStatus.OK, message, null);
		throw new CoreException(status);
	}
}