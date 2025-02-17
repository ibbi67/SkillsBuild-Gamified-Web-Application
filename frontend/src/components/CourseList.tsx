"use client"; 

import React from "react";

const courses = [
  { name: "Getting Started With Artificial Intelligence", url: "https://skills.yourlearning.ibm.com/activity/PLAN-E624C2604060?_gl=1*12dnqxt*_ga*MjUxNTY2NDY5LjE3MzgyNDQyMTM.*_ga_FYECCCS21D*MTczOTgwNzM1OC40LjAuMTczOTgwNzM1OC4wLjAuMA..&ngo-id=0302" },
  { name: "Cybersecurity Fundamentals", url: "https://skills.yourlearning.ibm.com/activity/PLAN-4FB8400F05FC?ngo-id=0302&_gl=1*1pij6h7*_ga*MjUxNTY2NDY5LjE3MzgyNDQyMTM.*_ga_FYECCCS21D*MTczOTgwNzM1OC40LjEuMTczOTgwNzQ3NS4wLjAuMA.." },
  { name: "Data Fundamentals", url: "https://skills.yourlearning.ibm.com/activity/PLAN-BC0FAEE8E439?ngo-id=0302&_gl=1*japszq*_ga*MjUxNTY2NDY5LjE3MzgyNDQyMTM.*_ga_FYECCCS21D*MTczOTgwNzM1OC40LjEuMTczOTgwNzUxMS4wLjAuMA.." },
];

const CourseList = () => {
    return (
      <div className="flex flex-col items-start space-y-4 p-6 w-full">
        {courses.map((course, index) => (
          <button
            key={index}
            className="w-48 h-12 bg-blue-500 text-white font-semibold rounded-lg shadow-lg hover:bg-blue-600 transition flex items-center justify-center"
            onClick={() => window.open(course.url, "_blank", "noopener,noreferrer")}
          >
            {course.name}
          </button>
        ))}
      </div>
    );
  };
  
  export default CourseList;
