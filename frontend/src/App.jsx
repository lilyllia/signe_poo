import { Routes, Route } from 'react-router-dom';
import Navbar from './components/layout/Navbar.jsx';
import Home from './pages/Home.jsx';
import ClientsPage from './pages/clients/ClientsPage.jsx';
import EmployeesPage from './pages/employees/EmployeesPage.jsx';
import ServicesPage from './pages/services-products/ServicesPage.jsx';

function App() {
  return (
    <>
      <Navbar />
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/clients" element={<ClientsPage />} />
        <Route path="/employees" element={<EmployeesPage/>} />
        <Route path="/services" element={<ServicesPage/>} />
      </Routes>
    </>
  );
}

export default App;