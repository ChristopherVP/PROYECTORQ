//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package interfaces;

import excepciones.FachadaException;
import java.util.List;
import objetosNegocio.Asiento;
import objetosNegocio.Bus;
import objetosNegocio.Empresa;
import objetosNegocio.Factura;
import objetosNegocio.Pago;
import objetosNegocio.Pasajero;
import objetosNegocio.Reserva;
import objetosNegocio.Ruta;
import objetosNegocio.Usuario;
import objetosNegocio.Viaje;
import objetosServicio.Periodo;

public interface IFachada {
    Bus obten(Bus var1) throws FachadaException;

    void agrega(Bus var1) throws FachadaException;

    void actualiza(Bus var1) throws FachadaException;

    void elimina(Bus var1) throws FachadaException;

    Ruta obten(Ruta var1) throws FachadaException;

    void agrega(Ruta var1) throws FachadaException;

    void actualiza(Ruta var1) throws FachadaException;

    void elimina(Ruta var1) throws FachadaException;

    Factura obten(Factura var1) throws FachadaException;

    void agrega(Factura var1) throws FachadaException;

    void actualiza(Factura var1) throws FachadaException;

    void elimina(Factura var1) throws FachadaException;

    Usuario obten(Usuario var1) throws FachadaException;

    void agrega(Usuario var1) throws FachadaException;

    void actualiza(Usuario var1) throws FachadaException;

    void elimina(Usuario var1) throws FachadaException;

    Empresa obten(Empresa var1) throws FachadaException;

    void agrega(Empresa var1) throws FachadaException;

    void actualiza(Empresa var1) throws FachadaException;

    void elimina(Empresa var1) throws FachadaException;

    Viaje obten(Viaje var1) throws FachadaException;

    void agrega(Viaje var1) throws FachadaException;

    void actualiza(Viaje var1) throws FachadaException;

    void elimina(Viaje var1) throws FachadaException;

    Reserva obten(Reserva var1) throws FachadaException;

    void agrega(Reserva var1) throws FachadaException;

    void actualiza(Reserva var1) throws FachadaException;

    void elimina(Reserva var1) throws FachadaException;

    Pago obten(Pago var1) throws FachadaException;

    void agrega(Pago var1) throws FachadaException;

    void actualiza(Pago var1) throws FachadaException;

    void elimina(Pago var1) throws FachadaException;

    Asiento obten(Asiento var1) throws FachadaException;

    void agrega(Asiento var1) throws FachadaException;

    void actualiza(Asiento var1) throws FachadaException;

    void elimina(Asiento var1) throws FachadaException;

    Pasajero obten(Pasajero var1) throws FachadaException;

    void agrega(Pasajero var1) throws FachadaException;

    void actualiza(Pasajero var1) throws FachadaException;

    void elimina(Pasajero var1) throws FachadaException;

    List<Asiento> consultaAsientos() throws FachadaException;

    List<Asiento> consultaAsientosPorViaje(String var1) throws FachadaException;

    List<Asiento> consultaAsientosPorEstado(String var1) throws FachadaException;

    List<Asiento> consultaAsientosDisponiblesPorViaje(String var1) throws FachadaException;

    List<Bus> consultaBuses() throws FachadaException;

    List<Bus> consultaBusesPorEstado(String var1) throws FachadaException;

    List<Bus> consultaBusesPorEmpresa(String var1) throws FachadaException;

    List<Ruta> consultaRutas() throws FachadaException;

    List<Ruta> consultaRutasPorOrigen(String var1) throws FachadaException;

    List<Ruta> consultaRutasPorDestino(String var1) throws FachadaException;

    List<Factura> consultaFacturas() throws FachadaException;

    List<Factura> consultaFacturasPorPasajero(String var1) throws FachadaException;

    List<Factura> consultaFacturasPorPeriodo(Periodo var1) throws FachadaException;

    List<Usuario> consultaUsuarios() throws FachadaException;

    List<Usuario> consultaUsuariosPorTipo(String var1) throws FachadaException;

    List<Empresa> consultaEmpresas() throws FachadaException;

    List<Viaje> consultaViajes() throws FachadaException;

    List<Viaje> consultaViajesPorRuta(String var1) throws FachadaException;

    List<Viaje> consultaViajesPorPeriodo(Periodo var1) throws FachadaException;

    List<Reserva> consultaReservas() throws FachadaException;

    List<Reserva> consultaReservasPorPasajero(String var1) throws FachadaException;

    List<Reserva> consultaReservasPorViaje(String var1) throws FachadaException;

    List<Pago> consultaPagos() throws FachadaException;

    List<Pago> consultaPagosPorUsuario(String var1) throws FachadaException;

    List<Pago> consultaPagosPorPeriodo(Periodo var1) throws FachadaException;

    List<Viaje> consultaViajesPorOrigen(String var1) throws FachadaException;

    List<Viaje> consultaViajesPorDestino(String var1) throws FachadaException;

    List<Viaje> consultaViajesPorEmpresa(Empresa var1) throws FachadaException;

    List<Reserva> consultaReservasPorPasajero(Pasajero var1) throws FachadaException;

    List<Reserva> consultaReservasPorViaje(Viaje var1) throws FachadaException;

    List<Factura> consultaFacturasPorPasajero(Pasajero var1) throws FachadaException;

    List<Factura> consultaFacturasPorEstado(String var1) throws FachadaException;

    List<Bus> consultaBusesPorEmpresa(Empresa var1) throws FachadaException;
}
