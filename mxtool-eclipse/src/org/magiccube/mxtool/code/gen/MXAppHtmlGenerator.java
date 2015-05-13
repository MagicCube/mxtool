package org.magiccube.mxtool.code.gen;

public class MXAppHtmlGenerator extends MXHtmlGenerator
{

	@Override
	public String getTitle(MXClassGenOptions p_options)
	{
		return p_options.properties.get("appDisplayName").toString();
	}

	@Override
	protected void writeMXWhenReadyScriptCode(StringBuilder builder, MXClassGenOptions p_options)
	{
		super.writeMXWhenReadyScriptCode(builder, p_options);
		
		String instanceName = p_options.namespace + ".app";
		builder.append("    ").append(instanceName).append(" = new ").append(p_options.getFullClassName()).append("();\n");
		builder.append("    // Or you can try this if you want to customize the root element of the app\n");
		builder.append("    // ").append(instanceName).append(" = new ").append(p_options.getFullClassName()).append("({ $element: $(\"body\") });\n\n");
		builder.append("    var args = { };\n");
		builder.append("    ").append(instanceName).append(".run(args);\n");
	}
	
}
