package org.magiccube.mxtool.eclipse.properties;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;
import org.eclipse.ui.dialogs.PropertyPage;
import org.magiccube.mxtool.eclipse.Activator;
import org.magiccube.mxtool.eclipse.builders.MXNature;
import org.magiccube.mxtool.eclipse.jobs.DownloadJQueryJob;
import org.magiccube.mxtool.eclipse.jobs.DownloadMXFrameworkJob;

public class MXProjectPropertyPage extends PropertyPage
{
	private Button _enableMXFrameworkCheckbox = null;
	private Text _scriptPathText = null;
	private Button _scriptPathBrowseButton = null;
	private Button _updateMXFrameworkButton = null;
	private Button _updateJQueryButton = null;

	@Override
	protected Control createContents(Composite p_parent)
	{
		Composite container = new Composite(p_parent, SWT.NULL);

		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.verticalSpacing = 9;
		container.setLayout(gridLayout);

		_enableMXFrameworkCheckbox = new Button(container, SWT.CHECK);
		_enableMXFrameworkCheckbox.setText("Enable MXFramework and MXBuilder");
		_enableMXFrameworkCheckbox.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				_setMXFrameworkEnabled(_enableMXFrameworkCheckbox.getSelection());
			}
		});

		Group groupContainer = new Group(container, SWT.NULL);
		groupContainer.setText("MX Project");
		groupContainer.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		gridLayout = new GridLayout(3, false);
		gridLayout.verticalSpacing = 9;
		groupContainer.setLayout(gridLayout);

		Label label = new Label(groupContainer, SWT.NULL);
		label.setText("Script path:");

		_scriptPathText = new Text(groupContainer, SWT.BORDER | SWT.SINGLE);
		_scriptPathText.setEditable(false);
		_scriptPathText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		_scriptPathBrowseButton = new Button(groupContainer, SWT.PUSH);
		_scriptPathBrowseButton.setText("Browse...");
		_scriptPathBrowseButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				IProject project = getProject();
				IResource member = project.findMember("scripts", true);
				IContainer container = member instanceof IContainer ? (IFolder) member : project;

				ContainerSelectionDialog dialog = new ContainerSelectionDialog(getShell(), container, false, "Select the Script path");
				if (dialog.open() == ContainerSelectionDialog.OK)
				{
					Object[] result = dialog.getResult();
					if (result.length == 1)
					{
						Path path = (Path) result[0];

						if (path.toString().startsWith(project.getFullPath().toString()))
						{
							path = (Path) path.makeRelativeTo(project.getFullPath());
							IFolder folder = project.getFolder(path);
							IFolder jqueryFolder = folder.getFolder("lib/jquery");
							if (!jqueryFolder.exists() || !jqueryFolder.getFile("jquery.js").exists())
							{
								MessageBox msgBox = new MessageBox(getShell(), SWT.YES | SWT.NO);
								msgBox.setText(Activator.PLUGIN_TITLE);
								msgBox.setMessage("JQuery is not found in the script path. Do you want to download it?");
								if (msgBox.open() == SWT.YES)
								{
									_downloadJQuery(jqueryFolder);
								}
							}
							
							IFolder mxFolder = folder.getFolder("mx");
							if (!mxFolder.exists() || !mxFolder.getFile("framework-core.js").exists())
							{
								MessageBox msgBox = new MessageBox(getShell(), SWT.YES | SWT.NO);
								msgBox.setText(Activator.PLUGIN_TITLE);
								msgBox.setMessage("MXFramework is not found in the script path. Do you want to download it?");
								if (msgBox.open() == SWT.YES)
								{
									_downloadMXFramework(mxFolder);
									_scriptPathText.setText(path.toString());
									_setMXFrameworkEnabled(true);
									validate();
								}
							}
							else
							{
								_scriptPathText.setText(path.toString());
								_setMXFrameworkEnabled(true);
								validate();
							}
						}
						else
						{
							MessageBox msgBox = new MessageBox(getShell(), SWT.OK);
							msgBox.setText(Activator.PLUGIN_TITLE);
							msgBox.setMessage("Script path must be under '" + project.getFullPath() + "'.");
							msgBox.open();
						}
					}
				}
			}
		});
		
		
		
		new Label(groupContainer, SWT.NULL);
		
		Composite buttonRow = new Composite(groupContainer, SWT.NULL);
		RowLayout buttonRowLayout = new RowLayout(SWT.HORIZONTAL);
		buttonRow.setLayout(buttonRowLayout);
		buttonRow.setData(new GridData(GridData.FILL_HORIZONTAL));
		_updateMXFrameworkButton = new Button(buttonRow, SWT.PUSH);
		_updateMXFrameworkButton.setText("Update MXFramework");
		_updateMXFrameworkButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				_downloadMXFramework(getProject().getFolder(_scriptPathText.getText() + "/mx"));
			}
		});
		_updateJQueryButton = new Button(buttonRow, SWT.PUSH);
		_updateJQueryButton.setText("Update JQuery");
		_updateJQueryButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				_downloadJQuery(getProject().getFolder(_scriptPathText.getText() + "/lib/jquery"));
			}
		});
		

		try
		{
			_loadProperties();
		}
		catch (CoreException e1)
		{
			e1.printStackTrace();
		}
		return container;
	}

	private IProject _project = null;

	public IProject getProject()
	{
		if (_project == null)
		{
			_project = (IProject) getElement();
		}
		return _project;
	}

	private MXProjectProperties _properties = null;

	public MXProjectProperties getProperties()
	{
		return _properties;
	}

	private void _loadProperties() throws CoreException
	{
		IProject project = getProject();
		_properties = MXProjectProperties.getProperties(project);
		String path = _properties.getScriptPath();
		_scriptPathText.setText(path != null ? path : "");
		_setMXFrameworkEnabled(_properties.isEnabled());		
		validate();
	}

	public boolean performOk()
	{
		try
		{
			if (!validate())
			{
				return false;
			}
			
			if (_enableMXFrameworkCheckbox.getSelection())
			{
				_properties.setEnabled(true);
				_properties.setScriptPath(_scriptPathText.getText());
				_enableMXBuilder();
			}
			else
			{
				_properties.setEnabled(false);
				_properties.setScriptPath(null);
				_disableMXBuilder();
			}
		}
		catch (CoreException e)
		{
			return false;
		}

		return true;
	}
	
	public boolean validate()
	{
		if (_enableMXFrameworkCheckbox.getSelection())
		{
			if (_scriptPathText.getText().equals(""))
			{
				setErrorMessage("Script path can not be empty.");
				return false;
			}
			
			IFolder scriptFolder = getProject().getFolder(_scriptPathText.getText());
			if (!scriptFolder.exists())
			{
				setErrorMessage("Script path can not be empty.");
				return false;
			}
			
			IFile jqueryFile = scriptFolder.getFile("lib/jquery/jquery.js");
			if (!jqueryFile.exists())
			{
				setMessage("JQuery is not found in the script path.", WARNING);
			}
			else
			{
				setMessage(null);
			}
			
			
			IFile mxFile = scriptFolder.getFile("mx/framework-core.js");
			if (!mxFile.exists())
			{
				setErrorMessage("MXFramework is not found or well structured in the script path.");
				return false;
			}
		}
		setErrorMessage(null);
		return true;
	}

	private void _enableMXBuilder() throws CoreException
	{
		IProject project = getProject();
		IProjectDescription description = project.getDescription();
		String[] natures = description.getNatureIds();

		for (int i = 0; i < natures.length; ++i)
		{
			if (MXNature.NATURE_ID.equals(natures[i]))
			{
				// Remove the nature
				String[] newNatures = new String[natures.length - 1];
				System.arraycopy(natures, 0, newNatures, 0, i);
				System.arraycopy(natures, i + 1, newNatures, i, natures.length - i - 1);
				description.setNatureIds(newNatures);
				project.setDescription(description, null);
			}
		}

		String[] newNatures = new String[natures.length + 1];
		System.arraycopy(natures, 0, newNatures, 0, natures.length);
		newNatures[natures.length] = MXNature.NATURE_ID;
		description.setNatureIds(newNatures);
		project.setDescription(description, null);
	}

	private void _disableMXBuilder() throws CoreException
	{
		IProject project = getProject();
		IProjectDescription description = project.getDescription();
		String[] natures = description.getNatureIds();

		for (int i = 0; i < natures.length; ++i)
		{
			if (MXNature.NATURE_ID.equals(natures[i]))
			{
				String[] newNatures = new String[natures.length - 1];
				System.arraycopy(natures, 0, newNatures, 0, i);
				System.arraycopy(natures, i + 1, newNatures, i, natures.length - i - 1);
				description.setNatureIds(newNatures);
				project.setDescription(description, null);
				return;
			}
		}
	}

	private void _setMXFrameworkEnabled(boolean p_enabled)
	{
		_enableMXFrameworkCheckbox.setSelection(p_enabled);
		_scriptPathBrowseButton.setEnabled(p_enabled);
		
		_updateMXFrameworkButton.setEnabled(p_enabled && !_scriptPathText.getText().equals(""));
		_updateJQueryButton.setEnabled(p_enabled && !_scriptPathText.getText().equals(""));
	}

	private void _downloadJQuery(final IFolder p_jqueryFolder)
	{
		ProgressMonitorDialog progress = new ProgressMonitorDialog(getShell());
		try
		{
			progress.run(true, false, new DownloadJQueryJob(p_jqueryFolder));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private void _downloadMXFramework(IFolder p_mxFolder)
	{
		ProgressMonitorDialog progress = new ProgressMonitorDialog(getShell());
		try
		{
			progress.run(true, false, new DownloadMXFrameworkJob(p_mxFolder));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
