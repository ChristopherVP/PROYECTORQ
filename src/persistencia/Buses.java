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
import java.util.stream.Collectors;
import objetosNegocio.Bus;
import objetosNegocio.Empresa;

public class Buses extends AccesoAleatorio {
    public Buses(String nomArchivo) {
        super(nomArchivo, 230);
    }

    private Bus leeBus() throws IOException {
        Bus bus = new Bus();
        bus.setIdBus(this.leeString(10));
        bus.setPlaca(this.leeString(10));
        bus.setNumeroChasis(this.leeString(20));
        bus.setNumeroMotor(this.leeString(20));
        bus.setMarca(this.leeString(20));
        bus.setAnioFabricacion(this.archivo.readInt());
        bus.setCapacidad(this.archivo.readInt());
        bus.setEstado(this.leeString(20));
        bus.setEmpresa(new Empresa(this.leeString(10)));
        return bus;
    }

    private void escribeBus(Bus bus) throws IOException {
        this.escribeString(bus.getIdBus(), 10);
        this.escribeString(bus.getPlaca(), 10);
        this.escribeString(bus.getNumeroChasis(), 20);
        this.escribeString(bus.getNumeroMotor(), 20);
        this.escribeString(bus.getMarca(), 20);
        this.archivo.writeInt(bus.getAnioFabricacion());
        this.archivo.writeInt(bus.getCapacidad());
        this.escribeString(bus.getEstado(), 20);
        String idEmpresa = bus.getEmpresa() != null ? bus.getEmpresa().getIdEmpresa() : "";
        this.escribeString(idEmpresa, 10);
    }

    private boolean esBusValido(Bus bus) {
        return bus != null && bus.getIdBus() != null && !bus.getIdBus().trim().isEmpty();
    }

    private String generarSiguienteCodigoBus() throws PersistenciaException {
        try {
            this.archivo = new RandomAccessFile(this.nomArchivo, "r");
            int maxNumero = 0;

            try {
                while(true) {
                    Bus bus = this.leeBus();
                    if (this.esBusValido(bus)) {
                        String codigo = bus.getIdBus();
                        if (codigo.startsWith("BUS") && codigo.length() > 3) {
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
                return String.format("BUS%03d", siguienteNumero);
            }
        } catch (FileNotFoundException var7) {
            return "BUS001";
        } catch (IOException e) {
            throw new PersistenciaException("Error al generar código de bus", e);
        }
    }

    private void validarBus(Bus bus) throws PersistenciaException {
        if (!Validador.validarNumeroPositivo(bus.getCapacidad())) {
            throw new PersistenciaException(Validador.getMensajeErrorNumeroPositivo("La capacidad"));
        } else if (!Validador.validarNoVacio(bus.getPlaca())) {
            throw new PersistenciaException("La placa no puede estar vacía");
        } else if (!Validador.validarNoVacio(bus.getMarca())) {
            throw new PersistenciaException("La marca no puede estar vacía");
        } else if (!Validador.validarNoVacio(bus.getEstado())) {
            throw new PersistenciaException("El estado no puede estar vacío");
        }
    }

    public Bus obten(Bus bus) throws PersistenciaException {
        Bus var3;
        try {
            this.archivo = new RandomAccessFile(this.nomArchivo, "r");

            Bus busLeido;
            do {
                busLeido = this.leeBus();
            } while(!this.esBusValido(busLeido) || !busLeido.getIdBus().equals(bus.getIdBus()));

            var3 = busLeido;
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

    public void agrega(Bus bus) throws PersistenciaException {
        String codigoGenerado = this.generarSiguienteCodigoBus();
        bus.setIdBus(codigoGenerado);
        this.validarBus(bus);

        try {
            this.archivo = new RandomAccessFile(this.nomArchivo, "rw");
            this.archivo.seek(this.archivo.length());
            this.escribeBus(bus);
            System.out.println("Bus agregado: " + bus.getIdBus());
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

    public void actualiza(Bus bus) throws PersistenciaException {
        this.validarBus(bus);
        boolean encontrado = false;

        try {
            this.archivo = new RandomAccessFile(this.nomArchivo, "rw");

            long posicionAntes;
            Bus busLeido;
            do {
                posicionAntes = this.archivo.getFilePointer();
                busLeido = this.leeBus();
            } while(!this.esBusValido(busLeido) || !busLeido.getIdBus().equals(bus.getIdBus()));

            this.archivo.seek(posicionAntes);
            this.escribeBus(bus);
            System.out.println("✓ Bus actualizado: " + bus.getIdBus());
            encontrado = true;
        } catch (FileNotFoundException var16) {
            throw new PersistenciaException("Archivo inexistente");
        } catch (EOFException var17) {
            if (!encontrado) {
                throw new PersistenciaException("El bus no existe");
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
            throw new PersistenciaException("El bus no existe");
        }
    }

    public void elimina(Bus bus) throws PersistenciaException {
        File archivoOriginal = new File(this.nomArchivo);
        File archivoTemporal = new File(this.nomArchivo + ".tmp");

        try {
            System.out.println("\n=== ELIMINANDO BUS: " + bus.getIdBus() + " ===");
            this.archivo = new RandomAccessFile(archivoOriginal, "r");
            List<Bus> todosLosBuses = new ArrayList();
            boolean encontrado = false;

            try {
                while(true) {
                    Bus b = this.leeBus();
                    if (this.esBusValido(b)) {
                        if (!b.getIdBus().equals(bus.getIdBus())) {
                            todosLosBuses.add(b);
                            System.out.println("  → Mantener: " + b.getIdBus());
                        } else {
                            encontrado = true;
                            System.out.println("  ✗ Eliminar: " + b.getIdBus());
                        }
                    }
                }
            } catch (EOFException var17) {
                this.archivo.close();
                this.archivo = null;
                if (!encontrado) {
                    throw new PersistenciaException("El bus no existe");
                }
            }

            System.out.println("Total a mantener: " + todosLosBuses.size());
            this.archivo = new RandomAccessFile(archivoTemporal, "rw");

            for(Bus b : todosLosBuses) {
                this.escribeBus(b);
            }

            this.archivo.close();
            this.archivo = null;
            if (!archivoOriginal.delete()) {
                throw new PersistenciaException("No se pudo eliminar el archivo original");
            }

            if (!archivoTemporal.renameTo(archivoOriginal)) {
                throw new PersistenciaException("No se pudo renombrar el archivo temporal");
            }

            System.out.println("✓ Bus eliminado exitosamente");
            System.out.println("===================================\n");
        } catch (FileNotFoundException var18) {
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

            if (archivoTemporal.exists()) {
                archivoTemporal.delete();
            }

        }

    }

    public List<Bus> lista() throws PersistenciaException {
        List<Bus> lista = new ArrayList();

        Object var3;
        try {
            this.archivo = new RandomAccessFile(this.nomArchivo, "r");

            while(true) {
                Bus busLeido = this.leeBus();
                if (this.esBusValido(busLeido)) {
                    lista.add(busLeido);
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

        return (List<Bus>)var3;
    }

    private static boolean esBusConEstado(Bus bus, String estado) {
        return bus.getEstado() != null && bus.getEstado().equals(estado);
    }

    private static boolean esBusDeEmpresa(Bus bus, String idEmpresa) {
        return bus.getEmpresa() != null && bus.getEmpresa().getIdEmpresa() != null && bus.getEmpresa().getIdEmpresa().equals(idEmpresa);
    }

    public List<Bus> listaPorEstado(String estado) throws PersistenciaException {
        return (List)this.lista().stream().filter((bus) -> esBusConEstado(bus, estado)).collect(Collectors.toList());
    }

    public List<Bus> listaPorEmpresa(String idEmpresa) throws PersistenciaException {
        return (List)this.lista().stream().filter((bus) -> esBusDeEmpresa(bus, idEmpresa)).collect(Collectors.toList());
    }
}
