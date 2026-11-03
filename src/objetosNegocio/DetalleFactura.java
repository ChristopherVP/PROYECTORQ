//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package objetosNegocio;

public class DetalleFactura {
    private String idDetalle;
    private String idFactura;
    private String descripcion;
    private int cantidad;
    private double precioUnitario;
    private double subtotal;

    public DetalleFactura() {
    }

    public DetalleFactura(String idDetalle, String idFactura, String descripcion, int cantidad, double precioUnitario) {
        this.idDetalle = idDetalle;
        this.idFactura = idFactura;
        this.descripcion = descripcion;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotal = (double)cantidad * precioUnitario;
    }

    public String getIdDetalle() {
        return this.idDetalle;
    }

    public void setIdDetalle(String idDetalle) {
        this.idDetalle = idDetalle;
    }

    public String getIdFactura() {
        return this.idFactura;
    }

    public void setIdFactura(String idFactura) {
        this.idFactura = idFactura;
    }

    public String getDescripcion() {
        return this.descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getCantidad() {
        return this.cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
        this.subtotal = (double)this.cantidad * this.precioUnitario;
    }

    public double getPrecioUnitario() {
        return this.precioUnitario;
    }

    public void setPrecioUnitario(double precioUnitario) {
        this.precioUnitario = precioUnitario;
        this.subtotal = (double)this.cantidad * this.precioUnitario;
    }

    public double getSubtotal() {
        return this.subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public String toString() {
        return String.format("%s: %d x $%.2f = $%.2f", this.descripcion, this.cantidad, this.precioUnitario, this.subtotal);
    }
}
