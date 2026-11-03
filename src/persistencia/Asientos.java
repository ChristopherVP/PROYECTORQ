//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package persistencia;

import excepciones.PersistenciaException;
import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import objetosNegocio.Asiento;

public class Asientos extends AccesoAleatorio {
    public Asientos(String nomArchivo) {
        super(nomArchivo, 174);
    }

    private Asiento leeAsiento() throws IOException {
        Asiento asiento = new Asiento();
        asiento.setIdAsiento(this.leeString(15));
        asiento.setNumeroAsiento(this.archivo.readInt());
        asiento.setEstado(this.leeString(15));
        asiento.setIdViaje(this.leeString(10));
        asiento.setIdReserva(this.leeString(10));
        asiento.setUbicacion(this.leeString(15));
        asiento.setLiberadoEn(this.leeString(20));
        return asiento;
    }

    private void escribeAsiento(Asiento asiento) throws IOException {
        this.escribeString(asiento.getIdAsiento(), 15);
        this.archivo.writeInt(asiento.getNumeroAsiento());
        this.escribeString(asiento.getEstado(), 15);
        this.escribeString(asiento.getIdViaje(), 10);
        this.escribeString(asiento.getIdReserva() != null ? asiento.getIdReserva() : "", 10);
        this.escribeString(asiento.getUbicacion(), 15);
        this.escribeString(asiento.getLiberadoEn() != null ? asiento.getLiberadoEn() : "", 20);
    }

    public Asiento obten(Asiento asiento) throws PersistenciaException {
        Asiento var3;
        try {
            this.archivo = new RandomAccessFile(this.nomArchivo, "r");

            Asiento asientoLeido;
            do {
                asientoLeido = this.leeAsiento();
            } while(!asiento.equals(asientoLeido));

            var3 = asientoLeido;
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

    public void agrega(Asiento asiento) throws PersistenciaException {
        try {
            this.archivo = new RandomAccessFile(this.nomArchivo, "rw");
            this.archivo.seek(this.archivo.length());
            this.escribeAsiento(asiento);
        } catch (FileNotFoundException var10) {
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

    public void actualiza(Asiento asiento) throws PersistenciaException {
        try {
            this.archivo = new RandomAccessFile(this.nomArchivo, "rw");

            Asiento asientoLeido;
            do {
                asientoLeido = this.leeAsiento();
            } while(!asiento.equals(asientoLeido));

            this.archivo.seek(this.archivo.getFilePointer() - (long)this.tamRegistro);
            this.escribeAsiento(asiento);
        } catch (FileNotFoundException var12) {
            throw new PersistenciaException("Archivo inexistente");
        } catch (EOFException var13) {
            throw new PersistenciaException("El asiento no existe");
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

    public void elimina(Asiento asiento) throws PersistenciaException {
        try {
            this.archivo = new RandomAccessFile(this.nomArchivo, "rw");

            Asiento asientoLeido;
            do {
                asientoLeido = this.leeAsiento();
            } while(!asiento.equals(asientoLeido));

            this.archivo.seek(this.archivo.getFilePointer() - (long)this.tamRegistro);
            this.borraRegistro();
            this.empaca();
        } catch (FileNotFoundException var12) {
            throw new PersistenciaException("Archivo inexistente");
        } catch (EOFException var13) {
            throw new PersistenciaException("El asiento no existe");
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

    public List<Asiento> lista() throws PersistenciaException {
        List<Asiento> lista = new ArrayList();

        Object var3;
        try {
            this.archivo = new RandomAccessFile(this.nomArchivo, "r");

            while(true) {
                lista.add(this.leeAsiento());
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

        return (List<Asiento>)var3;
    }

    private static boolean esAsientoDeViaje(Asiento asiento, String idViaje) {
        return idViaje.equals(asiento.getIdViaje());
    }

    private static boolean esAsientoConEstado(Asiento asiento, String estado) {
        return estado.equals(asiento.getEstado());
    }

    private static boolean esAsientoDisponibleEnViaje(Asiento asiento, String idViaje) {
        return idViaje.equals(asiento.getIdViaje()) && "Disponible".equals(asiento.getEstado());
    }

    public List<Asiento> listaPorViaje(String idViaje) throws PersistenciaException {
        return (List)this.lista().stream().filter((asiento) -> esAsientoDeViaje(asiento, idViaje)).collect(Collectors.toList());
    }

    public List<Asiento> listaPorEstado(String estado) throws PersistenciaException {
        return (List)this.lista().stream().filter((asiento) -> esAsientoConEstado(asiento, estado)).collect(Collectors.toList());
    }

    public List<Asiento> listaDisponiblesPorViaje(String idViaje) throws PersistenciaException {
        return (List)this.lista().stream().filter((asiento) -> esAsientoDisponibleEnViaje(asiento, idViaje)).collect(Collectors.toList());
    }

    public Asiento obtenPorViajeYNumero(String idViaje, int numeroAsiento) throws PersistenciaException {
        Asiento var4;
        try {
            this.archivo = new RandomAccessFile(this.nomArchivo, "r");

            Asiento asiento;
            do {
                asiento = this.leeAsiento();
            } while(!idViaje.equals(asiento.getIdViaje()) || numeroAsiento != asiento.getNumeroAsiento());

            var4 = asiento;
            return var4;
        } catch (FileNotFoundException var16) {
            throw new PersistenciaException("Archivo inexistente");
        } catch (EOFException var17) {
            var4 = null;
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

        return var4;
    }
}
