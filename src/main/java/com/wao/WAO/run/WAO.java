package com.wao.WAO.run;

import org.openxava.util.*;

/**
 * Ejecuta esta clase para arrancar la aplicación.
 */

public class WAO {

	public static void main(String[] args) throws Exception {
		DBServer.start("WAO-db"); // Para usar tu propia base de datos comenta esta línea y configura src/main/webapp/META-INF/context.xml
		AppServer.run("WAO"); // Usa AppServer.run("") para funcionar en el contexto raíz
	}

}
