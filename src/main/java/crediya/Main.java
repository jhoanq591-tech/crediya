package crediya;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Login login = new Login(sc);

        int opcion = -1;
        while (opcion != 0) {
            System.out.println("\n===== CREDIYA S.A.S. =====");
            System.out.println("1. Ingresar como cliente");
            System.out.println("2. Ingresar como administrador");
            System.out.println("0. Salir");
            System.out.print("Elija una opcion: ");

            try {
                opcion = Integer.parseInt(sc.nextLine());
            } catch (Exception e) {
                opcion = -1;
            }

            switch (opcion) {
                case 1:
                    int clienteId = login.ingresarCliente();
                    if (clienteId > 0) {
                        menuCliente(sc, clienteId);
                    }
                    break;
                case 2:
                    int administradorId = login.ingresarAdministrador();
                    if (administradorId > 0) {
                        menuAdministrador(sc, administradorId);
                    }
                    break;
                case 0:
                    System.out.println("Hasta pronto.");
                    break;
                default:
                    System.out.println("Opcion invalida.");
            }
        }
        sc.close();
    }

    // ---------- MENU DEL CLIENTE (usuario) ----------
    public static void menuCliente(Scanner sc, int clienteId) {
        Prestamos prestamos = new Prestamos(sc);

        int opcion = -1;
        while (opcion != 0) {
            System.out.println("\n----- MENU CLIENTE -----");
            System.out.println("1. Solicitar un prestamo");
            System.out.println("2. Ver mis prestamos");
            System.out.println("0. Cerrar sesion");
            System.out.print("Elija una opcion: ");

            try {
                opcion = Integer.parseInt(sc.nextLine());
            } catch (Exception e) {
                opcion = -1;
            }

            switch (opcion) {
                case 1:
                    prestamos.solicitarPrestamo(clienteId);
                    break;
                case 2:
                    prestamos.mostrarPrestamosDe(clienteId);
                    break;
                case 0:
                    System.out.println("Sesion cerrada.");
                    break;
                default:
                    System.out.println("Opcion invalida.");
            }
        }
    }

    // ---------- MENU DEL ADMINISTRADOR ----------
    public static void menuAdministrador(Scanner sc, int administradorId) {
        Empleados empleados = new Empleados(sc);
        Clientes clientes = new Clientes(sc);
        Prestamos prestamos = new Prestamos(sc);
        Pagos pagos = new Pagos(sc);
        Reportes reportes = new Reportes();

        int opcion = -1;
        while (opcion != 0) {
            System.out.println("\n----- MENU ADMINISTRADOR -----");
            System.out.println("1. Empleados");
            System.out.println("2. Clientes");
            System.out.println("3. Prestamos");
            System.out.println("4. Pagos");
            System.out.println("5. Reportes");
            System.out.println("0. Cerrar sesion");
            System.out.print("Elija una opcion: ");

            try {
                opcion = Integer.parseInt(sc.nextLine());
            } catch (Exception e) {
                opcion = -1;
            }

            switch (opcion) {
                case 1:
                    menuEmpleados(sc, empleados);
                    break;
                case 2:
                    menuClientes(sc, clientes);
                    break;
                case 3:
                    menuPrestamos(sc, prestamos, administradorId);
                    break;
                case 4:
                    menuPagos(sc, pagos);
                    break;
                case 5:
                    menuReportes(sc, reportes);
                    break;
                case 0:
                    System.out.println("Sesion cerrada.");
                    break;
                default:
                    System.out.println("Opcion invalida.");
            }
        }
    }

    public static void menuEmpleados(Scanner sc, Empleados empleados) {
        System.out.println("-- EMPLEADOS --\n1. Registrar\n2. Listar");
        System.out.print("Opcion: ");
        String op = sc.nextLine();
        switch (op) {
            case "1":
                empleados.registrarEmpleado();
                break;
            case "2":
                empleados.listarEmpleados();
                break;
            default:
                System.out.println("Opcion invalida.");
        }
    }

    public static void menuClientes(Scanner sc, Clientes clientes) {
        System.out.println("-- CLIENTES --\n1. Registrar\n2. Listar");
        System.out.print("Opcion: ");
        String op = sc.nextLine();
        switch (op) {
            case "1":
                clientes.registrarCliente();
                break;
            case "2":
                clientes.listarClientes();
                break;
            default:
                System.out.println("Opcion invalida.");
        }
    }

    public static void menuPrestamos(Scanner sc, Prestamos prestamos, int administradorId) {
        System.out.println("-- PRESTAMOS --");
        System.out.println("1. Ver prestamos de todos los clientes (por cliente)");
        System.out.println("2. Ver prestamos de un cliente");
        System.out.println("3. Aprobar / cancelar / dejar pendiente un prestamo");
        System.out.println("4. Crear un prestamo para un cliente");
        System.out.print("Opcion: ");
        String op = sc.nextLine();
        switch (op) {
            case "1":
                prestamos.verPrestamosPorCliente();
                break;
            case "2":
                prestamos.verPrestamosDeUnCliente();
                break;
            case "3":
                prestamos.decidirPrestamo(administradorId);
                break;
            case "4":
                prestamos.crearParaCliente();
                break;
            default:
                System.out.println("Opcion invalida.");
        }
    }

    public static void menuPagos(Scanner sc, Pagos pagos) {
        System.out.println("-- PAGOS --\n1. Registrar abono\n2. Listar pagos");
        System.out.print("Opcion: ");
        String op = sc.nextLine();
        switch (op) {
            case "1":
                pagos.registrarPago();
                break;
            case "2":
                pagos.listarPagos();
                break;
            default:
                System.out.println("Opcion invalida.");
        }
    }

    public static void menuReportes(Scanner sc, Reportes reportes) {
        System.out.println("-- REPORTES --\n1. Prestamos activos\n2. Prestamos morosos");
        System.out.print("Opcion: ");
        String op = sc.nextLine();
        switch (op) {
            case "1":
                reportes.listarPrestamosActivos();
                break;
            case "2":
                reportes.listarPrestamosMorosos();
                break;
            default:
                System.out.println("Opcion invalida.");
        }
    }
}
