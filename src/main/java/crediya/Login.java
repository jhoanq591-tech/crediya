package crediya;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

/**
 * Pide documento y clave y los revisa en la base de datos.
 * Si son correctos devuelve el id de la persona, si no devuelve 0.
 */
public class Login {

    private Scanner sc;
    private Conexion conexion = new Conexion();

    public Login(Scanner sc) {
        this.sc = sc;
    }

    // Usuario = cliente
    public int ingresarCliente() {
        String sql = "SELECT id, nombre FROM clientes WHERE documento = ? AND clave = ?";
        return buscar(sql);
    }

    // Administrador = empleado con rol 'Administrador'
    public int ingresarAdministrador() {
        String sql = "SELECT id, nombre FROM empleados WHERE documento = ? AND clave = ? AND rol = 'Administrador'";
        return buscar(sql);
    }

    private int buscar(String sql) {
        System.out.print("Documento: ");
        String documento = sc.nextLine();
        System.out.print("Clave: ");
        String clave = sc.nextLine();

        try (Connection con = conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, documento);
            ps.setString(2, clave);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                System.out.println("Bienvenido, " + rs.getString("nombre") + ".");
                return rs.getInt("id");
            }
            System.out.println("Documento o clave incorrectos.");
        } catch (Exception e) {
            System.out.println("Error al iniciar sesion: " + e.getMessage());
        }
        return 0;
    }
}
