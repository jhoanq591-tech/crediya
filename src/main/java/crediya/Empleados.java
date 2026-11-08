package crediya;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

public class Empleados {

    private Scanner sc;
    private Conexion conexion = new Conexion();

    public Empleados(Scanner sc) {
        this.sc = sc;
    }

    public void registrarEmpleado() {
        System.out.print("Nombre: ");
        String nombre = sc.nextLine();
        System.out.print("Documento: ");
        String documento = sc.nextLine();
        System.out.print("Rol (Administrador / Asesor / Cobrador): ");
        String rol = sc.nextLine();
        System.out.print("Correo: ");
        String correo = sc.nextLine();
        System.out.print("Salario: ");
        double salario = Double.parseDouble(leerNumero());
        System.out.print("Clave: ");
        String clave = sc.nextLine();

        // Validaciones basicas
        if (nombre.isEmpty() || documento.isEmpty() || rol.isEmpty() || clave.isEmpty()) {
            System.out.println("Nombre, documento, rol y clave no pueden estar vacios.");
        } else if (salario <= 0) {
            System.out.println("El salario debe ser mayor a cero.");
        } else {
            String sql = "INSERT INTO empleados (nombre, documento, rol, correo, salario, clave) VALUES (?, ?, ?, ?, ?, ?)";
            // try-with-resources cierra la conexion y el statement solos
            try (Connection con = conexion.conectar();
                 PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, nombre);
                ps.setString(2, documento);
                ps.setString(3, rol);
                ps.setString(4, correo);
                ps.setDouble(5, salario);
                ps.setString(6, clave);
                ps.executeUpdate();
                System.out.println("Empleado registrado correctamente.");
            } catch (Exception e) {
                System.out.println("Error al registrar empleado: " + e.getMessage());
            }
        }
    }

    public void listarEmpleados() {
        String sql = "SELECT id, nombre, documento, rol, correo, salario FROM empleados";
        try (Connection con = conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            System.out.println("--- LISTA DE EMPLEADOS ---");
            while (rs.next()) {
                System.out.println(rs.getInt("id") + " | " + rs.getString("nombre")
                        + " | " + rs.getString("documento") + " | " + rs.getString("rol")
                        + " | " + rs.getString("correo") + " | $" + rs.getDouble("salario"));
            }
        } catch (Exception e) {
            System.out.println("Error al listar empleados: " + e.getMessage());
        }
    }

    // Lee una linea y si no es un numero valido devuelve "0"
    private String leerNumero() {
        String texto = sc.nextLine();
        try {
            Double.parseDouble(texto);
            return texto;
        } catch (Exception e) {
            return "0";
        }
    }
}
