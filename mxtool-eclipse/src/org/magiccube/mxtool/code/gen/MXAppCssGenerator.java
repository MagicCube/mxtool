package org.magiccube.mxtool.code.gen;

public class MXAppCssGenerator extends CssGenerator
{
	public StringBuilder generateCode(MXClassGenOptions p_options)
	{
		StringBuilder builder = new StringBuilder();		
		builder.append("*\r\n");
		builder.append("{\r\n");
		builder.append("    box-sizing: border-box;\r\n");
		builder.append("    font-family: 'Microsoft Yahei', Verdana, 'Helvetica Neue', 'BBAlpha Sans', 'S60 Sans', Arial, sans-serif;\r\n");
		builder.append("    font-size: 88%;\r\n");
		builder.append("    -webkit-font-smoothing: antialiased;\r\n");
		builder.append("	-webkit-text-size-adjust: none;\r\n");
		builder.append("}\r\n\r\n");
		
		builder.append("html, body\r\n");
		builder.append("{\r\n");
		builder.append("    width: 100%;\r\n");
		builder.append("    height: 100%;\r\n");
		builder.append("    margin: 0;\r\n");
		builder.append("    padding: 0;\r\n");
		builder.append("}\r\n\r\n");

		builder.append("body\r\n");
		builder.append("{\r\n");
		builder.append("    overflow: hidden;\r\n");
		builder.append("}\r\n\r\n");
		
		builder.append("a\r\n");
		builder.append("{\r\n");
		builder.append("    color: blue;\r\n");
		builder.append("}\r\n\r\n");

		builder.append("img\r\n");
		builder.append("{\r\n");
		builder.append("    border: none;\r\n");
		builder.append("}\r\n\r\n");
		
		builder.append(".mx-app\r\n");
		builder.append("{\r\n");
		builder.append("    \r\n");
		builder.append("}\r\n\r\n");
		
		return builder;
	}
}
