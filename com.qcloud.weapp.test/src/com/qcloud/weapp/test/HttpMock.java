package com.qcloud.weapp.test;

import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mockito.Matchers;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class HttpMock {
	public HttpServletRequest request;
	public HttpServletResponse response;

	private StringWriter sw;
	private PrintWriter pw;
	public void setupResponseWriter() {
		try {
			sw = new StringWriter();
			pw = new PrintWriter(sw);
			when(response.getWriter()).thenReturn(pw);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private String responseText = null;
	public String getResponseText() {
		if (responseText == null) {
			pw.flush();
			responseText = sw.toString();
		}
		return responseText;
	}
	
	public void setRequestBody(String requestBody) {
		try {
			// @see http://blog.timmattison.com/archives/2014/12/16/mockito-and-servletinputstreams/
			ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(requestBody.getBytes("utf-8"));
			ServletInputStream mockServletInputStream = mock(ServletInputStream.class);
			when(mockServletInputStream.read(Matchers.<byte[]>any(), anyInt(), anyInt())).thenAnswer(new Answer<Integer>() {
			    @Override
			    public Integer answer(InvocationOnMock invocationOnMock) throws Throwable {
			        Object[] args = invocationOnMock.getArguments();
			        byte[] output = (byte[]) args[0];
			        int offset = (int) args[1];
			        int length = (int) args[2];
			        return byteArrayInputStream.read(output, offset, length);
			    }
			});
			when(request.getInputStream()).thenReturn(mockServletInputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}