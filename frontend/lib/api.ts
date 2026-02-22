import axios from "axios";

const api = axios.create({
  baseURL: "http://localhost:8080",
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem("token");

  // Do NOT attach token to auth endpoints
  const isAuthRequest =
    config.url?.includes("/auth/login") ||
    config.url?.includes("/auth/register");

  if (token && !isAuthRequest) {
    config.headers.Authorization = `Bearer ${token}`;
  }

  return config;
});

api.interceptors.response.use(
  (response) => response,
  (error) => {
    // Auto clear invalid/expired token
    if (error.response?.status === 401) {
      localStorage.removeItem("token");
    }

    return Promise.reject(error);
  }
);

export default api;