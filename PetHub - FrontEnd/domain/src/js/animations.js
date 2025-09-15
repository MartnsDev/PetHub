// Menu Mobile Functions
const mobileToggle = document.getElementById("mobileMenuToggle");
const mobileMenu = document.getElementById("mobileMenu");
const mobileOverlay = document.getElementById("mobileOverlay");

function toggleMobileMenu() {
  mobileToggle.classList.toggle("active");
  mobileMenu.classList.toggle("active");
  mobileOverlay.classList.toggle("active");

  // Muda o ícone
  const icon = mobileToggle.querySelector("i");
  if (mobileToggle.classList.contains("active")) {
    icon.className = "fas fa-times";
  } else {
    icon.className = "fas fa-bars";
  }
}

mobileToggle.addEventListener("click", toggleMobileMenu);
mobileOverlay.addEventListener("click", toggleMobileMenu);

// Fecha menu ao clicar em um link
document.querySelectorAll(".mobile-menu .menu-link").forEach((link) => {
  link.addEventListener("click", toggleMobileMenu);
});

// Criar patinhas caminhando
function createWalkingPaws() {
  const pawContainer = document.createElement("div");
  pawContainer.className = "paw-prints";
  pawContainer.style.cssText = `
          position: fixed;
          width: 100%;
          height: 100%;
          pointer-events: none;
          z-index: -1;
          overflow: hidden;
        `;

  document.body.appendChild(pawContainer);

  // Criar 5 patinhas
  for (let i = 0; i < 5; i++) {
    const paw = document.createElement("div");
    paw.innerHTML = "🐾";
    paw.className = "paw";
    paw.style.cssText = `
            position: absolute;
            top: ${10 + i * 20}%;
            left: -50px;
            font-size: 24px;
            color: rgba(59, 186, 157, 0.15);
            animation: walkPaw 15s linear infinite;
            animation-delay: ${-i * 3}s;
          `;
    pawContainer.appendChild(paw);
  }
}

// Criar ícones flutuantes
function createFloatingIcons() {
  const iconContainer = document.createElement("div");
  iconContainer.className = "floating-icons";
  iconContainer.style.cssText = `
          position: fixed;
          width: 100%;
          height: 100%;
          pointer-events: none;
          z-index: -1;
        `;

  document.body.appendChild(iconContainer);

  const icons = ["🐱", "🐶", "🐰", "🐹", "🦜"];

  // Criar ícones flutuantes
  for (let i = 0; i < 5; i++) {
    const icon = document.createElement("div");
    icon.innerHTML = icons[i];
    icon.className = "floating-icon";
    icon.style.cssText = `
            position: absolute;
            left: ${10 + i * 20}%;
            bottom: -50px;
            font-size: 16px;
            color: rgba(59, 186, 157, 0.2);
            animation: floatUp 12s linear infinite;
            animation-delay: ${-i * 2}s;
          `;
    iconContainer.appendChild(icon);
  }
}

// Efeito de corações ao clicar nos cards
function createHeartEffect() {
  document.addEventListener("click", function (e) {
    if (e.target.closest(".card")) {
      const heart = document.createElement("div");
      heart.innerHTML = "❤️";
      heart.className = "heart-float";
      heart.style.cssText = `
              position: fixed;
              left: ${e.clientX}px;
              top: ${e.clientY}px;
              font-size: 20px;
              color: #e74c3c;
              pointer-events: none;
              animation: heartFloat 2s ease-out forwards;
              z-index: 1000;
            `;
      document.body.appendChild(heart);
      setTimeout(() => heart.remove(), 2000);
    }
  });
}

// Melhorar responsividade do menu mobile
function handleMobileMenuResize() {
  const mobileMenu = document.getElementById("mobileMenu");
  const mobileOverlay = document.getElementById("mobileOverlay");
  const mobileToggle = document.getElementById("mobileMenuToggle");

  if (window.innerWidth > 768) {
    mobileMenu.classList.remove("active");
    mobileOverlay.classList.remove("active");
    mobileToggle.classList.remove("active");
    const icon = mobileToggle.querySelector("i");
    icon.className = "fas fa-bars";
  }
}

window.addEventListener("resize", handleMobileMenuResize);

// Swipe para fechar menu mobile
let startY = 0;
const mobileMenuEl = document.getElementById("mobileMenu");

mobileMenuEl.addEventListener("touchstart", function (e) {
  startY = e.touches[0].clientY;
});

mobileMenuEl.addEventListener("touchmove", function (e) {
  const currentY = e.touches[0].clientY;
  const diffY = startY - currentY;

  if (diffY < -50) {
    // Swipe para baixo
    const mobileToggle = document.getElementById("mobileMenuToggle");
    const mobileOverlay = document.getElementById("mobileOverlay");

    mobileMenuEl.classList.remove("active");
    mobileOverlay.classList.remove("active");
    mobileToggle.classList.remove("active");

    const icon = mobileToggle.querySelector("i");
    icon.className = "fas fa-bars";
  }
});

// Inicializar animações quando a página carregar
document.addEventListener("DOMContentLoaded", function () {
  createWalkingPaws();
  createFloatingIcons();
  createHeartEffect();

  // Adicionar animação de entrada aos cards existentes
  setTimeout(() => {
    const cards = document.querySelectorAll(".card");
    cards.forEach((card, index) => {
      card.style.animationDelay = `${index * 0.1}s`;
      card.classList.add("card-appear");
    });
  }, 500);
});
