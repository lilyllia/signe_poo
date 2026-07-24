import { useEffect, useState } from 'react';
import api from '../../services/api.js';

function EmployeesPage() {
  const [Employees, setEmployees] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const [selectedEmployee, setSelectedEmployee] = useState(null);

  useEffect(() => {
    fetchEmployees();
  }, []);

  async function fetchEmployees() {
    setLoading(true);
    try {
      const response = await api.get('/api/employees');
      setEmployees(response.data);
    } catch (err) {
      setError('Não foi possível carregar os funcionários. Cheque se o banco de dados está rodando.');
    } finally {
      setLoading(false);
    }
  }

  function handleEmployeeCreated(newEmployee) {
    setEmployees((CurrentEmployees) => [...CurrentEmployees, newEmployee]);
  }

  async function handleEditClick(Id) {
    try {
      const response = await api.get(`/api/employees/${employeeId}/profile`);
      setSelectedEmployee(response.data);
    } catch (err) {
      alert("Erro ao carregar detalhes do cliente.");
    }
  }

  function handleBackClick() {
    setSelectedEmployee(null); 
  }

return(
    <div>
        oi
    </div>
);

}

export default EmployeesPage;