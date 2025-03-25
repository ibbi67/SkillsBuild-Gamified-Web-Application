import React, { useState } from 'react';
import { useGoalDashboard } from '@/queries/goals/useGoalDashboard';
import { useUpdateEnrollmentCompletion } from '@/queries/goals/useUpdateEnrollmentCompletion';
import { useEnrollments } from '@/queries/enrollments/useEnrollments';
import Link from 'next/link';
import AddGoalModal from './AddGoalModal';
import toast from 'react-hot-toast';
import { useQueryClient } from '@tanstack/react-query';

export default function DashboardGoalsSection() {
    const [isAddGoalModalOpen, setIsAddGoalModalOpen] = useState(false);
    const { data: goals, isLoading, isError } = useGoalDashboard();
    const { data: enrollments } = useEnrollments();
    const updateEnrollmentCompletionMutation = useUpdateEnrollmentCompletion();
    const queryClient = useQueryClient();

    const handleMarkCourseAsCompleted = async (goalId: number, enrollmentId: number) => {
        try {
            await updateEnrollmentCompletionMutation.mutateAsync({ goalId, enrollmentId });
            toast.success('Course marked as completed');
            queryClient.invalidateQueries({ queryKey: ['goals'] });
        } catch (error) {
            toast.error('Failed to update course completion status');
        }
    };

    const openAddGoalModal = () => {
        setIsAddGoalModalOpen(true);
    };

    const closeAddGoalModal = () => {
        setIsAddGoalModalOpen(false);
    };

    if (isLoading) {
        return <div className="rounded-lg border p-4 shadow">Loading goals...</div>;
    }

    if (isError) {
        return <div className="rounded-lg border p-4 shadow">Error loading goals</div>;
    }

    // Only show up to 2 goals on the dashboard
    const displayGoals = goals?.data?.slice(0, 2) || [];
    const hasMoreGoals = (goals?.data?.length || 0) > 2;

    return (
        <div className="rounded-lg border p-4 shadow">
            <div className="flex items-center justify-between mb-4">
                <h2 className="font-bold">Your Learning Goals</h2>
                <button
                    onClick={openAddGoalModal}
                    className="px-3 py-1 text-sm bg-blue-500 text-white rounded-lg hover:bg-blue-600 transition"
                >
                    Add Goal
                </button>
            </div>

            {displayGoals.length === 0 ? (
                <p className="text-gray-500 text-sm">You don't have any active goals. Create one to track your progress!</p>
            ) : (
                <div className="space-y-4">
                    {displayGoals.map((goal) => {
                        // Calculate days remaining
                        const daysRemaining = Math.ceil((new Date(goal.endDate).getTime() - new Date().getTime()) / (1000 * 60 * 60 * 24));
                        const completedCount = goal.enrollments.filter(e => e.completed).length;
                        const totalCount = goal.enrollments.length;
                        const progressPercentage = totalCount > 0 ? (completedCount / totalCount) * 100 : 0;
                        
                        return (
                            <div key={goal.id} className="border rounded-lg p-3">
                                <div className="flex justify-between items-start mb-2">
                                    <h3 className="font-semibold text-sm">{goal.description}</h3>
                                    <span className={`text-xs px-2 py-1 rounded-full ${
                                        daysRemaining < 3 ? 'bg-red-100 text-red-800' : 'bg-blue-100 text-blue-800'
                                    }`}>
                                        {daysRemaining} days left
                                    </span>
                                </div>
                                
                                <div className="flex items-center mt-3 mb-1">
                                    <div className="flex-grow bg-gray-200 rounded-full h-2">
                                        <div
                                            className="bg-green-500 h-2 rounded-full"
                                            style={{ width: `${progressPercentage}%` }}
                                        ></div>
                                    </div>
                                    <span className="ml-2 text-xs">
                                        {completedCount}/{totalCount}
                                    </span>
                                </div>
                                
                                {goal.enrollments.length > 0 && (
                                    <div className="mt-2">
                                        <h4 className="text-xs font-medium mb-1">Next course:</h4>
                                        {goal.enrollments.find(e => !e.completed) ? (
                                            <div className="flex items-center justify-between bg-gray-50 p-2 rounded text-xs">
                                                <span>{goal.enrollments.find(e => !e.completed)?.course.title}</span>
                                                <button
                                                    onClick={() => handleMarkCourseAsCompleted(
                                                        goal.id, 
                                                        goal.enrollments.find(e => !e.completed)?.id || 0
                                                    )}
                                                    className="px-2 py-1 bg-blue-500 text-white rounded text-xs hover:bg-blue-600"
                                                >
                                                    Complete
                                                </button>
                                            </div>
                                        ) : (
                                            <p className="text-xs text-green-600">All courses completed!</p>
                                        )}
                                    </div>
                                )}
                            </div>
                        );
                    })}
                </div>
            )}
            
            {hasMoreGoals && (
                <div className="mt-3 text-center">
                    <Link href="/goals" className="text-blue-500 text-sm hover:underline">
                        View all goals
                    </Link>
                </div>
            )}
            
            <AddGoalModal
                isOpen={isAddGoalModalOpen}
                onClose={closeAddGoalModal}
                enrollments={enrollments?.data || []}
            />
        </div>
    );
}
