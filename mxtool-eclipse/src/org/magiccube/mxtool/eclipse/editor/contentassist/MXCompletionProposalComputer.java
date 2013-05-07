package org.magiccube.mxtool.eclipse.editor.contentassist;

import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.jsdt.core.IJavaScriptProject;
import org.eclipse.wst.jsdt.core.IType;
import org.eclipse.wst.jsdt.core.JavaScriptModelException;
import org.eclipse.wst.jsdt.ui.text.java.ContentAssistInvocationContext;
import org.eclipse.wst.jsdt.ui.text.java.IJavaCompletionProposalComputer;
import org.eclipse.wst.jsdt.ui.text.java.JavaContentAssistInvocationContext;

@SuppressWarnings("rawtypes")
public class MXCompletionProposalComputer implements IJavaCompletionProposalComputer
{

	public MXCompletionProposalComputer()
	{

	}

	@Override
	public List computeCompletionProposals(ContentAssistInvocationContext p_context, IProgressMonitor p_monitor)
	{
		JavaContentAssistInvocationContext context = (JavaContentAssistInvocationContext)p_context;
		
		IJavaScriptProject jsProject = context.getProject();
		IProject project = jsProject.getProject();
		
		int start = context.getCoreContext().getTokenStart();
		int end = context.getCoreContext().getTokenEnd();
		
		try
		{
			IType[] types = context.getCompilationUnit().getTypes();
			for (IType type : types)
			{
				System.out.println(type.getElementName());
			}
		}
		catch (JavaScriptModelException e)
		{
			e.printStackTrace();
		}
		
		int offset = context.getInvocationOffset();
		
		return Collections.emptyList();
	}

	@Override
	public List computeContextInformation(ContentAssistInvocationContext p_context, IProgressMonitor p_monitor)
	{
		return Collections.emptyList();
	}

	@Override
	public String getErrorMessage()
	{
		return "";
	}

	@Override
	public void sessionStarted()
	{

	}

	@Override
	public void sessionEnded()
	{

	}

}
