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
import objetosNegocio.Conductor;
import objetosNegocio.Empresa;
import objetosNegocio.Ruta;
import objetosNegocio.Viaje;

public class Viajes extends AccesoAleatorio {
    public Viajes(String nomArchivo) {
        super(nomArchivo, 290);
    }

    private Viaje leeViaje() throws IOException {
        Viaje viaje = new Viaje();
        viaje.setIdViaje(this.leeString(10));
        viaje.setBus(new Bus(this.leeString(10)));
        viaje.setRuta(new Ruta(this.leeString(10)));
        viaje.setFechaSalida(this.leeFecha());
        viaje.setFechaLlegada(this.leeFecha());
        viaje.setAsientosDisponibles(this.archivo.readInt());
        viaje.setEstado(this.leeString(20));
        viaje.setEmpresa(new Empresa(this.leeString(10)));
        viaje.setHoraSalida(this.leeString(5));
        viaje.setHoraLlegada(this.leeString(5));
        viaje.setPrecio(this.archivo.readDouble());
        viaje.setConductor1(new Conductor(this.leeString(10)));
        viaje.setConductor2(new Conductor(this.leeString(10)));
        return viaje;
    }

    private void escribeViaje(Viaje viaje) throws IOException {
        this.escribeString(viaje.getIdViaje(), 10);
        String idBus = viaje.getBus() != null ? viaje.getBus().getIdBus() : "";
        String idRuta = viaje.getRuta() != null ? viaje.getRuta().getIdRuta() : "";
        this.escribeString(idBus, 10);
        this.escribeString(idRuta, 10);
        this.escribeFecha(viaje.getFechaSalida());
        this.escribeFecha(viaje.getFechaLlegada());
        this.archivo.writeInt(viaje.getAsientosDisponibles());
        this.escribeString(viaje.getEstado(), 20);
        String idEmpresa = viaje.getEmpresa() != null ? viaje.getEmpresa().getIdEmpresa() : "";
        this.escribeString(idEmpresa, 10);
        this.escribeString(viaje.getHoraSalida() != null ? viaje.getHoraSalida() : "", 5);
        this.escribeString(viaje.getHoraLlegada() != null ? viaje.getHoraLlegada() : "", 5);
        this.archivo.writeDouble(viaje.getPrecio());
        this.escribeString(viaje.getConductor1() != null ? viaje.getConductor1().getIdConductor() : "", 10);
        this.escribeString(viaje.getConductor2() != null ? viaje.getConductor2().getIdConductor() : "", 10);
    }

    private boolean esViajeValido(Viaje viaje) {
        return viaje != null && viaje.getIdViaje() != null && !viaje.getIdViaje().trim().isEmpty();
    }

    private String generarSiguienteCodigoViaje() throws PersistenciaException {
        try {
            this.archivo = new RandomAccessFile(this.nomArchivo, "r");
            int maxNumero = 0;

            try {
                while(true) {
                    Viaje viaje = this.leeViaje();
                    if (this.esViajeValido(viaje)) {
                        String codigo = viaje.getIdViaje();
                        if (codigo.startsWith("VIA") && codigo.length() > 3) {
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
                return String.format("VIA%03d", siguienteNumero);
            }
        } catch (FileNotFoundException var7) {
            return "VIA001";
        } catch (IOException e) {
            throw new PersistenciaException("Error al generar código de viaje", e);
        }
    }

    private void validarViaje(Viaje viaje) throws PersistenciaException {
        if (!Validador.validarNumeroPositivo(viaje.getAsientosDisponibles())) {
            throw new PersistenciaException(Validador.getMensajeErrorNumeroPositivo("Los asientos disponibles"));
        } else if (!Validador.validarNoVacio(viaje.getEstado())) {
            throw new PersistenciaException("El estado no puede estar vacío");
        } else if (viaje.getBus() != null && Validador.validarNoVacio(viaje.getBus().getIdBus())) {
            if (viaje.getRuta() != null && Validador.validarNoVacio(viaje.getRuta().getIdRuta())) {
                if (viaje.getEmpresa() == null || !Validador.validarNoVacio(viaje.getEmpresa().getIdEmpresa())) {
                    throw new PersistenciaException("Debe asignar una empresa al viaje");
                }
            } else {
                throw new PersistenciaException("Debe asignar una ruta al viaje");
            }
        } else {
            throw new PersistenciaException("Debe asignar un bus al viaje");
        }
    }

    public Viaje obten(Viaje viaje) throws PersistenciaException {
        Viaje var3;
        try {
            this.archivo = new RandomAccessFile(this.nomArchivo, "r");

            Viaje viajeLeido;
            do {
                viajeLeido = this.leeViaje();
            } while(!this.esViajeValido(viajeLeido) || !viajeLeido.getIdViaje().equals(viaje.getIdViaje()));

            var3 = viajeLeido;
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

    public void agrega(Viaje viaje) throws PersistenciaException {
        String codigoGenerado = this.generarSiguienteCodigoViaje();
        viaje.setIdViaje(codigoGenerado);
        this.validarViaje(viaje);

        try {
            this.archivo = new RandomAccessFile(this.nomArchivo, "rw");
            this.archivo.seek(this.archivo.length());
            this.escribeViaje(viaje);
            System.out.println("Viaje agregado: " + viaje.getIdViaje());
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

    public void actualiza(Viaje viaje) throws PersistenciaException {
        this.validarViaje(viaje);
        boolean encontrado = false;

        try {
            this.archivo = new RandomAccessFile(this.nomArchivo, "rw");

            long posicionAntes;
            Viaje viajeLeido;
            do {
                posicionAntes = this.archivo.getFilePointer();
                viajeLeido = this.leeViaje();
            } while(!this.esViajeValido(viajeLeido) || !viajeLeido.getIdViaje().equals(viaje.getIdViaje()));

            this.archivo.seek(posicionAntes);
            this.escribeViaje(viaje);
            System.out.println("✓ Viaje actualizado: " + viaje.getIdViaje());
            encontrado = true;
        } catch (FileNotFoundException var16) {
            throw new PersistenciaException("Archivo inexistente");
        } catch (EOFException var17) {
            if (!encontrado) {
                throw new PersistenciaException("El viaje no existe");
            }
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

        if (!encontrado) {
            throw new PersistenciaException("El viaje no existe");
        }
    }

    public void elimina(Viaje viaje) throws PersistenciaException {
        File archivoOriginal = new File(this.nomArchivo);
        File archivoTemporal = new File(this.nomArchivo + ".tmp");

        try {
            System.out.println("\n=== ELIMINANDO VIAJE: " + viaje.getIdViaje() + " ===");
            this.archivo = new RandomAccessFile(archivoOriginal, "r");
            List<Viaje> todosLosViajes = new ArrayList();
            boolean encontrado = false;

            try {
                while(true) {
                    Viaje v = this.leeViaje();
                    if (this.esViajeValido(v)) {
                        if (!v.getIdViaje().equals(viaje.getIdViaje())) {
                            todosLosViajes.add(v);
                            System.out.println("  → Mantener: " + v.getIdViaje());
                        } else {
                            encontrado = true;
                            System.out.println("  ✗ Eliminar: " + v.getIdViaje());
                        }
                    }
                }
            } catch (EOFException var17) {
                this.archivo.close();
                this.archivo = null;
                if (!encontrado) {
                    throw new PersistenciaException("El viaje no existe");
                }
            }

            System.out.println("Total a mantener: " + todosLosViajes.size());
            this.archivo = new RandomAccessFile(archivoTemporal, "rw");

            for(Viaje v : todosLosViajes) {
                this.escribeViaje(v);
            }

            this.archivo.close();
            this.archivo = null;
            if (!archivoOriginal.delete()) {
                throw new PersistenciaException("No se pudo eliminar el archivo original");
            }

            if (!archivoTemporal.renameTo(archivoOriginal)) {
                throw new PersistenciaException("No se pudo renombrar el archivo temporal");
            }

            System.out.println("✓ Viaje eliminado exitosamente");
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

    public List<Viaje> lista() throws PersistenciaException {
        List<Viaje> lista = new ArrayList();

        Object var3;
        try {
            this.archivo = new RandomAccessFile(this.nomArchivo, "r");

            while(true) {
                Viaje viajeLeido = this.leeViaje();
                if (this.esViajeValido(viajeLeido)) {
                    lista.add(viajeLeido);
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

        return (List<Viaje>)var3;
    }

    private static boolean esViajeEnRuta(Viaje viaje, String idRuta) {
        return viaje.getRuta() != null && idRuta.equals(viaje.getRuta().getIdRuta());
    }

    private static boolean esViajeConEstado(Viaje viaje, String estado) {
        return estado.equals(viaje.getEstado());
    }

    public List<Viaje> listaPorRuta(String idRuta) throws PersistenciaException {
        return (List)this.lista().stream().filter((viaje) -> esViajeEnRuta(viaje, idRuta)).collect(Collectors.toList());
    }

    public List<Viaje> listaPorEstado(String estado) throws PersistenciaException {
        return (List)this.lista().stream().filter((viaje) -> esViajeConEstado(viaje, estado)).collect(Collectors.toList());
    }
}
