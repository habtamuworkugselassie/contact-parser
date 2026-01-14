import React from 'react';
import './ContactCard.css';

const ContactCard = ({ contact }) => {
  return (
    <div className="contact-card">
      <div className="contact-header">
        <div className="contact-name">
          {contact.name || 'N/A'} {contact.lastName || ''}
        </div>
        <div className="contact-id">ID: {contact.id || 'N/A'}</div>
      </div>

      <div className="contact-details">
        {contact.name && (
          <div className="contact-detail">
            <strong>Name:</strong> {contact.name}
          </div>
        )}
        {contact.lastName && (
          <div className="contact-detail">
            <strong>Last Name:</strong> {contact.lastName}
          </div>
        )}
      </div>

      {contact.contacts && contact.contacts.length > 0 && (
        <div className="sub-contacts">
          <h3>Sub-contacts ({contact.contacts.length})</h3>
          {contact.contacts.map((subContact, index) => (
            <div key={subContact.id || index} className="sub-contact">
              <ContactCard contact={subContact} />
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default ContactCard;
