//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package persistencia;

import excepciones.PersistenciaException;
import excepciones.Validador;
import java.io.EOFException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import objetosNegocio.Conductor;
import objetosNegocio.Empresa;

public class Conductores extends AccesoAleatorio {
    public Conductores(String nomArchivo) {
        super(nomArchivo, 290);
    }

    private Conductor leeConductor() throws IOException {
        Conductor c = new Conductor();
        c.setIdConductor(this.leeString(10));
        c.setNombre(this.leeString(50));
        c.setCedula(this.leeString(15));
        c.setTelefono(this.leeString(20));
        c.setLicencia(this.leeString(20));
        c.setEstado(this.leeString(20));
        c.setEmpresa(new Empresa(this.leeString(10)));
        return c;
    }

    private void escribeConductor(Conductor c) throws IOException {
        this.escribeString(c.getIdConductor(), 10);
        this.escribeString(c.getNombre(), 50);
        this.escribeString(c.getCedula(), 15);
        this.escribeString(c.getTelefono(), 20);
        this.escribeString(c.getLicencia(), 20);
        this.escribeString(c.getEstado(), 20);
        String idEmpresa = c.getEmpresa() != null ? c.getEmpresa().getIdEmpresa() : "";
        this.escribeString(idEmpresa, 10);
    }

    private boolean esConductorValido(Conductor c) {
        return c != null && c.getIdConductor() != null && !c.getIdConductor().trim().isEmpty();
    }

    private String generarSiguienteCodigo() throws PersistenciaException {
        try {
            this.archivo = new RandomAccessFile(this.nomArchivo, "r");
            int max = 0;

            try {
                while(true) {
                    Conductor c = this.leeConductor();
                    if (this.esConductorValido(c) && c.getIdConductor().startsWith("CDN")) {
                        try {
                            int num = Integer.parseInt(c.getIdConductor().substring(3));
                            if (num > max) {
                                max = num;
                            }
                        } catch (NumberFormatException var4) {
                        }
                    }
                }
            } catch (EOFException var5) {
                this.archivo.close();
                return String.format("CDN%03d", max + 1);
            }
        } catch (IOException var6) {
            return "CDN001";
        }
    }

    private void validarConductor(Conductor c) throws PersistenciaException {
        if (!Validador.validarNoVacio(c.getNombre())) {
            throw new PersistenciaException("El nombre es obligatorio");
        } else if (!Validador.validarCedulaEcuatoriana(c.getCedula())) {
            throw new PersistenciaException(Validador.getMensajeErrorCedula());
        } else if (!Validador.validarNoVacio(c.getLicencia())) {
            throw new PersistenciaException("La licencia es obligatoria");
        } else if (c.getEmpresa() == null || !Validador.validarNoVacio(c.getEmpresa().getIdEmpresa())) {
            throw new PersistenciaException("El conductor debe pertenecer a una empresa");
        }
    }

    public void agrega(Conductor c) throws PersistenciaException {
        c.setIdConductor(this.generarSiguienteCodigo());
        if (c.getEstado() == null) {
            c.setEstado("ACTIVO");
        }

        this.validarConductor(c);

        try {
            this.archivo = new RandomAccessFile(this.nomArchivo, "rw");
            this.archivo.seek(this.archivo.length());
            this.escribeConductor(c);
            System.out.println("Conductor agregado: " + c.getIdConductor());
        } catch (IOException e) {
            throw new PersistenciaException("Error al agregar conductor", e);
        } finally {
            try {
                if (this.archivo != null) {
                    this.archivo.close();
                }
            } catch (IOException var9) {
            }

        }

    }

    public Conductor obten(Conductor busqueda) throws PersistenciaException {
        Conductor var3;
        try {
            this.archivo = new RandomAccessFile(this.nomArchivo, "r");

            Conductor c;
            do {
                c = this.leeConductor();
            } while(!this.esConductorValido(c) || !c.getIdConductor().equals(busqueda.getIdConductor()));

            var3 = c;
            return var3;
        } catch (EOFException var14) {
            var3 = null;
        } catch (IOException e) {
            throw new PersistenciaException("Error al leer conductor", e);
        } finally {
            try {
                if (this.archivo != null) {
                    this.archivo.close();
                }
            } catch (IOException var13) {
            }

        }

        return var3;
    }

    public void actualiza(Conductor c) throws PersistenciaException {
        this.validarConductor(c);
        boolean encontrado = false;

        try {
            this.archivo = new RandomAccessFile(this.nomArchivo, "rw");

            long pos;
            Conductor leido;
            do {
                pos = this.archivo.getFilePointer();
                leido = this.leeConductor();
            } while(!this.esConductorValido(leido) || !leido.getIdConductor().equals(c.getIdConductor()));

            this.archivo.seek(pos);
            this.escribeConductor(c);
            encontrado = true;
        } catch (IOException e) {
            throw new PersistenciaException("Error al actualizar conductor", e);
        } finally {
            try {
                if (this.archivo != null) {
                    this.archivo.close();
                }
            } catch (IOException var12) {
            }

        }

        if (!encontrado) {
            throw new PersistenciaException("El conductor no existe");
        }
    }

    public void elimina(Conductor c) throws PersistenciaException {
        try {
            this.archivo = new RandomAccessFile(this.nomArchivo, "rw");
            boolean encontrado = false;

            long pos;
            Conductor leido;
            do {
                pos = this.archivo.getFilePointer();
                leido = this.leeConductor();
            } while(!this.esConductorValido(leido) || !leido.getIdConductor().equals(c.getIdConductor()));

            leido.setEstado("INACTIVO");
            this.archivo.seek(pos);
            this.escribeConductor(leido);
            encontrado = true;
            if (!encontrado) {
                throw new PersistenciaException("El conductor no existe");
            }
        } catch (IOException e) {
            throw new PersistenciaException("Error al eliminar conductor", e);
        } finally {
            try {
                if (this.archivo != null) {
                    this.archivo.close();
                }
            } catch (IOException var12) {
            }

        }

    }

    public List<Conductor> lista() throws PersistenciaException {
        List<Conductor> lista = new ArrayList();

        Object var3;
        try {
            this.archivo = new RandomAccessFile(this.nomArchivo, "r");

            while(true) {
                Conductor c = this.leeConductor();
                if (this.esConductorValido(c)) {
                    lista.add(c);
                }
            }
        } catch (EOFException var13) {
            var3 = lista;
        } catch (IOException e) {
            throw new PersistenciaException("Error al listar conductores", e);
        } finally {
            try {
                if (this.archivo != null) {
                    this.archivo.close();
                }
            } catch (IOException var12) {
            }

        }

        return (List<Conductor>)var3;
    }

    private static boolean esConductorDeEmpresa(Conductor c, String idEmpresa) {
        return c.getEmpresa() != null && idEmpresa.equals(c.getEmpresa().getIdEmpresa());
    }

    public List<Conductor> listaPorEmpresa(String idEmpresa) throws PersistenciaException {
        return (List)this.lista().stream().filter((c) -> esConductorDeEmpresa(c, idEmpresa)).collect(Collectors.toList());
    }
}
