package org.magiccube.mxtool.eclipse.properties;

import java.util.ArrayList;
import java.util.List;

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
	private Button _validationOnlyCheckbox = null;
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
				if (_enableMXFrameworkCheckbox.getSelection())
				{
					_setValidationOnly(false);
				}
			}
		});
		
		_validationOnlyCheckbox = new Button(container, SWT.CHECK);
		_validationOnlyCheckbox.setText("Perform validation only");
		_validationOnlyCheckbox.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				_setValidationOnly(_validationOnlyCheckbox.getSelection());
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
						IContainer folder = project.getFolder(path);
						if (path.toString().equals(project.getFullPath().toString()))
						{
							folder = project;
							path = null;
						}
						else if (path.toString().startsWith(project.getFullPath().toString()))
						{
							path = (Path) path.makeRelativeTo(project.getFullPath());
							folder = project.getFolder(path);
						}
						else
						{
							MessageBox msgBox = new MessageBox(getShell(), SWT.OK);
							msgBox.setText(Activator.PLUGIN_TITLE);
							msgBox.setMessage("Script path must be under '" + project.getFullPath() + "'.");
							msgBox.open();
							return;
						}
							
						IFolder jqueryFolder = folder.getFolder(Path.fromOSString("lib/jquery"));
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
						
						IFolder mxFolder = folder.getFolder(Path.fromOSString("mx"));
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
							return;
						}
						
						if (path != null)
						{
							_scriptPathText.setText(path.toString());
						}
						else
						{
							_scriptPathText.setText("/");
						}
						_setMXFrameworkEnabled(true);
						validate();
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
				String path = null;
				if (_scriptPathText.getText().equals("/"))
				{
					path = "/mx";
				}
				else
				{
					path = _scriptPathText.getText() + "/mx";
				}
				_downloadMXFramework(getProject().getFolder(path));
			}
		});
		_updateJQueryButton = new Button(buttonRow, SWT.PUSH);
		_updateJQueryButton.setText("Update JQuery");
		_updateJQueryButton.addSelectionListener(new SelectionAdapter()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				String path = null;
				if (_scriptPathText.getText().equals("/"))
				{
					path = "/lib/jquery";
				}
				else
				{
					path = _scriptPathText.getText() + "/lib/jquery";
				}
				_downloadJQuery(getProject().getFolder(path));
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
		if (_properties.isEnabled())
		{
			_setValidationOnly(_properties.isValidationOnly());
		}
		else
		{
			_setValidationOnly(false);
		}
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
				_properties.setValidationOnly(_validationOnlyCheckbox.getSelection());
				_enableMXBuilder();
			}
			else
			{
				_properties.setEnabled(false);
				_properties.setValidationOnly(false);
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
		}
		setErrorMessage(null);
		return true;
	}

	private void _enableMXBuilder() throws CoreException
	{
		IProject project = getProject();
		IProjectDescription description = project.getDescription();
		String[] natures = description.getNatureIds();
		List<String> newNatureList = new ArrayList<String>();

		for (int i = 0; i < natures.length; ++i)
		{
			if (MXNature.NATURE_ID.equals(natures[i]) || "org.eclipse.jdt.core.javabuilder".equals(natures[i]))
			{
				continue;
			}
			else
			{
				newNatureList.add(natures[i]);
			}
		}

		newNatureList.add(MXNature.NATURE_ID);
		String[] newNatures = new String[newNatureList.size()];
		newNatures = newNatureList.toArray(newNatures);
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
		_validationOnlyCheckbox.setEnabled(p_enabled);
		_scriptPathBrowseButton.setEnabled(p_enabled);
		
		_updateMXFrameworkButton.setEnabled(p_enabled && !_scriptPathText.getText().equals(""));
		_updateJQueryButton.setEnabled(p_enabled && !_scriptPathText.getText().equals(""));
	}
	
	private void _setValidationOnly(boolean p_enabled)
	{
		_validationOnlyCheckbox.setSelection(p_enabled);
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
