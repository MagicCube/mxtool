package org.magiccube.mxtool.eclipse.wizards.pages;

import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.magiccube.mxtool.code.gen.MXClassGenOptions;
import org.magiccube.mxtool.eclipse.properties.MXProjectProperties;
import org.magiccube.mxtool.eclipse.wizards.NewMXClassWizard;

public class NewMXClassWizardPage extends WizardPage implements ModifyListener
{
	protected Combo namespaceCombo = null;
	protected Text classNameText = null;
	protected Combo superClassCombo = null;
	protected Button singletonCheckbox = null;

	public NewMXClassWizardPage()
	{
		super("basicPage");
	}

	public void init(MXClassGenOptions p_options)
	{
		applyOptions(p_options);
	}

	protected IStructuredSelection getSelection()
	{
		if (getWizard() != null)
		{
			return ((NewMXClassWizard) getWizard()).getSelection();
		}
		else
		{
			return null;
		}
	}

	protected IProject getProject()
	{
		IWizard wizard = getWizard();
		if (wizard != null)
		{
			return ((NewMXClassWizard) getWizard()).getProject();
		}
		else
		{
			return null;
		}
	}

	protected MXProjectProperties getProjectProperties()
	{
		if (getWizard() != null)
		{
			return ((NewMXClassWizard) getWizard()).getProjectProperties();
		}
		else
		{
			return null;
		}
	}
	
	private MXClassGenOptions _genOptions = null;
	protected MXClassGenOptions getGenOptions()
	{
		return _genOptions;
	}

	public void createControl(Composite p_parent)
	{
		Composite container = new Composite(p_parent, SWT.NULL);
		GridLayout gridLayout = new GridLayout(3, false);
		gridLayout.verticalSpacing = 9;
		container.setLayout(gridLayout);

		namespaceCombo = addCombo(container, "Namespace:");
		classNameText = addText(container, "Class name:");
		singletonCheckbox = addCheckbox(container, "Singleton class");
		superClassCombo = addCombo(container, "Super class:");
		addSubcontrols(container);

		setControl(container);

		applyOptions(_genOptions);
	}

	protected void addSubcontrols(Composite p_container)
	{
		
	}

	@Override
	public boolean canFlipToNextPage()
	{
		boolean result = super.canFlipToNextPage();
		if (!result)
			return false;

		return validate();
	}

	protected void applyOptions(MXClassGenOptions p_options)
	{
		if (p_options != null)
		{
			_genOptions = p_options;

			setTitle("New " + _genOptions.superClassType + " Class");
			setDescription("This wizard creates a new " + _genOptions.superClassType + " subclass with MXFramework.");

			if (classNameText != null)
			{
				namespaceCombo.setText(p_options.namespace != null ? p_options.namespace : "");
				classNameText.setText(p_options.className);
				singletonCheckbox.setSelection(p_options.isSingleton);
				superClassCombo.setText(p_options.superClass != null ? p_options.superClass : "");
			}
		}
	}

	public void setErrorMessage(String message)
	{
		super.setErrorMessage(message);
		setPageComplete(message == null);
	}

	public boolean validate()
	{
		if (getProject() == null)
		{
			return false;
		}
		
		if (!validateNamespace())
		{
			return false;
		}
		if (!validateClassName())
		{
			return false;
		}
		if (!validateSuperClass())
		{
			return false;
		}
		
		_genOptions.isSingleton = singletonCheckbox.getSelection();
		
		return true;
	}

	protected Pattern namespacePattern = Pattern.compile("^[a-z][a-z0-9\\.]*[a-z0-9]$");

	protected boolean validateNamespace()
	{
		_genOptions.namespace = namespaceCombo.getText();
		
		if (namespaceCombo.getText().equals(""))
		{
			setErrorMessage("Namespace can not be empty.");
			return false;
		}

		if (!namespacePattern.matcher(namespaceCombo.getText()).matches())
		{
			setErrorMessage("Namespace is not validated.");
			return false;
		}
		return true;
	}

	protected Pattern classNamePattern = Pattern.compile("^[A-Z][a-zA-Z0-9]+$");

	protected boolean validateClassName()
	{
		_genOptions.className = classNameText.getText();
		
		if (classNameText.getText().equals(""))
		{
			setErrorMessage("Class name can not be empty.");
			return false;
		}

		if (!classNamePattern.matcher(classNameText.getText()).matches())
		{
			setErrorMessage("Class name is not validated.");
			return false;
		}
		
		Path filePath = getGenOptions().getJavaScriptPath();
		IFile file = getProject().getFile(filePath);
		if (file.exists())
		{
			setErrorMessage("'" + classNameText.getText() + "' is already existed.");
			return false;
		}
		
		if (_genOptions.classNamePostfix != null && !classNameText.getText().endsWith(_genOptions.classNamePostfix))
		{
			setMessage("The class name is suggested to be ended with '" + _genOptions.classNamePostfix + "'.", WARNING);
		}
		else
		{
			setMessage("");
		}
		
		return true;
	}

	protected boolean validateSuperClass()
	{
		_genOptions.superClass = superClassCombo.getText();
		
		if (superClassCombo.getText().equals(""))
		{
			setErrorMessage("Super class can not be empty.");
			return false;
		}
		return true;
	}

	protected Text addText(Composite p_parent, String p_title, String p_text)
	{
		Label label = new Label(p_parent, SWT.NULL);
		label.setText(p_title);

		Text text = new Text(p_parent, SWT.BORDER | SWT.SINGLE);
		text.setText(p_text);
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		text.addModifyListener(this);

		new Label(p_parent, SWT.NULL);
		return text;
	}
	
	protected Text addText(Composite p_parent, String p_title)
	{
		return addText(p_parent, p_title, "");
	}

	protected Combo addCombo(Composite p_parent, String p_title)
	{
		Label label = new Label(p_parent, SWT.NULL);
		label.setText(p_title);

		Combo combo = new Combo(p_parent, SWT.BORDER | SWT.SINGLE);
		combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		combo.addModifyListener(this);

		new Label(p_parent, SWT.NULL);
		return combo;
	}

	protected Button addCheckbox(Composite p_parent, String p_title, boolean p_checked)
	{
		new Label(p_parent, SWT.NULL);

		Button checkbox = new Button(p_parent, SWT.CHECK);
		checkbox.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		checkbox.setText(p_title);
		checkbox.setSelection(p_checked);

		new Label(p_parent, SWT.NULL);
		return checkbox;
	}
	
	protected Button addCheckbox(Composite p_parent, String p_title)
	{
		return addCheckbox(p_parent, p_title, false);
	}

	//
	// Listeners
	//
	@Override
	public void modifyText(ModifyEvent e)
	{
		if (e.getSource() == namespaceCombo)
		{
			if (validateNamespace())
			{
				setErrorMessage(null);
			}
		}
		else if (e.getSource() == classNameText)
		{
			if (validateClassName())
			{
				setErrorMessage(null);
			}
		}
		else if (e.getSource() == superClassCombo)
		{
			if (validateSuperClass())
			{
				setErrorMessage(null);
			}
		}
	}

}