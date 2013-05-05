package org.magiccube.mxtool.eclipse.wizards;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.magiccube.mxtool.code.gen.MXClassGenOptions;
import org.magiccube.mxtool.eclipse.properties.MXProjectProperties;


public class NewMXClassWizardPage extends WizardPage
{
	private MXClassGenOptions _options = null;
	protected Combo namespaceCombo = null;
	protected Text classNameText = null;
	protected Combo superClassCombo = null;
	protected Button singletonCheckbox = null;
	
	public NewMXClassWizardPage()
	{
		super("basicPage");
	}
	
	public void init(MXClassGenOptions p_options, IStructuredSelection p_selection)
	{
		_options = p_options;
		setTitle("New " + _options.superClassType + " Class");
		setDescription("This wizard creates a new " + _options.superClassType + " subclass with MXFramework.");
		_selection = (IStructuredSelection) p_selection;

		if (_selection == null || _selection.size() == 0)
		{
			updateStatus("Please select a project first.");
			return;
		}
		
		IResource resource = (IResource) _selection.getFirstElement();
		_project = resource.getProject();
		try
		{
			_projectProperties = MXProjectProperties.getProperties(_project);
		}
		catch (CoreException e)
		{

		}

		if (_projectProperties == null || !_projectProperties.isEnabled())
		{
			updateStatus("MXFramework of the current project haven't been enabled yet. You can enable it in the 'MXFramework' page of Project Properties.");
			return;
		}
		
		
		
		IFolder scriptFolder = _project.getFolder(_projectProperties.getScriptPath());
		if (scriptFolder == null)
		{
			updateStatus("The script path '" + _projectProperties.getScriptPath() + "' is not currently available. You can modify it in the 'MXFramework' page of Project Properties.");
			return;
		}
		
		if (resource.getFullPath().toString().startsWith(scriptFolder.getFullPath().toString()))
		{
			IPath relPath = resource.getFullPath().makeRelativeTo(scriptFolder.getFullPath());
			String ns = relPath.toString().replaceAll("\\/", ".");
			_options.namespace = ns;
		}
	}
	
	
	private IStructuredSelection _selection;
	protected IStructuredSelection getSelection()
	{
		return _selection;
	}
	
	private IProject _project = null;
	protected IProject getProject()
	{
		return _project;
	}
	
	private MXProjectProperties _projectProperties = null;
	protected MXProjectProperties getProjectProperties()
	{
		return _projectProperties;
	}

	
	

	
	public void createControl(Composite p_parent)
	{
		Composite container = new Composite(p_parent, SWT.NULL);
		GridLayout gridLayout = new GridLayout(3, false);
		gridLayout.verticalSpacing = 9;
		container.setLayout(gridLayout);
		
		namespaceCombo = _appendCombo(container, "Namespace:");
		classNameText = _appendText(container, "Class name:");
		singletonCheckbox = _appendCheckbox(container, "Singleton class");
		superClassCombo = _appendCombo(container, "Super class:");
		
		setControl(container);
		
		applyOptions(_options);
	}






	protected void applyOptions(MXClassGenOptions p_options)
	{
		namespaceCombo.setText(p_options.namespace != null ? p_options.namespace : "");
		classNameText.setText(p_options.className);
		singletonCheckbox.setSelection(p_options.isSingleton);
		superClassCombo.setText(p_options.superClass != null ? p_options.superClass : "");
	}

	protected void updateStatus(String message)
	{
		setErrorMessage(message);
		setPageComplete(message == null);
	}
	
	
	
	
	private Text _appendText(Composite p_parent, String p_title)
	{
		Label label = new Label(p_parent, SWT.NULL);
		label.setText(p_title);
		
		Text text = new Text(p_parent, SWT.BORDER | SWT.SINGLE);
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		new Label(p_parent, SWT.NULL);
		return text;
	}
	
	private Combo _appendCombo(Composite p_parent, String p_title)
	{
		Label label = new Label(p_parent, SWT.NULL);
		label.setText(p_title);
		
		Combo combo = new Combo(p_parent, SWT.BORDER | SWT.SINGLE);
		combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		new Label(p_parent, SWT.NULL);
		return combo;
	}

	private Button _appendCheckbox(Composite p_parent, String p_title)
	{
		new Label(p_parent, SWT.NULL);
		
		Button checkbox = new Button(p_parent, SWT.CHECK);
		checkbox.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		checkbox.setText(p_title);
		
		new Label(p_parent, SWT.NULL);
		return checkbox;
	}
}