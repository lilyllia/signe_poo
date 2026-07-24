import { NavLink } from 'react-router-dom';
import './Navbar.css';

function Navbar() {
  return (
    <nav className="navbar">
      <span className="navbar-brand">SIGNE</span>
      <div className="navbar-links">
        <NavLink to="/" end className="navbar-link">Início</NavLink>
        <NavLink to="/clients" className="navbar-link">Clientes</NavLink>
        <NavLink to="/employees" className="navbar-link">Funcionários</NavLink>
        <NavLink to="/services" className="navbar-link">Estoque & Serviços</NavLink>
        <NavLink to="/reports" className="navbar-link">Relatórios</NavLink>
      </div>
    </nav>
  );
}

export default Navbar;