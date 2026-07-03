App.registerPage("pedidos", initPedidos);

function initPedidos() {
  const form = document.getElementById("form-pedido");
  const lista = document.getElementById("lista-pedidos");
  if (!form || !lista) return;

  const pedidos = [];

  form.addEventListener("submit", (e) => {
    e.preventDefault();

    pedidos.push({
      mesa: document.getElementById("pedido-mesa").value,
      plato: document.getElementById("pedido-plato").value.trim(),
      cantidad: document.getElementById("pedido-cantidad").value,
    });

    renderPedidos(lista, pedidos);
    form.reset();
    document.getElementById("pedido-cantidad").value = "1";
  });
}

function renderPedidos(lista, pedidos) {
  if (pedidos.length === 0) {
    App.renderEmptyList(lista, "No hay pedidos registrados.");
    return;
  }

  lista.innerHTML = pedidos
    .map(
      (p, i) =>
        `<li>Mesa ${p.mesa} — ${p.plato} ×${p.cantidad}
          <button type="button" data-index="${i}" class="btn-remove btn-action btn-action--danger">✕</button>
        </li>`
    )
    .join("");

  lista.querySelectorAll(".btn-remove").forEach((btn) => {
    btn.addEventListener("click", () => {
      pedidos.splice(Number(btn.dataset.index), 1);
      renderPedidos(lista, pedidos);
    });
  });
}
