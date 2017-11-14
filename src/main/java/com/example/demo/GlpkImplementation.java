package com.example.demo;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Arrays;

import org.gnu.glpk.GLPK;

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

	public void addLibraryPath(String pathToAdd) throws Exception {
		final Field usrPathsField = ClassLoader.class.getDeclaredField("usr_paths");
		usrPathsField.setAccessible(true);

		// get array of paths
		final String[] paths = (String[]) usrPathsField.get(null);

		// check if the path to add is already present
		for (String path : paths) {
			if (path.equals(pathToAdd)) {
				return;
			}
		}

		// add the new path
		final String[] newPaths = Arrays.copyOf(paths, paths.length + 1);
		newPaths[newPaths.length - 1] = pathToAdd;
		usrPathsField.set(null, newPaths);
		File file = new File(pathToAdd);
		for (String string : file.list()) {
			if (string.endsWith(".dll")) {
				System.loadLibrary(string.replace(".dll", ""));
			}
		}
	}
}
