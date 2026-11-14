package crediya;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Reportes {

    private Conexion conexion = new Conexion();

    // Prestamos activos = los 'aprobado' que todavia se estan pagando
    public void listarPrestamosActivos() {
        String sql = "SELECT p.id, c.nombre AS cliente, p.monto, p.fecha_inicio "
                + "FROM prestamos p JOIN clientes c ON p.cliente_id = c.id "
                + "WHERE p.estado = 'aprobado'";
        try (Connection con = conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            System.out.println("--- PRESTAMOS ACTIVOS ---");
            while (rs.next()) {
                System.out.println("Prestamo " + rs.getInt("id") + " | " + rs.getString("cliente")
                        + " | $" + rs.getDouble("monto") + " | desde " + rs.getDate("fecha_inicio"));
            }
        } catch (Exception e) {
            System.out.println("Error en el reporte: " + e.getMessage());
        }
    }

    // Morosos = aprobados que empezaron hace mas de 30 dias
    public void listarPrestamosMorosos() {
        String sql = "SELECT p.id, c.nombre AS cliente, p.monto, p.fecha_inicio "
                + "FROM prestamos p JOIN clientes c ON p.cliente_id = c.id "
                + "WHERE p.estado = 'aprobado' AND p.fecha_inicio < DATE_SUB(CURDATE(), INTERVAL 30 DAY)";
        try (Connection con = conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            System.out.println("--- PRESTAMOS Vencidos (aprobados con mas de 30 dias) ---");
            while (rs.next()) {
                System.out.println("Prestamo " + rs.getInt("id") + " | " + rs.getString("cliente")
                        + " | $" + rs.getDouble("monto") + " | desde " + rs.getDate("fecha_inicio"));
            }
        } catch (Exception e) {
            System.out.println("Error en el reporte: " + e.getMessage());
        }
    }
}
