App.registerPage("cocina", initCocina);

function initCocina() {
  const grid = document.getElementById("ticket-grid");
  if (!grid) return;

  grid.querySelectorAll(".ticket").forEach((ticket) => {
    ticket.querySelector(".btn-nota")?.addEventListener("click", () => addNota(ticket));
    ticket.querySelector(".btn-despachar")?.addEventListener("click", () => despacharTicket(ticket));
  });

  updatePendientesCount();
}

function addNota(ticket) {
  const nota = prompt("Nota para cocina:");
  if (nota === null) return;

  const notaBlock = ticket.querySelector(".ticket-nota");
  const notaTexto = ticket.querySelector(".ticket-nota-texto");
  if (!notaBlock || !notaTexto) return;

  if (nota.trim() === "") {
    notaBlock.hidden = true;
    notaTexto.textContent = "";
    return;
  }

  notaTexto.textContent = nota.trim();
  notaBlock.hidden = false;
}

function despacharTicket(ticket) {
  ticket.remove();
  updatePendientesCount();
  showEmptyStateIfNeeded();
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
