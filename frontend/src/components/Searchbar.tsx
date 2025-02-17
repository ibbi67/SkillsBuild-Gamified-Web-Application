"use client";

import { useState, useEffect } from "react";
import Link from "next/link";

export default function SearchBar() {
    const [query, setQuery] = useState("");
    const [courses, setCourses] = useState([]);
    const [filteredCourses, setFilteredCourses] = useState([]);

    useEffect(() => {
        fetch("http://localhost:8080/courses")
            .then((response) => response.json())
            .then((data) => setCourses(data))
            .catch((error) => console.error("Error fetching courses:", error));
    }, []);

    useEffect(() => {
        if (query.trim() === "") {
            setFilteredCourses([]);
            return;
        }

        const results = courses.filter((course: any) =>
            course.title?.toLowerCase().includes(query.toLowerCase())
        );
        setFilteredCourses(results);
    }, [query, courses]);

    return (
        <div className="relative">
            <input
                type="text"
                value={query}
                onChange={(e) => setQuery(e.target.value)}
                placeholder="Search courses..."
                className="border rounded px-3 py-2 w-64"
            />

            {query && filteredCourses.length > 0 && (
                <div className="absolute mt-2 w-64 bg-white border shadow-lg rounded">
                    {filteredCourses.map((course: any) => (
                        <Link key={course.id} href={`/courses/${course.id}`} passHref>
                            <div key={course.id} className="p-2 hover:bg-gray-100 cursor-pointer">
                                {course.title}
                            </div>
                        </Link> 
                    ))}
                </div>
            )}
        </div>
    );
}
