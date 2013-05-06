package org.magiccube.mxtool.eclipse.wizards;

import org.magiccube.mxtool.code.gen.CssGenerator;
import org.magiccube.mxtool.code.gen.MXClassGenOptions;
import org.magiccube.mxtool.code.gen.MXClassGenerator;
import org.magiccube.mxtool.code.gen.MXHtmlGenerator;

public class NewMXComponentClassWizard extends NewMXClassWizard
{
	public NewMXComponentClassWizard()
	{
		super(new MXClassGenOptions("MXComponent"));
	}

	private MXClassGenerator _classGenerator = new MXClassGenerator();
	@Override
	protected MXClassGenerator getClassGenerator()
	{
		return _classGenerator;
	}
	
	@Override
	protected MXHtmlGenerator getHtmlGenerator()
	{
		return null;
	}

	@Override
	protected CssGenerator getCssGenerator()
	{
		return null;
	}
}
