import React from "react";

const courses = [
  { name: "Course 1", url: "https://example.com/course1" },
  { name: "Course 2", url: "https://example.com/course2" },
  { name: "Course 3", url: "https://example.com/course3" },
];

const CourseList = () => {
  return (
    <div className="flex flex-col items-center space-y-4 p-6">
      {courses.map((course, index) => (
        <button
          key={index}
          className="bg-blue-500 text-white px-6 py-3 rounded-lg shadow-lg hover:bg-blue-600 transition"
          onClick={() => window.open(course.url, "_blank", "noopener,noreferrer")}
        >
          {course.name}
        </button>
      ))}
    </div>
  );
};

export default CourseList;
