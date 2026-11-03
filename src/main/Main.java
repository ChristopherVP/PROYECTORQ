// Main.java
package main;

import control.ControladorConductores;
import excepciones.FachadaException;
import fachadas.FachadaArchivos;
import interfaces.IFachada;
import java.util.List;
import java.util.Scanner;
import objetosNegocio.*;
import objetosServicio.Fecha;
import objetosServicio.Periodo;

/**
 * Clase principal del Sistema de Gestión de Transportes Interprovinciales
 * Proporciona una interfaz de consola completa para gestionar el sistema
 *
 * @author Sistema de Gestión de Buses (Equipo7)
 */
public class Main {
    private static IFachada fachada;
    private static Scanner scanner;

    public static void main(String[] args) {
        inicializarSistema();
        generarDatosPrueba();
        mostrarMenuPrincipal();
        finalizarSistema();
    }

    /**
     * Inicializa el sistema
     */
    private static void inicializarSistema() {
        try {
            fachada = new FachadaArchivos();
            scanner = new Scanner(System.in);

            System.out.println("================================================");
            System.out.println("   SISTEMA DE GESTIÓN DE TRANSPORTES v2.0      ");
            System.out.println("================================================");
            System.out.println("Sistema inicializado correctamente ✓");
            System.out.println("Presione ENTER para continuar...");
            scanner.nextLine();

        } catch (Exception e) {
            System.err.println("Error al inicializar el sistema: " + e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Muestra el menú principal
     */
    private static void mostrarMenuPrincipal() {
        boolean salir = false;

        while (!salir) {
            limpiarPantalla();
            System.out.println("╔══════════════════════════════════════════════════════╗");
            System.out.println("║           MENÚ PRINCIPAL - SISTEMA DE BUSES         ║");
            System.out.println("╠══════════════════════════════════════════════════════╣");
            System.out.println("║  1. 🚌  GESTIÓN DE BUSES                            ║");
            System.out.println("║  2. 🛣️   GESTIÓN DE RUTAS                           ║");
            System.out.println("║  3. 👤  GESTIÓN DE USUARIOS                         ║");
            System.out.println("║  4. 🏢  GESTIÓN DE EMPRESAS                         ║");
            System.out.println("║  5. ✈️   GESTIÓN DE VIAJES                          ║");
            System.out.println("║  6. 📅  GESTIÓN DE RESERVAS                         ║");
            System.out.println("║  7. 🧾  GESTIÓN DE FACTURAS                         ║");
            System.out.println("║  8. 💰  GESTIÓN DE PAGOS                            ║");
            System.out.println("║  9. 💺  GESTIÓN DE ASIENTOS                         ║");
            System.out.println("║ 10. 🔍  CONSULTAS ESPECIALES                        ║");
            System.out.println("║ 11. 📊  REPORTES Y ESTADÍSTICAS                     ║");
            System.out.println("║ 12. 📤  EXPORTAR DATOS                              ║");
            System.out.println("║ 13. 🆘  AYUDA DEL SISTEMA                           ║");
            System.out.println("║  0. ❌  SALIR DEL SISTEMA                           ║");
            System.out.println("╚══════════════════════════════════════════════════════╝");
            System.out.print("\nSeleccione una opción: ");

            String opcionStr = scanner.nextLine().trim();

            if (opcionStr.isEmpty()) {
                continue;
            }

            try {
                int opcion = Integer.parseInt(opcionStr);

                switch (opcion) {
                    case 1:
                        menuGestion("Bus");
                        break;
                    case 2:
                        menuGestion("Ruta");
                        break;
                    case 3:
                        menuGestion("Usuario");
                        break;
                    case 4:
                        menuGestion("Empresa");
                        break;
                    case 5:
                        menuGestion("Viaje");
                        break;
                    case 6:
                        menuGestion("Reserva");
                        break;
                    case 7:
                        menuGestion("Factura");
                        break;
                    case 8:
                        menuGestion("Pago");
                        break;
                    case 9:
                        menuGestion("Asiento");
                        break;
                    case 10:
                        menuConsultasEspeciales();
                        break;
                    case 11:
                        mostrarEstadisticas();
                        break;
                    case 12:
                        exportarDatos();
                        break;
                    case 13:
                        mostrarAyuda();
                        break;
                    case 0:
                        salir = confirmarSalida();
                        break;
                    default:
                        System.out.println("Opción no válida. Intente de nuevo.");
                        pausar();
                }
            } catch (NumberFormatException e) {
                System.out.println("Por favor, ingrese un número válido.");
                pausar();
            }
        }
    }

    /**
     * Menú genérico para gestión de entidades
     */
    private static void menuGestion(String entidad) {
        boolean volver = false;

        while (!volver) {
            limpiarPantalla();
            System.out.println("╔══════════════════════════════════════════════════════╗");
            System.out.printf ("║           GESTIÓN DE %-30s ║\n", entidad.toUpperCase() + "S");
            System.out.println("╠══════════════════════════════════════════════════════╣");
            System.out.println("║  1. AGREGAR " + entidad);
            System.out.println("║  2. BUSCAR " + entidad + " por ID");
            System.out.println("║  3. ACTUALIZAR " + entidad);
            System.out.println("║  4. ELIMINAR " + entidad);
            System.out.println("║  5. LISTAR TODOS LOS " + entidad.toUpperCase() + "S");
            System.out.println("║  6. VOLVER AL MENÚ PRINCIPAL");
            System.out.println("╚══════════════════════════════════════════════════════╝");
            System.out.print("\nSeleccione una opción: ");

            String opcionStr = scanner.nextLine().trim();

            if (opcionStr.isEmpty()) {
                continue;
            }

            try {
                int opcion = Integer.parseInt(opcionStr);

                switch (opcion) {
                    case 1:
                        agregarEntidad(entidad);
                        break;
                    case 2:
                        buscarEntidad(entidad);
                        break;
                    case 3:
                        actualizarEntidad(entidad);
                        break;
                    case 4:
                        eliminarEntidad(entidad);
                        break;
                    case 5:
                        listarEntidades(entidad);
                        break;
                    case 6:
                        volver = true;
                        break;
                    default:
                        System.out.println("Opción no válida.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Por favor, ingrese un número válido.");
            }

            if (!volver) {
                pausar();
            }
        }
    }

    /**
     * Agrega una entidad al sistema
     */
    private static void agregarEntidad(String tipo) {
        try {
            System.out.println("\n=== AGREGAR " + tipo.toUpperCase() + " ===");

            switch (tipo) {
                case "Bus":
                    agregarBus();
                    break;
                case "Ruta":
                    agregarRuta();
                    break;
                case "Usuario":
                    agregarUsuario();
                    break;
                case "Empresa":
                    agregarEmpresa();
                    break;
                case "Viaje":
                    agregarViaje();
                    break;
                case "Reserva":
                    agregarReserva();
                    break;
                case "Factura":
                    agregarFactura();
                    break;
                case "Pago":
                    agregarPago();
                    break;
                case "Asiento":
                    agregarAsiento();
                    break;
                default:
                    System.out.println("Tipo de entidad no reconocido.");
            }

            System.out.println("\n✓ " + tipo + " agregado exitosamente.");
        } catch (FachadaException e) {
            System.out.println("✗ Error al agregar " + tipo + ": " + e.getMessage());
        } catch (Exception e) {
            System.out.println("✗ Error inesperado: " + e.getMessage());
        }
    }

    /**
     * Agrega un nuevo bus
     */
    private static void agregarBus() throws FachadaException {
        Bus bus = new Bus();
        bus.setIdBus(leerString("ID del bus (ej: BUS001): "));
        bus.setPlaca(leerString("Placa: "));
        bus.setMarca(leerString("Marca: "));
        bus.setNumeroChasis(leerString("Nº Chasis: "));
        bus.setNumeroMotor(leerString("Nº Motor: "));
        bus.setAnioFabricacion(leerEntero("Año: "));
        bus.setCapacidad(leerEntero("Capacidad: "));
        bus.setEstado(leerString("Estado (Activo/Inactivo/Mantenimiento): "));

        String idEmpresa = leerString("ID de la empresa: ");
        if (!idEmpresa.isEmpty()) {
            bus.setEmpresa(new Empresa(idEmpresa));
        }

        fachada.agrega(bus);
    }

    /**
     * Agrega una nueva ruta
     */
    private static void agregarRuta() throws FachadaException {
        Ruta ruta = new Ruta();
        ruta.setIdRuta(leerString("ID de ruta (ej: RUT001): "));
        ruta.setOrigen(leerString("Origen: "));
        ruta.setDestino(leerString("Destino: "));
        ruta.setDistancia(leerDouble("Distancia (km): "));
        ruta.setPrecio(leerDouble("Precio: "));

        fachada.agrega(ruta);
    }

    /**
     * Agrega un nuevo usuario
     */
    private static void agregarUsuario() throws FachadaException {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(leerString("ID de usuario (ej: USR001): "));
        usuario.setNombre(leerString("Nombre: "));
        usuario.setEmail(leerString("Email: "));
        usuario.setTelefono(leerString("Teléfono: "));
        usuario.setTipo(leerString("Tipo (Administrador/Operador): "));
        usuario.setEstado("ACTIVO");

        String idEmpresa = leerString("ID de la empresa: ");
        if (!idEmpresa.isEmpty()) {
            usuario.setEmpresa(new Empresa(idEmpresa));
        }

        fachada.agrega(usuario);
    }

    /**
     * Agrega una nueva empresa
     */
    private static void agregarEmpresa() throws FachadaException {
        Empresa empresa = new Empresa();
        empresa.setIdEmpresa(leerString("ID de empresa (ej: EMP001): "));
        empresa.setNombre(leerString("Nombre: "));
        empresa.setDireccion(leerString("Dirección: "));
        empresa.setTelefono(leerString("Teléfono: "));
        empresa.setEmail(leerString("Email: "));

        fachada.agrega(empresa);
    }

    /**
     * Agrega un nuevo viaje
     */
    private static void agregarViaje() throws FachadaException {
        Viaje viaje = new Viaje();
        viaje.setIdViaje(leerString("ID de viaje (ej: VIA001): "));

        String idBus = leerString("ID del bus: ");
        if (!idBus.isEmpty()) {
            viaje.setBus(new Bus(idBus));
        }

        String idRuta = leerString("ID de la ruta: ");
        if (!idRuta.isEmpty()) {
            viaje.setRuta(new Ruta(idRuta));
        }

        // Fecha de salida
        System.out.println("Fecha de salida:");
        Fecha fechaSalida = new Fecha(
                leerEntero("  Día: "),
                leerEntero("  Mes: "),
                leerEntero("  Año: ")
        );
        viaje.setFechaSalida(fechaSalida);

        // Fecha de llegada
        System.out.println("Fecha de llegada:");
        Fecha fechaLlegada = new Fecha(
                leerEntero("  Día: "),
                leerEntero("  Mes: "),
                leerEntero("  Año: ")
        );
        viaje.setFechaLlegada(fechaLlegada);

        viaje.setAsientosDisponibles(leerEntero("Asientos disponibles: "));
        viaje.setEstado(leerString("Estado (Programado/En curso/Completado/Cancelado): "));

        fachada.agrega(viaje);

        // Preguntar si generar asientos automáticamente
        System.out.print("\n¿Desea generar asientos automáticamente? (S/N): ");
        String respuesta = scanner.nextLine().trim().toUpperCase();
        if (respuesta.equals("S") || respuesta.equals("SI")) {
            if (viaje.getBus() != null) {
                try {
                    Bus bus = fachada.obten(viaje.getBus());
                    if (bus != null) {
                        ((FachadaArchivos) fachada).generarAsientosParaViaje(
                                viaje.getIdViaje(),
                                bus.getCapacidad()
                        );
                        System.out.println("✓ Asientos generados automáticamente.");
                    }
                } catch (FachadaException e) {
                    System.out.println("⚠ No se pudieron generar los asientos automáticamente.");
                }
            }
        }
    }

    /**
     * Agrega una nueva reserva
     */
    private static void agregarReserva() throws FachadaException {
        Reserva reserva = new Reserva();
        reserva.setIdReserva(leerString("ID de reserva (ej: RES001): "));

        String idPasajero = leerString("ID del pasajero: ");
        if (!idPasajero.isEmpty()) {
            reserva.setPasajero(new Pasajero(idPasajero));
        }

        String idViaje = leerString("ID del viaje: ");
        if (!idViaje.isEmpty()) {
            reserva.setViaje(new Viaje(idViaje));
        }

        reserva.setCantidadAsientos(leerEntero("Cantidad de asientos: "));

        System.out.println("Fecha de reserva:");
        Fecha fechaReserva = new Fecha(
                leerEntero("  Día: "),
                leerEntero("  Mes: "),
                leerEntero("  Año: ")
        );
        reserva.setFechaReserva(fechaReserva);

        reserva.setEstado(leerString("Estado (Confirmada/Cancelada/Usada): "));

        fachada.agrega(reserva);
    }

    /**
     * Agrega una nueva factura
     */
    private static void agregarFactura() throws FachadaException {
        Factura factura = new Factura();
        factura.setIdFactura(leerString("ID de factura (ej: FAC001): "));

        String idEmpresa = leerString("ID de la empresa: ");
        if (!idEmpresa.isEmpty()) {
            factura.setEmpresa(new Empresa(idEmpresa));
        }

        String idPasajero = leerString("ID del pasajero: ");
        if (!idPasajero.isEmpty()) {
            factura.setPasajero(new Pasajero(idPasajero));
        }

        String idReserva = leerString("ID de la reserva: ");
        if (!idReserva.isEmpty()) {
            factura.setReserva(new Reserva(idReserva));
        }

        factura.setTotal(leerDouble("Total: "));

        System.out.println("Fecha de emisión:");
        Fecha fechaEmision = new Fecha(
                leerEntero("  Día: "),
                leerEntero("  Mes: "),
                leerEntero("  Año: ")
        );
        factura.setFechaEmision(fechaEmision);

        factura.setEstado(leerString("Estado (Pagada/Pendiente/Cancelada): "));

        fachada.agrega(factura);
    }

    /**
     * Agrega un nuevo pago
     */
    private static void agregarPago() throws FachadaException {
        Pago pago = new Pago();
        pago.setIdPago(leerString("ID de pago (ej: PAG001): "));

        String idUsuario = leerString("ID del usuario: ");
        if (!idUsuario.isEmpty()) {
            pago.setUsuario(new Usuario(idUsuario));
        }

        String idFactura = leerString("ID de la factura: ");
        if (!idFactura.isEmpty()) {
            pago.setFactura(new Factura(idFactura));
        }

        pago.setMonto(leerDouble("Monto: "));

        System.out.println("Fecha de pago:");
        Fecha fechaPago = new Fecha(
                leerEntero("  Día: "),
                leerEntero("  Mes: "),
                leerEntero("  Año: ")
        );
        pago.setFechaPago(fechaPago);

        pago.setMetodo(leerString("Método (Tarjeta/Efectivo/Transferencia): "));

        fachada.agrega(pago);
    }

    /**
     * Agrega un nuevo asiento
     */
    private static void agregarAsiento() throws FachadaException {
        Asiento asiento = new Asiento();
        asiento.setIdAsiento(leerString("ID del asiento: "));
        asiento.setNumeroAsiento(leerEntero("Número de asiento: "));
        asiento.setIdViaje(leerString("ID del viaje: "));
        asiento.setUbicacion(leerString("Ubicación (Ventana/Pasillo): "));
        asiento.setEstado(leerString("Estado (Disponible/Reservado/Ocupado): "));

        String idReserva = leerString("ID de reserva (opcional): ");
        if (!idReserva.trim().isEmpty()) {
            asiento.setIdReserva(idReserva);
        }

        fachada.agrega(asiento);
    }

    /**
     * Busca una entidad por ID
     */
    private static void buscarEntidad(String tipo) {
        System.out.println("\n=== BUSCAR " + tipo.toUpperCase() + " ===");
        String id = leerString("ID del " + tipo.toLowerCase() + ": ");

        try {
            Object resultado = null;

            switch (tipo) {
                case "Bus":
                    resultado = fachada.obten(new Bus(id));
                    break;
                case "Ruta":
                    resultado = fachada.obten(new Ruta(id));
                    break;
                case "Usuario":
                    resultado = fachada.obten(new Usuario(id));
                    break;
                case "Empresa":
                    resultado = fachada.obten(new Empresa(id));
                    break;
                case "Viaje":
                    resultado = fachada.obten(new Viaje(id));
                    break;
                case "Reserva":
                    resultado = fachada.obten(new Reserva(id));
                    break;
                case "Factura":
                    resultado = fachada.obten(new Factura(id));
                    break;
                case "Pago":
                    resultado = fachada.obten(new Pago(id));
                    break;
                case "Asiento":
                    Asiento asientoBusqueda = new Asiento();
                    asientoBusqueda.setIdAsiento(id);
                    resultado = fachada.obten(asientoBusqueda);
                    break;
            }

            if (resultado != null) {
                System.out.println("\n✓ " + tipo + " encontrado:");
                System.out.println("----------------------------------------");
                System.out.println(resultado.toString());
            } else {
                System.out.println("\n✗ " + tipo + " no encontrado.");
            }
        } catch (FachadaException e) {
            System.out.println("✗ Error al buscar: " + e.getMessage());
        }
    }

    /**
     * Lista todas las entidades de un tipo
     */
    private static void listarEntidades(String tipo) {
        try {
            System.out.println("\n=== LISTADO DE " + tipo.toUpperCase() + "S ===");
            System.out.println("----------------------------------------");

            List<?> lista = null;

            switch (tipo) {
                case "Bus":
                    lista = fachada.consultaBuses();
                    break;
                case "Ruta":
                    lista = fachada.consultaRutas();
                    break;
                case "Usuario":
                    lista = fachada.consultaUsuarios();
                    break;
                case "Empresa":
                    lista = fachada.consultaEmpresas();
                    break;
                case "Viaje":
                    lista = fachada.consultaViajes();
                    break;
                case "Reserva":
                    lista = fachada.consultaReservas();
                    break;
                case "Factura":
                    lista = fachada.consultaFacturas();
                    break;
                case "Pago":
                    lista = fachada.consultaPagos();
                    break;
                case "Asiento":
                    lista = fachada.consultaAsientos();
                    break;
            }

            if (lista != null && !lista.isEmpty()) {
                int contador = 1;
                for (Object obj : lista) {
                    System.out.println("[" + contador + "] " + obj.toString());
                    contador++;
                }
                System.out.println("\nTotal: " + lista.size() + " " + tipo.toLowerCase() + "(s)");
            } else {
                System.out.println("No hay " + tipo.toLowerCase() + "s registrados.");
            }
        } catch (FachadaException e) {
            System.out.println("✗ Error al listar: " + e.getMessage());
        }
    }

    /**
     * Elimina una entidad
     */
    private static void eliminarEntidad(String tipo) {
        System.out.println("\n=== ELIMINAR " + tipo.toUpperCase() + " ===");
        String id = leerString("ID del " + tipo.toLowerCase() + " a eliminar: ");

        System.out.print("¿Está seguro? (S/N): ");
        String confirmacion = scanner.nextLine().trim().toUpperCase();

        if (!confirmacion.equals("S") && !confirmacion.equals("SI")) {
            System.out.println("Operación cancelada.");
            return;
        }

        try {
            switch (tipo) {
                case "Bus":
                    fachada.elimina(new Bus(id));
                    break;
                case "Ruta":
                    fachada.elimina(new Ruta(id));
                    break;
                case "Usuario":
                    fachada.elimina(new Usuario(id));
                    break;
                case "Empresa":
                    fachada.elimina(new Empresa(id));
                    break;
                case "Viaje":
                    fachada.elimina(new Viaje(id));
                    break;
                case "Reserva":
                    fachada.elimina(new Reserva(id));
                    break;
                case "Factura":
                    fachada.elimina(new Factura(id));
                    break;
                case "Pago":
                    fachada.elimina(new Pago(id));
                    break;
                case "Asiento":
                    Asiento asientoEliminar = new Asiento();
                    asientoEliminar.setIdAsiento(id);
                    fachada.elimina(asientoEliminar);
                    break;
            }

            System.out.println("✓ " + tipo + " eliminado exitosamente.");
        } catch (FachadaException e) {
            System.out.println("✗ Error al eliminar: " + e.getMessage());
        }
    }

    /**
     * Actualiza una entidad (implementación básica)
     */
    private static void actualizarEntidad(String tipo) {
        System.out.println("\n=== ACTUALIZAR " + tipo.toUpperCase() + " ===");
        System.out.println("Funcionalidad en desarrollo...");
        System.out.println("Por ahora, puede eliminar y volver a agregar el registro.");
    }

    /**
     * Menú de consultas especiales
     */
    private static void menuConsultasEspeciales() {
        boolean volver = false;

        while (!volver) {
            limpiarPantalla();
            System.out.println("╔══════════════════════════════════════════════════════╗");
            System.out.println("║           CONSULTAS ESPECIALES - SISTEMA            ║");
            System.out.println("╠══════════════════════════════════════════════════════╣");
            System.out.println("║  1. BUSES POR ESTADO                                ║");
            System.out.println("║  2. BUSES POR EMPRESA                               ║");
            System.out.println("║  3. RUTAS POR ORIGEN                                ║");
            System.out.println("║  4. RUTAS POR DESTINO                               ║");
            System.out.println("║  5. USUARIOS POR TIPO                               ║");
            System.out.println("║  6. FACTURAS POR PASAJERO                           ║");
            System.out.println("║  7. FACTURAS POR PERÍODO                            ║");
            System.out.println("║  8. RESERVAS POR PASAJERO                           ║");
            System.out.println("║  9. RESERVAS POR VIAJE                              ║");
            System.out.println("║ 10. VIAJES POR RUTA                                 ║");
            System.out.println("║ 11. VIAJES POR PERÍODO                              ║");
            System.out.println("║ 12. PAGOS POR USUARIO                               ║");
            System.out.println("║ 13. PAGOS POR PERÍODO                               ║");
            System.out.println("║ 14. ASIENTOS POR VIAJE                              ║");
            System.out.println("║ 15. ASIENTOS POR ESTADO                             ║");
            System.out.println("║ 16. ASIENTOS DISPONIBLES POR VIAJE                  ║");
            System.out.println("║ 17. GENERAR ASIENTOS PARA VIAJE                     ║");
            System.out.println("║ 18. RESERVAR ASIENTO ESPECÍFICO                     ║");
            System.out.println("║ 19. LIBERAR ASIENTO                                 ║");
            System.out.println("║ 20. ESTADÍSTICAS DE ASIENTOS                        ║");
            System.out.println("║  0. VOLVER AL MENÚ PRINCIPAL                        ║");
            System.out.println("╚══════════════════════════════════════════════════════╝");
            System.out.print("\nSeleccione una opción: ");

            String opcionStr = scanner.nextLine().trim();

            if (opcionStr.isEmpty()) {
                continue;
            }

            try {
                int opcion = Integer.parseInt(opcionStr);

                switch (opcion) {
                    case 1:
                        consultarBusesPorEstado();
                        break;
                    case 2:
                        consultarBusesPorEmpresa();
                        break;
                    case 3:
                        consultarRutasPorOrigen();
                        break;
                    case 4:
                        consultarRutasPorDestino();
                        break;
                    case 5:
                        consultarUsuariosPorTipo();
                        break;
                    case 6:
                        consultarFacturasPorPasajero();
                        break;
                    case 7:
                        consultarFacturasPorPeriodo();
                        break;
                    case 8:
                        consultarReservasPorPasajero();
                        break;
                    case 9:
                        consultarReservasPorViaje();
                        break;
                    case 10:
                        consultarViajesPorRuta();
                        break;
                    case 11:
                        consultarViajesPorPeriodo();
                        break;
                    case 12:
                        consultarPagosPorUsuario();
                        break;
                    case 13:
                        consultarPagosPorPeriodo();
                        break;
                    case 14:
                        consultarAsientosPorViaje();
                        break;
                    case 15:
                        consultarAsientosPorEstado();
                        break;
                    case 16:
                        consultarAsientosDisponiblesPorViaje();
                        break;
                    case 17:
                        generarAsientosParaViaje();
                        break;
                    case 18:
                        reservarAsientoEspecifico();
                        break;
                    case 19:
                        liberarAsiento();
                        break;
                    case 20:
                        obtenerEstadisticasAsientos();
                        break;
                    case 0:
                        volver = true;
                        break;
                    default:
                        System.out.println("Opción no válida.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Por favor, ingrese un número válido.");
            }

            if (!volver) {
                pausar();
            }
        }
    }

    // ==================== CONSULTAS ESPECÍFICAS ====================

    private static void consultarBusesPorEstado() {
        String estado = leerString("\nEstado del bus (Activo/Inactivo/Mantenimiento): ");

        try {
            List<Bus> buses = fachada.consultaBusesPorEstado(estado);

            System.out.println("\n=== BUSES EN ESTADO: " + estado.toUpperCase() + " ===");
            if (buses.isEmpty()) {
                System.out.println("No hay buses en este estado.");
            } else {
                for (Bus bus : buses) {
                    System.out.println(bus.toString());
                }
                System.out.println("\nTotal: " + buses.size() + " bus(es)");
            }
        } catch (FachadaException e) {
            System.out.println("✗ Error en la consulta: " + e.getMessage());
        }
    }

    private static void consultarBusesPorEmpresa() {
        String idEmpresa = leerString("\nID de la empresa: ");

        try {
            List<Bus> buses = fachada.consultaBusesPorEmpresa(idEmpresa);

            System.out.println("\n=== BUSES DE LA EMPRESA: " + idEmpresa + " ===");
            if (buses.isEmpty()) {
                System.out.println("No hay buses para esta empresa.");
            } else {
                for (Bus bus : buses) {
                    System.out.println(bus.toString());
                }
                System.out.println("\nTotal: " + buses.size() + " bus(es)");
            }
        } catch (FachadaException e) {
            System.out.println("✗ Error en la consulta: " + e.getMessage());
        }
    }

    private static void consultarRutasPorOrigen() {
        String origen = leerString("\nCiudad de origen: ");

        try {
            List<Ruta> rutas = fachada.consultaRutasPorOrigen(origen);

            System.out.println("\n=== RUTAS CON ORIGEN EN: " + origen.toUpperCase() + " ===");
            if (rutas.isEmpty()) {
                System.out.println("No hay rutas con este origen.");
            } else {
                for (Ruta ruta : rutas) {
                    System.out.println(ruta.toString());
                }
                System.out.println("\nTotal: " + rutas.size() + " ruta(s)");
            }
        } catch (FachadaException e) {
            System.out.println("✗ Error en la consulta: " + e.getMessage());
        }
    }

    private static void consultarRutasPorDestino() {
        String destino = leerString("\nCiudad de destino: ");

        try {
            List<Ruta> rutas = fachada.consultaRutasPorDestino(destino);

            System.out.println("\n=== RUTAS CON DESTINO EN: " + destino.toUpperCase() + " ===");
            if (rutas.isEmpty()) {
                System.out.println("No hay rutas con este destino.");
            } else {
                for (Ruta ruta : rutas) {
                    System.out.println(ruta.toString());
                }
                System.out.println("\nTotal: " + rutas.size() + " ruta(s)");
            }
        } catch (FachadaException e) {
            System.out.println("✗ Error en la consulta: " + e.getMessage());
        }
    }

    private static void consultarUsuariosPorTipo() {
        String tipo = leerString("\nTipo de usuario (Cliente/Administrador/Conductor): ");

        try {
            List<Usuario> usuarios = fachada.consultaUsuariosPorTipo(tipo);

            System.out.println("\n=== USUARIOS TIPO: " + tipo.toUpperCase() + " ===");
            if (usuarios.isEmpty()) {
                System.out.println("No hay usuarios de este tipo.");
            } else {
                for (Usuario usuario : usuarios) {
                    System.out.println(usuario.toString());
                }
                System.out.println("\nTotal: " + usuarios.size() + " usuario(s)");
            }
        } catch (FachadaException e) {
            System.out.println("✗ Error en la consulta: " + e.getMessage());
        }
    }

    private static void consultarFacturasPorPasajero() {
        String idPasajero = leerString("\nID del pasajero: ");

        try {
            List<Factura> facturas = fachada.consultaFacturasPorPasajero(idPasajero);

            System.out.println("\n=== FACTURAS DEL PASAJERO: " + idPasajero + " ===");
            if (facturas.isEmpty()) {
                System.out.println("No hay facturas para este usuario.");
            } else {
                double total = 0;
                for (Factura factura : facturas) {
                    System.out.println(factura.toString());
                    total += factura.getTotal();
                }
                System.out.printf("\nTotal: %d factura(s) - Monto total: $%.2f\n", facturas.size(), total);
            }
        } catch (FachadaException e) {
            System.out.println("✗ Error en la consulta: " + e.getMessage());
        }
    }

    private static void consultarFacturasPorPeriodo() {
        System.out.println("\n=== FACTURAS POR PERÍODO ===");

        System.out.println("Fecha de inicio:");
        Fecha fechaInicio = new Fecha(
                leerEntero("  Día: "),
                leerEntero("  Mes: "),
                leerEntero("  Año: ")
        );

        System.out.println("Fecha de fin:");
        Fecha fechaFin = new Fecha(
                leerEntero("  Día: "),
                leerEntero("  Mes: "),
                leerEntero("  Año: ")
        );

        Periodo periodo = new Periodo(fechaInicio, fechaFin);

        try {
            List<Factura> facturas = fachada.consultaFacturasPorPeriodo(periodo);

            System.out.println("\n=== FACTURAS DEL PERÍODO " + periodo.toString() + " ===");
            if (facturas.isEmpty()) {
                System.out.println("No hay facturas en este período.");
            } else {
                double total = 0;
                for (Factura factura : facturas) {
                    System.out.println(factura.toString());
                    total += factura.getTotal();
                }
                System.out.printf("\nTotal: %d factura(s) - Monto total: $%.2f\n", facturas.size(), total);
            }
        } catch (FachadaException e) {
            System.out.println("✗ Error en la consulta: " + e.getMessage());
        }
    }

    private static void consultarReservasPorPasajero() {
        String idPasajero = leerString("\nID del pasajero: ");

        try {
            List<Reserva> reservas = fachada.consultaReservasPorPasajero(idPasajero);

            System.out.println("\n=== RESERVAS DEL PASAJERO: " + idPasajero + " ===");
            if (reservas.isEmpty()) {
                System.out.println("No hay reservas para este usuario.");
            } else {
                for (Reserva reserva : reservas) {
                    System.out.println(reserva.toString());
                }
                System.out.println("\nTotal: " + reservas.size() + " reserva(s)");
            }
        } catch (FachadaException e) {
            System.out.println("✗ Error en la consulta: " + e.getMessage());
        }
    }

    private static void consultarReservasPorViaje() {
        String idViaje = leerString("\nID del viaje: ");

        try {
            List<Reserva> reservas = fachada.consultaReservasPorViaje(idViaje);

            System.out.println("\n=== RESERVAS DEL VIAJE: " + idViaje + " ===");
            if (reservas.isEmpty()) {
                System.out.println("No hay reservas para este viaje.");
            } else {
                int totalAsientos = 0;
                for (Reserva reserva : reservas) {
                    System.out.println(reserva.toString());
                    totalAsientos += reserva.getCantidadAsientos();
                }
                System.out.printf("\nTotal: %d reserva(s) - %d asiento(s) reservado(s)\n",
                        reservas.size(), totalAsientos);
            }
        } catch (FachadaException e) {
            System.out.println("✗ Error en la consulta: " + e.getMessage());
        }
    }

    private static void consultarViajesPorRuta() {
        String idRuta = leerString("\nID de la ruta: ");

        try {
            List<Viaje> viajes = fachada.consultaViajesPorRuta(idRuta);

            System.out.println("\n=== VIAJES DE LA RUTA: " + idRuta + " ===");
            if (viajes.isEmpty()) {
                System.out.println("No hay viajes para esta ruta.");
            } else {
                for (Viaje viaje : viajes) {
                    System.out.println(viaje.toString());
                }
                System.out.println("\nTotal: " + viajes.size() + " viaje(s)");
            }
        } catch (FachadaException e) {
            System.out.println("✗ Error en la consulta: " + e.getMessage());
        }
    }

    private static void consultarViajesPorPeriodo() {
        System.out.println("\n=== VIAJES POR PERÍODO ===");

        System.out.println("Fecha de inicio:");
        Fecha fechaInicio = new Fecha(
                leerEntero("  Día: "),
                leerEntero("  Mes: "),
                leerEntero("  Año: ")
        );

        System.out.println("Fecha de fin:");
        Fecha fechaFin = new Fecha(
                leerEntero("  Día: "),
                leerEntero("  Mes: "),
                leerEntero("  Año: ")
        );

        Periodo periodo = new Periodo(fechaInicio, fechaFin);

        try {
            List<Viaje> viajes = fachada.consultaViajesPorPeriodo(periodo);

            System.out.println("\n=== VIAJES DEL PERÍODO " + periodo.toString() + " ===");
            if (viajes.isEmpty()) {
                System.out.println("No hay viajes en este período.");
            } else {
                for (Viaje viaje : viajes) {
                    System.out.println(viaje.toString());
                }
                System.out.println("\nTotal: " + viajes.size() + " viaje(s)");
            }
        } catch (FachadaException e) {
            System.out.println("✗ Error en la consulta: " + e.getMessage());
        }
    }

    private static void consultarPagosPorUsuario() {
        String idUsuario = leerString("\nID del usuario: ");

        try {
            List<Pago> pagos = fachada.consultaPagosPorUsuario(idUsuario);

            System.out.println("\n=== PAGOS DEL USUARIO: " + idUsuario + " ===");
            if (pagos.isEmpty()) {
                System.out.println("No hay pagos para este usuario.");
            } else {
                double total = 0;
                for (Pago pago : pagos) {
                    System.out.println(pago.toString());
                    total += pago.getMonto();
                }
                System.out.printf("\nTotal: %d pago(s) - Monto total: $%.2f\n", pagos.size(), total);
            }
        } catch (FachadaException e) {
            System.out.println("✗ Error en la consulta: " + e.getMessage());
        }
    }

    private static void consultarPagosPorPeriodo() {
        System.out.println("\n=== PAGOS POR PERÍODO ===");

        System.out.println("Fecha de inicio:");
        Fecha fechaInicio = new Fecha(
                leerEntero("  Día: "),
                leerEntero("  Mes: "),
                leerEntero("  Año: ")
        );

        System.out.println("Fecha de fin:");
        Fecha fechaFin = new Fecha(
                leerEntero("  Día: "),
                leerEntero("  Mes: "),
                leerEntero("  Año: ")
        );

        Periodo periodo = new Periodo(fechaInicio, fechaFin);

        try {
            List<Pago> pagos = fachada.consultaPagosPorPeriodo(periodo);

            System.out.println("\n=== PAGOS DEL PERÍODO " + periodo.toString() + " ===");
            if (pagos.isEmpty()) {
                System.out.println("No hay pagos en este período.");
            } else {
                double total = 0;
                for (Pago pago : pagos) {
                    System.out.println(pago.toString());
                    total += pago.getMonto();
                }
                System.out.printf("\nTotal: %d pago(s) - Monto total: $%.2f\n", pagos.size(), total);
            }
        } catch (FachadaException e) {
            System.out.println("✗ Error en la consulta: " + e.getMessage());
        }
    }

    private static void consultarAsientosPorViaje() {
        String idViaje = leerString("\nID del viaje: ");

        try {
            List<Asiento> asientos = fachada.consultaAsientosPorViaje(idViaje);

            System.out.println("\n=== ASIENTOS DEL VIAJE: " + idViaje + " ===");
            if (asientos.isEmpty()) {
                System.out.println("No hay asientos para este viaje.");
            } else {
                for (Asiento asiento : asientos) {
                    System.out.println(asiento.toString());
                }
                System.out.println("\nTotal: " + asientos.size() + " asiento(s)");
            }
        } catch (FachadaException e) {
            System.out.println("✗ Error en la consulta: " + e.getMessage());
        }
    }

    private static void consultarAsientosPorEstado() {
        String estado = leerString("\nEstado del asiento (Disponible/Reservado/Ocupado): ");

        try {
            List<Asiento> asientos = fachada.consultaAsientosPorEstado(estado);

            System.out.println("\n=== ASIENTOS EN ESTADO: " + estado.toUpperCase() + " ===");
            if (asientos.isEmpty()) {
                System.out.println("No hay asientos en este estado.");
            } else {
                for (Asiento asiento : asientos) {
                    System.out.println(asiento.toString());
                }
                System.out.println("\nTotal: " + asientos.size() + " asiento(s)");
            }
        } catch (FachadaException e) {
            System.out.println("✗ Error en la consulta: " + e.getMessage());
        }
    }

    private static void consultarAsientosDisponiblesPorViaje() {
        String idViaje = leerString("\nID del viaje: ");

        try {
            List<Asiento> asientos = fachada.consultaAsientosDisponiblesPorViaje(idViaje);

            System.out.println("\n=== ASIENTOS DISPONIBLES DEL VIAJE: " + idViaje + " ===");
            if (asientos.isEmpty()) {
                System.out.println("No hay asientos disponibles para este viaje.");
            } else {
                for (Asiento asiento : asientos) {
                    System.out.println("  Asiento #" + asiento.getNumeroAsiento() + " - " +
                            asiento.getUbicacion());
                }
                System.out.println("\nTotal disponibles: " + asientos.size() + " asiento(s)");
            }
        } catch (FachadaException e) {
            System.out.println("✗ Error en la consulta: " + e.getMessage());
        }
    }

    private static void generarAsientosParaViaje() {
        System.out.println("\n=== GENERAR ASIENTOS PARA VIAJE ===");
        String idViaje = leerString("ID del viaje: ");
        int capacidad = leerEntero("Capacidad (número de asientos): ");

        try {
            ((FachadaArchivos) fachada).generarAsientosParaViaje(idViaje, capacidad);
            System.out.println("✓ Asientos generados exitosamente.");
        } catch (FachadaException e) {
            System.out.println("✗ Error al generar asientos: " + e.getMessage());
        }
    }

    private static void reservarAsientoEspecifico() {
        System.out.println("\n=== RESERVAR ASIENTO ESPECÍFICO ===");
        String idViaje = leerString("ID del viaje: ");
        int numeroAsiento = leerEntero("Número de asiento: ");
        String idReserva = leerString("ID de la reserva: ");

        try {
            boolean reservado = ((FachadaArchivos) fachada).reservarAsiento(idViaje, numeroAsiento, idReserva);
            if (reservado) {
                System.out.println("✓ Asiento reservado exitosamente.");
            } else {
                System.out.println("✗ No se pudo reservar el asiento.");
            }
        } catch (FachadaException e) {
            System.out.println("✗ Error al reservar asiento: " + e.getMessage());
        }
    }

    private static void liberarAsiento() {
        System.out.println("\n=== LIBERAR ASIENTO ===");
        String idViaje = leerString("ID del viaje: ");
        int numeroAsiento = leerEntero("Número de asiento: ");

        try {
            ((FachadaArchivos) fachada).liberarAsiento(idViaje, numeroAsiento);
            System.out.println("✓ Asiento liberado exitosamente.");
        } catch (FachadaException e) {
            System.out.println("✗ Error al liberar asiento: " + e.getMessage());
        }
    }

    private static void obtenerEstadisticasAsientos() {
        System.out.println("\n=== ESTADÍSTICAS DE ASIENTOS ===");
        String idViaje = leerString("ID del viaje: ");

        try {
            int[] estadisticas = ((FachadaArchivos) fachada).obtenerEstadisticasAsientos(idViaje);

            System.out.println("\n=== ESTADÍSTICAS PARA VIAJE " + idViaje + " ===");
            System.out.println("Disponibles: " + estadisticas[0]);
            System.out.println("Reservados:  " + estadisticas[1]);
            System.out.println("Ocupados:    " + estadisticas[2]);
            System.out.println("Total:       " + (estadisticas[0] + estadisticas[1] + estadisticas[2]));

            if ((estadisticas[0] + estadisticas[1] + estadisticas[2]) > 0) {
                double porcentajeOcupacion = ((estadisticas[1] + estadisticas[2]) * 100.0) /
                        (estadisticas[0] + estadisticas[1] + estadisticas[2]);
                System.out.printf("Ocupación:   %.1f%%\n", porcentajeOcupacion);
            }
        } catch (FachadaException e) {
            System.out.println("✗ Error al obtener estadísticas: " + e.getMessage());
        }
    }

    /**
     * Muestra estadísticas generales del sistema
     */
    private static void mostrarEstadisticas() {
        limpiarPantalla();
        System.out.println("╔══════════════════════════════════════════════════════╗");
        System.out.println("║           ESTADÍSTICAS DEL SISTEMA                  ║");
        System.out.println("╚══════════════════════════════════════════════════════╝");

        try {
            List<Bus> buses = fachada.consultaBuses();
            List<Ruta> rutas = fachada.consultaRutas();
            List<Usuario> usuarios = fachada.consultaUsuarios();
            List<Empresa> empresas = fachada.consultaEmpresas();
            List<Viaje> viajes = fachada.consultaViajes();
            List<Reserva> reservas = fachada.consultaReservas();
            List<Factura> facturas = fachada.consultaFacturas();
            List<Pago> pagos = fachada.consultaPagos();
            List<Asiento> asientos = fachada.consultaAsientos();

            System.out.println("\n📊 RESUMEN GENERAL:");
            System.out.println("----------------------------------------");
            System.out.printf("🚌  Buses:           %d\n", buses.size());
            System.out.printf("🛣️   Rutas:           %d\n", rutas.size());
            System.out.printf("👤  Usuarios:         %d\n", usuarios.size());
            System.out.printf("🏢  Empresas:         %d\n", empresas.size());
            System.out.printf("✈️   Viajes:          %d\n", viajes.size());
            System.out.printf("📅  Reservas:         %d\n", reservas.size());
            System.out.printf("🧾  Facturas:         %d\n", facturas.size());
            System.out.printf("💰  Pagos:            %d\n", pagos.size());
            System.out.printf("💺  Asientos:         %d\n", asientos.size());
            System.out.println("----------------------------------------");
            System.out.printf("📈  Total registros:  %d\n",
                    buses.size() + rutas.size() + usuarios.size() + empresas.size() +
                            viajes.size() + reservas.size() + facturas.size() + pagos.size() +
                            asientos.size());

            // Estadísticas adicionales
            System.out.println("\n📈 ESTADÍSTICAS ADICIONALES:");

            if (!buses.isEmpty()) {
                long busesActivos = buses.stream()
                        .filter(b -> b.getEstado() != null && b.getEstado().equals("Activo"))
                        .count();
                System.out.printf("  Buses activos: %d (%.1f%%)\n",
                        busesActivos, (busesActivos * 100.0 / buses.size()));
            }

            if (!viajes.isEmpty()) {
                long viajesProgramados = viajes.stream()
                        .filter(v -> v.getEstado() != null && v.getEstado().equals("Programado"))
                        .count();
                System.out.printf("  Viajes programados: %d (%.1f%%)\n",
                        viajesProgramados, (viajesProgramados * 100.0 / viajes.size()));
            }

        } catch (FachadaException e) {
            System.out.println("✗ Error al obtener estadísticas: " + e.getMessage());
        }

        pausar();
    }

    /**
     * Exporta datos del sistema
     */
    private static void exportarDatos() {
        limpiarPantalla();
        System.out.println("╔══════════════════════════════════════════════════════╗");
        System.out.println("║           EXPORTAR DATOS DEL SISTEMA                ║");
        System.out.println("╚══════════════════════════════════════════════════════╝");

        System.out.println("\nSeleccione el tipo de reporte a exportar:");
        System.out.println("1. Reporte completo (todos los datos)");
        System.out.println("2. Reporte de buses");
        System.out.println("3. Reporte de rutas");
        System.out.println("4. Reporte de usuarios");
        System.out.println("5. Reporte de empresas");
        System.out.println("6. Reporte de viajes");
        System.out.println("7. Reporte de reservas");
        System.out.println("8. Reporte de asientos");
        System.out.println("9. Mapa de asientos de un viaje");
        System.out.println("0. Cancelar");

        System.out.print("\nSeleccione una opción: ");
        String opcionStr = scanner.nextLine().trim();

        if (opcionStr.isEmpty()) {
            return;
        }

        try {
            int opcion = Integer.parseInt(opcionStr);

            if (opcion == 0) {
                System.out.println("Operación cancelada.");
                return;
            }

            String nombreArchivo = leerString("Nombre del archivo (sin extensión): ");

            FachadaArchivos fachadaArchivos = (FachadaArchivos) fachada;

            switch (opcion) {
                case 1:
                    fachadaArchivos.exportarTodosLosReportes();
                    break;
                case 2:
                    fachadaArchivos.exportarBusesATxt(nombreArchivo);
                    break;
                case 3:
                    fachadaArchivos.exportarRutasATxt(nombreArchivo);
                    break;
                case 4:
                    fachadaArchivos.exportarUsuariosATxt(nombreArchivo);
                    break;
                case 5:
                    fachadaArchivos.exportarEmpresasATxt(nombreArchivo);
                    break;
                case 6:
                    fachadaArchivos.exportarViajesATxt(nombreArchivo);
                    break;
                case 7:
                    fachadaArchivos.exportarReservasATxt(nombreArchivo);
                    break;
                case 8:
                    fachadaArchivos.exportarAsientosATxt(nombreArchivo);
                    break;
                case 9:
                    String idViaje = leerString("ID del viaje: ");
                    fachadaArchivos.exportarMapaAsientosATxt(idViaje, nombreArchivo);
                    break;
                default:
                    System.out.println("Opción no válida.");
                    return;
            }

            System.out.println("✓ Reporte exportado exitosamente.");

        } catch (NumberFormatException e) {
            System.out.println("Por favor, ingrese un número válido.");
        } catch (FachadaException e) {
            System.out.println("✗ Error al exportar: " + e.getMessage());
        }

        pausar();
    }

    /**
     * Muestra la ayuda del sistema
     */
    private static void mostrarAyuda() {
        limpiarPantalla();
        System.out.println("╔══════════════════════════════════════════════════════╗");
        System.out.println("║           AYUDA DEL SISTEMA                         ║");
        System.out.println("╚══════════════════════════════════════════════════════╝");

        System.out.println("\n📖 MANUAL DE USUARIO:");
        System.out.println("----------------------------------------");
        System.out.println("Este sistema permite gestionar todas las operaciones");
        System.out.println("de un sistema de transporte interprovincial.");
        System.out.println("\n🔧 FUNCIONALIDADES PRINCIPALES:");
        System.out.println("  1. Gestión de buses, rutas, usuarios y empresas");
        System.out.println("  2. Programación de viajes");
        System.out.println("  3. Sistema de reservas y asientos");
        System.out.println("  4. Facturación y pagos");
        System.out.println("  5. Reportes y estadísticas");
        System.out.println("  6. Exportación de datos a archivos TXT");
        System.out.println("\n💡 CONSEJOS:");
        System.out.println("  • Use IDs consistentes (ej: BUS001, RUT001)");
        System.out.println("  • Verifique la existencia de registros relacionados");
        System.out.println("  • Exporte reportes regularmente para respaldo");
        System.out.println("  • Use las consultas especiales para análisis");

        System.out.println("\n📞 SOPORTE:");
        System.out.println("  Para problemas técnicos, contacte al administrador");
        System.out.println("  del sistema o al departamento de TI.");

        pausar();
    }

    /**
     * Confirma la salida del sistema
     */
    private static boolean confirmarSalida() {
        System.out.print("\n¿Está seguro que desea salir? (S/N): ");
        String respuesta = scanner.nextLine().trim().toUpperCase();

        if (respuesta.equals("S") || respuesta.equals("SI")) {
            System.out.println("\n¡Gracias por usar el Sistema de Gestión de Transportes!");
            System.out.println("Hasta pronto.\n");
            return true;
        }

        return false;
    }

    /**
     * Limpia la pantalla de la consola
     */
    private static void limpiarPantalla() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            // Si falla, simplemente imprimir líneas en blanco
            for (int i = 0; i < 50; i++) {
                System.out.println();
            }
        }
    }

    /**
     * Pausa la ejecución esperando ENTER
     */
    private static void pausar() {
        System.out.print("\nPresione ENTER para continuar...");
        scanner.nextLine();
    }

    /**
     * Finaliza el sistema
     */
    private static void finalizarSistema() {
        if (scanner != null) {
            scanner.close();
        }
        System.out.println("Sistema finalizado.");
    }

    // ==================== MÉTODOS AUXILIARES DE ENTRADA ====================

    private static String leerString(String mensaje) {
        System.out.print(mensaje);
        return scanner.nextLine().trim();
    }

    private static int leerEntero(String mensaje) {
        while (true) {
            try {
                System.out.print(mensaje);
                String input = scanner.nextLine().trim();
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.print("Por favor, ingrese un número válido: ");
            }
        }
    }

    private static double leerDouble(String mensaje) {
        while (true) {
            try {
                System.out.print(mensaje);
                String input = scanner.nextLine().trim();
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                System.out.print("Por favor, ingrese un número válido: ");
            }
        }
    }

    /**
     * Genera datos de prueba para la demostración del proyecto.
     * Crea 15 empresas, usuarios, conductores, buses, rutas, viajes, pasajeros, reservas y facturas.
     */
    private static void generarDatosPrueba() {
        try {
            // Si ya existen empresas, asumimos que los datos ya están cargados
            /* COMENTADO PARA FORZAR LA REGENERACIÓN DE DATOS Y QUE SE VEAN LOS CAMBIOS
            if (!fachada.consultaEmpresas().isEmpty()) {
                System.out.println("Datos ya existentes. Omitiendo generación.");
                return;
            }
            */

            System.out.println("Generando datos de prueba completos (15 Empresas, 40 Buses/Rutas/Viajes)...");

            // --- LIMPIEZA PREVENTIVA ---
            // Borramos archivos residuales para evitar conflictos de IDs (como "Usuario repetido")
            String[] archivosLimpiar = {
                "usuarios.dat", "conductores.dat", "buses.dat", "rutas.dat", 
                "viajes.dat", "reservas.dat", "facturas.dat", "detalles_factura.dat", "pagos.dat", 
                "asientos.dat", "pasajeros.dat"
            };
            for (String archivo : archivosLimpiar) {
                java.io.File f = new java.io.File(archivo);
                if (f.exists()) f.delete();
            }

            // 1. EMPRESAS (15) - Datos Realistas
            String[] nombres = {
                "Cooperativa de Transportes Loja", "Flota Imbabura", "Transportes Ecuador", 
                "Panamericana Internacional", "Cooperativa Santa", "Transportes Occidentales",
                "Reina del Camino", "Cooperativa Baños", "Trans Esmeraldas", 
                "Aerotaxi", "Cooperativa Patria", "Transportes Kennedy", 
                "Cooperativa TAC", "Cita Express", "Transportes Macuchi"
            };
            
            String[] rucs = {
                "1190005526001", "1090006253001", "1790012675001", 
                "1790042308001", "0190072002001", "1790366481001",
                "1391722428001", "1891715634001", "1790016919001", 
                "1791322681001", "0691718892001", "1792135630001", 
                "0591714563001", "1891723456001", "0591734567001"
            };

            String[] direcciones = {
                "Av. Lauro Guerrero y 10 de Agosto, Loja", "Av. Mariano Acosta y Víctor Mideros, Ibarra", 
                "Juan León Mera N21-44 y Roca, Quito", "Av. Colón E4-30 y Reina Victoria, Quito", 
                "Av. España y Sebastián de Benalcázar, Cuenca", "Av. Mariscal Sucre S/N, Quito",
                "Av. 15 de Abril y Cristo del Consuelo, Portoviejo", "Terminal Terrestre, Baños de Agua Santa", 
                "Santa María y 9 de Octubre, Quito", "Av. Amazonas N24-196 y Av. Colón, Quito", 
                "Av. Daniel León Borja y Carabobo, Riobamba", "Av. de la Prensa N56-110, Quito", 
                "Antigua Panamericana Sur, Latacunga", "Av. Bolivariana y El Cóndor, Ambato", 
                "Calle Guayaquil y Quito, Quevedo"
            };

            String[] telefonos = {
                "072570222", "062955333", "022504455", 
                "022556677", "072841122", "022608899",
                "052932233", "032740555", "022523344", 
                "022445566", "032961122", "022435566", 
                "032801122", "032412233", "052751122"
            };

            for (int i = 0; i < 15; i++) {
                Empresa e = new Empresa();
                String id = String.format("EMP%03d", i + 1);
                
                e.setIdEmpresa(id);
                e.setNombre(nombres[i]);
                e.setRuc(rucs[i]);
                e.setDireccion(direcciones[i]);
                e.setTelefono(telefonos[i]);
                
                // Generar email limpio basado en el nombre
                String emailNombre = nombres[i].toLowerCase()
                        .replace("ñ", "n")
                        .replace("á", "a").replace("é", "e").replace("í", "i").replace("ó", "o").replace("ú", "u")
                        .replaceAll("[^a-z0-9]", "");
                // Acortar email quitando palabras comunes
                emailNombre = emailNombre.replace("cooperativa", "").replace("transportes", "").replace("flota", "").replace("de", "");
                if (emailNombre.length() > 15) emailNombre = emailNombre.substring(0, 15);
                
                e.setEmail("info@" + emailNombre + ".com.ec");
                
                fachada.agrega(e);
            }

            // 2. USUARIOS (1 Admin + 4 Operadores por empresa)
            String[] nombresPila = {"Juan", "María", "Carlos", "Ana", "Luis", "Elena", "José", "Lucía", "Pedro", "Sofía", "Miguel", "Paula", "David", "Carmen", "Javier", "Rosa", "Fernando", "Isabel", "Daniel", "Marta", "Alejandro", "Patricia", "Francisco", "Raquel", "Roberto", "Teresa", "Ricardo", "Beatriz", "Jorge", "Natalia"};
            String[] apellidos = {"García", "Rodríguez", "López", "Martínez", "González", "Pérez", "Sánchez", "Romero", "Torres", "Flores", "Rivera", "Gómez", "Díaz", "Reyes", "Morales", "Ortega", "Castillo", "Chávez", "Vargas", "Ramos", "Ruiz", "Mendoza", "Jiménez", "Silva", "Cruz", "Ortiz", "Gutiérrez", "Castro", "Vásquez", "Rojas"};

            // Listas auxiliares para referencias
            int contadorCedula = 1; // Semilla para generar cédulas únicas
            int globalNameIndex = 0; // Índice global para garantizar nombres únicos

            for (int i = 0; i < 15; i++) {
                String idEmpresa = String.format("EMP%03d", i + 1);
                Empresa empresaObj = new Empresa(idEmpresa);

                // --- 1 Administrador por empresa ---
                Usuario admin = new Usuario();
                String idAdmin = String.format("ADM%03d", i + 1);
                
                // Generar nombre único usando índice global
                int idxNombre = globalNameIndex % nombresPila.length;
                int idxApellido = (globalNameIndex + (globalNameIndex / nombresPila.length)) % apellidos.length;
                String nombreAdmin = nombresPila[idxNombre] + " " + apellidos[idxApellido];
                globalNameIndex++;
                
                admin.setIdUsuario(idAdmin);
                admin.setNombre(nombreAdmin);
                admin.setCedula(generarCedulaValida(contadorCedula++));
                admin.setContrasena("Admin1234"); // Cumple longitud > 8
                String apellidoAdminLimpio = apellidos[idxApellido].toLowerCase()
                        .replace("á", "a").replace("é", "e").replace("í", "i").replace("ó", "o").replace("ú", "u")
                        .replace("ñ", "n");
                admin.setEmail("admin." + apellidoAdminLimpio + "@" + idEmpresa.toLowerCase() + ".com");
                admin.setTelefono(String.format("099%07d", i + 1));
                admin.setTipo("Administrador");
                admin.setEstado("ACTIVO");
                admin.setEmpresa(empresaObj);
                admin.setFechaRegistro(new Fecha());
                fachada.agrega(admin);

                // --- 4 Operadores por empresa ---
                for (int j = 1; j <= 4; j++) {
                    Usuario oper = new Usuario();
                    int operIndex = (i * 4) + j; 
                    String idOper = String.format("OPE%03d", operIndex);
                    
                    // Generar nombre único usando índice global
                    idxNombre = globalNameIndex % nombresPila.length;
                    idxApellido = (globalNameIndex + (globalNameIndex / nombresPila.length)) % apellidos.length;
                    String nombreOper = nombresPila[idxNombre] + " " + apellidos[idxApellido];
                    globalNameIndex++;

                    oper.setIdUsuario(idOper);
                    oper.setNombre(nombreOper);
                    oper.setCedula(generarCedulaValida(contadorCedula++));
                    oper.setContrasena("Oper1234");
                    String apellidoOperLimpio = apellidos[idxApellido].toLowerCase()
                            .replace("á", "a").replace("é", "e").replace("í", "i").replace("ó", "o").replace("ú", "u")
                            .replace("ñ", "n");
                    oper.setEmail("oper." + apellidoOperLimpio + j + "@" + idEmpresa.toLowerCase() + ".com");
                    oper.setTelefono(String.format("098%07d", operIndex));
                    oper.setTipo("Operador");
                    oper.setEstado("ACTIVO");
                    oper.setEmpresa(empresaObj);
                    oper.setFechaRegistro(new Fecha());
                    fachada.agrega(oper);
                }
            }

            // 3. CONDUCTORES (3 por empresa = 45 total)
            ControladorConductores ctrlConductores = new ControladorConductores();
            for (int i = 0; i < 15; i++) {
                String idEmpresa = String.format("EMP%03d", i + 1);
                
                for (int j = 0; j < 3; j++) {
                    int conductorIndex = (i * 3) + j;
                    // Usar índices diferentes para variar nombres respecto a usuarios
                    String nombreConductor = nombresPila[(conductorIndex + 5) % nombresPila.length] + " " + apellidos[(conductorIndex + 10) % apellidos.length];
                    String cedula = generarCedulaValida(contadorCedula++);
                    String telefono = String.format("099%07d", conductorIndex + 500);
                    String licencia = (j % 2 == 0) ? "TIPO E" : "TIPO D"; // Variar licencias

                    ctrlConductores.agregarConductor(
                            nombreConductor,
                            cedula,
                            telefono,
                            licencia,
                            idEmpresa
                    );
                }
            }

            // 4. PASAJEROS (3 por empresa = 45 total)
            java.util.List<Pasajero> pasajerosGenerados = new java.util.ArrayList<>();
            for (int i = 0; i < 15; i++) {
                String idEmpresa = String.format("EMP%03d", i + 1);
                
                for (int j = 0; j < 3; j++) {
                    int pasIndex = (i * 3) + j + 1;
                    Pasajero p = new Pasajero();
                    p.setIdPasajero(String.format("PAS%03d", pasIndex));
                    
                    // Nombre único
                    int idxNombre = globalNameIndex % nombresPila.length;
                    int idxApellido = (globalNameIndex + (globalNameIndex / nombresPila.length)) % apellidos.length;
                    String nombrePas = nombresPila[idxNombre] + " " + apellidos[idxApellido];
                    globalNameIndex++;
                    
                    p.setNombre(nombrePas);
                    p.setCedula(generarCedulaValida(contadorCedula++));
                    p.setTelefono(String.format("099%07d", pasIndex + 800));
                    p.setEmail("cliente" + pasIndex + "@mail.com");
                    p.setDireccion("Av. Principal " + pasIndex);
                    
                    // Generar edades variadas para probar tipos dinámicos
                    int anioNac = 1990;
                    if (j == 0) anioNac = 2016; // Niño (~10 años)
                    else if (j == 1) anioNac = 1985; // Adulto (~41 años)
                    else if (j == 2) anioNac = 1950; // Tercera edad (~76 años)
                    p.setFechaNacimiento(new Fecha(10, 5, anioNac));
                    
                    p.setDiscapacidad(j == 2); // 1 de cada 3 con discapacidad
                    p.setEmpresa(new Empresa(idEmpresa));
                    
                    fachada.agrega(p);
                    pasajerosGenerados.add(p);
                }
            }

            // 5. BUSES (40 en total)
            String[] marcasBus = {"Mercedes-Benz", "Hino", "Scania", "Volvo", "Volkswagen"};
            String[] letrasPlaca = {"P", "G", "A", "T", "U", "L", "X", "H", "E", "I"}; // Provincias

            // Listas auxiliares para mantener referencias en memoria y evitar errores de recuperación
            java.util.List<Bus> busesGenerados = new java.util.ArrayList<>();
            java.util.List<Ruta> rutasGeneradas = new java.util.ArrayList<>();
            java.util.List<Viaje> viajesGenerados = new java.util.ArrayList<>();

            for (int i = 0; i < 40; i++) {
                int index = i + 1;
                Bus b = new Bus();
                String id = String.format("BUS%03d", index);
                b.setIdBus(id);
                
                // Generar Placa Realista (Ej: PAA-1234)
                String provincia = letrasPlaca[i % letrasPlaca.length];
                char letra2 = (char)('A' + (i % 26)); // Variar segunda letra
                String letras = provincia + letra2 + "A"; 
                int numeros = 1000 + (i * 55);
                b.setPlaca(String.format("%s-%04d", letras, numeros));
                
                b.setNumeroChasis("89" + (10000000 + i * 12345)); 
                b.setNumeroMotor("OM" + (900000 + i * 789));
                b.setMarca(marcasBus[i % marcasBus.length]);
                b.setAnioFabricacion(2018 + (i % 6)); // Entre 2018 y 2023
                b.setCapacidad(40 + ((i % 3) * 4)); // Capacidades: 40, 44, 48
                b.setEstado("ACTIVO");
                
                int empIdx = (i % 15) + 1; // Distribuir entre las 15 empresas
                b.setEmpresa(new Empresa(String.format("EMP%03d", empIdx)));
                fachada.agrega(b);
                busesGenerados.add(b);
            }

            // 6. RUTAS (40 en total)
            String[] ciudades = {
                "Quito", "Guayaquil", "Cuenca", "Manta", "Ambato", 
                "Loja", "Santo Domingo", "Machala", "Ibarra", "Esmeraldas", 
                "Riobamba", "Quevedo", "Portoviejo", "Latacunga", "Tulcán",
                "Babahoyo", "Salinas", "Tena", "Puyo", "Coca"
            };

            for (int i = 0; i < 40; i++) {
                int index = i + 1;
                Ruta r = new Ruta();
                String id = String.format("RUT%03d", index);
                r.setIdRuta(id);
                
                String origen = ciudades[i % ciudades.length];
                String destino = ciudades[(i + 5) % ciudades.length]; // Offset para variar destino
                
                r.setOrigen(origen);
                r.setDestino(destino);
                
                // Distancia simulada (entre 100 y 500 km)
                double distancia = 100.0 + (i * 20.5);
                r.setDistancia(distancia);
                
                // Precio aprox $0.08 por km
                double precio = Math.round((distancia * 0.08) * 100.0) / 100.0;
                r.setPrecio(precio);
                
                // Tiempo estimado (60km/h promedio)
                double tiempo = distancia / 60.0;
                r.setTiempoEstimado(Math.round(tiempo * 10.0) / 10.0);
                
                r.setParadas("Terminal " + origen + ", Santo Domingo, Quevedo, Terminal " + destino);
                r.setTiempoDetencion(15);
                r.setAdvertencias("Ninguna");
                r.setEstado("ACTIVA");
                
                int empIdx = (i % 15) + 1; // Distribuir entre las 15 empresas
                r.setEmpresa(new Empresa(String.format("EMP%03d", empIdx)));
                fachada.agrega(r);
                rutasGeneradas.add(r);
            }

            // 7. VIAJES (40 en total)
            for (int i = 0; i < 40; i++) {
                int index = i + 1;
                Viaje v = new Viaje();
                String idViaje = String.format("VIA%03d", index);
                v.setIdViaje(idViaje);
                
                // Usar objetos de las listas en memoria para evitar NullPointerException
                // Asegurar que Bus y Ruta sean de la misma empresa (i % 15 + 1)
                Bus bus = busesGenerados.get(i);
                Ruta ruta = rutasGeneradas.get(i);
                
                v.setBus(bus);
                v.setRuta(ruta);
                v.setEmpresa(bus.getEmpresa()); // Misma empresa
                
                // Fechas dinámicas (Variedad de años y meses)
                int anio = 2024 + (i % 3); // 2024, 2025, 2026
                int mes = 1 + (i % 12);    // 1 - 12
                int dia = 1 + (i % 28);    // 1 - 28
                v.setFechaSalida(new Fecha(dia, mes, anio));
                
                // Hora salida aleatoria (entre 06:00 y 20:00)
                int hora = 6 + (i % 14); 
                int min = (i % 4) * 15; // 00, 15, 30, 45
                v.setHoraSalida(String.format("%02d:%02d", hora, min));
                
                // Calcular llegada estimada basada en la distancia de la ruta (60km/h promedio)
                double horasDuracion = ruta.getDistancia() / 60.0;
                int minutosDuracion = (int)(horasDuracion * 60);
                
                int minutosSalida = hora * 60 + min;
                int minutosLlegada = minutosSalida + minutosDuracion;
                int horaLleg = (minutosLlegada / 60) % 24;
                int minLleg = minutosLlegada % 60;
                
                v.setFechaLlegada(new Fecha(dia, mes, anio)); // Simplificación: mismo día
                v.setHoraLlegada(String.format("%02d:%02d", horaLleg, minLleg));
                
                v.setAsientosDisponibles(bus.getCapacidad());
                
                // Estado variado
                if (i < 10) v.setEstado("Finalizado");
                else if (i < 15) v.setEstado("En Curso");
                else v.setEstado("Programado");
                
                v.setPrecio(ruta.getPrecio());
                // Asignar conductor de la misma empresa (hay 3 por empresa)
                int empIdx = (i % 15);
                int conductorId = (empIdx * 3) + 1; // Primer conductor de esa empresa
                v.setConductor1(new Conductor(String.format("CND%03d", conductorId)));
                
                fachada.agrega(v);
                ((FachadaArchivos) fachada).generarAsientosParaViaje(idViaje, bus.getCapacidad());
                viajesGenerados.add(v);
            }

            // 8. TRANSACCIONES (40 Reservas + Facturas)
            for (int i = 0; i < 40; i++) {
                int index = i + 1;
                
                // Usar pasajero existente (tenemos 45)
                Pasajero p = pasajerosGenerados.get(i);

                // Reserva
                Reserva r = new Reserva();
                String idRes = String.format("RES%03d", index);
                r.setIdReserva(idRes);
                r.setPasajero(p);
                
                // Asignar viaje correspondiente
                Viaje viaje = viajesGenerados.get(i);
                String idViaje = viaje.getIdViaje();
                
                r.setViaje(viaje);
                r.setEmpresa(viaje.getEmpresa());
                
                // Fecha reserva dinámica (1-5 días antes del viaje)
                int diasAntes = (i % 5) + 1;
                int dRes = viaje.getFechaSalida().getDia() - diasAntes;
                int mRes = viaje.getFechaSalida().getMes();
                int aRes = viaje.getFechaSalida().getAnio();
                if (dRes < 1) {
                    mRes--; if (mRes < 1) { mRes = 12; aRes--; }
                    dRes += 28;
                }
                r.setFechaReserva(new Fecha(dRes, mRes, aRes));
                r.setEstado("Confirmada");
                
                // Cantidad y Asientos (1 a 2 asientos)
                int cantidad = (i % 2) + 1; 
                r.setCantidadAsientos(cantidad);
                
                // Selección dinámica de asientos
                int asientoInicio = (i * 2) % 20 + 1; 
                
                for (int k = 0; k < cantidad; k++) {
                    int numAsiento = asientoInicio + k;
                    r.agregarAsiento(String.valueOf(numAsiento));
                    ((FachadaArchivos) fachada).reservarAsiento(idViaje, numAsiento, idRes);
                }
                
                // Actualizar disponibilidad del viaje
                viaje.setAsientosDisponibles(viaje.getAsientosDisponibles() - cantidad);
                fachada.actualiza(viaje);
                
                // Calcular precio total real
                double precioBase = viaje.getPrecio();
                double factor = 1.0;
                if ("NIÑO".equals(p.getTipoPasajero()) || "TERCERA_EDAD".equals(p.getTipoPasajero())) {
                    factor = 0.5;
                }
                double subtotal = precioBase * factor * cantidad;
                r.setPrecioTotal(subtotal);
                
                fachada.agrega(r);
                
                // Factura
                Factura f = new Factura();
                f.setIdFactura(String.format("FAC%03d", index));
                f.setEmpresa(r.getEmpresa());
                f.setPasajero(p);
                f.setReserva(r);
                
                double totalFactura = subtotal * 1.15; // IVA 15%
                f.setTotal(totalFactura);
                
                f.setFechaEmision(r.getFechaReserva()); // Misma fecha que la reserva
                f.setEstado("Pagada");
                
                // Nuevos campos Snapshot
                f.setNombreEmpresaSnapshot(r.getEmpresa().getNombre());
                f.setRucEmpresaSnapshot(r.getEmpresa().getRuc());
                f.setDireccionEmpresaSnapshot(r.getEmpresa().getDireccion());
                
                f.setNombreVendedor("Ventanilla Auto");
                
                f.setDatosPasajeroSnapshot(p.getNombre() + " (" + p.getCedula() + ")");
                
                fachada.agrega(f);
                
                // Generar Detalles de Factura
                persistencia.DetallesFactura detallesDAO = new persistencia.DetallesFactura("detalles_factura.dat");
                
                // Detalle 1: Pasajes
                DetalleFactura d1 = new DetalleFactura(null, f.getIdFactura(), "Servicio de Transporte (Pasajes)", cantidad, subtotal / cantidad);
                d1.setSubtotal(subtotal);
                detallesDAO.agrega(d1);
                
                // Detalle 2: IVA
                DetalleFactura d2 = new DetalleFactura(null, f.getIdFactura(), "Impuesto al Valor Agregado (15%)", 1, subtotal * 0.15);
                d2.setSubtotal(subtotal * 0.15);
                detallesDAO.agrega(d2);
            }

            // EXTRA EMP001: GENERACIÓN DE DATOS ADICIONALES (20 de cada uno)
            System.out.println("Generando carga adicional exclusiva para Empresa 001...");

            // 1. Extra Conductores (20) - Nombres únicos usando globalNameIndex
            for (int k = 0; k < 20; k++) {
                int idx = 46 + k; // IDs continuos desde el último generado (45)
                
                int idxNombre = globalNameIndex % nombresPila.length;
                int idxApellido = (globalNameIndex + (globalNameIndex / nombresPila.length)) % apellidos.length;
                String nombre = nombresPila[idxNombre] + " " + apellidos[idxApellido];
                globalNameIndex++;

                ctrlConductores.agregarConductor(
                    nombre,
                    generarCedulaValida(contadorCedula++),
                    "099" + String.format("%07d", idx + 900),
                    (k % 2 == 0) ? "TIPO E" : "TIPO C", // Licencia dinámica
                    "EMP001"
                );
            }

            // 2. Extra Pasajeros (20) - Nombres únicos
            for (int k = 0; k < 20; k++) {
                int idx = 46 + k;
                Pasajero p = new Pasajero();
                p.setIdPasajero(String.format("PAS%03d", idx));
                
                int idxNombre = globalNameIndex % nombresPila.length;
                int idxApellido = (globalNameIndex + (globalNameIndex / nombresPila.length)) % apellidos.length;
                String nombre = nombresPila[idxNombre] + " " + apellidos[idxApellido];
                globalNameIndex++;

                p.setNombre(nombre);
                p.setCedula(generarCedulaValida(contadorCedula++));
                p.setTelefono("099" + String.format("%07d", idx + 1000));
                p.setEmail("cliente.loja" + k + "@mail.com");
                p.setDireccion("Loja, Centro");
                p.setFechaNacimiento(new Fecha(1, 1, 1980 + (k % 20))); // Fecha nacimiento para extras
                p.setEmpresa(new Empresa("EMP001"));
                
                fachada.agrega(p);
                pasajerosGenerados.add(p);
            }

            // 3. Extra Buses (20)
            String[] marcasExtra = {"Scania", "Volvo", "Mercedes-Benz", "Hino", "Yutong"};
            for (int k = 0; k < 20; k++) {
                int idx = 41 + k; // IDs continuos desde 40
                Bus b = new Bus();
                b.setIdBus(String.format("BUS%03d", idx));
                b.setPlaca(String.format("LJA-%04d", 3000 + k));
                b.setMarca(marcasExtra[k % marcasExtra.length]);
                b.setAnioFabricacion(2020 + (k % 6));
                b.setCapacidad(40 + ((k % 3) * 2)); // 40, 42, 44
                b.setEstado("ACTIVO");
                b.setEmpresa(new Empresa("EMP001"));
                fachada.agrega(b);
                busesGenerados.add(b);
            }

            // 4. Extra Rutas (20)
            String[] destinosLoja = {"Quito", "Guayaquil", "Cuenca", "Machala", "Zamora", "Piñas", "Macará", "Vilcabamba", "Amaluza", "Zapotillo", "Gualaquiza", "Yantzaza", "Catamayo", "Saraguro", "Cariamanga", "El Pangui", "Balsas", "Alamor", "Celica", "Gonzanamá"};
            
            for (int k = 0; k < 20; k++) {
                int idx = 41 + k;
                Ruta r = new Ruta();
                r.setIdRuta(String.format("RUT%03d", idx));
                
                // Origen dinámico (Alternar entre Loja como origen y destino)
                if (k % 2 == 0) {
                    r.setOrigen("Loja");
                    r.setDestino(destinosLoja[k % destinosLoja.length]);
                } else {
                    r.setOrigen(destinosLoja[k % destinosLoja.length]);
                    r.setDestino("Loja");
                }
                
                double distBase = 50.0 + (k * 25.0); 
                if (r.getDestino().equals("Quito")) distBase = 680.0;
                else if (r.getDestino().equals("Guayaquil")) distBase = 400.0;
                else if (r.getDestino().equals("Cuenca")) distBase = 215.0;
                
                r.setDistancia(distBase);
                r.setPrecio(Math.round(distBase * 0.08 * 100.0) / 100.0);
                r.setTiempoEstimado(Math.round(distBase / 60.0 * 10.0) / 10.0);
                
                r.setParadas("Terminal Loja, Catamayo, Terminal " + r.getDestino());
                r.setTiempoDetencion(10);
                r.setEstado("ACTIVA");
                r.setEmpresa(new Empresa("EMP001"));
                fachada.agrega(r);
                rutasGeneradas.add(r);
            }

            // 5. Extra Viajes (20)
            for (int k = 0; k < 20; k++) {
                int idx = 41 + k;
                Viaje v = new Viaje();
                v.setIdViaje(String.format("VIA%03d", idx));
                
                // Usar los nuevos buses y rutas (indices 40 a 59 en las listas)
                v.setBus(busesGenerados.get(40 + k));
                v.setRuta(rutasGeneradas.get(40 + k));
                v.setEmpresa(new Empresa("EMP001"));
                
                int anio = 2024 + (k % 3);
                int mes = 1 + (k % 12);
                int dia = 1 + (k % 28);
                v.setFechaSalida(new Fecha(dia, mes, anio));
                v.setFechaLlegada(new Fecha(dia, mes, anio));
                
                int hora = 5 + (k % 16);
                int min = (k % 4) * 15;
                v.setHoraSalida(String.format("%02d:%02d", hora, min));
                
                double duracion = v.getRuta().getTiempoEstimado();
                int horaLlegada = (hora + (int)duracion) % 24;
                v.setHoraLlegada(String.format("%02d:%02d", horaLlegada, min));
                
                v.setAsientosDisponibles(v.getBus().getCapacidad());
                v.setEstado(k < 5 ? "Finalizado" : (k < 8 ? "En Curso" : "Programado"));
                v.setPrecio(v.getRuta().getPrecio());
                
                // Asignar conductor de Loja (creados en paso 1, IDs 46+k)
                v.setConductor1(new Conductor(String.format("CND%03d", 46 + (k % 20))));
                
                fachada.agrega(v);
                ((FachadaArchivos) fachada).generarAsientosParaViaje(v.getIdViaje(), v.getBus().getCapacidad());
                viajesGenerados.add(v);
            }

            // 6. Extra Transacciones (20)
            for (int k = 0; k < 20; k++) {
                int idx = 41 + k;
                
                // Usar pasajeros extra (indices 45 a 64) y viajes extra (indices 40 a 59)
                Pasajero p = pasajerosGenerados.get(45 + k);
                Viaje v = viajesGenerados.get(40 + k);
                
                Reserva r = new Reserva();
                r.setIdReserva(String.format("RES%03d", idx));
                r.setPasajero(p);
                r.setViaje(v);
                r.setEmpresa(new Empresa("EMP001"));
                
                // Fecha reserva dinámica para extras
                int diasAntesExtra = (k % 3) + 1;
                int dResEx = v.getFechaSalida().getDia() - diasAntesExtra;
                int mResEx = v.getFechaSalida().getMes();
                int aResEx = v.getFechaSalida().getAnio();
                if (dResEx < 1) {
                    mResEx--; if (mResEx < 1) { mResEx = 12; aResEx--; }
                    dResEx += 28;
                }
                r.setFechaReserva(new Fecha(dResEx, mResEx, aResEx));
                r.setEstado("Confirmada");
                
                int cantAsientos = (k % 3) + 1;
                r.setCantidadAsientos(cantAsientos);
                r.setPrecioTotal(v.getPrecio() * cantAsientos);
                r.setMetodoPago("EFECTIVO");
                
                for (int s = 0; s < cantAsientos; s++) {
                    int asientoNum = (k * 2) + s + 1;
                    if (asientoNum > v.getBus().getCapacidad()) asientoNum = (asientoNum % v.getBus().getCapacidad()) + 1;
                    r.agregarAsiento(String.valueOf(asientoNum));
                    ((FachadaArchivos) fachada).reservarAsiento(v.getIdViaje(), asientoNum, r.getIdReserva());
                }
                
                fachada.agrega(r);
                v.setAsientosDisponibles(v.getAsientosDisponibles() - cantAsientos);
                fachada.actualiza(v);
                
                Factura f = new Factura();
                f.setIdFactura(String.format("FAC%03d", idx));
                f.setEmpresa(new Empresa("EMP001"));
                f.setPasajero(p);
                f.setReserva(r);
                f.setTotal(r.getPrecioTotal() * 1.15);
                f.setFechaEmision(r.getFechaReserva()); // Fecha dinámica
                f.setEstado("Pagada");
                f.setNombreVendedor("Ventanilla Loja");
                f.setNombreEmpresaSnapshot("Cooperativa Loja");
                f.setRucEmpresaSnapshot("1190005526001");
                f.setDireccionEmpresaSnapshot("Av. Lauro Guerrero, Loja");
                f.setDatosPasajeroSnapshot(p.getNombre() + " (" + p.getCedula() + ")");
                
                fachada.agrega(f);
                
                // Detalles
                persistencia.DetallesFactura detallesDAO = new persistencia.DetallesFactura("detalles_factura.dat");
                DetalleFactura d1 = new DetalleFactura(null, f.getIdFactura(), "Pasaje " + v.getRuta().getDestino(), cantAsientos, v.getPrecio());
                d1.setSubtotal(r.getPrecioTotal());
                detallesDAO.agrega(d1);
                
                DetalleFactura d2 = new DetalleFactura(null, f.getIdFactura(), "IVA (15%)", 1, r.getPrecioTotal() * 0.15);
                d2.setSubtotal(r.getPrecioTotal() * 0.15);
                detallesDAO.agrega(d2);
            }

            // EXTRA EMP002: FLOTA IMBABURA (7 Registros)
            System.out.println("Generando carga adicional para Flota Imbabura (EMP002)...");
            String[] destinosIbarra = {"Quito", "Tulcán", "Otavalo", "Cayambe", "Atuntaqui", "Cotacachi", "San Gabriel"};

            for (int k = 0; k < 7; k++) {
                int idx = 61 + k; // IDs 61-67
                int conductorIdx = 66 + k; // IDs 66-72

                // Conductor
                int idxNombre = globalNameIndex % nombresPila.length;
                int idxApellido = (globalNameIndex + (globalNameIndex / nombresPila.length)) % apellidos.length;
                String nombreCond = nombresPila[idxNombre] + " " + apellidos[idxApellido];
                globalNameIndex++;
                ctrlConductores.agregarConductor(nombreCond, generarCedulaValida(contadorCedula++), "099" + String.format("%07d", idx + 2000), "TIPO E", "EMP002");

                // Bus
                Bus b = new Bus();
                b.setIdBus(String.format("BUS%03d", idx));
                b.setPlaca(String.format("IBA-%04d", 4000 + k));
                b.setMarca("Hino");
                b.setAnioFabricacion(2021);
                b.setCapacidad(42);
                b.setEstado("ACTIVO");
                b.setEmpresa(new Empresa("EMP002"));
                fachada.agrega(b);
                busesGenerados.add(b);

                // Ruta
                Ruta r = new Ruta();
                r.setIdRuta(String.format("RUT%03d", idx));
                r.setOrigen("Ibarra");
                r.setDestino(destinosIbarra[k % destinosIbarra.length]);
                r.setDistancia(120.0 + (k * 15));
                r.setPrecio(10.0 + k);
                r.setTiempoEstimado(2.5 + (k * 0.5));
                r.setEstado("ACTIVA");
                r.setEmpresa(new Empresa("EMP002"));
                fachada.agrega(r);
                rutasGeneradas.add(r);

                // Viaje
                Viaje v = new Viaje();
                v.setIdViaje(String.format("VIA%03d", idx));
                v.setBus(b);
                v.setRuta(r);
                v.setEmpresa(new Empresa("EMP002"));
                int anio = 2025;
                int mes = 1 + (k % 12);
                int dia = 1 + (k % 28);
                v.setFechaSalida(new Fecha(dia, mes, anio));
                v.setFechaLlegada(new Fecha(dia, mes, anio));
                v.setHoraSalida("08:00");
                v.setHoraLlegada("10:30");
                v.setAsientosDisponibles(b.getCapacidad());
                v.setEstado("Programado");
                v.setPrecio(r.getPrecio());
                v.setConductor1(new Conductor(String.format("CND%03d", conductorIdx)));
                fachada.agrega(v);
                ((FachadaArchivos) fachada).generarAsientosParaViaje(v.getIdViaje(), v.getBus().getCapacidad());
                viajesGenerados.add(v);
            }

            // EXTRA EMP003: TRANSPORTES ECUADOR (7 Registros)
            System.out.println("Generando carga adicional para Transportes Ecuador (EMP003)...");
            for (int k = 0; k < 7; k++) {
                int idx = 68 + k; // IDs 68-74
                int conductorIdx = 73 + k; // IDs 73-79

                // Conductor
                int idxNombre = globalNameIndex % nombresPila.length;
                int idxApellido = (globalNameIndex + (globalNameIndex / nombresPila.length)) % apellidos.length;
                String nombreCond = nombresPila[idxNombre] + " " + apellidos[idxApellido];
                globalNameIndex++;
                ctrlConductores.agregarConductor(nombreCond, generarCedulaValida(contadorCedula++), "099" + String.format("%07d", idx + 3000), "TIPO E", "EMP003");

                // Bus
                Bus b = new Bus();
                b.setIdBus(String.format("BUS%03d", idx));
                b.setPlaca(String.format("UIO-%04d", 5000 + k));
                b.setMarca("Volvo");
                b.setAnioFabricacion(2022);
                b.setCapacidad(44);
                b.setEstado("ACTIVO");
                b.setEmpresa(new Empresa("EMP003"));
                fachada.agrega(b);

                // Ruta
                Ruta r = new Ruta();
                r.setIdRuta(String.format("RUT%03d", idx));
                r.setOrigen("Quito");
                r.setDestino("Guayaquil");
                r.setDistancia(420.0);
                r.setPrecio(12.0);
                r.setTiempoEstimado(8.0);
                r.setEstado("ACTIVA");
                r.setEmpresa(new Empresa("EMP003"));
                fachada.agrega(r);

                // Viaje
                Viaje v = new Viaje();
                v.setIdViaje(String.format("VIA%03d", idx));
                v.setBus(b);
                v.setRuta(r);
                v.setEmpresa(new Empresa("EMP003"));
                int anio = 2026;
                int mes = 1 + (k % 12);
                int dia = 1 + (k % 28);
                v.setFechaSalida(new Fecha(dia, mes, anio));
                v.setFechaLlegada(new Fecha(dia, mes, anio));
                v.setHoraSalida("22:00");
                v.setHoraLlegada("06:00");
                v.setAsientosDisponibles(b.getCapacidad());
                v.setEstado("Programado");
                v.setPrecio(r.getPrecio());
                v.setConductor1(new Conductor(String.format("CND%03d", conductorIdx)));
                fachada.agrega(v);
                ((FachadaArchivos) fachada).generarAsientosParaViaje(v.getIdViaje(), v.getBus().getCapacidad());
            }

            // EXTRA EMP004: PANAMERICANA (6 Registros)
            System.out.println("Generando carga adicional para Panamericana (EMP004)...");
            for (int k = 0; k < 6; k++) {
                int idx = 75 + k; // IDs 75-80
                int conductorIdx = 80 + k; // IDs 80-85

                // Conductor
                int idxNombre = globalNameIndex % nombresPila.length;
                int idxApellido = (globalNameIndex + (globalNameIndex / nombresPila.length)) % apellidos.length;
                String nombreCond = nombresPila[idxNombre] + " " + apellidos[idxApellido];
                globalNameIndex++;
                ctrlConductores.agregarConductor(nombreCond, generarCedulaValida(contadorCedula++), "099" + String.format("%07d", idx + 4000), "TIPO E", "EMP004");

                // Bus
                Bus b = new Bus();
                b.setIdBus(String.format("BUS%03d", idx));
                b.setPlaca(String.format("PAN-%04d", 6000 + k));
                b.setMarca("Scania");
                b.setAnioFabricacion(2023);
                b.setCapacidad(46);
                b.setEstado("ACTIVO");
                b.setEmpresa(new Empresa("EMP004"));
                fachada.agrega(b);

                // Ruta
                Ruta r = new Ruta();
                r.setIdRuta(String.format("RUT%03d", idx));
                r.setOrigen("Quito");
                r.setDestino("Manta");
                r.setDistancia(380.0);
                r.setPrecio(11.0);
                r.setTiempoEstimado(7.0);
                r.setEstado("ACTIVA");
                r.setEmpresa(new Empresa("EMP004"));
                fachada.agrega(r);

                // Viaje
                Viaje v = new Viaje();
                v.setIdViaje(String.format("VIA%03d", idx));
                v.setBus(b);
                v.setRuta(r);
                v.setEmpresa(new Empresa("EMP004"));
                int anio = 2024 + (k % 2);
                int mes = 1 + (k % 12);
                int dia = 1 + (k % 28);
                v.setFechaSalida(new Fecha(dia, mes, anio));
                v.setFechaLlegada(new Fecha(dia, mes, anio));
                v.setHoraSalida("23:00");
                v.setHoraLlegada("06:00");
                v.setAsientosDisponibles(b.getCapacidad());
                v.setEstado("Programado");
                v.setPrecio(r.getPrecio());
                v.setConductor1(new Conductor(String.format("CND%03d", conductorIdx)));
                fachada.agrega(v);
                ((FachadaArchivos) fachada).generarAsientosParaViaje(v.getIdViaje(), v.getBus().getCapacidad());
            }

            System.out.println("=== DATOS DE PRUEBA GENERADOS EXITOSAMENTE ===");
            pausar();
        } catch (Exception e) {
            System.err.println("Error generando datos de prueba: " + e.getMessage());
            e.printStackTrace(); // Imprimir detalle del error para depuración
        }
    }

    /**
     * Genera una cédula ecuatoriana válida (algoritmo módulo 10)
     * para pasar las validaciones del sistema.
     * @param semilla Número secuencial para variar la cédula.
     * @return String con cédula de 10 dígitos válida.
     */
    private static String generarCedulaValida(int semilla) {
        // Usamos provincia 17 (Pichincha) y rellenamos con la semilla
        String base = String.format("17%07d", semilla);
        int suma = 0;
        for (int i = 0; i < 9; i++) {
            int digito = Character.getNumericValue(base.charAt(i));
            if (i % 2 == 0) { // Posiciones impares (0, 2, 4...) se multiplican por 2
                digito *= 2;
                if (digito > 9) digito -= 9;
            }
            suma += digito;
        }
        int digitoVerificador = (suma % 10 == 0) ? 0 : 10 - (suma % 10);
        return base + digitoVerificador;
    }
}
