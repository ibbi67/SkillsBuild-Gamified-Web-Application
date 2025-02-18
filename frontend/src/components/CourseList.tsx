"use client";

import React, { useEffect, useState } from "react";
import { Heart } from "lucide-react";

interface Course {
  id: number;
  title: string;
  description: string;
  link: string;
}

const mockCourses: Course[] = [
  { id: 1, title: "Introduction to Large Language Models", description: "", link: "https://skills.yourlearning.ibm.com/activity/MDL-568?ngo-id=0302&_gl=1*g7dncg*_ga*MjUxNTY2NDY5LjE3MzgyNDQyMTM.*_ga_FYECCCS21D*MTczOTg4NzQzNS41LjEuMTczOTg4NzQ1NS4wLjAuMA.." },
  { id: 2, title: "Getting Started with Cybersecurity", description: "", link: "https://skills.yourlearning.ibm.com/activity/PLAN-C7EE7CC95370?ngo-id=0302&_gl=1*h7d0bl*_ga*MjUxNTY2NDY5LjE3MzgyNDQyMTM.*_ga_FYECCCS21D*MTczOTg4NzQzNS41LjEuMTczOTg4NzQ3NS4wLjAuMA.." },
  { id: 3, title: "Getting Started with Data", description: "", link: "https://skills.yourlearning.ibm.com/activity/PLAN-14F2691E3A32?ngo-id=0302&_gl=1*chrsqf*_ga*MjUxNTY2NDY5LjE3MzgyNDQyMTM.*_ga_FYECCCS21D*MTczOTg4NzQzNS41LjEuMTczOTg4NzUwMS4wLjAuMA.." },
];

const CourseList: React.FC = () => {
  const [courses, setCourses] = useState<Course[]>([]);
  const [favorites, setFavorites] = useState<Course[]>([]);

  useEffect(() => {
    setCourses(mockCourses); // Load mock data
    const savedFavorites = localStorage.getItem("favorites");
    if (savedFavorites) {
      setFavorites(JSON.parse(savedFavorites));
    }
  }, []);

  const toggleFavorite = (course: Course) => {
    const isFavorite = favorites.some((fav) => fav.id === course.id);
    let updatedFavorites;

    if (isFavorite) {
      updatedFavorites = favorites.filter((fav) => fav.id !== course.id);
    } else {
      updatedFavorites = [...favorites, course];
    }

    setFavorites(updatedFavorites);
    localStorage.setItem("favorites", JSON.stringify(updatedFavorites));
  };

  return (
    <div className="p-6 w-full flex flex-col space-y-6">
      <h2 className="text-2xl font-bold text-blue-600">Available Courses</h2>

      <div className="flex flex-col items-start space-y-4">
        {courses.length === 0 ? (
          <p>No courses available at the moment.</p>
        ) : (
          courses.map((course) => (
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

              <button onClick={() => toggleFavorite(course)}>
                <Heart
                  className={`w-5 h-5 transition ${
                    favorites.some((fav) => fav.id === course.id)
                      ? "fill-red-500 text-red-500"
                      : "text-white"
                  }`}
                />
              </button>
            </div>
          ))
        )}
      </div>

      <div className="mt-6">
        <a href="/favorites" className="text-blue-600 hover:text-blue-800">
          View Favorite Courses
        </a>
      </div>
    </div>
  );
};

export default CourseList;
