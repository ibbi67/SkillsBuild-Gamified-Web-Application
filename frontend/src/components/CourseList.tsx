"use client";

import React, { useEffect, useState } from "react";
import { Heart } from "lucide-react";

interface Course {
  id: number;
  title: string;
  description: string;
  link: string;
}

const CourseList: React.FC = () => {
  const [courses, setCourses] = useState<Course[]>([]);
  const [favorites, setFavorites] = useState<Course[]>([]);

  // Fetch courses from backend
  useEffect(() => {
    fetch("http://localhost:8080/api/courses")
      .then((res) => res.json())
      .then((data) => setCourses(data))
      .catch((error) => console.error("Error fetching courses:", error));
  }, []);

  // Fetch favorite courses from backend
  useEffect(() => {
    fetch("http://localhost:8080/api/courses")
      .then((res) => res.json())
      .then((data) => setFavorites(data))
      .catch((error) => console.error("Error fetching favorites:", error));
  }, []);

  // Toggle favorite
  const toggleFavorite = async (course: Course) => {
    const isFavorite = favorites.some((fav) => fav.id === course.id);

    if (isFavorite) {
      // Remove from favorites
      await fetch(`http://localhost:8080/api/courses/${course.id}`, {
        method: "DELETE",
      });
      setFavorites(favorites.filter((fav) => fav.id !== course.id));
    } else {
      // Add to favorites
      const response = await fetch("http://localhost:8080/api/courses", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(course),
      });

      if (response.ok) {
        setFavorites([...favorites, course]);
      }
    }
  };

  return (
    <div className="p-6 w-full flex flex-col space-y-6">
      <h2 className="text-2xl font-bold text-blue-600">Available Courses</h2>

      {/* Course List */}
      <div className="flex flex-col items-start space-y-4">
        {courses.map((course) => (
          <div
            key={course.id}
            className="w-64 h-16 bg-blue-500 text-white font-semibold rounded-lg shadow-lg flex items-center justify-between px-4 cursor-pointer hover:bg-blue-600 transition"
          >
            <button
              className="flex-1 text-left"
              onClick={() => window.open(course.link, "_blank", "noopener,noreferrer")}
            >
              {course.title}
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
        <a href="/favorites" className="text-blue-600 hover:text-blue-800">
          View Favorite Courses
        </a>
      </div>
    </div>
  );
};

export default CourseList;
