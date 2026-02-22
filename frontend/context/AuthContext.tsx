"use client";

import {
  createContext,
  useContext,
  useState,
  useEffect,
} from "react";
import api from "@/lib/api";
import { useRouter } from "next/navigation";

interface AuthContextType {
  token: string | null;
  authLoading: boolean;
  login: (username: string, password: string) => Promise<void>;
  logout: () => void;
}

const AuthContext = createContext<AuthContextType | undefined>(
  undefined
);

export function AuthProvider({
  children,
}: {
  children: React.ReactNode;
}) {
  const [token, setToken] = useState<string | null>(null);
  const [authLoading, setAuthLoading] = useState(true);
  const router = useRouter();

  // ========================================
  // HYDRATE AUTH FROM LOCAL STORAGE
  // ========================================

  useEffect(() => {
    const storedToken = localStorage.getItem("token");

    if (storedToken) {
      setToken(storedToken);
    }

    setAuthLoading(false);
  }, []);

  // ========================================
  // LOGIN
  // ========================================

  const login = async (username: string, password: string) => {
    const response = await api.post("/auth/login", {
      username,
      password,
    });

    const jwt = response.data.token;

    localStorage.setItem("token", jwt);
    setToken(jwt);

    router.push("/dashboard");
  };

  // ========================================
  // LOGOUT
  // ========================================

  const logout = () => {
    localStorage.removeItem("token");
    setToken(null);
    router.push("/");
  };

  return (
    <AuthContext.Provider
      value={{
        token,
        authLoading,
        login,
        logout,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const context = useContext(AuthContext);

  if (!context) {
    throw new Error(
      "useAuth must be used within AuthProvider"
    );
  }

  return context;
}