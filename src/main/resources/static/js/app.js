/**
 * VitalScale - BMI Calculator & MySQL Persistence Engine
 * Client-side Controller & Dynamic Visualizations
 */

document.addEventListener('DOMContentLoaded', () => {
    // State management
    const state = {
        unit: 'metric', // 'metric' or 'imperial'
        history: [],
        dbConnected: false
    };

    // DOM Elements
    const bmiForm = document.getElementById('bmiForm');
    const nameInput = document.getElementById('nameInput');
    const ageInput = document.getElementById('ageInput');
    const heightInput = document.getElementById('heightInput');
    const heightSlider = document.getElementById('heightSlider');
    const weightInput = document.getElementById('weightInput');
    const weightSlider = document.getElementById('weightSlider');

    // Imperial inputs
    const heightFeetGroup = document.getElementById('heightFeetGroup');
    const heightMetricGroup = document.getElementById('heightMetricGroup');
    const heightFtInput = document.getElementById('heightFtInput');
    const heightInInput = document.getElementById('heightInInput');

    const heightUnitLabel = document.getElementById('heightUnitLabel');
    const weightUnitLabel = document.getElementById('weightUnitLabel');

    // Unit toggle buttons
    const tabMetric = document.getElementById('tabMetric');
    const tabImperial = document.getElementById('tabImperial');

    // Result display elements
    const emptyState = document.getElementById('emptyState');
    const resultsDisplay = document.getElementById('resultsDisplay');
    const bmiNumber = document.getElementById('bmiNumber');
    const categoryBadge = document.getElementById('categoryBadge');
    const gaugeNeedle = document.getElementById('gaugeNeedle');
    const minWeightValue = document.getElementById('minWeightValue');
    const maxWeightValue = document.getElementById('maxWeightValue');
    const weightDiffValue = document.getElementById('weightDiffValue');
    const primeCategoryValue = document.getElementById('primeCategoryValue');
    const adviceText = document.getElementById('adviceText');
    const dbAlert = document.getElementById('dbAlert');
    const dbAlertText = document.getElementById('dbAlertText');

    // History elements
    const historyTableBody = document.getElementById('historyTableBody');
    const historyCount = document.getElementById('historyCount');
    const searchHistory = document.getElementById('searchHistory');
    const exportCsvBtn = document.getElementById('exportCsvBtn');
    const refreshHistoryBtn = document.getElementById('refreshHistoryBtn');

    // Status & Modal
    const dbStatusPill = document.getElementById('dbStatusPill');
    const dbStatusDot = document.getElementById('dbStatusDot');
    const dbStatusText = document.getElementById('dbStatusText');
    const dbModal = document.getElementById('dbModal');
    const closeModalBtn = document.getElementById('closeModalBtn');
    const retryDbBtn = document.getElementById('retryDbBtn');
    const themeToggleBtn = document.getElementById('themeToggleBtn');

    // Form button
    const submitBtn = document.getElementById('submitBtn');
    const btnText = document.getElementById('btnText');
    const btnSpinner = document.getElementById('btnSpinner');

    // ----------------------------------------------------
    // Theme Management
    // ----------------------------------------------------
    const savedTheme = localStorage.getItem('vitalscale_theme') || 'dark';
    document.documentElement.setAttribute('data-theme', savedTheme);
    updateThemeIcon(savedTheme);

    themeToggleBtn.addEventListener('click', () => {
        const currentTheme = document.documentElement.getAttribute('data-theme');
        const nextTheme = currentTheme === 'dark' ? 'light' : 'dark';
        document.documentElement.setAttribute('data-theme', nextTheme);
        localStorage.setItem('vitalscale_theme', nextTheme);
        updateThemeIcon(nextTheme);
    });

    function updateThemeIcon(theme) {
        themeToggleBtn.innerHTML = theme === 'dark' ? '☀️' : '🌙';
    }

    // ----------------------------------------------------
    // Unit Switcher Logic
    // ----------------------------------------------------
    tabMetric.addEventListener('click', () => switchUnit('metric'));
    tabImperial.addEventListener('click', () => switchUnit('imperial'));

    function switchUnit(unit) {
        if (state.unit === unit) return;
        state.unit = unit;

        if (unit === 'metric') {
            tabMetric.classList.add('active');
            tabImperial.classList.remove('active');
            heightMetricGroup.style.display = 'block';
            heightFeetGroup.style.display = 'none';

            heightUnitLabel.textContent = '(cm)';
            weightUnitLabel.textContent = '(kg)';

            // Convert current values to metric
            const ft = parseFloat(heightFtInput.value) || 5;
            const inches = parseFloat(heightInInput.value) || 9;
            const totalInches = (ft * 12) + inches;
            const cm = Math.round(totalInches * 2.54);
            heightInput.value = cm;
            heightSlider.value = cm;

            const lbs = parseFloat(weightInput.value) || 154;
            const kg = Math.round(lbs * 0.453592);
            weightInput.value = kg;
            weightSlider.value = kg;
            weightSlider.min = 20;
            weightSlider.max = 250;
        } else {
            tabImperial.classList.add('active');
            tabMetric.classList.remove('active');
            heightMetricGroup.style.display = 'none';
            heightFeetGroup.style.display = 'grid';

            heightUnitLabel.textContent = '(ft / in)';
            weightUnitLabel.textContent = '(lbs)';

            // Convert current metric values to imperial
            const cm = parseFloat(heightInput.value) || 175;
            const totalInches = cm / 2.54;
            const ft = Math.floor(totalInches / 12);
            const inch = Math.round(totalInches % 12);
            heightFtInput.value = ft;
            heightInInput.value = inch;

            const kg = parseFloat(weightInput.value) || 70;
            const lbs = Math.round(kg / 0.453592);
            weightInput.value = lbs;
            weightSlider.value = lbs;
            weightSlider.min = 44;
            weightSlider.max = 550;
        }
    }

    // Slider & Input synchronization (Metric)
    heightSlider.addEventListener('input', (e) => {
        heightInput.value = e.target.value;
    });

    heightInput.addEventListener('input', (e) => {
        heightSlider.value = e.target.value;
    });

    weightSlider.addEventListener('input', (e) => {
        weightInput.value = e.target.value;
    });

    weightInput.addEventListener('input', (e) => {
        weightSlider.value = e.target.value;
    });

    // Quick age chip buttons
    document.querySelectorAll('.chip-btn').forEach(btn => {
        btn.addEventListener('click', () => {
            ageInput.value = btn.dataset.age;
        });
    });

    // ----------------------------------------------------
    // Database Health Status
    // ----------------------------------------------------
    async function checkDbStatus() {
        try {
            const res = await fetch('/api/bmi/status');
            if (res.ok) {
                const data = await res.json();
                state.dbConnected = data.databaseConnected;
                if (data.databaseConnected) {
                    dbStatusDot.className = 'status-dot online';
                    dbStatusText.textContent = 'MySQL Connected';
                } else {
                    dbStatusDot.className = 'status-dot offline';
                    dbStatusText.textContent = 'MySQL Offline';
                }
            }
        } catch (err) {
            dbStatusDot.className = 'status-dot offline';
            dbStatusText.textContent = 'MySQL Offline';
            state.dbConnected = false;
        }
    }

    dbStatusPill.addEventListener('click', () => {
        dbModal.classList.add('active');
    });

    closeModalBtn.addEventListener('click', () => {
        dbModal.classList.remove('active');
    });

    dbModal.addEventListener('click', (e) => {
        if (e.target === dbModal) dbModal.classList.remove('active');
    });

    retryDbBtn.addEventListener('click', async () => {
        retryDbBtn.disabled = true;
        retryDbBtn.textContent = 'Checking...';
        await checkDbStatus();
        retryDbBtn.disabled = false;
        retryDbBtn.textContent = 'Re-test MySQL Connection';
        fetchHistory();
    });

    // ----------------------------------------------------
    // Form Submission & Calculation
    // ----------------------------------------------------
    bmiForm.addEventListener('submit', async (e) => {
        e.preventDefault();

        const name = nameInput.value.trim();
        const age = parseInt(ageInput.value);

        let heightVal, heightInchesVal = 0;
        if (state.unit === 'metric') {
            heightVal = parseFloat(heightInput.value);
        } else {
            heightVal = parseFloat(heightFtInput.value);
            heightInchesVal = parseFloat(heightInInput.value) || 0;
        }

        const weightVal = parseFloat(weightInput.value);

        if (!name || isNaN(age) || isNaN(heightVal) || isNaN(weightVal)) {
            alert('Please fill out all fields with valid numbers.');
            return;
        }

        const payload = {
            name: name,
            age: age,
            height: heightVal,
            heightUnit: state.unit === 'metric' ? 'cm' : 'ft_in',
            heightInches: heightInchesVal,
            weight: weightVal,
            weightUnit: state.unit === 'metric' ? 'kg' : 'lbs'
        };

        setLoading(true);

        try {
            const response = await fetch('/api/bmi/calculate', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });

            if (!response.ok) {
                const errData = await response.json();
                throw new Error(Object.values(errData).join(', ') || 'Calculation failed');
            }

            const data = await response.json();
            renderResults(data);
            fetchHistory(); // Refresh history table
            checkDbStatus(); // Update status indicator
        } catch (error) {
            console.error('Calculation error:', error);
            alert('Error calculating BMI: ' + error.message);
        } finally {
            setLoading(false);
        }
    });

    function setLoading(isLoading) {
        if (isLoading) {
            submitBtn.disabled = true;
            btnText.textContent = 'Calculating & Saving...';
            btnSpinner.style.display = 'inline-block';
        } else {
            submitBtn.disabled = false;
            btnText.textContent = 'Calculate & Save to MySQL';
            btnSpinner.style.display = 'none';
        }
    }

    // ----------------------------------------------------
    // Render Results & Animated Gauge
    // ----------------------------------------------------
    function renderResults(data) {
        emptyState.style.display = 'none';
        resultsDisplay.style.display = 'flex';

        // Animate counter
        animateCounter(bmiNumber, data.bmi);

        // Category Badge
        categoryBadge.textContent = data.category;
        categoryBadge.style.color = data.categoryColor;
        categoryBadge.style.backgroundColor = `${data.categoryColor}20`;
        categoryBadge.style.borderColor = data.categoryColor;
        categoryBadge.style.boxShadow = `0 0 20px ${data.categoryColor}40`;

        // Rotate Speedometer Gauge Needle
        // Angle range: -80deg (BMI <= 15) to +80deg (BMI >= 40)
        // Clamp BMI between 15 and 40
        const clampedBmi = Math.max(15, Math.min(40, data.bmi));
        const percentage = (clampedBmi - 15) / (40 - 15);
        const targetAngle = -80 + (percentage * 160);
        gaugeNeedle.style.transform = `rotate(${targetAngle}deg)`;

        // Metrics
        minWeightValue.textContent = `${data.minHealthyWeightKg} kg`;
        maxWeightValue.textContent = `${data.maxHealthyWeightKg} kg`;
        primeCategoryValue.textContent = getBmiRivRange(data.category);

        // Difference to normal weight
        if (data.weightKg < data.minHealthyWeightKg) {
            const diff = (data.minHealthyWeightKg - data.weightKg).toFixed(1);
            weightDiffValue.textContent = `+${diff} kg to healthy`;
            weightDiffValue.style.color = 'var(--bmi-underweight)';
        } else if (data.weightKg > data.maxHealthyWeightKg) {
            const diff = (data.weightKg - data.maxHealthyWeightKg).toFixed(1);
            weightDiffValue.textContent = `-${diff} kg to healthy`;
            weightDiffValue.style.color = 'var(--bmi-overweight)';
        } else {
            weightDiffValue.textContent = 'Optimal range ✓';
            weightDiffValue.style.color = 'var(--bmi-normal)';
        }

        // Advice
        adviceText.textContent = data.healthAdvice;

        // MySQL persistence alert
        if (data.savedToDatabase) {
            dbAlert.className = 'db-alert success';
            dbAlertText.innerHTML = `<strong>Saved in MySQL!</strong> Stored in table <code>bmi_records</code> (Record ID: #${data.id || 'N/A'}).`;
        } else {
            dbAlert.className = 'db-alert warning';
            dbAlertText.innerHTML = `<strong>MySQL Note:</strong> ${data.databaseStatusMessage}`;
        }
    }

    function getBmiRivRange(category) {
        switch (category) {
            case 'Underweight': return '< 18.5';
            case 'Normal weight': return '18.5 – 24.9';
            case 'Overweight': return '25.0 – 29.9';
            case 'Obesity Class I': return '30.0 – 34.9';
            case 'Obesity Class II': return '35.0 – 39.9';
            default: return '≥ 40.0';
        }
    }

    function animateCounter(element, target) {
        const start = 0;
        const duration = 1000;
        const startTime = performance.now();

        function update(currentTime) {
            const elapsed = currentTime - startTime;
            const progress = Math.min(elapsed / duration, 1);
            // Ease out cubic
            const ease = 1 - Math.pow(1 - progress, 3);
            const current = (start + (target - start) * ease).toFixed(1);
            element.textContent = current;

            if (progress < 1) {
                requestAnimationFrame(update);
            } else {
                element.textContent = target.toFixed(1);
            }
        }
        requestAnimationFrame(update);
    }

    // ----------------------------------------------------
    // History Table Management
    // ----------------------------------------------------
    async function fetchHistory() {
        try {
            const res = await fetch('/api/bmi/history');
            if (res.ok) {
                state.history = await res.json();
                renderHistoryTable(state.history);
            }
        } catch (err) {
            console.error('Failed to load history:', err);
        }
    }

    function renderHistoryTable(records) {
        historyCount.textContent = `(${records.length})`;
        historyTableBody.innerHTML = '';

        if (!records || records.length === 0) {
            historyTableBody.innerHTML = `
                <tr>
                    <td colspan="8" style="text-align: center; color: var(--text-dim); padding: 32px;">
                        No calculation records found. Calculate a BMI above to save records to MySQL.
                    </td>
                </tr>
            `;
            return;
        }

        records.forEach(rec => {
            const tr = document.createElement('tr');
            const dateStr = rec.calculatedAt ? new Date(rec.calculatedAt).toLocaleString() : 'Just now';
            const badgeColor = getCategoryColor(rec.category);

            tr.innerHTML = `
                <td>#${rec.id || '-'}</td>
                <td><strong>${escapeHtml(rec.name)}</strong></td>
                <td>${rec.age} yrs</td>
                <td>${rec.heightCm} cm</td>
                <td>${rec.weightKg} kg</td>
                <td><strong>${rec.bmi.toFixed(1)}</strong></td>
                <td>
                    <span class="table-badge" style="background: ${badgeColor}22; color: ${badgeColor}; border: 1px solid ${badgeColor}55;">
                        ${rec.category}
                    </span>
                </td>
                <td>
                    <button class="btn-delete" title="Delete record" data-id="${rec.id}">
                        🗑️
                    </button>
                </td>
            `;
            historyTableBody.appendChild(tr);
        });

        // Add event listeners to delete buttons
        document.querySelectorAll('.btn-delete').forEach(btn => {
            btn.addEventListener('click', async (e) => {
                const id = btn.dataset.id;
                if (!id) return;
                if (confirm(`Are you sure you want to delete record #${id}?`)) {
                    await deleteRecord(id);
                }
            });
        });
    }

    async function deleteRecord(id) {
        try {
            const res = await fetch(`/api/bmi/history/${id}`, { method: 'DELETE' });
            if (res.ok) {
                fetchHistory();
            } else {
                alert('Could not delete record from MySQL.');
            }
        } catch (err) {
            console.error('Delete error:', err);
            alert('Failed to connect to backend.');
        }
    }

    function getCategoryColor(category) {
        switch (category) {
            case 'Underweight': return '#38bdf8';
            case 'Normal weight': return '#22c55e';
            case 'Overweight': return '#f59e0b';
            case 'Obesity Class I': return '#f97316';
            case 'Obesity Class II': return '#ef4444';
            default: return '#b91c1c';
        }
    }

    function escapeHtml(str) {
        if (!str) return '';
        return str.replace(/[&<>'"]/g, 
            tag => ({
                '&': '&amp;',
                '<': '&lt;',
                '>': '&gt;',
                "'": '&#39;',
                '"': '&quot;'
            }[tag] || tag)
        );
    }

    // Search filter
    searchHistory.addEventListener('input', (e) => {
        const query = e.target.value.toLowerCase().trim();
        const filtered = state.history.filter(item => 
            item.name.toLowerCase().includes(query) ||
            item.category.toLowerCase().includes(query) ||
            String(item.age).includes(query)
        );
        renderHistoryTable(filtered);
    });

    refreshHistoryBtn.addEventListener('click', () => {
        fetchHistory();
        checkDbStatus();
    });

    // CSV Export
    exportCsvBtn.addEventListener('click', () => {
        if (!state.history || state.history.length === 0) {
            alert('No records available to export.');
            return;
        }

        const headers = ['ID', 'Name', 'Age', 'Height (cm)', 'Weight (kg)', 'BMI', 'Category', 'Calculated At'];
        const rows = state.history.map(r => [
            r.id,
            `"${r.name.replace(/"/g, '""')}"`,
            r.age,
            r.heightCm,
            r.weightKg,
            r.bmi,
            `"${r.category}"`,
            `"${r.calculatedAt}"`
        ]);

        const csvContent = 'data:text/csv;charset=utf-8,' 
            + [headers.join(','), ...rows.map(e => e.join(','))].join('\n');

        const encodedUri = encodeURI(csvContent);
        const link = document.createElement('a');
        link.setAttribute('href', encodedUri);
        link.setAttribute('download', `bmi_records_${new Date().toISOString().slice(0, 10)}.csv`);
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
    });

    // Initial load
    checkDbStatus();
    fetchHistory();
});
