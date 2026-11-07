package control;

import excepciones.PersistenciaException;
import excepciones.Validador;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import objetosNegocio.Cargo;
import objetosNegocio.Conductor;
import objetosNegocio.Empresa;
import objetosNegocio.Pasajero;
import objetosNegocio.Rol;
import objetosNegocio.Usuario;
import objetosServicio.ConfiguracionAdmin;
import objetosServicio.Fecha;
import objetosServicio.Seguridad;
import persistencia.Conductores;
import persistencia.Empresas;
import persistencia.Pasajeros;
import persistencia.Usuarios;

/**
 * Reglas de negocio de cuentas: registro por rol, inicio de sesión, recuperación de contraseña
 * (cédula + pregunta de seguridad), perfil y administración de usuarios.
 */
public class ControladorUsuarios {
    public static final int MAX_INTENTOS = 5;
    public static final long BLOQUEO_MS = 15L * 60 * 1000;
    private static final long BLOQUEO_LOGIN_MS = 2L * 60 * 1000;
    private static final String MSJ_GENERICO_RECUPERACION = "No se encontró una cuenta con esos datos";
    private static final Map<String, long[]> FALLOS_LOGIN = new ConcurrentHashMap<>();
    private static String hashFalso;

    private final Usuarios usuarios;
    private final Conductores conductores;
    private final Pasajeros pasajeros;
    private final Empresas empresas;

    public ControladorUsuarios() {
        this(new Usuarios("usuarios.dat"), new Conductores("conductores.dat"), new Pasajeros("pasajeros.dat"), new Empresas("empresas.dat"));
    }

    public ControladorUsuarios(Usuarios usuarios, Conductores conductores, Pasajeros pasajeros, Empresas empresas) {
        this.usuarios = usuarios;
        this.conductores = conductores;
        this.pasajeros = pasajeros;
        this.empresas = empresas;
    }

    // ---------------------------------------------------------------- consultas básicas

    public boolean existeSuperAdministrador() {
        try {
            return usuarios.lista().stream().anyMatch(u -> u.esSuperAdministrador() && u.estaActivo());
        } catch (PersistenciaException e) {
            return false;
        }
    }

    public ResultadoOperacion obtener(String idUsuario) {
        try {
            Usuario u = usuarios.obtenPorIdUsuario(idUsuario);
            return u == null ? error("Usuario no encontrado") : ok("OK", u);
        } catch (PersistenciaException e) {
            return error(e.getMessage());
        }
    }

    public List<Usuario> listarVisibles(Usuario actor) throws PersistenciaException {
        List<Usuario> todos = usuarios.lista();
        if (actor == null || actor.getRol() != Rol.ADMINISTRADOR) {
            return new ArrayList<>();
        }
        if (actor.esSuperAdministrador()) {
            return todos;
        }
        return todos.stream().filter(u -> u.tieneEmpresa() && u.getEmpresa().getIdEmpresa().equals(actor.getEmpresa().getIdEmpresa())).collect(Collectors.toList());
    }

    // ---------------------------------------------------------------- altas

    /**
     * Garantiza que exista el administrador principal (cargo "Administrador principal"). Si no existe lo crea
     * con el usuario y la contraseña de {@link ConfiguracionAdmin}; con la contraseña temporal por defecto
     * queda obligado a cambiarla en su primer ingreso. Si ya existe no toca nada.
     */
    public synchronized ResultadoOperacion asegurarAdministradorPrincipal() {
        try {
            for (Usuario existente : usuarios.lista()) {
                if (existente.esAdministradorPrincipal()) {
                    return ok("El administrador principal ya existe", existente);
                }
            }
            String login = ConfiguracionAdmin.usuarioInicial();
            if (Validador.errorLogin(login) != null) {
                login = ConfiguracionAdmin.USUARIO_POR_DEFECTO;
            }
            if (usuarios.buscaPorLogin(login) != null) {
                return error("No se pudo crear el administrador principal: el usuario \"" + login + "\" ya está en uso");
            }
            String clave = ConfiguracionAdmin.passwordInicial();
            boolean cambioPendiente = ConfiguracionAdmin.usaPasswordPorDefecto();
            String aviso = "";
            if (Validador.errorContrasena(clave) != null) {
                clave = ConfiguracionAdmin.passwordTemporal();
                cambioPendiente = true;
                aviso = " (la contraseña de " + ConfiguracionAdmin.VARIABLE_PASSWORD + " no cumple la política: se usó la temporal)";
            }
            Usuario u = nuevoUsuario(login, ConfiguracionAdmin.NOMBRE_ADMIN, ConfiguracionAdmin.CEDULA_RESERVADA, clave, "", "",
                    Rol.ADMINISTRADOR, Usuario.ESTADO_ACTIVO);
            u.setCargo(Cargo.ADMINISTRADOR_PRINCIPAL.getEtiqueta());
            u.setClavePendiente(cambioPendiente);
            usuarios.agrega(u);
            return ok("Administrador principal creado" + aviso, u);
        } catch (PersistenciaException e) {
            return error(e.getMessage());
        }
    }

    public synchronized ResultadoOperacion registrarPasajero(String login, String nombre, String cedula, String clave, String confirmar,
                                                             String email, String telefono, String direccion, Fecha nacimiento, boolean discapacidad, String pregunta, String respuesta) {
        try {
            String problema = validarCuenta(login, nombre, cedula, clave, confirmar, email, telefono, pregunta, respuesta, true, null, true);
            if (problema == null) {
                problema = validarMayorDeEdad(nacimiento);
            }
            if (problema != null) {
                return error(problema);
            }
            Pasajero ficha = buscarPasajeroPorCedula(cedula.trim());
            boolean fichaNueva = ficha == null;
            if (fichaNueva) {
                ficha = new Pasajero();
                ficha.setNombre(nombre.trim());
                ficha.setCedula(cedula.trim());
                ficha.setTelefono(vacioSiNulo(telefono));
                ficha.setEmail(vacioSiNulo(email));
                ficha.setDireccion(direccion == null || direccion.trim().isEmpty() ? "No registrada" : direccion.trim());
                ficha.setEmpresa(null);
                ficha.setFechaNacimiento(nacimiento);
                ficha.setDiscapacidad(discapacidad);
                pasajeros.agrega(ficha);
            }
            Usuario u = nuevoUsuario(login, nombre, cedula, clave, email, telefono, Rol.PASAJERO, Usuario.ESTADO_ACTIVO);
            u.setIdPerfil(ficha.getIdPasajero());
            u.setPreguntaSeguridad(pregunta.trim());
            u.definirRespuestaSeguridad(respuesta);
            try {
                usuarios.agrega(u);
            } catch (PersistenciaException e) {
                if (fichaNueva) {
                    pasajeros.elimina(ficha);
                }
                throw e;
            }
            return ok("Cuenta creada. Ya puedes iniciar sesión.", u);
        } catch (PersistenciaException e) {
            return error(e.getMessage());
        }
    }

    /**
     * Un conductor solo puede crear su cuenta si el administrador/empresa ya lo registró como conductor
     * (se busca por cédula). La cuenta queda PENDIENTE hasta que un administrador la apruebe.
     */
    public synchronized ResultadoOperacion registrarConductor(String login, String cedula, String clave, String confirmar,
                                                              String email, String telefono, String pregunta, String respuesta) {
        try {
            String errCedula = Validador.errorCedula(cedula);
            if (errCedula != null) {
                return error(errCedula);
            }
            Conductor ficha = buscarConductorPorCedula(cedula.trim());
            if (ficha == null) {
                return error("No existe un conductor registrado con esa cédula. Pide a tu empresa o al administrador que te registre primero.");
            }
            if ("INACTIVO".equalsIgnoreCase(ficha.getEstado())) {
                return error("Tu ficha de conductor está inactiva. Contacta al administrador.");
            }
            for (Usuario existente : usuarios.lista()) {
                if (ficha.getIdConductor().equals(existente.getIdPerfil())) {
                    return error("Ese conductor ya tiene una cuenta creada.");
                }
            }
            String problema = validarCuenta(login, ficha.getNombre(), cedula, clave, confirmar, email, telefono, pregunta, respuesta, true, "conductor", true);
            if (problema != null) {
                return error(problema);
            }
            Usuario u = nuevoUsuario(login, ficha.getNombre(), cedula, clave, email, telefono, Rol.CONDUCTOR, Usuario.ESTADO_PENDIENTE);
            u.setEmpresa(ficha.getEmpresa());
            u.setIdPerfil(ficha.getIdConductor());
            u.setPreguntaSeguridad(pregunta.trim());
            u.definirRespuestaSeguridad(respuesta);
            usuarios.agrega(u);
            return ok("Solicitud enviada. Un administrador debe aprobar tu cuenta antes de que puedas ingresar.", u);
        } catch (PersistenciaException e) {
            return error(e.getMessage());
        }
    }

    /**
     * Alta de colaboradores (Administrador u Operador) con su cargo. Solo el administrador principal crea administradores;
     * cualquier administrador activo puede crear operadores. La contraseña es temporal: la persona debe cambiarla al ingresar.
     */
    public synchronized ResultadoOperacion crearUsuarioAdministrativo(Usuario actor, String tipo, String cargo, String login, String nombre, String cedula,
                                                                      String clave, String email, String telefono, String idEmpresa, String pregunta, String respuesta) {
        try {
            ResultadoOperacion permiso = exigirAdministrador(actor);
            if (!permiso.isExito()) {
                return permiso;
            }
            Rol rol = Rol.desdeEtiqueta(tipo);
            if (rol != Rol.ADMINISTRADOR && rol != Rol.OPERADOR) {
                return error("Desde aquí solo se pueden crear cuentas de Administrador u Operador");
            }
            if (rol == Rol.ADMINISTRADOR && !actor.esAdministradorPrincipal()) {
                return error("Solo el administrador principal puede crear administradores");
            }
            Cargo cargoElegido = Cargo.desdeEtiqueta(cargo);
            if (cargoElegido == null || cargoElegido.getRol() != rol || cargoElegido == Cargo.ADMINISTRADOR_PRINCIPAL) {
                return error("Selecciona un cargo válido para el rol " + rol.getEtiqueta());
            }
            String empresaFinal = actor.tieneEmpresa() ? actor.getEmpresa().getIdEmpresa() : (idEmpresa == null ? "" : idEmpresa.trim());
            if (rol == Rol.OPERADOR && empresaFinal.isEmpty()) {
                return error("El operador debe pertenecer a una empresa");
            }
            if (!empresaFinal.isEmpty() && empresas.obten(new Empresa(empresaFinal)) == null) {
                return error("La empresa seleccionada no existe");
            }
            boolean hayPregunta = pregunta != null && !pregunta.trim().isEmpty();
            String problema = validarCuenta(login, nombre, cedula, clave, clave, email, telefono, pregunta, respuesta, hayPregunta, null, false);
            if (problema != null) {
                return error(problema);
            }
            Usuario u = nuevoUsuario(login, nombre, cedula, clave, email, telefono, rol, Usuario.ESTADO_ACTIVO);
            u.setEmpresa(empresaFinal.isEmpty() ? null : new Empresa(empresaFinal));
            u.setCargo(cargoElegido.getEtiqueta());
            u.setClavePendiente(true);
            if (hayPregunta) {
                u.setPreguntaSeguridad(pregunta.trim());
                u.definirRespuestaSeguridad(respuesta);
            }
            usuarios.agrega(u);
            return ok("Usuario creado correctamente", u);
        } catch (PersistenciaException e) {
            return error(e.getMessage());
        }
    }

    // ---------------------------------------------------------------- sesión

    public ResultadoOperacion autenticar(String login, String contrasena) {
        if (login == null || login.trim().isEmpty() || contrasena == null || contrasena.isEmpty()) {
            return error("Ingresa tu usuario y tu contraseña");
        }
        String clave = login.trim().toLowerCase();
        long ahora = System.currentTimeMillis();
        long[] estado = FALLOS_LOGIN.get(clave);
        if (estado != null && estado[1] > ahora) {
            long minutos = (estado[1] - ahora + 59_999) / 60_000;
            return error("Demasiados intentos fallidos. Intenta de nuevo en " + minutos + " minuto(s).");
        }
        try {
            Usuario u = usuarios.buscaPorLogin(login);
            boolean correcta = u != null ? u.validarContrasena(contrasena) : Seguridad.verificar(contrasena, hashFalso());
            if (u == null || !correcta) {
                registrarFalloLogin(clave);
                return error("Usuario o contraseña incorrectos");
            }
            FALLOS_LOGIN.remove(clave);
            if (Usuario.ESTADO_PENDIENTE.equalsIgnoreCase(u.getEstado())) {
                return error("Tu cuenta está pendiente de aprobación por el administrador");
            }
            if (!u.estaActivo()) {
                return error("Tu cuenta está inactiva. Contacta al administrador");
            }
            return ok("Bienvenido, " + u.getNombre(), u);
        } catch (PersistenciaException e) {
            return error("No se pudo verificar la cuenta: " + e.getMessage());
        }
    }

    private void registrarFalloLogin(String clave) {
        long[] estado = FALLOS_LOGIN.computeIfAbsent(clave, k -> new long[2]);
        estado[0]++;
        if (estado[0] >= MAX_INTENTOS) {
            estado[0] = 0;
            estado[1] = System.currentTimeMillis() + BLOQUEO_LOGIN_MS;
        }
    }

    private static synchronized String hashFalso() {
        if (hashFalso == null) {
            hashFalso = Seguridad.hash("contraseña-inexistente");
        }
        return hashFalso;
    }

    // ---------------------------------------------------------------- recuperación (cédula + pregunta)

    public ResultadoOperacion obtenerPreguntaRecuperacion(String cedula) {
        String problema = Validador.errorCedula(cedula);
        if (problema != null) {
            return error(problema);
        }
        try {
            Usuario u = usuarios.buscaPorCedula(cedula.trim());
            if (u == null || Usuario.ESTADO_INACTIVO.equalsIgnoreCase(u.getEstado())) {
                return error(MSJ_GENERICO_RECUPERACION);
            }
            ResultadoOperacion bloqueo = verificarBloqueo(u);
            if (!bloqueo.isExito()) {
                return bloqueo;
            }
            if (!u.tienePreguntaSeguridad()) {
                return error("Esta cuenta no tiene pregunta de seguridad. Pide a un administrador que restablezca tu acceso.");
            }
            return ok("OK", u.getPreguntaSeguridad());
        } catch (PersistenciaException e) {
            return error(e.getMessage());
        }
    }

    public synchronized ResultadoOperacion restablecerContrasena(String cedula, String respuesta, String nueva, String confirmar) {
        String problema = Validador.errorCedula(cedula);
        if (problema == null) {
            problema = Validador.errorContrasena(nueva);
        }
        if (problema == null && !nueva.equals(confirmar)) {
            problema = "Las contraseñas no coinciden";
        }
        if (problema == null && (respuesta == null || respuesta.trim().isEmpty())) {
            problema = "Escribe la respuesta a tu pregunta de seguridad";
        }
        if (problema != null) {
            return error(problema);
        }
        try {
            Usuario u = usuarios.buscaPorCedula(cedula.trim());
            if (u == null || Usuario.ESTADO_INACTIVO.equalsIgnoreCase(u.getEstado()) || !u.tienePreguntaSeguridad()) {
                return error(MSJ_GENERICO_RECUPERACION);
            }
            ResultadoOperacion bloqueo = verificarBloqueo(u);
            if (!bloqueo.isExito()) {
                return bloqueo;
            }
            if (!u.validarRespuestaSeguridad(respuesta)) {
                int intentos = u.getIntentosRecuperacion() + 1;
                if (intentos >= MAX_INTENTOS) {
                    u.setIntentosRecuperacion(0);
                    u.setBloqueoHasta(System.currentTimeMillis() + BLOQUEO_MS);
                    usuarios.actualiza(u);
                    return error("Respuesta incorrecta. La recuperación quedó bloqueada por 15 minutos.");
                }
                u.setIntentosRecuperacion(intentos);
                usuarios.actualiza(u);
                return error("Respuesta incorrecta. Te quedan " + (MAX_INTENTOS - intentos) + " intento(s).");
            }
            u.definirContrasena(nueva);
            u.setClavePendiente(false);
            u.setIntentosRecuperacion(0);
            u.setBloqueoHasta(0);
            usuarios.actualiza(u);
            if (u.getLogin() != null) {
                FALLOS_LOGIN.remove(u.getLogin().toLowerCase());
            }
            return ok("Contraseña actualizada. Ya puedes iniciar sesión.", u);
        } catch (PersistenciaException e) {
            return error(e.getMessage());
        }
    }

    private ResultadoOperacion verificarBloqueo(Usuario u) {
        long restante = u.getBloqueoHasta() - System.currentTimeMillis();
        if (restante > 0) {
            return error("Recuperación bloqueada por demasiados intentos. Intenta en " + ((restante + 59_999) / 60_000) + " minuto(s).");
        }
        return ok("OK", null);
    }

    // ---------------------------------------------------------------- perfil

    public synchronized ResultadoOperacion actualizarPerfil(String idUsuario, String nombre, String email, String telefono) {
        try {
            Usuario u = usuarios.obtenPorIdUsuario(idUsuario);
            if (u == null) {
                return error("Usuario no encontrado");
            }
            String problema = null;
            boolean nombreEditable = u.getRol() != Rol.CONDUCTOR;
            if (nombreEditable) {
                problema = Validador.errorNombre(nombre);
            }
            if (problema == null) problema = Validador.errorEmailOpcional(email);
            if (problema == null) problema = Validador.errorTelefonoOpcional(telefono);
            if (problema == null) problema = verificarEmailUnico(email, u.getIdUsuario());
            if (problema != null) {
                return error(problema);
            }
            if (nombreEditable) {
                u.setNombre(nombre.trim());
            }
            u.setEmail(vacioSiNulo(email).trim());
            u.setTelefono(vacioSiNulo(telefono).trim());
            usuarios.actualiza(u);
            sincronizarFicha(u);
            return ok("Perfil actualizado", u);
        } catch (PersistenciaException e) {
            return error(e.getMessage());
        }
    }

    public synchronized ResultadoOperacion cambiarContrasena(String idUsuario, String actual, String nueva, String confirmar) {
        try {
            Usuario u = usuarios.obtenPorIdUsuario(idUsuario);
            if (u == null) {
                return error("Usuario no encontrado");
            }
            if (!u.validarContrasena(actual)) {
                return error("La contraseña actual no es correcta");
            }
            String problema = Validador.errorContrasena(nueva);
            if (problema == null && !nueva.equals(confirmar)) problema = "Las contraseñas nuevas no coinciden";
            if (problema == null && nueva.equals(actual)) problema = "La nueva contraseña debe ser distinta de la actual";
            if (problema != null) {
                return error(problema);
            }
            u.definirContrasena(nueva);
            u.setClavePendiente(false);
            usuarios.actualiza(u);
            return ok("Contraseña actualizada", u);
        } catch (PersistenciaException e) {
            return error(e.getMessage());
        }
    }

    public synchronized ResultadoOperacion cambiarPreguntaSeguridad(String idUsuario, String contrasenaActual, String pregunta, String respuesta) {
        try {
            Usuario u = usuarios.obtenPorIdUsuario(idUsuario);
            if (u == null) {
                return error("Usuario no encontrado");
            }
            if (!u.validarContrasena(contrasenaActual)) {
                return error("La contraseña actual no es correcta");
            }
            String problema = Validador.errorPreguntaSeguridad(pregunta);
            if (problema == null) problema = Validador.errorRespuestaSeguridad(respuesta);
            if (problema != null) {
                return error(problema);
            }
            u.setPreguntaSeguridad(pregunta.trim());
            u.definirRespuestaSeguridad(respuesta);
            u.setIntentosRecuperacion(0);
            u.setBloqueoHasta(0);
            usuarios.actualiza(u);
            return ok("Pregunta de seguridad actualizada", u);
        } catch (PersistenciaException e) {
            return error(e.getMessage());
        }
    }

    // ---------------------------------------------------------------- administración de usuarios

    public synchronized ResultadoOperacion actualizarUsuarioAdministrativo(Usuario actor, String idUsuario, String cargo, String nombre, String email,
                                                                           String telefono, String estado, String nuevaClave) {
        try {
            Usuario objetivo = usuarios.obtenPorIdUsuario(idUsuario);
            ResultadoOperacion permiso = verificarAlcance(actor, objetivo);
            if (!permiso.isExito()) {
                return permiso;
            }
            String problema = exigirPrincipalSiEsAdministrador(actor, objetivo);
            Cargo cargoNuevo = null;
            if (problema == null && !objetivo.esAdministradorPrincipal() && objetivo.getRol().esAdministrativo()) {
                cargoNuevo = Cargo.desdeEtiqueta(cargo);
                if (cargoNuevo == null || cargoNuevo.getRol() != objetivo.getRol() || cargoNuevo == Cargo.ADMINISTRADOR_PRINCIPAL) {
                    problema = "Selecciona un cargo válido para el rol " + objetivo.getTipo();
                }
            }
            if (problema == null && objetivo.getRol() != Rol.CONDUCTOR) problema = Validador.errorNombre(nombre);
            if (problema == null) problema = Validador.errorEmailOpcional(email);
            if (problema == null) problema = Validador.errorTelefonoOpcional(telefono);
            if (problema == null) problema = verificarEmailUnico(email, objetivo.getIdUsuario());
            if (problema == null && nuevaClave != null && !nuevaClave.isEmpty()) problema = Validador.errorContrasena(nuevaClave);
            if (problema == null && !(Usuario.ESTADO_ACTIVO.equals(estado) || Usuario.ESTADO_INACTIVO.equals(estado) || Usuario.ESTADO_PENDIENTE.equals(estado))) {
                problema = "Estado inválido";
            }
            boolean desactiva = !Usuario.ESTADO_ACTIVO.equals(estado) && objetivo.estaActivo();
            if (problema == null && desactiva) {
                problema = motivoNoDesactivable(actor, objetivo);
            }
            if (problema != null) {
                return error(problema);
            }
            if (objetivo.getRol() != Rol.CONDUCTOR) {
                objetivo.setNombre(nombre.trim());
            }
            objetivo.setEmail(vacioSiNulo(email).trim());
            objetivo.setTelefono(vacioSiNulo(telefono).trim());
            objetivo.setEstado(estado);
            if (cargoNuevo != null) {
                objetivo.setCargo(cargoNuevo.getEtiqueta());
            }
            if (nuevaClave != null && !nuevaClave.isEmpty()) {
                objetivo.definirContrasena(nuevaClave);
                // una clave puesta por otra persona es temporal: quien la recibe debe cambiarla
                objetivo.setClavePendiente(!objetivo.getIdUsuario().equals(actor.getIdUsuario()));
                objetivo.setIntentosRecuperacion(0);
                objetivo.setBloqueoHasta(0);
            }
            usuarios.actualiza(objetivo);
            sincronizarFicha(objetivo);
            return ok("Usuario actualizado", objetivo);
        } catch (PersistenciaException e) {
            return error(e.getMessage());
        }
    }

    public synchronized ResultadoOperacion eliminarUsuario(Usuario actor, String idUsuario) {
        try {
            Usuario objetivo = usuarios.obtenPorIdUsuario(idUsuario);
            ResultadoOperacion permiso = verificarAlcance(actor, objetivo);
            if (!permiso.isExito()) {
                return permiso;
            }
            String sinPermiso = exigirPrincipalSiEsAdministrador(actor, objetivo);
            if (sinPermiso != null) {
                return error(sinPermiso);
            }
            String motivo = motivoNoDesactivable(actor, objetivo);
            if (motivo != null) {
                return error(motivo.replace("desactivar", "eliminar"));
            }
            usuarios.elimina(objetivo);
            return ok("Usuario eliminado", objetivo);
        } catch (PersistenciaException e) {
            return error(e.getMessage());
        }
    }

    public synchronized ResultadoOperacion aprobarUsuario(Usuario actor, String idUsuario) {
        try {
            Usuario objetivo = usuarios.obtenPorIdUsuario(idUsuario);
            ResultadoOperacion permiso = verificarAlcance(actor, objetivo);
            if (!permiso.isExito()) {
                return permiso;
            }
            if (!Usuario.ESTADO_PENDIENTE.equalsIgnoreCase(objetivo.getEstado())) {
                return error("Solo se pueden aprobar cuentas en estado PENDIENTE");
            }
            objetivo.setEstado(Usuario.ESTADO_ACTIVO);
            usuarios.actualiza(objetivo);
            return ok("Cuenta aprobada: " + objetivo.getNombre(), objetivo);
        } catch (PersistenciaException e) {
            return error(e.getMessage());
        }
    }

    // ---------------------------------------------------------------- utilidades internas

    private ResultadoOperacion exigirAdministrador(Usuario actor) {
        if (actor == null || actor.getRol() != Rol.ADMINISTRADOR || !actor.estaActivo()) {
            return error("Solo un administrador activo puede realizar esta acción");
        }
        return ok("OK", null);
    }

    private ResultadoOperacion verificarAlcance(Usuario actor, Usuario objetivo) {
        ResultadoOperacion permiso = exigirAdministrador(actor);
        if (!permiso.isExito()) {
            return permiso;
        }
        if (objetivo == null) {
            return error("Usuario no encontrado");
        }
        if (!actor.esSuperAdministrador()) {
            boolean mismaEmpresa = objetivo.tieneEmpresa() && objetivo.getEmpresa().getIdEmpresa().equals(actor.getEmpresa().getIdEmpresa());
            if (!mismaEmpresa) {
                return error("No tienes permiso para gestionar a este usuario");
            }
        }
        return ok("OK", null);
    }

    /** Las cuentas de administrador solo las gestiona el administrador principal. */
    private String exigirPrincipalSiEsAdministrador(Usuario actor, Usuario objetivo) {
        if (objetivo.getRol() == Rol.ADMINISTRADOR && !actor.esAdministradorPrincipal()) {
            return "Solo el administrador principal puede modificar o eliminar cuentas de administrador";
        }
        return null;
    }

    private String motivoNoDesactivable(Usuario actor, Usuario objetivo) throws PersistenciaException {
        if (objetivo.esAdministradorPrincipal()) {
            return "El administrador principal no se puede desactivar ni eliminar";
        }
        if (actor.getIdUsuario() != null && actor.getIdUsuario().equals(objetivo.getIdUsuario())) {
            return "No puedes desactivar o eliminar tu propia cuenta";
        }
        if (objetivo.esSuperAdministrador() && objetivo.estaActivo()) {
            long activos = usuarios.lista().stream().filter(x -> x.esSuperAdministrador() && x.estaActivo()).count();
            if (activos <= 1) {
                return "No se puede desactivar al único super administrador activo";
            }
        }
        return null;
    }

    /** Valida los datos comunes de una cuenta nueva y comprueba unicidad de usuario, cédula y correo. */
    private String validarCuenta(String login, String nombre, String cedula, String clave, String confirmar, String email, String telefono,
                                 String pregunta, String respuesta, boolean exigirPregunta, String rolEspecial, boolean exigirContacto) throws PersistenciaException {
        String problema = Validador.errorLogin(login);
        if (problema == null && !"conductor".equals(rolEspecial)) problema = Validador.errorNombre(nombre);
        if (problema == null) problema = Validador.errorCedula(cedula);
        if (problema == null) problema = Validador.errorContrasena(clave);
        if (problema == null && (confirmar == null || !clave.equals(confirmar))) problema = "Las contraseñas no coinciden";
        if (problema == null) problema = exigirContacto ? Validador.errorEmailCuenta(email) : Validador.errorEmailOpcional(email);
        if (problema == null) problema = exigirContacto ? Validador.errorTelefonoCelular(telefono) : Validador.errorTelefonoOpcional(telefono);
        if (problema == null && exigirPregunta) {
            problema = Validador.errorPreguntaSeguridad(pregunta);
            if (problema == null) problema = Validador.errorRespuestaSeguridad(respuesta);
        }
        if (problema != null) {
            return problema;
        }
        if (usuarios.buscaPorLogin(login) != null) {
            return "Ese nombre de usuario ya está en uso";
        }
        if (usuarios.buscaPorCedula(cedula) != null) {
            return "Ya existe una cuenta con esa cédula";
        }
        return verificarEmailUnico(email, null);
    }

    private String verificarEmailUnico(String email, String idExcluido) throws PersistenciaException {
        if (email == null || email.trim().isEmpty()) {
            return null;
        }
        for (Usuario u : usuarios.lista()) {
            if (u.getEmail() != null && u.getEmail().equalsIgnoreCase(email.trim()) && !u.getIdUsuario().equals(idExcluido)) {
                return "Ese correo ya está registrado en otra cuenta";
            }
        }
        return null;
    }

    private String validarMayorDeEdad(Fecha nacimiento) {
        if (nacimiento == null) {
            return "La fecha de nacimiento es obligatoria";
        }
        try {
            LocalDate fecha = LocalDate.of(nacimiento.getAnio(), nacimiento.getMes(), nacimiento.getDia());
            if (fecha.isAfter(LocalDate.now())) {
                return "La fecha de nacimiento no puede ser futura";
            }
            int edad = Period.between(fecha, LocalDate.now()).getYears();
            if (edad < 18) {
                return "Debes ser mayor de edad (18 años) para crear una cuenta";
            }
            if (edad > 120) {
                return "La fecha de nacimiento no es válida";
            }
            return null;
        } catch (DateTimeException e) {
            return "La fecha de nacimiento no es válida";
        }
    }

    private Usuario nuevoUsuario(String login, String nombre, String cedula, String clave, String email, String telefono, Rol rol, String estado) {
        Usuario u = new Usuario();
        u.setLogin(login.trim());
        u.setNombre(nombre.trim());
        u.setCedula(cedula.trim());
        u.definirContrasena(clave);
        u.setEmail(vacioSiNulo(email).trim());
        u.setTelefono(vacioSiNulo(telefono).trim());
        u.setTipo(rol.getEtiqueta());
        u.setEstado(estado);
        u.setFechaRegistro(hoy());
        u.setPreguntaSeguridad("");
        u.setRespuestaSeguridad("");
        u.setIdPerfil("");
        return u;
    }

    private static Fecha hoy() {
        LocalDate d = LocalDate.now();
        return new Fecha(d.getDayOfMonth(), d.getMonthValue(), d.getYear());
    }

    private Pasajero buscarPasajeroPorCedula(String cedula) throws PersistenciaException {
        for (Pasajero p : pasajeros.lista()) {
            if (cedula.equals(p.getCedula())) {
                return p;
            }
        }
        return null;
    }

    private Conductor buscarConductorPorCedula(String cedula) throws PersistenciaException {
        for (Conductor c : conductores.lista()) {
            if (cedula.equals(c.getCedula())) {
                return c;
            }
        }
        return null;
    }

    /** Mantiene la ficha de pasajero/conductor alineada con los datos de contacto de la cuenta. */
    private void sincronizarFicha(Usuario u) throws PersistenciaException {
        if (u.getIdPerfil() == null || u.getIdPerfil().isEmpty()) {
            return;
        }
        if (u.getRol() == Rol.PASAJERO) {
            Pasajero p = pasajeros.obten(new Pasajero(u.getIdPerfil()));
            if (p != null) {
                p.setNombre(u.getNombre());
                p.setTelefono(vacioSiNulo(u.getTelefono()));
                p.setEmail(vacioSiNulo(u.getEmail()));
                pasajeros.actualiza(p);
            }
        } else if (u.getRol() == Rol.CONDUCTOR) {
            Conductor c = conductores.obten(new Conductor(u.getIdPerfil()));
            if (c != null && u.getTelefono() != null && !u.getTelefono().isEmpty()) {
                c.setTelefono(u.getTelefono());
                conductores.actualiza(c);
            }
        }
    }

    private static String vacioSiNulo(String s) {
        return s == null ? "" : s;
    }

    private static ResultadoOperacion error(String mensaje) {
        return new ResultadoOperacion(false, mensaje);
    }

    private static ResultadoOperacion ok(String mensaje, Object datos) {
        return new ResultadoOperacion(true, mensaje, datos);
    }
}