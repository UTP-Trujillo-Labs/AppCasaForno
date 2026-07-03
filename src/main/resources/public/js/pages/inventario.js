App.registerPage("inventario", initInventario);

function initInventario() {
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
