const API_BASE_URL = '/api';

// Global user info
let currentUser = null;

// Load dashboard data on page load
document.addEventListener('DOMContentLoaded', function() {
    initializeApp();
});

// Initialize the application
async function initializeApp() {
    // Check authentication first
    const isAuthenticated = await checkAuthentication();
    
    if (!isAuthenticated) {
        // Redirect to login page
        window.location.href = '/login.html';
        return;
    }
    
    // Load user info and dashboard data
    loadUserInfo();
    await loadDashboardData();
    setupNavigation();
}

// Check if user is authenticated
async function checkAuthentication() {
    const token = getStoredToken();
    
    if (!token) {
        return false;
    }
    
    try {
        const response = await fetch(`${API_BASE_URL}/auth/validate`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            }
        });
        
        if (response.ok) {
            const result = await response.json();
            if (result.success) {
                currentUser = result.data;
                return true;
            }
        }
    } catch (error) {
        console.error('Authentication check failed:', error);
    }
    
    // Clear invalid token
    clearStoredToken();
    return false;
}

// Get stored authentication token
function getStoredToken() {
    let token = localStorage.getItem('authToken') || sessionStorage.getItem('authToken');
    let expiry = localStorage.getItem('tokenExpiry') || sessionStorage.getItem('tokenExpiry');
    
    if (token && expiry) {
        if (new Date() < new Date(expiry)) {
            return token;
        } else {
            // Token expired
            clearStoredToken();
        }
    }
    
    return null;
}

// Clear stored authentication data
function clearStoredToken() {
    localStorage.removeItem('authToken');
    localStorage.removeItem('tokenExpiry');
    localStorage.removeItem('userInfo');
    sessionStorage.removeItem('authToken');
    sessionStorage.removeItem('tokenExpiry');
    sessionStorage.removeItem('userInfo');
}

// Load and display user information
function loadUserInfo() {
    if (currentUser) {
        // Add user info to header
        addUserInfoToHeader();
        
        // Store updated user info
        const userInfoStorage = localStorage.getItem('authToken') ? localStorage : sessionStorage;
        userInfoStorage.setItem('userInfo', JSON.stringify(currentUser));
    }
}

// Add user information and logout button to header
function addUserInfoToHeader() {
    const header = document.querySelector('header');
    if (header && currentUser) {
        // Create user info section
        const userInfoDiv = document.createElement('div');
        userInfoDiv.className = 'user-info';
        userInfoDiv.innerHTML = `
            <div class="user-details">
                <span class="user-name">
                    <i class="fas fa-user-circle"></i>
                    ${currentUser.username}
                </span>
                <span class="user-role">${currentUser.roleDisplayName}</span>
            </div>
            <button class="logout-btn" onclick="logout()">
                <i class="fas fa-sign-out-alt"></i>
                Logout
            </button>
        `;
        
        // Add styles for user info
        const style = document.createElement('style');
        style.textContent = `
            header {
                display: flex;
                justify-content: space-between;
                align-items: center;
                flex-wrap: wrap;
                gap: 20px;
            }
            
            .user-info {
                display: flex;
                align-items: center;
                gap: 15px;
                margin-left: auto;
            }
            
            .user-details {
                display: flex;
                flex-direction: column;
                align-items: flex-end;
                gap: 2px;
            }
            
            .user-name {
                font-weight: 600;
                color: #2c3e50;
                display: flex;
                align-items: center;
                gap: 8px;
            }
            
            .user-name i {
                font-size: 1.2em;
                color: #667eea;
            }
            
            .user-role {
                font-size: 0.85em;
                color: #7f8c8d;
                font-weight: 500;
            }
            
            .logout-btn {
                background: linear-gradient(135deg, #e74c3c, #c0392b);
                color: white;
                border: none;
                padding: 8px 16px;
                border-radius: 6px;
                cursor: pointer;
                font-weight: 500;
                display: flex;
                align-items: center;
                gap: 6px;
                transition: all 0.3s ease;
                font-size: 0.9em;
            }
            
            .logout-btn:hover {
                background: linear-gradient(135deg, #c0392b, #a93226);
                transform: translateY(-1px);
                box-shadow: 0 4px 8px rgba(231, 76, 60, 0.3);
            }
            
            @media (max-width: 768px) {
                .user-info {
                    width: 100%;
                    justify-content: space-between;
                    order: 3;
                }
                
                .user-details {
                    align-items: flex-start;
                }
            }
        `;
        document.head.appendChild(style);
        
        header.appendChild(userInfoDiv);
    }
}

// Logout function
async function logout() {
    const token = getStoredToken();
    
    try {
        // Call logout API
        if (token) {
            await fetch(`${API_BASE_URL}/auth/logout`, {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json'
                }
            });
        }
    } catch (error) {
        console.error('Logout API call failed:', error);
    } finally {
        // Clear local storage and redirect
        clearStoredToken();
        showMessage('Logged out successfully', 'success');
        setTimeout(() => {
            window.location.href = '/login.html';
        }, 1000);
    }
}

// Make authenticated API requests
async function makeAuthenticatedRequest(url, options = {}) {
    const token = getStoredToken();
    
    if (!token) {
        window.location.href = '/login.html';
        return null;
    }
    
    const headers = {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`,
        ...options.headers
    };
    
    try {
        const response = await fetch(url, {
            ...options,
            headers
        });
        
        if (response.status === 401) {
            // Token expired or invalid
            clearStoredToken();
            window.location.href = '/login.html';
            return null;
        }
        
        return response;
    } catch (error) {
        console.error('API request failed:', error);
        throw error;
    }
}

// Setup navigation with role-based access
function setupNavigation() {
    if (!currentUser) return;
    
    const navLinks = document.querySelectorAll('nav a');
    
    navLinks.forEach(link => {
        const href = link.getAttribute('href');
        
        // Check permissions for different sections
        if (href.includes('user-management') && !hasPermission('user.read')) {
            link.style.display = 'none';
        }
        // Add more permission checks as needed
    });
}

// Check if user has specific permission
function hasPermission(permission) {
    return currentUser && currentUser.permissions && currentUser.permissions.includes(permission);
}

async function loadDashboardData() {
    showLoading(true);
    try {
        // Load user count with authentication
        const userResponse = await makeAuthenticatedRequest(`${API_BASE_URL}/users/stats/count`);
        if (userResponse && userResponse.ok) {
            const userData = await userResponse.json();
            if (userData.success && userData.data) {
                document.getElementById('userCount').textContent = userData.data.activeUsers || 0;
            }
        } else {
            throw new Error('Failed to load user data');
        }

        // TODO: Load other dashboard data (inventory, suppliers, revenue)
        // For now, set default values
        document.getElementById('stockCount').textContent = '0';
        document.getElementById('supplierCount').textContent = '0';
        document.getElementById('revenueCount').textContent = '$0';

    } catch (error) {
        console.error('Error loading dashboard data:', error);
        document.getElementById('userCount').textContent = 'Error';
        showMessage('Failed to load dashboard data', 'error');
    } finally {
        showLoading(false);
    }
}

// Utility function to show loading overlay
function showLoading(show) {
    const loadingOverlay = document.getElementById('loadingOverlay');
    if (loadingOverlay) {
        loadingOverlay.style.display = show ? 'flex' : 'none';
    }
}

// Utility function to show messages
function showMessage(message, type = 'success') {
    // Remove existing message if any
    const existingMessage = document.querySelector('.message-toast');
    if (existingMessage) {
        existingMessage.remove();
    }

    const messageDiv = document.createElement('div');
    messageDiv.className = `message-toast message-${type}`;
    messageDiv.innerHTML = `
        <div class="message-content">
            <i class="fas ${type === 'success' ? 'fa-check-circle' : type === 'error' ? 'fa-times-circle' : 'fa-info-circle'}"></i>
            <span>${message}</span>
        </div>
        <button class="message-close" onclick="this.parentElement.remove()">
            <i class="fas fa-times"></i>
        </button>
    `;

    messageDiv.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        padding: 15px 20px;
        border-radius: 8px;
        color: white;
        z-index: 10000;
        display: flex;
        align-items: center;
        justify-content: space-between;
        gap: 15px;
        min-width: 300px;
        box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
        animation: slideInRight 0.3s ease-out;
        background: ${type === 'success' ? 'linear-gradient(135deg, #00b894 0%, #00cec9 100%)' :
        type === 'error' ? 'linear-gradient(135deg, #e74c3c 0%, #c0392b 100%)' :
            'linear-gradient(135deg, #74b9ff 0%, #0984e3 100%)'};
    `;

    document.body.appendChild(messageDiv);

    // Auto remove after 5 seconds
    setTimeout(() => {
        if (messageDiv && messageDiv.parentNode) {
            messageDiv.style.animation = 'slideOutRight 0.3s ease-in';
            setTimeout(() => {
                if (messageDiv && messageDiv.parentNode) {
                    messageDiv.remove();
                }
            }, 300);
        }
    }, 5000);
}

// Add CSS animations
const style = document.createElement('style');
style.textContent = `
    @keyframes slideInRight {
        from {
            transform: translateX(100%);
            opacity: 0;
        }
        to {
            transform: translateX(0);
            opacity: 1;
        }
    }
    
    @keyframes slideOutRight {
        from {
            transform: translateX(0);
            opacity: 1;
        }
        to {
            transform: translateX(100%);
            opacity: 0;
        }
    }
    
    .message-content {
        display: flex;
        align-items: center;
        gap: 10px;
    }
    
    .message-close {
        background: none;
        border: none;
        color: white;
        cursor: pointer;
        padding: 5px;
        border-radius: 50%;
        transition: background-color 0.3s ease;
    }
    
    .message-close:hover {
        background-color: rgba(255, 255, 255, 0.2);
    }
`;
document.head.appendChild(style);

// Test API connection on page load
async function testApiConnection() {
    try {
        const response = await fetch(`${API_BASE_URL}/test/hello`);
        if (response.ok) {
            const data = await response.json();
            console.log('API Connection successful:', data);
        } else {
            console.error('API Connection failed');
            showMessage('API connection failed. Please check if the server is running.', 'error');
        }
    } catch (error) {
        console.error('API Connection error:', error);
        showMessage('Unable to connect to server. Please check if the backend is running.', 'error');
    }
}

// Test API connection
testApiConnection();