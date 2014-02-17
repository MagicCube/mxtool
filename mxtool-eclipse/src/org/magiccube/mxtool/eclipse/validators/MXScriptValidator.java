package org.magiccube.mxtool.eclipse.validators;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.magiccube.mxtool.eclipse.resource.MXProjectResource;

public class MXScriptValidator
{
	private static Pattern _importPattern = Pattern.compile("\\$import\\(\"([^\"]*)\"\\)");
	private static Pattern _extendPattern = Pattern.compile("=\\s*\\$extend\\(([^\\)]*)\\)");
	private static Pattern _nsPattern = Pattern.compile("\\$ns\\(\"([^\")]*)\"\\)");
	private static Pattern _includePattern = Pattern.compile("\\$include\\(\"([^\")]*)\"\\)");
	private static Pattern _classDeclarationPattern = Pattern.compile("([a-z0-9\\.]+\\.[A-Z][a-zA-Z0-9]+)\\s*=\\s*function\\(\\)");
	private static Pattern _classNameDeclarationPattern = Pattern.compile("([a-z0-9\\.]+\\.[A-Z][a-zA-Z0-9]+).className\\s*=\\s*\\\"([a-z0-9\\.]+\\.[A-Z][a-zA-Z0-9]+)\\\"");


	private MXProjectResource _projectResource = null;

	public MXScriptValidator(MXProjectResource p_projectResource)
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
					if (!ns.equals(namespace))
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
				
				if (!_projectResource.hasResource(res))
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
				
				if (!_projectResource.hasClass(className))
				{
					return new ValidationResult(false, "Class '" + className  + "' is not found.");
				}
			}
		}
		
		matcher = _classDeclarationPattern.matcher(p_line);
		if (matcher.find())
		{
			if (matcher.groupCount() >= 1)
			{
				boolean endsWithClass = false;
				String className = matcher.group(1);
				if (className.endsWith("Class"))
				{
					endsWithClass = true;
					className = className.substring(0, className.length() - "Class".length());
				}
				if (!className.startsWith("me."))
				{
					String supposedName = _projectResource.getClassNameOfFile(p_file);
					if (!supposedName.equals(className))
					{
						if (endsWithClass && (className.endsWith(".New") || className.endsWith(".My") || className.endsWith(".MyNew")))
						{
							// Warning
						}
						else
						{
							return new ValidationResult(false, "The class name does not match the file name or path. The class name must be '" + supposedName + (endsWithClass ? "Class" : "") + "'.");
						}
					}
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
				
				if (!_projectResource.hasClass(className))
				{
					return new ValidationResult(false, "The super class '" + className  + "' is not found.");
				}
			}
		}
		
		
		
		
		matcher = _classNameDeclarationPattern.matcher(p_line);
		if (matcher.find())
		{
			if (matcher.groupCount() >= 2)
			{
				String supposedName = _projectResource.getClassNameOfFile(p_file);
				String className = matcher.group(1);
				boolean endsWithClass = false;
				if (className.endsWith("Class"))
				{
					endsWithClass = true;
					className = className.substring(0, className.length() - "Class".length());
				}
				if (!supposedName.equals(className))
				{
					if (endsWithClass && (className.endsWith(".New") || className.endsWith(".My") || className.endsWith(".MyNew")))
					{
						// Warning
					}
					else
					{
						return new ValidationResult(false, "The class name does not match the file name or path. The class name must be '" + supposedName + (endsWithClass ? "Class" : "") + "'.");
					}
				}
				
				className = matcher.group(2);
				if (endsWithClass)
				{
					className = className.substring(0, className.length() - "Class".length());
				}
				if (!supposedName.equals(className))
				{
					if (endsWithClass && (className.endsWith(".New") || className.endsWith(".My") || className.endsWith(".MyNew")))
					{
						// Warning
					}
					else
					{
						return new ValidationResult(false, "The class name does not match the file name or path. The class name must be '" + supposedName + (endsWithClass ? "Class" : "") + "'.");
					}
				}
			}
		}
				
		return new ValidationResult(true);
	}
}
