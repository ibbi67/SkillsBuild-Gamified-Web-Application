import React, { useState, useEffect } from 'react';
import { GoalProgressDTO } from '@/types/goalTypes';
import axiosInstance from '@/component/axiosInstance';
import { useUpdateEnrollmentCompletion } from '@/queries/goals/useUpdateEnrollmentCompletion';
import { useQueryClient } from '@tanstack/react-query';
import toast from 'react-hot-toast';

interface GoalDashboardCardProps {
  goal: GoalProgressDTO;
}

interface CourseInfo {
  id: number;
  name?: string;
  title?: string;
  completed: boolean;
}

const GoalDashboardCard: React.FC<GoalDashboardCardProps> = ({ goal }) => {
  const [showCourses, setShowCourses] = useState(false);
  const [courseDetails, setCourseDetails] = useState<CourseInfo[]>([]);
  const [loading, setLoading] = useState(false);
  const [updatingCourseId, setUpdatingCourseId] = useState<number | null>(null);
  
  // Use our new hook for updating enrollment completion status
  const updateEnrollmentStatus = useUpdateEnrollmentCompletion();
  const queryClient = useQueryClient();
  
  useEffect(() => {
    // Only fetch course details when the user expands the course list
    if (showCourses && goal.courses && Object.keys(goal.courses).length > 0 && courseDetails.length === 0) {
      const fetchCourseDetails = async () => {
        setLoading(true);
        try {
          // Create an array of course IDs from the goal's courses
          const courseIds = Object.keys(goal.courses || {});
          
          // Fetch course details one by one (as there's no bulk endpoint)
          const fetchPromises = courseIds.map(async (courseId) => {
            try {
              const response = await axiosInstance.get(`/courses/${courseId}`);
              // Based on your API structure, the course data should be in response.data.data
              return {
                id: parseInt(courseId),
                name: response.data.data.name,
                title: response.data.data.title,
                completed: goal.courses?.[parseInt(courseId)] || false
              };
            } catch (error) {
              console.error(`Error fetching course ${courseId}:`, error);
              return {
                id: parseInt(courseId),
                name: `Course ${courseId}`,
                completed: goal.courses?.[parseInt(courseId)] || false
              };
            }
          });
          
          const results = await Promise.all(fetchPromises);
          setCourseDetails(results);
        } catch (error) {
          console.error('Error fetching course details:', error);
          
          // If API calls fail, create fallback entries with just IDs
          const fallbackCourses = Object.entries(goal.courses || {}).map(([id, completed]) => ({
            id: parseInt(id),
            name: `Course ${id}`,
            completed: !!completed
          }));
          
          setCourseDetails(fallbackCourses);
        } finally {
          setLoading(false);
        }
      };
      
      fetchCourseDetails();
    }
  }, [showCourses, goal.courses, courseDetails.length]);
  
  // Update course details when courses in goal prop changes
  useEffect(() => {
    if (courseDetails.length > 0 && goal.courses) {
      const updatedCourseDetails = courseDetails.map(course => ({
        ...course,
        completed: goal.courses?.[course.id] || false
      }));
      setCourseDetails(updatedCourseDetails);
    }
  }, [goal.courses]);
  
  const handleToggleCourseStatus = async (courseId: number, currentStatus: boolean) => {
    // Set the course as updating
    setUpdatingCourseId(courseId);
    
    try {
      // Call the API to update the enrollment status
      await updateEnrollmentStatus.mutateAsync({
        goalId: goal.id,
        enrollmentId: courseId
      });
      
      // Update the local state for immediate feedback
      setCourseDetails(courses => 
        courses.map(course => 
          course.id === courseId ? { ...course, completed: !currentStatus } : course
        )
      );
      
      // Show success notification
      toast.success(`Course ${!currentStatus ? 'completed' : 'marked as incomplete'}`);
      
      // Refresh the dashboard data
      queryClient.invalidateQueries(['goals', 'dashboard']);
    } catch (error) {
      toast.error('Failed to update course status');
      console.error('Error updating course status:', error);
    } finally {
      setUpdatingCourseId(null);
    }
  };
  
  if (!goal) {
    return null;
  }
  
  const coursesCount = goal.courses ? Object.keys(goal.courses).length : 0;
  const completedCount = goal.courses ? 
    Object.values(goal.courses).filter(status => status === true).length : 0;
  
  return (
    <div className="rounded border p-3">
      <div className="flex justify-between">
        <h3 className="font-semibold">{goal.description}</h3>
        <span className="text-sm font-bold text-blue-600">{goal.progress?.toFixed(0)}%</span>
      </div>
      
      <div className="mt-2">
        <div className="h-2 w-full rounded-full bg-gray-200">
          <div 
            className="h-2 rounded-full bg-blue-600" 
            style={{ width: `${goal.progress || 0}%` }}
          ></div>
        </div>
      </div>
      
      <div className="mt-2 flex justify-between text-xs text-gray-500">
        <span>Courses: {completedCount}/{coursesCount}</span>
        <span>
          {new Date(goal.startDate).toLocaleDateString()} - {new Date(goal.endDate).toLocaleDateString()}
        </span>
      </div>
      
      {/* Reward section */}
      {goal.reward && (
        <div className="mt-2 text-xs">
          <span className="text-gray-500">Reward:</span> <span>{goal.reward}</span>
        </div>
      )}
      
      {/* Courses section */}
      <div className="mt-2">
        <button 
          onClick={() => setShowCourses(!showCourses)}
          className="text-xs text-blue-500 hover:text-blue-700 flex items-center"
        >
          {showCourses ? 'Hide' : 'Show'} Courses 
          <svg 
            className={`ml-1 w-3 h-3 transition-transform ${showCourses ? 'rotate-180' : ''}`} 
            fill="none" 
            stroke="currentColor" 
            viewBox="0 0 24 24" 
            xmlns="http://www.w3.org/2000/svg"
          >
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M19 9l-7 7-7-7"></path>
          </svg>
        </button>
        
        {showCourses && (
          <div className="mt-1 pl-1 text-xs">
            {loading ? (
              <p className="text-gray-400 italic">Loading courses...</p>
            ) : courseDetails.length > 0 ? (
              <ul className="space-y-2">
                {courseDetails.map((course) => (
                  <li key={course.id} className="flex items-center justify-between border-b pb-1">
                    <div className="flex items-center">
                      <span className={course.completed ? 'text-green-500' : 'text-gray-400'}>
                        {course.completed ? '✓' : '○'}
                      </span>
                      <span className="ml-2">{course.title || course.name || `Course ${course.id}`}</span>
                    </div>
                    <button
                      onClick={() => handleToggleCourseStatus(course.id, course.completed)}
                      disabled={updatingCourseId === course.id}
                      className={`
                        ml-2 px-2 py-1 rounded text-xs 
                        ${course.completed 
                          ? 'bg-gray-100 hover:bg-gray-200 text-gray-600' 
                          : 'bg-green-100 hover:bg-green-200 text-green-600'}
                        ${updatingCourseId === course.id ? 'opacity-50 cursor-not-allowed' : 'cursor-pointer'}
                      `}
                    >
                      {updatingCourseId === course.id ? (
                        <span className="inline-block w-3 h-3 border-t-2 border-r-2 border-green-600 rounded-full animate-spin"></span>
                      ) : course.completed ? (
                        'Undo'
                      ) : (
                        'Complete'
                      )}
                    </button>
                  </li>
                ))}
              </ul>
            ) : (
              <p className="text-gray-400 italic">No courses available</p>
            )}
          </div>
        )}
      </div>
    </div>
  );
};

export default GoalDashboardCard;
