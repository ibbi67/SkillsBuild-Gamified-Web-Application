"use client";

import { useEffect, useState } from "react";
import { useParams } from "next/navigation";
import Link from "next/link";

export default function CoursePage() {
    const { id } = useParams(); // Retrieves the dynamic route parameter
    const [course, setCourse] = useState(null);

    useEffect(() => {
        if (!id) return;
        fetch(`http://localhost:8080/courses/${id}`)
            .then((res) => res.json())
            .then((data) => setCourse(data))
            .catch((err) => console.error("Error fetching course:", err));
    }, [id]);

    if (!course) {
        return <p>Loading...</p>;
    }

    return (
        <div className="p-6">
            <Link href="/" className="text-blue-500">
                Go Back
            </Link>
            <h1 className="text-2xl font-bold">{course.title}</h1>
            <p className="mt-2">{course.description}</p>
            {course.link && (
                <a href={course.link} target="_blank" rel="noopener noreferrer" className="text-blue-500 mt-4 block">
                    Go to Course
                </a>
            )}
        </div>
    );
}
