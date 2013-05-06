package org.magiccube.mxtool.eclipse.wizards.pages;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class NewMXAppClassWizardPage extends NewMXClassWizardPage
{
	protected Text appDisplayNameText = null;
	protected Button genHtmlCheckbox = null;
	protected Button genCssCheckbox = null;
	
	@Override
	protected void addSubcontrols(Composite p_container)
	{
		appDisplayNameText = addText(p_container, "Application display name:", "New MX Application");
		genHtmlCheckbox = addCheckbox(p_container, "Generate web page", true);
		genCssCheckbox = addCheckbox(p_container, "Generate CSS", true);
	}

	public boolean validate()
	{
		boolean result = super.validate();
		if (!result) return false;
		
		getGenOptions().genHtml = genHtmlCheckbox.getSelection();
		getGenOptions().genCss = genCssCheckbox.getSelection();
		getGenOptions().properties.put("appDisplayName", appDisplayNameText.getText());
		
		return result;
	}
}