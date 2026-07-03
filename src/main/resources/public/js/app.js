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
