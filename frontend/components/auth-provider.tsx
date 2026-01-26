'use client';

import { useEffect, ReactNode } from 'react';
import { usePathname, useRouter } from 'next/navigation';
import { api } from '@/lib/api';
import { useAuthStore } from '@/lib/store';

const PUBLIC_ROUTES = ['/', '/auth/login', '/auth/verify'];

export function AuthProvider({ children }: { children: ReactNode }) {
  const pathname = usePathname();
  const router = useRouter();
  const { user, hasHydrated, setUser, setLoading } = useAuthStore();
  const isPublicRoute = PUBLIC_ROUTES.includes(pathname);
  const isProtectedRoute = pathname.startsWith('/dashboard');

  useEffect(() => {
    // Wait for hydration before doing anything
    if (!hasHydrated) return;

    const initAuth = async () => {
      // On public routes, nothing to do
      if (isPublicRoute) {
        return;
      }

      // On protected routes, verify session with backend if no user in store
      if (isProtectedRoute && !user) {
        setLoading(true);
        try {
          const currentUser = await api.auth.getCurrentUser();
          setUser(currentUser);
        } catch (error) {
          // Session invalid or expired
          setUser(null);
          router.push('/auth/login');
        } finally {
          setLoading(false);
        }
      }
    };

    initAuth();
  }, [pathname, hasHydrated, user, isPublicRoute, isProtectedRoute, setUser, setLoading, router]);

  return <>{children}</>;
}
