"use client";

import React, { useEffect, useState } from "react";

// Type definition for a course
interface Course {
  id: number;
  name: string;
  url: string;
}

const FavoritesPage: React.FC = () => {
  const [favorites, setFavorites] = useState<Course[]>([]);

  // Fetch favorites from localStorage
  useEffect(() => {
    const savedFavorites = localStorage.getItem("favorites");
    if (savedFavorites) {
      setFavorites(JSON.parse(savedFavorites));
    }
  }, []);

  return (
    <div className="min-h-screen flex items-center justify-center bg-blue-50 p-6">
      <div className="w-full max-w-2xl bg-white rounded-2xl shadow-lg p-6">
        <h2 className="text-3xl font-bold text-blue-600 text-center mb-6">
          Your Favorite Courses
        </h2>

        {favorites.length === 0 ? (
          <p className="text-gray-500 text-center">No favorite courses found.</p>
        ) : (
          <ul className="space-y-3">
            {favorites.map((course) => (
              <li
                key={course.id}
                className="flex items-center justify-between bg-blue-100 p-4 rounded-lg shadow-sm hover:bg-blue-200 transition"
              >
                <a
                  href={course.url}
                  target="_blank"
                  rel="noopener noreferrer"
                  className="text-blue-700 font-medium hover:underline"
                >
                  {course.name}
                </a>
              </li>
            ))}
          </ul>
        )}

        {/* Back Button */}
        <div className="mt-6 text-center">
          <a
            href="/"
            className="inline-block px-6 py-3 bg-blue-500 text-white rounded-lg font-medium shadow-md hover:bg-blue-600 transition"
          >
            ← Back to Courses
          </a>
        </div>
      </div>
    </div>
  );
};

export default FavoritesPage;
