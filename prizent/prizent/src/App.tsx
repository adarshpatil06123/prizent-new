import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './contexts/AuthContext';
import ProtectedRoute from './components/ProtectedRoute';
import LoginPage from './LoginPage';
import BrandsListPage from './pages/BrandsListPage';
import AddBrandPage from './pages/AddBrandPage';
import SuperAdminUsersPage from './superadmin/SuperAdminUsersPage';
import AddUserPage from './superadmin/AddUserPage';
import EditUserPage from './superadmin/EditUserPage';

const App: React.FC = () => {
  return (
    <AuthProvider>
      <Router>
        <Routes>
          <Route path="/login" element={<LoginPage />} />
          <Route path="/superadmin" element={
            <ProtectedRoute requireAdmin>
              <SuperAdminUsersPage />
            </ProtectedRoute>
          } />
          <Route path="/superadmin/users" element={
            <ProtectedRoute requireAdmin>
              <SuperAdminUsersPage />
            </ProtectedRoute>
          } />
          <Route path="/superadmin/add-user" element={
            <ProtectedRoute requireAdmin>
              <AddUserPage />
            </ProtectedRoute>
          } />
          <Route path="/superadmin/edit-user/:userId" element={
            <ProtectedRoute requireAdmin>
              <EditUserPage />
            </ProtectedRoute>
          } />
          <Route path="/brands" element={
            <ProtectedRoute>
              <BrandsListPage />
            </ProtectedRoute>
          } />
          <Route path="/add-brand" element={
            <ProtectedRoute requireAdmin>
              <AddBrandPage />
            </ProtectedRoute>
          } />
          <Route path="/" element={<Navigate to="/login" replace />} />
        </Routes>
      </Router>
    </AuthProvider>
  );
};

export default App;
