package org.magiccube.mxtool.eclipse.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.magiccube.mxtool.code.gen.MXClassGenOptions;
import org.magiccube.mxtool.eclipse.wizards.NewMXClassWizard;
import org.magiccube.mxtool.eclipse.wizards.NewMXComponentClassWizard;
import org.magiccube.mxtool.eclipse.wizards.NewMXObjectClassWizard;
import org.magiccube.mxtool.eclipse.wizards.NewMXViewClassWizard;

public class NewMXClassAction implements IObjectActionDelegate
{

	private Shell _shell = null;
	private ISelection _selection = null;

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
		String superClassType = action.getId().substring("org.magiccube.mxtool.eclipse.actions.new".length(), action.getId().length() - "ClassAction".length());
		NewMXClassWizard wizard = null;
		if (superClassType.equals("MXComponent"))
		{
			wizard = new NewMXComponentClassWizard();
		}
		else if (superClassType.equals("MXView"))
		{
			wizard = new NewMXViewClassWizard();
		}
		else
		{
			wizard = new NewMXObjectClassWizard();
		}
		IStructuredSelection structuredSelection = (IStructuredSelection)_selection;
		wizard.init(PlatformUI.getWorkbench(), structuredSelection);
		
		WizardDialog dialog = new WizardDialog(_shell, wizard);
		dialog.open();
	}

	public void selectionChanged(IAction action, ISelection selection)
	{
		_selection = selection;
	}

}
