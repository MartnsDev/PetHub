// ==== JAVASCRIPT ATUALIZADO PARA A API ADOPET ====

// Configuração da API - Ajuste conforme sua configuração
const API_BASE_URL = 'http://localhost:8080/api/auth';

// Estado da aplicação
let currentStep = 1;
let userEmail = '';
let verificationCode = '';

// Elementos DOM
const formTitle = document.getElementById('formTitle');
const formDescription = document.getElementById('formDescription');
const messageContainer = document.getElementById('messageContainer');
const steps = document.querySelectorAll('.step');
const formSteps = document.querySelectorAll('.form-step');

// Formulários
const emailForm = document.getElementById('emailForm');
const codeForm = document.getElementById('codeForm');
const passwordForm = document.getElementById('passwordForm');

// Inputs de código
const codeInputs = document.querySelectorAll('.code-input');

// Classe para lidar com a API
class AdopetApiClient {
    static async makeRequest(endpoint, method = 'GET', data = null) {
        try {
            const config = {
                method: method,
                headers: {
                    'Content-Type': 'application/json',
                    'Accept': 'application/json'
                }
            };

            if (data) {
                config.body = JSON.stringify(data);
            }

            const response = await fetch(`${API_BASE_URL}${endpoint}`, config);
            
            // Tentar fazer parse do JSON sempre
            let result;
            try {
                result = await response.json();
            } catch (parseError) {
                throw new Error('Erro de comunicação com o servidor');
            }
            
            // Verificar se a resposta foi bem-sucedida
            if (!response.ok) {
                throw new Error(result.mensagem || 'Erro na requisição');
            }
            
            return result;
        } catch (error) {
            if (error instanceof TypeError && error.message.includes('fetch')) {
                throw new Error('Erro de conexão. Verifique se o servidor está rodando.');
            }
            throw error;
        }
    }

    static async post(endpoint, data) {
        return this.makeRequest(endpoint, 'POST', data);
    }

    static async put(endpoint, data) {
        return this.makeRequest(endpoint, 'PUT', data);
    }
}

// Configuração dos inputs de código
codeInputs.forEach((input, index) => {
    // Permitir apenas números
    input.addEventListener('input', (e) => {
        const value = e.target.value;
        e.target.value = value.replace(/\D/g, '');
        
        // Adicionar classe visual se preenchido
        if (e.target.value) {
            e.target.classList.add('filled');
        } else {
            e.target.classList.remove('filled');
        }
        
        // Auto-focus no próximo campo
        if (e.target.value && index < codeInputs.length - 1) {
            codeInputs[index + 1].focus();
        }
    });

    // Navegação com backspace
    input.addEventListener('keydown', (e) => {
        if (e.key === 'Backspace' && !e.target.value && index > 0) {
            codeInputs[index - 1].focus();
        }
    });

    // Suporte a colar código
    input.addEventListener('paste', (e) => {
        e.preventDefault();
        const paste = e.clipboardData.getData('text');
        const digits = paste.match(/\d/g);
        
        if (digits) {
            // Limpar todos os campos primeiro
            codeInputs.forEach(inp => {
                inp.value = '';
                inp.classList.remove('filled');
            });
            
            // Preencher com os dígitos
            digits.slice(0, 6).forEach((digit, i) => {
                if (codeInputs[i]) {
                    codeInputs[i].value = digit;
                    codeInputs[i].classList.add('filled');
                }
            });
            
            // Focar no último campo preenchido ou no próximo vazio
            const lastIndex = Math.min(digits.length - 1, 5);
            codeInputs[lastIndex].focus();
        }
    });
});

// Funções de utilidade
function showMessage(message, type = 'info') {
    const messageEl = document.createElement('div');
    messageEl.className = `message ${type}-message`;
    messageEl.textContent = message;
    messageContainer.innerHTML = '';
    messageContainer.appendChild(messageEl);
    
    // Auto-remover mensagens de sucesso após 5 segundos
    if (type === 'success') {
        setTimeout(() => {
            if (messageContainer.contains(messageEl)) {
                messageContainer.removeChild(messageEl);
            }
        }, 5000);
    }
}

function clearMessage() {
    messageContainer.innerHTML = '';
}

function updateStep(step) {
    currentStep = step;
    
    // Atualizar indicadores
    steps.forEach((stepEl, index) => {
        stepEl.classList.remove('active', 'completed');
        if (index + 1 < currentStep) {
            stepEl.classList.add('completed');
        } else if (index + 1 === currentStep) {
            stepEl.classList.add('active');
        }
    });

    // Mostrar formulário correto
    formSteps.forEach(formStep => {
        formStep.classList.remove('active');
    });

    // Atualizar título, descrição e mostrar etapa correta
    switch (step) {
        case 1:
            document.getElementById('emailStep').classList.add('active');
            formTitle.textContent = 'Recuperar Senha';
            formDescription.textContent = 'Digite seu email para receber o código de verificação';
            document.getElementById('email').focus();
            break;
        case 2:
            document.getElementById('codeStep').classList.add('active');
            formTitle.textContent = 'Verificar Código';
            formDescription.textContent = `Código enviado para ${maskEmail(userEmail)}`;
            clearCodeInputs();
            codeInputs[0].focus();
            break;
        case 3:
            document.getElementById('passwordStep').classList.add('active');
            formTitle.textContent = 'Nova Senha';
            formDescription.textContent = 'Digite sua nova senha';
            document.getElementById('newPassword').focus();
            break;
    }
}

function maskEmail(email) {
    if (!email) return '';
    const [localPart, domain] = email.split('@');
    if (!domain) return email;
    
    const maskedLocal = localPart.length > 2 
        ? localPart[0] + '*'.repeat(localPart.length - 2) + localPart[localPart.length - 1]
        : localPart;
    
    return `${maskedLocal}@${domain}`;
}

function clearCodeInputs() {
    codeInputs.forEach(input => {
        input.value = '';
        input.classList.remove('filled', 'error');
    });
}

function setButtonLoading(buttonId, loaderId, textId, loading) {
    const button = document.getElementById(buttonId);
    const loader = document.getElementById(loaderId);
    const text = document.getElementById(textId);
    
    button.disabled = loading;
    if (loading) {
        text.style.display = 'none';
        loader.style.display = 'block';
        button.classList.add('loading');
    } else {
        text.style.display = 'block';
        loader.style.display = 'none';
        button.classList.remove('loading');
    }
}

function validateEmail(email) {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
}

function validatePassword(password) {
    return password && password.length >= 6;
}

// Manipuladores de formulário
emailForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    clearMessage();
    
    const email = document.getElementById('email').value.trim();
    
    if (!email) {
        showMessage('Por favor, digite seu email', 'error');
        return;
    }

    if (!validateEmail(email)) {
        showMessage('Por favor, digite um email válido', 'error');
        return;
    }

    setButtonLoading('emailBtn', 'emailLoader', 'emailBtnText', true);

    try {
        const response = await AdopetApiClient.post('/solicitar-codigo', {
            email: email
        });
        
        userEmail = email;
        showMessage(response.mensagem, 'success');
        
        setTimeout(() => {
            updateStep(2);
            clearMessage();
        }, 2000);
        
    } catch (error) {
        console.error('Erro ao solicitar código:', error);
        showMessage(error.message || 'Erro ao enviar código. Tente novamente.', 'error');
    } finally {
        setButtonLoading('emailBtn', 'emailLoader', 'emailBtnText', false);
    }
});

codeForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    clearMessage();
    
    const enteredCode = Array.from(codeInputs).map(input => input.value).join('');
    
    if (enteredCode.length !== 6) {
        showMessage('Por favor, digite o código completo', 'error');
        codeInputs.forEach(input => input.classList.add('error'));
        setTimeout(() => {
            codeInputs.forEach(input => input.classList.remove('error'));
        }, 1000);
        return;
    }

    if (!/^\d{6}$/.test(enteredCode)) {
        showMessage('O código deve conter apenas números', 'error');
        return;
    }

    setButtonLoading('codeBtn', 'codeLoader', 'codeBtnText', true);

    try {
        const response = await AdopetApiClient.post('/verificar-codigo', {
            email: userEmail,
            codigo: enteredCode
        });
        
        verificationCode = enteredCode; // Salvar o código para usar na próxima etapa
        showMessage(response.mensagem, 'success');
        
        setTimeout(() => {
            updateStep(3);
            clearMessage();
        }, 1500);
        
    } catch (error) {
        console.error('Erro ao verificar código:', error);
        showMessage(error.message || 'Código incorreto ou expirado. Tente novamente.', 'error');
        
        // Adicionar classe de erro visual
        codeInputs.forEach(input => input.classList.add('error'));
        setTimeout(() => {
            codeInputs.forEach(input => input.classList.remove('error'));
        }, 1000);
        
        // Focar no primeiro input e selecionar tudo
        codeInputs[0].focus();
        codeInputs[0].select();
    } finally {
        setButtonLoading('codeBtn', 'codeLoader', 'codeBtnText', false);
    }
});

passwordForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    clearMessage();
    
    const newPassword = document.getElementById('newPassword').value;
    const confirmPassword = document.getElementById('confirmPassword').value;
    
    // Validações no frontend
    if (!validatePassword(newPassword)) {
        showMessage('A senha deve ter pelo menos 6 caracteres', 'error');
        document.getElementById('newPassword').focus();
        return;
    }

    if (newPassword !== confirmPassword) {
        showMessage('As senhas não coincidem', 'error');
        document.getElementById('confirmPassword').focus();
        return;
    }

    setButtonLoading('passwordBtn', 'passwordLoader', 'passwordBtnText', true);

    try {
        const response = await AdopetApiClient.put('/recuperar', {
            email: userEmail,
            codigo: verificationCode,
            novaSenha: newPassword,
            confirmarSenha: confirmPassword
        });
        
        showMessage('Senha atualizada com sucesso! Redirecionando para o login...', 'success');
        
        // Desabilitar o formulário para evitar reenvios
        passwordForm.style.pointerEvents = 'none';
        
        // Salvar sucesso no sessionStorage para mostrar mensagem no login
        if (typeof Storage !== 'undefined') {
            sessionStorage.setItem('passwordResetSuccess', 'true');
        }
        
        setTimeout(() => {
            window.location.href = '/index.html';
        }, 3000);
        
    } catch (error) {
        console.error('Erro ao atualizar senha:', error);
        let errorMessage = error.message || 'Erro ao atualizar senha. Tente novamente.';
        
        // Tratar diferentes tipos de erro
        if (errorMessage.toLowerCase().includes('código')) {
            showMessage('Código expirado ou inválido. Solicitando novo código...', 'error');
            setTimeout(() => {
                updateStep(2);
                clearMessage();
                showMessage('Código expirado. Solicite um novo código.', 'info');
            }, 2000);
        } else if (errorMessage.toLowerCase().includes('senha')) {
            showMessage(errorMessage, 'error');
            document.getElementById('newPassword').focus();
        } else {
            showMessage(errorMessage, 'error');
        }
    } finally {
        setButtonLoading('passwordBtn', 'passwordLoader', 'passwordBtnText', false);
    }
});

// Botão de reenviar código
document.getElementById('resendBtn').addEventListener('click', async () => {
    clearMessage();
    
    if (!userEmail) {
        showMessage('Email não encontrado. Reinicie o processo.', 'error');
        updateStep(1);
        return;
    }
    
    const resendBtn = document.getElementById('resendBtn');
    const originalText = resendBtn.textContent;
    
    resendBtn.disabled = true;
    resendBtn.textContent = 'Reenviando...';
    
    try {
        const response = await AdopetApiClient.post('/solicitar-codigo', {
            email: userEmail
        });
        
        showMessage('Novo código enviado! Verifique sua caixa de entrada.', 'success');
        
        // Limpar campos de código
        clearCodeInputs();
        codeInputs[0].focus();
        
        // Iniciar countdown de 60 segundos
        startResendCountdown(resendBtn, originalText);
        
    } catch (error) {
        console.error('Erro ao reenviar código:', error);
        showMessage(error.message || 'Erro ao reenviar código. Tente novamente.', 'error');
        resendBtn.disabled = false;
        resendBtn.textContent = originalText;
    }
});

// Função para countdown do botão de reenvio
function startResendCountdown(button, originalText) {
    let countdown = 60;
    
    const interval = setInterval(() => {
        button.textContent = `Aguarde ${countdown}s`;
        countdown--;
        
        if (countdown < 0) {
            clearInterval(interval);
            button.disabled = false;
            button.textContent = originalText;
        }
    }, 1000);
}

// Validação em tempo real da senha
document.getElementById('newPassword').addEventListener('input', (e) => {
    const password = e.target.value;
    const confirmPassword = document.getElementById('confirmPassword').value;
    
    // Validar força da senha
    validatePasswordStrength(password);
    
    // Verificar se as senhas coincidem (apenas se confirmPassword não estiver vazio)
    if (confirmPassword) {
        validatePasswordMatch(password, confirmPassword);
    }
});

document.getElementById('confirmPassword').addEventListener('input', (e) => {
    const confirmPassword = e.target.value;
    const password = document.getElementById('newPassword').value;
    
    validatePasswordMatch(password, confirmPassword);
});

function validatePasswordStrength(password) {
    const passwordInput = document.getElementById('newPassword');
    
    // Remover classes anteriores
    passwordInput.classList.remove('weak-password', 'medium-password', 'strong-password');
    
    if (password.length === 0) return;
    
    if (password.length < 6) {
        passwordInput.classList.add('weak-password');
    } else if (password.length < 8) {
        passwordInput.classList.add('medium-password');
    } else {
        passwordInput.classList.add('strong-password');
    }
}

function validatePasswordMatch(password, confirmPassword) {
    const confirmInput = document.getElementById('confirmPassword');
    
    // Remover classes anteriores
    confirmInput.classList.remove('password-match', 'password-no-match');
    
    if (confirmPassword.length === 0) return;
    
    if (password === confirmPassword) {
        confirmInput.classList.add('password-match');
    } else {
        confirmInput.classList.add('password-no-match');
    }
}

// Função para testar conexão com o servidor
async function testServerConnection() {
    try {
        const response = await fetch(`${API_BASE_URL.replace('/api/auth', '')}/actuator/health`, {
            method: 'GET',
            timeout: 5000
        });
        return response.ok;
    } catch (error) {
        // Se não conseguir acessar o health check, tentar um endpoint público
        try {
            await fetch(API_BASE_URL, { method: 'OPTIONS' });
            return true;
        } catch (e) {
            return false;
        }
    }
}

// Função para salvar progresso no sessionStorage
function saveProgress() {
    if (typeof Storage !== 'undefined') {
        const progress = {
            currentStep,
            userEmail,
            timestamp: Date.now()
        };
        sessionStorage.setItem('passwordResetProgress', JSON.stringify(progress));
    }
}

function loadProgress() {
    if (typeof Storage !== 'undefined') {
        const saved = sessionStorage.getItem('passwordResetProgress');
        if (saved) {
            try {
                const progress = JSON.parse(saved);
                
                // Verificar se o progresso não é muito antigo (15 minutos)
                const fifteenMinutes = 15 * 60 * 1000;
                if (Date.now() - progress.timestamp < fifteenMinutes && progress.currentStep > 1) {
                    currentStep = progress.currentStep;
                    userEmail = progress.userEmail;
                    
                    if (currentStep > 1) {
                        document.getElementById('email').value = userEmail;
                    }
                    
                    updateStep(currentStep);
                    showMessage('Processo de recuperação restaurado.', 'info');
                } else {
                    // Limpar progresso antigo
                    sessionStorage.removeItem('passwordResetProgress');
                }
            } catch (error) {
                // Limpar dados corrompidos
                sessionStorage.removeItem('passwordResetProgress');
            }
        }
    }
}

function clearProgress() {
    if (typeof Storage !== 'undefined') {
        sessionStorage.removeItem('passwordResetProgress');
    }
}

// Event listeners para salvar progresso
emailForm.addEventListener('submit', saveProgress);
codeForm.addEventListener('submit', saveProgress);

// Limpar progresso ao completar
passwordForm.addEventListener('submit', clearProgress);

// Detectar quando o usuário sai da página
window.addEventListener('beforeunload', (e) => {
    if (currentStep > 1 && currentStep < 3) {
        saveProgress();
        e.returnValue = 'Você tem um processo de recuperação de senha em andamento. Tem certeza que deseja sair?';
    }
});

// Função para detectar status de conexão
function updateConnectionStatus() {
    const statusEl = document.getElementById('connectionStatus');
    
    if (!statusEl) {
        // Criar indicador de status se não existir
        const status = document.createElement('div');
        status.id = 'connectionStatus';
        status.className = 'connection-status';
        document.body.appendChild(status);
    }
    
    const status = document.getElementById('connectionStatus');
    
    if (navigator.onLine) {
        status.textContent = 'Online';
        status.className = 'connection-status online';
        setTimeout(() => {
            status.style.display = 'none';
        }, 3000);
    } else {
        status.textContent = 'Offline';
        status.className = 'connection-status offline';
        status.style.display = 'block';
    }
}

window.addEventListener('online', () => {
    updateConnectionStatus();
    clearMessage();
    showMessage('Conexão restaurada', 'success');
});

window.addEventListener('offline', () => {
    updateConnectionStatus();
    showMessage('Você está offline. Verifique sua conexão com a internet.', 'error');
});

// Função para detectar e tratar erros de rede
function handleNetworkError(error) {
    if (error.message.includes('Failed to fetch') || error.message.includes('Network')) {
        showMessage('Erro de conexão. Verifique sua internet e tente novamente.', 'error');
        updateConnectionStatus();
    }
}

// Adicionar suporte a teclas de atalho
document.addEventListener('keydown', (e) => {
    // ESC para voltar uma etapa (exceto na primeira)
    if (e.key === 'Escape' && currentStep > 1) {
        e.preventDefault();
        
        if (currentStep === 2) {
            updateStep(1);
        } else if (currentStep === 3) {
            updateStep(2);
        }
        
        showMessage('Voltou para a etapa anterior', 'info');
        setTimeout(clearMessage, 2000);
    }
    
    // Enter para submeter formulário ativo
    if (e.key === 'Enter' && !e.target.matches('button')) {
        e.preventDefault();
        
        const activeForm = document.querySelector('.form-step.active form');
        if (activeForm) {
            activeForm.dispatchEvent(new Event('submit'));
        }
    }
});

// Adicionar indicadores visuais melhorados
function addVisualEnhancements() {
    // Adicionar indicador de força da senha
    const passwordField = document.getElementById('newPassword').parentNode;
    if (!passwordField.querySelector('.password-strength-indicator')) {
        const strengthIndicator = document.createElement('div');
        strengthIndicator.className = 'password-strength-indicator';
        strengthIndicator.innerHTML = '<div class="password-strength-bar"></div>';
        passwordField.appendChild(strengthIndicator);
    }
    
    // Adicionar tooltips informativos
    addTooltips();
}

function addTooltips() {
    const emailInput = document.getElementById('email');
    const passwordInput = document.getElementById('newPassword');
    
    if (emailInput && !emailInput.title) {
        emailInput.title = 'Digite o email cadastrado na sua conta';
    }
    
    if (passwordInput && !passwordInput.title) {
        passwordInput.title = 'A senha deve ter pelo menos 6 caracteres';
    }
}

// Função para debug (remover em produção)
function debugInfo() {
    console.log('=== DEBUG INFO ===');
    console.log('Current Step:', currentStep);
    console.log('User Email:', userEmail);
    console.log('Verification Code:', verificationCode);
    console.log('API Base URL:', API_BASE_URL);
    console.log('Online Status:', navigator.onLine);
    console.log('==================');
}

// Inicialização da página
document.addEventListener('DOMContentLoaded', async () => {
    console.log('Inicializando sistema de recuperação de senha...');
    
    // Verificar se há mensagem de sucesso do login
    if (typeof Storage !== 'undefined') {
        const success = sessionStorage.getItem('passwordResetSuccess');
        if (success === 'true') {
            showMessage('Sua senha foi alterada com sucesso!', 'success');
            sessionStorage.removeItem('passwordResetSuccess');
        }
    }
    
    // Carregar progresso salvo (se houver)
    loadProgress();
    
    // Se não há progresso salvo, começar do início
    if (currentStep === 1) {
        updateStep(1);
    }
    
    // Adicionar melhorias visuais
    addVisualEnhancements();
    
  
    
    // Detectar status inicial de conexão
    updateConnectionStatus();
    
    console.log('Sistema inicializado com sucesso!');
});

// Expor funções úteis para debug (remover em produção)
if (typeof window !== 'undefined') {
    window.passwordResetDebug = {
        debugInfo,
        testConnection: testServerConnection,
        clearProgress,
        updateStep,
        showMessage
    };
}

// Botão de cancelar envio e voltar para etapa do email
document.getElementById('cancelBtn').addEventListener('click', () => {
    clearMessage();

    // Resetar email e código salvos
    userEmail = '';
    verificationCode = '';

    // Voltar para etapa 1
    updateStep(1);

    // Limpar inputs
    document.getElementById('email').value = '';
    clearCodeInputs();

    showMessage('Envio cancelado. Digite seu email novamente.', 'info');
});