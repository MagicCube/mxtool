package org.magiccube.mxtool.eclipse.wizards.pages;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class NewMXSceneClassWizardPage extends NewMXClassWizardPage
{
	protected Text sceneTitleText = null;
	protected Button autoFillParentCheckbox = null;
	protected Button overrideActivateCheckbox = null;
	
	@Override
	protected void addSubcontrols(Composite p_container)
	{
		sceneTitleText = addText(p_container, "Scene title:", "New MX Scene");
		autoFillParentCheckbox = addCheckbox(p_container, "Auto fill parent", true);
		overrideActivateCheckbox = addCheckbox(p_container, "Override 'activate' method", true);
	}

	public boolean validate()
	{
		boolean result = super.validate();
		if (!result) return false;
		
		getGenOptions().properties.put("sceneTitle", sceneTitleText.getText());
		getGenOptions().properties.put("autoFillParent", autoFillParentCheckbox.getSelection());
		getGenOptions().properties.put("overrideActivate", overrideActivateCheckbox.getSelection());
		
		return result;
	}
}