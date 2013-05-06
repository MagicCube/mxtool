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
		builder.append("<html>\r\n");
		
		writeHead(builder, p_options);
		writeLine(builder);
		
		writeBody(builder, p_options);
		writeLine(builder);
		
		writeScript(builder, p_options);
		
		builder.append("</html>\r\n");
		return builder;
	}

	
	protected void writeScript(StringBuilder builder, MXClassGenOptions p_options)
	{
		builder.append("<script>\r\n");
		writeScriptCode(builder, p_options);
		builder.append("</script>\r\n");
	}

	protected void writeScriptCode(StringBuilder builder, MXClassGenOptions p_options)
	{
		builder.append("$import(\"" + p_options.getFullClassName() + "\");\r\n\r\n");
		builder.append("mx.whenReady(function()\r\n");
		builder.append("{\r\n");
		writeMXWhenReadyScriptCode(builder, p_options);
		builder.append("});\r\n");
	}


	protected void writeMXWhenReadyScriptCode(StringBuilder builder, MXClassGenOptions p_options)
	{
		
	}


	protected void writeHead(StringBuilder builder, MXClassGenOptions p_options)
	{
		builder.append("<head>\r\n");
		writeHeadContent(builder, p_options);
		builder.append("</head>\r\n");
	}

	protected void writeHeadContent(StringBuilder builder, MXClassGenOptions p_options)
	{
		builder.append("<title>").append(getTitle(p_options)).append("</title>\r\n");
		builder.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />\r\n");
		writeMXFramework(builder, p_options);
	}
	
	protected void writeBody(StringBuilder builder, MXClassGenOptions p_options)
	{
		builder.append("<body>\r\n");
		writeBodyContent(builder, p_options);
		builder.append("</body>\r\n");
	}

	protected void writeBodyContent(StringBuilder builder, MXClassGenOptions p_options)
	{
		
	}


	protected void writeMXFramework(StringBuilder builder, MXClassGenOptions p_options)
	{
		builder.append("<script type=\"text/javascript\" src=\"scripts/mx/debug.js\"></script>\r\n");
	}

	protected void writeDocType(StringBuilder builder, MXClassGenOptions p_options)
	{
		builder.append("<!DOCTYPE html>\r\n");
	}
	
	protected void writeLine(StringBuilder builder)
	{
		builder.append("\r\n");
	}
}
