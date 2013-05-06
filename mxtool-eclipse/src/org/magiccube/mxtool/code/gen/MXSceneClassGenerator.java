package org.magiccube.mxtool.code.gen;

public class MXSceneClassGenerator extends MXClassGenerator
{

	public MXSceneClassGenerator()
	{
		
	}
	
	
	@Override
	protected void writeVarMe(StringBuilder builder, MXClassGenOptions p_options)
	{
		super.writeVarMe(builder, p_options);
		if (p_options.genCss)
		{
			if (p_options.properties.get("sceneTitle") != null)
			{
				builder.append("    me.title = \"" + p_options.properties.get("sceneTitle") + "\";\r\n");
			}
			builder.append("    me.autoFillParent = " + p_options.properties.get("autoFillParent") + ";\r\n");
		}
	}
	
	@Override
	protected void writeBody(StringBuilder builder, MXClassGenOptions p_options)
	{
		super.writeBody(builder, p_options);
		
		if ((Boolean)p_options.properties.get("overrideActivate"))
		{
			builder.append("    base.activate = me.activate;\r\n");
			builder.append("    me.activate = function(args, isBack)\r\n");
			builder.append("    {\r\n");
			builder.append("        base.activate(args, isPoppedBack);\r\n\r\n");
			builder.append("        if (!isPoppedBack)\r\n");
			builder.append("        {\r\n");
			builder.append("            // TODO the scene is activated normally.\r\n");
			builder.append("        }\r\n");
			builder.append("        else\r\n");
			builder.append("        {\r\n");
			builder.append("            // TODO the scene is activated when popped back after the user pressed 'Back' button.\r\n");
			builder.append("        }\r\n");
			builder.append("    };\r\n");
		}
	}
}
