package com.example.demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WebServiceController {

	GlpkImplementation glpkImplementation = new GlpkImplementation();

	// retrieve index.html from project resources
	@CrossOrigin
	@RequestMapping(value = "/WhereToFuel", method = RequestMethod.GET)
	public String whereToFuelIndex() throws IOException {
		String index = "";
		InputStream is = getClass().getClassLoader().getResourceAsStream("static/WhereToFuel/index.html");
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String line = null;
		while ((line = br.readLine()) != null) {
			index += line;
		}
		return index;
	}

	// execute simplex for prodution mix
	@CrossOrigin
	@RequestMapping(value = "/glpk", method = RequestMethod.POST)
	public String glpk(@RequestParam(value = "table") String table, @RequestParam(value = "type") String type,
			@RequestParam(value = "const") String cnst) {
		try {
			String[] split = table.replace("[", "").split("],");
			List<List<Double>> matrix = new ArrayList<List<Double>>();
			for (String string : split) {
				matrix.add(Arrays.asList(
						Arrays.stream(string.replace("]", "").split(",")).map(Double::valueOf).toArray(Double[]::new)));
			}
			List<Double> restrictions = new ArrayList<Double>();
			for (int i = 1; i < matrix.get(0).size(); i++) {
				restrictions.add(0d);
				restrictions.add(Double.MAX_VALUE);
			}
			return glpkImplementation.solve(matrix, type, restrictions, Double.valueOf(cnst));
		} catch (Exception e) {
			e.printStackTrace();
			return e.getMessage();
		}
	}

	// Just for testing purpose
	@CrossOrigin
	@RequestMapping(value = "/glpk/get", method = RequestMethod.GET)
	public String glpkGet(@RequestParam(value = "table") String table, @RequestParam(value = "type") String type,
			@RequestParam(value = "const") String cnst) {
		return glpk(table, type, cnst);
	}

	// Glpk checker
	@CrossOrigin
	@RequestMapping(value = "/glpk/version", method = RequestMethod.GET)
	public String glpkGet() {
		return glpkImplementation.getVersion();
	}

	// This is from the last work //

	@CrossOrigin
	@RequestMapping(value = "/Simplex", method = RequestMethod.GET)
	public String simplexIndex() throws IOException {
		String index = "";
		InputStream is = getClass().getClassLoader().getResourceAsStream("static/Simplex/index.html");
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		String line = null;
		while ((line = br.readLine()) != null) {
			index += line;
		}
		return index;
	}

	@CrossOrigin
	@RequestMapping(value = "/WebService", method = RequestMethod.POST)
	public String simplex(@RequestParam(value = "table") String table, @RequestParam(value = "type") String type) {
		return glpk(table, type, "0");
	}
}
