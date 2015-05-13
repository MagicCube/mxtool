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
		if (p_options.properties.get("sceneTitle") != null)
		{
			builder.append("    me.title = \"" + p_options.properties.get("sceneTitle") + "\";\n");
		}
		if (p_options.genCss)
		{
			builder.append("    me.autoFillParent = " + p_options.properties.get("autoFillParent") + ";\n");
		}
	}
	
	@Override
	protected void writeBody(StringBuilder builder, MXClassGenOptions p_options)
	{
		super.writeBody(builder, p_options);
		
		if ((Boolean)p_options.properties.get("overrideActivate"))
		{
			builder.append("    base.activate = me.activate;\n");
			builder.append("    me.activate = function(args, isPoppedBack)\n");
			builder.append("    {\n");
			builder.append("        base.activate(args, isPoppedBack);\n\n");
			builder.append("        if (!isPoppedBack)\n");
			builder.append("        {\n");
			builder.append("            // TODO the scene is activated normally.\n");
			builder.append("            console.log(\"").append(p_options.getFullClassName()).append(" is now activated.\");\n");
			builder.append("        }\n");
			builder.append("        else\n");
			builder.append("        {\n");
			builder.append("            // TODO the scene is activated when popped back after the user pressed 'Back' button.\n");
			builder.append("        }\n");
			builder.append("    };\n");
		}
	}
}
