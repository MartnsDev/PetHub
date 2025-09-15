// src/js/solicitarAdocao.js
async function solicitarAdocao(petId, petNome) {
  const token = localStorage.getItem("token");
  const tutorId = localStorage.getItem("idUsuario");

  if (!token || !tutorId) {
    alert("Você precisa estar logado para solicitar adoção!");
    return;
  }

  const motivo = prompt(
    `Por que você quer adotar ${petNome}?`,
    "Tenho muito carinho e cuidado para oferecer."
  );
  if (!motivo || motivo.trim() === "") {
    alert("Motivo é obrigatório para solicitar adoção.");
    return;
  }

  try {
    const response = await fetch("http://localhost:8080/adocoes", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
      body: JSON.stringify({
        idPet: Number(petId), // <--- aqui
        idTutor: Number(tutorId), // <--- aqui
        motivo: motivo.trim(),
      }),
    });

    const data = await response.json();

    if (!response.ok) {
      console.error("Erro do backend:", data);
      throw new Error(data.mensagem || "Erro ao solicitar adoção.");
    }

    alert("Solicitação de adoção enviada com sucesso!");
  } catch (error) {
    console.error("Erro ao solicitar adoção:", error);
    alert("Erro ao solicitar adoção: " + error.message);
  }
}

// Tornar a função global
window.solicitarAdocao = solicitarAdocao;
