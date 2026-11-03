//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package persistencia;

import excepciones.PersistenciaException;
import excepciones.Validador;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import objetosNegocio.Empresa;
import objetosNegocio.Pasajero;

public class Pasajeros extends AccesoAleatorio {
    public Pasajeros(String nomArchivo) {
        super(nomArchivo, 470);
    }

    private Pasajero leePasajero() throws IOException {
        Pasajero pasajero = new Pasajero();
        pasajero.setIdPasajero(this.leeString(10));
        pasajero.setNombre(this.leeString(50));
        pasajero.setCedula(this.leeString(10));
        pasajero.setTelefono(this.leeString(15));
        pasajero.setEmail(this.leeString(50));
        pasajero.setDireccion(this.leeString(80));
        pasajero.setEmpresa(new Empresa(this.leeString(10)));
        pasajero.setFechaNacimiento(this.leeFecha());
        pasajero.setDiscapacidad(this.archivo.readBoolean());
        return pasajero;
    }

    private void escribePasajero(Pasajero pasajero) throws IOException {
        this.escribeString(pasajero.getIdPasajero(), 10);
        this.escribeString(pasajero.getNombre(), 50);
        this.escribeString(pasajero.getCedula(), 10);
        this.escribeString(pasajero.getTelefono(), 15);
        this.escribeString(pasajero.getEmail(), 50);
        this.escribeString(pasajero.getDireccion(), 80);
        String idEmpresa = pasajero.getEmpresa() != null ? pasajero.getEmpresa().getIdEmpresa() : "";
        this.escribeString(idEmpresa, 10);
        this.escribeFecha(pasajero.getFechaNacimiento());
        this.archivo.writeBoolean(pasajero.isDiscapacidad());
    }

    private boolean esPasajeroValido(Pasajero pasajero) {
        return pasajero != null && pasajero.getIdPasajero() != null && !pasajero.getIdPasajero().trim().isEmpty();
    }

    private String generarId() throws PersistenciaException {
        try {
            this.archivo = new RandomAccessFile(this.nomArchivo, "r");
            int max = 0;

            try {
                while(true) {
                    Pasajero p = this.leePasajero();
                    if (this.esPasajeroValido(p) && p.getIdPasajero().startsWith("PAS")) {
                        try {
                            int num = Integer.parseInt(p.getIdPasajero().substring(3));
                            if (num > max) {
                                max = num;
                            }
                        } catch (NumberFormatException var4) {
                        }
                    }
                }
            } catch (EOFException var5) {
                this.archivo.close();
                return String.format("PAS%03d", max + 1);
            }
        } catch (IOException var6) {
            return "PAS001";
        }
    }

    public void agrega(Pasajero pasajero) throws PersistenciaException {
        pasajero.setIdPasajero(this.generarId());
        if (pasajero.getEmpresa() != null && Validador.validarNoVacio(pasajero.getEmpresa().getIdEmpresa())) {
            try {
                this.archivo = new RandomAccessFile(this.nomArchivo, "rw");
                this.archivo.seek(this.archivo.length());
                this.escribePasajero(pasajero);
            } catch (IOException e) {
                throw new PersistenciaException("Error al agregar pasajero", e);
            } finally {
                this.cerrar();
            }

        } else {
            throw new PersistenciaException("Debe asignar una empresa al pasajero");
        }
    }

    public Pasajero obten(Pasajero pBusqueda) throws PersistenciaException {
        Pasajero var3;
        try {
            this.archivo = new RandomAccessFile(this.nomArchivo, "r");

            Pasajero p;
            do {
                p = this.leePasajero();
            } while(!this.esPasajeroValido(p) || !p.getIdPasajero().equals(pBusqueda.getIdPasajero()));

            var3 = p;
            return var3;
        } catch (EOFException var8) {
            var3 = null;
        } catch (IOException e) {
            throw new PersistenciaException("Error al leer pasajero", e);
        } finally {
            this.cerrar();
        }

        return var3;
    }

    public void actualiza(Pasajero pActualizar) throws PersistenciaException {
        if (pActualizar.getEmpresa() != null && Validador.validarNoVacio(pActualizar.getEmpresa().getIdEmpresa())) {
            try {
                this.archivo = new RandomAccessFile(this.nomArchivo, "rw");

                long pos;
                Pasajero p;
                do {
                    pos = this.archivo.getFilePointer();
                    p = this.leePasajero();
                } while(!this.esPasajeroValido(p) || !p.getIdPasajero().equals(pActualizar.getIdPasajero()));

                this.archivo.seek(pos);
                this.escribePasajero(pActualizar);
            } catch (IOException e) {
                throw new PersistenciaException("Error al actualizar pasajero", e);
            } finally {
                this.cerrar();
            }

        } else {
            throw new PersistenciaException("Debe asignar una empresa al pasajero");
        }
    }

    public void elimina(Pasajero pEliminar) throws PersistenciaException {
        File original = new File(this.nomArchivo);
        File temp = new File(this.nomArchivo + ".tmp");

        try {
            this.archivo = new RandomAccessFile(original, "r");
            List<Pasajero> todos = new ArrayList();
            boolean encontrado = false;

            try {
                while(true) {
                    Pasajero p = this.leePasajero();
                    if (this.esPasajeroValido(p)) {
                        if (!p.getIdPasajero().equals(pEliminar.getIdPasajero())) {
                            todos.add(p);
                        } else {
                            encontrado = true;
                        }
                    }
                }
            } catch (EOFException var8) {
                this.archivo.close();
                this.archivo = null;
                if (!encontrado) {
                    throw new PersistenciaException("El pasajero no existe");
                } else {
                    this.archivo = new RandomAccessFile(temp, "rw");

                    for(Pasajero p : todos) {
                        this.escribePasajero(p);
                    }

                    this.archivo.close();
                    this.archivo = null;
                    if (!original.delete()) {
                        throw new PersistenciaException("No se pudo eliminar el archivo original");
                    } else if (!temp.renameTo(original)) {
                        throw new PersistenciaException("No se pudo renombrar el archivo temporal");
                    }
                }
            }
        } catch (IOException e) {
            throw new PersistenciaException("Error al eliminar", e);
        }
    }

    public List<Pasajero> lista() throws PersistenciaException {
        List<Pasajero> lista = new ArrayList();

        Object var3;
        try {
            this.archivo = new RandomAccessFile(this.nomArchivo, "r");

            while(true) {
                Pasajero p = this.leePasajero();
                if (this.esPasajeroValido(p)) {
                    lista.add(p);
                }
            }
        } catch (EOFException var8) {
            var3 = lista;
        } catch (IOException e) {
            throw new PersistenciaException("Error al listar", e);
        } finally {
            this.cerrar();
        }

        return (List<Pasajero>)var3;
    }

    private void cerrar() {
        try {
            if (this.archivo != null) {
                this.archivo.close();
            }
        } catch (IOException var2) {
        }

    }

    private static boolean esPasajeroDeEmpresa(Pasajero p, String idEmpresa) {
        return p.getEmpresa() != null && idEmpresa.equals(p.getEmpresa().getIdEmpresa());
    }

    public List<Pasajero> listaPorEmpresa(String idEmpresa) throws PersistenciaException {
        return (List)this.lista().stream().filter((p) -> esPasajeroDeEmpresa(p, idEmpresa)).collect(Collectors.toList());
    }
}
