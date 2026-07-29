import { useState, useEffect } from 'react';
import api from '../../services/api';
import './SchedulingPage.css';

const STATUS_BR = {
  SCHEDULED: 'Agendado',
  CONFIRMED: 'Confirmado',
  COMPLETED: 'Concluído',
  CANCELLED: 'Cancelado',
  MISSED: 'Faltou'
};

const PAYMENT_METHODS_BR = {
  PIX: 'Pix',
  CREDIT_CARD: 'Cartão de Crédito',
  DEBIT_CARD: 'Cartão de Débito',
  CASH: 'Dinheiro'
};

export default function SchedulingPage() {
  const [clients, setClients] = useState([]);
  const [specialists, setSpecialists] = useState([]);
  const [procedures, setProcedures] = useState([]);
  const [loadingData, setLoadingData] = useState(true);

  const [form, setForm] = useState({
    clientId: '',
    specialistId: '',
    procedureId: '',
    date: new Date().toISOString().split('T')[0], 
    start: '09:00'
  });
  const [bookingLoading, setBookingLoading] = useState(false);

  const [agendaDate, setAgendaDate] = useState(new Date().toISOString().split('T')[0]);
  const [agendaSpecialistId, setAgendaSpecialistId] = useState('');
  const [agenda, setAgenda] = useState([]);
  const [agendaLoading, setAgendaLoading] = useState(false);

  const [checkoutModal, setCheckoutModal] = useState({ isOpen: false, scheduling: null });
  const [paymentMethod, setPaymentMethod] = useState('');
  const [checkoutLoading, setCheckoutLoading] = useState(false);

  const handleStatusChange = async (schedulingId, action) => {
    try {
      await api.put(`/api/schedulings/${schedulingId}/status?action=${action}`);
      fetchAgenda(); 
    } catch (err) {
      if (err.response?.data) {
        alert(`Erro: ${err.response.data}`);
      } else {
        alert('Erro ao atualizar status do agendamento.');
      }
    }
  };

  useEffect(() => {
    fetchMasterData();
  }, []);

  useEffect(() => {
    if (agendaSpecialistId && agendaDate) {
      fetchAgenda();
    }
  }, [agendaSpecialistId, agendaDate]);

  async function fetchMasterData() {
    try {
      const [clientsRes, empsRes, procsRes] = await Promise.all([
        api.get('/api/clients'),
        api.get('/api/employees'),
        api.get('/api/procedures')
      ]);

      setClients(clientsRes.data);
      setProcedures(procsRes.data);
      
      const specList = empsRes.data.filter(emp => emp.commissionPercentage !== undefined);
      setSpecialists(specList);

      if (specList.length > 0) {
        setAgendaSpecialistId(specList[0].id);
      }

    } catch (err) {
      console.error("Erro ao carregar dados", err);
      alert("Erro de conexão. Verifique se o servidor está rodando.");
    } finally {
      setLoadingData(false);
    }
  }

  async function fetchAgenda() {
    setAgendaLoading(true);
    try {
      const response = await api.get(`/api/schedulings/daily`, {
        params: { specialistId: agendaSpecialistId, date: agendaDate }
      });
      const sortedAgenda = response.data.sort((a, b) => a.start.localeCompare(b.start));
      setAgenda(sortedAgenda);
    } catch (err) {
      console.error(err);
      setAgenda([]);
    } finally {
      setAgendaLoading(false);
    }
  }

  const handleBookscheduling = async (e) => {
    e.preventDefault();
    setBookingLoading(true);

    try {
      await api.post('/api/schedulings', form);
      alert('Horário agendado com sucesso!');
      
      if (form.date === agendaDate && form.specialistId === agendaSpecialistId) {
        fetchAgenda();
      }
      
      setForm({ ...form, start: '' }); 

    } catch (err) {
      if (err.response?.data) {
        alert(`Erro: ${err.response.data}`); // e.g. "Horário conflitante" or "Fora do expediente"
      } else {
        alert('Erro ao realizar agendamento.');
      }
    } finally {
      setBookingLoading(false);
    }
  };

  const handleCheckoutSubmit = async (e) => {
    e.preventDefault();
    setCheckoutLoading(true);

    try {
      const scheduling = checkoutModal.scheduling;
      const schedulingId = scheduling.id;

      const createResponse = await api.post(`/payments?schedulingId=${schedulingId}&paymentMethod=${paymentMethod}`);
      const paymentId = createResponse.data.id;

      await api.put(`/payments/${paymentId}/pay`);

      alert('Pagamento registrado com sucesso!');
      
      setCheckoutModal({ isOpen: false, scheduling: null });
      setPaymentMethod('');
      fetchAgenda();

    } catch (err) {
      if (err.response?.data?.message) {
        alert(`Erro: ${err.response.data.message}`);
      } else {
        alert('Erro ao processar o pagamento.');
      }
    } finally {
      setCheckoutLoading(false);
    }
  };
  if (loadingData) return <div className="scheduling-page">Carregando módulos do sistema...</div>;

  return (
    <div className="scheduling-page">
      <h1>Recepção & Agendamentos</h1>
      
      <div className="scheduling-layout">
        
        {/* LEFT COLUMN: THE BOOKING FORM */}
        <div className="booking-card">
          <h3>Nova Reserva</h3>
          <form onSubmit={handleBookscheduling}>
            
            <div className="form-group">
              <label>Cliente *</label>
              <select 
                required 
                className="form-control"
                value={form.clientId} 
                onChange={e => setForm({...form, clientId: e.target.value})}
              >
                <option value="">Selecione um cliente...</option>
                {clients.map(c => (
                  <option key={c.id} value={c.id}>{c.firstName} {c.lastName}</option>
                ))}
              </select>
            </div>

            <div className="form-group">
              <label>Serviço Desejado *</label>
              <select 
                required 
                className="form-control"
                value={form.procedureId} 
                onChange={e => setForm({...form, procedureId: e.target.value})}
              >
                <option value="">Selecione o procedimento...</option>
                {procedures.map(p => (
                  <option key={p.id} value={p.id}>{p.name} ({p.averageDuration} min)</option>
                ))}
              </select>
            </div>

            <div className="form-group">
              <label>Especialista *</label>
              <select 
                required 
                className="form-control"
                value={form.specialistId} 
                onChange={e => setForm({...form, specialistId: e.target.value})}
              >
                <option value="">Selecione o profissional...</option>
                {specialists.map(s => (
                  <option key={s.id} value={s.id}>{s.name}</option>
                ))}
              </select>
            </div>

            <div className="form-row">
              <div className="form-group">
                <label>Data *</label>
                <input 
                  required 
                  type="date" 
                  className="form-control"
                  value={form.date} 
                  onChange={e => setForm({...form, date: e.target.value})}
                />
              </div>
              <div className="form-group">
                <label>Horário *</label>
                <input 
                  required 
                  type="time" 
                  className="form-control"
                  value={form.start} 
                  onChange={e => setForm({...form, start: e.target.value})}
                />
              </div>
            </div>

            <button type="submit" className="submit-btn" disabled={bookingLoading}>
              {bookingLoading ? 'Processando...' : 'Confirmar Agendamento'}
            </button>
          </form>
        </div>

        {/* RIGHT COLUMN: THE DAILY AGENDA */}
        <div className="agenda-card">
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <h3 style={{ margin: 0, color: '#5c2d2d' }}>Agenda do Dia</h3>
          </div>

          <div className="agenda-filters" style={{ marginTop: '15px' }}>
            <select 
              className="form-control" style={{ flex: 2 }}
              value={agendaSpecialistId} 
              onChange={e => setAgendaSpecialistId(e.target.value)}
            >
              {specialists.map(s => (
                <option key={s.id} value={s.id}>Agenda de: {s.name}</option>
              ))}
            </select>
            <input 
              type="date" 
              className="form-control" style={{ flex: 1 }}
              value={agendaDate} 
              onChange={e => setAgendaDate(e.target.value)}
            />
          </div>

          {agendaLoading ? (
            <p>Consultando agenda...</p>
          ) : (
            <div className="timeline">
              {agenda.length === 0 ? (
                <div className="empty-agenda">
                  <p>Nenhum agendamento para este profissional nesta data.</p>
                  <p style={{fontSize: '2em', margin: 0}}>☕</p>
                </div>
              ) : (
                agenda.map(scheduling => (
                  <div key={scheduling.id} className="timeline-slot">
                    <div className="timeline-time">
                      {scheduling.start.slice(0, 5)} <br/> 
                      <span style={{fontSize: '0.6em', color: '#888'}}>até {scheduling.finish.slice(0, 5)}</span>
                    </div>
                    <div className="timeline-details" style={{ flex: 1, padding: '0 15px' }}>
                      <h4>{scheduling.client.firstName} {scheduling.client.lastName}</h4>
                      <p>{scheduling.procedure.name}</p>
                      
                      {/* ACTION BUTTONS BASED ON STATUS */}
                      <div className="timeline-actions" style={{ marginTop: '10px', display: 'flex', gap: '8px', alignItems: 'center' }}>
                        {scheduling.status === 'SCHEDULED' && (
                          <>
                            <button onClick={() => handleStatusChange(scheduling.id, 'confirm')} className="btn-action btn-confirm">Confirmar</button>
                            <button onClick={() => handleStatusChange(scheduling.id, 'cancel')} className="btn-action btn-cancel">Cancelar</button>
                          </>
                        )}
                        {scheduling.status === 'CONFIRMED' && (
                          <>
                            <button onClick={() => handleStatusChange(scheduling.id, 'complete')} className="btn-action btn-complete">Concluir</button>
                            <button onClick={() => handleStatusChange(scheduling.id, 'miss')} className="btn-action btn-miss">Faltou</button>
                            <button onClick={() => handleStatusChange(scheduling.id, 'cancel')} className="btn-action btn-cancel">Cancelar</button>
                          </>
                        )}
                        {scheduling.status === 'COMPLETED' && !scheduling.isPaid && (
                          <button 
                            onClick={() => setCheckoutModal({ isOpen: true, scheduling: scheduling })} 
                            className="btn-action btn-checkout"
                          >
                            Ir para o Caixa
                          </button>
                        )}
                        {scheduling.status === 'COMPLETED' && scheduling.isPaid && (
                          <span style={{ backgroundColor: '#e8f5e9', color: '#2e7d32', padding: '6px 12px', borderRadius: '4px', fontWeight: 'bold', fontSize: '0.85em' }}>
                            ✅ Pago
                          </span>
                        )}
                      </div>
                    </div>
                    <div>
                      {/* TRANSLATED STATUS BADGE */}
                      <span className={`status-badge status-${scheduling.status}`}>
                        {STATUS_BR[scheduling.status] || scheduling.status}
                      </span>
                    </div>
                    
                  </div>
                ))
              )}
            </div>
          )}

        </div>
      </div>

      {/* --- CHECKOUT MODAL --- */}
      {checkoutModal.isOpen && (
        <div className="modal-overlay">
          <div className="modal-content">
            <h3>Caixa: Finalizar Pagamento</h3>
            <p><strong>Cliente:</strong> {checkoutModal.scheduling.client.firstName} {checkoutModal.scheduling.client.lastName}</p>
            <p><strong>Serviço:</strong> {checkoutModal.scheduling.procedure.name}</p>
            
            <form onSubmit={handleCheckoutSubmit} style={{ marginTop: '20px' }}>
              <div className="form-group">
                <label>Método de Pagamento *</label>
                <select 
                  required 
                  className="form-control"
                  value={paymentMethod}
                  onChange={e => setPaymentMethod(e.target.value)}
                >
                  <option value="">Selecione...</option>
                  {Object.entries(PAYMENT_METHODS_BR).map(([key, label]) => (
                    <option key={key} value={key}>{label}</option>
                  ))}
                </select>
              </div>

              <div style={{ display: 'flex', gap: '10px', marginTop: '20px' }}>
                <button type="submit" className="submit-btn" disabled={checkoutLoading}>
                  {checkoutLoading ? 'Processando...' : 'Confirmar Pagamento'}
                </button>

                <button 
                  type="button" 
                  className="submit-btn" 
                  style={{ background: '#c62828' }} 
                  onClick={() => setCheckoutModal({ isOpen: false, scheduling: null })}
                >
                  Cancelar
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

    </div>
  );
}