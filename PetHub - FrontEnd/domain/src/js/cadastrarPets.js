document.addEventListener("DOMContentLoaded", () => {
  const form = document.getElementById("form-cadastrar-pet");
  const mensagem = document.getElementById("mensagem");
  const nomeUsuarioSpan = document.getElementById("nomeUsuario");
  const logoutBtn = document.getElementById("logoutBtn");

  // === Verifica se usuário está logado ===
  const token = localStorage.getItem("token");
  const nomeUsuario = localStorage.getItem("nomeUsuario"); // Nome salvo ao logar

  if (!token || !nomeUsuario) {
    window.location.href = "/index.html"; // Redireciona se não estiver logado
    return;
  }

  // Exibe o nome do usuário
  nomeUsuarioSpan.textContent = nomeUsuario;

  // === Logout ===
  logoutBtn.addEventListener("click", () => {
    localStorage.clear();
    window.location.href = "/index.html";
  });

  // === Cadastro do Pet ===
  form.addEventListener("submit", async (e) => {
    e.preventDefault();

    const nome = document.getElementById("nome").value;
    const raca = document.getElementById("raca").value;
    const idade = document.getElementById("idade").value;
    const tipo = document.getElementById("tipo").value;
    const cor = document.getElementById("cor").value;
    const peso = document.getElementById("peso").value;
    const fotosInput = document.getElementById("fotos");
    const abrigoId = form.dataset.abrigoId;

    const formData = new FormData();
    formData.append("nome", nome);
    formData.append("raca", raca);
    formData.append("idade", idade);
    formData.append("tipo", tipo);
    formData.append("cor", cor);
    formData.append("peso", peso);

    // Adiciona todas as fotos
    for (let i = 0; i < fotosInput.files.length; i++) {
      formData.append("fotos", fotosInput.files[i]);
    }

    try {
      const response = await fetch(`http://localhost:8080/abrigos/10/pets`, {
        method: "POST",
        headers: {
          Authorization: `Bearer ${token}`, // JWT
        },
        body: formData,
      });

      if (!response.ok) {
        let errMsg;
        try {
          const errJson = await response.json();
          errMsg = errJson.mensagem || response.statusText;
        } catch {
          errMsg = response.statusText;
        }
        throw new Error(`Erro ${response.status} - ${errMsg}`);
      }

      const data = await response.json();
      mensagem.style.color = "green";
      mensagem.textContent = `Pet cadastrado com sucesso: ${data.nome}`;
      form.reset();
    } catch (error) {
      mensagem.style.color = "red";
      mensagem.textContent = `Erro ao cadastrar pet: ${error.message}`;
      console.error(error);
    }
  });

  // === Mostra input se raça for "Outro" ===
  const selectRaca = document.getElementById("raca");
  const inputOutraRaca = document.getElementById("outraRaca");

  if (selectRaca && inputOutraRaca) {
    selectRaca.addEventListener("change", () => {
      if (selectRaca.value === "Outro") {
        inputOutraRaca.style.display = "block";
        inputOutraRaca.required = true;
      } else {
        inputOutraRaca.style.display = "none";
        inputOutraRaca.required = false;
      }
    });
  }
});
