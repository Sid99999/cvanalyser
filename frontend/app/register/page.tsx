"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import api from "@/lib/api";
import Link from "next/link";

export default function RegisterPage() {
  const router = useRouter();

  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!username || !password) return;

    setLoading(true);

    try {
      await api.post("/auth/register", {
        username,
        password,
      });

      alert("Registration successful! Please login.");
      router.push("/login");

    } catch (error: any) {
      alert(error.response?.data || "Registration failed");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-slate-950 text-white px-4">
      <div className="w-full max-w-sm bg-slate-900 p-8 rounded-2xl shadow-xl">

        <h2 className="text-2xl font-bold mb-6 text-center">
          Create Account
        </h2>

        <form onSubmit={handleSubmit} className="flex flex-col gap-4">

          <input
            type="text"
            placeholder="Choose a username"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            className="p-3 rounded bg-slate-800 outline-none focus:ring-2 focus:ring-indigo-500"
          />

          <PasswordInput
            placeholder="Choose a password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
          />

          <button
            type="submit"
            disabled={loading}
            className="bg-indigo-600 hover:bg-indigo-700 p-3 rounded font-semibold transition cursor-pointer disabled:opacity-50"
          >
            {loading ? "Creating account..." : "Register"}
          </button>

        </form>

        {/* Navigation Links */}
        <div className="mt-6 text-center text-sm text-slate-400 space-y-2">
          <p>
            Already have an account?{" "}
            <Link
              href="/login"
              className="text-indigo-400 hover:underline"
            >
              Login
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