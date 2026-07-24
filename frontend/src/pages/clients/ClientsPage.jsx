import { useEffect, useState } from 'react';
import api from '../../services/api.js';
import ClientsForm from './ClientsForm.jsx';
import './ClientsPage.css';

function ClientsPage() {
  const [Clients, setClients] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const [selectedClient, setSelectedClient] = useState(null);

  const [contactForm, setContactForm] = useState({ email: '', phone: '' });
  const [allergyInput, setAllergyInput] = useState('');
  const [notification, setNotification] = useState(null);

  function showNotification(message) {
    setNotification(message);
    setTimeout(() => setNotification(null), 5000); // Some depois de 3 segundos
  }

  useEffect(() => {
    fetchClients();
  }, []);

  async function fetchClients() {
    setLoading(true);
    try {
      const response = await api.get('/api/clients');
      setClients(response.data);
    } catch (err) {
      setError('Não foi possível carregar os clientes. Cheque se o banco de dados está rodando.');
    } finally {
      setLoading(false);
    }
  }

  function handleClientCreated(newClient) {
    setClients((CurrentClients) => [...CurrentClients, newClient]);
  }

  async function handleEditClick(clientId) {
    try {
      const response = await api.get(`/api/clients/${clientId}/profile`);
      setSelectedClient(response.data);
      
      setContactForm({
        email: response.data.email || '',
        phone: response.data.phoneNumber || ''
      });
    } catch (err) {
      showNotification("Erro ao carregar detalhes do cliente.");
    }
  }

  function handleBackClick() {
    setSelectedClient(null);
    setNotification(null);
    listAll();
  }

  async function handleCreateAnamnesis() {
    try {
      await api.post(`/api/clients/${selectedClient.clientId}/anamnesis`);
      
      handleEditClick(selectedClient.clientId);
    } catch (err) {
      alert("Erro ao criar a ficha de anamnese.");
    }
  }

  async function handleUpdateContact(e) {
    e.preventDefault();
    try {
      await api.put(`/api/clients/${selectedClient.clientId}/contact`, {
        email: contactForm.email,
        phone: contactForm.phone
      });
      showNotification("Contato atualizado com sucesso!");
      handleEditClick(selectedClient.clientId);
    } catch (err) {
      showNotification("Erro ao atualizar contato. Verifique se o email já existe.");
    }
  }

  async function handleAddAllergy(e) {
    e.preventDefault();
    if (!allergyInput.trim()) return;
    
    try {
      await api.put(`/api/clients/${selectedClient.clientId}/anamnesis/allergy`, {
        allergy: allergyInput
      });
      setAllergyInput('');
      showNotification("Alergia adicionada!");
      handleEditClick(selectedClient.clientId);
    } catch (err) {
      showNotification("Erro ao adicionar alergia.");
    }
  }


  /* =========================
  RETORNO (o componente em si) 
  ========================== */

  if(selectedClient){
    return(
      <div className="page-container">
        <button onClick={handleBackClick} className="back-button">
          &larr; Voltar para Lista
        </button>
        
        <div className="client-details-card">
          <h2>Perfil do Cliente</h2>
          
          <div className="details-section">
            <h3>Informações Básicas</h3>
            <p><strong>Nome:</strong> {selectedClient.fullName}</p>
            <p><strong>Telefone:</strong> {selectedClient.phoneNumber}</p>
            {/* We will build out forms to update these later! */}
            <p><strong>Email:</strong> {selectedClient.email || "Não informado"}</p>
            <p><strong>Data de Nascimento:</strong> {selectedClient.dateOfBirth || "Não informada"}</p>

            <form onSubmit={handleUpdateContact} className="edit-inline-form">
              <div>
                <label>Telefone:</label>
                <input 
                  type="text" 
                  value={contactForm.phone} 
                  onChange={(e) => setContactForm({...contactForm, phone: e.target.value})}
                  required
                />
              </div>
              <div>
                <label>Email:</label>
                <input 
                  type="email" 
                  value={contactForm.email} 
                  onChange={(e) => setContactForm({...contactForm, email: e.target.value})}
                />
              </div>
              <button type="submit" className="save-btn">Salvar Contato</button>
            </form>

          </div>

          <div className="details-section">
            <h3>Ficha de Anamnese</h3>
            
            {/* checagem se o cliente já tem ficha cadastrada */}
            {selectedClient.anamnesisRecord ? (
              <div>
                <p>✅ Ficha ativa.</p>
                <p><strong>Cabelo: </strong> {selectedClient.anamnesisRecord.hairProfile || "A análise do cabelo ainda não foi realizada."}</p>
                <p><strong>Pele:</strong> {selectedClient.anamnesisRecord.skinType || "A análise da pele ainda não foi realizada."}</p>
                <div className=''>
                  <p><strong>Alergias:</strong> {
                    selectedClient.anamnesisRecord.allergies && selectedClient.anamnesisRecord.allergies.length > 0 
                      ? selectedClient.anamnesisRecord.allergies.join(", ") 
                      : "Nenhuma alergia registrada."
                  }</p>
                    <form onSubmit={handleAddAllergy} className="add-allergy-form">
                      <input 
                        type="text" 
                        placeholder="Nova alergia..." 
                        value={allergyInput}
                        onChange={(e) => setAllergyInput(e.target.value)}
                      />
                      <button type="submit">Adicionar</button>
                    </form>
                </div>
                <p><strong>Anotações sobre o progresso:</strong> {selectedClient.anamnesisRecord.progressNotes || "Nenhuma mudança registrada."}</p>
              </div>
            ) : (
              <div className="no-record-warning">
                <p>Nenhuma ficha de anamnese cadastrada para este cliente.</p>
                <button onClick={handleCreateAnamnesis}>Criar Ficha em Branco</button>
              </div>
            )}
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="page-container">
      <h1>Clientes</h1>

      <ClientsForm onClientCreated={handleClientCreated} />

      {loading && <p>Carregando clientes...</p>}
      {error && <p className="form-error">{error}</p>}

      {!loading && !error && (
        <ul className="Client-lista">
          {Clients.length === 0 && <p>Nenhum cliente cadastrado ainda.</p>}
          {Clients.map((Client) => (
            <li key={Client.id} className="client-card">
              <strong>{Client.firstName} {Client.lastName}
              </strong>
              <span>{Client.phoneNumber}</span>
              <button 
                onClick={() => handleEditClick(Client.id)}
                style={{ padding: '8px 16px', cursor: 'pointer' }}
              >
                Editar
              </button>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}

export default ClientsPage;