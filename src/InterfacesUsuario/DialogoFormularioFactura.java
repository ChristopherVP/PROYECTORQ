//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package InterfacesUsuario;

import excepciones.PersistenciaException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import objetosNegocio.DetalleFactura;
import objetosNegocio.Empresa;
import objetosNegocio.Factura;
import objetosNegocio.Pasajero;
import objetosNegocio.Reserva;
import objetosNegocio.Usuario;
import objetosServicio.Fecha;
import persistencia.DetallesFactura;
import persistencia.Empresas;
import persistencia.Pasajeros;
import persistencia.Reservas;

public class DialogoFormularioFactura extends JDialog {
    private JTextField txtId;
    private JTextField txtTotal;
    private JTextField txtDia;
    private JTextField txtMes;
    private JTextField txtAnio;
    private JTextField txtEmpresa;
    private JTextField txtPasajero;
    private JTextField txtReserva;
    private JComboBox<String> cmbEstado;
    private JTable tablaDetalles;
    private DefaultTableModel modeloDetalles;
    private Factura factura;
    private boolean confirmado = false;
    private List<Empresa> empresas;
    private List<Reserva> reservas;
    private List<Pasajero> pasajeros;
    private Empresa empresaPreseleccionada;
    private Empresa empresaSeleccionadaObj;
    private Pasajero pasajeroSeleccionadoObj;
    private Reserva reservaSeleccionadaObj;
    private List<DetalleFactura> detallesGenerados;
    private Usuario usuarioActual;

    public DialogoFormularioFactura(JFrame padre, String titulo, Factura f, Empresa empresaPreseleccionada, Usuario usuario) {
        super(padre, titulo, true);
        this.factura = f;
        this.empresaPreseleccionada = empresaPreseleccionada;
        this.usuarioActual = usuario;
        this.cargarDatos();
        this.initComponents();
        this.detallesGenerados = new ArrayList();
        if (f != null) {
            this.cargar();
        }

    }

    private void cargarDatos() {
        try {
            this.empresas = (new Empresas("empresas.dat")).lista();
            this.reservas = (new Reservas("reservas.dat")).lista();
            this.pasajeros = (new Pasajeros("pasajeros.dat")).lista();
        } catch (PersistenciaException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar datos: " + e.getMessage());
            this.empresas = new ArrayList();
            this.reservas = new ArrayList();
            this.pasajeros = new ArrayList();
        }

    }

    private void initComponents() {
        this.setSize(600, 650);
        this.setLocationRelativeTo(this.getParent());
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints g = new GridBagConstraints();
        g.fill = 2;
        g.insets = new Insets(8, 8, 8, 8);
        g.gridx = 0;
        g.gridy = 0;
        g.anchor = 17;
        p.add(new JLabel("ID Factura:"), g);
        g.gridx = 1;
        this.txtId = new JTextField(20);
        this.txtId.setEnabled(false);
        this.txtId.setBackground(Color.LIGHT_GRAY);
        this.txtId.setText("(Se generará automáticamente)");
        p.add(this.txtId, g);
        g.gridx = 0;
        g.gridy = 1;
        p.add(new JLabel("Empresa: *"), g);
        g.gridx = 1;
        
        JPanel pEmpresa = new JPanel(new BorderLayout(5, 0));
        this.txtEmpresa = new JTextField();
        this.txtEmpresa.setEditable(false);
        JButton btnBuscarEmpresa = new JButton("🔍");
        btnBuscarEmpresa.addActionListener(e -> buscarEmpresa());
        pEmpresa.add(this.txtEmpresa, BorderLayout.CENTER);
        pEmpresa.add(btnBuscarEmpresa, BorderLayout.EAST);
        p.add(pEmpresa, g);

        g.gridx = 0;
        g.gridy = 2;
        p.add(new JLabel("Pasajero: *"), g);
        g.gridx = 1;
        
        JPanel pPasajero = new JPanel(new BorderLayout(5, 0));
        this.txtPasajero = new JTextField();
        this.txtPasajero.setEditable(false);
        JButton btnBuscarPasajero = new JButton("🔍");
        btnBuscarPasajero.addActionListener(e -> buscarPasajero());
        pPasajero.add(this.txtPasajero, BorderLayout.CENTER);
        pPasajero.add(btnBuscarPasajero, BorderLayout.EAST);
        p.add(pPasajero, g);

        g.gridx = 0;
        g.gridy = 3;
        p.add(new JLabel("Reserva: *"), g);
        g.gridx = 1;
        
        JPanel pReserva = new JPanel(new BorderLayout(5, 0));
        this.txtReserva = new JTextField();
        this.txtReserva.setEditable(false);
        JButton btnBuscarReserva = new JButton("🔍");
        btnBuscarReserva.addActionListener(e -> buscarReserva());
        pReserva.add(this.txtReserva, BorderLayout.CENTER);
        pReserva.add(btnBuscarReserva, BorderLayout.EAST);
        p.add(pReserva, g);

        g.gridx = 0;
        g.gridy = 4;
        p.add(new JLabel("Total: *"), g);
        g.gridx = 1;
        this.txtTotal = new JTextField(20);
        this.agregarFiltroDecimal(this.txtTotal);
        p.add(this.txtTotal, g);
        g.gridx = 0;
        g.gridy = 5;
        g.anchor = 18;
        JLabel lblFecha = new JLabel("Fecha Emisión: *");
        lblFecha.setFont(lblFecha.getFont().deriveFont(1));
        p.add(lblFecha, g);
        JPanel pFecha = new JPanel(new FlowLayout(0, 8, 0));
        pFecha.add(new JLabel("Día:"));
        this.txtDia = new JTextField(4);
        this.txtDia.setFont(new Font("Monospaced", 0, 14));
        this.agregarFiltroNumeros(this.txtDia, 2);
        this.agregarPlaceholder(this.txtDia, "DD");
        pFecha.add(this.txtDia);
        pFecha.add(new JLabel("/"));
        pFecha.add(new JLabel("Mes:"));
        this.txtMes = new JTextField(4);
        this.txtMes.setFont(new Font("Monospaced", 0, 14));
        this.agregarFiltroNumeros(this.txtMes, 2);
        this.agregarPlaceholder(this.txtMes, "MM");
        pFecha.add(this.txtMes);
        pFecha.add(new JLabel("/"));
        pFecha.add(new JLabel("Año:"));
        this.txtAnio = new JTextField(6);
        this.txtAnio.setFont(new Font("Monospaced", 0, 14));
        this.agregarFiltroNumeros(this.txtAnio, 4);
        this.agregarPlaceholder(this.txtAnio, "AAAA");
        pFecha.add(this.txtAnio);
        g.gridx = 1;
        g.gridy = 5;
        g.anchor = 17;
        p.add(pFecha, g);
        g.gridx = 0;
        g.gridy = 6;
        g.anchor = 17;
        p.add(new JLabel("Estado: *"), g);
        g.gridx = 1;
        this.cmbEstado = new JComboBox<>(new String[]{"Pagada", "Pendiente", "Cancelada"});
        p.add(this.cmbEstado, g);
        g.gridx = 0;
        g.gridy = 7;
        g.gridwidth = 2;
        JLabel lblNota = new JLabel("* Campos obligatorios");
        lblNota.setFont(new Font("Arial", 2, 11));
        lblNota.setForeground(Color.GRAY);
        p.add(lblNota, g);
        g.gridx = 0;
        g.gridy = 8;
        g.gridwidth = 2;
        g.fill = 1;
        g.weighty = (double)1.0F;
        String[] cols = new String[]{"Descripción", "Cant.", "P. Unit", "Subtotal"};
        this.modeloDetalles = new DefaultTableModel(cols, 0);
        this.tablaDetalles = new JTable(this.modeloDetalles);
        JScrollPane scrollDetalles = new JScrollPane(this.tablaDetalles);
        scrollDetalles.setBorder(BorderFactory.createTitledBorder("Detalles de la Factura"));
        scrollDetalles.setPreferredSize(new Dimension(500, 150));
        p.add(scrollDetalles, g);
        JPanel pBtn = new JPanel(new FlowLayout(1, 15, 10));
        JButton btnGuardar = new JButton("Guardar");
        JButton btnCancelar = new JButton("Cancelar");
        btnGuardar.setPreferredSize(new Dimension(120, 35));
        btnCancelar.setPreferredSize(new Dimension(120, 35));
        btnGuardar.addActionListener((ex) -> this.guardar());
        btnCancelar.addActionListener((ex) -> this.dispose());
        pBtn.add(btnGuardar);
        pBtn.add(btnCancelar);
        if (this.empresaPreseleccionada != null) {
            this.empresaSeleccionadaObj = this.empresaPreseleccionada;
            this.txtEmpresa.setText(this.empresaPreseleccionada.getNombre());
            btnBuscarEmpresa.setEnabled(false);
        }

        this.add(p, "Center");
        this.add(pBtn, "South");
    }

    private void buscarEmpresa() {
        DialogoBuscarEmpresa d = new DialogoBuscarEmpresa(this);
        d.setVisible(true);
        if (d.getEmpresaSeleccionada() != null) {
            this.empresaSeleccionadaObj = d.getEmpresaSeleccionada();
            this.txtEmpresa.setText(this.empresaSeleccionadaObj.getNombre());
            // Limpiar dependientes
            this.pasajeroSeleccionadoObj = null;
            this.txtPasajero.setText("");
            this.reservaSeleccionadaObj = null;
            this.txtReserva.setText("");
        }
    }

    private void buscarPasajero() {
        if (this.empresaSeleccionadaObj == null) {
            JOptionPane.showMessageDialog(this, "Seleccione una empresa primero.");
            return;
        }
        DialogoBuscarPasajero d = new DialogoBuscarPasajero(this, this.empresaSeleccionadaObj);
        d.setVisible(true);
        if (d.getPasajeroSeleccionado() != null) {
            this.pasajeroSeleccionadoObj = d.getPasajeroSeleccionado();
            this.txtPasajero.setText(this.pasajeroSeleccionadoObj.getNombre());
        }
    }

    private void buscarReserva() {
        if (this.empresaSeleccionadaObj == null) {
            JOptionPane.showMessageDialog(this, "Seleccione una empresa primero.");
            return;
        }
        DialogoBuscarReserva d = new DialogoBuscarReserva(this, this.empresaSeleccionadaObj);
        d.setVisible(true);
        if (d.getReservaSeleccionada() != null) {
            this.reservaSeleccionadaObj = d.getReservaSeleccionada();
            this.txtReserva.setText(this.reservaSeleccionadaObj.getIdReserva());
            this.actualizarDatosDesdeReserva();
        }
    }

    private void actualizarDatosDesdeReserva() {
        if (this.reservaSeleccionadaObj != null) {
            Reserva r = this.reservaSeleccionadaObj;
            double subtotal = r.getPrecioTotal();
            double total = subtotal * 1.15;
            this.txtTotal.setText(String.format("%.2f", total).replace(',', '.'));
            this.detallesGenerados.clear();
            this.modeloDetalles.setRowCount(0);
            DetalleFactura d1 = new DetalleFactura((String)null, (String)null, "Servicio de Transporte (Pasajes)", r.getCantidadAsientos(), r.getPrecioTotal() / (double)r.getCantidadAsientos());
            d1.setSubtotal(r.getPrecioTotal());
            this.detallesGenerados.add(d1);
            this.modeloDetalles.addRow(new Object[]{d1.getDescripcion(), d1.getCantidad(), String.format("$%.2f", d1.getPrecioUnitario()), String.format("$%.2f", d1.getSubtotal())});
            double iva = r.getPrecioTotal() * 0.15;
            DetalleFactura d2 = new DetalleFactura((String)null, (String)null, "Impuesto al Valor Agregado (15%)", 1, iva);
            this.detallesGenerados.add(d2);
            this.modeloDetalles.addRow(new Object[]{d2.getDescripcion(), d2.getCantidad(), String.format("$%.2f", d2.getPrecioUnitario()), String.format("$%.2f", d2.getSubtotal())});
            
            if (r.getPasajero() != null) {
                this.pasajeroSeleccionadoObj = r.getPasajero();
                this.txtPasajero.setText(this.pasajeroSeleccionadoObj.getNombre());
            }
        }
    }

    private void cargar() {
        this.txtId.setText(this.factura.getIdFactura());
        this.txtId.setEnabled(false);
        if (this.factura.getEmpresa() != null) {
            this.empresaSeleccionadaObj = this.factura.getEmpresa();
            this.txtEmpresa.setText(this.empresaSeleccionadaObj.getNombre());
        }

        if (this.factura.getPasajero() != null) {
            this.pasajeroSeleccionadoObj = this.factura.getPasajero();
            this.txtPasajero.setText(this.pasajeroSeleccionadoObj.getNombre());
        }

        if (this.factura.getReserva() != null) {
            this.reservaSeleccionadaObj = this.factura.getReserva();
            this.txtReserva.setText(this.reservaSeleccionadaObj.getIdReserva());
        }

        this.txtTotal.setText(String.valueOf(this.factura.getTotal()));
        this.cmbEstado.setSelectedItem(this.factura.getEstado());
        if (this.factura.getFechaEmision() != null) {
            Fecha f = this.factura.getFechaEmision();
            this.txtDia.setText(String.format("%02d", f.getDia()));
            this.txtMes.setText(String.format("%02d", f.getMes()));
            this.txtAnio.setText(String.valueOf(f.getAnio()));
        }

        try {
            DetallesFactura daoDetalles = new DetallesFactura("detalles_factura.dat");
            List<DetalleFactura> lista = daoDetalles.listaPorFactura(this.factura.getIdFactura());
            this.modeloDetalles.setRowCount(0);

            for(DetalleFactura d : lista) {
                this.modeloDetalles.addRow(new Object[]{d.getDescripcion(), d.getCantidad(), String.format("$%.2f", d.getPrecioUnitario()), String.format("$%.2f", d.getSubtotal())});
            }
        } catch (Exception var5) {
        }

    }

    private void guardar() {
        if (this.txtTotal.getText().trim().isEmpty()) {
            this.mostrarError("Complete el campo de Total");
            this.txtTotal.requestFocus();
        } else if (this.empresaSeleccionadaObj == null) {
            this.mostrarError("Seleccione una Empresa válida");
            // Focus logic
        } else if (this.pasajeroSeleccionadoObj == null) {
            this.mostrarError("Seleccione un Pasajero válido");
            // Focus logic
        } else if (this.reservaSeleccionadaObj == null) {
            this.mostrarError("Seleccione una Reserva válida");
            // Focus logic
        } else {
            String dia = this.txtDia.getText().trim();
            String mes = this.txtMes.getText().trim();
            String anio = this.txtAnio.getText().trim();
            if (!dia.isEmpty() && !mes.isEmpty() && !anio.isEmpty()) {
                try {
                    int d = Integer.parseInt(dia);
                    int m = Integer.parseInt(mes);
                    int a = Integer.parseInt(anio);
                    if (!this.validarFecha(d, m, a)) {
                        this.mostrarError("Fecha inválida\nDía: 1-31, Mes: 1-12, Año: mayor a 1900");
                        return;
                    }

                    double total = Double.parseDouble(this.txtTotal.getText().trim());
                    if (total <= (double)0.0F) {
                        this.mostrarError("El total debe ser un número positivo mayor a 0");
                        this.txtTotal.requestFocus();
                        return;
                    }

                    if (this.factura == null) {
                        this.factura = new Factura();
                    }

                    String idEmpresa = this.empresaSeleccionadaObj.getIdEmpresa();
                    String idPasajero = this.pasajeroSeleccionadoObj.getIdPasajero();
                    String idReserva = this.reservaSeleccionadaObj.getIdReserva();

                    for(Empresa e : this.empresas) {
                        if (e.getIdEmpresa().equals(idEmpresa)) {
                            this.factura.setNombreEmpresaSnapshot(e.getNombre());
                            this.factura.setRucEmpresaSnapshot(e.getRuc());
                            this.factura.setDireccionEmpresaSnapshot(e.getDireccion());
                            break;
                        }
                    }

                    if (this.usuarioActual != null) {
                        String vendedor = this.usuarioActual.getNombre();
                        if (this.usuarioActual.getCedula() != null && !this.usuarioActual.getCedula().isEmpty()) {
                            vendedor = vendedor + " (" + this.usuarioActual.getCedula() + ")";
                        }

                        this.factura.setNombreVendedor(vendedor);
                    }

                    this.factura.setEmpresa(new Empresa(idEmpresa));
                    this.factura.setPasajero(new Pasajero(idPasajero));
                    this.factura.setReserva(new Reserva(idReserva));
                    this.factura.setTotal(total);
                    this.factura.setEstado((String)this.cmbEstado.getSelectedItem());
                    Fecha fecha = new Fecha(d, m, a);
                    this.factura.setFechaEmision(fecha);
                    this.detallesGenerados.clear();
                    if (this.modeloDetalles.getRowCount() > 0) {
                        for(int i = 0; i < this.modeloDetalles.getRowCount(); ++i) {
                            String desc = (String)this.modeloDetalles.getValueAt(i, 0);
                            Object cantObj = this.modeloDetalles.getValueAt(i, 1);
                            Object puObj = this.modeloDetalles.getValueAt(i, 2);
                            Object subObj = this.modeloDetalles.getValueAt(i, 3);
                            int cantidad = Integer.parseInt(cantObj.toString());
                            double pUnit = Double.parseDouble(puObj.toString().replace("$", "").replace(",", "."));
                            double subtotalDetalle = Double.parseDouble(subObj.toString().replace("$", "").replace(",", "."));
                            DetalleFactura det = new DetalleFactura((String)null, (String)null, desc, cantidad, pUnit);
                            det.setSubtotal(subtotalDetalle);
                            this.detallesGenerados.add(det);
                        }
                    }

                    this.confirmado = true;
                    this.dispose();
                } catch (NumberFormatException var27) {
                    this.mostrarError("Los valores de fecha y total deben ser números válidos");
                } catch (Exception e) {
                    this.mostrarError("Error al guardar: " + e.getMessage());
                }

            } else {
                this.mostrarError("Complete todos los campos de fecha (Día, Mes, Año)");
                if (dia.isEmpty()) {
                    this.txtDia.requestFocus();
                } else if (mes.isEmpty()) {
                    this.txtMes.requestFocus();
                } else {
                    this.txtAnio.requestFocus();
                }

            }
        }
    }

    private boolean validarFecha(int dia, int mes, int anio) {
        if (dia >= 1 && dia <= 31) {
            if (mes >= 1 && mes <= 12) {
                if (anio >= 1900 && anio <= 2100) {
                    if (mes == 2 && dia > 29) {
                        return false;
                    } else {
                        return mes != 4 && mes != 6 && mes != 9 && mes != 11 || dia <= 30;
                    }
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error de Validación", 0);
    }

    private void agregarFiltroNumeros(final JTextField campo, final int maxDigitos) {
        campo.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                this.validar();
            }

            public void removeUpdate(DocumentEvent e) {
                this.validar();
            }

            public void insertUpdate(DocumentEvent e) {
                this.validar();
            }

            public void validar() {
                SwingUtilities.invokeLater(() -> {
                    String text = campo.getText();
                    String filtered = text.replaceAll("[^0-9]", "");
                    if (filtered.length() > maxDigitos) {
                        filtered = filtered.substring(0, maxDigitos);
                    }

                    if (!text.equals(filtered)) {
                        int pos = campo.getCaretPosition();
                        campo.setText(filtered);

                        try {
                            campo.setCaretPosition(Math.min(pos, filtered.length()));
                        } catch (IllegalArgumentException var6) {
                            campo.setCaretPosition(filtered.length());
                        }
                    }

                });
            }
        });
    }

    private void agregarFiltroDecimal(final JTextField campo) {
        campo.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                this.validar();
            }

            public void removeUpdate(DocumentEvent e) {
                this.validar();
            }

            public void insertUpdate(DocumentEvent e) {
                this.validar();
            }

            public void validar() {
                SwingUtilities.invokeLater(() -> {
                    String text = campo.getText();
                    String filtered = text.replaceAll("[^0-9.]", "");
                    int firstDot = filtered.indexOf(46);
                    if (firstDot != -1) {
                        String beforeDot = filtered.substring(0, firstDot + 1);
                        String afterDot = filtered.substring(firstDot + 1).replaceAll("\\.", "");
                        filtered = beforeDot + afterDot;
                    }

                    if (!text.equals(filtered)) {
                        int pos = campo.getCaretPosition();
                        campo.setText(filtered);

                        try {
                            campo.setCaretPosition(Math.min(pos, filtered.length()));
                        } catch (IllegalArgumentException var6) {
                            campo.setCaretPosition(filtered.length());
                        }
                    }

                });
            }
        });
    }

    private void agregarPlaceholder(JTextField campo, String placeholder) {
        campo.setToolTipText(placeholder);
    }

    public Factura getFactura() {
        return this.factura;
    }

    public boolean isConfirmado() {
        return this.confirmado;
    }

    public List<DetalleFactura> getDetallesGenerados() {
        return this.detallesGenerados;
    }
}
