import VerifyClient from './verify-client';

interface VerifyPageProps {
  searchParams: Promise<{ token?: string }>;
}

export default async function VerifyPage({ searchParams }: VerifyPageProps) {
  const params = await searchParams;
  return <VerifyClient token={params.token ?? null} />;
}
