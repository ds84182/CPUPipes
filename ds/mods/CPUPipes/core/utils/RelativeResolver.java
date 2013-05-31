package ds.mods.CPUPipes.core.utils;

import java.util.ArrayList;

public class RelativeResolver {

			public static String resolve(String path) {
						if (path.startsWith("/")) {
									path = path.substring(1);
						}
						String[] pathComponents = path.split("/");
						ArrayList<String> newPathComponents = new ArrayList<String>();
						for (String p : pathComponents) {
									if (p.equals(".")) {
									} else if (p.equals("..")) {
												if (newPathComponents.size() > 0) {
															newPathComponents.remove(newPathComponents.size() - 1);
												}
									} else {
												newPathComponents.add(p);
									}
						}
						String newString = "";
						for (String p : newPathComponents) {
									newString += "/" + p;
						}
						return newString;
			}
}
