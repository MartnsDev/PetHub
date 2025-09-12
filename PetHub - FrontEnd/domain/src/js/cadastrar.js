// src/js/register.js

const registerForm = document.getElementById('registerForm');
const registerBtn = document.getElementById('registerBtn');
const btnText = document.getElementById('btnText');
const btnLoader = document.getElementById('btnLoader');
const errorMessage = document.getElementById('errorMessage');
const successMessage = document.getElementById('successMessage');

registerForm.addEventListener('submit', async (e) => {
    e.preventDefault();

    // Esconde mensagens
    errorMessage.style.display = 'none';
    successMessage.style.display = 'none';

    // Mostra loader
    btnText.style.display = 'none';
    btnLoader.style.display = 'inline-block';

    const nome = document.getElementById('nome').value.trim();
    const email = document.getElementById('email').value.trim();
    const senha = document.getElementById('senha').value;
    const confirmarSenha = document.getElementById('confirmarSenha').value;

    // Validação local: senha e confirmação
    if (senha !== confirmarSenha) {
        errorMessage.textContent = "As senhas não conferem!";
        errorMessage.style.display = 'block';
        btnText.style.display = 'inline';
        btnLoader.style.display = 'none';
        return;
    }

    try {
        const response = await fetch('http://localhost:8080/auth/cadastro', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ nome, email, senha })
        });

        // Aqui sempre vamos receber JSON (ApiResponse)
        const data = await response.json();

        if (!response.ok) {
            // Mostra a mensagem de erro do backend
            throw new Error(data.mensagem || 'Erro ao cadastrar usuário');
        }

        // Sucesso
        successMessage.textContent = data.mensagem || "Cadastro realizado com sucesso! Redirecionando...";
        successMessage.style.display = 'block';

        // Redireciona para login após 2s
        setTimeout(() => {
            window.location.href = '/index.html';
        }, 2000);

    } catch (error) {
        console.error(error);
        errorMessage.textContent = error.message;
        errorMessage.style.display = 'block';
    } finally {
        btnText.style.display = 'inline';
        btnLoader.style.display = 'none';
    }
});
