import { useState, useEffect } from 'react';
import api from '../../services/api';
import './SchedulingPage.css';

export default function SchedulingPage() {
  // --- MASTER DATA (For Dropdowns) ---
  const [clients, setClients] = useState([]);
  const [specialists, setSpecialists] = useState([]);
  const [procedures, setProcedures] = useState([]);
  const [loadingData, setLoadingData] = useState(true);

  // --- BOOKING FORM STATE ---
  const [form, setForm] = useState({
    clientId: '',
    specialistId: '',
    procedureId: '',
    date: new Date().toISOString().split('T')[0], // Today's date default
    start: '09:00'
  });
  const [bookingLoading, setBookingLoading] = useState(false);

  // --- AGENDA STATE ---
  const [agendaDate, setAgendaDate] = useState(new Date().toISOString().split('T')[0]);
  const [agendaSpecialistId, setAgendaSpecialistId] = useState('');
  const [agenda, setAgenda] = useState([]);
  const [agendaLoading, setAgendaLoading] = useState(false);

  // 1. Fetch all required data when the page loads
  useEffect(() => {
    fetchMasterData();
  }, []);

  // 2. Fetch the agenda whenever the Agenda Date or Agenda Specialist changes
  useEffect(() => {
    if (agendaSpecialistId && agendaDate) {
      fetchAgenda();
    }
  }, [agendaSpecialistId, agendaDate]);

  async function fetchMasterData() {
    try {
      // Execute all 3 API calls at the same time to be fast!
      const [clientsRes, empsRes, procsRes] = await Promise.all([
        api.get('/api/clients'),
        api.get('/api/employees'),
        api.get('/api/procedures')
      ]);

      setClients(clientsRes.data);
      setProcedures(procsRes.data);
      
      // Filter employees to ONLY show Specialists (people who can actually do procedures)
      const specList = empsRes.data.filter(emp => emp.commissionPercentage !== undefined);
      setSpecialists(specList);

      // Pre-select the first specialist for the agenda view if available
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
      // Sort appointments chronologically by start time
      const sortedAgenda = response.data.sort((a, b) => a.start.localeCompare(b.start));
      setAgenda(sortedAgenda);
    } catch (err) {
      console.error(err);
      setAgenda([]);
    } finally {
      setAgendaLoading(false);
    }
  }

  const handleBookAppointment = async (e) => {
    e.preventDefault();
    setBookingLoading(true);

    try {
      await api.post('/api/schedulings', form);
      alert('Horário agendado com sucesso!');
      
      // If the booking was for the currently viewed agenda, refresh it!
      if (form.date === agendaDate && form.specialistId === agendaSpecialistId) {
        fetchAgenda();
      }
      
      // Reset the time so we don't accidentally double-book
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

  if (loadingData) return <div className="scheduling-page">Carregando módulos do sistema...</div>;

  return (
    <div className="scheduling-page">
      <h1>Recepção & Agendamentos</h1>
      
      <div className="scheduling-layout">
        
        {/* LEFT COLUMN: THE BOOKING FORM */}
        <div className="booking-card">
          <h3>Nova Reserva</h3>
          <form onSubmit={handleBookAppointment}>
            
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
                agenda.map(appt => (
                  <div key={appt.id} className="timeline-slot">
                    <div className="timeline-time">
                      {appt.start.slice(0, 5)} <br/> 
                      <span style={{fontSize: '0.6em', color: '#888'}}>até {appt.finish.slice(0, 5)}</span>
                    </div>
                    <div className="timeline-details" style={{ flex: 1, padding: '0 15px' }}>
                      <h4>{appt.client.firstName} {appt.client.lastName}</h4>
                      <p>{appt.procedure.name}</p>
                    </div>
                    <div>
                      <span className={`status-badge status-${appt.status}`}>
                        {appt.status}
                      </span>
                    </div>
                  </div>
                ))
              )}
            </div>
          )}

        </div>
      </div>
    </div>
  );
}