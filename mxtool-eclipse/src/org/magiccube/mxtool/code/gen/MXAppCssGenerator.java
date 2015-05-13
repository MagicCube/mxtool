package org.magiccube.mxtool.code.gen;

public class MXAppCssGenerator extends CssGenerator
{
	public StringBuilder generateCode(MXClassGenOptions p_options)
	{
		StringBuilder builder = new StringBuilder();		
		builder.append("*\n");
		builder.append("{\n");
		builder.append("    box-sizing: border-box;\n");
		builder.append("    font-family: 'Microsoft Yahei', Verdana, 'Helvetica Neue', 'BBAlpha Sans', 'S60 Sans', Arial, sans-serif;\n");
		builder.append("}\n\n");
		
		builder.append("html, body\n");
		builder.append("{\n");
		builder.append("    width: 100%;\n");
		builder.append("    margin: 0;\n");
		builder.append("    padding: 0;\n");
		builder.append("}\n\n");

		builder.append("body\n");
		builder.append("{\n");
		builder.append("    font-size: 14px;");
		builder.append("}\n\n");
		
		builder.append("a\n");
		builder.append("{\n");
		builder.append("    color: #337ab7;\n");
		builder.append("    text-decoration: none;\n");
		builder.append("}\n");
		builder.append("a:hover\n");
		builder.append("{\n");
		builder.append("    text-decoration: underline;\n");
		builder.append("}\n\n");

		builder.append("img\n");
		builder.append("{\n");
		builder.append("    border: none;\n");
		builder.append("}\n\n");
		
		builder.append(".mx-app\n");
		builder.append("{\n");
		builder.append("    \n");
		builder.append("}\n\n");
		
		return builder;
	}
}
