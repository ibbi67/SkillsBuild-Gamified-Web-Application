"use client";

import { useCourse } from "@/queries/courses/useCourse";
import { useRouter, useParams } from "next/navigation";
import CommentSection from "@/component/comment/CommentsSection";
import { useEffect, useState } from "react";
import { Course } from "@/types/databaseTypes";

export default function CourseDetailPage() {
    const params = useParams();
    const router = useRouter();
    const courseId = parseInt(params.id as string);

    const [isPageLoading, setIsPageLoading] = useState(true);
    const [isPageError, setIsPageError] = useState(false);
    const [errorMessage, setErrorMessage] = useState("");
    const [course, setCourse] = useState<Course | null>(null);

    const { data: courseData, isLoading, isError, error } = useCourse(courseId);

    useEffect(() => {
        if (!isLoading) {
            setIsPageLoading(false);

            if (isError || !courseData) {
                setIsPageError(true);
                setErrorMessage(error?.message || "Course not found");
            } else if (courseData) {
                setCourse(courseData.data);
                setIsPageError(false);
            }
        }
    }, [isLoading, isError, courseData, error]);

    return (
        <div className="m-4 grow flex flex-col gap-4 rounded-lg bg-white p-4 shadow-lg">
            {isPageLoading ? (
                <p>Loading course details...</p>
            ) : isPageError ? (
                <>
                    <p>Error loading course: {errorMessage}</p>
                    <button
                        className="px-4 py-2 bg-blue-500 text-white rounded-lg w-fit"
                        onClick={() => router.push("/course")}
                    >
                        Back to Courses
                    </button>
                </>
            ) : course && (
                <>
                    <div className="flex justify-between items-center mb-4">
                        <h1 className="text-2xl font-bold">{course.title}</h1>
                        <button
                            className="px-4 py-2 bg-gray-200 rounded-lg"
                            onClick={() => router.push("/course")}
                        >
                            Back to Courses
                        </button>
                    </div>

                    <div className="bg-gray-50 p-4 rounded-lg mb-4">
                        <h2 className="text-xl font-semibold mb-2">Course Details</h2>
                        <p className="mb-2">
                            <span className="font-medium">Description:</span> {course.description}
                        </p>

                        {course.difficulty !== undefined && (
                            <p className="mb-2">
                                <span className="font-medium">Difficulty:</span> {course.difficulty}/5
                            </p>
                        )}

                        {course.estimatedDuration && (
                            <p className="mb-2">
                                <span className="font-medium">Estimated Duration:</span>{" "}
                                {course.estimatedDuration} hours
                            </p>
                        )}

                        {course.link && (
                            <p>
                                <span className="font-medium">Course Link:</span>{" "}
                                <a
                                    href={course.link}
                                    target="_blank"
                                    rel="noopener noreferrer"
                                    className="text-blue-500 hover:underline"
                                >
                                    {course.link}
                                </a>
                            </p>
                        )}
                    </div>

                    <CommentSection courseId={courseId} />
                </>
            )}
        </div>
    );
}