import React, { useState, useEffect } from 'react';
import { GoalProgressDTO } from '@/types/goalTypes';
import axiosInstance from '@/component/axiosInstance';
import { useUpdateCourseStatus } from '@/queries/goals/useUpdateCourseStatus';
import { useQueryClient } from '@tanstack/react-query';
import toast from 'react-hot-toast';

interface GoalDashboardCardProps {
  goal: GoalProgressDTO;
}

export default function GoalDashboardCard({ goal }: GoalDashboardCardProps) {
  const { data: coursesData } = useCourses();
  const { mutate: updateCourseStatus, isPending, isError, error } = useUpdateCourseStatus();
  const [courseMap, setCourseMap] = useState<Record<number, Course>>({});
  
  // Format dates for display
  const startDate = new Date(goal.startDate).toLocaleDateString();
  const endDate = new Date(goal.endDate).toLocaleDateString();
  const daysLeft = Math.ceil((new Date(goal.endDate).getTime() - new Date().getTime()) / (1000 * 60 * 60 * 24));
  
  useEffect(() => {
    // Create a map of course IDs to Course objects for easier lookup
    if (coursesData?.data) {
      const map: Record<number, Course> = {};
      coursesData.data.forEach(course => {
        map[course.id] = course;
      });
      setCourseMap(map);
    }
  }, [coursesData]);

  useEffect(() => {
    if (isError && error) {
      toast.error(`Error updating course status: ${error.message}`);
    }
  }, [isError, error]);

  const handleCourseStatusChange = (courseId: number, completed: boolean) => {
    updateCourseStatus({
      goalId: goal.id,
      courseId,
      completed
    });
  };

  return (
    <div className="flex flex-col rounded-lg border border-blue-500 shadow-lg overflow-hidden">
      <div className="bg-blue-500 p-4">
        <h3 className="text-lg font-bold text-white">{goal.description}</h3>
        <div className="mt-2 text-sm text-white">
          <p>Target date: {startDate} - {endDate}</p>
          <p>{daysLeft > 0 ? `${daysLeft} days left` : "Goal expired"}</p>
          <p>Reward: {goal.reward}</p>
        </div>
      </div>

      <div className="p-4">
        <div className="mb-4">
          <p className="text-sm font-medium mb-1">Progress: {goal.progress.toFixed(0)}%</p>
          <div className="w-full bg-gray-200 rounded-full h-2.5">
            <div
              className="bg-blue-600 h-2.5 rounded-full"
              style={{ width: `${goal.progress}%` }}
            ></div>
          </div>
        </div>

        <h4 className="font-semibold mb-2">Courses:</h4>
        <ul className="space-y-2">
          {Object.entries(goal.courses).map(([courseId, completed]) => {
            const course = courseMap[parseInt(courseId)];
            return (
              <li key={courseId} className="flex items-center justify-between border-b pb-2">
                <div>
                  <p className="font-medium">{course?.title || `Course #${courseId}`}</p>
                  {course && <p className="text-xs text-gray-500">Est. duration: {course.estimatedDuration}h</p>}
                </div>
                <div className="flex items-center">
                  <input
                    type="checkbox"
                    checked={completed}
                    onChange={(e) => handleCourseStatusChange(parseInt(courseId), e.target.checked)}
                    disabled={isPending}
                    className="h-5 w-5 text-blue-600 rounded cursor-pointer"
                  />
                  <span className="ml-2 text-sm">{completed ? "Completed" : "In progress"}</span>
                </div>
              </li>
            );
          })}
        </ul>
      </div>
    </div>
  );
}
