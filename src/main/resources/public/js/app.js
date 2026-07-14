const App = {
  DEFAULT_PAGE: "pedidos",
  VALID_PAGES: ["pedidos", "cocina", "mesas", "inventario", "delivery"],

  pages: {},
  loadedScripts: new Set(),

  registerPage(name, initFn) {
    this.pages[name] = initFn;
  },

  getPageFromHash() {
    const hash = location.hash.replace("#", "");
    return this.VALID_PAGES.includes(hash) ? hash : this.DEFAULT_PAGE;
  },

  navigateTo(page) {
    if (!this.VALID_PAGES.includes(page)) return;
    location.hash = page;
  },

  setActiveMenuItem(page) {
    document.querySelectorAll(".menu-item").forEach((item) => {
      item.classList.toggle("active", item.dataset.page === page);
    });
  },

  loadScript(src) {
    return new Promise((resolve, reject) => {
      const script = document.createElement("script");
      script.src = src;
      script.onload = resolve;
      script.onerror = () => reject(new Error(`No se pudo cargar ${src}`));
      document.body.appendChild(script);
    });
  },

  async loadPageScript(page) {
    if (this.loadedScripts.has(page)) return;

    await this.loadScript(`/js/pages/${page}.js`);
    this.loadedScripts.add(page);
  },

  async loadPage(page) {
    const contentArea = document.getElementById("content-area");
    if (!contentArea) return;

    this.setActiveMenuItem(page);
    contentArea.innerHTML = '<p class="content-loading">Cargando…</p>';

    try {
      const response = await fetch(`/pages/${page}.html`);
      if (!response.ok) throw new Error(`HTTP ${response.status}`);

      contentArea.innerHTML = await response.text();
      await this.loadPageScript(page);
      this.pages[page]?.();
    } catch (err) {
      console.error(err);
      contentArea.innerHTML =
        '<p class="content-error">No se pudo cargar la página. Intenta de nuevo.</p>';
    }
  },

  renderEmptyList(listEl, message) {
    listEl.innerHTML = `<li class="data-list-empty">${message}</li>`;
  },

  bindContactForm() {
    const form = document.getElementById("contactForm");
    if (!form) return;

    form.addEventListener("submit", async (e) => {
      e.preventDefault();

      const formData = {
        nombre: document.getElementById("nombre").value,
        correo: document.getElementById("correo").value,
        mensaje: document.getElementById("mensaje").value,
      };

      try {
        const response = await fetch("/api/contact", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(formData),
        });

        const result = await response.json();
        alert(result.message);
      } catch (err) {
        console.error(err);
        alert("Error al enviar el mensaje.");
      }
    });
  },
};

document.addEventListener("DOMContentLoaded", () => {
  const contentArea = document.getElementById("content-area");
  const menu = document.getElementById("main-menu");

  if (!contentArea || !menu) return;

  menu.addEventListener("click", (e) => {
    const link = e.target.closest(".menu-item");
    if (!link) return;
    e.preventDefault();
    App.navigateTo(link.dataset.page);
  });

  window.addEventListener("hashchange", () => {
    App.loadPage(App.getPageFromHash());
  });

  if (!location.hash) {
    history.replaceState(null, "", `#${App.DEFAULT_PAGE}`);
  }

  App.loadPage(App.getPageFromHash());
});
