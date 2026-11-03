//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package InterfacesUsuario;

import excepciones.PersistenciaException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
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
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import objetosNegocio.Empresa;
import objetosNegocio.Usuario;
import objetosServicio.Fecha;
import persistencia.Empresas;
import persistencia.Usuarios;

public class DialogoFormularioUsuario extends JDialog {
    private JTextField txtId;
    private JTextField txtNombre;
    private JTextField txtCedula;
    private JTextField txtPassword;
    private JTextField txtEmail;
    private JTextField txtTelefono;
    private JComboBox<String> cmbTipo;
    private JComboBox<String> cmbEmpresa;
    private JComboBox<String> cmbEstado;
    private JLabel lblFortaleza;
    private Usuario usuario;
    private boolean confirmado = false;
    private List<Empresa> empresas;
    private String tipoPredefinido;
    private Empresa empresaPreseleccionada;

    public DialogoFormularioUsuario(JFrame padre, String titulo, Usuario u, String tipoPredefinido, Empresa empresaPreseleccionada) {
        super(padre, titulo, true);
        this.usuario = u;
        this.tipoPredefinido = tipoPredefinido;
        this.empresaPreseleccionada = empresaPreseleccionada;
        this.cargarEmpresas();
        this.initComponents();
        if (u != null) {
            this.cargar();
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
        this.setSize(500, 650);
        this.setLocationRelativeTo(this.getParent());
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints g = new GridBagConstraints();
        g.fill = 2;
        g.insets = new Insets(8, 8, 8, 8);
        int row = 0;
        g.gridx = 0;
        g.gridy = row;
        p.add(new JLabel("ID Usuario:"), g);
        g.gridx = 1;
        this.txtId = new JTextField(15);
        this.txtId.setEnabled(false);
        this.txtId.setBackground(Color.LIGHT_GRAY);
        this.txtId.setText("(Se generará automáticamente)");
        p.add(this.txtId, g);
        ++row;
        g.gridx = 0;
        g.gridy = row;
        p.add(new JLabel("Nombre: *"), g);
        g.gridx = 1;
        this.txtNombre = new JTextField(15);
        p.add(this.txtNombre, g);
        ++row;
        g.gridx = 0;
        g.gridy = row;
        p.add(new JLabel("Cédula: *"), g);
        g.gridx = 1;
        this.txtCedula = new JTextField(15);
        this.agregarFiltroCedula(this.txtCedula);
        p.add(this.txtCedula, g);
        ++row;
        g.gridx = 0;
        g.gridy = row;
        p.add(new JLabel("Contraseña: *"), g);
        g.gridx = 1;
        JPanel pPass = new JPanel(new BorderLayout(5, 0));
        this.txtPassword = new JPasswordField(15);
        this.txtPassword.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                DialogoFormularioUsuario.this.validarFortaleza();
            }

            public void removeUpdate(DocumentEvent e) {
                DialogoFormularioUsuario.this.validarFortaleza();
            }

            public void insertUpdate(DocumentEvent e) {
                DialogoFormularioUsuario.this.validarFortaleza();
            }
        });
        pPass.add(this.txtPassword, "Center");
        this.lblFortaleza = new JLabel("");
        this.lblFortaleza.setFont(new Font("Arial", 1, 11));
        pPass.add(this.lblFortaleza, "East");
        p.add(pPass, g);
        ++row;
        g.gridx = 0;
        g.gridy = row;
        p.add(new JLabel("Email: *"), g);
        g.gridx = 1;
        this.txtEmail = new JTextField(15);
        this.agregarFiltroEmail(this.txtEmail);
        p.add(this.txtEmail, g);
        ++row;
        g.gridx = 0;
        g.gridy = row;
        p.add(new JLabel("Teléfono:"), g);
        g.gridx = 1;
        this.txtTelefono = new JTextField(15);
        this.agregarFiltroTelefono(this.txtTelefono);
        p.add(this.txtTelefono, g);
        ++row;
        g.gridx = 0;
        g.gridy = row;
        p.add(new JLabel("Tipo: *"), g);
        g.gridx = 1;
        this.cmbTipo = new JComboBox<>(new String[]{"Administrador", "Operador"});
        p.add(this.cmbTipo, g);
        ++row;
        g.gridx = 0;
        g.gridy = row;
        p.add(new JLabel("Empresa: *"), g);
        g.gridx = 1;
        this.cmbEmpresa = new JComboBox<>();
        this.cmbEmpresa.addItem("-- Seleccione Empresa --");

        for(Empresa e : this.empresas) {
            JComboBox var10000 = this.cmbEmpresa;
            String var10001 = e.getIdEmpresa();
            var10000.addItem(var10001 + " - " + e.getNombre());
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

        ++row;
        g.gridx = 0;
        g.gridy = row;
        p.add(new JLabel("Estado: *"), g);
        g.gridx = 1;
        this.cmbEstado = new JComboBox<>(new String[]{"ACTIVO", "INACTIVO"});
        p.add(this.cmbEstado, g);
        ++row;
        g.gridx = 0;
        g.gridy = row;
        g.gridwidth = 2;
        JLabel lblNota = new JLabel("* Campos obligatorios");
        lblNota.setFont(new Font("Arial", 2, 11));
        lblNota.setForeground(Color.GRAY);
        p.add(lblNota, g);
        JPanel pBtn = new JPanel(new FlowLayout(1, 15, 10));
        JButton btnGuardar = new JButton("Guardar");
        btnGuardar.setPreferredSize(new Dimension(120, 35));
        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setPreferredSize(new Dimension(120, 35));
        btnGuardar.addActionListener((ex) -> this.guardar());
        btnCancelar.addActionListener((ex) -> this.dispose());
        pBtn.add(btnGuardar);
        pBtn.add(btnCancelar);
        this.add(p, "Center");
        this.add(pBtn, "South");
    }

    private void cargar() {
        this.txtId.setText(this.usuario.getIdUsuario());
        this.txtNombre.setText(this.usuario.getNombre());
        this.txtCedula.setText(this.usuario.getCedula());
        if (this.txtPassword != null) {
            this.txtPassword.setText(this.usuario.getContrasena());
        }

        this.txtEmail.setText(this.usuario.getEmail());
        this.txtTelefono.setText(this.usuario.getTelefono());
        if (this.cmbTipo != null) {
            this.cmbTipo.setSelectedItem(this.usuario.getTipo());
        }

        this.cmbEstado.setSelectedItem(this.usuario.getEstado() != null ? this.usuario.getEstado() : "ACTIVO");
        if (this.usuario.getEmpresa() != null) {
            for(int i = 0; i < this.cmbEmpresa.getItemCount(); ++i) {
                if (((String)this.cmbEmpresa.getItemAt(i)).startsWith(this.usuario.getEmpresa().getIdEmpresa())) {
                    this.cmbEmpresa.setSelectedIndex(i);
                    break;
                }
            }
        }

    }

    private void validarFortaleza() {
        String pass = this.txtPassword.getText();
        if (pass.isEmpty()) {
            this.lblFortaleza.setText("");
        } else if (pass.length() < 8) {
            this.lblFortaleza.setText("Muy corta");
            this.lblFortaleza.setForeground(Color.RED);
        } else {
            boolean hasUpper = !pass.equals(pass.toLowerCase());
            boolean hasLower = !pass.equals(pass.toUpperCase());
            boolean hasDigit = pass.matches(".*\\d.*");
            boolean hasSpecial = pass.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*");
            int score = (hasUpper ? 1 : 0) + (hasLower ? 1 : 0) + (hasDigit ? 1 : 0) + (hasSpecial ? 1 : 0);
            if (score < 2) {
                this.lblFortaleza.setText("Débil");
                this.lblFortaleza.setForeground(Color.ORANGE);
            } else if (score < 4) {
                this.lblFortaleza.setText("Media");
                this.lblFortaleza.setForeground(new Color(200, 180, 0));
            } else {
                this.lblFortaleza.setText("Fuerte");
                this.lblFortaleza.setForeground(new Color(0, 128, 0));
            }

        }
    }

    private void guardar() {
        if (!this.txtNombre.getText().trim().isEmpty() && !this.txtEmail.getText().trim().isEmpty()) {
            if (this.txtPassword.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "La contraseña es obligatoria", "Error", 0);
            } else {
                String cedula = this.txtCedula.getText().trim();
                if (this.validarCedulaEcuatoriana(cedula)) {
                    if (this.txtPassword.getText().length() < 8) {
                        JOptionPane.showMessageDialog(this, "La contraseña debe tener al menos 8 caracteres", "Error de Validación", 0);
                    } else {
                        String email = this.txtEmail.getText().trim();
                        if (email.contains(" ")) {
                            JOptionPane.showMessageDialog(this, "El email no puede contener espacios", "Error de Validación", 0);
                        } else if (!email.equals(email.toLowerCase())) {
                            JOptionPane.showMessageDialog(this, "El email no puede contener mayúsculas", "Error de Validación", 0);
                        } else if (!email.matches("^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,}$")) {
                            JOptionPane.showMessageDialog(this, "El formato del email no es válido", "Error de Validación", 0);
                        } else {
                            String telefono = this.txtTelefono.getText().trim();
                            if (telefono.isEmpty() || telefono.length() >= 9 && telefono.length() <= 10) {
                                if (this.cmbEmpresa.getSelectedIndex() == 0) {
                                    JOptionPane.showMessageDialog(this, "Debe seleccionar la empresa a la que pertenece el usuario.", "Error de Validación", 0);
                                } else {
                                    try {
                                        Usuarios usuariosDB = new Usuarios("usuarios.dat");
                                        List<Usuario> lista = usuariosDB.lista();
                                        String nuevaCedula = this.txtCedula.getText().trim();
                                        String nuevoEmail = this.txtEmail.getText().trim();
                                        String nuevoNombre = this.txtNombre.getText().trim();

                                        for(Usuario u : lista) {
                                            if (u.getNombre().equalsIgnoreCase(nuevoNombre)) {
                                                if (this.usuario == null || this.usuario.getIdUsuario() == null || !u.getIdUsuario().equals(this.usuario.getIdUsuario())) {
                                                    JOptionPane.showMessageDialog(this, "El nombre de usuario ya está registrado.", "Error de Validación", 0);
                                                    return;
                                                }
                                            } else if (u.getCedula().equals(nuevaCedula)) {
                                                if (this.usuario == null || this.usuario.getIdUsuario() == null || !u.getIdUsuario().equals(this.usuario.getIdUsuario())) {
                                                    JOptionPane.showMessageDialog(this, "La cédula ya está registrada en otro usuario.", "Error de Validación", 0);
                                                    return;
                                                }
                                            } else if (u.getEmail().equalsIgnoreCase(nuevoEmail) && (this.usuario == null || this.usuario.getIdUsuario() == null || !u.getIdUsuario().equals(this.usuario.getIdUsuario()))) {
                                                JOptionPane.showMessageDialog(this, "El email ya está registrado en otro usuario.", "Error de Validación", 0);
                                                return;
                                            }
                                        }

                                        if (this.usuario == null) {
                                            this.usuario = new Usuario();
                                            this.usuario.setFechaRegistro(new Fecha());
                                            String prefix = "USR";
                                            int nextId = lista.size() + 1;
                                            String generatedId = String.format("USU%03d", nextId);
                                            boolean exists = true;

                                            while(exists) {
                                                exists = false;

                                                for(Usuario u : lista) {
                                                    if (u.getIdUsuario().equals(generatedId)) {
                                                        exists = true;
                                                        ++nextId;
                                                        generatedId = String.format("USU%03d", nextId);
                                                        break;
                                                    }
                                                }
                                            }

                                            this.usuario.setIdUsuario(generatedId);
                                        }
                                    } catch (PersistenciaException var15) {
                                        if (this.usuario == null) {
                                            this.usuario = new Usuario();
                                            this.usuario.setFechaRegistro(new Fecha());
                                            this.usuario.setIdUsuario("ERR" + System.currentTimeMillis());
                                        }
                                    }

                                    this.usuario.setNombre(this.txtNombre.getText().trim());
                                    this.usuario.setCedula(cedula);
                                    this.usuario.setEmail(email);
                                    this.usuario.setTelefono(telefono);
                                    this.usuario.setEstado((String)this.cmbEstado.getSelectedItem());
                                    this.usuario.setTipo((String)this.cmbTipo.getSelectedItem());
                                    this.usuario.setContrasena(this.txtPassword.getText().trim());
                                    if (this.cmbEmpresa.getSelectedIndex() > 0) {
                                        String empresaSeleccionada = (String)this.cmbEmpresa.getSelectedItem();
                                        String idEmpresa = empresaSeleccionada.split(" - ")[0].trim();
                                        this.usuario.setEmpresa(new Empresa(idEmpresa));
                                    }

                                    this.confirmado = true;
                                    this.dispose();
                                }
                            } else {
                                JOptionPane.showMessageDialog(this, "El teléfono debe tener entre 9 y 10 dígitos", "Error de Validación", 0);
                            }
                        }
                    }
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Complete todos los campos obligatorios", "Error de Validación", 0);
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
                        } catch (IllegalArgumentException var5) {
                            campo.setCaretPosition(filtered.length());
                        }
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

    public Usuario getUsuario() {
        return this.usuario;
    }

    public boolean isConfirmado() {
        return this.confirmado;
    }
}
