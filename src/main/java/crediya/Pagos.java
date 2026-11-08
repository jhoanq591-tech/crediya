package crediya;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

public class Pagos {

    private Scanner sc;
    private Conexion conexion = new Conexion();

    public Pagos(Scanner sc) {
        this.sc = sc;
    }

    public void registrarPago() {
        int prestamoId = leerEntero("ID del prestamo: ");
        double abono = leerDecimal("Monto del abono: ");

        if (prestamoId <= 0) {
            System.out.println("El ID debe ser mayor a cero.");
        } else if (abono <= 0) {
            System.out.println("El abono debe ser mayor a cero.");
        } else {
            try (Connection con = conexion.conectar()) {
                // 1. Buscar el prestamo
                PreparedStatement psPrestamo = con.prepareStatement(
                        "SELECT monto, interes, estado FROM prestamos WHERE id = ?");
                psPrestamo.setInt(1, prestamoId);
                ResultSet rsPrestamo = psPrestamo.executeQuery();

                if (!rsPrestamo.next()) {
                    System.out.println("No existe un prestamo con ese ID.");
                } else if (rsPrestamo.getString("estado").equals("pagado")) {
                    System.out.println("Ese prestamo ya esta pagado.");
                } else if (!rsPrestamo.getString("estado").equals("aprobado")) {
                    System.out.println("Solo se pueden abonar prestamos aprobados (este esta "
                            + rsPrestamo.getString("estado") + ").");
                } else {
                    double total = rsPrestamo.getDouble("monto")
                            + (rsPrestamo.getDouble("monto") * rsPrestamo.getDouble("interes") / 100);

                    // 2. Cuanto se ha pagado hasta ahora
                    PreparedStatement psSuma = con.prepareStatement(
                            "SELECT COALESCE(SUM(monto), 0) AS pagado FROM pagos WHERE prestamo_id = ?");
                    psSuma.setInt(1, prestamoId);
                    ResultSet rsSuma = psSuma.executeQuery();
                    rsSuma.next();
                    double pagado = rsSuma.getDouble("pagado");
                    double saldo = total - pagado;

                    if (abono > saldo) {
                        System.out.println("El abono supera el saldo pendiente ($" + saldo + ").");
                    } else {
                        // 3. Guardar el abono
                        PreparedStatement psInsert = con.prepareStatement(
                                "INSERT INTO pagos (prestamo_id, fecha_pago, monto) VALUES (?, CURDATE(), ?)");
                        psInsert.setInt(1, prestamoId);
                        psInsert.setDouble(2, abono);
                        psInsert.executeUpdate();

                        double nuevoSaldo = saldo - abono;
                        System.out.println("Abono registrado. Saldo pendiente: $" + nuevoSaldo);

                        // 4. Si ya no debe nada, el prestamo queda pagado
                        if (nuevoSaldo <= 0.009) {
                            PreparedStatement psEstado = con.prepareStatement(
                                    "UPDATE prestamos SET estado = 'pagado' WHERE id = ?");
                            psEstado.setInt(1, prestamoId);
                            psEstado.executeUpdate();
                            System.out.println("El prestamo quedo PAGADO.");
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("Error al registrar pago: " + e.getMessage());
            }
        }
    }

    public void listarPagos() {
        int prestamoId = leerEntero("ID del prestamo (0 para ver todos): ");
        String sql;
        if (prestamoId > 0) {
            sql = "SELECT id, prestamo_id, fecha_pago, monto FROM pagos WHERE prestamo_id = ?";
        } else {
            sql = "SELECT id, prestamo_id, fecha_pago, monto FROM pagos";
        }

        try (Connection con = conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            if (prestamoId > 0) {
                ps.setInt(1, prestamoId);
            }
            ResultSet rs = ps.executeQuery();
            System.out.println("--- LISTA DE PAGOS ---");
            while (rs.next()) {
                System.out.println(rs.getInt("id") + " | prestamo " + rs.getInt("prestamo_id")
                        + " | " + rs.getDate("fecha_pago") + " | $" + rs.getDouble("monto"));
            }
        } catch (Exception e) {
            System.out.println("Error al listar pagos: " + e.getMessage());
        }
    }

    private int leerEntero(String mensaje) {
        System.out.print(mensaje);
        try {
            return Integer.parseInt(sc.nextLine());
        } catch (Exception e) {
            return 0;
        }
    }

    private double leerDecimal(String mensaje) {
        System.out.print(mensaje);
        try {
            return Double.parseDouble(sc.nextLine());
        } catch (Exception e) {
            return 0;
        }
    }
}
