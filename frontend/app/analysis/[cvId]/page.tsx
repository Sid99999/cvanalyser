"use client";
import Link from "next/link";
import { useEffect, useState } from "react";
import { useParams } from "next/navigation";
import api from "@/lib/api";
import ProtectedRoute from "@/components/ProtectedRoute";

interface AnalysisResult {
  id: number;
  status: string;
  finalScore: number;
  sectionScores: Record<string, number>;
  strengths: string;
  weaknesses: string;
  suggestions: string;
}

interface HistoryItem {
  id: number;
  status: string;
  finalScore: number;
  createdAt: string;
}

const MAX_JD_LENGTH = 5000;

export default function AnalysisPage() {
  const { cvId } = useParams() as { cvId: string };

  const [loading, setLoading] = useState(true);
  const [analyzing, setAnalyzing] = useState(false);

  const [jobDescription, setJobDescription] = useState("");

  const [result, setResult] = useState<AnalysisResult | null>(null);
  const [history, setHistory] = useState<HistoryItem[]>([]);

  // ==============================
  // FETCH DATA
  // ==============================

  const fetchData = async () => {
    try {
      setLoading(true);

      const [latestRes, historyRes] = await Promise.all([
        api.get(`/api/cvs/${cvId}/analysis/latest`).catch(() => null),
        api.get(`/api/cvs/${cvId}/analyses`).catch(() => null),
      ]);

      setResult(latestRes?.data ?? null);
      setHistory(historyRes?.data ?? []);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (!cvId) return;
    fetchData();
  }, [cvId]);

  // ==============================
  // RUN ANALYSIS
  // ==============================

  const handleAnalyze = async () => {
    try {
      setAnalyzing(true);

      await api.post(`/api/cvs/${cvId}/analysis`, {
        jobDescription: jobDescription || null,
      });

      await fetchData();
    } finally {
      setAnalyzing(false);
    }
  };

  const getScoreColor = (score: number) => {
    if (score >= 80) return "text-green-400";
    if (score >= 60) return "text-yellow-400";
    return "text-red-400";
  };

  return (
    <ProtectedRoute>
      <div className="min-h-screen bg-slate-950 text-white px-6 py-10 max-w-6xl mx-auto">
        <div className="mb-6">
          <Link
            href="/dashboard"
            className="text-sm text-slate-400 hover:text-white transition"
          >
            ← Back to Dashboard
          </Link>
        </div>
        {/* HEADER */}
        <h1 className="text-3xl font-bold mb-10 text-center">
          CV Analysis
        </h1>

        {/* JD + RUN SECTION */}
        <div className="bg-slate-800 rounded-2xl p-8 mb-12">

          <h2 className="text-xl font-semibold mb-4">
            Job Description (Optional)
          </h2>

          <textarea
            value={jobDescription}
            onChange={(e) => setJobDescription(e.target.value)}
            maxLength={MAX_JD_LENGTH}
            rows={6}
            className="w-full p-4 rounded bg-slate-900 outline-none resize-none focus:ring-2 focus:ring-purple-500"
            placeholder="Paste job description here for better alignment scoring..."
          />

          <div className="text-right text-xs text-slate-400 mt-2">
            {jobDescription.length} / {MAX_JD_LENGTH} characters
          </div>

          <div className="text-center mt-6">
            <button
              onClick={handleAnalyze}
              disabled={analyzing}
              className="px-8 py-3 bg-purple-600 hover:bg-purple-700 rounded-xl font-semibold transition cursor-pointer disabled:opacity-50"
            >
              {analyzing ? "Analyzing..." : "Run Analysis"}
            </button>
          </div>
        </div>

        {/* LOADING */}
        {loading && (
          <div className="animate-pulse h-32 bg-slate-800 rounded-xl mb-8"></div>
        )}

        {/* RESULT */}
        {!loading && result && (
          <>
            {/* SCORE */}
            <div className="bg-slate-800 rounded-2xl p-10 mb-10 text-center shadow-lg">
              <div
                className={`text-7xl font-bold ${getScoreColor(result.finalScore)}`}
              >
                {result.finalScore}
              </div>
              <p className="text-slate-400 mt-2">Final Score</p>
            </div>

            {/* SECTION BREAKDOWN */}
            <div className="grid md:grid-cols-2 gap-6 mb-10">
              {Object.entries(result.sectionScores || {}).map(([key, value]) => (
                <div key={key} className="bg-slate-800 p-6 rounded-xl">
                  <div className="flex justify-between mb-2">
                    <span className="capitalize">
                      {key.replace("Score", "")}
                    </span>
                    <span>{value}</span>
                  </div>

                  <div className="w-full bg-slate-700 h-2 rounded">
                    <div
                      className="bg-purple-600 h-2 rounded transition-all"
                      style={{ width: `${value}%` }}
                    />
                  </div>
                </div>
              ))}
            </div>

            {/* TEXT FEEDBACK */}
            <div className="grid md:grid-cols-3 gap-6 mb-10 text-sm">
              <div className="bg-slate-800 p-6 rounded-xl">
                <h3 className="text-green-400 font-semibold mb-3">Strengths</h3>
                <pre className="whitespace-pre-wrap">{result.strengths}</pre>
              </div>

              <div className="bg-slate-800 p-6 rounded-xl">
                <h3 className="text-red-400 font-semibold mb-3">Weaknesses</h3>
                <pre className="whitespace-pre-wrap">{result.weaknesses}</pre>
              </div>

              <div className="bg-slate-800 p-6 rounded-xl">
                <h3 className="text-indigo-400 font-semibold mb-3">Suggestions</h3>
                <pre className="whitespace-pre-wrap">{result.suggestions}</pre>
              </div>
            </div>

            {/* HISTORY */}
            <div className="bg-slate-800 rounded-xl p-6">
              <h2 className="text-xl font-semibold mb-6">
                Analysis History
              </h2>

              {history.map((item) => (
                <div
                  key={item.id}
                  className="flex justify-between items-center bg-slate-900 p-4 rounded mb-4"
                >
                  <div>
                    <p className="text-xs text-slate-400">
                      {new Date(item.createdAt).toLocaleString()}
                    </p>
                    <p>Status: {item.status}</p>
                    <p className="font-semibold">
                      Score:{" "}
                      {item.status === "COMPLETED" && item.finalScore > 0
                        ? item.finalScore
                        : <span className="text-slate-500">-</span>}
                    </p>
                  </div>
                </div>
              ))}
            </div>
          </>
        )}

      </div>
    </ProtectedRoute>
  );
}