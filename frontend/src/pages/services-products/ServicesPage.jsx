import { useState, useEffect } from 'react';
import api from '../../services/api';
import './ServicesPage.css';

// Translation dictionary for Procedure Categories
const PROCEDURE_CATEGORY_BR = {
  BLOWOUT: 'Escova',
  HAIR_TREATMENT: 'Tratamento Capilar',
  SCALP_THERAPY: 'Terapia Capilar',
  CONSULTATION: 'Avaliação',
  CHEMICAL_SERVICES: 'Química',
  HAIRCUT: 'Corte',
  HAIR_COLORING: 'Coloração',
  NAILS: 'Manicure / Pedicure'
};

export default function ServicesPage() {
  const [activeTab, setActiveTab] = useState('products'); // 'products' | 'procedures'
  
  // Data State
  const [products, setProducts] = useState([]);
  const [procedures, setProcedures] = useState([]);
  const [loading, setLoading] = useState(false);
  const [notification, setNotification] = useState(null);

  // Form States
  const [productForm, setProductForm] = useState({
    name: '', price: '', unitMeasure: '', brand: '', inventoryQuantity: ''
  });
  
  const [procedureForm, setProcedureForm] = useState({
    name: '', category: '', cost: '', rawMaterial: '', averageDuration: ''
  });

  useEffect(() => {
    if (activeTab === 'products') {
      fetchProducts();
    } else {
      fetchProcedures();
    }
  }, [activeTab]);

  const showToast = (msg) => {
    setNotification(msg);
    setTimeout(() => setNotification(null), 4000);
  };

  const fetchProducts = async () => {
    setLoading(true);
    try {
      const response = await api.get('/api/products');
      setProducts(response.data);
    } catch (err) {
      console.error(err);
      showToast('Erro ao carregar produtos.');
    } finally {
      setLoading(false);
    }
  };

  const fetchProcedures = async () => {
    setLoading(true);
    try {
      const response = await api.get('/api/procedures');
      setProcedures(response.data);
    } catch (err) {
      console.error(err);
      showToast('Erro ao carregar procedimentos.');
    } finally {
      setLoading(false);
    }
  };

  const handleCreateProduct = async (e) => {
    e.preventDefault();
    try {
      const payload = {
        ...productForm,
        price: parseFloat(productForm.price),
        inventoryQuantity: parseInt(productForm.inventoryQuantity)
      };
      await api.post('/api/products', payload);
      showToast('Produto cadastrado com sucesso!');
      setProductForm({ name: '', price: '', unitMeasure: '', brand: '', inventoryQuantity: '' });
      fetchProducts();
    } catch (err) {
      showToast('Erro ao cadastrar produto.');
    }
  };

  const handleCreateProcedure = async (e) => {
    e.preventDefault();
    try {
      const payload = {
        ...procedureForm,
        cost: parseFloat(procedureForm.cost),
        averageDuration: parseInt(procedureForm.averageDuration)
      };
      await api.post('/api/procedures', payload);
      showToast('Procedimento cadastrado com sucesso!');
      setProcedureForm({ name: '', category: '', cost: '', rawMaterial: '', averageDuration: '' });
      fetchProcedures();
    } catch (err) {
      showToast('Erro ao cadastrar procedimento.');
    }
  };

  const handleUpdateStock = async (productId, quantityToAddOrRemove) => {
    try {
      const isAdding = quantityToAddOrRemove > 0;
      const endpoint = isAdding ? `/api/products/${productId}/stock/add` : `/api/products/${productId}/stock/remove`;
      
      await api.put(endpoint, { quantity: Math.abs(quantityToAddOrRemove) });
      
      fetchProducts();
    } catch (err) {
      if(err.response?.data) {
         alert(err.response.data);
      } else {
         showToast('Erro ao atualizar estoque.');
      }
    }
  };

  const handleDeleteProduct = async (id) => {
    if(!window.confirm("Deseja mesmo remover este produto?")) return;
    try {
      await api.delete(`/api/products/${id}`);
      showToast('Produto removido.');
      fetchProducts();
    } catch (err) {
      showToast('Erro ao remover produto.');
    }
  };

  const handleDeleteProcedure = async (id) => {
    if(!window.confirm("Deseja mesmo remover este procedimento?")) return;
    try {
      await api.delete(`/api/procedures/${id}`);
      showToast('Procedimento removido.');
      fetchProcedures();
    } catch (err) {
      showToast('Erro ao remover procedimento.');
    }
  };

  return (
    <div className="services-page-container">
      {notification && <div className="notification-toast">{notification}</div>}
      
      <h1>Estoque & Serviços</h1>
      
      <div className="tabs-header">
        <button 
          className={`tab-button ${activeTab === 'products' ? 'active' : ''}`}
          onClick={() => setActiveTab('products')}
        >
          📦 Produtos (Estoque)
        </button>
        <button 
          className={`tab-button ${activeTab === 'procedures' ? 'active' : ''}`}
          onClick={() => setActiveTab('procedures')}
        >
          ✂️ Procedimentos (Serviços)
        </button>
      </div>

      {loading && <p>Carregando dados...</p>}

      {/* --- TAB 1: PRODUCTS --- */}
      {!loading && activeTab === 'products' && (
        <>
          {/* Create Product Form */}
          <div className="creation-card">
            <h3>Cadastrar Novo Produto</h3>
            <form onSubmit={handleCreateProduct}>
              <div className="form-grid">
                <div className="form-group">
                  <label>Nome do Produto *</label>
                  <input required type="text" value={productForm.name} onChange={e => setProductForm({...productForm, name: e.target.value})} placeholder="Ex: Shampoo Revitalizante" />
                </div>
                <div className="form-group">
                  <label>Marca</label>
                  <input type="text" value={productForm.brand} onChange={e => setProductForm({...productForm, brand: e.target.value})} placeholder="Ex: L'Oréal" />
                </div>
                <div className="form-group">
                  <label>Unidade de Medida</label>
                  <input type="text" value={productForm.unitMeasure} onChange={e => setProductForm({...productForm, unitMeasure: e.target.value})} placeholder="Ex: 500ml, Unidade" />
                </div>
                <div className="form-group">
                  <label>Preço de Venda (R$) *</label>
                  <input required type="number" step="0.01" min="0" value={productForm.price} onChange={e => setProductForm({...productForm, price: e.target.value})} placeholder="0.00" />
                </div>
                <div className="form-group">
                  <label>Estoque Inicial *</label>
                  <input required type="number" min="0" value={productForm.inventoryQuantity} onChange={e => setProductForm({...productForm, inventoryQuantity: e.target.value})} placeholder="0" />
                </div>
              </div>
              <button type="submit" className="submit-btn">Adicionar Produto</button>
            </form>
          </div>

          {/* Product List */}
          <div className="items-grid">
            {products.length === 0 ? <p>Nenhum produto cadastrado.</p> : null}
            {products.map(prod => (
              <div key={prod.id} className="item-card">
                <div>
                  <div className="item-header">
                    <h4>{prod.name}</h4>
                    <span className="badge">{prod.brand || 'Sem marca'}</span>
                  </div>
                  <div className="item-body">
                    <p><strong>Unidade:</strong> {prod.unitMeasure || 'N/A'}</p>
                    <p className="price-tag">R$ {prod.price.toFixed(2)}</p>
                  </div>
                </div>
                
                <div style={{display: 'flex', flexDirection: 'column'}}>
                  <div className="stock-controls">
                    <span style={{fontSize: '0.85em', color: '#666', flex: 1}}>Estoque Atual:</span>
                    <button className="stock-btn remove" onClick={() => handleUpdateStock(prod.id, -1)} title="Remover 1">-</button>
                    <span className={`stock-amount ${prod.inventoryQuantity <= 5 ? 'low-stock' : ''}`}>
                      {prod.inventoryQuantity}
                    </span>
                    <button className="stock-btn add" onClick={() => handleUpdateStock(prod.id, 1)} title="Adicionar 1">+</button>
                  </div>
                  <button className="delete-btn" onClick={() => handleDeleteProduct(prod.id)}>Excluir Produto</button>
                </div>
              </div>
            ))}
          </div>
        </>
      )}

      {/* --- TAB 2: PROCEDURES --- */}
      {!loading && activeTab === 'procedures' && (
        <>
          {/* Create Procedure Form */}
          <div className="creation-card">
            <h3>Cadastrar Novo Procedimento</h3>
            <form onSubmit={handleCreateProcedure}>
              <div className="form-grid">
                <div className="form-group">
                  <label>Nome do Serviço *</label>
                  <input required type="text" value={procedureForm.name} onChange={e => setProcedureForm({...procedureForm, name: e.target.value})} placeholder="Ex: Corte Feminino" />
                </div>
                <div className="form-group">
                  <label>Categoria *</label>
                  <select required value={procedureForm.category} onChange={e => setProcedureForm({...procedureForm, category: e.target.value})}>
                    <option value="">Selecione...</option>
                    {Object.entries(PROCEDURE_CATEGORY_BR).map(([key, label]) => (
                      <option key={key} value={key}>{label}</option>
                    ))}
                  </select>
                </div>
                <div className="form-group">
                  <label>Duração Média (Minutos) *</label>
                  <input required type="number" min="5" step="5" value={procedureForm.averageDuration} onChange={e => setProcedureForm({...procedureForm, averageDuration: e.target.value})} placeholder="Ex: 45" />
                </div>
                <div className="form-group">
                  <label>Custo / Preço (R$) *</label>
                  <input required type="number" step="0.01" min="0" value={procedureForm.cost} onChange={e => setProcedureForm({...procedureForm, cost: e.target.value})} placeholder="0.00" />
                </div>
                <div className="form-group" style={{gridColumn: '1 / -1'}}>
                  <label>Matéria Prima / Produtos Utilizados</label>
                  <input type="text" value={procedureForm.rawMaterial} onChange={e => setProcedureForm({...procedureForm, rawMaterial: e.target.value})} placeholder="Ex: Tinta L'Oréal, Água Oxigenada..." />
                </div>
              </div>
              <button type="submit" className="submit-btn">Adicionar Procedimento</button>
            </form>
          </div>

          {/* Procedure List */}
          <div className="items-grid">
            {procedures.length === 0 ? <p>Nenhum procedimento cadastrado.</p> : null}
            {procedures.map(proc => (
              <div key={proc.id} className="item-card">
                <div>
                  <div className="item-header">
                    <h4>{proc.name}</h4>
                    <span className="badge" style={{backgroundColor: '#e8f5e9', color: '#2e7d32'}}>
                      {PROCEDURE_CATEGORY_BR[proc.category]}
                    </span>
                  </div>
                  <div className="item-body">
                    <p>⏱️ {proc.averageDuration} minutos</p>
                    <p>🧴 <strong>Materiais:</strong> {proc.rawMaterial || 'Nenhum específico'}</p>
                    <p className="price-tag" style={{marginTop: '10px'}}>R$ {proc.cost.toFixed(2)}</p>
                  </div>
                </div>
                <button className="delete-btn" onClick={() => handleDeleteProcedure(proc.id)}>Excluir Serviço</button>
              </div>
            ))}
          </div>
        </>
      )}

    </div>
  );
}