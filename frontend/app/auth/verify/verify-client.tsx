'use client';

import { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import { api } from '@/lib/api';
import { useAuthStore } from '@/lib/store';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';

interface VerifyClientProps {
  token: string | null;
}

export default function VerifyClient({ token }: VerifyClientProps) {
  const [status, setStatus] = useState<'verifying' | 'success' | 'error'>('verifying');
  const [message, setMessage] = useState('Verifying your magic link...');
  const router = useRouter();
  const setUser = useAuthStore((state) => state.setUser);

  useEffect(() => {
    if (!token) {
      setStatus('error');
      setMessage('Invalid verification link');
      return;
    }

    const verify = async () => {
      try {
        const response = await api.auth.verifyMagicLink(token);
        setUser(response.user);
        setStatus('success');
        setMessage('Successfully signed in! Redirecting...');
        setTimeout(() => {
          router.push('/dashboard');
        }, 1500);
      } catch (error) {
        setStatus('error');
        setMessage('Verification failed. The link may have expired.');
      }
    };

    verify();
  }, [token, router, setUser]);

  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-purple-50 to-blue-50 p-4">
      <Card className="w-full max-w-md">
        <CardHeader>
          <CardTitle className="text-center">
            {status === 'verifying' && 'Verifying...'}
            {status === 'success' && '✓ Success!'}
            {status === 'error' && '✗ Error'}
          </CardTitle>
        </CardHeader>
        <CardContent>
          <p className="text-center text-muted-foreground">{message}</p>
        </CardContent>
      </Card>
    </div>
  );
}
