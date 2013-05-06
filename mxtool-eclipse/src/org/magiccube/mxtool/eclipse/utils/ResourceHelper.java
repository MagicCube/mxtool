package org.magiccube.mxtool.eclipse.utils;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

public final class ResourceHelper
{

	public static void prepareFolder(IFolder p_folder, IProgressMonitor p_monitor) throws CoreException
	{
		IContainer parent = p_folder.getParent();
		if (parent instanceof IFolder)
		{
			prepareFolder((IFolder)parent, p_monitor);
		}
		if (!p_folder.exists())
		{
			p_folder.create(true, true, p_monitor);
		}
	}
	
	public static void prepareFolder(IFolder p_folder) throws CoreException
	{
		prepareFolder(p_folder, null);
	}

}
