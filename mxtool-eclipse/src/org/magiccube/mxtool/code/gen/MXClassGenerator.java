package org.magiccube.mxtool.code.gen;

public class MXClassGenerator
{
	public StringBuilder generateCode(MXClassGenOptions p_options)
	{
		StringBuilder builder = new StringBuilder();
		writeNamespace(builder, p_options);
		writeLine(builder);
		writeImports(builder, p_options);
		writeLine(builder);
		writeBeginOfClass(builder, p_options);
		writeVarMe(builder, p_options);
		writeVarBase(builder, p_options);
		writeLine(builder);
		if (p_options.superClassType.equals("MXObject"))
		{
			overrideConstructor(builder, p_options);
		}
		else
		{
			overrideInit(builder, p_options);
		}
		writeLine(builder);
		writeBody(builder, p_options);
		writeLine(builder);
		writeEndOfClass(builder, p_options);
		if (p_options.isSingleton)
		{
			writeLine(builder);
			writeSingleton(builder, p_options);
		}
		return builder;
	}

	

	

	

	protected void writeLine(StringBuilder builder)
	{
		builder.append("\n");
	}
	
	protected void writeNamespace(StringBuilder builder, MXClassGenOptions p_options)
	{
		builder.append("$ns(\"").append(p_options.namespace).append("\");\n");
	}
	
	protected void writeImports(StringBuilder builder, MXClassGenOptions p_options)
	{
		if (!p_options.importedClasses.contains(p_options.superClass))
		{
			builder.append("$import(\"").append(p_options.superClass).append("\");\n");
			writeLine(builder);
		}
	}
	
	
	protected void writeBeginOfClass(StringBuilder builder, MXClassGenOptions p_options)
	{
		builder.append(p_options.namespace).append(".").append(p_options.className);
		if (p_options.isSingleton)
		{
			builder.append("Class");
		}
		builder.append(" = function()\n");
		builder.append("{\n");
	}

	protected void writeEndOfClass(StringBuilder builder, MXClassGenOptions p_options)
	{
		builder.append("    return me.endOfClass(arguments);\n");
		builder.append("};\n");
		builder.append(p_options.getFullClassName() + ".className = \"" + p_options.getFullClassName() + "\";\n");
	}

	

	
	
	

	
	
	protected void writeVarMe(StringBuilder builder, MXClassGenOptions p_options)
	{
		builder.append("    var me = $extend(").append(p_options.superClass).append(");\n");
	}

	protected void writeVarBase(StringBuilder builder, MXClassGenOptions p_options)
	{
		builder.append("    var base = {};\n");
	}
	
	
	

	
	protected void overrideConstructor(StringBuilder builder, MXClassGenOptions p_options)
	{
		builder.append("    base._ = me._;\n");
		builder.append("    me._ = function(p_options)\n");
		builder.append("    {\n");
		builder.append("        if (me.canConstruct())\n");
		builder.append("        {\n");
		builder.append("            base._(p_options);\n\n");
		builder.append("            // TODO add your own construction code here.\n");
		builder.append("        }\n");
		builder.append("    };\n");
	}

	protected void overrideInit(StringBuilder builder, MXClassGenOptions p_options)
	{
		builder.append("    base.init = me.init;\n");
		builder.append("    me.init = function(p_options)\n");
		builder.append("    {\n");
		builder.append("        base.init(p_options);\n\n");
		builder.append("        // TODO add your own initializing code here.\n");
		builder.append("    };\n");
	}
	
	
	
	protected void writeBody(StringBuilder builder, MXClassGenOptions p_options)
	{
	
	}
	
	protected void writeSingleton(StringBuilder builder, MXClassGenOptions p_options)
	{
		builder.append(p_options.namespace).append(".").append(p_options.className).append(" = new ").append(p_options.namespace).append(".").append(p_options.className).append("Class();\n");
	}
}
