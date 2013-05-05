package org.magiccube.mxtool.eclipse.wizards;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.magiccube.mxtool.code.gen.MXClassGenOptions;

public class NewMXClassWizard extends Wizard implements INewWizard
{
	private MXClassGenOptions _options = null;
	private NewMXClassWizardPage _basicPage = null;
	private ISelection _selection = null;

	public NewMXClassWizard()
	{
		this(new MXClassGenOptions());
	}

	public NewMXClassWizard(MXClassGenOptions p_options)
	{
		super();
		_basicPage = new NewMXClassWizardPage();
		_options = p_options;
		setNeedsProgressMonitor(true);
	}
	
	public void init(IWorkbench workbench, IStructuredSelection p_selection)
	{
		this._selection = p_selection;
		_basicPage.init(_options, p_selection);
	}


	public void addPages()
	{
		addPage(_basicPage);
	}

	public boolean performFinish()
	{
		final String containerName = null;
		final String fileName = null;
		IRunnableWithProgress op = new IRunnableWithProgress()
		{
			public void run(IProgressMonitor monitor)
					throws InvocationTargetException
			{
				try
				{
					doFinish(containerName, fileName, monitor);
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
			MessageDialog.openError(getShell(), "Error",
					realException.getMessage());
			return false;
		}
		return true;
	}


	private void doFinish(String containerName, String fileName,
			IProgressMonitor monitor) throws CoreException
	{
		// create a sample file
		monitor.beginTask("Creating " + fileName, 2);
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IResource resource = root.findMember(new Path(containerName));
		if (!resource.exists() || !(resource instanceof IContainer))
		{
			throwCoreException("Container \"" + containerName
					+ "\" does not exist.");
		}
		IContainer container = (IContainer) resource;
		final IFile file = container.getFile(new Path(fileName));
		try
		{
			InputStream stream = openContentStream();
			if (file.exists())
			{
				file.setContents(stream, true, true, monitor);
			}
			else
			{
				file.create(stream, true, monitor);
			}
			stream.close();
		}
		catch (IOException e)
		{
		}
		monitor.worked(1);
		monitor.setTaskName("Opening file for editing...");
		getShell().getDisplay().asyncExec(new Runnable()
		{
			public void run()
			{
				IWorkbenchPage page = PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getActivePage();
				try
				{
					IDE.openEditor(page, file, true);
				}
				catch (PartInitException e)
				{
				}
			}
		});
		monitor.worked(1);	}

	private InputStream openContentStream()
	{
		String contents = "This is the initial file contents for *.js file that should be word-sorted in the Preview page of the multi-page editor";
		return new ByteArrayInputStream(contents.getBytes());
	}

	private void throwCoreException(String message) throws CoreException
	{
		IStatus status = new Status(IStatus.ERROR,
				"org.magiccube.mxtool.eclipse", IStatus.OK, message, null);
		throw new CoreException(status);
	}
}