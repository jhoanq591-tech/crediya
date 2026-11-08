package crediya;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

public class Clientes {

    private Scanner sc;
    private Conexion conexion = new Conexion();

    public Clientes(Scanner sc) {
        this.sc = sc;
    }

    public void registrarCliente() {
        System.out.print("Nombre: ");
        String nombre = sc.nextLine();
        System.out.print("Documento: ");
        String documento = sc.nextLine();
        System.out.print("Correo: ");
        String correo = sc.nextLine();
        System.out.print("Telefono: ");
        String telefono = sc.nextLine();
        System.out.print("Clave: ");
        String clave = sc.nextLine();
        System.out.print("Ingresos mensuales: ");
        double ingresos = leerDecimal();

        if (nombre.isEmpty() || documento.isEmpty() || clave.isEmpty()) {
            System.out.println("Nombre, documento y clave no pueden estar vacios.");
        } else if (ingresos <= 0) {
            System.out.println("Los ingresos deben ser mayores a cero.");
        } else {
            String sql = "INSERT INTO clientes (nombre, documento, correo, telefono, clave, ingresos) VALUES (?, ?, ?, ?, ?, ?)";
            try (Connection con = conexion.conectar();
                 PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, nombre);
                ps.setString(2, documento);
                ps.setString(3, correo);
                ps.setString(4, telefono);
                ps.setString(5, clave);
                ps.setDouble(6, ingresos);
                ps.executeUpdate();
                System.out.println("Cliente registrado correctamente.");
            } catch (Exception e) {
                System.out.println("Error al registrar cliente: " + e.getMessage());
            }
        }
    }

    public void listarClientes() {
        String sql = "SELECT id, nombre, documento, correo, telefono, ingresos FROM clientes";
        try (Connection con = conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            System.out.println("--- LISTA DE CLIENTES ---");
            while (rs.next()) {
                System.out.println(rs.getInt("id") + " | " + rs.getString("nombre")
                        + " | " + rs.getString("documento") + " | " + rs.getString("correo")
                        + " | " + rs.getString("telefono") + " | ingresos $" + rs.getDouble("ingresos"));
            }
        } catch (Exception e) {
            System.out.println("Error al listar clientes: " + e.getMessage());
        }
    }

    // Lee una linea y si no es un numero valido devuelve 0
    private double leerDecimal() {
        try {
            return Double.parseDouble(sc.nextLine());
        } catch (Exception e) {
            return 0;
        }
    }
}
