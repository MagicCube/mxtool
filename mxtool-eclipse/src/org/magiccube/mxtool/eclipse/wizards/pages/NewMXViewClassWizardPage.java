package org.magiccube.mxtool.eclipse.wizards.pages;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class NewMXViewClassWizardPage extends NewMXClassWizardPage
{
	protected Button genCssCheckbox = null;
	
	@Override
	protected void addSubcontrols(Composite p_container)
	{
		genCssCheckbox = addCheckbox(p_container, "Generate CSS file in 'res' folder");
	}

	public boolean validate()
	{
		boolean result = super.validate();
		if (!result) return false;
		
		getGenOptions().genCss = genCssCheckbox.getSelection();
		
		return result;
	}
}