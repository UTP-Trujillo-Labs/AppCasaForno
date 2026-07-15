const ESTADOS_MESA = ["libre", "ocupada", "reservada"];
const ESTADO_LABELS = { libre: "Libre", ocupada: "Ocupada", reservada: "Reservada" };

App.registerPage("mesas", initMesas);

async function initMesas() {
  bindPagoModal();
  await cargarMesas();
}

async function cargarMesas() {
  const grid = document.getElementById("mesas-grid");
  if (!grid) return;

  try {
    const response = await fetch("/api/mesas/");
    if (!response.ok) throw new Error("Error al obtener mesas");

    const data = await response.json();
    renderMesas(data.mesas || []);
  } catch (err) {
    console.error(err);
    grid.innerHTML = '<p class="content-error">No se pudieron cargar las mesas.</p>';
  }
}

function renderMesas(mesas) {
  const grid = document.getElementById("mesas-grid");
  if (!grid) return;

  if (!mesas.length) {
    grid.innerHTML = '<p class="content-placeholder">No hay mesas registradas.</p>';
    return;
  }

  grid.innerHTML = mesas
    .map((mesa) => {
      const estado = ESTADOS_MESA.includes(mesa.estado) ? mesa.estado : "libre";
      return `
        <button type="button" class="mesa mesa-${estado}" data-mesa="${mesa.numero}">
          <span class="mesa-num">${mesa.numero}</span>
          <span class="mesa-estado">${ESTADO_LABELS[estado]}</span>
        </button>`;
    })
    .join("");

  grid.querySelectorAll(".mesa").forEach((mesaEl) => {
    mesaEl.addEventListener("click", () => avanzarEstadoMesa(mesaEl));
  });
}

async function avanzarEstadoMesa(mesaEl) {
  const idMesa = Number(mesaEl.dataset.mesa);
  const estadoActual =
    ESTADOS_MESA.find((e) => mesaEl.classList.contains(`mesa-${e}`)) || "libre";

  if (estadoActual === "ocupada") {
    await mostrarConfirmacionPago(mesaEl, idMesa);
    return;
  }

  await aplicarAvanceEstado(mesaEl, idMesa);
}

async function mostrarConfirmacionPago(mesaEl, idMesa) {
  const modal = document.getElementById("mesa-pago-modal");
  const contenido = document.getElementById("mesa-pago-contenido");
  const btnPagar = document.getElementById("mesa-pago-confirmar");
  const titulo = document.getElementById("mesa-pago-titulo");
  if (!modal || !contenido || !btnPagar || !titulo) return;

  titulo.textContent = `Pedido — Mesa ${idMesa}`;
  contenido.innerHTML = '<p class="content-loading">Cargando pedido…</p>';
  btnPagar.disabled = true;
  btnPagar.dataset.mesaId = String(idMesa);
  modal.hidden = false;

  try {
    const response = await fetch(`/api/pedidos/mesa/${idMesa}`);
    const pedidos = await response.json();
    if (!response.ok) {
      contenido.innerHTML = `<p class="content-error">${pedidos.error || "No se pudo cargar el pedido."}</p>`;
      return;
    }

    contenido.innerHTML = renderPedidoHtml(pedidos);
    btnPagar.disabled = false;
    btnPagar._mesaEl = mesaEl;
  } catch (err) {
    console.error(err);
    contenido.innerHTML = '<p class="content-error">Error al cargar el pedido de la mesa.</p>';
  }
}

function renderPedidoHtml(pedidos) {
  if (!pedidos.length) {
    return `
      <p class="mesa-pago-vacio">Esta mesa no tiene pedidos pendientes registrados.</p>
      <p class="mesa-pago-hint">Al pagar, la mesa pasará a estado libre.</p>`;
  }

  return pedidos
    .map(
      (pedido) => `
      <article class="mesa-pago-ticket">
        <header class="mesa-pago-ticket-header">
          <strong>Ticket #${pedido.ticket}</strong>
          <span>Cliente: ${pedido.cliente || "—"}</span>
        </header>
        <ul class="mesa-pago-items">
          ${(pedido.items || []).map((item) => `<li>${item}</li>`).join("")}
        </ul>
        ${pedido.nota ? `<p class="mesa-pago-nota">Nota: ${pedido.nota}</p>` : ""}
      </article>`
    )
    .join("");
}

function bindPagoModal() {
  const modal = document.getElementById("mesa-pago-modal");
  if (!modal || modal.dataset.bound === "1") return;
  modal.dataset.bound = "1";

  document.getElementById("mesa-pago-cerrar")?.addEventListener("click", cerrarPagoModal);
  document.getElementById("mesa-pago-cancelar")?.addEventListener("click", cerrarPagoModal);
  modal.addEventListener("click", (e) => {
    if (e.target === modal) cerrarPagoModal();
  });

  document.getElementById("mesa-pago-confirmar")?.addEventListener("click", async (e) => {
    const btn = e.currentTarget;
    const idMesa = Number(btn.dataset.mesaId);
    const mesaEl = btn._mesaEl;
    if (!idMesa) return;

    btn.disabled = true;
    try {
      const response = await fetch(`/api/pedidos/mesa/${idMesa}/pagar`, { method: "POST" });
      const result = await response.json();
      if (!response.ok) {
        alert(result.error || "No se pudo procesar el pago.");
        btn.disabled = false;
        return;
      }

      if (mesaEl) {
        actualizarVistaMesa(mesaEl, result.estado);
      }
      cerrarPagoModal();
    } catch (err) {
      console.error(err);
      alert("Error al procesar el pago de la mesa.");
      btn.disabled = false;
    }
  });
}

function cerrarPagoModal() {
  const modal = document.getElementById("mesa-pago-modal");
  const btnPagar = document.getElementById("mesa-pago-confirmar");
  if (modal) modal.hidden = true;
  if (btnPagar) {
    btnPagar.disabled = false;
    delete btnPagar._mesaEl;
    delete btnPagar.dataset.mesaId;
  }
}

async function aplicarAvanceEstado(mesaEl, idMesa) {
  try {
    const response = await fetch(`/api/mesas/${idMesa}`, { method: "POST" });
    const result = await response.json();
    if (!response.ok) {
      alert(result.error || "No se pudo actualizar la mesa.");
      return;
    }
    actualizarVistaMesa(mesaEl, result.estado);
  } catch (err) {
    console.error(err);
    alert("Error al actualizar el estado de la mesa.");
  }
}

function actualizarVistaMesa(mesaEl, estado) {
  const clases = ESTADOS_MESA.map((e) => `mesa-${e}`);
  mesaEl.classList.remove(...clases);
  mesaEl.classList.add(`mesa-${estado}`);
  mesaEl.querySelector(".mesa-estado").textContent = ESTADO_LABELS[estado] || estado;
}
