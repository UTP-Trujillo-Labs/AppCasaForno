App.registerPage("inventario", initInventario);

function initInventario() {
  cargarReporteCompletados();
  cargarInventario();
}

async function cargarInventario() {
  const tbody = document.getElementById("inventario-insumos");
  if (!tbody) return;

  try {
    const [productosRes, categoriasRes] = await Promise.all([
      fetch("/api/productos"),
      fetch("/api/categorias"),
    ]);
    if (!productosRes.ok) throw new Error("Error al obtener inventario");
    if (!categoriasRes.ok) throw new Error("Error al obtener categorías");

    const productos = await productosRes.json();
    const categorias = await categoriasRes.json();
    const labels = Object.fromEntries(categorias.map((c) => [c.id, c.label]));

    renderInventario(productos, labels);
  } catch (err) {
    console.error(err);
    tbody.innerHTML =
      '<tr><td colspan="4" class="content-error">No se pudo cargar el inventario.</td></tr>';
  }
}

function formatoStock(producto) {
  if (producto.stock == null || !producto.unidad) return "N/A";
  return `${producto.stock} ${producto.unidad}`;
}

function renderInventario(productos, labels) {
  const tbody = document.getElementById("inventario-insumos");
  if (!tbody) return;

  if (!productos.length) {
    tbody.innerHTML =
      '<tr><td colspan="4" class="content-placeholder">No hay insumos registrados.</td></tr>';
    return;
  }

  tbody.innerHTML = productos
    .map((producto) => {
      const categoria = labels[producto.categoria] || producto.categoria || "—";
      return `
        <tr>
          <td>${producto.nombre}</td>
          <td>${categoria}</td>
          <td>${formatoStock(producto)}</td>
          <td class="inventario-precio">${App.formatMoney(producto.precio)}</td>
        </tr>`;
    })
    .join("");
}

async function cargarReporteCompletados() {
  const tbody = document.getElementById("reporte-pedidos");
  const totalEl = document.getElementById("reporte-total");
  const cantidadEl = document.getElementById("reporte-cantidad");
  if (!tbody || !totalEl || !cantidadEl) return;

  try {
    const response = await fetch("/api/pedidos/completados");
    if (!response.ok) throw new Error("Error al obtener histórico");

    const pedidos = await response.json();
    renderReporteCompletados(pedidos);
  } catch (err) {
    console.error(err);
    tbody.innerHTML =
      '<tr><td colspan="6" class="content-error">No se pudo cargar el histórico de pedidos.</td></tr>';
    totalEl.textContent = App.formatMoney(0);
    cantidadEl.textContent = "0";
  }
}

function renderReporteCompletados(pedidos) {
  const tbody = document.getElementById("reporte-pedidos");
  const totalEl = document.getElementById("reporte-total");
  const cantidadEl = document.getElementById("reporte-cantidad");
  if (!tbody || !totalEl || !cantidadEl) return;

  if (!pedidos.length) {
    tbody.innerHTML =
      '<tr><td colspan="6" class="content-placeholder">Aún no hay pedidos en el histórico.</td></tr>';
    totalEl.textContent = App.formatMoney(0);
    cantidadEl.textContent = "0";
    return;
  }

  const totalHistorico = pedidos.reduce((sum, pedido) => sum + Number(pedido.total || 0), 0);
  totalEl.textContent = App.formatMoney(totalHistorico);
  cantidadEl.textContent = String(pedidos.length);

  tbody.innerHTML = pedidos
    .map((pedido) => {
      const estado = pedido.estado || "completado";
      const items = (pedido.items || []).join(", ");
      return `
        <tr>
          <td>#${pedido.ticket}</td>
          <td>${pedido.mesa || "—"}</td>
          <td>${pedido.cliente || "—"}</td>
          <td class="reporte-items">${items}</td>
          <td>
            <span class="estado-pedido estado-pedido-${estado}">
              ${App.ESTADO_PEDIDO_LABELS[estado] || estado}
            </span>
          </td>
          <td class="reporte-monto">${App.formatMoney(pedido.total)}</td>
        </tr>`;
    })
    .join("");
}
