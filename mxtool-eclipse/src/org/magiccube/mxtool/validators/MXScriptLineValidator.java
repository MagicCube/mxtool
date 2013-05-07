package org.magiccube.mxtool.validators;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.magiccube.mxtool.eclipse.resource.MXProjectResource;

public class MXScriptLineValidator
{
	private static Pattern _importPattern = Pattern.compile("\\$import\\(\"([^\"]*)\"\\)");
	private static Pattern _extendPattern = Pattern.compile("=\\s*\\$extend\\(([^\\)]*)\\)");
	private static Pattern _nsPattern = Pattern.compile("\\$ns\\(\"([^\")]*)\"\\)");
	private static Pattern _includePattern = Pattern.compile("\\$include\\(\"([^\")]*)\"\\)");

	private MXProjectResource _projectResource = null;

	public MXScriptLineValidator(MXProjectResource p_projectResource)
	{
		_projectResource = p_projectResource;
	}

	public ValidationResult validate(IFile p_file, String p_line)
	{
		Matcher matcher = _nsPattern.matcher(p_line);
		if (matcher.find())
		{
			if (matcher.groupCount() >= 1)
			{
				String namespace = matcher.group(1);
				if (_projectResource.hasNamespace(namespace))
				{
					String ns = _projectResource.getNamespaceOfFile(p_file);
					if (ns.equals(namespace))
					{
						return new ValidationResult(true);
					}
					else
					{
						return new ValidationResult(false, "The namespace of this class should be '" + ns  + "'.");
					}
				}
				else
				{
					return new ValidationResult(false, "Namespace '" + namespace  + "' is not found.");
				}
			}
		}
		
		
		matcher = _includePattern.matcher(p_line);
		if (matcher.find())
		{
			if (matcher.groupCount() >= 1)
			{
				String res = matcher.group(1);
				if (res.equals(""))
				{
					return new ValidationResult(false, "The resource name can not be empty.");
				}
				
				if (_projectResource.hasResource(res))
				{
					return new ValidationResult(true);
				}
				else
				{
					return new ValidationResult(false, "The resource file '" + res  + "' is not found.");
				}
			}
		}
		
		
		matcher = _importPattern.matcher(p_line);
		if (matcher.find())
		{
			if (matcher.groupCount() >= 1)
			{
				String className = matcher.group(1);
				if (className.equals(""))
				{
					return new ValidationResult(false, "The class name can not be empty.");
				}
				
				if (_projectResource.hasClass(className))
				{
					return new ValidationResult(true);
				}
				else
				{
					return new ValidationResult(false, "Class '" + className  + "' is not found.");
				}
			}
		}
		
		matcher = _extendPattern.matcher(p_line);
		if (matcher.find())
		{
			if (matcher.groupCount() >= 1)
			{
				String className = matcher.group(1);
				if (className.equals(""))
				{
					return new ValidationResult(false, "The class name can not be empty.");
				}
				
				if (_projectResource.hasClass(className))
				{
					return new ValidationResult(true);
				}
				else
				{
					return new ValidationResult(false, "The super class '" + className  + "' is not found.");
				}
			}
		}
		
		return new ValidationResult(true);
	}
}
