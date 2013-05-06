package org.magiccube.mxtool.eclipse.wizards;

import java.util.regex.Pattern;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.IStructuredSelection;
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

public class NewMXClassWizardPage extends WizardPage implements ModifyListener
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

	public void init(MXClassGenOptions p_options)
	{
		applyOptions(p_options);
	}

	protected IStructuredSelection getSelection()
	{
		return ((NewMXClassWizardPage) getWizard()).getSelection();
	}

	protected IProject getProject()
	{
		return ((NewMXClassWizardPage) getWizard()).getProject();
	}

	protected MXProjectProperties getProjectProperties()
	{
		return ((NewMXClassWizardPage) getWizard()).getProjectProperties();
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
			_options = p_options;

			setTitle("New " + _options.superClassType + " Class");
			setDescription("This wizard creates a new " + _options.superClassType + " subclass with MXFramework.");

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

	protected boolean validate()
	{
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
		return true;
	}

	protected Pattern namespacePattern = Pattern.compile("^[a-z][a-z0-9\\.]*[a-z0-9]$");

	protected boolean validateNamespace()
	{
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
		_options.namespace = namespaceCombo.getText();
		return true;
	}

	protected Pattern classNamePattern = Pattern.compile("^[A-Z][a-zA-Z0-9]+$");

	protected boolean validateClassName()
	{
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
		_options.className = classNameText.getText();
		return true;
	}

	protected boolean validateSuperClass()
	{
		if (superClassCombo.getText().equals(""))
		{
			setErrorMessage("Super class can not be empty.");
			return false;
		}
		_options.superClass = superClassCombo.getText();
		return true;
	}

	private Text _appendText(Composite p_parent, String p_title)
	{
		Label label = new Label(p_parent, SWT.NULL);
		label.setText(p_title);

		Text text = new Text(p_parent, SWT.BORDER | SWT.SINGLE);
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		text.addModifyListener(this);

		new Label(p_parent, SWT.NULL);
		return text;
	}

	private Combo _appendCombo(Composite p_parent, String p_title)
	{
		Label label = new Label(p_parent, SWT.NULL);
		label.setText(p_title);

		Combo combo = new Combo(p_parent, SWT.BORDER | SWT.SINGLE);
		combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		combo.addModifyListener(this);

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