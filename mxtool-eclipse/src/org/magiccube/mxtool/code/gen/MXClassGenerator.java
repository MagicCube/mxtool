package org.magiccube.mxtool.code.gen;

public class MXClassGenerator
{
	public MXClassGenerator()
	{
		
	}
	
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
		builder.append("\r\n");
	}
	
	protected void writeNamespace(StringBuilder builder, MXClassGenOptions p_options)
	{
		builder.append("$ns(\"").append(p_options.namespace).append("\");\r\n");
	}
	
	protected void writeImports(StringBuilder builder, MXClassGenOptions p_options)
	{
		if (!p_options.importedClasses.contains(p_options.superClass))
		{
			builder.append("$import(\"").append(p_options.superClass).append("\");\r\n");
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
		builder.append(" = function()\r\n");
		builder.append("{\r\n");
	}

	protected void writeEndOfClass(StringBuilder builder, MXClassGenOptions p_options)
	{
		builder.append("    return me.endOfClass(arguments);\r\n");
		builder.append("};\r\n");
	}

	

	
	
	

	
	
	protected void writeVarMe(StringBuilder builder, MXClassGenOptions p_options)
	{
		builder.append("    var me = $extend(").append(p_options.superClass).append(");\r\n");
	}

	protected void writeVarBase(StringBuilder builder, MXClassGenOptions p_options)
	{
		builder.append("    var base = {};\r\n");
	}
	
	
	

	
	protected void overrideConstructor(StringBuilder builder, MXClassGenOptions p_options)
	{
		builder.append("    base._ = me._;\r\n");
		builder.append("    me._ = function(p_options)\r\n");
		builder.append("    {\r\n");
		builder.append("        if (me.canConstruct())\r\n");
		builder.append("        {\r\n");
		builder.append("            base._(p_options);\r\n\r\n");
		builder.append("            // TODO add your own construction code here.\r\n");
		builder.append("        }\r\n");
		builder.append("    };\r\n");
	}

	protected void overrideInit(StringBuilder builder, MXClassGenOptions p_options)
	{
		builder.append("    base.init = me.init;\r\n");
		builder.append("    me.init = function(p_options)\r\n");
		builder.append("    {\r\n");
		builder.append("        base.init(p_options);\r\n\r\n");
		builder.append("        // TODO add your own initializing code here.\r\n");
		builder.append("    };\r\n");
	}
	
	
	
	protected void writeBody(StringBuilder builder, MXClassGenOptions p_options)
	{
	
	}
	
	protected void writeSingleton(StringBuilder builder, MXClassGenOptions p_options)
	{
		builder.append(p_options.namespace).append(".").append(p_options.className).append(" = new ").append(p_options.namespace).append(".").append(p_options.className).append("Class();\r\n");
	}
}
