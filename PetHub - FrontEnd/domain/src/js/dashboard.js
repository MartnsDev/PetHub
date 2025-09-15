document.addEventListener("DOMContentLoaded", () => {
  const token = localStorage.getItem("token");
  const nomeUsuario = localStorage.getItem("nomeUsuario");
  const tutorId = localStorage.getItem("idUsuario"); // pega o ID do usuário

  // Redireciona para login se não estiver logado
  if (!token || !tutorId) {
    window.location.href = "/index.html";
    return;
  }

  // Exibe usuário logado e botão de logout
  const userInfo = document.getElementById("userInfo");
  if (userInfo) {
    userInfo.innerHTML = `
      <span>👤 ${nomeUsuario || "Usuário"}</span>
      <button id="logoutBtn">Sair</button>
    `;
    document.getElementById("logoutBtn").addEventListener("click", () => {
      localStorage.clear();
      window.location.href = "/index.html";
    });
  }

  // Função para carregar pets
  async function carregarPets() {
    const petsGrid = document.getElementById("petsGrid");
    try {
      const response = await fetch("http://localhost:8080/pets/disponiveis", {
        headers: { Authorization: `Bearer ${token}` },
      });

      if (!response.ok) throw new Error(`Erro HTTP: ${response.status}`);
      const pets = await response.json();

      petsGrid.innerHTML = "";

      pets.forEach((pet) => {
        const foto =
          pet.imagens && pet.imagens.length > 0
            ? `http://localhost:8080${pet.imagens[0]}`
            : "https://via.placeholder.com/400x300?text=Sem+Foto";

        const card = document.createElement("div");
        card.className = "card";
        card.innerHTML = `
          <img src="${foto}" alt="${pet.nome}" onerror="this.src='https://via.placeholder.com/400x300?text=Sem+Foto'">
          <div class="card-body">
            <h3>${pet.nome}</h3>
            <p>Raça: ${pet.raca}</p>
            <p>Idade: ${pet.idade} anos</p>
            <p>Peso: ${pet.peso} kg</p>
            <p>Cor: ${pet.cor}</p>
            <button class="btn-falar" data-id="${pet.id}" data-email="${pet.abrigoEmail}">
              Tenho Interesse
            </button>
          </div>
        `;
        petsGrid.appendChild(card);
      });

      // Adiciona eventos de clique nos botões após renderizar os cards
      petsGrid.querySelectorAll(".btn-falar").forEach((btn) => {
        btn.addEventListener("click", () => {
          const petId = Number(btn.dataset.id); // garante número
          const petNome = btn
            .closest(".card-body")
            .querySelector("h3").textContent;

          solicitarAdocao(petId, petNome); // chama a função global
        });
      });
    } catch (error) {
      console.error("Erro ao carregar pets:", error);
      petsGrid.innerHTML = "<p class='error'>Erro: Servidor Desativado.</p>";
    }
  }

  carregarPets();
});
