package org.magiccube.mxtool.code.gen;

public class CssGenerator
{
	public StringBuilder generateCode(MXClassGenOptions p_options)
	{
		StringBuilder builder = new StringBuilder();
		builder.append("." + p_options.className + "\n");
		builder.append("{\n");
		builder.append("\n");
		builder.append("}\n");
		return builder;
	}
}
