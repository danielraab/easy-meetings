import { create } from 'zustand';
import { persist, createJSONStorage } from 'zustand/middleware';

interface User {
  id: string;
  email: string;
  name: string;
  avatarUrl?: string;
  isActive: boolean;
}

interface AuthState {
  user: User | null;
  isLoading: boolean;
  hasHydrated: boolean;
  setUser: (user: User | null) => void;
  setLoading: (loading: boolean) => void;
  logout: () => void;
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set) => ({
      user: null,
      isLoading: false,
      hasHydrated: false,
      setUser: (user) => set({ user, isLoading: false }),
      setLoading: (isLoading) => set({ isLoading }),
      logout: () => set({ user: null }),
    }),
    {
      name: 'auth-storage',
      storage: createJSONStorage(() => localStorage),
      partialize: (state) => ({ user: state.user }),
      onRehydrateStorage: () => (state) => {
        if (state) {
          state.hasHydrated = true;
          state.isLoading = false;
        }
      },
    }
  )
);
