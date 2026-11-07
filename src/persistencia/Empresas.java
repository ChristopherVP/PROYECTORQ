//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package persistencia;

import excepciones.PersistenciaException;
import excepciones.Validador;
import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import objetosNegocio.Empresa;

public class Empresas extends AccesoAleatorio {
    public Empresas(String nomArchivo) {
        super(nomArchivo, 450);
    }

    private Empresa leeEmpresa() throws IOException {
        Empresa empresa = new Empresa();
        empresa.setIdEmpresa(this.leeString(10));
        empresa.setNombre(this.leeString(50));
        empresa.setRuc(this.leeString(15));
        empresa.setDireccion(this.leeString(80));
        empresa.setTelefono(this.leeString(20));
        empresa.setEmail(this.leeString(50));
        return empresa;
    }

    private void escribeEmpresa(Empresa empresa) throws IOException {
        this.escribeString(empresa.getIdEmpresa(), 10);
        this.escribeString(empresa.getNombre(), 50);
        this.escribeString(empresa.getRuc(), 15);
        this.escribeString(empresa.getDireccion(), 80);
        this.escribeString(empresa.getTelefono(), 20);
        this.escribeString(empresa.getEmail(), 50);
    }

    private String generarSiguienteCodigoEmpresa() throws PersistenciaException {
        try {
            this.archivo = new RandomAccessFile(this.nomArchivo, "r");
            int maxNumero = 0;

            try {
                while(true) {
                    Empresa empresa = this.leeEmpresa();
                    if (this.esEmpresaValida(empresa)) {
                        String codigo = empresa.getIdEmpresa();
                        if (codigo.startsWith("EMP") && codigo.length() > 3) {
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
                return String.format("EMP%03d", siguienteNumero);
            }
        } catch (FileNotFoundException var7) {
            return "EMP001";
        } catch (IOException e) {
            throw new PersistenciaException("Error al generar código de empresa", e);
        }
    }

    private void validarEmpresa(Empresa empresa) throws PersistenciaException {
        if (!Validador.validarNoVacio(empresa.getNombre())) {
            throw new PersistenciaException("El nombre de la empresa no puede estar vacío");
        } else if (!Validador.validarRuc(empresa.getRuc())) {
            throw new PersistenciaException(Validador.getMensajeErrorRuc());
        } else if (!Validador.validarEmail(empresa.getEmail())) {
            throw new PersistenciaException(Validador.getMensajeErrorEmail());
        } else if (empresa.getTelefono() != null && !empresa.getTelefono().trim().isEmpty() && !Validador.validarTelefono(empresa.getTelefono())) {
            throw new PersistenciaException("Teléfono inválido. Debe tener entre 9 y 10 dígitos");
        } else if (!Validador.validarNoVacio(empresa.getDireccion())) {
            throw new PersistenciaException("La dirección no puede estar vacía");
        }
    }

    private boolean esEmpresaValida(Empresa empresa) {
        return empresa != null && empresa.getIdEmpresa() != null && !empresa.getIdEmpresa().trim().isEmpty();
    }

    public Empresa obten(Empresa empresa) throws PersistenciaException {
        Empresa var3;
        try {
            this.archivo = new RandomAccessFile(this.nomArchivo, "r");

            Empresa empresaLeida;
            do {
                empresaLeida = this.leeEmpresa();
            } while(!this.esEmpresaValida(empresaLeida) || !empresa.equals(empresaLeida));

            var3 = empresaLeida;
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

    public void agrega(Empresa empresa) throws PersistenciaException {
        String codigoGenerado = this.generarSiguienteCodigoEmpresa();
        empresa.setIdEmpresa(codigoGenerado);
        this.validarEmpresa(empresa);

        try {
            this.archivo = new RandomAccessFile(this.nomArchivo, "rw");
            this.archivo.seek(this.archivo.length());
            this.escribeEmpresa(empresa);
            System.out.println("Empresa escrita en archivo en posición: " + this.archivo.getFilePointer());
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

    public void actualiza(Empresa empresa) throws PersistenciaException {
        this.validarEmpresa(empresa);
        boolean encontrada = false;

        try {
            this.archivo = new RandomAccessFile(this.nomArchivo, "rw");

            long posicionAntes;
            Empresa empresaLeida;
            do {
                posicionAntes = this.archivo.getFilePointer();
                empresaLeida = this.leeEmpresa();
            } while(!this.esEmpresaValida(empresaLeida) || !empresaLeida.getIdEmpresa().equals(empresa.getIdEmpresa()));

            this.archivo.seek(posicionAntes);
            this.escribeEmpresa(empresa);
            System.out.println("Empresa actualizada: " + empresa.getIdEmpresa());
            encontrada = true;
        } catch (FileNotFoundException var16) {
            throw new PersistenciaException("Archivo inexistente");
        } catch (EOFException var17) {
            if (!encontrada) {
                throw new PersistenciaException("La empresa no existe");
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

        if (!encontrada) {
            throw new PersistenciaException("La empresa no existe");
        }
    }

    public void elimina(Empresa empresa) throws PersistenciaException {
        File archivoOriginal = new File(this.nomArchivo);
        File archivoTemporal = new File(this.nomArchivo + ".tmp");

        try {
            System.out.println("\n=== ELIMINANDO EMPRESA: " + empresa.getIdEmpresa() + " ===");
            this.archivo = new RandomAccessFile(archivoOriginal, "r");
            List<Empresa> todasLasEmpresas = new ArrayList();
            boolean encontrada = false;

            try {
                while(true) {
                    Empresa emp = this.leeEmpresa();
                    if (this.esEmpresaValida(emp)) {
                        if (!emp.getIdEmpresa().equals(empresa.getIdEmpresa())) {
                            todasLasEmpresas.add(emp);
                            System.out.println("  → Mantener: " + emp.getIdEmpresa());
                        } else {
                            encontrada = true;
                            System.out.println("  ✗ Eliminar: " + emp.getIdEmpresa());
                        }
                    }
                }
            } catch (EOFException var17) {
                this.archivo.close();
                this.archivo = null;
                if (!encontrada) {
                    throw new PersistenciaException("La empresa no existe");
                }
            }

            System.out.println("Total empresas a mantener: " + todasLasEmpresas.size());
            this.archivo = new RandomAccessFile(archivoTemporal, "rw");

            for(Empresa emp : todasLasEmpresas) {
                this.escribeEmpresa(emp);
            }

            this.archivo.close();
            this.archivo = null;
            if (!archivoOriginal.delete()) {
                throw new PersistenciaException("No se pudo eliminar el archivo original");
            }

            if (!archivoTemporal.renameTo(archivoOriginal)) {
                throw new PersistenciaException("No se pudo renombrar el archivo temporal");
            }

            System.out.println("✓ Empresa eliminada exitosamente");
            System.out.println("===================================\n");
        } catch (FileNotFoundException var18) {
            throw new PersistenciaException("Archivo inexistente");
        } catch (IOException e) {
            throw new PersistenciaException("Error al acceder al archivo: " + e.getMessage(), e);
        } finally {
            try {
                if (this.archivo != null) {
                    this.archivo.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (archivoTemporal.exists()) {
                archivoTemporal.delete();
            }

        }

    }

    public List<Empresa> lista() throws PersistenciaException {
        List<Empresa> lista = new ArrayList();

        Object var3;
        try {
            this.archivo = new RandomAccessFile(this.nomArchivo, "r");

            while(true) {
                Empresa empresaLeida = this.leeEmpresa();
                if (this.esEmpresaValida(empresaLeida)) {
                    lista.add(empresaLeida);
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
                throw new PersistenciaException("Error al cerrar el archivo", e);
            }

        }

        return (List<Empresa>)var3;
    }
}