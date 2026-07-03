App.registerPage("delivery", initDelivery);

function initDelivery() {
  const form = document.getElementById("form-delivery");
  const lista = document.getElementById("lista-delivery");
  if (!form || !lista) return;

  const deliveries = [];

  form.addEventListener("submit", (e) => {
    e.preventDefault();

    deliveries.push({
      cliente: document.getElementById("delivery-cliente").value.trim(),
      direccion: document.getElementById("delivery-direccion").value.trim(),
      pedido: document.getElementById("delivery-pedido").value.trim(),
    });

    renderDeliveries(lista, deliveries);
    form.reset();
  });
}

function renderDeliveries(lista, deliveries) {
  if (deliveries.length === 0) {
    App.renderEmptyList(lista, "No hay deliveries registrados.");
    return;
  }

  lista.innerHTML = deliveries
    .map(
      (d, i) =>
        `<li>
          <strong>${d.cliente}</strong> — ${d.pedido}<br>
          <small class="delivery-direccion">${d.direccion}</small>
          <button type="button" data-index="${i}" class="btn-dispatch btn-action btn-action--success">Despachar</button>
        </li>`
    )
    .join("");

  lista.querySelectorAll(".btn-dispatch").forEach((btn) => {
    btn.addEventListener("click", () => {
      deliveries.splice(Number(btn.dataset.index), 1);
      renderDeliveries(lista, deliveries);
    });
  });
}
