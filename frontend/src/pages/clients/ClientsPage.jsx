import { useEffect, useState } from 'react';
import api from '../../services/api.js';
import ClientsForm from './ClientsForm.jsx';
import './ClientsPage.css';

const SKIN_TYPE_BR = { DRY: 'Seca', OILY: 'Oleosa', COMBINATION: 'Mista', NORMAL: 'Normal', SENSITIVE: 'Sensível' };
const HAIR_SHAPE_BR = { STRAIGHT: 'Liso', WAVY: 'Ondulado', CURLY: 'Cacheado', COILY: 'Crespo' };
const HAIR_POROSITY_BR = { LOW: 'Baixa', NORMAL: 'Normal', HIGH: 'Alta' };
const HAIR_THICKNESS_BR = { FINE: 'Fino', MEDIUM: 'Médio', COARSE: 'Grosso' };
const HAIR_LENGTH_BR = { SHORT: 'Curto', MEDIUM: 'Médio', LONG: 'Longo', EXTRA_LONG: 'Extra Longo' };

function formatEnum(dict, value) {
  return value && dict[value] ? dict[value] : "Não informado";
}

function ClientsPage() {
  const currentUserRole = 'ADMIN'; 

  const [Clients, setClients] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const [selectedClient, setSelectedClient] = useState(null);
  
  const [isEditMode, setIsEditMode] = useState(false);

  const [profileForm, setProfileForm] = useState({
      firstName: '', 
      lastName: '', 
      email: '', 
      phoneNumber: '', 
      dateOfBirth: '' 
    });
  const [allergyInput, setAllergyInput] = useState('');
  const [anamnesisForm, setAnamnesisForm] = useState({
      skinType: '',
      hairShape: '',
      hairPorosity: '',
      hairThickness: '',
      hairLength: '',
      chemicallyTreated: false,
      damaged: false,
      progressNotes: ''
  });

  const [notification, setNotification] = useState(null);

  function showNotification(message) {
    setNotification(message);
    setTimeout(() => setNotification(null), 5000); 
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
      const data = response.data;
      setSelectedClient(response.data);
      
      setIsEditMode(false); 
      setProfileForm({
        firstName: response.data.firstName || '',
        lastName: response.data.lastName || '',
        email: response.data.email || '',
        phoneNumber: response.data.phoneNumber || '',
        dateOfBirth: response.data.dateOfBirth || '',
      });

      const record = data.anamnesisRecord;
      setAnamnesisForm({
        skinType: record?.skinType || '',
        hairShape: record?.hairProfile?.shape || '',
        hairPorosity: record?.hairProfile?.porosity || '',
        hairThickness: record?.hairProfile?.thickness || '',
        hairLength: record?.hairProfile?.length || '',
        chemicallyTreated: record?.hairProfile?.chemicallyTreated || false,
        damaged: record?.hairProfile?.damaged || false,
        progressNotes: record?.progressNotes || ''
      });
    } catch (err) {
      console.error("ERRO REAL AQUI:", err);
      showNotification("Erro ao carregar detalhes do cliente.");
    }

    
  }

  function handleBackClick() {
    setSelectedClient(null);
    setIsEditMode(false);
    setNotification(null);
    fetchClients();
  }

  // toggle de edição
  function toggleEditMode() {
    if (isEditMode) {
      setProfileForm({
        firstName: selectedClient.firstName || '',
        lastName: selectedClient.lastName || '',
        email: selectedClient.email || '',
        phoneNumber: selectedClient.phoneNumber || '',
        dateOfBirth: selectedClient.dateOfBirth || '',
      });
      const record = selectedClient.anamnesisRecord;
      setAnamnesisForm({
        skinType: record?.skinType || '',
        hairShape: record?.hairProfile?.shape || '',
        hairPorosity: record?.hairProfile?.porosity || '',
        hairThickness: record?.hairProfile?.thickness || '',
        hairLength: record?.hairProfile?.length || '',
        chemicallyTreated: record?.hairProfile?.chemicallyTreated || false,
        damaged: record?.hairProfile?.damaged || false,
        progressNotes: record?.progressNotes || ''
      });
      setAllergyInput('');
    }
    setIsEditMode(!isEditMode);
  }

  // dirty checking
  const isFirstNameDirty = selectedClient && profileForm.firstName !== (selectedClient.firstName || '');
  const isLastNameDirty = selectedClient && profileForm.lastName !== (selectedClient.lastName || '');
  const isPhoneNumberDirty = selectedClient && profileForm.phoneNumber !== (selectedClient.phoneNumber || '');
  const isEmailDirty = selectedClient && profileForm.email !== (selectedClient.email || '');
  const isdateOfBirthDirty = selectedClient && profileForm.dateOfBirth !== (selectedClient.dateOfBirth || '');
  const hasUnsavedChanges = isFirstNameDirty || isLastNameDirty || isPhoneNumberDirty || isEmailDirty || isdateOfBirthDirty;

  const isAnamnesisDirty = selectedClient && JSON.stringify(anamnesisForm) !== JSON.stringify({
      skinType: selectedClient.anamnesisRecord?.skinType || '',
      hairShape: selectedClient.anamnesisRecord?.hairProfile?.shape || '',
      hairPorosity: selectedClient.anamnesisRecord?.hairProfile?.porosity || '',
      hairThickness: selectedClient.anamnesisRecord?.hairProfile?.thickness || '',
      hairLength: selectedClient.anamnesisRecord?.hairProfile?.length || '',
      chemicallyTreated: selectedClient.anamnesisRecord?.hairProfile?.chemicallyTreated || false,
      damaged: selectedClient.anamnesisRecord?.hairProfile?.damaged || false,
      progressNotes: selectedClient.anamnesisRecord?.progressNotes || ''
  });

  async function handleCreateAnamnesis() {
    try {
      await api.post(`/api/clients/${selectedClient.clientId}/anamnesis`);
      handleEditClick(selectedClient.clientId);
    } catch (err) {
      alert("Erro ao criar a ficha de anamnese.");
    }
  }

  async function handleUpdateAnamnesis(e) {
    e.preventDefault();
    try {
      const payload = {
        ...anamnesisForm,
        skinType: anamnesisForm.skinType || null,
        hairShape: anamnesisForm.hairShape || null,
        hairPorosity: anamnesisForm.hairPorosity || null,
        hairThickness: anamnesisForm.hairThickness || null,
        hairLength: anamnesisForm.hairLength || null,
      };

      await api.put(`/api/clients/${selectedClient.clientId}/anamnesis`, payload);
      showNotification("Ficha de anamnese atualizada com sucesso!");
      handleEditClick(selectedClient.clientId);
    } catch (err) {
      showNotification("Erro ao atualizar ficha. Verifique o console.");
    }
  }

  async function handleUpdateProfile(e) {
    e.preventDefault();
    try {
      // FORMATANDO
      const payload = {
        firstName: profileForm.firstName,
        lastName: profileForm.lastName,
        email: profileForm.email.trim() === '' ? null : profileForm.email,
        phoneNumber: profileForm.phoneNumber,
        dateOfBirth: profileForm.dateOfBirth === '' ? null : profileForm.dateOfBirth
      };

      await api.put(`/api/clients/${selectedClient.clientId}/profile`, payload);
      
      showNotification("Perfil atualizado com sucesso!");
      handleEditClick(selectedClient.clientId);
    } catch (err) {
      showNotification("Erro ao atualizar. Verifique se o email já está em uso.");
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

  async function handleDeleteClient(clientId, name) {
    if (!window.confirm(`Deseja realmente desligar o cliente ${name}?`)) return;
    try {
      await api.delete(`/api/clients/${clientId}`);
      showNotification("Cliente removido com sucesso.");
      fetchClients();
    } catch (err) {
      showNotification("Erro ao remover cliente.");
    }
  }

  function formatDateBr(dateString) {
    if (!dateString) return "";
    const [year, month, day] = dateString.split('-');
    return `${day}/${month}/${year}`;
  }

  // =========================
  // TELA 1: DETALHES / EDIÇÃO (após o usuário apertar o botão na principak)
  // =========================

  if(selectedClient){
    return(
      <div className="page-container">
        <button onClick={handleBackClick} className="back-button">
          &larr; Voltar para Lista
        </button>
        
        {notification && <div className="notification-toast">{notification}</div>}

        <div className="client-details-card">
          
          <div className="detail-header">
            <h2>Perfil do Cliente</h2>
            
            {/* o switch só aparece se a role for ADMIN (por enquanto todo mundo é admin ent whatever) */}
            {(currentUserRole === 'ADMIN'|| 'SPECIALIST') && (
              <div className="edit-toggle-container">
                <span className="toggle-label">{isEditMode ? "Modo Edição" : "Modo Visualização"}</span>
                <label className="switch">
                  <input type="checkbox" checked={isEditMode} onChange={toggleEditMode} />
                  <span className="slider round"></span>
                </label>
              </div>
            )}
          </div>

          {isEditMode && (hasUnsavedChanges || isAnamnesisDirty) && (
            <div className="unsaved-warning">
              ⚠️ Você possui alterações de contato não salvas.
            </div>
          )}
          
          <div className="details-section">
            <h3>Informações Básicas</h3>

            {/* renderização condicional */}
            {!isEditMode ? (
              <div className="view-mode-info">
                <p><strong>Nome:</strong> {selectedClient.firstName} {selectedClient.lastName}</p>
                <p><strong>Data de Nascimento:</strong> {formatDateBr(selectedClient.dateOfBirth) || "Não informada"}</p>
                <p><strong>Telefone:</strong> {selectedClient.phoneNumber}</p>
                <p><strong>Email:</strong> {selectedClient.email || "Não informado"}</p>
              </div>
            ) : (
              <form onSubmit={handleUpdateProfile} className="edit-inline-form">
                <div>
                  <label>Nome:</label>
                  <input 
                    type="text" 
                    className={isFirstNameDirty ? 'input-dirty' : ''}
                    value={profileForm.firstName} 
                    onChange={(e) => setProfileForm({...profileForm, firstName: e.target.value})}
                    required
                  />
                </div>
                <div>
                  <label>Sobrenome:</label>
                  <input 
                    type="text" 
                    className={isLastNameDirty ? 'input-dirty' : ''}
                    value={profileForm.lastName} 
                    onChange={(e) => setProfileForm({...profileForm, lastName: e.target.value})}
                    required
                  />
                </div>
                <div>
                  <label>Data de nascimento:</label>
                  <input 
                    type="date" 
                    className={isdateOfBirthDirty ? 'input-dirty' : ''}
                    value={profileForm.dateOfBirth} 
                    onChange={(e) => setProfileForm({...profileForm, dateOfBirth: e.target.value})}
                  />
                </div>
                <div>
                  <label>Telefone:</label>
                  <input 
                    type="string" 
                    className={isPhoneNumberDirty ? 'input-dirty' : ''}
                    value={profileForm.phoneNumber} 
                    onChange={(e) => setProfileForm({...profileForm, phoneNumber: e.target.value})}
                    required
                  />
                </div>
                <div>
                  <label>Email:</label>
                  <input 
                    type="email" 
                    className={isEmailDirty ? 'input-dirty' : ''}
                    value={profileForm.email} 
                    onChange={(e) => setProfileForm({...profileForm, email: e.target.value})}
                  />
                </div>
                {/* só dá pra atualizar se o usuário tiver mudado algum campo */}
                <button type="submit" className="save-btn" disabled={!hasUnsavedChanges}>
                  Salvar Contato
                </button>
              </form>
            )}

          </div>

          <div className="details-section">
            <h3>Ficha de Anamnese</h3>
            {selectedClient.anamnesisRecord ? (
              <div>
                {isEditMode && <span className="status-badge active" style={{marginBottom: '15px'}}>Ficha ativa</span>}
                
                {!isEditMode ? (
                  <div className="view-mode-info">
                    <p><strong>Tipo de Pele:</strong> {formatEnum(SKIN_TYPE_BR, selectedClient.anamnesisRecord.skinType)}</p>
                    <p><strong>Cabelo (Curvatura):</strong> {formatEnum(HAIR_SHAPE_BR, selectedClient.anamnesisRecord.hairProfile?.shape)}</p>
                    <p><strong>Cabelo (Comprimento):</strong> {formatEnum(HAIR_LENGTH_BR, selectedClient.anamnesisRecord.hairProfile?.length)}</p>
                    <p><strong>Cabelo (Porosidade):</strong> {formatEnum(HAIR_POROSITY_BR, selectedClient.anamnesisRecord.hairProfile?.porosity)}</p>
                    <p><strong>Cabelo (Espessura):</strong> {formatEnum(HAIR_THICKNESS_BR, selectedClient.anamnesisRecord.hairProfile?.thickness)}</p>
                    <p><strong>Danificado:</strong> {selectedClient.anamnesisRecord.hairProfile?.damaged ? "Sim" : "Não"}</p>
                    <p><strong>Tratado Quimicamente:</strong> {selectedClient.anamnesisRecord.hairProfile?.chemicallyTreated ? "Sim" : "Não"}</p>
                    <p><strong>Anotações:</strong> {selectedClient.anamnesisRecord.progressNotes || "Nenhuma anotação."}</p>
                  </div>
                ) : (
                  <form onSubmit={handleUpdateAnamnesis} className="edit-inline-form anamnesis-form" style={{ marginBottom: '20px', paddingBottom: '20px', borderBottom: '1px solid #eee' }}>
                    <div>

                      <div>
                        <label>Tipo de Pele:</label>
                        <select value={anamnesisForm.skinType} onChange={(e) => setAnamnesisForm({...anamnesisForm, skinType: e.target.value})}>
                          <option value="">Selecione...</option>
                          {Object.entries(SKIN_TYPE_BR).map(([key, label]) => <option key={key} value={key}>{label}</option>)}
                        </select>
                      </div>
                      <div>
                        <label>Curvatura do Cabelo:</label>
                        <select value={anamnesisForm.hairShape} onChange={(e) => setAnamnesisForm({...anamnesisForm, hairShape: e.target.value})}>
                          <option value="">Selecione...</option>
                          {Object.entries(HAIR_SHAPE_BR).map(([key, label]) => <option key={key} value={key}>{label}</option>)}
                        </select>
                      </div>
                      <div>
                        <label>Comprimento:</label>
                        <select value={anamnesisForm.hairLength} onChange={(e) => setAnamnesisForm({...anamnesisForm, hairLength: e.target.value})}>
                          <option value="">Selecione...</option>
                          {Object.entries(HAIR_LENGTH_BR).map(([key, label]) => <option key={key} value={key}>{label}</option>)}
                        </select>
                      </div>
                      <div>
                        <label>Porosidade:</label>
                        <select value={anamnesisForm.hairPorosity} onChange={(e) => setAnamnesisForm({...anamnesisForm, hairPorosity: e.target.value})}>
                          <option value="">Selecione...</option>
                          {Object.entries(HAIR_POROSITY_BR).map(([key, label]) => <option key={key} value={key}>{label}</option>)}
                        </select>
                      </div>
                      <div>
                        <label>Espessura:</label>
                        <select value={anamnesisForm.hairThickness} onChange={(e) => setAnamnesisForm({...anamnesisForm, hairThickness: e.target.value})}>
                          <option value="">Selecione...</option>
                          {Object.entries(HAIR_THICKNESS_BR).map(([key, label]) => <option key={key} value={key}>{label}</option>)}
                        </select>
                      </div>
                      
                    </div>
                    <div>
                      <div style={{ display: 'flex', gap: '20px', alignItems: 'center', flexDirection:'row' }}>
                        <label style={{ display: 'flex', alignItems: 'center', gap: '5px', width: 'auto' }}>
                          <input type="checkbox" checked={anamnesisForm.damaged} onChange={(e) => setAnamnesisForm({...anamnesisForm, damaged: e.target.checked})} style={{ width: 'auto' }} />
                          Cabelo Danificado
                        </label>
                        <label style={{ display: 'flex', alignItems: 'center', gap: '5px', width: 'auto' }}>
                          <input type="checkbox" checked={anamnesisForm.chemicallyTreated} onChange={(e) => setAnamnesisForm({...anamnesisForm, chemicallyTreated: e.target.checked})} style={{ width: 'auto' }} />
                          Tratado Quimicamente
                        </label>
                      </div>
                      <div style={{ width: '100%', marginTop: '10px' }}>
                        <label>Anotações sobre progresso:</label>
                        <textarea 
                          value={anamnesisForm.progressNotes} 
                          onChange={(e) => setAnamnesisForm({...anamnesisForm, progressNotes: e.target.value})}
                          rows="3"
                          style={{ width: '100%', padding: '8px', borderRadius: '4px', border: '1px solid #ccc' }}
                        />
                      </div>
                      <button type="submit" className="save-btn" disabled={!isAnamnesisDirty}>Salvar Detalhes da Ficha</button>
                    </div>

                  </form>
                )}

                <div className="allergy-container">
                  <p><strong>Alergias:</strong> {
                    selectedClient.anamnesisRecord.allergies && selectedClient.anamnesisRecord.allergies.length > 0 
                      ? selectedClient.anamnesisRecord.allergies.join(", ") 
                      : "Nenhuma alergia registrada."
                  }</p>
                  
                  {isEditMode && (
                    <form onSubmit={handleAddAllergy} className="add-allergy-form">
                      <input type="text" placeholder="Nova alergia..." value={allergyInput} onChange={(e) => setAllergyInput(e.target.value)} />
                      <button type="submit">Adicionar Alergia</button>
                    </form>
                  )}
                </div>
              </div>
            ) : (
              <div className="no-record-warning">
                <p>Nenhuma ficha de anamnese cadastrada para este cliente.</p>
                {isEditMode && <button onClick={handleCreateAnamnesis} className="create-record-btn">Criar Ficha em Branco</button>}
              </div>
            )}
          </div>
        </div>
      </div>
    );
  }

  // =========================
  // TELA 2: LISTAGEM PRINCIPAL
  // =========================
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
            <li key={Client.id} className="client-card" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <div>
                <strong>{Client.firstName} {Client.lastName}</strong>
                <br />
                <span style={{ fontSize: '0.9em', color: '#666' }}>{Client.phoneNumber}</span>
              </div>
              
              <button 
                onClick={() => handleEditClick(Client.id)}
                style={{ padding: '8px 16px', cursor: 'pointer' }}
              >
                Detalhar
              </button>
              <button 
                className="delete-btn" 
                onClick={() => handleDeleteClient(Client.id, Client.firstName)}
              >
                Excluir
              </button>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}

export default ClientsPage;