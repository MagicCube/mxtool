package org.magiccube.mxtool.eclipse.jobs;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.magiccube.mxtool.eclipse.resource.ResourceHelper;

public class DownloadJQueryJob implements IRunnableWithProgress
{
	private IFolder _jqueryFolder = null;
	
	public DownloadJQueryJob(IFolder p_jqueryFolder)
	{
		_jqueryFolder = p_jqueryFolder;
	}
	
	@Override
	public void run(IProgressMonitor p_monitor) throws InvocationTargetException, InterruptedException
	{
		try
		{
			URL url = null;
			try
			{
				url = new URL("http://code.jquery.com/jquery-2.0.0.min.js");
			}
			catch (MalformedURLException e)
			{

			}
			
			p_monitor.beginTask("Downloading '" + url + "'", 1);
			URLConnection connection = url.openConnection();
			connection.setReadTimeout(10 * 1000);
			InputStream stream = connection.getInputStream();
			
			ResourceHelper.prepareFolder(_jqueryFolder, p_monitor);
			IFile file = _jqueryFolder.getFile("jquery.js");
			if (file.exists())
			{
				file.setContents(stream, true, true, p_monitor);
			}
			else
			{
				file.create(stream, true, p_monitor);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			p_monitor.worked(1);
		}
	}

}
