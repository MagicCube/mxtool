package org.magiccube.mxtool.eclipse.wizards;

import org.magiccube.mxtool.code.gen.MXClassGenOptions;
import org.magiccube.mxtool.code.gen.MXClassGenerator;
import org.magiccube.mxtool.code.gen.MXHtmlGenerator;
import org.magiccube.mxtool.code.gen.MXViewClassGenerator;
import org.magiccube.mxtool.code.gen.MXViewHtmlGenerator;
import org.magiccube.mxtool.eclipse.wizards.pages.NewMXClassWizardPage;
import org.magiccube.mxtool.eclipse.wizards.pages.NewMXViewClassWizardPage;

public class NewMXViewClassWizard extends NewMXClassWizard
{
	public NewMXViewClassWizard()
	{
		super(new MXClassGenOptions("MXView"));
	}
	
	
	private NewMXViewClassWizardPage _basicPage = null;
	@Override
	protected NewMXClassWizardPage getBasicPage()
	{
		if (_basicPage == null)
		{
			_basicPage = new NewMXViewClassWizardPage();
		}
		return _basicPage;
	}
	
	private MXViewClassGenerator _classGenerator = new MXViewClassGenerator();
	@Override
	protected MXClassGenerator getClassGenerator()
	{
		return _classGenerator;
	}

	
	private MXViewHtmlGenerator _htmlGenerator = new MXViewHtmlGenerator();
	@Override
	protected MXHtmlGenerator getHtmlGenerator()
	{
		return _htmlGenerator;
	}
}
