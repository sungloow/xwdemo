import http from './http';

export const uploadImage = async (file: File): Promise<string> => {
  const formData = new FormData();
  formData.append('file', file);
  const res = await http.post<any, { data: string }>('/upload/image', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  });
  return res.data;
};
