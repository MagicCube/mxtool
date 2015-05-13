package org.magiccube.mxtool.code.gen;

public class MXAppClassGenerator extends MXClassGenerator
{

	public MXAppClassGenerator()
	{

	}

	@Override
	protected void writeImports(StringBuilder builder, MXClassGenOptions p_options)
	{
		super.writeImports(builder, p_options);

		if (p_options.genCss)
		{
			String ns = null;
			int pos = p_options.namespace.indexOf(".");
			if (pos == -1)
			{
				ns = p_options.namespace + ".res.";
			}
			else
			{
				ns = p_options.namespace.substring(0, pos) + ".res.";
			}
			String cssFile = ns + p_options.className + ".css";
			builder.append("$include(\"" + cssFile + "\");\n");
		}
	}

	@Override
	protected void writeVarMe(StringBuilder builder, MXClassGenOptions p_options)
	{
		super.writeVarMe(builder, p_options);
		String appId = p_options.getFullClassName().replaceAll("\\.", "-").toLowerCase();
		if (appId.endsWith("-app-application"))
		{
			appId = appId.replace("-app-application", "-app");
		}
		else if (appId.endsWith("-app-app"))
		{
			appId = appId.replace("-app-app", "-app");
		}
		
		builder.append("    me.appId = \"").append(appId).append("\";\n");
		if (p_options.properties.get("appDisplayName") != null && p_options.properties.get("appDisplayName").toString().trim() != "")
		{
			builder.append("    me.appDisplayName = \"" + p_options.properties.get("appDisplayName") + "\";\n");
		}
	}

	@Override
	protected void writeBody(StringBuilder builder, MXClassGenOptions p_options)
	{
		super.writeBody(builder, p_options);

		builder.append("    base.run = me.run;\n");
		builder.append("    me.run = function(args)\n");
		builder.append("    {\n");
		builder.append("        // TODO add your application starting logic here.\n");
		builder.append("        console.log(\"").append(p_options.getFullClassName()).append(" is now running.\");\n");
		builder.append("    \n");
		builder.append("    };\n");
	}
}
