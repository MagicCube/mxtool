package org.magiccube.mxtool.eclipse.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.magiccube.mxtool.eclipse.wizards.NewMXClassWizard;

public class NewMXClassAction implements IObjectActionDelegate
{

	private Shell _shell;

	public NewMXClassAction()
	{
		super();
	}

	public void setActivePart(IAction action, IWorkbenchPart targetPart)
	{
		_shell = targetPart.getSite().getShell();
	}


	public void run(IAction action)
	{
		NewMXClassWizard wizard = new NewMXClassWizard();
		WizardDialog dialog = new WizardDialog(_shell, wizard);
		dialog.open();
	}

	public void selectionChanged(IAction action, ISelection selection)
	{
	}

}
