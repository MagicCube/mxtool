package org.magiccube.mxbuild;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.magiccube.common.io.FileReader;
import org.magiccube.common.io.TextStreamWriter;

import com.google.javascript.jscomp.CheckLevel;
import com.google.javascript.jscomp.CompilationLevel;
import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.CompilerOptions;
import com.google.javascript.jscomp.CompilerOptions.LanguageMode;
import com.google.javascript.jscomp.JSError;
import com.google.javascript.jscomp.Result;
import com.google.javascript.jscomp.SourceFile;


public class MXModuleBuild
{
    private String _moduleName = null;
    private File _moduleFolder = null;
           
    public MXModuleBuild(File p_moduleFolder) throws IOException
    {
        _moduleFolder = p_moduleFolder;
        if (!_moduleFolder.exists() || !_moduleFolder.isDirectory())
        {
        	throw new FileNotFoundException(_moduleFolder.getAbsolutePath() + " not found.");
        }
        _moduleName = _moduleFolder.getName();
    }
    
    public MXModuleBuild(String p_modulePath) throws IOException
    {
        this(new File(p_modulePath));
    }

    
    
    public static Compiler getClosureCompiler()
    {
        Compiler.setLoggingLevel(Level.SEVERE);
        Compiler compiler = new Compiler();
        return compiler;
    }
    
    
    
    public File getSourceFolder()
    {
        return _moduleFolder;
    }
    
    public File getMinFolder()
    {
    	return getSourceFolder();    	
    }
    
    public File getMinJSFile()
    {
        File moduleFolder = getMinFolder();
        File minFile = new File(moduleFolder, "min.js");
        return minFile;
    }
    
    public File getMinCssFile()
    {
    	File moduleFolder = getMinFolder();
        File minFile = new File(new File(moduleFolder, "res"), "min.css");
        return minFile;
    }
    
    
    
    public MXBuildResult buildModule() throws IOException
    {
        MXBuildResult result = compileModuleJavaScript();
        MXBuildResult cssResult = compileModuleCss();
        return result.merge(cssResult);
    }
    
    public MXBuildResult compileModuleCss() throws IOException
    {
    	MXBuildResult result = new MXBuildResult(true);
    	List<File> files = _getFilesInDirectory(new File(_moduleFolder, "res"), "css");
    	String css = "";
    	for (File file : files)
        {
    		if (file.getName().equals("min.css"))
        	{
        		continue;
        	}
    		css += FileReader.readText(file);
        }
    	if (!css.trim().equals(""))
    	{
	    	File outputFile = getMinCssFile();
	        TextStreamWriter writer = new TextStreamWriter(outputFile);
	        writer.write(css);
	        writer.close();
    	}
        return result;
    }

    public MXBuildResult compileModuleJavaScript() throws IOException
    {
        List<SourceFile> asts = new ArrayList<SourceFile>();
        List<File> files = _getFilesInDirectory(_moduleFolder, "js");
        for (File file : files)
        {
        	if (_moduleName.equals("mx") && file.getName().equals("debug.js"))
        	{
        		continue;
        	}
        	if (file.getName().equals("min.js"))
        	{
        		continue;
        	}
            asts.add(SourceFile.fromFile(file, Charset.forName("UTF-8")));
        }
        Compiler compiler = getClosureCompiler();
        Result closureResult = compiler.compile(new ArrayList<SourceFile>(), asts, _getCompilerOptions());
        if (closureResult.success)
        {
            System.out.println("Successfully compiled module '" + _moduleName + "'");
            File outputFile = getMinJSFile();
            TextStreamWriter writer = new TextStreamWriter(outputFile);
            String minCode = compiler.toSource();
            writer.write(minCode);
            writer.close();
            return new MXBuildResult(true);
        }
        else
        {
        	MXBuildResult result = new MXBuildResult(false);
            for (int i = 0; i < closureResult.errors.length; i++)
            {
            	JSError jsError = closureResult.errors[i];
            	if (jsError.getDefaultLevel().equals(CheckLevel.ERROR))
            	{
	            	int pos = (int)_moduleFolder.getAbsolutePath().length() + 1;
	            	MXBuildError e = new MXBuildError(jsError.sourceName.substring(pos), jsError.lineNumber, jsError.description);
	                result.errors.add(e);
            	}
            }
            return result;
        }
    }
    
    
    
    
    
    
    private List<File> _getFilesInDirectory(File p_directory, String p_extension)
    {    	
        List<File> files = new ArrayList<File>();
        if (p_directory == null || !p_directory.exists()) return files;
        		
        File[] subfiles = p_directory.listFiles();
        
        if (p_extension.equals("js"))
        {
            File mxbuildFile = new File(p_directory, "mx.build");
            if (mxbuildFile.exists())
            {
                try
                {
                    String[] lines = FileReader.readLines(mxbuildFile);
                    for (String line : lines)
                    {
                    	if (!line.endsWith(p_extension))
                    	{
                    		continue;
                    	}
                        files.add(new File(p_directory.getAbsolutePath(), line));
                    }
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
        
        for (File subfile : subfiles)
        {
            if (subfile.isFile() && subfile.getName().endsWith("." + p_extension))
            {
                if (!files.contains(subfile))
                {
                    files.add(subfile);
                }
            }
            else if (subfile.isDirectory())
            {
            	if (subfile.getName().matches("^[a-z0-9]+$"))
            	{
            		files.addAll(_getFilesInDirectory(subfile, p_extension));
            	}
            }
        }
        return files;
    }
    

    private static CompilerOptions _getCompilerOptions()
    {
        CompilerOptions compilerOptions = new CompilerOptions();
        compilerOptions.setLanguageIn(LanguageMode.ECMASCRIPT5);
        CompilationLevel.SIMPLE_OPTIMIZATIONS.setOptionsForCompilationLevel(compilerOptions);
        return compilerOptions;
    }
    
    public static void main(String[] args)
    {
        try
        {
        	String path = new File(args[0]).getAbsolutePath();
            MXModuleBuild build = new MXModuleBuild(path);
            build.buildModule();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
