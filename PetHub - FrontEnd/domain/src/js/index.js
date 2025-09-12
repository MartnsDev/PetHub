// src/js/index.js
const loginForm = document.getElementById('loginForm');
const loginBtn = document.getElementById('loginBtn');
const btnText = document.getElementById('btnText');
const btnLoader = document.getElementById('btnLoader');
const errorMessage = document.getElementById('errorMessage');

loginForm.addEventListener('submit', async (e) => {
    e.preventDefault();

    // Mostra loader
    btnText.style.display = 'none';
    btnLoader.style.display = 'inline-block';
    errorMessage.style.display = 'none';

    const email = document.getElementById('email').value.trim();
    const senha = document.getElementById('password').value;

    try {
        const response = await fetch('http://localhost:8080/auth/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, senha }) // nome do campo correto
        });

        const data = await response.json();

        if (!response.ok) {
            throw new Error(data.mensagem || 'Erro ao fazer login');
        }

        // Salva token e dados do usuário no localStorage
        localStorage.setItem('token', data.token);
        localStorage.setItem('emailUsuario', data.email);
        localStorage.setItem('nomeUsuario', data.nome);

        // Redireciona para o dashboard
        window.location.href = '/domain/dashboard.html';

    } catch (error) {
        console.error(error);
        errorMessage.textContent = error.message;
        errorMessage.style.display = 'block';
    } finally {
        btnText.style.display = 'inline';
        btnLoader.style.display = 'none';
    }
});
