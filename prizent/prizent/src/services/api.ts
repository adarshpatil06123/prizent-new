class ApiService {
  private baseURL = '';

  private getHeaders() {
    const token = localStorage.getItem('token');
    console.log('ApiService: Getting headers, token exists:', !!token);
    if (token) {
      try {
        const payload = JSON.parse(atob(token.split('.')[1]));
        const currentTime = Date.now() / 1000;
        console.log('ApiService: Token expires at:', payload.exp, 'Current time:', currentTime, 'Is expired:', payload.exp < currentTime);
      } catch (e) {
        console.error('ApiService: Token decode error:', e);
      }
    }
    return {
      'Content-Type': 'application/json',
      ...(token && { 'Authorization': `Bearer ${token}` })
    };
  }

  private async handleResponse(response: Response) {
    if (response.status === 401 || response.status === 403) {
      console.log('Authentication failed, clearing token and redirecting to login');
      // Token is invalid or expired, clear it and redirect to login
      localStorage.removeItem('token');
      // Use window.location.replace to avoid back button issues
      window.location.replace('/login');
      throw new Error('Authentication required');
    }
    
    if (!response.ok) {
      const errorData = await response.json().catch(() => ({ message: `HTTP error! status: ${response.status}` }));
      throw new Error(errorData.message || `HTTP error! status: ${response.status}`);
    }
    
    return response.json();
  }

  async login(username: string, password: string) {
    const response = await fetch(`${this.baseURL}/api/auth/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ 
        username, 
        password,
        clientId: "Test Client"
      })
    });
    
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }
    
    return response.json();
  }

  async logout() {
    const response = await fetch(`${this.baseURL}/api/auth/logout`, {
      method: 'POST',
      headers: this.getHeaders()
    });
    
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }
    
    return response.json();
  }

  async getAllUsers() {
    console.log('API: Making request to /api/admin/users');
    console.log('API: Headers:', this.getHeaders());
    console.log('API: Token from localStorage:', localStorage.getItem('token'));
    
    const response = await fetch(`${this.baseURL}/api/admin/users`, {
      headers: this.getHeaders()
    });
    
    console.log('API: Response status:', response.status);
    console.log('API: Response headers:', Object.fromEntries(response.headers.entries()));
    
    if (!response.ok) {
      const errorText = await response.text();
      console.error('API: Error response body:', errorText);
      console.error('API: Full response:', response);
      throw new Error(`HTTP error! status: ${response.status}, body: ${errorText}`);
    }
    
    const data = await response.json();
    console.log('API: Response data:', data);
    return data;
  }

  async createUser(userData: any) {
    const response = await fetch(`${this.baseURL}/api/admin/users`, {
      method: 'POST',
      headers: this.getHeaders(),
      body: JSON.stringify(userData)
    });
    
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }
    
    return response.json();
  }

  async getUserById(userId: string) {
    const response = await fetch(`${this.baseURL}/api/admin/users/${userId}`, {
      headers: this.getHeaders()
    });
    
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }
    
    return response.json();
  }

  async updateUser(userId: string, userData: any) {
    const response = await fetch(`${this.baseURL}/api/admin/users/${userId}`, {
      method: 'PUT',
      headers: this.getHeaders(),
      body: JSON.stringify(userData)
    });
    
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }
    
    return response.json();
  }

  async enableUser(userId: string) {
    const response = await fetch(`${this.baseURL}/api/admin/users/${userId}/enable`, {
      method: 'PATCH',
      headers: this.getHeaders()
    });
    
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }
    
    return response.json();
  }

  async disableUser(userId: string) {
    const response = await fetch(`${this.baseURL}/api/admin/users/${userId}/disable`, {
      method: 'PATCH',
      headers: this.getHeaders()
    });
    
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }
    
    return response.json();
  }

  async updateUserPassword(userId: string, password: string) {
    // Note: This endpoint may not exist yet in the backend
    // This is a placeholder for future password update functionality
    const response = await fetch(`${this.baseURL}/api/admin/users/${userId}/password`, {
      method: 'PATCH',
      headers: this.getHeaders(),
      body: JSON.stringify({ password })
    });
    
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }
    
    return response.json();
  }

  async deleteUser(userId: string) {
    const response = await fetch(`${this.baseURL}/api/admin/users/${userId}`, {
      method: 'DELETE',
      headers: this.getHeaders()
    });
    
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }
    
    return response.json();
  }

  // Brand API methods
  async getAllBrands() {
    const response = await fetch(`${this.baseURL}/api/admin/brands`, {
      headers: this.getHeaders()
    });
    
    return this.handleResponse(response);
  }

  async createBrand(brandData: { name: string; description: string; enabled: boolean }) {
    console.log('ApiService: Creating brand with data:', brandData);
    console.log('ApiService: Using headers:', this.getHeaders());
    
    const response = await fetch(`${this.baseURL}/api/admin/brands`, {
      method: 'POST',
      headers: this.getHeaders(),
      body: JSON.stringify(brandData)
    });
    
    console.log('ApiService: Create brand response status:', response.status);
    
    return this.handleResponse(response);
  }

  async updateBrand(brandId: string, brandData: { name: string; description: string; enabled: boolean }) {
    const response = await fetch(`${this.baseURL}/api/admin/brands/${brandId}`, {
      method: 'PUT',
      headers: this.getHeaders(),
      body: JSON.stringify(brandData)
    });
    
    return this.handleResponse(response);
  }

  async deleteBrand(brandId: string) {
    const response = await fetch(`${this.baseURL}/api/admin/brands/${brandId}`, {
      method: 'DELETE',
      headers: this.getHeaders()
    });
    
    return this.handleResponse(response);
  }
}

export const apiService = new ApiService();