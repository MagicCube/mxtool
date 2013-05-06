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

	@Override
	protected void writeVarMe(StringBuilder builder, MXClassGenOptions p_options)
	{
		super.writeVarMe(builder, p_options);
		
	}
}
