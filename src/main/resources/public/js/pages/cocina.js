App.registerPage("cocina", initCocina);

async function initCocina() {
  await cargarPedidosPendientes();
}

async function cargarPedidosPendientes() {
  const grid = document.getElementById("ticket-grid");
  if (!grid) return;

  try {
    const response = await fetch("/api/cocina/");
    if (!response.ok) throw new Error("Error al obtener pedidos pendientes");

    const pedidos = await response.json();
    renderTickets(pedidos);
  } catch (err) {
    console.error(err);
    grid.innerHTML = '<p class="ticket-grid-empty">No se pudieron cargar los pedidos.</p>';
    updatePendientesCount();
  }
}

function renderTickets(pedidos) {
  const grid = document.getElementById("ticket-grid");
  if (!grid) return;

  if (!pedidos.length) {
    grid.innerHTML = '<p class="ticket-grid-empty">No hay pedidos pendientes.</p>';
    updatePendientesCount();
    return;
  }

  grid.innerHTML = pedidos.map(crearTicketHtml).join("");
  bindTicketEvents();
  updatePendientesCount();
}

function crearTicketHtml(pedido) {
  const items = pedido.items.map((item) => `<li>${item}</li>`).join("");
  const nota = (pedido.nota || "").trim();
  const notaHtml = nota
    ? `<div class="ticket-nota"><p class="ticket-nota-texto">${nota}</p></div>`
    : `<div class="ticket-nota" hidden><p class="ticket-nota-texto"></p></div>`;
  const estado = pedido.estado || "pendiente";

  return `
    <article class="ticket" data-ticket="${pedido.ticket}">
      <header class="ticket-header">
        <div class="ticket-header-top">
          <h3>Ticket #${pedido.ticket}</h3>
          <span class="estado-pedido estado-pedido-${estado}">${App.ESTADO_PEDIDO_LABELS[estado] || estado}</span>
        </div>
        <p class="ticket-mesa">Mesa: ${pedido.mesa}</p>
      </header>
      <hr class="ticket-divider" />
      <ul class="ticket-items">
        ${items}
      </ul>
      ${notaHtml}
      <footer class="ticket-footer">
        <button type="button" class="btn-despachar">✔ Despachar</button>
      </footer>
    </article>
  `;
}

function bindTicketEvents() {
  const grid = document.getElementById("ticket-grid");
  if (!grid) return;

  grid.querySelectorAll(".ticket").forEach((ticket) => {
    ticket.querySelector(".btn-despachar")?.addEventListener("click", () => despacharTicket(ticket));
  });
}

async function despacharTicket(ticketEl) {
  const numeroTicket = Number(ticketEl.dataset.ticket);
  const btn = ticketEl.querySelector(".btn-despachar");
  if (!numeroTicket) return;

  if (btn) btn.disabled = true;

  try {
    const response = await fetch(`/api/cocina/${numeroTicket}/despachar`, { method: "POST" });
    const result = await response.json();
    if (!response.ok) {
      alert(result.error || "No se pudo despachar el pedido.");
      if (btn) btn.disabled = false;
      return;
    }

    ticketEl.remove();
    updatePendientesCount();
    showEmptyStateIfNeeded();
  } catch (err) {
    console.error(err);
    alert("Error al despachar el pedido.");
    if (btn) btn.disabled = false;
  }
}

function updatePendientesCount() {
  const countEl = document.getElementById("cocina-pendientes-count");
  const grid = document.getElementById("ticket-grid");
  if (!countEl || !grid) return;

  countEl.textContent = grid.querySelectorAll(".ticket").length;
}

function showEmptyStateIfNeeded() {
  const grid = document.getElementById("ticket-grid");
  if (!grid || grid.querySelector(".ticket")) return;

  grid.innerHTML = '<p class="ticket-grid-empty">No hay pedidos pendientes.</p>';
}
