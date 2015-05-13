package org.magiccube.mxtool.code.gen;

public class MXSceneHtmlGenerator extends MXHtmlGenerator
{

	@Override
	public String getTitle(MXClassGenOptions p_options)
	{
		return p_options.properties.get("sceneTitle").toString();
	}

	@Override
	protected void writeMXWhenReadyScriptCode(StringBuilder builder, MXClassGenOptions p_options)
	{
		super.writeMXWhenReadyScriptCode(builder, p_options);
		
		String instanceName = p_options.className.substring(0, 1).toLowerCase() + p_options.className.substring(1);
		builder.append("    var ").append(instanceName).append(" = new ").append(p_options.getFullClassName()).append("({\n");
		builder.append("        id: \"" + instanceName.replace("Scene", "") + "\"\n");
		builder.append("    });\n");
		builder.append("    $(document.body).append(").append(instanceName).append(".$element);\n");
		builder.append("    \n");
		builder.append("    var args = { };\n");
		builder.append("    ").append(instanceName).append(".activate(args, false);\n");
	}
	
}
