package crediya;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Clase sencilla para conectarse a MySQL / MariaDB.
 *
 * Si tu MySQL tiene otro usuario o contrasena, cambia estas 3 lineas:
 */
public class Conexion {

    private static final String URL = "jdbc:mysql://localhost:3306/crediya_db";
    private static final String USUARIO = "root";
    private static final String CLAVE = "";

    public Connection conectar() throws Exception {
        // Si existen las variables de entorno DB_URL, DB_USER y DB_PASSWORD, tienen prioridad
        String url = System.getenv("DB_URL");
        String usuario = System.getenv("DB_USER");
        String clave = System.getenv("DB_PASSWORD");

        if (url == null) {
            url = URL;
        }
        if (usuario == null) {
            usuario = USUARIO;
        }
        if (clave == null) {
            clave = CLAVE;
        }

        try {
            return DriverManager.getConnection(url, usuario, clave);
        } catch (Exception e) {
            throw new Exception("No se pudo conectar a la base de datos. "
                    + "Revise que MySQL este encendido, que exista crediya_db y que el usuario y la clave "
                    + "de Conexion.java sean correctos. (" + e.getMessage() + ")");
        }
    }
}
