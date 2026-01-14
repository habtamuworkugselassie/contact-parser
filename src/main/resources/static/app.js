document.addEventListener('DOMContentLoaded', () => {
    const filePathInput = document.getElementById('filePath');
    const parseBtn = document.getElementById('parseBtn');
    const fileInput = document.getElementById('fileInput');
    const uploadBtn = document.getElementById('uploadBtn');
    const xmlContent = document.getElementById('xmlContent');
    const parseContentBtn = document.getElementById('parseContentBtn');
    const tabBtns = document.querySelectorAll('.tab-btn');
    const tabContents = document.querySelectorAll('.tab-content');
    const loading = document.getElementById('loading');
    const error = document.getElementById('error');
    const results = document.getElementById('results');
    const contactList = document.getElementById('contactList');
    const contactCount = document.getElementById('contactCount');

    tabBtns.forEach(btn => {
        btn.addEventListener('click', () => {
            const targetTab = btn.getAttribute('data-tab');
            
            tabBtns.forEach(b => b.classList.remove('active'));
            tabContents.forEach(c => c.classList.remove('active'));
            
            btn.classList.add('active');
            document.getElementById(`${targetTab}-tab`).classList.add('active');
        });
    });

    parseBtn.addEventListener('click', async () => {
        const filePath = filePathInput.value.trim();

        if (!filePath) {
            showError('Please enter a file path');
            return;
        }

        await parseFromPath(filePath);
    });

    filePathInput.addEventListener('keypress', (e) => {
        if (e.key === 'Enter') {
            parseBtn.click();
        }
    });

    uploadBtn.addEventListener('click', async () => {
        const file = fileInput.files[0];

        if (!file) {
            showError('Please select a file');
            return;
        }

        await parseFromUpload(file);
    });

    parseContentBtn.addEventListener('click', async () => {
        const content = xmlContent.value.trim();

        if (!content) {
            showError('Please paste XML content');
            return;
        }

        await parseFromContent(content);
    });

    async function parseFromPath(filePath) {
        hideError();
        hideResults();
        showLoading();

        try {
            const response = await fetch('/api/parse', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ filePath: filePath })
            });

            const data = await response.json();
            handleResponse(data);
        } catch (err) {
            hideLoading();
            showError('Network error: ' + err.message);
        }
    }

    async function parseFromUpload(file) {
        hideError();
        hideResults();
        showLoading();

        try {
            const formData = new FormData();
            formData.append('file', file);

            const response = await fetch('/api/parse/upload', {
                method: 'POST',
                body: formData
            });

            const data = await response.json();
            handleResponse(data);
        } catch (err) {
            hideLoading();
            showError('Network error: ' + err.message);
        }
    }

    async function parseFromContent(content) {
        hideError();
        hideResults();
        showLoading();

        try {
            const response = await fetch('/api/parse', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ xmlContent: content })
            });

            const data = await response.json();
            handleResponse(data);
        } catch (err) {
            hideLoading();
            showError('Network error: ' + err.message);
        }
    }

    function handleResponse(data) {
        hideLoading();

        if (data.success) {
            displayContacts(data.contacts);
            contactCount.textContent = data.count;
            showResults();
        } else {
            showError(data.error || 'Failed to parse XML');
        }
    }

    function showLoading() {
        loading.classList.remove('hidden');
    }

    function hideLoading() {
        loading.classList.add('hidden');
    }

    function showError(message) {
        error.textContent = 'Error: ' + message;
        error.classList.remove('hidden');
    }

    function hideError() {
        error.classList.add('hidden');
    }

    function showResults() {
        results.classList.remove('hidden');
    }

    function hideResults() {
        results.classList.add('hidden');
    }

    function displayContacts(contacts) {
        contactList.innerHTML = '';
        contacts.forEach(contact => {
            contactList.appendChild(createContactCard(contact));
        });
    }

    function createContactCard(contact) {
        const card = document.createElement('div');
        card.className = 'contact-card';

        const header = document.createElement('div');
        header.className = 'contact-header';

        const nameDiv = document.createElement('div');
        nameDiv.className = 'contact-name';
        nameDiv.textContent = `${contact.name || 'N/A'} ${contact.lastName || ''}`.trim();

        const idDiv = document.createElement('div');
        idDiv.className = 'contact-id';
        idDiv.textContent = `ID: ${contact.id || 'N/A'}`;

        header.appendChild(nameDiv);
        header.appendChild(idDiv);

        const details = document.createElement('div');
        details.className = 'contact-details';

        if (contact.name) {
            const nameDetail = document.createElement('div');
            nameDetail.className = 'contact-detail';
            nameDetail.innerHTML = `<strong>Name:</strong> ${contact.name}`;
            details.appendChild(nameDetail);
        }

        if (contact.lastName) {
            const lastNameDetail = document.createElement('div');
            lastNameDetail.className = 'contact-detail';
            lastNameDetail.innerHTML = `<strong>Last Name:</strong> ${contact.lastName}`;
            details.appendChild(lastNameDetail);
        }

        card.appendChild(header);
        card.appendChild(details);

        if (contact.contacts && contact.contacts.length > 0) {
            const subContacts = document.createElement('div');
            subContacts.className = 'sub-contacts';

            const subTitle = document.createElement('h3');
            subTitle.textContent = `Sub-contacts (${contact.contacts.length})`;
            subContacts.appendChild(subTitle);

            contact.contacts.forEach(subContact => {
                const subCard = createContactCard(subContact);
                subCard.classList.remove('contact-card');
                subCard.classList.add('sub-contact');
                subContacts.appendChild(subCard);
            });

            card.appendChild(subContacts);
        }

        return card;
    }
});
