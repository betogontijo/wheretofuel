package com.example.demo;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;

import org.gnu.glpk.GLPK;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import br.com.betogontijo.nativeutils.NativeUtils;

public class GlpkImplementation {

	public GlpkImplementation() {
		PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
		String prefix = "/static/glpk-4.63/w64/";
		try {
			for (Resource resource : resolver.getResources("classpath*:" + prefix + "*")) {
				try {
					String library = prefix + resource.getFilename();
					if (library.endsWith(".dll")) {
						NativeUtils.loadLibraryFromJar(library);
					}
				} catch (IOException | NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// try {
		// String path =
		// getClass().getResource("/static/glpk-4.63/w64/").getPath().replaceAll("/",
		// "\\\\");
		// path = path.startsWith("\\") ? path.substring(1) : path;
		// System.setProperty("java.library.path", path + ";" +
		// System.getProperty("java.library.path"));
		// addLibraryPath(path);
		// } catch (Exception e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// String pathToAdd =
		// context.getResource("classpath:static/libglpk-java-1.10.0/swig/.libs/").getURI().toString();
		// System.out.println(pathToAdd);
		// try {
		// addLibraryPath(pathToAdd);
		// } catch (Exception e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
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
