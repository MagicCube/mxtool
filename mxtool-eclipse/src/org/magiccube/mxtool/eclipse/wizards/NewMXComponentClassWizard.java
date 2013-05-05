package org.magiccube.mxtool.eclipse.wizards;

import org.magiccube.mxtool.code.gen.MXClassGenOptions;

public class NewMXComponentClassWizard extends NewMXClassWizard
{
	public NewMXComponentClassWizard()
	{
		super(new MXClassGenOptions("MXComponent"));
	}
}
