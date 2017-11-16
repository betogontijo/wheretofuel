package com.example.demo;

import java.util.List;

import org.gnu.glpk.GLPK;
import org.gnu.glpk.GLPKConstants;
import org.gnu.glpk.SWIGTYPE_p_double;
import org.gnu.glpk.SWIGTYPE_p_int;
import org.gnu.glpk.glp_prob;
import org.gnu.glpk.glp_smcp;

import br.com.betogontijo.nativeutils.GlpkLoader;

public class GlpkImplementation {

	public GlpkImplementation() {
		GlpkLoader.load();
	}

	public void runSimplex() {
		System.out.println(GLPK.glp_version());
	}

	public String getVersion() {
		return GLPK.glp_version();
	}

	public String solve(List<List<Double>> table, String type, List<Double> restric, Double constant) {
		glp_prob lp;
		glp_smcp parm;
		SWIGTYPE_p_int ind;
		SWIGTYPE_p_double val;
		int ret;
		String resp = new String();
		// Create problem
		lp = GLPK.glp_create_prob();
		System.out.println("Problem created");
		GLPK.glp_set_prob_name(lp, "myProblem");
		// Define columns
		int columnsSize = table.get(0).size() - 1;
		GLPK.glp_add_cols(lp, columnsSize);
		for (int i = 0; i < columnsSize * 2; i += 2) {
			int index = ((i / 2) + 1);
			GLPK.glp_set_col_name(lp, index, "x" + index);
			GLPK.glp_set_col_bnds(lp, index, GLPKConstants.GLP_DB, restric.get(i), restric.get(i + 1));
		}

		// Create constraints

		// Allocate memory
		ind = GLPK.new_intArray(table.size());
		val = GLPK.new_doubleArray(table.size());

		// Create rows
		GLPK.glp_add_rows(lp, table.size() - 1);

		// Set row details
		for (int i = 1; i < table.size(); i++) {
			GLPK.glp_set_row_name(lp, i, "c" + i);
			GLPK.glp_set_row_bnds(lp, i, GLPKConstants.GLP_UP, 0, table.get(i).get(0));
			for (int j = 1; j < table.get(i).size(); j++) {
				GLPK.intArray_setitem(ind, j, j);
				GLPK.doubleArray_setitem(val, j, table.get(i).get(j));
			}
			GLPK.glp_set_mat_row(lp, i, table.get(i).size() - 1, ind, val);
		}
		// GLPK.glp_set_row_name(lp, 1, "c1");
		// // GLPK.glp_set_row_bnds(lp, 1, GLPKConstants.GLP_DB, 0, 0.2);
		// GLPK.intArray_setitem(ind, 1, 1);
		// GLPK.intArray_setitem(ind, 2, 2);
		// GLPK.doubleArray_setitem(val, 1, 1.);
		// GLPK.doubleArray_setitem(val, 2, -.5);
		// GLPK.glp_set_mat_row(lp, 1, 2, ind, val);
		//
		// GLPK.glp_set_row_name(lp, 2, "c2");
		// GLPK.glp_set_row_bnds(lp, 2, GLPKConstants.GLP_UP, 0, 0.4);
		// GLPK.intArray_setitem(ind, 1, 2);
		// GLPK.intArray_setitem(ind, 2, 3);
		// GLPK.doubleArray_setitem(val, 1, -1.);
		// GLPK.doubleArray_setitem(val, 2, 1.);
		// GLPK.glp_set_mat_row(lp, 2, 2, ind, val);

		// Free memory
		GLPK.delete_intArray(ind);
		GLPK.delete_doubleArray(val);

		// Define objective
		GLPK.glp_set_obj_name(lp, "z");
		if (type.toUpperCase().equals("MAX")) {
			GLPK.glp_set_obj_dir(lp, GLPKConstants.GLP_MAX);
		} else if (type.toUpperCase().equals("MIN")) {
			GLPK.glp_set_obj_dir(lp, GLPKConstants.GLP_MIN);
		} else {
			return "Type must be MAX or MIN.";
		}
		// GLPK.glp_set_obj_coef(lp, 0, 1.);
		List<Double> list = table.get(0);
		GLPK.glp_set_obj_coef(lp, 0, constant);
		for (int i = 1; i < list.size(); i++) {
			GLPK.glp_set_obj_coef(lp, i, list.get(i));
		}

		// Write model to file
		GLPK.glp_write_lp(lp, null, "lp.lp");

		// Solve model
		parm = new glp_smcp();
		GLPK.glp_init_smcp(parm);
		ret = GLPK.glp_simplex(lp, parm);

		// Retrieve solution
		if (ret == 0) {
			resp = write_lp_solution(lp);
		} else {
			return "The problem could not be solved";
		}

		// Free memory
		GLPK.glp_delete_prob(lp);
		return resp;
	}

	static String write_lp_solution(glp_prob lp) {
		int i;
		int n;
		String name;
		double val;
		String resp = "{";
		name = GLPK.glp_get_obj_name(lp);
		val = GLPK.glp_get_obj_val(lp);
		resp += "\"" + name + "\":" + val + ",";
		n = GLPK.glp_get_num_cols(lp);
		for (i = 1; i <= n; i++) {
			name = GLPK.glp_get_col_name(lp, i);
			val = GLPK.glp_get_col_prim(lp, i);
			resp += "\"" + name + "\":" + val + ",";
		}
		resp = resp.substring(0, resp.length() - 1) + "}";
		return resp;
	}
}
