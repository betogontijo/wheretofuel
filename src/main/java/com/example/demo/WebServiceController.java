package com.example.demo;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WebServiceController {

	@Autowired
	ServletContext servletContext;

	@RequestMapping(value = "/glpk", method = RequestMethod.GET)
	public String version() {
		String result = new String();
		try {
			GlpkImplementation glpk = new GlpkImplementation();
			result = glpk.getVersion();
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			result = sw.toString();
		}
		return result;
	}
}
