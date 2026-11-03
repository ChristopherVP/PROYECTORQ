//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package control;

import excepciones.FachadaException;
import fachadas.FachadaArchivos;
import interfaces.IFachada;
import java.util.List;
import java.util.regex.Pattern;
import objetosNegocio.Empresa;
import objetosNegocio.Usuario;
import objetosServicio.Fecha;

public class ControladorUsuarios {
    private IFachada fachada = FachadaArchivos.getInstance();

    public ResultadoOperacion registrarUsuario(String idUsuario, String nombre, String cedula, String contrasena, String confirmarContrasena, String email, String telefono, String tipo, String idEmpresa) {
        try {
            ResultadoOperacion validacion = this.validarDatosRegistro(idUsuario, nombre, cedula, contrasena, confirmarContrasena, email, telefono, tipo, idEmpresa);
            if (!validacion.isExito()) {
                return validacion;
            } else {
                Usuario existente = this.fachada.obten(new Usuario(idUsuario));
                if (existente != null) {
                    return new ResultadoOperacion(false, "El ID de usuario ya está en uso");
                } else {
                    for(Usuario u : this.fachada.consultaUsuarios()) {
                        if (u.getCedula() != null && u.getCedula().equals(cedula)) {
                            return new ResultadoOperacion(false, "La cédula ya está registrada");
                        }

                        if (u.getNombre() != null && u.getNombre().equalsIgnoreCase(nombre)) {
                            return new ResultadoOperacion(false, "El nombre de usuario ya está registrado");
                        }
                    }

                    Empresa empresa = null;
                    if (idEmpresa != null && !idEmpresa.isEmpty()) {
                        empresa = this.fachada.obten(new Empresa(idEmpresa));
                        if (empresa == null) {
                            return new ResultadoOperacion(false, "La empresa especificada no existe");
                        }
                    }

                    Usuario nuevoUsuario = new Usuario(idUsuario, nombre, cedula, contrasena, email, telefono, tipo, new Fecha(), empresa, "ACTIVO");
                    this.fachada.agrega(nuevoUsuario);
                    return new ResultadoOperacion(true, "Usuario registrado exitosamente", nuevoUsuario);
                }
            }
        } catch (FachadaException e) {
            return new ResultadoOperacion(false, "Error al registrar usuario: " + e.getMessage());
        }
    }

    public ResultadoOperacion autenticar(String idUsuario, String contrasena) {
        try {
            if (idUsuario != null && !idUsuario.trim().isEmpty()) {
                if (contrasena != null && !contrasena.trim().isEmpty()) {
                    Usuario usuario = this.fachada.obten(new Usuario(idUsuario.trim()));
                    if (usuario == null) {
                        return new ResultadoOperacion(false, "Usuario no encontrado");
                    } else if (!usuario.validarContrasena(contrasena)) {
                        return new ResultadoOperacion(false, "Contraseña incorrecta");
                    } else {
                        return "INACTIVO".equals(usuario.getEstado()) ? new ResultadoOperacion(false, "Usuario inactivo. Contacte al administrador.") : new ResultadoOperacion(true, "Autenticación exitosa", usuario);
                    }
                } else {
                    return new ResultadoOperacion(false, "Debe ingresar una contraseña");
                }
            } else {
                return new ResultadoOperacion(false, "Debe ingresar un ID de usuario");
            }
        } catch (FachadaException e) {
            return new ResultadoOperacion(false, "Error en la autenticación: " + e.getMessage());
        }
    }

    public ResultadoOperacion actualizarUsuario(String idUsuario, String nombre, String email, String telefono, String tipo, String idEmpresa) {
        try {
            Usuario usuario = this.fachada.obten(new Usuario(idUsuario));
            if (usuario == null) {
                return new ResultadoOperacion(false, "Usuario no encontrado");
            } else if (nombre != null && !nombre.trim().isEmpty()) {
                if (!this.validarEmail(email)) {
                    return new ResultadoOperacion(false, "Email inválido");
                } else if (!this.validarTelefono(telefono)) {
                    return new ResultadoOperacion(false, "Teléfono inválido");
                } else {
                    Empresa empresa = null;
                    if (idEmpresa != null && !idEmpresa.isEmpty()) {
                        empresa = this.fachada.obten(new Empresa(idEmpresa));
                        if (empresa == null) {
                            return new ResultadoOperacion(false, "La empresa especificada no existe");
                        }
                    }

                    usuario.setNombre(nombre);
                    usuario.setEmail(email);
                    usuario.setTelefono(telefono);
                    usuario.setTipo(tipo);
                    usuario.setEmpresa(empresa);
                    this.fachada.actualiza(usuario);
                    return new ResultadoOperacion(true, "Usuario actualizado exitosamente", usuario);
                }
            } else {
                return new ResultadoOperacion(false, "El nombre es obligatorio");
            }
        } catch (FachadaException e) {
            return new ResultadoOperacion(false, "Error al actualizar usuario: " + e.getMessage());
        }
    }

    public ResultadoOperacion cambiarContrasena(String idUsuario, String contrasenaActual, String contrasenaNueva, String confirmarNueva) {
        try {
            Usuario usuario = this.fachada.obten(new Usuario(idUsuario));
            if (usuario == null) {
                return new ResultadoOperacion(false, "Usuario no encontrado");
            } else if (!usuario.validarContrasena(contrasenaActual)) {
                return new ResultadoOperacion(false, "La contraseña actual es incorrecta");
            } else if (contrasenaNueva != null && contrasenaNueva.length() >= 8) {
                if (!contrasenaNueva.equals(confirmarNueva)) {
                    return new ResultadoOperacion(false, "Las contraseñas no coinciden");
                } else {
                    usuario.setContrasena(contrasenaNueva);
                    this.fachada.actualiza(usuario);
                    return new ResultadoOperacion(true, "Contraseña actualizada exitosamente");
                }
            } else {
                return new ResultadoOperacion(false, "La nueva contraseña debe tener al menos 8 caracteres");
            }
        } catch (FachadaException e) {
            return new ResultadoOperacion(false, "Error al cambiar contraseña: " + e.getMessage());
        }
    }

    public ResultadoOperacion eliminarUsuario(String idUsuario) {
        // $FF: Couldn't be decompiled
        return null;
    }

    public ResultadoOperacion consultarUsuarios() {
        // $FF: Couldn't be decompiled
        return null;
    }

    public ResultadoOperacion consultarUsuariosPorTipo(String tipo) {
        // $FF: Couldn't be decompiled
        return null;
    }

    private ResultadoOperacion validarDatosRegistro(String idUsuario, String nombre, String cedula, String contrasena, String confirmarContrasena, String email, String telefono, String tipo, String idEmpresa) {
        // $FF: Couldn't be decompiled
        return null;
    }

    private boolean validarEmail(String email) {
        // $FF: Couldn't be decompiled
        return false;
    }

    private boolean validarTelefono(String telefono) {
        return telefono != null && !telefono.trim().isEmpty() ? telefono.matches("\\d{7,15}") : false;
    }
}
