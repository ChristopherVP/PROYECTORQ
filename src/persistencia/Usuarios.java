//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package persistencia;

import excepciones.PersistenciaException;
import excepciones.Validador;
import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import objetosNegocio.Empresa;
import objetosNegocio.Usuario;

public class Usuarios extends AccesoAleatorio {
    public Usuarios(String nomArchivo) {
        super(nomArchivo, 442);
    }

    private Usuario leeUsuario() throws IOException {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(this.leeString(10));
        usuario.setNombre(this.leeString(50));
        usuario.setCedula(this.leeString(15));
        usuario.setContrasena(this.leeString(20));
        usuario.setEmail(this.leeString(50));
        usuario.setTelefono(this.leeString(20));
        usuario.setTipo(this.leeString(20));
        usuario.setFechaRegistro(this.leeFecha());
        usuario.setEmpresa(new Empresa(this.leeString(10)));
        usuario.setEstado(this.leeString(20));
        return usuario;
    }

    private void escribeUsuario(Usuario usuario) throws IOException {
        this.escribeString(usuario.getIdUsuario(), 10);
        this.escribeString(usuario.getNombre(), 50);
        this.escribeString(usuario.getCedula(), 15);
        this.escribeString(usuario.getContrasena(), 20);
        this.escribeString(usuario.getEmail(), 50);
        this.escribeString(usuario.getTelefono(), 20);
        this.escribeString(usuario.getTipo(), 20);
        this.escribeFecha(usuario.getFechaRegistro());
        String idEmpresa = usuario.getEmpresa() != null ? usuario.getEmpresa().getIdEmpresa() : "";
        this.escribeString(idEmpresa, 10);
        this.escribeString(usuario.getEstado(), 20);
    }

    private boolean esUsuarioValido(Usuario usuario) {
        return usuario != null && usuario.getIdUsuario() != null && !usuario.getIdUsuario().trim().isEmpty();
    }

    private String generarSiguienteCodigoUsuario() throws PersistenciaException {
        try {
            this.archivo = new RandomAccessFile(this.nomArchivo, "r");
            int maxNumero = 0;

            try {
                while(true) {
                    Usuario usuario = this.leeUsuario();
                    if (this.esUsuarioValido(usuario)) {
                        String codigo = usuario.getIdUsuario();
                        if (codigo.startsWith("USU") && codigo.length() > 3) {
                            try {
                                int numero = Integer.parseInt(codigo.substring(3));
                                if (numero > maxNumero) {
                                    maxNumero = numero;
                                }
                            } catch (NumberFormatException var5) {
                            }
                        }
                    }
                }
            } catch (EOFException var6) {
                this.archivo.close();
                int siguienteNumero = maxNumero + 1;
                return String.format("USU%03d", siguienteNumero);
            }
        } catch (FileNotFoundException var7) {
            return "USU001";
        } catch (IOException e) {
            throw new PersistenciaException("Error al generar código de usuario", e);
        }
    }

    private void validarUsuario(Usuario usuario) throws PersistenciaException {
        if (!Validador.validarNoVacio(usuario.getNombre())) {
            throw new PersistenciaException("El nombre no puede estar vacío");
        } else if (!Validador.validarCedulaEcuatoriana(usuario.getCedula())) {
            throw new PersistenciaException(Validador.getMensajeErrorCedula());
        } else if (!Validador.validarNoVacio(usuario.getContrasena())) {
            throw new PersistenciaException("La contraseña no puede estar vacía");
        } else if (usuario.getContrasena().length() < 8) {
            throw new PersistenciaException("La contraseña debe tener al menos 8 caracteres");
        } else if (!Validador.validarEmail(usuario.getEmail())) {
            throw new PersistenciaException(Validador.getMensajeErrorEmail());
        } else if (usuario.getTelefono() != null && !usuario.getTelefono().trim().isEmpty() && !Validador.validarTelefono(usuario.getTelefono())) {
            throw new PersistenciaException("Teléfono inválido. Debe tener entre 9 y 10 dígitos");
        } else if (!Validador.validarNoVacio(usuario.getTipo())) {
            throw new PersistenciaException("El tipo de usuario no puede estar vacío");
        } else if (usuario.getEmpresa() == null || !Validador.validarNoVacio(usuario.getEmpresa().getIdEmpresa())) {
            throw new PersistenciaException("Los usuarios administrativos/operadores deben pertenecer a una empresa");
        }
    }

    public Usuario obten(Usuario usuario) throws PersistenciaException {
        Usuario var3;
        try {
            this.archivo = new RandomAccessFile(this.nomArchivo, "r");

            Usuario usuarioLeido;
            do {
                usuarioLeido = this.leeUsuario();
            } while(!this.esUsuarioValido(usuarioLeido) || !usuarioLeido.getIdUsuario().equals(usuario.getIdUsuario()));

            var3 = usuarioLeido;
            return var3;
        } catch (FileNotFoundException var15) {
            throw new PersistenciaException("Archivo inexistente");
        } catch (EOFException var16) {
            var3 = null;
        } catch (IOException e) {
            throw new PersistenciaException("Error al acceder al archivo", e);
        } finally {
            try {
                if (this.archivo != null) {
                    this.archivo.close();
                }
            } catch (IOException e) {
                throw new PersistenciaException("Error al cerrar el archivo", e);
            }

        }

        return var3;
    }

    public void agrega(Usuario usuario) throws PersistenciaException {
        String codigoGenerado = this.generarSiguienteCodigoUsuario();
        usuario.setIdUsuario(codigoGenerado);
        if (usuario.getEstado() == null) {
            usuario.setEstado("ACTIVO");
        }

        this.validarUsuario(usuario);

        try {
            this.archivo = new RandomAccessFile(this.nomArchivo, "rw");
            this.archivo.seek(this.archivo.length());
            this.escribeUsuario(usuario);
            System.out.println("Usuario Agregado: " + usuario.getIdUsuario());
        } catch (FileNotFoundException var11) {
            throw new PersistenciaException("Archivo inexistente");
        } catch (IOException e) {
            throw new PersistenciaException("Error al acceder al archivo", e);
        } finally {
            try {
                if (this.archivo != null) {
                    this.archivo.close();
                }
            } catch (IOException e) {
                throw new PersistenciaException("Error al cerrar el archivo", e);
            }

        }

    }

    public void actualiza(Usuario usuario) throws PersistenciaException {
        this.validarUsuario(usuario);
        boolean encontrado = false;

        try {
            this.archivo = new RandomAccessFile(this.nomArchivo, "rw");

            long posicionAntes;
            Usuario usuarioLeido;
            do {
                posicionAntes = this.archivo.getFilePointer();
                usuarioLeido = this.leeUsuario();
            } while(!this.esUsuarioValido(usuarioLeido) || !usuarioLeido.getIdUsuario().equals(usuario.getIdUsuario()));

            this.archivo.seek(posicionAntes);
            this.escribeUsuario(usuario);
            System.out.println("✓ Usuario actualizado: " + usuario.getIdUsuario());
            encontrado = true;
        } catch (FileNotFoundException var16) {
            throw new PersistenciaException("Archivo inexistente");
        } catch (EOFException var17) {
            if (!encontrado) {
                throw new PersistenciaException("El usuario no existe");
            }
        } catch (IOException e) {
            throw new PersistenciaException("Error al acceder al archivo", e);
        } finally {
            try {
                if (this.archivo != null) {
                    this.archivo.close();
                }
            } catch (IOException e) {
                throw new PersistenciaException("Error al cerrar el archivo", e);
            }

        }

        if (!encontrado) {
            throw new PersistenciaException("El usuario no existe");
        }
    }

    public void elimina(Usuario usuario) throws PersistenciaException {
        try {
            this.archivo = new RandomAccessFile(this.nomArchivo, "rw");
            boolean encontrado = false;

            long posicionAntes;
            Usuario usr;
            do {
                posicionAntes = this.archivo.getFilePointer();
                usr = this.leeUsuario();
            } while(!this.esUsuarioValido(usr) || !usr.getIdUsuario().equals(usuario.getIdUsuario()));

            usr.setEstado("INACTIVO");
            this.archivo.seek(posicionAntes);
            this.escribeUsuario(usr);
            encontrado = true;
            System.out.println("✓ Usuario desactivado (borrado lógico): " + usr.getIdUsuario());
            if (!encontrado) {
                throw new PersistenciaException("El usuario no existe");
            }
        } catch (FileNotFoundException var14) {
            throw new PersistenciaException("Archivo inexistente");
        } catch (IOException e) {
            throw new PersistenciaException("Error: " + e.getMessage(), e);
        } finally {
            try {
                if (this.archivo != null) {
                    this.archivo.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    public List<Usuario> lista() throws PersistenciaException {
        List<Usuario> lista = new ArrayList();

        Object var3;
        try {
            this.archivo = new RandomAccessFile(this.nomArchivo, "r");

            while(true) {
                Usuario usuarioLeido = this.leeUsuario();
                if (this.esUsuarioValido(usuarioLeido)) {
                    lista.add(usuarioLeido);
                }
            }
        } catch (FileNotFoundException var14) {
            throw new PersistenciaException("Archivo inexistente");
        } catch (EOFException var15) {
            var3 = lista;
        } catch (IOException e) {
            throw new PersistenciaException("Error al acceder al archivo", e);
        } finally {
            try {
                if (this.archivo != null) {
                    this.archivo.close();
                }
            } catch (IOException e) {
                throw new PersistenciaException("Error al cerrar", e);
            }

        }

        return (List<Usuario>)var3;
    }

    private static boolean esUsuarioDeTipo(Usuario usuario, String tipo) {
        return usuario.getTipo() != null && tipo.equalsIgnoreCase(usuario.getTipo().trim());
    }

    public List<Usuario> listaPorTipo(String tipo) throws PersistenciaException {
        return (List)this.lista().stream().filter((usuario) -> esUsuarioDeTipo(usuario, tipo)).collect(Collectors.toList());
    }

    public Usuario obtenPorIdUsuario(String idUsuario) throws PersistenciaException {
        return this.obten(new Usuario(idUsuario));
    }
}
