import { useState, useEffect } from 'react';
import api from '../../services/api';
import './EmployeesPage.css'

const SPECIALIZATIONS_BR = {
  HAIRDRESSER: 'Cabeleireiro(a)',
  MANICURE: 'Manicure / Pedicure',
  PODIATRIST: 'Podóloga',
  COLORING: 'Colorista',
  CAPILLARY_THERAPY: 'Terapia Capilar'
};

export default function EmployeesPage() {
  // --- MOCK SECURITY ---
  // Change this to 'SPECIALIST' to see the contract fields lock down!
  const currentUserRole = 'ADMIN'; 

  const [activeTab, setActiveTab] = useState('list'); // 'list' | 'new'
  const [employees, setEmployees] = useState([]);
  const [loading, setLoading] = useState(false);
  const [notification, setNotification] = useState(null);

  // --- EDIT STATE ---
  const [selectedEmployee, setSelectedEmployee] = useState(null);
  const [isEditMode, setIsEditMode] = useState(false);
  const [editForm, setEditForm] = useState({});

  // --- CREATION FORM STATE ---
  const [role, setRole] = useState('SPECIALIST'); 
  const [form, setForm] = useState({
    name: '', cpf: '', email: '', phone: '', address: '', baseSalary: '',
    commissionPercentage: '', start: '09:00', finish: '18:00', specializations: []
  });

  useEffect(() => {
    if (activeTab === 'list') {
      fetchEmployees();
    }
  }, [activeTab]);

  const showToast = (msg) => {
    setNotification(msg);
    setTimeout(() => setNotification(null), 4000);
  };

  const fetchEmployees = async () => {
    setLoading(true);
    try {
      const response = await api.get('/api/employees');
      setEmployees(response.data);
    } catch (err) {
      console.error(err);
      showToast('Erro ao carregar funcionários.');
    } finally {
      setLoading(false);
    }
  };

  // --- HANDLERS FOR EDIT VIEW ---
  const handleEditClick = (emp) => {
    setSelectedEmployee(emp);
    setIsEditMode(false);
    setEditForm({
      name: emp.name || '',
      phone: emp.phone || '',
      address: emp.address || '',
      baseSalary: emp.baseSalary || '',
      commissionPercentage: emp.commissionPercentage ? (emp.commissionPercentage * 100) : '',
      start: emp.start || '09:00',
      finish: emp.finish || '18:00',
      specializations: emp.specializations || []
    });
  };

  const handleBackClick = () => {
    setSelectedEmployee(null);
    setIsEditMode(false);
    fetchEmployees();
  };

  const handleEditCheckboxChange = (specKey) => {
    if (currentUserRole !== 'ADMIN') return; 

    setEditForm(prev => {
      const currentSpecs = prev.specializations;
      if (currentSpecs.includes(specKey)) {
        return { ...prev, specializations: currentSpecs.filter(s => s !== specKey) };
      } else {
        return { ...prev, specializations: [...currentSpecs, specKey] };
      }
    });
  };

  const handleUpdateSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      // 1. Update Profile Data (Anyone can do this)
      const profilePayload = {
        name: editForm.name, phone: editForm.phone, address: editForm.address
      };
      await api.put(`/api/employees/${selectedEmployee.id}/profile`, profilePayload);

      // 2. Update Contract Data (Only if the user is an ADMIN)
      if (currentUserRole === 'ADMIN') {
        if (isSpecialist(selectedEmployee)) {
          const contractPayload = {
            baseSalary: parseFloat(editForm.baseSalary),
            commissionPercentage: parseFloat(editForm.commissionPercentage) / 100.0,
            start: editForm.start,
            finish: editForm.finish,
            specializations: editForm.specializations
          };
          await api.put(`/api/employees/specialist/${selectedEmployee.id}/contract`, contractPayload);
        } else {
          await api.put(`/api/employees/admin/${selectedEmployee.id}/contract`, { baseSalary: parseFloat(editForm.baseSalary) });
        }
      }

      showToast('Dados atualizados com sucesso!');
      handleBackClick();
    } catch (err) {
      console.error(err);
      showToast('Erro ao atualizar dados.');
    } finally {
      setLoading(false);
    }
  };

  // --- HANDLERS FOR CREATION ---
  const handleCheckboxChange = (specKey) => {
    setForm(prev => {
      const currentSpecs = prev.specializations;
      if (currentSpecs.includes(specKey)) {
        return { ...prev, specializations: currentSpecs.filter(s => s !== specKey) };
      } else {
        return { ...prev, specializations: [...currentSpecs, specKey] };
      }
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);

    try {
      const basePayload = {
        name: form.name, cpf: form.cpf, email: form.email,
        phone: form.phone, address: form.address, baseSalary: parseFloat(form.baseSalary)
      };

      if (role === 'ADMIN') {
        await api.post('/api/employees/admin', basePayload);
      } else {
        const specialistPayload = {
          ...basePayload,
          commissionPercentage: parseFloat(form.commissionPercentage) / 100.0,
          start: form.start, finish: form.finish, specializations: form.specializations
        };
        await api.post('/api/employees/specialist', specialistPayload);
      }

      showToast('Funcionário contratado com sucesso!');
      setForm({
        name: '', cpf: '', email: '', phone: '', address: '', baseSalary: '',
        commissionPercentage: '', start: '09:00', finish: '18:00', specializations: []
      });
      setActiveTab('list');
      
    } catch (err) {
      if (err.response?.data) {
        showToast(`Erro: ${err.response.data}`);
      } else {
        showToast('Erro ao cadastrar funcionário.');
      }
    } finally {
      setLoading(false);
    }
  };

  const handleDeactivate = async (id, name) => {
    if (!window.confirm(`Deseja mesmo desligar/desativar o(a) funcionário(a) ${name}?`)) return;
    
    try {
      await api.delete(`/api/employees/${id}`);
      showToast('Funcionário desativado com sucesso.');
      fetchEmployees(); 
    } catch (err) {
      showToast('Erro ao desativar funcionário.');
    }
  };

  const isSpecialist = (employee) => employee.commissionPercentage !== undefined;

  return (
    <>
      <div className="employees-page">
        {notification && <div className="toast">{notification}</div>}

        {}
        {/* --- VIEW: DETAILS & EDIT --- */}
        {selectedEmployee ? (
          <div className="emp-card" style={{ animation: 'fadeIn 0.3s ease' }}>
            <button onClick={handleBackClick} className="back-btn">&larr; Voltar para Lista</button>
            
            <div className="detail-header">
              <div>
                <h2 style={{ margin: 0, color: '#5c2d2d' }}>{selectedEmployee.name}</h2>
              </div>
              <div className="edit-toggle-container">
                <span className="toggle-label">{isEditMode ? "Modo Edição" : "Modo Visualização"}</span>
                <label className="switch">
                  <input type="checkbox" checked={isEditMode} onChange={() => setIsEditMode(!isEditMode)} />
                  <span className="slider round"></span>
                </label>
              </div>
            </div>

            {!isEditMode ? (
              <div className="view-mode-info" style={{ display: 'grid', gap: '15px' }}>
                <div>
                  <h4 style={{ margin: '0 0 5px 0', color: '#555' }}>Dados Pessoais</h4>
                  <p><strong>CPF:</strong> {selectedEmployee.cpf}</p>
                  <p><strong>Email:</strong> {selectedEmployee.email}</p>
                  <p><strong>Telefone:</strong> {selectedEmployee.phone || 'Não informado'}</p>
                  <p><strong>Endereço:</strong> {selectedEmployee.address || 'Não informado'}</p>
                </div>
                <div style={{ padding: '15px', background: '#f9f9f9', borderRadius: '8px', border: '1px solid #eee' }}>
                  <h4 style={{ margin: '0 0 10px 0', color: '#5c2d2d' }}>Contrato & Remuneração</h4>
                  <p><strong>Salário Base:</strong> R$ {selectedEmployee.baseSalary.toFixed(2)}</p>
                  {isSpecialist(selectedEmployee) && (
                    <>
                      <p><strong>Comissão:</strong> {(selectedEmployee.commissionPercentage * 100).toFixed(1)}%</p>
                      <p><strong>Expediente:</strong> {selectedEmployee.start} às {selectedEmployee.finish}</p>
                      <p><strong>Especializações:</strong> {
                        selectedEmployee.specializations?.map(s => SPECIALIZATIONS_BR[s]).join(', ') || 'Nenhuma'
                      }</p>
                    </>
                  )}
                </div>
              </div>
            ) : (
              <form onSubmit={handleUpdateSubmit}>
                <div className="form-grid">
                  <div className="form-group">
                    <label>Nome Completo *</label>
                    <input required className="form-control" value={editForm.name} onChange={e => setEditForm({...editForm, name: e.target.value})} />
                  </div>
                  <div className="form-group">
                    <label>Telefone</label>
                    <input className="form-control" value={editForm.phone} onChange={e => setEditForm({...editForm, phone: e.target.value})} />
                  </div>
                  <div className="form-group" style={{gridColumn: '1 / -1'}}>
                    <label>Endereço</label>
                    <input className="form-control" value={editForm.address} onChange={e => setEditForm({...editForm, address: e.target.value})} />
                  </div>
                </div>

                <h4 style={{ color: '#5c2d2d', marginTop: '20px', display: 'flex', alignItems: 'center', gap: '8px' }}>
                  Contrato & Remuneração
                  {currentUserRole !== 'ADMIN' && <span style={{fontSize: '0.7em', color: '#c62828', background: '#ffebee', padding: '2px 6px', borderRadius: '4px'}}>Somente Leitura 🔒</span>}
                </h4>
                
                <div className="form-grid">
                  <div className="form-group">
                    <label>Salário Base (R$) *</label>
                    <input required type="number" step="0.01" min="0" className="form-control" value={editForm.baseSalary} disabled={currentUserRole !== 'ADMIN'} onChange={e => setEditForm({...editForm, baseSalary: e.target.value})} />
                  </div>
                  
                  {isSpecialist(selectedEmployee) && (
                    <>
                      <div className="form-group">
                        <label>Comissão (%) *</label>
                        <input required type="number" step="0.1" min="0" max="100" className="form-control" value={editForm.commissionPercentage} disabled={currentUserRole !== 'ADMIN'} onChange={e => setEditForm({...editForm, commissionPercentage: e.target.value})} />
                      </div>
                      <div className="form-group">
                        <label>Início *</label>
                        <input required type="time" className="form-control" value={editForm.start} disabled={currentUserRole !== 'ADMIN'} onChange={e => setEditForm({...editForm, start: e.target.value})} />
                      </div>
                      <div className="form-group">
                        <label>Término *</label>
                        <input required type="time" className="form-control" value={editForm.finish} disabled={currentUserRole !== 'ADMIN'} onChange={e => setEditForm({...editForm, finish: e.target.value})} />
                      </div>
                    </>
                  )}
                </div>

                {isSpecialist(selectedEmployee) && (
                  <div style={{marginBottom: '20px', padding: '15px', background: currentUserRole === 'ADMIN' ? '#fafafa' : '#f0f0f0', borderRadius: '8px', border: '1px solid #eee'}}>
                    <label style={{fontWeight: 'bold', color: '#555', display: 'block'}}>Especializações:</label>
                    <div className="specs-grid">
                      {Object.entries(SPECIALIZATIONS_BR).map(([key, label]) => (
                        <label key={key} className="spec-checkbox" style={{ opacity: currentUserRole !== 'ADMIN' ? 0.6 : 1, cursor: currentUserRole !== 'ADMIN' ? 'not-allowed' : 'pointer' }}>
                          <input 
                            type="checkbox" 
                            disabled={currentUserRole !== 'ADMIN'}
                            checked={editForm.specializations.includes(key)}
                            onChange={() => handleEditCheckboxChange(key)}
                          />
                          {label}
                        </label>
                      ))}
                    </div>
                  </div>
                )}

                <button type="submit" className="submit-btn" disabled={loading}>
                  {loading ? 'Salvando...' : 'Salvar Alterações'}
                </button>
              </form>
            )}
          </div>
        ) : (
          <>
            {}
            <h1>Gestão de Equipe</h1>

            <div className="tabs-header">
              <button className={`tab-btn ${activeTab === 'list' ? 'active' : ''}`} onClick={() => setActiveTab('list')}>
                Equipe Ativa
              </button>
              <button className={`tab-btn ${activeTab === 'new' ? 'active' : ''}`} onClick={() => setActiveTab('new')}>
                Contratar Funcionário
              </button>
            </div>

            {/* --- TAB: NEW HIRE FORM --- */}
            {activeTab === 'new' && (
              <div className="emp-card">
                <div className="role-selector">
                  <label className="role-option">
                    <input type="radio" name="role" checked={role === 'SPECIALIST'} onChange={() => setRole('SPECIALIST')} />
                    Especialista (Cabeleireiro, Manicure...)
                  </label>
                  <label className="role-option">
                    <input type="radio" name="role" checked={role === 'ADMIN'} onChange={() => setRole('ADMIN')} />
                    Administrador (Gerente, Recepção)
                  </label>
                </div>

                <form onSubmit={handleSubmit}>
                  <h4 style={{ color: '#5c2d2d', marginTop: 0 }}>Dados Pessoais</h4>
                  <div className="form-grid">
                    <div className="form-group">
                      <label>Nome Completo *</label>
                      <input required className="form-control" value={form.name} onChange={e => setForm({...form, name: e.target.value})} placeholder="João da Silva" />
                    </div>
                    <div className="form-group">
                      <label>CPF *</label>
                      <input required className="form-control" value={form.cpf} onChange={e => setForm({...form, cpf: e.target.value})} placeholder="000.000.000-00" />
                    </div>
                    <div className="form-group">
                      <label>Email *</label>
                      <input required type="email" className="form-control" value={form.email} onChange={e => setForm({...form, email: e.target.value})} placeholder="joao@email.com" />
                    </div>
                    <div className="form-group">
                      <label>Telefone</label>
                      <input className="form-control" value={form.phone} onChange={e => setForm({...form, phone: e.target.value})} placeholder="(11) 99999-9999" />
                    </div>
                    <div className="form-group" style={{gridColumn: '1 / -1'}}>
                      <label>Endereço Completo</label>
                      <input className="form-control" value={form.address} onChange={e => setForm({...form, address: e.target.value})} placeholder="Rua das Flores, 123..." />
                    </div>
                  </div>

                  <h4 style={{ color: '#5c2d2d', marginTop: '20px' }}>Contrato & Remuneração</h4>
                  <div className="form-grid">
                    <div className="form-group">
                      <label>Salário Base (R$) *</label>
                      <input required type="number" step="0.01" min="0" className="form-control" value={form.baseSalary} onChange={e => setForm({...form, baseSalary: e.target.value})} placeholder="1500.00" />
                    </div>
                    
                    {role === 'SPECIALIST' && (
                      <>
                        <div className="form-group">
                          <label>Comissão (%) *</label>
                          <input required type="number" step="0.1" min="0" max="100" className="form-control" value={form.commissionPercentage} onChange={e => setForm({...form, commissionPercentage: e.target.value})} placeholder="30" />
                        </div>
                        <div className="form-group">
                          <label>Horário de Início *</label>
                          <input required type="time" className="form-control" value={form.start} onChange={e => setForm({...form, start: e.target.value})} />
                        </div>
                        <div className="form-group">
                          <label>Horário de Término *</label>
                          <input required type="time" className="form-control" value={form.finish} onChange={e => setForm({...form, finish: e.target.value})} />
                        </div>
                      </>
                    )}
                  </div>

                  {role === 'SPECIALIST' && (
                    <div style={{marginBottom: '20px'}}>
                      <label style={{fontWeight: 'bold', color: '#555', display: 'block'}}>Especializações:</label>
                      <div className="specs-grid">
                        {Object.entries(SPECIALIZATIONS_BR).map(([key, label]) => (
                          <label key={key} className="spec-checkbox">
                            <input 
                              type="checkbox" 
                              checked={form.specializations.includes(key)}
                              onChange={() => handleCheckboxChange(key)}
                            />
                            {label}
                          </label>
                        ))}
                      </div>
                    </div>
                  )}

                  <button type="submit" className="submit-btn" disabled={loading}>
                    {loading ? 'Contratando...' : 'Finalizar Contratação'}
                  </button>
                </form>
              </div>
            )}

            {}
            {/* --- TAB: ROSTER LIST --- */}
            {activeTab === 'list' && (
              <div className="emp-list">
                {loading && <p>Carregando equipe...</p>}
                {!loading && employees.length === 0 && <p>Nenhum funcionário cadastrado no momento.</p>}
                
                {employees.map(emp => (
                  <div key={emp.id} className="emp-item">
                    <div className="emp-info">
                      <div style={{display: 'flex', alignItems: 'center', gap: '10px', marginBottom: '8px'}}>
                        <h3 style={{margin: 0}}>{emp.name}</h3>
                        {isSpecialist(emp) 
                          ? <span className="badge specialist">Especialista</span>
                          : <span className="badge admin">Administrador</span>
                        }
                      </div>
                      <p>✉️ {emp.email} | 📞 {emp.phone || 'N/A'}</p>
                      <p>💰 Salário Base: R$ {emp.baseSalary.toFixed(2)}</p>
                      
                      {isSpecialist(emp) && (
                        <div style={{marginTop: '10px', padding: '10px', background: '#f9f9f9', borderRadius: '6px'}}>
                          <p style={{margin: '0 0 5px 0'}}><strong>Comissão:</strong> {(emp.commissionPercentage * 100).toFixed(1)}%</p>
                          <p style={{margin: '0 0 5px 0'}}><strong>Expediente:</strong> {emp.start} às {emp.finish}</p>
                          <p style={{margin: 0}}><strong>Especializações:</strong> {
                            emp.specializations?.map(s => SPECIALIZATIONS_BR[s]).join(', ') || 'Nenhuma'
                          }</p>
                        </div>
                      )}
                    </div>
                    
                    <div className="emp-actions">
                      <button className="fire-btn" style={{color: '#5c2d2d', borderColor: '#5c2d2d', background: 'none'}} onClick={() => handleEditClick(emp)}>
                        Detalhar / Editar
                      </button>
                      <button className="fire-btn" onClick={() => handleDeactivate(emp.id, emp.name)}>
                        Desligar
                      </button>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </>
        )}
      </div>
    </>
  );
}