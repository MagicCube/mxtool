package org.magiccube.mxtool.code.gen;

public abstract class MXHtmlGenerator
{
	public String getTitle(MXClassGenOptions p_options)
	{
		return p_options.getFullClassName();
	}
	
	public StringBuilder generateCode(MXClassGenOptions p_options)
	{
		StringBuilder builder = new StringBuilder();
		writeDocType(builder, p_options);
		builder.append("<html>\n");
		
		writeHead(builder, p_options);
		writeLine(builder);
		
		writeBody(builder, p_options);
		writeLine(builder);
		
		writeScript(builder, p_options);
		
		builder.append("</html>\n");
		return builder;
	}

	
	protected void writeScript(StringBuilder builder, MXClassGenOptions p_options)
	{
		builder.append("<script>\n");
		writeScriptCode(builder, p_options);
		builder.append("</script>\n");
	}

	protected void writeScriptCode(StringBuilder builder, MXClassGenOptions p_options)
	{
		builder.append("$import(\"" + p_options.getFullClassName() + "\");\n\n");
		builder.append("mx.whenReady(function()\n");
		builder.append("{\n");
		writeMXWhenReadyScriptCode(builder, p_options);
		builder.append("});\n");
	}


	protected void writeMXWhenReadyScriptCode(StringBuilder builder, MXClassGenOptions p_options)
	{
		
	}


	protected void writeHead(StringBuilder builder, MXClassGenOptions p_options)
	{
		builder.append("<head>\n");
		writeHeadContent(builder, p_options);
		builder.append("</head>\n");
	}

	protected void writeHeadContent(StringBuilder builder, MXClassGenOptions p_options)
	{
		builder.append("<meta charset=\"utf-8\">\n");
		builder.append("<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">\n");
		builder.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n");
		builder.append("<title>").append(getTitle(p_options)).append("</title>\n");
		builder.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1, maximum-scale=1\">\n");
		writeMXFramework(builder, p_options);
	}
	
	protected void writeBody(StringBuilder builder, MXClassGenOptions p_options)
	{
		builder.append("<body>\n");
		writeBodyContent(builder, p_options);
		builder.append("</body>\n");
	}

	protected void writeBodyContent(StringBuilder builder, MXClassGenOptions p_options)
	{
		
	}


	protected void writeMXFramework(StringBuilder builder, MXClassGenOptions p_options)
	{
		builder.append("<script type=\"text/javascript\" src=\"scripts/mx/debug.js\"></script>\n");
	}

	protected void writeDocType(StringBuilder builder, MXClassGenOptions p_options)
	{
		builder.append("<!DOCTYPE html>\n");
	}
	
	protected void writeLine(StringBuilder builder)
	{
		builder.append("\n");
	}
}
