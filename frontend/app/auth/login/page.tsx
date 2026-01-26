import LoginClient from './login-client';

interface LoginPageProps {
  searchParams: Promise<{ email?: string }>;
}

export default async function LoginPage({ searchParams }: LoginPageProps) {
  const params = await searchParams;
  return <LoginClient initialEmail={params.email} />;
}
