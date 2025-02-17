"use client";

import React, { useState } from "react";
import { Heart } from "lucide-react";

// Type definition for a course
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
    const [favorites, setFavorites] = useState<Course[]>([]);
  
    // Toggle favorite courses
    const toggleFavorite = (course: Course) => {
      setFavorites((prevFavorites) =>
        prevFavorites.some((fav) => fav.id === course.id)
          ? prevFavorites.filter((fav) => fav.id !== course.id) // Remove if exists
          : [...prevFavorites, course] // Add if not exists
      );
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
  
        {/* Favorite Courses List */}
        {favorites.length > 0 && (
          <div className="mt-6">
            <h2 className="text-lg font-bold mb-3">Favorite Courses:</h2>
            <ul className="list-disc list-inside">
              {favorites.map((course) => (
                <li key={course.id} className="text-blue-600">
                  <a href={course.url} target="_blank" rel="noopener noreferrer">
                    {course.name}
                  </a>
                </li>
              ))}
            </ul>
          </div>
        )}
      </div>
    );
  };
  
  export default CourseList;