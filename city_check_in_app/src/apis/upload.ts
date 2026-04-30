import axios from './http';

/**
 * 上传图片
 * @param file 图片文件，支持 jpg/png/gif/webp，单文件不超过 10MB
 * @returns 图片可访问 URL，如 http://localhost:8080/uploads/2026/03/31/xxx.png
 */
export const uploadImage = async (file: File): Promise<string> => {
  const formData = new FormData();
  formData.append('file', file);
  const res = await axios.post<any, { data: string }>('/api/upload/image', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  });
  return res.data;
};
