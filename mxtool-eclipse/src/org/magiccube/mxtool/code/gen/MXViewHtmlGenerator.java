package org.magiccube.mxtool.code.gen;

public class MXViewHtmlGenerator extends MXHtmlGenerator
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
		
		String instanceName = p_options.className.substring(0, 1).toLowerCase() + p_options.className.substring(1);
		builder.append("    var ").append(instanceName).append(" = new ").append(p_options.getFullClassName()).append("(\r\n");
		builder.append("    {\r\n");
		builder.append("        id: \"" + instanceName.replace("View", "") + "\"\r\n");
		builder.append("    });\r\n");
		builder.append("    $(document.body).append(").append(instanceName).append(".$element);\r\n");
	}
	
}
