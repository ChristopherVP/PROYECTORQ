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
import objetosNegocio.Empresa;
import objetosNegocio.Pasajero;
import objetosNegocio.Reserva;
import objetosNegocio.Viaje;

public class Reservas extends AccesoAleatorio {
    public Reservas(String nomArchivo) {
        super(nomArchivo, 260);
    }

    private Reserva leeReserva() throws IOException {
        Reserva reserva = new Reserva();
        reserva.setIdReserva(this.leeString(10));
        reserva.setPasajero(new Pasajero(this.leeString(10)));
        reserva.setViaje(new Viaje(this.leeString(10)));
        reserva.setCantidadAsientos(this.archivo.readInt());
        reserva.setFechaReserva(this.leeFecha());
        reserva.setEstado(this.leeString(20));
        reserva.setEmpresa(new Empresa(this.leeString(10)));
        reserva.setPrecioTotal(this.archivo.readDouble());
        reserva.setMetodoPago(this.leeString(20));
        reserva.setNinosBrazos(this.archivo.readInt());
        return reserva;
    }

    private void escribeReserva(Reserva reserva) throws IOException {
        this.escribeString(reserva.getIdReserva(), 10);
        String idPasajero = reserva.getPasajero() != null ? reserva.getPasajero().getIdPasajero() : "";
        String idViaje = reserva.getViaje() != null ? reserva.getViaje().getIdViaje() : "";
        this.escribeString(idPasajero, 10);
        this.escribeString(idViaje, 10);
        this.archivo.writeInt(reserva.getCantidadAsientos());
        this.escribeFecha(reserva.getFechaReserva());
        this.escribeString(reserva.getEstado(), 20);
        String idEmpresa = reserva.getEmpresa() != null ? reserva.getEmpresa().getIdEmpresa() : "";
        this.escribeString(idEmpresa, 10);
        this.archivo.writeDouble(reserva.getPrecioTotal());
        this.escribeString(reserva.getMetodoPago() != null ? reserva.getMetodoPago() : "EFECTIVO", 20);
        this.archivo.writeInt(reserva.getNinosBrazos());
    }

    private boolean esReservaValida(Reserva reserva) {
        return reserva != null && reserva.getIdReserva() != null && !reserva.getIdReserva().trim().isEmpty();
    }

    private String generarSiguienteCodigoReserva() throws PersistenciaException {
        try {
            this.archivo = new RandomAccessFile(this.nomArchivo, "r");
            int maxNumero = 0;

            try {
                while(true) {
                    Reserva reserva = this.leeReserva();
                    if (this.esReservaValida(reserva)) {
                        String codigo = reserva.getIdReserva();
                        if (codigo.startsWith("RES") && codigo.length() > 3) {
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
                return String.format("RES%03d", siguienteNumero);
            }
        } catch (FileNotFoundException var7) {
            return "RES001";
        } catch (IOException e) {
            throw new PersistenciaException("Error al generar código de reserva", e);
        }
    }

    private void validarReserva(Reserva reserva) throws PersistenciaException {
        if (!Validador.validarNumeroPositivo(reserva.getCantidadAsientos())) {
            throw new PersistenciaException(Validador.getMensajeErrorNumeroPositivo("La cantidad de asientos"));
        } else if (!Validador.validarNoVacio(reserva.getEstado())) {
            throw new PersistenciaException("El estado no puede estar vacío");
        } else if (reserva.getPasajero() != null && Validador.validarNoVacio(reserva.getPasajero().getIdPasajero())) {
            if (reserva.getViaje() != null && Validador.validarNoVacio(reserva.getViaje().getIdViaje())) {
                if (reserva.getEmpresa() == null || !Validador.validarNoVacio(reserva.getEmpresa().getIdEmpresa())) {
                    throw new PersistenciaException("Debe asignar una empresa a la reserva");
                }
            } else {
                throw new PersistenciaException("Debe asignar un viaje a la reserva");
            }
        } else {
            throw new PersistenciaException("Debe asignar un pasajero a la reserva");
        }
    }

    public Reserva obten(Reserva reserva) throws PersistenciaException {
        Reserva var3;
        try {
            this.archivo = new RandomAccessFile(this.nomArchivo, "r");

            Reserva reservaLeida;
            do {
                reservaLeida = this.leeReserva();
            } while(!this.esReservaValida(reservaLeida) || !reservaLeida.getIdReserva().equals(reserva.getIdReserva()));

            var3 = reservaLeida;
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

    public void agrega(Reserva reserva) throws PersistenciaException {
        String codigoGenerado = this.generarSiguienteCodigoReserva();
        reserva.setIdReserva(codigoGenerado);
        this.validarReserva(reserva);

        try {
            this.archivo = new RandomAccessFile(this.nomArchivo, "rw");
            this.archivo.seek(this.archivo.length());
            this.escribeReserva(reserva);
            System.out.println("Reserva agregada: " + reserva.getIdReserva());
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

    public void actualiza(Reserva reserva) throws PersistenciaException {
        this.validarReserva(reserva);
        boolean encontrada = false;

        try {
            this.archivo = new RandomAccessFile(this.nomArchivo, "rw");

            long posicionAntes;
            Reserva reservaLeida;
            do {
                posicionAntes = this.archivo.getFilePointer();
                reservaLeida = this.leeReserva();
            } while(!this.esReservaValida(reservaLeida) || !reservaLeida.getIdReserva().equals(reserva.getIdReserva()));

            this.archivo.seek(posicionAntes);
            this.escribeReserva(reserva);
            System.out.println("✓ Reserva actualizada: " + reserva.getIdReserva());
            encontrada = true;
        } catch (FileNotFoundException var16) {
            throw new PersistenciaException("Archivo inexistente");
        } catch (EOFException var17) {
            if (!encontrada) {
                throw new PersistenciaException("La reserva no existe");
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

        if (!encontrada) {
            throw new PersistenciaException("La reserva no existe");
        }
    }

    public void elimina(Reserva reserva) throws PersistenciaException {
        File archivoOriginal = new File(this.nomArchivo);
        File archivoTemporal = new File(this.nomArchivo + ".tmp");

        try {
            System.out.println("\n=== ELIMINANDO RESERVA: " + reserva.getIdReserva() + " ===");
            this.archivo = new RandomAccessFile(archivoOriginal, "r");
            List<Reserva> todasLasReservas = new ArrayList();
            boolean encontrada = false;

            try {
                while(true) {
                    Reserva r = this.leeReserva();
                    if (this.esReservaValida(r)) {
                        if (!r.getIdReserva().equals(reserva.getIdReserva())) {
                            todasLasReservas.add(r);
                            System.out.println("  → Mantener: " + r.getIdReserva());
                        } else {
                            encontrada = true;
                            System.out.println("  ✗ Eliminar: " + r.getIdReserva());
                        }
                    }
                }
            } catch (EOFException var17) {
                this.archivo.close();
                this.archivo = null;
                if (!encontrada) {
                    throw new PersistenciaException("La reserva no existe");
                }
            }

            System.out.println("Total a mantener: " + todasLasReservas.size());
            this.archivo = new RandomAccessFile(archivoTemporal, "rw");

            for(Reserva r : todasLasReservas) {
                this.escribeReserva(r);
            }

            this.archivo.close();
            this.archivo = null;
            if (!archivoOriginal.delete()) {
                throw new PersistenciaException("No se pudo eliminar el archivo original");
            }

            if (!archivoTemporal.renameTo(archivoOriginal)) {
                throw new PersistenciaException("No se pudo renombrar el archivo temporal");
            }

            System.out.println("✓ Reserva eliminada exitosamente");
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

    public List<Reserva> lista() throws PersistenciaException {
        List<Reserva> lista = new ArrayList();

        Object var3;
        try {
            this.archivo = new RandomAccessFile(this.nomArchivo, "r");

            while(true) {
                Reserva reservaLeida = this.leeReserva();
                if (this.esReservaValida(reservaLeida)) {
                    lista.add(reservaLeida);
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

        return (List<Reserva>)var3;
    }

    private static boolean esReservaDePasajero(Reserva reserva, String idPasajero) {
        return reserva.getPasajero() != null && idPasajero.equals(reserva.getPasajero().getIdPasajero());
    }

    private static boolean esReservaDeViaje(Reserva reserva, String idViaje) {
        return reserva.getViaje() != null && idViaje.equals(reserva.getViaje().getIdViaje());
    }

    public List<Reserva> listaPorPasajero(String idPasajero) throws PersistenciaException {
        return (List)this.lista().stream().filter((reserva) -> esReservaDePasajero(reserva, idPasajero)).collect(Collectors.toList());
    }

    public List<Reserva> listaPorViaje(String idViaje) throws PersistenciaException {
        return (List)this.lista().stream().filter((reserva) -> esReservaDeViaje(reserva, idViaje)).collect(Collectors.toList());
    }
}
