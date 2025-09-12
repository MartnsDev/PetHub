// src/js/dashboard.js

// Recupera dados do usuário logado
const nomeUsuario = localStorage.getItem('nomeUsuario');
const emailUsuario = localStorage.getItem('emailUsuario');
const token = localStorage.getItem('token');

// Se não tiver token, redireciona para login
if (!token) {
    window.location.href = '/index.html';
}

// Mostra nome e email na página
document.getElementById('nomeUsuario').textContent = nomeUsuario || '';
document.getElementById('emailUsuario').textContent = emailUsuario || '';

// Logout
document.getElementById('logoutBtn').addEventListener('click', () => {
    localStorage.clear(); // limpa token e dados do usuário
    window.location.href = '/index.html';
});
