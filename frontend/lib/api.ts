const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080';

interface ApiOptions extends RequestInit {
  requiresAuth?: boolean;
}

async function fetchWithAuth(url: string, options: ApiOptions = {}) {
  const { requiresAuth = true, ...fetchOptions } = options;

  const headers: HeadersInit = {
    'Content-Type': 'application/json',
    ...fetchOptions.headers,
  };

  const response = await fetch(`${API_BASE_URL}${url}`, {
    ...fetchOptions,
    headers,
    credentials: 'include', // Important for cookies
  });

  if (response.status === 401 && requiresAuth) {
    // Redirect to login
    window.location.href = '/auth/login';
    throw new Error('Unauthorized');
  }

  if (!response.ok) {
    const error = await response.json().catch(() => ({ error: 'An error occurred' }));
    throw new Error(error.error || 'An error occurred');
  }

  if (response.status === 204) {
    return null;
  }

  return response.json();
}

export const api = {
  // Auth
  auth: {
    requestMagicLink: (email: string) =>
      fetchWithAuth('/api/auth/magic-link', {
        method: 'POST',
        body: JSON.stringify({ email }),
        requiresAuth: false,
      }),
    verifyMagicLink: (token: string) =>
      fetchWithAuth('/api/auth/magic-link/verify', {
        method: 'POST',
        body: JSON.stringify({ token }),
        requiresAuth: false,
      }),
    getCurrentUser: () => fetchWithAuth('/api/auth/me'),
    logout: () => fetchWithAuth('/api/auth/logout', { method: 'POST' }),
  },

  // Meeting Series
  meetingSeries: {
    list: () => fetchWithAuth('/api/meeting-series'),
    get: (id: string) => fetchWithAuth(`/api/meeting-series/${id}`),
    create: (data: any) =>
      fetchWithAuth('/api/meeting-series', {
        method: 'POST',
        body: JSON.stringify(data),
      }),
    update: (id: string, data: any) =>
      fetchWithAuth(`/api/meeting-series/${id}`, {
        method: 'PUT',
        body: JSON.stringify(data),
      }),
    delete: (id: string) =>
      fetchWithAuth(`/api/meeting-series/${id}`, { method: 'DELETE' }),
    
    // Members
    inviteMember: (seriesId: string, data: any) =>
      fetchWithAuth(`/api/meeting-series/${seriesId}/members`, {
        method: 'POST',
        body: JSON.stringify(data),
      }),
    listMembers: (seriesId: string) =>
      fetchWithAuth(`/api/meeting-series/${seriesId}/members`),
    updateMemberRole: (seriesId: string, memberId: string, data: any) =>
      fetchWithAuth(`/api/meeting-series/${seriesId}/members/${memberId}`, {
        method: 'PUT',
        body: JSON.stringify(data),
      }),
    removeMember: (seriesId: string, memberId: string) =>
      fetchWithAuth(`/api/meeting-series/${seriesId}/members/${memberId}`, {
        method: 'DELETE',
      }),
  },

  // Appointments
  appointments: {
    list: (seriesId: string) =>
      fetchWithAuth(`/api/meeting-series/${seriesId}/appointments`),
    get: (appointmentId: string) =>
      fetchWithAuth(`/api/meeting-series/_/appointments/${appointmentId}`),
    create: (seriesId: string, data: any) =>
      fetchWithAuth(`/api/meeting-series/${seriesId}/appointments`, {
        method: 'POST',
        body: JSON.stringify(data),
      }),
    update: (appointmentId: string, data: any) =>
      fetchWithAuth(`/api/meeting-series/_/appointments/${appointmentId}`, {
        method: 'PUT',
        body: JSON.stringify(data),
      }),
    delete: (appointmentId: string) =>
      fetchWithAuth(`/api/meeting-series/_/appointments/${appointmentId}`, {
        method: 'DELETE',
      }),
  },

  // Areas
  areas: {
    list: (seriesId: string) =>
      fetchWithAuth(`/api/meeting-series/${seriesId}/areas`),
    create: (seriesId: string, data: any) =>
      fetchWithAuth(`/api/meeting-series/${seriesId}/areas`, {
        method: 'POST',
        body: JSON.stringify(data),
      }),
    update: (areaId: string, data: any) =>
      fetchWithAuth(`/api/meeting-series/_/areas/${areaId}`, {
        method: 'PUT',
        body: JSON.stringify(data),
      }),
    delete: (areaId: string) =>
      fetchWithAuth(`/api/meeting-series/_/areas/${areaId}`, {
        method: 'DELETE',
      }),
  },

  // Topics
  topics: {
    list: (areaId: string) => fetchWithAuth(`/api/areas/${areaId}/topics`),
    create: (areaId: string, data: any) =>
      fetchWithAuth(`/api/areas/${areaId}/topics`, {
        method: 'POST',
        body: JSON.stringify(data),
      }),
    update: (topicId: string, data: any) =>
      fetchWithAuth(`/api/areas/_/topics/${topicId}`, {
        method: 'PUT',
        body: JSON.stringify(data),
      }),
    delete: (topicId: string) =>
      fetchWithAuth(`/api/areas/_/topics/${topicId}`, { method: 'DELETE' }),
  },

  // Entries
  entries: {
    listByTopic: (topicId: string) =>
      fetchWithAuth(`/api/entries/topic/${topicId}`),
    listByAppointment: (appointmentId: string) =>
      fetchWithAuth(`/api/entries/appointment/${appointmentId}`),
    create: (data: any) =>
      fetchWithAuth('/api/entries', {
        method: 'POST',
        body: JSON.stringify(data),
      }),
    update: (entryId: string, data: any) =>
      fetchWithAuth(`/api/entries/${entryId}`, {
        method: 'PUT',
        body: JSON.stringify(data),
      }),
    delete: (entryId: string) =>
      fetchWithAuth(`/api/entries/${entryId}`, { method: 'DELETE' }),
  },
};
