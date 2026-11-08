package crediya;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Scanner;

public class Prestamos {

    // Un cliente no puede pedir mas de 5 veces sus ingresos
    private static final int TOPE_INGRESOS = 5;
    // Interes total (%) que cobra CrediYa en todos los prestamos
    private static final double INTERES = 5;

    private Scanner sc;
    private Conexion conexion = new Conexion();

    public Prestamos(Scanner sc) {
        this.sc = sc;
    }

    // El administrador elige para que cliente crear el prestamo
    public void crearParaCliente() {
        int clienteId = leerEntero("ID del cliente: ");
        solicitarPrestamo(clienteId);
    }

    // Un prestamo nuevo siempre queda 'pendiente' hasta que el administrador decida
    public void solicitarPrestamo(int clienteId) {
        double monto = leerDecimal("Monto del prestamo: ");
        int cuotas = leerEntero("Numero de cuotas: ");

        if (clienteId <= 0) {
            System.out.println("El ID del cliente debe ser mayor a cero.");
        } else if (monto <= 0) {
            System.out.println("El monto debe ser mayor a cero.");
        } else if (cuotas <= 0) {
            System.out.println("Las cuotas deben ser mayores a cero.");
        } else {
            try (Connection con = conexion.conectar()) {
                // 1. Buscar los ingresos del cliente
                PreparedStatement psCliente = con.prepareStatement("SELECT ingresos FROM clientes WHERE id = ?");
                psCliente.setInt(1, clienteId);
                ResultSet rsCliente = psCliente.executeQuery();

                if (!rsCliente.next()) {
                    System.out.println("No existe un cliente con ese ID.");
                } else {
                    double maximo = rsCliente.getDouble("ingresos") * TOPE_INGRESOS;
                    if (monto > maximo) {
                        System.out.println("El monto supera lo permitido segun sus ingresos (maximo $" + (long) maximo + ").");
                    } else {
                        // 2. Guardar el prestamo como pendiente
                        double total = monto + (monto * INTERES / 100);
                        PreparedStatement ps = con.prepareStatement(
                                "INSERT INTO prestamos (cliente_id, monto, interes, cuotas, fecha_inicio, estado) "
                                + "VALUES (?, ?, ?, ?, CURDATE(), 'pendiente')");
                        ps.setInt(1, clienteId);
                        ps.setDouble(2, monto);
                        ps.setDouble(3, INTERES);
                        ps.setInt(4, cuotas);
                        ps.executeUpdate();
                        System.out.println("Prestamo solicitado. Queda PENDIENTE hasta que lo revise el administrador.");
                        System.out.println("Total a pagar: $" + total);
                        System.out.println("Valor de cada cuota: $" + (total / cuotas));
                    }
                }
            } catch (Exception e) {
                System.out.println("Error al solicitar prestamo: " + e.getMessage());
            }
        }
    }

    // El administrador aprueba, cancela o deja pendiente un prestamo
    public void decidirPrestamo(int administradorId) {
        int id = leerEntero("ID del prestamo: ");

        if (id <= 0) {
            System.out.println("El ID debe ser mayor a cero.");
        } else {
            try (Connection con = conexion.conectar()) {
                PreparedStatement psBuscar = con.prepareStatement("SELECT estado FROM prestamos WHERE id = ?");
                psBuscar.setInt(1, id);
                ResultSet rs = psBuscar.executeQuery();

                if (!rs.next()) {
                    System.out.println("No existe un prestamo con ese ID.");
                } else if (rs.getString("estado").equals("pagado")) {
                    System.out.println("Ese prestamo ya esta pagado, no se puede cambiar.");
                } else {
                    System.out.println("Estado actual: " + rs.getString("estado"));
                    System.out.println("1. Aprobar\n2. Cancelar\n3. Dejar pendiente");
                    int opcion = leerEntero("Opcion: ");

                    String nuevoEstado = "";
                    if (opcion == 1) {
                        nuevoEstado = "aprobado";
                    } else if (opcion == 2) {
                        nuevoEstado = "cancelado";
                    } else if (opcion == 3) {
                        nuevoEstado = "pendiente";
                    }

                    if (nuevoEstado.isEmpty()) {
                        System.out.println("Opcion invalida.");
                    } else {
                        // Al aprobar, la fecha de inicio es hoy
                        String sql = "UPDATE prestamos SET estado = ?, empleado_id = ? WHERE id = ?";
                        if (nuevoEstado.equals("aprobado")) {
                            sql = "UPDATE prestamos SET estado = ?, empleado_id = ?, fecha_inicio = CURDATE() WHERE id = ?";
                        }
                        PreparedStatement ps = con.prepareStatement(sql);
                        ps.setString(1, nuevoEstado);
                        ps.setInt(2, administradorId);
                        ps.setInt(3, id);
                        ps.executeUpdate();
                        System.out.println("El prestamo " + id + " quedo " + nuevoEstado.toUpperCase() + ".");
                    }
                }
            } catch (Exception e) {
                System.out.println("Error al cambiar estado: " + e.getMessage());
            }
        }
    }

    // Administrador: los prestamos de todos los clientes, separados por cliente
    public void verPrestamosPorCliente() {
        ArrayList<Integer> ids = new ArrayList<>();
        try (Connection con = conexion.conectar();
             PreparedStatement ps = con.prepareStatement("SELECT id FROM clientes ORDER BY id");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ids.add(rs.getInt("id"));
            }
        } catch (Exception e) {
            System.out.println("Error al listar clientes: " + e.getMessage());
        }

        for (int id : ids) {
            mostrarPrestamosDe(id);
        }
    }

    // Administrador: pide el ID de un cliente y muestra solo sus prestamos
    public void verPrestamosDeUnCliente() {
        int clienteId = leerEntero("ID del cliente: ");
        mostrarPrestamosDe(clienteId);
    }

    // Muestra los datos de un cliente y todos sus prestamos (la usa tambien el cliente)
    public void mostrarPrestamosDe(int clienteId) {
        String sqlCliente = "SELECT nombre, documento, ingresos FROM clientes WHERE id = ?";
        String sqlPrestamos = "SELECT p.id, p.monto, p.interes, p.cuotas, p.fecha_inicio, p.estado, "
                + "(SELECT COALESCE(SUM(g.monto), 0) FROM pagos g WHERE g.prestamo_id = p.id) AS pagado "
                + "FROM prestamos p WHERE p.cliente_id = ? ORDER BY p.id";

        try (Connection con = conexion.conectar()) {
            PreparedStatement psCliente = con.prepareStatement(sqlCliente);
            psCliente.setInt(1, clienteId);
            ResultSet rsCliente = psCliente.executeQuery();

            if (!rsCliente.next()) {
                System.out.println("No existe un cliente con ese ID.");
            } else {
                System.out.println("\n=== " + rsCliente.getString("nombre") + " | documento "
                        + rsCliente.getString("documento") + " | ingresos $" + rsCliente.getDouble("ingresos") + " ===");

                PreparedStatement ps = con.prepareStatement(sqlPrestamos);
                ps.setInt(1, clienteId);
                ResultSet rs = ps.executeQuery();

                boolean hay = false;
                while (rs.next()) {
                    hay = true;
                    double monto = rs.getDouble("monto");
                    double total = monto + (monto * rs.getDouble("interes") / 100);
                    double pagado = rs.getDouble("pagado");
                    System.out.println("Prestamo " + rs.getInt("id") + " | " + rs.getString("estado").toUpperCase()
                            + " | $" + monto + " | " + rs.getInt("cuotas") + " cuotas | desde " + rs.getDate("fecha_inicio")
                            + " | total $" + total + " | pagado $" + pagado + " | saldo $" + (total - pagado));
                }
                if (!hay) {
                    System.out.println("(sin prestamos)");
                }
            }
        } catch (Exception e) {
            System.out.println("Error al mostrar prestamos: " + e.getMessage());
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
