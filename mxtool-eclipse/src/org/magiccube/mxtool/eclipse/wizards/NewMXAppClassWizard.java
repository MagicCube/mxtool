package org.magiccube.mxtool.eclipse.wizards;

import org.magiccube.mxtool.code.gen.CssGenerator;
import org.magiccube.mxtool.code.gen.MXAppClassGenerator;
import org.magiccube.mxtool.code.gen.MXAppCssGenerator;
import org.magiccube.mxtool.code.gen.MXAppHtmlGenerator;
import org.magiccube.mxtool.code.gen.MXClassGenOptions;
import org.magiccube.mxtool.code.gen.MXHtmlGenerator;
import org.magiccube.mxtool.eclipse.wizards.pages.NewMXAppClassWizardPage;
import org.magiccube.mxtool.eclipse.wizards.pages.NewMXClassWizardPage;

public class NewMXAppClassWizard extends NewMXClassWizard
{
	public NewMXAppClassWizard()
	{
		super(new MXClassGenOptions("MXApp"));
	}
	
	
	private NewMXAppClassWizardPage _basicPage = null;
	@Override
	protected NewMXClassWizardPage getBasicPage()
	{
		if (_basicPage == null)
		{
			_basicPage = new NewMXAppClassWizardPage();
		}
		return _basicPage;
	}
	
	
	private MXAppClassGenerator _classGenerator = new MXAppClassGenerator();
	@Override
	protected MXAppClassGenerator getClassGenerator()
	{
		return _classGenerator;
	}

	
	
	private MXAppHtmlGenerator _htmlGenerator = new MXAppHtmlGenerator();
	@Override
	protected MXHtmlGenerator getHtmlGenerator()
	{
		return _htmlGenerator;
	}

	private MXAppCssGenerator _cssGenerator = new MXAppCssGenerator();
	@Override
	protected CssGenerator getCssGenerator()
	{
		return _cssGenerator;
	}
}
