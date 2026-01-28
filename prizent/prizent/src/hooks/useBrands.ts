import { useState, useCallback } from 'react';
import { apiService } from '../services/api';

interface Brand {
  id: string;
  name: string;
  description: string;
  logo?: string;
  enabled: boolean;
  createDateTime: string;
  updateDateTime: string;
}

interface UseBrandsReturn {
  brands: Brand[];
  loading: boolean;
  error: string;
  fetchBrands: () => Promise<void>;
  createBrand: (brandData: { name: string; description: string; enabled: boolean }) => Promise<Brand>;
  updateBrand: (brandId: string, brandData: { name: string; description: string; enabled: boolean }) => Promise<Brand>;
  deleteBrand: (brandId: string) => Promise<void>;
  refreshBrands: () => Promise<void>;
}

export const useBrands = (): UseBrandsReturn => {
  const [brands, setBrands] = useState<Brand[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const fetchBrands = useCallback(async () => {
    try {
      setLoading(true);
      setError('');
      const response = await apiService.getAllBrands();
      setBrands(response || []);
    } catch (err) {
      console.error('Error fetching brands:', err);
      setError('Failed to load brands. Please try again.');
    } finally {
      setLoading(false);
    }
  }, []);

  const createBrand = useCallback(async (brandData: { name: string; description: string; enabled: boolean }) => {
    try {
      setLoading(true);
      setError('');
      const newBrand = await apiService.createBrand(brandData);
      setBrands(prevBrands => [newBrand, ...prevBrands]);
      return newBrand;
    } catch (err: any) {
      console.error('Error creating brand:', err);
      const errorMessage = err.message || 'Failed to create brand. Please try again.';
      setError(errorMessage);
      throw err;
    } finally {
      setLoading(false);
    }
  }, []);

  const updateBrand = useCallback(async (brandId: string, brandData: { name: string; description: string; enabled: boolean }) => {
    try {
      setLoading(true);
      setError('');
      const updatedBrand = await apiService.updateBrand(brandId, brandData);
      setBrands(prevBrands => 
        prevBrands.map(brand => 
          brand.id === brandId ? updatedBrand : brand
        )
      );
      return updatedBrand;
    } catch (err: any) {
      console.error('Error updating brand:', err);
      const errorMessage = err.message || 'Failed to update brand. Please try again.';
      setError(errorMessage);
      throw err;
    } finally {
      setLoading(false);
    }
  }, []);

  const deleteBrand = useCallback(async (brandId: string) => {
    try {
      setLoading(true);
      setError('');
      await apiService.deleteBrand(brandId);
      setBrands(prevBrands => prevBrands.filter(brand => brand.id !== brandId));
    } catch (err: any) {
      console.error('Error deleting brand:', err);
      const errorMessage = err.message || 'Failed to delete brand. Please try again.';
      setError(errorMessage);
      throw err;
    } finally {
      setLoading(false);
    }
  }, []);

  const refreshBrands = useCallback(async () => {
    await fetchBrands();
  }, [fetchBrands]);

  return {
    brands,
    loading,
    error,
    fetchBrands,
    createBrand,
    updateBrand,
    deleteBrand,
    refreshBrands
  };
};
