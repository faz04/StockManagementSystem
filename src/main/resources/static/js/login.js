// Login page functionality - Simplified to prevent loops
const API_BASE_URL = '/api';

// DOM elements
const loginForm = document.getElementById('loginForm');
const usernameInput = document.getElementById('username');
const passwordInput = document.getElementById('password');
const rememberMeInput = document.getElementById('rememberMe');
const loginBtn = document.getElementById('loginBtn');
const loadingOverlay = document.getElementById('loadingOverlay');

// Initialize page - NO automatic session checks to prevent loops
document.addEventListener('DOMContentLoaded', function() {
    // Only add form submit handler - no automatic redirects
    if (loginForm) {
        loginForm.addEventListener('submit', handleLogin);
    }
    
    // Add enter key handler for inputs
    if (usernameInput) {
        usernameInput.addEventListener('keypress', handleEnterKey);
    }
    if (passwordInput) {
        passwordInput.addEventListener('keypress', handleEnterKey);
    }
    
    console.log('Login page initialized');
});

// Handle form submission
async function handleLogin(event) {
    event.preventDefault();
    
    const username = usernameInput.value.trim();
    const password = passwordInput.value;
    const rememberMe = rememberMeInput.checked;
    
    // Validate inputs
    if (!username || !password) {
        showMessage('Please enter both username and password', 'error');
        return;
    }
    
    // Show loading state
    showLoading(true);
    disableForm(true);
    
    try {
        const loginData = {
            username: username,
            password: password,
            rememberMe: rememberMe
        };
        
        console.log('Attempting login for:', username);
        
        const response = await fetch(`${API_BASE_URL}/auth/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(loginData)
        });
        
        const result = await response.json();
        console.log('Login response:', result);
        
        if (response.ok && result.success) {
            // Login successful
            console.log('Login successful, storing token and redirecting');
            handleLoginSuccess(result.data, rememberMe);
        } else {
            // Login failed
            const errorMessage = result.message || 'Login failed. Please check your credentials.';
            showMessage(errorMessage, 'error');
            
            // Clear password field on failed login
            passwordInput.value = '';
            passwordInput.focus();
        }
        
    } catch (error) {
        console.error('Login error:', error);
        showMessage('Connection error. Please try again.', 'error');
    } finally {
        showLoading(false);
        disableForm(false);
    }
}

// Handle successful login
function handleLoginSuccess(loginResponse, rememberMe) {
    console.log('Processing successful login:', loginResponse);
    
    // Store authentication token
    storeToken(loginResponse.token, rememberMe);
    
    // Store user info
    storeUserInfo(loginResponse);
    
    // Show success message
    showMessage(`Welcome back, ${loginResponse.username}!`, 'success');
    
    // Debug information
    console.log('Attempting redirect...');
    console.log('Current location:', window.location.href);
    console.log('Token stored in localStorage:', localStorage.getItem('authToken'));
    console.log('Token stored in sessionStorage:', sessionStorage.getItem('authToken'));
    
    // Try immediate redirect (no setTimeout)
    console.log('Trying immediate redirect...');
    try {
        console.log('About to execute: window.location.href = "/pages/user-management.html"');
        window.location.href = '/pages/user-management.html';
        console.log('Redirect command executed successfully');
    } catch (error) {
        console.error('Immediate redirect failed:', error);
        
        // Try alternative method
        console.log('Trying window.location.assign...');
        try {
            window.location.assign('/pages/user-management.html');
            console.log('Assign redirect executed');
        } catch (error2) {
            console.error('Assign redirect also failed:', error2);
            
            // Try replace method
            console.log('Trying window.location.replace...');
            try {
                window.location.replace('/pages/user-management.html');
                console.log('Replace redirect executed');
            } catch (error3) {
                console.error('All redirect methods failed:', error3);
                alert('Redirect failed. Please manually navigate to /pages/user-management.html');
            }
        }
    }
}

// Token management
function storeToken(token, rememberMe) {
    console.log('Storing token, rememberMe:', rememberMe);
    
    const expiryTime = new Date(Date.now() + 8 * 60 * 60 * 1000).toISOString(); // 8 hours
    
    if (rememberMe) {
        localStorage.setItem('authToken', token);
        localStorage.setItem('tokenExpiry', expiryTime);
    } else {
        sessionStorage.setItem('authToken', token);
        sessionStorage.setItem('tokenExpiry', expiryTime);
    }
    
    console.log('Token stored successfully');
}

// Store user information
function storeUserInfo(userInfo) {
    const userData = {
        userId: userInfo.userId,
        username: userInfo.username,
        role: userInfo.role,
        roleDisplayName: userInfo.roleDisplayName,
        isAdmin: userInfo.isAdmin,
        permissions: userInfo.permissions,
        loginTime: userInfo.loginTime
    };
    
    if (localStorage.getItem('authToken')) {
        localStorage.setItem('userInfo', JSON.stringify(userData));
    } else {
        sessionStorage.setItem('userInfo', JSON.stringify(userData));
    }
    
    console.log('User info stored:', userData);
}

// UI Helper functions
function showLoading(show) {
    if (loadingOverlay) {
        loadingOverlay.style.display = show ? 'flex' : 'none';
    }
}

function disableForm(disabled) {
    if (usernameInput) usernameInput.disabled = disabled;
    if (passwordInput) passwordInput.disabled = disabled;
    if (rememberMeInput) rememberMeInput.disabled = disabled;
    if (loginBtn) loginBtn.disabled = disabled;
    
    if (loginBtn) {
        if (disabled) {
            loginBtn.classList.add('loading');
        } else {
            loginBtn.classList.remove('loading');
        }
    }
}

function showMessage(message, type) {
    // Remove existing messages
    const existingMessages = document.querySelectorAll('.message');
    existingMessages.forEach(msg => msg.remove());
    
    // Create message element
    const messageDiv = document.createElement('div');
    messageDiv.className = `message ${type}`;
    messageDiv.innerHTML = `
        <i class="fas ${type === 'success' ? 'fa-check-circle' : 'fa-exclamation-circle'}"></i>
        <span>${message}</span>
    `;
    
    // Add styles
    messageDiv.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        padding: 12px 20px;
        border-radius: 8px;
        color: white;
        font-weight: 500;
        z-index: 10000;
        animation: slideInRight 0.3s ease-out;
        box-shadow: 0 4px 12px rgba(0,0,0,0.15);
        display: flex;
        align-items: center;
        gap: 8px;
        min-width: 300px;
        background: ${type === 'success' ? '#27ae60' : '#e74c3c'};
    `;
    
    document.body.appendChild(messageDiv);
    
    // Auto remove after 5 seconds
    setTimeout(() => {
        if (messageDiv.parentNode) {
            messageDiv.remove();
        }
    }, 5000);
}

function handleEnterKey(event) {
    if (event.key === 'Enter') {
        event.preventDefault();
        if (loginForm) {
            loginForm.dispatchEvent(new Event('submit'));
        }
    }
}

// Password toggle functionality
function togglePassword() {
    const passwordField = document.getElementById('password');
    const toggleIcon = document.getElementById('passwordToggleIcon');
    
    if (passwordField && toggleIcon) {
        if (passwordField.type === 'password') {
            passwordField.type = 'text';
            toggleIcon.classList.remove('fa-eye');
            toggleIcon.classList.add('fa-eye-slash');
        } else {
            passwordField.type = 'password';
            toggleIcon.classList.remove('fa-eye-slash');
            toggleIcon.classList.add('fa-eye');
        }
    }
}

// Demo credentials auto-fill
function fillCredentials(username, password) {
    if (usernameInput && passwordInput) {
        usernameInput.value = username;
        passwordInput.value = password;
        
        // Add visual feedback
        usernameInput.focus();
        setTimeout(() => passwordInput.focus(), 100);
        setTimeout(() => usernameInput.focus(), 200);
        
        showMessage('Demo credentials filled. Click Sign In to continue.', 'success');
    }
}

// Add CSS for loading state
const style = document.createElement('style');
style.textContent = `
    .login-btn.loading {
        opacity: 0.7;
        cursor: not-allowed;
    }
    
    .login-btn.loading span {
        opacity: 0.7;
    }
    
    .message {
        animation: slideInRight 0.3s ease-out;
    }
    
    @keyframes slideInRight {
        from {
            opacity: 0;
            transform: translateX(100px);
        }
        to {
            opacity: 1;
            transform: translateX(0);
        }
    }
    
    .loading-overlay {
        position: fixed;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background: rgba(0, 0, 0, 0.5);
        display: flex;
        justify-content: center;
        align-items: center;
        z-index: 9999;
    }
    
    .loading-spinner {
        background: white;
        padding: 40px;
        border-radius: 12px;
        text-align: center;
        box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
    }
    
    .spinner {
        width: 40px;
        height: 40px;
        border: 4px solid #f3f3f3;
        border-top: 4px solid #667eea;
        border-radius: 50%;
        animation: spin 1s linear infinite;
        margin: 0 auto 20px;
    }
    
    @keyframes spin {
        0% { transform: rotate(0deg); }
        100% { transform: rotate(360deg); }
    }
    
    .loading-spinner p {
        margin: 0;
        color: #666;
        font-weight: 500;
    }
`;
document.head.appendChild(style);

console.log('Login.js loaded successfully');
