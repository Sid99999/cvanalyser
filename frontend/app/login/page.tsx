"use client";

import { useState } from "react";
import { useAuth } from "@/context/AuthContext";
import Link from "next/link";

export default function LoginPage() {
  const { login } = useAuth();

  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!username || !password) return;

    setLoading(true);
    try {
      await login(username, password);
    } catch {
      alert("Invalid credentials");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-slate-950 text-white px-4">
      <div className="w-full max-w-sm bg-slate-900 p-8 rounded-2xl shadow-xl">

        <h2 className="text-2xl font-bold mb-6 text-center">
          Welcome Back
        </h2>

        <form onSubmit={handleSubmit} className="flex flex-col gap-4">

          <input
            type="text"
            placeholder="Username"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            className="p-3 rounded bg-slate-800 outline-none focus:ring-2 focus:ring-indigo-500"
          />

          <input
            type="password"
            placeholder="Password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            className="p-3 rounded bg-slate-800 outline-none focus:ring-2 focus:ring-indigo-500"
          />

          <button
            type="submit"
            disabled={loading}
            className="bg-indigo-600 hover:bg-indigo-700 p-3 rounded font-semibold transition cursor-pointer disabled:opacity-50"
          >
            {loading ? "Logging in..." : "Login"}
          </button>

        </form>

        {/* Navigation Links */}
        <div className="mt-6 text-center text-sm text-slate-400 space-y-2">
          <p>
            Don’t have an account?{" "}
            <Link
              href="/register"
              className="text-indigo-400 hover:underline"
            >
              Register
            </Link>
          </p>

          <p>
            <Link
              href="/"
              className="text-slate-500 hover:underline"
            >
              Back to Home
            </Link>
          </p>
        </div>

      </div>
    </div>
  );
}