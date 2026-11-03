//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package persistencia;

import excepciones.PersistenciaException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import objetosNegocio.Asiento;
import objetosNegocio.Bus;
import objetosNegocio.Empresa;
import objetosNegocio.Factura;
import objetosNegocio.Pago;
import objetosNegocio.Reserva;
import objetosNegocio.Ruta;
import objetosNegocio.Usuario;
import objetosNegocio.Viaje;

public class ExportadorTXT {
    private static final String SEPARADOR = "================================================================================";
    private static final String SUBSEPARADOR = "--------------------------------------------------------------------------------";
    private static final String DIRECTORIO_REPORTES = "reportes_txt/";

    public ExportadorTXT() {
        File directorio = new File("reportes_txt/");
        if (!directorio.exists()) {
            directorio.mkdirs();
        }

    }

    public void exportarBuses(List<Bus> buses, String nombreArchivo) throws PersistenciaException {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("reportes_txt/" + nombreArchivo + ".txt"));
            writer.write("================================================================================");
            writer.newLine();
            writer.write("                    REPORTE DE BUSES - SISTEMA DE TRANSPORTE INTERPROVINCIAL");
            writer.newLine();
            writer.write("================================================================================");
            writer.newLine();
            writer.write("Fecha de generación: " + this.obtenerFechaActual());
            writer.newLine();
            writer.write("Total de buses: " + buses.size());
            writer.newLine();
            writer.write("================================================================================");
            writer.newLine();
            writer.newLine();
            int contador = 1;

            for(Bus bus : buses) {
                writer.write("BUS #" + contador);
                writer.newLine();
                writer.write("--------------------------------------------------------------------------------");
                writer.newLine();
                writer.write("  ID Bus:          " + bus.getIdBus());
                writer.newLine();
                writer.write("  Placa:           " + bus.getPlaca());
                writer.newLine();
                writer.write("  Marca:           " + bus.getMarca());
                writer.newLine();
                String var10001 = bus.getNumeroChasis() != null ? bus.getNumeroChasis() : "N/A";
                writer.write("  Nº Chasis:       " + var10001);
                writer.newLine();
                var10001 = bus.getNumeroMotor() != null ? bus.getNumeroMotor() : "N/A";
                writer.write("  Nº Motor:        " + var10001);
                writer.newLine();
                Object var9 = bus.getAnioFabricacion() > 0 ? bus.getAnioFabricacion() : "N/A";
                writer.write("  Año:             " + String.valueOf(var9));
                writer.newLine();
                writer.write("  Capacidad:       " + bus.getCapacidad() + " pasajeros");
                writer.newLine();
                writer.write("  Estado:          " + bus.getEstado());
                writer.newLine();
                if (bus.getEmpresa() != null) {
                    String var10 = bus.getEmpresa().getNombre();
                    writer.write("  Empresa:         " + var10 + " (ID: " + bus.getEmpresa().getIdEmpresa() + ")");
                } else {
                    writer.write("  Empresa:         No asignada");
                }

                writer.newLine();
                writer.newLine();
                ++contador;
            }

            writer.write("================================================================================");
            writer.write("\nFin del reporte");
            writer.newLine();
            writer.close();
            System.out.println("✓ Reporte de buses exportado exitosamente a: reportes_txt/" + nombreArchivo + ".txt");
        } catch (IOException e) {
            throw new PersistenciaException("Error al exportar buses a TXT", e);
        }
    }

    public void exportarRutas(List<Ruta> rutas, String nombreArchivo) throws PersistenciaException {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("reportes_txt/" + nombreArchivo + ".txt"));
            writer.write("================================================================================");
            writer.newLine();
            writer.write("                    REPORTE DE RUTAS - SISTEMA DE TRANSPORTE INTERPROVINCIAL");
            writer.newLine();
            writer.write("================================================================================");
            writer.newLine();
            writer.write("Fecha de generación: " + this.obtenerFechaActual());
            writer.newLine();
            writer.write("Total de rutas: " + rutas.size());
            writer.newLine();
            writer.write("================================================================================");
            writer.newLine();
            writer.newLine();
            int contador = 1;

            for(Ruta ruta : rutas) {
                writer.write("RUTA #" + contador);
                writer.newLine();
                writer.write("--------------------------------------------------------------------------------");
                writer.newLine();
                writer.write("  ID Ruta:         " + ruta.getIdRuta());
                writer.newLine();
                writer.write("  Origen:          " + ruta.getOrigen());
                writer.newLine();
                writer.write("  Destino:         " + ruta.getDestino());
                writer.newLine();
                Object[] var10002 = new Object[]{ruta.getDistancia()};
                writer.write("  Distancia:       " + String.format("%.2f", var10002) + " km");
                writer.newLine();
                var10002 = new Object[]{ruta.getPrecio()};
                writer.write("  Precio:          $" + String.format("%.2f", var10002));
                writer.newLine();
                writer.newLine();
                ++contador;
            }

            writer.write("================================================================================");
            writer.write("\nFin del reporte");
            writer.newLine();
            writer.close();
            System.out.println("✓ Reporte de rutas exportado exitosamente a: reportes_txt/" + nombreArchivo + ".txt");
        } catch (IOException e) {
            throw new PersistenciaException("Error al exportar rutas a TXT", e);
        }
    }

    public void exportarUsuarios(List<Usuario> usuarios, String nombreArchivo) throws PersistenciaException {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("reportes_txt/" + nombreArchivo + ".txt"));
            writer.write("================================================================================");
            writer.newLine();
            writer.write("                   REPORTE DE USUARIOS - SISTEMA DE TRANSPORTE INTERPROVINCIAL");
            writer.newLine();
            writer.write("================================================================================");
            writer.newLine();
            writer.write("Fecha de generación: " + this.obtenerFechaActual());
            writer.newLine();
            writer.write("Total de usuarios: " + usuarios.size());
            writer.newLine();
            writer.write("================================================================================");
            writer.newLine();
            writer.newLine();
            int contador = 1;

            for(Usuario usuario : usuarios) {
                writer.write("USUARIO #" + contador);
                writer.newLine();
                writer.write("--------------------------------------------------------------------------------");
                writer.newLine();
                writer.write("  ID Usuario:      " + usuario.getIdUsuario());
                writer.newLine();
                writer.write("  Nombre:          " + usuario.getNombre());
                writer.newLine();
                writer.write("  Email:           " + usuario.getEmail());
                writer.newLine();
                writer.write("  Teléfono:        " + usuario.getTelefono());
                writer.newLine();
                writer.write("  Tipo:            " + usuario.getTipo());
                writer.newLine();
                if (usuario.getFechaRegistro() != null) {
                    writer.write("  Fecha Registro:  " + usuario.getFechaRegistro().toString());
                }

                writer.newLine();
                writer.newLine();
                ++contador;
            }

            writer.write("================================================================================");
            writer.write("\nFin del reporte");
            writer.newLine();
            writer.close();
            System.out.println("✓ Reporte de usuarios exportado exitosamente a: reportes_txt/" + nombreArchivo + ".txt");
        } catch (IOException e) {
            throw new PersistenciaException("Error al exportar usuarios a TXT", e);
        }
    }

    public void exportarEmpresas(List<Empresa> empresas, String nombreArchivo) throws PersistenciaException {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("reportes_txt/" + nombreArchivo + ".txt"));
            writer.write("================================================================================");
            writer.newLine();
            writer.write("                  REPORTE DE EMPRESAS - SISTEMA DE TRANSPORTE INTERPROVINCIAL");
            writer.newLine();
            writer.write("================================================================================");
            writer.newLine();
            writer.write("Fecha de generación: " + this.obtenerFechaActual());
            writer.newLine();
            writer.write("Total de empresas: " + empresas.size());
            writer.newLine();
            writer.write("================================================================================");
            writer.newLine();
            writer.newLine();
            int contador = 1;

            for(Empresa empresa : empresas) {
                writer.write("EMPRESA #" + contador);
                writer.newLine();
                writer.write("--------------------------------------------------------------------------------");
                writer.newLine();
                writer.write("  ID Empresa:      " + empresa.getIdEmpresa());
                writer.newLine();
                writer.write("  Nombre:          " + empresa.getNombre());
                writer.newLine();
                writer.write("  Dirección:       " + empresa.getDireccion());
                writer.newLine();
                writer.write("  Teléfono:        " + empresa.getTelefono());
                writer.newLine();
                writer.write("  Email:           " + empresa.getEmail());
                writer.newLine();
                writer.newLine();
                ++contador;
            }

            writer.write("================================================================================");
            writer.write("\nFin del reporte");
            writer.newLine();
            writer.close();
            System.out.println("✓ Reporte de empresas exportado exitosamente a: reportes_txt/" + nombreArchivo + ".txt");
        } catch (IOException e) {
            throw new PersistenciaException("Error al exportar empresas a TXT", e);
        }
    }

    public void exportarViajes(List<Viaje> viajes, String nombreArchivo) throws PersistenciaException {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("reportes_txt/" + nombreArchivo + ".txt"));
            writer.write("================================================================================");
            writer.newLine();
            writer.write("                   REPORTE DE VIAJES - SISTEMA DE TRANSPORTE INTERPROVINCIAL");
            writer.newLine();
            writer.write("================================================================================");
            writer.newLine();
            writer.write("Fecha de generación: " + this.obtenerFechaActual());
            writer.newLine();
            writer.write("Total de viajes: " + viajes.size());
            writer.newLine();
            writer.write("================================================================================");
            writer.newLine();
            writer.newLine();
            int contador = 1;

            for(Viaje viaje : viajes) {
                writer.write("VIAJE #" + contador);
                writer.newLine();
                writer.write("--------------------------------------------------------------------------------");
                writer.newLine();
                writer.write("  ID Viaje:        " + viaje.getIdViaje());
                writer.newLine();
                if (viaje.getBus() != null) {
                    String var10001 = viaje.getBus().getPlaca();
                    writer.write("  Bus:             " + var10001 + " (ID: " + viaje.getBus().getIdBus() + ")");
                    writer.newLine();
                }

                if (viaje.getRuta() != null) {
                    String var8 = viaje.getRuta().getOrigen();
                    writer.write("  Ruta:            " + var8 + " → " + viaje.getRuta().getDestino());
                    writer.newLine();
                    writer.write("                   (ID: " + viaje.getRuta().getIdRuta() + ")");
                    writer.newLine();
                }

                if (viaje.getFechaSalida() != null) {
                    writer.write("  Fecha Salida:    " + viaje.getFechaSalida().toString());
                    writer.newLine();
                }

                if (viaje.getFechaLlegada() != null) {
                    writer.write("  Fecha Llegada:   " + viaje.getFechaLlegada().toString());
                    writer.newLine();
                }

                writer.write("  Asientos Disp.:  " + viaje.getAsientosDisponibles());
                writer.newLine();
                writer.write("  Estado:          " + viaje.getEstado());
                writer.newLine();
                writer.newLine();
                ++contador;
            }

            writer.write("================================================================================");
            writer.write("\nFin del reporte");
            writer.newLine();
            writer.close();
            System.out.println("✓ Reporte de viajes exportado exitosamente a: reportes_txt/" + nombreArchivo + ".txt");
        } catch (IOException e) {
            throw new PersistenciaException("Error al exportar viajes a TXT", e);
        }
    }

    public void exportarReservas(List<Reserva> reservas, String nombreArchivo) throws PersistenciaException {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("reportes_txt/" + nombreArchivo + ".txt"));
            writer.write("================================================================================");
            writer.newLine();
            writer.write("                  REPORTE DE RESERVAS - SISTEMA DE TRANSPORTE INTERPROVINCIAL");
            writer.newLine();
            writer.write("================================================================================");
            writer.newLine();
            writer.write("Fecha de generación: " + this.obtenerFechaActual());
            writer.newLine();
            writer.write("Total de reservas: " + reservas.size());
            writer.newLine();
            writer.write("================================================================================");
            writer.newLine();
            writer.newLine();
            int contador = 1;

            for(Reserva reserva : reservas) {
                writer.write("RESERVA #" + contador);
                writer.newLine();
                writer.write("--------------------------------------------------------------------------------");
                writer.newLine();
                writer.write("  ID Reserva:      " + reserva.getIdReserva());
                writer.newLine();
                if (reserva.getPasajero() != null) {
                    String var10001 = reserva.getPasajero().getNombre();
                    writer.write("  Pasajero:        " + var10001 + " (ID: " + reserva.getPasajero().getIdPasajero() + ")");
                    writer.newLine();
                }

                if (reserva.getViaje() != null) {
                    writer.write("  Viaje:           " + reserva.getViaje().getIdViaje());
                    writer.newLine();
                }

                writer.write("  Cant. Asientos:  " + reserva.getCantidadAsientos());
                writer.newLine();
                if (reserva.getFechaReserva() != null) {
                    writer.write("  Fecha Reserva:   " + reserva.getFechaReserva().toString());
                    writer.newLine();
                }

                writer.write("  Estado:          " + reserva.getEstado());
                writer.newLine();
                writer.newLine();
                ++contador;
            }

            writer.write("================================================================================");
            writer.write("\nFin del reporte");
            writer.newLine();
            writer.close();
            System.out.println("✓ Reporte de reservas exportado exitosamente a: reportes_txt/" + nombreArchivo + ".txt");
        } catch (IOException e) {
            throw new PersistenciaException("Error al exportar reservas a TXT", e);
        }
    }

    public void exportarFacturas(List<Factura> facturas, String nombreArchivo) throws PersistenciaException {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("reportes_txt/" + nombreArchivo + ".txt"));
            writer.write("================================================================================");
            writer.newLine();
            writer.write("                  REPORTE DE FACTURAS - SISTEMA DE TRANSPORTE INTERPROVINCIAL");
            writer.newLine();
            writer.write("================================================================================");
            writer.newLine();
            writer.write("Fecha de generación: " + this.obtenerFechaActual());
            writer.newLine();
            writer.write("Total de facturas: " + facturas.size());
            writer.newLine();
            writer.write("================================================================================");
            writer.newLine();
            writer.newLine();
            int contador = 1;

            for(Factura factura : facturas) {
                writer.write("FACTURA #" + contador);
                writer.newLine();
                writer.write("--------------------------------------------------------------------------------");
                writer.newLine();
                writer.write("  ID Factura:      " + factura.getIdFactura());
                writer.newLine();
                if (factura.getPasajero() != null) {
                    String var10001 = factura.getPasajero().getNombre();
                    writer.write("  Pasajero:        " + var10001 + " (ID: " + factura.getPasajero().getIdPasajero() + ")");
                    writer.newLine();
                }

                Object[] var10002 = new Object[]{factura.getTotal()};
                writer.write("  Total:           $" + String.format("%.2f", var10002));
                writer.newLine();
                if (factura.getFechaEmision() != null) {
                    writer.write("  Fecha Emisión:   " + factura.getFechaEmision().toString());
                    writer.newLine();
                }

                writer.write("  Estado:          " + factura.getEstado());
                writer.newLine();
                writer.newLine();
                ++contador;
            }

            writer.write("================================================================================");
            writer.write("\nFin del reporte");
            writer.newLine();
            writer.close();
            System.out.println("✓ Reporte de facturas exportado exitosamente a: reportes_txt/" + nombreArchivo + ".txt");
        } catch (IOException e) {
            throw new PersistenciaException("Error al exportar facturas a TXT", e);
        }
    }

    public void exportarPagos(List<Pago> pagos, String nombreArchivo) throws PersistenciaException {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("reportes_txt/" + nombreArchivo + ".txt"));
            writer.write("================================================================================");
            writer.newLine();
            writer.write("                    REPORTE DE PAGOS - SISTEMA DE TRANSPORTE INTERPROVINCIAL");
            writer.newLine();
            writer.write("================================================================================");
            writer.newLine();
            writer.write("Fecha de generación: " + this.obtenerFechaActual());
            writer.newLine();
            writer.write("Total de pagos: " + pagos.size());
            writer.newLine();
            writer.write("================================================================================");
            writer.newLine();
            writer.newLine();
            int contador = 1;

            for(Pago pago : pagos) {
                writer.write("PAGO #" + contador);
                writer.newLine();
                writer.write("--------------------------------------------------------------------------------");
                writer.newLine();
                writer.write("  ID Pago:         " + pago.getIdPago());
                writer.newLine();
                if (pago.getUsuario() != null) {
                    String var10001 = pago.getUsuario().getNombre();
                    writer.write("  Usuario:         " + var10001 + " (ID: " + pago.getUsuario().getIdUsuario() + ")");
                    writer.newLine();
                }

                if (pago.getFactura() != null) {
                    writer.write("  Factura:         " + pago.getFactura().getIdFactura());
                    writer.newLine();
                }

                Object[] var10002 = new Object[]{pago.getMonto()};
                writer.write("  Monto:           $" + String.format("%.2f", var10002));
                writer.newLine();
                writer.write("  Método Pago:     " + pago.getMetodoPago());
                writer.newLine();
                if (pago.getFechaPago() != null) {
                    writer.write("  Fecha Pago:      " + pago.getFechaPago().toString());
                    writer.newLine();
                }

                writer.write("  Estado:          " + pago.getEstado());
                writer.newLine();
                writer.newLine();
                ++contador;
            }

            writer.write("================================================================================");
            writer.write("\nFin del reporte");
            writer.newLine();
            writer.close();
            System.out.println("✓ Reporte de pagos exportado exitosamente a: reportes_txt/" + nombreArchivo + ".txt");
        } catch (IOException e) {
            throw new PersistenciaException("Error al exportar pagos a TXT", e);
        }
    }

    public void exportarAsientos(List<Asiento> asientos, String nombreArchivo) throws PersistenciaException {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("reportes_txt/" + nombreArchivo + ".txt"));
            writer.write("================================================================================");
            writer.newLine();
            writer.write("                  REPORTE DE ASIENTOS - SISTEMA DE TRANSPORTE INTERPROVINCIAL");
            writer.newLine();
            writer.write("================================================================================");
            writer.newLine();
            writer.write("Fecha de generación: " + this.obtenerFechaActual());
            writer.newLine();
            writer.write("Total de asientos: " + asientos.size());
            writer.newLine();
            writer.write("================================================================================");
            writer.newLine();
            writer.newLine();

            for(Asiento asiento : asientos) {
                writer.write("  Asiento #" + asiento.getNumeroAsiento() + " | ");
                Object[] var10002 = new Object[]{asiento.getUbicacion()};
                writer.write(String.format("%-12s", var10002) + " | ");
                var10002 = new Object[]{asiento.getEstado()};
                writer.write(String.format("%-12s", var10002) + " | ");
                writer.write("Viaje: " + asiento.getIdViaje());
                if (asiento.getIdReserva() != null && !asiento.getIdReserva().isEmpty()) {
                    writer.write(" | Reserva: " + asiento.getIdReserva());
                }

                writer.newLine();
            }

            writer.newLine();
            writer.write("================================================================================");
            writer.write("\nFin del reporte");
            writer.newLine();
            writer.close();
            System.out.println("✓ Reporte de asientos exportado exitosamente a: reportes_txt/" + nombreArchivo + ".txt");
        } catch (IOException e) {
            throw new PersistenciaException("Error al exportar asientos a TXT", e);
        }
    }

    public void exportarMapaAsientos(List<Asiento> asientos, String idViaje, String nombreArchivo) throws PersistenciaException {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("reportes_txt/" + nombreArchivo + ".txt"));
            writer.write("================================================================================");
            writer.newLine();
            writer.write("                     MAPA DE ASIENTOS - VIAJE " + idViaje);
            writer.newLine();
            writer.write("================================================================================");
            writer.newLine();
            writer.write("Fecha de generación: " + this.obtenerFechaActual());
            writer.newLine();
            writer.newLine();
            writer.write("LEYENDA:");
            writer.newLine();
            writer.write("  [D] = Disponible    [R] = Reservado    [O] = Ocupado");
            writer.newLine();
            writer.write("--------------------------------------------------------------------------------");
            writer.newLine();
            writer.newLine();
            writer.write("                    [CONDUCTOR]");
            writer.newLine();
            writer.write("                  ________________");
            writer.newLine();
            writer.newLine();
            int asientosPorFila = 4;
            int totalAsientos = asientos.size();
            int filas = (int)Math.ceil((double)totalAsientos / (double)asientosPorFila);

            for(int fila = 0; fila < filas; ++fila) {
                Object[] var10002 = new Object[]{fila + 1};
                writer.write("  Fila " + String.format("%2d", var10002) + "   ");

                for(int pos = 0; pos < asientosPorFila; ++pos) {
                    int indiceAsiento = fila * asientosPorFila + pos;
                    if (indiceAsiento < totalAsientos) {
                        Asiento asiento = (Asiento)asientos.get(indiceAsiento);
                        String simbolo = this.obtenerSimboloEstado(asiento.getEstado());
                        writer.write("[" + simbolo + "-" + String.format("%02d", asiento.getNumeroAsiento()) + "]");
                    } else {
                        writer.write("      ");
                    }

                    if (pos == 1) {
                        writer.write("    ");
                    } else if (pos < asientosPorFila - 1) {
                        writer.write(" ");
                    }
                }

                writer.newLine();
            }

            writer.newLine();
            writer.write("--------------------------------------------------------------------------------");
            writer.newLine();
            writer.write("ESTADÍSTICAS:");
            writer.newLine();
            int disponibles = 0;
            int reservados = 0;
            int ocupados = 0;

            for(Asiento asiento : asientos) {
                if ("Disponible".equals(asiento.getEstado())) {
                    ++disponibles;
                } else if ("Reservado".equals(asiento.getEstado())) {
                    ++reservados;
                } else if ("Ocupado".equals(asiento.getEstado())) {
                    ++ocupados;
                }
            }

            writer.write("  Total de asientos:  " + totalAsientos);
            writer.newLine();
            writer.write("  Disponibles:        " + disponibles);
            writer.newLine();
            writer.write("  Reservados:         " + reservados);
            writer.newLine();
            writer.write("  Ocupados:           " + ocupados);
            writer.newLine();
            writer.newLine();
            writer.write("================================================================================");
            writer.write("\nFin del mapa de asientos");
            writer.newLine();
            writer.close();
            System.out.println("✓ Mapa de asientos exportado exitosamente a: reportes_txt/" + nombreArchivo + ".txt");
        } catch (IOException e) {
            throw new PersistenciaException("Error al exportar mapa de asientos a TXT", e);
        }
    }

    private String obtenerFechaActual() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return sdf.format(new Date());
    }

    private String obtenerSimboloEstado(String estado) {
        if ("Disponible".equals(estado)) {
            return "D";
        } else if ("Reservado".equals(estado)) {
            return "R";
        } else {
            return "Ocupado".equals(estado) ? "O" : "?";
        }
    }

    public String getDirectorioReportes() {
        return "reportes_txt/";
    }
}
