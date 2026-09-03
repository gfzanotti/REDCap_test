package mi.istitutotumori.app;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class VersionUtil {
	private static final Properties props = new Properties();

	static {
		try (InputStream is = VersionUtil.class
				.getResourceAsStream("/META-INF/maven/mi.istitutotumori/e_crf/pom.properties")) {
			if (is != null) {
				props.load(is);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String getVersion() {
		return props.getProperty("version", "dev");
	}

}
