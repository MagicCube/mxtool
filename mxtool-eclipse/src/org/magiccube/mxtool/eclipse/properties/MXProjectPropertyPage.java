package org.magiccube.mxtool.eclipse.properties;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
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

public class MXProjectPropertyPage extends PropertyPage
{
	private Button _enableMXFrameworkCheckbox = null;
	private Text _scriptPathText = null;
	private Button _scriptPathBrowseButton = null;

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
							IFolder folder = (IFolder) project.findMember(path);
							if (folder.getFolder("mx").exists())
							{
								_scriptPathText.setText(path.toString());
							}
							else
							{
								MessageBox msgBox = new MessageBox(getShell(), SWT.OK);
								msgBox.setText(Activator.PLUGIN_TITLE);
								msgBox.setMessage("Please ensure the script folder containing a subfolder named 'mx'.");
								msgBox.open();
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
		_setMXFrameworkEnabled(_properties.isEnabled());
		String path = _properties.getScriptPath();
		_scriptPathText.setText(path != null ? path : "");
	}

	public boolean performOk()
	{
		try
		{
			if (_enableMXFrameworkCheckbox.getSelection())
			{
				if (_scriptPathText.getText() == null || _scriptPathText.equals(""))
				{
					MessageBox msgBox = new MessageBox(getShell(), SWT.ERROR);
					msgBox.setText(Activator.PLUGIN_TITLE);
					msgBox.setMessage("Script path can not be empty.");
					msgBox.open();
					return false;
				}
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
	}
}
