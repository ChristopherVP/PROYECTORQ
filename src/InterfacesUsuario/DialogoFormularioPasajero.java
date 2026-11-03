//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package InterfacesUsuario;

import excepciones.PersistenciaException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import objetosNegocio.Empresa;
import objetosNegocio.Pasajero;
import objetosServicio.Fecha;
import persistencia.Empresas;
import persistencia.Pasajeros;

public class DialogoFormularioPasajero extends JDialog {
    private JTextField txtId;
    private JTextField txtNombre;
    private JTextField txtCedula;
    private JTextField txtTelefono;
    private JTextField txtEmail;
    private JTextField txtDireccion;
    private JTextField txtDiaNac;
    private JTextField txtMesNac;
    private JTextField txtAnioNac;
    private JLabel lblTipoPasajero;
    private JCheckBox chkDiscapacidad;
    private JComboBox<String> cmbEmpresa;
    private Pasajero pasajero;
    private boolean confirmado = false;
    private List<Empresa> empresas;
    private Empresa empresaPreseleccionada;

    public DialogoFormularioPasajero(Window padre, String titulo, Pasajero p, Empresa empresaPreseleccionada) {
        super(padre, titulo, ModalityType.APPLICATION_MODAL);
        this.pasajero = p;
        this.empresaPreseleccionada = empresaPreseleccionada;
        this.cargarEmpresas();
        this.initComponents();
        if (p != null) {
            this.cargarDatos();
        }

    }

    private void cargarEmpresas() {
        try {
            this.empresas = (new Empresas("empresas.dat")).lista();
        } catch (PersistenciaException e) {
            this.empresas = new ArrayList();
            JOptionPane.showMessageDialog(this, "Error al cargar empresas: " + e.getMessage());
        }

    }

    private void initComponents() {
        this.setSize(500, 550);
        this.setLocationRelativeTo(this.getParent());
        this.setLayout(new BorderLayout());
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = 2;
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("ID Pasajero:"), gbc);
        gbc.gridx = 1;
        this.txtId = new JTextField(20);
        this.txtId.setEnabled(false);
        this.txtId.setBackground(Color.LIGHT_GRAY);
        this.txtId.setText("(Se generará automáticamente)");
        panel.add(this.txtId, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Nombre: *"), gbc);
        gbc.gridx = 1;
        this.txtNombre = new JTextField(20);
        panel.add(this.txtNombre, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Cédula: *"), gbc);
        gbc.gridx = 1;
        this.txtCedula = new JTextField(20);
        panel.add(this.txtCedula, gbc);
        this.agregarFiltroCedula(this.txtCedula);
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Teléfono:"), gbc);
        gbc.gridx = 1;
        this.txtTelefono = new JTextField(20);
        panel.add(this.txtTelefono, gbc);
        this.agregarFiltroTelefono(this.txtTelefono);
        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        this.txtEmail = new JTextField(20);
        panel.add(this.txtEmail, gbc);
        this.agregarFiltroEmail(this.txtEmail);
        gbc.gridx = 0;
        gbc.gridy = 5;
        panel.add(new JLabel("Dirección:"), gbc);
        gbc.gridx = 1;
        this.txtDireccion = new JTextField(20);
        panel.add(this.txtDireccion, gbc);
        gbc.gridx = 0;
        gbc.gridy = 6;
        panel.add(new JLabel("Fecha Nacimiento:"), gbc);
        JPanel pFecha = new JPanel(new FlowLayout(0));
        this.txtDiaNac = new JTextField(2);
        this.txtMesNac = new JTextField(2);
        this.txtAnioNac = new JTextField(4);
        pFecha.add(this.txtDiaNac);
        pFecha.add(new JLabel("/"));
        pFecha.add(this.txtMesNac);
        pFecha.add(new JLabel("/"));
        pFecha.add(this.txtAnioNac);
        DocumentListener dateListener = new SimpleDocumentListener(() -> this.actualizarTipoPasajero());
        this.txtDiaNac.getDocument().addDocumentListener(dateListener);
        this.txtMesNac.getDocument().addDocumentListener(dateListener);
        this.txtAnioNac.getDocument().addDocumentListener(dateListener);
        gbc.gridx = 1;
        panel.add(pFecha, gbc);
        gbc.gridx = 0;
        gbc.gridy = 7;
        panel.add(new JLabel("Discapacidad:"), gbc);
        gbc.gridx = 1;
        this.chkDiscapacidad = new JCheckBox("Sí");
        this.chkDiscapacidad.addActionListener((ex) -> this.actualizarTipoPasajero());
        panel.add(this.chkDiscapacidad, gbc);
        gbc.gridx = 0;
        gbc.gridy = 8;
        panel.add(new JLabel("Tipo Calculado:"), gbc);
        gbc.gridx = 1;
        this.lblTipoPasajero = new JLabel("ADULTO");
        this.lblTipoPasajero.setFont(new Font("Arial", 1, 12));
        this.lblTipoPasajero.setForeground(new Color(0, 102, 204));
        panel.add(this.lblTipoPasajero, gbc);
        gbc.gridx = 0;
        gbc.gridy = 9;
        panel.add(new JLabel("Empresa: *"), gbc);
        gbc.gridx = 1;
        this.cmbEmpresa = new JComboBox<>();
        this.cmbEmpresa.addItem("-- Seleccione Empresa --");

        for(Empresa e : this.empresas) {
            JComboBox var10000 = this.cmbEmpresa;
            String var10001 = e.getIdEmpresa();
            var10000.addItem(var10001 + " - " + e.getNombre());
        }

        panel.add(this.cmbEmpresa, gbc);
        if (this.empresaPreseleccionada != null) {
            for(int i = 0; i < this.cmbEmpresa.getItemCount(); ++i) {
                if (((String)this.cmbEmpresa.getItemAt(i)).startsWith(this.empresaPreseleccionada.getIdEmpresa())) {
                    this.cmbEmpresa.setSelectedIndex(i);
                    this.cmbEmpresa.setEnabled(false);
                    break;
                }
            }
        }

        gbc.gridx = 0;
        gbc.gridy = 10;
        gbc.gridwidth = 2;
        JLabel lblNota = new JLabel("* Campos obligatorios");
        lblNota.setFont(new Font("Arial", 2, 11));
        lblNota.setForeground(Color.GRAY);
        panel.add(lblNota, gbc);
        JPanel botones = new JPanel(new FlowLayout(1, 15, 10));
        JButton btnGuardar = new JButton("Guardar");
        btnGuardar.setPreferredSize(new Dimension(120, 35));
        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setPreferredSize(new Dimension(120, 35));
        btnGuardar.addActionListener((ex) -> this.guardar());
        btnCancelar.addActionListener((ex) -> this.dispose());
        botones.add(btnGuardar);
        botones.add(btnCancelar);
        this.add(panel, "Center");
        this.add(botones, "South");
    }

    private void actualizarTipoPasajero() {
        if (this.chkDiscapacidad.isSelected()) {
            this.lblTipoPasajero.setText("DISCAPACITADO");
        } else {
            try {
                int d = Integer.parseInt(this.txtDiaNac.getText().trim());
                int m = Integer.parseInt(this.txtMesNac.getText().trim());
                int a = Integer.parseInt(this.txtAnioNac.getText().trim());
                Pasajero temp = new Pasajero();
                temp.setFechaNacimiento(new Fecha(d, m, a));
                temp.setDiscapacidad(false);
                JLabel var10000 = this.lblTipoPasajero;
                String var10001 = temp.getTipoPasajero();
                var10000.setText(var10001 + " (" + temp.calcularEdad() + " años)");
            } catch (Exception var5) {
                this.lblTipoPasajero.setText("ADULTO (Por defecto)");
            }

        }
    }

    private void cargarDatos() {
        this.txtId.setText(this.pasajero.getIdPasajero());
        this.txtNombre.setText(this.pasajero.getNombre());
        this.txtCedula.setText(this.pasajero.getCedula());
        this.txtTelefono.setText(this.pasajero.getTelefono());
        this.txtEmail.setText(this.pasajero.getEmail());
        this.txtDireccion.setText(this.pasajero.getDireccion());
        if (this.pasajero.getFechaNacimiento() != null) {
            this.txtDiaNac.setText("" + this.pasajero.getFechaNacimiento().getDia());
            this.txtMesNac.setText("" + this.pasajero.getFechaNacimiento().getMes());
            this.txtAnioNac.setText("" + this.pasajero.getFechaNacimiento().getAnio());
        }

        this.chkDiscapacidad.setSelected(this.pasajero.isDiscapacidad());
        this.actualizarTipoPasajero();
        if (this.pasajero.getEmpresa() != null) {
            for(int i = 0; i < this.cmbEmpresa.getItemCount(); ++i) {
                if (((String)this.cmbEmpresa.getItemAt(i)).startsWith(this.pasajero.getEmpresa().getIdEmpresa())) {
                    this.cmbEmpresa.setSelectedIndex(i);
                    break;
                }
            }
        }

    }

    private void guardar() {
        if (!this.txtNombre.getText().trim().isEmpty() && !this.txtCedula.getText().trim().isEmpty() && !this.txtDireccion.getText().trim().isEmpty() && !this.txtEmail.getText().trim().isEmpty()) {
            try {
                Pasajeros pasajerosDB = new Pasajeros("pasajeros.dat");
                List<Pasajero> lista = pasajerosDB.lista();
                String nuevoNombre = this.txtNombre.getText().trim();

                for(Pasajero p : lista) {
                    if (p.getNombre().equalsIgnoreCase(nuevoNombre) && (this.pasajero == null || !p.getIdPasajero().equals(this.pasajero.getIdPasajero()))) {
                        JOptionPane.showMessageDialog(this, "El nombre del pasajero ya está registrado.", "Error de Validación", 0);
                        return;
                    }
                }
            } catch (PersistenciaException var11) {
            }

            if (this.validarCedulaEcuatoriana(this.txtCedula.getText().trim())) {
                String email = this.txtEmail.getText().trim();
                if (!email.matches("^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,}$")) {
                    JOptionPane.showMessageDialog(this, "El formato del email no es válido", "Error de Validación", 0);
                } else {
                    String telefono = this.txtTelefono.getText().trim();
                    if (telefono.isEmpty() || telefono.length() >= 9 && telefono.length() <= 10) {
                        if (this.cmbEmpresa.getSelectedIndex() == 0) {
                            JOptionPane.showMessageDialog(this, "Debe seleccionar una empresa", "Error de Validación", 0);
                        } else {
                            try {
                                Pasajeros pasajerosDB = new Pasajeros("pasajeros.dat");
                                List<Pasajero> lista = pasajerosDB.lista();
                                String nuevaCedula = this.txtCedula.getText().trim();
                                String nuevoEmail = this.txtEmail.getText().trim();

                                for(Pasajero p : lista) {
                                    if (p.getCedula().equals(nuevaCedula)) {
                                        if (this.pasajero == null || !p.getIdPasajero().equals(this.pasajero.getIdPasajero())) {
                                            JOptionPane.showMessageDialog(this, "La cédula ya está registrada en otro pasajero.", "Error de Validación", 0);
                                            return;
                                        }
                                    } else if (p.getEmail().equalsIgnoreCase(nuevoEmail) && (this.pasajero == null || !p.getIdPasajero().equals(this.pasajero.getIdPasajero()))) {
                                        JOptionPane.showMessageDialog(this, "El email ya está registrado en otro pasajero.", "Error de Validación", 0);
                                        return;
                                    }
                                }
                            } catch (PersistenciaException var10) {
                            }

                            if (this.pasajero == null) {
                                this.pasajero = new Pasajero();
                            }

                            this.pasajero.setNombre(this.txtNombre.getText().trim());
                            this.pasajero.setCedula(this.txtCedula.getText().trim());
                            this.pasajero.setTelefono(telefono);
                            this.pasajero.setEmail(email);
                            this.pasajero.setDireccion(this.txtDireccion.getText().trim());

                            try {
                                int d = Integer.parseInt(this.txtDiaNac.getText());
                                int m = Integer.parseInt(this.txtMesNac.getText());
                                int a = Integer.parseInt(this.txtAnioNac.getText());
                                this.pasajero.setFechaNacimiento(new Fecha(d, m, a));
                            } catch (Exception var9) {
                            }

                            this.pasajero.setDiscapacidad(this.chkDiscapacidad.isSelected());
                            String empresaSeleccionada = (String)this.cmbEmpresa.getSelectedItem();
                            String idEmpresa = empresaSeleccionada.split(" - ")[0].trim();
                            this.pasajero.setEmpresa(new Empresa(idEmpresa));
                            this.confirmado = true;
                            this.dispose();
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "El teléfono debe tener entre 9 y 10 dígitos", "Error de Validación", 0);
                    }
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Complete todos los campos obligatorios (Nombre, Cédula, Email, Dirección)", "Error de Validación", 0);
        }
    }

    private boolean validarCedulaEcuatoriana(String cedula) {
        if (cedula.length() != 10) {
            JOptionPane.showMessageDialog(this, "La cédula debe tener exactamente 10 dígitos", "Error de Validación", 0);
            return false;
        } else {
            return true;
        }
    }

    private void agregarFiltroCedula(final JTextField campo) {
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
                    if (filtered.length() > 10) {
                        filtered = filtered.substring(0, 10);
                    }

                    if (!text.equals(filtered)) {
                        campo.setText(filtered);
                    }

                });
            }
        });
    }

    private void agregarFiltroTelefono(final JTextField campo) {
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
                    if (filtered.length() > 10) {
                        filtered = filtered.substring(0, 10);
                    }

                    if (!text.equals(filtered)) {
                        campo.setText(filtered);
                    }

                });
            }
        });
    }

    private void agregarFiltroEmail(final JTextField campo) {
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
                    String filtered = text.toLowerCase().replaceAll("\\s", "");
                    if (!text.equals(filtered)) {
                        int pos = campo.getCaretPosition();
                        campo.setText(filtered);

                        try {
                            campo.setCaretPosition(Math.min(pos, filtered.length()));
                        } catch (Exception var5) {
                        }
                    }

                });
            }
        });
    }

    public Pasajero getPasajero() {
        return this.pasajero;
    }

    public boolean isConfirmado() {
        return this.confirmado;
    }

    private static class SimpleDocumentListener implements DocumentListener {
        private final Runnable action;

        public SimpleDocumentListener(Runnable action) {
            this.action = action;
        }

        public void insertUpdate(DocumentEvent e) {
            this.action.run();
        }

        public void removeUpdate(DocumentEvent e) {
            this.action.run();
        }

        public void changedUpdate(DocumentEvent e) {
            this.action.run();
        }
    }
}
