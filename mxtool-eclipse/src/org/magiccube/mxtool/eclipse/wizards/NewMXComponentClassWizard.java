package org.magiccube.mxtool.eclipse.wizards;

import org.magiccube.mxtool.code.gen.MXClassGenOptions;
import org.magiccube.mxtool.code.gen.MXClassGenerator;

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
}
