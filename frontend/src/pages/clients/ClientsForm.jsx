import { useState } from 'react';
import api from '../../services/api.js';

function ClientsForm ({ onClientCreated }) {
  const [firstName, setFirstName] = useState('');
  const [lastName, setLastName] = useState('');
  const [phoneNumber, setPhoneNumber] = useState('');

  const [sending, setSending] = useState(false);
  const [error, setError] = useState(null);

  async function handleSubmit(event) {
    event.preventDefault();
    setSending(true);
    setError(null);

    try {
      const response = await api.post('/api/clients', { firstName, lastName, phoneNumber });
      onClientCreated(response.data);
      setFirstName('');
      setLastName('');
      setPhoneNumber('');
    } catch (err) {
      setError('Não foi possível cadastrar o cliente. Verifique o backend.');
    } finally {
      setSending(false);
    }
  }

  return (
    <form onSubmit={handleSubmit} >
      <div className="client-form">
        <input
          type="text"
          placeholder="Nome do Cliente"
          value={firstName}
          onChange={(e) => setFirstName(e.target.value)}
          required
        />
        <input
          type="text"
          placeholder="Sobrenome do Cliente"
          value={lastName}
          onChange={(e) => setLastName(e.target.value)}
          required
        />
        <input
          type="tel"
          placeholder="Telefone"
          value={phoneNumber}
          onChange={(e) => setPhoneNumber(e.target.value)}
          required
        />
        <button type="submit" disabled={sending}>
          {sending ? 'Salvando...' : 'Cadastrar novo cliente'}
        </button>
      </div>
      {error && <p className="form-error">{error}</p>}
    </form>
  );
}

export default ClientsForm;