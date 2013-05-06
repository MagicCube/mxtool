package org.magiccube.mxtool.eclipse.wizards;

import org.magiccube.mxtool.code.gen.CssGenerator;
import org.magiccube.mxtool.code.gen.MXClassGenOptions;
import org.magiccube.mxtool.code.gen.MXClassGenerator;
import org.magiccube.mxtool.code.gen.MXHtmlGenerator;
import org.magiccube.mxtool.code.gen.MXSceneClassGenerator;
import org.magiccube.mxtool.code.gen.MXSceneHtmlGenerator;
import org.magiccube.mxtool.eclipse.wizards.pages.NewMXClassWizardPage;
import org.magiccube.mxtool.eclipse.wizards.pages.NewMXSceneClassWizardPage;

public class NewMXSceneClassWizard extends NewMXClassWizard
{
	public NewMXSceneClassWizard()
	{
		super(new MXClassGenOptions("MXScene"));
	}
	
	
	private NewMXSceneClassWizardPage _basicPage = null;
	@Override
	protected NewMXClassWizardPage getBasicPage()
	{
		if (_basicPage == null)
		{
			_basicPage = new NewMXSceneClassWizardPage();
		}
		return _basicPage;
	}
	
	private MXSceneClassGenerator _classGenerator = new MXSceneClassGenerator();
	@Override
	protected MXClassGenerator getClassGenerator()
	{
		return _classGenerator;
	}

	private MXSceneHtmlGenerator _htmlGenerator = new MXSceneHtmlGenerator();
	@Override
	protected MXHtmlGenerator getHtmlGenerator()
	{
		return _htmlGenerator;
	}
	

	private CssGenerator _cssGenerator = new CssGenerator();
	@Override
	protected CssGenerator getCssGenerator()
	{
		return _cssGenerator;
	}
}
