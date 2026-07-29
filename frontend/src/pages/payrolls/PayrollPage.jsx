import { useState, useEffect } from 'react';
import api from '../../services/api';
import './PayrollPage.css';

export default function PayrollPage() {
  const [specialists, setSpecialists] = useState([]);
  const [selectedSpecialist, setSelectedSpecialist] = useState('');
  
  const currentMonth = new Date().getMonth() + 1; // 1-12
  const currentYear = new Date().getFullYear();
  
  const [month, setMonth] = useState(currentMonth);
  const [year, setYear] = useState(currentYear);
  
  const [payStub, setPayStub] = useState(null); 
  const [history, setHistory] = useState([]);  
  
  const [loading, setLoading] = useState(false);
  const [notification, setNotification] = useState(null);

  useEffect(() => {
    fetchSpecialists();
  }, []);

  const fetchSpecialists = async () => {
    try {
      const response = await api.get('/api/employees');
      const onlySpecialists = response.data.filter(emp => emp.commissionPercentage !== undefined);
      setSpecialists(onlySpecialists);
    } catch (err) {
      showToast('Erro ao carregar especialistas.', 'error');
    }
  };
  useEffect(() => {
    if (selectedSpecialist) {
      fetchHistory(selectedSpecialist);
      setPayStub(null);
    } else {
      setHistory([]);
    }
  }, [selectedSpecialist]);

  const fetchHistory = async (id) => {
    try {
      const response = await api.get(`/api/payroll/specialist/${id}`);
      setHistory(response.data);
    } catch (err) {
      console.error(err);
    }
  };

  const handleGenerate = async (e) => {
    e.preventDefault();
    if (!selectedSpecialist) return;
    
    setLoading(true);
    try {
      //POST endpoint: /api/payroll/generate?specialistId=...&month=...&year=...
      const response = await api.post(`/api/payroll/generate?specialistId=${selectedSpecialist}&month=${month}&year=${year}`);
      
      setPayStub(response.data);
      showToast('Folha gerada com sucesso!', 'success');
      fetchHistory(selectedSpecialist); // Refresh history
      
    } catch (err) {
      if (err.response?.status === 409) {
        showToast('A folha deste mês já foi gerada para este especialista!', 'error');
        const existing = history.find(h => h.referenceMonth == month && h.referenceYear == year);
        if(existing) setPayStub(existing);
      } else {
        showToast('Erro ao gerar folha de pagamento.', 'error');
      }
    } finally {
      setLoading(false);
    }
  };

  const showToast = (msg, type = 'success') => {
    setNotification({ text: msg, type });
    setTimeout(() => setNotification(null), 4000);
  };

  return (
    <div className="payroll-page">
      {notification && <div className={`toast ${notification.type}`}>{notification.text}</div>}

      <h1>Folha de Pagamento</h1>
      <p className="subtitle">Gere os contracheques dos especialistas com base nos serviços concluídos.</p>

      <div className="payroll-layout">
        
        <div className="payroll-controls card">
          <h3>Gerar Pagamento</h3>
          <form onSubmit={handleGenerate}>
            <div className="form-group">
              <label>Especialista</label>
              <select 
                value={selectedSpecialist} 
                onChange={(e) => setSelectedSpecialist(e.target.value)}
                required
              >
                <option value="">Selecione um profissional...</option>
                {specialists.map(spec => (
                  <option key={spec.id} value={spec.id}>{spec.name}</option>
                ))}
              </select>
            </div>

            <div className="form-row">
              <div className="form-group">
                <label>Mês</label>
                <select value={month} onChange={(e) => setMonth(e.target.value)} required>
                  <option value="1">Janeiro</option>
                  <option value="2">Fevereiro</option>
                  <option value="3">Março</option>
                  <option value="4">Abril</option>
                  <option value="5">Maio</option>
                  <option value="6">Junho</option>
                  <option value="7">Julho</option>
                  <option value="8">Agosto</option>
                  <option value="9">Setembro</option>
                  <option value="10">Outubro</option>
                  <option value="11">Novembro</option>
                  <option value="12">Dezembro</option>
                </select>
              </div>
              <div className="form-group">
                <label>Ano</label>
                <input 
                  type="number" 
                  min="2024" 
                  max="2030" 
                  value={year} 
                  onChange={(e) => setYear(e.target.value)} 
                  required 
                />
              </div>
            </div>

            <button type="submit" className="btn-generate" disabled={loading || !selectedSpecialist}>
              {loading ? 'Processando...' : 'Processar Folha'}
            </button>
          </form>

          {history.length > 0 && (
            <div className="history-section">
              <h4>Histórico Recente</h4>
              <ul className="history-list">
                {history.slice().reverse().map(stub => (
                  <li key={stub.id || `${stub.referenceMonth}-${stub.referenceYear}`} 
                      className={`history-item ${payStub?.id === stub.id ? 'active' : ''}`}
                      onClick={() => setPayStub(stub)}>
                    📅 {stub.referenceMonth}/{stub.referenceYear}
                    <span className="history-salary">R$ {stub.totalSalary?.toFixed(2)}</span>
                  </li>
                ))}
              </ul>
            </div>
          )}
        </div>

        <div className="payroll-receipt card">
          {payStub ? (
            <div className="receipt-content animation-fade-in">
              <div className="receipt-header">
                <h2>Recibo de Pagamento</h2>
                <div className="receipt-meta">
                  <p><strong>Profissional:</strong> {payStub.specialistName}</p>
                  <p><strong>Período:</strong> {payStub.referenceMonth}/{payStub.referenceYear}</p>
                  <p><strong>Gerado em:</strong> {new Date(payStub.generatedAt).toLocaleDateString('pt-BR')}</p>
                </div>
              </div>

              <div className="receipt-body">
                <h4>Serviços Realizados (Concluídos)</h4>
                {payStub.services && payStub.services.length > 0 ? (
                  <table className="receipt-table">
                    <thead>
                      <tr>
                        <th>Procedimento</th>
                        <th style={{textAlign: 'right'}}>Valor Gerado (R$)</th>
                      </tr>
                    </thead>
                    <tbody>
                      {payStub.services.map((srv, index) => (
                        <tr key={index}>
                          <td>{srv.procedureName}</td>
                          <td style={{textAlign: 'right'}}>R$ {srv.value.toFixed(2)}</td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                ) : (
                  <p className="empty-services">Nenhum serviço registrado neste período.</p>
                )}
              </div>

              <div className="receipt-footer">
                <div className="summary-row">
                  <span>Produtividade Bruta:</span>
                  <span>R$ {payStub.productivity?.toFixed(2)}</span>
                </div>
                <div className="summary-row total">
                  <span>Salário Final a Pagar (Base + Comissão):</span>
                  <span>R$ {payStub.totalSalary?.toFixed(2)}</span>
                </div>
              </div>
              
              <button className="btn-print" onClick={() => window.print()}>🖨️ Imprimir Recibo</button>
            </div>
          ) : (
            <div className="empty-state">
              <span className="empty-icon"></span>
              <p>Selecione um especialista e clique em "Processar Folha" para visualizar o demonstrativo.</p>
            </div>
          )}
        </div>

      </div>
    </div>
  );
}