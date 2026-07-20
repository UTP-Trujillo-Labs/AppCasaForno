package pe.edu.utp.appcasaforno.domain.model;

public record Producto(
        String id,
        String nombre,
        double precio,
        String categoria,
        String imagen,
        Integer stock,
        String unidad) {

    public Producto(String id, String nombre, double precio, String categoria, String imagen) {
        this(id, nombre, precio, categoria, imagen, null, null);
    }

    public Producto conStock(Integer nuevoStock) {
        return new Producto(id, nombre, precio, categoria, imagen, nuevoStock, unidad);
    }
}
