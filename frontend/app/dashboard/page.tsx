"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import api from "@/lib/api";
import ProtectedRoute from "@/components/ProtectedRoute";
import { useAuth } from "@/context/AuthContext";

interface Cv {
  id: number;
  title: string;
  fileName: string;
  uploadedAt: string;
}

export default function DashboardPage() {
  const router = useRouter();
  const { token, authLoading, logout } = useAuth();

  const [cvs, setCvs] = useState<Cv[]>([]);
  const [loading, setLoading] = useState(true);

  const [file, setFile] = useState<File | null>(null);
  const [title, setTitle] = useState("");
  const [message, setMessage] = useState("");

  // ========================================
  // FETCH CVS
  // ========================================

  const fetchCvs = async () => {
    try {
      const res = await api.get("/api/cvs");
      setCvs(res.data);
    } catch (error) {
      console.error("Failed to fetch CVs", error);
      setCvs([]);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (authLoading) return; // wait until auth initializes

    if (!token) {
      setLoading(false);
      return;
    }

    fetchCvs();
  }, [token, authLoading]);

  // ========================================
  // UPLOAD CV
  // ========================================

  const handleUpload = async () => {
    if (!file || !title) {
      setMessage("Please select a file and enter a title");
      return;
    }

    const formData = new FormData();
    formData.append("file", file);
    formData.append("title", title);

    try {
      await api.post("/api/cvs", formData);

      setMessage("CV uploaded successfully 🚀");
      setFile(null);
      setTitle("");
      await fetchCvs();
    } catch (error) {
      console.error("Upload failed", error);
      setMessage("Upload failed ❌");
    }
  };

  // ========================================
  // DELETE CV
  // ========================================

  const handleDelete = async (id: number) => {
    try {
      await api.delete(`/api/cvs/${id}`);
      setCvs((prev) => prev.filter((cv) => cv.id !== id));
    } catch (error) {
      console.error("Delete failed", error);
    }
  };

  // ========================================
  // DOWNLOAD CV
  // ========================================

  const handleDownload = async (
    id: number,
    fileName: string
  ) => {
    try {
      const response = await api.get(
        `/api/cvs/${id}/download`,
        { responseType: "blob" }
      );

      const blob = new Blob([response.data]);
      const url = window.URL.createObjectURL(blob);

      const link = document.createElement("a");
      link.href = url;
      link.download = fileName;
      link.click();

      window.URL.revokeObjectURL(url);
    } catch (error) {
      console.error("Download failed", error);
    }
  };

  // ========================================
  // NAVIGATION
  // ========================================

  const goToAnalysis = (cvId: number) => {
    router.push(`/analysis/${cvId}`);
  };

  return (
    <ProtectedRoute>
      <div className="min-h-screen bg-slate-950 text-white px-8 py-10">

        {/* HEADER */}
        <div className="flex justify-between items-center mb-10">
          <h1 className="text-3xl font-bold">
            Your CV Dashboard
          </h1>

          <button
            onClick={logout}
            className="px-4 py-2 bg-red-600 hover:bg-red-700 rounded cursor-pointer transition"
          >
            Logout
          </button>
        </div>

        {/* UPLOAD SECTION */}
        <div className="bg-slate-800 p-6 rounded-xl mb-12 max-w-lg">
          <h2 className="text-xl font-semibold mb-4">
            Upload New CV
          </h2>

          <div className="flex flex-col gap-4">

            <input
              type="text"
              placeholder="Enter CV title"
              value={title}
              onChange={(e) => setTitle(e.target.value)}
              className="p-2 rounded bg-slate-700 outline-none"
            />

            <input
              type="file"
              accept=".pdf,.docx"
              onChange={(e) =>
                setFile(e.target.files?.[0] || null)
              }
              className="p-2 rounded bg-slate-700 cursor-pointer"
            />

            <button
              onClick={handleUpload}
              className="px-4 py-2 bg-indigo-600 hover:bg-indigo-700 rounded cursor-pointer transition"
            >
              Upload CV
            </button>

            {message && (
              <p className="text-sm text-slate-300">
                {message}
              </p>
            )}
          </div>
        </div>

        {/* CV LIST */}
        <h2 className="text-2xl font-semibold mb-6">
          Your Uploaded CVs
        </h2>

        {loading && (
          <p className="text-slate-400">Loading...</p>
        )}

        {!loading && cvs.length === 0 && (
          <p className="text-slate-400">
            No CVs uploaded yet.
          </p>
        )}

        <div className="grid gap-6">
          {cvs.map((cv) => (
            <div
              key={cv.id}
              className="bg-slate-800 p-6 rounded-xl flex justify-between items-center"
            >
              <div>
                <h3 className="text-lg font-semibold">
                  {cv.title}
                </h3>
                <p className="text-sm text-slate-400">
                  File: {cv.fileName}
                </p>
                <p className="text-sm text-slate-400">
                  Uploaded:{" "}
                  {new Date(cv.uploadedAt).toLocaleString()}
                </p>
              </div>

              <div className="flex gap-3">

                <button
                  onClick={() =>
                    handleDownload(cv.id, cv.fileName)
                  }
                  className="px-3 py-1 bg-blue-600 hover:bg-blue-700 rounded text-sm cursor-pointer transition"
                >
                  Download
                </button>

                <button
                  onClick={() => handleDelete(cv.id)}
                  className="px-3 py-1 bg-red-600 hover:bg-red-700 rounded text-sm cursor-pointer transition"
                >
                  Delete
                </button>

                <button
                  onClick={() => goToAnalysis(cv.id)}
                  className="px-3 py-1 bg-purple-600 hover:bg-purple-700 rounded text-sm cursor-pointer transition"
                >
                  View Analysis
                </button>

              </div>
            </div>
          ))}
        </div>

      </div>
    </ProtectedRoute>
  );
}