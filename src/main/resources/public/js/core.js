document.addEventListener("DOMContentLoaded", () => {
  const stack = document.querySelector(".stack");

  // Listar las etiquetas
  fetch("/api/demo")
    .then((response) => {
      if (!response.ok) throw new Error("Error al obtener los tags: " + response.status);
      return response.json();
    })
    .then((tags) => {
      stack.innerHTML = "";
      tags.forEach((tag) => {
        const span = document.createElement("span");
        span.className = "tag";
        span.textContent = tag;
        stack.appendChild(span);
      });
    })
    .catch((err) => {
      console.error(err);
      stack.innerHTML = "<span class='tag' style='color:#f87171'>Error al cargar tags</span>";
    });

  // Boton accion para contacto
  document.getElementById('contactForm').addEventListener('submit', async (e) => {
    e.preventDefault();

    const formData = {
      nombre: document.getElementById('nombre').value,
      correo: document.getElementById('correo').value,
      mensaje: document.getElementById('mensaje').value
    };

    const response = await fetch('/api/contact', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(formData)
    });

    const result = await response.json();
    alert(result.message); // Muestra OK o mensaje de error
  });
});
