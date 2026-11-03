//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package InterfacesUsuario;

import excepciones.PersistenciaException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import objetosNegocio.Bus;
import objetosNegocio.Empresa;
import persistencia.Buses;
import persistencia.Empresas;

public class DialogoFormularioBus extends JDialog {
    private JTextField txtIdBus;
    private JTextField txtPlaca;
    private JTextField txtChasis;
    private JTextField txtMotor;
    private JTextField txtMarca;
    private JTextField txtAnio;
    private JTextField txtCapacidad;
    private JSpinner spinFilas;
    private JComboBox<String> cmbDistribucion;
    private JComboBox<String> cmbEstado;
    private JComboBox<String> cmbEmpresa;
    private JButton btnGuardar;
    private JButton btnCancelar;
    private Bus busActual;
    private boolean confirmado = false;
    private List<Empresa> empresas;
    private Empresa empresaPreseleccionada;

    public DialogoFormularioBus(JFrame padre, String titulo, Bus bus, Empresa empresaPreseleccionada) {
        super(padre, titulo, true);
        this.busActual = bus;
        this.empresaPreseleccionada = empresaPreseleccionada;
        this.cargarEmpresas();
        this.initComponents();
        if (bus != null) {
            this.cargarDatosBus();
        }

    }

    private void cargarEmpresas() {
        try {
            Empresas persistenciaEmpresas = new Empresas("empresas.dat");
            this.empresas = persistenciaEmpresas.lista();
        } catch (PersistenciaException e) {
            this.empresas = new ArrayList();
            JOptionPane.showMessageDialog(this, "Error al cargar empresas: " + e.getMessage(), "Error", 0);
        }

    }

    private void initComponents() {
        this.setSize(500, 600);
        this.setLocationRelativeTo(this.getParent());
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints g = new GridBagConstraints();
        g.fill = 2;
        g.insets = new Insets(8, 8, 8, 8);
        g.gridx = 0;
        g.gridy = 0;
        p.add(new JLabel("ID Bus:"), g);
        g.gridx = 1;
        this.txtIdBus = new JTextField(20);
        this.txtIdBus.setEnabled(false);
        this.txtIdBus.setBackground(Color.LIGHT_GRAY);
        this.txtIdBus.setText("(Se generará automáticamente)");
        p.add(this.txtIdBus, g);
        g.gridx = 0;
        g.gridy = 1;
        p.add(new JLabel("Placa: *"), g);
        g.gridx = 1;
        this.txtPlaca = new JTextField(20);
        this.agregarFiltroPlaca(this.txtPlaca);
        p.add(this.txtPlaca, g);
        g.gridx = 0;
        g.gridy = 2;
        p.add(new JLabel("Nº Chasis:"), g);
        g.gridx = 1;
        this.txtChasis = new JTextField(20);
        this.agregarFiltroAlfanumerico(this.txtChasis, 17);
        p.add(this.txtChasis, g);
        g.gridx = 0;
        g.gridy = 3;
        p.add(new JLabel("Nº Motor:"), g);
        g.gridx = 1;
        this.txtMotor = new JTextField(20);
        this.agregarFiltroAlfanumerico(this.txtMotor, 20);
        p.add(this.txtMotor, g);
        g.gridx = 0;
        g.gridy = 4;
        p.add(new JLabel("Marca: *"), g);
        g.gridx = 1;
        this.txtMarca = new JTextField(20);
        p.add(this.txtMarca, g);
        g.gridx = 0;
        g.gridy = 5;
        p.add(new JLabel("Año Fabricación:"), g);
        g.gridx = 1;
        this.txtAnio = new JTextField(20);
        this.agregarFiltroNumeros(this.txtAnio, 4);
        p.add(this.txtAnio, g);
        g.gridx = 0;
        g.gridy = 6;
        p.add(new JLabel("Configuración:"), g);
        JPanel pConfig = new JPanel(new FlowLayout(0, 5, 0));
        this.cmbDistribucion = new JComboBox<>(new String[]{"Estándar (4 x fila)"});
        this.spinFilas = new JSpinner(new SpinnerNumberModel(10, 4, 20, 1));
        this.cmbDistribucion.addActionListener((e) -> this.calcularCapacidad());
        this.spinFilas.addChangeListener((e) -> this.calcularCapacidad());
        pConfig.add(this.cmbDistribucion);
        pConfig.add(new JLabel("Filas:"));
        pConfig.add(this.spinFilas);
        g.gridx = 1;
        p.add(pConfig, g);
        g.gridx = 0;
        g.gridy = 7;
        p.add(new JLabel("Capacidad Total: *"), g);
        g.gridx = 1;
        this.txtCapacidad = new JTextField(20);
        this.txtCapacidad.setToolTipText("Calculado automáticamente, pero puede editarse manualmente");
        this.agregarFiltroNumeros(this.txtCapacidad);
        p.add(this.txtCapacidad, g);
        this.calcularCapacidad();
        g.gridx = 0;
        g.gridy = 8;
        p.add(new JLabel("Estado: *"), g);
        g.gridx = 1;
        this.cmbEstado = new JComboBox<>(new String[]{"ACTIVO", "INACTIVO"});
        p.add(this.cmbEstado, g);
        g.gridx = 0;
        g.gridy = 9;
        p.add(new JLabel("Empresa: *"), g);
        g.gridx = 1;
        this.cmbEmpresa = new JComboBox<>();
        this.cmbEmpresa.addItem("-- Seleccione Empresa --");

        for(Empresa empresa : this.empresas) {
            JComboBox var10000 = this.cmbEmpresa;
            String var10001 = empresa.getIdEmpresa();
            var10000.addItem(var10001 + " - " + empresa.getNombre());
        }

        p.add(this.cmbEmpresa, g);
        if (this.empresaPreseleccionada != null) {
            for(int i = 0; i < this.cmbEmpresa.getItemCount(); ++i) {
                if (((String)this.cmbEmpresa.getItemAt(i)).startsWith(this.empresaPreseleccionada.getIdEmpresa())) {
                    this.cmbEmpresa.setSelectedIndex(i);
                    this.cmbEmpresa.setEnabled(false);
                    break;
                }
            }
        }

        g.gridx = 0;
        g.gridy = 10;
        g.gridwidth = 2;
        JLabel lblNota = new JLabel("* Campos obligatorios");
        lblNota.setFont(new Font("Arial", 2, 11));
        lblNota.setForeground(Color.GRAY);
        p.add(lblNota, g);
        JPanel panelBotones = new JPanel(new FlowLayout(1, 15, 10));
        this.btnGuardar = new JButton("Guardar");
        this.btnGuardar.setPreferredSize(new Dimension(120, 35));
        this.btnGuardar.addActionListener((e) -> this.guardar());
        this.btnCancelar = new JButton("Cancelar");
        this.btnCancelar.setPreferredSize(new Dimension(120, 35));
        this.btnCancelar.addActionListener((e) -> this.dispose());
        panelBotones.add(this.btnGuardar);
        panelBotones.add(this.btnCancelar);
        this.add(p, "Center");
        this.add(panelBotones, "South");
    }

    private void calcularCapacidad() {
        int filas = (Integer)this.spinFilas.getValue();
        int asientosPorFila = 4;
        int total = filas * asientosPorFila;
        this.txtCapacidad.setText(String.valueOf(total));
    }

    private void cargarDatosBus() {
        this.txtIdBus.setText(this.busActual.getIdBus());
        this.txtPlaca.setText(this.busActual.getPlaca());
        this.txtChasis.setText(this.busActual.getNumeroChasis());
        this.txtMotor.setText(this.busActual.getNumeroMotor());
        this.txtMarca.setText(this.busActual.getMarca());
        this.txtAnio.setText(String.valueOf(this.busActual.getAnioFabricacion()));
        this.txtCapacidad.setText(String.valueOf(this.busActual.getCapacidad()));
        this.cmbEstado.setSelectedItem(this.busActual.getEstado());
        if (this.busActual.getEmpresa() != null) {
            for(int i = 0; i < this.cmbEmpresa.getItemCount(); ++i) {
                if (((String)this.cmbEmpresa.getItemAt(i)).startsWith(this.busActual.getEmpresa().getIdEmpresa())) {
                    this.cmbEmpresa.setSelectedIndex(i);
                    break;
                }
            }
        }

    }

    private void guardar() {
        String placa = this.txtPlaca.getText().trim().toUpperCase();
        if (!placa.isEmpty() && !this.txtMarca.getText().trim().isEmpty() && !this.txtCapacidad.getText().trim().isEmpty()) {
            if (!placa.matches("^[A-Z]{3}-\\d{3}$")) {
                JOptionPane.showMessageDialog(this, "El formato de la placa es inválido. Debe ser XXX-999 (ej: PNY-123).", "Error de validación", 0);
            } else {
                String chasis = this.txtChasis.getText().trim();
                if (!chasis.isEmpty() && chasis.length() != 17) {
                    JOptionPane.showMessageDialog(this, "El número de chasis debe tener 17 caracteres.", "Error de validación", 0);
                } else {
                    String motor = this.txtMotor.getText().trim();
                    if (motor.isEmpty() || motor.length() >= 6 && motor.length() <= 20) {
                        int anio = 0;
                        if (!this.txtAnio.getText().trim().isEmpty()) {
                            anio = Integer.parseInt(this.txtAnio.getText().trim());
                            int anioActual = Calendar.getInstance().get(1);
                            if (anio < 1980 || anio > anioActual + 1) {
                                JOptionPane.showMessageDialog(this, "El año de fabricación debe ser un año válido (entre 1980 y " + (anioActual + 1) + ").", "Error de validación", 0);
                                return;
                            }
                        }

                        try {
                            int capacidad = Integer.parseInt(this.txtCapacidad.getText().trim());
                            if (capacidad < 10 || capacidad > 80) {
                                JOptionPane.showMessageDialog(this, "La capacidad debe ser realista (entre 10 y 80 asientos).", "Error de validación", 0);
                                return;
                            }
                        } catch (NumberFormatException var11) {
                            JOptionPane.showMessageDialog(this, "La capacidad debe ser un número válido", "Error de validación", 0);
                            return;
                        }

                        if (this.cmbEmpresa.getSelectedIndex() == 0) {
                            JOptionPane.showMessageDialog(this, "Por favor seleccione una empresa", "Error de validación", 0);
                        } else {
                            try {
                                Buses busesDB = new Buses("buses.dat");
                                List<Bus> lista = busesDB.lista();
                                String nuevaPlaca = placa;

                                for(Bus b : lista) {
                                    if (b.getPlaca().equalsIgnoreCase(nuevaPlaca) && (this.busActual == null || !b.getIdBus().equals(this.busActual.getIdBus()))) {
                                        JOptionPane.showMessageDialog(this, "La placa " + nuevaPlaca + " ya está registrada en otro bus.", "Error de validación", 0);
                                        return;
                                    }
                                }
                            } catch (PersistenciaException var10) {
                            }

                            if (this.busActual == null) {
                                this.busActual = new Bus();
                            }

                            this.busActual.setPlaca(placa);
                            this.busActual.setNumeroChasis(chasis);
                            this.busActual.setNumeroMotor(motor);
                            this.busActual.setMarca(this.txtMarca.getText().trim());
                            this.busActual.setAnioFabricacion(anio);
                            this.busActual.setCapacidad(Integer.parseInt(this.txtCapacidad.getText().trim()));
                            this.busActual.setEstado((String)this.cmbEstado.getSelectedItem());
                            String empresaSeleccionada = (String)this.cmbEmpresa.getSelectedItem();
                            String idEmpresa = empresaSeleccionada.split(" - ")[0];
                            this.busActual.setEmpresa(new Empresa(idEmpresa));
                            this.confirmado = true;
                            this.dispose();
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "El número de motor debe tener entre 6 y 20 caracteres.", "Error de validación", 0);
                    }
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Por favor complete todos los campos obligatorios", "Error de validación", 0);
        }
    }

    private void agregarFiltroNumeros(final JTextField campo) {
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
                    if (!text.equals(filtered)) {
                        campo.setText(filtered);
                    }

                });
            }
        });
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
                        campo.setText(filtered);
                    }

                });
            }
        });
    }

    private void agregarFiltroPlaca(final JTextField campo) {
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
                    String text = campo.getText().toUpperCase();
                    String filtered = text.replaceAll("[^A-Z0-9-]", "");
                    if (filtered.length() > 7) {
                        filtered = filtered.substring(0, 7);
                    }

                    if (!text.equals(filtered)) {
                        campo.setText(filtered);
                    }

                });
            }
        });
    }

    private void agregarFiltroAlfanumerico(final JTextField campo, final int maxDigitos) {
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
                    String text = campo.getText().toUpperCase();
                    String filtered = text.replaceAll("[^A-Z0-9]", "");
                    if (filtered.length() > maxDigitos) {
                        filtered = filtered.substring(0, maxDigitos);
                    }

                    if (!text.equals(filtered)) {
                        campo.setText(filtered);
                    }

                });
            }
        });
    }

    public Bus getBus() {
        return this.busActual;
    }

    public boolean isConfirmado() {
        return this.confirmado;
    }
}
