//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package InterfacesUsuario;

import excepciones.PersistenciaException;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import objetosNegocio.Bus;
import objetosNegocio.Empresa;
import objetosNegocio.Factura;
import objetosNegocio.Pasajero;
import objetosNegocio.Reserva;
import objetosNegocio.Ruta;
import objetosNegocio.Usuario;
import objetosNegocio.Viaje;
import persistencia.Buses;
import persistencia.Empresas;
import persistencia.Facturas;
import persistencia.Pasajeros;
import persistencia.Reservas;
import persistencia.Rutas;
import persistencia.Usuarios;
import persistencia.Viajes;

public class PantallaReportes extends JDialog {
    private JList<String> listaReportes;
    private JTextArea areaResultados;
    private JButton btnLimpiar;
    private JButton btnCerrar;
    private Empresa empresaFiltro;

    public PantallaReportes(JFrame padre, Empresa empresaFiltro) {
        super(padre, "Reportes y Estadísticas", true);
        this.empresaFiltro = empresaFiltro;
        this.initComponents();
    }

    private void initComponents() {
        this.setSize(900, 600);
        this.setLocationRelativeTo(this.getParent());
        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        String[] reportes = new String[]{"Resumen General del Sistema", "Estadísticas de Viajes", "Estadísticas de Reservas", "Estadísticas de Facturas", "Estadísticas de Buses", "Estadísticas de Usuarios", "Estadísticas de Empresas", "Top 5 Rutas más Populares", "Top 5 Pasajeros más Activos", "Ingresos Totales"};
        this.listaReportes = new JList(reportes);
        this.listaReportes.setSelectionMode(0);
        this.listaReportes.setFont(new Font("Arial", 0, 13));
        this.listaReportes.addListSelectionListener((e) -> {
            if (!e.getValueIsAdjusting()) {
                this.generarReporte();
            }

        });
        JScrollPane scrollLista = new JScrollPane(this.listaReportes);
        scrollLista.setBorder(BorderFactory.createTitledBorder("Reportes Disponibles"));
        scrollLista.setPreferredSize(new Dimension(250, 0));
        this.areaResultados = new JTextArea();
        this.areaResultados.setEditable(false);
        this.areaResultados.setFont(new Font("Monospaced", 0, 12));
        JScrollPane scrollResultados = new JScrollPane(this.areaResultados);
        scrollResultados.setBorder(BorderFactory.createTitledBorder("Resultados"));
        JPanel panelBotones = new JPanel(new FlowLayout(1));
        this.btnLimpiar = new JButton("Limpiar");
        this.btnCerrar = new JButton("Cerrar");
        this.btnLimpiar.addActionListener((e) -> this.areaResultados.setText(""));
        this.btnCerrar.addActionListener((e) -> this.dispose());
        panelBotones.add(this.btnLimpiar);
        panelBotones.add(this.btnCerrar);
        panelPrincipal.add(scrollLista, "West");
        panelPrincipal.add(scrollResultados, "Center");
        this.add(panelPrincipal, "Center");
        this.add(panelBotones, "South");
    }

    private void generarReporte() {
        String reporte = (String)this.listaReportes.getSelectedValue();
        if (reporte != null) {
            try {
                StringBuilder resultado = new StringBuilder();
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                resultado.append("=".repeat(70)).append("\n");
                resultado.append(reporte.toUpperCase()).append("\n");
                resultado.append("Fecha de generación: ").append(sdf.format(new Date())).append("\n");
                resultado.append("=".repeat(70)).append("\n\n");
                switch (reporte) {
                    case "Resumen General del Sistema" -> this.generarResumenGeneral(resultado);
                    case "Estadísticas de Viajes" -> this.generarEstadisticasViajes(resultado);
                    case "Estadísticas de Reservas" -> this.generarEstadisticasReservas(resultado);
                    case "Estadísticas de Facturas" -> this.generarEstadisticasFacturas(resultado);
                    case "Estadísticas de Buses" -> this.generarEstadisticasBuses(resultado);
                    case "Estadísticas de Usuarios" -> this.generarEstadisticasUsuarios(resultado);
                    case "Estadísticas de Empresas" -> this.generarEstadisticasEmpresas(resultado);
                    case "Top 5 Rutas más Populares" -> this.generarTopRutas(resultado);
                    case "Top 5 Pasajeros más Activos" -> this.generarTopPasajeros(resultado);
                    case "Ingresos Totales" -> this.generarIngresosTotales(resultado);
                }

                this.areaResultados.setText(resultado.toString());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error al generar reporte: " + e.getMessage());
            }

        }
    }

    private void generarResumenGeneral(StringBuilder resultado) throws PersistenciaException {
        if (this.empresaFiltro != null) {
            resultado.append("RESUMEN DE EMPRESA: ").append(this.empresaFiltro.getNombre()).append("\n\n");
        }

        int totalBuses = (new Buses("buses.dat")).lista().size();
        int totalRutas = (new Rutas("rutas.dat")).lista().size();
        int totalViajes = (new Viajes("viajes.dat")).lista().size();
        int totalReservas = (new Reservas("reservas.dat")).lista().size();
        int totalFacturas = (new Facturas("facturas.dat")).lista().size();
        int totalUsuarios = (new Usuarios("usuarios.dat")).lista().size();
        int totalEmpresas = (new Empresas("empresas.dat")).lista().size();
        if (this.empresaFiltro != null) {
            String id = this.empresaFiltro.getIdEmpresa();
            totalBuses = (int)(new Buses("buses.dat")).lista().stream().filter((b) -> b.getEmpresa() != null && b.getEmpresa().getIdEmpresa().equals(id)).count();
            totalRutas = (int)(new Rutas("rutas.dat")).lista().stream().filter((r) -> r.getEmpresa() != null && r.getEmpresa().getIdEmpresa().equals(id)).count();
            totalViajes = (int)(new Viajes("viajes.dat")).lista().stream().filter((v) -> v.getEmpresa() != null && v.getEmpresa().getIdEmpresa().equals(id)).count();
            totalReservas = (int)(new Reservas("reservas.dat")).lista().stream().filter((r) -> r.getEmpresa() != null && r.getEmpresa().getIdEmpresa().equals(id)).count();
            totalFacturas = (int)(new Facturas("facturas.dat")).lista().stream().filter((f) -> f.getEmpresa() != null && f.getEmpresa().getIdEmpresa().equals(id)).count();
            totalUsuarios = (int)(new Usuarios("usuarios.dat")).lista().stream().filter((u) -> u.getEmpresa() != null && u.getEmpresa().getIdEmpresa().equals(id)).count();
            totalEmpresas = 1;
        }

        resultado.append("RESUMEN DEL SISTEMA\n\n");
        resultado.append(String.format("Total de Buses:        %d\n", totalBuses));
        resultado.append(String.format("Total de Rutas:        %d\n", totalRutas));
        resultado.append(String.format("Total de Viajes:       %d\n", totalViajes));
        resultado.append(String.format("Total de Reservas:     %d\n", totalReservas));
        resultado.append(String.format("Total de Facturas:     %d\n", totalFacturas));
        resultado.append(String.format("Total de Usuarios:     %d\n", totalUsuarios));
        resultado.append(String.format("Total de Empresas:     %d\n", totalEmpresas));
    }

    private void generarEstadisticasViajes(StringBuilder resultado) throws PersistenciaException {
        List<Viaje> viajes = (new Viajes("viajes.dat")).lista();
        int programados = 0;
        int enCurso = 0;
        int finalizados = 0;
        int cancelados = 0;

        for(int i = 0; i < viajes.size(); ++i) {
            Viaje v = (Viaje)viajes.get(i);
            if (this.empresaFiltro == null || v.getEmpresa() != null && v.getEmpresa().getIdEmpresa().equals(this.empresaFiltro.getIdEmpresa())) {
                String estado = v.getEstado();
                if ("Programado".equals(estado)) {
                    ++programados;
                } else if ("En Curso".equals(estado)) {
                    ++enCurso;
                } else if ("Finalizado".equals(estado)) {
                    ++finalizados;
                } else if ("Cancelado".equals(estado)) {
                    ++cancelados;
                }
            }
        }

        int total = programados + enCurso + finalizados + cancelados;
        resultado.append("ESTADÍSTICAS DE VIAJES\n\n");
        resultado.append(String.format("Total de viajes:       %d\n", total));
        if (total > 0) {
            resultado.append(String.format("Programados:           %d (%.1f%%)\n", programados, (double)programados * (double)100.0F / (double)total));
            resultado.append(String.format("En Curso:              %d (%.1f%%)\n", enCurso, (double)enCurso * (double)100.0F / (double)total));
            resultado.append(String.format("Finalizados:           %d (%.1f%%)\n", finalizados, (double)finalizados * (double)100.0F / (double)total));
            resultado.append(String.format("Cancelados:            %d (%.1f%%)\n", cancelados, (double)cancelados * (double)100.0F / (double)total));
        }

    }

    private void generarEstadisticasReservas(StringBuilder resultado) throws PersistenciaException {
        List<Reserva> reservas = (new Reservas("reservas.dat")).lista();
        int confirmadas = 0;
        int pendientes = 0;
        int canceladas = 0;
        int totalAsientos = 0;

        for(int i = 0; i < reservas.size(); ++i) {
            Reserva r = (Reserva)reservas.get(i);
            if (this.empresaFiltro == null || r.getEmpresa() != null && r.getEmpresa().getIdEmpresa().equals(this.empresaFiltro.getIdEmpresa())) {
                String estado = r.getEstado();
                totalAsientos += r.getCantidadAsientos();
                if ("Confirmada".equals(estado)) {
                    ++confirmadas;
                } else if ("Pendiente".equals(estado)) {
                    ++pendientes;
                } else if ("Cancelada".equals(estado)) {
                    ++canceladas;
                }
            }
        }

        int total = confirmadas + pendientes + canceladas;
        resultado.append("ESTADÍSTICAS DE RESERVAS\n\n");
        resultado.append(String.format("Total de reservas:     %d\n", total));
        resultado.append(String.format("Confirmadas:           %d\n", confirmadas));
        resultado.append(String.format("Pendientes:            %d\n", pendientes));
        resultado.append(String.format("Canceladas:            %d\n", canceladas));
        resultado.append(String.format("Total asientos reservados: %d\n", totalAsientos));
        if (total > 0) {
            resultado.append(String.format("Promedio asientos/reserva: %.2f\n", (double)totalAsientos * (double)1.0F / (double)total));
        }

    }

    private void generarEstadisticasFacturas(StringBuilder resultado) throws PersistenciaException {
        List<Factura> facturas = (new Facturas("facturas.dat")).lista();
        int pagadas = 0;
        int pendientes = 0;
        int canceladas = 0;
        double totalPagado = (double)0.0F;
        double totalPendiente = (double)0.0F;

        for(int i = 0; i < facturas.size(); ++i) {
            Factura f = (Factura)facturas.get(i);
            if (this.empresaFiltro == null || f.getEmpresa() != null && f.getEmpresa().getIdEmpresa().equals(this.empresaFiltro.getIdEmpresa())) {
                String estado = f.getEstado();
                if ("Pagada".equals(estado)) {
                    ++pagadas;
                    totalPagado += f.getTotal();
                } else if ("Pendiente".equals(estado)) {
                    ++pendientes;
                    totalPendiente += f.getTotal();
                } else if ("Cancelada".equals(estado)) {
                    ++canceladas;
                }
            }
        }

        int total = pagadas + pendientes + canceladas;
        resultado.append("ESTADÍSTICAS DE FACTURAS\n\n");
        resultado.append(String.format("Total de facturas:     %d\n", total));
        resultado.append(String.format("Pagadas:               %d\n", pagadas));
        resultado.append(String.format("Pendientes:            %d\n", pendientes));
        resultado.append(String.format("Canceladas:            %d\n", canceladas));
        resultado.append(String.format("\nMonto pagado:          $%.2f\n", totalPagado));
        resultado.append(String.format("Monto pendiente:       $%.2f\n", totalPendiente));
        resultado.append(String.format("Monto total:           $%.2f\n", totalPagado + totalPendiente));
    }

    private void generarEstadisticasBuses(StringBuilder resultado) throws PersistenciaException {
        List<Bus> buses = (new Buses("buses.dat")).lista();
        int activos = 0;
        int inactivos = 0;
        int mantenimiento = 0;
        int capacidadTotal = 0;

        for(int i = 0; i < buses.size(); ++i) {
            Bus b = (Bus)buses.get(i);
            if (this.empresaFiltro == null || b.getEmpresa() != null && b.getEmpresa().getIdEmpresa().equals(this.empresaFiltro.getIdEmpresa())) {
                String estado = b.getEstado();
                capacidadTotal += b.getCapacidad();
                if ("ACTIVO".equalsIgnoreCase(estado)) {
                    ++activos;
                } else if ("INACTIVO".equalsIgnoreCase(estado)) {
                    ++inactivos;
                } else if ("MANTENIMIENTO".equalsIgnoreCase(estado)) {
                    ++mantenimiento;
                }
            }
        }

        int total = activos + inactivos + mantenimiento;
        resultado.append("ESTADÍSTICAS DE BUSES\n\n");
        resultado.append(String.format("Total de buses:        %d\n", total));
        resultado.append(String.format("Activos:               %d\n", activos));
        resultado.append(String.format("Inactivos:             %d\n", inactivos));
        resultado.append(String.format("En mantenimiento:      %d\n", mantenimiento));
        resultado.append(String.format("\nCapacidad total:       %d asientos\n", capacidadTotal));
        if (total > 0) {
            resultado.append(String.format("Capacidad promedio:    %.1f asientos\n", (double)capacidadTotal * (double)1.0F / (double)total));
        }

    }

    private void generarEstadisticasUsuarios(StringBuilder resultado) throws PersistenciaException {
        List<Usuario> usuarios = (new Usuarios("usuarios.dat")).lista();
        int clientes = 0;
        int administradores = 0;
        int operadores = 0;

        for(int i = 0; i < usuarios.size(); ++i) {
            Usuario u = (Usuario)usuarios.get(i);
            if (this.empresaFiltro == null || u.getEmpresa() != null && u.getEmpresa().getIdEmpresa().equals(this.empresaFiltro.getIdEmpresa())) {
                String tipo = u.getTipo();
                if ("Cliente".equals(tipo) || "Pasajero".equals(tipo)) {
                    ++clientes;
                } else if ("Administrador".equals(tipo)) {
                    ++administradores;
                } else if ("Operador".equals(tipo)) {
                    ++operadores;
                }
            }
        }

        int total = clientes + administradores + operadores;
        resultado.append("ESTADÍSTICAS DE USUARIOS\n\n");
        resultado.append(String.format("Total de usuarios:     %d\n", total));
        resultado.append(String.format("Clientes:              %d\n", clientes));
        resultado.append(String.format("Administradores:       %d\n", administradores));
        resultado.append(String.format("Operadores:            %d\n", operadores));
    }

    private void generarEstadisticasEmpresas(StringBuilder resultado) throws PersistenciaException {
        List<Empresa> empresas = (new Empresas("empresas.dat")).lista();
        List<Bus> buses = (new Buses("buses.dat")).lista();
        resultado.append("ESTADÍSTICAS DE EMPRESAS\n\n");
        if (this.empresaFiltro != null) {
            resultado.append("Mostrando datos solo para: ").append(this.empresaFiltro.getNombre()).append("\n\n");
        } else {
            resultado.append(String.format("Total de empresas:     %d\n\n", empresas.size()));
        }

        resultado.append("Buses por empresa:\n");

        for(int i = 0; i < empresas.size(); ++i) {
            Empresa e = (Empresa)empresas.get(i);
            if (this.empresaFiltro == null || e.getIdEmpresa().equals(this.empresaFiltro.getIdEmpresa())) {
                int contador = 0;

                for(int j = 0; j < buses.size(); ++j) {
                    Bus b = (Bus)buses.get(j);
                    if (b.getEmpresa() != null && b.getEmpresa().getIdEmpresa().equals(e.getIdEmpresa())) {
                        ++contador;
                    }
                }

                resultado.append(String.format("  %s: %d buses\n", e.getNombre(), contador));
            }
        }

    }

    private void generarTopRutas(StringBuilder resultado) throws PersistenciaException {
        resultado.append("TOP 5 RUTAS MÁS POPULARES\n\n");
        resultado.append("(Basado en número de viajes programados)\n\n");
        Rutas persistenciaRutas = new Rutas("rutas.dat");
        Viajes persistenciaViajes = new Viajes("viajes.dat");
        List<Ruta> rutas = persistenciaRutas.lista();
        List<Viaje> viajes = persistenciaViajes.lista();

        for(int i = 0; i < Math.min(rutas.size(), 5); ++i) {
            Ruta r = (Ruta)rutas.get(i);
            if (this.empresaFiltro == null || r.getEmpresa() != null && r.getEmpresa().getIdEmpresa().equals(this.empresaFiltro.getIdEmpresa())) {
                int contador = 0;

                for(int j = 0; j < viajes.size(); ++j) {
                    Viaje v = (Viaje)viajes.get(j);
                    if (v.getRuta() != null && v.getRuta().getIdRuta().equals(r.getIdRuta())) {
                        ++contador;
                    }
                }

                resultado.append(String.format("%d. %s -> %s (%d viajes)\n", i + 1, r.getOrigen(), r.getDestino(), contador));
            }
        }

    }

    private void generarTopPasajeros(StringBuilder resultado) throws PersistenciaException {
        resultado.append("TOP 5 PASAJEROS MÁS ACTIVOS\n\n");
        resultado.append("(Basado en número de reservas)\n\n");
        Pasajeros persistenciaPasajeros = new Pasajeros("pasajeros.dat");
        Reservas persistenciaReservas = new Reservas("reservas.dat");
        List<Pasajero> pasajeros = persistenciaPasajeros.lista();
        List<Reserva> reservas = persistenciaReservas.lista();

        for(int i = 0; i < Math.min(pasajeros.size(), 5); ++i) {
            Pasajero p = (Pasajero)pasajeros.get(i);
            if (this.empresaFiltro == null || p.getEmpresa() != null && p.getEmpresa().getIdEmpresa().equals(this.empresaFiltro.getIdEmpresa())) {
                int contador = 0;

                for(int j = 0; j < reservas.size(); ++j) {
                    Reserva r = (Reserva)reservas.get(j);
                    if (r.getPasajero() != null && r.getPasajero().getIdPasajero().equals(p.getIdPasajero())) {
                        ++contador;
                    }
                }

                resultado.append(String.format("%d. %s (%d reservas)\n", i + 1, p.getNombre(), contador));
            }
        }

    }

    private void generarIngresosTotales(StringBuilder resultado) throws PersistenciaException {
        List<Factura> facturas = (new Facturas("facturas.dat")).lista();
        double totalPagado = (double)0.0F;
        double totalPendiente = (double)0.0F;
        double totalCancelado = (double)0.0F;

        for(int i = 0; i < facturas.size(); ++i) {
            Factura f = (Factura)facturas.get(i);
            if (this.empresaFiltro == null || f.getEmpresa() != null && f.getEmpresa().getIdEmpresa().equals(this.empresaFiltro.getIdEmpresa())) {
                if ("Pagada".equals(f.getEstado())) {
                    totalPagado += f.getTotal();
                } else if ("Pendiente".equals(f.getEstado())) {
                    totalPendiente += f.getTotal();
                } else if ("Cancelada".equals(f.getEstado())) {
                    totalCancelado += f.getTotal();
                }
            }
        }

        resultado.append("INGRESOS TOTALES\n\n");
        resultado.append(String.format("Ingresos recibidos (pagadas):  $%,.2f\n", totalPagado));
        resultado.append(String.format("Ingresos pendientes:           $%,.2f\n", totalPendiente));
        resultado.append(String.format("Ingresos cancelados:           $%,.2f\n", totalCancelado));
        resultado.append("\n");
        resultado.append(String.format("TOTAL ESPERADO:                $%,.2f\n", totalPagado + totalPendiente));
    }
}