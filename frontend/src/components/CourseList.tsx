"use client";

import React, { useState } from "react";
import { Heart } from "lucide-react";


interface Course {
  id: number;
  name: string;
  url: string;
}

const courses: Course[] = [
  { id: 1, name: "Course 1", url: "https://example.com/course1" },
  { id: 2, name: "Course 2", url: "https://example.com/course2" },
  { id: 3, name: "Course 3", url: "https://example.com/course3" },
];

const CourseList: React.FC = () => {
  const [favorites, setFavorites] = useState<Course[]>(() => {

    const savedFavorites = localStorage.getItem("favorites");
    return savedFavorites ? JSON.parse(savedFavorites) : [];
  });

  const toggleFavorite = (course: Course) => {
    const updatedFavorites = favorites.some((fav) => fav.id === course.id)
      ? favorites.filter((fav) => fav.id !== course.id) 
      : [...favorites, course]; 

    setFavorites(updatedFavorites);
    localStorage.setItem("favorites", JSON.stringify(updatedFavorites)); 
  };

  return (
    <div className="p-6 w-full flex flex-col space-y-6">
      {/* Course List */}
      <div className="flex flex-col items-start space-y-4">
        {courses.map((course) => (
          <div
            key={course.id}
            className="w-64 h-12 bg-blue-500 text-white font-semibold rounded-lg shadow-lg flex items-center justify-between px-4 cursor-pointer hover:bg-blue-600 transition"
          >
            <button
              className="flex-1 text-left"
              onClick={() => window.open(course.url, "_blank", "noopener,noreferrer")}
            >
              {course.name}
            </button>

            {/* Heart Icon (Favorite Toggle) */}
            <button onClick={() => toggleFavorite(course)}>
              <Heart
                className={`w-5 h-5 transition ${
                  favorites.some((fav) => fav.id === course.id) ? "fill-red-500 text-red-500" : "text-white"
                }`}
              />
            </button>
          </div>
        ))}
      </div>

      {/* Link to Favorites Page */}
      <div className="mt-6">
        <a
          href="/favorites"
          className="text-blue-600 hover:text-blue-800"
        >
          View Favorite Courses
        </a>
      </div>
    </div>
  );
};

export default CourseList;