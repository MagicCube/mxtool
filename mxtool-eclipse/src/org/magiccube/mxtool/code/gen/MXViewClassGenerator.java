package org.magiccube.mxtool.code.gen;

public class MXViewClassGenerator extends MXClassGenerator
{

	public MXViewClassGenerator()
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
			builder.append("$include(\"" + cssFile + "\");\r\n");
		}
	}

	@Override
	protected void writeVarMe(StringBuilder builder, MXClassGenOptions p_options)
	{
		super.writeVarMe(builder, p_options);
		if (p_options.genCss)
		{
			builder.append("    me.elementClass = \"" + p_options.className + "\";\r\n");
		}
	}
}
