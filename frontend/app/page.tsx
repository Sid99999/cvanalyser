"use client";

import { useEffect } from "react";
import { useRouter } from "next/navigation";

export default function Home() {
  const router = useRouter();

  useEffect(() => {
    const token = localStorage.getItem("token");
    if (token) {
      router.push("/dashboard");
    }
  }, [router]);

  return (
    <main className="min-h-screen flex flex-col items-center justify-center bg-slate-950 text-white px-6">

      <div className="text-center max-w-2xl">

        <h1 className="text-5xl font-bold mb-6">
          AI CV Analyzer
        </h1>

        <p className="text-slate-400 mb-10 text-lg">
          Upload your resume and receive AI-powered scoring,
          ATS keyword evaluation, section breakdown,
          and detailed improvement suggestions.
        </p>

        <div className="flex justify-center gap-6">

          <button
            onClick={() => router.push("/login")}
            className="px-8 py-3 bg-indigo-600 rounded-lg hover:bg-indigo-700 transition cursor-pointer"
          >
            Login
          </button>

          <button
            onClick={() => router.push("/register")}
            className="px-8 py-3 bg-slate-800 rounded-lg hover:bg-slate-700 transition cursor-pointer"
          >
            Sign Up
          </button>

        </div>

      </div>

    </main>
  );
}