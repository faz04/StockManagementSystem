// Password Strength Meter Implementation

function initializePasswordStrengthMeter() {
    const passwordInputs = ['password', 'newPassword'];
    
    passwordInputs.forEach(inputId => {
        const input = document.getElementById(inputId);
        if (input) {
            input.addEventListener('input', function() {
                updatePasswordStrength(inputId);
            });
        }
    });
}

function updatePasswordStrength(inputId) {
    const input = document.getElementById(inputId);
    const password = input.value;
    
    let strengthBarId, strengthTextId, requirementPrefix;
    if (inputId === 'password') {
        strengthBarId = 'passwordStrengthBar';
        strengthTextId = 'passwordStrengthText';
        requirementPrefix = 'req-';
    } else if (inputId === 'newPassword') {
        strengthBarId = 'resetPasswordStrengthBar';
        strengthTextId = 'resetPasswordStrengthText';
        requirementPrefix = 'reset-req-';
    }
    
    const strengthBar = document.getElementById(strengthBarId);
    const strengthText = document.getElementById(strengthTextId);
    
    if (!strengthBar || !strengthText) return;
    
    const strength = calculatePasswordStrength(password);
    
    strengthBar.className = `password-strength-bar ${strength.level}`;
    strengthText.textContent = strength.text;
    strengthText.className = `password-strength-text ${strength.level}`;
    
    updatePasswordRequirements(password, requirementPrefix);
}

function calculatePasswordStrength(password) {
    if (!password) {
        return { level: '', text: 'Enter password to check strength', score: 0 };
    }
    
    let score = 0;
    const checks = {
        length: password.length >= 8,
        uppercase: /[A-Z]/.test(password),
        lowercase: /[a-z]/.test(password),
        number: /\d/.test(password),
        special: /[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]/.test(password)
    };
    
    Object.values(checks).forEach(check => {
        if (check) score++;
    });
    
    if (score < 2) {
        return { level: 'weak', text: 'Weak password', score };
    } else if (score < 3) {
        return { level: 'fair', text: 'Fair password', score };
    } else if (score < 5) {
        return { level: 'good', text: 'Good password', score };
    } else {
        return { level: 'strong', text: 'Strong password', score };
    }
}

function updatePasswordRequirements(password, prefix) {
    const requirements = [
        { id: `${prefix}length`, check: password.length >= 8 },
        { id: `${prefix}uppercase`, check: /[A-Z]/.test(password) },
        { id: `${prefix}lowercase`, check: /[a-z]/.test(password) },
        { id: `${prefix}number`, check: /\d/.test(password) },
        { id: `${prefix}special`, check: /[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]/.test(password) }
    ];
    
    requirements.forEach(req => {
        const element = document.getElementById(req.id);
        if (element) {
            if (req.check) {
                element.classList.add('met');
                element.querySelector('i').className = 'fas fa-check';
            } else {
                element.classList.remove('met');
                element.querySelector('i').className = 'fas fa-times';
            }
        }
    });
}
