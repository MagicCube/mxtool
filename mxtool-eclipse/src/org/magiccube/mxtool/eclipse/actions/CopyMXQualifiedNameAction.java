package org.magiccube.mxtool.eclipse.actions;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.magiccube.mxtool.eclipse.resource.MXProjectResource;

public class CopyMXQualifiedNameAction implements IObjectActionDelegate
{

	private Shell _shell = null;
	private ISelection _selection = null;

	public CopyMXQualifiedNameAction()
	{
		super();
	}

	public void setActivePart(IAction action, IWorkbenchPart targetPart)
	{
		_shell = targetPart.getSite().getShell();
	}


	public void run(IAction action)
	{
		if (_selection != null)
		{
			if (_selection instanceof IStructuredSelection && ((IStructuredSelection) _selection).getFirstElement() instanceof IResource)
			{
				IResource resource = (IResource)((IStructuredSelection)_selection).getFirstElement();
				if (resource instanceof IFile)
				{
					IFile file = (IFile)resource;
					MXProjectResource projectResource = new MXProjectResource(file.getProject());
					if (!projectResource.getProjectProperties().isEnabled())
					{
						return;
					}
					
					final Clipboard cb = new Clipboard(_shell.getDisplay());
					String className = projectResource.getClassNameOfFile(file);
					TextTransfer textTransfer = TextTransfer.getInstance();
			        cb.setContents(new Object[] { className }, new Transfer[] { textTransfer });
				}
			}
		}
	}

	public void selectionChanged(IAction action, ISelection selection)
	{
		_selection = selection;
	}

}
