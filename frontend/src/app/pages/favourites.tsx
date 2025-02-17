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
    <div className="p-6 w-full flex flex-col space-y-6">
      <h2 className="text-2xl font-bold mb-4">Your Favorite Courses</h2>
      {favorites.length === 0 ? (
        <p>No favorite courses found.</p>
      ) : (
        <ul className="list-disc list-inside">
          {favorites.map((course) => (
            <li key={course.id} className="text-blue-600">
              <a href={course.url} target="_blank" rel="noopener noreferrer">
                {course.name}
              </a>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
};

export default FavoritesPage;