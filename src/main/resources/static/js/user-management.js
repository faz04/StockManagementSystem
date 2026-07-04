// User Management JavaScript - No Authentication Redirects

const API_BASE_URL = '/api';
let allUsers = [];
let filteredUsers = [];

// Initialize when DOM is loaded
document.addEventListener('DOMContentLoaded', function() {
    console.log('User management page loaded successfully');
    
    // Show a welcome message
    showMessage('Welcome to User Management Dashboard!', 'success');
    
    // Try to load users (with authentication if token exists)
    loadUsers();
    setupEventListeners();
});

// Get stored authentication token (simple version - no redirects)
function getStoredToken() {
    let token = localStorage.getItem('authToken') || sessionStorage.getItem('authToken');
    let expiry = localStorage.getItem('tokenExpiry') || sessionStorage.getItem('tokenExpiry');
    
    if (token && expiry) {
        if (new Date() < new Date(expiry)) {
            console.log('Valid token found');
            return token;
        } else {
            console.log('Token expired');
        }
    } else {
        console.log('No token found');
    }
    return null;
}

// Make API requests (with optional authentication)
async function makeApiRequest(url, options = {}) {
    const token = getStoredToken();
    
    const headers = {
        'Content-Type': 'application/json',
        ...options.headers
    };
    
    // Add auth header if token exists
    if (token) {
        headers['Authorization'] = `Bearer ${token}`;
        console.log('Making authenticated request to:', url);
    } else {
        console.log('Making unauthenticated request to:', url);
    }
    
    try {
        const response = await fetch(url, {
            ...options,
            headers
        });
        
        console.log('API response status:', response.status);
        
        if (response.status === 401) {
            console.log('Authentication failed - but not redirecting');
            showMessage('Authentication required. Please login first.', 'error');
            return null;
        }
        
        return response;
    } catch (error) {
        console.error('API request failed:', error);
        showMessage('Network error: ' + error.message, 'error');
        return null;
    }
}

// Setup event listeners
function setupEventListeners() {
    console.log('Setting up event listeners');
    
    // Create user form
    const createForm = document.getElementById('createUserForm');
    if (createForm) {
        createForm.addEventListener('submit', handleCreateUser);
    }

    // Edit user form
    const editForm = document.getElementById('editUserForm');
    if (editForm) {
        editForm.addEventListener('submit', handleEditUser);
    }

    // Reset password form
    const resetForm = document.getElementById('resetPasswordForm');
    if (resetForm) {
        resetForm.addEventListener('submit', handleResetPassword);
    }

    // Search functionality
    const searchInput = document.getElementById('searchInput');
    if (searchInput) {
        searchInput.addEventListener('keyup', debounce(filterUsers, 300));
    }

    // Filter dropdowns
    const roleFilter = document.getElementById('roleFilter');
    if (roleFilter) {
        roleFilter.addEventListener('change', filterUsers);
    }

    const statusFilter = document.getElementById('statusFilter');
    if (statusFilter) {
        statusFilter.addEventListener('change', filterUsers);
    }

    // Modal close events
    setupModalEvents();

    // Table event delegation for action buttons
    const usersTableBody = document.getElementById('usersTableBody');
    if (usersTableBody) {
        usersTableBody.addEventListener('click', handleTableClick);
    }
    
    console.log('Event listeners setup complete');
}
// Replace the setupModalEvents function with:
function setupModalEvents() {
    // Close modal when clicking on close buttons
    const closeButtons = document.querySelectorAll('.close');
    closeButtons.forEach(button => {
        button.addEventListener('click', closeModal);
    });

    // Close modal when clicking outside of it
    window.addEventListener('click', function(event) {
        const editModal = document.getElementById('editModal');
        const passwordModal = document.getElementById('passwordModal');

        if (event.target === editModal) {
            editModal.style.display = 'none';
        }
        if (event.target === passwordModal) {
            passwordModal.style.display = 'none';
        }
    });

    // Close modal with Escape key
    document.addEventListener('keydown', function(event) {
        if (event.key === 'Escape') {
            closeModal();
        }
    });
}

// Add this function after the updateStats function (around line 206)
function loadUserStats() {
    updateStats();
}

// Handle clicks on table buttons
function handleTableClick(event) {
    const button = event.target.closest('button');
    if (!button) return;

    const userId = button.dataset.userId;
    const username = button.dataset.username;

    if (button.classList.contains('btn-edit')) {
        openEditModal(userId);
    } else if (button.classList.contains('btn-reset')) {
        openPasswordModal(userId);
    } else if (button.classList.contains('btn-delete')) {
        handleDeleteUser(userId, username);
    }
}

// Load all users
async function loadUsers() {
    console.log('Loading users...');
    showLoading(true);
    
    try {
        const response = await makeApiRequest(`${API_BASE_URL}/users/active`);


        if (response && response.ok) {
            const result = await response.json();
            console.log('Users loaded:', result);
            
            if (result.success && result.data) {
                allUsers = result.data;
                filteredUsers = [...allUsers];
                displayUsers(filteredUsers);
                updateStats();
                console.log(`Successfully loaded ${allUsers.length} users`);
                showMessage(`Loaded ${allUsers.length} users`, 'success');
            } else {
                console.log('No users data in response');
                displayUsers([]);
                showMessage('No users found', 'info');
            }
        } else if (response) {
            console.log('Failed to load users, status:', response.status);
            displayUsers([]);
            showMessage('Failed to load users', 'error');
        } else {
            console.log('No response received');
            displayUsers([]);
            showMessage('Unable to connect to server', 'error');
        }
    } catch (error) {
        console.error('Error loading users:', error);
        displayUsers([]);
        showMessage('Error loading users: ' + error.message, 'error');
    } finally {
        showLoading(false);
    }
}

// Update statistics
function updateStats() {
    const totalUsers = allUsers.length;
    const activeUsers = allUsers.filter(user => user.active).length;
    const adminUsers = allUsers.filter(user => user.role === 'ADMIN').length;
    const staffUsers = allUsers.filter(user => user.role !== 'ADMIN').length;

    // Update stats if elements exist
    const totalElement = document.getElementById('totalUsers');
    const activeElement = document.getElementById('activeUsers');
    const adminElement = document.getElementById('adminUsers');
    const staffElement = document.getElementById('staffUsers');

    if (totalElement) totalElement.textContent = totalUsers;
    if (activeElement) activeElement.textContent = activeUsers;
    if (adminElement) adminElement.textContent = adminUsers;
    if (staffElement) staffElement.textContent = staffUsers;
    
    console.log('Stats updated:', { totalUsers, activeUsers, adminUsers, staffUsers });
}

// Display users in table
function displayUsers(users) {
    const tbody = document.getElementById('usersTableBody');
    if (!tbody) {
        console.log('Users table body not found');
        return;
    }

    if (users.length === 0) {
        tbody.innerHTML = `
            <tr>
                <td colspan="7" style="text-align: center; padding: 40px; color: #7f8c8d;">
                    <i class="fas fa-users" style="font-size: 2rem; margin-bottom: 10px; display: block;"></i>
                    No users found
                </td>
            </tr>
        `;
        return;
    }

    tbody.innerHTML = users.map(user => `
        <tr>
            <td>${user.id}</td>
            <td><strong>${user.username}</strong></td>
            <td>${getRoleBadge(user.role)}</td>
            <td>${getStatusBadge(user.active)}</td>
            <td>${formatDate(user.createdAt)}</td>
            <td>${formatDate(user.updatedAt)}</td>
            <td>
                <div class="action-buttons">
                    <button class="btn-small btn-edit" data-user-id="${user.id}">
                        <i class="fas fa-edit"></i> Edit
                    </button>
                    <button class="btn-small btn-reset" data-user-id="${user.id}">
                        <i class="fas fa-key"></i> Reset
                    </button>
                    <button class="btn-small btn-delete" data-user-id="${user.id}" data-username="${user.username}">
                        <i class="fas fa-trash"></i> Delete
                    </button>
                </div>
            </td>
        </tr>
    `).join('');
    
    console.log(`Displayed ${users.length} users in table`);
}

// Filter users
function filterUsers() {
    const searchTerm = document.getElementById('searchInput')?.value.toLowerCase().trim() || '';
    const roleFilter = document.getElementById('roleFilter')?.value || '';
    const statusFilter = document.getElementById('statusFilter')?.value || '';

    filteredUsers = allUsers.filter(user => {
        const matchesSearch = !searchTerm || user.username.toLowerCase().includes(searchTerm);
        const matchesRole = !roleFilter || user.role === roleFilter;
        const matchesStatus = !statusFilter || user.active.toString() === statusFilter;
        return matchesSearch && matchesRole && matchesStatus;
    });

    displayUsers(filteredUsers);
    console.log(`Filtered to ${filteredUsers.length} users`);
}

// Create new user
async function handleCreateUser(event) {
    event.preventDefault();
    
    const formData = new FormData(event.target);
    const userData = {
        username: formData.get('username'),
        password: formData.get('password'),
        role: formData.get('role')
    };
    
    try {
        showLoading(true);
        const response = await makeApiRequest(`${API_BASE_URL}/users`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(userData)
        });
        
        const data = await response.json();
        
        if (response.ok && data.success) {
            showMessage('User created successfully!', 'success');
            event.target.reset();
            loadUsers();
            loadUserStats();
        } else {
            throw new Error(data.message || 'Failed to create user');
        }
    } catch (error) {
        console.error('Error creating user:', error);
        showMessage('Failed to create user: ' + error.message, 'error');
    } finally {
        showLoading(false);
    }
}

// Delete user
async function handleDeleteUser(userId, username) {
    const confirmed = confirm(`Are you sure you want to delete user "${username}"?`);
    if (!confirmed) return;
    
    try {
        showLoading(true);
        const response = await makeApiRequest(`${API_BASE_URL}/users/${userId}`, {
            method: 'DELETE'
        });
        
        const data = await response.json();
        
        if (response.ok && data.success) {
            showMessage(`User "${username}" deleted successfully!`, 'success');
            loadUsers();
            loadUserStats();
        } else {
            throw new Error(data.message || 'Failed to delete user');
        }
    } catch (error) {
        console.error('Error deleting user:', error);
        showMessage('Failed to delete user: ' + error.message, 'error');
    } finally {
        showLoading(false);
    }
}

// Open edit modal
function openEditModal(userId) {
    const user = allUsers.find(u => u.id === userId);
    if (!user) {
        showMessage('User not found', 'error');
        return;
    }
    
    // Populate edit form
    document.getElementById('editUserId').value = user.id;
    document.getElementById('editUsername').value = user.username;
    document.getElementById('editRole').value = user.role;
    document.getElementById('editActive').checked = user.active;
    
    // Show modal
    document.getElementById('editModal').style.display = 'block';
}

// Handle edit user
async function handleEditUser(event) {
    event.preventDefault();
    
    const formData = new FormData(event.target);
    const userId = document.getElementById('editUserId').value;
    const userData = {
        username: formData.get('username'),
        role: formData.get('role'),
        active: formData.get('active') === 'on'
    };
    
    try {
        showLoading(true);
        const response = await makeApiRequest(`${API_BASE_URL}/users/${userId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(userData)
        });
        
        const data = await response.json();
        
        if (response.ok && data.success) {
            showMessage('User updated successfully!', 'success');
            closeModal();
            loadUsers();
        } else {
            throw new Error(data.message || 'Failed to update user');
        }
    } catch (error) {
        console.error('Error updating user:', error);
        showMessage('Failed to update user: ' + error.message, 'error');
    } finally {
        showLoading(false);
    }
}

// Open password reset modal
function openPasswordModal(userId) {
    const user = allUsers.find(u => u.id === userId);
    if (!user) {
        showMessage('User not found', 'error');
        return;
    }
    
    document.getElementById('resetUserId').value = user.id;
    document.getElementById('passwordModal').style.display = 'block';
}

// Handle password reset
async function handleResetPassword(event) {
    event.preventDefault();
    
    const userId = document.getElementById('resetUserId').value;
    const newPassword = document.getElementById('newPassword').value;
    
    try {
        showLoading(true);
        const response = await makeApiRequest(`${API_BASE_URL}/users/${userId}/reset-password`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ newPassword })
        });
        
        const data = await response.json();
        
        if (response.ok && data.success) {
            showMessage('Password reset successfully!', 'success');
            closePasswordModal();
            document.getElementById('resetPasswordForm').reset();
        } else {
            throw new Error(data.message || 'Failed to reset password');
        }
    } catch (error) {
        console.error('Error resetting password:', error);
        showMessage('Failed to reset password: ' + error.message, 'error');
    } finally {
        showLoading(false);
    }
}

// Modal functions
function closeModal() {
    document.getElementById('editModal').style.display = 'none';
    document.getElementById('passwordModal').style.display = 'none';
}

function closePasswordModal() {
    document.getElementById('passwordModal').style.display = 'none';
}

// Utility functions
function showMessage(message, type = 'info') {
    console.log(`Message (${type}):`, message);
    
    // Remove existing messages
    const existingMessages = document.querySelectorAll('.message-toast');
    existingMessages.forEach(msg => msg.remove());

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
        background: ${type === 'success' ? '#00b894' : type === 'error' ? '#e74c3c' : '#3498db'};
    `;

    document.body.appendChild(messageDiv);

    // Auto remove after 5 seconds
    setTimeout(() => {
        if (messageDiv && messageDiv.parentNode) {
            messageDiv.remove();
        }
    }, 5000);
}

function showLoading(show) {
    const loadingOverlay = document.getElementById('loadingOverlay');
    if (loadingOverlay) {
        loadingOverlay.style.display = show ? 'flex' : 'none';
    }
}

function formatDate(dateString) {
    if (!dateString) return '—';
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', {
        month: 'short',
        day: 'numeric',
        year: 'numeric'
    });
}

function getRoleBadge(role) {
    const roleNames = {
        'ADMIN': 'Admin',
        'STOCK_MANAGER': 'Stock Manager',
        'SALES_STAFF': 'Sales Staff',
        'HR_STAFF': 'HR Staff',
        'MARKETING_MANAGER': 'Marketing Manager'
    };
    return `<span class="badge badge-role">${roleNames[role] || role}</span>`;
}

function getStatusBadge(active) {
    return active 
        ? '<span class="badge badge-status active">Active</span>' 
        : '<span class="badge badge-status inactive">Inactive</span>';
}

function setButtonLoading(button, loading) {
    if (!button) return;
    
    if (loading) {
        button.disabled = true;
        button.dataset.originalText = button.innerHTML;
        button.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Loading...';
    } else {
        button.disabled = false;
        button.innerHTML = button.dataset.originalText || 'Submit';
    }
}

function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}

// Add CSS animations
const style = document.createElement('style');
style.textContent = `
    @keyframes slideInRight {
        from { transform: translateX(100%); opacity: 0; }
        to { transform: translateX(0); opacity: 1; }
    }
    
    .message-toast {
        font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
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
        border-radius: 4px;
    }
    
    .message-close:hover {
        background: rgba(255, 255, 255, 0.2);
    }
`;
document.head.appendChild(style);

console.log('User management JavaScript loaded successfully - no authentication redirects');