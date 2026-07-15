App.registerPage("inventario", initInventario);

function initInventario() {
  bindFormMerma();
  cargarReporteCompletados();
}

function bindFormMerma() {
  const form = document.getElementById("form-merma");
  const lista = document.getElementById("lista-mermas");
  if (!form || !lista) return;

  form.addEventListener("submit", (e) => {
    e.preventDefault();

    const producto = document.getElementById("merma-producto").value.trim();
    const cantidad = document.getElementById("merma-cantidad").value;
    const motivo = document.getElementById("merma-motivo").value.trim();

    const li = document.createElement("li");
    li.textContent = `${producto} — ${cantidad} u. (${motivo})`;
    lista.appendChild(li);
    form.reset();
  });
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
