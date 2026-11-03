//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package fachadas;

import excepciones.FachadaException;
import interfaces.IFachada;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
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
import persistencia.Asientos;
import persistencia.Buses;
import persistencia.Empresas;
import persistencia.ExportadorTXT;
import persistencia.Facturas;
import persistencia.Pagos;
import persistencia.Pasajeros;
import persistencia.Reservas;
import persistencia.Rutas;
import persistencia.Usuarios;
import persistencia.Viajes;

public class FachadaArchivos implements IFachada {
    private static FachadaArchivos instancia;
    private Buses catalogoBuses = new Buses("buses.dat");
    private Rutas catalogoRutas = new Rutas("rutas.dat");
    private Facturas catalogoFacturas = new Facturas("facturas.dat");
    private Usuarios catalogoUsuarios = new Usuarios("usuarios.dat");
    private Empresas catalogoEmpresas = new Empresas("empresas.dat");
    private Viajes catalogoViajes = new Viajes("viajes.dat");
    private Reservas catalogoReservas = new Reservas("reservas.dat");
    private Pagos catalogoPagos = new Pagos("pagos.dat");
    private Asientos catalogoAsientos = new Asientos("asientos.dat");
    private Pasajeros catalogoPasajeros = new Pasajeros("pasajeros.dat");
    private ExportadorTXT exportador = new ExportadorTXT();

    public static synchronized FachadaArchivos getInstance() {
        if (instancia == null) {
            instancia = new FachadaArchivos();
        }

        return instancia;
    }

    public Bus obten(Bus bus) throws FachadaException {
        try {
            Bus busBuscado = this.catalogoBuses.obten(bus);
            if (busBuscado != null) {
                Empresa empresa = this.catalogoEmpresas.obten(busBuscado.getEmpresa());
                busBuscado.setEmpresa(empresa);
            }

            return busBuscado;
        } catch (Exception pe) {
            throw new FachadaException("No se puede obtener el bus", pe);
        }
    }

    public void agrega(Bus bus) throws FachadaException {
        try {
            if (this.catalogoBuses.obten(bus) != null) {
                throw new FachadaException("Bus repetido");
            }
        } catch (Exception var4) {
        }

        try {
            this.catalogoBuses.agrega(bus);
        } catch (Exception pe) {
            throw new FachadaException("No se puede agregar el bus", pe);
        }
    }

    public void actualiza(Bus bus) throws FachadaException {
        try {
            this.catalogoBuses.actualiza(bus);
        } catch (Exception pe) {
            throw new FachadaException("No se puede actualizar el bus", pe);
        }
    }

    public void elimina(Bus bus) throws FachadaException {
        try {
            this.catalogoBuses.elimina(bus);
        } catch (Exception pe) {
            throw new FachadaException("No se puede eliminar el bus", pe);
        }
    }

    public Ruta obten(Ruta ruta) throws FachadaException {
        try {
            Ruta rutaBuscada = this.catalogoRutas.obten(ruta);
            if (rutaBuscada != null) {
                Empresa empresa = this.catalogoEmpresas.obten(rutaBuscada.getEmpresa());
                rutaBuscada.setEmpresa(empresa);
            }

            return rutaBuscada;
        } catch (Exception pe) {
            throw new FachadaException("No se puede obtener la ruta", pe);
        }
    }

    public void agrega(Ruta ruta) throws FachadaException {
        try {
            if (this.catalogoRutas.obten(ruta) != null) {
                throw new FachadaException("Ruta repetida");
            }
        } catch (Exception var4) {
        }

        try {
            this.catalogoRutas.agrega(ruta);
        } catch (Exception pe) {
            throw new FachadaException("No se puede agregar la ruta", pe);
        }
    }

    public void actualiza(Ruta ruta) throws FachadaException {
        try {
            this.catalogoRutas.actualiza(ruta);
        } catch (Exception pe) {
            throw new FachadaException("No se puede actualizar la ruta", pe);
        }
    }

    public void elimina(Ruta ruta) throws FachadaException {
        try {
            this.catalogoRutas.elimina(ruta);
        } catch (Exception pe) {
            throw new FachadaException("No se puede eliminar la ruta", pe);
        }
    }

    public Factura obten(Factura factura) throws FachadaException {
        try {
            Factura facturaBuscada = this.catalogoFacturas.obten(factura);
            if (facturaBuscada != null) {
                Pasajero pasajero = this.catalogoPasajeros.obten(facturaBuscada.getPasajero());
                Empresa empresa = this.catalogoEmpresas.obten(facturaBuscada.getEmpresa());
                facturaBuscada.setPasajero(pasajero);
                facturaBuscada.setEmpresa(empresa);
            }

            return facturaBuscada;
        } catch (Exception pe) {
            throw new FachadaException("No se puede obtener la factura", pe);
        }
    }

    public void agrega(Factura factura) throws FachadaException {
        try {
            if (this.catalogoFacturas.obten(factura) != null) {
                throw new FachadaException("Factura repetida");
            }
        } catch (Exception var4) {
        }

        try {
            this.catalogoFacturas.agrega(factura);
        } catch (Exception pe) {
            throw new FachadaException("No se puede agregar la factura", pe);
        }
    }

    public void actualiza(Factura factura) throws FachadaException {
        try {
            this.catalogoFacturas.actualiza(factura);
        } catch (Exception pe) {
            throw new FachadaException("No se puede actualizar la factura", pe);
        }
    }

    public void elimina(Factura factura) throws FachadaException {
        try {
            this.catalogoFacturas.elimina(factura);
        } catch (Exception pe) {
            throw new FachadaException("No se puede eliminar la factura", pe);
        }
    }

    public Usuario obten(Usuario usuario) throws FachadaException {
        try {
            Usuario usuarioBuscado = this.catalogoUsuarios.obten(usuario);
            if (usuarioBuscado != null && usuarioBuscado.getEmpresa() != null) {
                Empresa empresa = this.catalogoEmpresas.obten(usuarioBuscado.getEmpresa());
                usuarioBuscado.setEmpresa(empresa);
            }

            return usuarioBuscado;
        } catch (Exception pe) {
            throw new FachadaException("No se puede obtener el usuario", pe);
        }
    }

    public void agrega(Usuario usuario) throws FachadaException {
        try {
            if (this.catalogoUsuarios.obten(usuario) != null) {
                throw new FachadaException("Usuario repetido");
            }
        } catch (Exception var4) {
        }

        try {
            this.catalogoUsuarios.agrega(usuario);
        } catch (Exception pe) {
            throw new FachadaException("No se puede agregar el usuario", pe);
        }
    }

    public void actualiza(Usuario usuario) throws FachadaException {
        try {
            this.catalogoUsuarios.actualiza(usuario);
        } catch (Exception pe) {
            throw new FachadaException("No se puede actualizar el usuario", pe);
        }
    }

    public void elimina(Usuario usuario) throws FachadaException {
        try {
            this.catalogoUsuarios.elimina(usuario);
        } catch (Exception pe) {
            throw new FachadaException("No se puede eliminar el usuario", pe);
        }
    }

    public Empresa obten(Empresa empresa) throws FachadaException {
        try {
            return this.catalogoEmpresas.obten(empresa);
        } catch (Exception pe) {
            throw new FachadaException("No se puede obtener la empresa", pe);
        }
    }

    public void agrega(Empresa empresa) throws FachadaException {
        try {
            if (this.catalogoEmpresas.obten(empresa) != null) {
                throw new FachadaException("Empresa repetida");
            }
        } catch (Exception var4) {
        }

        try {
            this.catalogoEmpresas.agrega(empresa);
        } catch (Exception pe) {
            throw new FachadaException("No se puede agregar la empresa", pe);
        }
    }

    public void actualiza(Empresa empresa) throws FachadaException {
        try {
            this.catalogoEmpresas.actualiza(empresa);
        } catch (Exception pe) {
            throw new FachadaException("No se puede actualizar la empresa", pe);
        }
    }

    public void elimina(Empresa empresa) throws FachadaException {
        try {
            this.catalogoEmpresas.elimina(empresa);
        } catch (Exception pe) {
            throw new FachadaException("No se puede eliminar la empresa", pe);
        }
    }

    public Viaje obten(Viaje viaje) throws FachadaException {
        try {
            Viaje viajeBuscado = this.catalogoViajes.obten(viaje);
            if (viajeBuscado != null) {
                Bus bus = this.catalogoBuses.obten(viajeBuscado.getBus());
                Ruta ruta = this.catalogoRutas.obten(viajeBuscado.getRuta());
                Empresa empresa = this.catalogoEmpresas.obten(viajeBuscado.getEmpresa());
                viajeBuscado.setBus(bus);
                viajeBuscado.setRuta(ruta);
                viajeBuscado.setEmpresa(empresa);
            }

            return viajeBuscado;
        } catch (Exception pe) {
            throw new FachadaException("No se puede obtener el viaje", pe);
        }
    }

    public void agrega(Viaje viaje) throws FachadaException {
        try {
            if (this.catalogoViajes.obten(viaje) != null) {
                throw new FachadaException("Viaje repetido");
            }
        } catch (Exception var4) {
        }

        try {
            this.catalogoViajes.agrega(viaje);
        } catch (Exception pe) {
            throw new FachadaException("No se puede agregar el viaje", pe);
        }
    }

    public void actualiza(Viaje viaje) throws FachadaException {
        try {
            this.catalogoViajes.actualiza(viaje);
        } catch (Exception pe) {
            throw new FachadaException("No se puede actualizar el viaje", pe);
        }
    }

    public void elimina(Viaje viaje) throws FachadaException {
        try {
            this.catalogoViajes.elimina(viaje);
        } catch (Exception pe) {
            throw new FachadaException("No se puede eliminar el viaje", pe);
        }
    }

    public Reserva obten(Reserva reserva) throws FachadaException {
        try {
            Reserva reservaBuscada = this.catalogoReservas.obten(reserva);
            if (reservaBuscada != null) {
                Pasajero pasajero = this.catalogoPasajeros.obten(reservaBuscada.getPasajero());
                Viaje viaje = this.catalogoViajes.obten(reservaBuscada.getViaje());
                Empresa empresa = this.catalogoEmpresas.obten(reservaBuscada.getEmpresa());
                reservaBuscada.setPasajero(pasajero);
                reservaBuscada.setViaje(viaje);
                reservaBuscada.setEmpresa(empresa);
            }

            return reservaBuscada;
        } catch (Exception pe) {
            throw new FachadaException("No se puede obtener la reserva", pe);
        }
    }

    public void agrega(Reserva reserva) throws FachadaException {
        try {
            if (this.catalogoReservas.obten(reserva) != null) {
                throw new FachadaException("Reserva repetida");
            }
        } catch (Exception var4) {
        }

        try {
            this.catalogoReservas.agrega(reserva);
        } catch (Exception pe) {
            throw new FachadaException("No se puede agregar la reserva", pe);
        }
    }

    public void actualiza(Reserva reserva) throws FachadaException {
        try {
            this.catalogoReservas.actualiza(reserva);
        } catch (Exception pe) {
            throw new FachadaException("No se puede actualizar la reserva", pe);
        }
    }

    public void elimina(Reserva reserva) throws FachadaException {
        try {
            this.catalogoReservas.elimina(reserva);
        } catch (Exception pe) {
            throw new FachadaException("No se puede eliminar la reserva", pe);
        }
    }

    public Pago obten(Pago pago) throws FachadaException {
        try {
            Pago pagoBuscado = this.catalogoPagos.obten(pago);
            if (pagoBuscado != null) {
                Usuario usuario = this.catalogoUsuarios.obten(pagoBuscado.getUsuario());
                Factura factura = this.catalogoFacturas.obten(pagoBuscado.getFactura());
                pagoBuscado.setUsuario(usuario);
                pagoBuscado.setFactura(factura);
            }

            return pagoBuscado;
        } catch (Exception pe) {
            throw new FachadaException("No se puede obtener el pago", pe);
        }
    }

    public void agrega(Pago pago) throws FachadaException {
        try {
            if (this.catalogoPagos.obten(pago) != null) {
                throw new FachadaException("Pago repetido");
            }
        } catch (Exception var4) {
        }

        try {
            this.catalogoPagos.agrega(pago);
        } catch (Exception pe) {
            throw new FachadaException("No se puede agregar el pago", pe);
        }
    }

    public void actualiza(Pago pago) throws FachadaException {
        try {
            this.catalogoPagos.actualiza(pago);
        } catch (Exception pe) {
            throw new FachadaException("No se puede actualizar el pago", pe);
        }
    }

    public void elimina(Pago pago) throws FachadaException {
        try {
            this.catalogoPagos.elimina(pago);
        } catch (Exception pe) {
            throw new FachadaException("No se puede eliminar el pago", pe);
        }
    }

    public Asiento obten(Asiento asiento) throws FachadaException {
        try {
            return this.catalogoAsientos.obten(asiento);
        } catch (Exception pe) {
            throw new FachadaException("No se puede obtener el asiento", pe);
        }
    }

    public void agrega(Asiento asiento) throws FachadaException {
        try {
            if (this.catalogoAsientos.obten(asiento) != null) {
                throw new FachadaException("Asiento repetido");
            }
        } catch (Exception var4) {
        }

        try {
            this.catalogoAsientos.agrega(asiento);
        } catch (Exception pe) {
            throw new FachadaException("No se puede agregar el asiento", pe);
        }
    }

    public void actualiza(Asiento asiento) throws FachadaException {
        try {
            this.catalogoAsientos.actualiza(asiento);
        } catch (Exception pe) {
            throw new FachadaException("No se puede actualizar el asiento", pe);
        }
    }

    public void elimina(Asiento asiento) throws FachadaException {
        try {
            this.catalogoAsientos.elimina(asiento);
        } catch (Exception pe) {
            throw new FachadaException("No se puede eliminar el asiento", pe);
        }
    }

    public Pasajero obten(Pasajero pasajero) throws FachadaException {
        try {
            Pasajero pasajeroBuscado = this.catalogoPasajeros.obten(pasajero);
            if (pasajeroBuscado != null && pasajeroBuscado.getEmpresa() != null) {
                Empresa empresa = this.catalogoEmpresas.obten(pasajeroBuscado.getEmpresa());
                pasajeroBuscado.setEmpresa(empresa);
            }

            return pasajeroBuscado;
        } catch (Exception pe) {
            throw new FachadaException("No se puede obtener el pasajero", pe);
        }
    }

    public void agrega(Pasajero pasajero) throws FachadaException {
        try {
            if (this.catalogoPasajeros.obten(pasajero) != null) {
                throw new FachadaException("Pasajero repetido");
            }
        } catch (Exception var4) {
        }

        try {
            this.catalogoPasajeros.agrega(pasajero);
        } catch (Exception pe) {
            throw new FachadaException("No se puede agregar el pasajero", pe);
        }
    }

    public void actualiza(Pasajero pasajero) throws FachadaException {
        try {
            this.catalogoPasajeros.actualiza(pasajero);
        } catch (Exception pe) {
            throw new FachadaException("No se puede actualizar el pasajero", pe);
        }
    }

    public void elimina(Pasajero pasajero) throws FachadaException {
        try {
            this.catalogoPasajeros.elimina(pasajero);
        } catch (Exception pe) {
            throw new FachadaException("No se puede eliminar el pasajero", pe);
        }
    }

    public List<Pasajero> consultaPasajeros() throws FachadaException {
        try {
            return this.catalogoPasajeros.lista();
        } catch (Exception pe) {
            throw new FachadaException("No se puede obtener la lista de pasajeros", pe);
        }
    }

    private List<Bus> agregaRelacionesBuses(List<Bus> listaBuses) throws FachadaException {
        try {
            for(Bus bus : listaBuses) {
                Empresa empresa = this.catalogoEmpresas.obten(bus.getEmpresa());
                bus.setEmpresa(empresa);
            }

            return listaBuses;
        } catch (Exception pe) {
            throw new FachadaException("No se puede procesar relaciones de buses", pe);
        }
    }

    private List<Ruta> agregaRelacionesRutas(List<Ruta> listaRutas) throws FachadaException {
        try {
            for(Ruta ruta : listaRutas) {
                Empresa empresa = this.catalogoEmpresas.obten(ruta.getEmpresa());
                ruta.setEmpresa(empresa);
            }

            return listaRutas;
        } catch (Exception pe) {
            throw new FachadaException("No se puede procesar relaciones de rutas", pe);
        }
    }

    private List<Factura> agregaRelacionesFacturas(List<Factura> listaFacturas) throws FachadaException {
        try {
            for(Factura factura : listaFacturas) {
                Pasajero pasajero = this.catalogoPasajeros.obten(factura.getPasajero());
                Empresa empresa = this.catalogoEmpresas.obten(factura.getEmpresa());
                factura.setPasajero(pasajero);
                factura.setEmpresa(empresa);
            }

            return listaFacturas;
        } catch (Exception pe) {
            throw new FachadaException("No se puede procesar relaciones de facturas", pe);
        }
    }

    private List<Viaje> agregaRelacionesViajes(List<Viaje> listaViajes) throws FachadaException {
        try {
            for(Viaje viaje : listaViajes) {
                Bus bus = this.catalogoBuses.obten(viaje.getBus());
                Ruta ruta = this.catalogoRutas.obten(viaje.getRuta());
                Empresa empresa = this.catalogoEmpresas.obten(viaje.getEmpresa());
                viaje.setBus(bus);
                viaje.setRuta(ruta);
                viaje.setEmpresa(empresa);
            }

            return listaViajes;
        } catch (Exception pe) {
            throw new FachadaException("No se puede procesar relaciones de viajes", pe);
        }
    }

    private List<Reserva> agregaRelacionesReservas(List<Reserva> listaReservas) throws FachadaException {
        try {
            for(Reserva reserva : listaReservas) {
                Pasajero pasajero = this.catalogoPasajeros.obten(reserva.getPasajero());
                Viaje viaje = this.catalogoViajes.obten(reserva.getViaje());
                Empresa empresa = this.catalogoEmpresas.obten(reserva.getEmpresa());
                reserva.setPasajero(pasajero);
                reserva.setViaje(viaje);
                reserva.setEmpresa(empresa);
            }

            return listaReservas;
        } catch (Exception pe) {
            throw new FachadaException("No se puede procesar relaciones de reservas", pe);
        }
    }

    private List<Pago> agregaRelacionesPagos(List<Pago> listaPagos) throws FachadaException {
        try {
            for(Pago pago : listaPagos) {
                Usuario usuario = this.catalogoUsuarios.obten(pago.getUsuario());
                Factura factura = this.catalogoFacturas.obten(pago.getFactura());
                pago.setUsuario(usuario);
                pago.setFactura(factura);
            }

            return listaPagos;
        } catch (Exception pe) {
            throw new FachadaException("No se puede procesar relaciones de pagos", pe);
        }
    }

    private List<Usuario> agregaRelacionesUsuarios(List<Usuario> listaUsuarios) throws FachadaException {
        try {
            for(Usuario usuario : listaUsuarios) {
                Empresa empresa = this.catalogoEmpresas.obten(usuario.getEmpresa());
                usuario.setEmpresa(empresa);
            }

            return listaUsuarios;
        } catch (Exception pe) {
            throw new FachadaException("No se puede procesar relaciones de usuarios", pe);
        }
    }

    public List<Bus> consultaBuses() throws FachadaException {
        try {
            List<Bus> lista = this.catalogoBuses.lista();
            return this.agregaRelacionesBuses(lista);
        } catch (Exception pe) {
            throw new FachadaException("No se puede obtener la lista de buses", pe);
        }
    }

    public List<Bus> consultaBusesPorEstado(String estado) throws FachadaException {
        try {
            List<Bus> lista = this.catalogoBuses.listaPorEstado(estado);
            return this.agregaRelacionesBuses(lista);
        } catch (Exception pe) {
            throw new FachadaException("No se puede consultar buses por estado", pe);
        }
    }

    public List<Bus> consultaBusesPorEmpresa(String idEmpresa) throws FachadaException {
        try {
            List<Bus> lista = this.catalogoBuses.listaPorEmpresa(idEmpresa);
            return this.agregaRelacionesBuses(lista);
        } catch (Exception pe) {
            throw new FachadaException("No se puede consultar buses por empresa", pe);
        }
    }

    public List<Ruta> consultaRutas() throws FachadaException {
        try {
            List<Ruta> lista = this.catalogoRutas.lista();
            return this.agregaRelacionesRutas(lista);
        } catch (Exception pe) {
            throw new FachadaException("No se puede obtener la lista de rutas", pe);
        }
    }

    public List<Ruta> consultaRutasPorOrigen(String origen) throws FachadaException {
        try {
            List<Ruta> lista = this.catalogoRutas.listaPorOrigen(origen);
            return this.agregaRelacionesRutas(lista);
        } catch (Exception pe) {
            throw new FachadaException("No se puede consultar rutas por origen", pe);
        }
    }

    public List<Ruta> consultaRutasPorDestino(String destino) throws FachadaException {
        try {
            List<Ruta> lista = this.catalogoRutas.listaPorDestino(destino);
            return this.agregaRelacionesRutas(lista);
        } catch (Exception pe) {
            throw new FachadaException("No se puede consultar rutas por destino", pe);
        }
    }

    public List<Factura> consultaFacturas() throws FachadaException {
        try {
            List<Factura> lista = this.catalogoFacturas.lista();
            return this.agregaRelacionesFacturas(lista);
        } catch (Exception pe) {
            throw new FachadaException("No se puede obtener la lista de facturas", pe);
        }
    }

    public List<Factura> consultaFacturasPorPasajero(String idPasajero) throws FachadaException {
        try {
            List<Factura> lista = this.catalogoFacturas.listaPorPasajero(idPasajero);
            return this.agregaRelacionesFacturas(lista);
        } catch (Exception pe) {
            throw new FachadaException("No se puede consultar facturas por pasajero", pe);
        }
    }

    public List<Factura> consultaFacturasPorPeriodo(Periodo periodo) throws FachadaException {
        try {
            List<Factura> lista = this.catalogoFacturas.lista();
            List<Factura> procesado = this.agregaRelacionesFacturas(lista);
            return (List)procesado.stream().filter((f) -> periodo.contiene(f.getFechaEmision())).collect(Collectors.toList());
        } catch (Exception pe) {
            throw new FachadaException("No se puede consultar facturas por periodo", pe);
        }
    }

    public List<Usuario> consultaUsuarios() throws FachadaException {
        try {
            List<Usuario> lista = this.catalogoUsuarios.lista();
            return this.agregaRelacionesUsuarios(lista);
        } catch (Exception pe) {
            throw new FachadaException("No se puede obtener la lista de usuarios", pe);
        }
    }

    public List<Usuario> consultaUsuariosPorTipo(String tipo) throws FachadaException {
        try {
            List<Usuario> lista = this.catalogoUsuarios.listaPorTipo(tipo);
            return this.agregaRelacionesUsuarios(lista);
        } catch (Exception pe) {
            throw new FachadaException("No se puede consultar usuarios por tipo", pe);
        }
    }

    public List<Empresa> consultaEmpresas() throws FachadaException {
        try {
            return this.catalogoEmpresas.lista();
        } catch (Exception pe) {
            throw new FachadaException("No se puede obtener la lista de empresas", pe);
        }
    }

    public List<Viaje> consultaViajes() throws FachadaException {
        try {
            List<Viaje> lista = this.catalogoViajes.lista();
            return this.agregaRelacionesViajes(lista);
        } catch (Exception pe) {
            throw new FachadaException("No se puede obtener la lista de viajes", pe);
        }
    }

    public List<Viaje> consultaViajesPorRuta(String idRuta) throws FachadaException {
        try {
            List<Viaje> lista = this.catalogoViajes.listaPorRuta(idRuta);
            return this.agregaRelacionesViajes(lista);
        } catch (Exception pe) {
            throw new FachadaException("No se puede consultar viajes por ruta", pe);
        }
    }

    public List<Viaje> consultaViajesPorPeriodo(Periodo periodo) throws FachadaException {
        try {
            List<Viaje> lista = this.catalogoViajes.lista();
            List<Viaje> procesado = this.agregaRelacionesViajes(lista);
            return (List)procesado.stream().filter((v) -> periodo.contiene(v.getFechaSalida())).collect(Collectors.toList());
        } catch (Exception pe) {
            throw new FachadaException("No se puede consultar viajes por periodo", pe);
        }
    }

    public List<Reserva> consultaReservas() throws FachadaException {
        try {
            List<Reserva> lista = this.catalogoReservas.lista();
            return this.agregaRelacionesReservas(lista);
        } catch (Exception pe) {
            throw new FachadaException("No se puede obtener la lista de reservas", pe);
        }
    }

    public List<Reserva> consultaReservasPorPasajero(String idPasajero) throws FachadaException {
        try {
            List<Reserva> lista = this.catalogoReservas.listaPorPasajero(idPasajero);
            return this.agregaRelacionesReservas(lista);
        } catch (Exception pe) {
            throw new FachadaException("No se puede consultar reservas por pasajero", pe);
        }
    }

    public List<Reserva> consultaReservasPorViaje(String idViaje) throws FachadaException {
        try {
            List<Reserva> lista = this.catalogoReservas.listaPorViaje(idViaje);
            return this.agregaRelacionesReservas(lista);
        } catch (Exception pe) {
            throw new FachadaException("No se puede consultar reservas por viaje", pe);
        }
    }

    public List<Pago> consultaPagos() throws FachadaException {
        try {
            List<Pago> lista = this.catalogoPagos.lista();
            return this.agregaRelacionesPagos(lista);
        } catch (Exception pe) {
            throw new FachadaException("No se puede obtener la lista de pagos", pe);
        }
    }

    public List<Pago> consultaPagosPorUsuario(String idUsuario) throws FachadaException {
        try {
            List<Pago> lista = this.catalogoPagos.listaPorUsuario(idUsuario);
            return this.agregaRelacionesPagos(lista);
        } catch (Exception pe) {
            throw new FachadaException("No se puede consultar pagos por usuario", pe);
        }
    }

    public List<Pago> consultaPagosPorPeriodo(Periodo periodo) throws FachadaException {
        try {
            List<Pago> lista = this.catalogoPagos.lista();
            List<Pago> procesado = this.agregaRelacionesPagos(lista);
            return (List)procesado.stream().filter((p) -> periodo.contiene(p.getFechaPago())).collect(Collectors.toList());
        } catch (Exception pe) {
            throw new FachadaException("No se puede consultar pagos por periodo", pe);
        }
    }

    public List<Asiento> consultaAsientos() throws FachadaException {
        try {
            return this.catalogoAsientos.lista();
        } catch (Exception pe) {
            throw new FachadaException("No se puede obtener la lista de asientos", pe);
        }
    }

    public List<Asiento> consultaAsientosPorViaje(String idViaje) throws FachadaException {
        try {
            return this.catalogoAsientos.listaPorViaje(idViaje);
        } catch (Exception pe) {
            throw new FachadaException("No se puede consultar asientos por viaje", pe);
        }
    }

    public List<Asiento> consultaAsientosPorEstado(String estado) throws FachadaException {
        try {
            return this.catalogoAsientos.listaPorEstado(estado);
        } catch (Exception pe) {
            throw new FachadaException("No se puede consultar asientos por estado", pe);
        }
    }

    public List<Asiento> consultaAsientosDisponiblesPorViaje(String idViaje) throws FachadaException {
        try {
            return this.catalogoAsientos.listaDisponiblesPorViaje(idViaje);
        } catch (Exception pe) {
            throw new FachadaException("No se puede consultar asientos disponibles", pe);
        }
    }

    public List<Viaje> consultaViajesPorOrigen(String origen) throws FachadaException {
        try {
            List<Viaje> todosLosViajes = this.consultaViajes();
            List<Viaje> resultado = new ArrayList();

            for(Viaje viaje : todosLosViajes) {
                if (viaje.getRuta() != null && viaje.getRuta().getOrigen() != null && viaje.getRuta().getOrigen().equalsIgnoreCase(origen)) {
                    resultado.add(viaje);
                }
            }

            return resultado;
        } catch (Exception e) {
            throw new FachadaException("Error al consultar viajes por origen: " + e.getMessage(), e);
        }
    }

    public List<Viaje> consultaViajesPorDestino(String destino) throws FachadaException {
        try {
            List<Viaje> todosLosViajes = this.consultaViajes();
            List<Viaje> resultado = new ArrayList();

            for(Viaje viaje : todosLosViajes) {
                if (viaje.getRuta() != null && viaje.getRuta().getDestino() != null && viaje.getRuta().getDestino().equalsIgnoreCase(destino)) {
                    resultado.add(viaje);
                }
            }

            return resultado;
        } catch (Exception e) {
            throw new FachadaException("Error al consultar viajes por destino: " + e.getMessage(), e);
        }
    }

    public List<Viaje> consultaViajesPorEmpresa(Empresa empresa) throws FachadaException {
        try {
            List<Viaje> todosLosViajes = this.consultaViajes();
            List<Viaje> resultado = new ArrayList();

            for(Viaje viaje : todosLosViajes) {
                if (viaje.getBus() != null && viaje.getBus().getEmpresa() != null && viaje.getBus().getEmpresa().getIdEmpresa().equals(empresa.getIdEmpresa())) {
                    resultado.add(viaje);
                }
            }

            return resultado;
        } catch (Exception e) {
            throw new FachadaException("Error al consultar viajes por empresa: " + e.getMessage(), e);
        }
    }

    public List<Reserva> consultaReservasPorPasajero(Pasajero pasajero) throws FachadaException {
        try {
            return this.consultaReservasPorPasajero(pasajero.getIdPasajero());
        } catch (Exception e) {
            throw new FachadaException("Error al consultar reservas por pasajero: " + e.getMessage(), e);
        }
    }

    public List<Reserva> consultaReservasPorViaje(Viaje viaje) throws FachadaException {
        try {
            return this.consultaReservasPorViaje(viaje.getIdViaje());
        } catch (Exception e) {
            throw new FachadaException("Error al consultar reservas por viaje: " + e.getMessage(), e);
        }
    }

    public List<Factura> consultaFacturasPorPasajero(Pasajero pasajero) throws FachadaException {
        try {
            return this.consultaFacturasPorPasajero(pasajero.getIdPasajero());
        } catch (Exception e) {
            throw new FachadaException("Error al consultar facturas por pasajero: " + e.getMessage(), e);
        }
    }

    public List<Factura> consultaFacturasPorEstado(String estado) throws FachadaException {
        try {
            List<Factura> todasLasFacturas = this.consultaFacturas();
            List<Factura> resultado = new ArrayList();

            for(Factura factura : todasLasFacturas) {
                if (factura.getEstado() != null && factura.getEstado().equalsIgnoreCase(estado)) {
                    resultado.add(factura);
                }
            }

            return resultado;
        } catch (Exception e) {
            throw new FachadaException("Error al consultar facturas por estado: " + e.getMessage(), e);
        }
    }

    public List<Bus> consultaBusesPorEmpresa(Empresa empresa) throws FachadaException {
        try {
            return this.consultaBusesPorEmpresa(empresa.getIdEmpresa());
        } catch (Exception e) {
            throw new FachadaException("Error al consultar buses por empresa: " + e.getMessage(), e);
        }
    }

    public void exportarBusesATxt(String nombreArchivo) throws FachadaException {
        try {
            List<Bus> buses = this.consultaBuses();
            this.exportador.exportarBuses(buses, nombreArchivo);
        } catch (Exception e) {
            throw new FachadaException("Error al exportar buses a TXT", e);
        }
    }

    public void exportarRutasATxt(String nombreArchivo) throws FachadaException {
        try {
            List<Ruta> rutas = this.consultaRutas();
            this.exportador.exportarRutas(rutas, nombreArchivo);
        } catch (Exception e) {
            throw new FachadaException("Error al exportar rutas a TXT", e);
        }
    }

    public void exportarUsuariosATxt(String nombreArchivo) throws FachadaException {
        try {
            List<Usuario> usuarios = this.consultaUsuarios();
            this.exportador.exportarUsuarios(usuarios, nombreArchivo);
        } catch (Exception e) {
            throw new FachadaException("Error al exportar usuarios a TXT", e);
        }
    }

    public void exportarEmpresasATxt(String nombreArchivo) throws FachadaException {
        try {
            List<Empresa> empresas = this.consultaEmpresas();
            this.exportador.exportarEmpresas(empresas, nombreArchivo);
        } catch (Exception e) {
            throw new FachadaException("Error al exportar empresas a TXT", e);
        }
    }

    public void exportarViajesATxt(String nombreArchivo) throws FachadaException {
        try {
            List<Viaje> viajes = this.consultaViajes();
            this.exportador.exportarViajes(viajes, nombreArchivo);
        } catch (Exception e) {
            throw new FachadaException("Error al exportar viajes a TXT", e);
        }
    }

    public void exportarReservasATxt(String nombreArchivo) throws FachadaException {
        try {
            List<Reserva> reservas = this.consultaReservas();
            this.exportador.exportarReservas(reservas, nombreArchivo);
        } catch (Exception e) {
            throw new FachadaException("Error al exportar reservas a TXT", e);
        }
    }

    public void exportarPasajerosATxt(String nombreArchivo) throws FachadaException {
        try {
            List var2 = this.catalogoPasajeros.lista();
        } catch (Exception e) {
            throw new FachadaException("Error al exportar pasajeros a TXT", e);
        }
    }

    public void exportarAsientosATxt(String nombreArchivo) throws FachadaException {
        try {
            List<Asiento> asientos = this.consultaAsientos();
            this.exportador.exportarAsientos(asientos, nombreArchivo);
        } catch (Exception e) {
            throw new FachadaException("Error al exportar asientos a TXT", e);
        }
    }

    public void exportarMapaAsientosATxt(String idViaje, String nombreArchivo) throws FachadaException {
        try {
            List<Asiento> asientos = this.consultaAsientosPorViaje(idViaje);
            this.exportador.exportarMapaAsientos(asientos, idViaje, nombreArchivo);
        } catch (Exception e) {
            throw new FachadaException("Error al exportar mapa de asientos a TXT", e);
        }
    }

    public void exportarTodosLosReportes() throws FachadaException {
        try {
            System.out.println("\n=== EXPORTANDO TODOS LOS REPORTES ===");
            this.exportarBusesATxt("reporte_buses");
            this.exportarRutasATxt("reporte_rutas");
            this.exportarUsuariosATxt("reporte_usuarios");
            this.exportarEmpresasATxt("reporte_empresas");
            this.exportarViajesATxt("reporte_viajes");
            this.exportarReservasATxt("reporte_reservas");
            this.exportarAsientosATxt("reporte_asientos");
            System.out.println("\n✓ Todos los reportes han sido exportados exitosamente.");
            System.out.println("  Ubicación: " + this.exportador.getDirectorioReportes());
        } catch (Exception e) {
            throw new FachadaException("Error al exportar todos los reportes", e);
        }
    }

    public void generarAsientosParaViaje(String idViaje, int capacidadBus) throws FachadaException {
        try {
            System.out.println("\nGenerando " + capacidadBus + " asientos para el viaje " + idViaje + "...");

            for(int i = 1; i <= capacidadBus; ++i) {
                String idAsiento = idViaje + "-A" + String.format("%03d", i);
                String ubicacion = this.determinarUbicacionAsiento(i);
                Asiento asiento = new Asiento(idAsiento, i, idViaje, ubicacion);
                asiento.setEstado("Disponible");
                this.agrega(asiento);
            }

            System.out.println("✓ Asientos generados exitosamente para el viaje " + idViaje);
        } catch (Exception e) {
            throw new FachadaException("Error al generar asientos para el viaje", e);
        }
    }

    private String determinarUbicacionAsiento(int numeroAsiento) {
        int posicion = (numeroAsiento - 1) % 4;
        switch (posicion) {
            case 0:
            case 3:
                return "Ventana";
            case 1:
            case 2:
                return "Pasillo";
            default:
                return "Centro";
        }
    }

    public boolean reservarAsiento(String idViaje, int numeroAsiento, String idReserva) throws FachadaException {
        try {
            Asiento asiento = this.catalogoAsientos.obtenPorViajeYNumero(idViaje, numeroAsiento);
            if (asiento == null) {
                throw new FachadaException("El asiento no existe");
            } else {
                if (asiento.getEstado() != null && "Disponible".equalsIgnoreCase(asiento.getEstado().trim())) {
                    asiento.setEstado("Disponible");
                }

                if (asiento.reservar(idReserva)) {
                    asiento.setEstado("Reservado");
                    this.actualiza(asiento);
                    return true;
                } else {
                    return false;
                }
            }
        } catch (Exception e) {
            throw new FachadaException("Error al reservar el asiento", e);
        }
    }

    public void liberarAsiento(String idViaje, int numeroAsiento) throws FachadaException {
        try {
            Asiento asiento = this.catalogoAsientos.obtenPorViajeYNumero(idViaje, numeroAsiento);
            if (asiento == null) {
                throw new FachadaException("El asiento no existe");
            } else {
                asiento.setEstado("Disponible");
                this.actualiza(asiento);
            }
        } catch (Exception e) {
            throw new FachadaException("Error al liberar el asiento", e);
        }
    }

    public int[] obtenerEstadisticasAsientos(String idViaje) throws FachadaException {
        try {
            List<Asiento> asientos = this.consultaAsientosPorViaje(idViaje);
            int[] estadisticas = new int[3];

            for(Asiento asiento : asientos) {
                if ("Disponible".equals(asiento.getEstado())) {
                    int var10002 = estadisticas[0]++;
                } else if ("Reservado".equals(asiento.getEstado())) {
                    int var7 = estadisticas[1]++;
                } else if ("Ocupado".equals(asiento.getEstado())) {
                    int var8 = estadisticas[2]++;
                }
            }

            return estadisticas;
        } catch (Exception e) {
            throw new FachadaException("Error al obtener estadísticas de asientos", e);
        }
    }
}
