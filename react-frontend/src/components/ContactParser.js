import React, { useState } from 'react';
import './ContactParser.css';
import ContactCard from './ContactCard';

const ContactParser = () => {
  const [activeTab, setActiveTab] = useState('filepath');
  const [filePath, setFilePath] = useState('');
  const [xmlContent, setXmlContent] = useState('');
  const [contacts, setContacts] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [selectedFile, setSelectedFile] = useState(null);

  const handleTabChange = (tab) => {
    setActiveTab(tab);
    setError('');
    setContacts([]);
  };

  const parseFromPath = async () => {
    if (!filePath.trim()) {
      setError('Please enter a file path');
      return;
    }

    setLoading(true);
    setError('');
    setContacts([]);

    try {
      const response = await fetch('/api/parse', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ filePath: filePath.trim() }),
      });

      const data = await response.json();
      handleResponse(data);
    } catch (err) {
      setLoading(false);
      setError('Network error: ' + err.message);
    }
  };

  const parseFromUpload = async () => {
    if (!selectedFile) {
      setError('Please select a file');
      return;
    }

    setLoading(true);
    setError('');
    setContacts([]);

    try {
      const formData = new FormData();
      formData.append('file', selectedFile);

      const response = await fetch('/api/parse/upload', {
        method: 'POST',
        body: formData,
      });

      const data = await response.json();
      handleResponse(data);
    } catch (err) {
      setLoading(false);
      setError('Network error: ' + err.message);
    }
  };

  const parseFromContent = async () => {
    if (!xmlContent.trim()) {
      setError('Please paste XML content');
      return;
    }

    setLoading(true);
    setError('');
    setContacts([]);

    try {
      const response = await fetch('/api/parse', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ xmlContent: xmlContent.trim() }),
      });

      const data = await response.json();
      handleResponse(data);
    } catch (err) {
      setLoading(false);
      setError('Network error: ' + err.message);
    }
  };

  const handleResponse = (data) => {
    setLoading(false);

    if (data.success) {
      setContacts(data.contacts || []);
      setError('');
    } else {
      setError(data.error || 'Failed to parse XML');
      setContacts([]);
    }
  };

  const handleFileChange = (e) => {
    const file = e.target.files[0];
    setSelectedFile(file);
  };

  return (
    <div className="container">
      <header>
        <h1>Contact XML Parser</h1>
        <p>Parse XML contact files using SAX parser - React Frontend</p>
      </header>

      <main>
        <div className="tabs">
          <button
            className={`tab-btn ${activeTab === 'filepath' ? 'active' : ''}`}
            onClick={() => handleTabChange('filepath')}
          >
            File Path
          </button>
          <button
            className={`tab-btn ${activeTab === 'upload' ? 'active' : ''}`}
            onClick={() => handleTabChange('upload')}
          >
            Upload File
          </button>
          <button
            className={`tab-btn ${activeTab === 'content' ? 'active' : ''}`}
            onClick={() => handleTabChange('content')}
          >
            Paste XML
          </button>
        </div>

        {activeTab === 'filepath' && (
          <div className="tab-content">
            <div className="input-section">
              <label htmlFor="filePath">XML File Path:</label>
              <input
                type="text"
                id="filePath"
                value={filePath}
                onChange={(e) => setFilePath(e.target.value)}
                onKeyPress={(e) => e.key === 'Enter' && parseFromPath()}
                placeholder="Enter path to XML file (e.g., src/main/resources/test-contacts.xml)"
              />
              <button onClick={parseFromPath}>Parse XML</button>
            </div>
          </div>
        )}

        {activeTab === 'upload' && (
          <div className="tab-content">
            <div className="input-section">
              <label htmlFor="fileInput">Select XML File:</label>
              <input
                type="file"
                id="fileInput"
                accept=".xml,text/xml"
                onChange={handleFileChange}
              />
              <button onClick={parseFromUpload}>Upload & Parse</button>
            </div>
          </div>
        )}

        {activeTab === 'content' && (
          <div className="tab-content">
            <div className="input-section">
              <label htmlFor="xmlContent">Paste XML Content:</label>
              <textarea
                id="xmlContent"
                rows="15"
                value={xmlContent}
                onChange={(e) => setXmlContent(e.target.value)}
                placeholder="Paste your XML content here..."
              />
              <button onClick={parseFromContent}>Parse XML</button>
            </div>
          </div>
        )}

        {loading && (
          <div className="loading">
            <div className="spinner"></div>
            <p>Parsing XML...</p>
          </div>
        )}

        {error && (
          <div className="error">
            Error: {error}
          </div>
        )}

        {contacts.length > 0 && (
          <div className="results">
            <h2>Parsed Contacts ({contacts.length})</h2>
            <div className="contact-list">
              {contacts.map((contact, index) => (
                <ContactCard key={contact.id || index} contact={contact} />
              ))}
            </div>
          </div>
        )}
      </main>
    </div>
  );
};

export default ContactParser;
