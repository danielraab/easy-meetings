'use client';

import { useAuthStore } from '@/lib/store';

export default function DashboardLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  const { isLoading } = useAuthStore();

  if (isLoading) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <p className="text-muted-foreground">Loading...</p>
      </div>
    );
  }

  return <>{children}</>;
}
