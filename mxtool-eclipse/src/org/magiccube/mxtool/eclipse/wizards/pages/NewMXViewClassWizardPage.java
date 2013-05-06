package org.magiccube.mxtool.eclipse.wizards.pages;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class NewMXViewClassWizardPage extends NewMXClassWizardPage
{
	protected Button genCssCheckbox = null;
	protected Button genHtmlCheckbox = null;
	
	@Override
	protected void addSubcontrols(Composite p_container)
	{
		genHtmlCheckbox = addCheckbox(p_container, "Generate debug web page", false);
		genCssCheckbox = addCheckbox(p_container, "Generate CSS", true);
	}

	public boolean validate()
	{
		boolean result = super.validate();
		if (!result) return false;
		
		getGenOptions().genHtml = genHtmlCheckbox.getSelection();
		getGenOptions().genCss = genCssCheckbox.getSelection();
		
		return result;
	}
}