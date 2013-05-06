package org.magiccube.mxtool.code.gen;

public class CssGenerator
{
	public StringBuilder generateCode(MXClassGenOptions p_options)
	{
		StringBuilder builder = new StringBuilder();
		builder.append("." + p_options.className + "\r\n");
		builder.append("{\r\n");
		builder.append("\r\n");
		builder.append("}\r\n");
		return builder;
	}
}
