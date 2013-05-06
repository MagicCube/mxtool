package org.magiccube.mxtool.eclipse.wizards;

import org.magiccube.mxtool.code.gen.MXClassGenOptions;
import org.magiccube.mxtool.code.gen.MXClassGenerator;

public class NewMXObjectClassWizard extends NewMXClassWizard
{
	public NewMXObjectClassWizard()
	{
		super(new MXClassGenOptions("MXObject"));
	}

	private MXClassGenerator _classGenerator = new MXClassGenerator();
	@Override
	protected MXClassGenerator getClassGenerator()
	{
		return _classGenerator;
	}
}
