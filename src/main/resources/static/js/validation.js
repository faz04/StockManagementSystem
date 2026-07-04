// Advanced form validation JavaScript

// Real-time password validation
function setupPasswordValidation() {
    const passwordInputs = document.querySelectorAll('input[type="password"]');

    passwordInputs.forEach(input => {
        const indicator = createPasswordStrengthIndicator(input);

        input.addEventListener('input', function() {
            updatePasswordStrengthIndicator(input, indicator);
        });
    });
}

// Create password strength indicator
function createPasswordStrengthIndicator(input) {
    const indicator = document.createElement('div');
    indicator.className = 'password-strength-indicator';
    indicator.innerHTML = `
        <div class="password-requirements">
            <div class="requirement" data-requirement="length">
                <i class="fas fa-times"></i> At least 8 characters
            </div>
            <div class="requirement" data-requirement="uppercase">
                <i class="fas fa-times"></i> One uppercase letter
            </div>
            <div class="requirement" data-requirement="lowercase">
                <i class="fas fa-times"></i> One lowercase letter
            </div>
            <div class="requirement" data-requirement="number">
                <i class="fas fa-times"></i> One number
            </div>
            <div class="requirement" data-requirement="special">
                <i class="fas fa-times"></i> One special character
            </div>
        </div>
        <div class="strength-bar">
            <div class="strength-fill"></div>
        </div>
    `;

    input.parentNode.insertBefore(indicator, input.nextSibling);
    return indicator;
}

// Update password strength indicator
function updatePasswordStrengthIndicator(input, indicator) {
    const password = input.value;
    const requirements = {
        length: password.length >= 8,
        uppercase: /[A-Z]/.test(password),
        lowercase: /[a-z]/.test(password),
        number: /\d/.test(password),
        special: /[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]/.test(password)
    };

    let strength = 0;

    Object.keys(requirements).forEach(req => {
        const element = indicator.querySelector(`[data-requirement="${req}"]`);
        const icon = element.querySelector('i');

        if (requirements[req]) {
            element.classList.add('met');
            element.classList.remove('unmet');
            icon.className = 'fas fa-check';
            strength++;
        } else {
            element.classList.add('unmet');
            element.classList.remove('met');
            icon.className = 'fas fa-times';
        }
    });

    // Update strength bar
    const strengthFill = indicator.querySelector('.strength-fill');
    const strengthPercent = (strength / 5) * 100;
    strengthFill.style.width = strengthPercent + '%';

    // Update strength bar color
    if (strength <= 2) {
        strengthFill.className = 'strength-fill weak';
    } else if (strength <= 4) {
        strengthFill.className = 'strength-fill medium';
    } else {
        strengthFill.className = 'strength-fill strong';
    }
}

// Username availability checker
async function checkUsernameAvailability(username, currentUserId = null) {
    if (!username || username.length < 3) return { available: false, message: 'Username too short' };

    try {
        const response = await fetch(`${API_BASE_URL}/users/username/${username}`);
        if (response.status === 404) {
            return { available: true, message: 'Username available' };
        } else if (response.ok) {
            const data = await response.json();
            if (data.success && data.data.id != currentUserId) {
                return { available: false, message: 'Username already taken' };
            }
            return { available: true, message: 'Username available' };
        }
    } catch (error) {
        console.error('Error checking username availability:', error);
    }

    return { available: false, message: 'Unable to check availability' };
}

// Setup username validation
function setupUsernameValidation() {
    const usernameInputs = document.querySelectorAll('input[name="username"]');

    usernameInputs.forEach(input => {
        let timeout;
        const indicator = createUsernameIndicator(input);

        input.addEventListener('input', function() {
            clearTimeout(timeout);
            timeout = setTimeout(async () => {
                const currentUserId = document.getElementById('editUserId')?.value;
                const result = await checkUsernameAvailability(input.value, currentUserId);
                updateUsernameIndicator(indicator, result);
            }, 500);
        });
    });
}

// Create username availability indicator
function createUsernameIndicator(input) {
    const indicator = document.createElement('div');
    indicator.className = 'username-indicator';
    input.parentNode.insertBefore(indicator, input.nextSibling);
    return indicator;
}

// Update username indicator
function updateUsernameIndicator(indicator, result) {
    const iconClass = result.available ? 'fa-check' : 'fa-times';
    const statusClass = result.available ? 'available' : 'unavailable';

    indicator.innerHTML = `
        <div class="username-status ${statusClass}">
            <i class="fas ${iconClass}"></i>
            ${result.message}
        </div>
    `;
}

// Initialize validation on page load
document.addEventListener('DOMContentLoaded', function() {
    setupPasswordValidation();
    setupUsernameValidation();
});