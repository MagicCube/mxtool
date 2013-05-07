package org.magiccube.mxtool.eclipse.jobs;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.magiccube.mxtool.eclipse.resource.ResourceHelper;

public class DownloadMXFrameworkJob implements IRunnableWithProgress
{
	private IFolder _mxFolder = null;
	
	public DownloadMXFrameworkJob(IFolder p_mxFolder)
	{
		_mxFolder = p_mxFolder;
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public void run(IProgressMonitor p_monitor) throws InvocationTargetException, InterruptedException
	{
		try
		{
			URL url = null;
			try
			{
				url = new URL("https://github.com/MagicCube/mxframework-core/archive/master.zip");
			}
			catch (MalformedURLException e)
			{

			}
			
			p_monitor.beginTask("Downloading '" + url + "'", 1);
			URLConnection connection = url.openConnection();
			connection.setReadTimeout(15 * 1000);
			InputStream downloadInputStream = connection.getInputStream();
			ResourceHelper.prepareFolder(_mxFolder, p_monitor);
			
			IFile downloadFile = _mxFolder.getFile("mx.zip");
			if (downloadFile.exists())
			{
				downloadFile.setContents(downloadInputStream, true, true, p_monitor);
			}
			else
			{
				downloadFile.create(downloadInputStream, true, p_monitor);
			}
			
			ZipFile zipFile = new ZipFile(downloadFile.getRawLocation().toString());
			Enumeration entries = zipFile.entries();
			ZipEntry entry = null;
			while (entries.hasMoreElements())
			{
				entry = (ZipEntry)entries.nextElement();
				if (entry.getName().equals("mxframework-core-master/"))
				{
					continue;
				}
				else if (entry.getName().startsWith("mxframework-core-master/"))
				{
					String path = entry.getName().substring("mxframework-core-master/".length());
					if (entry.isDirectory())
					{
						ResourceHelper.prepareFolder(_mxFolder.getFolder(path.substring(0, path.length() - 1)), p_monitor);
					}
					else
					{
						InputStream zipStream = zipFile.getInputStream(entry);
						IFile file = _mxFolder.getFile(path);
						if (file.exists())
						{
							file.setContents(zipStream, true, true, p_monitor);
						}
						else
						{
							file.create(zipStream, true, p_monitor);
						}
					}
				}
			}
			zipFile.close();
			downloadFile.delete(true, p_monitor);
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
